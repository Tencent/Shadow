package com.tencent.shadow.core

import com.android.build.api.transform.TransformInvocation
import com.tencent.shadow.core.transformkit.*
import javassist.*
import javassist.bytecode.CodeAttribute
import javassist.bytecode.Descriptor
import javassist.bytecode.MethodInfo
import javassist.bytecode.Opcode
import org.gradle.api.Project
import java.io.File
import java.util.*

class ShadowTransform(project: Project, classPoolBuilder: ClassPoolBuilder, val useHostContext: () -> Array<String>) : JavassistTransform(project, classPoolBuilder) {

    companion object {
        const val ShadowFragmentClassname = "com.tencent.shadow.runtime.ShadowFragment"
        const val ShadowDialogFragmentClassname = "com.tencent.shadow.runtime.ShadowDialogFragment"
        const val AndroidDialogClassname = "android.app.Dialog"
        const val ShadowDialogClassname = "com.tencent.shadow.runtime.ShadowDialog"
        const val AndroidWebViewClassname = "android.webkit.WebView"
        const val ShadowWebViewClassname = "com.tencent.shadow.runtime.ShadowWebView"
        const val AndroidPendingIntentClassname = "android.app.PendingIntent"
        const val ShadowPendingIntentClassname = "com.tencent.shadow.runtime.ShadowPendingIntent"
        const val ShadowUriClassname = "com.tencent.shadow.runtime.UriConverter"
        const val AndroidUriClassname = "android.net.Uri"
        const val AndroidPackageManagerClassname = "android.content.pm.PackageManager"
        const val ShadowAndroidPackageManagerClassname = "com.tencent.shadow.runtime.ShadowPackageManager"
        const val AndroidProviderInfo = "android.content.pm.ProviderInfo"
        const val AndroidActivityInfo = "android.content.pm.ActivityInfo"
        const val AndroidApplicationInfo = "android.content.pm.ApplicationInfo"
        const val AndroidServiceInfo = "android.content.pm.ServiceInfo"
        const val AndroidPackageItemInfo = "android.content.pm.PackageItemInfo"
        const val ShadowAndroidPackageItemInfo = "com.tencent.shadow.runtime.ShadowPackageItemInfo"
        val RenameMap = mapOf(
                "android.app.Application"
                        to "com.tencent.shadow.runtime.ShadowApplication"
                ,
                "android.app.Activity"
                        to "com.tencent.shadow.runtime.ShadowActivity"
                ,
                "android.app.Service"
                        to "com.tencent.shadow.runtime.ShadowService"
                ,
                "android.app.Fragment"
                        to ShadowFragmentClassname
                ,
                "android.app.DialogFragment"
                        to ShadowDialogFragmentClassname
                ,
                "android.app.FragmentManager"
                        to "com.tencent.shadow.runtime.PluginFragmentManager"
                ,
                "android.app.FragmentTransaction"
                        to "com.tencent.shadow.runtime.PluginFragmentTransaction"
                ,
                "android.app.Application\$ActivityLifecycleCallbacks"
                        to "com.tencent.shadow.runtime.ShadowActivityLifecycleCallbacks"
                ,
                AndroidDialogClassname
                        to ShadowDialogClassname
                ,
                "android.app.Instrumentation"
                        to "com.tencent.shadow.runtime.ShadowInstrumentation"
        )

        const val RemoteLocalSdkPackageName = "com.tencent.shadow.remoteview.localsdk"
        val RemoteViewRenameMap = mapOf(
                "com.tencent.shadow.remoteview.localsdk.RemoteViewCreator" to "com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreator",
                "com.tencent.shadow.remoteview.localsdk.RemoteViewCreatorFactory" to "com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreatorFactory",
                "com.tencent.shadow.remoteview.localsdk.RemoteViewCreateCallback" to "com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreateCallback",
                "com.tencent.shadow.remoteview.localsdk.RemoteViewCreateException" to "com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreateException"
        )
    }

    private val containerFragmentCtClass: CtClass get() = classPool["com.tencent.shadow.runtime.ContainerFragment"]
    private val containerDialogFragmentCtClass: CtClass get() = classPool["com.tencent.shadow.runtime.ContainerDialogFragment"]

    val mAppFragments: MutableSet<CtClass> = mutableSetOf()
    val mAppDialogFragments: MutableSet<CtClass> = mutableSetOf()

    override fun beforeTransform(invocation: TransformInvocation) {
        super.beforeTransform(invocation)
        mAppFragments.clear()
        mAppDialogFragments.clear()
    }

    override fun onTransform() {
        step1_renameShadowClass()
        step2_findFragments()
        step3_renameFragments()
        step4_redirectDialogMethod()
        step5_renameWebViewChildClass()
        step6_redirectPendingIntentMethod()
        step7_redirectUriMethod()
        step8_redirectResolverMethod()
        step9_keepHostContext()
        step10_redirectPackageManagerMethod()
        step11_redirectPackageItemInfoMethod()
    }



    private fun renameRemoteViewCreatorClass() {
        forEachAppClass { ctClass ->
            // 除RemoteLocalSdk包外的所有类，都需要替换
            if (RemoteLocalSdkPackageName != ctClass.packageName) {
                RemoteViewRenameMap.forEach {
                    ctClass.replaceClassName(it.key, it.value)
                }
            }

        }
    }

    private inline fun forEachAppClass(action: (CtClass) -> Unit) {
        val appClasses = mCtClassInputMap.keys
        appClasses.forEach(action)
    }

    private inline fun forEachCanRecompileAppClass(targetClassList: List<String>, action: (CtClass) -> Unit) {
        val appClasses = mCtClassInputMap.keys
        appClasses.filter { ctClass ->
            targetClassList.any { targetClass ->
                ctClass.refClasses.contains(targetClass)
            }
        }.filter {
            it.refClasses.all {
                var found: Boolean;
                try {
                    classPool[it as String]
                    found = true
                } catch (e: NotFoundException) {
                    found = false
                }
                found
            }
        }.forEach(action)
    }

    private inline fun forEachCanRecompileAppClass( action: (CtClass) -> Unit) {
        val appClasses = mCtClassInputMap.keys
        appClasses.filter {
            it.refClasses.all {
                var found: Boolean;
                try {
                    classPool[it as String]
                    found = true
                } catch (e: NotFoundException) {
                    found = false
                }
                found
            }
        }.forEach(action)
    }

    private fun step1_renameShadowClass() {
        forEachAppClass { ctClass ->
            RenameMap.forEach {
                ctClass.replaceClassName(it.key, it.value)
            }
        }

        // 替换跨插件apk创建view相关的类
        renameRemoteViewCreatorClass()
    }

    private fun step2_findFragments() {
        forEachAppClass { ctClass ->
            if (ctClass.isDialogFragment()) {
                mAppDialogFragments.add(ctClass)
            } else if (ctClass.isFragment()) {
                mAppFragments.add(ctClass)
            }
        }
    }

    private fun step3_renameFragments() {
        val fragmentsName = listOf(mAppFragments, mAppDialogFragments).flatten().flatMap { listOf(it.name) }
        forEachAppClass { ctClass ->
            fragmentsName.forEach { fragmentName ->
                ctClass.replaceClassName(fragmentName, fragmentName.appendFragmentAppendix())
            }
        }
        listOf(
                mAppFragments to containerFragmentCtClass,
                mAppDialogFragments to containerDialogFragmentCtClass
        ).forEach { (fragmentSet, container) ->
            fragmentSet.forEach {
                val inputClass = mCtClassInputMap[it]!!
                val originalFragmentName = it.name.removeFragmentAppendix()
                var ctClassOriginOutputFile: File? = null
                var ctClassOriginOutputEntryName: String? = null
                when (inputClass) {
                    is DirInputClass -> {
                        ctClassOriginOutputFile = inputClass.getOutput(originalFragmentName)
                    }
                    is JarInputClass -> {
                        ctClassOriginOutputEntryName = inputClass.getOutput(originalFragmentName)
                    }
                }

                inputClass.renameOutput(originalFragmentName, it.name)

                val newContainerFragmentCtClass = classPool.makeClass(originalFragmentName, container)
                when (inputClass) {
                    is DirInputClass -> {
                        inputClass.addOutput(newContainerFragmentCtClass.name, ctClassOriginOutputFile!!)
                    }
                    is JarInputClass -> {
                        inputClass.addOutput(newContainerFragmentCtClass.name, ctClassOriginOutputEntryName!!)
                    }
                }
            }
        }
    }

    private fun step4_redirectDialogMethod() {
        val dialogMethods = classPool[AndroidDialogClassname].methods!!
        val shadowDialogMethods = classPool[ShadowDialogClassname].methods!!
        val method_getOwnerActivity = dialogMethods.find { it.name == "getOwnerActivity" }!!
        val method_setOwnerActivity = dialogMethods.find { it.name == "setOwnerActivity" }!!
        val method_getOwnerPluginActivity = shadowDialogMethods.find { it.name == "getOwnerPluginActivity" }!!
        val method_setOwnerPluginActivity = shadowDialogMethods.find { it.name == "setOwnerPluginActivity" }!!
        //appClass中的Activity都已经被改名为ShadowActivity了．所以要把方法签名也先改一下．
        method_getOwnerActivity.copyDescriptorFrom(method_getOwnerPluginActivity)
        method_setOwnerActivity.copyDescriptorFrom(method_setOwnerPluginActivity)

        val codeConverter = CodeConverter()
        codeConverter.redirectMethodCall(method_getOwnerActivity, method_getOwnerPluginActivity)
        codeConverter.redirectMethodCall(method_setOwnerActivity, method_setOwnerPluginActivity)

        forEachCanRecompileAppClass(listOf(ShadowDialogClassname)) { appCtClass ->
            try {
                appCtClass.instrument(codeConverter)
            } catch (e: Exception) {
                System.err.println("处理" + appCtClass.name + "时出错")
                throw e
            }
        }
    }

    private fun step5_renameWebViewChildClass(){
        forEachCanRecompileAppClass { ctClass ->
            if (ctClass.superclass.name == AndroidWebViewClassname) {
                ctClass.classFile.superclass = ShadowWebViewClassname
            }
        }

        val codeConverter = CodeConverter()
        codeConverter.replaceNew(classPool[AndroidWebViewClassname], classPool[ShadowWebViewClassname])
        forEachCanRecompileAppClass(listOf(AndroidWebViewClassname)) { appCtClass ->
            try {
                appCtClass.instrument(codeConverter)
            } catch (e: Exception) {
                System.err.println("处理" + appCtClass.name + "时出错")
                throw e
            }
        }
    }

    private fun step6_redirectPendingIntentMethod(){
        val pendingIntentMethod = classPool[AndroidPendingIntentClassname].methods!!
        val shadowPendingIntentMethod = classPool[ShadowPendingIntentClassname].methods!!

        val method_getPengdingIntent = pendingIntentMethod.filter { it.name == "getService" || it.name == "getActivity" }
        val shadow_method_getPengdingIntent = shadowPendingIntentMethod.filter { it.name == "getService" || it.name == "getActivity"}!!
        val codeConverter = CodeConverter()

        for( ctAndroidMethod in method_getPengdingIntent) {
            for (ctShadowMedthod in shadow_method_getPengdingIntent) {
                if(ctShadowMedthod.methodInfo.name == ctAndroidMethod.methodInfo.name && ctAndroidMethod.methodInfo.descriptor == ctShadowMedthod.methodInfo.descriptor){
                    codeConverter.redirectMethodCall(ctAndroidMethod, ctShadowMedthod)
                }
            }
        }

        forEachCanRecompileAppClass(listOf(AndroidPendingIntentClassname)) { appCtClass ->
            try {
                appCtClass.instrument(codeConverter)
            } catch (e: Exception) {
                System.err.println("处理" + appCtClass.name + "时出错")
                throw e
            }
        }

    }

    private fun step7_redirectUriMethod() {
        val uriMethod = classPool[AndroidUriClassname].methods!!
        val shadowUriMethod = classPool[ShadowUriClassname].methods!!

        val method_parse = uriMethod.filter { it.name == "parse"  }
        val shadow_method_parse = shadowUriMethod.filter { it.name == "parse"}!!
        val codeConverter = CodeConverter()

        for( ctAndroidMethod in method_parse) {
            for (ctShadowMedthod in shadow_method_parse) {
                if( ctAndroidMethod.methodInfo.descriptor == ctShadowMedthod.methodInfo.descriptor){
                    codeConverter.redirectMethodCall(ctAndroidMethod, ctShadowMedthod)
                }
            }
        }

        forEachCanRecompileAppClass(listOf(AndroidUriClassname)) { appCtClass ->
            try {
                appCtClass.instrument(codeConverter)
            } catch (e: Exception) {
                System.err.println("处理" + appCtClass.name + "时出错")
                throw e
            }
        }

        val uriClass = classPool[AndroidUriClassname]
        val uriBuilderName = "android.net.Uri\$Builder"
        val uriBuilderClass = classPool[uriBuilderName]
        val buildMethod = uriBuilderClass.getMethod("build", Descriptor.ofMethod(uriClass, null))
        val newBuildMethod = classPool[ShadowUriClassname].getMethod("build", Descriptor.ofMethod(uriClass, arrayOf(uriBuilderClass)))
        val codeConverterExt = CodeConverterExtension()
        codeConverterExt.redirectMethodCallToStaticMethodCall(buildMethod, newBuildMethod)
        forEachCanRecompileAppClass(listOf(uriBuilderName)) { appCtClass ->
            try {
                appCtClass.instrument(codeConverterExt)
            } catch (e: Exception) {
                System.err.println("处理" + appCtClass.name + "时出错")
                throw e
            }
        }
    }

    private fun step8_redirectResolverMethod() {
        val codeConverter = CodeConverterExtension()
        val resolverName = "android.content.ContentResolver"
        val resolverClass = classPool[resolverName]
        val targetClass = classPool[ShadowUriClassname]
        val uriClass = classPool["android.net.Uri"]
        val stringClass = classPool["java.lang.String"]
        val bundleClass = classPool["android.os.Bundle"]
        val observerClass = classPool["android.database.ContentObserver"]

        val callMethod = resolverClass.getMethod("call", Descriptor.ofMethod(bundleClass,
                arrayOf(uriClass, stringClass, stringClass, bundleClass)))
        val newCallMethod = targetClass.getMethod("call", Descriptor.ofMethod(bundleClass,
                arrayOf(resolverClass, uriClass, stringClass, stringClass, bundleClass)))
        codeConverter.redirectMethodCallToStaticMethodCall(callMethod, newCallMethod)

        val notifyMethod1 = resolverClass.getMethod("notifyChange", Descriptor.ofMethod(CtClass.voidType,
                arrayOf(uriClass, observerClass)))
        val newNotifyMethod1 = targetClass.getMethod("notifyChange", Descriptor.ofMethod(CtClass.voidType,
                arrayOf(resolverClass, uriClass, observerClass)))
        codeConverter.redirectMethodCallToStaticMethodCall(notifyMethod1, newNotifyMethod1)

        val notifyMethod2 = resolverClass.getMethod("notifyChange", Descriptor.ofMethod(CtClass.voidType,
                arrayOf(uriClass, observerClass, CtClass.booleanType)))
        val newNotifyMethod2 = targetClass.getMethod("notifyChange", Descriptor.ofMethod(CtClass.voidType,
                arrayOf(resolverClass, uriClass, observerClass, CtClass.booleanType)))
        codeConverter.redirectMethodCallToStaticMethodCall(notifyMethod2, newNotifyMethod2)

        val notifyMethod3 = resolverClass.getMethod("notifyChange", Descriptor.ofMethod(CtClass.voidType,
                arrayOf(uriClass, observerClass, CtClass.intType)))
        val newNotifyMethod3 = targetClass.getMethod("notifyChange", Descriptor.ofMethod(CtClass.voidType,
                arrayOf(resolverClass, uriClass, observerClass, CtClass.intType)))
        codeConverter.redirectMethodCallToStaticMethodCall(notifyMethod3, newNotifyMethod3)

        forEachCanRecompileAppClass(listOf(resolverName)) { appCtClass ->
            try {
                appCtClass.instrument(codeConverter)
            } catch (e: Exception) {
                System.err.println("处理" + appCtClass.name + "时出错")
                throw e
            }
        }
    }

    private fun step9_keepHostContext() {
        val ShadowContextClassName = "com.tencent.shadow.runtime.ShadowContext"

        data class Rule(
                val ctClass: CtClass
                , val ctMethod: CtMethod
                , val convertArgs: Array<Int>
        )

        fun parseKeepHostContextRules(): List<Rule> {
            fun String.assertRuleHasOnlyOneChar(char: Char) {
                if (this.count { it == char } != 1) {
                    throw IllegalArgumentException("rule:${this}中\'$char\'的数量不为1")
                }
            }

            val appClasses = mCtClassInputMap.keys

            return useHostContext().map { rule ->
                rule.assertRuleHasOnlyOneChar('(')
                rule.assertRuleHasOnlyOneChar(')')

                val indexOfLeftParenthesis = rule.indexOf('(')
                val indexOfRightParenthesis = rule.indexOf(')')

                val classNameAndMethodNamePart = rule.substring(0, indexOfLeftParenthesis)
                val indexOfLastDot = classNameAndMethodNamePart.indexOfLast { it == '.' }

                val className = classNameAndMethodNamePart.substring(0, indexOfLastDot)
                val methodName = classNameAndMethodNamePart.substring(indexOfLastDot + 1, indexOfLeftParenthesis)
                val methodParametersClassName = rule.substring(indexOfLeftParenthesis + 1, indexOfRightParenthesis).split(',')
                val keepSpecifying = rule.substring(indexOfRightParenthesis + 1)

                val ctClass = appClasses.find {
                    it.name == className
                } ?: throw ClassNotFoundException("没有找到${rule}中指定的类$className")

                val parametersCtClass = methodParametersClassName.map {
                    classPool.getOrNull(it)
                            ?: throw ClassNotFoundException("没有找到${rule}中指定的类$it")
                }.toTypedArray()
                val ctMethod = ctClass.getDeclaredMethod(methodName, parametersCtClass)

                val tmp = keepSpecifying.split('$')
                val convertArgs = tmp.subList(1, tmp.size).map { Integer.parseInt(it) }.toTypedArray()

                Rule(ctClass, ctMethod, convertArgs)
            }
        }

        val rules = parseKeepHostContextRules()

        fun wrapArg(num: Int): String = "(($ShadowContextClassName)\$${num}).getBaseContext()"

        for (rule in rules) {
            val ctClass = rule.ctClass
            val ctMethod = rule.ctMethod
            val cloneMethod = CtNewMethod.copy(ctMethod, ctMethod.name + "_KeepHostContext", ctClass, null)

            val newBodyBuilder = StringBuilder()
            newBodyBuilder.append("${ctMethod.name}(")
            for (i in 1..cloneMethod.parameterTypes.size) {//从1开始是因为在Javassist中$0表示this,$1表示第一个参数
                if (i > 1) {
                    newBodyBuilder.append(',')
                }
                if (i in rule.convertArgs) {
                    newBodyBuilder.append(wrapArg(i))
                } else {
                    newBodyBuilder.append("\$${i}")
                }
            }
            newBodyBuilder.append(");")

            cloneMethod.setBody(newBodyBuilder.toString())
            ctClass.addMethod(cloneMethod)

            val codeConverter = CodeConverter()
            codeConverter.redirectMethodCall(ctMethod, cloneMethod)
            forEachCanRecompileAppClass(listOf(ctClass.name)) {
                if (it != ctClass)
                    it.instrument(codeConverter)
            }
        }
    }

    private fun step10_redirectPackageManagerMethod() {
        val method_targets = getTargetMethods(arrayOf(AndroidPackageManagerClassname), arrayOf("getActivityInfo", "getPackageInfo", "resolveContentProvider"))

        forEachCanRecompileAppClass(listOf(AndroidPackageManagerClassname)) { appCtClass ->
            val codeConverter = CodeConverterExtension()
            for (method_target in method_targets) {
                addStaticRedirectMethodIfNeed(classPool[AndroidPackageManagerClassname], method_target, appCtClass, ShadowAndroidPackageManagerClassname, codeConverter)
            }
        }

    }

    private fun step11_redirectPackageItemInfoMethod() {
        val targetClassNames = arrayOf(AndroidProviderInfo, AndroidServiceInfo, AndroidApplicationInfo, AndroidActivityInfo);
        val method_targets = getTargetMethods(targetClassNames, arrayOf("loadXmlMetaData"))

        for (targetClassName in targetClassNames) {
            forEachCanRecompileAppClass(listOf(targetClassName)) { appCtClass ->
                val codeConverter = CodeConverterExtension()
                for (method_target in method_targets) {
                    addStaticRedirectMethodIfNeed(classPool[AndroidPackageItemInfo], method_target, appCtClass, ShadowAndroidPackageItemInfo, codeConverter)
                }
            }
        }


    }

    private fun addStaticRedirectMethodIfNeed(targetClass: CtClass, method_target: CtMethod, appCtClass: CtClass, redirectClassName: String, codeConverter: CodeConverterExtension) {
        if (matchMethod(method_target, appCtClass)) {
            System.out.println(appCtClass.name + " matchMethod :" + method_target.methodInfo.name + "  =================")
            try {
                val parameterTypes: Array<CtClass> = Array(method_target.parameterTypes.size + 1) { index ->
                    if (index == 0) {
                        targetClass
                    } else {
                        method_target.parameterTypes[index - 1]
                    }
                }
                val newMethod = CtNewMethod.make(Modifier.PUBLIC or Modifier.STATIC, method_target.returnType, method_target.name + "_shadow", parameterTypes, method_target.exceptionTypes, null, appCtClass)
                val newBodyBuilder = StringBuilder()
                newBodyBuilder.append("return " + redirectClassName + "." + method_target.methodInfo.name + "(" + appCtClass.name + ".class.getClassLoader(),")
                for (i in 1..newMethod.parameterTypes.size) {
                    if (i > 1) {
                        newBodyBuilder.append(',')
                    }
                    newBodyBuilder.append("\$${i}")
                }
                newBodyBuilder.append(");")

                newMethod.setBody(newBodyBuilder.toString())
                appCtClass.addMethod(newMethod)
                codeConverter.redirectMethodCallToStaticMethodCall(method_target, newMethod)
                appCtClass.instrument(codeConverter)
            } catch (e: Exception) {
                System.err.println("处理" + appCtClass.name + "时出错:" + e)
                throw e
            }
        }
    }

    /**
     * 查找目标class对象的目标method
     */
    fun getTargetMethods(targetClassNames: Array<String>, targetMethodName: Array<String>): List<CtMethod> {
        val method_targets = ArrayList<CtMethod>()
        for (targetClassName in targetClassNames) {
            val methods = classPool[targetClassName].methods
            method_targets.addAll(methods.filter { targetMethodName.contains(it.name) })
        }
        return method_targets
    }


    /**
     * 查找目标class是否存在目标method的调用
     */
    fun matchMethod(ctMethod: CtMethod, clazz: CtClass): Boolean {
        for (methodInfo in clazz.classFile2.methods) {
            methodInfo as MethodInfo
            val codeAttr: CodeAttribute? = methodInfo.codeAttribute
            val constPool = methodInfo.constPool
            if (codeAttr != null) {
                val iterator = codeAttr.iterator()
                while (iterator.hasNext()) {
                    val pos = iterator.next()
                    val c = iterator.byteAt(pos)
                    if (c == Opcode.INVOKEINTERFACE || c == Opcode.INVOKESPECIAL
                            || c == Opcode.INVOKESTATIC || c == Opcode.INVOKEVIRTUAL) {
                        val index = iterator.u16bitAt(pos + 1)
                        val cname = constPool.eqMember(ctMethod.name, ctMethod.methodInfo2.descriptor, index)
                        val className = ctMethod.declaringClass.name
                        val matched = cname != null && matchClass(ctMethod.name, ctMethod.methodInfo.descriptor, className, cname, clazz.classPool)
                        if (matched) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun matchClass(methodName: String, methodDescriptor: String, classname: String, name: String, pool: ClassPool): Boolean {
        if (classname == name)
            return true

        try {
            val clazz = pool.get(name)
            val declClazz = pool.get(classname)
            if (clazz.subtypeOf(declClazz))
                try {
                    val m = clazz.getMethod(methodName, methodDescriptor)
                    return m.declaringClass.name == classname
                } catch (e: NotFoundException) {
                    // maybe the original method has been removed.
                    return true
                }

        } catch (e: NotFoundException) {
            return false
        }

        return false
    }

    private fun CtMethod.copyDescriptorFrom(other: CtMethod) {
        methodInfo.descriptor = other.methodInfo.descriptor
    }

    private fun String.appendFragmentAppendix() = this + "_"

    private fun String.removeFragmentAppendix() = this.substring(0, this.length - 1)

    private fun CtClass.isClassOf(className: String): Boolean {
        var tmp: CtClass? = this
        do {
            if (tmp?.name == className) {
                return true
            }
            try {
                tmp = tmp?.superclass
            } catch (e: NotFoundException) {
                return false
            }
        } while (tmp != null)
        return false
    }

    private fun CtClass.isFragment(): Boolean = isClassOf(ShadowFragmentClassname)
    private fun CtClass.isDialogFragment(): Boolean = isClassOf(ShadowDialogFragmentClassname)

    override fun getName(): String = "ShadowTransform"
}