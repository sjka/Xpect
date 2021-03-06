/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.xpect.ui;

import org.osgi.framework.BundleContext;
import org.xpect.registry.DelegatingLanguageRegistry;
import org.xpect.registry.ILanguageInfo;
import org.xpect.ui.internal.XpectActivator;
import org.xpect.ui.registry.ExtensionRegistryReader;
import org.xpect.ui.registry.UILanugageRegistry;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 */
public class XpectPluginActivator extends XpectActivator {

	public final static String XPECT_EDITOR_ID = XpectPluginActivator.ORG_XPECT_XPECT;
	public final static String XT_EDITOR_ID = "org.xpect.Xt";

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		UILanugageRegistry lanugageRegistry = new UILanugageRegistry();
		((DelegatingLanguageRegistry) ILanguageInfo.Registry.INSTANCE).setDelegate(lanugageRegistry);
		new ExtensionRegistryReader(lanugageRegistry).readRegistry();
	}

}
