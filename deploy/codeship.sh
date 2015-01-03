#!/bin/sh

sbt package
mv ./target/scala-2.11/prose-and-conz*.war ./deploy/proseandconz.war

cd ./deploy

# mkdir packer
# cd packer
# wget https://dl.bintray.com/mitchellh/packer/packer_0.7.5_linux_amd64.zip
# unzip packer_0.7.5_linux_amd64.zip
# cd ..

# ./packer/packer build ./web-srv-packer.json

mkdir terraform
cd terraform
wget https://dl.bintray.com/mitchellh/terraform/terraform_0.3.5_linux_amd64.zip
unzip terraform_0.3.5_linux_amd64.zip
cd ..

./terraform/terraform plan -var "access_key=${AWS_ACCESS_KEY_ID}" -var "secret_key=${AWS_SECRET_ACCESS_KEY}"
./terraform/terraform apply -var "access_key=${AWS_ACCESS_KEY_ID}" -var "secret_key=${AWS_SECRET_ACCESS_KEY}"

ls -l
cat ./terraform.tfstate
