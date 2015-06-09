#!/bin/sh

#
# Builds the JBoss EAP system for the HA CEP BRMS demo. Does the following things:
# * Install JBoss EAP 6.3.0.
# * Patch JBoss EAP 6.3.0 to JBoss EAP 6.3.2.
# * Create and configure hacepbrms-standalone-full.xml profile and configures it.
#
# author: ddoyle@redhat.com
#
INSTALLATION_ZIPS_DIR=installation_zips
SETUP_SCRIPTS_DIR=setup-scripts
TARGET_DIR=target
JBOSS_EAP_ZIP=jboss-eap-6.3.0.zip
JBOSS_EAP_PATCH_ZIP=jboss-eap-6.3.3-patch.zip

# Check if the required files are present.
if [ ! -f ./$INSTALLATION_ZIPS_DIR/$JBOSS_EAP_ZIP ]; then
    echo "Setup requires the JBoss EAP 6.3 ZIP '$JBOSS_EAP_ZIP' to be present in the '$INSTALLATION_ZIPS_DIR' directory."
    exit 1
fi

if [ ! -f ./$INSTALLATION_ZIPS_DIR/$JBOSS_EAP_PATCH_ZIP ]; then
    echo "Setup requires the JBoss EAP 6.3.3 Patch ZIP '$JBOSS_EAP_PATCH_ZIP' to be present in the '$INSTALLATION_ZIPS_DIR' directory."
    exit 1
fi

echo "Deleting target directory."
rm -rf ./$TARGET_DIR

# Setup JBoss EAP 6.3
echo "Installing JBoss EAP 6.3."
unzip ./$INSTALLATION_ZIPS_DIR/$JBOSS_EAP_ZIP -d ./$TARGET_DIR

# Patch
echo "Patching JBoss EAP 6.3 installation to JBoss EAP 6.3.3."
./$SETUP_SCRIPTS_DIR/patch-jboss-eap.sh -j ./$TARGET_DIR/jboss-eap-6.3 -p ./$INSTALLATION_ZIPS_DIR/jboss-eap-6.3.3-patch.zip

# Create profile
echo "Creating the JBoss EAP profile for HA CEP BRMS demo."
./$SETUP_SCRIPTS_DIR/setup-jboss-eap-profile.sh -j ./$TARGET_DIR/jboss-eap-6.3 -s standalone-full.xml -t hacepbrms-standalone-full.xml -c ./$SETUP_SCRIPTS_DIR/cli-scripts 

# Add admin user.
echo "Adding admin user."
./$TARGET_DIR/jboss-eap-6.3/bin/add-user.sh admin jboss@01 --silent
echo "Addind guest application user."
./$TARGET_DIR/jboss-eap-6.3/bin/add-user.sh -a -r ApplicationRealm -u guest -p guest@01 -ro guest --silent
