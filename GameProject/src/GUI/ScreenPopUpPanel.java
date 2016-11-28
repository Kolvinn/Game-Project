package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;


/**
 * This class is a basic representation of the pop up window that will show
 * when a user clicks on an item in the game. This window will have buttons and
 * options that will allow the user to do something, and actually see more information
 * about what they clicked on
 *
 * @author berceadrag
 *
 */
public class ScreenPopUpPanel extends JFrame {


	// For text in the pop up:
	private JPanel textPanel = new JPanel ();

	// the name and description of the Item that the user has requested
	// these are what will be displayed on the pop up:
	private JTextArea name = new JTextArea ();
	private JTextArea description = new JTextArea ();

	// the string values for the name and description of the item
	private String itemName;
	private String itemDescription;

	public static boolean windowIsClosed = true;


	/**
	 * Create a pop up for the item the user clicked on to pop up on the specified x and y coordinates
	 * the descrition is a 2d array containing the name and item description
	 *
	 * @param descr
	 * @param x
	 * @param y
	 */
	public ScreenPopUpPanel (String[] descr,int x, int y) {

		this.setTitle("Details");
		this.itemName = descr[0];
		this.itemDescription = descr[1];

		this.textPanel.setLayout(new BorderLayout());

		this.name.setEditable(false);
		this.description.setEditable(false);

		this.name.setText(itemName);
		this.description.setText(itemDescription);

		this.textPanel.add(name,BorderLayout.NORTH);
		this.textPanel.add(description,BorderLayout.CENTER);


		setUpWindowListener();
		this.setLocation(x, y);
		this.setVisible(true);
		this.setSize(250, 100);


		this.add(textPanel);
	}

	/**
	 * add a window listener to this frame, so that multiple clicks does not brink up multiple
	 * pop ups, one on top of the other:
	 */
	public void setUpWindowListener () {

		this.addWindowListener(new WindowAdapter () {

			public void windowOpened(WindowEvent e) {
				windowIsClosed = false;
			}

			public void windowClosing (WindowEvent e) {
				windowIsClosed = true;
			}
		});
	}





}
