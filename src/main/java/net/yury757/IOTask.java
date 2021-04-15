package net.yury757;

import net.yury757.Constant.HttpUrlType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class IOTask implements Runnable{
    public final static int BUFFER_SIZE = 8 * 1024 * 1024;
    public final static int CHAR_BUFFER_SIZE = 2014;
    private HttpResponse response;
    private SocketChannel client;

    public IOTask(HttpResponse response, SocketChannel client) {
        this.response = response;
        this.client = client;
    }

    @Override
    public void run() {
        ByteBuffer responseBuffer = response.getResponseBuffer();
        FileChannel fileChannel = null;
        try{
            client.write(responseBuffer);
            if (this.response.getHttpUrlType() == HttpUrlType.RESOURCE_URL){
                fileChannel = this.response.getFileChannel();
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                while (fileChannel.read(buffer) != -1){
                    buffer.flip();
                    // 直接将缓冲区中的数据写入客户端会有问题
                    // 如果客户端的缓冲区满了，write方法就会返回0，即没写入数据，这样会造成数据丢失
                    while (buffer.hasRemaining()){
                        client.write(buffer);
                    }
                    buffer.clear();
                }
            }
            client.shutdownOutput();
            if ("/SHUTDOWN".equals(response.getRequest().getUrl())){
                Server.POWER = false;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (fileChannel != null){
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
