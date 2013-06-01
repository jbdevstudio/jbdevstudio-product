# Red Hat JBoss Developer Studio - Product Build


## Summary

_Red Hat JBoss Developer Studio_ is the productization project for JBoss Tools, which includes an update site, izPack installers, sources, and some generated HTML pages.


## Upstream projects

_Red Hat JBoss Developer Studio_ uses a parent pom, target platform, and Central Discovery plugin which are not part of this project's sources. Also upstream are multiple JBoss Tools projects. Sources can be found here:

* Parent: https://github.com/jbosstools/jbosstools-build/
* Target Platform: https://github.com/jbosstools/jbosstools-target-platforms/
* Central Discovery: https://github.com/jbosstools/jbosstools-discovery/
* Other JBoss Tools projects: https://github.com/jbosstools/


## Download an installer jar or update site zip

_Red Hat JBoss Developer Studio_ is the productization project for [JBoss Tools](http://jboss.org/tools). It can be downloaded in binary form from here: 

* https://devstudio.jboss.com/download/ (current releases)
* https://devstudio.jboss.com/earlyaccess/ (future releases)
* https://devstudio.jboss.com/updates/ (update sites)


## Build Red Hat JBoss Developer Studio 

Building _Red Hat JBoss Developer Studio_ requires Java 6 and Maven 3. See [How to Build JBoss Tools with Maven 3](https://community.jboss.org/wiki/HowToBuildJBossTools41FAQ) for Maven configuration and other tips.

Build product features, site, and installers like this:

    $ cd devstudio/product
    $ mvn clean install


## Install Red Hat JBoss Developer Studio

After a successful build, three artifacts are produced:

* an installer jar
* a sources zip
* an update site (also known as a p2 repository)


Installer jar(s) will be produced in installer/target/ and can be installed using 'java -jar jbdevstudio-*.jar'

Sources zip can be found in results/target/; you can build from the source zip in there by unpacking it, then using the same instructions above.

An unpacked update site, which can be used to perform an initial install or to install updates to a previous installation, can be found in site/target/repository/.

The installer jar can be used as an archived update site. Launch Eclipse or JBoss Developer Studio, then select:

  Help > Install New Software... > Add... > Archive... > Browse for the JBoss Developer Studio installer jar > OK > 
    Work with: jar:file:/path/to/jbdevstudio-product-*.jar!/ > Install the JBoss Developer Studio feature(s) > 
      Restart when prompted. 


Or, to install the "Bring Your Own Eclipse" category or "Core Features" feature into Eclipse (not as an update to an existing JBoss Developer Studio install):

  * Help > Install from the generated site in site/target/repository/ (or the installer jar, as noted above
  * Select the "JBoss Developer Studio (Core Features)" feature


## Update Red Hat JBoss Developer Studio

To update from one version of JBoss Developer Studio on the same stream to another (eg., from JBoss Developer Studio 6.0.0 to 6.0.1 or from 7.0.0 to 7.0.1):

  * Help > Install from the generated site in site/target/repository/ (or the installer jar, as noted above)
  * Uncheck the box for 'Group items by category'
  * Select the "JBoss Developer Studio (Branded Product)" feature


## Contribute fixes and features

_Red Hat JBoss Developer Studio_ is open source, and we welcome anybody that wants to participate and contribute!

If you want to fix a bug or make any changes, please log an issue in the [JBoss Developer Studio JIRA](https://issues.jboss.org/browse/JBoss Developer Studio) describing the bug or new feature.

After you are happy with your changes and a full build runs successfully, attach a patch to the JIRA. 
