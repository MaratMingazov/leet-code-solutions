package maratmingazovr.leetcode.java_fx;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import lombok.NonNull;

public class PixelFx extends Point2D {

    @NonNull
    private final Color color;

    public PixelFx(@NonNull Integer x,
                   @NonNull Integer y,
                   @NonNull Color color) {
        super(x, y);
        this.color = color;
    }



    @NonNull
    public Color getColor() {
        return this.color;
    }

    public Integer getIntX() {
        return (int)getX();
    }

    public Integer getIntY() {
        return (int) getY();
    }
}
