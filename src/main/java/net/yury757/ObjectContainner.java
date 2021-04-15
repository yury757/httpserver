package net.yury757;

import java.util.HashMap;
import java.util.Map;

public class ObjectContainner {
    private static Map<Class<?>, Object> container = new HashMap<>();

    public static void add(Class<?> c) throws IllegalAccessException, InstantiationException {
        container.put(c, c.newInstance());
    }

    public static Object get(Class<?> c){
        return container.get(c);
    }
}
