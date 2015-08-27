rem This is a DOS batch script for the WinGUT distribution
rem (http://www.gutenberg.eu.org)
rem Put this script in jPicEdt add-ons directory.
rem Then configure jPicEdt by opening Edit->Preferences->Command and loading configWinGUT.properties
cd "%1"
cd /D "%1"
rem babel format :
wg_exec bt tex &latex %2
rem french format :
rem wg_exec ft tex &latex %2

