package de.dgs.apps.osfxe.physics;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.List;

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
 * Helper class to detect if a coordinate is blocked by a certain shape.
 */
public class CoordinateBlockedDetector {
    public static class BlockedRectangle {
        private double xStart;
        private double xEnd;
        private double yStart;
        private double yEnd;

        public BlockedRectangle(double xStart, double xEnd, double yStart, double yEnd) {
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.yStart = yStart;
            this.yEnd = yEnd;
        }

        public double getXStart() {
            return xStart;
        }

        public double getXEnd() {
            return xEnd;
        }

        public double getYStart() {
            return yStart;
        }

        public double getYEnd() {
            return yEnd;
        }
    }

    public static class BlockedCircle {
        private double centerX;
        private double centerY;
        private double radius;

        public BlockedCircle(double centerX, double centerY, double radius) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
        }

        public double getCenterX() {
            return centerX;
        }

        public double getCenterY() {
            return centerY;
        }

        public double getRadius() {
            return radius;
        }
    }

    private List<BlockedRectangle> blockedRectangles;
    private List<BlockedCircle> blockedCircles;

    public CoordinateBlockedDetector() {
        blockedRectangles = new LinkedList<>();
        blockedCircles = new LinkedList<>();
    }

    public List<? extends BlockedRectangle> getBlockedRectangles() {
        return blockedRectangles;
    }

    public List<? extends BlockedCircle> getBlockedCircles() {
        return blockedCircles;
    }

    public BlockedRectangle addRectangle(ImageView imageView) {
        BlockedRectangle blockedRectangle = new BlockedRectangle(
                imageView.getLayoutX(),
                imageView.getLayoutX() + imageView.getFitWidth(),
                imageView.getLayoutY(),
                imageView.getLayoutY() + imageView.getFitHeight());

        addRectangle(blockedRectangle);
        return blockedRectangle;
    }

    public BlockedRectangle addRectangle(Pane pane) {
        BlockedRectangle blockedRectangle = new BlockedRectangle(
                pane.getLayoutX(),
                pane.getLayoutX() + pane.getPrefWidth(),
                pane.getLayoutY(),
                pane.getLayoutY() + pane.getPrefHeight());

        addRectangle(blockedRectangle);
        return blockedRectangle;
    }

    public BlockedCircle addCircle(ImageView imageView) {
        BlockedCircle blockedCircle = new BlockedCircle(
                imageView.getLayoutX(),
                imageView.getLayoutY(),
                imageView.getFitWidth());

        addCircle(blockedCircle);
        return blockedCircle;
    }

    public void addRectangle(BlockedRectangle blockedRectangle) {
        blockedRectangles.add(blockedRectangle);
    }

    public boolean removeRectangle(BlockedRectangle blockedRectangle) {
        return blockedRectangles.remove(blockedRectangle);
    }

    public void addCircle(BlockedCircle blockedCircle) {
        blockedCircles.add(blockedCircle);
    }

    public boolean removeCircle(BlockedCircle blockedCircle) {
        return blockedCircles.remove(blockedCircle);
    }

    public boolean isCoordinateBlocked(double x, double y) {
        return isCoordinateBlocked(x, y, 0);
    }

    public boolean isCoordinateBlocked(double x, double y, double padding) {
        for(BlockedRectangle tmpRectangle : blockedRectangles) {
            if(x >= (tmpRectangle.getXStart() - padding) && x <= (tmpRectangle.getXEnd() + padding)) {
                if(y >= (tmpRectangle.getYStart() - padding) && y <= (tmpRectangle.getYEnd() + padding))
                    return true;
            }
        }

        for(BlockedCircle tmpCircle : blockedCircles) {
            double location = Math.pow((x - tmpCircle.getCenterX()), 2) + Math.pow((y - tmpCircle.getCenterY()), 2);
            double squareRadius = Math.pow(tmpCircle.getRadius() + padding, 2);

            if(location < squareRadius) {
                return true;
            }
        }

        return false;
    }
}
