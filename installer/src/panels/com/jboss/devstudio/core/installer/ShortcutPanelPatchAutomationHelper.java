package com.jboss.devstudio.core.installer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.adaptator.IXMLWriter;
import com.izforge.izpack.adaptator.impl.XMLWriter;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.InstallerException;
import com.izforge.izpack.installer.PanelAutomation;
import com.izforge.izpack.util.Debug;

public class ShortcutPanelPatchAutomationHelper implements PanelAutomation {

	@Override
	public void makeXMLData(AutomatedInstallData installData, IXMLElement panelRoot) {
	}

	@Override
	public void runAutomated(AutomatedInstallData installData, IXMLElement panelRoot) throws InstallerException {
		File file = new File(installData.getVariable("INSTALL_PATH"), "InstallConfigRecord.xml");
		FileOutputStream out = null;
		BufferedOutputStream outBuff = null;
		try {
			out = new FileOutputStream(file);
			outBuff = new BufferedOutputStream(out, 5120);
			IXMLWriter writer = new XMLWriter(out);
			writer.write(installData.xmlData);
		} catch (IOException e) {
			Debug.trace(e);
		} finally {
			try {
				if (outBuff != null) {
					outBuff.close();
				}
			} catch (IOException ignored) {
				Debug.trace(ignored);
			}
		}
	}
}
