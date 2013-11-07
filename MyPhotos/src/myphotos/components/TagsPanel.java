package myphotos.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import myphotos.main.App;

public class TagsPanel extends JPanel {
	private JList<String> tagsList;
	private DefaultListModel<String> listModel;
	private JScrollPane scrollPane;
	private JButton newTagButton;
	private JButton delTagButton;
	private JButton editTagButton;
	private Dimension minSize = new Dimension(200, 300);
	private Dimension maxSize = new Dimension(200, 4000);
	private Dimension preferSize = new Dimension(200, 400);

	public TagsPanel() {
		super(new GridLayout(10, 1));
		setVisible(true);
		this.setLayout(new BorderLayout(0, 0));
		tagsList = createJList();
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(tagsList);
		add(scrollPane);

		JLabel lblNewLabel = new JLabel("Tags");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		this.add(lblNewLabel, BorderLayout.NORTH);
		newTagButton = new JButton("Add");
		newTagButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		newTagButton.addActionListener(new newTagButtonListener());
		delTagButton = new JButton("Delete");
		delTagButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		delTagButton.addActionListener(new delTagButtonListener());
		editTagButton = new JButton("Edit");
		editTagButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		editTagButton.addActionListener(new editTagButtonListener());
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(1, 0));
		buttonsPanel.add(newTagButton);
		buttonsPanel.add(delTagButton);
		buttonsPanel.add(editTagButton);
		this.add(buttonsPanel, BorderLayout.SOUTH);		
	}

	private JList<String> createJList() {
		listModel = new DefaultListModel<String>();
		LinkedList<String> tagList = App.getModel().getTagsList();
		tagList.addFirst("--All--");
		for (String tag : tagList) {
			listModel.addElement(tag);
		}
		
		JList<String> list = new JList<String>(listModel);
		list.setFont(new Font("Tahoma", Font.PLAIN, 16));
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(new ListSelection());
		return list;
	}
	@Override
	public Dimension getMinimumSize() {
		return minSize;
	}
	@Override
	public Dimension getMaximumSize() {
		return maxSize;
	}
	@Override
	public Dimension getPreferredSize() {
		return preferSize;
	}
	
	class ListSelection implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			System.out.println("You select something!");
			if (!arg0.getValueIsAdjusting()) {
				JList list = (JList) arg0.getSource();
				String tag = (String) list.getSelectedValue();
				System.out.println("Selected tag is - " + tag);
				App.getMainFrame().showImagesWithThatTag(tag);
				App.getMainFrame().repaint();
				App.getMainFrame().revalidate();
			}
		}
	}

	class newTagButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String input = JOptionPane.showInputDialog(App.getMainFrame(),
					"Enter new tag:");
			if (input != null) {
				if (App.getModel().addNewTag(input)) {
					listModel.addElement(input);
				} else {
					JOptionPane.showMessageDialog(App.getMainFrame(), "Can't add tag \"" + input + "\".");
				}
			}
		}
	}

	class delTagButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {			
			String tag = tagsList.getSelectedValue();
			if (tag != null) {
				listModel.removeElement(tagsList.getSelectedValue());
				App.getModel().deleteTag(tag);
			}
		}
	}
	
	class editTagButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String tag = tagsList.getSelectedValue();
			String input = JOptionPane.showInputDialog(App.getMainFrame(),
					"Enter new name for tag: \"" + tag + "\"");
			if (tag != null) {
				listModel.removeElement(tagsList.getSelectedValue());
				listModel.addElement(input);
				App.getModel().editTag(tag, input);
			}			
		}		
	} 
}
