package net.yury757;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    public static final int CORE_POOL_SIZE = 50;
    public static final int MAX_POOL_SIZE = 200;
    public static final long KEEP_ALIVE_TIME = 60L;
    public static final int QUEUE_CAPACITY = 1000;
    public static final int port = 8083;
    public static final int TIMEOUT = 0;
    public static boolean POWER = true;

    static{
        try{
            ObjectContainner.add(HttpController.class);
            ObjectContainner.add(UrlMappingResolver.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void run(){
        Date startTime = new Date();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());
        try(ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        )
        {
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            Date endTime = new Date();
            System.out.println("服务器在8083端口启动成功，消耗" + (endTime.getTime() - startTime.getTime()) + "毫秒");

            while (true){
                if (!POWER) break;
                if (selector.select(TIMEOUT) == 0){
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    if (sk.isAcceptable()){
                        SocketChannel socketChannel = ((ServerSocketChannel)sk.channel()).accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }else if (sk.isReadable()){
                        SocketChannel socketChannel = (SocketChannel) sk.channel();
                        ServiceTask serviceTask = new ServiceTask(socketChannel, selector);
                        sk.interestOps(sk.interestOps() & ~SelectionKey.OP_READ);
                        threadPool.execute(serviceTask);
                    }else if (sk.isWritable()){
                        HttpResponse response = (HttpResponse)sk.attachment();
                        SocketChannel socketChannel = (SocketChannel) sk.channel();
                        IOTask ioTask = new IOTask(response, socketChannel);
                        threadPool.execute(ioTask);
                        sk.interestOps(sk.interestOps() & ~SelectionKey.OP_WRITE);
                    }
                }
            }
            System.out.println("Bye");
            System.exit(1);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
