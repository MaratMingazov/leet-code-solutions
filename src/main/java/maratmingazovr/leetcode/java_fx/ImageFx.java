package maratmingazovr.leetcode.java_fx;

import javafx.scene.paint.Color;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImageFx {

    @NonNull
    private List<List<PixelFx>> pixels = new ArrayList<>();

    @NonNull
    private Integer width;

    @NonNull
    private Integer height;


    public ImageFx(@NonNull List<Color> colors) {
        int imageShape = (int)Math.sqrt(colors.size());
        int pixelIndex = 0;
        for (int y = 0; y < imageShape; y++) {
            List<PixelFx> row = new ArrayList<>();
            for (int x = 0; x < imageShape; x++) {
                row.add(new PixelFx(x, y, colors.get(pixelIndex)));
                pixelIndex++;
            }
            pixels.add(row);
            this.height = imageShape;
            this.width = imageShape;
        }
    }
}
