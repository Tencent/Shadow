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

package test;

public class MethodRedirectToStatic {

    public static void main(String[] args) {
        System.out.println(new MethodRedirectToStatic().test());
    }

    int add(int a, int b) {
        return a + b;
    }

    public int test() {
        return add(1, 2);
    }
}

class MethodRedirectToStatic2 {
    public static int add2(MethodRedirectToStatic target, int a, int b) {
        return target.add(a * 10, b * 10);
    }
}
