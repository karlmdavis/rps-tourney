#!/bin/sh

# Define the URL to download the "installation" package from.
installationUrl=http://download.eclipse.org/technology/epp/downloads/release/kepler/SR1/eclipse-java-kepler-SR1-linux-gtk-x86_64.tar.gz

# Pull apart the name of the file that will be downloaded.
installationName=eclipse-java-kepler-SR1-linux-gtk-x86_64
installationFile=$installationName.tar.gz

# Define the directory to save the install to.
installationDirectoryRoot=/usr/local/eclipse
installationDirectory=$installationDirectoryRoot/$installationName

# Create the installation directory root.
mkdir -p $installationDirectoryRoot/

# Download, extract, and relocate the installation bundle.
wget $installationUrl
tar -xzf $installationFile
rm $installationFile
mv eclipse/ $installationDirectory/

# Create the application launcher.
cat <<EOF > /usr/share/applications/$installationName.desktop
[Desktop Entry]
Version=1.0
Name=Eclipse Kepler
  
Exec=$installationDirectory/eclipse
Terminal=false
Icon=$installationDirectory/icon.xpm
Type=Application
Categories=IDE;Development
X-Ayatana-Desktop-Shortcuts=NewWindow

[NewWindow Shortcut Group]
Name=New Window
Exec=$installationDirectory/eclipse
TargetEnvironment=Unity
EOF

