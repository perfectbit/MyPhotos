package myphotos.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SaveImageOnDisk extends Thread{
	private File file;
	private BufferedImage img;
	private String cacheName;
	
	public SaveImageOnDisk(BufferedImage img, File file, String cacheName) {
		this.file = file;
		this.img = img;
		this.cacheName = cacheName;
	}
	
	public void run() {
		try {
		    File outputfile = new File("imgcache\\" + cacheName);
		    ImageIO.write(img, "jpeg", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}
