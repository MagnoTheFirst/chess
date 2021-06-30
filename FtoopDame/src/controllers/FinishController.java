package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import settings.Player;

/**
 * fxml controller for the finish screen
 */
public class FinishController extends BaseController {

	/**
	 * Reference for the new opened window
	 */
	private Stage window;

	/**
	 * Reference to the title field, used to display the winner
	 */
	@FXML
	Label title;

	// Restart the game after click on restart
	/**
	 * Restart the game if the restart button gets pressed
	 * @param event ActionEvent
	 */
	@FXML
	private void restartGame(ActionEvent event) {
		game.setIntro();
		window.close();
	}

	/**
	 * Set winning player
	 * @param winner indicates the winning player
	 */
	public void setWinner(Player winner) {
		// If the winner is equals to "null" the game is drawd
		if (winner == null) {
			title.setText("Untentschieden");
		} else {
			title.setText(winner.getColor().getName() + " gewinnt!");
		}
	}

	/**
	 * Set the window that needs to be closed later
	 * @param window 
	 */
	public void setWindow(Stage window) {
		this.window = window;
	}
}
