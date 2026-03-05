package com.example.lab3.slideshow;

public interface Iterator {
    public boolean hasNext();
    public Object next();
    public Object preview();
    public boolean hasPreview();
    public int getCurrentIndex();
}