@call:config ALF_USERNAME ALF_USERNAME
@call:config ALF_PASSWORD ALF_PASSWORD
@call:config ALF_MACHINE_ADDRESS ALF_MACHINE_ADDRESS
@call:config ALF_HOME ALF_HOME
@call:config CLASSPATH CLASSPATH
@SET XML_FILE=publications.xml
curl -k -X POST -u%ALF_USERNAME%:%ALF_PASSWORD% -o %XML_FILE% "%ALF_HOME%/cmis/queries" -H "Content-Type:application/cmisquery+xml" -d @cmis-list-publications.xml
SET CLASSPATH=%CLASSPATH%;jars\serializer.jar;jars\xalan.jar;jars\xercesImpl.jar;jars\xml-apis.jar
java -classpath %CLASSPATH% org.apache.xalan.xslt.Process -IN %XML_FILE% -XSL convert_publications.xsl 
@goto:eof
:config
@for /f "tokens=2 delims==" %%a in ('find "%~1=" ../config_params.cfg') do @set %~2=%%a
@goto:eof

:eof
