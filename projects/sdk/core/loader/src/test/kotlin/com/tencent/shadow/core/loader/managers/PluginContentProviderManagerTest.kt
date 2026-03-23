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

package com.tencent.shadow.core.loader.managers

import android.net.Uri
import com.tencent.shadow.core.loader.infos.ContainerProviderInfo
import com.tencent.shadow.core.runtime.PluginManifest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`

@RunWith(Enclosed::class)
class PluginContentProviderManagerTest {

    @RunWith(Parameterized::class)
    class Convert2PluginUriTest(
        private val containerAuthority: String,
        private val pluginAuthority: String,
        private val input: String,
        private val expected: String
    ) {
        private lateinit var manager: PluginContentProviderManager

        companion object {
            @JvmStatic
            @Parameters
            fun data(): Collection<Array<String>> = listOf(
                "com.container.auth" to "com.plugin.auth",
                "com.container.auth" to "com.container.auth"
            )
                .flatMap { (containerAuthority, pluginAuthority) ->
                    val same = containerAuthority == pluginAuthority
                    listOf(
                        "content://$containerAuthority" to "content://$containerAuthority",
                        "content://$containerAuthority/" to if (same) "content://$pluginAuthority/" else "content://",
                        "content://$containerAuthority/path" to if (same) "content://$pluginAuthority/path" else "content://path",
                        "content://$containerAuthority/$pluginAuthority" to "content://$pluginAuthority",
                        "content://$containerAuthority/$pluginAuthority/" to "content://$pluginAuthority/",
                        "content://$containerAuthority/$pluginAuthority/path" to "content://$pluginAuthority/path",
                        "content://$containerAuthority/$containerAuthority/$pluginAuthority" to "content://$pluginAuthority",
                        "content://$containerAuthority/$containerAuthority/$pluginAuthority/" to "content://$pluginAuthority/",
                        "content://$containerAuthority/$containerAuthority/$pluginAuthority/path" to "content://$pluginAuthority/path",
                        "content://$containerAuthority/$pluginAuthority/$containerAuthority" to if (same) "content://$pluginAuthority" else "content://$pluginAuthority/$containerAuthority",
                        "content://$containerAuthority/$pluginAuthority/$containerAuthority/" to if (same) "content://$pluginAuthority/" else "content://$pluginAuthority/$containerAuthority/",
                        "content://$containerAuthority/$pluginAuthority/$containerAuthority/path" to if (same) "content://$pluginAuthority/path" else "content://$pluginAuthority/$containerAuthority/path",
                        "content://$containerAuthority/$pluginAuthority/$containerAuthority/$pluginAuthority" to if (same) "content://$pluginAuthority" else "content://$pluginAuthority/$containerAuthority/$pluginAuthority",
                        "content://$containerAuthority/$pluginAuthority/$containerAuthority/$pluginAuthority/" to if (same) "content://$pluginAuthority/" else "content://$pluginAuthority/$containerAuthority/$pluginAuthority/",
                        "content://$containerAuthority/$pluginAuthority/$containerAuthority/$pluginAuthority/path" to if (same) "content://$pluginAuthority/path" else "content://$pluginAuthority/$containerAuthority/$pluginAuthority/path",
                        "content://$containerAuthority/$pluginAuthority/path/$containerAuthority" to "content://$pluginAuthority/path/$containerAuthority",
                        "content://$containerAuthority/$pluginAuthority/path/$containerAuthority/" to "content://$pluginAuthority/path/$containerAuthority/",
                        "content://$containerAuthority/$pluginAuthority/path/$containerAuthority/file" to "content://$pluginAuthority/path/$containerAuthority/file",
                        "content://$containerAuthority/$pluginAuthority/path/$pluginAuthority" to "content://$pluginAuthority/path/$pluginAuthority",
                        "content://$containerAuthority/$pluginAuthority/path/$pluginAuthority/" to "content://$pluginAuthority/path/$pluginAuthority/",
                        "content://$containerAuthority/$pluginAuthority/path/$pluginAuthority/file" to "content://$pluginAuthority/path/$pluginAuthority/file",
                        "content://$containerAuthority/$pluginAuthority/path/$containerAuthority/$pluginAuthority" to "content://$pluginAuthority/path/$containerAuthority/$pluginAuthority",
                        "content://$containerAuthority/$pluginAuthority/path/$containerAuthority/$pluginAuthority/" to "content://$pluginAuthority/path/$containerAuthority/$pluginAuthority/",
                        "content://$containerAuthority/$pluginAuthority/path/$containerAuthority/$pluginAuthority/file" to "content://$pluginAuthority/path/$containerAuthority/$pluginAuthority/file",
                        "content://$containerAuthority/$pluginAuthority/path/$pluginAuthority/$containerAuthority" to "content://$pluginAuthority/path/$pluginAuthority/$containerAuthority",
                        "content://$containerAuthority/$pluginAuthority/path/$pluginAuthority/$containerAuthority/" to "content://$pluginAuthority/path/$pluginAuthority/$containerAuthority/",
                        "content://$containerAuthority/$pluginAuthority/path/$pluginAuthority/$containerAuthority/file" to "content://$pluginAuthority/path/$pluginAuthority/$containerAuthority/file"
                    )
                        .map { arrayOf(containerAuthority, pluginAuthority, it.first, it.second) }
                }
        }

        @Before
        fun init() {
            manager = PluginContentProviderManager().apply {
                addContentProviderInfo(
                    "partKey",
                    PluginManifest.ProviderInfo("pluginClassName", pluginAuthority, true),
                    ContainerProviderInfo("containerClassName", containerAuthority),
                    pluginAuthority
                )
            }
        }

        @Test
        fun testConvert2PluginUri() {
            mockStatic(Uri::class.java).use {
                it.`when`<Uri> { Uri.parse(anyString()) }
                    .thenAnswer { invocation -> mockUri(invocation.getArgument(0)) }

                Assert.assertEquals(expected, manager.convert2PluginUri(mockUri(input)).toString())
            }
        }
    }
}

private fun mockUri(input: String): Uri {
    val uri = mock(Uri::class.java)
    `when`(uri.toString()).thenReturn(input)

    val startIndex = "content://".length
    val indexOf = input.indexOf('/', startIndex)
    val endIndex = if (indexOf < 0) input.length else indexOf
    val authority = input.substring(startIndex, endIndex)
    `when`(uri.authority).thenReturn(authority)

    return uri
}