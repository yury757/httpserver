package net.yury757.Constant;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    HEAD("HEAD");

    HttpMethod(String value){
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
