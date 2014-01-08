#!/bin/bash

mkdir CMEF-${1}

cp CMEF.jar CMEF-${1}/

zip CMEF-${1}.zip CMEF-${1}/CMEF.jar

cp CMEF-${1}.zip ~/Dropbox/Public/
