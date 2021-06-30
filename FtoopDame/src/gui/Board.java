/*
 * Quelle Regeln: https://de.wikipedia.org/wiki/Dame_(Spiel)
 */

package gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import logic.Move;
import settings.Player;
import settings.Position;
import settings.Theme;

/**
 * Everything related to the game board
 */
public class Board {

	
	/**
	 * Define the pattern for the fields 8x8 = 64 fields
	 */
	public int pattern = 8;
	
	/**
	 * The size of the board
	 */
	private int size = 600;

	/**
	 * This is used to set the current playing player
	 */
	private Player playing;

	/**
	 * Group fields and stones
	 */
	private Group fieldGroup = new Group();
	private Group stoneGroup = new Group();

	/**
	 * Array for both players and fields
	 */
	protected Player[] players = new Player[2];
	protected Field[][] fields;

	/**
	 * Create lock object for threads in this board
	 */
	public Object LOCK = new Object();
	
	/**
	 * Used in threads to check if game is still running
	 */
	protected boolean running = true;
	
	/**
	 * Used to check if game has started
	 */
	private boolean started = false;

	/**
	 * Reference to the game and scene
	 */
	private Game game;
	private Scene scene;
	
	/**
	 * Used to check if the board is run in a simulation or not
	 */
	protected boolean simulation = false;

	/**
	 * Those are used to check for a tie
	 */
	private int movesSinceKill = 0;
	private int movesSinceTransformation = 0;

	/**
	 * Create ArrayList for every situation on the board
	 */
	private List<String> situations = new ArrayList<>();

	/**
	 * Board constructor
	 * @param game set reference to game
	 */
	public Board(Game game) {
		this.game = game;
	}

	/**
	 * Start game if it hasn't started yet, and return the scene
	 * @return current scene of the board
	 */
	public Scene getScene() {
		if (!started) {
			start();
		}

		return scene;
	}

	/**
	 * Start game
	 */
	public void start() {
		
		VBox root = new VBox();
		
		// Create pane for the main board
		Pane board = new Pane();
		board.setPrefSize(size, size);

		board.getChildren().addAll(fieldGroup, stoneGroup);
		
		// Add menu and board to the root (VBOX)
		root.getChildren().addAll(getMenu(), board);

		// Initialize fields and players
		createFields();
		createPlayers();

		scene = new Scene(root);
	}

	/**
	 * Used to generate the menu bar
	 * @return MenuBar
	 */
	private HBox getMenu() {

		MenuBar menuBar = new MenuBar();

		// --- Menu Game
		Menu menuGame = new Menu("Spiel");
		MenuItem newGame = new MenuItem("Neues Spiel");
		newGame.setOnAction(e -> {
			game.setIntro();
		});

		MenuItem restartGame = new MenuItem("Neustarten");
		restartGame.setOnAction(e -> {
			game.restartGame();
		});

		MenuItem endGame = new MenuItem("Beenden");
		endGame.setOnAction(e -> {
			game.getWindow().close();
		});

		menuGame.getItems().addAll(newGame, restartGame, endGame);

		// --- Menu Help
		Menu menuHelp = new Menu("Hilfe");
		MenuItem rules = new MenuItem("Regeln");
		// Open wikipedia link for rules
		rules.setOnAction(e -> {
			try {
				Desktop.getDesktop().browse(new URI("https://de.wikipedia.org/wiki/Dame_(Spiel)"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});

		MenuItem bugReport = new MenuItem("Fehler melden");
		// Open githup issues tab for the project
		bugReport.setOnAction(e -> {
			try {
				Desktop.getDesktop().browse(new URI("https://git.ffhs.ch/alejandro.laneri/ftoopgruppenarbeit/issues"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});

		menuHelp.getItems().addAll(rules, bugReport);

		// Generate spacer between both menus
		Region spacer = new Region();
		spacer.getStyleClass().add("menu-bar");

		MenuBar hintBar = new MenuBar();
		Menu menuHint = new Menu("Tipp");
		MenuItem randomHint = new MenuItem("Belibiger Tipp");

		randomHint.setOnAction(e -> {
			playing.showRandomHint();
		});

		MenuItem allHints = new MenuItem("Alle Zugmöglichkeiten");
		allHints.setOnAction(e -> {
			playing.showAllHints();
		});

		menuHint.getItems().addAll(randomHint, allHints);

		hintBar.getMenus().addAll(menuHint);
		menuBar.getMenus().addAll(menuGame, menuHelp);

		HBox.setHgrow(spacer, Priority.SOMETIMES);
		HBox header = new HBox();
		header.getChildren().addAll(menuBar, spacer, hintBar);

		return header;
	}

	/**
	 * Create fields by defined pattern
	 */
	public void createFields() {
		fields = new Field[pattern][pattern];
		for (int y = 0; y < pattern; y++) {
			for (int x = 0; x < pattern; x++) {
				Field field = new Field(this, (x + y) % 2 != 0, x, y);
				fields[y][x] = field;
				fieldGroup.getChildren().add(field);
			}
		}
	}

	/**
	 * Create both players by set settings
	 */
	public void createPlayers() {

		// Get defined settings defined in the intro
		Integer nonBotPlayers = (Integer) game.getSetting("players");
		Integer player1Color = (Integer) game.getSetting("color");

		// Create players
		players[0] = new Player(this, Position.BOTTOM, (player1Color == 0) ? Theme.WHITE : Theme.BLACK);
		players[1] = new Player(this, Position.TOP, (player1Color == 0) ? Theme.BLACK : Theme.WHITE);

		// Create bots by non bot palyers
		int bots = 2 - nonBotPlayers;

		for (int i = 1; i <= bots; i++) {
			players[2 - i].createBot(true);
		}

		// if both players are a bot set difficulty of first to 1
		if (players[0].isBot()) {
			players[0].getBot().setDifficulty(1);
		}

		players[0].setOpponent(players[1]);
		players[1].setOpponent(players[0]);

		for (Player player : players) {

			// Loop through start fields
			boolean[][] positionFields = player.getStartFields();

			// Black player starts
			if (player.getColor() == Theme.BLACK)
				playing = player;

			for (int y = 0; y < positionFields.length; y++) {
				for (int x = 0; x < positionFields.length; x++) {
					// check if field is equals to true
					if (positionFields[y][x]) {
						// place stone
						Stone stone = new Stone(player, x, y);
						player.addStone(stone);
						fields[y][x].setStone(stone);
						stone.setField(fields[y][x]);
						stoneGroup.getChildren().add(stone);
					}
				}
			}
		}

	}

	/**
	 * Get pattern value of pixel
	 * @param pixel x-or y-position on scene
	 * @return pattern value (Between 1-8)
	 */
	public int onBoard(double pixel) {
		return Math.max(0, Math.min(pattern - 1, (int) (pixel + getFieldSize() / 2) / getFieldSize()));
	}

	/**
	 * @return all fields from board
	 */
	public Field[][] getFields() {
		return fields;
	}

	/**
	 * @return all playing player
	 */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * Stop game and run endscreen
	 * @param winner set winner
	 */
	public void done(Player winner) {
		stop();
		// Call finish if this isn't a simulation
		if (!simulation) {
			Platform.runLater(() -> {
				try {
					new Finish(game, winner);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	/**
	 * @param player set playing player
	 */
	public void setPlaying(Player player) {
		// if provied player hasnt any moves left, end game
		if (player.getMoves().size() == 0) {
			playing = player;
			done(playing.getOpponent());
		} else {

			// Check if player is changing
			if (playing != player) {
				
				// occurrences of current game situation
				int occurrences = 0;

				// only check moves and situation if it isn't a simulation
				if (!simulation) {

					// Add current situation to situations
					String situation = getSituation();
					situations.add(situation);

					// Get amount of occurences
					occurrences = Collections.frequency(situations, situation);

					if (playing.getLastMove() == null || playing.getLastMove().getType() != Move.Type.KILL) {
						movesSinceKill++;
					} else {
						movesSinceKill = 0;
					}

					if (playing.getLastMove() == null || !playing.getLastMove().hasTransformed()) {
						movesSinceTransformation++;
					} else {
						movesSinceTransformation = 0;
					}
				}

				int threshhold = 50;

				// if any of the following conditions is true finish the game without a winner
				if (movesSinceKill > threshhold || movesSinceTransformation > threshhold || occurrences >= 4) {
					done(null);
				} else {
					synchronized (LOCK) {
						// if player is a computer check if he's already started
						// and notify or start him
						if (player.isBot()) {
							if (player.getBot().isAlive()) {
								LOCK.notify();
							} else {
								player.getBot().start();
							}
						}
					}
				}

				playing = player;
			}

		}
	}

	/**
	 * Generate Base-3 string out of current situation
	 * @return return base-3 string
	 */
	private String getSituation() {
		String situation = "";

		for (Field[] row : fields) {
			for (Field field : row) {
				/*
				 * If field has a stone set 2 for white 1 for black else 0 for none
				 */
				if (field.hasStone()) {
					Theme color = field.getStone().getColor();
					if (color == Theme.WHITE) {
						situation += "2";
					} else {
						situation += "1";
					}
				} else {
					situation += "0";
				}
			}
		}

		return situation;
	}

	/**
	 * @return board pattern
	 */
	public int getPattern() {
		return pattern;
	}

	/**
	 * Get amount of starting stones by pattern
	 * @return amount of stones
	 */
	public int getStonePerPlayer() {
		return (pattern / 2 - 1) * pattern / 2;
	}

	/**
	 * Get size of each field by pattern
	 * @return field size
	 */
	public int getFieldSize() {
		return size / pattern;
	}

	/**
	 * Remove stone from the board
	 * @param stone to remove
	 */
	public void removeStone(Stone stone) {
		stoneGroup.getChildren().remove(stone);
	}

	/**
	 * Check if a player is playing
	 * @param player player to check
	 * @return state of the player
	 */
	public boolean isPlaying(Player player) {
		return playing == player;
	}

	/**
	 * @return
	 */
	public Player getPlaying() {
		return playing;
	}

	/**
	 * @return if the game for this board is running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Stop the game
	 */
	public void stop() {
		running = false;
		// Interrupt every bot thread
		for (Player player : players) {
			if (player.isBot()) {
				player.getBot().interrupt();
			}
		}
	}

	/**
	 * @return current game of the board
	 */
	public Game getGame() {
		return game;
	}

}
