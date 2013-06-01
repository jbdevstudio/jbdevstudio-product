package com.jboss.jbds.product;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class MetaGeneratorApplication implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		return IApplication.EXIT_OK;
	}

	public void stop() {
	}

}
