. ../config_params.cfg
. ./config.sh
XML_FILE=publications.xml
curl -k -X POST -u${ALF_USERNAME}:${ALF_PASSWORD} -o ${XML_FILE} "${ALF_HOME}/cmis/queries" -H "Content-Type:application/cmisquery+xml" -d @cmis-list-publications.xml
java -classpath ${CLASSPATH} org.apache.xalan.xslt.Process -IN ${XML_FILE} -XSL convert_publications.xsl 
