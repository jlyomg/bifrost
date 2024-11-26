#!/bin/bash

. ./common/variables.sh

cd ../../

git reset --hard
git pull

mvn clean package -Dmaven.test.skip=true

cd cicd/bins/
cp -rf ../../${project_name}/target/${project_name}.jar ../package

echo "Package OK"
