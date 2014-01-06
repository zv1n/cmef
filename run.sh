#!/bin/bash

function die() {
  echo $*
  exit 1
}

if [[ $ARGC -eq 1 ]]; then
  experiment=$(shift)
else
  experiment=$(pwd)/bin/EXP2/IteratorExperiment.txt
fi

echo $experiment

java -jar CMEF.jar -x $experiment $*
