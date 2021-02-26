![dgsLabsLogo](https://github.com/DGS-Development/openSpeedyFX/blob/main/docs/img/dgsLabsLogo.png?raw=true)

**NOTICE: This project isn't maintained on a regular basis, because it is a research project. Please don't open issues!**

# openSpeedyFX
![openSpeedyFxLogo](https://github.com/DGS-Development/openSpeedyFX/blob/main/docs/img/openSpeedyFXLogo.png?raw=true)

openSpeedyFX is a free open-source 2D board game, inspired by Urtis Šulinskas's "Speedy Roll". It was a student research project to practice software engineering. The implementation is Java based.

![featuresPreview](https://github.com/DGS-Development/openSpeedyFX/blob/main/docs/img/featuresPreview.png?raw=true)

This repository includes the game itself ("openSpeedyFX") and an expandable game engine, using JavaFX and jbox2d ("osfxEngine"). It's the foundation for the project, but can be used for any 2D-based games. 

Although the code is covered by a permissive open-source-license (Apache-2.0 License) , and no assets of "Speedy Roll" were used, you can't use the project commercially ("as it is"). It includes some assets which forbid any commercial use.
Please check the file "ASSETS-LICENSES.txt" for more detailed information.

## Features

* Competitive mode (hedgehog race for 2-4 players)
* Cooperative mode (hedgehog chase for 1-10 players)
* Difficulty support (easy, medium and hard)
* Mapeditor to create custom maps
* Localization support (includes English and German by default)

# Developer instructions

## Requirements
- At least JDK-14, preinstalled
- The **correct path variables** "JAVA_HOME" and "Path" **MUST** be set

## Setup using IntelliJ
1. Click on the menu bar entry "File ->  New -> Project from Version Control..." OR click on "Get from Version Control" in the initial dialog
2. Enter the repository URL and adjust the directory path, if necessary
3. Click on "Clone"
4. Enter the necessary credentials or use an access token (optional)
5. Click on "Import" when the build-script-dialog appears in the lower right corner
6. Wait until IntelliJ finished setting up the project
7. Close IntelliJ
8. Reopen IntelliJ and the project
9. Open the "Maven" sidebar (on the upper right corner)
10. Open "osfxEngine"
11. Open the "Lifecycle" category
12. Run "install"
13. Open "openSpeedyFX"
14. Open the "Lifecycle" category
15. Run "clean"
16. Run "install"
17. Open the "Plugins" category
18. Open the "javafx" category
19. Run "javafx:run"

## Custom Run-Configuration with IntelliJ
1. Click on the menu bar entry "Run -> Edit Configurations..."
2. Click on the "+" icon in the opened window
3. Select "Maven"
4. Select "openSpeedyFX" as directory of "Working directory"
5. Enter a name into the "Name" text field (for instance "openSpeedyFX")
6. Type "compile javafx:run" into the "Command line" text field
7. Click on "OK"