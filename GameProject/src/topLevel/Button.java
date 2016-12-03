package topLevel;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Button extends JPanel{
	private String name;
	private Image background;
	private JPanel obs;
	public Button(Rectangle bounds, String name, JPanel obs){
		this.obs=obs;
		try {
			background = ImageIO.read(new File("src/menu/menuBtn.jpg"));
		} catch (IOException e) {
			System.out.println("FAILURE");
			e.printStackTrace();
		}
		setBounds(bounds);
		this.name=name;
		this.repaint();
		this.setVisible(true);
		
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		System.out.println("SDSDSDSDSD");
		g.drawImage(background, this.getX(), this.getY(),this.getWidth(),this.getHeight(), obs);
	}
}
