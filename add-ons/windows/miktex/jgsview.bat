rem This is a DOS batch script for the MikTeX distribution
rem (http://www.miktex.org)
rem Put this script in jPicEdt add-ons directory.
rem Then configure jPicEdt by opening Edit->Preferences->Command and loading configMikTeX.properties
@echo off
verify OTHER 2>nul
setlocal ENABLEEXTENSIONS
if errorlevel 1 (echo ERROR: jPicEdt MSDOS Unable to enable extensions)

cd /D %1

set _done=no


Rem try and locate where is gsview32.exe among a few likely locations if
Rem gsview32.exe is not in your path, and in neither of these locations you
Rem should modify the list between round brackets below to add your gvview in it.
set _psview=
if defined JPICEDT_GSVIEW (set _psview="%JPICEDT_GSVIEW%")
for %%i in (
%_psview%
gsview32.exe
"C:\Program Files\Ghostgum\gsview\gsview32.exe"
"C:\Programme\Ghostgum\gsview\gsview32.exe"
"D:\Program Files\Ghostgum\gsview\gsview32.exe"
"D:\Programme\Ghostgum\gsview\gsview32.exe"
""
) do (
set _ltx_utility=%%i
call :trial %2
)
if %_done% == yes (goto end)
@echo ERROR: jPicEdt Can't find gsview.
goto end

:trial 
if %_done% == yes (goto :EOF)
@echo on
%_ltx_utility% %1.ps
@echo off

set _el=%ERRORLEVEL%
if %_el% == 9009 (SET _done=not_found)
if %_el% == 3    (set _done=not_found)
if not %_done% == not_found (set _done=yes)
if %_el% == 0    (set _done=yes)
goto :EOF


:end
endlocal
GOTO :EOF
