#!/bin/bash
CMAKES=`sudo find -name cmake_build_output* | grep debug`
for CMK in $CMAKES
do
	stat $CMK
	cat -n $CMK
done
