#!/bin/sh

sbt package
mv ./target/scala-2.11/prose-and-conz*.war ./deploy/proseandconz.war

cd ./deploy

# Install packer
# mkdir packer
# cd packer
# wget https://dl.bintray.com/mitchellh/packer/packer_0.7.5_linux_amd64.zip
# unzip packer_0.7.5_linux_amd64.zip
# cd ..

# Build the AMI for our server
# ./packer/packer build ./web-srv-packer.json

# Install terraform
mkdir terraform
cd terraform
wget https://dl.bintray.com/mitchellh/terraform/terraform_0.3.5_linux_amd64.zip
unzip terraform_0.3.5_linux_amd64.zip
cd ..

# Get the terraform state
pip install awscli
aws s3 cp s3://proseandconz/terraform/terraform.tfstate ./terraform.tfstate 

# Update the AWS infrastructure
./terraform/terraform plan  s-var "access_key=${AWS_ACCESS_KEY_ID}" -var "secret_key=${AWS_SECRET_ACCESS_KEY}"
./terraform/terraform apply -var "access_key=${AWS_ACCESS_KEY_ID}" -var "secret_key=${AWS_SECRET_ACCESS_KEY}"

# Save the terraform state for next time
cat ./terraform.tfstate
aws s3 cp ./terraform.tfstate s3://proseandconz/terraform/terraform.tfstate

