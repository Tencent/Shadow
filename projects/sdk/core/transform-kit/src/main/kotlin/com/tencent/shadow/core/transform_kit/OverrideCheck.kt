package com.tencent.shadow.core.transform_kit

import com.android.build.gradle.internal.utils.toImmutableMap
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.NotFoundException

typealias Method_OriginalDeclaringClass = Pair<CtMethod, String>

class OverrideCheck {
    /**
     * key:Transform前的类名
     * value：Override的方法
     */
    private val methodMap: MutableMap<String, Collection<Method_OriginalDeclaringClass>> =
        hashMapOf()

    /**
     * 这个map用于应对Transform后类名变化的情况
     * Transform后，check时，从CtClass取出新类名
     *
     * key:Transform前的类名
     * value: CtClass
     */
    private val ctClassMap: MutableMap<String, CtClass> = hashMapOf()

    private fun String.isKotlinClass(): Boolean {
        return startsWith("kotlin")
    }

    fun prepare(inputClasses: Set<CtClass>) {
        methodMap.clear()
        ctClassMap.clear()

        inputClasses
            .filter { it.packageName != null }
            .filter {
                //kotlinx里有一些方法的覆盖检查不出来，反正我们也不改它，就不检查了。
                it.packageName.isKotlinClass().not()
            }.filter {
                try {
                    it.methods
                    it.superclass
                    true
                } catch (e: NotFoundException) {
                    false
                }
            }.forEach { clazz ->
                val name = clazz.name
                try {
                    methodMap[name] = clazz.findAllOverrideMethods()
                    ctClassMap[name] = clazz
                } catch (e: Exception) {
                    throw RuntimeException("处理${name}时发生错误", e)
                }
            }
    }

    fun makeNewNameToOldNameMap(): HashMap<String, String> {
        val newNameToOldName = hashMapOf<String, String>()
        ctClassMap.forEach {
            newNameToOldName[it.value.name] = it.key
        }
        return newNameToOldName
    }

    fun getOverrideMethods() = methodMap.toImmutableMap()

    fun check(
        debugClassPool: ClassPool,
        classNames: List<String>
    ): Map<String, List<Method_OriginalDeclaringClass>> {
        val newNameToOldName = makeNewNameToOldNameMap()

        val errorResult: HashMap<String, MutableList<Method_OriginalDeclaringClass>> = hashMapOf()
        classNames
            .filter { it.contains(".") }
            .filter {
                it.isKotlinClass().not()
            }.filter {
                val clazz = debugClassPool[it]!!
                try {
                    clazz.methods
                    clazz.superclass
                    true
                } catch (e: NotFoundException) {
                    false
                }
            }.forEach { className ->
                try {
                    val oldName = newNameToOldName[className]!!
                    val methods = methodMap[oldName]!!
                    val clazz = debugClassPool[className]!!
                    methods.forEach {
                        val isOverride = clazz.isMethodOverride(it.first)
                        if (!isOverride) {
                            var list = errorResult[className]
                            if (list == null) {
                                list = mutableListOf()
                                errorResult[className] = list
                            }
                            list.add(it)
                        }
                    }
                } catch (e: RuntimeException) {
                    throw RuntimeException("className==$className", e)
                }

            }
        return errorResult
    }

    companion object {
        private fun CtClass.findAllOverrideMethods(): List<Pair<CtMethod, String>> {
            val methodsDeclaredInClass = mutableListOf<Pair<CtMethod, String>>()
            declaredMethods.forEach {
                try {
                    val methodOfSuperClass = superclass.getMethod(it.name, it.signature)
                    methodsDeclaredInClass.add(it to methodOfSuperClass.declaringClass.name)
                } catch (ignored: NotFoundException) {
                    try {
                        val methodOfSuperClass = superclass.getMethod(it.name, it.genericSignature)
                        methodsDeclaredInClass.add(it to methodOfSuperClass.declaringClass.name)
                    } catch (ignored: NotFoundException) {
                    }
                }
            }
            return methodsDeclaredInClass
        }

        private fun CtClass.hasSameMethod(m: CtMethod) =
            try {
                try {
                    getMethod(m.name, m.signature)
                    true
                } catch (ignored: NotFoundException) {
                    getMethod(m.name, m.genericSignature)
                    true
                }
            } catch (ignored: NotFoundException) {
                false
            }

        private fun CtClass.isMethodOverride(originMethod: CtMethod) =
            superclass.hasSameMethod(originMethod)
    }
}