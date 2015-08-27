#!/bin/sh
# This is a shell for the Unix/TeTeX distribution
# Put this script in jPicEdt add-ons directory.
# Then configure jPicEdt by opening Edit->Preferences->Command and loading configTeTeX.properties
echo "Command line : cd $1 ; open $2.pdf"
cd $1
open $2.pdf

