import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.*;

public class GameController {
    public final int maxAmountOfBulletsConst = 30;
    private final int sizeX = 275;
    private final int sizeY = 750;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private final List<Explosion> explosions = new ArrayList<>();
    private final Map<Enemy, Explosion> pendingExplosions = new HashMap<>();

    public int currentAmountOfBullets = 0;

    @FXML
    public Text ScoreField;

    @FXML
    ProgressBar progressBar = new ProgressBar(0);

    @FXML
    private Canvas gameCanvas;

    private GraphicsContext gc;
    private Player player;
    private Random rand = new Random();
    private int score = 0;
    private int bulletDelay = 0;
    private final int bulletDelayConstant = 50;
    private ArrayList<Integer> levels = new ArrayList<>(List.of(1, 2, 3, 4, 5));
    private int currentLevel = 0;

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();
        player = new Player(sizeX, sizeY, 50, 50 );

        Platform.runLater(() -> {
            gameCanvas.getScene().setOnKeyPressed(e -> keysPressed.add(e.getCode()));
            gameCanvas.getScene().setOnKeyReleased(e -> keysPressed.remove(e.getCode()));
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (update() != 1) {
                    this.stop();
                    Platform.exit();
                }
                render();
            }
        };
        timer.start();
    }

    private int update() {
        // Update score and UI
        ScoreField.setText("Score: " + score);
        progressBar.setProgress((double) bulletDelay / bulletDelayConstant);

        if (bulletDelay > 0) bulletDelay--;

        // Spawn new wave
        if (enemies.isEmpty() && pendingExplosions.isEmpty()) {
            spawnWave(levels.get(currentLevel), 5);
            bullets.clear();
            currentAmountOfBullets = 0;
            currentLevel = Math.min(currentLevel + 1, levels.size() - 1);
        }

        // Handle input
        if (keysPressed.contains(KeyCode.LEFT)) player.move(-5);
        if (keysPressed.contains(KeyCode.RIGHT)) player.move(5);
        if (keysPressed.contains(KeyCode.SPACE) && currentAmountOfBullets < maxAmountOfBulletsConst && bulletDelay == 0) {
            bullets.add(new Bullet(player.getX() + 20, player.getY()));
            bulletDelay = bulletDelayConstant;
            currentAmountOfBullets++;
        }

        // Move enemies
        for (Enemy enemy : enemies) {
            enemy.move(1);
            if (enemy.getY() > sizeY + 50) return 0;
        }

        // Update bullets
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();

            if (bullet.getY() < 0) {
                removeBullet(bulletIterator);
                continue;
            }

            for (Enemy enemy : enemies) {
                if (enemy.collidesWith(bullet)) {
                    if (!pendingExplosions.containsKey(enemy)) {
                        killEnemy(enemy);
                        removeBullet(bulletIterator);
                    }
                    break;
                }
            }
        }

        // Update explosions
        Iterator<Map.Entry<Enemy, Explosion>> expIt = pendingExplosions.entrySet().iterator();
        while (expIt.hasNext()) {
            Map.Entry<Enemy, Explosion> entry = expIt.next();
            Explosion explosion = entry.getValue();
            explosion.update();
            if (explosion.isFinished()) {
                enemies.remove(entry.getKey());
                expIt.remove();
            }
        }

        return 1;
    }

    private void removeBullet(Iterator<Bullet> iterator) {
        iterator.remove();
        currentAmountOfBullets--;
    }

    private void killEnemy(Enemy enemy) {
        Explosion explosion = new Explosion(
                enemy.getX() + enemy.getWidth() / 2.0,
                enemy.getY() + enemy.getHeight() / 2.0
        );
        explosions.add(explosion);
        pendingExplosions.put(enemy, explosion);
        score++;
    }

    private void spawnWave(int rows, int cols) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                enemies.add(new Enemy(100 + j * 80, 100 + i * 80, 40, 40));
            }
        }
    }

    private void render() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw player
        player.render(gc);


        // Draw enemies
        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }


        // Draw bullets
        gc.setFill(Color.RED);
        for (Bullet bullet : bullets) {
            gc.fillRect(bullet.getX(), bullet.getY(), 5, 10);
        }

        // Draw explosions
        for (Explosion explosion : explosions) {
            explosion.render(gc);
        }

        // Clean up finished explosions
        explosions.removeIf(Explosion::isFinished);
    }

    // Inner class for explosion effect
    public static class Explosion {
        private double x, y;
        private int duration = 20;
        private int currentFrame = 0;
        private Color color = Color.ORANGE;

        public Explosion(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void update() {
            currentFrame++;
        }

        public boolean isFinished() {
            return currentFrame >= duration;
        }

        public void render(GraphicsContext gc) {
            double radius = 10 + currentFrame * 1.5;
            gc.setFill(color.deriveColor(1, 1, 1 - (double) currentFrame / duration, 1));
            gc.fillOval(x - radius / 2, y - radius / 2, radius, radius);
        }
    }
}
