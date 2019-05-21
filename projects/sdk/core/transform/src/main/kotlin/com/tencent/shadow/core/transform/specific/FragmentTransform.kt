package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform_kit.*
import javassist.CtClass
import java.io.File

class FragmentTransform(val mCtClassInputMap: Map<CtClass, InputClass>) : SpecificTransform() {
    companion object {
        const val FragmentClassname = "android.app.Fragment"
        const val ShadowFragmentClassname = "com.tencent.shadow.core.runtime.ShadowFragment"
        const val DialogFragmentClassname = "android.app.DialogFragment"
        const val ShadowDialogFragmentClassname = "com.tencent.shadow.core.runtime.ShadowDialogFragment"
        const val ContainerFragmentClassname = "com.tencent.shadow.core.runtime.ContainerFragment"
        const val ContainerDialogFragmentClassname = "com.tencent.shadow.core.runtime.ContainerDialogFragment"
    }

    val RenameMap = mapOf(
            FragmentClassname
                    to ShadowFragmentClassname
            ,
            DialogFragmentClassname
                    to ShadowDialogFragmentClassname
            ,
            "android.app.FragmentManager"
                    to "com.tencent.shadow.core.runtime.PluginFragmentManager"
            ,
            "android.app.FragmentTransaction"
                    to "com.tencent.shadow.core.runtime.PluginFragmentTransaction"
    )

    val mAppFragments: MutableSet<CtClass> = mutableSetOf()
    val mAppDialogFragments: MutableSet<CtClass> = mutableSetOf()

    /**
     * 当前Transform的App中的Fragment的父类，不在当前Transform的App中，但它是Fragment，记录在这个集合。
     */
    val mRuntimeSuperclassFragments: MutableSet<CtClass> = mutableSetOf()

    private fun CtClass.isFragment(): Boolean = isClassOf(FragmentClassname)

    private fun CtClass.isDialogFragment(): Boolean = isClassOf(DialogFragmentClassname)

    private fun String.appendFragmentAppendix() = this + "_"

    private lateinit var fragmentsName: List<String>

    override fun setup(allInputClass: Set<CtClass>) {
        //收集哪些当前Transform的App类是Fragment
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

        //收集不在当前Transform的App中的类，但它是Fragment.只关心App中的Fragment的父类即可。
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> =
                    listOf<Set<CtClass>>(mAppDialogFragments, mAppFragments).flatten().toSet()

            override fun transform(ctClass: CtClass) {
                val superclass = ctClass.superclass
                if (superclass !in mAppDialogFragments
                        && superclass !in mAppFragments
                        && superclass.isFragment()
                        && superclass.name != FragmentClassname
                        && superclass.name != DialogFragmentClassname
                ) {
                    mRuntimeSuperclassFragments.add(superclass)
                }
            }
        })

        //替换App中出现的所有名字
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> = allInputClass

            override fun transform(ctClass: CtClass) {
                RenameMap.forEach {
                    ReplaceClassName.replaceClassName(ctClass, it.key, it.value)
                }
            }
        })

        //将App中所有Fragment名字都加上后缀
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> {
                val flattenList = listOf(
                        mAppFragments,
                        mAppDialogFragments,
                        mRuntimeSuperclassFragments
                ).flatten()

                fragmentsName = flattenList.flatMap { listOf(it.name) }

                return flattenList.toSet()
            }

            override fun transform(ctClass: CtClass) {
                val fragmentName = ctClass.name
                ReplaceClassName.replaceClassName(ctClass, fragmentName, fragmentName.appendFragmentAppendix())
            }
        })

        //将App中所有对Fragment的引用也都改为加上后缀名的
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> {
                return allInputClass
            }

            override fun transform(ctClass: CtClass) {
                fragmentsName.forEach { fragmentName ->
                    ReplaceClassName.replaceClassName(ctClass, fragmentName, fragmentName.appendFragmentAppendix())
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