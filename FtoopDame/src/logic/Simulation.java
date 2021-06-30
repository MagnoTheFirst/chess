package logic;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import gui.Board;
import gui.Field;
import gui.Stone;
import settings.Player;

/**
 * Simulate a board to derminate best moves
 */
public class Simulation extends Board {

	/**
	 * Real player on non-simulated board
	 */
	Player realPlayer;
	/**
	 * Copy of the player
	 */
	Player player;
	/**
	 * Move that gets simulated
	 */
	Move move;

	/**
	 * @param player
	 */
	public Simulation(Player player) {
		super(player.getBoard().getGame());
		this.simulation = true;
		this.realPlayer = player;
		// start simulation board
		start();
	}

	/* (non-Javadoc)
	 * @see gui.Board#start()
	 */
	public void start() {
		super.start();
		copy();
	}

	/**
	 * 
	 */
	public void copy() {
		// Copy everything from board
		Board board = realPlayer.getBoard();
		pattern = board.getPattern();

		reset();

	}

	/**
	 * 
	 */
	public void reset() {

		// empty all fields
		for (Field[] row : fields) {
			for (Field field : row) {
				field.setStone(null);
			}
		}

		// set stone from simulated players like real player
		for (int i = 0; i < players.length; i++) {
			players[i].createBot(false);
			players[i].clearStones();
			List<Stone> stones = realPlayer.getBoard().getPlayers()[i].getStones();
			for (Stone stone : stones) {
				Stone stoneCopy = new Stone(players[i], stone.getX(), stone.getY());
				players[i].addStone(stoneCopy);
				fields[stone.getY()][stone.getX()].setStone(stoneCopy);
			}

			if (players[i].getColor() == realPlayer.getColor()) {
				player = players[i];
			}
		}

		setPlaying(player);

	}

	/**
	 * @param move
	 */
	public void simulate(Move move) {

		// Original Field
		Field orgOriginField = move.getStone().getField();
		Field orgTargetField = move.getField();

		// Simulated field
		Field originField = fields[orgOriginField.getCoords().get("y")][orgOriginField.getCoords().get("x")];
		Field targetField = fields[orgTargetField.getCoords().get("y")][orgTargetField.getCoords().get("x")];

		int score = 0;

		// Simulate move
		Move sim = new Move(player, originField.getStone(), targetField);

		Stone stone = sim.getStone();
		boolean isQueen = stone.isQueen();
		Player opponent = player.getOpponent();
		// Get kill moves of opnnent before move got run
		int opponentInitKills = opponent.getMoves().stream().filter(m -> m.getType() == Move.Type.KILL).collect(Collectors.toList()).size();

		sim.run();

		// check if stone transformed after move
		if (isQueen != stone.isQueen()) {
			score += 20;
		}

		switch (sim.getType()) {
		case MOVE:
			score += 1;
			break;
		case KILL:
			// If simulated move has next moves run those
			if (sim.getNextMoves().size() > 0) {
				score += 1;
				Simulation deep = new Simulation(player);
				List<Move> nextMoves = sim.getNextMoves();
				for (Move m : nextMoves) {
					deep.simulate(m);
				}

				Collections.sort(nextMoves, Collections.reverseOrder());
				score += nextMoves.get(0).getScore();
			} else {
				score += 5;
			}

			break;
		default:
			break;
		}

		// Get kills of opnnenent after the move has run
		int opponentNewKills = opponent.getMoves().stream().filter(m -> m.getType() == Move.Type.KILL).collect(Collectors.toList()).size();

		// Check if amount of kills has changed
		if (opponentInitKills > opponentNewKills) {
			score += 5;
		} else if (opponentInitKills < opponentNewKills) {
			score -= 10;
		} else if (opponentInitKills == opponentNewKills && opponentInitKills > 0) {
			score -= 5;
		}

		// Reset the simulation
		reset();
		move.setScore(score);
	}

	/* (non-Javadoc)
	 * @see gui.Board#stop()
	 */
	public void stop() {
		running = false;
	}

}
