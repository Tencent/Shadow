package com.tencent.cubershi

import java.io.InputStream
import java.io.OutputStream
import java.util.function.BiConsumer

class CuberPluginLoaderTransform : CustomClassTransform() {
    override fun loadTransformFunction(): BiConsumer<InputStream, OutputStream> =
            BiConsumer { input, output ->
                input.copyTo(output)
            }

    override fun getName(): String = "CuberPluginLoaderTransform"

}