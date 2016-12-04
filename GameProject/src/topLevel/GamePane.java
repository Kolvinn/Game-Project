package topLevel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import render.RenderPanel;
import server_client.Control;

public class GamePane extends LayeredPane implements KeyListener, MouseListener{
	private static final long serialVersionUID = 1L;
	private Control controller;
	
	public GamePane(GameWindow window){
		super(window);
		controller = new Control(this);
		this.setSize(width, height);
		this.setLayout(null);
		this.setVisible(true);
		RenderPanel render = new RenderPanel();
		render.setFocusable(true);
		render.setBounds(0, 0, width,height);
		this.add(render,DEFAULT_LAYER);

		this.setFocusable(true);
		this.repaint();
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		System.out.println("MOUSECLICKED");
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		//System.out.println("MOUSERELEASED");
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		//System.out.println("KEYPRESSED");
		switch(arg0.getKeyChar()){
		case('w'):
			System.out.println('w');break;
		case('s'):
			System.out.println('s');break;
		case('d'):
			System.out.println('d');break;
		case('a'):
			System.out.println('a');break;
		default:
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		//System.out.println("KEYRELEASED");
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		//System.out.println("KETTYPED");
		
	}
	
	

}
