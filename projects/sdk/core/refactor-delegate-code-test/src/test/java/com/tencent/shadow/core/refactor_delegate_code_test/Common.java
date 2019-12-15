package com.tencent.shadow.core.refactor_delegate_code_test;

import java.util.Set;

import javassist.CtMethod;

class Common {
    static boolean containsCtMethod(Set<CtMethod> set, CtMethod method) {
        for (CtMethod ctMethod : set) {
            if (ctMethod.getName().equals(method.getName())) {
                if (ctMethod.getGenericSignature() != null && method.getGenericSignature() != null) {
                    if (ctMethod.getGenericSignature().equals(method.getGenericSignature())) {
                        return true;
                    }
                } else {
                    if (ctMethod.getSignature().equals(method.getSignature())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
