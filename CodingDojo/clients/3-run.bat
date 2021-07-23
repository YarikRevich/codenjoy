call 0-settings.bat

echo off
echo [44;93m
echo        +-------------------------------------------------------------------------+
echo        !                   Now we are running clients...                        !
echo        +-------------------------------------------------------------------------+
echo [0m
echo on

IF "%LANGUAGE%"=="php" (
    call :php
)

IF "%LANGUAGE%"=="python" (
    call :python
)

cd %LANGUAGE%
call 2-run.bat
cd %ROOT%

call :ask

:php
    SET PATH=%PHP_HOME%;%PATH%
goto :eof

:python
    SET PATH=%PYTHON_HOME%;%PATH%
goto :eof


:ask
    echo Press any key to continue
    pause >nul
goto :eof