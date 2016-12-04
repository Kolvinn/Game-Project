package topLevel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import render.Renderer;
import render.RenderPanel;
import server_client.Control;

public class GameWindow extends JFrame {
	
	public enum View {GAME,MENU,CHARACTER};
	
	private static int width =1200, height = 1000;
	private static int btnHeight = 50;
	private static int btnDist = 20;
	
	private View currentView;
	private ImageIcon gif = new ImageIcon(getClass().getResource("gif3.gif"));

	private JLayeredPane controlPane;
	
	public GameWindow(){
		
		controlPane = new JLayeredPane();
		controlPane.setSize(width, btnHeight);
		controlPane.setVisible(true);
		this.add(controlPane);

		this.setSize(width,height);
		this.setFocusable(true);
		updateView(View.MENU);
		this.setVisible(true);
	}
	/**
	 * Determines what Panel will run based on what the given view is
	 * @param view
	 */
	public void updateView(View view){
		//TODO need to remove all without breaking it
		if(controlPane.getComponentCount()!=0)
			controlPane.remove(0);;
		
		switch(view){	
		case CHARACTER:				
			controlPane.add(new CharacterPane(this), JLayeredPane.DEFAULT_LAYER);
			break;
		case GAME:			
			//controlPane.add(new RenderPane(), JLayeredPane.DEFAULT_LAYER);
			//repaint();
			GamePane pane= new GamePane(this);
			this.addKeyListener(pane);
			this.addMouseListener(pane);
			controlPane.add(new GamePane(this), JLayeredPane.DEFAULT_LAYER);
			break;
		case MENU:
			controlPane.add(new MenuPane(this),JLayeredPane.DEFAULT_LAYER);
			break;
		default:
			System.out.println("repainting window");
			repaint();			
		}
	}


}
