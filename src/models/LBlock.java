package models;

import wheelsunh.users.Rectangle;

import java.awt.*;

/**
 * LBlock.java:
 * Creates a L Block tetronimo
 *
 * @author Dominic Cronauer
 * @see Point
 */
public class LBlock extends Tetronimo
{
    /**
     * Creates the tetronimo and puts it in the vertical orientation
     */
    public LBlock()
    {
        /* vert layout default
        2
        1(center)
        3 4
         */
        super.r2Offset =-Tetronimo.SIZE;
        super.r3Offset =Tetronimo.SIZE;
        super.r4Offset =Tetronimo.SIZE;
        super.r2Offset2 =0;
        super.r3Offset2 =0;
        super.r4Offset2 =Tetronimo.SIZE;
        super.pieceColor=Color.ORANGE;
        super.pieceOffset=1;
        finishConstructing();//finish setting up the piece after setting this piece's specific offsets
    }

    /**
     * Rotates the tetronimo
     */
    @Override
    public void rotate(Rectangle[][] playingField) {
        super.rotate(playingField);
        SetTopLeftPos();//set the appropriate top left position
    }

    /**
     * Gets the width of the tetronimo based on the orientation
     *
     * @return The width of the tetronimo
     */
    @Override
    public int getWidth(){
        if( super.curRotation % 2 == 0 )
        {
            return Tetronimo.SIZE*2;
        }
        else
        {
            return Tetronimo.SIZE * 3;
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
            return Tetronimo.SIZE * 3;
        }
        else
        {
            return Tetronimo.SIZE*2;
        }
    }

    /**
     * Implemented abstract from tetronimo. sets the proper TopLeftPosition
     */
    public void SetTopLeftPos(){
        switch(super.curRotation % 4)//handle where to put the top left
        {//arrow's tail is closest to the origin for pieces that aren't centered. pieces rotate about their origin
            case 0://to vertical 1 (↓) (default)
                super.TopLeftPos = super.r2.getLocation();
                break;
            case 1://to horiz 1 (←)
                super.TopLeftPos = super.r3.getLocation();
                break;
            case 2://to vertical 2 (↑)
                super.TopLeftPos = super.r4.getLocation();
                break;
            case 3://to horiz 2 (→)
                /*
                    4
                2 1 3
                 */
                super.TopLeftPos.x = super.r2.getXLocation();
                super.TopLeftPos.y = super.r4.getYLocation();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + super.curRotation % 4);
        }
    }

}