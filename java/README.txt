The easiest way to run this is to use the fully packaged jar

java -jar target/cmis-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar test.properties upload example.properties

java -jar target/cmis-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar test.properties list /Sites/ian-test-site/documentlibrary/study

java -jar target/cmis-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar test.properties set-property /Sites/ian-test-site/documentlibrary/study cggh:samplesProcessed 5