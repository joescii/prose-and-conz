#!/bin/bash -eux

wget http://download.eclipse.org/jetty/8.1.16.v20140903/dist/jetty-distribution-8.1.16.v20140903.zip
sudo mkdir /usr/lib/jetty
unzip jetty-distribution-8.1.16.v20140903.zip
cd jetty-distribution-8.1.16.v20140903
sudo mv . /usr/lib/jetty
