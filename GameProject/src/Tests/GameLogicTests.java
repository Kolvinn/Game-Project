package Tests;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.Set;

import org.junit.Test;

import game.GameRunner;
import game.Player;
import game.Room;
import game.Player.Direction;

/**
 * This class is a test suite for the Game Logic only, so all the classes that are in the game package
 * @author berceadrag
 *
 */
public class GameLogicTests {

	/**
	 * Create a test game runner, adding one player to the starting room of the game.
	 * This test then checks that there are no other players in the starting room, and that
	 * the player that has been created is actually in the starting room:
	 */

	@Test public void testAddingOnePlayerToCurrentRoom () {

		// create a new game runner
		GameRunner gameRunner = new GameRunner();
		// add a player to the starting room of the game
		Player player = gameRunner.createPlayer("player1", "I am a test player");
		// get the room that the player is in:
		Room playersRoom = gameRunner.getPlayerRoom(player);
		// get the players that are in the same room as player
		Set<Player> playersInStartingRoom = gameRunner.getRoomToPlayers().get(playersRoom);

		// now the only players in that room should be 'player':
		assertTrue(playersInStartingRoom.size() == 1);
		assertTrue(playersInStartingRoom.contains(player));

	}

	/**
	 * This test is similar to the one above, only now we will add multiple players to the starting room and check that
	 * only those players (and all of those players) are in the starting room. Also test that all of the rooms for each player
	 * are in the hash map
	 */
	@Test public void testAddingMultiplePlayersToStartingRoom () {

		GameRunner gameRunner = new GameRunner();

		// add a bunch of players to the starting game room:
		Player player1 = gameRunner.createPlayer("player1","I am player 1");
		Player player2 = gameRunner.createPlayer("player2","I am player 2");
		Player player3 = gameRunner.createPlayer("player3","I am player 3");

		// get the respective rooms of the players:
		Room player1Room = gameRunner.getPlayerRoom(player1);
		Room player2Room = gameRunner.getPlayerRoom(player2);
		Room player3Room = gameRunner.getPlayerRoom(player3);


		// test that the players rooms are in the hashMap roomToPlayers:
		assertTrue(gameRunner.getRoomToPlayers().containsKey(player1Room));
		assertTrue(gameRunner.getRoomToPlayers().containsKey(player2Room));
		assertTrue(gameRunner.getRoomToPlayers().containsKey(player3Room));


		// get the set of players that are in the starting room of the game:
		Set<Player> playersInStartingRoom = gameRunner.getRoomToPlayers().get(player1Room);


		// test that all of the players are in that room, and they are the only ones there:
		assertTrue(playersInStartingRoom.size() == 3);
		assertTrue(playersInStartingRoom.contains(player1));
		assertTrue(playersInStartingRoom.contains(player2));
		assertTrue(playersInStartingRoom.contains(player3));

	}

	/**
	 *  This test creates a new game runner, and then adds a new player to the starting room
	 *  since the starting room should now exist, getRoom(startingRoom) shouldn't return null:
	 */
	@Test public void testGetRoom () {

		GameRunner gameRunner = new GameRunner();

		// Create a new Player in the starting room, (so the starting room should exist):
		Player player = gameRunner.createPlayer("player1", "I am a test player");

		assertTrue(gameRunner.getRoom("room0")!=null );

	}

	/**
	 * A test for the getCorrespondingPoint method in game runner, covering the player moving north
	 */
	@Test public void testPlayerMovesNorth () {

		GameRunner gameRunner = new GameRunner();

		// Create a new Player in the starting room, (so the starting room should exist):
		Player player = gameRunner.createPlayer("player1", "I am a test player");

		// the players current position:
		int playersOriginalX = player.getPoint().x;
		int playersOriginalY = player.getPoint().y;

		// move the player north:
		Point playersNewLocation = gameRunner.getCorrespondingPoint(player,Direction.North);

		// check the players x and y, so that the player has moved north:
		assertTrue(playersNewLocation.getX() == playersOriginalX);
		assertTrue(playersNewLocation.getY() == playersOriginalY - 1);
	}


	/**
	 * A test for the getCorrespondingPoint method in game runner, covering the player moving South
	 */
	@Test public void testPlayerMovesSouth () {

		GameRunner gameRunner = new GameRunner();

		// Create a new Player in the starting room, (so the starting room should exist):
		Player player = gameRunner.createPlayer("player1", "I am a test player");

		// the players current position:
		int playersOriginalX = player.getPoint().x;
		int playersOriginalY = player.getPoint().y;

		// move the player north:
		Point playersNewLocation = gameRunner.getCorrespondingPoint(player,Direction.South);

		// check the players x and y, so that the player has moved north:
		assertTrue(playersNewLocation.getX() == playersOriginalX);
		assertTrue(playersNewLocation.getY() == playersOriginalY + 1);
	}

	/**
	 * A test for the getCorrespondingPoint method in game runner, covering the player moving East
	 */
	@Test public void testPlayerMovesEast () {

		GameRunner gameRunner = new GameRunner();

		// Create a new Player in the starting room, (so the starting room should exist):
		Player player = gameRunner.createPlayer("player1", "I am a test player");

		// the players current position:
		int playersOriginalX = player.getPoint().x;
		int playersOriginalY = player.getPoint().y;

		// move the player north:
		Point playersNewLocation = gameRunner.getCorrespondingPoint(player,Direction.East);

		// check the players x and y, so that the player has moved north:
		assertTrue(playersNewLocation.getX() == playersOriginalX + 1);
		assertTrue(playersNewLocation.getY() == playersOriginalY);
	}

	/**
	 * A test for the getCorrespondingPoint method in game runner, covering the player moving West
	 */
	@Test public void testPlayerMovesWest () {

		GameRunner gameRunner = new GameRunner();

		// Create a new Player in the starting room, (so the starting room should exist):
		Player player = gameRunner.createPlayer("player1", "I am a test player");

		// the players current position:
		int playersOriginalX = player.getPoint().x;
		int playersOriginalY = player.getPoint().y;

		// move the player north:
		Point playersNewLocation = gameRunner.getCorrespondingPoint(player,Direction.West);

		// check the players x and y, so that the player has moved north:
		assertTrue(playersNewLocation.getX() == playersOriginalX - 1);
		assertTrue(playersNewLocation.getY() == playersOriginalY);
	}


	/**
	 * A test that places the player at the center of the screen
	 * and tests that the player can validly move up five times (since the map
	 * has no objects in the players way) this is valid player movement and the test
	 * should pass
	 */
	@Test public void testPlayerMovesUpToNorthWall () {

		GameRunner gameRunner = new GameRunner();

		// Create a new Player in the starting room:
		Player player = gameRunner.createPlayer("player1", "I am a test player");
		Room roomZero = gameRunner.getPlayerRoom(player);


		// from the center, move the player up five times
		// this should be valid, because it will place them one step before a north wall:
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));

		// check that the move updated the players position:
		assertTrue(player.getPoint().getX() == 5);
		assertTrue(player.getPoint().getY() == 1);
	}

	/**
	 * Same as the test above, only this time the player will reach the north wall, try to pass through
	 * it and off the map. This is invalid movement, and the test should confirm it
	 */
	@Test public void testPlayerCantMoveUpAndThroughWall () {

		GameRunner gameRunner = new GameRunner();

		// Create a new Player in the starting room:
		Player player = gameRunner.createPlayer("player1", "I am a test player");
		Room roomZero = gameRunner.getPlayerRoom(player);


		// from the center, move the player up five times
		// this should be valid, because it will place them one step before a north wall:
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		// now the player will try and move up through the north wall, not valid!
		assertFalse(gameRunner.movePlayer(player, Direction.North));

		// the player should remain at the location they were at before they tried
		// to move through the wall and off the map:
		assertTrue(player.getPoint().getX() == 5);
		assertTrue(player.getPoint().getY() == 1);

	}

	/**
	 * The player is placed at the center of the map, and they try and move left until they reach a wall
	 * like the test above, the player try's to move through the wall, and this test also ensure that this is
	 * not possible!
	 */
	@Test public void testPlayerCantMoveLeftAndThroughWall () {

		GameRunner gameRunner = new GameRunner();

		// Create a new Player in the starting room:
		Player player = gameRunner.createPlayer("player1", "I am a test player"); // places player at 5,6
		Room roomZero = gameRunner.getPlayerRoom(player);

		// from the center, move the player left (once) which is valid
		// as this will bring them right next to a wall on their left:
		assertTrue(gameRunner.movePlayer(player, Direction.West));
		// now check that the valid movement has correctly updated the players location:
		assertTrue(player.getPoint().getX() == 4);
		assertTrue(player.getPoint().getY() == 6);

		// now try and move the player through the wall:
		assertFalse(gameRunner.movePlayer(player, Direction.West));
		// check that the player remains in their most recent position:
		assertTrue(player.getPoint().getX() == 4);
		assertTrue(player.getPoint().getY() == 6);
	}

	/**
	 * In this test the player will step on the stairs tile, and will be transported to a different room
	 * this test ensures this transportation occurs properly:
	 */
	@Test public void testPlayerCanUseStairWell () {

		GameRunner gameRunner = new GameRunner();

		// Create a new Player in the starting room:
		Player player = gameRunner.createPlayer("player1", "I am a test player"); // places player at 5,6
		Room roomZero = gameRunner.getPlayerRoom(player); // the room that the player is initially in

		// move the player to the stairs:
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));

		assertTrue(gameRunner.movePlayer(player, Direction.East)); // the player is now on the stair well

		// ensure that the player has changed rooms:
		assertTrue(!gameRunner.getPlayerRoom(player).equals(roomZero));


	}


	/**
	 * In this test, a player will move to the stairs, go into a room and then move back again.
	 * This test ensures that the player can move back to the original room
	 */
	@Test public void testPlayerCanMoveBackToOriginalRoom () {

		// move the player to the other room as before (room 1):
		GameRunner gameRunner = new GameRunner();

		// Create a new Player in the starting room:
		Player player = gameRunner.createPlayer("player1", "I am a test player"); // places player at 5,6
		Room roomZero = gameRunner.getPlayerRoom(player); // the room that the player is initially in

		// move the player to the stairs:
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.North));

		assertTrue(gameRunner.movePlayer(player, Direction.East)); // the player is now on the stair well

		// ensure that the player has changed rooms:
		assertTrue(!gameRunner.getPlayerRoom(player).equals(roomZero));

		// now the player is on 1,27 in room 1. So move them up and down once
		// and then back to the original room:
		assertTrue(gameRunner.movePlayer(player, Direction.North));
		assertTrue(gameRunner.movePlayer(player, Direction.South));

		// ensure that they are back in the original room
		assertTrue(gameRunner.getPlayerRoom(player).equals(roomZero));

	}



}
