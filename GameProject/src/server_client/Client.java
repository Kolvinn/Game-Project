package server_client;


import java.awt.Point;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;


import GUI.GameWindow;
import game.ClientRoom;
import game.GameError;
import game.Player;
import game.WorldObject;
import render.Renderer;
import game.Player.Direction;
import game.Storage;
import game.Tile;


/**
 * the CLient is responsible for transmitting player interactions with the game world to the Server and
 * for receiving Server transmissions updating the players View of the current board state
 * @author william white (300163535)
 *
 */
public class Client extends Thread{
	int index =0;
	//static ints representing cases in the input loop, named for the methods they call on this client
	public static final int updateRoom = 0;
	public static final int updateObjectLocation = 1;
	public static final int processObjectInfo = 2;
	public static final int placePlayer = 3;
	public static final int processStorageContents = 4;
	public static final int processItemPickup = 5;
	public static final int processDropItem = 6;
	public static final int processMessage = 7;
	public static final int processRemoveItem = 8;
	public static final int processPlaceItem = 9;
	public static final int processDropCrystals = 10;


	//private Renderer renderer;
	private final Socket socket;
	private DataOutputStream output;
	private DataInputStream input;
	//private int uid;

	private static final Object outputLock = new Object();
	private static final Object inputLock = new Object();

	//private Direction orientation; //who added this? why is it needed?

	private ClientRoom room;
	private Renderer renderer;
	private GameWindow frame;
	int timerStart =0;


	/**
	 * create the client from an already existing socket
	 * @param socket
	 */
	public Client(Socket socket, GameWindow frame){
		this.socket=socket;
		this.frame=frame;

		this.room = new ClientRoom(this);//create an empty room to be updated when we receive input
		timerStart = (int) System.currentTimeMillis();
		try {
			//create the input and output object streams
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());

			requestPlayerLocation();//ask for the players location
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(){
		
		int total=0;
		try {
			boolean exit = false;
			while(!exit){//loop as long as the client is running
				//this.room = server.getRoom(uid);
				
				try {
					total++;
					

					if(input.available()!=0){   //if the server sends anything
						processInput();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get the room object this client is responsible for updating
	 * @return
	 */
	public ClientRoom getRoom(){
		return room;
	}

	/**
	 * give this client the renderer so it can call repaint whenever something changes
	 * @param renderer
	 */
	public void setRenderer(Renderer renderer){
		this.renderer = renderer;
	}


/*
 * ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * methods below here govern processing input and updating the ClientRoom
 * ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 */
	/**
	 * Utility method for the run method to outsource processing the data received
	 * from the stream
	 * @param object
	 */
	private void processInput(){
		synchronized(inputLock){ //Probably unnecessary but just to be sure
			try {
				int num = input.readInt();
				switch(num){
					case updateRoom://means this is a room object update
						updateRoom();
						//placePlayer();
						break;
					case updateObjectLocation: //means this is an update to an objects location in the room
						updateObjectLocation();
						break;
					case processObjectInfo://this is information on a worldObject
						processObjectInfo();
						break;
					case placePlayer://this is the players coordinates
						placePlayer();
						break;
					case processStorageContents://incoming input is the contents of a chest
						processStorageContents();
						break;
					case processItemPickup://input is an instruction to pickup an item
						processItemPickUp();
						break;
					case processDropItem://input is information on dropping an item
						processDropItem();
						break;
					case processMessage:
						processMessage();
						break;
					case processRemoveItem:
						processRemoveItem();
						break;
					case processPlaceItem:
						processPlaceItem();
						break;
					case processDropCrystals:
						processDropCrystals();
						break;

				}
				//check we actually have a renderer and that the players coordinates are not MIN_VALUE, which is default when the romm is changed
				if(renderer!=null && room.getPlayer().getPoint().getX()!=Integer.MIN_VALUE){


					renderer.repaint();//once any changes from the input are applied ask the renderer to repaint
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * server has told us to drop crystals
	 */
	private void processDropCrystals(){
		room.getPlayer().resetCrystals();
	}

	/**
	 *remove an item at a location specified by integers x and y read from the inputStream
	 */
	private void processRemoveItem(){
		try{
			synchronized(inputLock){
				int x = input.readInt();//get the coordinates
				int y = input.readInt();

				room.setEmptyTile(x, y);//remove the item
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *place an item in the room specified by x and y coordiates, and a pair of strings detailing the object itself, all retrieved from the inputStream
	 */
	private void processPlaceItem(){
		try{
			synchronized(inputLock){
				int x = input.readInt();//get the x and y coordinate
				int y = input.readInt();

				WorldObject object = processWorldObject();//get the object
				Tile tile = new Tile(true, "fn", 0,0);//set it on a tile
				tile.setGameObject(object);

				room.setTile(tile, x, y);//add that tile to the room
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * inputStream contains a string from a gameError, this method retrieves, decodes and passes that string on to the gui
	 */
	private void processMessage(){
		try{
			synchronized(inputLock){
				byte[] titleBytes = new byte[input.readInt()];//get a byte[] of the length specified by an int sent first in inputStream
				input.readFully(titleBytes, 0, titleBytes.length);

				byte[] textBytes = new byte[input.readInt()];
				input.readFully(textBytes, 0, textBytes.length);

				String message = new String(textBytes);
				String title = new String(titleBytes);
				this.frame.displayMessage(title,message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * get instructions from the inputStream for the index of an item in the players inventory and the x and y coordinates to place it at
	 */
	private void processDropItem(){
		try{
			synchronized(inputLock){
				int index = input.readInt();//get the index of the item in the players invetory array
				int x = input.readInt();//and the x,y position it is dropped to
				int y = input.readInt();

				synchronized(room.updateLock){
					WorldObject object = room.getPlayer().getInventory().remove(index);//remove the item from the players inventory, create a tile for it to sit on and place it in the room at x y
					Tile tile = new Tile(true, "fn", 0,0);
					tile.setGameObject(object);
					room.setTile(tile, x, y);
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get the coordinates of an item we have just picked up and apply the necessary changes to the client copies of the objects
	 */
	private void processItemPickUp(){
		try{
			synchronized(inputLock){
				int x = input.readInt();//get the x and y coordinates of the object we have picked up
				int y = input.readInt();

				synchronized(room.updateLock){
					WorldObject object = room.getTile(x, y).getObject();//get the object, add it to the players inventory, then remove it from the room
					room.getPlayer().addItem(object);
					room.setEmptyTile(x, y);
				}
			}
		}catch (IOException | GameError e) {
			e.printStackTrace();
		}
	}

	/**
	 * get the players coordinates from the inputStream and place the player at that location
	 */
	private void placePlayer(){
		try{
			synchronized(inputLock){
				int x=input.readInt();
				int y=input.readInt();
				synchronized(room.updateLock){
					switch(input.readInt()){
						case 0:
							room.getPlayer().setLastDirection(Direction.North);
							break;
						case 1:
							room.getPlayer().setLastDirection(Direction.East);
							break;
						case 2:
							room.getPlayer().setLastDirection(Direction.South);;
							break;
						case 3:
							room.getPlayer().setLastDirection(Direction.West);;
							break;
					}

					room.getPlayer().setPoint(new Point(x,y));//set the player

					room.setPlayer(x, y);
				}
			}
		} catch (IOException e) {
		e.printStackTrace();
		}
	}

	/**
	 * update information on a worldObject using information received from the InputStream
	 */
	private void processObjectInfo(){
		try{
			synchronized(inputLock){
				int x = input.readInt();//get the coordinates of the object being described
				int y = input.readInt();


				room.getTile(x, y).setGameObject(processWorldObject());

			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * input from the server is the contents of a storage object, this method reads those objects and adds them to the designated storage object in the
	 * clientRoom
	 */
	private void processStorageContents(){
		try{
			synchronized(inputLock){
				int x = input.readInt();//get the x and y coordinates
				int y = input.readInt();

				WorldObject object = room.getTile(x, y).getObject();//get the object at that location

				if(!object.getClass().isAssignableFrom(Storage.class)){//make sure it is an extension of Storage
					throw new IOException("expected Storage.class, got " + object.getClass());
				}
				Storage storage = (Storage)object;

				synchronized(room.updateLock){
					for(int i=0; i< input.readInt();++i){//get all the objects it contains
						storage.addItem(processWorldObject());
					}
				}
			}
		}catch (IOException | GameError e) {
			e.printStackTrace();
		}
	}

	/**
	 * helper method that gets the name and description fields of a worldObject from the input stream and returns a new WorldObject with those fields
	 * NOTE: only used internally in other input methods where we already know what to expect
	 * @return
	 */
	private WorldObject processWorldObject(){
		try{
			synchronized(inputLock){

				byte[] nameBytes = new byte[input.readInt()];//read the length of the 2 byte arrays containing the name and description of an object
				input.read(nameBytes, 0, nameBytes.length);
				String name = new String(nameBytes);

				byte[] descriptionBytes = new byte[input.readInt()];
				input.read(descriptionBytes,0,descriptionBytes.length);
				String description = new String(descriptionBytes);

				return new WorldObject(name, description);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		return null;//unreachable (hopefully)
	}

	/**
	 * called by the processInput method, input is an update to the room object so we process that here
	 */
	private void updateRoom(){
		synchronized(inputLock){ //Probably unnecessary but just to be sure
			try {
				room.setIsDark(input.readBoolean());
				int num = input.readInt();
				//get the size of the byte[] to be sent
				byte[] bytes = new byte[num];
				input.readFully(bytes); //read the byte[num] from the stream
				num = input.readInt();//re-use num to get the width of the room object

				synchronized(room.updateLock){
					room.getPlayer().setPoint(new Point(Integer.MIN_VALUE,Integer.MIN_VALUE));
					room.fromByteArray(bytes, num);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * helper for the processInput method, this processes an update to a player objects location
	 */
	private void updateObjectLocation(){
		try{
			synchronized(inputLock){
				int oldX, oldY, newX, newY, d;
				Direction direction=Direction.North;
				oldX=input.readInt();
				oldY=input.readInt();
				newX=input.readInt();
				newY=input.readInt();
				d=input.readInt();
				switch(d){
					case 0:
						direction=Direction.North;
						break;
					case 1:
						direction=Direction.East;
						break;
					case 2:
						direction=Direction.South;
						break;
					case 3:
						direction=Direction.West;
						break;
				}
				synchronized(room.updateLock){
					//TODO maybe?
					//Room now contains objects via a map, point-> object, so im not sure if you should be using this or that
					//
					//TODO
					WorldObject object = room.getTile(oldX, oldY).getObject();//this should be a Player Object
					Tile tile = room.getTile(oldX, oldY);//move the tile containing the player rather than the player
					if (object == null){ //if we are updating the location of an object hen this is a player we dont know about
						tile = room.newPlayerTile(direction, newY, newX);
					}
					if(object instanceof Player){
						((Player)object).setPoint(new Point(newX,newY));
						((Player)object).setLastDirection(direction);
					}
					room.setEmptyTile(oldX, oldY);
					room.setTile(tile, newX, newY);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

/*
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * methods below here govern output to the server
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 */
	/**
	 * move the player this client is responsible for in the given direction
	 * @param x
	 * @param y
	 */
	public void requestMovePlayer(Direction direction){
		synchronized(outputLock){ //Probably unnecessary but just to be sure
			try {
				output.writeInt(Server.processMovementRequest);//tell the server this is a request regarding the player movement
				switch (direction){
					case North:
						output.writeInt(0);//0 is north
						break;
					case East:
						output.writeInt(1);//1 is east
						break;
					case South:
						output.writeInt(2);//2 is south
						break;
					case West:
						output.writeInt(3);//3 is west
						break;
				}
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ask the server to send the room object again
	 */
	public void requestRoomUpdate(){
		synchronized(outputLock){//Probably unnecessary but just to be sure
			try {
				output.writeInt(Server.sendRoomAndPlayerLocation);
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * requests information about an object at location [y][x] in the Room's Tile[][]
	 * @param x
	 * @param y
	 */
	public void requestObject(int x, int y) {
		synchronized(outputLock){
			try {
				output.writeInt(Server.processInfoRequest);//tells the server this is a request for object interaction
				output.writeInt(x);
				output.writeInt(y);
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * asks the server to send this client's player object's location,
	 * the actual location will be received in the run loop to be processed by the processInput method
	 */
	public void requestPlayerLocation(){
		synchronized(outputLock){ //Probably unnecessary but just to be sure
			try {
				output.writeInt(Server.sendPlayerLocation); //0 tells the server this is a request regarding the player
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ask the server for the result of an interaction with whatever is infront of the player
	 */
	public void requestInteract(){
		try{
			synchronized(outputLock){
				output.writeInt(Server.processInteract);
				output.flush();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *ask the server for the contents of a chest at postion x y
	 * @param x
	 * @param y
	 */
	public void requestChestContents(int x, int y){
		try{
			synchronized(outputLock){
				output.writeInt(Server.processInventoryRequest);//tells the server we are asking for the contents of a chest

				output.writeInt(x);//send the coordinates of the chest we are asking about
				output.writeInt(y);

				output.flush();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * request for the server to update the player and room by dropping the passed in object
	 * @param object
	 */
	public void requestDropItem(int index){
		try{
			synchronized(outputLock){
				output.writeInt(Server.processItemDrop);//6 tells the server we are dropping an item
				output.writeInt(index);
				output.flush();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * -------------------------------------------------------------------------------------------------------------------------------------
	 */

	/**
	 * get the player this client is responsible for's inventory set
	 * @return
	 */
	public List<WorldObject> getPlayerInventory(){
		return room.getPlayer().getInventory();
	}
}
