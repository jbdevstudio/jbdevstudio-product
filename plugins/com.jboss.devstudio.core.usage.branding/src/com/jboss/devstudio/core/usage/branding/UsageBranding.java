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
 * Provides branding to Usage Reporting for Red Hat CodeReady Studio.
 * 
 * @author Andre Dietisheim
 */
public class UsageBranding implements IUsageBranding {

	public String getPreferencesDescription() {
		return UsageBrandingMessages.UsageReportPreferencePage_Description;
	}

	public String getPreferencesAllowReportingCheckboxLabel() {
		return UsageBrandingMessages.UsageReportPreferencePage_AllowReporting;
	}
	
	public String getStartupAllowReportingTitle() {
		return UsageBrandingMessages.UsageReport_DialogTitle;
	}
	
	public String getStartupAllowReportingMessage() {
		return UsageBrandingMessages.UsageReport_DialogMessage;
	}

	public String getStartupAllowReportingCheckboxLabel() {
		return UsageBrandingMessages.UsageReport_Checkbox_Text;
	}
	
	public String getStartupAllowReportingDetailLink() {
		return UsageBrandingMessages.UsageReport_ExplanationPage;
	}
	
	public String getGlobalRemotePropertiesUrl() {
		return UsageBrandingMessages.GlobalUsageSettings_RemoteProps_URL;
	}

	public String getGoogleAnalyticsAccount() {
		return UsageBrandingMessages.UsageReport_GoogleAnalytics_Account;
	}

	public String getGoogleAnalyticsReportingHost() {
		return UsageBrandingMessages.UsageReport_HostName;
	}
}
