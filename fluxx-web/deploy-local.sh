mvn clean package -Dglassfish-home="C:\Program Files\glassfish-3.0.1\glassfish" -Denvironment-name=development
rm -rf "C:\Program Files\glassfish-3.0.1\glassfish\domains\domain1\autodeploy\fluxx.war"
cp target/fluxx.war "C:\Program Files\glassfish-3.0.1\glassfish\domains\domain1\autodeploy"
