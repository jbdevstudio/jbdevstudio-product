package com.jboss.devstudio.core.splash;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.splash.BasicSplashHandler;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * This SplashHandler add's in the version number onto Red Hat CodeReady Studio splash screen.
 * 
 * Supports the same location info as Eclipse but introduces "startupVersionLocation" property to 
 * render version right aligned to a lower right position.
 * 
 * @author max
 *
 */
public class SplashHandler extends BasicSplashHandler {

	
	/** which font data to use for the version string - defaults to "Arial-bold-9" **/
	private static final String STARTUP_VERSION_FONT_DATA = "startupVersionFont";
	/** the lower right position of the version number. The version will be rendered to align with this point */
	private static final String STARTUP_VERSION_LOCATION = "startupVersionLocation";

	public SplashHandler() {
		super();
	}

	public void init(Shell splash) {
		super.init(splash);
		String progressRectString = null;
		String messageRectString = null;
		String foregroundColorString = null;
		String versionLocation = null;
	    String versionFont = null;
		
		IProduct product = Platform.getProduct();
		
		if (product != null) {
			progressRectString = product
					.getProperty(IProductConstants.STARTUP_PROGRESS_RECT);
			messageRectString = product
					.getProperty(IProductConstants.STARTUP_MESSAGE_RECT);
			foregroundColorString = product
					.getProperty(IProductConstants.STARTUP_FOREGROUND_COLOR);
			versionLocation = product.getProperty(STARTUP_VERSION_LOCATION);
			versionFont = product.getProperty(STARTUP_VERSION_FONT_DATA);
		}
		
		Rectangle progressRect = StringConverter.asRectangle(
				progressRectString, new Rectangle(10, 10, 300, 15));
		setProgressRect(progressRect);

		Rectangle messageRect = StringConverter.asRectangle(messageRectString,
				new Rectangle(10, 35, 300, 15));
		setMessageRect(messageRect);

		int foregroundColorInteger;
		try {
			foregroundColorInteger = Integer
					.parseInt(foregroundColorString, 16);
		} catch (Exception ex) {
			foregroundColorInteger = 0x5A5A5A; // dark grey
		}

		setForeground(new RGB((foregroundColorInteger & 0x5A0000) >> 16,
				(foregroundColorInteger & 0x5A00) >> 8,
				foregroundColorInteger & 0x5A));
		
		final String versionString = getVersionString(); // System.out.println("versionString = " + versionString); 
		
		final Point versionLocationPoint = StringConverter.asPoint(
				versionLocation, new Point(228, 209));
	
		final FontData fd = StringConverter.asFontData(versionFont, new FontData("Arial", 9, SWT.BOLD));
		
		getContent().addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				e.gc.setTextAntialias(SWT.ON);
				
				Font newF = new Font(e.gc.getDevice(), fd);
				e.gc.setFont(newF);

				Point point = e.gc.textExtent(versionString);
				e.gc.setForeground(getForeground());
				// when version text is too long it will be left aligned otherwise it will be right aligned
				if (point.x > 199){
          e.gc.drawText(versionString, 29,
              versionLocationPoint.y, true);
				}
				else {
	        e.gc.drawText(versionString, versionLocationPoint.x - point.x,
	            versionLocationPoint.y, true);
				}
			}
		});
	}

	private String getVersionString() {
		Bundle bundle = Platform.getBundle("com.jboss.devstudio.core");

		String versionString = (String) bundle.getHeaders().get(
				"Bundle-Version");
		
		if (versionString != null) {
			Version version = new Version(versionString);
			String qualifier = null;
			if(Boolean.getBoolean("SHOW_BUILDID_ON_STARTUP") || PrefUtil.getInternalPreferenceStore().getBoolean(
			"SHOW_BUILDID_ON_STARTUP")) {
				qualifier = version.getQualifier();
			} else {
				qualifier = version.getQualifier(); // qualifier = "Alpha1-v20120906-1414-B123";
				int ix = qualifier.indexOf('-'); //JBDS-2251, JBDS-2264: new qualifier order requires new way to extract the BUILD_ALIAS - input: Alpha1-v20120906-1414-B123; output: Alpha1
				if (ix > 0) {
					qualifier = qualifier.substring(0,ix);
				} 
			}
			if ("qualifier".equals(qualifier))
			{
				return version.getMajor() + "." + version.getMinor() + "."
						+ version.getMicro();
			}
			return version.getMajor() + "." + version.getMinor() + "."
					+ version.getMicro() + "." + qualifier;
		}
		return "";
	}
}
