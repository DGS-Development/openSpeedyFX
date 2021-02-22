package de.dgs.apps.osfxe.gui;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

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
 * Sprite animation transition to display sprites in an {@link ImageView}.
 */
public class SpriteAnimation extends Transition {
    private final ImageView imageView;
    private final int count;
    private final int columns;
    private final double offsetX;
    private final double offsetY;
    private final double width;
    private final double height;

    private int lastIndex;

    /**
     * Creates a new {@link SpriteAnimation}.
     * @param imageView The {@link ImageView} to display the animation.
     * @param duration The duration a single cutout is displayed.
     * @param count Amount of cutouts to display.
     * @param columns Amount of columns (cutouts) per row.
     * @param offsetX Offset beside a cutout.
     * @param offsetY Offset above a cutout.
     * @param width Width of a single cutout.
     * @param height Height of a single cutout.
     */
    public SpriteAnimation(
            ImageView imageView,
            Duration duration,
            int count, int columns,
            double offsetX, double offsetY,
            double width, double height) {
        this.imageView = imageView;
        this.count = count;
        this.columns = columns;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;

        setCycleDuration(duration);
        setInterpolator(Interpolator.LINEAR);
    }

    /**
     * Calculates the active cutout, inside the sprite sheet.
     * @param percentage The percentage of the transition from 0.0 to 1.0.
     */
    protected void interpolate(double percentage) {
        int index = Math.min((int) Math.floor(percentage * count), count - 1);

        if (index != lastIndex) {
            //Use next row if count > columns.
            double x = (index % columns) * width + offsetX;

            @SuppressWarnings("IntegerDivisionInFloatingPointContext")
            double y = (index / columns) * height + offsetY;

            imageView.setViewport(new Rectangle2D(x, y, width, height));
            lastIndex = index;
        }
    }
}