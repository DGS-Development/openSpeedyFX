package de.dgs.apps.openspeedyfx.scenes.dialogues;

import de.dgs.apps.osfxe.scenes.GameController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class InformationDialogueScene extends GameController {
    @FXML
    private Label lblText;

    @FXML
    private ImageView imgImage;

    @FXML
    private Button btnContinue;

    @Override
    public String getFxmlPath() {
        return "/assets/fxml/dialogues/informationDialogue.fxml";
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

    public Button getBtnContinue() {
        return btnContinue;
    }
}
