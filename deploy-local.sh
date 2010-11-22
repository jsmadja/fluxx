mvn clean package -Dglassfish-home="/Users/juliensmadja/Developpement/projets/fluxx/glassfish/glassfish" -Denvironment-name=local -DskipTests=true
rm -rf "/Users/juliensmadja/Developpement/projets/fluxx/glassfish/glassfish/domains/domain1/autodeploy/fluxx.war"
cp fluxx-web/target/fluxx.war "/Users/juliensmadja/Developpement/projets/fluxx/glassfish/glassfish/domains/domain1/autodeploy"
