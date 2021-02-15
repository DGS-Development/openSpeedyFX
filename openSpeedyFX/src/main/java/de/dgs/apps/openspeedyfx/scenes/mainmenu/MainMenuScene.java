package de.dgs.apps.openspeedyfx.scenes.mainmenu;

import animatefx.animation.*;
import com.google.gson.Gson;
import de.dgs.apps.openspeedyfx.game.mapinfo.MapInfo;
import de.dgs.apps.openspeedyfx.game.logic.model.Color;
import de.dgs.apps.openspeedyfx.game.logic.model.Player;
import de.dgs.apps.openspeedyfx.game.resourcepacks.Resourcepack;
import de.dgs.apps.osfxe.audio.AudioPlayer;
import de.dgs.apps.osfxe.scenes.GameController;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class MainMenuScene extends GameController {
    @FXML
    private ImageView imgLogo;

    @FXML
    private ImageView imgHedgehog;

    @FXML
    private BorderPane bpControls;

    @FXML
    private Button btnCompetitiveMode;

    @FXML
    private Button btnCompetitiveBack;

    @FXML
    private Button btnCooperativeBack;

    @FXML
    private Button btnCooperativeMode;

    @FXML
    private Button btnColorBack;

    @FXML
    private Button btnCooperativeStart;

    @FXML
    private Button btnSettings;

    @FXML
    private Button btnLevelEditor;

    @FXML
    private Button btnSettingsBack;

    @FXML
    private Button btnAboutBack;

    @FXML
    private TabPane tabPaneMenus;

    @FXML
    private Tab tabMainMenu;

    @FXML
    private Tab tabCompetitiveMenu;

    @FXML
    private Tab tabCooperativeMenu;

    @FXML
    private Tab tabSettingsMenu;

    @FXML
    private Tab tabMapSelection;

    @FXML
    private Tab tabColorMenu;

    @FXML
    private Tab tabAboutMenu;

    @FXML
    private Spinner<Integer> spPlayersCount;

    @FXML
    private Button btnGreen;

    @FXML
    private Button btnRed;

    @FXML
    private Button btnYellow;

    @FXML
    private Button btnBlue;

    @FXML
    private TextField txtPlayerName;

    @FXML
    private Button btn2Players;

    @FXML
    private Button btn3Players;

    @FXML
    private Button btn4Players;

    @FXML
    private Button btnCredits;

    @FXML
    private TextArea txtAreaCredits;

    @FXML
    private Slider sliderMusicVolume;

    @FXML
    private Slider sliderEffectsVolume;

    @FXML
    private CheckBox cbShowHints;

    @FXML
    private Label lblNameError;

    @FXML
    private ListView<String> lvMaps;

    @FXML
    private ImageView imgMapPreview;

    @FXML
    private Button btnSelectMap;

    @FXML
    private VBox vbMapSelection;

    @FXML
    private MediaView mvBackground;

    @FXML
    private StackPane spRoot;

    @FXML
    private Button btnPlay;

    @FXML
    private Tab tabPlayMenu;

    @FXML
    private Button btnPlayBack;

    @FXML
    private Button btnMapSelectionBack;

    @FXML
    private CheckBox cbAutoScroll;

    @FXML
    private Spinner spFoxMovementCount;

    @FXML
    private TextField txtCustomMapPath;

    @FXML
    private Button btnQuit;

    @Override
    public String getFxmlPath() {
        return "/assets/fxml/mainmenu/mainmenu.fxml";
    }

    private interface ImageLoadedCallback {
        void onImageLoaded(Image image);
    }

    private boolean isCooperativeMode = false;
    private int playersCount = 0;
    private List<Player> players;

    private AudioPlayer audioPlayer;

    @Override
    public void onInitialized() {
        //Setup animations.
        imgHedgehog.setImage(new Image(getClass().getResourceAsStream("/assets/fxml/mainmenu/hedgehogWalking.gif")));

        new Pulse(imgLogo).setCycleCount(AnimationFX.INDEFINITE).play();
        new FadeInUpBig(bpControls).setDelay(Duration.millis(1500)).play();

        //Setup logic.
        setupMainMenuLogic();

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3);
        spPlayersCount.setValueFactory(valueFactory);

        setupCreditsText();

        txtPlayerName.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.isBlank())
                lblNameError.setVisible(false);
        });

        lblNameError.setVisible(false);
        txtAreaCredits.setWrapText(true);

        spRoot.widthProperty().addListener((observable, oldValue, newValue) -> mvBackground.setFitWidth(newValue.doubleValue()));
        spRoot.heightProperty().addListener((observable, oldValue, newValue) -> mvBackground.setFitHeight(newValue.doubleValue()));
    }

    private void reset() {
        isCooperativeMode = false;
        playersCount = 0;
        btnGreen.setDisable(false);
        btnRed.setDisable(false);
        btnYellow.setDisable(false);
        btnBlue.setDisable(false);
        txtPlayerName.clear();
        lvMaps.getSelectionModel().clearSelection();
        tabPaneMenus.getSelectionModel().select(tabMainMenu);
        imgMapPreview.setImage(null);
    }

    private void setupCreditsText() {
        String content;

        try {
            content = Files.readString(Path.of(getClass().getResource("/resources.txt").toURI()));
        }
        catch (Exception exception) {
            content = "Unable to load credits: " + exception.toString();
        }

        txtAreaCredits.setText(content);
    }

    public void setupMenuScene(Resourcepack resourcepack, MainMenuSettingsData mainMenuSettingsData,
                               MenuSceneCallback menuSceneCallback) {
        try {
            byte[] menuLoopBytes = resourcepack.getResourceAsStream("/music/menuLoop.ogg").readAllBytes();

            audioPlayer = new AudioPlayer(menuLoopBytes, true, mainMenuSettingsData.getMusicVolume());
            audioPlayer.play();

            //Setup settings.
            sliderMusicVolume.setValue(mainMenuSettingsData.getMusicVolume() * 100);

            sliderMusicVolume.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                mainMenuSettingsData.setMusicVolume(newValue.floatValue() / 100);
                menuSceneCallback.onMainMenuDataUpdate(mainMenuSettingsData);

                audioPlayer.setVolume(newValue.floatValue() / 100);
            });

            sliderEffectsVolume.setValue(mainMenuSettingsData.getEffectsVolume() * 100);

            sliderEffectsVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
                mainMenuSettingsData.setEffectsVolume(newValue.floatValue() / 100);
                menuSceneCallback.onMainMenuDataUpdate(mainMenuSettingsData);
            });

            cbShowHints.setSelected(mainMenuSettingsData.getShowHints());

            cbShowHints.selectedProperty().addListener((observable, oldValue, newValue) -> {
                mainMenuSettingsData.setShowHints(newValue.booleanValue());
                menuSceneCallback.onMainMenuDataUpdate(mainMenuSettingsData);
            });

            cbAutoScroll.setSelected(mainMenuSettingsData.isAutoScroll());

            cbAutoScroll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                mainMenuSettingsData.setAutoScroll(newValue.booleanValue());
                menuSceneCallback.onMainMenuDataUpdate(mainMenuSettingsData);
            });

            txtCustomMapPath.setText(mainMenuSettingsData.getCustomMapPath());

            spFoxMovementCount.valueProperty().addListener((observable, oldValue, newValue) -> {
                mainMenuSettingsData.setFoxMovementCount(Integer.parseInt(newValue.toString()));
                menuSceneCallback.onMainMenuDataUpdate(mainMenuSettingsData);
            });

            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 8, mainMenuSettingsData.getFoxMovementCount());
            spFoxMovementCount.setValueFactory(valueFactory);

            //Setup UI logic.
            btnQuit.setOnAction(event -> {
                menuSceneCallback.onQuit();
            });

            btnGreen.setOnAction(event -> {
                createPlayer(Color.GREEN, btnGreen, menuSceneCallback, mainMenuSettingsData);
            });

            btnRed.setOnAction(event -> {
                createPlayer(Color.RED, btnRed, menuSceneCallback, mainMenuSettingsData);
            });

            btnBlue.setOnAction(event -> {
                createPlayer(Color.BLUE, btnBlue, menuSceneCallback, mainMenuSettingsData);
            });

            btnYellow.setOnAction(event -> {
                createPlayer(Color.YELLOW, btnYellow, menuSceneCallback, mainMenuSettingsData);
            });

            btnLevelEditor.setOnAction(event -> {
                audioPlayer.stop();
                menuSceneCallback.onLevelEditorClicked();
            });

            //Ignore tab changes with arrows.
            tabPaneMenus.addEventFilter(
                    KeyEvent.ANY,
                    event -> {
                        if (event.getCode().isArrowKey()) {
                            event.consume();
                        }
                    });

            //Play background video loop.
            Media media = new Media(getClass().getResource("/assets/fxml/mainmenu/backgroundVideo.mp4").toURI().toString());

            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setAutoPlay(true);

            mvBackground.setMediaPlayer(mediaPlayer);
        }
        catch (Exception exception) {
            menuSceneCallback.onException(exception);
        }
    }

    public void playAudioIfAvailable() {
        if(audioPlayer != null)
            audioPlayer.play();
    }

    public void stopAudioIfAvailable() {
        if(audioPlayer != null)
            audioPlayer.stop();
    }

    private void createPlayer(Color color, Button btnColor, MenuSceneCallback menuSceneCallback, MainMenuSettingsData settingsData) {
        String name = txtPlayerName.getText();

        if(name.isBlank()) {
            lblNameError.setVisible(true);
            return;
        }

        btnColor.setDisable(true);

        Player player = new Player(name, color);

        if (isCooperativeMode) {
            loadMaps(menuSceneCallback, settingsData, List.of(player));
        }
        else {
            players.add(player);

            if (players.size() == playersCount) {
                loadMaps(menuSceneCallback, settingsData, players);
            }
            else {
                txtPlayerName.setText("");
            }
        }
    }

    private void setupMainMenuLogic() {
        btnCompetitiveMode.setOnAction(event -> {
            players = new LinkedList<>();
            tabPaneMenus.getSelectionModel().select(tabCompetitiveMenu);
            playTabPaneFadeTransition();
        });

        btnCooperativeMode.setOnAction(event -> {
            tabPaneMenus.getSelectionModel().select(tabCooperativeMenu);
            playTabPaneFadeTransition();
        });

        btnSettings.setOnAction(event -> {
            tabPaneMenus.getSelectionModel().select(tabSettingsMenu);
            playTabPaneFadeTransition();
        });

        btnCompetitiveBack.setOnAction(event -> {
            reset();
            playTabPaneFadeTransition();
        });

        btnColorBack.setOnAction(event -> {
            reset();
            playTabPaneFadeTransition();
        });

        btnCooperativeBack.setOnAction(event -> {
            reset();
            playTabPaneFadeTransition();
        });

        btnSettingsBack.setOnAction(event -> {
            reset();
            playTabPaneFadeTransition();
        });

        btnAboutBack.setOnAction(event -> {
            reset();
            playTabPaneFadeTransition();
        });

        btnPlayBack.setOnAction(event -> {
            reset();
            playTabPaneFadeTransition();
        });

        btnMapSelectionBack.setOnAction(event -> {
            reset();
            playTabPaneFadeTransition();
        });

        btnCooperativeStart.setOnAction(event -> {
            isCooperativeMode = true;
            tabPaneMenus.getSelectionModel().select(tabColorMenu);
            playTabPaneFadeTransition();
        });

        btn2Players.setOnAction(event -> {
            playersCount = 2;
            tabPaneMenus.getSelectionModel().select(tabColorMenu);
            playTabPaneFadeTransition();
        });

        btn3Players.setOnAction(event -> {
            playersCount = 3;
            tabPaneMenus.getSelectionModel().select(tabColorMenu);
            playTabPaneFadeTransition();
        });

        btn4Players.setOnAction(event -> {
            playersCount = 4;
            tabPaneMenus.getSelectionModel().select(tabColorMenu);
            playTabPaneFadeTransition();
        });

        btnCredits.setOnAction(event -> {
            tabPaneMenus.getSelectionModel().select(tabAboutMenu);
            playTabPaneFadeTransition();
        });

        btnPlay.setOnAction(event -> {
            tabPaneMenus.getSelectionModel().select(tabPlayMenu);
            playTabPaneFadeTransition();
        });
    }

    private void loadMaps(MenuSceneCallback menuSceneCallback, MainMenuSettingsData settingsData, List<Player> players) {
        try {
            tabPaneMenus.getSelectionModel().select(tabMapSelection);


            List<Path> mapPaths = new LinkedList<>();

            //Load maps from classpath.
            Path mapFilesPath;

            if(isCooperativeMode){
                mapFilesPath = Path.of(getClass().getResource("/assets/fxml/defaultmaps/cooperative").toURI());
            }
            else{
                mapFilesPath = Path.of(getClass().getResource("/assets/fxml/defaultmaps/competitive").toURI());
            }

            Files.list(mapFilesPath)
                    .filter(path -> path.getFileName().toString().endsWith(".mapj"))
                    .forEach(tmpPath -> mapPaths.add(tmpPath));

            //Load maps from filesystem.
            File customMapDirectory = new File(settingsData.getCustomMapPath());

            if(customMapDirectory.isDirectory()) {
                Files.list(customMapDirectory.toPath())
                        .filter(path -> path.getFileName().toString().endsWith(".mapj") && path.toFile().isFile())
                        .forEach(tmpPath -> mapPaths.add(tmpPath));
            }

            Map<String, Path> namePathMap = new HashMap<>();
            Map<String, MapInfo> nameMapInfoMap = new HashMap<>();
            Map<String, Image> namePreviewMap = new HashMap<>();

            List<String> names = new LinkedList<>();

            for (Path tmpPath : mapPaths) {
                MapInfo mapInfo = new Gson().fromJson(Files.readString(tmpPath), MapInfo.class);
                names.add(mapInfo.getMapName());

                namePathMap.put(mapInfo.getMapName(), tmpPath);
                nameMapInfoMap.put(mapInfo.getMapName(), mapInfo);
            }

            SortedList<String> sortedNames = new SortedList(FXCollections.observableList(names));
            sortedNames.setComparator((name1, name2) -> name1.compareToIgnoreCase(name2));
            lvMaps.setItems(sortedNames);

            SimpleObjectProperty<ChangeListener<String>> changeListenerSimpleObjectProperty = new SimpleObjectProperty<>();

            ChangeListener<String> selectionChangeListener = (observable, oldValue, newValue) -> {
                String selectedMapName = lvMaps.getSelectionModel().getSelectedItem();

                if (selectedMapName != null) {
                    Path mapPath = namePathMap.get(selectedMapName);

                    if(mapPath != null) {
                        Path fxmlPath = mapPath.getParent().resolve(nameMapInfoMap.get(selectedMapName).getFxmlName());

                        vbMapSelection.setDisable(true);

                        if (!namePreviewMap.containsKey(selectedMapName)) {
                            loadMapPreviewImage(menuSceneCallback, fxmlPath, image -> {
                                namePreviewMap.put(selectedMapName, image);
                                imgMapPreview.setImage(image);

                                vbMapSelection.setDisable(false);
                                btnSelectMap.setDisable(false);
                            });
                        } else {
                            imgMapPreview.setImage(namePreviewMap.get(selectedMapName));

                            vbMapSelection.setDisable(false);
                            btnSelectMap.setDisable(false);
                        }

                        btnSelectMap.setOnAction(event -> {
                            lvMaps.getSelectionModel().selectedItemProperty().removeListener(changeListenerSimpleObjectProperty.get());

                            if (isCooperativeMode) {
                                menuSceneCallback.onCooperativeStart(spPlayersCount.getValue(), players.get(0), mapPath);
                            } else {
                                menuSceneCallback.onCompetitiveStart(players, mapPath);
                            }

                            reset();
                        });
                    }
                }
            };

            changeListenerSimpleObjectProperty.set(selectionChangeListener);
            lvMaps.getSelectionModel().selectedItemProperty().addListener(selectionChangeListener);
        }
        catch (Exception exception) {
            menuSceneCallback.onException(exception);
        }
    }

    private void loadMapPreviewImage(MenuSceneCallback menuSceneCallback, Path fxmlPath, ImageLoadedCallback imageLoadedCallback) {
        new Thread(() -> {
            final SimpleObjectProperty<Scene> sceneObjectProperty = new SimpleObjectProperty<>(null);

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(fxmlPath.toUri().toURL());

                Parent rootNode = fxmlLoader.load();
                Scene tmpScene = new Scene(rootNode);

                sceneObjectProperty.set(tmpScene);
            }
            catch (Exception exception) {
                menuSceneCallback.onException(exception);
            }

            if(sceneObjectProperty.get() != null)
                Platform.runLater(() -> imageLoadedCallback.onImageLoaded(sceneObjectProperty.get().snapshot(null)));
        }).start();
    }

    private void playTabPaneFadeTransition() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), tabPaneMenus);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }
}