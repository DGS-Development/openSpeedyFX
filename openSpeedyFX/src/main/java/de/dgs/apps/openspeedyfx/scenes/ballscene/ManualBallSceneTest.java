package de.dgs.apps.openspeedyfx.scenes.ballscene;

import de.dgs.apps.openspeedyfx.game.resourcepacks.DefaultResourcepack;
import de.dgs.apps.openspeedyfx.game.resourcepacks.Resourcepack;
import de.dgs.apps.openspeedyfx.scenes.ballscene.logic.*;
import de.dgs.apps.osfxe.scenes.SceneManager;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManualBallSceneTest {
    private final Stage primaryStage;

    private InteractiveBallScene.Roll activeRoll = null;

    public ManualBallSceneTest(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void startTest() throws Exception {
        SceneManager sceneManager = new SceneManager(primaryStage);

        //Setup roll.
        InteractiveBallScene interactiveBallScene = sceneManager.loadScene(InteractiveBallScene.class);

        RollProperties rollProperties = new SelectiveRollProperties() {
            @Override
            public ResourceBundle getResourceBundle() {
                return ResourceBundle.getBundle("localization/UiResources", Locale.ENGLISH);
            }

            @Override
            public Resourcepack getResourcepack() {
                return DefaultResourcepack.getInstance();
            }

            @Override
            public CollectablesCount getCollectablesCount() {
                return new CollectablesCount(6,6,6);
            }

            @Override
            public float getEffectsBaseVolume() {
                return 1;
            }
        };

        RollCallback rollCallback = new SelectiveRollCallback() {
            @Override
            public void onRollIsReady() {
                if(!primaryStage.isShowing()) {
                    sceneManager.switchScene(interactiveBallScene);
                    primaryStage.show();
                }
            }

            @Override
            public void onRollCompleted(CollectablesCount collectablesCount) {
                int collectedItemsCount = collectablesCount.getApplesCount() + collectablesCount.getLeafsCount() + collectablesCount.getMushroomsCount();

                Alert messageAlert;

                if(collectedItemsCount == 0 || collectedItemsCount > 4) {
                    messageAlert = new Alert(Alert.AlertType.ERROR);
                    messageAlert.setTitle("Oh no...");
                    messageAlert.setHeaderText(collectedItemsCount + " items... Nice try...");
                    messageAlert.setContentText("Too bad! Please try again.");
                }
                else {
                    messageAlert = new Alert(Alert.AlertType.INFORMATION);
                    messageAlert.setTitle("Excellent!");
                    messageAlert.setHeaderText(collectedItemsCount + " items! Great!");
                }

                messageAlert.showAndWait();

                messageAlert = new Alert(Alert.AlertType.CONFIRMATION);
                messageAlert.setTitle("Try again?");
                messageAlert.setHeaderText("Do you want to perform another roll?");
                messageAlert.setContentText(":D");

                Optional<ButtonType> result = messageAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    interactiveBallScene.doRoll(activeRoll);
                }
                else {
                    primaryStage.hide();
                }
            }

            @Override
            public void onRollException(Exception exception) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("An unexpected exception occurred.");
                alert.setHeaderText("Exception:");
                alert.setContentText(exception.toString());
                alert.showAndWait();

                System.exit(1);
            }
        };

        activeRoll = new InteractiveBallScene.Roll(rollProperties, rollCallback);
        interactiveBallScene.doRoll(activeRoll);
    }
}
