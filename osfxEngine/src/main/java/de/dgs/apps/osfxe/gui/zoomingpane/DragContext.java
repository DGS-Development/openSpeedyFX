package de.dgs.apps.osfxe.gui.zoomingpane;

/**
 * Simple POJO container for drag information.
 */
public class DragContext {
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    public DragContext() {
        //Ignore...
    }

    public DragContext(double mouseAnchorX, double mouseAnchorY, double translateAnchorX, double translateAnchorY) {
        this.mouseAnchorX = mouseAnchorX;
        this.mouseAnchorY = mouseAnchorY;
        this.translateAnchorX = translateAnchorX;
        this.translateAnchorY = translateAnchorY;
    }

    public double getMouseAnchorX() {
        return mouseAnchorX;
    }

    public void setMouseAnchorX(double mouseAnchorX) {
        this.mouseAnchorX = mouseAnchorX;
    }

    public double getMouseAnchorY() {
        return mouseAnchorY;
    }

    public void setMouseAnchorY(double mouseAnchorY) {
        this.mouseAnchorY = mouseAnchorY;
    }

    public double getTranslateAnchorX() {
        return translateAnchorX;
    }

    public void setTranslateAnchorX(double translateAnchorX) {
        this.translateAnchorX = translateAnchorX;
    }

    public double getTranslateAnchorY() {
        return translateAnchorY;
    }

    public void setTranslateAnchorY(double translateAnchorY) {
        this.translateAnchorY = translateAnchorY;
    }
}
