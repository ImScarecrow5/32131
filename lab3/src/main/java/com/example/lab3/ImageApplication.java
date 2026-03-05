package com.example.lab3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class ImageApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Получаем URL ресурса
        URL fxmlLocation = getClass().getResource("/com/example/lab3/slideshow.fxml");

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Слайдшоу");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}