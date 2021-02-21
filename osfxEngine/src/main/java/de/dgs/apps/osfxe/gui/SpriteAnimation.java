package de.dgs.apps.osfxe.gui;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

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
     * @param imageView The imageView of the sprite sheet.
     * @param duration The time a single cutout is displayed.
     * @param count Amount of images to display.
     * @param columns Amount of columns per row.
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
     * Calculates the x and y coordinates of the cutout of the sprite sheet.
     * @param percentage The percentage of the transition from 0.0 to 1.0.
     */
    protected void interpolate(double percentage) {
        final int index = Math.min((int) Math.floor(percentage * count), count - 1);
        if (index != lastIndex) {
            //Use next row if count > columns.
            final double x = (index % columns) * width + offsetX;

            @SuppressWarnings("IntegerDivisionInFloatingPointContext")
            final double y = (index / columns) * height + offsetY;

            imageView.setViewport(new Rectangle2D(x, y, width, height));
            lastIndex = index;
        }
    }
}