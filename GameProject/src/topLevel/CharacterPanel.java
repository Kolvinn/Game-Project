package topLevel;

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
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import GUI.UserInputPanel;
import server_client.Main;

/**\
 * This class is a subclass of JPanel that will show up on the screen when the user selects new game
 * The panel has a text field to allow the user to name their character. After they name their character
 * they will be taken to a new JoinGame Panel where they can enter their name and a server address
 *
 * @author berceadrag
 *
 */


public class CharacterPanel extends LayeredPane{
	private JTextField text = new JTextField();	

	//private JTextField text = new JTextField();
	private Window window;
	
	public CharacterPanel(Window window){
		this.window= window;
		this.setSize(width, height);
		
		this.setLayout(null);
		readImages();
		setBackground();
		text.setBounds(300, 300, 100, 50);
		this.add(text, PALETTE_LAYER);
		this.setVisible(true);
		this.repaint();
	}

}
