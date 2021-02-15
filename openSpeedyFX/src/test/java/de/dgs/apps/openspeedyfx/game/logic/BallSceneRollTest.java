package de.dgs.apps.openspeedyfx.game.logic;

import de.dgs.apps.openspeedyfx.engine.scenes.SceneManager;
import de.dgs.apps.openspeedyfx.game.resourcepacks.DefaultResourcepack;
import de.dgs.apps.openspeedyfx.game.resourcepacks.Resourcepack;
import de.dgs.apps.openspeedyfx.scenes.ballscene.InteractiveBallScene;
import de.dgs.apps.openspeedyfx.scenes.ballscene.logic.*;
import de.dgs.apps.osfxe.openspeedyfx.scenes.ballscene.logic.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.robot.Motion;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith(ApplicationExtension.class)
public class BallSceneRollTest {
    private InteractiveBallScene interactiveBallScene;
    private Stage stage;
    private SceneManager sceneManager;
    private InteractiveBallScene.Roll activeRoll;

    @Start
    public void start(Stage stage) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        //Setup scene.
        sceneManager = new SceneManager(stage);
        this.stage = stage;

        //Setup roll.
        interactiveBallScene = sceneManager.loadScene(InteractiveBallScene.class);
    }

    @Test
    public void initialTest(FxRobot robot) {
        AtomicBoolean isTestCompleted = new AtomicBoolean(false);
        SimpleObjectProperty<CollectablesCount> collectablesCountProperty = new SimpleObjectProperty<>();

        RollProperties rollProperties = new SelectiveRollProperties() {
            @Override
            public ResourceBundle getResourceBundle() {
                return ResourceBundle.getBundle("localization/UiResources", Locale.ENGLISH);
            }

            @Override
            public long getRandomSeed() {
                return 0;
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
                if(!stage.isShowing()) {
                    sceneManager.switchScene(interactiveBallScene);
                    stage.show();

                    //Click on the floor.
                    Node paneFloor = robot.lookup("#paneFloor").query();
                    Bounds boundsInScreen = paneFloor.localToScreen(paneFloor.getBoundsInLocal());

                    robot.clickOn(new Point2D(boundsInScreen.getMinX()+60,boundsInScreen.getMaxY()-60), Motion.DEFAULT, MouseButton.PRIMARY);

                    //Rotate the hedgehog.
                    for(int count=0; count < 8; count++){
                        robot.press(KeyCode.getKeyCode("A"));
                        robot.release(KeyCode.getKeyCode("A"));
                    }

                    //Shoot the ball in the selected direction.
                    robot.press(KeyCode.SPACE);
                    robot.release(KeyCode.SPACE);

                    Thread thread = new Thread(()->{
                        try {
                            Thread.sleep(3000);
                        }
                        catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }

                        Platform.runLater(()->{
                            robot.press(KeyCode.SPACE);
                            robot.release(KeyCode.SPACE);
                        });
                    });

                    thread.start();
                }
            }

            @Override
            public void onRollCompleted(CollectablesCount rollCollectablesCount) {
                collectablesCountProperty.set(rollCollectablesCount);
                isTestCompleted.set(true);
            }

            @Override
            public void onRollException(Exception exception) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("An unexpected exception occurred.");
                alert.setHeaderText("Exception:");
                alert.setContentText(exception.toString());
                alert.showAndWait();
                exception.printStackTrace();
                System.exit(1);
            }
        };

        activeRoll = new InteractiveBallScene.Roll(rollProperties, rollCallback);

        Platform.runLater(()-> interactiveBallScene.doRoll(activeRoll));

        while(!isTestCompleted.get()){
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }

        Assertions.assertThat(collectablesCountProperty.get().getApplesCount()).isEqualTo(2);
        Assertions.assertThat(collectablesCountProperty.get().getLeafsCount()).isEqualTo(1);
        Assertions.assertThat(collectablesCountProperty.get().getMushroomsCount()).isEqualTo(1);
    }
}
