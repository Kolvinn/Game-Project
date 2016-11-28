package GUI;

import java.awt.Graphics;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import server_client.Main;

/**
 * This class will contain that appears after the player has named their character.
 * It will allow the player to join the game by entering the players name and the
 * server address:
 *
 * @author berceadrag
 *
 */
public class JoinGamePanel extends UserInputPanel {

	// Components that will go on this JPanel:
	private JTextField enterPlayerName = new JTextField ();
	private JTextField enterServerAddress = new JTextField ();

	private AbstractAction goToRenderer; // or by using the enter key, which is what this action will help with


	// for the text field, and user input:
	private String enterNameMessage = "Please Enter Your Name";
	private String enterAddressMessage = "Please Enter a server address";

	// this class will store the server address and the players name:
	private String playersName;
	private String serverAddress;


	// I will use these MouseAdapters to clear the text when the user clicks on a text field:
	private MouseAdapter clearNameText;
	private MouseAdapter clearServerText;


	public JoinGamePanel () {

		enterPlayerName.setText(enterNameMessage);
		enterServerAddress.setText(enterAddressMessage);

		super.createGUI();


		try {super.setUpImageBackground("src/menu/SelectionMenus.jpg");}
		catch (IOException e) {e.printStackTrace();}

		this.add(enterPlayerName);
		this.add(enterServerAddress);
	}





	/**
	 * Take in a textField for the input the user has chosen, make sure that text field is filled in properly
	 * and only if it is, change the panel of the GameWindow frame to the renderer by calling launchServerAndClient
	 *
	 * @param textField: the text field where the user inputs some information
	 * @param nameOfInput: if this is for the address or the players name
	 */
	public boolean validateInput (JTextField textField, String nameOfInput) {
		String [] OK = {"Ok"};


		// either the user has entered an empty String, which I don't think should be valid:
		if (textField.getText().length() ==  0) {
			JOptionPane.showOptionDialog(null, "You haven't entered your " + nameOfInput,
					nameOfInput + " can't be empty!", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, OK, OK[0]);
			return false;
		}

		// or the User hasn't modified the text  field at all, this is still invalid, although the error message should be different:

		// is this input for the players name ?
		if (nameOfInput.equals("Player Name") && textField.getText().equals(enterNameMessage)) {
			JOptionPane.showOptionDialog(null, "You haven't entered your name",
					"Please Enter your name", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, OK, OK[0]);
			return false;
		}

		// or is this input for the server address ?
		if (nameOfInput.equals("Server Address") && textField.getText().equals(enterAddressMessage)) {
			JOptionPane.showOptionDialog(null, "You haven't entered your server address!",
					"Please Enter an address", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, OK, OK[0]);
			return false;
		}

		return true;
	}

	/**
	 * This acts as a wrapper method for the one above, by checking both text fields and only if both have
	 * valid input, does it launch the renderer:
	 */
	public void validateAllUserInput () {

		if (validateInput(enterServerAddress,"Server Address") && validateInput(enterPlayerName,"Player Name")) {

			playersName = enterPlayerName.getText();
			serverAddress = enterServerAddress.getText();
			Main.initializeClientAndGUI(serverAddress);
		}
	}

	/**
	 * Similar to the method in the SelectCharacterPanel class , although this selection panel has two text fields
	 * so I will use two mouse adapters to clear the text on each one when the user clicks on one of them:
	 */
	public void setUpMouseAdapterToClearTextWithCLick () {

		clearNameText = new MouseAdapter () {
			public void mouseClicked(MouseEvent e) {
				enterPlayerName.setText("");
			}
		};

		clearServerText = new MouseAdapter () {
			public void mouseClicked(MouseEvent e) {
				enterServerAddress.setText("");
			}
		};

		enterPlayerName.addMouseListener(clearNameText);
		enterServerAddress.addMouseListener(clearServerText);
	}


	/**
	 * Set up the key bindings on both the text fields to allow the user to continue by pressing enter:
	 */
	public void setUpKeyBindings () {

		goToRenderer = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {
				validateAllUserInput();
			}
		};

		// add the bindings to the Player Name input:
		enterPlayerName.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "Allow the user to continue with enter");
		enterPlayerName.getActionMap().put("Allow the user to continue with enter", goToRenderer);

		// add the bindings to the server address input:
		enterServerAddress.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "Allow the user to continue with enter");
		enterServerAddress.getActionMap().put("Allow the user to continue with enter", goToRenderer);

	}

	@Override
	public void setActionListener() {
		super.action = new ActionListener () {

			public void actionPerformed(ActionEvent e) {
				validateAllUserInput();
			}

		};
	}

}
