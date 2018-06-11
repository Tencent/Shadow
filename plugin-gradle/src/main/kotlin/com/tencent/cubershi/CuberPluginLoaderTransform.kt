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
        const val AndroidApplicationClassname = "android.app.Application"
        const val MockApplicationClassname = "com.tencent.cubershi.mock_interface.MockApplication"
        const val AndroidActivityClassname = "android.app.Activity"
        const val MockActivityClassname = "com.tencent.cubershi.mock_interface.MockActivity"
        const val AndroidServiceClassname = "android.app.Service"
        const val MockServiceClassname = "com.tencent.cubershi.mock_interface.MockService"
        const val AndroidFragmentClassname = "android.app.Fragment"
        const val MockFragmentClassname = "com.tencent.cubershi.mock_interface.MockFragment"
        const val AndroidFragmentManagerClassname = "android.app.FragmentManager"
        const val PluginFragmentManagerClassname = "com.tencent.cubershi.mock_interface.PluginFragmentManager"
        const val AndroidFragmentTransactionClassname = "android.app.FragmentTransaction"
        const val PluginFragmentTransactionClassname = "com.tencent.cubershi.mock_interface.PluginFragmentTransaction"
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
            ctClass.replaceClassName(AndroidActivityClassname, MockActivityClassname)
            ctClass.replaceClassName(AndroidApplicationClassname, MockApplicationClassname)
            ctClass.replaceClassName(AndroidServiceClassname, MockServiceClassname)
            ctClass.replaceClassName(AndroidFragmentClassname, MockFragmentClassname)
            ctClass.replaceClassName(AndroidFragmentManagerClassname, PluginFragmentManagerClassname)
            ctClass.replaceClassName(AndroidFragmentTransactionClassname, PluginFragmentTransactionClassname)
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

    private fun renameFragment(ctClass: CtClass) {
        ctClass.refClasses.forEach {
            val refClassName: String = it as String
            if (refClassName == MockFragmentClassname) {
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
            if (tmp?.name == MockFragmentClassname) {
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