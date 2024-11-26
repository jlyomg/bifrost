#!/bin/bash

. ./common/variables.sh

pid=$(ps -ef|grep $project_name |awk 'NR==1 {print $2}')

kill -9 $pid

wait $pid

echo "${app_name}" "is closing..."

sleep 5

echo "${app_name}" "is closed."