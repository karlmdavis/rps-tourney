#!/bin/bash

# Define the Eclipse directory.
installationName=eclipse-java-kepler-SR1-linux-gtk-x86_64
installationDirectoryRoot=/usr/local/eclipse
installationDirectory=$installationDirectoryRoot/$installationName

# Define the Eclipse repositories to install plugins from.
p2Repos_base=http://download.eclipse.org/releases/kepler/,http://download.eclipse.org/eclipse/updates/4.3
p2Repos_markdown=http://www.winterwell.com/software/updatesite/
p2Repos=$p2Repos_base,$p2Repos_m2e_wtp,$p2Repos_markdown

# Install all the plugins in one go.
# References:
# * [Stack Overflow: Bash: How to Put Line Comment for a Multi-line Command](http://stackoverflow.com/a/12797512)
# * [Eclipse Wiki: WTP FAQ: How do I install WTP?](http://wiki.eclipse.org/WTP_FAQ#How_do_I_install_WTP.3F)
$installationDirectory/eclipse -nosplash \
  -application org.eclipse.equinox.p2.director \
  -repository $p2Repos \
  -destination $installationDirectory \
  -installIU org.eclipse.jst.enterprise_ui.feature.feature.group       `# Eclipse Java EE Developer Tools (and dependencies).` \
  -installIU org.eclipse.wst.web_ui.feature.feature.group              `# Eclipse Web Developer Tools (and dependencies).` \
  -installIU org.eclipse.jst.ws.jaxws.feature.feature.group            `# JAX-WS Tools.` \
  -installIU org.eclipse.jst.ws.jaxws.dom.feature.feature.group        `# JAX-WS DOM Tools.` \
  -installIU org.eclipse.jst.server_adapters.ext.feature.feature.group `# JST Server Adapters Extension.` \
  -installIU org.eclipse.wst.server_adapters.feature.feature.group     `# WST Server Adapters.` \
  -installIU org.eclipse.jst.ws.cxf.feature.feature.group              `# CXF Web Services (and dependencies).` \
  -installIU org.eclipse.m2e.wtp.feature.feature.group                 `# m2e-wtp - Maven Integration for WTP (and dependencies).` \
  -installIU org.eclipse.m2e.wtp.jaxrs.feature.feature.group           `# m2e-wtp - JAX-RS configurator for WTP (Optional).` \
  -installIU markdown.editor.feature.feature.group                     `# Markdown Editor and preview.`

# Is this needed?
#org.eclipse.jst.server_adapters.feature

