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

import com.tencent.shadow.core.runtime.container.HostActivityDelegator;
import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

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
        private boolean isRegistered;

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
            //此时PluginActivity尚未构造。改由onPluginActivityPreCreated通知。
        }

        public void onPluginActivityPreCreated(ShadowActivity pluginActivity, Bundle savedInstanceState) {
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

    class Holder {
        final private Map<ShadowActivityLifecycleCallbacks,
                WeakReference<Wrapper>>
                mShadowActivityLifecycleCallbacksWrapperMap = new WeakHashMap<>();

        /**
         * 针对业务代码自己不持有ActivityLifecycleCallbacks的情况，
         * 无法通过mShadowActivityLifecycleCallbacksWrapperMap获取所有wrapper，
         * 需要特别记录所有wrapper。
         * <p>
         * 采用弱引用以便不影响Wrapper原本的GC时机，Wrapper至少被系统持有。
         * <p>
         * GuardedBy mShadowActivityLifecycleCallbacksWrapperMap
         */
        final private Map<ShadowActivityLifecycleCallbacks.Wrapper, Object>
                mAllShadowActivityLifecycleCallbackWrappers = new WeakHashMap<>();

        public void notifyPluginActivityPreCreated(ShadowActivity pluginActivity,
                                                   Bundle savedInstanceState) {
            synchronized (mShadowActivityLifecycleCallbacksWrapperMap) {
                //onPluginActivityPreCreated中可能会再次调用registerActivityLifecycleCallbacks，
                //进而修改mAllShadowActivityLifecycleCallbackWrappers，
                //因此需要先复制出待通知的callback，再通知。
                List<Wrapper> copiedWrappers = new LinkedList<>();
                Set<Wrapper> wrappers
                        = mAllShadowActivityLifecycleCallbackWrappers.keySet();
                for (ShadowActivityLifecycleCallbacks.Wrapper wrapper : wrappers) {
                    // wrapper是弱引用持有的，需要二次验证其是否处于register状态
                    if (wrapper != null && wrapper.isRegistered) {
                        copiedWrappers.add(wrapper);
                    }
                }

                for (ShadowActivityLifecycleCallbacks.Wrapper wrapper : copiedWrappers) {
                    wrapper.onPluginActivityPreCreated(pluginActivity, savedInstanceState);
                }
            }
        }

        private ShadowActivityLifecycleCallbacks.Wrapper shadowActivityLifecycleCallbacksToWrapper(
                ShadowActivityLifecycleCallbacks callbacks,
                Object caller
        ) {
            if (callbacks == null) {
                return null;
            }
            synchronized (mShadowActivityLifecycleCallbacksWrapperMap) {
                ShadowActivityLifecycleCallbacks.Wrapper wrapper;
                WeakReference<ShadowActivityLifecycleCallbacks.Wrapper> weakReference
                        = mShadowActivityLifecycleCallbacksWrapperMap.get(callbacks);
                wrapper = weakReference == null ? null : weakReference.get();
                if (wrapper == null) {
                    wrapper = new ShadowActivityLifecycleCallbacks.Wrapper(callbacks, caller);
                    mShadowActivityLifecycleCallbacksWrapperMap.put(callbacks,
                            new WeakReference<>(wrapper));
                    mAllShadowActivityLifecycleCallbackWrappers.put(wrapper, null);
                }
                return wrapper;
            }
        }

        void registerActivityLifecycleCallbacks(ShadowActivityLifecycleCallbacks callback,
                                                Object caller,
                                                Object hostActivityDelegatorOrApplication) {
            Wrapper wrapper = shadowActivityLifecycleCallbacksToWrapper(callback, caller);
            wrapper.isRegistered = true;
            if (hostActivityDelegatorOrApplication instanceof HostActivityDelegator) {
                ((HostActivityDelegator) hostActivityDelegatorOrApplication)
                        .registerActivityLifecycleCallbacks(wrapper);
            } else {
                ((Application) hostActivityDelegatorOrApplication)
                        .registerActivityLifecycleCallbacks(wrapper);
            }
        }

        void unregisterActivityLifecycleCallbacks(ShadowActivityLifecycleCallbacks callback,
                                                  Object caller,
                                                  Object hostActivityDelegatorOrApplication) {
            Wrapper wrapper = shadowActivityLifecycleCallbacksToWrapper(callback, caller);
            wrapper.isRegistered = false;
            if (hostActivityDelegatorOrApplication instanceof HostActivityDelegator) {
                ((HostActivityDelegator) hostActivityDelegatorOrApplication)
                        .unregisterActivityLifecycleCallbacks(wrapper);
            } else {
                ((Application) hostActivityDelegatorOrApplication)
                        .unregisterActivityLifecycleCallbacks(wrapper);
            }
        }
    }
}