package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import game.Storage;
import game.Player.Direction;
import render.Renderer;
import render.Renderer.Rotation;
import server_client.Client;

/**
 * This class will be responsible for storing the key bindings. The ScreenPanel
 * will have these key bindings.
 *
 * @author berceadrag
 *
 */
public class GameKeyListener {

	private JPanel screenPanel;
	private Client client;

	// THE ACTIONS THAT WILL HAPPEN WHEN A CERTAIN KEY IS PRESSED:
	private AbstractAction up;
	private AbstractAction down;
	private AbstractAction left;
	private AbstractAction right;
	private AbstractAction interact; // for pressing E
	private AbstractAction rotate; // rotating the game perspective by pressing R
	private AbstractAction showInventory; // for showing the players inventory
	private AbstractAction toggleDarkness;
	private Rotation orientate;

	private Direction curNorth = Direction.North, curEast= Direction.East, curWest= Direction.West, curSouth= Direction.South;

	public GameKeyListener (JPanel screen, Client client, Rotation orientate) {
		this.screenPanel = screen;
		this.client = client;
		this.orientate=orientate;
		setUpKeyEvents();
	}

	private void orientate(Rotation dir){
		switch(dir){
		case WEST:
			curNorth=Direction.West;
			curEast=Direction.North;
			curSouth=Direction.East;
			curWest=Direction.South;
			break;
		case NORTH:
			curNorth=Direction.North;
			curEast=Direction.East;
			curSouth=Direction.South;
			curWest=Direction.West;
			break;
		case SOUTH:
			curNorth=Direction.South;
			curEast=Direction.West;
			curSouth=Direction.North;
			curWest=Direction.East;
			break;
		case EAST:
			curNorth=Direction.East;
			curEast=Direction.South;
			curSouth=Direction.West;
			curWest=Direction.North;
			break;
		default:
			break;

		}
	}
	/**
	 * (Sets up the actions)
	 * This method takes all of the above actions, and adds functionality to them.
	 * Each action will have some functionality, and then each key will be given one of
	 * these actions.
	 */

	public void setUpActionsForKeys () {

		up = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("The up key was pressed");

				client.requestMovePlayer(curNorth);
				//updateClientState();


			}
		};

		down = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("The down key was pressed");
				client.requestMovePlayer(curSouth);
				//updateClientState();
			}
		};

		left = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("The left key was pressed");
				client.requestMovePlayer(curWest);
				//updateClientState();
			}
		};

		right = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("The right key was pressed");
				client.requestMovePlayer(curEast);
				//updateClientState();
			}
		};

		interact = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {
				/*
				new StorageAndPlayerListPanel((Storage)client.getObject(),client);

				if (client.getObject() instanceof Storage) {
					// list all items inside the storage
					new StorageAndPlayerListPanel((Storage)client.getObject(),client);
				}
				*/
				client.requestInteract();
			}
		};

		rotate = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				//System.out.println("Rotating perspective");
				orientate(((Renderer) screenPanel).rotatePerspective());
				screenPanel.repaint(); //TODO: not ideal for repainting

			}
		};

		showInventory = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				new PlayerInventoryFrame(client);
				screenPanel.repaint(); //TODO: not ideal for repainting
			}
		};
/*
		toggleDarkness = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				System.out.println("toggled");
				((Renderer) screenPanel).toggleDarkness();
				screenPanel.repaint();
			}
		};
		*/

	}

	/**
	 * update the client side objects, request updated info from the server(via the client)
	 * and tell the renderer to redraw
	 */
	private void updateClientState(){
		client.requestRoomUpdate();
		client.requestPlayerLocation();
	}



	/**
	 * Add all of the actions to the actual key presses:
	 * The way it works, is a key event is related to a name (by the input map), and then
	 * that name is related to an action (by the action map). Each unique key press will have a
	 * unique name, and each unique name will have a unique action (one of the
	 * ones from above). This method makes all these relations:
	 */

	public void setUpKeyEvents () {
		setUpActionsForKeys();

		// For the up events:
		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("W"),"pressed up");
		screenPanel.getActionMap().put("pressed up", this.up);

		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("UP"),"pressed up");
		screenPanel.getActionMap().put("pressed up", this.up);


		// For the down events:
		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("S"),"pressed down");
		screenPanel.getActionMap().put("pressed down", this.down);

		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("DOWN"),"pressed down");
		screenPanel.getActionMap().put("pressed down", this.down);

		// For the left events:
		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("A"),"pressed left");
		screenPanel.getActionMap().put("pressed left", this.left);

		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("LEFT"),"pressed left");
		screenPanel.getActionMap().put("pressed left", this.left);

		// For the right events:
		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("D"),"pressed right");
		screenPanel.getActionMap().put("pressed right", this.right);

		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"),"pressed right");
		screenPanel.getActionMap().put("pressed right", this.right);

		//For rotating perspectives
		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("R"),"pressed R key");
		screenPanel.getActionMap().put("pressed R key", this.rotate);

		// For the interaction event:
		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("E"),"pressed interact");
		screenPanel.getActionMap().put("pressed interact", this.interact);

		// For bringing up the players inventory:
		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("I"),"show inventory");
		screenPanel.getActionMap().put("show inventory", this.showInventory);

		// dam dood
		screenPanel.getInputMap().put(KeyStroke.getKeyStroke("C"),"toggle darkness");
		screenPanel.getActionMap().put("toggle darkness", this.toggleDarkness);


	}




}
