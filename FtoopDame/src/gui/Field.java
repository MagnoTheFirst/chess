package gui;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Instance of a field from the board
 */
public class Field extends Rectangle {

	/**
	 * Instance for the stone on the field
	 */
	private Stone stone;
	/**
	 * Coords of the field on the board as map (x,y)
	 */
	private Map<String, Integer> coords;
	/**
	 * used to check if its a dark field
	 */
	private boolean dark;
	/**
	 * the board the field is placed on
	 */
	private Board board;

	/**
	 * @param board the board the field belongs to
	 * @param dark if its a dark field
	 * @param x position on the board
	 * @param y position on the board
	 */
	public Field(Board board, boolean dark, int x, int y) {
		this.board = board;
		// Save coords of field
		coords = new HashMap<>();
		coords.put("x", x);
		coords.put("y", y);

		// Set field size
		setWidth(getSize());
		setHeight(getSize());

		// place field
		relocate(x * getSize(), y * getSize());

		// set color
		this.dark = dark;
		setFill(dark ? Color.valueOf("#3F4D59") : Color.valueOf("#F1F2F4"));
	}

	/**
	 * @param stone that has been placed on the field
	 */
	public void setStone(Stone stone) {
		this.stone = stone;
		if (stone != null)
			stone.setField(this);
	}

	/**
	 * Check if the field has a stone
	 * @return if board has stone
	 */
	public boolean hasStone() {
		return stone != null;
	}

	/**
	 * Get the current stone placed to the field
	 * @return the stone on the field
	 */
	public Stone getStone() {
		return stone;
	}

	/**
	 * Check if field is dark
	 * @return
	 */
	public boolean isDark() {
		return dark;
	}

	/**
	 * Get coordinations of the field
	 * @return coords
	 */
	public Map<String, Integer> getCoords() {
		return coords;
	}

	/**
	 * Get field size defined in the board
	 * @return field size
	 */
	public int getSize() {
		return board.getFieldSize();
	}

}
