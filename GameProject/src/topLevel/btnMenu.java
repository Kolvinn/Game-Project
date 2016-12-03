package topLevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class btnMenu extends JPanel implements ActionListener{
	private int width, height;
	private static int btnHeight = 70;
	private static int btnDist = 40;
	private static String[] main = {"New Game","Load Game","Options","Quit"};
	private static String[] current = main, previous;
	private static String[] newGame = {"Classic","Speed Run","Death March"};
	private HashMap<Rectangle, String> currentWords = new HashMap<Rectangle, String>();
	private ImageIcon back = new ImageIcon(getClass().getResource("menuBtn.jpg"));
	private ImageIcon gif = new ImageIcon(getClass().getResource("giphy.gif"));
	private Image background;

	String currentState = "main";

	public btnMenu(int width, int height) {
		this.width=width;
		this.height=height;

		try {
			background = ImageIO.read(new File("src/menu/MainMenu.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reset();
		Button b = new Button(new Rectangle(100,100,180, btnHeight), "Button", this);
		b.setBounds(new Rectangle(100,100,180, btnHeight));
		
		
		this.add(b);
		System.out.println(this.getComponent(0).getBounds());
		repaint();
		//setMenu(main);		
	}
	private void reset(){
		this.setLayout(null);
		this.setSize(width,height);	
		this.setVisible(true);
	}
	
	private int determineBtnY(int btnCount){
		
		//total pixels that buttons and their distances apart will take up
		int pixelRemoved = (btnCount*btnHeight)   +   ((btnCount-1)*btnDist);
		//determine the starting y
		int startY = (height - pixelRemoved)/2; 
		//System.out.println(startY);
		return startY;
		
	}
	
	private void setBtn(JButton btn, String string, int y){
		btn.setIcon(back);
		//System.out.println(string);
		//JLabel label = new JLabel(string);
		
		//Color maroon = new Color (128, 0, 0);
		//label.setFont(new Font("Arial", Font.BOLD, 25));
		//label.setForeground(maroon);
		//label.setBounds(400, y, 380, btnHeight);
		//btn.add(label, new BorderLayout());
		
		btn.addActionListener(this);
		
	}
	
	private void setMenu(String[] menu){
		int y = determineBtnY(menu.length);
		//JButton top = new JButton("Main Menu");		
		//top.setBounds(400,50,380,btnHeight);
		//setBtn(top,"Main Menu",50);
		JLabel l = new JLabel();
		l.setBounds(400,50,380,btnHeight);
		l.setIcon(back);

		l.setForeground(new Color (128, 0, 0));
		
		l.setText("asdasdasd");
		
		currentWords.put(new Rectangle(400,120,380,btnHeight), "Main Menu");
		this.add(l);
		
		for(String s:menu){
			
			JButton btn = new JButton(s);
			btn.setBounds(400, y, 380, btnHeight);
			setBtn(btn,s,y);
			this.add(btn);
			currentWords.put(new Rectangle(400,y+btnHeight,380,btnHeight), s);
			y = y+btnHeight+btnDist;
		}
	}
	
	public void paintComponent(Graphics g){
		//super.paintComponent(g);
        //g.drawImage(background, 0, 0,width,height, null);
       /* for(Entry<Rectangle, String> entry: currentWords.entrySet()){
        	Color maroon = new Color (128, 0, 0);
        	g.setColor(maroon);
    		setFont(new Font("Arial", Font.BOLD, 25));
    		g.se
        	g.drawString(entry.getValue(),entry.getKey().x, entry.getKey().y);
        	
        }*/
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println(e.getActionCommand());
		//for(Component j:getComponents())
			//System.out.println(((JButton)j).getComponent(0).getSize());
		//System.out.println(j);
		switch(e.getActionCommand()){
		case("New Game"):
			previous = current;
			
			//for(Component j:getComponents())
				//System.out.println(j);
			removeAll();
			System.out.println("REMOVED ALL");
			//for(Component j:getComponents())
				//System.out.println(j);
			this.repaint();
			reset();
			setMenu(newGame);
			break;
		}
		
	}

}
