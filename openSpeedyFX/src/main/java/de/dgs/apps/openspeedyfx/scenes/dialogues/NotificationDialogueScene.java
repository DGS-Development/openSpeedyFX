package de.dgs.apps.openspeedyfx.scenes.dialogues;

import de.dgs.apps.osfxe.scenes.GameController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class NotificationDialogueScene extends GameController {
    @FXML
    private Label lblText;

    @FXML
    private ImageView imgImage;

    @Override
    public String getFxmlPath() {
        return "/assets/fxml/dialogues/notificationDialogue.fxml";
    }

    @Override
    public void onInitialized() {
        //Ignore
    }

    public Label getLblText() {
        return lblText;
    }

    public ImageView getImgImage() {
        return imgImage;
    }
}
