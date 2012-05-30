#!/bin/bash

tmpdir=$(mktemp -d tmp.XXXXXX)

function die() {
	echo $*
	exit 1
}


cp -r bin/pkgCMEF $tmpdir || die "Failed to created pkgCMEF dir."

cd $tmpdir

zip CMEF.jar ./pkgCMEF/* || die "Failed to zip Jar contents."

cd -

mv $tmpdir/CMEF.jar ./

rm -rf $tmpdir || die "Failed to remove temp file."
