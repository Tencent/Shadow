/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform_kit.SpecificTransform
import com.tencent.shadow.core.transform_kit.TransformStep
import javassist.CodeConverter
import javassist.CtClass
import javassist.CtMethod
import javassist.Modifier
import javassist.NotFoundException
import javassist.compiler.Javac.CtFieldWithInit

/**
 * 系统回调BroadcastReceiver的onReceive(Context context, Intent intent)方法时：
 * 1. 传回的context是宿主的，需要修改为插件的。
 * 2. intent的ExtrasClassLoader是宿主的，需要改为插件的。
 *
 * 如果是系统类的BroadcastReceiver，它也不会和插件的context或classloader有什么联系，
 * 所以我们只需要修改插件代码中的BroadcastReceiver。
 *
 * 把原本插件的onReceive方法改个名字，再统一添加一个onReceive方法。
 * 在新增的onReceive方法中修改收到的系统回调参数，再转调被改名了的原本插件的onReceive方法。
 */
class ReceiverSupportTransform : SpecificTransform() {

    companion object {
        const val AndroidBroadcastReceiverClassname = "android.content.BroadcastReceiver"
        const val AndroidContextClassname = "android.content.Context"
        const val AndroidIntentClassname = "android.content.Intent"
    }

    private fun CtClass.isReceiver(): Boolean = isClassOf(AndroidBroadcastReceiverClassname)

    override fun setup(allInputClass: Set<CtClass>) {
        mClassPool.importPackage("android.content")
        mClassPool.importPackage("com.tencent.shadow.core.runtime")

        val androidContext = mClassPool[AndroidContextClassname]
        val androidIntent = mClassPool[AndroidIntentClassname]

        /**
         * 收集覆盖了onReceive方法的Receiver作为修改目标
         */
        val targetReceivers = mutableSetOf<CtClass>()
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>) = allInputClass
                .filter { it.isReceiver() }
                .toSet()

            override fun transform(ctClass: CtClass) {
                val onReceiveMethod: CtMethod? =
                    try {
                        ctClass.getDeclaredMethod(
                            "onReceive",
                            arrayOf(androidContext, androidIntent)
                        )
                    } catch (e: NotFoundException) {
                        null
                    }
                if (onReceiveMethod != null && !Modifier.isVolatile(onReceiveMethod.modifiers)) {
                    targetReceivers.add(ctClass)
                }
            }
        })

        /**
         * 对原本的onReceive方法改名，并添加新的onReceive方法。
         */
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>) = targetReceivers

            override fun transform(ctClass: CtClass) {
                ctClass.defrost()

                // 改名
                val originalOnReceiveMethod: CtMethod =
                    ctClass.getDeclaredMethod(
                        "onReceive",
                        arrayOf(androidContext, androidIntent)
                    )
                originalOnReceiveMethod.name = "onReceiveShadowContext"
                originalOnReceiveMethod.modifiers =
                    Modifier.setPrivate(originalOnReceiveMethod.modifiers)

                // 声明两个域变量保存onReceive收到的原始参数，供调用super方法时使用。
                // the compiler embedded in Javassist does not support generics
                arrayOf(
                    CtFieldWithInit.make(
                        "ThreadLocal originalOnReceiveContext = new ThreadLocal();",
                        ctClass
                    ),
                    CtFieldWithInit.make(
                        "ThreadLocal originalOnReceiveIntent = new ThreadLocal();",
                        ctClass
                    ),
                ).forEach {
                    ctClass.addField(it)
                }

                // 添加新onReceive方法
                val newOnReceiveMethod = CtMethod.make(
                    """
                        public void onReceive(Context context, Intent intent) {
                            try{
                                //保存收到的参数
                                originalOnReceiveContext.set(context);
                                originalOnReceiveIntent.set(intent);
                                
                                //通过当前插件类ClassLoader找到相关的插件Application
                                ClassLoader cl = this.getClass().getClassLoader();
                                PluginPartInfo info = PluginPartInfoManager.getPluginInfo(cl);
                                Context shadowContext = info.application;
                                Intent intentCopy = new Intent(intent);//不修改原本的intent
                                intentCopy.setExtrasClassLoader(cl);
                                
                                //调用原本的onReceive方法
                                onReceiveShadowContext(shadowContext, intentCopy);
                            }finally {
                                originalOnReceiveContext.remove();
                                originalOnReceiveIntent.remove();
                            }
                        }
                    """.trimIndent(), ctClass
                )
                ctClass.addMethod(newOnReceiveMethod)

                // 定义superOnReceiveMethod方法
                val newSuperMethod = CtMethod.make(
                    """
                        private void superOnReceive(Context context, Intent intent) {
                            context = (Context)originalOnReceiveContext.get();
                            intent = (Intent)originalOnReceiveIntent.get();
                            super.onReceive(context, intent);
                        }
                    """.trimIndent(), ctClass
                )

                //转调super.onReceive方法
                val superMethod: CtMethod =
                    ctClass.superclass.getMethod(
                        "onReceive",
                        "(Landroid/content/Context;Landroid/content/Intent;)V"
                    )

                if (Modifier.isAbstract(superMethod.modifiers).not()) {
                    val codeConverter = CodeConverter()
                    codeConverter.redirectMethodCall(superMethod, newSuperMethod)
                    try {
                        ctClass.instrument(codeConverter)
                    } catch (e: Exception) {
                        System.err.println("处理" + ctClass.name + "时出错:" + e)
                        throw e
                    }
                    ctClass.addMethod(newSuperMethod)
                }
            }

        })
    }
}
