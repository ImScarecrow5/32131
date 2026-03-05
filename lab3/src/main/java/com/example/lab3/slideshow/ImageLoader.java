package com.example.lab3.slideshow;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {
    // Загрузка изображения из файла
    public static Image loadFromFile(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return new Image(is);
        } catch (IOException e) {
            System.err.println("Ошибка загрузки изображения: " + file.getName());
            e.printStackTrace();
            return null;
        }
    }

    // Загрузка из ресурсов (для тестирования)
    public static Image loadFromResource(String path) {
        try {
            InputStream is = ImageLoader.class.getResourceAsStream(path);
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки ресурса: " + path);
            e.printStackTrace();
        }
        return null;
    }
}