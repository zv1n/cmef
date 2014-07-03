#!/bin/bash

function die() {
  echo $*
  exit 1
}

if [[ $# -ge 1 ]]; then
  experiment=$*
else
  experiment="-x $(pwd)/bin/dev/Experiment.txt"
fi

java -Xms512m -Xmx512m -jar CMEF.jar $experiment 
