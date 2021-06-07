call 0-settings.bat

echo off
echo [44;93m
echo        +-------------------------------------------------------------------------+
echo        !                   Now we are downloading stuff...                       !
echo        +-------------------------------------------------------------------------+
echo [0m
echo on

IF "%LANGUAGE%"=="java" (
    call :jdk
)

IF "%LANGUAGE%"=="pseudo" (
    call :jdk
)

IF "%LANGUAGE%"=="java-script" (
    call :node
)

echo off
echo [44;93m
echo        +-------------------------------------+
echo        !     Now you can run 2-build.bat     !
echo        +-------------------------------------+
echo [0m
echo on

call :ask

goto :eof

:jdk
    echo off
    echo [44;93m
    echo        +-------------------------------------+
    echo        !           Installing JDK            !
    echo        +-------------------------------------+
    echo [0m
    echo on

	if "%SKIP_JDK_INSTALL%"=="true" ( goto :eof )
    cd %ROOT%
    rd /S /Q %JAVA_HOME%
    powershell -command "& { set-executionpolicy remotesigned -s currentuser; [System.Net.ServicePointManager]::SecurityProtocol = 3072 -bor 768 -bor 192 -bor 48; $client=New-Object System.Net.WebClient; $client.Headers.Add([System.Net.HttpRequestHeader]::Cookie, 'oraclelicense=accept-securebackup-cookie'); $client.DownloadFile('%ARCH_JDK%','%TOOLS%\jdk.zip') }"
    %TOOLS%\7z x -y -o%TOOLS%\.. %TOOLS%\jdk.zip
    rename %TOOLS%\..\%ARCH_JDK_FOLDER% .jdk
    cd %ROOT%
goto :eof

:node
    echo off
    echo [44;93m
    echo        +-------------------------------------+
    echo        !         Installing Node.js          !
    echo        +-------------------------------------+
    echo [0m
    echo on

    cd %ROOT%
    rd /S /Q %NODE_HOME%
    powershell -command "& { set-executionpolicy remotesigned -s currentuser; [System.Net.ServicePointManager]::SecurityProtocol = 3072 -bor 768 -bor 192 -bor 48; $client=New-Object System.Net.WebClient; $client.Headers.Add([System.Net.HttpRequestHeader]::Cookie, 'oraclelicense=accept-securebackup-cookie'); $client.DownloadFile('%ARCH_NODE%','%TOOLS%\node.zip') }"
    %TOOLS%\7z x -y -o%TOOLS%\.. %TOOLS%\node.zip
    rename %TOOLS%\..\%ARCH_NODE_FOLDER% .node
    cd %ROOT%
goto :eof

:ask
    echo Press any key to continue
    pause >nul
goto :eof