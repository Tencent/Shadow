package com.tencent.cubershi

import com.tencent.cubershi.transformkit.DirInputClass
import com.tencent.cubershi.transformkit.JarInputClass
import com.tencent.cubershi.transformkit.JavassistTransform
import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import java.io.File

class CuberPluginLoaderTransform(classPool: ClassPool) : JavassistTransform(classPool) {

    companion object {
        const val MockFragmentClassname = "com.tencent.cubershi.mock_interface.MockFragment"
        const val MockDialogFragmentClassname = "com.tencent.cubershi.mock_interface.MockDialogFragment"
        val RenameMap = mapOf(
                "android.app.Application"
                        to "com.tencent.cubershi.mock_interface.MockApplication"
                ,
                "android.app.Activity"
                        to "com.tencent.cubershi.mock_interface.MockActivity"
                ,
                "android.app.Service"
                        to "com.tencent.cubershi.mock_interface.MockService"
                ,
                "android.app.Fragment"
                        to MockFragmentClassname
                ,
                "android.app.DialogFragment"
                        to MockDialogFragmentClassname
                ,
                "android.app.FragmentManager"
                        to "com.tencent.cubershi.mock_interface.PluginFragmentManager"
                ,
                "android.app.FragmentTransaction"
                        to "com.tencent.cubershi.mock_interface.PluginFragmentTransaction"
                ,
                "android.app.Application\$ActivityLifecycleCallbacks"
                        to "com.tencent.cubershi.mock_interface.MockActivityLifecycleCallbacks"
        )
    }

    val ContainerFragmentCtClass = classPool["com.tencent.cubershi.mock_interface.ContainerFragment"]
    val ContainerDialogFragmentCtClass = classPool["com.tencent.cubershi.mock_interface.ContainerDialogFragment"]

    val mAppFragments: MutableSet<CtClass> = mutableSetOf()
    val mAppDialogFragments: MutableSet<CtClass> = mutableSetOf()

    override fun onTransform() {
        step1_renameMockClass()
        step2_findFragments()
        step3_renameFragments()
    }

    private fun step1_renameMockClass() {
        val appClasses = mCtClassInputMap.keys
        appClasses.forEach { ctClass ->
            RenameMap.forEach {
                ctClass.replaceClassName(it.key, it.value)
            }
        }
    }

    private fun step2_findFragments() {
        val appClasses = mCtClassInputMap.keys
        appClasses.forEach { ctClass ->
            if (ctClass.isDialogFragment()) {
                mAppDialogFragments.add(ctClass)
            } else if (ctClass.isFragment()) {
                mAppFragments.add(ctClass)
            }
        }
    }

    private fun step3_renameFragments() {
        val appClasses = mCtClassInputMap.keys
        val fragmentsName = listOf(mAppFragments, mAppDialogFragments).flatten().flatMap { listOf(it.name) }
        appClasses.forEach { ctClass ->
            fragmentsName.forEach { fragmentName ->
                ctClass.replaceClassName(fragmentName, fragmentName.appendFragmentAppendix())
            }
        }
        listOf(
                mAppFragments to ContainerFragmentCtClass,
                mAppDialogFragments to ContainerDialogFragmentCtClass
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

    private fun CtClass.isFragment(): Boolean = isClassOf(MockFragmentClassname)
    private fun CtClass.isDialogFragment(): Boolean = isClassOf(MockDialogFragmentClassname)

    override fun getName(): String = "CuberPluginLoaderTransform"
}