#!/bin/bash

. ./common/variables.sh

chmod 711 *.sh

log_dir="/data/logs/${app_name}"
xxljob_log_path="/data/applogs/xxl-job/jobhandler"

if [ ! -d "$log_dir" ]; then
  sudo mkdir -p "$log_dir"
  sudo chmod 777 "$log_dir"
  echo "App log dir is created"
fi

if [ ! -d "$xxljob_log_path" ]; then
  sudo mkdir -p "$xxljob_log_path"
  sudo chmod 777 "$xxljob_log_path"
  echo "xxl job log dir is created"
fi

echo "OK"