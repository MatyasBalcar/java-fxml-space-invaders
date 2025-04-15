import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.*;

public class GameController {
    public final int maxAmountOfBulletsConst = 30;
    private final int sizeX = 275;
    private final int sizeY = 750;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final Set<KeyCode> keysPressed = new HashSet<>();
    public int currentAmountOfBullets = 0;
    @FXML
    public Text ScoreField;
    Random rand = new Random();
    @FXML
    private Canvas gameCanvas;
    private int score = 0;
    private GraphicsContext gc;
    private Player player;

    private int bulletDelay = 0;
    private final int bulletDelayConstant = 50;

    private ArrayList<Integer> levels = new ArrayList<>(List.of(1,2,3,4,5));
    private int currentLevel = 0;

    @FXML
    ProgressBar progressBar = new ProgressBar(0);

    @FXML
    public void initialize() {


        gc = gameCanvas.getGraphicsContext2D();

        player = new Player(sizeX, sizeY, 50, 20);




        // Wait until the canvas is added to the scene
        javafx.application.Platform.runLater(() -> {
            gameCanvas.getScene().setOnKeyPressed(e -> keysPressed.add(e.getCode()));
            gameCanvas.getScene().setOnKeyReleased(e -> keysPressed.remove(e.getCode()));
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (update() != 1) {
                    this.stop();//to tu asi byt nemusi? vypina se platform ale tak coz
                    Platform.exit();
                }
                render();
            }
        };
        timer.start();
    }


    private int update() {
        //Updating score and bullet bar
        ScoreField.setText("Score: " + score);
        progressBar.setProgress((double) bulletDelay /bulletDelayConstant);


        if(bulletDelay > 0){
            bulletDelay--;
        }

        if(enemies.isEmpty()){
            spawnWave( levels.get(currentLevel),5);
            bullets.clear();
            currentAmountOfBullets = 0;
            currentLevel++;
        }

        // Movement
        if (keysPressed.contains(KeyCode.LEFT)) {
            player.move(-5);
        }
        if (keysPressed.contains(KeyCode.RIGHT)) {
            player.move(5);
        }
        if (keysPressed.contains(KeyCode.SPACE) && currentAmountOfBullets < maxAmountOfBulletsConst && bulletDelay  == 0) {
            bullets.add(new Bullet(player.getX() + 20, player.getY()));
            bulletDelay = bulletDelayConstant;
            currentAmountOfBullets++;


        }

        for (Enemy enemy : enemies) {
            enemy.move(1);
            if (enemy.getY() > sizeY + 50) return 0;
        }

        // Update bullets
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();
            if (bullet.getY() < 0) removeBullet(bulletIterator);

            for (Enemy enemy : enemies) {
                if (enemy.collidesWith(bullet)) {
                    killEnemy(enemy);
                    removeBullet(bulletIterator);
                    break;
                }
            }
        }
        return 1;
    }

    private void removeBullet(Iterator<Bullet> iterator) {
        iterator.remove();
        currentAmountOfBullets--;
    }

    private void killEnemy(Enemy enemy) {
        enemies.remove(enemy);
        //Adds a new enemy
        //enemies.add(new Enemy(100 + rand.nextInt(4) * 80, 100 + rand.nextInt(8) * 40, 40, 20));
        score++;

    }

    /// Cols is max 4
    ///  Rows is technicaly unlimited
    private void spawnWave(int rows, int cols) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                enemies.add(new Enemy(100 + j * 80, 100 + i * 80, 40, 20));
            }
        }
    }

    private void render() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw player
        gc.setFill(Color.BLUE);
        gc.fillRect(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        // Draw enemies
        for (Enemy enemy : enemies) {
            gc.setFill(enemy.getColor());
            gc.fillRect(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
        }

        // Draw bullets
        gc.setFill(Color.RED);
        for (Bullet bullet : bullets) {
            gc.fillRect(bullet.getX(), bullet.getY(), 5, 10);
        }
    }
}
