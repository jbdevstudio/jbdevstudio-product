package com.jboss.devstudio.core.installer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.installer.PanelAutomation;
import com.jboss.devstudio.core.installer.bean.RuntimePath;


public class JBossAsSelectPanel extends IzPanel  {
	
	private static final long serialVersionUID = 1256443616359329176L;

	Pattern ELEMENT_NAME_DEFAULT_PATTERN = Pattern.compile("[a-zA-Z_][ a-zA-Z0-9_\\.\\-]*");

	ServerListPanel serverList;
	private JRadioButton option1, option2;
	
	String installedASName = "JBoss Application Server 4.2";
	
	public JBossAsSelectPanel(InstallerFrame parent, InstallData idata, java.awt.LayoutManager2 lm) {
    	super(parent, idata, lm);
    }
	
	public JBossAsSelectPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata, new IzPanelLayout());
		
		JPanel headPanel = new JPanel();
		
		String installGroup = JBossAsSelectPanel.this.idata.getVariable("INSTALL_GROUP");
		if(!"devstudio".equals(installGroup)) {
			headPanel.setLayout(new GridLayout(3,1));
			JLabel label = new JLabel(parent.langpack.getString("JBossAsSelectPanel.question"));
			
			headPanel.add(label);
			
			option1 = new JRadioButton(parent.langpack.getString("JBossAsSelectPanel.YesOption"));
			option1.setSelected(true);
			option1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					JBossAsSelectPanel.this.idata.setVariable("INSTALL_GROUP", "jbosseap");
				}
			});
			
			headPanel.add(option1);
			
			option2 = new JRadioButton(parent.langpack.getString("JBossAsSelectPanel.NoOption"));
			option2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					JBossAsSelectPanel.this.idata.setVariable("INSTALL_GROUP", "devstudio");
				}
			});
			
			headPanel.add(option2);
			
			ButtonGroup group = new ButtonGroup();
		    group.add(option1);
		    group.add(option2);
			add(headPanel, NEXT_LINE);
		}

		add(Box.createVerticalStrut(10));
		JLabel label = new JLabel(parent.langpack.getString("JBossAsSelectPanel.question1"));
		add(label, NEXT_LINE);
		serverList = new ServerListPanel(parent.langpack);
		add(serverList, NEXT_LINE);

	}
	
	public void panelDeactivate(){
		StringBuffer server = new StringBuffer();
		Properties servers = new Properties();
		for(int i = 0; i < serverList.getServerList().size(); i++){
			RuntimePath data = serverList.getServerList().get(i);
				servers.put("server" + (i+1),data.toString());
		}
		File runtimesFolder = new File (JBossAsSelectPanel.this.idata.getVariable("INSTALL_PATH"),"runtimes");
		RuntimePath runtimesPath = new RuntimePath(runtimesFolder.getAbsolutePath(),true);
		servers.put("server" + (serverList.getServerList().size()+1), runtimesPath.toString());
		JBossAsSelectPanel.this.idata.setAttribute("AS_SERVERS",servers);
		super.panelDeactivate();
	}
	
	public void validateNextButton(){
		boolean flag = false;
		
		if(option1.isSelected()){
			flag = true;
		}else{
			if(serverList.getServerList().size() > 0)
				flag = true;
		}
		if(flag)
			parent.unlockNextButton();
		else
			parent.lockNextButton();
	}
		
	public boolean isValidated(){
		return true;
	}
	
	@Override
	public void makeXMLData(IXMLElement panelRoot) {
		PanelAutomation helper = new JBossAsSelectPanelAutomationHelper();
		helper.makeXMLData(this.idata, panelRoot);
	}
}
