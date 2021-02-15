package de.dgs.apps.openspeedyfx.speedyfield;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SpeedyFxField extends ImageView {
    public static final String XML_TAG = "SpeedyFxField";
    public static final String XML_ATTRIBUTE_FIELD_ID = "fieldId";

    private static final String ASSET_TEXTURES_PATH = "/assets/speedyfield";
    private static final int INITIAL_WIDTH = 80;

    private SimpleObjectProperty<String> fxFieldIdProperty;
    private SimpleObjectProperty<SpeedyFxFieldTypes> fxFieldTypeProperty;

    public SpeedyFxField() {
        this.fxFieldIdProperty = new SimpleObjectProperty<>(createRandomFieldId());

        this.fxFieldTypeProperty = new SimpleObjectProperty<>(SpeedyFxFieldTypes.RANDOM_ITEM_FIELD);
        this.fxFieldTypeProperty.addListener((observable, oldValue, newValue) -> SpeedyFxField.this.setImage(newValue));
        this.setImage(this.fxFieldTypeProperty.get());

        this.preserveRatioProperty().set(true);
        this.fitWidthProperty().set(INITIAL_WIDTH);
    }

    public static String createRandomFieldId() {
        return "sff" + System.currentTimeMillis();
    }

    public SimpleObjectProperty<String> fieldIdProperty() {
        return this.fxFieldIdProperty;
    }

    public String getFieldId() {
        return fxFieldIdProperty.get();
    }

    public void setFieldId(String fxFieldId) {
        this.fxFieldIdProperty.set(fxFieldId);
    }

    public SimpleObjectProperty<SpeedyFxFieldTypes> fieldTypeProperty() {
        return this.fxFieldTypeProperty;
    }

    public SpeedyFxFieldTypes getFxFieldType() {
        return this.fxFieldTypeProperty.get();
    }

    public void setFxFieldType(SpeedyFxFieldTypes fxFieldType) {
        this.fxFieldTypeProperty.set(fxFieldType);
    }

    private void setImage(SpeedyFxFieldTypes fieldType) {
        if (fieldType == SpeedyFxFieldTypes.START_FIELD) {
            this.setImage(new Image(SpeedyFxField.class.getResourceAsStream(ASSET_TEXTURES_PATH + "/mapStartField.png")));
        } else if (fieldType == SpeedyFxFieldTypes.END_FIELD) {
            this.setImage(new Image(SpeedyFxField.class.getResourceAsStream(ASSET_TEXTURES_PATH + "/mapEndField.png")));
        } else if (fieldType == SpeedyFxFieldTypes.APPLE_ITEM_FIELD) {
            this.setImage(new Image(SpeedyFxField.class.getResourceAsStream(ASSET_TEXTURES_PATH + "/mapAppleItemField.png")));
        } else if (fieldType == SpeedyFxFieldTypes.LEAF_ITEM_FIELD) {
            this.setImage(new Image(SpeedyFxField.class.getResourceAsStream(ASSET_TEXTURES_PATH + "/mapLeafItemField.png")));
        } else if (fieldType == SpeedyFxFieldTypes.MUSHROOM_ITEM_FIELD) {
            this.setImage(new Image(SpeedyFxField.class.getResourceAsStream(ASSET_TEXTURES_PATH + "/mapMushroomItemField.png")));
        } else if (fieldType == SpeedyFxFieldTypes.FOX_OFFSET) {
            this.setImage(new Image(SpeedyFxField.class.getResourceAsStream(ASSET_TEXTURES_PATH + "/mapFoxOffset.png")));
        } else {
            this.setImage(new Image(SpeedyFxField.class.getResourceAsStream(ASSET_TEXTURES_PATH + "/mapRandomItemField.png")));
        }
    }
}
