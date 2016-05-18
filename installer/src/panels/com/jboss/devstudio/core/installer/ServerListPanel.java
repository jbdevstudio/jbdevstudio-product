package com.jboss.devstudio.core.installer;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.izforge.izpack.LocaleDatabase;
import com.izforge.izpack.gui.EtchedLineBorder;
import com.izforge.izpack.installer.ResourceManager;
import com.jboss.devstudio.core.installer.bean.RuntimePath;
import com.jboss.devstudio.core.installer.bean.ServerListBean;

public class ServerListPanel extends JPanel {

	private static final long serialVersionUID = 1256443616359329176L;
	private static final Dimension TABLE_PREF_SIZE = new Dimension(550, 60);
	private static Pattern ELEMENT_NAME_DEFAULT_PATTERN = Pattern.compile("[a-zA-Z0-9_][ a-zA-Z0-9_\\.\\-\\(\\)]*");

	private JTable table;
	private JScrollPane scrollPane;
	private LocaleDatabase langpack = null;
	private ServerListActionsPanel buttonPanel;
	String installedASName = "JBoss Application Server 4.2";
	
	ServerListBean serverListBean = new ServerListBean();
	File storedFolder = new File("/home/eskimo/exadel-projects/devstudio-ganymede/plugins/devstudio-installer/src/test-resources/servers/jboss-4.0.5.GA");
	
	
	public ServerListPanel(LocaleDatabase langpack, ServerListBean serverListBean) {
		this(langpack);
		this.serverListBean = serverListBean;
	}
	
	public ServerListPanel(LocaleDatabase langpack) {
		
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
		
		table = new JTable(new ServerListTableModel(serverListBean, langpack)) {
			private boolean trackViewportWidth = false;
			private boolean inited = false;
			private boolean ignoreUpdates = false;

			@Override
			protected void initializeLocalVars() {
				super.initializeLocalVars();
				inited = true;
				updateColumnWidth();
			}

			@Override
			public void addNotify() {
				super.addNotify();
				updateColumnWidth();
				getParent().addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						invalidate();
					}
				});
			}

			@Override
			public void doLayout() {
				super.doLayout();
				if (!ignoreUpdates) {
					updateColumnWidth();
				}
				ignoreUpdates = false;
			}

			protected void updateColumnWidth() {
				if (getParent() != null) {
					int width = 0;
					for (int col = 0; col < getColumnCount(); col++) {
						int colWidth = 0;
						for (int row = 0; row < getRowCount(); row++) {
							int prefWidth = getCellRenderer(row, col)
									.getTableCellRendererComponent(this,
											getValueAt(row, col), false, false,
											row, col).getPreferredSize().width;
							colWidth = Math.max(colWidth, prefWidth
									+ getIntercellSpacing().width);
						}

						TableColumn tc = getColumnModel().getColumn(
								convertColumnIndexToModel(col));
						tc.setPreferredWidth(colWidth);
						width += colWidth;
					}

					Container parent = getParent();
					if (parent instanceof JViewport) {
						parent = parent.getParent();
					}

					trackViewportWidth = width < parent.getWidth();
				}
			}

			@Override
			public void tableChanged(TableModelEvent e) {
				super.tableChanged(e);
				if (inited) {
					updateColumnWidth();
				}
			}

			public boolean getScrollableTracksViewportWidth() {
				return trackViewportWidth;
			}

			@Override
			protected TableColumnModel createDefaultColumnModel() {
				TableColumnModel model = super.createDefaultColumnModel();
				model.addColumnModelListener(new TableColumnModelListener() {
					public void columnAdded(TableColumnModelEvent e) {
					}

					public void columnRemoved(TableColumnModelEvent e) {
					}

					public void columnMoved(TableColumnModelEvent e) {
						if (!ignoreUpdates) {
							ignoreUpdates = true;
							updateColumnWidth();
						}
					}

					public void columnMarginChanged(ChangeEvent e) {
						if (!ignoreUpdates) {
							ignoreUpdates = true;
							updateColumnWidth();
						}
					}

					public void columnSelectionChanged(ListSelectionEvent e) {
					}
				});
				return model;
			}
		};

		table.getColumnModel().getColumn(1).setPreferredWidth(440);
		table.getColumnModel().getColumn(1).setMinWidth(440);
		table.getColumnModel().getColumn(0).setPreferredWidth(110);
		table.getColumnModel().getColumn(0).setMinWidth(110);
		table.getColumnModel().getColumn(0).setMaxWidth(130);
		
		scrollPane = new JScrollPane(table, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setPreferredScrollableViewportSize(TABLE_PREF_SIZE);
		add(scrollPane, c);
		
		buttonPanel = new ServerListActionsPanel(new ServerListAction[] {
			new AddAction(),
			new EditAction(),
			new RemoveAction()});
		c.gridx = 1;
		c.weightx = c.weighty = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5,5,5,5);
		add(buttonPanel, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0; c.weighty = 1.0;
		c.gridheight = 1;
		c.gridwidth = 1;
		JPanel panel = new JPanel();
		add(panel,c);

	}
	
	public void setSelection(int[] selection) {
		table.getSelectionModel().setValueIsAdjusting(true);
		for (int i = 0; i < selection.length; i++) {
			table.getSelectionModel().addSelectionInterval(selection[i], selection[i]);
		}
		table.getSelectionModel().setValueIsAdjusting(false);
	}

	public void setTableEnabled(boolean flag){
		table.setEnabled(flag);
		table.setBackground(flag? Color.white :Color.lightGray);
		table.setGridColor(flag? Color.white :Color.lightGray);
		table.setRowSelectionAllowed(flag);
		scrollPane.getViewport().setBackground(flag? Color.white :Color.lightGray);
		Stack stack = new Stack();
		stack.push(Arrays.asList(flag?scrollPane.getComponents():getComponents()).iterator());
		while(!stack.isEmpty()) {
			Iterator iter = (Iterator)stack.peek(); 
			while(iter.hasNext()){
				Component comp = (Component)iter.next();
				comp.setEnabled(flag);
				if(comp instanceof JButton) {
					JButton bt = (JButton)comp;
					if(bt.getAction()!=null)
					bt.getAction().setEnabled(flag);
				} else if(comp instanceof Container) {
					Container cont = (Container)comp;
					if(cont.getComponents().length>0) {
						stack.push(Arrays.asList(cont.getComponents()).iterator());
						break;
					}
				}	
			}
			if(!iter.hasNext())
				stack.pop();	
		}
		if(flag) {
			validateButtons();
		}
	}
	
	public void validateButtons(){
		buttonPanel.valueChanged(null);
	}
	
	public boolean isValidated(){
		return true;
	}

	private JComboBox versionCombo = null;
	private JRadioButton type1=null; 
	private JRadioButton type2=null; 
	private JRadioButton type3=null;
	private JRadioButton type4=null;
	private JRadioButton type5=null;
	private JRadioButton type6=null;
	
	public static class ServerListTableModel extends AbstractTableModel implements PropertyChangeListener {
		ServerListBean serverList;
		LocaleDatabase langpack;
		
		public ServerListTableModel(ServerListBean list,LocaleDatabase langpack){
			this.serverList = list;
			this.langpack = langpack;
			list.addPropertyChangeListener(this);
		}

		public Class getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 1:
				return String.class;
			case 0:
				return Boolean.class;//langpack.getString("JBossAsSelectPanel.LocationColumn");\
			}
		throw new IllegalArgumentException();
		}

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
				case 1:
					return langpack.getString("JBossAsSelectPanel.LocationColumn");
				case 0:
					return "Scan every start";//langpack.getString("JBossAsSelectPanel.LocationColumn");\
			}
			throw new IllegalArgumentException();
		}

		public int getRowCount() {
			return serverList.getServers().size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 1:
					return serverList.getServers().get(rowIndex).getLocation();
				case 0:
					return serverList.getServers().get(rowIndex).isScannedOnStartup();
			}
			throw new IllegalArgumentException();
		}
		
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex==0;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(columnIndex==0) {
				 serverList.getServers().get(rowIndex).setScannedOnStartup(((Boolean)aValue).booleanValue());
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if(evt instanceof IndexedPropertyChangeEvent) {
				IndexedPropertyChangeEvent event = (IndexedPropertyChangeEvent)evt;
				fireTableRowsUpdated(event.getIndex(),event.getIndex());
			} else {
				fireTableDataChanged();
			}
		}
	}

	public List<RuntimePath> getServerList() {
		return serverListBean.getServers();
	}
	
	/**
	 * @author eskimo
	 *
	 */
	public class ServerPanel extends JPanel {

		private EditDialog editDialog;
		private static final int PREF_WIDTH = 450;
		JTextField locationField;
		JCheckBox scan;
		RuntimePath bean;
		RuntimePath originalBean;
		Validator validator = new Validator();
		
		public ServerPanel(EditDialog dialog, RuntimePath bean) {
			ImageIcon image = null;
			this.bean = bean;
			this.originalBean = new RuntimePath(bean);
			editDialog = dialog;
			ValidatorAdapter validatorAdapter =  new ValidatorAdapter();
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weighty = 1.0;
		    c.weightx = 1.0;
			c.fill = GridBagConstraints.HORIZONTAL;
			
			JPanel bodyPanel = this;
			bodyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			bodyPanel.setLayout(new GridBagLayout());
			
			JLabel locationLabel = new JLabel(langpack.getString("JBossAsSelectPanel.Location"));
			c = new GridBagConstraints();
		    c.fill = GridBagConstraints.HORIZONTAL;
		    c.insets = new Insets(5, 5, 5, 5);
		    c.gridx = 0;
		    c.gridy = 0;
		    c.gridwidth = 1;
		    c.gridheight = 1;
		    c.weightx = c.weighty = 0.0;
			bodyPanel.add(locationLabel,c);
			
			locationField = new JTextField(30);
			locationField.setText(bean.getLocation());
			
		    c.gridx = 1;
		    c.gridy = 0;
		    c.gridwidth = 1;
		    c.gridheight = 1;
		    c.weightx = 1.0;
		    c.weighty = 0.0;
			
		    bodyPanel.add(locationField,c);
			
			JButton locationButton = new JButton(langpack.getString("JBossAsSelectPanel.Browse"));
			locationButton.addActionListener(new BrowseAction());

		    c.gridx = 2;
		    c.gridy = 0;
		    c.gridwidth = 1;
		    c.gridheight = 1;
		    c.weightx = 0.0;
		    c.weighty = 0.0;
			bodyPanel.add(locationButton,c);

			c.gridx = 1;
		    c.gridy = 1;
		    c.gridwidth = 2;
		    c.gridheight = 1;
		    c.weightx = 0.0;
		    c.weighty = 0.0;
		    c.insets = new Insets(0, 0, 0, 0);
		    
			scan = new JCheckBox(langpack.getString("JBossAsSelectPanel.ScannEveryStartup"));
			scan.setSelected(bean.isScannedOnStartup());
			bodyPanel.add(scan,c);
			
			scan.addActionListener(validatorAdapter);
			scan.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ServerPanel.this.bean.setScannedOnStartup(scan.isSelected());
				}
			});
			
		    locationField.getDocument().addDocumentListener(validatorAdapter);
		    locationField.getDocument().addDocumentListener(new DocumentListener(){
				public void changedUpdate(DocumentEvent e) {
					ServerPanel.this.bean.setLocation(locationField.getText().trim());
				}
				public void insertUpdate(DocumentEvent e) {
					ServerPanel.this.bean.setLocation(locationField.getText().trim());
				}
				public void removeUpdate(DocumentEvent e) {
					ServerPanel.this.bean.setLocation(locationField.getText().trim());
				}});
		    validator.setDisabled(false);
			validateBean();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(PREF_WIDTH,super.getPreferredSize().height);
		}
		
		public void setEditDialog(EditDialog editDialog) {
			this.editDialog = editDialog;
		}
		
		public void loadDefaults(File selectedFile) {
			locationField.setText(selectedFile.getPath());
			validateBean();
		}
		
		public void validateBean() {
			if(!getValidator().isDisabled()) {
				ValidationResult result = validator.validate(originalBean,bean,serverListBean.getServers());
				String message = langpack.getString(result.getMessageKey());
				if(result.getSeverety() == ValidationResult.ERROR) {
					editDialog.setErrorMessage(message);
					editDialog.setComplete(false);
				} else if(result.getSeverety() == ValidationResult.WARNING) {
					editDialog.setWarningMessage(message);
					editDialog.setComplete(!originalBean.equals(bean));
				} else if(result.getSeverety() == ValidationResult.OK) {
					editDialog.setInfoMessage(message);
					editDialog.setComplete(!originalBean.equals(bean));
				}
			}
		}
		
		public void setValidator(Validator validator) {
			this.validator = validator;
		}
		
		public Validator getValidator() {
			return validator;
		}
		
		/**
		 * @author eskimo
		 */
		public class BrowseAction extends AbstractAction {
		

			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent event){
				JDialog parent = (JDialog)SwingUtilities.getAncestorOfClass(JDialog.class, ServerPanel.this);
				JFileChooser chooser = new JFileChooser(storedFolder);
				chooser.setDialogTitle(langpack.getString("JBossAsSelectPanel.FileChooserTitle"));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnValue = chooser.showDialog(parent, langpack.getString("JBossAsSelectPanel.FileChooserButton"));
				if(returnValue == JFileChooser.APPROVE_OPTION){
					File selectedFile = chooser.getSelectedFile();
					// this is workaround for mac issue with JFileChooser dialog https://jira.jboss.org/jira/browse/JBDS-1041
					// Assuming there is no way to select none existing folder in JFileChooser
					// It check selected folder and if it is not exist trying to get parent folder
					// which is right selected folder for mac
					if(!selectedFile.exists()) {
						selectedFile = selectedFile.getParentFile();
					}
					storedFolder = selectedFile.getParentFile();
					loadDefaults(selectedFile);
				}
			}
		}
		
		public class ValidatorAdapter implements DocumentListener,
				ActionListener {

			public void changedUpdate(DocumentEvent e) {
				if(!getValidator().isDisabled()) {
					validateBean();
				}
			}

			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			public void actionPerformed(ActionEvent e) {
				if(!getValidator().isDisabled()) {
					validateBean();
				}
			}
		}
	}
	
	public static class Validator {
		static final RuntimePath EMPTY_BEAN = new RuntimePath();
		static final public String ERR_LOCATION_DOESNT_EXIST = "ServerDialog_ErrLocationDoesntExist";
		static final public String ERR_LOCATION_IS_EMPTY = "ServerDialog_ErrLocationIsEmpty";
		static final public String ERR_NAME_IS_EMPTY = "ServerDialog_ErrNameIsEmpty";
		static final public String ERR_NAME_IS_NOT_CORRECT = "ServerDialog_ErrNameIsNotCorrect";
		static final public String ERR_SERVER_TYPE_IS_NOT_SELECTED = "ServerDialog_ErrServerTypeDoesntSelected";
		static final public String ERR_SERVER_VERSION_IS_NOT_SELECTED = "ServerDialog_ErrVersionIsNotSelected";
		static final public String ERR_SERVER_VERSION_IS_NOT_SUPPORTED = "ServerDialog_ErrVersionIsNotSupported";
		static final public String ERR_SERVER_NAME_IS_ALREADY_USED = "ServerDialog_ErrNameIsAlreadyUsed";
		static final public String OK_NEW_DEFAULT = "ServerDialog_DefaultNewServerMessage";
		static final public String OK_EDIT_DEFAULT = "ServerDialog_DefaultEditServerMessage";
		static final public String WARN_SERVER_UNKNOWN = "ServerDialog_WarnUnknownServerSelected";
		static final public String WARN_SERVER_TYPE_VERSION_DONT_MATCH = "ServerDialog_ServerTypeVersionDoesntMatch";
		static final public String WARN_SERVER_VERSION_DONT_MATCH = "ServerDialog_ServerVersionDoesntMatch";
		static final public String WARN_SERVER_TYPE_DONT_MATCH = "ServerDialog_ServerTypeDoesntMatch";
		static final public String NO_ERRORS = "";
		static final public String MSG_AS_SERVER_INSIDE_EAP_SELECTED = "ServerDialog_ASInsideEAPSelected";
		static final public String ERR_AS_SERVER_INSIDE_EAP_SELECTED = "ServerDialog_ErrASInsideEAPSelected";
		
		private boolean disabled = false;
		
		public ValidationResult validate(RuntimePath originalBean, RuntimePath bean, List<RuntimePath> servers) {
			if(!disabled)  {
				if(bean.equals(originalBean)) {
					if(bean.equals(EMPTY_BEAN)) {
						// Show default message when dialog is empty, which means:
						// location and name is empty, type and version are not seleted
						return createOkResult(OK_NEW_DEFAULT);
					} else {
						return createOkResult(OK_EDIT_DEFAULT);
					}
				} else {
					//------------------------
					// validate entered values
					// TODO: Add validation for links using File.getAbsolutePath()
					//------------------------
					File location = new File(bean.getLocation());
					if(EMPTY_BEAN.getLocation().equals(bean.getLocation().trim())) {
						// validate selected location is not empty
						return createErrorResult(ERR_LOCATION_IS_EMPTY);
					} else if (!location.exists() || !location.isDirectory()) {
						// validate selected location exists
						return createErrorResult(ERR_LOCATION_DOESNT_EXIST);
					} else if(isLocationInList(bean, servers)) {
						return createErrorResult(ERR_SERVER_NAME_IS_ALREADY_USED);
					}
				}
				if(originalBean.equals(EMPTY_BEAN)) {
					// New Server Dialog default message
					return createOkResult(OK_NEW_DEFAULT);
				} else {
					// Edit Server Dialog Default message
					return createOkResult(OK_EDIT_DEFAULT);
				}
			}
			return createOkResult(NO_ERRORS);
		}
		

		private static boolean isLocationInList(RuntimePath location, List<RuntimePath> servers) {
			for (RuntimePath serverBean : servers) {
				if(serverBean.getLocation().equals(location) && serverBean!=location) {
					return true;
				}
			}
			return false;
		}

		
		private static ValidationResult createErrorResult(String messageKey) {
			return new ValidationResult(ValidationResult.ERROR,messageKey);
		}
		
		private static ValidationResult createWarningResult(String messageKey) {
			return new ValidationResult(ValidationResult.WARNING,messageKey);
		}
		
		private static ValidationResult createOkResult(String messageKey) {
			return new ValidationResult(ValidationResult.OK,messageKey);
		}
		
		public boolean isDisabled() {
			return disabled;	
		}
		
		public void setDisabled(boolean set) {
			this.disabled = set;
		}
	}
	
	public static class ValidationResult {
		public static final int OK = 0;
		public static final int ERROR = 1;
		public static final int WARNING = 2;
		private int severety = ERROR;
		private String messageKey = null;
		
		public ValidationResult(int severety, String messageKey) {
			this.severety = severety;
			this.messageKey = messageKey;
		}

		public String getMessageKey() {
			return messageKey;
		}

		public int getSeverety() {
			return severety;
		}
	}
	
	public abstract class ServerListAction extends AbstractAction {
		
		private int[] selection;

		public ServerListAction() {
			super();
		}

		public ServerListAction(String name) {
			super(name);
		}
		
		public void setSelection(int[] selection) {
			this.selection = selection;
			selectionChanged();
		}
		
		public abstract void selectionChanged();

		public void setTarget(ServerListBean bean) {
			
		}
		
		public int[] getSelection() {
			return selection;
		}
	}
	
	
	public interface FindActionNotifier {
		public void serverAdded(RuntimePath bean);
	}
	
	public class AddAction extends ServerListAction {
		public AddAction() {
			super(langpack.getString("JBossAsSelectPanel.AddButton"));
		}
		public void actionPerformed(ActionEvent e) {
			JFrame parent = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, (Component)e.getSource());
			ImageIcon image = null;
			try{
				image = ResourceManager.getInstance().getImageIconResource("JBossAsSelectPanel.create.image");
			}catch(Exception ex){
			}
			EditDialog dialog = new EditDialog(parent, true,  image);
			dialog.setTitle(langpack.getString("JBossAsSelectPanel.AddDialogTitle"));
			RuntimePath bean = new RuntimePath();
			dialog.setContent(new ServerPanel(dialog, bean));
			
			dialog.setLocation(parent.getLocation().x+parent.getSize().width/2-dialog.getSize().width/2, parent.getLocation().y+parent.getSize().height/2-dialog.getSize().height/2);
			dialog.doLayout();
			dialog.pack();
			dialog.setResizable(false);
			dialog.setVisible(true);
			if(dialog.isOkPressed()) {
				List<RuntimePath> servers = serverListBean.getServers();
				servers.add(bean);
				serverListBean.setServers(servers);
			}
		}
		
		public void selectionChanged() {
			setEnabled(true);
		}
	}
	
	public class EditAction extends ServerListAction {
		public EditAction() {
			super(langpack.getString("JBossAsSelectPanel.EditButton"));
			setEnabled(false);
		}
		public void actionPerformed(ActionEvent e) {
			JFrame parent = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, (Component)e.getSource());
			ImageIcon image = null;
			try{
				image = ResourceManager.getInstance().getImageIconResource("JBossAsSelectPanel.edit.image");
			}catch(Exception ex){
			}
			EditDialog dialog = new EditDialog(parent, true,  image);
			dialog.setTitle(langpack.getString("JBossAsSelectPanel.EditDialogTitle"));
			dialog.setLocation(parent.getLocation().x+parent.getSize().width/2-dialog.getSize().width/2, parent.getLocation().y+parent.getSize().height/2-dialog.getSize().height/2);
			RuntimePath beanCopy = new RuntimePath();
			RuntimePath bean = serverListBean.getServers(getSelection()[0]);
			beanCopy.setLocation(bean.getLocation());
			beanCopy.setScannedOnStartup(bean.isScannedOnStartup());
			
			dialog.getContentPane().add(new ServerPanel(dialog, beanCopy));
			dialog.doLayout();
			dialog.pack();
			dialog.setResizable(false);
			dialog.setVisible(true);
			if(dialog.isOkPressed()) {
				serverListBean.setServers(getSelection()[0], beanCopy);
			}
		}
		
		public void selectionChanged() {
			setEnabled(getSelection().length==1);
		}
	}
	
	public class RemoveAction extends ServerListAction {
		public RemoveAction() {
			super(langpack.getString("JBossAsSelectPanel.RemoveButton"));
			setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent e) {
			List<RuntimePath> newList = new ArrayList<RuntimePath>(serverListBean.getServers());
			List<RuntimePath> removeList = new ArrayList<RuntimePath>();
 			for(int i = 0; i<getSelection().length;i++) {
 				removeList.add(newList.get(getSelection()[i]));
			}
 			newList.removeAll(removeList);
 			serverListBean.setServers(newList);
		}
		
		public void selectionChanged() {
			setEnabled(getSelection().length>0);
		}
	}
	
	public class ServerListActionsPanel extends JPanel implements ListSelectionListener{
		
		ServerListAction[] actions =  new ServerListAction[0];
		
		List<RuntimePath> beans = new ArrayList<RuntimePath>();
		
		public ServerListActionsPanel(ServerListAction[] actions) {
			GridLayout layout = new GridLayout();
			this.actions = actions;
			layout.setRows(actions.length);
			layout.setVgap(5);
			setLayout(layout);
			
			for (ServerListAction serverListAction : actions) {
				add(new JButton(serverListAction));
			}
			table.getSelectionModel().addListSelectionListener(this);
		}

		public void valueChanged(ListSelectionEvent e) {
			for (ServerListAction action : actions) {
				action.setSelection(table.getSelectedRows());
			}
		}
	}
	
	public class EditDialog extends JDialog {
		
		protected final Object OK = new Object();
		protected final Object CANCEL = new Object();
		
		ImageIcon image = null;
		JLabel titleLabel = null;
		JTextArea messageLabel = null;
		
		JButton okButton;
		
		boolean okPressed = false;
		
		public EditDialog(Frame owner, boolean modal, ImageIcon image)
				throws HeadlessException {
			this(owner,"",modal,image);
		}

		public EditDialog(Frame owner, String title, boolean modal, ImageIcon image)
				throws HeadlessException {
			super(owner, title, modal);
			this.image = image;
			
			//head panel
			JPanel headPanel = new JPanel();
			headPanel.setLayout(new GridBagLayout());
			headPanel.setBackground(Color.white);
			
			// title
			titleLabel = new JLabel(langpack.getString("JBossAsSelectPanel.EditDialogName"));
			titleLabel.setFont(new Font("default", Font.BOLD, 12));
			titleLabel.setBorder( BorderFactory.createEmptyBorder(7,10,3,10));
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weighty = 0.0;
		    c.weightx = 1.0;
			c.fill = GridBagConstraints.HORIZONTAL;
			headPanel.add(titleLabel,c);
			
			// message
			messageLabel = new JTextArea();
			messageLabel.setEditable(false);
			messageLabel.setWrapStyleWord(true);
			messageLabel.setLineWrap(true);
			messageLabel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
			messageLabel.setRows(2);
			messageLabel.setBackground(Color.WHITE);
			c.gridy = 1;
			JScrollPane scroll = new JScrollPane(messageLabel);
			scroll.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
			scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			scroll.setPreferredSize(messageLabel.getPreferredScrollableViewportSize());
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 1;
			c.weighty = 1.0;
		    c.weightx = 1.0;
			c.fill = GridBagConstraints.HORIZONTAL;
			headPanel.add(scroll,c);
			
			JLabel imageLabel = new JLabel(image);
			c.gridx = 2;
			c.gridy = 0;
			c.gridheight = 2;
			c.weighty = c.weightx = 0;
			headPanel.add(imageLabel,c);
			
			add(headPanel,BorderLayout.NORTH);
			
			// button panel
			JPanel buttonPanel = new JPanel();
			buttonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 8,
	                8, 8), BorderFactory.createTitledBorder(new EtchedLineBorder(), ""
	                		, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.CENTER, new Font(
	                        "Dialog", Font.PLAIN, 10))));
			buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.LINE_AXIS));
			
			buttonPanel.add(Box.createHorizontalGlue());
			
			okButton = new JButton(langpack.getString("JBossAsSelectPanel.Ok"));
			okButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					setVisible(false);
					okPressed = true;
				}
			});
			buttonPanel.add(okButton);
			
			buttonPanel.add(Box.createHorizontalStrut(5));
			
			JButton cancelButton = new JButton(langpack.getString("JBossAsSelectPanel.Cancel"));
			cancelButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
			cancelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					setVisible(false);
				}
			});
			buttonPanel.add(cancelButton);
			getContentPane().add(buttonPanel,BorderLayout.SOUTH);
			
			getRootPane().registerKeyboardAction(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				},
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			    JComponent.WHEN_IN_FOCUSED_WINDOW
			);

		}
		
		public void setContent(ServerPanel content) {
			add(content,BorderLayout.CENTER);
			content.setEditDialog(this);
		}
		
		public void setErrorMessage(String text) {
			 messageLabel.setText(text);
			 messageLabel.setForeground(Color.RED);
		}
		
		public void setComplete(boolean b) {
			 okButton.setEnabled(b);
		}

		public void setWarningMessage(String text) {
			 messageLabel.setText(text);
			 messageLabel.setForeground(Color.BLACK);
		}
		
		public void setInfoMessage(String text) {
			 messageLabel.setText(text);
			 messageLabel.setForeground(Color.BLACK);
		}
		
		public boolean isOkPressed() {
			return okPressed;
		}
	}
}