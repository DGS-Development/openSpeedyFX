package de.dgs.apps.openspeedyfx.scenes.ballscene;

import animatefx.animation.RotateIn;
import de.dgs.apps.openspeedyfx.game.resourcepacks.Resourcepack;
import de.dgs.apps.openspeedyfx.game.resourcepacks.ResourcepackPaths;
import de.dgs.apps.openspeedyfx.game.resourcepacks.ResourcepackPaths.Textures.BallScene;
import de.dgs.apps.osfxe.scenes.GameController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

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

public abstract class AbstractBallScene extends GameController {
    @FXML
    protected AnchorPane apRoot;

    @FXML
    protected Pane paneBorderLeft;

    @FXML
    protected Pane paneBorderRight;

    @FXML
    protected Pane paneBorderTop;

    @FXML
    protected Pane paneBorderBottom;

    @FXML
    protected Pane paneFloor;

    @FXML
    protected ProgressBar pbPower;

    @FXML
    protected ImageView imgDirectionArrow;

    @FXML
    protected ImageView imgHedgehog;

    @FXML
    protected ImageView imgHedgehogInstructor;

    @FXML
    protected Label lblCollectedApplesCount;

    @FXML
    protected Label lblCollectedLeavesCount;

    @FXML
    protected Label lblCollectedMushroomsCount;

    @FXML
    protected Label lblInstructions;

    @FXML
    protected Label lblPower;

    @FXML
    protected Label lblCollectedItems;

    @FXML
    protected ImageView imgCollectedApples;

    @FXML
    protected ImageView imgCollectedLeaves;

    @FXML
    protected ImageView imgCollectedMushrooms;

    @Override
    public String getFxmlPath() {
        return "/assets/fxml/ballscene/ballscene.fxml";
    }

    private static final String LOCALIZATION_POWER_LABEL = "ballscene.powerLabel";
    private static final String LOCALIZATION_COLLECTED_ITEMS_LABEL = "ballscene.collectedLabel";

    protected static final String LOCALIZATION_HEDGEHOG_POSITION = "ballscene.instructions.setHedgehogPosition";
    protected static final String LOCALIZATION_HEDGEHOG_PUSH_DIRECTION = "ballscene.instructions.setHedgehogPushDirection";
    protected static final String LOCALIZATION_HEDGEHOG_PUSH_FORCE = "ballscene.instructions.setHedgehogPushForce";
    protected static final String LOCALIZATION_HEDGEHOG_WOOSH = "ballscene.instructions.woosh";

    protected static final int MENU_COLLECTED_ITEMS_FIT_WIDTH = 30;
    protected static final int COLLECTABLES_BORDER_PADDING = 70;
    protected static final int COLLECTABLES_ITEM_PADDING = 40;
    protected static final int COLLECTABLES_ITEM_FIT_WIDTH = 45;

    protected SimpleIntegerProperty lblCollectedApplesCountProperty;
    protected SimpleIntegerProperty lblCollectedLeavesCountProperty;
    protected SimpleIntegerProperty lblCollectedMushroomsCountProperty;

    private static final double SCENE_LABEL_FONTSIZE = 14.5;

    @Override
    public void onInitialized() {
        //Setup bindings.
        lblCollectedApplesCountProperty = new SimpleIntegerProperty();
        lblCollectedApplesCountProperty.addListener((observable, oldValue, newValue) -> lblCollectedApplesCount.setText(newValue + ""));

        lblCollectedLeavesCountProperty = new SimpleIntegerProperty();
        lblCollectedLeavesCountProperty.addListener((observable, oldValue, newValue) -> lblCollectedLeavesCount.setText(newValue + ""));

        lblCollectedMushroomsCountProperty = new SimpleIntegerProperty();
        lblCollectedMushroomsCountProperty.addListener((observable, oldValue, newValue) -> lblCollectedMushroomsCount.setText(newValue + ""));

        //Setup elements.
        imgHedgehogInstructor.setOnMouseClicked(event -> new RotateIn(imgHedgehogInstructor).play());
    }

    protected void setupSceneFont(Resourcepack resourcepack) {
        Font sillyFont = Font.loadFont(
                resourcepack.getResourceAsStream(ResourcepackPaths.Fonts.SILLY_FONT_TTF),
                SCENE_LABEL_FONTSIZE);

        if(sillyFont != null) {
            lblInstructions.setFont(sillyFont);
            lblInstructions.setWrapText(true);
        }
    }

    protected void setupLocalization(ResourceBundle resourceBundle) {
        lblPower.setText(resourceBundle.getString(LOCALIZATION_POWER_LABEL));
        lblCollectedItems.setText(resourceBundle.getString(LOCALIZATION_COLLECTED_ITEMS_LABEL));
    }

    protected void setupSceneTextures(Resourcepack resourcepack) {
        //Setup icons.
        imgCollectedApples.setImage(new Image(
                resourcepack.getResourceAsStream(ResourcepackPaths.Fields.APPLE_FIELD_PNG),
                MENU_COLLECTED_ITEMS_FIT_WIDTH,
                MENU_COLLECTED_ITEMS_FIT_WIDTH,
                true,
                true));

        imgCollectedLeaves.setImage(new Image(
                resourcepack.getResourceAsStream(ResourcepackPaths.Fields.LEAF_FIELD_PNG),
                MENU_COLLECTED_ITEMS_FIT_WIDTH,
                MENU_COLLECTED_ITEMS_FIT_WIDTH,
                true,
                true));

        imgCollectedMushrooms.setImage(new Image(
                resourcepack.getResourceAsStream(ResourcepackPaths.Fields.MUSHROOM_FIELD_PNG),
                MENU_COLLECTED_ITEMS_FIT_WIDTH,
                MENU_COLLECTED_ITEMS_FIT_WIDTH,
                true,
                true));

        imgHedgehog.setImage(new Image(
                resourcepack.getResourceAsStream(ResourcepackPaths.Figures.HEDGEHOG_BALL_PNG),
                imgHedgehog.getFitWidth(),
                imgHedgehog.getFitWidth(),
                true,
                false));

        imgHedgehogInstructor.setImage(new Image(
                resourcepack.getResourceAsStream(ResourcepackPaths.Figures.HEDGEHOG_BALL_PNG),
                imgHedgehogInstructor.getFitWidth(),
                imgHedgehogInstructor.getFitWidth(),
                true,
                true));

        //Setup walls.
        Image bordersTextureImage = new Image(
                resourcepack.getResourceAsStream(BallScene.BORDER_TEXTURE_PNG));

        BackgroundImage bordersBackgroundImage = new BackgroundImage(
                bordersTextureImage,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        paneBorderBottom.getStyleClass().clear();
        paneBorderBottom.setBackground(new Background(bordersBackgroundImage));

        paneBorderLeft.getStyleClass().clear();
        paneBorderLeft.setBackground(new Background(bordersBackgroundImage));

        paneBorderRight.getStyleClass().clear();
        paneBorderRight.setBackground(new Background(bordersBackgroundImage));

        paneBorderTop.getStyleClass().clear();
        paneBorderTop.setBackground(new Background(bordersBackgroundImage));

        //Setup floor.
        Image floorTextureImage = new Image(
                resourcepack.getResourceAsStream(BallScene.FLOOR_TEXTURE_PNG));

        BackgroundImage floorBackgroundImage = new BackgroundImage(
                floorTextureImage,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        paneFloor.getStyleClass().clear();
        paneFloor.setBackground(new Background(floorBackgroundImage));
    }
}