/*
 * IzPack - Copyright 2001-2008 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izforge.izpack.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.util.Housekeeper;
import com.izforge.izpack.util.Log;
import com.izforge.izpack.util.OsVersion;
import com.izforge.izpack.util.VariableSubstitutor;
import com.jboss.devstudio.core.installer.RunAfterAction;


/**
 * The simple finish panel class.
 *
 * @author Julien Ponge
 */
public class SimpleFinishPanel extends IzPanel {
	/**
     *
     */
	private static final long serialVersionUID = 3689911781942572085L;

	/**
	 * The variables substitutor.
	 */
	private VariableSubstitutor vs;

	private RunAfterAction runAfterAction;

	/**
	 * The constructor.
	 *
	 * @param parent
	 *            The parent.
	 * @param idata
	 *            The installation data.
	 */
	public SimpleFinishPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata, new GridBagLayout());
		this.vs = new VariableSubstitutor(idata.getVariables());
		this.runAfterAction = new RunAfterAction(this, idata);
	}

	/**
	 * Indicates wether the panel has been validated or not.
	 *
	 * @return true if the panel has been validated.
	 */
	public boolean isValidated() {
		return true;
	}

	/**
	 * Called when the panel becomes active.
	 */
	public void panelActivate() {
		Housekeeper.getInstance().registerForCleanup(this.runAfterAction);
		parent.lockNextButton();
		parent.lockPrevButton();
		parent.setQuitButtonText(parent.langpack.getString("FinishPanel.done"));
		parent.setQuitButtonIcon("done");

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		if (idata.installSuccess) {

			// We set the information
			JLabel successLabel = LabelFactory.create(
					parent.langpack.getString("FinishPanel.success"),
					parent.icons.getImageIcon("check"), LEADING);
			constraints.weighty = 1.0;
			constraints.gridx = 0;
			constraints.gridy = 0;
			add(successLabel, constraints);

			final JCheckBox runAfterCheckbox = new JCheckBox(parent.langpack.getString("SimpleFinishPanel.headline.run_after"), true);
			runAfterCheckbox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					SimpleFinishPanel.this.runAfterAction.setRunAfter(runAfterCheckbox.isSelected());
				}
			});

	        constraints.gridx = 0;
	        constraints.gridy = 1;
	        constraints.gridwidth = 1;
	        constraints.gridheight = 1;

	        constraints.fill = GridBagConstraints.HORIZONTAL;
	        constraints.anchor = GridBagConstraints.NORTHWEST;

	        add(runAfterCheckbox, constraints);

			if (idata.uninstallOutJar != null) {
				// We prepare a message for the uninstaller feature
				String path = translatePath("$INSTALL_PATH") + File.separator
						+ "Uninstaller";

				JLabel infoLabel = LabelFactory.create(parent.langpack.getString("FinishPanel.uninst.info"),
				parent.icons.getImageIcon("preferences"), LEADING);
				constraints.weighty = 0.0;
				constraints.anchor = GridBagConstraints.SOUTH;
				constraints.gridx = 0;
				constraints.gridy = 1;
				add(infoLabel, constraints);

				JLabel pathLabel = LabelFactory.create(path,
				parent.icons.getImageIcon("empty"), LEADING);
				constraints.anchor = GridBagConstraints.NORTH;
				constraints.weighty = 0.0;
				constraints.gridx = 0;
				constraints.gridy = 2;
				add(pathLabel, constraints);
			}
		} else {
			add(LabelFactory.create(
					parent.langpack.getString("FinishPanel.fail"),
					parent.icons.getImageIcon("stop"), LEADING), constraints);
		}
		getLayoutHelper().completeLayout(); // Call, or call not?
		Log.getInstance().informUser();
	}

	/**
	 * Translates a relative path to a local system path.
	 *
	 * @param destination
	 *            The path to translate.
	 * @return The translated path.
	 */
	private String translatePath(String destination) {
		// Parse for variables
		destination = vs.substitute(destination, null);

		// Convert the file separator characters
		return destination.replace('/', File.separatorChar);
	}

	public void runAfter() {
		try{
			String path = translatePath("$INSTALL_PATH");
			String[] command = null;
			if(OsVersion.IS_MAC){
				command = new String[3];
				command[0] = "open";
				command[1] = "-a";
				command[2] = path+File.separator+"codereadystudio.app";
			}else{
				command = new String[]{path+File.separator+"codereadystudio"};
				if(OsVersion.IS_WINDOWS){
					command[0] += ".bat";
				}
			}
			Process process = Runtime.getRuntime().exec(command, null, new File(path));
		}catch(IOException ex){
			System.out.println("could not execute the command!");
			emitError("Exception", ex.getMessage());
            ex.printStackTrace();
		}
	}
}
