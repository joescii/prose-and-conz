#!/bin/sh

mkdir packer
cd packer
wget https://dl.bintray.com/mitchellh/packer/packer_0.7.5_linux_amd64.zip
unzip packer_0.7.5_linux_amd64.zip
cd ..

./packer/packer build ./deploy/web-srv-packer.json
