package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import logic.Move;
import settings.Player;
import settings.Theme;

/**
 * This class is used for the stones and its main interactions
 */
public class Stone extends StackPane {

	/**
	 * Mouse x and y if the stone is dragged
	 */
	private double mouseX, mouseY;

	/**
	 * Origin position of the stone before its moved
	 */
	private double oldX, oldY;

	/**
	 * Stone radius
	 */
	private double radius;
	/**
	 * Center of the stone
	 */
	private double center;
	/**
	 * Color of the stone
	 */
	private Theme color;
	/**
	 * The current field the stone is placed on
	 */
	private Field field;
	/**
	 * The player the stone belongs to
	 */
	private Player player;
	/**
	 * If the stone is a queen
	 */
	private boolean queen = false;

	/**
	 * @param player the stone belongs to
	 * @param x position on the board
	 * @param y position on the board
	 */
	public Stone(Player player, int x, int y) {
		this.player = player;
		this.color = player.getColor();
		
		// Move stone to x and y position on the board
		move(x, y);

		// Create radius from field size
		radius = player.getBoard().getFieldSize() / 2 * 0.65;

		// Design visual of the stone
		Circle bg = new Circle();
		bg.setRadius(radius);
		bg.setFill(color.getFill());
		bg.setStroke(color.getStroke());
		bg.setStrokeWidth(2);

		Circle inner = new Circle();
		inner.setRadius(radius - 4);
		inner.setFill(color.getFill());
		inner.setStroke(color.getStroke());
		inner.setStrokeWidth(2);

		center = player.getBoard().getFieldSize() / 2 - radius;

		bg.setTranslateX(center);
		bg.setTranslateY(center);

		inner.setTranslateX(center);
		inner.setTranslateY(center);

		getChildren().addAll(bg, inner);

		// Set player interactions if player isn't a bot
		if (!player.isBot()) {
			setOnMousePressed(e -> {
				if (player.canPlay()) {
					toFront();
					mouseX = e.getSceneX();
					mouseY = e.getSceneY();
				}
			});

			setOnMouseDragged(e -> {
				if (player.canPlay()) {
					relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
				}
			});

			setOnMouseReleased(e -> {
				synchronized (player.getBoard().LOCK) {
					if (player.canPlay()) {
						// Get location on the board from x-,y-cordinations
						int newX = player.getBoard().onBoard(getLayoutX());
						int newY = player.getBoard().onBoard(getLayoutY());

						Field newField = player.getBoard().getFields()[newY][newX];
						
						// Run the move
						Move move = new Move(player, this, newField);
						move.run();
					}
				}
			});
		}
	}

	/**
	 * This returns all posible moves for this stone, without player restrictions
	 * @return moves for the  stone
	 */
	public List<Move> getMoves() {
		List<Move> moves = new ArrayList<>();

		for (Field[] row : player.getBoard().getFields()) {
			for (Field field : row) {
				Move move = new Move(player, this, field);
				if (move.getType() != Move.Type.NONE) {
					moves.add(move);
				}
			}
		}

		return moves;

	}

	/**
	 * Move stone to a given field
	 * @param field
	 */
	public void move(Field field) {
		Map<String, Integer> coords = field.getCoords();
		int newX = coords.get("x");
		int newY = coords.get("y");

		move(newX, newY);
	}

	/**
	 * Move stone to a given location
	 * @param x
	 * @param y
	 */
	public void move(double x, double y) {
		oldX = x * player.getBoard().getFieldSize();
		oldY = y * player.getBoard().getFieldSize();

		relocate(oldX, oldY);
	}

	/**
	 * Set field for stone
	 * @param field Field
	 */
	public void setField(Field field) {
		this.field = field;
	}

	/**
	 * Get current field from stone
	 * @return Field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * @return Theme
	 */
	public Theme getColor() {
		return color;
	}

	/**
	 * @return
	 */
	public double getCenter() {
		return center;
	}

	/**
	 * @return
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @return x position on board
	 */
	public int getX() {
		return player.getBoard().onBoard(oldX);
	}

	/**
	 * @return y position on board
	 */
	public int getY() {
		return player.getBoard().onBoard(oldY);
	}

	/**
	 * Abort move and relocate to old position
	 */
	public void abort() {
		relocate(oldX, oldY);
	}

	/**
	 * Transform stone to queen
	 */
	public void transform() {
		queen = true;
		Circle bg = new Circle();
		bg.setRadius(radius);
		bg.setFill(color.getFill());
		bg.setStroke(Color.valueOf("#73071F"));
		bg.setStrokeWidth(2);

		Circle inner = new Circle();
		inner.setRadius(radius - 4);
		inner.setFill(color.getFill());
		inner.setStroke(Color.valueOf("#73071F"));
		inner.setStrokeWidth(2);

		double center = player.getBoard().getFieldSize() / 2 - radius;

		bg.setTranslateX(center);
		bg.setTranslateY(center);

		inner.setTranslateX(center);
		inner.setTranslateY(center);

		Platform.runLater(() -> {
			getChildren().addAll(bg, inner);
		});
	}

	/**
	 * @return is the stone a queen
	 */
	public boolean isQueen() {
		return queen;
	}
}
