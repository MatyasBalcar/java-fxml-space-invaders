import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Player {
    private final double speedConstantMultiplier = 0.5;
    private double x;
    private final double y;
    private final double width;
    private final double height;
    private final Image sprite;

    public Player(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Load player sprite from resources
        sprite = new Image(Objects.requireNonNull(getClass().getResourceAsStream("player.png")));
    }

    public void move(double dx) {
        x += dx * speedConstantMultiplier;
        if (x < 0) x = 0;
        if (x > 550) x = 550;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(sprite, x, y, width, height);
    }

    // Getters
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
