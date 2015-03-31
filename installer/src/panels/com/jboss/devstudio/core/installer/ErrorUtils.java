package com.jboss.devstudio.core.installer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class ErrorUtils {
	 
	public static void showError(JFrame owner, ImageIcon image, String title, String message, String text){
		if (owner == null || !owner.isVisible()) {
			System.out.println(text);
		} else {
			final JDialog frame = new JDialog(owner,true);
			frame.setTitle(title);
			frame.setLayout(new BorderLayout());
			//frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			
			JLabel label = new JLabel(message,image, SwingConstants.LEFT);
	
			label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			JTextArea area = new JTextArea(text);
			area.setEditable(false);
			area.setWrapStyleWord(false);
			area.setLineWrap(false);
			
			JScrollPane scroll = new JScrollPane(area);
			scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			JButton ok = new JButton("OK");
			ok.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					frame.dispose();
				}
			});
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			buttonPanel.add(Box.createGlue());
			buttonPanel.add(ok);
			
			frame.add(label, BorderLayout.NORTH);
			frame.add(scroll, BorderLayout.CENTER);
			frame.add(buttonPanel, BorderLayout.SOUTH);
			frame.pack();
			frame.setSize(new Dimension(350,200));
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
	}
}
