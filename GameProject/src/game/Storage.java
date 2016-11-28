package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Storage extends WorldObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -4973050551618843208L;
	private int storageLimit;
	private List<WorldObject> inventory = new ArrayList<WorldObject>();

	public Storage(String name, String description, int limit) {
		super(name, description);
		this.storageLimit = limit;
	}

	public void removeItem(WorldObject item){
		inventory.remove(item);
	}
	//TODO could add a weight element later on
	public void addItem(WorldObject item) throws GameError{
		if(inventory.size()+1>storageLimit)
			throw new GameError("Inventory is full!");
		inventory.add(item);
	}
	public List<WorldObject> getInventory(){
		return inventory;
	}

}
