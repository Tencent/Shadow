package com.leelu.constants

/**
 *
 * CreateDate: 2022/3/15 15:17
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
object Constant {
    const val KEY_PLUGIN_ZIP_PATH = "key_plugin_zip_path"
    const val KEY_ACTIVITY_CLASSNAME = "key_activity_classname"
    const val KEY_PLUGIN_PART_KEY = "key_plugin_part_key"
    const val KEY_EXTRAS = "key_extras"
    const val KEY_PLUGIN_NAME = "key_plugin_name"

    const val PLUGIN_APP_NAME = "plugin-app"
    const val PLUGIN_OTHER_NAME = "plugin-other"

    const val FROM_ID_NOOP = 1000
    const val FROM_ID_START_ACTIVITY: Long = 1002 //标识启动的是Activity
    const val FROM_ID_START_ACTIVITY_NORMAL: Long = 1007 //标识启动的是Activity
    const val FROM_ID_CALL_SERVICE = 1001 //标识启动的是Service
    const val FROM_ID_CLOSE = 1003
    const val FROM_ID_LOAD_VIEW_TO_HOST = 1004

    const val FROM_ID_INSTALL_PLUGIN: Long = 1005
    const val FROM_ID_LOAD_LOADER_AND_RUNTIME: Long = 1006


    const val PART_KEY = "plugin_app"

}