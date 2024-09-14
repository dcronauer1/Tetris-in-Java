package models;

import controllers.TetrisController;
import views.TetrisBoard;
import wheelsunh.users.Rectangle;
import wheelsunh.users.ShapeGroup;

import java.awt.*;

/**
 * Tetronimo.java:
 * An abstract class to model the base capabilities of a tetronimo
 * modified from @author Professor Rossi
 * @implNote finishConstructing() MUST be called at the end of subclasses constructor
 * @author Dominic Cronauer
 * @see Color
 * @see StraightLine
 */
public abstract class Tetronimo extends ShapeGroup
{
    /**
     * Constant to represent the size of the tetronimo
     */
    public static final int SIZE= 20;

    protected Rectangle r1;
    protected Rectangle r2;
    protected Rectangle r3;
    protected Rectangle r4;

    //offsets are stored here, set in the sub clas
    int r2Offset=0;
    int r3Offset=0;
    int r4Offset=0;
    //2nd set of offsets are for the alternate offset (x if the piece starts vertical)
    int r2Offset2=0;
    int r3Offset2=0;
    int r4Offset2=0;

    protected int curRotation = 0;
    public Point TopLeftPos= new Point();//need a new point
    public int pieceOffset;//y offset from top
    public Color pieceColor;
    Point p= new Point(0,0);//temp point
    /**
     * Generates the four rectangles for the tetronino and puts them on the screen, they are at the default coordinates
     * to start
     */
    public Tetronimo()
    {
        super();
        //set initial colors to null because it was possible to briefly see the piece
        this.r1 = new Rectangle();
        r1.setColor(null);
        this.r1.setSize( Tetronimo.SIZE, Tetronimo.SIZE );

        this.r2 = new Rectangle();
        r2.setColor(null);
        this.r2.setSize( Tetronimo.SIZE, Tetronimo.SIZE );

        this.r3 = new Rectangle();
        r3.setColor(null);
        this.r3.setSize( Tetronimo.SIZE, Tetronimo.SIZE );

        this.r4 = new Rectangle();
        r4.setColor(null);
        this.r4.setSize( Tetronimo.SIZE, Tetronimo.SIZE );
    }
    /**
     * Method must be called from subclasses' constructor once the constructor sets the proper offsets
     */
    protected void finishConstructing(){
        super.setLocation(0, 0);
        r1.setLocation( 0, 0);
        r2.setLocation( r2Offset2, r2Offset);
        r3.setLocation( r3Offset2, r3Offset);
        r4.setLocation( r4Offset2, r4Offset);
        super.add( r1 );
        super.add( r2 );
        super.add( r3 );
        super.add( r4 );

        setLocation(1000,0);//default starting location, offscreen
        //set colors at the end cause it was possible to briefly see the piece
        this.r1.setFrameColor( Color.BLACK );
        this.r2.setFrameColor( Color.BLACK );
        this.r3.setFrameColor( Color.BLACK );
        this.r4.setFrameColor( Color.BLACK );
        this.r1.setFillColor(pieceColor);
        this.r2.setFillColor(pieceColor);
        this.r3.setFillColor(pieceColor);
        this.r4.setFillColor(pieceColor);
    }

    /**
     * Handles basic rotation of the Tetronimo
     * Other classes can override this, but shouldnt need to
     * @param playingField - playing field from TetrisBoard
     */
    public void rotate(Rectangle[][] playingField)
    {
        this.curRotation++;
        Point curLoc = super.getLocation();
        super.setLocation(0, 0);
        Point[] tempArrInitial = {r1.getLocation(),r2.getLocation(),r3.getLocation(),r4.getLocation()};
        r1.setLocation(0,0);
        switch(this.curRotation % 4)//handle the 4 rotations
        {//arrow's tail is closest to the origin for pieces that aren't centered. pieces rotate about their origin
            case 1://to horiz 1 (←)
                r2.setLocation(-1*r2Offset, r2Offset2);
                r3.setLocation(-1*r3Offset, r3Offset2);
                r4.setLocation(-1*r4Offset, r4Offset2);
                break;
            case 2://to vertical 2 (↑)
                r2.setLocation(-1*r2Offset2, -1*r2Offset);
                r3.setLocation(-1*r3Offset2, -1*r3Offset);
                r4.setLocation(-1*r4Offset2, -1*r4Offset);
                break;
            case 3://to horiz 2 (→)
                r2.setLocation(r2Offset, -r2Offset2);
                r3.setLocation(r3Offset, -r3Offset2);
                r4.setLocation(r4Offset, -r4Offset2);
                break;
            case 0://to vertical 1 (↓) (default)
                r2.setLocation(r2Offset2, r2Offset);
                r3.setLocation(r3Offset2, r3Offset);
                r4.setLocation(r4Offset2, r4Offset);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + curRotation % 4);
        }
        setLocation( curLoc.x, curLoc.y );
        Point temp = rotateCheck(playingField,false);
        setLocation( curLoc.x-temp.x, curLoc.y-temp.y );
        //failsafe, revert rotation
        if(!checkForOtherPieceCollisions(playingField,0,0,false)){
            //System.out.println("double check failsafe activated, remember to remove this line");
            //double check moving the piece upwards
            temp = rotateCheck(playingField,true);
            setLocation( curLoc.x-temp.x, curLoc.y-temp.y );
            if(!checkForOtherPieceCollisions(playingField,0,0,false)) {
                //System.out.println("true failsafe activated, remember to remove this line");
                super.setLocation(0, 0);
                this.curRotation--;
                Rectangle[] tempArr = {r1, r2, r3, r4};
                for (int i = 0; i < tempArr.length; i++) {
                    tempArr[i].setLocation(tempArrInitial[i]);
                }
                setLocation(curLoc.x, curLoc.y);
            }
        }

    }
    /**
     * Checks if piece would be in bounds when its rotated, should be called by subclasses in their rotate() implementation
     * @param playingField - playing field from TetrisBoard
     * @param prioritizeY - if true, then it will prioritize moving the piece y away from other pieces instead of x
     * @return Point - how far x and y the piece is outside the wall
     */
    public Point rotateCheck(Rectangle[][] playingField,boolean prioritizeY)
    {
        p.setLocation(0,0);//temp point
        double xTopLeft = TopLeftPos.x;
        double yTopLeft = TopLeftPos.y;
        //right side
        if(getWidth()+xTopLeft>TetrisBoard.WIDTH*Tetronimo.SIZE+TetrisBoard.BoardLeftX){
            p.x=(int)(getWidth()+xTopLeft-(TetrisBoard.WIDTH*Tetronimo.SIZE+TetrisBoard.BoardLeftX));//return how far right extra it would be as a pos num

        }//left side
        else if(xTopLeft<TetrisBoard.BoardLeftX) {
            p.x=(int)(xTopLeft - TetrisBoard.BoardLeftX);//return how far left extra it would be as a neg num
        }
        //bottom
        if(getHeight()+yTopLeft>TetrisBoard.HEIGHT*Tetronimo.SIZE){
            p.y=(int)(getHeight()+yTopLeft- TetrisBoard.HEIGHT*Tetronimo.SIZE);
        }
        //check if will go into another piece
        //if its a bad combination, the failsafe in rotate() should handle it (so if the piece is in-between a wall and pieces
        Rectangle[] tempArr = {r1,r2,r3,r4};
        for (Rectangle rectangle : tempArr) {
            int tempX = (rectangle.getXLocation() - TetrisBoard.BoardLeftX) / SIZE;
            int tempY = rectangle.getYLocation() / SIZE;
            try {
                if (playingField[tempX][tempY].getFillColor() != Color.WHITE) {
                    //if here, then something collided
                    //want whichever offset is larger, so abs. if prioritizeY is true, then skip this check
                    if (Math.abs((getWidth()-Tetronimo.SIZE)-(r1.getXLocation()-rectangle.getXLocation())) > p.x && !prioritizeY) {
                        int temp;
                        if((r1.getXLocation()-rectangle.getXLocation())>0){//collision on left of current piece
                            temp=(r1.getXLocation()-rectangle.getXLocation())-(getWidth()-Tetronimo.SIZE);
                        }
                        else{//collision on right of current piece
                            temp=(getWidth()-Tetronimo.SIZE)+(r1.getXLocation()-rectangle.getXLocation());
                        }
                        p.x = temp;

                    }//elseif cause we dont want it moving both an x and y distance for piece collisions
                    else if (getHeight()+yTopLeft-rectangle.getYLocation() > p.y) {
                        p.y = (int) (getHeight()+yTopLeft-rectangle.getYLocation());
                    }

                }
            } catch (ArrayIndexOutOfBoundsException e) {
                //if here, then out of bounds (should've already been accounted for above)
                // so don't do anything

                //System.out.println(e);
            }
        }
        return p;
    }

    /**
     * Method to check if the piece is going to collide given an offset
     * @param playingField the playing field from TetrisBoard
     * @param xSpacesOver x units over to check
     * @param ySpacesOver y units over to check
     * @param careAboutOOB if true, then return false if collided with out of bounds on the **Y** axis
     * @return true if it wont collide
     */
    public boolean checkForOtherPieceCollisions(Rectangle[][] playingField,int xSpacesOver,int ySpacesOver,boolean careAboutOOB){
        Rectangle[] tempArr = {r1,r2,r3,r4};
        for (Rectangle rectangle : tempArr) {
            int tempX = (rectangle.getXLocation() - TetrisBoard.BoardLeftX) / SIZE;
            int tempY = rectangle.getYLocation() / SIZE;
            try {
                if(playingField[tempX+xSpacesOver][tempY+ySpacesOver].getFillColor()!=Color.WHITE) {
                    //System.out.println("will collide");
                    return false;//piece will collide with another piece
                    }
            } catch (ArrayIndexOutOfBoundsException e) {
                if(careAboutOOB && (tempY+ySpacesOver<0 || tempY+ySpacesOver>TetrisBoard.HEIGHT-1))
                    return false;//collided out of bounds
                if(tempX+xSpacesOver<0 || tempX+xSpacesOver>TetrisBoard.WIDTH-1){
                    return false;//collided out of l/r bounds
                }
            }
        }
        return true;//piece is good to do whatever
    }

    /**
     * Shifts the tetronimo left one row
     * checks for collisions before doing so
     */
    public void shiftLeft(Rectangle[][] playingField)
    {
        if(checkForOtherPieceCollisions(playingField,-1,0,false))
            this.setLocation( super.getXLocation() - Tetronimo.SIZE, super.getYLocation() );
    }
    /**
     * Shifts the tetronimo right one row
     * checks for collisions before doing so
     * @param playingField - the playing field from TetrisBoard
     */
    public void shiftRight(Rectangle[][] playingField)
    {
        if(checkForOtherPieceCollisions(playingField,1,0,false))
            this.setLocation( super.getXLocation() + Tetronimo.SIZE, super.getYLocation() );
    }

    /**
     * this fixes initial piece placement. sets the top left pos when moving the piece
     * @param x - x coordinate
     * @param y - y coordinate
     */
    @Override
    public void setLocation(int x, int y){
        super.setLocation( x, y );
        SetTopLeftPos();
    }
    /**
     * this fixes initial piece placement. sets the top left pos when moving the piece
     * @param p point
     */
    @Override
    public void setLocation(Point p){
        super.setLocation(p);
        SetTopLeftPos();
    }

    /**
     * Subclasses should set this to set the appropriate top left pos based on its rotation
     * @see StraightLine
     */
    public abstract void SetTopLeftPos();

    /**
     * Method to set the colors on the TetrisBoard's Grid after a piece places
     * Will also detect when the game is over
     * @param playingField - the playing field from TetrisBoard
     * @return the y grid coordinates that the piece occupied
     */
    public int[] SetColorsOnGrid(Rectangle[][] playingField){
        Rectangle[] tempArr = {r1,r2,r3,r4};
        int[] returnTempArray = {0,0,0,0};
        boolean gameLost=false;
        for (int i=0;i<tempArr.length;i++) {
            int tempX = (tempArr[i].getXLocation() - TetrisBoard.BoardLeftX) / SIZE;
            int tempY = tempArr[i].getYLocation() / SIZE;
            returnTempArray[i]=tempY;
            try {
                playingField[tempX][tempY].setFillColor(pieceColor);
                //I spent too long trying to figure out how to actually delete the pieces, but I give up
                tempArr[i].setColor(null);
            } catch (ArrayIndexOutOfBoundsException e) {
                if(tempArr[i].getYLocation()<0){
                    //if the piece places oob above the screen, then game over
                    gameLost=true;
                }else
                    throw new RuntimeException(e);//something went wrong
            }
        }
        if(gameLost)
            TetrisController.GameLost();
        this.setLocation(800, 0);//move the piece just in case
        r1=r2=r3=r4=null;//i dont think this does anything but it doesnt hurt to put it here
        TopLeftPos=p=null;
        return returnTempArray;
    }
}