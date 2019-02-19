package com.tencent.shadow.core.transform

import com.tencent.shadow.core.transform.common.Transform
import com.tencent.shadow.core.transform.common.TransformStep
import com.tencent.shadow.core.transformkit.DirInputClass
import com.tencent.shadow.core.transformkit.InputClass
import com.tencent.shadow.core.transformkit.JarInputClass
import javassist.CtClass
import java.io.File

class FragmentTransform(val mCtClassInputMap: Map<CtClass, InputClass>) : Transform() {
    companion object {
        const val ShadowFragmentClassname = "com.tencent.shadow.runtime.ShadowFragment"
        const val ShadowDialogFragmentClassname = "com.tencent.shadow.runtime.ShadowDialogFragment"
        const val ContainerFragmentClassname = "com.tencent.shadow.runtime.ContainerFragment"
        const val ContainerDialogFragmentClassname = "com.tencent.shadow.runtime.ContainerDialogFragment"
    }

    val RenameMap = mapOf(
            "android.app.Fragment"
                    to ShadowFragmentClassname
            ,
            "android.app.DialogFragment"
                    to ShadowDialogFragmentClassname
    )

    val mAppFragments: MutableSet<CtClass> = mutableSetOf()
    val mAppDialogFragments: MutableSet<CtClass> = mutableSetOf()

    private fun CtClass.isFragment(): Boolean = isClassOf(ShadowFragmentClassname)

    private fun CtClass.isDialogFragment(): Boolean = isClassOf(ShadowDialogFragmentClassname)

    private fun String.appendFragmentAppendix() = this + "_"

    override fun setup(allInputClass: Set<CtClass>) {
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> = allInputClass

            override fun transform(ctClass: CtClass) {
                RenameMap.forEach {
                    ctClass.replaceClassName(it.key, it.value)
                }
            }
        })

        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> = allInputClass

            override fun transform(ctClass: CtClass) {
                if (ctClass.isDialogFragment()) {
                    mAppDialogFragments.add(ctClass)
                } else if (ctClass.isFragment()) {
                    mAppFragments.add(ctClass)
                }
            }
        })

        newStep(object : TransformStep {
            lateinit var fragmentsName: List<String>

            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> {
                fragmentsName = listOf(mAppFragments, mAppDialogFragments)
                        .flatten()
                        .flatMap { listOf(it.name) }
                return allInputClass
            }

            override fun transform(ctClass: CtClass) {
                fragmentsName.forEach { fragmentName ->
                    ctClass.replaceClassName(fragmentName, fragmentName.appendFragmentAppendix())
                }
            }

        })

        newStep(MakeContainerStep(mAppFragments, mClassPool[ContainerFragmentClassname]))
        newStep(MakeContainerStep(mAppDialogFragments, mClassPool[ContainerDialogFragmentClassname]))
    }

    inner class MakeContainerStep(private val inputClass: Set<CtClass>,
                                  private val container: CtClass)
        : TransformStep {
        override fun filter(allInputClass: Set<CtClass>) = inputClass

        override fun transform(ctClass: CtClass) {
            val originalFragmentName = ctClass.name.removeFragmentAppendix()
            val newContainerFragmentCtClass = mClassPool.makeClass(originalFragmentName, container)

            val outputControl = mCtClassInputMap[ctClass]!!
            var ctClassOriginOutputFile: File? = null
            var ctClassOriginOutputEntryName: String? = null
            when (outputControl) {
                is DirInputClass -> {
                    ctClassOriginOutputFile = outputControl.getOutput(originalFragmentName)
                }
                is JarInputClass -> {
                    ctClassOriginOutputEntryName = outputControl.getOutput(originalFragmentName)
                }
            }

            outputControl.renameOutput(originalFragmentName, ctClass.name)

            when (outputControl) {
                is DirInputClass -> {
                    outputControl.addOutput(newContainerFragmentCtClass.name, ctClassOriginOutputFile!!)
                }
                is JarInputClass -> {
                    outputControl.addOutput(newContainerFragmentCtClass.name, ctClassOriginOutputEntryName!!)
                }
            }
        }

        private fun String.removeFragmentAppendix() = this.substring(0, this.length - 1)
    }
}