package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import server_client.Main;

/**\
 * This class is a subclass of JPanel that will show up on the screen when the user selects new game
 * The panel has a text field to allow the user to name their character. After they name their character
 * they will be taken to a new JoinGame Panel where they can enter their name and a server address
 *
 * @author berceadrag
 *
 */


public class SelectCharacterPanel extends UserInputPanel {

	// I will have this class know about the GameWindow Frame, not low coupling I know ... :
	private GameWindow frame;
	// The components that will go on the page:
	private JTextField enterCharacterName = new JTextField ();
	private JButton done = new JButton("Done"); // once the user presses this button the JoinGamePanel will be the Panel in the GameWindow frame
	// Details regarding the text field:
	private String characterName;
	private final String inputMessage = "Enter the name of your character";

	// the action that will allow the user to press enter and continue to the renderer panel:
	private AbstractAction continueToJoinGame;

	// the background image for this panel:

	// I will use this MouseAdapter to clear the text when the user clicks on the text field:
	private MouseAdapter clearText;

	public SelectCharacterPanel (GameWindow frame) {

		this.frame = frame;

		this.requestFocusInWindow();
		this.setLayout(new BorderLayout());

		super.createGUI();

		try {
			super.setUpImageBackground("src/menu/SelectionMenus.jpg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		enterCharacterName.setText(this.inputMessage);
		this.add(enterCharacterName,BorderLayout.NORTH);

	}



	/**
	 * Do some error/sensibility checking for the users input of their characters name:
	 * If all goes well, change the panel for the GameWindow to Ryan's Renderer:
	 */

	public void validateCharacterName () {
		// If the User has pressed done before they have entered their character name, then show them
		// an error message
		String [] OK = {"Ok"};


		// either the user has entered an empty String, which I don't think should be valid:
		if (enterCharacterName.getText().length() ==  0) {
			JOptionPane.showOptionDialog(null, "You haven't named your character",
					"name can't be empty!", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, OK, OK[0]);
			return;
		}

		// or the User hasn't modified the text  field at all, this is still invalid, although the error message should be different:
		if (enterCharacterName.getText().equals(inputMessage)) {
			JOptionPane.showOptionDialog(null, "You haven't named your character",
					"Please Enter a name", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, OK, OK[0]);
			return;
		}

		// the player has named their character:
		else {
			characterName = enterCharacterName.getText();
			Main.launchServerAndClient();

		}
	}


	/**
	 * The mouse adapter will be used so that the user can clear the text field when they click on it
	 * Ryan and I think this is a nice feature that improves the user experience
	 */
	public void setUpMouseAdapterToClearTextWithCLick () {

		clearText = new MouseAdapter () {
			public void mouseClicked(MouseEvent e) {
				enterCharacterName.setText("");
			}
		};

		enterCharacterName.addMouseListener(clearText);
	}



	public void setUpKeyBindings () {

		continueToJoinGame = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {
				validateCharacterName();
			}
		};

		this.enterCharacterName.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),"Move on to the renderer panel");
		this.enterCharacterName.getActionMap().put("Move on to the renderer panel", continueToJoinGame);

	}



	@Override
	public void setActionListener() {
		super.action = new ActionListener () {

			public void actionPerformed(ActionEvent e) {
				validateCharacterName();
			}

		};
	}




}
