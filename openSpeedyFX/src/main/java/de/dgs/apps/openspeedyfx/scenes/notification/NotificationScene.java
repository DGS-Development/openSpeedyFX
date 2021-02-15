package de.dgs.apps.openspeedyfx.scenes.notification;

import de.dgs.apps.osfxe.scenes.GameController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class NotificationScene extends GameController {
    @FXML
    private ImageView imgImage;

    @FXML
    private Label lblText;

    @FXML
    private Label lblButtonText;

    @FXML
    private Button btnConfirm;

    @Override
    public String getFxmlPath() {
        return "/assets/fxml/notification/notification.fxml";
    }

    @Override
    public void onInitialized() {
        //Ignore
    }

    public ImageView getImgImage() {
        return imgImage;
    }

    public Label getLblText() {
        return lblText;
    }

    public Label getLblButtonText() {
        return lblButtonText;
    }

    public Button getBtnConfirm() {
        return btnConfirm;
    }
}
