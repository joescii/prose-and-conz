#!/bin/sh

# Exit if anything fails
set -e

# Create a timestamp for uniquefying stuff
timestamp=`date +"%Y%m%d%H%M%S"`

# Package our application war and place it in our deploy directory 
sbt package
mv ./target/scala-2.11/prose-and-conz*.war ./deploy/proseandconz.war

cd ./deploy

# Install packer
mkdir packer
cd packer
wget https://dl.bintray.com/mitchellh/packer/packer_0.7.5_linux_amd64.zip
unzip packer_0.7.5_linux_amd64.zip
cd ..

# Install terraform
mkdir terraform
cd terraform
wget https://dl.bintray.com/mitchellh/terraform/terraform_0.3.5_linux_amd64.zip
unzip terraform_0.3.5_linux_amd64.zip
cd ..

# Install aws cli
pip install awscli

# Install jq 
wget http://stedolan.github.io/jq/download/linux64/jq
chmod 700 ./jq


# Build the AMI for our server
./packer/packer build -var timestamp=${timestamp} ./web-srv-packer.json

# Query for the AMI ID just created by packer, based on the timestamp tag
aws ec2 describe-images --filters Name=tag:Timestamp,Values=${timestamp} > ami.json
PAC_AMI_ID=`./jq '.Images[0].ImageId' ami.json`

# Get the current terraform state
aws s3 cp s3://proseandconz/terraform/terraform.tfstate ./terraform.tfstate 

# Update the AWS infrastructure
./terraform/terraform plan  \
  -var "access_key=${AWS_ACCESS_KEY_ID}" \
  -var "secret_key=${AWS_SECRET_ACCESS_KEY}" \
  -var "pac_ami_id=${PAC_AMI_ID}" 
./terraform/terraform apply \
  -var "access_key=${AWS_ACCESS_KEY_ID}" \
  -var "secret_key=${AWS_SECRET_ACCESS_KEY}" \
  -var "pac_ami_id=${PAC_AMI_ID}" 

# Save the terraform state 
cat ./terraform.tfstate
aws s3 cp ./terraform.tfstate s3://proseandconz/terraform/terraform.tfstate

