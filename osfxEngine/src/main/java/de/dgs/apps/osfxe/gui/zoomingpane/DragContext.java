package de.dgs.apps.osfxe.gui.zoomingpane;

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
