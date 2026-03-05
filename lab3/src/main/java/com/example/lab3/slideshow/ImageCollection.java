package com.example.lab3.slideshow;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

public class ImageCollection implements Aggregate {
    private File[] files;
    private String currentFilter = "all";

    private static final String[] ALL_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
    private static final String[] JPG_EXTENSIONS = {".jpg", ".jpeg"};
    private static final String[] PNG_EXTENSIONS = {".png"};
    private static final String[] GIF_EXTENSIONS = {".gif"};
    private static final String[] BMP_EXTENSIONS = {".bmp"};

    public ImageCollection(File directory) {
        loadFiles(directory, currentFilter);
    }

    private void loadFiles(File directory, String filterType) {
        if (directory.exists() && directory.isDirectory()) {
            String[] exts = getExtensions(filterType);

            this.files = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String lowercase = name.toLowerCase();
                    for (String ext : exts) {
                        if (lowercase.endsWith(ext)) {
                            return true;
                        }
                    }
                    return false;
                }
            });

            if (files != null) {
                Arrays.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                });
            } else {
                files = new File[0];
            }
        } else {
            files = new File[0];
        }
    }

    private String[] getExtensions(String filterType) {
        switch (filterType) {
            case "jpg": return JPG_EXTENSIONS;
            case "png": return PNG_EXTENSIONS;
            case "gif": return GIF_EXTENSIONS;
            case "bmp": return BMP_EXTENSIONS;
            default: return ALL_EXTENSIONS;
        }
    }

    public void setFilter(File directory, String filterType) {
        this.currentFilter = filterType;
        loadFiles(directory, filterType);
    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    @Override
    public Iterator getIterator() {
        return new ImageFileIterator();
    }

    public File getFile(int index) {
        if (index >= 0 && index < files.length) {
            return files[index];
        }
        return null;
    }

    public int size() {
        return files.length;
    }

    private class ImageFileIterator implements Iterator {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < files.length - 1;
        }

        @Override
        public Object next() {
            if (hasNext()) {
                return files[++currentIndex];
            }
            return null;
        }

        @Override
        public Object preview() {
            if (hasPreview()) {
                return files[--currentIndex];
            }
            return null;
        }

        @Override
        public boolean hasPreview() {
            return currentIndex > 0;
        }

        @Override
        public int getCurrentIndex() {
            return currentIndex;
        }
    }
}