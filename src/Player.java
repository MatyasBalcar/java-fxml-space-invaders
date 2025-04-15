public class Player {
    private final double speedConstantMultiplier = 0.5;
    private double x;
    private final double y;
    private final double width;
    private final double height;

    public Player(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move(double dx) {
        x += dx * speedConstantMultiplier;
        if (x < 0) x = 0;
        if (x > 550) x = 550;
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
