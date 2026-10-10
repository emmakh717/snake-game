import java.awt.*; // Imports the core classes for creating GUIs in Java 
import java.awt.event.*; // Imports the event classes used to handle user input 
import java.util.ArrayList;
import java.util.Random; // Imports the Random class to generate random numbers 
import javax.imageio.ImageIO; // Imports the ImageIO class from the javax.imageio package to read and write image files 
import javax.swing.*; // Imports all the classes in the javax.swing package that has the core classes for creating GUIs 
import java.io.*; // Imports all the classes in the java.io package that has the core classes for input and output operations

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x; 
        int y;
    
        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    int boardWidth; 
    int boardHeight;
    int tileSize = 25;

    // snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // food
    Tile food;
    Image foodImage;
    
    // A random number generator used to place the food on the game board
    Random random;
    
    // game logic
    int velocityX; 
    int velocityY;
    Timer gameLoop;
    boolean gameOver = false;

    /**
     *  Constructs a new SnakeGame object with the given board dimensions.
     * @param boardWidth the width of the game board
     * @param boardHeight the height of the game board
     */
    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);

        // Add this panel as a key listener to detect user input 
        addKeyListener(this);

        // Make the panel focusable so that key events are triggered
        setFocusable(true);

        // Initialize snake
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();
        
        // Load food image
        try {
            foodImage = ImageIO.read(new File("Apple.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize food and random object
        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        // Initialize game loop timer
        velocityX = 0;
        velocityY = 0;
        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    /**
     * Draws the current state of the game on the screen.
     * @param g the Graphics object used to draw on the screen
     */
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    /**
     * Draws the game objects on the screen
     * @param g the Graphics object used to draw on the screen
     */
    public void draw(Graphics g){
        for(int i = 0; i < boardWidth/tileSize; i++){
            g.drawLine(i*tileSize, 0, i*tileSize, boardHeight);
            g.drawLine(0, i*tileSize, boardWidth, i*tileSize);
        }

        // Draw food image
        g.drawImage(foodImage, food.x*tileSize, food.y*tileSize, tileSize, tileSize, this);

        // Draw snake head
        g.setColor(Color.green);
        g.fillRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize);
        
        // Draw snake body
        for(Tile part : snakeBody){
            g.fillRect(part.x*tileSize, part.y*tileSize, tileSize, tileSize);
        }
        
        // Shows the Score
        g.setFont(new Font("Arial", Font.PLAIN, 16)); 
        if (gameOver){
            g.setColor(Color.red);
            g.drawString("Game Over: " + snakeBody.size(), tileSize - 16, tileSize);
            g.drawString("Press Enter to Restart", tileSize - 16, tileSize + 20); 
        } else {
            g.drawString("Score: " + snakeBody.size(), tileSize - 16, tileSize);
        }
    }

    // Method to place food randomly on the board
    public void placeFood(){
        food.x = random.nextInt(boardWidth/tileSize);
        food.y = random.nextInt(boardHeight/tileSize);
    }

    // Method to move the snake and check for collisions
    public void move(){
        // Check for collision between the snake head and the food
        if (collision(snakeHead, food)){
            snakeBody.add(new Tile(food.x, food.y));
            // Place new food on the board
            placeFood();
        }

        // Move snake body
        for (int i = snakeBody.size() - 1; i > 0; i--){
            // Set the current tile to previous tile's position
            snakeBody.get(i).x = snakeBody.get(i-1).x;
            snakeBody.get(i).y = snakeBody.get(i-1).y;
        }
        
        if(!snakeBody.isEmpty()){
            snakeBody.get(0).x = snakeHead.x;
            snakeBody.get(0).y = snakeHead.y;
        }

        // Move snake head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Check for collisions between the snake head and the snake body
        for (Tile part : snakeBody){
            if(collision(snakeHead, part)){
                gameOver = true;
            }
        }

        // Check for collisions between the snake head and game board boundaries
        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize || snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize){
            gameOver = true;
        }

        // Repaint the panel
        repaint();
    }

    // Collision detection
    public boolean collision(Tile t1, Tile t2){
        return t1.x == t2.x && t1.y == t2.y;
    }

    public void actionPerformed(ActionEvent e){
        if (!gameOver){
            move();
        }
    }

    // Keys used to control the snake
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && velocityX == 0){
            velocityX = -1;
            velocityY = 0;
        } else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && velocityX == 0){
            velocityX = 1;
            velocityY = 0;
        } else if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && velocityY == 0) {
            velocityX = 0;
            velocityY = -1;
        } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && velocityY == 0) {
            velocityX = 0;
            velocityY = 1;
        } else if (key == KeyEvent.VK_ENTER && gameOver) {
            restart();
        }
    }

    public void keyTyped(KeyEvent e){}

    public void keyReleased(KeyEvent e){}

    // Restart the game
    public void restart(){
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        gameOver = false;
        velocityX = 0;
        velocityY = 0;
        placeFood();
        repaint();
    }
}