package com.jboss.devstudio.core.intro.action;

import java.util.Properties;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.activities.ITriggerPoint;
import org.eclipse.ui.activities.WorkbenchActivityHelper;
import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.cheatsheets.ICheatSheetResource;
import org.eclipse.ui.internal.cheatsheets.data.CheatSheet;
import org.eclipse.ui.internal.cheatsheets.registry.CheatSheetCollectionElement;
import org.eclipse.ui.internal.cheatsheets.registry.CheatSheetElement;
import org.eclipse.ui.internal.cheatsheets.registry.CheatSheetRegistryReader;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import com.jboss.devstudio.core.Activator;

public class OpenCheatSheetIntroURLAction  implements IIntroAction {
    private IWorkbenchWindow workbenchWindow;

	public void run(IIntroSite site, Properties params) {
		if (params == null || params.getProperty("cheatSheetId", null) == null) //$NON-NLS-1$
			return;
        String cheatSheetId = params.getProperty("cheatSheetId", null); //$NON-NLS-1$
		
		workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (workbenchWindow == null) {
            return;
        }

        if(cheatSheetId != null && cheatSheetId.trim().length() > 0) {
    		CheatSheetCollectionElement cheatSheets = (CheatSheetCollectionElement)CheatSheetRegistryReader.getInstance().getCheatSheets();
    		CheatSheetElement cheatSheet = cheatSheets.findCheatSheet(cheatSheetId, true);
    		if (cheatSheet != null) {
				ITriggerPoint triggerPoint = PlatformUI.getWorkbench()
							.getActivitySupport().getTriggerPointManager()
							.getTriggerPoint(ICheatSheetResource.TRIGGER_POINT_ID);
				if (WorkbenchActivityHelper.allowUseOf(triggerPoint, cheatSheet)) {
					new OpenCheatSheetAction(cheatSheetId).run();
				}
    		}
        }
	}
}
