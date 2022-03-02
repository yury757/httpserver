#/bin/bash

log="/home/yury/java/httpserver/nohup.out"
process="/home/yury/java/httpserver/target/MyHttpServer.jar"
nohup java -jar $process >> $log 2>&1 &
