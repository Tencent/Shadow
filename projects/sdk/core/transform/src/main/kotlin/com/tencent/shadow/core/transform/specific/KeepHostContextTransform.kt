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
import javassist.CodeConverter
import javassist.CtClass
import javassist.CtMethod
import javassist.CtNewMethod

class KeepHostContextTransform(private val rules: Array<String>) : SpecificTransform() {
    companion object {
        const val ShadowContextClassName = "com.tencent.shadow.core.runtime.ShadowContext"
    }

    data class Rule(
            val ctClass: CtClass
            , val ctMethod: CtMethod
            , val convertArgs: Array<Int>
    )

    private fun String.assertRuleHasOnlyOneChar(char: Char) {
        if (this.count { it == char } != 1) {
            throw IllegalArgumentException("rule:${this}中\'$char\'的数量不为1")
        }
    }

    private fun parseKeepHostContextRules(appClasses: Set<CtClass>): List<Rule> {
        return rules.map { rule ->
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
                mClassPool.getOrNull(it)
                        ?: throw ClassNotFoundException("没有找到${rule}中指定的类$it")
            }.toTypedArray()
            val ctMethod = ctClass.getDeclaredMethod(methodName, parametersCtClass)

            val tmp = keepSpecifying.split('$')
            val convertArgs = tmp.subList(1, tmp.size).map { Integer.parseInt(it) }.toTypedArray()

            Rule(ctClass, ctMethod, convertArgs)
        }
    }

    private fun wrapArg(num: Int): String = "(($ShadowContextClassName)\$${num}).getBaseContext()"

    override fun setup(allInputClass: Set<CtClass>) {
        val rules = parseKeepHostContextRules(allInputClass)

        for (rule in rules) {
            val targetClass = rule.ctClass
            val ctMethod = rule.ctMethod
            val cloneMethod = CtNewMethod.copy(ctMethod, ctMethod.name + "_KeepHostContext", targetClass, null)

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
            targetClass.addMethod(cloneMethod)

            val codeConverter = CodeConverter()
            codeConverter.redirectMethodCall(ctMethod, cloneMethod)

            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) =
                        filterRefClasses(allInputClass, listOf(targetClass.name))

                override fun transform(ctClass: CtClass) {
                    if (ctClass != targetClass)
                        ctClass.instrument(codeConverter)
                }
            })
        }
    }
}