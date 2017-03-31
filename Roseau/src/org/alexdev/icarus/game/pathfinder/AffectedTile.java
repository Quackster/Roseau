package org.alexdev.icarus.game.pathfinder;

import java.util.ArrayList;
import java.util.List;

public class AffectedTile
{
	private int x;
	private int y;
	private int i;
	
	public AffectedTile() {
		this.x = 0;
		this.y = 0;
		this.i = 0;
	}
	
	public AffectedTile(int x, int y, int i) {
		this.x = x;
		this.y = y;
		this.i = i;
	}
	
	public static List<AffectedTile> getAffectedTilesAt(int length, int width, int posX, int posY, int rotation) {
		
		List<AffectedTile> points = new ArrayList<AffectedTile>();

		if (length > 1)	{
			if (rotation == 0 || rotation == 4) {
				for (int i = 1; i < length; i++) {
					points.add(new AffectedTile(posX, posY + i, i));

					for (int j = 1; j < width; j++) {
						points.add(new AffectedTile(posX + j, posY + i, (i < j) ? j : i));
					}
				}
			} else if (rotation == 2 || rotation == 6) {
				for (int i = 1; i < length; i++) {
					points.add(new AffectedTile(posX + i, posY, i));

					for (int j = 1; j < width; j++) {
						points.add(new AffectedTile(posX + i, posY + j, (i < j) ? j : i));
					}
				}
			}
		}

		if (width > 1) {
			if (rotation == 0 || rotation == 4) {
				for (int i = 1; i < width; i++) {
					points.add(new AffectedTile(posX + i, posY, i));

					for (int j = 1; j < length; j++) {
						points.add(new AffectedTile(posX + i, posY + j, (i < j) ? j : i));
					}
				}
			} else if (rotation == 2 || rotation == 6) {
				for (int i = 1; i < width; i++) {
					points.add(new AffectedTile(posX, posY + i, i));

					for (int j = 1; j < length; j++) {
						points.add(new AffectedTile(posX + j, posY + i, (i < j) ? j : i));
					}
				}
			}
		}
		
		return points;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getI() {
		return i;
	}
}