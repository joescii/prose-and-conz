#!/bin/bash
# 

# Exit if anything fails
set -e

if [ $# -lt 1 ]; then
  echo "USAGE: route53 <zoneId> <elbName>"
  exit 1
fi

zone=$1
elbName=$2
rel_json=./route53.json
abs_json=$(cd $(dirname "${rel_json}") && pwd -P)/$(basename "${rel_json}")

aws elb describe-load-balancers --load-balancer-name ${elbName} > elb.json
elbZone=`./jq --raw-output '.LoadBalancerDescriptions[0].CanonicalHostedZoneNameID' elb.json`
elbDns=`./jq --raw-output '.LoadBalancerDescriptions[0].DNSName' elb.json`

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

aws route53 change-resource-record-sets \
  --hosted-zone-id ${zone} \
  --change-batch file://${abs_json}
