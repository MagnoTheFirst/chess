package logic;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import gui.Board;
import settings.Player;

/**
 * Bot with a simple logic for multiple difficulties 
 */
public class Bot extends Thread {

	/**
	 * The player the bot belongs to
	 */
	Player player;
	/**
	 * Difficulty of the bot
	 */
	int difficulty;
	/**
	 * Max difficulty for all bots
	 */
	final int maxDifficulty = 5;

	/**
	 * @param player the bot belongs to
	 */
	public Bot(Player player) {
		this.player = player;
		// Set default difficulty from settings
		difficulty = (int) player.getBoard().getGame().getSetting("difficulty");
	}

	/**
	 * Change difficulty
	 * @param difficulty
	 */
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		while (player.getBoard().isRunning()) {
			// Synchronize to board lock
			synchronized (player.getBoard().LOCK) {
				// Wait if it isnt the current player playing
				if (!player.isPlaying()) {
					try {
						player.getBoard().LOCK.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				
				if (!interrupted()) {
					try {
						// sleep for better user experience 
						sleep(200);
						// get moves
						List<Move> moves = getMoves();
						if (moves.size() > 0) {
							// get a random move
							Random rand = new Random();
							Move move = moves.get(rand.nextInt(moves.size()));
							move.run();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} else {
					break;
				}

			}
		}

	}

	/**
	 * Logic to derminate the best moves for the given difficulty
	 * @return list of moves
	 */
	public List<Move> getMoves() {
		// Get all moves for this player
		List<Move> moves = player.getMoves();

		// If there is only one move to play return it
		if (moves.size() == 1) {
			return moves;
		} else {
			// Start a simulation
			Simulation simulation = new Simulation(player);
			// set highestScore to a minimum
			int highestScore = -9999;
			for (Move move : moves) {
				// Simulate every move and update highestScore if needed
				simulation.simulate(move);
				highestScore = move.getScore() > highestScore ? move.getScore() : highestScore;
			}
			// create final highscore for filter
			final int highscore = highestScore;
			simulation.stop();

			// Sort moves in reverse order so highest scores are first
			Collections.sort(moves, Collections.reverseOrder());

			List<Move> bestMoves;
			switch (difficulty) {
			// Hardest difficulty
			case maxDifficulty:
				// Get only the moves with the highest score
				bestMoves = moves.stream().filter(m -> m.getScore() == highscore).collect(Collectors.toList());
				break;
			// Above avarage scores
			case maxDifficulty - 1:
			case maxDifficulty - 2:
				// Get only positive moves
				bestMoves = moves.stream().filter(m -> m.getScore() > 0).collect(Collectors.toList());
			default:
				bestMoves = moves;
				break;
			}

			// if no best moves are found take every move
			if (bestMoves.size() == 0)
				bestMoves = moves;

			// if difficulty is lower than 5 get percentage of top moves
			if (difficulty < maxDifficulty) {
				int total = bestMoves.size();
				// calculate ratio for moves to keep based on the difficulty
				double ratio = 1 - ((difficulty - 1.0) / (maxDifficulty - 1.0));
				int keep = (int) Math.ceil(total * ratio);

				// remove all unneeded moves
				Iterator<Move> itr = bestMoves.iterator();
				int counter = 0;
				while (itr.hasNext()) {
					counter++;
					if (counter > keep) {
						itr.remove();
					}
					itr.next();
				}
			}

			return bestMoves;
		}

	}
}
