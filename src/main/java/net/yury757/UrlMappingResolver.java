package net.yury757;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class UrlMappingResolver {
    public static HashMap<String, Method> mappedUrl = new HashMap<>();

    static{
        try {
            initUrlMapping();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static void initUrlMapping() throws NoSuchMethodException {
        Class<HttpController> httpControllerClass = HttpController.class;
        Method[] declaredMethods = httpControllerClass.getDeclaredMethods();
        Set<String> paramNames = new HashSet<>();
        for (Method declaredMethod : declaredMethods) {
            Set<Class<?>> parameterTypes = new HashSet<>(Arrays.asList(declaredMethod.getParameterTypes()));
            if (!(parameterTypes.contains(HttpRequest.class) && parameterTypes.contains(HttpResponse.class))){
                continue;
            }
            RequestMapping declaredAnnotation = declaredMethod.getDeclaredAnnotation(RequestMapping.class);
            if (declaredAnnotation != null){
                mappedUrl.put(declaredAnnotation.value(), declaredMethod);
            }
        }
    }

    public static Method getMappedMethod(String url){
        Method method = mappedUrl.get(url);
        if (method != null){
            return method;
        }else{
            return mappedUrl.get("/404");
        }
    }
}
