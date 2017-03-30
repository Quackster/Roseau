package org.alexdev.roseau.game.room.model;

public class Rotation {
	
	public static int calculate(int x1, int y1, int X2, int Y2) {
        int rotation = 0;

        if (x1 > X2 && y1 > Y2)
            rotation = 7;
        else if (x1 < X2 && y1 < Y2)
            rotation = 3;
        else if (x1 > X2 && y1 < Y2)
            rotation = 5;
        else if (x1 < X2 && y1 > Y2)
            rotation = 1;
        else if (x1 > X2)
            rotation = 6;
        else if (x1 < X2)
            rotation = 2;
        else if (y1 < Y2)
            rotation = 4;
        else if (y1 > Y2)
            rotation = 0;
  
        return rotation;
    }
}
