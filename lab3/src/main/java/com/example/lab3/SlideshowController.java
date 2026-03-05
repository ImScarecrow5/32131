package com.example.lab3;

import com.example.lab3.slideshow.ImageCollection;
import com.example.lab3.slideshow.ImageLoader;
import com.example.lab3.slideshow.Iterator;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SlideshowController implements Initializable {

    @FXML
    private ImageView imageView;
    @FXML
    private Button btnFirst, btnPrev, btnPlay, btnNext, btnLast;
    @FXML
    private Label positionLabel, totalLabel, fileNameLabel, filterLabel;
    @FXML
    private Slider timeSlider;
    @FXML
    private CheckBox fadeCheck, scaleCheck;
    @FXML
    private ComboBox<String> filterBox;

    private ImageCollection imageCollection;
    private Iterator iterator;
    private Timeline timeline;
    private boolean isPlaying = false;
    private double slideTime = 3000;
    private File imageDir;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageDir = new File("src/main/resources/images");
        if (!imageDir.exists()) {
            imageDir = new File("images");
        }
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }

        setupFilterBox();

        imageCollection = new ImageCollection(imageDir);
        iterator = imageCollection.getIterator();

        setupButtons();

        if (imageCollection.size() > 0) {
            File firstFile = imageCollection.getFile(0);
            imageView.setImage(ImageLoader.loadFromFile(firstFile));
            updateFileInfo(firstFile);
            updatePosition();
        }

        timeSlider.setMin(1);
        timeSlider.setMax(10);
        timeSlider.setValue(3);

        timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            slideTime = newVal.doubleValue() * 1000;
            if (isPlaying) {
                stopShow();
                startShow();
            }
        });

        updateTotalLabel();

        fadeCheck.setSelected(true);

        fadeCheck.setOnAction(e -> {
            if (fadeCheck.isSelected()) {
                scaleCheck.setSelected(false);
            }
        });

        scaleCheck.setOnAction(e -> {
            if (scaleCheck.isSelected()) {
                fadeCheck.setSelected(false);
            }
        });

        positionLabel.setMinWidth(30);
        positionLabel.setAlignment(javafx.geometry.Pos.CENTER);
        totalLabel.setMinWidth(30);
        totalLabel.setAlignment(javafx.geometry.Pos.CENTER);
        fileNameLabel.setMinWidth(800);
        filterLabel.setMinWidth(150);
    }

    private void setupFilterBox() {
        filterBox.getItems().addAll("Все", "JPG", "PNG", "GIF", "BMP");
        filterBox.setValue("Все");

        filterBox.setOnAction(e -> {
            String filter = filterBox.getValue();
            String filterType = "all";

            switch (filter) {
                case "JPG": filterType = "jpg"; break;
                case "PNG": filterType = "png"; break;
                case "GIF": filterType = "gif"; break;
                case "BMP": filterType = "bmp"; break;
                default: filterType = "all";
            }

            applyFilter(filterType);
        });
    }

    private void applyFilter(String filterType) {
        imageCollection.setFilter(imageDir, filterType);
        iterator = imageCollection.getIterator();

        filterLabel.setText("Фильтр: " + filterBox.getValue());

        if (imageCollection.size() > 0) {
            while (iterator.hasPreview()) {
                iterator.preview();
            }
            File firstFile = imageCollection.getFile(0);
            imageView.setImage(ImageLoader.loadFromFile(firstFile));
            updateFileInfo(firstFile);
            updatePosition();
        } else {
            imageView.setImage(null);
            fileNameLabel.setText("Файл: нет изображений");
            positionLabel.setText("0");
        }

        updateTotalLabel();

        if (isPlaying) {
            stopShow();
            startShow();
        }
    }

    private void setupButtons() {
        btnFirst.setOnAction(e -> goToFirst());
        btnPrev.setOnAction(e -> goToPrev());
        btnPlay.setOnAction(e -> togglePlay());
        btnNext.setOnAction(e -> goToNext());
        btnLast.setOnAction(e -> goToLast());
    }

    private void loadWithAnimation(File file) {
        if (file != null && file.exists()) {
            Image newImg = ImageLoader.loadFromFile(file);
            if (newImg != null) {
                if (fadeCheck.isSelected()) {
                    doFade(newImg);
                } else if (scaleCheck.isSelected()) {
                    doScale(newImg);
                } else {
                    imageView.setImage(newImg);
                }
                updateFileInfo(file);
                updatePosition();
            }
        }
    }

    private void doFade(Image newImg) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), imageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            imageView.setImage(newImg);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), imageView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private void doScale(Image newImg) {
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(300), imageView);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(0.1);
        scaleOut.setToY(0.1);

        scaleOut.setOnFinished(e -> {
            imageView.setImage(newImg);
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), imageView);
            scaleIn.setFromX(0.1);
            scaleIn.setFromY(0.1);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            scaleIn.play();
        });

        scaleOut.play();
    }

    private void updateFileInfo(File file) {
        String name = file.getName();
        fileNameLabel.setText("Файл: " + name);
    }

    private void updateTotalLabel() {
        totalLabel.setText(String.valueOf(imageCollection.size()));
    }

    private void goToFirst() {
        if (imageCollection.size() > 0) {
            while (iterator.hasPreview()) {
                iterator.preview();
            }
            loadWithAnimation(imageCollection.getFile(0));
        }
    }

    private void goToLast() {
        if (imageCollection.size() > 0) {
            while (iterator.hasNext()) {
                iterator.next();
            }
            loadWithAnimation(imageCollection.getFile(imageCollection.size() - 1));
        }
    }

    private void goToNext() {
        if (imageCollection.size() > 0) {
            if (iterator.hasNext()) {
                loadWithAnimation((File) iterator.next());
            } else {
                goToFirst();
            }
        }
    }

    private void goToPrev() {
        if (imageCollection.size() > 0) {
            if (iterator.hasPreview()) {
                loadWithAnimation((File) iterator.preview());
            } else {
                goToLast();
            }
        }
    }

    private void togglePlay() {
        if (isPlaying) {
            stopShow();
        } else {
            startShow();
        }
    }

    private void startShow() {
        if (imageCollection.size() == 0) return;

        timeline = new Timeline(new KeyFrame(Duration.millis(slideTime), e -> goToNext()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        isPlaying = true;
        btnPlay.setText("❚❚");
    }

    private void stopShow() {
        if (timeline != null) {
            timeline.stop();
        }
        isPlaying = false;
        btnPlay.setText("▶");
    }

    private void updatePosition() {
        int current = iterator.getCurrentIndex() + 1;
        positionLabel.setText(String.valueOf(current));
    }
}