package net.yury757.Constant;

public enum HttpProtocolVersion {
    HTTP1_1("HTTP/1.1"),
    HTTP1_0("HTTP/1.0"),
    HTTP0_9("HTTP/0.9");

    HttpProtocolVersion(String value){
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
