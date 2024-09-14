package views;

import controllers.TetrisController;
import models.Tetronimo;
import wheelsunh.users.*;
import wheelsunh.users.Frame;
import wheelsunh.users.Rectangle;
import wheelsunh.users.TextBox;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;
import java.util.Scanner;

/**
 * TetrisBoard.java:
 * Class to model the tetris board
 * @author Dominic Cronauer
 * modified from @author Professor Rossi
 *
 * @see Color
 * @see KeyListener
 * @see KeyEvent
 */
public class TetrisBoard implements KeyListener
{
    /**
     * Constant to represent the width of the board
     */
    public static final int WIDTH = 10;

    /**
     * Constant to represent the height of the board
     */
    public static final int HEIGHT = 24;
    //board's left x coord
    public static final int BoardLeftX = 40;

    private final TetrisController CONTROLLER;
    private Tetronimo tetronimo;
    private Rectangle[][] playingField;
    private boolean tetronimoCanMove = false;//true if the piece can move
    private boolean moveFaster=false;//true if down arrow is being held
    private int dropTile=1;//1 if spacebar isnt being held, 0 otherwise
    private boolean instantPlaceTile=false;
    public int score;
    private final TextBox scoreText;
    private final Scanner scanner;
    /**
     * Constructor to initialize the board
     *
     * @param frame The wheelsunh frame (so we can add this class as a key listener for the frame)
     */
    public TetrisBoard( Frame frame )
    {
        frame.addKeyListener( this );
        this.CONTROLLER = new TetrisController( this );

        this.buildBoard(false);
        scoreText = new TextBox("Score: "+score);
        scoreText.setLocation(300,0);
        this.tetronimo=CONTROLLER.getNextTetromino();//get the initial piece
        scanner = new Scanner(System.in);//open scanner for newGame()
        this.run();

    }
    /**
     * Builds the playing field for tetris
     * @param newGame if its a new game, dont make a new Rectangle[][]
     */
    private void buildBoard(boolean newGame)
    {
        if(!newGame) {
            this.playingField = new Rectangle[WIDTH][HEIGHT];
        }

        for ( int i = 0; i < TetrisBoard.WIDTH; i++ )
        {
            for ( int j = 0; j < TetrisBoard.HEIGHT; j++ ) {
                if (!newGame){//if its a new game, dont do this stuff again
                    this.playingField[i][j] = new Rectangle();
                    this.playingField[ i ][ j ].setLocation( i * 20 + BoardLeftX, j * 20 );
                    this.playingField[ i ][ j ].setSize( Tetronimo.SIZE, Tetronimo.SIZE );
                }
                this.playingField[ i ][ j ].setColor( Color.WHITE );
                this.playingField[ i ][ j ].setFrameColor( Color.BLACK );
            }
        }
    }

    /**
     * Starts gameplay and is responsible for keeping the game going
     */
    public void run() {
        while (TetrisController.keepPlaying){
            this.tetronimo=CONTROLLER.getNextTetromino();//make the tetronomino that is displayed next
            CONTROLLER.playNextTetromino(this.tetronimo);//play the next tetronimo
            tetronimoCanMove=true;
            //put a check here if new piece immediately collides. do a while loop and keep moving it up if it collides. if it goes off screen completely then end the game
            int tempLandedCounter=0;
            do{
                if(moveFaster)
                    Utilities.sleep(50 *dropTile);
                else {
                    //sleeps multiple times cause I couldn't get .wake working
                    for(int i=0;i<6;i++) {//300ms or nothing if spacebar is pressed
                        Utilities.sleep(50 * dropTile);
                    }

                }

                //move the piece down if it didnt land
                if(!this.CONTROLLER.tetronimoLanded( this.tetronimo)) {//check just incase piece rotates into a landing position
                    this.tetronimo.setLocation(this.tetronimo.getXLocation(), this.tetronimo.getYLocation() + Tetronimo.SIZE);
                }
                //let the piece still be moved for a bit after landing
                //if player is holding down arrow, it will land instantly
                //if the player is holding space, it will land faster
                if(this.CONTROLLER.tetronimoLanded( this.tetronimo ) && tempLandedCounter<4){
                    tempLandedCounter++;
                    int tempCounter=0;
                    instantPlaceTile=false;//if user taps space here the piece will insta-place
                    do{//sleeps tempCounter amount of times cause I couldnt get .wake working
                        Utilities.sleep( (int)(100*(dropTile+0.75)) );
                        tempCounter++;

                    }while(this.CONTROLLER.tetronimoLanded( this.tetronimo ) && tempCounter<6 && !moveFaster && !instantPlaceTile);
                }
            }while( !this.CONTROLLER.tetronimoLanded( this.tetronimo ));
            //place the piece
            tetronimoCanMove=false;
            int[] temp = this.tetronimo.SetColorsOnGrid(playingField);//update the playing field
            //returns the y grid coordinates of the placed piece for below
            if(TetrisController.keepPlaying) {//check if game was lost, if it was then skip and exit loop
                if (this.CONTROLLER.clearLines(temp)) {
                    scoreText.setText("Score: " + score);//update score
                    Utilities.sleep(500);//wait a little bit
                }
                Utilities.sleep(200);//wait till next piece}
            }
        }
        this.tetronimo = null;
        newGame();//see if the player wants to play again and handle accordingly
    }

    /**
     * Method newGame prompts the player if they would like to play a new game, and handles starting a new game
     * terminates program if user does not
     */
    private void newGame(){
        System.out.println("Play a new game? Y/N: ");
        String choice = scanner.next();  // Read user input
        if(Objects.equals(choice, "Y") || Objects.equals(choice, "y")){
            this.buildBoard(true);
            score=0;
            scoreText.setText("Score: " + score);//update score
            dropTile=1;//redundant
            tetronimoCanMove = false;//redundant
            moveFaster=false;//redundant
            TetrisController.keepPlaying=true;
            Utilities.sleep(1000);//wait 1 second
            this.run();//run a new game
        }
        else {//if anything else just exit
            System.out.println("Goodbye");
            scanner.close();//close the scanner
            System.exit(0);//terminate the program
        }
    }

    /**
     * Getter method for the array representing the playing field, not used yet but will be needed by the controller later
     *
     * @return The playing field
     */
    public Rectangle[][] getPlayingField()
    {
        return playingField;
    }

    /**
     * This method is not used in this program
     *
     * @param e The key event
     */
    @Override
    public void keyTyped( KeyEvent e )
    {
        //not in use
    }

    /**
     * Handles the key events by the user (INCOMPLETE)
     *
     * @param e The key event
     */
    @Override
    public void keyPressed( KeyEvent e )
    {
        int key = e.getKeyCode();

        if( this.tetronimo == null || !tetronimoCanMove)
        {
            return;
        }
        switch( key )
        {
            case 38:
                this.tetronimo.rotate(playingField);
                break;
            case 37:
                if( this.tetronimo.TopLeftPos.x > BoardLeftX )
                {
                    this.tetronimo.shiftLeft(playingField);
                }
                break;
            case 39:
                if( (this.tetronimo.TopLeftPos.x + this.tetronimo.getWidth()) < ((TetrisBoard.WIDTH * Tetronimo.SIZE) + BoardLeftX))
                {
                    this.tetronimo.shiftRight(playingField);
                }
                break;
            case 40://down arrow
                moveFaster=true;
                //t.notify();//wake up the thread to move faster //scrapped
                break;
            case 32://spacebar
                dropTile=0;
                instantPlaceTile=true;//only used when the piece is about to drop. set to false right before
                //t.notify();//wake up the thread to drop the tile
                break;
        }
    }

    /**
     * This method is not used in this program
     *
     * @param e The key event
     */
    @Override
    public void keyReleased( KeyEvent e )
    {
        int key = e.getKeyCode();
        switch( key )
        {
            case 38:
                break;
            case 37:
                break;
            case 39:
                break;
            case 40://down arrow
                moveFaster=false;
                break;
            case 32://spacebar
                dropTile=1;
                break;
        }
    }
}