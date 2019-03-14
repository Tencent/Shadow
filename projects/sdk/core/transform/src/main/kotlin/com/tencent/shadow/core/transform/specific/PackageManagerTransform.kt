package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform.common.CodeConverterExtension
import com.tencent.shadow.core.transform.common.SpecificTransform
import com.tencent.shadow.core.transform.common.TransformStep
import javassist.CtClass
import javassist.CtMethod
import javassist.CtNewMethod
import javassist.Modifier
import java.util.*

class PackageManagerTransform : SpecificTransform() {
    companion object {
        const val AndroidPackageManagerClassname = "android.content.pm.PackageManager"
        const val ShadowAndroidPackageManagerClassname = "com.tencent.shadow.runtime.ShadowPackageManager"
        const val AndroidProviderInfo = "android.content.pm.ProviderInfo"
        const val AndroidActivityInfo = "android.content.pm.ActivityInfo"
        const val AndroidApplicationInfo = "android.content.pm.ApplicationInfo"
        const val AndroidServiceInfo = "android.content.pm.ServiceInfo"
        const val AndroidPackageItemInfo = "android.content.pm.PackageItemInfo"
        const val ShadowAndroidPackageItemInfo = "com.tencent.shadow.runtime.ShadowPackageItemInfo"
    }

    private fun setup(
            targetClassNames: Array<String>,
            targetMethodName: Array<String>,
            redirectRule: Pair<String, String>
    ) {
        val targetMethods = getTargetMethods(targetClassNames, targetMethodName)
        targetMethods.forEach { targetMethod ->
            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) =
                        allCanRecompileAppClass(
                                allInputClass,
                                targetClassNames.asList()
                        ).filter { matchMethodCallInClass(targetMethod, it) }.toSet()

                override fun transform(ctClass: CtClass) {
                    System.out.println(ctClass.name + " matchMethodCallInClass :" + targetMethod.methodInfo.name + "  =================")
                    try {
                        val targetClass = mClassPool[redirectRule.first]
                        val redirectClassName = redirectRule.second
                        val parameterTypes: Array<CtClass> =
                                Array(targetMethod.parameterTypes.size + 1) { index ->
                                    if (index == 0) {
                                        targetClass
                                    } else {
                                        targetMethod.parameterTypes[index - 1]
                                    }
                                }
                        val newMethod = CtNewMethod.make(
                                Modifier.PUBLIC or Modifier.STATIC,
                                targetMethod.returnType,
                                targetMethod.name + "_shadow",
                                parameterTypes,
                                targetMethod.exceptionTypes,
                                null,
                                ctClass
                        )
                        val newBodyBuilder = StringBuilder()
                        newBodyBuilder
                                .append("return ")
                                .append(redirectClassName)
                                .append(".")
                                .append(targetMethod.methodInfo.name)
                                .append("(")
                                .append(ctClass.name)
                                .append(".class.getClassLoader(),")
                        for (i in 1..newMethod.parameterTypes.size) {
                            if (i > 1) {
                                newBodyBuilder.append(',')
                            }
                            newBodyBuilder.append("\$${i}")
                        }
                        newBodyBuilder.append(");")

                        newMethod.setBody(newBodyBuilder.toString())
                        ctClass.addMethod(newMethod)
                        val codeConverter = CodeConverterExtension()
                        codeConverter.redirectMethodCallToStaticMethodCall(targetMethod, newMethod)
                        ctClass.instrument(codeConverter)
                    } catch (e: Exception) {
                        System.err.println("处理" + ctClass.name + "时出错:" + e)
                        throw e
                    }
                }
            })
        }
    }

    override fun setup(allInputClass: Set<CtClass>) {
        setup(
                arrayOf(AndroidPackageManagerClassname),
                arrayOf(
                        "getActivityInfo",
                        "getPackageInfo",
                        "resolveContentProvider"
                ),
                AndroidPackageManagerClassname to ShadowAndroidPackageManagerClassname
        )
        setup(
                arrayOf(
                        AndroidProviderInfo,
                        AndroidServiceInfo,
                        AndroidApplicationInfo,
                        AndroidActivityInfo
                ),
                arrayOf("loadXmlMetaData"),
                AndroidPackageItemInfo to ShadowAndroidPackageItemInfo
        )
    }

    /**
     * 查找目标class对象的目标method
     */
    private fun getTargetMethods(
            targetClassNames: Array<String>,
            targetMethodName: Array<String>
    ): List<CtMethod> {
        val method_targets = ArrayList<CtMethod>()
        for (targetClassName in targetClassNames) {
            val methods = mClassPool[targetClassName].methods
            method_targets.addAll(methods.filter { targetMethodName.contains(it.name) })
        }
        return method_targets
    }
}