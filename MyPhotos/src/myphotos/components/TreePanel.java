package myphotos.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import myphotos.main.MainFrame;

public class TreePanel extends JPanel {

	private FileViewer fileViewer;
	private JScrollPane scrollPane;

	public TreePanel(MainFrame frame) {
		super();
		fileViewer = new FileViewer(frame);
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(fileViewer);
		add(scrollPane);
		JLabel lblNewLabel_1 = new JLabel("Files");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		this.add(lblNewLabel_1, BorderLayout.NORTH);
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(150, 100);
	};
	
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(150, 4000);
	};

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(150, 400);
	};

}