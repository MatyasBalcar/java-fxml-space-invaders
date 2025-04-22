import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class GameController {
    public final int maxAmountOfBulletsConst = 30;
    private final int sizeX = 275;
    private final int sizeY = 750;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private final List<Explosion> explosions = new ArrayList<>();
    private final Map<Enemy, Explosion> pendingExplosions = new HashMap<>();
    private final int bulletDelayConstant = 50;
    public int currentAmountOfBullets = 0;
    @FXML
    public Text ScoreField;
    @FXML
    public Text HealthField;
    @FXML
    public Text mainText;
    @FXML
    ProgressBar progressBar = new ProgressBar(0);
    @FXML
    private Canvas gameCanvas;
    @FXML
    private Button startButton;
    private boolean gameStarted = false;
    private AnimationTimer timer;
    private GraphicsContext gc;
    private Player player;
    private final Random rand = new Random();
    private int score = 0;
    private int bulletDelay = 0;
    private final ArrayList<Integer> levels = new ArrayList<>(List.of(1, 2, 3, 4, 5));
    private int currentLevel = 0;

    private int health = 3;

    Font font = Font.loadFont(getClass().getResource("/pixel-font/PixelatedEleganceRegular-ovyAA.ttf").toExternalForm(), 45);


    String musicFile = "shoot.mp3";
    Media sound = new Media(new File(musicFile).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(sound);

    String deathFile = "deathxd.mp3";
    Media deathSound = new Media(new File(deathFile).toURI().toString());
    MediaPlayer deathPlayer = new MediaPlayer(deathSound);

    String backgroundFile = "backgroundmusic.mp3";
    Media backgroundSound = new Media(new File(backgroundFile).toURI().toString());
    MediaPlayer backgroundPlayer = new MediaPlayer(backgroundSound);




    @FXML
    public void initialize() {
        if (font == null) {
            System.out.println("Font failed to load!");
        }
        mainText.setFont(font);
        gc = gameCanvas.getGraphicsContext2D();
        player = new Player(sizeX, sizeY-50, 100, 100);
        backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundPlayer.setVolume(0.5);
        backgroundPlayer.play();

        Platform.runLater(() -> {
            gameCanvas.getScene().setOnKeyPressed(e -> keysPressed.add(e.getCode()));
            gameCanvas.getScene().setOnKeyReleased(e -> keysPressed.remove(e.getCode()));
            gameCanvas.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("stylesheet.css")).toExternalForm());
        });


        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameStarted && update() != 1) {
                    deathPlayer.play();
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    this.stop();
                    Platform.exit();
                }
                if (gameStarted) render();
            }
        };
    }

    @FXML
    private void onStartButtonClick() {
        startButton.setVisible(false);
        gameStarted = true;
        timer.start();
    }


    private int update() {
        ScoreField.setText("Score: " + score + " points");
        HealthField.setText("Health: " + health + " ❤");
        progressBar.setProgress((double) bulletDelay / bulletDelayConstant);

        if (bulletDelay > 0) bulletDelay--;
        if (health <= 0) return 0;

        if (enemies.isEmpty() && pendingExplosions.isEmpty()) {
            spawnWave(levels.get(currentLevel), 5);
            bullets.clear();
            currentAmountOfBullets = 0;
            currentLevel = Math.min(currentLevel + 1, levels.size() - 1);
        }

        if (keysPressed.contains(KeyCode.LEFT)) player.move(-5);
        if (keysPressed.contains(KeyCode.RIGHT)) player.move(5);
        if (keysPressed.contains(KeyCode.SPACE) && currentAmountOfBullets < maxAmountOfBulletsConst && bulletDelay == 0) {
            bullets.add(new Bullet(player.getX() + 45, player.getY()));
            bulletDelay = bulletDelayConstant;
            currentAmountOfBullets++;
            mediaPlayer.stop();
            mediaPlayer.play();
        }
        //iter
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.getY() > sizeY + 50) {
                health--;
                enemyIterator.remove();
                continue;
            }
            enemy.move(1);
        }

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

        player.render(gc);


        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }



        gc.setFill(Color.RED);
        for (Bullet bullet : bullets) {
            gc.fillRect(bullet.getX(), bullet.getY(), 5, 10);
        }

        for (Explosion explosion : explosions) {
            explosion.render(gc);
        }

        explosions.removeIf(Explosion::isFinished);
    }

    public static class Explosion {
        private final double x;
        private final double y;
        private final int duration = 20;
        private int currentFrame = 0;
        private final Color color = Color.ORANGE;

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
