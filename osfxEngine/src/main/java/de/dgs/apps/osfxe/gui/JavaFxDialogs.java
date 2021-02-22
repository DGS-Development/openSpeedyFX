package de.dgs.apps.osfxe.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

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

/**
 * Helper class to create common JavaFX dialogs.
 */
public class JavaFxDialogs {
    public static Alert createAlertDialog(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert;
    }

    public static Alert createExceptionDialog(String title, String header, String content, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);

        String exceptionText = stringWriter.toString();

        Label lblException = new Label("Exception:");

        TextArea taExceptionText = new TextArea(exceptionText);
        taExceptionText.setEditable(false);
        taExceptionText.setWrapText(true);

        taExceptionText.setMaxWidth(Double.MAX_VALUE);
        taExceptionText.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(taExceptionText, Priority.ALWAYS);
        GridPane.setHgrow(taExceptionText, Priority.ALWAYS);

        GridPane gpExpandableContent = new GridPane();
        gpExpandableContent.setMaxWidth(Double.MAX_VALUE);
        gpExpandableContent.add(lblException, 0, 0);
        gpExpandableContent.add(taExceptionText, 0, 1);

        alert.getDialogPane().setExpandableContent(gpExpandableContent);

        return alert;
    }
}
