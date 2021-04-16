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

package com.tencent.shadow.core.runtime;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

public interface ShadowActivityLifecycleCallbacks {

    void onActivityPreCreated(ShadowActivity activity, Bundle savedInstanceState);

    void onActivityCreated(ShadowActivity activity, Bundle savedInstanceState);

    void onActivityPostCreated(ShadowActivity activity, Bundle savedInstanceState);


    void onActivityPreStarted(ShadowActivity activity);


    void onActivityStarted(ShadowActivity activity);


    void onActivityPostStarted(ShadowActivity activity);


    void onActivityPreResumed(ShadowActivity activity);


    void onActivityResumed(ShadowActivity activity);


    void onActivityPostResumed(ShadowActivity activity);


    void onActivityPrePaused(ShadowActivity activity);


    void onActivityPaused(ShadowActivity activity);


    void onActivityPostPaused(ShadowActivity activity);


    void onActivityPreStopped(ShadowActivity activity);


    void onActivityStopped(ShadowActivity activity);


    void onActivityPostStopped(ShadowActivity activity);


    void onActivityPreSaveInstanceState(ShadowActivity activity, Bundle outState);


    void onActivitySaveInstanceState(ShadowActivity activity, Bundle outState);


    void onActivityPostSaveInstanceState(ShadowActivity activity, Bundle outState);


    void onActivityPreDestroyed(ShadowActivity activity);


    void onActivityDestroyed(ShadowActivity activity);


    void onActivityPostDestroyed(ShadowActivity activity);

    class Wrapper implements Application.ActivityLifecycleCallbacks {

        final ShadowActivityLifecycleCallbacks shadowActivityLifecycleCallbacks;
        final Object runtimeObject;

        public Wrapper(ShadowActivityLifecycleCallbacks shadowActivityLifecycleCallbacks, Object runtimeObject) {
            this.shadowActivityLifecycleCallbacks = shadowActivityLifecycleCallbacks;
            this.runtimeObject = runtimeObject;
        }

        private ShadowActivity getPluginActivity(Activity activity) {
            if (activity instanceof PluginContainerActivity) {
                return (ShadowActivity) PluginActivity.get((PluginContainerActivity) activity);
            } else {
                return null;
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                shadowActivityLifecycleCallbacks.onActivityCreated(pluginActivity, savedInstanceState);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                shadowActivityLifecycleCallbacks.onActivityStarted(pluginActivity);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                shadowActivityLifecycleCallbacks.onActivityResumed(pluginActivity);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                shadowActivityLifecycleCallbacks.onActivityPaused(pluginActivity);
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                shadowActivityLifecycleCallbacks.onActivityStopped(pluginActivity);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                shadowActivityLifecycleCallbacks.onActivitySaveInstanceState(pluginActivity, outState);
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                shadowActivityLifecycleCallbacks.onActivityDestroyed(pluginActivity);
            }
        }

        @Override
        public void onActivityPreCreated(Activity activity, Bundle savedInstanceState) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPreCreated(pluginActivity, savedInstanceState);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPostCreated(Activity activity, Bundle savedInstanceState) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPostCreated(pluginActivity, savedInstanceState);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPreStarted(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPreStarted(pluginActivity);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPostStarted(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPostStarted(pluginActivity);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPreResumed(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPreResumed(pluginActivity);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPostResumed(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPostResumed(pluginActivity);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPrePaused(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPrePaused(pluginActivity);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPostPaused(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPostPaused(pluginActivity);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPreStopped(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPreStopped(pluginActivity);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPostStopped(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPostStopped(pluginActivity);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPreSaveInstanceState(Activity activity, Bundle outState) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPreSaveInstanceState(pluginActivity, outState);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPostSaveInstanceState(Activity activity, Bundle outState) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPostSaveInstanceState(pluginActivity, outState);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPreDestroyed(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPreDestroyed(pluginActivity);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        @Override
        public void onActivityPostDestroyed(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity)) {
                try {
                    shadowActivityLifecycleCallbacks.onActivityPostDestroyed(pluginActivity);
                } catch (AbstractMethodError ignored) {
                    //兼容Java8接口default方法
                }
            }
        }

        /**
         * 检测Activity是否属于当前Application所在的插件
         *
         * @param activity 插件Activity
         * @return 是否属于当前Application所在的插件 true属于
         */
        private boolean checkOwnerActivity(PluginActivity activity) {
            if (activity == null) {
                return false;
            } else if (runtimeObject instanceof ShadowApplication) {
                return activity.mPluginApplication == runtimeObject;
            } else {
                return activity == runtimeObject;
            }
        }
    }
}
