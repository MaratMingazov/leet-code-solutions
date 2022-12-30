package maratmingazovr.leetcode;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.generative_network.naive_bayes_generator.NaiveBayesImageGenerator;
import maratmingazovr.leetcode.java_fx.JavaFxUtils;
import maratmingazovr.leetcode.tasks.generative_network.naive_bayes_generator.ZeroDigitGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("maratmingazovr.leetcode.config")
public class JavaFX extends Application {

    public static void main(String[] args) {
        Application.launch(JavaFX.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        val zeroDigit = new ZeroDigitGenerator();
        val imagesColors = zeroDigit.getColors();
        val generator = new NaiveBayesImageGenerator(imagesColors.get(0).size());
        imagesColors.forEach(generator::addInput);
        generator.calculateProbabilities();
        val image = generator.generateImage();
        val scaledImage = JavaFxUtils.scaleImage2D(image, 10);
        stage.setScene(JavaFxUtils.generateImage2D(scaledImage));
        stage.show();


    }


    @NonNull
    private Scene getLineChart() {
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of Month");
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);

        lineChart.setTitle("Stock Monitoring, 2010");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
        //populating the series with data
        series.getData().add(new XYChart.Data(1, 23));
        series.getData().add(new XYChart.Data(2, 14));
        series.getData().add(new XYChart.Data(3, 15));
        series.getData().add(new XYChart.Data(4, 24));
        series.getData().add(new XYChart.Data(5, 34));
        series.getData().add(new XYChart.Data(6, 36));
        series.getData().add(new XYChart.Data(7, 22));
        series.getData().add(new XYChart.Data(8, 45));
        series.getData().add(new XYChart.Data(9, 43));
        series.getData().add(new XYChart.Data(10, 17));
        series.getData().add(new XYChart.Data(11, 29));
        series.getData().add(new XYChart.Data(12, 25));

        Scene scene  = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);
        return scene;
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
