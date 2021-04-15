package net.yury757;

import net.yury757.Constant.HttpContentType;
import net.yury757.Constant.HttpUrlType;

import java.lang.reflect.Method;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ServiceTask implements Runnable{
    private SocketChannel clientChannel;
    private Selector selector;

    public ServiceTask(SocketChannel clientChannel, Selector selector){
        this.clientChannel = clientChannel;
        this.selector = selector;
    }

    @Override
    public void run() {
        HttpRequest httpRequest = null;
        HttpResponse httpResponse = null;
        try {
            httpRequest = new HttpRequest(clientChannel);
            httpResponse = new HttpResponse(clientChannel, httpRequest);

            String url = httpRequest.getUrl();
            HttpUrlType httpUrlType = httpRequest.getHttpUrlType();
            if (httpUrlType == HttpUrlType.REQUEST_URL){
                Method method = UrlMappingResolver.getMappedMethod(url);
                if (method == null){
                    method = UrlMappingResolver.getMappedMethod("/404");
                }
                HttpController controller = (HttpController)ObjectContainner.get(HttpController.class);
                method.invoke(controller, httpRequest, httpResponse);
            }else{
                httpResponse.setHttpContentType(HttpContentType.MIME);
                httpResponse.prepareResource();
            }
            this.clientChannel.register(this.selector, SelectionKey.OP_WRITE, httpResponse);
            this.selector.wakeup();
        } catch (Exception e) {
            System.out.println(e.toString());
            httpResponse = new HttpResponse(clientChannel, httpRequest);
            try {
                HttpController controller = (HttpController)ObjectContainner.get(HttpController.class);
                controller.to500(httpRequest, httpResponse);
                this.clientChannel.register(this.selector, SelectionKey.OP_WRITE, httpResponse);
                this.selector.wakeup();
            }catch (Exception ex){
                System.out.println(ex.toString());
            }
        }
    }
}
