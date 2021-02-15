# openSpeedyFX

## Requirements
- At least JDK-14, preinstalled
- The **correct path variables** "JAVA_HOME" and "Path" **MUST** be set!

## Setup using IntelliJ
1. Click on "File ->  New -> Project from Version Control..." OR click on "Get from Version Control" in the initial dialog
2. Enter the repository URL and adjust the directory path, if necessary
3. Click on "Clone"
4. Click on "Import" when the build-script-dialog appears
5. Wait until IntelliJ finished to set up the project
6. Close IntelliJ (indexing workaround)
7. Reopen IntelliJ and the project
8. Open the "Maven" sidebar (on the upper right corner)
9. Open the "Plugins" category
10. Open the "clean" category
11. Run "clean:clean"
12. Open the "javafx" category
13. Run "javafx:compile"
14. Run "javafx:run"

## Custom Run-Configuration with IntelliJ
1. Select the project folder
2. Click on "Run -> Edit Configurations..."
3. Click on the "+" icon
4. Select "Maven"
5. Type "javafx:compile javafx:run" into the "Command line" text field
6. Click on "OK"