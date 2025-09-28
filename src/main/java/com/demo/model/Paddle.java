package com.demo.model;



import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

    public class Paddle extends Movable{
        private double speed;
        private double screenWidth;

        public Paddle(double x, double y, double width, double height, double screenWidth) {
             super(0, 0, x, y, width, height);
             this.speed = 10;
             this.screenWidth = screenWidth;
        }

        public void moveLeft() {
            x -= speed;
        }

        public void moveRight() {
            x += speed;
        }

        public void draw(GraphicsContext gc) {
            gc.setFill(Color.WHITE);
            gc.fillRect(x, y, width, height);
        }

        // Getters và setters
        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }
    }

