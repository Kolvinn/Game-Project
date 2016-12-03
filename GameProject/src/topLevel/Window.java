package topLevel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import server_client.Control;
import server_client.Control.View;

public class Window extends JFrame {
	private static int width =1200, height = 1000;
	private static int btnHeight = 50;
	private static int btnDist = 20;
	
	private View currentView;
	private ImageIcon gif = new ImageIcon(getClass().getResource("gif3.gif"));
	private Control controller;
	JLayeredPane controlPane;
	
	public Window(Control controller, View view ){
		
		controlPane = new JLayeredPane();
		controlPane.setSize(width, btnHeight);
		controlPane.setVisible(true);
		this.add(controlPane);
		
		this.currentView = view;
		this.controller=controller;
		
		this.setVisible(true);
		this.setSize(width,height);
		this.setFocusable(true);

		run(currentView);
		this.setVisible(true);
	}

	public void run(View view){
		//TODO need to remove all without breaking it
		if(controlPane.getComponentCount()!=0)
			controlPane.remove(0);
		
		switch(view){	
		case CHARACTER:				
			controlPane.add(new CharacterPanel(this), JLayeredPane.PALETTE_LAYER);
			break;
		case GAME:
			break;
		case MENU:
			controlPane.add(new MenuPanel(this),JLayeredPane.DEFAULT_LAYER);
			break;
		default:
			repaint();
			break;
			
		}
	}
	

	
	public void updateView(View view){
		controller.update(view);
	}
}
