package models;

import wheelsunh.users.Rectangle;

import java.awt.*;

/**
 * Square.java:
 * Creates a Square tetronimo
 *
 * @author Dominic Cronauer
 * @see Point
 */
public class Square extends Tetronimo
{
    /**
     * Creates the tetronimo and puts it in the vertical orientation
     */
    public Square()
    {
        /* layout
        1 2
        3 4 (center in the middle)
         */
        super.r2Offset =0;
        super.r3Offset =Tetronimo.SIZE;
        super.r4Offset =Tetronimo.SIZE;
        super.r2Offset2 =Tetronimo.SIZE;
        super.r3Offset2 =0;
        super.r4Offset2 =Tetronimo.SIZE;
        super.pieceColor=Color.YELLOW;
        super.pieceOffset=0;

        finishConstructing();//finish setting up the piece after setting this piece's specific offsets
    }

    /**
     * Rotates the tetronimo
     */
    @Override
    public void rotate(Rectangle[][] playingField) {
        this.curRotation++;
        Point curLoc = super.getLocation();
        setLocation( curLoc.x, curLoc.y );

        SetTopLeftPos();//set the appropriate top left position
    }

    /**
     * Gets the width of the tetronimo based on the orientation
     *
     * @return The width of the tetronimo
     */
    @Override
    public int getWidth(){
        return Tetronimo.SIZE * 2;
    }

    /**
     * Gets the height of the tetronimo based on the orientation
     *
     * @return The height of the tetronimo
     */
    @Override
    public int getHeight(){
        return Tetronimo.SIZE * 2;
    }

    /**
     * Implemented abstract from tetronimo. sets the proper TopLeftPosition
     */
    public void SetTopLeftPos() {
        super.TopLeftPos = super.r1.getLocation();
    }
}


