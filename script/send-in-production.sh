mvn clean install -Dglassfish-home="/Users/juliensmadja/Developpement/projets/fluxx/glassfish/glassfish" -Denvironment-name=production -DskipTests=true
scp fluxx-web/target/fluxx.war fluxx@fluxx.fr.cr:/opt/glassfishv3/glassfish/domains/domain1/autodeploy
scp fluxx-admin/target/fluxx-admin.war fluxx@fluxx.fr.cr:/opt/glassfishv3/glassfish/domains/domain1/autodeploy
