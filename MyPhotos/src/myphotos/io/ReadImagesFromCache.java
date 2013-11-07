package myphotos.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

public class ReadImagesFromCache {
	private LinkedList<String> nameOfCacheFiles;
	
	public ReadImagesFromCache(LinkedList<String> cacheFiles) {
		nameOfCacheFiles = cacheFiles;
	}
	
	public synchronized LinkedList<BufferedImage> read() {
		int filesCount = 0;
		LinkedList<ReadImageFromDisk> tasks = new LinkedList<ReadImageFromDisk>();
		ReadImageFromDisk task;
		ThreadGroup threadGroup = new ThreadGroup("Group of Threads");

		for (String fileName : nameOfCacheFiles) {
			String str = System.getProperty("user.dir") +  "\\imgcache\\" + fileName;
			
			File file = new File(str);
			System.out.println("Read file - " + file.getAbsolutePath());
			task = new ReadImageFromDisk(threadGroup, new File(str));
			tasks.add(task);
			task.start();
			if (++filesCount > 15) {
				try {
					filesCount = 0;
					wait(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		while (threadGroup.activeCount() > 0) {
			try {
				wait(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LinkedList<BufferedImage> buffList = new LinkedList<BufferedImage>();
		
		for (ReadImageFromDisk elem : tasks) {
			if (elem.getImage() == null) {
				System.out.println("image == null");
			}
			buffList.add(elem.getImage());
		}
		System.out.println("All images readed from harddisc");
		return buffList;
	}
}
