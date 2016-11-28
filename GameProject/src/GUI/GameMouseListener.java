package GUI;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import render.Renderer;

/**
 * This is a simple class that allows the user to click on the screen and have
 * the mouse respond to that action. This Mouse Listener only does something if the
 * user clicks on the main game screen, as shown on line 25 - 26
 *
 * @author berceadrag
 *
 */

public class GameMouseListener implements MouseListener {

	private Renderer renderer;
	private ScreenPopUpPanel popUp;

	public GameMouseListener (Renderer screen) {
		this.renderer = screen;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		// now create a pop up, since the user has clicked on an item on the screen:
		String[] descriptionOfTile = renderer.getTile(e.getX(), e.getY());

		if (descriptionOfTile == null)
			return;

		// only bring up a new pop up if the current window is closed
		if (ScreenPopUpPanel.windowIsClosed)
			popUp = new ScreenPopUpPanel(descriptionOfTile,e.getXOnScreen(),e.getYOnScreen());
		// if the window is open do not open another one!
		else
			return;

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
