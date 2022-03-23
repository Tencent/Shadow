package com.tencent.shadow.core.manifest_parser

sealed class AndroidManifestKeys {
    companion object {
        const val appComponentFactory = "android:appComponentFactory"
        const val `package` = "package"
        const val name = "android:name"
        const val theme = "android:theme"
        const val configChanges = "android:configChanges"
        const val windowSoftInputMode = "android:windowSoftInputMode"
        const val authorities = "android:authorities"
        const val `intent-filter` = "intent-filter"
        const val action = "action"
        const val manifest = "manifest"
        const val application = "application"
        const val activity = "activity"
        const val service = "service"
        const val provider = "provider"
        const val receiver = "receiver"
        const val grantUriPermissions = "android:grantUriPermissions"
    }
}
typealias ComponentMapKey = String
typealias ComponentMapValue = Any
typealias ComponentMap = Map<ComponentMapKey, ComponentMapValue>
typealias MutableComponentMap = MutableMap<ComponentMapKey, ComponentMapValue>