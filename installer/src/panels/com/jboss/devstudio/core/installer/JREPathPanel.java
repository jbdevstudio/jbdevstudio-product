package com.jboss.devstudio.core.installer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.util.Debug;
import com.izforge.izpack.util.FileExecutor;
import com.izforge.izpack.util.OsVersion;
import com.jboss.devstudio.core.installer.bean.Java;

// Referenced classes of package com.izforge.izpack.panels:
//            PathInputPanel, PathSelectionPanel

public class JREPathPanel extends PathInputPanel implements IChangeListener {

	private static final String VPE_NOT_SUPPORTED_ARCH = "64";

	static final String VAR_JAVA_HOME = "JAVA_HOME";
	static final String VAR_SELECTED_JAVA_PATH = "JREPath"; 

	private static final String JAVA_APPLET_PLUGIN = "JavaAppletPlugin.plugin";

	private static final long serialVersionUID = 1256443616359329172L;

	public static final String DATA_MODEL_VAR = "DATA_MODEL";

	private static final String SYSPN_JAVA_VENDOR = "java.vendor";
	static final String SYSPN_JAVA_VERSION = "java.version";
	private static final String SYSPN_SUN_ARCH_DATA_MODEL = "sun.arch.data.model";

	private static final String winTestFiles[];
	private static final String linTestFiles[];
	private static final String gnuVersion = "gij ";
	private static final int minVersion = 8;
	private static final int maxVersion = 8;

	static {
		winTestFiles = (new String[] { "bin" + File.separator + "javaw.exe",
				"jre" + File.separator + "bin" + File.separator + "javaw.exe" });
		linTestFiles = (new String[] { "bin" + File.separator + "java",
				"jre" + File.separator + "bin" + File.separator + "java" });
	}

	protected boolean pathIsValid() {
		if (existFiles == null)
			return true;
		for (int i = 0; i < existFiles.length; ++i) {
			File path = new File(pathSelectionPanel.getPath(), existFiles[i]).getAbsoluteFile();
			if (path.exists())
				return true;
		}
		return false;
	}

	private JRadioButton rb1, rb2;
	private JLabel messageLabel;
	private JLabel messageLabelJdk;
	private JLabel vendor;
	private JLabel version;
	private JLabel arch;

	private Properties jvmProperties;

	@SuppressWarnings("serial")
	public JREPathPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata, new IzPanelLayout());
		// Set default values
		emptyTargetMsg = getI18nStringForClass("empty_target", "TargetPanel");
		warnMsg = getI18nStringForClass("warn", "TargetPanel");

		String introText = getI18nStringForClass("intro", "PathInputPanel");

		add(new JLabel(introText), NEXT_LINE);

		rb1 = new JRadioButton("Default Java VM", true);
		rb1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
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
		rb1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (rb1.isSelected()) {
					pathSelectionPanel.setEnabled(false);
				} else {
					pathSelectionPanel.setEnabled(true);
				}
			}
		});

		pathSelectionPanel.addChangeListener(this);

		JPanel jvmInfo = new JPanel() {
			@Override
			public int getWidth() {
				return pathSelectionPanel.getWidth();
			}
		};
		Border border2 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder((EtchedBorder.LOWERED)),
				"VM Information");
		jvmInfo.setBorder(border2);
		jvmInfo.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(4, 4, 2, 4);
		c.weightx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		jvmInfo.add(new JLabel("Vendor:", LEFT), c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(2, 4, 2, 4);
		c.weightx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		jvmInfo.add(new JLabel("Version:", LEFT), c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(2, 4, 4, 4);
		c.weightx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		jvmInfo.add(new JLabel("Architecture:", LEFT), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(4, 2, 2, 4);
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		jvmInfo.add(vendor = new JLabel(""), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(2, 2, 2, 4);
		c.fill = GridBagConstraints.HORIZONTAL;
		jvmInfo.add(version = new JLabel(""), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(2, 2, 4, 4);
		c.fill = GridBagConstraints.HORIZONTAL;
		jvmInfo.add(arch = new JLabel(""), c);

		add(jvmInfo, NEXT_LINE);

		messageLabel = createLabel();
		add(messageLabel, NEXT_LINE);
		messageLabelJdk = createLabel();
		add(messageLabelJdk, NEXT_LINE);

		createLayoutBottom();
		getLayoutHelper().completeLayout();

		setMustExist(true);
		if (OsVersion.IS_WINDOWS) {
			setExistFiles(winTestFiles);
		} else if (OsVersion.IS_UNIX) {
			setExistFiles(linTestFiles);
		}
	}

	private JLabel createLabel() {
		return createLabel("");
	}

	@SuppressWarnings("serial")
	private JLabel createLabel(String title) {
		JLabel temp = new JLabel(title) {
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
		temp.setVerticalAlignment(TOP);
		temp.setHorizontalAlignment(LEFT);
		return temp;
	}

	public boolean isValidated() {
		if (rb1.isSelected()) {
			idata.setVariable(VAR_SELECTED_JAVA_PATH, new File(idata.getVariable(VAR_JAVA_HOME)).getPath());
		} else {
			idata.setVariable(VAR_SELECTED_JAVA_PATH, pathSelectionPanel.getPath());
		}
		// Next button is enabled only when JVM configuration is correct
		return true;
	}

	public void panelActivate() {
		super.panelActivate();
		String chosenPath = idata.getVariable(VAR_SELECTED_JAVA_PATH);
		if (chosenPath == null || "".equals(chosenPath)) {
			File javaHome = getDefaultJava7Location(idata.getVariable(VAR_JAVA_HOME));
			System.out.println("[DEBUG] idata.setVariable(VAR_JAVA_HOME,javaHome.getAbsolutePath()) = "
					+ VAR_JAVA_HOME + ", " + javaHome.getAbsolutePath());
			idata.setVariable(VAR_JAVA_HOME, javaHome.getAbsolutePath());
			// This case is for starting installer with jdk/bin/java under any platform
			Properties props = getJavaPlatformProperties(javaHome.getAbsolutePath(), new String[2]);
			if ("jre".equals(javaHome.getName())) {
				File parentFolder = javaHome.getParentFile();
				System.out.println("[DEBUG] panelActivate() parentFolder = " + parentFolder.toString());
				File bin = new File(parentFolder, "bin");
				String ext = OsVersion.IS_WINDOWS ? ".exe" : "";
				File java = new File(bin, "java" + ext);
				File javac = new File(bin, "javac" + ext);
				if (javac.canRead() && java.canRead()) {
					System.out.println("[DEBUG] idata.setVariable(VAR_JAVA_HOME,parentFolder.getAbsolutePath()) = "
							+ VAR_JAVA_HOME + ", " + parentFolder.getAbsolutePath());
					idata.setVariable(VAR_JAVA_HOME, parentFolder.getAbsolutePath());
				}
			} else if (OsVersion.IS_WINDOWS && javaHome.getName().matches("jre\\d")) {
				// try to discover windows jdk
				// c:\Program Files\Java\jdk${java_version}
				String javaVersion = (String) props.get(SYSPN_JAVA_VERSION);
				if (javaVersion != null) {
					File parentFolder = javaHome.getParentFile();
					File jdkLocation = new File(parentFolder, "jdk" + javaVersion);
					File bin = new File(jdkLocation, "bin");
					File java = new File(bin, "java.exe");
					File javac = new File(bin, "javac.exe");
					if (java.canRead() && javac.canRead()) {
						idata.setVariable(VAR_JAVA_HOME, jdkLocation.getAbsolutePath());
					}
				}
			}
		}
		updateJava(true);
	}

	private void clearJava() {
		idata.setVariable(VAR_SELECTED_JAVA_PATH, "");
		updateJava(true);
	}

	private int updateJava(boolean setPath) {
		if (setPath) {
			String chosenPath = "";
			chosenPath = idata.getVariable(VAR_SELECTED_JAVA_PATH);
			if (chosenPath == null || "".equals(chosenPath))
				chosenPath = new File(idata.getVariable(VAR_JAVA_HOME)).getPath();
			pathSelectionPanel.setPath(chosenPath);
		}

		idata.setVariable(VAR_SELECTED_JAVA_PATH, pathSelectionPanel.getPath());
		jvmProperties = new Properties();
		int status = verifyVersion(pathSelectionPanel.getPath(), jvmProperties);
		if ("installer".equals(idata.getVariable("PACK_NAME"))) {
			vendor.setText(jvmProperties.getProperty(SYSPN_JAVA_VENDOR, "Unknown"));
			version.setText(jvmProperties.getProperty(SYSPN_JAVA_VERSION, "Unknown"));
			arch.setText(jvmProperties.getProperty(SYSPN_SUN_ARCH_DATA_MODEL) == null ? "Unknown"
					: jvmProperties.getProperty(SYSPN_SUN_ARCH_DATA_MODEL) + "-bit");
			arch.getParent().doLayout();
		}
		return status;
	}

	@Override
	public void panelDeactivate() {
		super.panelDeactivate();
		String installPath = idata.getVariable("INSTALL_PATH");
		idata.setVariable("NORMALIZED_INSTALL_PATH", installPath.replace('\\', '/'));
	}

	public String getSummaryBody() {
		if (rb1.isSelected())
			return "default";
		else
			return idata.getVariable(VAR_SELECTED_JAVA_PATH);
	}

	private static boolean isGnuVersion(String[] output) {
		// "My" VM writes the version on stderr :-(
		String vs = (output[0].length() > 0) ? output[0] : output[1];
		return vs.indexOf(gnuVersion) >= 0;
	}

	public static int verifyVersion(String jvmLocation, Properties props) {
		String[] output = new String[2];
		props.putAll(getJavaPlatformProperties(jvmLocation, output));
		if (OsVersion.IS_OSX && jvmLocation.contains(JAVA_APPLET_PLUGIN)) {
			return -6;
		} else if (isGnuVersion(output)) {
			return -1;
		}
		return verifyVersionRange(props.getProperty(SYSPN_JAVA_VERSION));
	}

	public static int verifyVersionRange(String detectedVersion) {
		// Java versions cold be 1.1..1.9 considering early access for java 9
		// So we accept the pattern for java version 1\.[1-9] and
		// check 3d char for >6 and return new error code -3 for none supported
		// java version
		if (detectedVersion == null) {
			return -5;
		}

		if (!detectedVersion.matches("1\\.[1-9]\\.[0-9].*") && !detectedVersion.matches("[1-9]-.*")) {
			return -4; // Unknown version
		}

		int versionNumber = 0;

		if (detectedVersion.matches("1\\.[1-9]\\.[0-9].*")) {
			versionNumber = Integer.parseInt(detectedVersion.substring(2, 3));
		} else {
			versionNumber = 9;
		}

		if (versionNumber < minVersion) {
			return -3; // Version is less that minimum version
		}

		if (versionNumber > maxVersion) {
			return -2;
		}
		return 0;
	}

	public static Properties getJavaPlatformProperties(String location, String[] output) {
		String jarPath = P2DirectorStarterListener.findPathJar(JREPathPanel.class);
		Debug.trace(jarPath);

		String[] params = { location + File.separator + "bin" + File.separator + "java", "-Djava.awt.headless=true",
				"-showversion", "-classpath", jarPath + File.pathSeparator + System.getProperty("java.class.path"),
				JREPathPanel.class.getName() };

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

	public static File getDefaultJava7Location(String izPackDefaultJVM) {
		if (OsVersion.IS_OSX && izPackDefaultJVM.contains(JAVA_APPLET_PLUGIN)) {
			String[] params = { "/usr/libexec/java_home", "-v", "1." + JREPathPanel.minVersion };
			String[] output = new String[2];
			FileExecutor fe = new FileExecutor();
			Debug.trace("[DEBUG] getDefaultJava7Location() " + params[0] + " " + params[1]);
			fe.executeCommand(params, output);
			Debug.trace("[DEBUG] getDefaultJava7Location() " + output[0]);
			File location = new File(output[0].trim());
			if (location.canRead()) {
				return location;
			}
		}

		return new File(izPackDefaultJVM);
	}

	public boolean isArchSupported(String arch) {
		String[] output = new String[2];
		String jarPath = P2DirectorStarterListener.findPathJar(JREPathPanel.class);
		Debug.trace(jarPath);

		String[] params = { pathSelectionPanel.getPath() + File.separator + "bin" + File.separator + "java",
				"-Djava.awt.headless=true", "-showversion", "-d" + arch, "-classpath",
				jarPath + File.pathSeparator + System.getProperty("java.class.path"), JREPathPanel.class.getName() };

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
		System.out.println(
				MessageFormat.format(pattern, SYSPN_JAVA_VENDOR, System.getProperty(SYSPN_JAVA_VENDOR, "Unknown")));
		System.out.println(
				MessageFormat.format(pattern, SYSPN_JAVA_VERSION, System.getProperties().get(SYSPN_JAVA_VERSION)));
		System.out.println(MessageFormat.format(pattern, Java.SYSPN_JAVA_HOME, System.getProperties().get(Java.SYSPN_JAVA_HOME)));
		System.out.println(MessageFormat.format(pattern, SYSPN_SUN_ARCH_DATA_MODEL,
				System.getProperties().get(SYSPN_SUN_ARCH_DATA_MODEL)));
	}

	/**
	 * Asks to make the XML panel data.
	 *
	 * @param panelRoot
	 *            The tree to put the data in.
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

	public void change() {
		pathSelectionPanel.removeChangeListener();
		messageLabel.setText("");
		messageLabel.setForeground(Color.black);
		messageLabelJdk.setText("");
		if (!pathExists()) {
			messageLabel.setText(parent.langpack.getString(getI18nStringForClass("wrongPath.title", "JREPathPanel")));
			messageLabel.setForeground(Color.red);
			parent.lockNextButton();
		} else if (pathIsValid()) {
			int status = updateJava(false);
			if (status == 0 && (OsVersion.IS_WINDOWS || OsVersion.IS_OSX)
					&& VPE_NOT_SUPPORTED_ARCH.equals(jvmProperties.getProperty(SYSPN_SUN_ARCH_DATA_MODEL))) {
				messageLabel.setText(parent.langpack.getString("JREPathPanel.VPEdoesNotSupportJava64.title"));
				parent.unlockNextButton();
			} else if (status == 0) {
				parent.unlockNextButton();
			} else if (status == -2) {
				messageLabel.setText(
						"<html><p>This JVM was not tested with Red Hat JBoss Developer Studio.<br>It is not guaranteed to work.</p></html>");
				parent.unlockNextButton();
			} else if (status == -1) {
				messageLabel.setText(parent.langpack.getString(getI18nStringForClass("badVersion2", "PathInputPanel")));
				messageLabel.setForeground(Color.red);
				parent.lockNextButton();
			} else if (status == -3 || status == -4 || status == -5) {
				messageLabel.setText(parent.langpack.getString(getI18nStringForClass("badVersion3", "PathInputPanel")));
				messageLabel.setForeground(Color.red);
				parent.lockNextButton();
			} else if (status == -6) {
				messageLabel.setText(parent.langpack.getString(getI18nStringForClass("badVersion4", "PathInputPanel")));
				messageLabel.setForeground(Color.red);
				parent.lockNextButton();
			}
			// Verify if selected runtime is jre and show warning
			File javacLocation = new File(idata.getVariable(VAR_SELECTED_JAVA_PATH),
					"bin/javac" + (OsVersion.IS_WINDOWS ? ".exe" : ""));
			if (!javacLocation.canRead() && !messageLabel.getForeground().equals(Color.red)) {
				messageLabelJdk.setText(
						"<html><p>Chosen Java VM is a Java Runtime only, it will run Developer Studio but it is recommended to use a Java SDK.</p></html>");
				messageLabelJdk.setForeground(Color.black);
			}
		} else {
			messageLabel.setText(parent.langpack.getString(getI18nStringForClass("notValid", "PathInputPanel")));
			messageLabel.setForeground(Color.red);
		}
		pathSelectionPanel.addChangeListener(this);

	}

	private boolean pathExists() {
		return new File(pathSelectionPanel.getPath()).canRead();
	}
}
