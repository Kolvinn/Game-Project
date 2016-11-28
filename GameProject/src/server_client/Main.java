package server_client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import GUI.GameWindow;
import game.GameRunner;
/**
 * contains the main class and various static methods to call the constructors for the Client/server, Game, GameWindow
 * renderer with the required arguments and tie them all together
 * @author william white (300163535)
 *
 */
public class Main {
	public static final int DefaultServerPort = 32769; // default port
	private static GameWindow frame;

	/**
	 * on launch creates a menu (StartUpGameMenu) with the various options for starting/loading a game
	 * @param args
	 */
	public static void main (String [] args) {
		frame = new GameWindow();
		frame.setLocationRelativeTo(null);
	}

	/**
	 * creates a new game (client, GameWindow, renderer) connected to a local server and game object etc
	 * a full server is created regardless as more users may be added later
	 */
	public static void launchServerAndClient() {
		try {
			//create a serverSocket and Game and pass them to a new Server
			ServerSocket srvrSocket = new ServerSocket(DefaultServerPort);
			GameRunner game = new GameRunner();
			Server server = new Server(game,srvrSocket);
			server.start();
			//create all the client side stuff, client, renderer, GUI etc
			initializeClientAndGUI(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * takes an address (string) for the server
	 * and creates the client, GameWindow, and renderer (as this will be the same for both)
	 * @param socket
	 */
	public static void initializeClientAndGUI(String address){
		//create a new client with a passed in socket

		Socket socket = null;
		try {
			socket = new Socket(InetAddress.getByName(address), DefaultServerPort);
			Client client = new Client(socket, frame);
			client.start();
			//create the GUI
			frame.intializeGame(client);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

