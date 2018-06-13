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
        val FragmentCtClassCache = mutableSetOf<String>()
    }

    val ContainerFragmentCtClass = classPool["com.tencent.cubershi.mock_interface.ContainerFragment"]

    override fun onTransform() {
        step1()
        step2()
    }

    private fun step1() {
        val appClasses = mCtClassInputMap.keys
        appClasses.forEach { ctClass ->
            RenameMap.forEach {
                ctClass.replaceClassName(it.key, it.value)
            }
        }
    }

    private fun step2() {
        val appClasses = mCtClassInputMap.keys
        appClasses.forEach { ctClass ->
            val inputClass = mCtClassInputMap[ctClass]!!
            val ctClassOriginName = ctClass.name
            var ctClassOriginOutputFile: File? = null
            var ctClassOriginOutputEntryName: String? = null
            when (inputClass) {
                is DirInputClass -> {
                    ctClassOriginOutputFile = inputClass.getOutput(ctClass.name)
                }
                is JarInputClass -> {
                    ctClassOriginOutputEntryName = inputClass.getOutput(ctClass.name)
                }
            }

            renameFragment(ctClass)
            if (ctClass.name != ctClassOriginName) {
                inputClass.renameOutput(ctClassOriginName, ctClass.name)
            }
            if (ctClass.isFragment()) {
                val newContainerFragmentCtClass = classPool.makeClass(ctClassOriginName, ContainerFragmentCtClass)
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

    private fun String.isMockFragmentClassname() =
            (this == MockFragmentClassname) or (this == MockDialogFragmentClassname)

    private fun renameFragment(ctClass: CtClass) {
        ctClass.refClasses.forEach {
            val refClassName: String = it as String
            if (refClassName.isMockFragmentClassname()) {
                return@forEach
            }
            if (refClassName in FragmentCtClassCache) {
                ctClass.replaceFragmentName(refClassName)
            } else if (classPool.getOrNull(refClassName) != null
                    && classPool.getOrNull(refClassName).isFragment()) {
                FragmentCtClassCache.add(refClassName)
                ctClass.replaceFragmentName(refClassName)
            }
        }
    }

    private fun CtClass.replaceFragmentName(fragmentClassName: String) {
        this.replaceClassName(fragmentClassName, fragmentClassName + "_")
    }

    private fun CtClass.isFragment(): Boolean {
        var tmp: CtClass? = this
        do {
            if (tmp?.name?.isMockFragmentClassname() == true) {
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

    override fun getName(): String = "CuberPluginLoaderTransform"
}