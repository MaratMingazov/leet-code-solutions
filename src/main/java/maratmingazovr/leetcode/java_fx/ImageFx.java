package maratmingazovr.leetcode.java_fx;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class ImageFx {

    @NonNull
    private List<List<PixelFx>> pixels;

    @NonNull
    private Integer width;

    @NonNull
    private Integer height;

    public ImageFx(@NonNull List<List<PixelFx>> pixels) {
        this.pixels = pixels;
        this.height = pixels.size();
        this.width = pixels.get(0).size();
    }
}
