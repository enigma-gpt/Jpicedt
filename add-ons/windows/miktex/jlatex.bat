rem This is a DOS batch script for the MikTeX distribution (http://www.miktex.org). Put this script in jPicEdt
rem add-ons directory.  Then configure jPicEdt by opening Edit/Preferences/Command, and then loading
rem configMikTeX.properties
@echo off
verify OTHER 2>nul
setlocal ENABLEEXTENSIONS
if errorlevel 1 (echo ERROR: jPicEdt MSDOS Unable to enable extensions)
set _done=no

rem To be robust to the case when the prolog contain some \usepackage{mypackage} with mypackage in same
rem directory as drawing, or the case when drawing text element make some \input or \includegraphics, we add 
rem a include-directory directive pointing at the location where the drawing is found.						  
set _x=%3
if defined _x (
   set _jpe_include_directive=
)
else (
   set _jpe_include_directive=-include-directory=%3
)

cd /D "%1"
set _done=no

rem Try and locate where is latex among a few likely locations if latex is not in your path, and in neither
rem of these locations you should modify the list between round brackets below to add your latex in it.
set _latex=
if defined JPICEDT_LATEX (set _latex="%JPICEDT_LATEX%")
for %%i in (
%_latex%
latex
) do (
 set _ltx_utility=%%i
 call :latex %2
)
if %_done%==yes (goto end)

@echo ERROR: jPicEdt can't find latex, please set environnement variables JPICEDT_LATEX or PATH
goto end

:latex
if %_done%==yes (goto :EOF)

@echo on
%_ltx_utility% -interaction=nonstopmode %_jpe_include_directive% %1.tex
@echo off

set _el=%ERRORLEVEL%
if %_el% == 9009 set _done=not_found
if %_el% == 3    set _done=not_found
if not "%_done%" == "not_found" set _done=yes
if %_el% == 0    set _done=yes
goto :EOF

:end
endlocal
goto :EOF
