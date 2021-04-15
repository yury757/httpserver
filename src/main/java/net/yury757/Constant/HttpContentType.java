package net.yury757.Constant;

public enum HttpContentType {

    HTML("text/html; charset=utf-8"),
    TEXT("text/plain; charset=utf-8"),
    XML("text/xml"),
    GIF("image/gif"),
    JEPG("image/jepg"),
    PNG("image/png"),
    MIME("application/octet-stream");

    private String value;

    HttpContentType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
