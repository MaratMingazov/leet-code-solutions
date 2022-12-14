package maratmingazovr.leetcode.java_fx;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import lombok.NonNull;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JavaFxUtils {

    @NonNull
    public static Scene generateImage2D(@NonNull ImageFx image) {

        val width = image.getWidth();
        val height = image.getHeight();
        val pixels = image.getPixels();
        ImageView imageView = new ImageView();
        WritableImage wImage = new WritableImage(width, height);
        PixelWriter pixelWriter = wImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                val pixel = pixels.get(y).get(x);
                pixelWriter.setColor(pixel.getIntX(), pixel.getIntY(), pixel.getColor());
            }
        }

        imageView.setImage(wImage);
        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, width, height);
        return scene;
    }

    @NonNull
    public static ImageFx scaleImage2D(@NonNull ImageFx image,
                                       @NonNull Integer scale) {
        List<List<PixelFx>> result = new ArrayList<>();
        val width = image.getWidth();
        val height = image.getHeight();
        val pixels = image.getPixels();

        int newY = 0;
        for (int y = 0; y < height; y++) {
            for (int m = 0; m < scale; m++) {
                List<PixelFx> row = new ArrayList<>();
                int newX = 0;
                for (int x = 0; x < width; x++) {
                    val pixel = pixels.get(y).get(x);
                    for (int s = 0; s < scale; s++) {
                        row.add(new PixelFx(newX, newY, pixel.getColor()));
                        newX++;
                    }
                }
                result.add(row);
                newY++;
            }
        }
        List<Color> colors = new ArrayList<>();
        result.forEach(row -> colors.addAll(row.stream().map(PixelFx::getColor).collect(Collectors.toList())));
        return new ImageFx(colors);
    }
}
