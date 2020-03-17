package it.polimi.ingsw.Model;

public class Cell {

    private Builder builder;
    private int height = 0;
    private boolean hasDome = false;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

}
