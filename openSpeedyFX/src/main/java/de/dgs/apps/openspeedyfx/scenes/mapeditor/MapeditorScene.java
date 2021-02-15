package de.dgs.apps.openspeedyfx.scenes.mapeditor;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import de.dgs.apps.openspeedyfx.game.mapinfo.MapInfo;
import de.dgs.apps.openspeedyfx.game.mapinfo.MapInfo.PathInfo;
import de.dgs.apps.openspeedyfx.speedyfield.SpeedyFxField;
import de.dgs.apps.openspeedyfx.speedyfield.SpeedyFxFieldTypes;
import de.dgs.apps.osfxe.gui.ClipAdaptionHelper;
import de.dgs.apps.osfxe.gui.JavaFxDialogs;
import de.dgs.apps.osfxe.gui.QuadCurveManipulationHelper;
import de.dgs.apps.osfxe.gui.zoomingpane.ZoomingPane;
import de.dgs.apps.osfxe.gui.zoomingpane.ZoomingPaneInteractionController;
import de.dgs.apps.osfxe.scenes.GameController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class MapeditorScene extends GameController {
    @FXML
    private Pane paneRoot;

    @FXML
    private TextField txtMapName;

    @FXML
    private ChoiceBox<?> choType;

    @FXML
    private TextField txtFxmlPath;

    @FXML
    private TextField txtMapType;

    @FXML
    private Button btnLoadFxml;

    @FXML
    private MenuBar mbMenu;

    @FXML
    private Button btnPathAdd;

    @FXML
    private Button btnPathEdit;

    @FXML
    private Button btnPathRemove;

    @FXML
    private Button btnPathRemoveAll;

    @FXML
    private Button btnPathAccept;

    @FXML
    private Button btnPathCancel;

    @FXML
    private Button btnCenterMap;

    @FXML
    private Label lblStatus;

    @FXML
    private VBox vbPathTools;

    @FXML
    private MenuItem miNewMap;

    @FXML
    private MenuItem miLoadMap;

    @FXML
    private MenuItem miSaveMap;

    @Override
    public String getFxmlPath() {
        return "/assets/fxml/mapeditor/mapeditor.fxml";
    }

    private enum MapState {
        NO_MAP,
        MAP_WAS_LOADED,
        ADD_MAP_PATH
    }

    private interface PathNodeSelectedCallback {
        void onPathStartSelected(SpeedyFxField startField);
    }

    private interface PathCurveSelectedCallback {
        void onPathCurveSelected(QuadCurve quadCurve);
    }

    private static class PathNodeMapping {
        private final SpeedyFxField startNode;
        private final SpeedyFxField endNode;
        private final QuadCurve quadCurve;

        public PathNodeMapping(SpeedyFxField startNode, SpeedyFxField endNode, QuadCurve quadCurve) {
            this.startNode = startNode;
            this.endNode = endNode;
            this.quadCurve = quadCurve;
        }

        public SpeedyFxField getStartNode() {
            return startNode;
        }

        public SpeedyFxField getEndNode() {
            return endNode;
        }

        public QuadCurve getQuadCurve() {
            return quadCurve;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathNodeMapping that = (PathNodeMapping) o;
            return startNode.equals(that.startNode) && endNode.equals(that.endNode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startNode, endNode, quadCurve);
        }
    }

    //Map info.
    private boolean isMapCooperativeMode;
    private String lastMapDirectoryPath;

    //General.
    private Faker faker;
    private SimpleObjectProperty<MapState> mapStateProperty;

    //Actual map.
    private File fxmlFile;
    private Pane rootNode;
    private ClipAdaptionHelper clipAdaptionHelper;
    private ZoomingPane zoomingPane;
    private ZoomingPaneInteractionController sceneInteractionController;

    //Path variables.
    private boolean isIndividualPathSelectionActive;

    private QuadCurveManipulationHelper quadCurveManipulationHelper;
    private SpeedyFxField pathStartNodeToAdd;
    private SpeedyFxField pathEndNodeToAdd;
    private QuadCurve quadCurveToAdd;
    private final List<SpeedyFxField> selectedPathNodeFields = new LinkedList<>();

    private final List<PathNodeMapping> pathNodeMappings = new LinkedList<>();

    private double originalControlX;
    private double originalControlY;
    private QuadCurve selectedQuadCurve;

    @Override
    public void onInitialized() {
        setupZoomingPane();
        setupClipAdaption();
        setupMapState();
    }

    private void setupZoomingPane() {
        zoomingPane = new ZoomingPane();
        paneRoot.getChildren().add(zoomingPane);

        sceneInteractionController = new ZoomingPaneInteractionController(
                zoomingPane,
                true,
                true,
                ZoomingPaneInteractionController.DEFAULT_MIN_SCALE,
                1);

        sceneInteractionController.registerEventFilters(zoomingPane);
    }

    private void setupClipAdaption() {
        clipAdaptionHelper = new ClipAdaptionHelper();
        clipAdaptionHelper.bindClipAdaption(paneRoot);
    }

    private void setupMapState() {
        mapStateProperty = new SimpleObjectProperty<>();
        mapStateProperty.set(MapState.NO_MAP);
    }

    public void setupMapEditor(ResourceBundle resourceBundle) {
        setupMapEditor(resourceBundle, null);
    }

    public void setupMapEditor(ResourceBundle resourceBundle, File defaultDirectory) {
        if(defaultDirectory != null && defaultDirectory.isDirectory())
            lastMapDirectoryPath = defaultDirectory.getAbsolutePath();

        faker = new Faker(resourceBundle.getLocale());

        //Setup of UI logic.
        btnLoadFxml.setOnAction(mouseEvent -> {
            onLoadFxml(resourceBundle);
        });

        miNewMap.setOnAction(event -> {
            mapStateProperty.set(MapState.NO_MAP);
        });

        miLoadMap.setOnAction(event -> {
            onLoadMap(resourceBundle);
        });

        miSaveMap.setOnAction(event -> {
            onSaveMap(resourceBundle);
        });

        mapStateProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue == MapState.NO_MAP) {
                reset();
            }
            else if(newValue == MapState.MAP_WAS_LOADED) {
                disableMapUi(false);
            }
            else if(newValue == MapState.ADD_MAP_PATH) {
                disablePathManipulationUi(false);
            }
        });

        btnPathAdd.setOnAction(event -> {
            onPathAdd(resourceBundle);
        });

        btnPathEdit.setOnAction(event -> {
            onPathEdit(resourceBundle);
        });

        btnPathRemove.setOnAction(event -> {
            onPathRemove(resourceBundle);
        });

        btnPathRemoveAll.setOnAction(event -> {
            onPathRemoveAll(resourceBundle);
        });

        btnPathAccept.setOnAction(event -> {
            onPathAccept();
        });

        btnPathCancel.setOnAction(event -> {
            onPathCancel();
        });

        btnCenterMap.setOnAction(event -> {
            centerMap();
        });
    }

    private void onSaveMap(ResourceBundle resourceBundle) {
        String mapName = txtMapName.getText();

        if(mapName.isEmpty()) {
            Alert alert = JavaFxDialogs.createAlertDialog(
                    resourceBundle.getString("mapeditor.unableToSaveMap"),
                    resourceBundle.getString("mapeditor.unableToSaveMap"),
                    resourceBundle.getString("mapeditor.pleaseSetAMapName"),
                    Alert.AlertType.ERROR);

            alert.showAndWait();
        }
        else {
            try {
                List<PathInfo> pathInfos = new LinkedList<>();

                pathNodeMappings.forEach(tmpMapping -> {
                    pathInfos.add(pathNodeMappingToPathInfo(tmpMapping));
                });

                MapInfo mapInfo = new MapInfo(mapName, !isMapCooperativeMode, fxmlFile.getName(), pathInfos);
                String mapInfoJson = new Gson().toJson(mapInfo);

                String pureFxmlName = fxmlFile.getName().replace(".FXML", "").replace(".fxml", "");
                File jsonFile = new File(fxmlFile.getAbsolutePath().replace(fxmlFile.getName(), pureFxmlName + ".mapj"));

                FileWriter jsonFileWriter = new FileWriter(jsonFile, false);
                jsonFileWriter.write(mapInfoJson);
                jsonFileWriter.flush();
                jsonFileWriter.close();

                Alert alert = JavaFxDialogs.createAlertDialog(
                        resourceBundle.getString("mapeditor.theMapWasSavedSuccessfully"),
                        resourceBundle.getString("mapeditor.theMapWasSavedSuccessfully"),
                        jsonFile.getAbsolutePath(),
                        Alert.AlertType.INFORMATION);

                alert.showAndWait();
            }
            catch (Exception exception) {
                Alert alert = JavaFxDialogs.createExceptionDialog(
                        resourceBundle.getString("mapeditor.unableToSaveMap"),
                        resourceBundle.getString("mapeditor.unableToSaveMap"),
                        resourceBundle.getString("mapeditor.theFollowingErrorOccurred"),
                        exception);

                alert.showAndWait();
            }
        }
    }

    private MapInfo.PathInfo pathNodeMappingToPathInfo(PathNodeMapping pathNodeMapping) {
        return new MapInfo.PathInfo(
                new MapInfo.FieldInfo(
                        pathNodeMapping.getStartNode().getFieldId(),
                        pathNodeMapping.getStartNode().getFxFieldType().name()),
                new MapInfo.FieldInfo(
                        pathNodeMapping.getEndNode().getFieldId(),
                        pathNodeMapping.getEndNode().getFxFieldType().name()),
                pathNodeMapping.getQuadCurve().getControlX(),
                pathNodeMapping.getQuadCurve().getControlY()
        );
    }

    private void onLoadMap(ResourceBundle resourceBundle) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(resourceBundle.getString("mapeditor.chooseFxmlFile"));

            File directoryFile = new File(System.getProperty("user.home"));
            if(lastMapDirectoryPath != null) {
                File lastDirectory = new File(lastMapDirectoryPath);

                if(lastDirectory.isDirectory())
                    directoryFile = new File(lastMapDirectoryPath);
            }

            fileChooser.setInitialDirectory(directoryFile);
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MAPJ", "*.mapj"));

            File jsonFile = fileChooser.showOpenDialog(null);

            if(jsonFile != null) {
                lastMapDirectoryPath = jsonFile.getParentFile().getAbsolutePath();

                String jsonString = Files.readString(jsonFile.toPath());
                MapInfo mapInfo = new Gson().fromJson(jsonString, MapInfo.class);

                File fxmlFile = new File(jsonFile.getParentFile().getAbsolutePath() + "/" + mapInfo.getFxmlName());

                if(!fxmlFile.isFile()) {
                    Alert alert = JavaFxDialogs.createAlertDialog(
                            resourceBundle.getString("mapeditor.unableToLoadMapFile"),
                            resourceBundle.getString("mapeditor.unableToLoadMapFile"),
                            resourceBundle.getString("mapeditor.unableToFindFxmlFile") +
                                    " (" + fxmlFile.getAbsolutePath() + ")",
                            Alert.AlertType.ERROR);

                    alert.showAndWait();
                }
                else {
                    loadMap(resourceBundle, fxmlFile);
                    populateLoadedMap(resourceBundle, mapInfo);
                    disablePathManipulationUi(true);
                }
            }
        }
        catch(Exception exception) {
            Alert alert = JavaFxDialogs.createExceptionDialog(
                    resourceBundle.getString("mapeditor.unableToLoadMapFile"),
                    resourceBundle.getString("mapeditor.unableToLoadMapFile"),
                    resourceBundle.getString("mapeditor.theFollowingErrorOccurred"),
                    exception);

            alert.showAndWait();
        }
    }

    private void populateLoadedMap(ResourceBundle resourceBundle, MapInfo mapInfo) {
        List<String> warnings = new LinkedList<>();
        List<PathNodeMapping> newPathNodeMappings = new LinkedList<>();

        if(isMapCooperativeMode == mapInfo.isCompetitive())
            warnings.add(resourceBundle.getString("mapeditor.theGameModeOfTheFxmlFileDoesntMatchTheMapData"));

        List<SpeedyFxField> speedyFxFields = readAvailableSpeedyFxFields();

        for(MapInfo.PathInfo tmpPathInfo : mapInfo.getPaths()) {
            SpeedyFxField startNode = findSpeedyFxField(speedyFxFields, tmpPathInfo.getStartFieldInfo().getFieldId());

            if(startNode == null) {
                warnings.add(resourceBundle.getString("mapeditor.unableToFindSpeedyFxFieldInFxmlFile") +
                        " Id: " + tmpPathInfo.getStartFieldInfo().getFieldId() + "\n");

                continue;
            }

            if(!startNode.getFxFieldType().name().equals(tmpPathInfo.getStartFieldInfo().getFieldType())) {
                warnings.add(resourceBundle.getString("mapeditor.unableToAddSpeedyFxFieldTheTypeInTheFxmlFileDoesntMatch") +
                        "\n\nMap (" + tmpPathInfo.getStartFieldInfo().getFieldId() +  ")" +
                        " / FXML (" + startNode.getFieldId() + "):\n" +
                        tmpPathInfo.getStartFieldInfo().getFieldType() + "/" + startNode.getFxFieldType().name() + "\n");

                continue;
            }

            SpeedyFxField endNode = findSpeedyFxField(speedyFxFields, tmpPathInfo.getEndFieldInfo().getFieldId());

            if(endNode == null) {
                warnings.add(resourceBundle.getString("mapeditor.unableToFindSpeedyFxFieldInFxmlFile") +
                        " Id: " + tmpPathInfo.getEndFieldInfo().getFieldId() + "\n");

                continue;
            }

            if(!endNode.getFxFieldType().name().equals(tmpPathInfo.getEndFieldInfo().getFieldType())) {
                warnings.add(resourceBundle.getString("mapeditor.unableToAddSpeedyFxFieldTheTypeInTheFxmlFileDoesntMatch") +
                        "\n\nMap (" + tmpPathInfo.getEndFieldInfo().getFieldId() +  ")" +
                        " / FXML (" + endNode.getFieldId() + "):\n" +
                        tmpPathInfo.getEndFieldInfo().getFieldType() + "/" + endNode.getFxFieldType().name() + "\n");

                continue;
            }

            QuadCurve quadCurve = createDefaultQuadCurve();

            quadCurve.setStartX(startNode.getLayoutX() + (startNode.getFitWidth() / 2));
            quadCurve.setStartY(startNode.getLayoutY() + (startNode.getFitWidth() / 2));

            quadCurve.setEndX(endNode.getLayoutX() + (endNode.getFitWidth() / 2));
            quadCurve.setEndY(endNode.getLayoutY() + (endNode.getFitWidth() / 2));

            quadCurve.setControlX(tmpPathInfo.getControlX());
            quadCurve.setControlY(tmpPathInfo.getControlY());

            newPathNodeMappings.add(new PathNodeMapping(startNode, endNode, quadCurve));
        }

        boolean loadMap = true;

        if(!warnings.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            for(String tmpWarning : warnings)
                stringBuilder.append(tmpWarning).append("\n");

            Alert alert = JavaFxDialogs.createAlertDialog(
                    resourceBundle.getString("mapeditor.proceedWithWarnings"),
                    resourceBundle.getString("mapeditor.theFollowingWarningsOccurredWhileReadingTheMapData"),
                    stringBuilder.toString(),
                    Alert.AlertType.CONFIRMATION);

            Optional<ButtonType> result = alert.showAndWait();
            loadMap = result.get() == ButtonType.OK;
        }

        if(loadMap) {
            txtMapName.setText(mapInfo.getMapName());

            for(PathNodeMapping tmpPathNodeMapping : newPathNodeMappings) {
                rootNode.getChildren().add(tmpPathNodeMapping.getQuadCurve());
                pathNodeMappings.add(tmpPathNodeMapping);
            }
        }
    }

    private SpeedyFxField findSpeedyFxField(List<SpeedyFxField> speedyFxFields, String fieldId) {
        SpeedyFxField foundField = null;

        for(int tmpIndex = 0; tmpIndex < speedyFxFields.size(); tmpIndex++) {
            SpeedyFxField tmpSpeedyFxField = speedyFxFields.get(tmpIndex);

            if(tmpSpeedyFxField.getFieldId().equals(fieldId)) {
                foundField = tmpSpeedyFxField;
                break;
            }
        }

        return foundField;
    }

    private void onPathAdd(ResourceBundle resourceBundle) {
        isIndividualPathSelectionActive = false;
        disablePathManipulationUi(false);

        List<SpeedyFxField> speedyFxFields = readAvailableSpeedyFxFields();

        lblStatus.setText(resourceBundle.getString("mapeditor.pleaseSelectAStartNodeOrCancel"));

        selectPathNode(speedyFxFields, List.of(SpeedyFxFieldTypes.END_FIELD), Color.GREENYELLOW, startField -> {
            lblStatus.setText(resourceBundle.getString("mapeditor.pleaseSelectAEndNodeOrCancel"));

            List<SpeedyFxFieldTypes> forbiddenFieldTypes = new LinkedList<>();

            if(startField.getFxFieldType() != SpeedyFxFieldTypes.FOX_OFFSET) {
                forbiddenFieldTypes.add(SpeedyFxFieldTypes.FOX_OFFSET);

                if(startField.getFxFieldType() != SpeedyFxFieldTypes.START_FIELD) {
                    forbiddenFieldTypes.add(SpeedyFxFieldTypes.START_FIELD);
                }
                else {
                    forbiddenFieldTypes.add(SpeedyFxFieldTypes.END_FIELD);
                }
            }
            else {
                forbiddenFieldTypes.addAll(List.of(
                        SpeedyFxFieldTypes.APPLE_ITEM_FIELD,
                        SpeedyFxFieldTypes.LEAF_ITEM_FIELD,
                        SpeedyFxFieldTypes.MUSHROOM_ITEM_FIELD,
                        SpeedyFxFieldTypes.RANDOM_ITEM_FIELD,
                        SpeedyFxFieldTypes.END_FIELD));
            }

            List<SpeedyFxField> forbiddenEndFields = new LinkedList<>();

            pathNodeMappings.forEach(pathNodeMapping -> {
                if(startField == pathNodeMapping.getStartNode()) {
                    forbiddenEndFields.add(pathNodeMapping.getEndNode());
                }
                else if(startField == pathNodeMapping.getEndNode()) {
                    forbiddenEndFields.add(pathNodeMapping.getStartNode());
                }
            });

            selectPathNode(speedyFxFields, forbiddenFieldTypes, forbiddenEndFields, Color.GREENYELLOW, endField -> {
                resetPathSelection();

                lblStatus.setText(resourceBundle.getString("mapeditor.pleaseModifyThePathCurve"));

                QuadCurve quadCurve = createDefaultQuadCurve();

                quadCurve.setStartX(startField.getLayoutX() + (startField.getFitWidth() / 2));
                quadCurve.setStartY(startField.getLayoutY() + (startField.getFitWidth() / 2));

                quadCurve.setEndX(endField.getLayoutX() + (endField.getFitWidth() / 2));
                quadCurve.setEndY(endField.getLayoutY() + (endField.getFitWidth() / 2));

                quadCurve.setControlX(startField.getLayoutX() + (startField.getFitWidth() / 2));
                quadCurve.setControlY(startField.getLayoutY() + (startField.getFitWidth() / 2));

                rootNode.getChildren().add(quadCurve);

                quadCurveManipulationHelper = new QuadCurveManipulationHelper(rootNode.getChildren());
                quadCurveManipulationHelper.add(quadCurve);

                quadCurveToAdd = quadCurve;
                pathStartNodeToAdd = startField;
                pathEndNodeToAdd = endField;

                btnPathAccept.setDisable(false);
            });
        });
    }

    private void onPathEdit(ResourceBundle resourceBundle) {
        isIndividualPathSelectionActive = true;

        disablePathManipulationUi(false);
        lblStatus.setText(resourceBundle.getString("mapeditor.pleaseSelectAQuadCurveElement"));

        selectIndividualPathCurve(Color.GREENYELLOW, quadCurve -> {
            originalControlX = quadCurve.getControlX();
            originalControlY = quadCurve.getControlY();
            selectedQuadCurve = quadCurve;

            quadCurveManipulationHelper = new QuadCurveManipulationHelper(rootNode.getChildren());
            quadCurveManipulationHelper.add(quadCurve);

            btnPathAccept.setDisable(false);

            lblStatus.setText(resourceBundle.getString("mapeditor.pleaseModifyThePathCurve"));
        });
    }

    private void onPathRemove(ResourceBundle resourceBundle) {
        isIndividualPathSelectionActive = true;
        disablePathManipulationUi(false);

        lblStatus.setText(resourceBundle.getString("mapeditor.pleaseSelectAQuadCurveElement"));

        selectIndividualPathCurve(Color.GREENYELLOW, quadCurve -> {
            for(PathNodeMapping tmpPathNodeMapping : pathNodeMappings) {
                if(tmpPathNodeMapping.getQuadCurve() == quadCurve) {
                    rootNode.getChildren().remove(quadCurve);
                    pathNodeMappings.remove(tmpPathNodeMapping);

                    disablePathManipulationUi(true);
                    break;
                }
            }
        });
    }

    private void onPathRemoveAll(ResourceBundle resourceBundle) {
        Alert alert = JavaFxDialogs.createAlertDialog(
                resourceBundle.getString("mapeditor.pleaseConfirmTheDeletion"),
                resourceBundle.getString("mapeditor.doYouReallyWantToDeleteAllPaths"),
                resourceBundle.getString("mapeditor.pleaseConfirmTheDeletion"),
                Alert.AlertType.CONFIRMATION
        );

        Optional<ButtonType> dialogResult = alert.showAndWait();

        if(dialogResult.get() == ButtonType.OK) {
            for(PathNodeMapping tmpPathNodeMapping : pathNodeMappings)
                rootNode.getChildren().remove(tmpPathNodeMapping.getQuadCurve());

            pathNodeMappings.clear();
        }

        disablePathManipulationUi(true);
    }

    private void selectIndividualPathCurve(Color selectionColor, PathCurveSelectedCallback callback) {
        List<QuadCurve> quadCurves = readAvailablePathCurves();

        for(QuadCurve tmpQuadCurve : quadCurves) {
            Effect originalEffect = tmpQuadCurve.getEffect();

            tmpQuadCurve.setOnMouseEntered(event -> {
                tmpQuadCurve.setEffect(createSelectionDropShadow(selectionColor));
            });

            tmpQuadCurve.setOnMouseExited(event -> {
                tmpQuadCurve.setEffect(originalEffect);
            });

            tmpQuadCurve.setOnMouseClicked(event -> {
                resetQuadCurvesSelection();
                callback.onPathCurveSelected(tmpQuadCurve);
            });
        }
    }

    private List<QuadCurve> readAvailablePathCurves() {
        List<QuadCurve> quadCurves = new LinkedList<>();

        pathNodeMappings.forEach(pathNodeMapping -> quadCurves.add(pathNodeMapping.getQuadCurve()));

        return quadCurves;
    }

    private void onPathAccept() {
        if(!isIndividualPathSelectionActive) {
            pathNodeMappings.add(new PathNodeMapping(pathStartNodeToAdd, pathEndNodeToAdd, quadCurveToAdd));

            pathEndNodeToAdd = null;
            quadCurveToAdd = null;
        }

        quadCurveManipulationHelper.removeAll();
        disablePathManipulationUi(true);
    }

    private void onPathCancel() {
        if(isIndividualPathSelectionActive) {
            if(selectedQuadCurve != null) {
                selectedQuadCurve.setControlX(originalControlX);
                selectedQuadCurve.setControlY(originalControlY);
            }

            resetQuadCurvesSelection();
        }
        else {
            resetPathSelection();

            if(quadCurveToAdd != null)
                rootNode.getChildren().remove(quadCurveToAdd);
        }

        if(quadCurveManipulationHelper != null)
            quadCurveManipulationHelper.removeAll();

        lblStatus.setText("-");
        btnPathAccept.setDisable(true);
        disablePathManipulationUi(true);
    }

    private void resetQuadCurvesSelection() {
        readAvailablePathCurves().forEach(quadCurve -> {
            resetSelectableNodeEvents(quadCurve);
            quadCurve.setEffect(createQuadCurveShadow());
        });
    }

    private QuadCurve createDefaultQuadCurve() {
        QuadCurve quadCurve = new QuadCurve();
        quadCurve.setFill(Color.TRANSPARENT);

        LinearGradient strokeGradient = new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 166, 0)),
                new Stop(0.5, Color.rgb(255, 206, 80)),
                new Stop(1, Color.rgb(255, 166, 80)));

        quadCurve.setStroke(strokeGradient);
        quadCurve.setStrokeWidth(3);
        quadCurve.setStrokeType(StrokeType.CENTERED);
        quadCurve.setStrokeLineCap(StrokeLineCap.ROUND);
        quadCurve.setStrokeLineJoin(StrokeLineJoin.MITER);
        quadCurve.setStrokeMiterLimit(10);
        quadCurve.setStrokeDashOffset(0);

        quadCurve.setEffect(createQuadCurveShadow());

        return quadCurve;
    }

    private DropShadow createQuadCurveShadow() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.BLACK);
        dropShadow.setWidth(7);
        dropShadow.setHeight(7);
        dropShadow.setRadius(3);

        return dropShadow;
    }

    private void resetPathSelection() {
        selectedPathNodeFields.clear();
        readAvailableSpeedyFxFields().forEach(this::resetSelectableNode);
    }

    private List<SpeedyFxField> readAvailableSpeedyFxFields() {
        List<SpeedyFxField> speedyFxFields = new LinkedList<>();

        for(Node tmpNode : rootNode.getChildrenUnmodifiable()) {
            if(tmpNode instanceof SpeedyFxField)
                speedyFxFields.add((SpeedyFxField) tmpNode);
        }

        return speedyFxFields;
    }

    private void selectPathNode(List<SpeedyFxField> speedyFxFields, List<SpeedyFxFieldTypes> forbiddenFieldTypes,
                                Color selectionColor, PathNodeSelectedCallback callback) {
        selectPathNode(speedyFxFields, forbiddenFieldTypes, new ArrayList<>(0), selectionColor, callback);
    }

    private void selectPathNode(List<SpeedyFxField> speedyFxFields, List<SpeedyFxFieldTypes> forbiddenFieldTypes,
                                List<SpeedyFxField> forbiddenFields, Color selectionColor, PathNodeSelectedCallback callback) {
        DropShadow dropShadow = createSelectionDropShadow(selectionColor);

        for(SpeedyFxField tmpSpeedyFxField : speedyFxFields) {
            if(selectedPathNodeFields.contains(tmpSpeedyFxField))
                continue;

            double originalOpacity = tmpSpeedyFxField.getOpacity();

            if(!forbiddenFields.contains(tmpSpeedyFxField) && !forbiddenFieldTypes.contains(tmpSpeedyFxField.getFxFieldType())) {
                tmpSpeedyFxField.setOnMouseEntered(mouseEvent -> {
                    tmpSpeedyFxField.setEffect(dropShadow);
                    tmpSpeedyFxField.setOpacity(1);
                });

                tmpSpeedyFxField.setOnMouseExited(mouseEvent -> {
                    if(selectedPathNodeFields.contains(tmpSpeedyFxField))
                        return;

                    tmpSpeedyFxField.setEffect(null);
                    tmpSpeedyFxField.setOpacity(originalOpacity);
                });

                tmpSpeedyFxField.setOnMouseClicked(mouseEvent -> {
                    tmpSpeedyFxField.setOpacity(originalOpacity);

                    selectedPathNodeFields.add(tmpSpeedyFxField);

                    speedyFxFields.forEach(speedyFxField -> {
                        if(speedyFxField != tmpSpeedyFxField) {
                            resetSelectableNode(speedyFxField);
                        }
                        else {
                            resetSelectableNodeEvents(speedyFxField);
                        }
                    });

                    callback.onPathStartSelected(tmpSpeedyFxField);
                });
            }
        }
    }

    private DropShadow createSelectionDropShadow(Color selectionColor) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(selectionColor);
        dropShadow.setOffsetX(0f);
        dropShadow.setOffsetY(0f);
        dropShadow.setWidth(100);
        dropShadow.setHeight(100);
        dropShadow.setRadius(50);
        dropShadow.setSpread(0.5);

        return dropShadow;
    }

    private void resetSelectableNode(Node node) {
        resetSelectableNodeEvents(node);
        resetSelectableNodeEffects(node);
    }

    private void resetSelectableNodeEvents(Node node) {
        node.setOnMouseEntered(null);
        node.setOnMouseExited(null);
        node.setOnMouseClicked(null);
    }

    private void resetSelectableNodeEffects(Node node) {
        node.setEffect(null);
    }

    private void disablePathManipulationUi(boolean disable) {
        sceneInteractionController.setDisabled(!disable);

        btnPathAdd.setDisable(!disable);

        if(disable && pathNodeMappings.isEmpty()) {
            btnPathEdit.setDisable(true);
            btnPathRemove.setDisable(true);
            btnPathRemoveAll.setDisable(true);
        }
        else {
            btnPathEdit.setDisable(!disable);
            btnPathRemove.setDisable(!disable);
            btnPathRemoveAll.setDisable(!disable);
        }

        btnPathAccept.setDisable(true);
        btnPathCancel.setDisable(disable);

        btnCenterMap.setDisable(!disable);

        mbMenu.setDisable(!disable);
        btnLoadFxml.setDisable(!disable);
        txtMapName.setDisable(!disable);

        lblStatus.setText("-");
    }

    private void centerMap() {
        double paneCenterX = (paneRoot.getWidth() / 2);
        double paneCenterY = (paneRoot.getHeight() / 2);

        double mapCenterX = zoomingPane.getTranslateX() + (zoomingPane.getWidth() / 2);
        double mapCenterY = zoomingPane.getTranslateY() + (zoomingPane.getHeight() / 2);

        double mapX, mapY;

        if(mapCenterX > paneCenterX) {
            mapX = zoomingPane.getTranslateX() - (mapCenterX - paneCenterX);
        }
        else {
            mapX = zoomingPane.getTranslateX() + (paneCenterX - mapCenterX);
        }

        if(mapCenterY > paneCenterY) {
            mapY = zoomingPane.getTranslateY() - (mapCenterY - paneCenterY);
        }
        else {
            mapY = zoomingPane.getTranslateY() + (paneCenterY - mapCenterY);
        }

        zoomingPane.setTranslateX(mapX);
        zoomingPane.setTranslateY(mapY);
    }

    private void disableMapUi(boolean disable) {
        miSaveMap.setDisable(disable);

        vbPathTools.setDisable(disable);
        btnPathAdd.setDisable(disable);

        btnCenterMap.setDisable(disable);

        txtMapName.setDisable(disable);
    }

    private void reset() {
        disablePathManipulationUi(true);
        disableMapUi(true);

        txtMapType.setText("");
        txtFxmlPath.setText("");
        txtMapName.setText("");

        zoomingPane.getChildren().clear();
        pathNodeMappings.clear();
    }

    private void onLoadFxml(ResourceBundle resourceBundle) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(resourceBundle.getString("mapeditor.chooseFxmlFile"));

            File directoryFile = new File(System.getProperty("user.home"));
            if(lastMapDirectoryPath != null) {
                File lastDirectory = new File(lastMapDirectoryPath);

                if(lastDirectory.isDirectory())
                    directoryFile = new File(lastMapDirectoryPath);
            }

            fileChooser.setInitialDirectory(directoryFile);
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FXML", "*.fxml"));

            File fxmlFile = fileChooser.showOpenDialog(null);

            if(fxmlFile != null) {
                lastMapDirectoryPath = fxmlFile.getParentFile().getAbsolutePath();
                fixFxmlSpeedyFxFieldId(fxmlFile);
                loadMap(resourceBundle, fxmlFile);
            }
        }
        catch(Exception exception) {
            Alert alert = JavaFxDialogs.createExceptionDialog(
                    resourceBundle.getString("mapeditor.unableToOpenFxmlFile"),
                    resourceBundle.getString("mapeditor.unableToOpenFxmlFile"),
                    resourceBundle.getString("mapeditor.theFollowingErrorOccurred"),
                    exception);

            alert.showAndWait();
        }
    }

    private void loadMap(ResourceBundle resourceBundle, File fxmlFile) throws Exception {
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());

        Parent rootNode = loader.load();

        if(!(rootNode instanceof Pane))
            throw new Exception("Unable to load map. The root node must be a instance of \"Pane\", " +
                    "not \"" + rootNode.getClass().getSimpleName() + "\".");

        boolean isMapCooperativeMode = false;

        int startNodesCount = 0;
        int endNodesCount = 0;
        boolean mapHasItemField = false;

        for(Node tmpNode : rootNode.getChildrenUnmodifiable()) {
            if(tmpNode instanceof SpeedyFxField) {
                SpeedyFxField speedyFxField = (SpeedyFxField) tmpNode;

                if(!isMapCooperativeMode && speedyFxField.getFxFieldType() == SpeedyFxFieldTypes.FOX_OFFSET) {
                    isMapCooperativeMode = true;
                }

                if(speedyFxField.getFxFieldType() == SpeedyFxFieldTypes.END_FIELD) {
                    endNodesCount++;
                }

                if(speedyFxField.getFxFieldType() == SpeedyFxFieldTypes.START_FIELD) {
                    startNodesCount++;
                }

                if(!mapHasItemField) {
                    if(speedyFxField.getFxFieldType() == SpeedyFxFieldTypes.APPLE_ITEM_FIELD) {
                        mapHasItemField = true;
                    }
                    else if(speedyFxField.getFxFieldType() == SpeedyFxFieldTypes.MUSHROOM_ITEM_FIELD) {
                        mapHasItemField = true;
                    }
                    else if(speedyFxField.getFxFieldType() == SpeedyFxFieldTypes.LEAF_ITEM_FIELD) {
                        mapHasItemField = true;
                    }
                    else if(speedyFxField.getFxFieldType() == SpeedyFxFieldTypes.RANDOM_ITEM_FIELD) {
                        mapHasItemField = true;
                    }
                }
            }
        }

        List<String> errors = new LinkedList<>();

        if(startNodesCount == 0) {
            errors.add(resourceBundle.getString("mapeditor.theMapHasNoStartNode"));
        }

        if(endNodesCount == 0) {
            errors.add(resourceBundle.getString("mapeditor.theMapHasNoEndNode"));
        }

        if(startNodesCount > 1) {
            errors.add(resourceBundle.getString("mapeditor.theMapHasMultipleStartNodes"));
        }

        if(endNodesCount > 1) {
            errors.add(resourceBundle.getString("mapeditor.theMapHasMultipleEndNodes"));
        }

        if(!mapHasItemField) {
            errors.add(resourceBundle.getString("mapeditor.theMapHasNoItemNodes"));
        }

        if(!errors.isEmpty()) {
            StringBuilder errorMessagesStringBuilder = new StringBuilder();

            for(byte errorIndex = 0; errorIndex < errors.size(); errorIndex++) {
                if(errorIndex > 0)
                    errorMessagesStringBuilder.append("\n");

                errorMessagesStringBuilder.append(errors.get(errorIndex));
            }

            Alert alert = JavaFxDialogs.createAlertDialog(resourceBundle.getString("mapeditor.unableToLoadMap"),
                    resourceBundle.getString("mapeditor.theLoadedMapStructureIsInvalid"),
                    errorMessagesStringBuilder.toString(),
                    Alert.AlertType.ERROR);

            alert.showAndWait();
        }
        else {
            mapStateProperty.set(MapState.NO_MAP);

            this.isMapCooperativeMode = isMapCooperativeMode;
            zoomingPane.getChildren().add(rootNode);

            if(isMapCooperativeMode) {
                txtMapType.setText(resourceBundle.getString("mapeditor.cooperativeMap"));
            }
            else {
                txtMapType.setText(resourceBundle.getString("mapeditor.competitiveMap"));
            }

            txtMapName.setText(faker.funnyName().name().replace(" ", ""));
            txtFxmlPath.setText(fxmlFile.getAbsolutePath());

            this.rootNode = (Pane) rootNode;
            this.fxmlFile = fxmlFile;

            mapStateProperty.set(MapState.MAP_WAS_LOADED);
        }
    }

    private void fixFxmlSpeedyFxFieldId(File fxmlFile) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        //Load FXML file.
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = documentBuilder.parse(fxmlFile);
        document.getDocumentElement().normalize();

        NodeList nodeList = document.getElementsByTagName(SpeedyFxField.XML_TAG);

        int lastFieldId = 0;

        for(int tmpNodeId = 0; tmpNodeId < nodeList.getLength(); tmpNodeId++) {
            org.w3c.dom.Node tmpFieldNode = nodeList.item(tmpNodeId);

            if (tmpFieldNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element tmpElement = (Element) tmpFieldNode;

                if(!tmpElement.hasAttribute(SpeedyFxField.XML_ATTRIBUTE_FIELD_ID))
                    tmpElement.setAttribute(SpeedyFxField.XML_ATTRIBUTE_FIELD_ID, SpeedyFxField.createRandomFieldId() + lastFieldId++);
            }
        }

        //Save edited file.
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(document);
        FileOutputStream fileOutputStream = new FileOutputStream(fxmlFile);
        StreamResult streamResult = new StreamResult(fileOutputStream);
        transformer.transform(source, streamResult);
    }
}
