package com.jboss.devstudio.core.installer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.izforge.izpack.Pack;
import com.izforge.izpack.PackFile;
import com.izforge.izpack.event.InstallerListener;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.util.AbstractUIProgressHandler;
import com.izforge.izpack.util.Debug;
import com.izforge.izpack.util.OsVersion;
import com.jboss.devstudio.core.installer.JavaVersionReader.ResponseListener;

public class P2DirectorStarterListener implements InstallerListener {

	public static final String DEVSTUDIO_LOCATION =  "studio" + (OsVersion.IS_OSX ? File.separator + "codereadystudio.app" + File.separator + "Contents" + File.separator + "Eclipse" : "");
	
	private String installLocation;
	private String selectedJvm;
	
	public void afterDir(File arg0, PackFile arg1) throws Exception {
	}

	public void afterFile(File arg0, PackFile arg1) throws Exception {
	}

	public void afterInstallerInitialization(AutomatedInstallData arg0) throws Exception {
		Debug.trace(arg0.getVariable("INSTALL_PATH"));
	}

	public void afterPack(Pack arg0, Integer arg1, AbstractUIProgressHandler arg2) throws Exception {
	}

	public void afterPacks(AutomatedInstallData arg0, AbstractUIProgressHandler arg1) throws Exception {
	}

	public void beforeDir(File arg0, PackFile arg1) throws Exception {
	}

	public void beforeFile(File arg0, PackFile arg1) throws Exception {
	}

	public void beforePack(Pack arg0, Integer arg1, AbstractUIProgressHandler arg2) throws Exception {
	}

	public void beforePacks(AutomatedInstallData arg0, Integer arg1, AbstractUIProgressHandler arg2) throws Exception {
		this.installLocation = arg0.getVariable("INSTALL_PATH");
		this.selectedJvm = arg0.getVariable("JAVA_HOME");
	}

	public boolean isFileListener() {
		return false;
	}
	
	public static String findPathJar(Class<?> context) throws IllegalStateException {
		String jarRelatedPath = "/" + context.getName().replace(".", "/")+ ".class";
		URL location = context.getResource(jarRelatedPath);
		String jarPath = "";
		try {
			jarPath = URLDecoder.decode(location.getPath(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int exclIndex = jarPath.lastIndexOf("!");
		
		if(exclIndex < 0 ) {
			
			return jarPath.substring(0, jarPath.indexOf(jarRelatedPath)+1);
		}
		return jarPath.substring("file:".length(), jarPath.lastIndexOf("!"));
	}
	
	public interface ConsoleCommand {
		final String URL_SURROUND_CHAR = OsVersion.IS_WINDOWS ? "\"" :"''";
		void execute() throws ConsoleCommandException;
		void execute(ResponseListener listener) throws ConsoleCommandException;
		ConsoleCommand setParameter(String param);
		void close();
		
	}
	
	public static class BaseConsoleCommand implements ConsoleCommand, ResponseListener {
		protected static final String EOL = System.getProperty("line.separator");
		private final JavaVersionReader rt = new JavaVersionReader();
		protected String cmd = "";
		private List<String> parameters = new ArrayList<String>();

		public void execute() throws ConsoleCommandException {
			execute(this);
		}

		public void execute(ResponseListener listener) throws ConsoleCommandException {

			if (cmd.length() > 0) {
				String result = rt.executeJava(parameters.get(0) + File.separator + "bin",
						MessageFormat.format(cmd, parameters.toArray(new Object[parameters.size()])), listener);
				Debug.trace(result);
				if (rt.getErrCode() != 0) {
					throw new ConsoleCommandException(rt.getErrCode(), result);
				}
			} else {
				throw new IllegalArgumentException("Command line template is not initialized");
			}

		}

		public ConsoleCommand setParameter(String param) {
			parameters.add(param);
			return this;
		}

		public void stdout(String line) {
		}

		public void errout(String line) {
			stdout(line);
		}
		
		public void close() {
			File studioLocation = new File(parameters.get(3), DEVSTUDIO_LOCATION);
			File cancelLocation = new File(studioLocation, "cancel");
			Debug.trace(cancelLocation);
			try {
				cancelLocation.createNewFile();
			} catch (IOException e) {
				Debug.trace("[DEBUG] Cannot cancel installation");
			};
				
		}
		
		public int getErrCode() {
			return rt.getErrCode();
		}
	}
	
	public static class BundleListConsoleCommand extends BaseConsoleCommand {
		
		Pattern completePattern = Pattern.compile("Operation completed .*");
		
		private List<String> bundles = new ArrayList<String>(256);
		public BundleListConsoleCommand() {
			cmd="-jar \"{1}\" -l -r " + URL_SURROUND_CHAR + "{2}" + URL_SURROUND_CHAR + " -d \"{3}" + File.separator + DEVSTUDIO_LOCATION + "\" ";
		}

		@Override
		public void stdout(String line) {
			Debug.trace(line.replace(EOL,""));
			if(!completePattern.matcher(line).matches() && line.contains("=")) {
				bundles.add(line.substring(0,line.indexOf('=')));
			}
		}

		public List<String> getBundles() {
			return bundles;
		}
	}
	
	public static class FeatureInstallConsoleCommand extends BaseConsoleCommand {
		Pattern downloadPattern = Pattern.compile("Downloading .*");
		Pattern configPattern = Pattern.compile("(Configuring .*| Installing .*)");

		int counter = 0;
		int DOWNLOADING_WEIGHT = 5;
		int CONFI_INSTALL_WEIGHT = 1;
		public FeatureInstallConsoleCommand(AutomatedInstallData installerData) {
			cmd="-jar \"{1}\" " +
					"-roaming " +
					"-vm \"{0}\" " +
					"-r " +
					URL_SURROUND_CHAR + "{2}" + URL_SURROUND_CHAR + " " +
					"-d \"{3}" + File.separator + DEVSTUDIO_LOCATION + "\" " +
					"-p devstudio " +
					"-i {4} " +
					"-profileProperties org.eclipse.update.install.features=true";
			if(OsVersion.IS_OSX && installerData.getVariable(JREPathPanel.DATA_MODEL_VAR)!=null) {
				// this is required to force director to install selected architecture
				// devstudio.ini file doesn't need this -d32 or -d64 because in some way 
				// mac always select right architecture for studio
				cmd = cmd + " -vmargs -d" + installerData.getVariable(JREPathPanel.DATA_MODEL_VAR);
			}
		}

		@Override
		public void stdout(String line) {
			Debug.trace(line.replace(EOL,""));
			if(line.contains("Downloading")) {
				downloadProgress(counter+=DOWNLOADING_WEIGHT,"Fetching " + line.substring(line.indexOf(' ')));
			} else if(line.contains("Configuring") || line.contains("Installing")) {
				configInstallProgress(counter+=CONFI_INSTALL_WEIGHT,line);
			}
		}
		public void downloadProgress(int i,String line) {
		}
		public void configInstallProgress(int i,String line) {
		}
		
		public int calculateAmountOfWork(int bundles) {
			return bundles*DOWNLOADING_WEIGHT + bundles*CONFI_INSTALL_WEIGHT;
		}
	}
	
	public static class MetadataGenerationConsoleCommand extends BaseConsoleCommand {
		int counter = 0;
		public MetadataGenerationConsoleCommand() {
			cmd="-jar \"{1}\" -vm \"{0}\" -application com.jboss.devstudio.core.EclipseGenerator -noSplash -clean";
		}
	}
	
}
