package GUI;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import render.Renderer;
import render.Renderer.Rotation;
import server_client.Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *  This class is the main JFrame for the GUI, and will hold of the Panel components
 *  (all the subclasses of JPanel that hold Buttons, MouseListeners etc)
 *  The default Border Layout is used here, so the Screen Panel is in the center
 *  the Buttons are at the bottom, etc.
 *
 * @author berceadrag
 *
 */

public class GameWindow extends JFrame{

	public static final int WINDOW_SIZE = 700;
	//public static final int WINDOW_HEIGHT = 700;

	private Renderer renderer;
	private Client client;

	// THE PANELS THAT WILL GO ON THIS FRAME
	private JPanel panel;
	private String gameTitle = "Adventure Game";


	// for the help feature:
	private JTextArea helpTextArea = new JTextArea();


	// COMPONENTS FOR THE MAIN GAME MENU:
	private JMenuBar menu = new JMenuBar ();
	private JMenu jMenu = new JMenu ("Menu");


	private JMenuItem help = new JMenuItem("Help");
	private JMenuItem exitGame = new JMenuItem("Exit Game"); // if the user is in the game and would like to exit

	// LISTENERS:
	private GameKeyListener keyListener;


	public GameWindow () {

		this.setVisible(true);
		this.setSize(WINDOW_SIZE,WINDOW_SIZE);
		this.setTitle("Main Game Menu");

		this.add(new StartUpGameMenu(this));
	}


	/**
	 * Add all of the JMenuItems to a JMenu, and then at the end
	 * add the JMenu to the JMenuBar:
	 */

	public void setUpGameMenu () {
		setUpMenuActionListeners();

		jMenu.add(help);
		jMenu.add(exitGame);

		menu.add(jMenu);
	}

	/**
	 * Launches a JOptionPane displaying the passed in error message
	 * @param error
	 */
	public void displayMessage (String title ,String error) {
		String [] OK = {"Ok"};
		JOptionPane.showOptionDialog(null, error, title ,JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, OK, OK[0]);
	}

	/**
	 * Add action listeners to all of the options in the JMenu
	 * so that they actually do something
	 */

	public void setUpMenuActionListeners () {


		help.addActionListener(new ActionListener () {

			public void actionPerformed(ActionEvent e) {
				// create a new JPanel where the user can get help:
				JFrame helpText = new JFrame ();
				helpText.setLocationRelativeTo(null);

				JPanel panel = new JPanel();


				helpTextArea.setEditable(false);

				try {loadHelpFile("src/menu/ReadMe.txt");}
				catch (FileNotFoundException e1) {e1.printStackTrace();}

				panel.add(helpTextArea,BorderLayout.CENTER);

				JScrollPane scrolPane = new JScrollPane(panel);
				helpText.add(scrolPane);

				helpText.setTitle("Help Menu");
				helpText.setVisible(true);
				helpText.setSize(300, 300);

			}

		});

		// If the user presses exit, open a JOption Pane to ask them if they are sure:
		exitGame.addActionListener(new ActionListener () {

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
					System.exit(0);

			}

		});

	}

	/**
	 * Read in the help text file, and load it's contents into the JText area, which will
	 * then be placed on a different frame when the user selects help
	 *
	 * @param file: the file name that has the help text
	 * @throws FileNotFoundException
	 */
	public void loadHelpFile (String file) throws FileNotFoundException {

		Scanner scan = new Scanner(new File(file));

		String helpText = "";
		while (scan.hasNext()){
			helpText = helpText + scan.nextLine();
			helpText += "\n";
		}

		this.helpTextArea.setText(helpText);
	}

	public void setRenderer (Renderer r) {
		this.renderer = r;
	}


	/**
	 * clear the contentpane and replaces the content of the frame with a renderer etc... essentially like a second constructor
	 */

	public void intializeGame(Client client) {

		this.client=client;

		// get rid of the start up gameMenu Panel
		this.getContentPane().removeAll();


		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		renderer = new Renderer(client.getRoom());
		client.setRenderer(renderer);

		this.add(renderer, BorderLayout.CENTER);

		// add some listeners:
		this.addMouseListener(new GameMouseListener(renderer));
		renderer.requestFocusInWindow();
		keyListener = new GameKeyListener(renderer,client, Rotation.NORTH);


		setUpGameMenu();
		menu.setVisible(true);
		this.setJMenuBar(menu);
		//this.pack();
		this.setVisible(true);

	}

	/**
	 * Set the GameWindows JPanel to this panel:
	 */
	public void setPanel (JPanel panel) {
		// get rid of a previous panel
		this.getContentPane().removeAll();

		this.add(panel);
		this.setTitle(gameTitle);
		this.setVisible(true);
	}




}
