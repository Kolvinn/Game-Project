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
import server_client.Control.View;

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
	
	private String characterName;

	//private JTextField text = new JTextField();
	private Window window;
	private boolean isError = false;
	private ImagePanel but;
	
	public CharacterPanel(Window window){
		this.window= window;
		this.setSize(width, height);		
		this.setLayout(null);
		
		readImages();
		setBackground();
		setTextAndBtn();
		
		this.setVisible(true);
		this.repaint();
	}
	public String getCharacterName(){
		return characterName;
	}
	private void setTextAndBtn(){
		text.setBounds(450, 300, 300, 50);
		text.setEditable(true);
		text.setHorizontalAlignment(JTextField.CENTER);

		text.setText("Please enter the name of your character");
		this.add(text, PALETTE_LAYER);
		
		but = new ImagePanel("Done",button,this);
		but.setBounds(450, 400, 300, 100);
		this.add(but,PALETTE_LAYER);
	}
	public void update(String name){
		System.out.println(name);
		if(text.getText().length()==0 || text.getText().contains("Please enter the name of your character")){
			isError=true;
		}
		else{
			characterName=text.getText();
			System.out.println("Updating game");
			window.updateView(View.GAME);
		}
	}
	
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(isError){
			System.out.println("IS an ERROR");
			g.drawString("POOOOOOOOOOOOOOOOOOOOOOOOOOP", 100, 100);
		}
	}

}
