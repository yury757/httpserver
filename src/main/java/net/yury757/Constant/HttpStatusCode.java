package net.yury757.Constant;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    HttpStatusCode(int code, String value){
        this.code = code;
        this.value = value;
    }

    private int code;
    private String value;

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
