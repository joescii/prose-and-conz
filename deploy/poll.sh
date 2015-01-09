#!/bin/sh
# Polls a URL and prints the status code until killed

# Exit if anything fails
set -e

if [ $# -lt 1 ]; then
  echo "USAGE: poll <url>"
  exit 1
fi

url=$1
while [ "0" -eq "0" ]; do
  status=`curl -w %{http_code} ${url} | tail -1`
  echo ${status}
done
