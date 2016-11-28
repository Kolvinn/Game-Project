package game;

import java.io.Serializable;

/**
 * A WorldObject is anything that populates the game world. This includes all the players, items and storage etc.
 * Because the world is Tile based, a worldObject is not concerned about where it is, only what it is. The one exception to this rule is the player,
 * however this is a special case
 * @author Jeremy Purvis
 *
 */
public class WorldObject implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 6266172336007711708L;
	protected String name;
	protected String description;

	public WorldObject(String name, String description){
		this.name=name;
		this.description=description;
	}
	/**
	 * Returns the name of this object
	 */
	public String toString(){
		return name;

	}
	/**
	 * Returns the description String that is defines what this object is in the world
	 * @return
	 */
	public String getDescription(){
		return description;

	}
	public boolean equals(Object o){
		WorldObject object = (WorldObject) o;
		if(object.toString().equals(name))
			return true;
		return false;
	}

}
