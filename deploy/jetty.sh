#!/bin/bash -eux

cd /usr/lib
sudo wget http://download.eclipse.org/jetty/8.1.16.v20140903/dist/jetty-distribution-8.1.16.v20140903.zip
sudo unzip jetty-distribution-8.1.16.v20140903.zip
sudo rm jetty-distribution-8.1.16.v20140903.zip
sudo mv jetty-distribution-8.1.16.v20140903 jetty
sudo mv /tmp/proseandconz.war /usr/lib/jetty/webapps