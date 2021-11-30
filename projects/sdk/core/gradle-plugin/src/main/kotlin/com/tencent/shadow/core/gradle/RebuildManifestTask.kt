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
package com.tencent.shadow.core.gradle

import groovy.xml.DOMBuilder
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * 解析插件的AndroidManifest.xml文件
 * 由于系统开放接口不提供广播的action信息，此处采用手动解析的方式处理,减少插件化的适配工作
 * 后续对于AndroidManifest.xml的处理可在此基础上扩展
 *
 * @author xuedizi2009@163.com
 */
internal fun rebuildManifest(manifestFile: File) {
  println("RebuildManifest task run")
  val document = DOMBuilder.parse(manifestFile.reader(Charsets.UTF_8))
  try {
    buildReceiver(document)
  }catch (e : Exception){
    println("RebuildManifest fail , check <receiver> tag")
  }
  writeDocument(document,manifestFile)
}

fun writeDocument(document: Document,manifestFile : File) {
  val newTransformer = TransformerFactory.newInstance().newTransformer()
  newTransformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8")
  newTransformer.setOutputProperty(OutputKeys.INDENT,"yes")
  newTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no")
  newTransformer.transform(DOMSource(document),StreamResult(manifestFile.writer()))
}

@Throws(Exception::class)
fun buildReceiver(document: Document){
  val receiverElements = document.getElementsByTagName("receiver")
  for (receiverIndex in 0 until receiverElements.length){
    val receiverNode = receiverElements.item(receiverIndex)
    val element = receiverNode as Element
    val receiveNodeValue = element.getAttribute("android:name")
    for (intentFilterIndex in 0 until receiverNode.childNodes.length){
      val intentFilterNode = receiverNode.childNodes.item(intentFilterIndex)
      if(intentFilterNode.nodeName == "intent-filter"){
        for (actionIndex in 0 until intentFilterNode.childNodes.length){
          val actionNode = intentFilterNode.childNodes.item(actionIndex)
          if(actionNode.nodeName == "action"){
            val metaDateElement = document.createElement("meta-data")
            metaDateElement.setAttribute("android:name",(actionNode as Element).getAttribute("android:name"))
            metaDateElement.setAttribute("android:value",receiveNodeValue)
            receiverNode.appendChild(metaDateElement)
          }
        }
      }
    }
  }
}
