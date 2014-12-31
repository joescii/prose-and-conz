#!/bin/sh

sbt package
mv ./target/scala-2.11/prose-and-conz*.war ./deploy/proseandconz.war

cd ./deploy

mkdir packer
cd packer
wget https://dl.bintray.com/mitchellh/packer/packer_0.7.5_linux_amd64.zip
unzip packer_0.7.5_linux_amd64.zip
cd ..

ls -l

./packer/packer build ./web-srv-packer.json
