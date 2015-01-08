#!/bin/sh

# Exit if anything fails
set -e

if [ $# -lt 1 ]; then
  echo "USAGE: elb-stickiness <elbName>"
  exit 1
fi

name=$1

aws elb create-lb-cookie-stickiness-policy \
  --load-balancer-name ${name} \
  --policy-name LiftStickySessionPolicy 
aws elb set-load-balancer-policies-of-listener \
  --load-balancer-name ${name} \
  --load-balancer-port 80 \
  --policy-names LiftStickySessionPolicy

