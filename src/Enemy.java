import java.awt.*;
import java.util.Random;

public class Enemy {
    private final double speedConstant = 0.25;
    private final double x;
    private double y;
    private final double width;
    private final double height;
    private final Color color;

    Random rand = new Random();

    public Enemy(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public boolean collidesWith(Bullet bullet) {
        return bullet.getX() < x + width &&
                bullet.getX() + 5 > x &&
                bullet.getY() < y + height &&
                bullet.getY() + 10 > y;
    }

    public void move(int dy) {
        y += dy * speedConstant;

    }

    // Getters
    public javafx.scene.paint.Paint getColor() {

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();
        double opacity = a / 255.0 ;
        return javafx.scene.paint.Color.rgb(r, g, b, opacity);
    }

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
}
