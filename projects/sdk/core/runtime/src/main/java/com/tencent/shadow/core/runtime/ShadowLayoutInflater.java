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

import android.content.Context;
import android.view.LayoutInflater;


/**
 * 本类主要有2个目的
 * 1.替换xml里面的WebView为ShadowWebView
 * 2.给插件自定义View加上特定的前缀，防止插件切换的时候由于多插件自定义view重名，LayoutInflater缓存类构造器导致view冲突
 */
public class ShadowLayoutInflater extends ShadowWebViewLayoutInflater {

    private Factory mOriginalFactory = null;
    private Factory2 mOriginalFactory2 = null;

    @Override
    public void setFactory(Factory factory) {
        mOriginalFactory = factory;
        super.setFactory(factory);
    }

    @Override
    public void setFactory2(Factory2 factory) {
        mOriginalFactory = mOriginalFactory2 = factory;
        super.setFactory2(factory);
    }

    public static Factory getOriginalFactory(LayoutInflater inflater) {
        if (inflater instanceof ShadowLayoutInflater) {
            return ((ShadowLayoutInflater) inflater).mOriginalFactory;
        } else {
            return inflater.getFactory();
        }
    }

    public static Factory2 getOriginalFactory2(LayoutInflater inflater) {
        if (inflater instanceof ShadowLayoutInflater) {
            return ((ShadowLayoutInflater) inflater).mOriginalFactory2;
        } else {
            return inflater.getFactory2();
        }
    }

    public static ShadowLayoutInflater build(LayoutInflater original, Context newContext, String partKey) {
        InnerInflater innerLayoutInflater = new InnerInflater(original, newContext, partKey);
        return new ShadowLayoutInflater(innerLayoutInflater, newContext, partKey);
    }

    private static class InnerInflater extends ShadowLayoutInflater {
        private InnerInflater(LayoutInflater original, Context newContext, String partKey) {
            super(original, newContext, partKey);
            setFactory2(new ShadowFactory2(partKey, this));
        }
    }

    private ShadowLayoutInflater(LayoutInflater original, Context newContext, String partKey) {
        super(original, newContext);
    }


}
