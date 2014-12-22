#!/usr/bin/env python3

# This script downloads and installs Eclipse and other development from the 
# internet, into the user's '~/workspaces/tools' folder.
#
# It will do the following:
# * Download and install various Eclipse plugins.
# * Create a launcher shortcut for Eclipse.
# * Download and install Apache Maven.
#
# Usage:
# This script is intended only for standalone usage, e.g.:
# $ ./devenv-install.py

from urllib.parse import urlsplit
import urllib.request
import shutil
import cgi
import os
import tarfile
import re
import collections
import subprocess
import tempfile
import stat

def main():
    """
    The main function for this script.
    """
    
    print('Development Environment Installer')
    print('=================================')
    
    # Install Eclipse.
    eclipse_archive = eclipse_download_from_internet()
    eclipse_install_dir = eclipse_install_archive(eclipse_archive)
    eclipse_create_shortcut(eclipse_install_dir)
    eclipse_install_plugins(eclipse_install_dir)

    # Install Maven.
    maven_archive = maven_download()
    maven_install_dir = maven_install(maven_archive)
    maven_config_env(maven_install_dir)

    # Install Tomcat.
    tomcat_archive = tomcat_download()
    tomcat_install(tomcat_archive)

def eclipse_download_from_internet():
    """
    Download the Eclipse archive/installer from eclipse.org.

    Returns:
        The path to the downloaded file, which will be saved into the
        `get_installers_dir()` directory.
    """
    
    # The URL to download from: Luna for 64bit Linux.
    eclipse_url = "http://mirrors.ibiblio.org/eclipse/technology/epp/downloads/release/luna/R/eclipse-jee-luna-R-linux-gtk-x86_64.tar.gz"
    
    # The path to save the installer to.
    file_name = urlsplit(eclipse_url).path.split('/')[-1]
    file_path_local = os.path.join(get_installers_dir(), file_name)
    
    print('1) Install Eclipse')
    
    if not os.path.exists(file_path_local):
        # Download the installer.
        print('   - Downloading ' + file_name + '... ', end="", flush=True)
        with urllib.request.urlopen(eclipse_url) as response, open(file_path_local, 'wb') as eclipse_archive:
            shutil.copyfileobj(response, eclipse_archive)
        print('downloaded.')
    else:
        print('   - Installer ' + file_name + ' already downloaded.')
    
    return file_path_local

def eclipse_install_archive(eclipse_archive_path):
    """
    Extract the specified Eclipse archive/installer into the `get_tools_dir()`
    directory.
    
    Args:
        eclipse_archive_path (str): The local path to the archive/installer to
            extract Eclipse from.
    
    Returns:
        The path to the downloaded file, which will be saved into the
        `get_installers_dir()` directory.
    """
    
    # The path to install to.
    _, eclipse_name = os.path.split(eclipse_archive_path)
    eclipse_name = re.sub('\.tar\.gz$', '', eclipse_name)
    eclipse_install_path = os.path.join(get_tools_dir(), eclipse_name)
    eclipse_install_path_tmp = os.path.join(get_tools_dir(), eclipse_name + "-tmp")

    if not os.path.exists(eclipse_install_path):
        # Extract the Eclipse install.
        print('   - Extracting ' + eclipse_name + '... ', end="", flush=True)
        with tarfile.open(eclipse_archive_path) as eclipse_archive:
            eclipse_archive.extractall(eclipse_install_path_tmp)

        # Make the extracted 'eclipse...-tmp/eclipse' directory the actual 
        # install.
        shutil.move(os.path.join(eclipse_install_path_tmp, 'eclipse'), 
                eclipse_install_path)
        os.rmdir(eclipse_install_path_tmp)
        print('extracted.')
    else:
        print('   - Archive ' + eclipse_name + ' already extracted.')
    
    return eclipse_install_path

def eclipse_create_shortcut(eclipse_install_dir):
    """
    Creates an OS launcher shortcut to the specified Eclipse installation. 
    
    Will replace any existing shortcut for that installation.
    
    Args:
        eclipse_install_dir (str): The local path for the Eclipse install.
    
    Returns:
        (nothing)
    """
    
    # The launcher/shortcut file path.
    _, eclipse_name = os.path.split(eclipse_install_dir)
    launcher_path = os.path.join(os.path.expanduser('~'), '.local', 'share', 
            'applications', eclipse_name + '.desktop')
    launcher_exists = os.path.exists(launcher_path)
    
    # Write the launcher file.
    with open(launcher_path, 'w') as launcher_file:
        launcher_file.write('[Desktop Entry]\n')
        launcher_file.write('Version=1.0\n')
        launcher_file.write('Name=Eclipse Luna\n')
        launcher_file.write('\n')
        launcher_file.write('Exec={}\n'.format(os.path.join(eclipse_install_dir, 'eclipse')))
        launcher_file.write('Terminal=false\n')
        launcher_file.write('Icon={}\n'.format(os.path.join(eclipse_install_dir, 'icon.xpm')))
        launcher_file.write('Type=Application\n')
        launcher_file.write('Categories=IDE;Development\n')
        launcher_file.write('X-Ayatana-Desktop-Shortcuts=NewWindow\n')
        launcher_file.write('\n')
        launcher_file.write('[NewWindow Shortcut Group]\n')
        launcher_file.write('Name=New Window\n')
        launcher_file.write('Exec={}\n'.format(os.path.join(eclipse_install_dir, 'eclipse')))
        launcher_file.write('TargetEnvironment=Unity\n')
    if launcher_exists:
        print('   - Launcher updated.')
    else:
        print('   - Launcher created.')

def eclipse_install_plugins(eclipse_install_dir):
    """
    Installs/updates the plugins in the specified Eclipse installation.
    
    Args:
        eclipse_install_dir (str): The local path for the Eclipse install.
    
    Returns:
        (nothing)
    """
    
    # The plugins to install. Each PluginGroup is a collection of IUs to 
    # install together. Each Plugin is an IU with a specific version. Locking 
    # the versions is important, as otherwise there's no guarantee that this
    # script's results will be stable in the future.
    Plugin = collections.namedtuple('Plugin', ['iu', 'version'])
    PluginGroup = collections.namedtuple('PluginGroup', ['name', 'plugins', 'repos'])
    aptPlugins = PluginGroup('Maven Integration for Eclipse JDT APT', 
            [Plugin('org.jboss.tools.maven.apt.feature.feature.group', '1.1.0.201405210909')], 
            ['http://download.jboss.org/jbosstools/updates/m2e-extensions/m2e-apt'])
    markdownPlugins = PluginGroup('Markdown Editor', 
            [Plugin('markdown.editor.feature.feature.group', '0.2.3')], 
            ['http://www.winterwell.com/software/updatesite/'])

    # The Eclipse executable.
    eclipse_exe = os.path.join(eclipse_install_dir, 'eclipse')
    
    # Install the plugins, one at a time. This is slower, but makes debugging 
    # problems a lot simpler.
    print('2) Install Eclipse Plugins')
    for plugin_group in [aptPlugins, markdownPlugins]:
        print('   - Installing ' + plugin_group.name + '... ', end="", flush=True)
        
        # Build the comma-separated list of repos for this install.
        repos = ','.join(plugin_group.repos)
        
        # Build the list of '-installIU' args for this install.
        ius_install_args = []
        for plugin in plugin_group.plugins:
            ius_install_args.extend(['-installIU', '{}/{}'.format(plugin.iu, plugin.version)])
        
        # Build the full list of args for Eclipse.
        eclipse_args = [eclipse_exe, 
                '-nosplash', '-application', 'org.eclipse.equinox.p2.director',
                '-destination', eclipse_install_dir,
                '-repository', repos]
        eclipse_args.extend(ius_install_args)
        
        # Run Eclipse (headless) to install the plugins.
        subprocess.check_call(eclipse_args, 
                stdout=subprocess.DEVNULL, stderr=subprocess.STDOUT)
        
        print('done.')

def maven_download():
    """
    Download the Maven archive/installer from maven.apache.org.

    Returns:
        The path to the downloaded file, which will be saved into the
        `get_installers_dir()` directory.
    """
    
    # The URL to download from: Maven 3.2.3.
    maven_url = "http://www.us.apache.org/dist/maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz"
    
    # The path to save the installer to.
    file_name = urlsplit(maven_url).path.split('/')[-1]
    file_path_local = os.path.join(get_installers_dir(), file_name)
    
    print('3) Install Maven')
    
    if not os.path.exists(file_path_local):
        # Download the installer.
        print('   - Downloading ' + file_name + '... ', end="", flush=True)
        with urllib.request.urlopen(maven_url) as response, open(file_path_local, 'wb') as maven_archive:
            shutil.copyfileobj(response, maven_archive)
        print('downloaded.')
    else:
        print('   - Installer ' + file_name + ' already downloaded.')
    
    return file_path_local

def maven_install(maven_archive_path):
    """
    Extract the specified Maven archive/installer into the `get_tools_dir()`
    directory.
    
    Args:
        maven_archive_path (str): The local path to the archive/installer to
            extract Maven from.
    
    Returns:
        The path to the downloaded file, which will be saved into the
        `get_installers_dir()` directory.
    """
    
    # The path to install to.
    _, maven_name = os.path.split(maven_archive_path)
    maven_name = re.sub('-bin\.tar\.gz$', '', maven_name)
    maven_install_path = os.path.join(get_tools_dir(), maven_name)
    maven_install_path_tmp = os.path.join(get_tools_dir(), maven_name + "-tmp")

    if not os.path.exists(maven_install_path):
        # Extract the Maven install.
        print('   - Extracting ' + maven_name + '... ', end="", flush=True)

        # Python's tarfile module can't process recent Maven releases, due to:
        # https://jira.codehaus.org/browse/PLXCOMP-233
        # with tarfile.open(maven_archive_path) as maven_archive:
        #     maven_archive.extractall(maven_install_path_tmp)
        
        # Extract the TGZ with tar, instead.
        os.mkdir(maven_install_path_tmp)
        subprocess.check_call(['tar', '--extract', '--ungzip', '--directory', 
                maven_install_path_tmp, '--file', maven_archive_path], 
                stdout=subprocess.DEVNULL, stderr=subprocess.STDOUT)

        # Make the extracted 'apache-maven...-tmp/apache-maven-3.2.3' directory the actual 
        # install.
        shutil.move(os.path.join(maven_install_path_tmp, maven_name), 
                maven_install_path)
        os.rmdir(maven_install_path_tmp)
        print('extracted.')
    else:
        print('   - Archive ' + maven_name + ' already extracted.')
    
    return maven_install_path

def maven_config_env(maven_install_dir):
    """
    Configures the environment variables, etc. for the specified Maven installation.
    
    Args:
        maven_install_dir (str): The local path for the Maven install.
    
    Returns:
        (nothing)
    """
    
    # Set MAVEN_HOME and add Maven to the path.
    print('   - Updating .bashrc... ', end="", flush=True)
    bash_var_export('MAVEN_HOME', maven_install_dir)
    bash_path_include(os.path.join(maven_install_dir, 'bin'))
    print('done.')

def tomcat_download():
    """
    Download the Tomcat archive/installer from tomcat.apache.org.

    Returns:
        The path to the downloaded file, which will be saved into the
        `get_installers_dir()` directory.
    """
    
    # The URL to download from: Tomcat 7.0.57.
    tomcat_url = "http://www.us.apache.org/dist/tomcat/tomcat-7/v7.0.57/bin/apache-tomcat-7.0.57.tar.gz"
    
    # The path to save the installer to.
    file_name = urlsplit(tomcat_url).path.split('/')[-1]
    file_path_local = os.path.join(get_installers_dir(), file_name)
    
    print('4) Install Tomcat')
    
    if not os.path.exists(file_path_local):
        # Download the installer.
        print('   - Downloading ' + file_name + '... ', end="", flush=True)
        with urllib.request.urlopen(tomcat_url) as response, open(file_path_local, 'wb') as tomcat_archive:
            shutil.copyfileobj(response, tomcat_archive)
        print('downloaded.')
    else:
        print('   - Installer ' + file_name + ' already downloaded.')
    
    return file_path_local

def tomcat_install(tomcat_archive_path):
    """
    Extract the specified Tomcat archive/installer into the `get_tools_dir()`
    directory.
    
    Args:
        tomcat_archive_path (str): The local path to the archive/installer to
            extract Tomcat from.
    
    Returns:
        The path to the downloaded file, which will be saved into the
        `get_installers_dir()` directory.
    """
    
    # The path to install to.
    _, tomcat_name = os.path.split(tomcat_archive_path)
    tomcat_name = re.sub('\.tar\.gz$', '', tomcat_name)
    tomcat_install_path = os.path.join(get_tools_dir(), tomcat_name)
    tomcat_install_path_tmp = os.path.join(get_tools_dir(), tomcat_name + "-tmp")

    if not os.path.exists(tomcat_install_path):
        # Extract the Tomcat install.
        print('   - Extracting ' + tomcat_name + '... ', end="", flush=True)

        with tarfile.open(tomcat_archive_path) as tomcat_archive:
            tomcat_archive.extractall(tomcat_install_path_tmp)

        # Make the extracted 'apache-tomcat...-tmp/apache-tomcat-7.0.57' directory the 
        # actual install.
        shutil.move(os.path.join(tomcat_install_path_tmp, tomcat_name), 
                tomcat_install_path)
        os.rmdir(tomcat_install_path_tmp)
        print('extracted.')
    else:
        print('   - Archive ' + tomcat_name + ' already extracted.')
    
    return tomcat_install_path

def get_installers_dir():
    """
    Return the path to the directory to save installers to.
    
    Create the directory if it does not already exist.
    
    Returns:
        The path to the directory to save installers to.
    """
    
    installers_dir = os.path.join(get_tools_dir(), 'installers')
    os.makedirs(installers_dir, exist_ok=True)
    return installers_dir

def get_tools_dir():
    """
    Return the path to the directory to install development tools to.
    
    Create the directory if it does not already exist.
    
    Returns:
        The path to the directory to install development tools to.
    """
    
    tools_dir = os.path.join(os.path.expanduser('~'), 'workspaces', 'tools')
    os.makedirs(tools_dir, exist_ok=True)
    return tools_dir

def bash_var_export(name, value):
    """
    Sets the specified environment variable in the user's `.bashrc` file.
    
    This isn't the world's most intelligent function: it searches `.bashrc`
    for a line that begins with "`export varname=`". If such a line is found,
    it's replaced. Otherwise, such a line is added to the end of the file.
    
    Args:
        name (str): The name of the environment variable to set.
        value (str): The value to set the environment variable to.
    
    Returns:
        (nothing)
    """
    
    # The .bashrc path to read from (and eventually replace).
    bashrc_path = os.path.join(os.path.expanduser('~'), '.bashrc')
    
    # The .bashrctmp path to write to.
    bashrctmp_path = ''
    with tempfile.NamedTemporaryFile(delete=False) as bashrc_tmp:
        bashrctmp_path = bashrc_tmp.name
    
    # Open two files:
    #  1. .bashrc, for reading
    #  2. A temp file to write the modified contents of .bashrc out to.
    var_found = False
    with open(bashrc_path, 'r') as bashrc, \
         open(bashrctmp_path, 'w') as bashrc_tmp:
        for line in bashrc:
            if line.startswith('export {}='.format(name)):
                var_found = True
                bashrc_tmp.write('export {}={}\n'.format(name, value))
            else:
                bashrc_tmp.write(line)
        if not var_found:
            bashrc_tmp.write('export {}={}\n'.format(name, value))
    
    # Replace the original .bashrc with the modified one.
    bashrc_stat = os.stat(bashrc_path)
    os.chmod(bashrctmp_path, stat.S_IMODE(bashrc_stat.st_mode))
    os.chown(bashrctmp_path, bashrc_stat.st_uid, bashrc_stat.st_gid)
    os.replace(bashrctmp_path, bashrc_path)

def bash_path_include(directory):
    """
    Adds the specified directory to the path in the user's `.bashrc` file.
    
    This isn't the world's most intelligent function: it searches `.bashrc`
    for an `export PATH=...` line matching the one being added. If such a line
    is found, it's replaced. Otherwise, such a line is added to the end of the 
    file.
    
    Args:
        directory (str): The directory to add to the path.
    
    Returns:
        (nothing)
    """
    
    # The .bashrc path to read from (and eventually replace).
    bashrc_path = os.path.join(os.path.expanduser('~'), '.bashrc')
    
    # The .bashrctmp path to write to.
    bashrctmp_path = ''
    with tempfile.NamedTemporaryFile(delete=False) as bashrc_tmp:
        bashrctmp_path = bashrc_tmp.name
    
    # The line being added to the file.
    path_entry = 'export PATH=${PATH}:' + directory + '\n'
    
    
    # Open two files:
    #  1. .bashrc, for reading
    #  2. A temp file to write the modified contents of .bashrc out to.
    path_entry_found = False
    with open(bashrc_path, 'r') as bashrc, \
            open(bashrctmp_path, 'w') as bashrc_tmp:
        for line in bashrc:
            if line == path_entry:
                path_entry_found = True
            bashrc_tmp.write(line)
        if not path_entry_found:
            bashrc_tmp.write(path_entry)
    
    # Replace the original .bashrc with the modified one.
    bashrc_stat = os.stat(bashrc_path)
    os.chmod(bashrctmp_path, stat.S_IMODE(bashrc_stat.st_mode))
    os.chown(bashrctmp_path, bashrc_stat.st_uid, bashrc_stat.st_gid)
    os.replace(bashrctmp_path, bashrc_path)

# If this file is being run as a standalone script, call the main() function.
# (Otherwise, do nothing.)
if __name__ == "__main__":
    main()

