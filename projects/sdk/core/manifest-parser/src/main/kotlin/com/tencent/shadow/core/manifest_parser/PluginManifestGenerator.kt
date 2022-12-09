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
 */
class PluginManifestGenerator {
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
        val pluginManifestBuilder = PluginManifestBuilder(manifestMap)
        val pluginManifest = pluginManifestBuilder.build()
        JavaFile.builder(packageName, pluginManifest)
            .build()
            .writeTo(outputDir)
    }
}

private class PluginManifestBuilder(val manifestMap: ManifestMap) {
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

        val resIdLiteral = themeStringToResId(manifestValue)
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
            themeStringToResId(it)
        }
        val configChangesLiteral = makeResIdLiteral(AndroidManifestKeys.configChanges) {
            it
        }
        val softInputModeLiteral = makeResIdLiteral(AndroidManifestKeys.windowSoftInputMode) {
            it
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
        //如果未传值使用android.content.pm.ProviderInfo.grantUriPermissions的默认值false
        val grantUriPermissions = componentMap[AndroidManifestKeys.grantUriPermissions] ?: false

        val authoritiesLiteral =
            if (authoritiesValue != null) {
                "\"${authoritiesValue}\""
            } else {
                "null"
            }

        return "new com.tencent.shadow.core.runtime.PluginManifest" +
                ".ProviderInfo(\"${componentMap[AndroidManifestKeys.name]}\", $authoritiesLiteral,$grantUriPermissions)"
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

        fun themeStringToResId(manifestValue: Any): String {
            val formatValue = manifestValue as String // for example: @ref/0x7e0b009e
            if (formatValue.startsWith("@ref/")) {
                return formatValue.removePrefix("@ref/")
            } else {
                // 其余格式：https://cs.android.com/android-studio/platform/tools/base/+/mirror-goog-studio-main:apkparser/analyzer/src/main/java/com/android/tools/apk/analyzer/BinaryXmlParser.java;l=193
                throw TODO("不支持其他格式")
            }
        }
    }
}
