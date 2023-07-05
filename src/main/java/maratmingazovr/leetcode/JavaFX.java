package maratmingazovr.leetcode;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.java_fx.ImageFx;
import maratmingazovr.leetcode.java_fx.JavaFxUtils;
import maratmingazovr.leetcode.neural_network.MathUtil;
import maratmingazovr.leetcode.tasks.generative_network.naive_bayes_generator.ZeroDigitGenerator;
import maratmingazovr.leetcode.tasks.neural_network.function_approximator.FunctionApproximator;

//@SpringBootApplication
//@ConfigurationPropertiesScan("maratmingazovr.leetcode.config")
public class JavaFX extends Application {

    public static void main(String[] args) {
        Application.launch(JavaFX.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        FunctionApproximator functionApproximator = new FunctionApproximator();
        functionApproximator.calculateValues();

        double[] values = new double[]{1,2,3,4,5};
        values = MathUtil.generateNormalDistribution(7, 3, 8000);
        values = MathUtil.convertToZValues(values);
        values = MathUtil.convertToNormalDistribution(values, 15, 7);
        values = MathUtil.round2Digits(values);



        stage.setScene(JavaFxUtils.getDistributionBarChart(values));
        stage.show();

    }

    public void generateZeroImage(Stage stage) {
        ZeroDigitGenerator zeroDigitGenerator = new ZeroDigitGenerator();
        zeroDigitGenerator.train();
        ImageFx image = zeroDigitGenerator.generateImage();
        ImageFx scaledImage = JavaFxUtils.scaleImage2D(image, 10);
        stage.setScene(JavaFxUtils.generateImage2D(scaledImage));
        stage.show();
    }




    @NonNull
    private Scene getBubbleChart() {
        val xAxis = new NumberAxis();
        val yAxis = new NumberAxis();
        ScatterChart<Number, Number> bubbleChart = new ScatterChart<>(xAxis, yAxis);


        XYChart.Series series1 = new XYChart.Series<>();
        series1.setName("Bubbles");
        series1.getData().add(new XYChart.Data(1, 1,1));
        series1.getData().add(new XYChart.Data(10, 10,1));
        bubbleChart.getData().add(series1);

        XYChart.Series series2 = new XYChart.Series<>();
        series2.setName("Bubbles2");
        series2.getData().add(new XYChart.Data(5, 5,0.1));
        series2.getData().add(new XYChart.Data(6, 6,0.1));
        bubbleChart.getData().add(series2);

        Scene scene  = new Scene(bubbleChart, 800, 600);

        return scene;
    }

    @NonNull
    private Scene showImage() {
        // Create Image and ImageView objects
//        Image image = new Image("http://docs.oracle.com/javafx/"
//                                        + "javafx/images/javafx-documentation.png");
        ImageView imageView = new ImageView();
//        imageView.setImage(image);
//
//        // Obtain PixelReader
//        PixelReader pixelReader = image.getPixelReader();
//        System.out.println("Image Width: "+image.getWidth());
//        System.out.println("Image Height: "+image.getHeight());
//        System.out.println("Pixel Format: "+pixelReader.getPixelFormat());

        // Create WritableImage
        WritableImage wImage = new WritableImage(100,100
//                (int)image.getWidth(),
//                (int)image.getHeight()
              );

        PixelWriter pixelWriter = wImage.getPixelWriter();

        // Determine the color of each pixel in a specified row
        for(int readY=0;readY<100;readY++){
            for(int readX=0; readX<100;readX++){
//                Color color = pixelReader.getColor(readX, readY);
                double value = readX;
                Color color = Color.gray(value/100D);
                System.out.println("\nPixel color at coordinates ("+
                                           readX+","+readY+") "
                                           +color.toString());
                System.out.println("R = "+color.getRed());
                System.out.println("G = "+color.getGreen());
                System.out.println("B = "+color.getBlue());
                System.out.println("Opacity = "+color.getOpacity());
                System.out.println("Saturation = "+color.getSaturation());

                // Now write a brighter color to the PixelWriter.
                color = color.brighter();
                pixelWriter.setColor(readX,readY,color);
            }
        }

        // Display image on screen
        imageView.setImage(wImage);
        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, 300, 250);
        return scene;
    }
}
