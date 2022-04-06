package dk.easv;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController implements  Runnable
{
    private final List<Image> images = new ArrayList<>();
    public Slider sliderSpeed;
    public Label lblFileName;
    public Label lblPixelstats;
    private int currentImageIndex = 0;
    private String filename;
    private boolean stop = false;

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;


    @FXML
    private void handleBtnLoadAction()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (!files.isEmpty())
        {
            files.forEach((File f) ->
            {
                images.add(new Image(f.toURI().toString()));
            });
            displayImage();
        }

        if (!stop){
            Thread thread = new Thread(slideshow);
            thread.start();
        }
    }


    Runnable slideshow = () -> {
        while (true){
            try {

                Thread.sleep((int) sliderSpeed.getValue() * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handleBtnNextAction();

        }
    };

    Runnable displayName = () -> {

        String url = images.get(currentImageIndex).getUrl();
        String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
        this.filename = fileName;
        Platform.runLater(() -> lblFileName.setText(filename));

    };

    Runnable pixelReader = () -> {

        int colorBlue = 0;
        int colorRed = 0;
        int colorGreen = 0;

        for (int i = 0; i < (int) imageView.getImage().getWidth(); i++){
            for (int k = 0; k < (int) imageView.getImage().getHeight(); k++){
                Color color = imageView.getImage().getPixelReader().getColor(i,k);
                if (color.getBlue() > color.getGreen() && color.getBlue() > color.getRed()){
                    colorBlue++;
                }else if(color.getRed() > color.getBlue() && color.getRed() > color.getGreen()){
                    colorRed++;
                }else{
                    colorGreen++;
                }

            }
        }

        int finalColorRed = colorRed;
        int finalColorBlue = colorBlue;
        int finalColorGreen = colorGreen;
        Platform.runLater(() -> lblPixelstats.setText("red: " + finalColorRed + " blue: " + finalColorBlue + " Green: " + finalColorGreen));
    };




    @FXML
    private void handleBtnPreviousAction()
    {
        if (!images.isEmpty())
        {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction()
    {
        if (!images.isEmpty())
        {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
        }
    }

    private void displayImage()
    {
        if (!images.isEmpty())
        {
            imageView.setImage(images.get(currentImageIndex));
        }



        if(!stop){
            Thread thread2 = new Thread(pixelReader);
            thread2.start();

            Thread thread1 = new Thread(displayName);
            thread1.start();
        }
    }


    @Override
    public void run() {

    }

    public void onStopBtn(ActionEvent actionEvent) {



    }
}