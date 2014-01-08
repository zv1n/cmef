#!/bin/bash

function die() {
  echo $*
  exit 1
}

if [[ $# -ge 1 ]]; then
  experiment=$*
else
  experiment=$(pwd)/bin/dev/Experiment.txt
fi

java -jar CMEF.jar $experiment
