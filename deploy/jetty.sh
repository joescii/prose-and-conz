#!/bin/bash -eux

version=9.2.6.v20141205

sudo mkdir -p /opt/jetty
sudo mkdir -p /opt/web/mybase
sudo mkdir -p /opt/jetty/temp

cd /opt/jetty
sudo wget http://download.eclipse.org/jetty/${version}/dist/jetty-distribution-${version}.zip
sudo unzip jetty-distribution-${version}.zip
sudo rm jetty-distribution-${version}.zip

cd /opt/web/mybase
sudo java -jar /opt/jetty/jetty-distribution-${version}/start.jar --add-to-start=deploy,http,logging

sudo mv /tmp/root.war /opt/web/mybase/webapps

# Set up user
sudo useradd --user-group --shell /bin/false --home-dir /opt/jetty/temp jetty

sudo chown --recursive jetty /opt/jetty
sudo chown --recursive jetty /opt/web/mybase
sudo chown --recursive jetty /opt/jetty/temp

# Set up service
sudo sh -c "cat > /etc/init.d/jetty" <<EOF
#!/usr/bin/env bash
JAVA_OPTIONS+=("-Drun.mode=production")
/opt/jetty/jetty-distribution-${version}/bin/jetty.sh \$*
EOF
sudo chmod 0750 /etc/init.d/jetty

sudo sh -c "cat > /etc/default/jetty" <<EOF
JETTY_HOME=/opt/jetty/jetty-distribution-${version}
JETTY_BASE=/opt/web/mybase
TMPDIR=/opt/jetty/temp
EOF

sudo update-rc.d jetty defaults
