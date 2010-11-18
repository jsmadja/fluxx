mvn clean package -Dglassfish-home="/Users/juliensmadja/Developpement/projets/fluxx/glassfish/glassfish" -Denvironment-name=development
rm -rf "/Users/juliensmadja/Developpement/projets/fluxx/glassfish/domains/domain1/autodeploy/fluxx.war"
cp target/fluxx.war "/Users/juliensmadja/Developpement/projets/fluxx/glassfish/domains/domain1/autodeploy"
