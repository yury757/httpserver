package net.yury757.Constant;

public enum HttpUrlType {
    REQUEST_URL("普通请求url"),
    RESOURCE_URL("资源获取url");

    private String value;

    public String getValue() {
        return value;
    }

    HttpUrlType(String value){
        this.value = value;
    }
}
