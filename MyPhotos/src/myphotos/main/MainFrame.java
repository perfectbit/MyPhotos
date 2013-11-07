package myphotos.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import myphotos.components.ImagePreview;
import myphotos.components.TreePanel;
import myphotos.components.ImagesPanel;
import myphotos.components.TagsPanel;
import myphotos.io.ReadImageFromDisk;

public class MainFrame extends JFrame {
	private final int X_START = 150;
	private final int Y_START = 100;
	private final int FRAME_WIDTH = 1000;
	private final int FRAME_HEIGHT = 800;

	private JMenuBar mainMenu;
	private JSlider sliderSize;
	private JFileChooser fileChOneImage;
	private JFileChooser fileChScan;

	private TreePanel treePanel;
	private ImagesPanel imagesPanel;
	private TagsPanel tagsPanel;

	public MainFrame() {
		super("MyPhotos 0.1");
		this.setBounds(X_START, Y_START, FRAME_WIDTH, FRAME_HEIGHT);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);

		WindowListener exitListener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int confirm = JOptionPane.showOptionDialog(null,
						"Are You Sure to Close Application?",
						"Exit Confirmation", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);
				
				if (confirm == JOptionPane.YES_OPTION) {
					App.getModel().shutdown();
					System.exit(JFrame.DISPOSE_ON_CLOSE);
				}
			}
		};
		// TODO "NO" option doesnt work
		//this.addWindowListener(exitListener);
		treePanel = new TreePanel(this);
		tagsPanel = new TagsPanel();
		imagesPanel = new ImagesPanel();
		// create topSplitPane
		JSplitPane topSplitPane = new JSplitPane();
		topSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		topSplitPane.setContinuousLayout(true);
		topSplitPane.setOneTouchExpandable(true);
		this.getContentPane().add(topSplitPane, BorderLayout.CENTER);
		// create leftSplitPane
		JSplitPane leftSplitPane = new JSplitPane();
		leftSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		topSplitPane.setLeftComponent(leftSplitPane);
		leftSplitPane.setLeftComponent(tagsPanel);
		leftSplitPane.setRightComponent(treePanel);
		topSplitPane.setRightComponent(imagesPanel);
		initMainMenu();
		// create slider
		sliderSize = new JSlider(128, 512, 256);
		sliderSize.addChangeListener(new StateChanged());
		JPanel statusBar = new JPanel();
		statusBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		statusBar.add(sliderSize);
		statusBar.add(new JLabel("Size"));
		// this.getContentPane().add(statusBar, BorderLayout.SOUTH);
		setJMenuBar(mainMenu);
	}
	
	public void showImagesWithThatTag(String tag) {
		imagesPanel.showImagesWithThatTag(tag);
	}

	private void initMainMenu() {
		JMenu menuFile;
		JMenuItem menuItemOpen;
		JMenuItem menuItemQuit;
		JMenuItem menuScanFolder;
		mainMenu = new JMenuBar();
		menuItemOpen = new JMenuItem("Open Image");
		menuItemOpen.addActionListener(new ActionOpenImage());
		menuItemQuit = new JMenuItem("Quit");
		menuItemQuit.addActionListener(new ActionQuit());
		menuScanFolder = new JMenuItem("Scan folder");
		menuScanFolder.addActionListener(new ScanAction());
		menuFile = new JMenu("  File  ");
		menuFile.add(menuItemOpen);
		menuFile.add(menuScanFolder);
		menuFile.add(menuItemQuit);
		mainMenu.add(menuFile);
	}

	private class ScanAction implements ActionListener {
		@Override
		public synchronized void actionPerformed(ActionEvent arg0) {
			JFrame mainFrame = App.getMainFrame();
			int result = 0;
			if (fileChScan == null) {
				fileChScan = new JFileChooser();
				fileChScan.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChScan.setCurrentDirectory(new File("."));
				result = fileChScan.showOpenDialog(mainFrame);
			} else {
				result = fileChScan.showOpenDialog(mainFrame);
			}
			if (fileChScan != null && result == JFileChooser.APPROVE_OPTION
					&& fileChScan.getSelectedFile().isDirectory()) {
				File[] files;
				File directory = fileChScan.getSelectedFile();
				FilenameFilter fnf = new ImageFilter();
				files = directory.listFiles(fnf);
				LinkedList<BufferedImage> listOfImages;

				imagesPanel.clearOldImages();
				// TODO use thread queue complete
				// TODO use swingworks for loding progressbar
				listOfImages = readImages(files);

				int i = 0;
				for (BufferedImage img : listOfImages) {
					ImagePreview imgPre = new ImagePreview(img, files[i], "--New--");
					imagesPanel.addImagePreview(imgPre);
					App.getModel().addNewImage(img, files[i++]);
				}
				imagesPanel.changePreferredSize();
				imagesPanel.revalidate();
				App.getMainFrame().validate();
			}
		}

		private LinkedList<BufferedImage> readImages(File[] files) {
			int filesCount = 0;
			LinkedList<ReadImageFromDisk> tasks = new LinkedList<ReadImageFromDisk>();
			ReadImageFromDisk task;
			ThreadGroup threadGroup = new ThreadGroup("Group of Threads");

			for (File file : files) {
				task = new ReadImageFromDisk(threadGroup, file);
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
				buffList.add(elem.getImage());
			}
			return buffList;
		}
	}

	private class StateChanged implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent event) {
			JSlider source = (JSlider) event.getSource();
		}
	}

	private class ActionQuit implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int confirm = JOptionPane.showOptionDialog(null,
					"Are You Sure to Close Application?",
					"Exit Confirmation", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (confirm == JOptionPane.YES_OPTION) {				
				App.getModel().shutdown();
				System.exit(JFrame.DISPOSE_ON_CLOSE);
			}
		}
	}

	private class ImageFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".jpg")
					|| name.toLowerCase().endsWith(".jpeg")
					|| name.toLowerCase().endsWith(".png")
					|| name.toLowerCase().endsWith(".bmp");
		}
	}

	private class ImageAndFolderFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".jpg")
					|| f.getName().toLowerCase().endsWith(".jpeg")
					|| f.getName().toLowerCase().endsWith(".png")
					|| f.getName().toLowerCase().endsWith(".bmp")
					|| f.isDirectory();
		}

		@Override
		public String getDescription() {
			return "Image Files (.jpg, .jpeg, .png, .bmp)";
		}
	}

	private class ActionOpenImage implements ActionListener {
		@Override
		public synchronized void actionPerformed(ActionEvent event) {
			JFrame mainFrame = App.getMainFrame();
			int result = 0;
			if (fileChOneImage == null) {
				fileChOneImage = new JFileChooser();
				fileChOneImage.setFileFilter(new ImageAndFolderFilter());
				fileChOneImage.setCurrentDirectory(new File("."));
				result = fileChOneImage.showOpenDialog(mainFrame);
			} else {
				result = fileChOneImage.showOpenDialog(mainFrame);
			}
			if (fileChOneImage != null && result == JFileChooser.APPROVE_OPTION) {
				ReadImageFromDisk task = new ReadImageFromDisk(fileChOneImage.getSelectedFile());
				task.start();
				while (task.isAlive()) {
					try {
						wait(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				ImagePreview imagePreview = new ImagePreview(task.getImage(),
						fileChOneImage.getSelectedFile(), "--New--");
				App.getModel().addNewImage(task.getImage(), fileChOneImage.getSelectedFile());
				imagesPanel.clearOldImages();
				imagesPanel.addImagePreview(imagePreview);
				
				ImagesPanel.showImagePanel();
				imagesPanel.revalidate();
				App.getMainFrame().validate();
			}
			System.out.println(imagesPanel.getImagesCount());
			repaint();
		}
	}
}
