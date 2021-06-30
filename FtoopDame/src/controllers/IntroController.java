package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;

/**
 * fxml controller for the intro screen
 */
public class IntroController extends BaseController {

	/**
	 * Used to define the game mode
	 */
	@FXML
	ChoiceBox<String> mode;
	/**
	 * Used to define the arrangement
	 */
	@FXML
	ChoiceBox<String> arrangement;
	/**
	 * Use to define the difficulty
	 */
	@FXML
	Slider difficulty;

	/**
	 * Set all needed settings and start the game
	 * @param event ActionEvent
	 */
	@FXML
	private void initGame(ActionEvent event) {
		game.setSetting("players", mode.getSelectionModel().getSelectedIndex());
		game.setSetting("color", arrangement.getSelectionModel().getSelectedIndex());
		game.setSetting("difficulty", ((Double) (difficulty.getValue())).intValue());
		game.startGame();
	}
}
