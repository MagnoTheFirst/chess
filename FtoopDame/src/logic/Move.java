package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gui.Board;
import gui.Field;
import gui.Stone;
import settings.Player;

/**
 * Run the move the player makes
 */
public class Move implements Comparable<Move> {

	/**
	 * Types of moves possible
	 */
	public enum Type {
		NONE, MOVE, KILL
	}

	/**
	 * Default move type is set to NONE
	 */
	Type type = Type.NONE;
	/**
	 * The stone thats moved
	 */
	Stone stone;
	/**
	 * The destination field
	 */
	Field field;
	/**
	 * Killed stone 
	 */
	Stone killed;
	/**
	 * Player making the move
	 */
	Player player;
	/**
	 * Moves possible after this move (only kill moves)
	 */
	List<Move> nextMoves = new ArrayList<>();
	/**
	 * Score of the move (used for bots)
	 */
	private Integer score = 0;
	/**
	 * if stone gets transformed during this move (used for bots)
	 */
	private boolean transformed = false;

	/**
	 * @param player making the move
	 * @param stone that gets moved
	 * @param field the stone gets moved to
	 */
	public Move(Player player, Stone stone, Field field) {
		this.field = field;
		this.stone = stone;
		this.player = player;

		// target location
		int x = field.getCoords().get("x");
		int y = field.getCoords().get("y");

		// Return false move if field isn't dark or has a stone on it
		if (!field.isDark() || field.hasStone()) {
			return;
		}

		// get start location
		int startX = stone.getX();
		int startY = stone.getY();

		int distanceX = Math.abs(x - startX);
		int distanceY = Math.abs(y - startY);
		int directionX = (x - startX) < 0 ? -1 : 1;
		int directionY = (y - startY) < 0 ? -1 : 1;

		int maxDistance = stone.isQueen() ? player.getBoard().getPattern() : 1;

		// Check if stone is moving in right direction
		if (!stone.isQueen() && directionY != player.getPosition().getDir())
			return;

		// Check if move is diagonal
		if (distanceX == distanceY && distanceX <= maxDistance + 1) {

			if (distanceX > 1) {
				// check if there is a stone
				List<Field> enemies = new ArrayList<>();
				for (int i = 1; i < distanceX; i++) {
					Field[][] fields = player.getBoard().getFields();

					int tempX = startX + (i * directionX);
					int tempY = startY + (i * directionY);
					Field tempField = fields[tempY][tempX];

					if (tempField.hasStone()) {
						// Return if field has own stones
						if (tempField.getStone().getColor() == stone.getColor())
							return;

						enemies.add(tempField);
						// if there are more than one enemy return
						if (enemies.size() > 1)
							return;
					}
				}

				if (enemies.size() == 1) {
					Map<String, Integer> enemyCoords = enemies.get(0).getCoords();
					Map<String, Integer> fieldCoords = field.getCoords();

					// check if landing position is behind enemy
					if (enemyCoords.get("x") + directionX == fieldCoords.get("x")
							&& enemyCoords.get("y") + directionY == fieldCoords.get("y")) {
						this.killed = enemies.get(0).getStone();
						this.type = Type.KILL;
					}
					return;
				} else if (distanceX <= maxDistance) {
					this.type = Type.MOVE;
				}

			} else {
				this.type = Type.MOVE;
			}
		}

	}

	/**
	 * 
	 */
	public void run() {

		Field oldField = stone.getField();

		// Change type to none if player doesn't contain this move
		if (!player.getMoves().contains(this)) {
			type = Type.NONE;
		}

		// Set player latest move
		player.setLastMove(this);

		// Check for transformation
		if ((type == Type.KILL || type == Type.MOVE) && !stone.isQueen()) {
			switch (player.getPosition().getDir()) {
			case -1:
				if (field.getCoords().get("y") == 0) {
					transformed = true;
				}
				break;

			case 1:
				if (field.getCoords().get("y") == player.getBoard().getPattern() - 1) {
					transformed = true;
				}
				break;
			}
		}

		switch (type) {
		// If type is none abort move
		case NONE:
			stone.abort();
			break;
		// If its a regular move, move the stone and change player
		case MOVE:
			stone.move(field);
			field.setStone(stone);
			oldField.setStone(null);
			player.getBoard().setPlaying(player.getOpponent());
			break;
		// if its a kill move, move the stone, remove stone from opponent and check if a next move is possible
		case KILL:
			stone.move(field);
			field.setStone(stone);
			oldField.setStone(null);
			player.getOpponent().removeStone(killed);
			nextMoves = stone.getMoves().stream().filter(move -> move.getType() == Move.Type.KILL)
					.collect(Collectors.toList());

			if (nextMoves.size() > 0) {
				player.setMovedStone(stone);
			} else {
				player.setMovedStone(null);
				player.getBoard().setPlaying(player.getOpponent());
			}

			break;
		default:
			break;
		}

		// Finally transform if possible
		if (transformed) {
			stone.transform();
		}

	}

	/**
	 * @return Type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return stone that got killed
	 */
	public Stone getKilled() {
		return this.killed;
	}

	/**
	 * @return next moves
	 */
	public List<Move> getNextMoves() {
		return nextMoves;
	}

	/**
	 * @return stone that got moved
	 */
	public Stone getStone() {
		return stone;
	}

	/**
	 * @return destination field of the move
	 */
	public Field getField() {
		return field;
	}

	/**
	 * @param score set score of the move
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return get score of the move
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @return check if stone has transformed during move
	 */
	public boolean hasTransformed() {
		return transformed;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		// If stone and destination fields are equal its same move
		Move move = (Move) o;
		return stone == move.getStone() && field == move.getField();

	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Move move) {
		return this.score.compareTo(move.getScore());
	}

}
