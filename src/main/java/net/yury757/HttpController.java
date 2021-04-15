package net.yury757;

import net.yury757.Constant.HttpContentType;
import net.yury757.Constant.HttpProtocolVersion;
import net.yury757.Constant.HttpStatusCode;

import java.io.IOException;

public class HttpController {

    @RequestMapping("/hello")
    public void hello(HttpRequest request, HttpResponse response) throws IOException {
        response.setHttpProtocolVersion(HttpProtocolVersion.HTTP1_1);
        response.setHttpContentType(HttpContentType.TEXT);
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.setHtml("/hello.html");
    }

    @RequestMapping("/")
    public void index(HttpRequest request, HttpResponse response) throws IOException {
        response.setHttpProtocolVersion(HttpProtocolVersion.HTTP1_1);
        response.setHttpContentType(HttpContentType.HTML);
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.setHtml("/index.html");
    }

    @RequestMapping("/404")
    public void to404(HttpRequest request, HttpResponse response) {
        response.reply404();
    }

    @RequestMapping("/500")
    public void to500(HttpRequest request, HttpResponse response) {
        response.reply500();
    }

    @RequestMapping("/oldUrl")
    public void oldUrl(HttpRequest request, HttpResponse response){
        response.sendRedirect("/newUrl");
    }

    @RequestMapping("/newUrl")
    public void newUrl(HttpRequest request, HttpResponse response) throws IOException {
        response.setHttpContentType(HttpContentType.HTML);
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.setHtml("/new.html");
    }

    @RequestMapping("/SHUTDOWN")
    public void shutdown(HttpRequest request, HttpResponse response){
        response.write("<h1>服务器已成功关闭。</h1>");
    }

}
