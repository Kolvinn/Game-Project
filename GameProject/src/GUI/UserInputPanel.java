package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * This class is here because both JoinGamePanel and SelectCharacterPanel, have similar
 * behavior, so some of the methods that those classes have will be inherited from here:
 *
 * @author berceadrag
 *
 */
public abstract class UserInputPanel extends JPanel {

	// the background image for this panel:
	private Image image;
	// finishing can be done by using a JButton
	private JButton done = new JButton("Done");
	// the action that happens when the Done button is pressed on this UserInputPanel
	protected ActionListener action;


	public UserInputPanel () {

	}


	/**
	 * For both subclasses, the GUI functionality needs to be created, this method will do it:
	 */
	public void createGUI () {
		setUpKeyBindings();
		setActionListener();
		setUpButton();
		setUpMouseAdapterToClearTextWithCLick();
	}


	public void paintComponent (Graphics g) {
		g.drawImage(image, 0, 0,this.getWidth(),this.getHeight(), null);
	}


	/**
	 * Load the image for the background of this panel:
	 *
	 * @param fileName: the name of the image
	 * @throws IOException
	 */
	public void setUpImageBackground (String fileName) throws IOException {
		this.image = ImageIO.read(new File(fileName));
	}


	/**
	 * It'd be cool to allow the user to have the option of either pressing enter, or the 'ok' button to proceed
	 * so I will set up some Key Bindings
	 */
	public abstract void setUpKeyBindings ();

	/**
	 * The mouse adapter will be used so that the user can clear the text field when they click on it
	 * Ryan and I think this is a nice feature that improves the user experience
	 */
	public abstract void setUpMouseAdapterToClearTextWithCLick ();

	/**
	 * both subclasses of this one have slightly different actions
	 */
	public abstract void setActionListener ();


	/**
	 * add our action for the done button, to the done button
	 * and make it look bold, colorful and nice:
	 */
	public void setUpButton () {
		done.addActionListener(action);

		done.setOpaque(false);
		done.setContentAreaFilled(false);
		done.setBorderPainted(false);
		done.setForeground(Color.ORANGE);
		done.setSize(200, 200);

		done.setFont(new Font ("Arial",Font.PLAIN,40)); // Make the Done Button Bold and in the center of the page

		this.add(done,BorderLayout.CENTER);
	}


}
