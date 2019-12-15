package com.tencent.shadow.core.refactor_delegate_code_test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import static com.tencent.shadow.core.refactor_delegate_code_test.Common.containsCtMethod;

@RunWith(Parameterized.class)
public class PluginContainerActivityTest {
    private static final String PACKAGE_NAME = "com.tencent.shadow.core.runtime.container";

    private static Prepare prepare = new Prepare();

    private final CtMethod testOriginalMethod;
    private final String signature;
    private final String methodName;


    public PluginContainerActivityTest(String methodName, String signature, CtMethod testOriginalMethod) {
        this.testOriginalMethod = testOriginalMethod;
        this.signature = signature;
        this.methodName = methodName;
    }

    @Parameterized.Parameters(name = "Run {index}: methodName = {0} signature={1}")
    public static Iterable<Object[]> data() {
        LinkedList<Object[]> list = new LinkedList<>();
        for (CtMethod method : prepare.originalDeclaredMethods) {
            list.add(new Object[]{method.getName(), method.getSignature(), method});
        }
        return list;
    }

    @Test
    public void testMethodExist() {
        Assert.assertTrue(containsCtMethod(prepare.newDeclaredMethods, testOriginalMethod));
    }

    static class Prepare {
        private final Set<CtMethod> originalDeclaredMethods = new HashSet<>();
        private final Set<CtMethod> newDeclaredMethods = new HashSet<>();

        Prepare() {
            try {
                ClassPool classPool = ClassPool.getDefault();
                CtClass oldPluginContainerActivity = classPool.get(PACKAGE_NAME + ".OldPluginContainerActivity");
                CtClass generatedPluginContainerActivity = classPool.get(PACKAGE_NAME + ".GeneratedPluginContainerActivity");
                CtClass pluginContainerActivity = classPool.get(PACKAGE_NAME + ".PluginContainerActivity");

                originalDeclaredMethods.addAll(Arrays.asList(oldPluginContainerActivity.getDeclaredMethods()));
                newDeclaredMethods.addAll(Arrays.asList(generatedPluginContainerActivity.getDeclaredMethods()));
                newDeclaredMethods.addAll(Arrays.asList(pluginContainerActivity.getDeclaredMethods()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}