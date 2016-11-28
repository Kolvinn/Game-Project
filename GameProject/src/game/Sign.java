package game;

public class Sign extends WorldObject{
	String message;
	public Sign(String name, String description, String message) {
		super(name, description);
		this.message = message;
	}
	public String getMessage(){
		return message;
	}

}
