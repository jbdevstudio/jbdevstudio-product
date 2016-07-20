/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.jboss.devstudio.core.installer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.izforge.izpack.LocaleDatabase;
import com.jboss.devstudio.core.installer.ServerListPanel.ServerListAction;
import com.jboss.devstudio.core.installer.bean.RuntimePath;
import com.jboss.devstudio.core.installer.bean.RuntimeServer;
import com.jboss.devstudio.core.installer.bean.RuntimeServerListBean;

public class RuntimeServerListPanel extends JPanel {
	
	private static final long serialVersionUID = 1256443616359329176L;
	private static final Dimension TABLE_PREF_SIZE = new Dimension(550, 60);

	private JTable table;
	private JScrollPane scrollPane;
	private LocaleDatabase langpack = null;
	private RuntimeServerListActionsPanel buttonPanel;
	
	RuntimeServerListBean listBean; 
	
	public RuntimeServerListPanel(LocaleDatabase langpack, List<RuntimeServer> defaultRTs, List<RuntimeServer> additionalRTs) {
		this.langpack = langpack;
		this.listBean = new RuntimeServerListBean(defaultRTs, additionalRTs);
		// get a bean with one indexed property the same way as
		// server list works
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		this.langpack = langpack;

		table = new JTable(new RuntimeServerListTableModel());

		table.getColumnModel().getColumn(1).setPreferredWidth(440);
		table.getColumnModel().getColumn(1).setMinWidth(440);
		table.getColumnModel().getColumn(0).setPreferredWidth(110);
		table.getColumnModel().getColumn(0).setMinWidth(110);
		table.getColumnModel().getColumn(0).setMaxWidth(130);

		scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setPreferredScrollableViewportSize(TABLE_PREF_SIZE);
		add(scrollPane, c);

		buttonPanel = new RuntimeServerListActionsPanel(new AbstractAction[] { new SelectAllAction(), new DeselectAllAction()});
		c.gridx = 1;
		c.weightx = c.weighty = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 5, 5, 5);
		add(buttonPanel, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 1.0;
		c.gridheight = 1;
		c.gridwidth = 1;
		JPanel panel = new JPanel();
		add(panel, c);
	}
	
	public class RuntimeServerListActionsPanel extends JPanel {
		
		AbstractAction[] actions =  new ServerListAction[0];
		
		List<RuntimePath> beans = new ArrayList<RuntimePath>();
		
		public RuntimeServerListActionsPanel(AbstractAction[] actions) {
			GridLayout layout = new GridLayout();
			this.actions = actions;
			layout.setRows(actions.length);
			layout.setVgap(5);
			setLayout(layout);
			
			for (AbstractAction serverListAction : actions) {
				add(new JButton(serverListAction));
			}
		}
	}
	
	public class SelectAllAction extends AbstractAction {
		
		SelectAllAction() {
			super("Select All");
		}
		public void actionPerformed(ActionEvent e) {
			selectAll();
		}
	}
	
	public class DeselectAllAction extends AbstractAction {
		
		DeselectAllAction() {
			super("Deselect All");
		}
		public void actionPerformed(ActionEvent e) {
			deselectAll();
		}
	}
	
	public class RuntimeServerListTableModel extends AbstractTableModel {

		public Class getColumnClass(int columnIndex) {
			switch (columnIndex) {
				case 1:
					return String.class;
				case 0:
					return Boolean.class;
			}
			throw new IllegalArgumentException(MessageFormat.format("Wrong column index {0}",columnIndex));
		}

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
				case 1:
					return langpack.getString("JBossAsSelectPanel.LocationColumn");
				case 0:
					return "Install";
			}
			throw new IllegalArgumentException();
		}

		public int getRowCount() {
			return listBean.getAdditionalRTs().size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 1:
					return listBean.getAdditionalRTs().get(rowIndex).getLabel();
				case 0:
					return listBean.getAdditionalRTs().get(rowIndex).isSelected();
			}
			throw new IllegalArgumentException();
		}
		
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex==0;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(columnIndex==0) {
				listBean.getAdditionalRTs().get(rowIndex).setSelected(((Boolean)aValue));
			}
		}
	}

	public RuntimeServerListBean getRTListBean() {
		return listBean;
	}
	
	public void deselectAll() {
		for (int i=0; i< table.getRowCount(); i++) {
			deselectRT(i);
		}
		table.updateUI();	
	}
	
	public void selectAll() {
		for (int i=0; i< table.getRowCount(); i++) {
			selectRT(i);
		}
		table.updateUI();		
	}

	public void selectRT(int index) {
		table.setValueAt(Boolean.TRUE, index, 0);
	}
	
	public void deselectRT(int index) {
		table.setValueAt(Boolean.FALSE, index, 0);
	}

}
