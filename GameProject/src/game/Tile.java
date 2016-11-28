package game;

import java.io.Serializable;
/**
 * The tile object is what makes up the game. Every room is made up of tile objects, and the tiles themselves each contain an x and y and
 * a world object. Since the game is completely tile based, the tiles have string codes which correspond to the pictures that draw them.
 * @author Jeremy
 *
 */
public class Tile implements Serializable{

	private static final long serialVersionUID = -5796249978227339268L;
	private boolean walkable;
	private WorldObject object = null;
	private int x, y;
	private String code;

	public Tile(boolean walkable, String code, int x, int y) {
		this.walkable = walkable;
		this.code = code;
		this.x = x;
		this.y = y;
	}

	public void setGameObject(WorldObject object) {
		this.object = object;
	}
	public boolean isWalkable(){
		return walkable;
	}

	public String toString() {
		return code;
	}

	public WorldObject getObject() {
		return object;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean equals(Object o) {
		if (o instanceof Tile) {
			if (((Tile) o).x == x && ((Tile) o).y == y)
				return true;
		}
		return false;
	}
}
