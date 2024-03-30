import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyCorgi extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 500;
    int boardHeight = 680;

    //images
    Image backgroundImg;
    Image corgiImg;
    Image topBoneImg;
    Image bottomBoneImg;

    //corgi class
    int corgiX = boardWidth/8;
    int corgiY = boardWidth/2;
    int corgiWidth = 70;
    int corgiHeight = 50;

    class Corgi {
        int x = corgiX;
        int y = corgiY;
        int width = corgiWidth;
        int height = corgiHeight;
        Image img;

        Corgi(Image img) {
            this.img = img;
        }
    }

    //bone class
    int boneX = boardWidth;
    int boneY = 0;
    int boneWidth = 70;  //scaled by 1/6
    int boneHeight = 412;
    
    class Bone {
        int x = boneX;
        int y = boneY;
        int width = boneWidth;
        int height = boneHeight;
        Image img;
        boolean passed = false;

        Bone(Image img) {
            this.img = img;
        }
    }

    //game logic
    Corgi corgi;
    int velocityX = -4; //move bones to the left speed (simulates corgi moving right)
    int velocityY = 0; //move corgi up/down speed.
    int gravity = 1;

    ArrayList<Bone> bones;
    Random random = new Random();

    Timer gameLoop;
    Timer placeBoneTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyCorgi() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappycorgibg.png")).getImage();
        corgiImg = new ImageIcon(getClass().getResource("./flappycorgi.png")).getImage();
        topBoneImg = new ImageIcon(getClass().getResource("./topbone.png")).getImage();
        bottomBoneImg = new ImageIcon(getClass().getResource("./bottombone.png")).getImage();

        //corgi
        corgi = new Corgi(corgiImg);
        bones = new ArrayList<Bone>();

        //place bones timer
        placeBoneTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // Code to be executed
              placeBone();
            }
        });
        placeBoneTimer.start();
        
		//game timer
		gameLoop = new Timer(1000/60, this); //how long it takes to start timer, milliseconds gone between frames 
        gameLoop.start();
	}
    
    void placeBone() {
        //(0-1) * BoneHeight/2.
        // 0 -> -128 (BoneHeight/4)
        // 1 -> -128 - 256 (boneHeight/4 - boneHeight/2) = -3/4 boneHeight
        int randomBoneY = (int) (boneY - boneHeight/4 - Math.random()*(boneHeight/2));
        int openingSpace = boardHeight/4;
    
        Bone topBone = new Bone(topBoneImg);
        topBone.y = randomBoneY;
        bones.add(topBone);
    
        Bone bottomBone = new Bone(bottomBoneImg);
        bottomBone.y = topBone.y  + boneHeight + openingSpace;
        bones.add(bottomBone);
    }
    
    
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        //corgi
        g.drawImage(corgiImg, corgi.x, corgi.y, corgi.width, corgi.height, null);

        //bones
        for (int i = 0; i < bones.size(); i++) {
            Bone bone = bones.get(i);
            g.drawImage(bone.img, bone.x, bone.y, bone.width, bone.height, null);
        }

        //score
        g.setColor(Color.white);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
        
	}

    public void move() {
        //corgi
        velocityY += gravity;
        corgi.y += velocityY;
        corgi.y = Math.max(corgi.y, 0); //apply gravity to current corgi.y, limit the corgi.y to top of the canvas

        //bones
        for (int i = 0; i < bones.size(); i++) {
            Bone bone = bones.get(i);
            bone.x += velocityX;

            if (!bone.passed && corgi.x > bone.x + bone.width) {
                score += 0.5; //0.5 because there are 2 bones! so 0.5*2 = 1, 1 for each set of bones
                bone.passed = true;
            }

            if (collision(corgi, bone)) {
                gameOver = true;
            }
        }

        if (corgi.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Corgi a, Bone b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) { //called every x milliseconds by gameLoop timer
        move();
        repaint();
        if (gameOver) {
            placeBoneTimer.stop();
            gameLoop.stop();
        }
    }  

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // System.out.println("JUMP!");
            velocityY = -9;

            if (gameOver) {
                //restart game by resetting conditions
                corgi.y = corgiY;
                velocityY = 0;
                bones.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placeBoneTimer.start();
            }
        }
    }

    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
