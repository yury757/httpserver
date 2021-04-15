# 介绍

最近在学《计算机网络》，发现计算机网络本质就是网络IO，http协议就是传输的网络数据满足http协议规定的格式，然后就可以被浏览器和服务器接收并处理。

这个项目就是用java写的一个很简单很轻量的非阻塞式http服务器，没有使用任何官方服务器或者第三方服务器，也没有使用任何架子或包。本项目参考了《深入剖析Tomcat》这本书里面的一些思路和架构。

写这个项目中在多线程调试和selector那里碰到很多问题，以及各种模块的设计上也很差（这时还没学设计模式），代码层面也有很多问题，现在仍在学习过程中，等以后有机会再维护更新。

# 使用方法

```shell
mvn clean package
cd target
mkdir resources
# 将项目下的"/httpserver/resources"文件夹复制到当前目录下
xcopy ..\resources .\resources /s /f /h   # windows
# cp -r ../resources/* resources/         # Linux
java -jar MyHttpServer
```

服务器端口为8083，资源文件应该放在上面新建的resources目录下，jar包内部的resources目录无效。

主页：http://localhost:8083

然后根据主页导航可测试功能：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Index</title>
</head>
<body>

<div style="align-content: center">

    <h1>导航：</h1>
    <hr/>

    <p>测试纯文本显示：</p>
    <p><a href="http://localhost:8083/hello">http://localhost:8083/hello</a></p>
    <br/>

    <p>测试404（以下连接或任何不存在的链接）：</p>
    <p><a href="http://localhost:8083/404">http://localhost:8083/404</a></p>
    <br/>

    <p>测试下载文件（在jar包所在目录的resources目录下放置任何文件，都可以直接通过http访问，只要url设置和resources下的url一样）：</p>
    <p><a href="http://localhost:8083/httpserver简单架构.pdf">http://localhost:8083/httpserver简单架构.pdf</a></p>
    <br/>

    <p>测试重定向：</p>
    <p><a href="http://localhost:8083/oldUrl">http://localhost:8083/oldUrl</a></p>
    <br/>

    <p>关闭服务器，访问两次以下请求，第一次访问成功，第二次若一直在转，说明服务器成功关闭：</p>
    <p><a href="http://localhost:8083/SHUTDOWN">http://localhost:8083/SHUTDOWN</a></p>
    <br/>

</div>

</body>
</html>
```

# http_load测试

性能上用http_load测试结果如下，对比了别人手写的一个http服务器，我这个好像很拉跨。

```shell
# 第一次
./http_load -p 100 -s 10 urllist.txt
30217 fetches, 90 max parallel, 3.61093e+07 bytes, in 10.0011 seconds
1195 mean bytes/connection
3021.36 fetches/sec, 3.61053e+06 bytes/sec
msecs/connect: 1.39576 mean, 27.424 max, 0.314 min
msecs/first-response: 3.39209 mean, 49.072 max, 2.435 min
HTTP response codes:
  code 200 -- 30217

# 第二次
./http_load -p 100 -s 10 urllist.txt
35790 fetches, 40 max parallel, 4.2769e+07 bytes, in 10.0001 seconds
1195 mean bytes/connection
3578.95 fetches/sec, 4.27685e+06 bytes/sec
msecs/connect: 1.07808 mean, 5.783 max, 0.248 min
msecs/first-response: 2.7947 mean, 6.942 max, 2.217 min
HTTP response codes:
  code 200 -- 35790
```

