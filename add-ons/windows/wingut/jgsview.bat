rem This is a DOS batch script for the WinGUT distribution
rem (http://www.gutenberg.eu.org)
rem Put this script in jPicEdt add-ons directory.
rem Then configure jPicEdt by opening Edit->Preferences->Command and loading configWinGUT.properties
cd "%1"
cd /D "%1"
gsview32 %2.ps
