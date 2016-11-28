package game;

public class StairBlock extends WorldObject {
	int crystalCount=0, crystalLimit =0;
	public StairBlock(String name, String description, int crystalLimit) {
		super(name, description);
		this.crystalLimit=crystalLimit;
	}
	public void setCount(int count){
		this.crystalCount=count;
	}
	public int crystalCount(){
		return crystalCount;
	}
	public int crystalLimit(){
		return crystalLimit;
	}

}
