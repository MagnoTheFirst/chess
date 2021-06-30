package gui;

import java.io.IOException;

import controllers.FinishController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import settings.Player;

/**
 * Finish screen of the game
 */
public class Finish {
	private Scene scene;
	/**
	 * Game the screen belongs to
	 */
	private Game game;

	/**
	 * @param game relation to the game
	 * @param winner winner of the game
	 * @throws IOException
	 */
	public Finish(Game game, Player winner) throws IOException {
		this.game = game;

		// Load fxml
		FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/finish.fxml"));
		Parent layout = loader.load();

		// Get controller set in the fxml and set game and winner
		FinishController controller = (FinishController) loader.getController();
		controller.setGame(game);
		controller.setWinner(winner);

		// create scene and load css file
		scene = new Scene(layout);
		scene.getStylesheets().add(getClass().getResource("resources/intro.css").toExternalForm());

		// create new window
		Stage newWindow = new Stage();
		newWindow.setTitle("Ende");
		newWindow.setScene(scene);
		newWindow.show();

		controller.setWindow(newWindow);

	}

	/**
	 * @return scene
	 */
	public Scene getScene() {
		return scene;
	}
}
