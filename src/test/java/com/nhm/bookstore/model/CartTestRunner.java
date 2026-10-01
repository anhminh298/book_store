package com.nhm.bookstore.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class CartTestRunner {
    public static void main(String[] args) {
        CartTest_24162073 testInstance = new CartTest_24162073();
        Class<?> clazz = CartTest_24162073.class;

        Method[] methods = clazz.getDeclaredMethods();
        List<Method> testMethods = new ArrayList<>();
        Method setUpMethod = null;

        for (Method m : methods) {
            if ("setUp".equals(m.getName())) {
                setUpMethod = m;
            } else if (m.getName().startsWith("test") && Modifier.isPublic(m.getModifiers()) || m.getName().startsWith("test")) {
                testMethods.add(m);
            }
        }

        System.out.println("==================================================");
        System.out.println("Running " + testMethods.size() + " Cart Model tests...");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        for (Method tm : testMethods) {
            try {
                if (setUpMethod != null) {
                    setUpMethod.setAccessible(true);
                    setUpMethod.invoke(testInstance);
                }
                tm.setAccessible(true);
                tm.invoke(testInstance);
                System.out.println(" [PASS] " + tm.getName());
                passed++;
            } catch (Throwable t) {
                System.err.println(" [FAIL] " + tm.getName() + " -> " + t.getCause());
                if (t.getCause() != null) {
                    t.getCause().printStackTrace(System.err);
                }
                failed++;
            }
        }

        System.out.println("==================================================");
        System.out.println("Test Results: " + passed + " passed, " + failed + " failed.");
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
