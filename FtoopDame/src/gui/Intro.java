package gui;

import java.io.IOException;

import controllers.IntroController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Intro of the game
 */
public class Intro {

	private Scene scene;
	/**
	 * Game where the intro belongs to
	 */
	private Game game;

	/**
	 * @param game
	 * @throws IOException
	 */
	public Intro(Game game) throws IOException {
		this.game = game;

		// Load fxml
		FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/intro.fxml"));
		Parent root = loader.load();

		// Get fxml controller
		IntroController controller = (IntroController) loader.getController();
		controller.setGame(game);

		// Load style for the intro
		scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("resources/intro.css").toExternalForm());

	}

	/**
	 * @return
	 */
	public Scene getScene() {
		return scene;
	}

}
