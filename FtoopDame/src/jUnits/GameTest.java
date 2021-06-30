package jUnits;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import gui.Board;
import gui.Game;
import gui.Stone;
import logic.Move;
import settings.Player;
import settings.Position;
import settings.Theme;

public class GameTest {

	private Player p1;
	private Player p2;
	private Stone s1;
	private Stone s2;
	private Stone s3;
	private Theme theme;
	private Board b1;

	@Before
	public void setUp() {

		// Create fields, board and stones
		Game game = new Game();
		b1 = new Board(game);
		b1.createFields();

		p1 = new Player(b1, Position.BOTTOM, theme.WHITE);
		p2 = new Player(b1, Position.TOP, theme.BLACK);

		s1 = new Stone(p1, 1, 0);
		s2 = new Stone(p1, 5, 2);
		s3 = new Stone(p2, 0, 1);

	}

	@Test
	public void moveTest() {

		s1.move(1, 0);
		assertSame(1, s1.getX());
		assertSame(0, s1.getY());

		s2.move(5, 2);
		assertSame(5, s2.getX());
		assertSame(2, s2.getY());

		int maxField = b1.getPattern() - 1; // Expected 7
		s3.move(18, 22);
		assertSame(maxField, s3.getX());
		assertSame(maxField, s3.getY());

	}

	@Test
	public void isPlayingTest() {
		assertEquals(-1, p1.getPosition().getDir());
		assertEquals(1, p2.getPosition().getDir());

	}

	@Test
	public void canPlayTest() {
		assertFalse(p2.canPlay());
	}
}