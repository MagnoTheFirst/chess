package settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import gui.Board;
import gui.Field;
import gui.Game;
import gui.Stone;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import logic.Bot;
import logic.Move;

/**
 * Player of the game
 */
public class Player {
	/**
	 * Position of the player (Top or Bottom)
	 */
	private Position position;
	/**
	 * Color of the player (Black or White)
	 */
	private Theme color;
	/**
	 * The assigned bot to the player
	 */
	private Bot bot;
	/**
	 * Its opponent
	 */
	private Player opponent;
	/**
	 * All avaible stones
	 */
	private List<Stone> stones = new ArrayList<>();

	/**
	 * Last moved stone
	 */
	private Stone movedStone = null;
	/**
	 * All avaiable moves
	 */
	private List<Move> moves = new ArrayList<>();
	/**
	 * The board the player belongs to
	 */
	private Board board;
	/**
	 * Last move made
	 */
	private Move lastMove = null;
	/**
	 * Amount of hints showing
	 */
	private int hintsShowing = 0;

	/**
	 * @param board
	 * @param position
	 * @param color
	 */
	public Player(Board board, Position position, Theme color) {
		this.board = board;
		this.position = position;
		this.color = color;
	}

	/**
	 * @param isBot boolean if bot should be created
	 */
	public void createBot(boolean isBot) {
		if (isBot) {
			this.bot = new Bot(this);
		} else {
			this.bot = null;
		}
	}

	/**
	 * @return
	 */
	public Position getPosition() {
		return this.position;
	}

	/**
	 * @return
	 */
	public Theme getColor() {
		return this.color;
	}

	/**
	 * @return
	 */
	public Bot getBot() {
		return this.bot;
	}

	/**
	 * @return
	 */
	public boolean isBot() {
		return bot != null;
	}

	/**
	 * @return
	 */
	public boolean canPlay() {
		return board.isPlaying(this) && hintsShowing == 0;
	}

	/**
	 * @return
	 */
	public boolean isPlaying() {
		return board.isPlaying(this);
	}

	/**
	 * @param opponent
	 */
	public void setOpponent(Player opponent) {
		this.opponent = opponent;
	}

	/**
	 * @return
	 */
	public Player getOpponent() {
		return this.opponent;
	}

	/**
	 * clear all stones for this player
	 */
	public void clearStones() {
		stones.clear();
	}

	/**
	 * @param stone add new stone
	 */
	public void addStone(Stone stone) {
		stones.add(stone);
	}

	/**
	 * @return
	 */
	public List<Stone> getStones() {
		return stones;
	}

	/**
	 * @return
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * @return
	 */
	public Move getLastMove() {
		return lastMove;
	}

	/**
	 * @param move
	 */
	public void setLastMove(Move move) {
		lastMove = move;
	}

	/**
	 * @param stone to remove
	 */
	public void removeStone(Stone stone) {
		stones.remove(stone);
		stone.getField().setStone(null);
		// Callupdate
		Platform.runLater(() -> {
			board.removeStone(stone);
		});
	}

	/**
	 * @param stone that has been moved
	 */
	public void setMovedStone(Stone stone) {
		this.movedStone = stone;
	}

	/**
	 * Show a random hint for this player
	 */
	public void showRandomHint() {
		List<Move> moves = getMoves();

		Random rand = new Random();
		Move move = moves.get(rand.nextInt(moves.size()));

		hintsShowing = 1;

		showHint(move, 0);
	}

	/**
	 * Show all avaible hints for this player
	 */
	public void showAllHints() {

		List<Move> moves = getMoves();
		hintsShowing = moves.size();

		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i);
			int delay = i * 1000;

			showHint(move, delay);
		}
	}

	/**
	 * @param move that should be shown
	 * @param delay 
	 */
	public void showHint(Move move, int delay) {

		Stone stone = move.getStone();
		Field field = move.getField();

		stone.getLayoutX();
		stone.toFront();
		Path path = new Path();
		// Start position of the transition
		double start = stone.getCenter() + field.getSize() / 2 - stone.getRadius();

		double distanceX = field.getLayoutX() - stone.getField().getLayoutX() + start;
		double distanceY = field.getLayoutY() - stone.getField().getLayoutY() + start;

		path.getElements().add(new MoveTo(start, start));
		path.getElements().add(new LineTo(distanceX, distanceY));
		// Create path transition
		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(1000));
		pathTransition.setDelay(Duration.millis(delay));
		pathTransition.setPath(path);
		pathTransition.setNode(stone);
		pathTransition.setOrientation(PathTransition.OrientationType.NONE);
		pathTransition.setCycleCount(1);
		pathTransition.play();
		pathTransition.setOnFinished(e -> {
			pathTransition.playFromStart();
			pathTransition.jumpTo(Duration.millis(0));
			pathTransition.stop();
			hintsShowing--;
		});

	}

	/**
	 * @return
	 */
	public List<Move> getMoves() {
		moves.clear();
		// If a stone is already moved, only this stone can be played again
		if (movedStone != null) {
			// select only kill moves
			moves = movedStone.getMoves().stream().filter(move -> move.getType() == Move.Type.KILL)
					.collect(Collectors.toList());
		} else {
			List<Move> allMoves = new ArrayList<>();
			for (Stone stone : stones) {
				allMoves.addAll(stone.getMoves());
			}

			// Check if any kill moves are avaiable
			List<Move> killMoves = allMoves.parallelStream().filter(move -> move.getType() == Move.Type.KILL)
					.collect(Collectors.toList());

			// let player only make kill moves
			moves = killMoves.size() > 0 ? killMoves : allMoves;
		}

		// playable = stones;
		return moves;
	}

	/**
	 * @return
	 */
	public boolean[][] getStartFields() {

		int marked = 0;
		Field[][] boardFields = null;
		boolean[][] fields = new boolean[board.getPattern()][board.getPattern()];

		switch (position.getDir()) {
		case 1:
			boardFields = board.getFields();
			break;

		case -1:
			Field[][] tempFields = board.getFields();
			int j = tempFields.length;
			boardFields = new Field[tempFields.length][];
			for (int i = 0; i < tempFields.length; i++) {
				boardFields[j - 1] = tempFields[i];
				j = j - 1;
			}
			break;
		}

		outerloop: for (Field[] row : boardFields) {
			for (Field field : row) {
				// only place on dark fields
				if (field.isDark()) {
					if (marked < board.getStonePerPlayer()) {
						Map<String, Integer> coords = field.getCoords();
						fields[coords.get("y")][coords.get("x")] = true;
						marked++;
					} else {
						break outerloop;
					}
				}
			}
		}

		return fields;
	}
}
