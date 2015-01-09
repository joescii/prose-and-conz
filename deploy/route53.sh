#!/bin/bash
# Creates a Route 53 ALIAS in the zone specified by `zoneId` for the
# ELB specified by `elbName`. This returns 30 seconds after the update 
# returns with status "INSYNC".

# Exit if anything fails
set -e

if [ $# -lt 2 ]; then
  echo "USAGE: route53 <zoneId> <elbName>"
  exit 1
fi

zone=$1
elbName=$2

# File to contain our provisioning settings for Route 53
rel_json=./route53.json
abs_json=$(cd $(dirname "${rel_json}") && pwd -P)/$(basename "${rel_json}")

# Describe the ELB to get the zone ID and DNS
aws elb describe-load-balancers --load-balancer-name ${elbName} > elb.json
elbZone=`./jq --raw-output '.LoadBalancerDescriptions[0].CanonicalHostedZoneNameID' elb.json`
elbDns=`./jq --raw-output '.LoadBalancerDescriptions[0].DNSName' elb.json`

# Write our command for AWS Route 53 to a json file
cat > ${rel_json} <<EOF
{
  "Comment": "Updated via route53.sh",
  "Changes": [
    {
      "Action": "UPSERT",
      "ResourceRecordSet": {
        "Name": "joescii.com.",
        "Type": "A",
        "AliasTarget": {
          "HostedZoneId": "${elbZone}",
          "DNSName": "${elbDns}",
          "EvaluateTargetHealth": false
        }
      }
    }
  ]
}
EOF

# Update the record set
aws route53 change-resource-record-sets \
  --hosted-zone-id ${zone} \
  --change-batch file://${abs_json} \
  > ./route53-change.json

# Get the ID for our change request
changeId=`./jq --raw-output '.ChangeInfo.Id' ./route53-change.json`
status=PENDING

# Poll the status of our change request until it is "INSYNC"
while [ "${status}" = "PENDING" ]; do
  sleep 5
  aws route53 get-change --id ${changeId} > ./route53-change.json
  status=`./jq --raw-output '.ChangeInfo.Status' ./route53-change.json`
  echo "Route53 ALIAS status: ${status}"
done

# Wait another 30 seconds while the Internets update
sleep 30