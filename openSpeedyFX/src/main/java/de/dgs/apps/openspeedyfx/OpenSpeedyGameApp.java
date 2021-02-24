package de.dgs.apps.openspeedyfx;

import de.dgs.apps.openspeedyfx.game.logic.model.Player;
import de.dgs.apps.openspeedyfx.game.resourcepacks.DefaultResourcepack;
import de.dgs.apps.openspeedyfx.game.resourcepacks.Resourcepack;
import de.dgs.apps.openspeedyfx.game.resourcepacks.ResourcepackPaths.Figures;
import de.dgs.apps.openspeedyfx.scenes.ballscene.logic.HedgehogIrritation;
import de.dgs.apps.openspeedyfx.scenes.ballscene.logic.HedgehogPhysicsProperties;
import de.dgs.apps.openspeedyfx.scenes.ballscene.logic.RollProperties;
import de.dgs.apps.openspeedyfx.scenes.ballscene.logic.SelectiveRollProperties;
import de.dgs.apps.openspeedyfx.scenes.dialogues.NotificationDialogueScene;
import de.dgs.apps.openspeedyfx.scenes.dialogues.SelectionDialogueScene;
import de.dgs.apps.openspeedyfx.scenes.mainmenu.Difficulty;
import de.dgs.apps.openspeedyfx.scenes.mainmenu.MainMenuScene;
import de.dgs.apps.openspeedyfx.scenes.mainmenu.MainMenuSettingsData;
import de.dgs.apps.openspeedyfx.scenes.mainmenu.MenuSceneCallback;
import de.dgs.apps.openspeedyfx.scenes.mapeditor.MapeditorScene;
import de.dgs.apps.openspeedyfx.scenes.gamemap.GameMapCallback;
import de.dgs.apps.openspeedyfx.scenes.gamemap.GameMapData;
import de.dgs.apps.openspeedyfx.scenes.gamemap.GameMapScene;
import de.dgs.apps.osfxe.gui.JavaFxDialogs;
import de.dgs.apps.osfxe.scenes.GameControllerLoader;
import de.dgs.apps.osfxe.scenes.SceneCreator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.aeonbits.owner.ConfigFactory;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.*;

/*
Copyright 2021 DGS-Development (https://github.com/DGS-Development)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

public class OpenSpeedyGameApp extends Application {
    private Stage mainMenuStage;

    private GameMapScene gameMapScene;
    private Stage gameMapStage;

    private Stage mapeditorStage;

    //Necessary to create a new instance of the game scene.
    private void setupGameMap(ResourceBundle resourceBundle) throws Exception {
        gameMapScene = GameControllerLoader.loadController(GameMapScene.class, resourceBundle);

        Scene gameMapSceneObject = SceneCreator.createScene(gameMapScene);

        gameMapStage = new Stage();
        gameMapStage.setScene(gameMapSceneObject);
        setStageInfo(gameMapStage, resourceBundle.getString("app.gameMapStageTitle"), "/assets/fxml/gamemap/mapIcon.png");
    }

    public void start(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainMenuStage = primaryStage;

        OpenSpeedyConfiguration configuration = ConfigFactory.create(OpenSpeedyConfiguration.class);

        String selectedLanguageTag = createSelectionDialogue(
                "Please select a language.",
                "Please select a language:",
                List.of(configuration.languageTags()),
                new Image(getClass().getResourceAsStream("/assets/general/flags.png")));

        if(selectedLanguageTag == null)
            System.exit(0);

        Locale appLocale = Locale.forLanguageTag(selectedLanguageTag.toLowerCase());

        //--------------------------
        //Load defaults.
        //--------------------------
        long randomSeed;

        if(configuration.useCustomSeed()) {
            randomSeed = configuration.customSeed();
        }
        else {
            randomSeed = System.currentTimeMillis();
        }

        Random random = new Random(randomSeed);

        Resourcepack resourcepack = DefaultResourcepack.getInstance();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("localization/UiResources", appLocale);

        //--------------------------
        //Setup map editor scene.
        //--------------------------
        MapeditorScene mapeditorScene = GameControllerLoader.loadController(MapeditorScene.class, resourceBundle);
        mapeditorScene.setupMapEditor(resourceBundle);

        Scene mapeditorSceneObject = SceneCreator.createScene(mapeditorScene);

        mapeditorStage = new Stage();
        mapeditorStage.setScene(mapeditorSceneObject);
        setStageInfo(mapeditorStage, resourceBundle.getString("app.mapeditorStageTitle"), "/assets/fxml/mapeditor/mapIcon.png");

        //--------------------------
        //Setup menu scene.
        //--------------------------
        MainMenuScene mainMenuScene = GameControllerLoader.loadController(MainMenuScene.class, resourceBundle);
        Scene mainMenuSceneObject = SceneCreator.createScene(mainMenuScene);

        setStageInfo(mainMenuStage, resourceBundle.getString("app.title"), "/assets/fxml/mainmenu/hedgehogIcon.png");
        mainMenuStage.setScene(mainMenuSceneObject);

        mainMenuStage.setMinWidth(configuration.menuMinWidth());
        mainMenuStage.setMinHeight(configuration.menuMinHeight());

        mainMenuStage.setMaximized(true);

        SelectiveRollProperties selectiveRollProperties = new SelectiveRollProperties() {
            @Override
            public ResourceBundle getResourceBundle() {
                return resourceBundle;
            }

            @Override
            public Resourcepack getResourcepack() {
                return resourcepack;
            }

            @Override
            public float getEffectsBaseVolume() {
                return configuration.effectsVolume();
            }

            @Override
            public long getRandomSeed() {
                return randomSeed;
            }
        };

        GameMapCallback gameMapCallback = new GameMapCallback() {
            @Override
            public void onMapIsReady() {
                gameMapStage.show();
            }

            @Override
            public void onGameOver() {
                try {
                    gameMapStage.hide();
                    gameMapScene.deactivate();

                    mainMenuScene.playAudioIfAvailable();

                    mainMenuStage.setMaximized(true);
                    mainMenuStage.show();
                }
                catch (Exception exception) {
                    OpenSpeedyGameApp.this.onException(resourceBundle, exception);
                }
            }

            @Override
            public void onException(Exception exception) {
                OpenSpeedyGameApp.this.onException(resourceBundle, exception);
            }
        };

        //Default map data.
        GameMapData gameMapData = new GameMapData();
        gameMapData.setGameMapCallback(gameMapCallback);
        gameMapData.setRandom(random);
        gameMapData.setResourceBundle(resourceBundle);
        gameMapData.setResourcepack(resourcepack);
        gameMapData.setRollProperties(selectiveRollProperties);

        MenuSceneCallback menuSceneCallback = new MenuSceneCallback() {
            @Override
            public void onCooperativeStart(int playersCount, Player player, Path mapInfoPath, Difficulty difficulty) {
                try {
                    Stage notificationStage = createNotificationStage(
                            resourceBundle.getString("app.loadingGameData"),
                            new Image(resourcepack.getResourceAsStream(Figures.HEDGEHOG_PNG)));

                    notificationStage.setOnShown(showEvent -> {
                        showEvent.consume();

                        Platform.runLater(() -> {
                            try {
                                //Setup difficulty properties.
                                selectiveRollProperties.setHedgehogIrritation(irritationFromDifficulty(difficulty, configuration));
                                selectiveRollProperties.setHedgehogPhysicsProperties(physicsPropertiesFromDifficulty(difficulty, configuration));
                                gameMapData.setFoxMovementCount(foxMovementCountFromDifficulty(difficulty, configuration));

                                //Set game map data.
                                reconfigureGameMapData(gameMapData, configuration);

                                gameMapData.setPhysicalPlayersCount(playersCount);
                                gameMapData.setPlayers(List.of(player));
                                gameMapData.setMapInfoPath(mapInfoPath);

                                //Show game map scene.
                                setupGameMap(resourceBundle);

                                gameMapScene.setupGameMap(gameMapData);

                                gameMapStage.setOnCloseRequest(event -> {
                                    gameMapScene.deactivate();
                                    gameMapStage.hide();

                                    mainMenuScene.playAudioIfAvailable();

                                    mainMenuStage.setMaximized(true);
                                    mainMenuStage.show();
                                });

                                gameMapStage.setMaximized(true);
                                notificationStage.close();
                            }
                            catch (Exception exception) {
                                OpenSpeedyGameApp.this.onException(resourceBundle, exception);
                            }
                        });
                    });

                    mainMenuScene.stopAudioIfAvailable();
                    mainMenuStage.hide();

                    notificationStage.show();
                }
                catch (Exception exception) {
                    OpenSpeedyGameApp.this.onException(resourceBundle, exception);
                }
            }

            @Override
            public void onCompetitiveStart(List<Player> players, Path mapInfoPath, Difficulty difficulty) {
                try {
                    Stage notificationStage = createNotificationStage(
                            resourceBundle.getString("app.loadingGameData"),
                            new Image(resourcepack.getResourceAsStream(Figures.HEDGEHOG_PNG)));

                    notificationStage.setOnShown(showEvent -> {
                        showEvent.consume();

                        Platform.runLater(() -> {
                            try {
                                //Setup difficulty properties.
                                selectiveRollProperties.setHedgehogIrritation(irritationFromDifficulty(difficulty, configuration));
                                selectiveRollProperties.setHedgehogPhysicsProperties(physicsPropertiesFromDifficulty(difficulty, configuration));

                                //Set game map data.
                                reconfigureGameMapData(gameMapData, configuration);

                                gameMapData.setPhysicalPlayersCount(0);
                                gameMapData.setPlayers(players);
                                gameMapData.setMapInfoPath(mapInfoPath);

                                //Show game map scene.
                                setupGameMap(resourceBundle);

                                gameMapScene.setupGameMap(gameMapData);

                                gameMapStage.setOnCloseRequest(event -> {
                                    gameMapScene.deactivate();
                                    gameMapStage.hide();

                                    mainMenuScene.playAudioIfAvailable();

                                    mainMenuStage.setMaximized(true);
                                    mainMenuStage.show();
                                });

                                gameMapStage.setMaximized(true);
                                notificationStage.close();
                            }
                            catch (Exception exception) {
                                OpenSpeedyGameApp.this.onException(resourceBundle, exception);
                            }
                        });
                    });

                    mainMenuScene.stopAudioIfAvailable();
                    mainMenuStage.hide();

                    notificationStage.show();
                }
                catch (Exception exception) {
                    OpenSpeedyGameApp.this.onException(resourceBundle, exception);
                }
            }

            @Override
            public void onLevelEditorClicked() {
                mainMenuStage.hide();

                mapeditorStage.setMaximized(true);
                mapeditorStage.show();
            }

            @Override
            public void onMainMenuDataUpdate(MainMenuSettingsData mainMenuData) {
                try {
                    configuration.setProperty(OpenSpeedyConfiguration.PROPERTY_MUSIC_VOLUME, mainMenuData.getMusicVolume() + "");
                    configuration.setProperty(OpenSpeedyConfiguration.PROPERTY_EFFECTS_VOLUME, mainMenuData.getEffectsVolume() + "");
                    configuration.setProperty(OpenSpeedyConfiguration.PROPERTY_SHOW_HINTS, mainMenuData.getShowHints() + "");
                    configuration.setProperty(OpenSpeedyConfiguration.PROPERTY_AUTO_SCROLL, mainMenuData.isAutoScroll() + "");

                    configuration.store(new FileOutputStream(OpenSpeedyConfiguration.FILENAME), "");

                    configuration.reload();
                }
                catch (Exception exception) {
                    OpenSpeedyGameApp.this.onException(resourceBundle, exception);
                }
            }

            @Override
            public void onException(Exception exception) {
                OpenSpeedyGameApp.this.onException(resourceBundle, exception);
            }

            @Override
            public void onQuit() {
                primaryStage.close();
            }
        };

        MainMenuSettingsData mainMenuSettingsData = new MainMenuSettingsData();
        mainMenuSettingsData.setEffectsVolume(configuration.effectsVolume());
        mainMenuSettingsData.setMusicVolume(configuration.musicVolume());
        mainMenuSettingsData.setShowHints(configuration.showHints());
        mainMenuSettingsData.setAutoScroll(configuration.autoScroll());
        mainMenuSettingsData.setCustomMapPath(configuration.customMapsDirectoryPath());

        mainMenuScene.setupMenuScene(resourcepack, mainMenuSettingsData, menuSceneCallback, resourceBundle);

        //Setup editor change.
        mapeditorStage.setOnCloseRequest(event -> {
            event.consume();
            mapeditorStage.hide();

            mainMenuScene.playAudioIfAvailable();

            mainMenuStage.show();
            mainMenuStage.setMaximized(true);
        });

        mainMenuStage.show();
    }

    private int indexFromDifficulty(Difficulty difficulty) {
        int index = 0;

        if (difficulty == Difficulty.MEDIUM) {
            index = 1;
        }
        else if (difficulty == Difficulty.HARD) {
            index = 2;
        }

        return index;
    }

    private int foxMovementCountFromDifficulty(Difficulty difficulty, OpenSpeedyConfiguration configuration) {
        int index = indexFromDifficulty(difficulty);

        return configuration.foxMovementCount()[index];
    }

    private HedgehogIrritation irritationFromDifficulty(Difficulty difficulty, OpenSpeedyConfiguration configuration) {
        int index = indexFromDifficulty(difficulty);

        HedgehogIrritation hedgehogIrritation = new HedgehogIrritation(
                configuration.difficultiesIrritationImbalance()[index],
                configuration.difficultiesIrritationSlowdownFactor()[index]);

        return hedgehogIrritation;
    }

    private HedgehogPhysicsProperties physicsPropertiesFromDifficulty(Difficulty difficulty, OpenSpeedyConfiguration configuration) {
        int index = indexFromDifficulty(difficulty);

        HedgehogPhysicsProperties hedgehogPhysicsProperties = new HedgehogPhysicsProperties(
                configuration.difficultiesPhysicsLinearDamping()[index],
                configuration.difficultiesPhysicsDensity()[index],
                configuration.difficultiesPhysicsFriction()[index],
                configuration.difficultiesPhysicsRestitution()[index]);

        return hedgehogPhysicsProperties;
    }

    private void reconfigureGameMapData(GameMapData gameMapData, OpenSpeedyConfiguration configuration) {
        gameMapData.setShowHints(configuration.showHints());
        gameMapData.setAutoScroll(configuration.autoScroll());
        gameMapData.setSoundsVolume(configuration.effectsVolume());
        gameMapData.setMusicVolume(configuration.musicVolume());
    }

    private Stage createNotificationStage(String text, Image image) throws Exception {
        NotificationDialogueScene notificationDialogueScene = GameControllerLoader.loadController(NotificationDialogueScene.class);
        Scene notificationDialogueSceneObject = SceneCreator.createScene(notificationDialogueScene);

        notificationDialogueScene.getLblText().setText(text);
        notificationDialogueScene.getImgImage().setImage(image);

        Stage notificationStage = new Stage(StageStyle.UNDECORATED);
        notificationStage.setScene(notificationDialogueSceneObject);

        return notificationStage;
    }

    private void setStageInfo(Stage primaryStage, String title, String path) {
        primaryStage.setTitle(title);

        primaryStage.getIcons().removeAll();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(path)));
    }

    private String createSelectionDialogue(String title, String labelHeader, List<String> entries, Image stageImage) throws Exception {
        SelectionDialogueScene selectionDialogueScene = GameControllerLoader.loadController(SelectionDialogueScene.class);
        Scene selectionDialogueSceneObject = SceneCreator.createScene(selectionDialogueScene);

        Stage selectionDialogueStage = new Stage();

        selectionDialogueStage.getIcons().add(stageImage);
        selectionDialogueStage.setScene(selectionDialogueSceneObject);
        selectionDialogueStage.setResizable(false);
        selectionDialogueStage.setTitle(title);
        selectionDialogueStage.initModality(Modality.APPLICATION_MODAL);

        selectionDialogueScene.getLblTitle().setText(labelHeader);
        selectionDialogueScene.getLvItems().setItems(FXCollections.observableList(entries));

        final String[] selectedItem = new String[1];

        selectionDialogueScene.getLvItems().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectionDialogueScene.getBtnContinue().setDisable(false);
            selectedItem[0] = newValue;
        });

        if(selectionDialogueScene.getLvItems().getItems().size() > 0)
            selectionDialogueScene.getLvItems().getSelectionModel().select(0);

        selectionDialogueScene.getBtnContinue().setOnAction(event -> selectionDialogueStage.close());
        selectionDialogueStage.showAndWait();

        return selectedItem[0];
    }

    private void onException(ResourceBundle resourceBundle, Exception exception) {
        exception.printStackTrace();

        //Run later, to catch exceptions which occur while playing animations.
        Platform.runLater(() -> {
            if(mainMenuStage != null)
                mainMenuStage.hide();

            if(mapeditorStage != null)
                mapeditorStage.hide();

            if(gameMapStage != null)
                gameMapStage.hide();

            Alert alert = JavaFxDialogs.createExceptionDialog(
                    resourceBundle.getString("app.anUnexpectedErrorOccurred"),
                    resourceBundle.getString("app.theFollowingErrorOccurredLabel"),
                    resourceBundle.getString("app.pleaseContactAnAdministrator"),
                    exception);

            alert.showAndWait();

            System.exit(1);
        });
    }
}
