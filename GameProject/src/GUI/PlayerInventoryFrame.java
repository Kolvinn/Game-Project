package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import game.WorldObject;
import server_client.Client;

/**
 * Upon pressing "I" this frame shows the players inventory, and allows them to drop objects:
 * @author berceadrag
 *
 */
public class PlayerInventoryFrame extends JFrame {

	private JPanel panel =  new JPanel ();

	private Set<String> mockInventory = new HashSet<String> ();

	// the actual players inventory:
	private List<String> playersInventory = new ArrayList <String> ();

	private ArrayList<JButton> inventoryItems = new ArrayList<JButton> ();

	private ActionListener listener;

	private Client client;


	public PlayerInventoryFrame (Client client) {


		this.client = client;
		panel.setLayout(new GridLayout(0,1));

		loadButtonList(client.getPlayerInventory());

		panel.setBackground(Color.BLACK);
//		panel.setBackground(new Color(0,0,0,50));

		this.add(panel);
		this.setTitle("Players Inventory");
		this.setSize(200, 200);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}


	/**
	 *
	 */
	public void createTitle () {
		JButton storageTitle = new JButton ("Players Inventory");
		styleUpButton(storageTitle,Color.RED);
		panel.add(storageTitle);
	}


	public void loadButtonList (List<WorldObject> inventory) {

		createTitle();
		createActionListener();

		// load the storage button list:
		for (int index = 0; index<inventory.size();++index) {
			WorldObject item = inventory.get(index);
			if(item==null){break;}
			JButton button = new JButton(item.toString());
			button.setName(item.toString());
			button.addActionListener(this.listener);
			styleUpButton(button,Color.orange);
			button.setActionCommand(Integer.toString(index));

			inventoryItems.add(button);
			panel.add(button);
		}


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
					// get the JPanel that this button is on:
					JPanel panelForThisButton = (JPanel) button.getParent();
					// get the name of this JPanel:
					String panelName = panelForThisButton.getName();

					//System.out.println(button.getName());
					int index = Integer.parseInt(e.getActionCommand());//the actioncommand on the button is its index in the array
					client.requestDropItem(index);

					playersInventory.remove(button.getName());

					panelForThisButton.remove(button);
					panelForThisButton.repaint();
					revalidate();
				}
				}
			}


		};
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



}
