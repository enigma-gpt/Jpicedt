rem This is a DOS batch script for the MikTeX distribution
rem (http://www.miktex.org)
rem Put this script in jPicEdt add-ons directory.
rem Then configure jPicEdt by opening Edit->Preferences->Command and loading configMikTeX.properties

@echo off
verify OTHER 2>nul
setlocal ENABLEEXTENSIONS
if errorlevel 1 (echo ERROR: jPicEdt MSDOS Unable to enable extensions)
set _done=no

@echo on
cd /D %1
@echo off

Rem try and locate where is yap among a few likely locations if yap is not
Rem in your path, and in neither of these locations you should modify the list
Rem between round brackets below to add your yap in it.
set _dvi=
if defined JPICEDT_DVI (set _dvi="%JPICEDT_DVI%")
for %%i in (
%_dvi%
yap
) do (
set _ltx_utility=%%i
call :trial %2
)

if %_done%==yes (goto end)
@echo ERROR: jPicEdt Can't find yap.
goto end

:trial
IF %_done%==yes (goto end)

@echo on
%_ltx_utility% %1.dvi
@echo off
set _el=%ERRORLEVEL%
if %_el% == 9009 set _done=not_found
if %_el% == 3    set _done=not_found
if not %_done% == not_found set _done=yes
if %_el% == 0    set _done=yes
goto :EOF

:end
GOTO :EOF
Rem =========================================================================
