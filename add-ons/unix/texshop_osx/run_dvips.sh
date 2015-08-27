#!/bin/sh
# This is a shell for the MacOSX-Tiger with the TeXShop distribution
# Put this script in jPicEdt add-ons directory.
# Then configure jPicEdt by opening Edit->Preferences->Command and loading configTeXShop.properties
echo "Command line : cd $1 ; dvips $2.dvi -o $2.ps"
cd $1
dvips $2.dvi -o $2.ps

