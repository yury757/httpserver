#/bin/bash

process="MyHttpServer.jar"
pid=$(ps aux | grep $process | grep -v grep | awk '{print $2}')
if [ -z $pid ]; then
    echo "process $process not exists"
    exit
else
    echo "process id: $pid"
    kill -9 $pid
    echo "process $process stopped"
fi
