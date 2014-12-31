#!/bin/bash -eux

version=9.2.6.v20141205

sudo mkdir -p /opt/jetty
sudo mkdir -p /opt/web/mybase
sudo mkdir -p /opt/jetty/temp

cd /opt/jetty
sudo wget http://download.eclipse.org/jetty/${version}/dist/jetty-distribution-${version}.zip
sudo unzip jetty-distribution-${version}.zip
sudo rm jetty-distribution-${version}.zip

sudo java -jar /opt/jetty/jetty-distribution-${version}/start.jar --add-to-start=deploy,http,logging

sudo mv /tmp/proseandconz.war /opt/web/mybase/webapps

# Set up user
sudo useradd --user-group --shell /bin/false --home-dir /opt/jetty/temp jetty

sudo chown --recursive jetty /opt/jetty
sudo chown --recursive jetty /opt/web/mybase
sudo chown --recursive jetty /opt/jetty/temp

# Change port to 80?

# Set up service
sudo cp /opt/jetty/jetty-distribution-${version}/bin/jetty.sh /etc/init.d/jetty
sudo mkdir /etc/default
sudo echo "JETTY_HOME=/opt/jetty/jetty-distribution-${version}" > /etc/default/jetty
sudo echo "JETTY_BASE=/opt/web/mybase" >> /etc/default/jetty
sudo echo "TMPDIR=/opt/jetty/temp" >> /etc/default/jetty
