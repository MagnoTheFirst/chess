/**
* Dame Spiel
*
* @author Alejandro Laneri, Nicola Lorenz, Ramona Koksa
* @version	1.0 (2019.05.31)
*/

package gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Main class
 */
public class Game extends Application {

	/**
	 * Main window
	 */
	private static Stage window;
	/**
	 * Settings for the game
	 */
	private static Map<String, Object> settings = new HashMap<>();
	/**
	 * The current board set
	 */
	private Board board;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	public void start(Stage stage) {

		// Set window and start intro

		window = stage;
		window.setTitle("Dame");
		setIntro();
		window.show();

	}

	/**
	 * Initialize the Intro
	 */
	public void setIntro() {
		// stop game if its running
		if (board != null && board.isRunning()) {
			board.stop();
		}

		try {
			Intro intro = new Intro(this);
			window.setScene(intro.getScene());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start the game
	 */
	public void startGame() {
		// Create new board
		board = new Board(this);

		window.setScene(board.getScene());
		// Stop running board if the window is closed
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				board.stop();
			}
		});

		// If the first player is a bot, make it start
		if (board.getPlaying().isBot()) {
			board.getPlaying().getBot().start();
		}
	}

	/**
	 * Start a new game with same settings
	 */
	public void restartGame() {
		board.stop();
		startGame();
	}

	/**
	 * @param key
	 *            name of the setting
	 * @param value
	 *            value of the setting
	 */
	public void setSetting(String key, Object value) {
		settings.put(key, value);
	}

	/**
	 * @param key
	 * @return value
	 */
	public Object getSetting(String key) {
		return settings.get(key);
	}

	/**
	 * Get main window
	 * 
	 * @return
	 */
	public Stage getWindow() {
		return window;
	}

	/**
	 * @param scene
	 */
	public void setScene(Scene scene) {
		window.setScene(scene);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
