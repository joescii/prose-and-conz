#!/bin/sh

# Exit if anything fails
set -e

# Create a timestamp for uniquefying stuff
timestamp=`date +"%Y%m%d%H%M%S"`

# Package our application war and place it in our deploy directory 
sbt package
mv ./target/scala-2.11/*.war ./deploy/root.war

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
wget https://dl.bintray.com/mitchellh/terraform/terraform_0.4.0_linux_amd64.zip
unzip terraform_0.4.0_linux_amd64.zip
cd ..

# Install aws cli
pip install awscli

# Install jq 
wget http://stedolan.github.io/jq/download/linux64/jq
chmod 700 ./jq

chmod 700 ./*.sh

# Archive the war file
aws s3 cp ./root.war s3://proseandconz/deployments/pac-${timestamp}.war

if [ -z "$PAC_AMI_ID" ]; then
  # Build the AMI for our server
  ./packer/packer build -var timestamp=${timestamp} ./web-srv-packer.json

  # Query for the AMI ID just created by packer, based on the timestamp tag
  aws ec2 describe-images --filters Name=tag:Timestamp,Values=${timestamp} > ami.json
  PAC_AMI_ID=`./jq --raw-output '.Images[0].ImageId' ami.json`
fi

# Get the current terraform state
aws s3 cp s3://proseandconz/terraform/terraform.tfstate ./terraform.tfstate

PAC_LAUNCH_CONFIG_NAME=pac-launch-config-${timestamp}
# Update the AWS infrastructure
./terraform/terraform plan  \
  -var "access_key=${AWS_ACCESS_KEY_ID}" \
  -var "secret_key=${AWS_SECRET_ACCESS_KEY}" \
  -var "pac_ami_id=${PAC_AMI_ID}" \
  -var "timestamp=${timestamp}" 
./terraform/terraform apply \
  -var "access_key=${AWS_ACCESS_KEY_ID}" \
  -var "secret_key=${AWS_SECRET_ACCESS_KEY}" \
  -var "pac_ami_id=${PAC_AMI_ID}" \
  -var "timestamp=${timestamp}"

# If ever needed...
#./terraform/terraform destroy -force  \

# Save the terraform state 
cat ./terraform.tfstate
aws s3 cp ./terraform.tfstate s3://proseandconz/terraform/terraform.tfstate

