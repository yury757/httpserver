package net.yury757.utils;

import java.util.HashMap;

public class PathUtil {
    private static String resourcesPath = "/resources";
    private static String rootPath;

    static {
        rootPath = System.getProperty("user.dir") + resourcesPath;
        System.out.println(rootPath);
    }

    public static String getRootPath(){
        return rootPath;
    }

    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        map.put("a", "a");
        String b = map.get("b");
        System.out.println(b);
    }
}
