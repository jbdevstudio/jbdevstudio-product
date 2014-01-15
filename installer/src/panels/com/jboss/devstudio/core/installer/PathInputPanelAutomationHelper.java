package com.jboss.devstudio.core.installer;

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.adaptator.impl.XMLElementImpl;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.PanelAutomation;
import com.izforge.izpack.util.Debug;
import com.izforge.izpack.util.VariableSubstitutor;

public class PathInputPanelAutomationHelper implements PanelAutomation {
    
	/**
     * Asks to make the XML panel data.
     *
     * @param idata     The installation data.
     * @param panelRoot The tree to put the data in.
     */
    public void makeXMLData(AutomatedInstallData idata, IXMLElement panelRoot)
    {
        // Installation path markup
        IXMLElement ipath = new XMLElementImpl("installpath",panelRoot);
        // check this writes even if value is the default,
        // because without the constructor, default does not get set.
        ipath.setContent(idata.getInstallPath());
        Debug.trace(idata.getInstallPath());
        // Checkings to fix bug #1864
        IXMLElement prev = panelRoot.getFirstChildNamed("installpath");
        if (prev != null)
        {
            panelRoot.removeChild(prev);
        }
        panelRoot.addChild(ipath);
    }

    /**
     * Asks to run in the automated mode.
     *
     * @param idata     The installation data.
     * @param panelRoot The XML tree to read the data from.
     */
    public void runAutomated(AutomatedInstallData idata, IXMLElement panelRoot)
    {
        // We set the installation path
        IXMLElement ipath = panelRoot.getFirstChildNamed("installpath");

        // Allow for variable substitution of the installpath value
        VariableSubstitutor vs = new VariableSubstitutor(idata.getVariables());
        String path = ipath.getContent();
        path = vs.substitute(path, null);
        idata.setInstallPath(path);
    }
}
