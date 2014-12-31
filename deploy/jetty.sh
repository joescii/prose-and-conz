#!/bin/bash -eux

mkdir -p /opt/jetty
mkdir -p /opt/web/mybase
mkdir -p /opt/jetty/temp

sudo useradd --user-group --shell /bin/false --home-dir /opt/jetty/temp jetty

chown --recursive jetty /opt/jetty
chown --recursive jetty /opt/web/mybase
chown --recursive jetty /opt/jetty/temp

cd /opt/jetty
sudo wget http://download.eclipse.org/jetty/8.1.16.v20140903/dist/jetty-distribution-8.1.16.v20140903.zip
sudo unzip jetty-distribution-8.1.16.v20140903.zip
sudo rm jetty-distribution-8.1.16.v20140903.zip

java -jar /opt/jetty/jetty-distribution-9.1.0.v20131115/start.jar --add-to-start=deploy,http,logging

mv /tmp/proseandconz.war /opt/web/mybase/webapps

# Change port to 80?

cp /opt/jetty/jetty-distribution-9.1.0.v20131115/bin/jetty.sh /etc/init.d/jetty
echo "JETTY_HOME=/opt/jetty/jetty-distribution-9.1.0.v20131115" > /etc/default/jetty
echo "JETTY_BASE=/opt/web/mybase" >> /etc/default/jetty
echo "TMPDIR=/opt/jetty/temp" >> /etc/default/jetty
