package com.tencent.shadow.core.manifest_parser

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

typealias ManifestMap = Map<String, Any>

/**
 * 读取xml格式的Manifest到内存Map中
 */
class AndroidManifestReader {
    /**
     * 读取入口方法
     *
     * @param xmlFile com.android.build.gradle.tasks.ManifestProcessorTask任务的输出文件，
     *                一般位于apk工程的build/intermediates/merged_manifest目录中。
     */
    fun read(xmlFile: File): ManifestMap {
        val manifest = readXml(xmlFile).documentElement
        val application = readApplication(manifest)
        val globalAttributes = readGlobalAttributes(manifest, application)
        val components = readComponents(application)
        return globalAttributes.plus(components)
    }

    private fun readXml(xmlFile: File): Document {
        try {
            val documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            return documentBuilder.parse(xmlFile)!!
        } catch (e: Exception) {
            throw RuntimeException("xml应该是AGP生成的合法文件，所以不兼容任何xml读取错误", e)
        }
    }

    private fun readApplication(manifest: Element): Element? {
        val elements = manifest.getElementsByTagName(AndroidManifestKeys.application)
        return if (elements.length == 1) {
            val node = elements.item(0)
            assert(node.nodeType == Node.ELEMENT_NODE)
            elements.item(0) as Element
        } else {
            null
        }
    }

    /**
     * 读取Manifest中那些唯一的属性
     */
    private fun readGlobalAttributes(manifest: Element, application: Element?): Map<String, Any> {
        val globalAttributes = mutableMapOf<String, Any>()

        fun manifestAttribute(name: String) {
            globalAttributes[name] = manifest.getAttribute(name)
        }

        fun applicationAttribute(name: String) {
            if (application != null) {
                val attribute = application.getAttribute(name)
                if (attribute.isNotEmpty()) {
                    globalAttributes[name] = attribute
                }
            }
        }

        manifestAttribute(AndroidManifestKeys.`package`)
        listOf(
            AndroidManifestKeys.name,
            AndroidManifestKeys.theme,
            AndroidManifestKeys.appComponentFactory,
        ).forEach(::applicationAttribute)
        return globalAttributes
    }

    private fun readComponents(application: Element?) =
        listOf(
            AndroidManifestKeys.activity to ::parseActivity,
            AndroidManifestKeys.service to ::parseService,
            AndroidManifestKeys.receiver to ::parseReceiver,
            AndroidManifestKeys.provider to ::parseProvider,
        ).map { (componentKey, parseMethod) ->
            val componentArray = parseComponents(application, componentKey, parseMethod)
            componentKey to componentArray
        }


    private fun parseComponents(
        application: Element?,
        componentKey: String,
        parseFunction: (Element) -> ComponentMap
    ): Array<ComponentMap> {
        if (application == null) {
            return emptyArray()
        }
        val nodeList = application.getElementsByTagName(componentKey)
        val length = nodeList.length
        val collectionList = mutableListOf<ComponentMap>()
        for (i in 0 until length) {
            val node = nodeList.item(i)
            assert(node.nodeType == Node.ELEMENT_NODE)
            val map = parseFunction(node as Element)
            collectionList.add(map)
        }
        return collectionList.toTypedArray()
    }

    private fun parseActivity(element: Element): ComponentMap {
        val activityMap = parseComponent(element).toMutableMap()

        listOf(
            AndroidManifestKeys.theme,
            AndroidManifestKeys.configChanges,
            AndroidManifestKeys.windowSoftInputMode,
        ).forEach { attributeKey ->
            activityMap.putAttributeIfNotNull(element, attributeKey)
        }
        return activityMap
    }

    private fun parseService(element: Element): ComponentMap {
        return parseComponent(element)
    }

    private fun parseReceiver(element: Element): ComponentMap {
        val receiverMap = parseComponent(element).toMutableMap()

        val receiverActions = parseReceiverActions(element)
        if (receiverActions.isNotEmpty()) {
            receiverMap[AndroidManifestKeys.action] = receiverActions
        }

        return receiverMap
    }

    private fun parseReceiverActions(receiverElement: Element): List<String> {
        val intentFilters =
            receiverElement.getElementsByTagName(AndroidManifestKeys.`intent-filter`)
        val collectionList = mutableListOf<String>()
        for (i in 0 until intentFilters.length) {
            val intentFilter = intentFilters.item(i)
            assert(intentFilter.nodeType == Node.ELEMENT_NODE)
            val actions = (intentFilter as Element).getElementsByTagName(AndroidManifestKeys.action)
            for (j in 0 until actions.length) {
                val action = actions.item(j)
                assert(action.nodeType == Node.ELEMENT_NODE)
                val actionName = (action as Element).getAttribute(AndroidManifestKeys.name)
                collectionList.add(actionName)
            }
        }
        return collectionList
    }

    private fun parseProvider(element: Element): ComponentMap {
        val providerMap = parseComponent(element).toMutableMap()

        providerMap.putAttributeIfNotNull(element, AndroidManifestKeys.authorities)
        providerMap.putAttributeIfNotNull(element, AndroidManifestKeys.grantUriPermissions)

        return providerMap
    }

    private fun parseComponent(element: Element): ComponentMap {
        val componentName = element.getAttribute(AndroidManifestKeys.name)
        return mapOf(
            AndroidManifestKeys.name to componentName
        )
    }

    private fun MutableComponentMap.putAttributeIfNotNull(
        componentElement: Element,
        attributeKey: String
    ) {
        if (componentElement.hasAttribute(attributeKey)) {
            this[attributeKey] = componentElement.getAttribute(attributeKey)
        }
    }
}
