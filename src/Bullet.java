public class Bullet {
    private final double bulletSpeedConstant = 0.7;
    private final double x;
    private double y;

    public Bullet(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y -= 10 * bulletSpeedConstant;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

