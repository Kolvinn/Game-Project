package server_client;



import game.GameRunner;
import topLevel.GamePane;
import topLevel.GameWindow;
import topLevel.GameWindow.View;

public class Control extends Thread {
	private GamePane pane;
	public Control(GamePane pane){
		this.pane=pane;
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
		}
				
	}

}
