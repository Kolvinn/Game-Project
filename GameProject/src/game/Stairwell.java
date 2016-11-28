package game;

public class Stairwell extends Tile{
	/**
	 *
	 */
	private static final long serialVersionUID = -2673955089776999953L;
	private String connectedRoom;
	private String connectedTile;
	private int crystals;

	public Stairwell(boolean walkable, String code, int x, int y) {
		super(walkable, code, x, y);

	}
	public int crystalCount(){
		return crystals;
	}
	public void setCrystalCount(int count){
		crystals=count;
	}
	/**
	 * Sets the room name that is connected to this stair
	 * @param code
	 */
	public void setConnectedRoom(String code){
		this.connectedRoom=code;
	}
	/**
	 * Returns the room name that is connected to this stair
	 * @param code
	 */
	public String connectedRoom(){
		return connectedRoom;
	}
	/**
	 * Sets the tile code that is connected to this stair
	 */
	public void setConnectedTile(String code){
		this.connectedTile=code;
	}
	/**
	 * Returns the tile name that is connected to this stair
	 */
	public String connectedTile(){
		return connectedTile;
	}
}
