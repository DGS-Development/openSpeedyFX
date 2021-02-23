package de.dgs.apps.openspeedyfx.scenes.notification;

import de.dgs.apps.osfxe.scenes.GameController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

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
