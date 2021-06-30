package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import gui.Game;
import javafx.fxml.Initializable;

/**
 * This is the base class for all fxml controllers
 */
public class BaseController {

	protected Game game;

	/**
	 * @param game Reference to the current game
	 */
	public void setGame(Game game) {
		this.game = game;
	}
}
