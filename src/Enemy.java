import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Enemy {
    private double x, y;
    private double width, height;
    private Image sprite;
    private final double speedMultiplier = 0.3;

    public Enemy(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        sprite = new Image(Objects.requireNonNull(getClass().getResourceAsStream("./enemy.png")));
    }

    public void move(double dy) {
        y += dy * speedMultiplier;
    }

    public boolean collidesWith(Bullet bullet) {
        return bullet.getX() < x + width &&
                bullet.getX() + 5 > x &&
                bullet.getY() < y + height &&
                bullet.getY() + 10 > y;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(sprite, x, y, width, height);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    // Optional: remove this if not used anymore
    public Color getColor() {
        return Color.GREEN;
    }
}
