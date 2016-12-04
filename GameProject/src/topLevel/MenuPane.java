package topLevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import topLevel.GameWindow.View;


public class MenuPane extends LayeredPane {
	
	private static String[] main = {"New Game","Load Game","Options","Quit"}, 
			newGame = {"Classic","Speed Run","Death March"},
			options = {"Audio", "Controls"},
			loadGame = {"NO GAMES NIGASS"},
			previous, current;

	
	public MenuPane(GameWindow window){
		super(window);
		this.setSize(width, height);
		this.setLayout(null);
		readImages();
		setBackground();
		current=main;
		createBtns(current);

		this.repaint();
		this.setVisible(true);

		
		
	}

	/**
	 * Performs the actions that relate to the given button, updates the UI to represent the change
	 * @param button
	 */
	public void update(String button){
		if(button.equals(null))
			return;
		switch(button){
		case("Classic"):
			//TODO create some character selection Panel
			System.out.println("updating to character");
			window.updateView(View.CHARACTER);
			

			break;		
		case("Back"):
			//TODO need to add a queue of previous lists
			current=previous;
			previous =null;
			resetUI(current);
			break;
		case("New Game"):
			previous=current;
			current=newGame;
			resetUI(newGame);
			break;
		case("Load Game"):
			previous=current;
			current=loadGame;
			resetUI(loadGame);
			break;
		case("Options"):
			previous=current;
			current=options;
			resetUI(options);
			break;
		case("Quit"):
			// open up a new option pane, to see what the user wants to do
			JOptionPane areTheySure = new JOptionPane();

			// what they can do:
			String [] exitOptions = {"Yes" ,"No"};

			// the choice they make:
			int choice = JOptionPane.showOptionDialog(null, "Are you sure you want to exit the game ?","EXIT",
					JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,exitOptions,exitOptions[0]);

			// if the user chose to exit, close the program (not really sure how to close only the frame):
			if (choice == 0)
				System.exit(0);
			break;
			default:
				System.out.println("does nothing");
		}
		
	}

	private void resetUI(String[] buttons){
		removeAll();
		setBackground();
		createBtns(buttons);
		
	}
	/**
	 * Adds all buttons in the current menu set to the palette layer
	 * @param buttons
	 */
	private void createBtns(String[] buttons){
		ImagePanel panel;
		//add a back button if there was a previous menu
		if(previous!=null){
			panel = new ImagePanel("Back", button,this);
			panel.setBounds(400,50,380,btnHeight);		
		}
		//make main menu if furthest back
		else{
			panel = new ImagePanel("Main Menu", button,this);
			panel.setBounds(400,50,380,btnHeight);
		}
		this.add(panel, PALETTE_LAYER);

		int y = determineBtnY(buttons.length);
		for(String s:buttons){	
			panel = new ImagePanel(s,button, this);
			panel.setBounds(400, y, 380, btnHeight);
			this.add(panel, PALETTE_LAYER);
			y = y+btnHeight+btnDist;
		}
		
	}
	private int determineBtnY(int btnCount){		
		//total pixels that buttons and their distances apart will take up
		int pixelRemoved = (btnCount*btnHeight)   +   ((btnCount-1)*btnDist);
		//determine the starting y
		int startY = (height - pixelRemoved)/2; 
		return startY;		
	}	

}
