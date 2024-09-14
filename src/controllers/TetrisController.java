package controllers;

import models.*;
import views.TetrisBoard;
import wheelsunh.users.Rectangle;

import java.awt.*;
import java.util.Random;

/**
 * TetrisController.java:
 * Class to hold all the game logic for tetris
 * modified from @author Professor Rossi's version
 * @author Dominic Cronauer
 */
public class TetrisController
{
    private final TetrisBoard TETRIS_BOARD;
    private static Tetronimo tetronimo1;
    private static Tetronimo tetronimo2;

    private static int pieceCounter=0;
    public static boolean keepPlaying=true;
    /**
     * Constructor to take in a tetris board so the controller and the board can communicate
     *
     * @param tetrisBoard A tetris board instance
     */
    public TetrisController( TetrisBoard tetrisBoard )
    {
        this.TETRIS_BOARD = tetrisBoard;
    }

    /**
     * Randomly chooses the next tetronimo and returns it
     *
     * @return The next tetronimo to be played
     */
    public Tetronimo getNextTetromino()
    {
        // create instance of Random class
        Tetronimo tetronimoTemp = null;
        Random rand = new Random();
        int randInt = rand.nextInt(7);
        rand =null;
        //pick the piece at random
        switch(randInt)
        {
            case 0:
                tetronimoTemp = new StraightLine();
                break;
            case 1:
                tetronimoTemp = new Square();
                break;
            case 2:
                tetronimoTemp = new SBlock();
                break;
            case 3:
                tetronimoTemp = new ZBlock();
                break;
            case 4:
                tetronimoTemp = new TBlock();
                break;
            case 5:
                tetronimoTemp = new JBlock();
                break;
            case 6:
                tetronimoTemp = new LBlock();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + randInt);
        }
        pieceCounter++;//alternate between 1 and 2
        //first set the piece offscreen
        tetronimoTemp.setLocation( TetrisBoard.BoardLeftX + (12 * Tetronimo.SIZE), 60);
        //get the next piece, return the previous next piece
        if(pieceCounter % 2==0) {
            tetronimo1 = tetronimoTemp;
            return tetronimo2;
        }
        else{
            tetronimo2=tetronimoTemp;
            return tetronimo1;
        }
    }

    /**
     * Method Play the next tetromino
     * @param tetronimo the tetronimo to play
     */
    public void playNextTetromino(Tetronimo tetronimo){
        tetronimo.setLocation( TetrisBoard.BoardLeftX + (5 * Tetronimo.SIZE), 0);
        //check if the piece spawned in another piece here, and handle
        int temp=0;
        while(!tetronimo.checkForOtherPieceCollisions(TETRIS_BOARD.getPlayingField(),0,0,false)){
            temp+=tetronimo.SIZE;
            //System.out.println("here");
            tetronimo.setLocation( TetrisBoard.BoardLeftX + (5 * Tetronimo.SIZE), -temp);
        }
    }

    /**
     * Method to determine if the tetronimo has landed
     *
     * @param tetronimo The tetronimo to evaluate
     * @return True if the tetronimo has landed (on the bottom of the board or another tetronimo), false if it has not
     */
    public boolean tetronimoLanded( Tetronimo tetronimo ) {
        int nextY = tetronimo.TopLeftPos.y + tetronimo.getHeight() + Tetronimo.SIZE;
        if(nextY > 480) {
            return true;
        }
        //check for collisions with other pieces 1 space below
        if(!tetronimo.checkForOtherPieceCollisions(TETRIS_BOARD.getPlayingField(),0,1,false)) {
            return true;
        }
        return false;
    }

    /**
     * Method to check if lines can be cleared and to clear them
     * @param yCoordsToCheck : y grid coordinates to be checked
     * @return true if lines were cleared
     */
    public boolean clearLines(int[] yCoordsToCheck){
        Rectangle[][] board = TETRIS_BOARD.getPlayingField();
        boolean isRowFull=true;
        boolean clearedRows=false;
        int howManyRows=0;
        int[] yCoordsFull = new int[4];
        for (int i=0;i<yCoordsToCheck.length;i++) {
            isRowFull=true;
            for(int j=0;j<board.length;j++){
                if(board[j][yCoordsToCheck[i]].getFillColor()== Color.WHITE){
                    isRowFull=false;//found a white piece, so no
                    break;//break current for loop
                }
            }
            if(isRowFull){//row is full, handle
                clearedRows=true;
                boolean alreadyFoundThisY=false;
                for(int yCoordsSoFar : yCoordsFull){
                    if(yCoordsToCheck[i]==yCoordsSoFar){
                        alreadyFoundThisY=true;
                        break;
                    }
                }
                if(!alreadyFoundThisY) {//add this y to the array
                    yCoordsFull[howManyRows] = yCoordsToCheck[i];
                    howManyRows++;
                }
            }
        }
        if(clearedRows) {//a row was cleared
            if (howManyRows == 4) {
                //big score here
                TETRIS_BOARD.score += 800;
            } else
                TETRIS_BOARD.score += 100 * howManyRows;


            //also need to actually move rows down
            int currentShiftCount = 0;//used to keep track of how much to shift the row
            for (int i = TetrisBoard.HEIGHT - 1; i >= 0; i--) {
                if (currentShiftCount < howManyRows) {//determine how many rows we need to shift this row
                    currentShiftCount = 0;
                    for (int j = 0; j < howManyRows; j++) {
                        if (yCoordsFull[j] > i) {
                            currentShiftCount++;//add 1 to the shift count
                        }
                    }
                } else {
                    currentShiftCount = howManyRows;//already at max shift
                }
                if (currentShiftCount > 0) {
                    for (int j = 0; j < TetrisBoard.WIDTH; j++) {
                        board[j][i + currentShiftCount].setFillColor(board[j][i].getFillColor());//shift it
                        if (i<howManyRows){//clear top rows
                            board[j][i].setFillColor(Color.WHITE);
                        }
                    }
                }
            }
        }
        return clearedRows;//true if a row was cleared
    }

    /**
     * Method called when the game is lost
     */
    public static void GameLost(){
        keepPlaying=false;
        System.out.println("You lost!");
    }
}
