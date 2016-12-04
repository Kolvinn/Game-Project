package topLevel;


import java.awt.Graphics;

import javax.swing.JTextField;
import topLevel.GameWindow.View;

/**\
 * This class is a subclass of JPanel that will show up on the screen when the user selects new game
 * The panel has a text field to allow the user to name their character. After they name their character
 * they will be taken to a new JoinGame Panel where they can enter their name and a server address
 *
 * @author berceadrag
 *
 */


public class CharacterPane extends LayeredPane{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField text = new JTextField();	
	
	private String characterName;


	private boolean isError = false;
	private ImagePanel but;
	
	public CharacterPane(GameWindow window){
		super(window);
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
