import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Explosion {
    private double x, y;
    private int duration = 20; // frames the explosion will last
    private int currentFrame = 0;
    private Color color = Color.ORANGE;

    public Explosion(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean isFinished() {
        return currentFrame >= duration;
    }

    public void update() {
        currentFrame++;
    }

    public void render(GraphicsContext gc) {
        double radius = 10 + currentFrame * 1.5; // Expands over time
        gc.setFill(color.deriveColor(1, 1, 1 - (double)currentFrame / duration, 1));
        gc.fillOval(x - radius / 2, y - radius / 2, radius, radius);
    }
}
