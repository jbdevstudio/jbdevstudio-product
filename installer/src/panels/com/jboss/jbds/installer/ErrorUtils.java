package com.jboss.jbds.installer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import sun.swing.DefaultLookup;

public class ErrorUtils {
	 
	public static void showError(JFrame owner, String title, String message, String text){
		final JDialog frame = new JDialog(owner,true);
		frame.setTitle(title);
		frame.setLayout(new BorderLayout());
		//frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		JLabel label = new JLabel(message, SwingConstants.LEFT);
		Icon icon = (Icon)DefaultLookup.get(label, label.getUI(), "OptionPane.errorIcon");
		label.setIcon(icon);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JTextArea area = new JTextArea(text);
		area.setEditable(false);
		area.setWrapStyleWord(false);
		area.setLineWrap(false);
		
		JScrollPane scroll = new JScrollPane(area);
		scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JButton ok = new JButton("Ok");
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
