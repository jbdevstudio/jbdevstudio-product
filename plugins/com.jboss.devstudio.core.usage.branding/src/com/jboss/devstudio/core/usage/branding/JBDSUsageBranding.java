/*******************************************************************************
 * Copyright (c) 2010-2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.jboss.devstudio.core.usage.branding;

import org.jboss.tools.usage.branding.IUsageBranding;

/**
 * Provides branding to Usage Reporting for Red Hat Developer Studio.
 * 
 * @author Andre Dietisheim
 */
public class JBDSUsageBranding implements IUsageBranding {

	public String getPreferencesDescription() {
		return JBDSUsageBrandingMessages.UsageReportPreferencePage_Description;
	}

	public String getPreferencesAllowReportingCheckboxLabel() {
		return JBDSUsageBrandingMessages.UsageReportPreferencePage_AllowReporting;
	}
	
	public String getStartupAllowReportingTitle() {
		return JBDSUsageBrandingMessages.UsageReport_DialogTitle;
	}
	
	public String getStartupAllowReportingMessage() {
		return JBDSUsageBrandingMessages.UsageReport_DialogMessage;
	}

	public String getStartupAllowReportingCheckboxLabel() {
		return JBDSUsageBrandingMessages.UsageReport_Checkbox_Text;
	}
	
	public String getStartupAllowReportingDetailLink() {
		return JBDSUsageBrandingMessages.UsageReport_ExplanationPage;
	}
	
	public String getGlobalRemotePropertiesUrl() {
		return JBDSUsageBrandingMessages.GlobalUsageSettings_RemoteProps_URL;
	}

	public String getGoogleAnalyticsAccount() {
		return JBDSUsageBrandingMessages.UsageReport_GoogleAnalytics_Account;
	}

	public String getGoogleAnalyticsReportingHost() {
		return JBDSUsageBrandingMessages.UsageReport_HostName;
	}
}
