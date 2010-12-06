mvn clean install -Dglassfish-home="/Users/juliensmadja/Developpement/projets/fluxx/glassfish/glassfish" -Denvironment-name=preproduction -DskipTests=true
export DEPLOY_DIR="/Users/juliensmadja/glassfishv3/glassfish/domains/domain1/autodeploy/"
rm -rf "$DEPLOY_DIR/fluxx*"
cp fluxx-web/target/fluxx.war "$DEPLOY_DIR"
cp fluxx-admin/target/fluxx-admin.war "$DEPLOY_DIR"
