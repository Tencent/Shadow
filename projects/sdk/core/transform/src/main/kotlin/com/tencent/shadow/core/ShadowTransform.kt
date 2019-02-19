package com.tencent.shadow.core

import com.android.build.api.transform.TransformInvocation
import com.tencent.shadow.core.transform.TransformManager
import com.tencent.shadow.core.transformkit.*
import javassist.CodeConverter
import javassist.CtClass
import javassist.NotFoundException
import javassist.bytecode.Descriptor
import org.gradle.api.Project
import java.io.File

class ShadowTransform(project: Project, classPoolBuilder: ClassPoolBuilder, val useHostContext: () -> Array<String>) : JavassistTransform(project, classPoolBuilder) {

    companion object {
        const val ShadowFragmentClassname = "com.tencent.shadow.runtime.ShadowFragment"
        const val ShadowDialogFragmentClassname = "com.tencent.shadow.runtime.ShadowDialogFragment"
        const val ShadowUriClassname = "com.tencent.shadow.runtime.UriConverter"
        const val AndroidUriClassname = "android.net.Uri"
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
                "android.app.Instrumentation"
                        to "com.tencent.shadow.runtime.ShadowInstrumentation"
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

        val transformManager = TransformManager(mCtClassInputMap, classPool, useHostContext)
        transformManager.fireAll()

        step7_redirectUriMethod()
        step8_redirectResolverMethod()
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