package game;

public class Item extends WorldObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -8020797582522666424L;
	private boolean pickup;
	public Item(String name, String description, boolean pickup) {
		super(name, description);
		this.pickup=pickup;
	}
	/**
	 * Set whether this item can be carried in a player's inventory or not
	 * @param set
	 */
	public void setPickup(boolean set){
		pickup = set;
	}
	/**
	 * Returns true if this item can be carried by a player, false otherise
	 * @return
	 */
	public boolean isPickupable(){
		return pickup;
	}

}
