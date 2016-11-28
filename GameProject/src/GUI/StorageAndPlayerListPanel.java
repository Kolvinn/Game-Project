package GUI;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import game.Item;
import game.Storage;
import server_client.Client;

/**
 * For the panel that will show the item list and the players inventory
 * @author berceadrag
 *
 */
public class StorageAndPlayerListPanel extends JFrame {

	private Storage storageObject;
	private ActionListener listener;

	// There will be two panels: one for listing the item list, the other
	// for listing the players inventory
	private JPanel storagePanel = new JPanel();
	private JPanel playerPanel = new JPanel();

	// the two collections of items, one from the players inventory, and the other from the storage inventory
	private Set<Item> storageInventory;
	private Set<Item> playerInventory;

	// FOR TESTING ONLY:
	// two collections of strings that I will use to test the buttons and their listeners:
	private Set<String> storageStuff = new HashSet<String> ();
	private Set<String> playerStuff = new HashSet<String> ();



	public StorageAndPlayerListPanel (Storage storageObject, Client client) {

		storagePanel.setName("storagePanel");
		playerPanel.setName("playerPanel");

		this.setLocationRelativeTo(null);
		this.storageObject = storageObject;

		loadFakeSets();
		loadButtonLists();
		//		this.storageInventory = storageObject.getItems();
		//		this.playerInventory = client.getPlayer().getItems();


		// TODO: will decide the number of rows here later
		storagePanel.setLayout(new GridLayout(10,0));
		playerPanel.setLayout(new GridLayout(10,0));
		this.setLayout(new GridLayout(0,2));
		//		mockCreateButtonsFromCollections();

		storagePanel.setBackground(Color.BLACK);
		playerPanel.setBackground(Color.BLACK);

		this.add(storagePanel);
		this.add(playerPanel);

		this.setLocationRelativeTo(null);

		this.setSize(305,200);
		this.setVisible(true);

	}

	/**
	 * create the action listener that will be added to all of the buttons
	 */
	public void createActionListener () {
		this.listener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton) e.getSource();
				switch(e.getActionCommand()){
				default:{
					if (button.getParent().getName().equals("storagePanel")) {
						storagePanel.remove(button);
						playerPanel.add(button);

						storagePanel.repaint();
						playerPanel.repaint();
						revalidate();
						return;
					}

					if (button.getParent().getName().equals("playerPanel")) {
						playerPanel.remove(button);
						storagePanel.add(button);

						storagePanel.repaint();
						playerPanel.repaint();
						revalidate();
						return;
					}
				}
				}

			}

		};
	}


	/**
	 * Because I am using two panels, I will add two Buttons that do nothing as the title:
	 */
	public void insertPanelTitles () {

		JButton storageTitle = new JButton ("Storage");
		styleUpButton(storageTitle,Color.RED);
		storagePanel.add(storageTitle);

		JButton playerTitle = new JButton ("Player Inventory");
		styleUpButton(playerTitle,Color.RED);
		playerPanel.add(playerTitle);
	}



	/**
	 * Load the ArrayLists of Buttons from the sets of strings:
	 */
	public void loadButtonLists () {

		createActionListener();
		insertPanelTitles();

		// load the storage button list:
		for (String storageItem: storageStuff) {
			JButton button = new JButton(storageItem);
			button.addActionListener(listener);
			button.setActionCommand(storageItem);
			styleUpButton(button,Color.orange);
			storagePanel.add(button);
		}

		// load the player inventory button list:
		for (String playerItem: playerStuff) {
			JButton button = new JButton(playerItem);
			button.addActionListener(listener);
			button.setActionCommand(playerItem);
			styleUpButton(button,Color.orange);
			playerPanel.add(button);
		}

	}

	/**
	 * Set the button to be transparent, have no border and have a nice lovely
	 * orange color
	 * @param button
	 */
	public void styleUpButton (JButton button, Color color) {
		// don't style the title:

		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setForeground(color);
	}



	/**
	 * Load the fake sets for testing:
	 */
	public void loadFakeSets () {

		storageStuff.add("Box");
		storageStuff.add("ball");
		storageStuff.add("rope");


		playerStuff.add("FlashLight");
		playerStuff.add("Water");
		playerStuff.add("Fire");

	}




	/**
	 * (Actual method to be used later once client.getObject() actually works)
	 * Iterate through each inventory creating buttons for each one on the left and right hand sides:
	 */
	public void createButtonsFromCollections () {

		for (Item storageItem: storageInventory) {
			JButton button = new JButton(storageItem.toString());
			this.storagePanel.add(button);
		}

		for (Item playerItem: playerInventory) {
			JButton button = new JButton(playerItem.toString());
			this.playerPanel.add(button);
		}

	}


}
