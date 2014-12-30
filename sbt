#!/usr/bin/env bash

root=$(
	cd $(dirname $(readlink $0 || echo $0))/..
	pwd
)

sbtver=0.13.5
sbtjar=sbt-launch.jar
sbtsum=c74ce9787e64c3db246d740869a27117

function download {
	echo "downloading ${sbtjar}" 1>&2
	curl -O "http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/${sbtver}/${sbtjar}"
	mkdir -p project/ && mv ${sbtjar} project/${sbtjar}
}

function sbtjar_md5 {
	openssl md5 < project/${sbtjar} | cut -f2 -d'=' | awk '{print $1}'
}

if [ ! -f "project/${sbtjar}" ]; then
	download
fi

test -f "project/${sbtjar}" || exit 1

jarmd5=$(sbtjar_md5)
if [ "${jarmd5}" != "${sbtsum}" ]; then
	echo "Bad MD5 checksum on ${sbtjar}!" 1>&2
	echo "Moving current sbt-launch.jar to sbt-launch.jar.old!" 1>&2
	mv "project/${sbtjar}" "project/${sbtjar}.old"
	download

	jarmd5=$(sbtjar_md5)
	if [ "${jarmd5}" != "${sbtsum}" ]; then
		echo "Bad MD5 checksum *AGAIN*!" 1>&2
		exit 1
	fi
fi

if [ $# -eq 0 ]; then
	echo "no sbt command given"
	exit 1
fi

case $1 in
	prod*)
		echo "Prod-Mode"
		SBT_OPTS="-Xms64M -Xmx256M -XX:MaxPermSize=512M -Drun.mode=production"
		SBT_CMD="~container:start"
		;;
	rebel*)
		echo "Dev-Mode with JRebel"
		SBT_OPTS="-Xms64M -Xmx256M -XX:MaxPermSize=1G -noverify -javaagent:/usr/jrebel/jrebel.jar"
		SBT_CMD="~container:start"
		;;
	*)
		echo "Dev-Mode"
		SBT_OPTS="-Xms64M -Xmx256M -XX:MaxPermSize=512M"
		SBT_CMD=$@
		;;
esac


java -ea -server ${JAVA_OPTS} ${SBT_OPTS}		\
	-XX:+AggressiveOpts             		\
	-XX:+OptimizeStringConcat			\
	-XX:+UseConcMarkSweepGC               		\
	-XX:+CMSParallelRemarkEnabled   		\
	-XX:+CMSClassUnloadingEnabled   		\
	-XX:+CMSIncrementalMode         		\
	-jar project/${sbtjar} ${SBT_CMD}
