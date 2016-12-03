package server_client;

import java.awt.Component;

import game.GameRunner;
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
		window.run(view);
	}

}
