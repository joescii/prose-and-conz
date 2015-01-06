#!/bin/sh
# Waits for a URL to return a 200 code by polling every `interval` seconds until `maxTries` is reached.

# Exit if anything fails
set -e

if [ $# -lt 1 ]; then
  echo "USAGE: waitFor <url> <interval> <maxTries>"
  exit 1
fi

url=$1
interval=$2
maxTries=$3
count=0
while [ ${count} -lt ${maxTries} ]; do
  status=`curl -w %{http_code} ${url} | tail -1`
  echo ${status}
  if [ ${status} -eq 200 ]; then
    exit 0
  fi
  count=`expr $count + 1`  
  sleep ${interval}
done

exit 2