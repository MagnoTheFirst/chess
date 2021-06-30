package settings;

import javafx.scene.paint.Color;

/**
 * All settings for a theme
 */
public enum Theme {
	/**
	 * Set values (Fill color, Stroke color, if this theme starts, name of the theme)
	 */
	BLACK("#151624", "#3F4D59", true, "Schwarz"),
	WHITE("#F1F2F4", "#3F4D59", false, "Weiss");

	/**
	 * fill color
	 */
	final String fill;
	/**
	 * Stroke color
	 */
	final String stroke;
	/**
	 * If the player of this theme starts
	 */
	final boolean begins;
	/**
	 * Name of the theme
	 */
	final String name;

	/**
	 * @param fill
	 * @param stroke
	 * @param begins
	 * @param name
	 */
	Theme(String fill, String stroke, boolean begins, String name) {
		this.fill = fill;
		this.stroke = stroke;
		this.begins = begins;
		this.name = name;
	}

	/**
	 * @return
	 */
	public Color getFill() {
		return Color.valueOf(this.fill);
	}

	/**
	 * @return
	 */
	public Color getStroke() {
		return Color.valueOf(this.stroke);
	}

	/**
	 * @return
	 */
	public boolean starts() {
		return begins;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
}
