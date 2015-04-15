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

tmpDir="/tmp/r53"

if [ ! -e $tmpDir ]; then
  mkdir $tmpDir
fi

zone=$1
elbName=$2

# File to contain our provisioning settings for Route 53
rel_json=$tmpDir/route53.json_$zone
abs_json=$(cd $(dirname "${rel_json}") && pwd -P)/$(basename "${rel_json}")

# Describe the ELB to get the zone ID and DNS
aws elb describe-load-balancers --load-balancer-name ${elbName} > $tmpDir/elb.json_$zone
elbZone=`./jq --raw-output '.LoadBalancerDescriptions[0].CanonicalHostedZoneNameID' $tmpDir/elb.json_$zone`
elbDns=`./jq --raw-output '.LoadBalancerDescriptions[0].DNSName' $tmpDir/elb.json_$zone`

# Describe the Zone to get its Name
aws route53 get-hosted-zone --id ${zone} > $tmpDir/r53.json_$zone
r53Name=`./jq --raw-output '.HostedZone.Name' $tmpDir/r53.json_$zone`

# Write our command for AWS Route 53 to a json file
cat > ${rel_json} <<EOF
{
  "Comment": "Updated via route53.sh",
  "Changes": [
    {
      "Action": "UPSERT",
      "ResourceRecordSet": {
        "Name": "${r53Name}",
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
  > $tmpDir/route53-change.json_$zone

# Get the ID for our change request
changeId=`./jq --raw-output '.ChangeInfo.Id' $tmpDir/route53-change.json_$zone`
status=PENDING

# Poll the status of our change request until it is "INSYNC"
while [ "${status}" = "PENDING" ]; do
  sleep 5
  aws route53 get-change --id ${changeId} > $tmpDir/route53-change.json_$zone
  status=`./jq --raw-output '.ChangeInfo.Status' $tmpDir/route53-change.json_$zone`
  echo "Route53 ALIAS status: ${status}"
done

find $tmpDir -type f -delete

# Wait another 30 seconds while the Internets update
sleep 30
