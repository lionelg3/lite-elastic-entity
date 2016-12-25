package com.gmail.lionelg3.elastic.io;

import javax.xml.bind.annotation.XmlID;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by lionel on 29/11/2016.
 *
 */
class ElasticIdResolver {

    private enum KindOfId {
        USE_GET_ID_METHOD,
        USE_ANNOTATED_METHOD,
        USE_ANNOTATED_ATTRIBUTE
    }

    private HashMap<Class, KindOfId> classCache;
    private HashMap<Class, Method> methodCache;
    private HashMap<Class, Field> fieldCache;

    ElasticIdResolver() {
        this.classCache = new HashMap<>();
        this.methodCache = new HashMap<>();
        this.fieldCache = new HashMap<>();
    }

    public String getId(Object o) {
        if (classCache.containsKey(o.getClass())) {
            switch (classCache.get(o.getClass())) {
                case USE_GET_ID_METHOD: ;
                case USE_ANNOTATED_METHOD: ;
                case USE_ANNOTATED_ATTRIBUTE: ;
            }
        }

        // use annotated method
        for (Method method : o.getClass().getMethods()) {
            if (method.isAnnotationPresent(XmlID.class)) {
                classCache.put(o.getClass(), KindOfId.USE_ANNOTATED_METHOD);
                methodCache.put(o.getClass(), method);
                return _useAnnotatedMethod(o);
            }
        }
        // use annotated attribute
        for (Field field : o.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(XmlID.class)) {
                field.setAccessible(true);
                classCache.put(o.getClass(), KindOfId.USE_ANNOTATED_ATTRIBUTE);
                fieldCache.put(o.getClass(), field);
                return _useAnnotatedField(o);
            }
        }
        // use getId()
        try {
            Method getId = o.getClass().getMethod("getId");
            if (getId != null) {
                classCache.put(o.getClass(), KindOfId.USE_GET_ID_METHOD);
                return _useGetIdMethod(o);
            }
        } catch (NoSuchMethodException ignored) {}

        return null;
    }

    private String _useGetIdMethod(Object o) {
        try {
            Method getId = o.getClass().getMethod("getId");
            return  (getId.invoke(o) != null) ? getId.invoke(o).toString() : null;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
        return null;
    }

    private String _useAnnotatedMethod(Object o) {
        try {
            Method method = this.methodCache.get(o.getClass());
            return (method.invoke(o) != null) ? method.invoke(o).toString() : null;
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
        return null;
    }

    private String _useAnnotatedField(Object o) {
        try {
            Field field = fieldCache.get(o.getClass());
            return ((field.get(o) != null) ? field.get(o).toString() : null);
        } catch (IllegalAccessException ignored) {}
        return null;
    }
}
