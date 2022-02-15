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
import javassist.*
import javassist.bytecode.Descriptor

class FragmentSupportTransform : SpecificTransform() {
    companion object {
        const val ObjectClassname = "java.lang.Object"
        const val ShadowActivityClassname = "com.tencent.shadow.core.runtime.ShadowActivity"
        const val AndroidActivityClassname = "android.app.Activity"
        const val AndroidFragmentClassname = "android.app.Fragment"
        const val AndroidIntentClassname = "android.content.Intent"
        const val AndroidBundleClassname = "android.os.Bundle"
        const val AndroidContextClassname = "android.content.Context"
        const val AndroidAttributeSetClassname = "android.util.AttributeSet"
        const val ShadowFragmentSupportClassname =
            "com.tencent.shadow.core.runtime.ShadowFragmentSupport"
    }

    private fun CtClass.isFragment(): Boolean = isClassOf(AndroidFragmentClassname)

    override fun setup(allInputClass: Set<CtClass>) {
        val javaObject = mClassPool[ObjectClassname]
        val androidActivity = mClassPool[AndroidActivityClassname]
        val androidFragment = mClassPool[AndroidFragmentClassname]
        val androidIntent = mClassPool[AndroidIntentClassname]
        val androidBundle = mClassPool[AndroidBundleClassname]
        val androidContext = mClassPool[AndroidContextClassname]
        val androidAttributeSet = mClassPool[AndroidAttributeSetClassname]
        val shadowActivity = mClassPool[ShadowActivityClassname]
        val shadowFragmentSupport = mClassPool[ShadowFragmentSupportClassname]
        val getActivityMethod = androidFragment.getDeclaredMethod("getActivity")
        val getContextMethod = androidFragment.getDeclaredMethod("getContext")
        val getHostMethod = androidFragment.getDeclaredMethod("getHost")
        val fragmentGetActivityMethod = shadowFragmentSupport.getMethod(
            "fragmentGetActivity",
            Descriptor.ofMethod(
                shadowActivity,
                arrayOf(androidFragment)
            )
        )
        val fragmentGetContextMethod = shadowFragmentSupport.getMethod(
            "fragmentGetContext",
            Descriptor.ofMethod(
                androidContext,
                arrayOf(androidFragment)
            )
        )
        val fragmentGetHostMethod = shadowFragmentSupport.getMethod(
            "fragmentGetHost",
            Descriptor.ofMethod(
                javaObject,
                arrayOf(androidFragment)
            )
        )

        mClassPool.importPackage("android.app")
        mClassPool.importPackage("android.content")
        mClassPool.importPackage("android.util")
        mClassPool.importPackage("android.os")
        mClassPool.importPackage("com.tencent.shadow.core.runtime")

        //appClass中的Activity都已经被改名为ShadowActivity了．所以要把方法签名也先改一下．
        getActivityMethod.methodInfo.descriptor =
            "()Lcom/tencent/shadow/core/runtime/ShadowActivity;"

        val startActivityMethod1 = androidFragment.getMethod(
            "startActivity",
            Descriptor.ofMethod(
                CtClass.voidType,
                arrayOf(androidIntent)
            )
        )
        val fragmentStartActivityMethod1 = shadowFragmentSupport.getMethod(
            "fragmentStartActivity",
            Descriptor.ofMethod(
                CtClass.voidType,
                arrayOf(androidFragment, androidIntent)
            )
        )
        val startActivityMethod2 = androidFragment.getMethod(
            "startActivity",
            Descriptor.ofMethod(
                CtClass.voidType,
                arrayOf(androidIntent, androidBundle)
            )
        )
        val fragmentStartActivityMethod2 = shadowFragmentSupport.getMethod(
            "fragmentStartActivity",
            Descriptor.ofMethod(
                CtClass.voidType,
                arrayOf(androidFragment, androidIntent, androidBundle)
            )
        )

        val startActivityForResultMethod1 = androidFragment.getMethod(
            "startActivityForResult",
            Descriptor.ofMethod(
                CtClass.voidType,
                arrayOf(androidIntent, CtClass.intType)
            )
        )
        val fragmentStartActivityForResultMethod1 = shadowFragmentSupport.getMethod(
            "fragmentStartActivityForResult",
            Descriptor.ofMethod(
                CtClass.voidType,
                arrayOf(androidFragment, androidIntent, CtClass.intType)
            )
        )
        val startActivityForResultMethod2 = androidFragment.getMethod(
            "startActivityForResult",
            Descriptor.ofMethod(
                CtClass.voidType,
                arrayOf(androidIntent, CtClass.intType, androidBundle)
            )
        )
        val fragmentStartActivityForResultMethod2 = shadowFragmentSupport.getMethod(
            "fragmentStartActivityForResult",
            Descriptor.ofMethod(
                CtClass.voidType,
                arrayOf(androidFragment, androidIntent, CtClass.intType, androidBundle)
            )
        )

        /**
         * 调用插件Fragment的Activity相关方法时将ContainerActivity转换成ShadowActivity
         */
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>) = allInputClass

            override fun transform(ctClass: CtClass) {
                ctClass.defrost()
                val codeConverter = EnhancedCodeConverter()
                codeConverter.redirectMethodCallToStatic(
                    getActivityMethod,
                    fragmentGetActivityMethod
                )
                codeConverter.redirectMethodCallExceptSuperCallToStatic(
                    getContextMethod,
                    fragmentGetContextMethod
                )
                codeConverter.redirectMethodCallToStatic(getHostMethod, fragmentGetHostMethod)
                codeConverter.redirectMethodCallToStatic(
                    startActivityMethod1,
                    fragmentStartActivityMethod1
                )
                codeConverter.redirectMethodCallToStatic(
                    startActivityMethod2,
                    fragmentStartActivityMethod2
                )
                codeConverter.redirectMethodCallToStatic(
                    startActivityForResultMethod1,
                    fragmentStartActivityForResultMethod1
                )
                codeConverter.redirectMethodCallToStatic(
                    startActivityForResultMethod2,
                    fragmentStartActivityForResultMethod2
                )
                try {
                    ctClass.instrument(codeConverter)
                } catch (e: Exception) {
                    System.err.println("处理" + ctClass.name + "时出错:" + e)
                    throw e
                }
            }
        })

        fun onAttachSupport() {
            //收集哪些Fragment覆盖了onAttach方法
            val overrideOnAttachContextFragments = mutableSetOf<CtClass>()
            val overrideOnAttachActivityFragments = mutableSetOf<CtClass>()
            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) = allInputClass
                    .filter { it.isFragment() }
                    .toSet()

                override fun transform(ctClass: CtClass) {
                    val onAttachContext: CtMethod? =
                        try {
                            ctClass.getDeclaredMethod("onAttach", arrayOf(androidContext))
                        } catch (e: NotFoundException) {
                            null
                        }
                    val onAttachActivity: CtMethod? =
                        try {
                            ctClass.getDeclaredMethod("onAttach", arrayOf(shadowActivity))
                        } catch (e: NotFoundException) {
                            null
                        }
                    if (onAttachContext != null) {
                        overrideOnAttachContextFragments.add(ctClass)
                    }
                    if (onAttachActivity != null) {
                        overrideOnAttachActivityFragments.add(ctClass)
                    }
                }
            })

            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) = overrideOnAttachContextFragments

                override fun transform(ctClass: CtClass) {
                    ctClass.defrost()
                    val onAttachContext: CtMethod =
                        ctClass.getDeclaredMethod("onAttach", arrayOf(androidContext))
                    //原来的onAttach方法改名为onAttachShadowContext
                    onAttachContext.name = "onAttachShadowContext"
                    onAttachContext.modifiers = Modifier.setPrivate(onAttachContext.modifiers)

                    //重新定义onAttach方法
                    val newOnAttachContext = CtMethod.make(
                        """
                        public void onAttach(Context context) {
                            Context pluginActivity = ShadowFragmentSupport.toPluginContext(context);
                            onAttachShadowContext(pluginActivity);
                        }
                    """.trimIndent(), ctClass
                    )
                    ctClass.addMethod(newOnAttachContext)
                }
            })

            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) = overrideOnAttachActivityFragments

                override fun transform(ctClass: CtClass) {
                    ctClass.defrost()
                    val onAttachActivity: CtMethod =
                        ctClass.getDeclaredMethod("onAttach", arrayOf(shadowActivity))
                    //原来的onAttach方法改名为onAttachShadowActivity
                    onAttachActivity.name = "onAttachShadowActivity"
                    onAttachActivity.modifiers = Modifier.setPrivate(onAttachActivity.modifiers)

                    //重新定义onAttach方法
                    val newOnAttachActivity = CtMethod.make(
                        """
                        public void onAttach(Activity activity) {
                            ShadowActivity shadowActivity = (ShadowActivity)ShadowFragmentSupport.toPluginContext(activity);
                            onAttachShadowActivity(shadowActivity);
                        }
                    """.trimIndent(), ctClass
                    )
                    ctClass.addMethod(newOnAttachActivity)
                }
            })

            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) = overrideOnAttachContextFragments

                override fun transform(ctClass: CtClass) {
                    ctClass.defrost()
                    //定义将插件Activity还原为ContainerActivity再调用super.onAttach方法的转调方法
                    val superOnAttach = CtMethod.make(
                        """
                        private void superOnAttach(Context context) {
                            Context pluginContainerActivity = ShadowFragmentSupport.toOriginalContext(context);
                            super.onAttach(pluginContainerActivity);
                        }
                    """.trimIndent(), ctClass
                    )

                    //将插件Fragment中对super.onAttach的调用改调到superOnAttach上
                    val codeConverter = CodeConverter()
                    val superOnAttachContext: CtMethod =
                        ctClass.superclass.getMethod("onAttach", "(Landroid/content/Context;)V")
                    codeConverter.redirectMethodCall(superOnAttachContext, superOnAttach)
                    try {
                        ctClass.instrument(codeConverter)
                    } catch (e: Exception) {
                        System.err.println("处理" + ctClass.name + "时出错:" + e)
                        throw e
                    }

                    /**
                     * 一定要在super.onAttach转调到superOnAttach之后
                     * 再把superOnAttach添加到ctClass上，避免superOnAttach中的
                     * super.onAttach也被改成superOnAttach
                     */
                    ctClass.addMethod(superOnAttach)
                }
            })

            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) = overrideOnAttachActivityFragments

                override fun transform(ctClass: CtClass) {
                    ctClass.defrost()
                    //定义将插件Activity还原为ContainerActivity再调用super.onAttach方法的转调方法

                    val superOnAttach =
                        fixSuperOnAttachCall(ctClass) {
                            CtMethod.make(
                                """
                        private void superOnAttach(ShadowActivity shadowActivity) {
                            Activity pluginContainerActivity = (Activity)ShadowFragmentSupport.toOriginalContext(shadowActivity);
                            super.onAttach(pluginContainerActivity);
                        }
                    """.trimIndent(), ctClass
                            )
                        }

                    //将插件Fragment中对super.onAttach的调用改调到superOnAttach上
                    val codeConverter = CodeConverter()
                    var superOnAttachActivity: CtMethod =
                        androidFragment.getDeclaredMethod("onAttach", arrayOf(androidActivity))
                    superOnAttachActivity =
                        CtNewMethod.copy(superOnAttachActivity, androidFragment, null)
                    superOnAttachActivity.methodInfo.descriptor =
                        "(Lcom/tencent/shadow/core/runtime/ShadowActivity;)V"
                    codeConverter.redirectMethodCall(superOnAttachActivity, superOnAttach)
                    try {
                        ctClass.instrument(codeConverter)
                    } catch (e: Exception) {
                        System.err.println("处理" + ctClass.name + "时出错:" + e)
                        throw e
                    }

                    /**
                     * 一定要在super.onAttach转调到superOnAttach之后
                     * 再把superOnAttach添加到ctClass上，避免superOnAttach中的
                     * super.onAttach也被改成superOnAttach
                     */
                    ctClass.addMethod(superOnAttach)
                }

                /**
                 * Javassist疑似有bug，当super类存在满足签名的方法时，就不会去父类的父类中查找更加准确匹配的方法了。
                 * 导致当父类只Override了onAttach(Context)方法时，我们定义的superOnAttach方法中的
                 * super.onAttach(Activity)调用会编译成对onAttach(Context)的调用。这与正常的Javac编译结果不一致。
                 *
                 * 因此，在这里如果父类没有定义onAttach(Activity)，先为它添加上，make后再移除。
                 */
                private fun fixSuperOnAttachCall(ctClass: CtClass, make: () -> CtMethod): CtMethod {
                    val superclass = ctClass.superclass
                    val needFix = try {
                        superclass.getDeclaredMethod("onAttach", arrayOf(androidActivity))
                        false
                    } catch (e: NotFoundException) {
                        true
                    }
                    return if (needFix) {
                        superclass.defrost()
                        val newOnAttachActivity = CtMethod.make(
                            """
                                public void onAttach(Activity activity) {}
                            """.trimIndent(), superclass
                        )
                        superclass.addMethod(newOnAttachActivity)
                        val result = make()
                        superclass.removeMethod(newOnAttachActivity)
                        result
                    } else {
                        make()
                    }
                }
            })
        }
        onAttachSupport()

        fun onInflateSupport() {
            //收集哪些Fragment覆盖了onInflate方法
            val overrideOnInflateContextFragments = mutableSetOf<CtClass>()
            val overrideOnInflateActivityFragments = mutableSetOf<CtClass>()
            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) = allInputClass
                    .filter { it.isFragment() }
                    .toSet()

                override fun transform(ctClass: CtClass) {
                    val onInflateContext: CtMethod? =
                        try {
                            ctClass.getDeclaredMethod(
                                "onInflate",
                                arrayOf(androidContext, androidAttributeSet, androidBundle)
                            )
                        } catch (e: NotFoundException) {
                            null
                        }
                    val onInflateActivity: CtMethod? =
                        try {
                            ctClass.getDeclaredMethod(
                                "onInflate",
                                arrayOf(shadowActivity, androidAttributeSet, androidBundle)
                            )
                        } catch (e: NotFoundException) {
                            null
                        }
                    if (onInflateContext != null) {
                        overrideOnInflateContextFragments.add(ctClass)
                    }
                    if (onInflateActivity != null) {
                        overrideOnInflateActivityFragments.add(ctClass)
                    }
                }
            })

            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) = overrideOnInflateContextFragments

                override fun transform(ctClass: CtClass) {
                    ctClass.defrost()
                    val onInflateContext: CtMethod = ctClass.getDeclaredMethod(
                        "onInflate",
                        arrayOf(androidContext, androidAttributeSet, androidBundle)
                    )
                    //原来的onInflate方法改名为onInflateShadowContext
                    onInflateContext.name = "onInflateShadowContext"
                    onInflateContext.modifiers = Modifier.setPrivate(onInflateContext.modifiers)

                    //重新定义onInflate方法
                    val newOnInflateContext = CtMethod.make(
                        """
                        public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
                            Context pluginActivity = ShadowFragmentSupport.toPluginContext(context);
                            onInflateShadowContext(pluginActivity, attrs, savedInstanceState);
                        }
                    """.trimIndent(), ctClass
                    )
                    ctClass.addMethod(newOnInflateContext)
                }
            })

            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) =
                    overrideOnInflateActivityFragments

                override fun transform(ctClass: CtClass) {
                    ctClass.defrost()
                    val onInflateActivity: CtMethod = ctClass.getDeclaredMethod(
                        "onInflate",
                        arrayOf(shadowActivity, androidAttributeSet, androidBundle)
                    )
                    //原来的onInflate方法改名为onInflateShadowContext
                    onInflateActivity.name = "onInflateShadowContext"
                    onInflateActivity.modifiers = Modifier.setPrivate(onInflateActivity.modifiers)

                    //重新定义onInflate方法
                    val newOnInflateContext = CtMethod.make(
                        """
                        public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
                            ShadowActivity shadowActivity = (ShadowActivity)ShadowFragmentSupport.toPluginContext(activity);
                            onInflateShadowContext(shadowActivity, attrs, savedInstanceState);
                        }
                    """.trimIndent(), ctClass
                    )
                    ctClass.addMethod(newOnInflateContext)
                }
            })

            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) = overrideOnInflateContextFragments

                override fun transform(ctClass: CtClass) {
                    ctClass.defrost()
                    //定义将插件Activity还原为ContainerActivity再调用super.onInflate方法的转调方法
                    val superOnInflate = CtMethod.make(
                        """
                        private void superOnInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
                            Context pluginContainerActivity = ShadowFragmentSupport.toOriginalContext(context);
                            super.onInflate(pluginContainerActivity, attrs, savedInstanceState);
                        }
                    """.trimIndent(), ctClass
                    )

                    //将插件Fragment中对super.onAttach的调用改调到superOnAttach上
                    val codeConverter = CodeConverter()
                    val superOnInflateContext: CtMethod = ctClass.superclass.getMethod(
                        "onInflate",
                        "(Landroid/content/Context;Landroid/util/AttributeSet;Landroid/os/Bundle;)V"
                    )
                    codeConverter.redirectMethodCall(superOnInflateContext, superOnInflate)
                    try {
                        ctClass.instrument(codeConverter)
                    } catch (e: Exception) {
                        System.err.println("处理" + ctClass.name + "时出错:" + e)
                        throw e
                    }

                    /**
                     * 一定要在super.onInflate转调到superOnInflate之后
                     * 再把superOnInflate添加到ctClass上，避免superOnInflate中的
                     * super.onInflate也被改成superOnInflate
                     */
                    ctClass.addMethod(superOnInflate)
                }
            })

            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) =
                    overrideOnInflateActivityFragments

                override fun transform(ctClass: CtClass) {
                    ctClass.defrost()
                    //定义将插件Activity还原为ContainerActivity再调用super.onInflate方法的转调方法

                    val superOnInflate =
                        fixSuperOnInflateCall(ctClass) {
                            CtMethod.make(
                                """
                        private void superOnInflate(ShadowActivity shadowActivity, AttributeSet attrs, Bundle savedInstanceState) {
                            Activity pluginContainerActivity = (Activity)ShadowFragmentSupport.toOriginalContext(shadowActivity);
                            super.onInflate(pluginContainerActivity, attrs, savedInstanceState);
                        }
                    """.trimIndent(), ctClass
                            )
                        }

                    //将插件Fragment中对super.onAttach的调用改调到superOnAttach上
                    val codeConverter = CodeConverter()
                    var superOnInflateActivity: CtMethod = androidFragment.getDeclaredMethod(
                        "onInflate",
                        arrayOf(androidActivity, androidAttributeSet, androidBundle)
                    )
                    superOnInflateActivity =
                        CtNewMethod.copy(superOnInflateActivity, androidFragment, null)
                    superOnInflateActivity.methodInfo.descriptor =
                        "(Lcom/tencent/shadow/core/runtime/ShadowActivity;Landroid/util/AttributeSet;Landroid/os/Bundle;)V"
                    codeConverter.redirectMethodCall(superOnInflateActivity, superOnInflate)
                    try {
                        ctClass.instrument(codeConverter)
                    } catch (e: Exception) {
                        System.err.println("处理" + ctClass.name + "时出错:" + e)
                        throw e
                    }

                    /**
                     * 一定要在super.onInflate转调到superOnInflate之后
                     * 再把superOnInflate添加到ctClass上，避免superOnInflate中的
                     * super.onInflate也被改成superOnInflate
                     */
                    ctClass.addMethod(superOnInflate)
                }

                /**
                 * Javassist疑似有bug，当super类存在满足签名的方法时，就不会去父类的父类中查找更加准确匹配的方法了。
                 * 导致当父类只Override了onAttach(Context)方法时，我们定义的superOnAttach方法中的
                 * super.onAttach(Activity)调用会编译成对onAttach(Context)的调用。这与正常的Javac编译结果不一致。
                 *
                 * 因此，在这里如果父类没有定义onAttach(Activity)，先为它添加上，make后再移除。
                 */
                private fun fixSuperOnInflateCall(
                    ctClass: CtClass,
                    make: () -> CtMethod
                ): CtMethod {
                    val superclass = ctClass.superclass
                    val needFix = try {
                        superclass.getDeclaredMethod(
                            "onInflate",
                            arrayOf(androidActivity, androidAttributeSet, androidBundle)
                        )
                        false
                    } catch (e: NotFoundException) {
                        true
                    }
                    return if (needFix) {
                        superclass.defrost()
                        val newOnInflateActivity = CtMethod.make(
                            """
                                public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {}
                            """.trimIndent(), superclass
                        )
                        superclass.addMethod(newOnInflateActivity)
                        val result = make()
                        superclass.removeMethod(newOnInflateActivity)
                        result
                    } else {
                        make()
                    }
                }
            })
        }
        onInflateSupport()
    }

}