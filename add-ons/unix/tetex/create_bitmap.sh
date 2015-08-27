#!/bin/sh
# This is a shell for the Unix/TeTeX distribution
# Put this script in jPicEdt add-ons directory.
# Then configure jPicEdt by opening Edit->Preferences->Command and loading configTeTeX.properties
# $1=/tmp $2=TeX_file_prefix $3=bitmap_file_extension $4=dpi
cd $1
latex $2
dvips -E $2
#pstoimg -depth 8 -density 150 -antialias -aaliastext -crop a -out $2.$3 $2.ps
echo $1 $2 $3 $4
echo pstoimg -depth 8 -density $4 -transparent -antialias -aaliastext -crop a -out $2.$3 $2.ps
pstoimg -depth 8 -density $4 -transparent -antialias -aaliastext -crop a -out $2.$3 $2.ps

