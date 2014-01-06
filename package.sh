#!/bin/bash

tmpdir=$(mktemp -d tmp.XXXXXX)

function die() {
	echo $*
	exit 1
}

javac src/pkgCMEF/*.java -Xlint:unchecked || die "Failed to compile CMEF."

rm -rf bin/pkgCMEF/*.class || die "Failed to delete class files."

mv src/pkgCMEF/*.class bin/pkgCMEF/ || die "Failed to move class files into place."

cp -r bin/pkgCMEF $tmpdir || die "Failed to created pkgCMEF dir."

cd $tmpdir || die "Failed to chdir into temporary dir." 

echo 'Main-Class: pkgCMEF.CmeApp' > Manifest.txt

jar cmf Manifest.txt CMEF.jar pkgCMEF/*.class || die "Failed to zip Jar contents."

cd -

mv $tmpdir/CMEF.jar ./

rm -rf $tmpdir || die "Failed to remove temp file."
