package myphotos.components;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import myphotos.io.ReadImageFromDisk;
import myphotos.main.App;
import myphotos.main.DBProvider;

public class ImagesPanel extends JPanel {	
	private static ImagesPanel thisPanel = null;
	private String IMAGE_VIEW = "image view";	
	private String PANEL_WITH_IMAGES = "panel with images";

	private LinkedList<ImagePreview> imagesPreviews;
	private ImageView imageView;
	private JPanel panelWithImages;
	private JScrollPane scrollPane;
	
	public static void showImage(File file) {
		CardLayout c1;
		if (thisPanel != null) {
			thisPanel.imageView.setImage(file);
			thisPanel.changePreferredSize();
			c1 = (CardLayout) thisPanel.getLayout();
			c1.show(thisPanel, thisPanel.IMAGE_VIEW);			
					
		}
	}
	
	public void changePreferredSize() {		
		int frameWidth = this.getWidth();
		int count = imagesPreviews.size();
		int compInLine;
		if (frameWidth != 0) { 
			compInLine = frameWidth/200;
		} else {
			compInLine = 3;
		}
		
		if (count > compInLine) {
			int k = 260*((count/compInLine)+1);			
			panelWithImages.setPreferredSize(new Dimension(frameWidth, k));
			panelWithImages.setMaximumSize(new Dimension(frameWidth, k));
			panelWithImages.setSize(frameWidth, 800);
		} else {			
			panelWithImages.setPreferredSize(new Dimension(frameWidth, 800));
			panelWithImages.setMaximumSize(new Dimension(frameWidth, 800));
			panelWithImages.setSize(frameWidth, 800);
		}
		this.revalidate();		
	}
	
	public static void showImagePanel() {
		CardLayout c1;
		if (thisPanel != null) {
			c1 = (CardLayout) thisPanel.getLayout();
			c1.show(thisPanel, thisPanel.PANEL_WITH_IMAGES);
			thisPanel.changePreferredSize();
		}
	}
	
	public static void deselectPreviews() {
		if (thisPanel != null) {
			for(ImagePreview img : thisPanel.imagesPreviews) {
				img.setFlag(false);
			}
		}
	}

	public ImagesPanel() {
		super();
		setVisible(true);
		setFocusable(true);
		setLayout(new FlowLayout(FlowLayout.LEFT));		
		setAutoscrolls(true);
		imagesPreviews = new LinkedList<ImagePreview>();
		
		this.setLayout(new CardLayout());
		
		imageView = new ImageView();
		add(IMAGE_VIEW, imageView);
		
		panelWithImages = new JPanel(new FlowLayout(FlowLayout.LEFT));		
		panelWithImages.setPreferredSize(new Dimension(310, 310));
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportView(panelWithImages);		
		add(PANEL_WITH_IMAGES, scrollPane);
		
		thisPanel = this;
		createPreviews();
		
		CardLayout c1;
		
		c1 = (CardLayout) thisPanel.getLayout();
		c1.show(thisPanel, thisPanel.PANEL_WITH_IMAGES);		
	}
	
	private void createPreviews() {
		LinkedList<ImagePreview> imgPreviewsList = App.getModel().createPreviews();
		for (ImagePreview preview : imgPreviewsList) {
			addImagePreview(preview);
		}
	}
	
	public void addImagePreview(ImagePreview imgPreview) {
		imagesPreviews.add(imgPreview);
		panelWithImages.add(imgPreview);
	}
	
	public void clearOldImages() {
		imagesPreviews.clear();
		panelWithImages.removeAll();
	}

	public int getImagesCount() {
		return imagesPreviews.size();
	}

	public void showImagesWithThatTag(String tag) {
		if (tag==null) {
			clearOldImages();
			return;
		}
		clearOldImages();
		LinkedList<ImagePreview> imgPreviewsList = null;
		if (tag.equals("--All--")) {
			imgPreviewsList = App.getModel().createPreviews();
		} else {
			imgPreviewsList = App.getModel().createPreviews(tag);
		}
		for (ImagePreview preview : imgPreviewsList) {
			addImagePreview(preview);
		}
	}	
}