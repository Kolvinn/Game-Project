package server_client;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JLayeredPane;

import game.GameRunner;
import game.Player;
import render.RendererRemade;
import topLevel.CharacterPanel;
import topLevel.MenuPanel;
import topLevel.Window;

public class Control extends Thread {
	public enum View {GAME,MENU,CHARACTER};
	Window window;
	public Control(){
		window = new Window(this, View.MENU);
	}


	public void update(View view){
		if(view!=View.GAME)
			window.run(view);
		else{
			startGame();
		}
	}
	
	private void startGame(){
		GameRunner game = new GameRunner();
		System.out.println(window.getComponent(0));
		
		CharacterPanel pp = (CharacterPanel)window.getPane().getComponent(0);
		
		Player player = game.createPlayer(pp.getCharacterName(),"Mr Hero Pants");
		
		//remove the starting menus
		window.remove();
		RendererRemade render = new RendererRemade(game.getPlayerRoom(player));
		window.getPane().add(render, JLayeredPane.PALETTE_LAYER);
		run();
		
			
	}
	@Override
	public void run(){
		//update every x amount of seconds
		while (true) {
		    try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    System.out.println("updating");
		    window.repaint();
		}
		
		
	}

}
