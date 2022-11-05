package com.coditory.quark.eventbus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.joining;

final class Reflections {
    static String toShortString(Method method) {
        String params = Arrays.stream(method.getGenericParameterTypes())
                .map(t -> t instanceof Class<?> ? ((Class<?>) t).getSimpleName() : t.getTypeName())
                .collect(joining(", "));
        return method.getDeclaringClass().getSimpleName() +
                '.' +
                method.getName() +
                '(' +
                params +
                ')';
    }

    static List<Method> getAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationType) {
        Map<MethodIdentifier, Method> identifiers = new LinkedHashMap<>();
        List<Class<?>> types = getAllInterfacesAndClasses(clazz);
        for (Class<?> type : types) {
            for (Method method : type.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotationType) && !method.isSynthetic()) {
                    MethodIdentifier identifier = MethodIdentifier.of(method);
                    if (!identifiers.containsKey(identifier)) {
                        identifiers.put(identifier, method);
                    }
                }
            }
        }
        return List.copyOf(identifiers.values());
    }

    static List<Class<?>> getAllInterfacesAndClasses(Class<?> clazz) {
        Set<Class<?>> result = new LinkedHashSet<>();
        getAllInterfacesAndClasses(clazz, result);
        return List.copyOf(result);
    }

    private static void getAllInterfacesAndClasses(Class<?> clazz, Set<Class<?>> visited) {
        while (clazz != null && !visited.contains(clazz)) {
            visited.add(clazz);
            for (Class<?> iface : clazz.getInterfaces()) {
                getAllInterfacesAndClasses(iface, visited);
            }
            clazz = clazz.getSuperclass();
        }
    }

    private record MethodIdentifier(String name, List<Class<?>> parameterTypes) {
        static MethodIdentifier of(Method method) {
            return new MethodIdentifier(method.getName(), Arrays.asList(method.getParameterTypes()));
        }
    }
}
