import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Jumpx - A Dino jumping game inspired by Google Chrome Dino Game.
 *
 * First Java application project (December 2023).
 * Developed using Java Swing, event handling,
 * and object-oriented programming principles.
 *
 * Features:
 * - Keyboard-controlled jumping
 * - Obstacle collision detection
 * - Coin collection and score system
 * - High score tracking
 */
public class Jumpx extends JFrame implements ActionListener, KeyListener {
    private BufferedImage dinoImage;
    private BufferedImage groundImage;
    private BufferedImage coinImage;
    private BufferedImage sunflowerImage;
    private int dinoY;
    private static int dinoX = 100;
    private final int GROUND;
    private int dinoSpeed = 0;
    private boolean jumping = false;
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Star> stars = new ArrayList<>();
    private final List<Moon> moons = new ArrayList<>();
    private int odelay = 5000;
    private Timer obstacleTimer;
    private int score = 0;
    private int scoreIncrease = 1;
    private Timer scoreTimer;
    private Timer starTimer;
    private Timer moonTimer;
    Random random = new Random();
    private Clip jumpSound;
    private HighScoreList highScoreList;

    public Jumpx() {
        // Design the frame.
        setTitle("Jumping");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Read the dinoImage file.
        try {
            dinoImage = ImageIO.read(new File("dino.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GROUND = 355 - dinoImage.getHeight();
        dinoY = GROUND;

        // Read the groundImage file.
        try {
            groundImage = ImageIO.read(new File("ground.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            coinImage = ImageIO.read(new File("coin.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            sunflowerImage = ImageIO.read(new File("sunflower.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadJumpSound();

        // The game main timer.
        Timer timer = new Timer(20, this);
        timer.start();

        // The obstacle timer. Every 4-5 seconds(randomly), call the method "generateObstacle".
        odelay = random.nextInt(1000) + 4000;
        obstacleTimer = new Timer(odelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateObstacle();
            }
        });
        obstacleTimer.start();

        // The star timer. Every 6 seconds, call the method "generateStar".
        starTimer = new Timer(6000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateStar();
            }
        });
        starTimer.start();

        // The score timer. The score is based on the time. Every one second, add one score.
        scoreTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                score += scoreIncrease;
            }
        });
        scoreTimer.start();

        // The moon timer. Every 10 seconds, call the method "generateMoon".
        moonTimer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMoon();
            }

        });
        moonTimer.start();

        addKeyListener(this);
        setFocusable(true);

        highScoreList = new HighScoreList();
    }

    private void loadJumpSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("jump.wav"));
            jumpSound = AudioSystem.getClip();
            jumpSound.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Create new obstacles, new yellow circles(stars) and orange triangle(moons).
    private void generateObstacle() {
        int obstacleWidth = 10 + random.nextInt(50);
        int obstacleHeight = 20 + random.nextInt(50);
        int obstacleX = getWidth();
        int obstacleY = 355 - obstacleHeight;

        Obstacle obstacle = new Obstacle(obstacleX, obstacleY, obstacleWidth, obstacleHeight);
        obstacles.add(obstacle);
    }

    private void generateStar() {
        int sd = 20;
        int sx = getWidth();
        int sy = 200;

        Star star = new Star(sx, sy, sd);
        stars.add(star);
    }

    private void generateMoon() {
        int mx = getWidth();
        int my = 150;

        Moon moon = new Moon(mx, my);
        moons.add(moon);
    }

    public void actionPerformed(ActionEvent e) {

        if (jumping) {
            dinoY -= dinoSpeed;
            dinoSpeed--;

            if (dinoY > GROUND) {
                dinoY = GROUND;
                jumping = false;
            }
        }

        moveObstacles();
        moveStars();
        moveMoons();

        if (pickStar()) {
            increaseScore();
        }

        if (pickMoon()) {
            speedUp();
        }

        if (checkCollision()) {
            endGame();
        }

        repaint();
    }

    private void moveStars() {
        for (int i = 0; i< stars.size(); i++) {
            stars.get(i).move();
            if (stars.get(i).getSX() + stars.get(i).getSD() < 0) {
                stars.remove(i);
            }
        }
    }

    private void moveObstacles() {
        for (int j = 0; j< obstacles.size(); j++) {
            obstacles.get(j).move();
            if(obstacles.get(j).getX() + obstacles.get(j).getWidth() < 0) {
                obstacles.remove(j);
            }
        }
    }

    private void moveMoons() {
        for (int m = 0; m< moons.size(); m++) {
            moons.get(m).move();
            if(moons.get(m).getMX() + 40 < 0) {
                moons.remove(m);
            }
        }
    }

    private boolean pickStar() {
        // Check if the dino touches the stars.
        Rectangle rectRect2 = new Rectangle(dinoX, dinoY, 50, 50);
        for (Star star : stars) {
            Rectangle starRect = new Rectangle(star.getSX(), star.getSY(), star.getSD(), star.getSD());

            //If the dino touches the star(yellow circles), the star disappears.
            if (rectRect2.intersects(starRect)) {
                int index = stars.indexOf(star);
                stars.remove(index);
                return true;
            }
        }
        return false;
    }

    private void increaseScore() {
        score += 2;
    }

    private boolean pickMoon() {
        Rectangle rectRect3 = new Rectangle(dinoX, dinoY, 50, 50);
        for (Moon moon : moons) {
            Rectangle moonRect = new Rectangle(moon.getMX(), moon.getMY(), 40, 40);

            if (rectRect3.intersects(moonRect)) {
                int indexM = moons.indexOf(moon);
                moons.remove(indexM);
                return true;
            }
        }
        return false;
    }

    // If the dino touches the moons, obstacles move faster and scores increase faster( 1 sec + 2 scores).
    private void speedUp() {
        Obstacle.increaseSpeed(2);
        scoreIncrease = 2;
    }

    private boolean checkCollision() {
        Rectangle rectRect1 = new Rectangle(dinoX, dinoY, 50, 50);
        for (Obstacle obstacle : obstacles) {
            Rectangle obstacleRect = new Rectangle(obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight());

            if (rectRect1.intersects(obstacleRect)) {
                return true;
            }
        }
        return false;
    }

    private void endGame() {
        obstacleTimer.stop();
        scoreTimer.stop();
        starTimer.stop();

        int highestScore = highScoreList.getHighestScore();
        if (score > highestScore) {
            highScoreList.addScore(score);
            JOptionPane.showMessageDialog(this, "New High Score: " + score);
        } else {
            int result = JOptionPane.showConfirmDialog(this,
                    "Your Score: " + score + "\nHighest Score: " + highestScore + "\nDo you want to play again?",
                    "Game Over", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                // Restart the game
                restartGame();
            } else {
                // Close the application
                System.exit(0);
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE ) {
            jumping = true;
            // Set the initial velocity.
            dinoSpeed = 13;
            playJumpSound();
        }
    }

    private void playJumpSound() {
        if (jumpSound.isRunning()) {
            jumpSound.stop();
        }
        jumpSound.setFramePosition(0);
        jumpSound.start();
    }


    public void keyReleased(KeyEvent e) {
        //rise = false;
    }
    public void keyTyped(KeyEvent e) {
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(new Color(173,216,230));
        g.fillRect(0,0,getWidth(),getHeight());

        g.drawImage(groundImage, 0, 350, this);

        g.drawImage(dinoImage, dinoX, dinoY, this);

        for (Obstacle obstacle : obstacles) {
            g.setColor(Color.BLUE);
            g.fillRect(obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight());
        }

        for (Star star : stars) {
            int largerSize = 2 * star.getSD();
            g.drawImage(coinImage, star.getSX(), star.getSY(),largerSize, largerSize,this);
        }

        for(Moon moon : moons) {
            int scaledWidth = 50;
            int scaledHeight = 50;
            g.drawImage(sunflowerImage, moon.getMX(), moon.getMY(), scaledWidth,scaledHeight,this);

        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 20, 70);

        Toolkit.getDefaultToolkit().sync();//Important
    }

    private void restartGame() {
        dinoY = 355 - dinoImage.getHeight();
        dinoSpeed = 0;
        jumping = false;
        obstacles.clear();
        Obstacle.resetSpeed();
        stars.clear();
        moons.clear();
        odelay = 5000;
        score = 0;
        scoreIncrease = 1;

        obstacleTimer.restart();
        starTimer.restart();
        moonTimer.restart();
        scoreTimer.restart();

        repaint();
    }

    public static void main(String[] args) {
        Jumpx game = new Jumpx();
        game.setVisible(true);
    }
}

class Obstacle {
    private int x;
    private int y;
    private int width;
    private int height;
    private static int speed = 5;

    public Obstacle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move() {
        x -= speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSpeed() {
        return speed;
    }

    public static void increaseSpeed (int amount) {
        speed += amount;
    }

    public static void resetSpeed(){
        speed = 5;
    }
}

class Star {
    private int sx;
    private int sy;
    private int sd;

    public Star (int sx, int sy, int sd) {
        this.sx = sx;
        this.sy = sy;
        this.sd = sd;
    }

    public void move() {
        sx -= 5;
    }

    public int getSX() {
        return sx;
    }

    public int getSY() {
        return sy;
    }

    public int getSD() {
        return sd;
    }
}

class Moon {
    private int mx;
    private int my;

    public Moon(int mx, int my) {
        this.mx = mx;
        this.my = my;
    }

    public void move() {
        mx -= 5;
    }

    public int getMX() {
        return mx;
    }

    public int getMY() {
        return my;
    }
}

class HighScoreList {
    private List<Integer> scores;

    public HighScoreList() {
        scores = new ArrayList<>();
    }

    public void addScore(int score) {
        scores.add(score);
        Collections.sort(scores, Collections.reverseOrder()); // Sort in descending order
    }

    public int getHighestScore() {
        if (scores.isEmpty()) {
            return 0; // No scores available
        }
        return scores.get(0);
    }
}
