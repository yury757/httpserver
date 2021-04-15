package net.yury757;

import net.yury757.Constant.HttpMethod;
import net.yury757.Constant.HttpProtocolVersion;
import net.yury757.Constant.HttpUrlType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class HttpRequest {
    private HttpMethod method;
    private String url;
    private HttpProtocolVersion httpProtocolVersion;
    private HashMap<String, String> properties;
    private String data;
    private HttpUrlType httpUrlType;

    public HttpUrlType getHttpUrlType() {
        return httpUrlType;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public HttpProtocolVersion getHttpProtocolVersion() {
        return httpProtocolVersion;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public String getData() {
        return data;
    }

    public HttpRequest(SocketChannel source) throws IOException {
        this.parseRequest(source);
    }

    private void parseRequest(SocketChannel source) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int len = 0;
        StringBuilder sb = new StringBuilder(2048);
        while ((len = source.read(byteBuffer)) > 0){
            byteBuffer.flip();
            for (int i = 0; i < len; i++) {
                sb.append((char) byteBuffer.get());
            }
            byteBuffer.clear();
        }
        this.parseRequest(sb.toString());
    }

    private void parseRequest(String requestString) throws UnsupportedEncodingException {
        int index1 = requestString.indexOf(' ');
        if (index1 != -1){
            String methodString = requestString.substring(0, index1);
            // 解析http方法
            this.setHttpMethod(methodString);
            if (this.method == null){
                // todo #001 405响应
            }
            int index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1){
                String urlString = URLDecoder.decode(requestString.substring(index1 + 1, index2), "utf-8");
                // 解析url
                this.setUrl(urlString);
                // todo #005 url拦截器
                if (urlString.contains(".")){
                    this.setHttpUrlType(HttpUrlType.RESOURCE_URL);
                }else{
                    this.setHttpUrlType(HttpUrlType.REQUEST_URL);
                }
                int index3 = requestString.indexOf("\r\n", index2 + 1);
                if (index3 > index2){
                    String pvString = requestString.substring(index2 + 1, index3);
                    // 解析请求协议
                    this.setHttpProtocolVersion(pvString);
                    System.out.println(requestString.substring(0, index3));
                    // todo #002 解析请求头部的其他属性properties

                    // todo #003 解析请求数据data

                }
            }
        }
    }


    private void setHttpMethod(String methodString){
        HttpMethod m;
        switch (methodString){
            case "GET":
                m = HttpMethod.GET;
                break;
            case "POST":
                m = HttpMethod.POST;
                break;
            case "HEAD":
                m = HttpMethod.HEAD;
                break;
            default:
                m = null;
        }
        this.method = m;
    }

    private void setUrl(String urlString){
        this.url = urlString;
    }

    private void setHttpUrlType(HttpUrlType httpUrlType){
        this.httpUrlType = httpUrlType;
    }

    private void setHttpProtocolVersion(String pvString){
        HttpProtocolVersion pv;
        switch (pvString){
            case "HTTP/1.1":
                pv = HttpProtocolVersion.HTTP1_1;
                break;
            case "HTTP/1.0":
                pv = HttpProtocolVersion.HTTP1_0;
                break;
            case "HTTP/0.9":
                pv = HttpProtocolVersion.HTTP0_9;
                break;
            default:
                pv = null;
        }
        this.httpProtocolVersion = pv;
    }
}
