package com.tencent.shadow.core.manifest_parser

import com.squareup.javapoet.*
import com.tencent.shadow.core.runtime.PluginManifest
import java.io.File
import java.util.*
import javax.lang.model.element.Modifier

/**
 * PluginManifest.java生成器
 *
 * 将Loader所需的插件Manifest信息生成为Java文件，
 * 添加runtime中PluginManifest接口的实现方法
 *
 * @param packageForR 生成对R.java引用时需要的R文件的包名
 */
class PluginManifestGenerator(private val packageForR: String) {
    /**
     * 生成器入口方法
     *
     * 根据AndroidManifestReader输出的Map生成PluginManifest.java到outputDir目录中。
     *
     * @param manifestMap   AndroidManifestReader#read的输出Map
     * @param outputDir     生成文件的输出目录
     * @param packageName   生成类的包名
     */
    fun generate(manifestMap: ManifestMap, outputDir: File, packageName: String) {
        val pluginManifestBuilder = PluginManifestBuilder(manifestMap, packageForR)
        val pluginManifest = pluginManifestBuilder.build()
        JavaFile.builder(packageName, pluginManifest)
            .build()
            .writeTo(outputDir)
    }
}

private class PluginManifestBuilder(
    val manifestMap: ManifestMap,
    val packageForR: String
) {
    val classBuilder: TypeSpec.Builder =
        TypeSpec.classBuilder("PluginManifest")
            .addSuperinterface(ClassName.get(PluginManifest::class.java))
            .addModifiers(Modifier.PUBLIC)!!

    fun build(): TypeSpec {
        listOf(
            *buildApplicationFields(),
            buildActivityInfoArrayField(),
            buildServiceInfoArrayField(),
            buildReceiverInfoArrayField(),
            buildProviderInfoArrayField(),
        ).forEach { fieldSpec ->
            val getterMethod = buildGetterMethod(fieldSpec)
            classBuilder.addField(fieldSpec)
            classBuilder.addMethod(getterMethod)
        }
        return classBuilder.build()
    }

    private fun buildApplicationFields(): Array<FieldSpec> {
        val stringFields = mapOf(
            "applicationPackageName" to AndroidManifestKeys.`package`,
            "applicationClassName" to AndroidManifestKeys.name,
            "appComponentFactory" to AndroidManifestKeys.appComponentFactory,
        ).map { (fieldName, key) ->
            buildStringField(fieldName, key)
        }

        val resIdFields = mapOf(
            "applicationTheme" to AndroidManifestKeys.theme,
        ).map { (fieldName, key) ->
            buildResIdField(fieldName, key)
        }

        return (stringFields + resIdFields).toTypedArray()
    }

    private fun buildActivityInfoArrayField() = buildComponentArrayField(
        AndroidManifestKeys.activity,
        "ActivityInfo",
        "activities",
        ::toNewActivityInfo,
    )

    private fun buildServiceInfoArrayField() = buildComponentArrayField(
        AndroidManifestKeys.service,
        "ServiceInfo",
        "services",
        ::toNewServiceInfo,
    )

    private fun buildReceiverInfoArrayField() = buildComponentArrayField(
        AndroidManifestKeys.receiver,
        "ReceiverInfo",
        "receivers",
        ::toNewReceiverInfo,
    )

    private fun buildProviderInfoArrayField() = buildComponentArrayField(
        AndroidManifestKeys.provider,
        "ProviderInfo",
        "providers",
        ::toNewProviderInfo,
    )

    private fun buildComponentArrayField(
        key: String,
        subClassName: String,
        fieldName: String,
        transform: (ComponentMap) -> String
    ): FieldSpec {
        @Suppress("UNCHECKED_CAST")
        val componentMapArray = manifestMap[key] as Array<ComponentMap>
        val literal = componentMapArray.joinToString(
            separator = ",\n",
            prefix = "{\n",
            postfix = "\n}",
            transform = transform
        )

        val componentInfoArrayTypeName = ArrayTypeName.of(
            ClassName.get(
                "com.tencent.shadow.core.runtime",
                "PluginManifest",
                subClassName
            )
        )

        val codeBlock = if (componentMapArray.isNotEmpty()) {
            CodeBlock.of("new \$1T \$2L", componentInfoArrayTypeName, literal)
        } else {
            nullCodeBlock()
        }
        return privateStaticFinalFieldBuilder(
            componentInfoArrayTypeName,
            fieldName,
        ).initializer(
            codeBlock
        ).build()
    }

    private fun buildStringField(fieldName: String, key: String): FieldSpec {
        val value = manifestMap[key]
        val codeBlock = if (value != null) {
            CodeBlock.of("\"$1L\"", value)
        } else {
            nullCodeBlock()
        }
        return privateStaticFinalStringFieldBuilder(fieldName)
            .initializer(codeBlock).build()
    }

    private fun buildGetterMethod(fieldSpec: FieldSpec): MethodSpec =
        MethodSpec.methodBuilder("get${fieldSpec.name.capitalize()}")
            .addModifiers(
                Modifier.PUBLIC,
                Modifier.FINAL,
            )
            .returns(fieldSpec.type)
            .addStatement(CodeBlock.of("return ${fieldSpec.name}"))
            .build()

    private fun buildResIdField(fieldName: String, key: String): FieldSpec {
        val manifestValue = manifestMap[key]
        return if (manifestValue != null) {
            buildResIdFieldWithValue(fieldName, manifestValue)
        } else {
            privateStaticFinalIntFieldBuilder(fieldName)
                .initializer(
                    CodeBlock.of("$1L", "0")
                ).build()
        }
    }

    private fun buildResIdFieldWithValue(
        fieldName: String,
        manifestValue: Any,
    ): FieldSpec {

        val resIdLiteral = themeStringToResId(manifestValue, packageForR)
        return privateStaticFinalIntFieldBuilder(fieldName)
            .initializer(
                CodeBlock.of("$1L", resIdLiteral)
            ).build()
    }


    private fun toNewActivityInfo(componentMap: ComponentMap): String {
        fun makeResIdLiteral(
            key: String,
            valueToResId: (value: String) -> String
        ): String {
            val value = componentMap[key] as String?
            val literal = if (value != null) {
                valueToResId(value)
            } else {
                "0"
            }
            return literal
        }

        val themeLiteral = makeResIdLiteral(AndroidManifestKeys.theme) {
            themeStringToResId(it, packageForR)
        }
        val configChangesLiteral = makeResIdLiteral(AndroidManifestKeys.configChanges) {
            configChangesToInt(it)
        }
        val softInputModeLiteral = makeResIdLiteral(AndroidManifestKeys.windowSoftInputMode) {
            windowSoftInputModeToInt(it)
        }

        return "new com.tencent.shadow.core.runtime.PluginManifest" +
                ".ActivityInfo(" +
                "\"${componentMap[AndroidManifestKeys.name]}\", " +
                "$themeLiteral ," +
                "$configChangesLiteral ," +
                softInputModeLiteral +
                ")"
    }

    private fun toNewServiceInfo(componentMap: ComponentMap): String {
        return "new com.tencent.shadow.core.runtime.PluginManifest" +
                ".ServiceInfo(\"${componentMap[AndroidManifestKeys.name]}\")"
    }

    private fun toNewReceiverInfo(componentMap: ComponentMap): String {
        @Suppress("UNCHECKED_CAST")
        val actions = componentMap[AndroidManifestKeys.action] as List<String>?
        val actionsLiteral =
            actions?.joinToString(
                prefix = "new String[]{\"",
                separator = "\", \"",
                postfix = "\"}"
            ) ?: "null"

        return "new com.tencent.shadow.core.runtime.PluginManifest" +
                ".ReceiverInfo(\"${componentMap[AndroidManifestKeys.name]}\", " +
                actionsLiteral +
                ")"
    }

    private fun toNewProviderInfo(componentMap: ComponentMap): String {
        val authoritiesValue = componentMap[AndroidManifestKeys.authorities]
        val authoritiesLiteral =
            if (authoritiesValue != null) {
                "\"${authoritiesValue}\""
            } else {
                "null"
            }

        return "new com.tencent.shadow.core.runtime.PluginManifest" +
                ".ProviderInfo(\"${componentMap[AndroidManifestKeys.name]}\", $authoritiesLiteral)"
    }

    companion object {
        fun privateStaticFinalFieldBuilder(type: TypeName, fieldName: String) = FieldSpec.builder(
            type,
            fieldName,
            Modifier.PRIVATE,
            Modifier.STATIC,
            Modifier.FINAL,
        )!!

        fun privateStaticFinalStringFieldBuilder(fieldName: String) =
            privateStaticFinalFieldBuilder(
                ClassName.get(String::class.java),
                fieldName,
            )

        fun privateStaticFinalIntFieldBuilder(fieldName: String) =
            privateStaticFinalFieldBuilder(
                TypeName.INT,
                fieldName,
            )

        fun nullCodeBlock() = CodeBlock.of("null")!!

        fun themeStringToResId(manifestValue: Any, packageForR: String): String {
            val resName = manifestValue as String // for example: @style/TestPluginTheme
            val split = resName.split('/')
            val packagePart = split[0] // for example: @style
            val namePart = split[1] // for example: TestPluginTheme

            val isSystemResource = packagePart.startsWith("@android:")
            val packageNameOfRClass = if (isSystemResource) {
                "android"
            } else {
                packageForR
            }

            val innerClassName = if (isSystemResource) { // for example: style
                packagePart.removePrefix("@android:")
            } else {
                packagePart.removePrefix("@")
            }

            val resIdFieldName = namePart.replace('.', '_')
            return "${packageNameOfRClass}.R.${innerClassName}.${resIdFieldName}"
        }

        private fun flagsToInt(
            stringValue: String,
            className: String,
            fieldMap: (String) -> String,
        ): String =
            stringValue.split('|')
                .map(String::trim)
                .map(fieldMap)
                .map { "${className}.${it}" }
                .reduce { acc, i -> "$acc|$i" }

        private fun configChangesToInt(configChangesValue: String): String =
            flagsToInt(
                configChangesValue,
                "android.content.pm.ActivityInfo"
            ) {
                when (it) {
                    "mcc" -> "CONFIG_MCC"
                    "mnc" -> "CONFIG_MNC"
                    "locale" -> "CONFIG_LOCALE"
                    "touchscreen" -> "CONFIG_TOUCHSCREEN"
                    "keyboard" -> "CONFIG_KEYBOARD"
                    "keyboardHidden" -> "CONFIG_KEYBOARD_HIDDEN"
                    "navigation" -> "CONFIG_NAVIGATION"
                    "orientation" -> "CONFIG_ORIENTATION"
                    "screenLayout" -> "CONFIG_SCREEN_LAYOUT"
                    "uiMode" -> "CONFIG_UI_MODE"
                    "screenSize" -> "CONFIG_SCREEN_SIZE"
                    "smallestScreenSize" -> "CONFIG_SMALLEST_SCREEN_SIZE"
                    "density" -> "CONFIG_DENSITY"
                    "layoutDirection" -> "CONFIG_LAYOUT_DIRECTION"
                    "colorMode" -> "CONFIG_COLOR_MODE"
                    "assetsPaths" -> "CONFIG_ASSETS_PATHS"
                    "fontScale" -> "CONFIG_FONT_SCALE"
                    "windowConfiguration" -> "CONFIG_WINDOW_CONFIGURATION"
                    else -> throw IllegalArgumentException("不认识$it")
                }
            }

        private fun windowSoftInputModeToInt(windowSoftInputModeValue: String): String =
            flagsToInt(
                windowSoftInputModeValue,
                "android.view.WindowManager.LayoutParams"
            ) {
                when (it) {
                    "stateUnspecified" -> "SOFT_INPUT_STATE_UNSPECIFIED"
                    "stateUnchanged" -> "SOFT_INPUT_STATE_UNCHANGED"
                    "stateHidden" -> "SOFT_INPUT_STATE_HIDDEN"
                    "stateAlwaysHidden" -> "SOFT_INPUT_STATE_ALWAYS_HIDDEN"
                    "stateVisible" -> "SOFT_INPUT_STATE_VISIBLE"
                    "stateAlwaysVisible" -> "SOFT_INPUT_STATE_ALWAYS_VISIBLE"
                    "adjustUnspecified" -> "SOFT_INPUT_ADJUST_UNSPECIFIED"
                    "adjustResize" -> "SOFT_INPUT_ADJUST_RESIZE"
                    "adjustPan" -> "SOFT_INPUT_ADJUST_PAN"
                    "adjustNothing" -> "SOFT_INPUT_ADJUST_NOTHING"
                    "isForwardNavigation" -> "SOFT_INPUT_IS_FORWARD_NAVIGATION"
                    else -> throw IllegalArgumentException("不认识$it")
                }
            }
    }
}
