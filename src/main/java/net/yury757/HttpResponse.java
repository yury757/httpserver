package net.yury757;

import net.yury757.Constant.HttpContentType;
import net.yury757.Constant.HttpProtocolVersion;
import net.yury757.Constant.HttpStatusCode;
import net.yury757.Constant.HttpUrlType;
import net.yury757.utils.DateUtil;
import net.yury757.utils.PathUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import static net.yury757.IOTask.CHAR_BUFFER_SIZE;

public class HttpResponse {
    private HttpProtocolVersion httpProtocolVersion;
    private HttpStatusCode httpStatusCode;
    private HttpRequest request;
    private SocketChannel outChannel;
    private HttpContentType httpContentType;
    private StringBuilder responseBody;
    private String connection;
    private HttpUrlType httpUrlType;
    private String location;

    public void setLocation(String location) {
        this.location = location;
    }

    public HttpUrlType getHttpUrlType() {
        return httpUrlType;
    }

    public void setHttpContentType(HttpContentType httpContentType) {
        this.httpContentType = httpContentType;
    }

    public void setRequest(HttpRequest request){
        this.request = request;
    }

    public void setHttpProtocolVersion(HttpProtocolVersion httpProtocolVersion) {
        this.httpProtocolVersion = httpProtocolVersion;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * 默认设置
     */
    public HttpResponse(SocketChannel outChannel, HttpRequest request){
        this.outChannel = outChannel;
        this.request = request;
        this.httpStatusCode = HttpStatusCode.OK;
        this.httpContentType = HttpContentType.HTML;
        this.httpProtocolVersion = HttpProtocolVersion.HTTP1_1;
        this.connection = "keep-alive";
        this.httpUrlType = request.getHttpUrlType();
        this.responseBody = new StringBuilder();
    }

    public boolean prepareResource() throws Exception {
        if (this.request.getHttpUrlType() != HttpUrlType.RESOURCE_URL){
            this.reply404();
            this.httpUrlType = HttpUrlType.REQUEST_URL;
            return false;
        }
        File file = new File(PathUtil.getRootPath(), this.request.getUrl());
        if (!file.exists()){
            this.reply404();
            this.httpUrlType = HttpUrlType.REQUEST_URL;
            return false;
        }
        return true;
    }

    public FileChannel getFileChannel() throws IOException {
        File file = new File(PathUtil.getRootPath(), this.request.getUrl());
        this.httpContentType = HttpContentType.MIME;
        System.out.println("请求文件：" + file.getPath());
        return FileChannel.open(Paths.get(file.getPath()), StandardOpenOption.READ);
    }

    private byte[] getResponseHeaderBytes(){
        StringBuilder sb = new StringBuilder(2048);
        sb.append(this.httpProtocolVersion.getValue());
        sb.append(" ");
        sb.append(this.httpStatusCode.getCode());
        sb.append(" ");
        sb.append(this.httpStatusCode.getValue());
        sb.append("\r\nContent-Type: ");
        sb.append(this.httpContentType.getValue());
        sb.append("\r\nConnection: ");
        sb.append(this.connection);
        if (this.httpUrlType == HttpUrlType.RESOURCE_URL){
            sb.append("\r\ncontent-disposition: attachment; filename=");
            String url = this.request.getUrl();
            sb.append(url.substring(url.lastIndexOf("/") + 1));
        }
        if ((int)(this.httpStatusCode.getCode() / 100) == 3){
            sb.append("\r\nLocation: ");
            sb.append(this.location);
        }
        sb.append("\r\nDate: ");
        sb.append(DateUtil.GMTDateFormatter.format(new Date()));
        sb.append("\r\n\r\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public HttpRequest getRequest() {
        return request;
    }

    private byte[] getResponseBodyBytes(){
        return this.responseBody.toString().getBytes(StandardCharsets.UTF_8);
    }

    public ByteBuffer getResponseBuffer(){
        byte[] headBytes = this.getResponseHeaderBytes();
        byte[] bodyBytes = this.getResponseBodyBytes();

        ByteBuffer responseBuffer = ByteBuffer.allocate(headBytes.length + bodyBytes.length);

        responseBuffer.put(headBytes);
        responseBuffer.put(bodyBytes);
        responseBuffer.flip();
        return responseBuffer;
    }

    public void sendRedirect(String newUrl){
        this.httpStatusCode = HttpStatusCode.FOUND;
        this.location = newUrl;
    }

    public void write(String responseString){
        this.responseBody.append(responseString);
    }

    public void setHtml(String htmlFileUrl) throws IOException{
        File file = new File(PathUtil.getRootPath(), htmlFileUrl);
        if (!file.exists()){
            this.reply404();
            return;
        }
        char[] buffer = new char[CHAR_BUFFER_SIZE];
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        int len = 0;
        while ((len = reader.read(buffer)) != -1){
            this.responseBody.append(buffer, 0, len);
        }
        reader.close();
    }

    public void reply404() {
        this.setHttpStatusCode(HttpStatusCode.NOT_FOUND);
        this.setHttpContentType(HttpContentType.HTML);
        this.responseBody.append("<h1>页面丢失了</h1>");
    }

    public void reply500() {
        this.setHttpStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
        this.setHttpContentType(HttpContentType.HTML);
        this.responseBody.append("<h1>代码有bug了。。。</h1>");
    }
}
