package myphotos.io;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

public class ReadImageFromDisk extends Thread {	
	private final int IMAGE_SIZE = 200;
	private volatile File file;
	private volatile BufferedImage image = null;
	
	public ReadImageFromDisk(ThreadGroup threadGroup, File f) {
		super(threadGroup, "IOTask");
		file = f;
	}
	
	public ReadImageFromDisk(File f) {		
		file = f;
	}
	
	public void run() {
		if (file == null) {
			System.out.println("File == null");
			return;
		}
		if (!file.exists()) {
			System.out.println("File does not exist - " + file.getAbsolutePath());
			return;
		}
		readAndSetImage(file);
	}
	
	private boolean readAndSetImage(File file)  {
		BufferedImage buffImage = null;
		InputStream input = null;
	    ImageInputStream imageInput = null;
	    // TODO use "trumbil" library for reading and resize image
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
		
		int width = buffImage.getWidth();
		int height = buffImage.getHeight();
		int imageWidth;
		int imageHeight;		
		if(width>height) {
			imageWidth = IMAGE_SIZE;
			imageHeight = IMAGE_SIZE*height/width;			
		} else {
			imageWidth = IMAGE_SIZE*width/height;
			imageHeight = IMAGE_SIZE;
		}				
		if (width>IMAGE_SIZE || height>IMAGE_SIZE) {
			image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = image.createGraphics();
			g2d.drawImage(buffImage, 0, 0, imageWidth, imageHeight, null);
			buffImage = null;
			g2d.dispose();
		} else {
			image = buffImage;
			buffImage = null;
			if (image == null) {
				System.out.println("image == null");
			}
		}
		return true;
	}
	
	public BufferedImage getImage() {
		return image;
	}
}