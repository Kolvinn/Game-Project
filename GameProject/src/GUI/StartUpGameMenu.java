package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import server_client.Main;

/**
 * panel subclass of JFrame will be used in order to render the game menu that shows up whenever a new game is created
 * @author berceadrag
 *
 */
public class StartUpGameMenu extends JPanel {

	// For the Background of this frame:
	private Image background;
//	private GameMenuPanel panel;

	// For the textColor of the JButtons:
	private Color buttonColor = new Color (0,204,204);

	// Buttons for the start up menu:
	private JButton newGame = new JButton ("New Game");
	private JButton joinGame = new JButton ("Join Game");
	private JButton exit = new JButton ("Exit");

	// Dimensions of the game menu:
	private final int WIDTH = 1000;
	private final int HEIGHT = 1000;

	// Numbers for where and how the buttons will be displayed:

	// Coordinates for where we will start drawing the JButtons on the panel:
	private final int BUTTON_X = 250;
	private final int BUTTON_Y = 100;

	// the amount of space between buttons:
	private final int BUTTON_SPACE = 200;


	// So that the StartUpGameMenu knows about the GameWindow frame:
	public GameWindow gameWindow;


	public StartUpGameMenu (GameWindow frame) {
		this.gameWindow = frame;

		this.setSize(WIDTH, HEIGHT);

		// set up the background of the Start up game menu:
		try {
			loadBackground("src/menu/MainMenu.jpg");
		}
		catch (IOException e) {System.out.println("couldn't read the file!");}


		// I will set the positions of the buttons on the screen myself:
		this.setLayout(null);
		setUpButtons();

		this.setVisible(true);

	}

	/**
	 * set up the background for the panel that will go on this frame
	 * the background is specified by an image that has a file name:
	 *
	 * @param fileName
	 * @throws IOException
	 */
	public void loadBackground (String fileName) throws IOException {
		background = ImageIO.read(new File(fileName));
	}

	/**
	 * This method, takes in a button, makes it transparent, gets rid of the border
	 * sets the color of the buttons text (to the buttonColor field)
	 * and basically just makes it blend into the background
	 *
	 * @param button
	 */
	public void setUpStyleForButton (JButton button) {

		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setForeground(this.buttonColor);
	}



	/**
	 * Takes in a Button and its number (so a number of 0 would mean that this is the first one being added
	 * to the menu, 1 would mean it's the second etc..), height and width. It then sets the dimensions of the
	 * button before adding it to the panel
	 *
	 * @param button
	 * @param buttonNumber: please note that this is 0 based!
	 * @param buttonHeight
	 * @param buttonWidth
	 */
	public void addButtonToMenu (JButton button, int buttonNumber, int buttonHeight, int buttonWidth) {
		button.setBounds(BUTTON_X,BUTTON_Y +
				buttonNumber*buttonHeight + buttonNumber*BUTTON_SPACE,buttonWidth,buttonHeight);
		this.add(button);
	}



	/**
	 * Set up action listeners for the buttons and add them to panel panel
	 */
	public void setUpButtons () {

		GameWindow gameWindow = this.gameWindow;

		newGame.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO: Will need some code uin here that opens up a different
				// Window that allows the user to  create a server etc ..
				gameWindow.setPanel(new SelectCharacterPanel(gameWindow));
			}

		});


		joinGame.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				gameWindow.setPanel(new JoinGamePanel());
				System.out.println("options ");
			}

		});

		exit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// open up a new option pane, to see what the user wants to do
				JOptionPane areTheySure = new JOptionPane();

				// what they can do:
				String [] exitOptions = {"Yes" ,"No"};

				// the choice they make:
				int choice = JOptionPane.showOptionDialog(null, "Are you sure you want to exit the game ?","EXIT",
						JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,exitOptions,exitOptions[0]);

				// if the user chose to exit, close the program (not really sure how to close only the frame):
				if (choice == 0)
					System.exit(0);			}

		});

		// Set the locations of the buttons on screen and add them to the panel:
		addButtonToMenu(newGame,0,40,180);
		addButtonToMenu(joinGame,1,40,180);
		addButtonToMenu(exit,2,40,180);

		// Style the buttons a little:
		setUpStyleForButton(newGame);
		setUpStyleForButton(joinGame);
		setUpStyleForButton(exit);


	}


	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0,this.getWidth(),this.getHeight(), null);
	}



	/**
	 * an inner class for the panel that this game menu will use:
	 *
	 * @author berceadrag
	 *
	 */
	private class GameMenuPanel extends JPanel {

		private Image background;

		public GameMenuPanel(Image image) {
			this.background = image;
		}

		public void paintComponent (Graphics g) {
			super.paintComponent(g);
			g.drawImage(background, 0, 0,this.getWidth(),this.getHeight(), null);
		}

	}






}
