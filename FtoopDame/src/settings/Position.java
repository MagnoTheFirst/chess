package settings;

import java.util.Map;

import gui.Board;
import gui.Field;
import gui.Game;

/**
 * Player position and its playing direction
 */
public enum Position {
	TOP(1), BOTTOM(-1);

	/**
	 * Playing direction of the position
	 */
	final int dir;

	Position(int dir) {
		this.dir = dir;
	}

	public int getDir() {
		return dir;
	}
}
