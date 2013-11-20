package myphotos.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import myphotos.main.App;

public class ReadImagesFromCache {
	private LinkedList<String> nameOfCacheFiles;
	
	public ReadImagesFromCache(LinkedList<String> cacheFiles) {
		nameOfCacheFiles = cacheFiles;
	}
	
	public synchronized LinkedList<BufferedImage> read() {	
		final int lenght = nameOfCacheFiles.size();
		final LinkedList<ReadImageFromDisk> tasks = new LinkedList<ReadImageFromDisk>();
		final ThreadGroup threadGroup = new ThreadGroup("ThreadGroup");

		final JDialog pleaseWaitDialog = new JDialog(App.getMainFrame(),
				"Loading images", true);
		final JProgressBar progressBar = new JProgressBar(0, lenght);
		final JPanel panel = new JPanel();
		panel.add(progressBar);
		pleaseWaitDialog.getContentPane().add(panel);
		pleaseWaitDialog.pack();
		pleaseWaitDialog.setLocationRelativeTo(App.getMainFrame());
		
		SwingWorker<Void, Integer> swingWorker = new SwingWorker<Void, Integer>() {
			@Override
			protected Void doInBackground() throws Exception {
				ReadImageFromDisk task;
				System.out.println("lenght = " + lenght);
				int i = 0;
				for (String fileName : nameOfCacheFiles) {
					publish(i++);
					task = new ReadImageFromDisk(threadGroup, new File("imgcache\\" + fileName));
					tasks.add(task);
					task.start();
					Thread.sleep(50);
					while (threadGroup.activeCount() > 5) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				return null;
			}

			@Override
			protected void process(List<Integer> chunks) {
				final Integer integer = chunks.get(chunks.size() - 1);
				progressBar.setValue(integer);
				panel.repaint();
			}

			@Override
			protected void done() {
				pleaseWaitDialog.setVisible(false);
			}
		};
		swingWorker.execute();
		pleaseWaitDialog.setVisible(true);
		
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
