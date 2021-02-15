package de.dgs.apps.openspeedyfx.scenes.gamemap;

import animatefx.animation.*;
import com.google.gson.Gson;
import de.dgs.apps.openspeedyfx.game.logic.model.Map;
import de.dgs.apps.openspeedyfx.game.mapinfo.MapInfo;
import de.dgs.apps.openspeedyfx.game.mapinfo.MapInfoParser;
import de.dgs.apps.openspeedyfx.game.mapinfo.MapInfoParser.MapData;
import de.dgs.apps.openspeedyfx.game.resourcepacks.Resourcepack;
import de.dgs.apps.openspeedyfx.scenes.ballscene.InteractiveBallScene;
import de.dgs.apps.openspeedyfx.scenes.dialogues.InformationDialogueScene;
import de.dgs.apps.openspeedyfx.scenes.dialogues.SelectionDialogueScene;
import de.dgs.apps.openspeedyfx.scenes.notification.NotificationScene;
import de.dgs.apps.openspeedyfx.speedyfield.SpeedyFxField;
import de.dgs.apps.openspeedyfx.game.logic.model.*;
import de.dgs.apps.openspeedyfx.scenes.ballscene.logic.*;
import de.dgs.apps.osfxe.audio.AudioPlayer;
import de.dgs.apps.osfxe.audio.SoundAudioPlayer;
import de.dgs.apps.osfxe.gui.ClipAdaptionHelper;
import de.dgs.apps.osfxe.gui.JavaFxDialogs;
import de.dgs.apps.osfxe.gui.NodeSelectionHelper;
import de.dgs.apps.osfxe.gui.zoomingpane.ZoomingPane;
import de.dgs.apps.osfxe.gui.zoomingpane.ZoomingPaneInteractionController;
import de.dgs.apps.osfxe.scenes.GameController;
import de.dgs.apps.osfxe.scenes.GameControllerLoader;
import de.dgs.apps.osfxe.scenes.SceneCreator;
import de.dgs.apps.osfxe.scenes.SceneManager;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GameMapScene extends GameController {
    @FXML
    private BorderPane bpRoot;

    @FXML
    private BorderPane bpMap;

    @FXML
    private ImageView imgActivePlayer;

    @FXML
    private Label lblActivePlayerDescriptionText;

    @FXML
    private Label lblActivePlayerText;

    @FXML
    private Label lblActivePlayerIdText;

    @FXML
    private Label lblApplesItemCount;

    @FXML
    private Label lblLeafsItemsCount;

    @FXML
    private Label lblMushroomsItemCount;

    @FXML
    private VBox vbControls;

    @FXML
    private ImageView imgCollectedApples;

    @FXML
    private ImageView imgCollectedLeafs;

    @FXML
    private ImageView imgCollectedMushrooms;

    @FXML
    private Pane paneRoot;

    @Override
    public String getFxmlPath() {
        return "/assets/fxml/gamemap/gamemap.fxml";
    }

    private static class PlayerSlots {
        private int freeSlotsCount = 4;
        private HashMap<Integer, Player> slotPlayerMap = new HashMap<>();
        private boolean[] freeSlot = new boolean[] {true, true, true, true};

        private Random random;
        private double centerOffset;

        public PlayerSlots(Random random, double centerOffset) {
            this.random = random;
            this.centerOffset = centerOffset;
        }

        public Point2D getSlotOffset(Player player) throws Exception {
            if(freeSlotsCount == 0)
                throw new Exception("Unable to get slot. Cause: All slots were set.");

            boolean slotFound = false;

            int slotId = 0;

            while(!slotFound) {
                slotId = random.nextInt(freeSlot.length);

                if(freeSlot[slotId])
                    slotFound = true;
            }

            freeSlot[slotId] = false;
            slotPlayerMap.put(slotId, player);

            freeSlotsCount--;

            if(slotId == 0) {
                return new Point2D(-centerOffset, -centerOffset);
            }
            else if(slotId == 1) {
                return new Point2D(centerOffset, -centerOffset);
            }
            else if(slotId == 2) {
                return new Point2D(-centerOffset, centerOffset);
            }
            else {
                return new Point2D(centerOffset, centerOffset);
            }
        }

        public boolean freeSlot(Player player) {
            int slotId = -1;

            for(var tmpEntry : slotPlayerMap.entrySet()) {
                if(tmpEntry.getValue().equals(player)) {
                    slotId = tmpEntry.getKey();
                    break;
                }
            }

            if(slotId == -1)
                return false;

            slotPlayerMap.remove(player);
            freeSlot[slotId] = true;

            freeSlotsCount++;

            return true;
        }
    }

    //Actual map.
    private ZoomingPane zoomingPane;
    private ZoomingPaneInteractionController sceneInteractionController;
    private ClipAdaptionHelper clipAdaptionHelper;

    private HashMap<Actor, ImageView> actorImageMap;
    private HashMap<SpeedyFxField, PlayerSlots> fieldSlotsMap;

    //UI
    private SimpleIntegerProperty applesIntegerProperty;
    private SimpleIntegerProperty leafsIntegerProperty;
    private SimpleIntegerProperty mushroomsIntegerProperty;

    private SimpleIntegerProperty activePlayerIdIntegerProperty;

    //Game logic.
    private MapData mapData;
    private Map map;
    private AbstractGameMode gameMode;
    private Player activePlayer;

    //Internal.
    private Stage rollStage;

    private boolean isInitialized = false;
    private boolean isAudioActive;
    private boolean isNotificationOpen = false;

    private AudioPlayer musicLoop1AudioPlayer = null;
    private AudioPlayer musicLoop2AudioPlayer = null;
    private AudioPlayer ambientAudioPlayer = null;

    private SoundAudioPlayer winSoundAudioPlayer = null;
    private SoundAudioPlayer hedgehogSoundAudioPlayer = null;
    private SoundAudioPlayer foxSoundAudioPlayer = null;
    private SoundAudioPlayer foxIsNearAudioPlayer = null;

    @Override
    public void onInitialized() {
        setupIntegerProperties();
    }

    private void setupIntegerProperties() {
        applesIntegerProperty = new SimpleIntegerProperty(0);

        applesIntegerProperty.addListener((observable, oldValue, newValue) -> {
            lblApplesItemCount.setText(newValue + "");

            if(newValue.intValue() != 0)
                new Flash(imgCollectedApples).play();
        });

        leafsIntegerProperty = new SimpleIntegerProperty(0);

        leafsIntegerProperty.addListener((observable, oldValue, newValue) -> {
            lblLeafsItemsCount.setText(newValue + "");

            if(newValue.intValue() != 0)
                new Flash(imgCollectedLeafs).play();
        });

        mushroomsIntegerProperty = new SimpleIntegerProperty(0);

        mushroomsIntegerProperty.addListener((observable, oldValue, newValue) -> {
            lblMushroomsItemCount.setText(newValue + "");

            if(newValue.intValue() != 0)
                new Flash(imgCollectedMushrooms).play();
        });

        activePlayerIdIntegerProperty = new SimpleIntegerProperty(0);

        activePlayerIdIntegerProperty.addListener((observable, oldValue, newValue) -> {
            lblActivePlayerIdText.setText("(" + newValue + ")");
        });
    }

    public boolean setupGameMap(GameMapData gameMapData) throws Exception {
        if(isInitialized)
            return false;

        isInitialized = true;

        //Setup zooming pane.
        if(gameMapData.isAutoScroll()) {
            setupZoomingPane(1);
        }
        else {
            setupZoomingPane(0.5);
        }

        //Load icons.
        imgCollectedApples.setImage(
                new Image(gameMapData.getResourcepack().getResourceAsStream("/fields/appleField.png"),
                        imgCollectedApples.getFitWidth(),
                        imgCollectedApples.getFitHeight(),
                        true,
                        true));

        imgCollectedLeafs.setImage(
                new Image(gameMapData.getResourcepack().getResourceAsStream("/fields/leafField.png"),
                        imgCollectedLeafs.getFitWidth(),
                        imgCollectedLeafs.getFitHeight(),
                        true,
                        true));

        imgCollectedMushrooms.setImage(
                new Image(gameMapData.getResourcepack().getResourceAsStream("/fields/mushroomField.png"),
                        imgCollectedMushrooms.getFitWidth(),
                        imgCollectedMushrooms.getFitHeight(),
                        true,
                        true));

        //Load map data.
        mapData = loadMapData(gameMapData.getMapInfoPath());

        //Field slot map.
        fieldSlotsMap = new HashMap<>();

        mapData.getSpeedyFxFields().forEach(tmpField -> {
            fieldSlotsMap.put(tmpField, new PlayerSlots(gameMapData.getRandom(), 40));
        });

        //Player image representation map.
        actorImageMap = new HashMap<>();

        //Setup audio.
        isAudioActive = true;

        LineListener musicLoop1Listener = event -> {
            if(event.getType().equals(LineEvent.Type.STOP)) {
                if(isAudioActive)
                    musicLoop2AudioPlayer.restart();
            }
        };

        LineListener musicLoop2Listener = event -> {
            if(event.getType().equals(LineEvent.Type.STOP)) {
                if(isAudioActive)
                    musicLoop1AudioPlayer.restart();
            }
        };

        musicLoop1AudioPlayer = new AudioPlayer(
                gameMapData.getResourcepack().getResourceAsStream("/music/ingameMusic1.ogg").readAllBytes(),
                false,
                gameMapData.getMusicVolume(),
                musicLoop1Listener);

        musicLoop2AudioPlayer = new AudioPlayer(
                gameMapData.getResourcepack().getResourceAsStream("/music/ingameMusic2.ogg").readAllBytes(),
                false,
                gameMapData.getMusicVolume(),
                musicLoop2Listener);

        if(gameMapData.getRandom().nextBoolean()) {
            musicLoop1AudioPlayer.play();
        }
        else {
            musicLoop2AudioPlayer.play();
        }

        int randomNoiseId = gameMapData.getRandom().nextInt(2) + 1;

        ambientAudioPlayer = new AudioPlayer(
                gameMapData.getResourcepack()
                        .getResourceAsStream("/sounds/gamemapscene/ambientNoise" + randomNoiseId + ".ogg").readAllBytes(),
                true,
                gameMapData.getSoundsVolume());

        ambientAudioPlayer.play();

        winSoundAudioPlayer = new SoundAudioPlayer(gameMapData.getSoundsVolume(), gameMapData.getRandom());

        winSoundAudioPlayer.addSound(gameMapData.getResourcepack()
                .getResourceAsStream("/sounds/gamemapscene/applause1.wav"));

        winSoundAudioPlayer.addSound(gameMapData.getResourcepack()
                .getResourceAsStream("/sounds/gamemapscene/applause2.wav"));

        hedgehogSoundAudioPlayer = new SoundAudioPlayer(gameMapData.getSoundsVolume(), gameMapData.getRandom());

        hedgehogSoundAudioPlayer.addSound(gameMapData.getResourcepack()
                .getResourceAsStream("/sounds/gamemapscene/hedgehogMove.wav"));

        foxSoundAudioPlayer = new SoundAudioPlayer(gameMapData.getSoundsVolume(), gameMapData.getRandom());

        foxSoundAudioPlayer.addSound(gameMapData.getResourcepack()
                .getResourceAsStream("/sounds/gamemapscene/foxMove.wav"));

        foxIsNearAudioPlayer = new SoundAudioPlayer(gameMapData.getSoundsVolume(), gameMapData.getRandom());

        foxIsNearAudioPlayer.addSound(gameMapData.getResourcepack()
                .getResourceAsStream("/sounds/gamemapscene/foxIsNear.wav"));

        //Start game.
        setupUi(gameMapData.getResourcepack());
        setupGameLogic(gameMapData);

        gameMapData.getGameMapCallback().onMapIsReady();
        gameMode.nextTurn();

        return true;
    }

    private void setupZoomingPane(double minScale) {
        zoomingPane = new ZoomingPane();

        zoomingPane.setEffect(new DropShadow());

        sceneInteractionController = new ZoomingPaneInteractionController(
                zoomingPane,
                true,
                true,
                minScale,
                1,
                ZoomingPaneInteractionController.DEFAULT_MOUSE_DELTA_FACTOR,
                false);

        sceneInteractionController.registerEventFilters(zoomingPane);

        clipAdaptionHelper = new ClipAdaptionHelper();
        clipAdaptionHelper.bindClipAdaption(paneRoot);

        paneRoot.getChildren().add(zoomingPane);
    }

    private void setupUi(Resourcepack resourcepack) {
        replaceSpeedyFxFieldImage(mapData.getSpeedyFxFields(), resourcepack);
        zoomingPane.getChildren().add(mapData.getRootNode());
    }

    private void setupGameLogic(GameMapData gameMapData) {
        map = new Map(mapData.getFirstTile());

        GameModeCallback gameModeCallback = new GameModeCallback() {
            @Override
            public void onInitialized(List<? extends Actor> actors) {
                //Add actors to map.
                SpeedyFxField startField = mapData.getTileFieldMapping().get(mapData.getStartTile());

                for(Actor tmpActor : actors) {
                    try {
                        Point2D offset;
                        double actorWidth, actorHeight;

                        if(tmpActor instanceof Player) {
                            offset = fieldSlotsMap.get(startField).getSlotOffset((Player) tmpActor);
                            actorWidth = 95;
                            actorHeight = 95;
                        }
                        else {
                            offset = new Point2D(0,0);
                            actorWidth = 250;
                            actorHeight = 250;
                        }

                        ImageView actorImageView = new ImageView(
                                actorToImage(gameMapData.getResourcepack(), tmpActor, actorWidth, actorHeight));

                        actorImageMap.put(tmpActor, actorImageView);

                        Point2D startFieldCenter;

                        if(tmpActor instanceof Player) {
                            startFieldCenter = calculateCenter(startField);
                        }
                        else {
                            startFieldCenter = calculateCenter(mapData.getTileFieldMapping().get(mapData.getFirstTile()));
                        }

                        actorImageView.setLayoutX(startFieldCenter.getX() - (actorImageView.getImage().getWidth() / 2) + offset.getX());
                        actorImageView.setLayoutY(startFieldCenter.getY() - (actorImageView.getImage().getHeight() / 2) + offset.getY());

                        mapData.getRootNode().getChildren().add(actorImageView);
                    }
                    catch (Exception exception) {
                        gameMapData.getGameMapCallback().onException(exception);
                    }
                }
            }

            @Override
            public void onRoll(int appleCount, int mushroomCount, int leafCount) {
                lblActivePlayerDescriptionText.setText(
                        gameMapData.getResourceBundle().getString("gamemap.helloPleaseHelpMeToCollectItemsByRollingTheBall"));

                String text = gameMapData.getResourceBundle().getString("gamemap.pleaseRollTheBall") +
                        " " + gameMapData.getResourceBundle().getString("gamemap.activePlayerLabel") + " " + activePlayer.getName();

                try {
                    showNotificationDialog(actorToImageStream(gameMapData.getResourcepack(), activePlayer),
                            text,
                            gameMapData.getResourceBundle().getString("gamemap.rollTheBall"),
                            event -> {
                                hideNotificationDialog(0, hideEvent -> {
                                    try {
                                        performRoll(gameMapData, gameMode, new CollectablesCount(appleCount, leafCount, mushroomCount));
                                    }
                                    catch (Exception exception) {
                                        gameMapData.getGameMapCallback().onException(exception);
                                    }
                                });
                            });
                }
                catch (Exception exception) {
                    gameMapData.getGameMapCallback().onException(exception);
                }
            }

            @Override
            public void onActivePlayerSet(Player player) {
                if(gameMapData.getPhysicalPlayersCount() > 0) {
                    if(activePlayerIdIntegerProperty.get() < gameMapData.getPhysicalPlayersCount()) {
                        activePlayerIdIntegerProperty.set(activePlayerIdIntegerProperty.get() + 1);
                    }
                    else {
                        activePlayerIdIntegerProperty.set(1);
                    }
                }

                activePlayer = player;

                setActivePlayerImage(gameMapData.getResourcepack(), player);
                lblActivePlayerText.setText(player.getName());

                applesIntegerProperty.set(0);
                leafsIntegerProperty.set(0);
                mushroomsIntegerProperty.set(0);

                lblActivePlayerDescriptionText.setText(gameMapData.getResourceBundle().getString("gamemap.helloPleaseHelpMeToCollectItemsByRollingTheBall"));
            }

            @Override
            public void onReadyToMove(List<Move> possibleMoves, int appleCount, int mushroomCount, int leafCount) {
                if(gameMapData.isAutoScroll())
                    scrollToField(mapData.getTileFieldMapping().get(activePlayer.getCurrentTile()));

                applesIntegerProperty.set(appleCount);
                mushroomsIntegerProperty.set(mushroomCount);
                leafsIntegerProperty.set(leafCount);

                lblActivePlayerDescriptionText.setText(
                        gameMapData.getResourceBundle().getString("gamemap.greatPleaseClickOnTheMapItemYouWantToMoveToOrClickOnContinue"));

                new Pulse(actorImageMap.get(activePlayer)).setDelay(Duration.millis(500)).play();

                List<Circle> selectableFieldsMarkers = new LinkedList<>();
                List<SpeedyFxField> selectableSpeedyFxFields = new LinkedList<>();

                HashMap<SpeedyFxField, Move> fieldMoveMap = new HashMap<>();

                for(Move tmpMove : possibleMoves) {
                    Tile endTile = tmpMove.getEndTile();

                    SpeedyFxField selectableSpeedyFxField = mapData.getTileFieldMapping().get(endTile);

                    fieldMoveMap.put(selectableSpeedyFxField, tmpMove);
                    selectableSpeedyFxFields.add(selectableSpeedyFxField);

                    if(gameMapData.isShowHints()) {
                        Point2D markerCoordinate = calculateCenter(selectableSpeedyFxField);

                        Circle marker = new Circle(
                                markerCoordinate.getX(),
                                markerCoordinate.getY(),
                                10,
                                javafx.scene.paint.Color.RED);

                        marker.setMouseTransparent(true);

                        selectableFieldsMarkers.add(marker);
                    }
                }

                mapData.getRootNode().getChildren().addAll(selectableFieldsMarkers);

                NodeSelectionHelper<SpeedyFxField> nodeSelectionHelper = new NodeSelectionHelper<>(
                        javafx.scene.paint.Color.GREENYELLOW, 1, 0.5);

                nodeSelectionHelper.getSelectableNode(selectableSpeedyFxFields, false, selectedField -> {
                    try {
                        Move move = fieldMoveMap.get(selectedField);

                        movePlayer(activePlayer, mapData.getTileFieldMapping().get(move.getStartTile()), selectedField);

                        mapData.getRootNode().getChildren().removeAll(selectableFieldsMarkers);
                        gameMode.onMoveSelected(move);

                        hedgehogSoundAudioPlayer.playRandomSound();
                    }
                    catch (Exception exception) {
                        gameMapData.getGameMapCallback().onException(exception);
                    }
                });

                try {
                    showNotificationDialog(getClass().getResourceAsStream("/assets/fxml/gamemap/mapIcon.png"),
                            gameMapData.getResourceBundle().getString("gamemap.greatPleaseClickOnTheMapItemYouWantToMoveToOrClickOnContinue"),
                            gameMapData.getResourceBundle().getString("gamemap.continue"),
                            event -> hideNotificationDialog(0, hideEvent -> {
                                try {
                                    mapData.getRootNode().getChildren().removeAll(selectableFieldsMarkers);
                                    nodeSelectionHelper.reset();

                                    gameMode.onEarlyTurnEnd();

                                    gameMode.nextTurn();
                                }
                                catch (Exception exception) {
                                    gameMapData.getGameMapCallback().onException(exception);
                                }
                            }));
                }
                catch (Exception exception) {
                    gameMapData.getGameMapCallback().onException(exception);
                }
            }

            @Override
            public void onUnableToMove() {
                try {
                    String text = gameMapData.getResourceBundle().getString("gamemap.unableToMoveWithTheCollectedItems");

                    showInfoDialogue(
                            text,
                            text,
                            new Image(getClass().getResourceAsStream("/assets/fxml/gamemap/mapIconBlocked.png")),
                            true,
                            gameMapData.getGameMapCallback());
                }
                catch (Exception exception) {
                    gameMapData.getGameMapCallback().onException(exception);
                }
            }

            @Override
            public void onTooManyItemsCollected(int itemsCount) {
                try {
                    String text = gameMapData.getResourceBundle().getString("gamemap.youCollectedTooManyItems");

                    showInfoDialogue(
                            text,
                            text + " (" + itemsCount + ")",
                            new Image(getClass().getResourceAsStream("/assets/fxml/gamemap/mapIconBlocked.png")),
                            true,
                            gameMapData.getGameMapCallback());
                }
                catch (Exception exception) {
                    gameMapData.getGameMapCallback().onException(exception);
                }
            }

            @Override
            public void onPlayerWon(Player player) {
                winSoundAudioPlayer.playRandomSound();

                try {
                    String text = gameMapData.getResourceBundle().getString("gamemap.playerWonTheGame1") + " " + player.getName() + " " +
                            gameMapData.getResourceBundle().getString("gamemap.playerWonTheGame2");

                    showInfoDialogue(
                            text,
                            text,
                            actorImageMap.get(player).getImage(),
                            true,
                            gameMapData.getGameMapCallback());
                }
                catch (Exception exception) {
                    gameMapData.getGameMapCallback().onException(exception);
                }

                removeActivePlayer();
            }

            @Override
            public void onPlayerLost(Player player) {
                try {
                    String text = gameMapData.getResourceBundle().getString("gamemap.playerLostTheGame1") + " " + player.getName() + " " +
                            gameMapData.getResourceBundle().getString("gamemap.playerLostTheGame2");

                    showInfoDialogue(
                            text,
                            text,
                            actorImageMap.get(player).getImage(),
                            true,
                            gameMapData.getGameMapCallback());
                }
                catch (Exception exception) {
                    gameMapData.getGameMapCallback().onException(exception);
                }

                removeActivePlayer();
            }

            @Override
            public void onGameDone(List<Player> winners) {
                if(winners.size() > 0) {
                    try {
                        List<String> winnerNames = new ArrayList<>(winners.size());

                        for(Player tmpPlayer : winners)
                            winnerNames.add(tmpPlayer.getName());

                        showListDialogue(
                                gameMapData.getResourceBundle().getString("gamemap.theGameIsOver"),
                                gameMapData.getResourceBundle().getString("gamemap.playersWonLabel"),
                                winnerNames,
                                new Image(gameMapData.getResourcepack().getResourceAsStream("/figures/hedgehog.png")),
                                true,
                                gameMapData.getGameMapCallback());
                    }
                    catch (Exception exception) {
                        gameMapData.getGameMapCallback().onException(exception);
                    }
                }

                gameMapData.getGameMapCallback().onGameOver();
            }

            @Override
            public void onPlayerInDanger(Player player) {
                foxIsNearAudioPlayer.playRandomSound();
            }

            @Override
            public void onFoxMove(NPC fox, List<Tile> tilesToMove) {
                Tile firstTile = tilesToMove.get(0);
                Tile lastTile = tilesToMove.get(tilesToMove.size() - 1);

                if(gameMapData.isAutoScroll()) {
                    scrollToField(mapData.getTileFieldMapping().get(firstTile), event -> {
                        foxSoundAudioPlayer.playRandomSound();

                        SpeedyFxField tmpField = mapData.getTileFieldMapping().get(lastTile);
                        moveFox(fox, tmpField);

                        scrollToField(mapData.getTileFieldMapping().get(activePlayer.getCurrentTile()));
                    });
                }
                else {
                    foxSoundAudioPlayer.playRandomSound();

                    SpeedyFxField tmpField = mapData.getTileFieldMapping().get(lastTile);
                    moveFox(fox, tmpField);
                }
            }
        };

        if(mapData.getMapInfo().isCompetitive()) {
            gameMode = new Competitive(gameMapData.getPlayers(), gameModeCallback, map);
        }
        else {
            gameMode = new Cooperative(gameMapData.getPlayers(), gameModeCallback, map, gameMapData.getFoxMovementCount());
        }
    }

    private void showListDialogue(String title, String labelHeader, List<String> entries, Image stageImage,
                                  boolean undecorated, GameMapCallback callback) {
        try {
            SelectionDialogueScene selectionDialogueScene = GameControllerLoader.loadController(SelectionDialogueScene.class);
            Scene selectionDialogueSceneObject = SceneCreator.createScene(selectionDialogueScene);

            Stage selectionDialogueStage;

            if(undecorated) {
                selectionDialogueStage = new Stage(StageStyle.UNDECORATED);
            }
            else {
                selectionDialogueStage = new Stage();
            }

            selectionDialogueStage.getIcons().add(stageImage);
            selectionDialogueStage.setScene(selectionDialogueSceneObject);
            selectionDialogueStage.setResizable(false);
            selectionDialogueStage.setTitle(title);
            selectionDialogueStage.initModality(Modality.APPLICATION_MODAL);

            selectionDialogueScene.getLblTitle().setText(labelHeader);
            selectionDialogueScene.getLvItems().setItems(FXCollections.observableList(entries));

            selectionDialogueScene.getBtnContinue().setOnAction(event -> selectionDialogueStage.close());

            selectionDialogueStage.showAndWait();
        }
        catch (Exception exception) {
            callback.onException(exception);
        }
    }

    private void showInfoDialogue(String title, String text, Image image, boolean undecorated, GameMapCallback callback) {
        try {
            InformationDialogueScene informationDialogueScene = GameControllerLoader.loadController(InformationDialogueScene.class);
            Scene informationDialogueSceneObject = SceneCreator.createScene(informationDialogueScene);

            Stage infoDialogueStage;

            if(undecorated) {
                infoDialogueStage = new Stage(StageStyle.UNDECORATED);
            }
            else {
                infoDialogueStage = new Stage();
            }

            infoDialogueStage.getIcons().add(image);
            infoDialogueStage.setScene(informationDialogueSceneObject);
            infoDialogueStage.setResizable(false);
            infoDialogueStage.setTitle(title);
            infoDialogueStage.initModality(Modality.APPLICATION_MODAL);

            informationDialogueScene.getImgImage().setImage(image);
            informationDialogueScene.getLblText().setText(text);

            informationDialogueScene.getBtnContinue().setOnAction(event -> infoDialogueStage.close());

            infoDialogueStage.showAndWait();
        }
        catch (Exception exception) {
            callback.onException(exception);
        }
    }

    private void scrollToField(SpeedyFxField speedyFxField) {
        scrollToField(speedyFxField, null);
    }

    private void scrollToField(SpeedyFxField speedyFxField, EventHandler<ActionEvent> onEndEventHandler) {
        double paneCenterX = (paneRoot.getWidth() / 2);
        double paneCenterY = (paneRoot.getHeight() / 2);

        double fieldCenterX = zoomingPane.getTranslateX() + speedyFxField.getLayoutX() + (speedyFxField.getFitWidth() / 2);
        double fieldCenterY = zoomingPane.getTranslateY() + speedyFxField.getLayoutY() + (speedyFxField.getFitWidth() / 2);

        double mapX, mapY;

        if(fieldCenterX > paneCenterX) {
            mapX = zoomingPane.getTranslateX() - (fieldCenterX - paneCenterX);
        }
        else {
            mapX = zoomingPane.getTranslateX() + (paneCenterX - fieldCenterX);
        }

        if(fieldCenterY > paneCenterY) {
            mapY = zoomingPane.getTranslateY() - (fieldCenterY - paneCenterY);
        }
        else {
            mapY = zoomingPane.getTranslateY() + (paneCenterY - fieldCenterY);
        }

        TranslateTransition translateTransition = new TranslateTransition();

        if(onEndEventHandler != null)
            translateTransition.setOnFinished(onEndEventHandler);

        translateTransition.setNode(zoomingPane);
        translateTransition.setFromX(zoomingPane.getTranslateX());
        translateTransition.setFromY(zoomingPane.getTranslateY());
        translateTransition.setToX(mapX);
        translateTransition.setToY(mapY);
        translateTransition.setCycleCount(1);
        translateTransition.setDuration(Duration.millis(1000));
        translateTransition.setInterpolator(Interpolator.EASE_BOTH);
        translateTransition.play();
    }

    public void deactivate() {
        closeRollStage();
        stopAudioPlayers();
    }

    private void closeRollStage() {
        if(rollStage != null)
            rollStage.close();
    }

    private void stopAudioPlayers() {
        isAudioActive = false;

        if(winSoundAudioPlayer != null)
            winSoundAudioPlayer.stopSounds();

        if(hedgehogSoundAudioPlayer != null)
            hedgehogSoundAudioPlayer.stopSounds();

        if(foxSoundAudioPlayer != null)
            foxSoundAudioPlayer.stopSounds();

        if(foxIsNearAudioPlayer != null)
            foxIsNearAudioPlayer.stopSounds();

        if(musicLoop1AudioPlayer != null)
            musicLoop1AudioPlayer.pause();

        if(musicLoop2AudioPlayer != null)
            musicLoop2AudioPlayer.pause();

        if(ambientAudioPlayer != null)
            ambientAudioPlayer.pause();
    }

    private void removeActivePlayer() {
        SpeedyFxField playerField = mapData.getTileFieldMapping().get(activePlayer.getCurrentTile());
        fieldSlotsMap.get(playerField).freeSlot(activePlayer);

        ImageView actorImageView = actorImageMap.get(activePlayer);
        mapData.getRootNode().getChildren().remove(actorImageView);
    }

    private void showNotificationDialog(InputStream imageStream, String text, String buttonText, EventHandler<ActionEvent> eventHandler) throws Exception {
        showNotificationDialog(imageStream, text, 15, 100, 10, buttonText, eventHandler);
    }

    private void showNotificationDialog(InputStream imageStream, String text, double fontSize, double height, double padding,
                                        String buttonText, EventHandler<ActionEvent> eventHandler) throws Exception {
        NotificationScene notificationScene = GameControllerLoader.loadController(NotificationScene.class);

        notificationScene.getImgImage().setImage(
                new Image(
                        imageStream,
                        height - padding * 2,
                height - padding * 2,
                        true,
                        true));

        notificationScene.getLblText().setText(text);

        notificationScene.getLblButtonText().setText(buttonText);

        notificationScene.getBtnConfirm().setOnAction(event -> {
            notificationScene.getBtnConfirm().setDisable(true);
            eventHandler.handle(event);
        });

        bpMap.setBottom(notificationScene.getRootNode());

        if(!isNotificationOpen)
            new FadeIn(bpMap.getBottom()).play();

        isNotificationOpen = true;
    }

    private void hideNotificationDialog(int delayMilliseconds, EventHandler<ActionEvent> eventHandler) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100));
        fadeTransition.setDelay(Duration.millis(delayMilliseconds));

        fadeTransition.setOnFinished(event -> {
            bpMap.setBottom(null);

            if(eventHandler != null)
                eventHandler.handle(event);
        });

        fadeTransition.play();

        isNotificationOpen = false;
    }

    private void performRoll(GameMapData gameMapData, AbstractGameMode gameMode, CollectablesCount collectablesCount) throws Exception {
        rollStage = new Stage();
        rollStage.setTitle(gameMapData.getResourceBundle().getString("gamemap.rollTheBall"));
        rollStage.getIcons().add(new Image(gameMapData.getResourcepack().getResourceAsStream("/figures/hedgehogBall.png")));
        rollStage.setResizable(false);

        SceneManager sceneManager = new SceneManager(rollStage);
        InteractiveBallScene interactiveBallScene = sceneManager.loadScene(InteractiveBallScene.class);

        RollProperties finalRollProperties = new RollProperties() {
            @Override
            public HedgehogIrritation getHedgehogIrritation() {
                return gameMapData.getRollProperties().getHedgehogIrritation();
            }

            @Override
            public HedgehogPhysicsProperties getHedgehogPhysicsProperties() {
                return gameMapData.getRollProperties().getHedgehogPhysicsProperties();
            }

            @Override
            public ResourceBundle getResourceBundle() {
                return gameMapData.getRollProperties().getResourceBundle();
            }

            @Override
            public Resourcepack getResourcepack() {
                return gameMapData.getRollProperties().getResourcepack();
            }

            @Override
            public CollectablesCount getCollectablesCount() {
                return collectablesCount;
            }

            @Override
            public float getEffectsBaseVolume() {
                return gameMapData.getRollProperties().getEffectsBaseVolume();
            }

            @Override
            public long getRandomSeed() {
                return gameMapData.getRandom().nextLong();
            }
        };

        RollCallback rollCallback = new SelectiveRollCallback() {
            @Override
            public void onRollIsReady() {
               if(!rollStage.isShowing()) {
                    sceneManager.switchScene(interactiveBallScene);

                    rollStage.show();
                    rollStage.setAlwaysOnTop(true);
                }
            }

            @Override
            public void onRollCompleted(CollectablesCount collectablesCount) {
               rollStage.hide();

                Roll roll = new Roll(
                        collectablesCount.getApplesCount(),
                        collectablesCount.getMushroomsCount(),
                        collectablesCount.getLeafsCount());

                gameMode.onRollCompleted(roll);
            }

            @Override
            public void onRollException(Exception exception) {
                gameMapData.getGameMapCallback().onException(exception);
            }
        };

        rollStage.setOnCloseRequest(event -> {
            event.consume();

            rollStage.hide();

            Alert alert = JavaFxDialogs.createAlertDialog(gameMapData.getResourceBundle().getString("gamemap.doYouWantToExitTheGame"),
                    gameMapData.getResourceBundle().getString("gamemap.doYouWantToExitTheGame"),
                    "",
                    Alert.AlertType.CONFIRMATION);

            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK) {
                gameMapData.getGameMapCallback().onGameOver();
            }
            else {
                rollStage.show();
            }
        });

        interactiveBallScene.doRoll(new InteractiveBallScene.Roll(finalRollProperties, rollCallback));
    }

    private void movePlayer(Player player, SpeedyFxField startField, SpeedyFxField destinationField) throws Exception {
        fieldSlotsMap.get(startField).freeSlot(player);

        ImageView actorImageView = actorImageMap.get(player);

        Point2D destinationFieldCenter = calculateCenter(destinationField);
        Point2D offset = fieldSlotsMap.get(destinationField).getSlotOffset(player);

        actorImageView.setLayoutX(destinationFieldCenter.getX() - (actorImageView.getImage().getWidth() / 2) + offset.getX());
        actorImageView.setLayoutY(destinationFieldCenter.getY() - (actorImageView.getImage().getHeight() / 2) + offset.getY());
    }

    private void moveFox(NPC fox, SpeedyFxField destinationField) {
        ImageView actorImageView = actorImageMap.get(fox);

        Point2D destinationFieldCenter = calculateCenter(destinationField);

        actorImageView.setLayoutX(destinationFieldCenter.getX() - (actorImageView.getImage().getWidth() / 2));
        actorImageView.setLayoutY(destinationFieldCenter.getY() - (actorImageView.getImage().getHeight() / 2));
    }

    private void setActivePlayerImage(Resourcepack resourcepack, Player player) {
        imgActivePlayer.setImage(actorToImage(resourcepack, player, imgActivePlayer.getFitWidth(), imgActivePlayer.getFitHeight()));
    }

    private Point2D calculateCenter(ImageView node) {
        return new Point2D(node.getLayoutX() + (node.getFitWidth() / 2), node.getLayoutY() + (node.getFitWidth() / 2));
    }

    private Image actorToImage(Resourcepack resourcepack, Actor actor, double width, double height) {
        return new Image(
                actorToImageStream(resourcepack, actor),
                width,
                imgActivePlayer.getFitWidth(),
                true, true);
    }

    private InputStream actorToImageStream(Resourcepack resourcepack, Actor actor) {
        String resourcePath = null;

        if(actor instanceof Player) {
            Player player = (Player) actor;

            if(player.getColor() == Color.BLUE) {
                resourcePath = "/figures/hedgehogPlayerBlue.png";
            }
            else if(player.getColor() == Color.GREEN) {
                resourcePath = "/figures/hedgehogPlayerGreen.png";
            }
            else if(player.getColor() == Color.RED) {
                resourcePath = "/figures/hedgehogPlayerRed.png";
            }
            else if(player.getColor() == Color.YELLOW) {
                resourcePath = "/figures/hedgehogPlayerYellow.png";
            }
        }
        else if(actor instanceof NPC) {
            resourcePath = "/figures/fox.png";
        }

        return resourcepack.getResourceAsStream(resourcePath);
    }

    private void replaceSpeedyFxFieldImage(List<SpeedyFxField> speedyFxFields, Resourcepack resourcepack) {
        speedyFxFields.forEach(tmpSpeedyFxField -> {
            switch (tmpSpeedyFxField.getFxFieldType()) {
                case START_FIELD:
                case FOX_OFFSET:
                    tmpSpeedyFxField.setImage(null);
                    break;

                case APPLE_ITEM_FIELD:
                    tmpSpeedyFxField.setImage(
                            new Image(
                                    resourcepack.getResourceAsStream("/fields/appleField.png"),
                                    tmpSpeedyFxField.getFitWidth(),
                                    tmpSpeedyFxField.getFitWidth(),
                                    true, true));
                    break;

                case LEAF_ITEM_FIELD:
                    tmpSpeedyFxField.setImage(
                            new Image(
                                    resourcepack.getResourceAsStream("/fields/leafField.png"),
                                    tmpSpeedyFxField.getFitWidth(),
                                    tmpSpeedyFxField.getFitWidth(),
                                    true, true));
                    break;

                case MUSHROOM_ITEM_FIELD:
                    tmpSpeedyFxField.setImage(
                            new Image(
                                    resourcepack.getResourceAsStream("/fields/mushroomField.png"),
                                    tmpSpeedyFxField.getFitWidth(),
                                    tmpSpeedyFxField.getFitWidth(),
                                    true, true));
                    break;

                case END_FIELD:
                    tmpSpeedyFxField.setImage(
                            new Image(
                                    resourcepack.getResourceAsStream("/fields/endField.png"),
                                    tmpSpeedyFxField.getFitWidth(),
                                    tmpSpeedyFxField.getFitWidth(),
                                    true, true));
                    break;
            }
        });
    }

    private MapData loadMapData(Path mapInfoPath) throws Exception {
        String jsonString = Files.readString(mapInfoPath);
        MapInfo mapInfo = new Gson().fromJson(jsonString, MapInfo.class);

        Path fxmlPath = mapInfoPath.getParent().resolve(mapInfo.getFxmlName());
        FXMLLoader loader = new FXMLLoader(fxmlPath.toUri().toURL());

        Parent rootNode = loader.load();

        if(!(rootNode instanceof Pane))
            throw new Exception("Unable to load map. The root node must be a instance of \"" + Pane.class.getSimpleName() + "\", " +
                    "not \"" + rootNode.getClass().getSimpleName() + "\".");

        return MapInfoParser.createMapData(mapInfo, (Pane) rootNode);
    }
}
