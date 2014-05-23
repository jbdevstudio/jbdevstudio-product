package com.jboss.devstudio.core.installer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.util.Debug;
import com.izforge.izpack.util.FileExecutor;
import com.izforge.izpack.util.OsVersion;

// Referenced classes of package com.izforge.izpack.panels:
//            PathInputPanel, PathSelectionPanel



public class JREPathPanel extends PathInputPanel implements IChangeListener
{
	private static final String SYSPN_JAVA_VERSION        = "java.version";
	private static final String SYSPN_SUN_ARCH_DATA_MODEL = "sun.arch.data.model";
	public static final String DATA_MODEL_VAR = "DATA_MODEL";
	private static final long serialVersionUID = 1256443616359329172L;
    private static final String winTestFiles[];
    private static final String linTestFiles[];
    private String variableName;
    private static final String gnuVersion = "gij ";
    private static final String minVersion = "1.6.";
    private static final String maxVersion = "1.7.";
    
    private String detectedVersion;
    
    
    protected JRadioButton rb1,rb2;

    static 
    {
        winTestFiles = (new String[] {
                "bin" + File.separator + "javaw.exe",
                "jre" + File.separator + "bin" + File.separator + "javaw.exe"
            });
        linTestFiles = (new String[] {
                "bin" + File.separator + "java",
                "jre" + File.separator + "bin" + File.separator + "java"
            });
    }
    
    protected boolean pathIsValid()
    {
        if (existFiles == null) return true;
        for (int i = 0; i < existFiles.length; ++i)
        {
            File path = new File(pathSelectionPanel.getPath(), existFiles[i]).getAbsoluteFile();
            if (path.exists()) return true;
        }
        return false;
    }

    
	JPanel headPanel = new JPanel();
	private JRadioButton option1, option2;
	private ButtonGroup archGroup;
	private JLabel messageLabel;

    
    public JREPathPanel(InstallerFrame parent, InstallData idata)
    {
        //super(parent, idata);
    	super(parent, idata, new IzPanelLayout());
        // Set default values
        emptyTargetMsg = getI18nStringForClass("empty_target", "TargetPanel");
        warnMsg = getI18nStringForClass("warn", "TargetPanel");
         
        String introText = getI18nStringForClass("intro", "PathInputPanel");
        
        add(new JLabel(introText), NEXT_LINE);
        
        
        rb1 = new JRadioButton("Default Java VM", true);
        rb1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				clearJava();
			}
		});
        rb2 = new JRadioButton("Specific Java VM", false);
        add(rb1, NEXT_LINE);
        add(rb2, NEXT_LINE);
        
        ButtonGroup group = new ButtonGroup();
        group.add(rb1);
        group.add(rb2);

        pathSelectionPanel = new PathSelectionPanel(this, idata, "JREPathPanel.fileDialog.title");
        add(pathSelectionPanel, NEXT_LINE);
        pathSelectionPanel.setEnabled(false);
        rb1.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent e){
        		if(rb1.isSelected()){
        			pathSelectionPanel.setEnabled(false);
        		}else{
        			pathSelectionPanel.setEnabled(true);
        		}
        	}
        });
		headPanel.setLayout(new GridLayout(3,1));
		pathSelectionPanel.addChangeListener(this);
		 
		if("installer".equals(idata.getVariable("PACK_NAME"))) {
			JLabel label = new JLabel(parent.langpack.getString("JREPathPanel.dataModel.title"));
			
			headPanel.add(label);
			
			option1 = new JRadioButton(parent.langpack.getString("JREPathPanel.dataModel32.title"));
			option1.setSelected(true);
			option1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					JREPathPanel.this.idata.setVariable(DATA_MODEL_VAR, "32");
					if(OsVersion.IS_WINDOWS || OsVersion.IS_OSX) {
	        			messageLabel.setText("");
	            		messageLabel.setForeground(Color.black);
					}
				}
			});
			
			headPanel.add(option1);
			
			option2 = new JRadioButton(parent.langpack.getString("JREPathPanel.dataModel64.title"));
			option2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					JREPathPanel.this.idata.setVariable(DATA_MODEL_VAR, "64");
					if(OsVersion.IS_WINDOWS || OsVersion.IS_OSX) {
	        			messageLabel.setText(JREPathPanel.this.parent.langpack.getString("JREPathPanel.VPEdoesNotSupportJava64.title"));
	            		messageLabel.setForeground(Color.black);
					}

				}
			});
			
			headPanel.add(option2);
			
			archGroup = new ButtonGroup();
			archGroup.add(option1);
			archGroup.add(option2);
			
			if(!OsVersion.IS_OSX){
				option1.setEnabled(false);
				option2.setEnabled(false);
			}
		}
		add(headPanel, NEXT_LINE);
		
		messageLabel = new JLabel(""){
			public Dimension getPreferredSize() {
                return new Dimension(500, 50);
            }
            public Dimension getMinimumSize() {
                return new Dimension(500, 50);
            }
            public Dimension getMaximumSize() {
                return new Dimension(500, 50);
            }
		};
		messageLabel.setVerticalAlignment(TOP);
		messageLabel.setHorizontalAlignment(LEFT);
		add(messageLabel, NEXT_LINE);
		
        createLayoutBottom();
        getLayoutHelper().completeLayout();
    	// // //
        setMustExist(true);
        if(OsVersion.IS_WINDOWS) {
            setExistFiles(winTestFiles);
        } else if(OsVersion.IS_UNIX) {
            setExistFiles(linTestFiles);
        }
        setVariableName("JREPath");
    }

    public boolean isValidated()
    {
        if(idata.getVariable("PANEL_LAYOUT_TEST") != null)
            return true;
        
        if(rb1.isSelected()){
    		//idata.setVariable(getVariableName(), "");
    		//return (true);
            pathSelectionPanel.setPath((new File(idata.getVariable("JAVA_HOME"))).getPath());
        }
        
        String chosenPath = pathSelectionPanel.getPath();
        boolean ok = true;

        // We put a warning if the specified target is nameless
        if (chosenPath.length() == 0)
        {
        	emitError(parent.langpack.getString("installer.error"), parent.langpack
                    .getString(getI18nStringForClass("empty", "PathInputPanel")));
            return false;
        }
        
        File path = new File(chosenPath).getAbsoluteFile();
        chosenPath = path.toString();
        pathSelectionPanel.setPath(chosenPath);
        if (!path.exists())
        {
        	String message = parent.langpack
                    .getString(getI18nStringForClass("wrongPath.title", "JREPathPanel"),new String[]{path.getAbsolutePath()});
            emitError(parent.langpack.getString("installer.error"), message);
            return false;
        }
        if (!pathIsValid())
        {
            emitError(parent.langpack.getString("installer.error"), parent.langpack
                    .getString(getI18nStringForClass("notValid", "PathInputPanel")));
            return false;
        }else {
        	int status = verifyVersion(new Properties()); 
        	if (status == 0 || status == -2)
            {
        		if(rb1.isSelected()) {
            		idata.setVariable(getVariableName(), new File(idata.getVariable("JAVA_HOME")).getPath());
        		} else {
                	idata.setVariable(getVariableName(), pathSelectionPanel.getPath());
        		}
                return true;
            }
        	if(status == -1){
        		emitError(parent.langpack.getString("installer.error"), parent.langpack
        				.getString(getI18nStringForClass("badVersion2", "PathInputPanel")));
        	}
        	return (false);
        }
    }

    public void panelActivate()
    {
        super.panelActivate();
        String chosenPath = idata.getVariable(getVariableName());
        if (chosenPath == null || "".equals(chosenPath)) {
        	File javaHome = new File(idata.getVariable("JAVA_HOME"));
        	// This case is for starting installer with jdk/bin/java under any platform
        	if("jre".equals(javaHome.getName())) {
        		File parentFolder = javaHome.getParentFile();
        		File bin = new File(parentFolder,"bin");
        		String ext = OsVersion.IS_WINDOWS ? ".exe" : "";
        		File java = new File(bin,"java" + ext);
        		File javac = new File(bin,"javac" + ext);
        		if(javac.canRead() && java.canRead()) {
        			idata.setVariable("JAVA_HOME",parentFolder.getAbsolutePath());
        		}
        	} else if(OsVersion.IS_WINDOWS && javaHome.getName().matches("jre\\d")) {
        		// try to discover windows jdk
        		// c:\Program Files\Java\jdk${java_version}
        		Properties props = getJavaPlatformProperties(javaHome.getAbsolutePath(), new String[2]);
        		String javaVersion = (String) props.get(SYSPN_JAVA_VERSION);
        		if(javaVersion != null) {
            		File parentFolder = javaHome.getParentFile();
            		File jdkLocation = new File(parentFolder,"jdk" + javaVersion);
            		File bin = new File(jdkLocation,"bin");
            		File java = new File(bin,"java.exe");
            		File javac = new File(bin,"javac.exe");
            		if(java.canRead() && javac.canRead()) {
            			idata.setVariable("JAVA_HOME",jdkLocation.getAbsolutePath());
            		}
        		}
        	}
        }
        updateJava(true);
    }
    
    private void clearJava(){
    	idata.setVariable(getVariableName(), "");
		updateJava(true);
    }
    
    private int updateJava(boolean setPath){
        if(setPath){
        	String chosenPath="";
            chosenPath = idata.getVariable(getVariableName());
            if(chosenPath == null || "".equals(chosenPath))
            	chosenPath = new File(idata.getVariable("JAVA_HOME")).getPath();
        	pathSelectionPanel.setPath(chosenPath);
        }
        
        idata.setVariable(getVariableName(),pathSelectionPanel.getPath());
		Properties properties = new Properties();
		int status = verifyVersion(properties);

		if("installer".equals(idata.getVariable("PACK_NAME"))) {
			String dataArch = properties.getProperty(SYSPN_SUN_ARCH_DATA_MODEL);
			option1.setSelected("32".equals(dataArch));
			option2.setSelected("64".equals(dataArch));
			if(OsVersion.IS_OSX) {
				if("32".equals(dataArch)) {
					boolean b = isArchSupported("64");
					option1.setEnabled(b);
					option2.setEnabled(b);
				}else {
					boolean b = isArchSupported("32");
					option1.setEnabled(b);
					option2.setEnabled(b);
				}
			}
		}
		return status;
    }
    
	@Override
	public void panelDeactivate() {
		super.panelDeactivate();
		String installPath = idata.getVariable("INSTALL_PATH");
		idata.setVariable("NORMALIZED_INSTALL_PATH", installPath.replace('\\', '/'));
	}
	
    public String getVariableName()
    {
        return variableName;
    }

    public void setVariableName(String string)
    {
        variableName = string;
    }

    public String getSummaryBody()
    {
    	if(rb1.isSelected())
    		return "default";
    	else
    		return idata.getVariable(getVariableName());
    }

    private boolean isGnuVersion(String[] output)
    {
        // "My" VM writes the version on stderr :-(
        String vs = (output[0].length() > 0) ? output[0] : output[1];
        return vs.indexOf(gnuVersion) >= 0;
    }
    
    private int verifyVersion(Properties props)
    {
    	String[] output = new String[2];
        
    	Properties jvmInfo = getJavaPlatformProperties(output);
        props.putAll(jvmInfo);
        detectedVersion = jvmInfo.getProperty(SYSPN_JAVA_VERSION);
        if(isGnuVersion(output)) return -1;
        if(detectedVersion.indexOf(minVersion) < 0 && detectedVersion.indexOf(maxVersion) < 0) return -2;
        return 0;
    }

	private int verifyArchSupported(Properties props, String arch) {
		String[] output = new String[2];
		Properties jvmInfo = getJavaPlatformProperties(output);
		props.putAll(jvmInfo);
		detectedVersion = jvmInfo.getProperty(SYSPN_JAVA_VERSION);
		if (isGnuVersion(output))
			return -1;
		if (detectedVersion.indexOf(minVersion) < 0
				&& detectedVersion.indexOf(maxVersion) < 0)
			return -2;
		return 0;
	}

	public Properties getJavaPlatformProperties(String[] output) {
		return getJavaPlatformProperties(pathSelectionPanel.getPath(), output);
	}

	public Properties getJavaPlatformProperties(String location, String[] output) {
		String jarPath = P2DirectorStarterListener.findPathJar(JREPathPanel.class);
		Debug.trace(jarPath);
        
     	String[] params = {
                location + File.separator + "bin" + File.separator + "java",
                "-Djava.awt.headless=true",
                "-showversion",
                "-classpath",
                jarPath + File.pathSeparator + System.getProperty("java.class.path"),
                JREPathPanel.class.getName()
                };

        FileExecutor fe = new FileExecutor();
        Debug.trace(params[0] + " " + params[1]);
        fe.executeCommand(params, output);
        Debug.trace(output[0]);
        Properties jvmInfo = new Properties();
        try {
			jvmInfo.load(new StringInputStream(output[0]));
		} catch (IOException e) {
			jvmInfo = new Properties();
		}
		return jvmInfo;
	}

	public boolean isArchSupported(String arch) {
		String[] output = new String[2];
		String jarPath = P2DirectorStarterListener
				.findPathJar(JREPathPanel.class);
		Debug.trace(jarPath);

		String[] params = {
				pathSelectionPanel.getPath() + File.separator + "bin"
						+ File.separator + "java",
				"-Djava.awt.headless=true",
				"-showversion",
				"-d" + arch,
				"-classpath",
				jarPath + File.pathSeparator
						+ System.getProperty("java.class.path"),
				JREPathPanel.class.getName() };

		FileExecutor fe = new FileExecutor();
		Debug.trace(params[0] + " " + params[1]);
		fe.executeCommand(params, output);
		Debug.trace(output[0]);
		Properties jvmInfo = new Properties();
		try {
			jvmInfo.load(new StringInputStream(output[0]));
			return arch.equals(jvmInfo.get(SYSPN_SUN_ARCH_DATA_MODEL));
		} catch (IOException e) {
			jvmInfo = new Properties();
			return false;
		}
	}
	
    public static void main(String[] args) {
    	final String pattern = "{0} = {1}";
		System.out.println(MessageFormat.format(pattern, SYSPN_JAVA_VERSION, System.getProperties().get(SYSPN_JAVA_VERSION)));			
		System.out.println(MessageFormat.format(pattern, SYSPN_SUN_ARCH_DATA_MODEL, System.getProperties().get(SYSPN_SUN_ARCH_DATA_MODEL)));
	}
    
    /**
     * Asks to make the XML panel data.
     *
     * @param panelRoot The tree to put the data in.
     */
    public void makeXMLData(IXMLElement panelRoot) { 
    }
    
    static public class StringInputStream extends InputStream {
    	StringReader reader;
    	
		public StringInputStream(String source) {
			super();
			reader = new StringReader(source);
		}
		@Override
		public int read() throws IOException {
			return reader.read();
		}	
    }
    
    public void change(){
    	pathSelectionPanel.removeChangeListener();
    	messageLabel.setText("");
    	if(!pathExists()) {
    		messageLabel.setText(parent.langpack.getString(getI18nStringForClass("wrongPath.title", "JREPathPanel"),new String[]{pathSelectionPanel.getPath()}));
    		messageLabel.setForeground(Color.red);
    	} else if(pathIsValid()){
    		int status = updateJava(false);
    		if (status == 0 && (OsVersion.IS_WINDOWS || OsVersion.IS_OSX) && option2.isSelected()) {
        			messageLabel.setText(parent.langpack.getString("JREPathPanel.VPEdoesNotSupportJava64.title"));
            		messageLabel.setForeground(Color.black);
       		}
        	if(status == -2){
        		messageLabel.setText("<html><p>This JVM (version "+detectedVersion+") was not tested with JBoss Developer Studio.<br>It is not guaranteed to work.</p></html>");
			messageLabel.setForeground(Color.black);
        	}
        	if(status == -1){
        		messageLabel.setText(parent.langpack.getString(getI18nStringForClass("badVersion2", "PathInputPanel")));
        		messageLabel.setForeground(Color.red);
        	}
    		// Verify if selected runtime is jre and show warning
    		File javacLocation = new File(idata.getVariable(getVariableName()),"bin/javac" + (OsVersion.IS_WINDOWS? ".exe":""));
    		if(!javacLocation.canRead()) {
    			messageLabel.setText("<html><p>Chosen Java VM is a Java Runtime only, it will run Developer Studio but it is recommended to use a Java SDK.</p></html>");
    			messageLabel.setForeground(Color.black);
    		}
    	} else {
    		messageLabel.setText(parent.langpack.getString(getI18nStringForClass("notValid", "PathInputPanel")));
    		messageLabel.setForeground(Color.red);
    	}
    	pathSelectionPanel.addChangeListener(this);
    }

	private boolean pathExists() {
		// TODO Auto-generated method stub
		return new File(pathSelectionPanel.getPath()).canRead();
	}
}
