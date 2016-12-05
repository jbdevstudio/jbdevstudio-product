package com.jboss.devstudio.core.installer;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.izforge.izpack.LocaleDatabase;
import com.jboss.devstudio.core.installer.ServerListPanel.ServerListAction;
import com.jboss.devstudio.core.installer.bean.P2IU;
import com.jboss.devstudio.core.installer.bean.P2IUListBean;
import com.jboss.devstudio.core.installer.bean.RuntimePath;
import com.jboss.devstudio.core.installer.bean.ServerListBean;

public class P2IUListPanel extends JPanel {
	
	private static final long serialVersionUID = 1256443616359329176L;
	private static final Dimension TABLE_PREF_SIZE = new Dimension(550, 65);

	private JTable table;
	private JScrollPane scrollPane;
	private LocaleDatabase langpack = null;
	private P2IUListActionsPanel buttonPanel;
	
	P2IUListBean listBean; 
	
	public P2IUListPanel(LocaleDatabase langpack, List<P2IU> defaultIUs, List<P2IU> additionalIUs) {
		this.langpack = langpack;
		this.listBean = new P2IUListBean(defaultIUs,additionalIUs);
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

		table = new JTable(new P2IUListTableModel());
		table.setRowSelectionAllowed(false);
		table.getColumnModel().getColumn(1).setPreferredWidth(440);
		table.getColumnModel().getColumn(1).setMinWidth(440);
		table.getColumnModel().getColumn(0).setPreferredWidth(110);
		table.getColumnModel().getColumn(0).setMinWidth(110);
		table.getColumnModel().getColumn(0).setMaxWidth(130);

		scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setPreferredScrollableViewportSize(TABLE_PREF_SIZE);
		add(scrollPane, c);

		buttonPanel = new P2IUListActionsPanel(new AbstractAction[] { new SelectAllAction(), new DeselectAllAction()});
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
	
	public class P2IUListActionsPanel extends JPanel {
		
		AbstractAction[] actions =  new ServerListAction[0];
		
		List<RuntimePath> beans = new ArrayList<RuntimePath>();
		
		public P2IUListActionsPanel(AbstractAction[] actions) {
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
	
	public class P2IUListTableModel extends AbstractTableModel {

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
					return langpack.getString("JBossAsSelectPanel.Feature");
				case 0:
					return "Install";
			}
			throw new IllegalArgumentException();
		}

		public int getRowCount() {
			return listBean.getAdditionalIUs().size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 1:
					return listBean.getAdditionalIUs().get(rowIndex).getLabel();
				case 0:
					return listBean.getAdditionalIUs().get(rowIndex).isSelected();
			}
			throw new IllegalArgumentException();
		}
		
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex==0;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(columnIndex==0) {
				listBean.getAdditionalIUs().get(rowIndex).setSelected(((Boolean)aValue));
			}
		}
	}

	public P2IUListBean getIUListBean() {
		return listBean;
	}
	
	public void deselectAll() {
		for (int i=0; i< table.getRowCount(); i++) {
			deselectIU(i);
		}
		table.updateUI();	
	}
	
	public void selectAll() {
		for (int i=0; i< table.getRowCount(); i++) {
			selectIU(i);
		}
		table.updateUI();		
	}

	public void selectIU(int index) {
		table.setValueAt(Boolean.TRUE, index, 0);
	}
	
	public void deselectIU(int index) {
		table.setValueAt(Boolean.FALSE, index, 0);
	}

}
