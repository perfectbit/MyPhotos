package myphotos.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import myphotos.io.SaveImageOnDisk;
import myphotos.main.App;

public class ImagePreview extends JPanel {

	private final int IMAGE_SIZE = 200;
	private final int PANEL_SIZE = 255;
	private int BORDER_WIDTH = 3;	
	private int panelWidth = IMAGE_SIZE;
	private int panelHeight = PANEL_SIZE;

	private File imgFile;
	private boolean borderFlag = false;
	private JustImage imagePanel;
	private JLabel label;
	private JComboBox<String> listOfTags;
	private BufferedImage image = null;
	private RoundRectangle2D border;
	private Color color = new Color(0, 184, 245);

	public ImagePreview(BufferedImage newImage, File file, String tag) {
		super();
		setVisible(true);
		setFocusable(true);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(panelWidth, panelHeight));
		setMaximumSize(new Dimension(panelWidth, panelHeight));
		setMinimumSize(new Dimension(panelWidth, panelHeight));

		imgFile = file;
		image = newImage;
		imagePanel = new JustImage(image);
		imagePanel.addMouseListener(new PreviewMouseListener());
		add(imagePanel, BorderLayout.CENTER);

		JPanel panel = new JPanel(new GridLayout(2, 1));
		label = new JLabel(imgFile.getName());
		panel.add(label);
		listOfTags = createJComboBox(tag);

		panel.add(listOfTags);
		add(panel, BorderLayout.SOUTH);
	}

	private JComboBox<String> createJComboBox(String tag) {
		String[] A = App.getModel().getTagsArray();
		if (A == null) {
			return new JComboBox<String>();
		}
		ComboBoxModel<String> comboModel = new DefaultComboBoxModel<String>(A);
		JComboBox<String> combo = new JComboBox<String>(comboModel);
		combo.setFont(new Font("Tahoma", Font.PLAIN, 16));
		combo.setSelectedItem(tag);
		combo.addActionListener(new ComboListener());
		return combo;
	}

	public void setImage(BufferedImage newImage) {
		image = newImage;
		repaint();
		setSize(PANEL_SIZE, PANEL_SIZE);
		setVisible(true);
		setFocusable(true);
		repaint();
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(panelWidth, panelHeight);
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(panelWidth, panelHeight);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(panelWidth, panelHeight);
	}

	public File getImgFile() {
		return imgFile;
	}

	public void setImgFile(File imgFile) {
		this.imgFile = imgFile;
	}

	public void setFlag(boolean flag) {
		this.borderFlag = flag;
	}

	class ComboListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			JComboBox combo = (JComboBox) arg0.getSource();
			String newTag = (String) combo.getSelectedItem();
			App.getModel().setTagForImage(imgFile, newTag);
		}
	}

	class PreviewMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (arg0.getClickCount() >= 2) {
				ImagesPanel.showImage(imgFile);
			}
			if (arg0.getClickCount() == 1) {
				ImagesPanel.uncheckPreviews();
				if (!borderFlag) {
					borderFlag = true;
				} else {
					borderFlag = false;
				}
				App.getMainFrame().repaint();
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
		private int width;
		private int height;
		private int x;
		private int y;

		JustImage(BufferedImage img) {
			if (img.getWidth() > img.getHeight()) {
				width = IMAGE_SIZE;
				height = IMAGE_SIZE * img.getHeight() / img.getWidth();
			} else {
				width = IMAGE_SIZE * img.getWidth() / img.getHeight();
				height = IMAGE_SIZE;
			}
			x = (IMAGE_SIZE - width) / 2;
			y = (IMAGE_SIZE - height) / 2;

			if (x > y) {
				border = new RoundRectangle2D.Double(x, y + 1, width,
						height - 3, 0, 0);
			} else {
				border = new RoundRectangle2D.Double(x + 1, y, width - 3,
						height, 0, 0);
			}
		}

		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.drawImage(image, x, y, width, height, null);

			if (borderFlag) {
				g2d.setStroke(new BasicStroke(BORDER_WIDTH));
				g2d.setColor(color);
				g2d.draw(border);
			}
		}
	}

}