rem This is a DOS batch script for the MikTeX distribution (http://www.miktex.org). Put this script in jPicEdt
rem add-ons directory.  Then configure jPicEdt by opening Edit->Preferences->Command and loading
rem configMikTeX.properties

@echo off
verify OTHER 2>nul
setlocal ENABLEEXTENSIONS
if errorlevel 1 (echo ERROR: jPicEdt MSDOS Unable to enable extensions)
set _done=no

@echo on
cd /D %1
@echo off

rem try and locate where is dvips among a few likely locations if dvips is not in your path, and in neither of
rem these locations you should modify the list between round brackets below to add your dvips in it.
set _dvips=
if defined JPICEDT_DVIPS (set _dvips="%JPICEDT_DVIPS%")
for %%i in (
%_dvips%
dvips
""
) do (
set _ltx_utility=%%i
call :trial %2
)

if %_done%==yes (goto end)
@echo ERROR: jPicEdt Can't find dvips.
goto :end

rem =========================================================================================================
:trial
if %_done%==yes (goto :EOF)
@echo on
start /wait %_ltx_utility% %1.dvi
@echo off
set _el=%ERRORLEVEL%
if %_el% == 9009 set _done=not_found
if %_el% == 3    set _done=not_found
if not "%_done%" == "not_found" set _done=yes
if %_el% == 0    set _done=yes
goto :EOF

:end
goto :EOF
rem =========================================================================================================
