package de.dgs.apps.openspeedyfx.scenes.dialogues;

import de.dgs.apps.osfxe.scenes.GameController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class SelectionDialogueScene extends GameController {
    @FXML
    private Label lblTitle;

    @FXML
    private ListView<String> lvItems;

    @FXML
    private Button btnContinue;

    @Override
    public String getFxmlPath() {
        return "/assets/fxml/dialogues/selectionDialogue.fxml";
    }

    @Override
    public void onInitialized() {
        //Ignore
    }

    public Label getLblTitle() {
        return lblTitle;
    }

    public ListView<String> getLvItems() {
        return lvItems;
    }

    public Button getBtnContinue() {
        return btnContinue;
    }
}
