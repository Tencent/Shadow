@file:Suppress("DEPRECATION")

package com.tencent.shadow.coding.code_generator

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.FragmentManager
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.Window
import com.squareup.javapoet.*
import javassist.ClassMap
import javassist.ClassPool
import javassist.CtMethod
import javassist.bytecode.Descriptor
import java.io.File
import java.lang.reflect.Method
import javax.lang.model.element.Modifier

/**
 * Activity相关代码生成逻辑
 *
 * 这个逻辑应该可以进一步抽象成任意组件的相关代码生成逻辑，不局限于Activity
 *
 * 下面解释相关代码指的是什么。
 *
 * 假设业务有Activity名为BizActivity。那么原本BizActivity extends Activity。
 * 在Shadow的方案中，也就是代理的方案中，需要宿主中有一个PluginContainerActivity，
 * PluginContainerActivity extends Activity。然后将BizActivity改为
 * BizActivity extends ShadowActivity。其中ShadowActivity extends PluginActivity。
 * 然后让PluginContainerActivity implements HostActivityDelegator接口，
 * 让Loader中的ShadowActivityDelegate implements HostActivityDelegate接口。
 *
 * 然后我们要将Activity上的方法分成3类：
 * 1.私有方法，BizActivity原本也不会访问到这些方法，这些方法我们不用管。
 *
 * 2.系统会调用的方法，如onCreate等生命周期回调，这些方法需要由PluginContainerActivity
 * 转调给HostActivityDelegate，HostActivityDelegate再转调给ShadowActivity，
 * ShadowActivity再转调给HostActivityDelegator接口上定义的super前缀方法，
 * super前缀方法实现为调用原本的super相应方法。这样一来，系统调用到PluginContainerActivity
 * 上的方法，就可以被BizActivity响应，并且控制何时调用super方法。
 *
 * 3.插件会调用的方法，如setContentView，这些方法需要在ShadowActivity中
 * 声明出一样的方法，并且实现为调用HostActivityDelegator接口上的同名方法。以便
 * BizActivity在Override后通过super调用还能够调用到原本的Activity父类实现上。
 * 与"系统会调用的方法"相比，省去了super前缀方法转调，因为PluginContainerActivity
 * 不需要Override这些方法。
 *
 * 其中涉及被Shadow Transform修改了类型的方法，需要将相关代码的类型也修改掉。比如，
 * getParent方法的返回类型需要改为ShadowActivity。
 *
 */
class ActivityCodeGenerator {

    companion object {
        const val ACTIVITY_CONTAINER_PACKAGE = "com.tencent.shadow.core.runtime.container"
        const val RUNTIME_PACKAGE = "com.tencent.shadow.core.runtime"
        const val DELEGATE_PACKAGE = "com.tencent.shadow.core.loader.delegates"

        const val PREFIX = "Generated"
        //CS:const string
        const val CS_HostActivityDelegate = "${PREFIX}HostActivityDelegate"
        const val CS_HostActivityDelegator = "${PREFIX}HostActivityDelegator"
        const val CS_PluginContainerActivity = "${PREFIX}PluginContainerActivity"
        const val CS_PluginActivity = "${PREFIX}PluginActivity"
        const val CS_ShadowActivityDelegate = "${PREFIX}ShadowActivityDelegate"
        const val CS_delegate_field = "hostActivityDelegate"
        const val CS_delegator_field = "hostActivityDelegator"
        const val CS_pluginActivity_field = "pluginActivity"

        val classPool = ClassPool.getDefault()
        val ActivityClass = Activity::class.java
        val ModifiedActivityClass = modifySdkClass(Activity::class.java)

        init {
            classPool.makeClass("$RUNTIME_PACKAGE.ShadowApplication").toClass()
            classPool.makeClass("$RUNTIME_PACKAGE.PluginFragmentManager").toClass()
        }

        val activityCallbackMethods = getActivityCallbackMethods(ActivityClass)
        val otherMethods = getOtherMethods(ActivityClass)
        val activityCallbackMethodsModified = getActivityCallbackMethods(ModifiedActivityClass)
        val otherMethodsModified = getOtherMethods(ModifiedActivityClass)

        /**
         * 统一在这里修改SDK中的Class对象，替换其中的类型为Shadow Runtime的类型
         */
        fun modifySdkClass(clazz: Class<*>): Class<*> {
            val name = clazz.name
            val renameMap = ClassMap()
            val ctClass = classPool.get(name)
            val newClassNames = mutableListOf<String>()
            ctClass.name = name

            mapOf(
                    Activity::class to "ShadowActivity",
                    Application::class to "ShadowApplication",
                    FragmentManager::class to "PluginFragmentManager"
            ).forEach {
                val newClassName = "$RUNTIME_PACKAGE.${it.value}"
                renameMap[Descriptor.toJvmName(it.key.java.name)] =
                        Descriptor.toJvmName(newClassName)

                newClassNames.add(newClassName)
            }

            //因为之前的手工实现中有些方法没有实现，签名也就没有修改，所以暂时保持它们不变
            //在rename前先remove掉它们，在rename后再添加回去
            val keepMethods = mutableListOf<CtMethod>()

            keepMethods.addAll(ctClass.getDeclaredMethods("startActivityFromChild").toList())

            keepMethods.forEach { ctClass.removeMethod(it) }
            ctClass.replaceClassName(renameMap)
            keepMethods.forEach { ctClass.addMethod(it) }

            return ctClass.toClass()
        }

        fun getActivityMethods(clazz: Class<*>): List<Method> {
            val allMethods = clazz.methods.toMutableSet()
            allMethods.addAll(clazz.declaredMethods)
            return allMethods
                    .filter {
                        it.declaringClass != Object::class.java
                    }
        }

        /**
         * 有一部分方法系统会调用，插件也会调用。
         * 这部分方法是否真的是这样，以后还是需要再仔细看一下。
         * 先这样定义出来，叫做Custom也是因为暂时不知道叫什么好。
         */
        fun getCustomMethods(clazz: Class<*>): Set<Method> {
            val set = mutableSetOf<Method>()

            fun addMethod(name: String, vararg args: Class<*>) {
                val method =
                        try {
                            clazz.getDeclaredMethod(name, * args)
                        } catch (e: NoSuchMethodException) {
                            clazz.getMethod(name, * args)
                        }
                set.add(method)
            }

            addMethod("isChangingConfigurations")
            addMethod("finish")
            addMethod("startActivityFromChild", Activity::class.java, Intent::class.java, Int::class.javaPrimitiveType!!)
            addMethod("getClassLoader")
            addMethod("getLayoutInflater")
            addMethod("getResources")
            addMethod("recreate")
            addMethod("getCallingActivity")

            return set
        }

        fun getActivityCallbackMethods(clazz: Class<*>): Set<Method> {
            val callbacks = mutableSetOf<Method>()

            callbacks.addAll(getCustomMethods(clazz))

            val startWithOnMethods = getActivityMethods(clazz)
                    .filter {
                        java.lang.reflect.Modifier.isPublic(it.modifiers) or
                                java.lang.reflect.Modifier.isProtected(it.modifiers)
                    }.filter {
                        it.name.startsWith("on")
                    }
            callbacks.addAll(startWithOnMethods)

            val callbackInterface = getActivityMethods(clazz).filter {
                java.lang.reflect.Modifier.isPublic(it.modifiers) or
                        java.lang.reflect.Modifier.isProtected(it.modifiers)
            }.filter {
                it.hasSameDefineIn(Window.Callback::class.java) or
                        it.hasSameDefineIn(KeyEvent.Callback::class.java)
            }
            callbacks.addAll(callbackInterface)

            return callbacks;
        }

        fun Method.hasSameDefineIn(clazz: Class<*>): Boolean {
            return try {
                clazz.getDeclaredMethod(name, *parameterTypes)
                true
            } catch (e: NoSuchMethodException) {
                false
            }
        }

        fun Method.hasSameMethodIn(clazz: Class<*>): Boolean {
            return try {
                try {
                    clazz.getDeclaredMethod(name, *parameterTypes)
                    true
                } catch (e: NoSuchMethodException) {
                    clazz.getMethod(name, *parameterTypes)
                    true
                }
            } catch (e: NoSuchMethodException) {
                false
            }
        }

        fun getOtherMethods(clazz: Class<*>): Set<Method> {
            val activityMethods = getActivityMethods(clazz)
            val callbackMethods = getActivityCallbackMethods(clazz)
            val filter = activityMethods.filterNot {
                callbackMethods.contains(it)
            }.filterNot {
                (it.declaringClass != clazz) and
                        java.lang.reflect.Modifier.isFinal(it.modifiers)
            }

            val result = mutableSetOf<Method>()
            result.addAll(filter)
            result.addAll(getCustomMethods(clazz))
            return result
        }

        fun Method.toMethodSpecBuilder(prefix: String = ""): MethodSpec.Builder {
            val methodName = if (prefix.isEmpty()) name else prefix + name.capitalize()
            val builder = MethodSpec.methodBuilder(methodName)
            parameters.forEach {
                builder.addParameter(
                        ParameterSpec.builder(it.parameterizedType, it.name).build()
                )
            }
            builder.addExceptions(
                    exceptionTypes.map {
                        TypeName.get(it)
                    }
            )
            builder.addTypeVariables(typeParameters.map {
                TypeVariableName.get(it)
            })
            builder.returns(genericReturnType)
            return builder
        }

        fun Method.toInterfaceMethodSpec(prefix: String = ""): MethodSpec {
            val builder = toMethodSpecBuilder(prefix)
            builder.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            return builder.build()
        }
    }

    val commonJavadoc = "由\n" + "{@link com.tencent.shadow.coding.code_generator.ActivityCodeGenerator}\n" + "自动生成\n"

    val activityDelegate = defineActivityDelegate()
    val activityDelegator = defineActivityDelegator()
    val pluginContainerActivity = definePluginContainerActivity()
    val pluginActivity = definePluginActivity()
    val shadowActivityDelegate = defineShadowActivityDelegate()

    fun defineActivityDelegate() =
            TypeSpec.interfaceBuilder(CS_HostActivityDelegate)
                    .addModifiers(Modifier.PUBLIC)
                    .addJavadoc(commonJavadoc
                            + "HostActivity的被委托者接口\n"
                            + "被委托者通过实现这个接口中声明的方法达到替代委托者实现的目的，从而将HostActivity的行为动态化。\n"
                    )

    fun defineActivityDelegator() =
            TypeSpec.interfaceBuilder(CS_HostActivityDelegator)
                    .addModifiers(Modifier.PUBLIC)
                    .addJavadoc(commonJavadoc
                            + "HostActivityDelegator作为委托者的接口。主要提供它的委托方法的super方法，\n"
                            + "以便Delegate可以通过这个接口调用到Activity的super方法。\n"
                    )

    fun definePluginContainerActivity() =
            TypeSpec.classBuilder(CS_PluginContainerActivity)
                    .addModifiers(Modifier.ABSTRACT)
                    .superclass(ActivityClass)
                    .addSuperinterface(ClassName.get(ACTIVITY_CONTAINER_PACKAGE, CS_HostActivityDelegator))
                    .addAnnotation(
                            AnnotationSpec.builder(SuppressLint::class.java)
                                    .addMember("value", "{\"NewApi\", \"MissingPermission\"}")
                                    .build()
                    )
                    .addField(
                            ClassName.get(ACTIVITY_CONTAINER_PACKAGE, CS_HostActivityDelegate),
                            CS_delegate_field
                    )
                    .addJavadoc(commonJavadoc)

    fun definePluginActivity() =
            TypeSpec.classBuilder(CS_PluginActivity)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .superclass(ClassName.get(RUNTIME_PACKAGE, "ShadowContext"))
                    .addSuperinterfaces(listOf(
                            ClassName.get(Window.Callback::class.java),
                            ClassName.get(KeyEvent.Callback::class.java)
                    ))
                    .addAnnotation(
                            AnnotationSpec.builder(SuppressLint::class.java)
                                    .addMember("value", "{\"NullableProblems\", \"deprecation\"}")
                                    .build()
                    )
                    .addField(
                            ClassName.get(ACTIVITY_CONTAINER_PACKAGE, CS_HostActivityDelegator),
                            CS_delegator_field
                    )
                    .addJavadoc(commonJavadoc)

    fun defineShadowActivityDelegate() =
            TypeSpec.classBuilder(CS_ShadowActivityDelegate)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .superclass(ClassName.get(DELEGATE_PACKAGE, "ShadowDelegate"))
                    .addSuperinterface(
                            ClassName.get(ACTIVITY_CONTAINER_PACKAGE, CS_HostActivityDelegate)
                    )
                    .addField(
                            ClassName.get(RUNTIME_PACKAGE, CS_PluginActivity),
                            CS_pluginActivity_field
                    )
                    .addJavadoc(commonJavadoc)

    fun generate(outputDir: File) {
        val activityContainerOutput = File(outputDir, "activity_container")
        val runtimeOutput = File(outputDir, "runtime")
        val loaderOutput = File(outputDir, "loader")
        activityContainerOutput.mkdirs()
        runtimeOutput.mkdirs()
        loaderOutput.mkdirs()

        addMethods()
        writeOutJavaFiles(activityContainerOutput, runtimeOutput, loaderOutput)
    }

    fun writeOutJavaFiles(activityContainerOutput: File, runtimeOutput: File, loaderOutput: File) {
        JavaFile.builder(ACTIVITY_CONTAINER_PACKAGE, activityDelegate.build())
                .build().writeTo(activityContainerOutput)
        JavaFile.builder(ACTIVITY_CONTAINER_PACKAGE, activityDelegator.build())
                .build().writeTo(activityContainerOutput)
        JavaFile.builder(ACTIVITY_CONTAINER_PACKAGE, pluginContainerActivity.build())
                .build().writeTo(activityContainerOutput)
        JavaFile.builder(RUNTIME_PACKAGE, pluginActivity.build())
                .build().writeTo(runtimeOutput)
        JavaFile.builder(DELEGATE_PACKAGE, shadowActivityDelegate.build())
                .build().writeTo(loaderOutput)
    }

    fun addMethods() {
        //将系统会调用的方法都定义出来，供转调之用。
        activityDelegate.addMethods(activityCallbackMethods.map { it.toInterfaceMethodSpec() })

        //将Activity可以被调用的方法都暴露出来
        activityDelegator.addMethods(
                otherMethods.map { it.toInterfaceMethodSpec() }
        )

        //TODO:这些方法应该不需要定义出来，但先对齐原手工实现的类，保证单元测试检测生成类和原手工写的类一致可以通过。
        activityDelegator.addMethods(
                activityCallbackMethods.filter {
                    it.hasSameDefineIn(Window.Callback::class.java)
                }.map { it.toInterfaceMethodSpec() }
        )

        //添加系统会调用的方法的对应super方法，这些super方法实现时实现为调用super同名方法
        activityDelegator.addMethods(
                activityCallbackMethods.map { it.toInterfaceMethodSpec("super") }
        )

        //TODO:这些方法并不需要添加super前缀方法，但先对齐原手工实现的类，保证单元测试检测生成类和原手工写的类一致可以通过。
        activityDelegator.addMethods(
                otherMethods.filterNot { activityCallbackMethods.contains(it) }
                        .map { it.toInterfaceMethodSpec("super") }
        )

        //对系统会调用的方法转调到hostActivityDelegate去，再生成对应的super方法
        pluginContainerActivity.addMethods(
                activityCallbackMethods.map(::delegateCallbackMethod)
        )
        pluginContainerActivity.addMethods(
                activityCallbackMethods.map(::implementSuperMethod)
        )

        //TODO:这些方法并不需要添加super前缀方法，但先对齐原手工实现的类，保证单元测试检测生成类和原手工写的类一致可以通过。
        pluginContainerActivity.addMethods(
                otherMethods.filterNot { activityCallbackMethods.contains(it) }
                        .map(::implementSuperMethod)
        )

        //将所有protected方法暴露成public方法
        pluginContainerActivity.addMethods(
                otherMethods.filter {
                    java.lang.reflect.Modifier.isProtected(it.modifiers)
                }.map(::exposeProtectedMethod)
        )

        pluginActivity.addMethods(
                activityCallbackMethodsModified
                        .filterNot { it.name == "getResources" }
                        .filterNot { it.name == "getClassLoader" }
                        .map {
                            defineMethodXXX(it, true)
                        }
        )

        pluginActivity.addMethods(
                otherMethodsModified
                        .filterNot {
                            it.hasSameMethodIn(ContextThemeWrapper::class.java)
                        }
                        .filterNot { getCustomMethods(ModifiedActivityClass).contains(it) }
                        .map {
                            defineMethodXXX(it, false)
                        }
                        .toList()
        )

        //实现所有Delegate方法
        shadowActivityDelegate.addMethods(
                activityCallbackMethodsModified
                        .filter { it.isNotModified() }
                        .map {
                            implementDelegateMethod(it)
                        }
        )
    }

    //定义转调一个系统会调用的方法的实现
    fun delegateCallbackMethod(method: Method): MethodSpec {
        val methodBuilder = method.toMethodSpecBuilder()
        if (java.lang.reflect.Modifier.isPublic(method.modifiers)) {
            methodBuilder.addModifiers(Modifier.PUBLIC)
        } else {
            methodBuilder.addModifiers(Modifier.PROTECTED)
        }
        methodBuilder.addAnnotation(Override::class.java)

        val ret = if (method.returnType == Void::class.javaPrimitiveType) "" else "return "

        methodBuilder.beginControlFlow("if (${CS_delegate_field} != null)")
        val args = method.parameters.joinToString(separator = ", ") {
            it.name
        }
        methodBuilder.addStatement("${ret}${CS_delegate_field}.${method.name}($args)")
        methodBuilder.nextControlFlow("else")
        methodBuilder.addStatement("${ret}super.${method.name}($args)")
        methodBuilder.endControlFlow()

        return methodBuilder.build()
    }

    //实现super前缀方法
    fun implementSuperMethod(method: Method): MethodSpec {
        val methodBuilder = method.toMethodSpecBuilder("super")
        methodBuilder.addModifiers(Modifier.PUBLIC)
        methodBuilder.addAnnotation(Override::class.java)

        val ret = if (method.returnType == Void::class.javaPrimitiveType) "" else "return "
        val args = method.parameters.joinToString(separator = ", ") {
            it.name
        }
        methodBuilder.addStatement("${ret}super.${method.name}($args)")

        return methodBuilder.build()
    }

    //定义暴露protected的方法
    fun exposeProtectedMethod(method: Method): MethodSpec {
        val methodBuilder = method.toMethodSpecBuilder()
        methodBuilder.addModifiers(Modifier.PUBLIC)

        val ret = if (method.returnType == Void::class.javaPrimitiveType) "" else "return "
        val args = method.parameters.joinToString(separator = ", ") {
            it.name
        }
        methodBuilder.addStatement("${ret}super.${method.name}($args)")

        return methodBuilder.build()
    }

    //定义方法的实现
    fun defineMethod(method: Method, hasSuperMethod: Boolean): MethodSpec {
        val methodBuilder = method.toMethodSpecBuilder()
        methodBuilder.addModifiers(Modifier.PUBLIC)

        val ret = if (method.returnType == Void::class.javaPrimitiveType) "" else "return "
        val args = method.parameters.joinToString(separator = ", ") {
            it.name
        }
        val invokeMethod =
                if (hasSuperMethod)
                    "super${method.name.capitalize()}"
                else
                    "${method.name}"
        methodBuilder.addStatement("${ret}${CS_delegator_field}.${invokeMethod}($args)")

        return methodBuilder.build()
    }

    fun defineAbstractMethod(method: Method): MethodSpec {
        val methodBuilder = method.toMethodSpecBuilder()
        methodBuilder.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
        return methodBuilder.build()
    }

    fun Method.isNotModified(): Boolean {
        return try {
            val m = ActivityClass.getDeclaredMethod(name, *parameterTypes)
            m.returnType == returnType
        } catch (e: NoSuchMethodException) {
            try {
                val m = ActivityClass.getMethod(name, *parameterTypes)
                m.returnType == returnType
            } catch (e: NoSuchMethodException) {
                false
            }
        }
    }

    fun defineMethodXXX(method: Method, hasSuperMethod: Boolean) =
            if (method.isNotModified()) {
                defineMethod(method, hasSuperMethod)
            } else {
                defineAbstractMethod(method)
            }

    fun implementDelegateMethod(method: Method): MethodSpec {
        val methodBuilder = method.toMethodSpecBuilder()
        methodBuilder.addModifiers(Modifier.PUBLIC)
        methodBuilder.addAnnotation(Override::class.java)

        val ret = if (method.returnType == Void::class.javaPrimitiveType) "" else "return "
        val args = method.parameters.joinToString(separator = ", ") {
            it.name
        }
        methodBuilder.addStatement("${ret}${CS_pluginActivity_field}.${method.name}($args)")

        return methodBuilder.build()
    }
}
