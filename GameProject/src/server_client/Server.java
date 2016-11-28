package server_client;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import game.GameError;
import game.GameRunner;
import game.Item;
import game.Player;
import game.Player.Direction;
import game.Room;
import game.Sign;
import game.StairBlock;
import game.Storage;
import game.WorldObject;

/**
 * The server holds the back-end game object(Controller) and state of the board(Model) for the multiplayer game, players
 * interact with the game through Client interaction with the Server, with any actions taken by the
 * player sent by the Client to the Server and any changes to the board state transmitted by the server to its
 * connected Clients
 * @author William White (300163535)
 *
 */
public class Server extends Thread{
	//static integers representative of method calls which they are named for, used by the input loop to process client requests
	public static final int processMovementRequest = 0;
	public static final int sendRoomAndPlayerLocation = 1;
	public static final int processInfoRequest = 2;
	public static final int sendPlayerLocation = 3;
	public static final int processInteract = 4;
	public static final int processInventoryRequest = 5;
	public static final int processItemDrop = 6;

	private static final int MAX_CONNECTIONS = 4;//the maximum number of connections the server will take, arbitrary at this point
	protected final GameRunner game;
	private final ServerSocket srvrSocket;
	private int uid = 0; //TODO may need to change this to be set at a passed in value in the constructor

	protected HashMap<Player, ConnectionThread> connectionsMap = new HashMap<Player, ConnectionThread>();

	/**
	 * construct a new server with an already existing gamerunner and serversocket
	 * @param game
	 * @param socket
	 */
	public Server(GameRunner game, ServerSocket socket){
		this.game=game;
		this.srvrSocket = socket;
	}

	@Override
	public void run(){
		boolean exit = false;
		while (!exit){
			try {
				if (connectionsMap.size()<MAX_CONNECTIONS){
					Socket socket = srvrSocket.accept();
					if(socket!=null){
						new ConnectionThread(uid++, socket);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * methods that update all connectionThreads
	 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
		/**
		 * updates all the connectionThreads who's player is in the passed in room
		 *
		 */
		public void updateConnectionThreads(Player player, int oldX, int oldY){
			for(Player p: game.getPlayersInRoom(game.getPlayerRoom(player))){
				connectionsMap.get(p).sendObjectMovement(oldX, oldY, player.getPoint().x, player.getPoint().y, player.getDirectionSet().lastDirection());
			}
		}

		/**
		 * removes an object at the location x y from all the clients who's players are in room
		 * @param room
		 * @param x
		 * @param y
		 */
		public void removeObjectFromRoom(Room room, int x, int y){
			for (Player p: game.getPlayersInRoom(room)){
				connectionsMap.get(p).sendObjectRemove(x, y);
			}
		}

		/**
		 * adds object at position x y in room for every client with a player in room
		 * @param object
		 * @param room
		 * @param x
		 * @param y
		 */
		public void addItemToRoom(WorldObject object, Room room, int x, int y){
			for (Player p: game.getPlayersInRoom(room)){
				connectionsMap.get(p).sendPlaceObject(object, x, y);
			}
		}

	/**
	 * sub-class of the server that governs a single connection to the parent server by a client
	 * @author will
	 *
	 */
	private class ConnectionThread extends Thread{

		//private final int uid;
		private final Socket socket;
		private DataOutputStream output;
		private DataInputStream input;
		private final Player player;

		private final Object outputLock = new Object();
		private final Object inputLock = new Object();

		/**
		 * create a new connectionThread responsible for communicating with a client
		 * @param uid
		 * @param socket
		 */
		public ConnectionThread(int uid, Socket socket){

			this.socket=socket;

			//create the player this new connection controls and add it to the game
			this.player = game.createPlayer("Player1", "");

			connectionsMap.put(player, this);
			//create the input/output streams
			try {
				this.input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				this.output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

				sendRoom();

			} catch (IOException e) {
				e.printStackTrace();
			}
			this.start();//start this thread
		}


		@Override
		public void run(){
			try {

				boolean exit = false;
				while (!exit){
					if(input.available()!=0){//just loop listening for input and delegate the processing of the input to other methods
						processInput();
					}
				}
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
/*
 * ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * methods for processing input from a client
 * ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 */
		/**
		 * utility for the input processing from the run loop
		 */
		private void processInput() {
			synchronized(inputLock){
				try {
					int num = input.readInt();
					switch(num){
						case processMovementRequest: //0 means the client is making a movement request
							processMovementRequest();
							return;
						case sendRoomAndPlayerLocation: // is a request for an update to the room
							sendRoom();
							sendPlayerLocation();
							return;
						case processInfoRequest://asking for information about a World object
							processInfoRequest();
							return;
						case sendPlayerLocation://just a request for the players x,y coordinates
							sendPlayerLocation();
							return;
						case processInteract://request to interact with the object in-front of the player
							processInteract();
							return;
						case processInventoryRequest://asking for the contents of a storage object
							processInventoryRequest();
							return;
						case processItemDrop://asking for the player to drop an item
							processItemDrop();
							return;


					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * reads the index of the item the player wishes to drop from the inputStream, removes it from the player inventory
		 * and sets it on a nearby tile
		 */
		private void processItemDrop(){
			try{
				synchronized(inputLock){
					int index = input.readInt();
					WorldObject item = player.getInventory().get(index);

					Point point = game.dropObject(player, (Item)item);//cast should be safe as if it is in the inventory it should be an item

					this.sendDropItem(index, point.x, point.y);
					addItemToRoom(item, game.getPlayerRoom(player), point.x, point.y);

				}
			}catch (GameError e){
				sendMessage("Error",e.getMessage());
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * process a clients request to interact with the object infront of them, and if necisary return a result
		 */
		private void processInteract(){
			try{
				synchronized(inputLock){
					Point point = game.getCorrespondingPoint(player, player.getDirectionSet().up());//get the tile this interact applies to
					WorldObject object = game.interact(player);//perform the interact and get the returned object if any

					if(object!=null){//if object isnt null then we have picked it up
						if(object instanceof StairBlock){
							sendDropCrystals();
							removeObjectFromRoom(game.getPlayerRoom(player), point.x, point.y);//if the return is a stairblock then we have unlocked the door, so we just remove it
							return;
						}
						sendPickUp((int)point.getX(),(int)point.getY());
						removeObjectFromRoom(game.getPlayerRoom(player), point.x, point.y);
						return;
					}
					//if the interact doesnt return an item then print out a description of the object infront if any
					object = game.getPlayerRoom(player).getWorldObject(point);
					if(object!=null){
						if (object instanceof Sign){
							sendMessage(object.toString(),((Sign)object).getMessage());
						}
					}
				}
			}catch(GameError e){
				sendMessage("Error",e.getMessage());
			}
		}

		/**
		 * get the x and y coordinates form the inputStream of the storage object the client is asking about
		 */
		private void processInventoryRequest(){
			try{
				synchronized(inputLock){
					int x = input.readInt();
					int y = input.readInt();
					WorldObject object = game.getPlayerRoom(player).getTile(x, y).getObject();

					if (object == null) {return;}//check there is actually an object where we are looking

					if (object.getClass().isAssignableFrom(Storage.class)){//make sure the object we are looking at extends storage
						sendStorageContents((Storage)object, x, y);
					}
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 *get the x and y coordinates of an object the client is requesting infromation on from the inputStream and call the sendObjectInfo method to give the desired information
		 */
		private void processInfoRequest(){
			try{
				synchronized(outputLock){
					int x = input.readInt();//get the x and y coordinates of the object requested
					int y = input.readInt();

					sendObjectInfo(x, y, game.getPlayerRoom(player).getWorldObject(new Point(x,y)));
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * process input from the client regarding a player movement
		 */
		private void processMovementRequest(){
			try{
				synchronized(inputLock){

					Direction direction = Direction.North;
					Room room = game.getPlayerRoom(player);
					Point oldPoint = player.getPoint();//record the players location before movement

					switch(input.readInt()){//get the desired location of movement
						case 0:
							direction = Direction.North;
							break;
						case 1:
							direction = Direction.East;
							break;
						case 2:
							direction = Direction.South;
							break;
						case 3:
							direction = Direction.West;
							break;
					}
					game.movePlayer(player, direction);//execute the move
					if(room.equals(game.getPlayerRoom(player))){//if we are still in the same room
						updateConnectionThreads(player,oldPoint.x,oldPoint.y);
						return;
					}
					removeObjectFromRoom(room, oldPoint.x, oldPoint.y);
					sendRoom();
					sendPlayerLocation();//send the new room and player location to the client
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

/*
 * ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * methods for sending updates to the client
 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 */

		/**
		 *tell the client to drop all crystals they are holding
		 */
		private void sendDropCrystals(){
			try{
				synchronized(outputLock){
					output.writeInt(Client.processDropCrystals);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/**
		 * tell the client to remove an object at location x y from the room
		 * @param x
		 * @param y
		 */
		private void sendObjectRemove(int x, int y){
			try{
				synchronized(outputLock){
					output.writeInt(Client.processRemoveItem);
					output.writeInt(x);//just need the x and y locations for this
					output.writeInt(y);

					output.flush();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * tell the client to add the given object to the room at location x y
		 * @param object
		 * @param x
		 * @param y
		 */
		private void sendPlaceObject(WorldObject object, int x, int y){
			try{
				synchronized(outputLock){
					output.writeInt(Client.processPlaceItem);
					output.writeInt(x);//just need the x and y locations for this
					output.writeInt(y);

					sendWorldObjectNameAndDescription(object);//out-source writing the fields to a utility method as other classes do this too

					output.flush();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * send a string generated from a gameError to the client to be output to the player
		 * @param message
		 */
		private void sendMessage(String title,String message){
			try{
				synchronized(outputLock){
					output.writeInt(Client.processMessage);

					byte[] titleBytes = title.getBytes();//turn the two strings to byte[]s
					byte[] textBytes = message.getBytes();

					output.writeInt(titleBytes.length);
					output.write(titleBytes);

					output.writeInt(textBytes.length);
					output.write(textBytes);

					output.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * tells the client that we are dropping the item at index in the player inventory at the location x,y
		 * @param index
		 * @param x
		 * @param y
		 */
		private void sendDropItem(int index, int x, int y){
			try{
				synchronized(outputLock){
					output.writeInt(Client.processDropItem);//tells the client this is a drop item instruction

					output.writeInt(index);
					output.writeInt(x);
					output.writeInt(y);
					output.flush();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * sends info on the result of a game.interact call to the client
		 */
		private void sendPickUp(int x, int y){
			try{
				synchronized(outputLock){
					output.writeInt(Client.processItemPickup);//tells the client this is an object pick-up
					output.writeInt(x);
					output.writeInt(y);
					output.flush();
				}

			}catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * send the location of an object in the Room to the client in order to update the clients version
		 * of the Room
		 * @param oldX
		 * @param oldY
		 * @param newX
		 * @param newY
		 * @param direction
		 */
		public void sendObjectMovement(int oldX, int oldY, int newX, int newY, Direction direction){
			try{
				synchronized(outputLock){
					output.writeInt(Client.updateObjectLocation);//tells the client this is an object movement
					output.writeInt(oldX);
					output.writeInt(oldY);//output the x and y positions of the object to be moved, their destination, and direction
					output.writeInt(newX);
					output.writeInt(newY);
					switch(direction){
					case North:
						output.writeInt(0);
						break;
					case East:
						output.writeInt(1);
						break;
					case South:
						output.writeInt(2);
						break;
					case West:
						output.writeInt(3);
						break;
					}
					output.flush();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * send the room object this threads player is in to the client at the other end of the outputStream
		 */
		public void sendRoom(){
			synchronized(outputLock){
				Room room = game.getPlayerRoom(player);
				byte[] bytes = room.toByteArray();
				try {
					output.writeInt(Client.updateRoom);
					output.writeBoolean(room.getIsDark());//send whether this room is lit or not
					output.writeInt(bytes.length);//send the length of the byte[] representing the room, the byte[], and the witdth of the Tile[][]
					output.write(bytes);
					output.writeInt(room.getWidth());
					output.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * send the information on the passed in WorldObject to the Client
		 * @param object
		 */
		public void sendObjectInfo(int x, int y, WorldObject object){
			synchronized(outputLock){
				try{
					output.writeInt(Client.processObjectInfo);//tell the client this is object information

					output.writeInt(x);//write the coordinates of the object we are describing to the client
					output.writeInt(y);

					sendWorldObjectNameAndDescription(object);//out-source writing the fields to a utility method as other classes do this too

					output.flush();

				}catch (IOException e) {
						e.printStackTrace();
				}
			}
		}

		/**
		 * send the client the x,y coordinates of the player and their direction
		 */
		private void sendPlayerLocation(){
			try{
				synchronized(outputLock){
					output.writeInt(Client.placePlayer);//tells the client this is the players location
					output.writeInt(player.getPoint().x);
					output.writeInt(player.getPoint().y);
					switch(player.getDirectionSet().lastDirection()){
						case North:
							output.writeInt(0);
							break;
						case East:
							output.writeInt(1);
							break;
						case South:
							output.writeInt(2);
							break;
						case West:
							output.writeInt(3);
							break;
					}
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * get the contents of the storage object at location x y and send it to the client
		 * @param object
		 * @param x
		 * @param y
		 */
		private void sendStorageContents(Storage object, int x, int y){
			try{
				synchronized(outputLock){
					Collection<WorldObject> objects = object.getInventory();//get the collection of objects to be sent

					output.writeInt(Client.processStorageContents);//tells the client this is the contents of a storage object

					output.writeInt(x);//send the coordinates of the storage object we are updating
					output.writeInt(y);

					output.writeInt(objects.size());//tell the client how many objects we are sending
					for(WorldObject o: objects){ //for each worldObject in the storage send its details to the client
						sendWorldObjectNameAndDescription(o);
					}
					output.flush();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * utility used by the sendObjectInfo and sendStorageContents to send the name and description fields of a worldObject to the client
		 * NOTE: only used internally in other methods where the client has already been told what to expect
		 * @param object
		 */
		private void sendWorldObjectNameAndDescription(WorldObject object){
			try{
				synchronized(outputLock){

					byte[] nameBytes = object.toString().getBytes(); //get the information we need to send in byte arrays
					byte[] descriptionBytes = object.getDescription().getBytes();

					output.writeInt(nameBytes.length);//give the client how long each byte[] is before sending the name and description
					output.write(nameBytes);

					output.writeInt(descriptionBytes.length);
					output.write(descriptionBytes);

				}

			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
