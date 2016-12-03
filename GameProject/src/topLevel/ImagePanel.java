package topLevel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class ImagePanel extends JPanel implements MouseListener{
	private Image background;
	private String name;
	private LayeredPane menuPanel;
	public ImagePanel(Image background, LayeredPane menuPanel){
		this.menuPanel=menuPanel;
		this.background=background;
	}
	public ImagePanel(String name, Image background,LayeredPane menuPanel){
		this.menuPanel=menuPanel;
		this.background=background;
		this.name=name;
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.GRAY,Color.GRAY));
		setFocusable(true);
		this.addMouseListener(this);
	}
	public void paintComponent(Graphics g){
			g.drawImage(background, 0, 0, getWidth(), getHeight(),this);
			if(name!=null){
				FontMetrics metrics = g.getFontMetrics(new Font("Arial", Font.BOLD, 25));
				Color maroon = new Color (128, 0, 0);
				g.setFont(new Font("Arial", Font.BOLD, 25));
				g.setColor(maroon);
				// Determine the X coordinate for the text
				int x = (getBounds().width - metrics.stringWidth(name)) / 2;
				// Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
				int y = ((getBounds().height - metrics.getHeight()) / 2) + metrics.getAscent();
				g.drawString(name, x, y);
			}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("Click");
		if(name!=null)
			menuPanel.update(name);
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.RED,Color.RED));
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.GRAY,Color.GRAY));
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		//this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.GRAY,Color.GRAY));
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {

		
		
	}

}
