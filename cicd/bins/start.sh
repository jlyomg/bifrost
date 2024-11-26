#!/bin/bash

. ./common/variables.sh

log_path="/data/logs/${app_name}/start.log"

if [ ! -f "$log_path" ]; then
  # 判断目录是否存在，如果不存在则创建目录
  dir_path=$(dirname "$log_path")
  if [ ! -d "$dir_path" ]; then
    mkdir -p "$dir_path"
  fi
  # 创建文件
  touch "$log_path"
fi

nohup java -jar ../package/$project_name.jar >$log_path 2>&1 &

tail -f $log_path
