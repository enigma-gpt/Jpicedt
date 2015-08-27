#!/bin/sh
# This is a shell for the Unix/TeTeX distribution
# Put this script in jPicEdt add-ons directory.
# Then configure jPicEdt by opening Edit->Preferences->Command and loading configTeTeX.properties
echo "Command line : cd $1 ; latex $2 ; dvips $2"
cd $1
latex $2
dvips $2
