package com.myapp.booknow.mvvm.model;

public class Category {

    private static final int DEFAULT_TEXT_COLOR = 0xFF333333; // Light Black


    private String name; // name of the category
    private int logoResourceId; // logo of the category to put in the card view
    private int backgroundColor; // the back ground to assign to the card view
    private int nameTextColor; // the color of the category title

    public Category(String name, String nameDesign, int logoResourceId, int backgroundColor) {
        this.name = name;
        this.logoResourceId = logoResourceId;
        this.backgroundColor = backgroundColor;
        this.nameTextColor = DEFAULT_TEXT_COLOR;
    }

    public Category(String name, String nameDesign, int logoResourceId, int backgroundColor, int nameTextColor) {
        this.name = name;
        this.logoResourceId = logoResourceId;
        this.backgroundColor = backgroundColor;
        this.nameTextColor = nameTextColor;
    }

    public String getName() {
        return name;
    }

    public int getLogoResourceId() {
        return logoResourceId;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getNameTextColor() {
        return nameTextColor;
    }
}
