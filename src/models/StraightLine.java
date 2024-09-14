package models;

import wheelsunh.users.Rectangle;

import java.awt.*;

/**
 * StraightLine.java:
 * Creates a straight line tetronimo
 *
 * @author Dominic Cronauer
 * modified from @author Professor Rossi's StraightLine class
 * @see Point
 */
public class StraightLine extends Tetronimo
{
    /**
     * Creates the tetronimo and puts it in the vertical orientation
     */
    public StraightLine()
    {
        /* vert layout default
        2
        1(center)
        3
        4
         */
        super.r2Offset =Tetronimo.SIZE * -1;;
        super.r3Offset =Tetronimo.SIZE;
        super.r4Offset =Tetronimo.SIZE * 2;
        //other offsets are 0 for this
        super.pieceColor=Color.CYAN;
        super.pieceOffset=1;
        finishConstructing();//finish setting up the piece after setting this piece's specific offsets
    }

    /**
     * Gets the width of the tetronimo based on the orientation
     *
     * @return The width of the tetronimo
     */
    @Override
    public int getWidth(){
        if( super.curRotation % 2 == 0 )//vert
        {
            return Tetronimo.SIZE;
        }
        else//horiz
        {
            return Tetronimo.SIZE * 4;
        }
    }

    /**
     * Gets the height of the tetronimo based on the orientation
     *
     * @return The height of the tetronimo
     */
    @Override
    public int getHeight(){
        if( super.curRotation % 2 == 0 )
        {
            return Tetronimo.SIZE * 4;
        }
        else
        {
            return Tetronimo.SIZE;
        }
    }

    /**
     * Implemented abstract from tetronimo. sets the proper TopLeftPosition
     */
    public void SetTopLeftPos(){
        switch(super.curRotation % 4)//handle where to put the top left
        {//arrow's tail is closest to the origin for pieces that aren't centered. pieces rotate about their origin
            case 1://to horiz 1 (←)
            case 2://to vertical 2 (↑)
                super.TopLeftPos = super.r4.getLocation();
                break;
            case 0://to vertical 1 (↓) (default)
            case 3://to horiz 2 (→)
                super.TopLeftPos = super.r2.getLocation();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + super.curRotation % 4);
        }
    }

}


