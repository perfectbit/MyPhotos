package myphotos.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ImageView extends JPanel {
	private BufferedImage buffImage;
	private JPanel buttonsPanel;
	private JustImage imagePanel;
	private JButton turnLeft = new JButton("Turn left");
	private JButton turnRight = new JButton("Turn right");
	private JButton zoomIn = new JButton("Zoom In");
	private JButton zoomOut = new JButton("Zoom out");

	public ImageView() {		
		setLayout(new BorderLayout());
		buttonsPanel = new JPanel();
		
		buttonsPanel.add(turnLeft);
		buttonsPanel.add(turnRight);
		buttonsPanel.add(zoomIn);
		buttonsPanel.add(zoomOut);		
		
		imagePanel = new JustImage();
		imagePanel.addMouseListener(new ViewMouseListener());
		add(imagePanel, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
	}
	
	public void setImage(File file) {
		InputStream input = null;
	    ImageInputStream imageInput = null;	    
		try {
			try {
				input = new FileInputStream(file);
				imageInput = ImageIO.createImageInputStream(input);
		    	buffImage = ImageIO.read(imageInput);
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class ViewMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (arg0.getClickCount() >= 2) {
				ImagesPanel.showImagePanel();
				repaint();
			}
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}
	
	class JustImage extends JPanel {
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			int width = 0;
			int height = 0;

			if (buffImage.getWidth() > buffImage.getHeight()) {
				width = this.getWidth();
				height = buffImage.getHeight() * this.getWidth()
						/ buffImage.getWidth();
			} else {
				width = buffImage.getWidth() * this.getHeight()
						/ buffImage.getHeight();
				height = this.getHeight();
			}
			int x = (this.getWidth() - width) / 2;
			int y = (this.getHeight() - height) / 2;

			g2d.drawImage(buffImage, x, y, width, height, null);
		}
	}
}
