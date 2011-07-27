/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.Version;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;

public class EclipseUtilTest extends TestCase
{

	public void testGetApplicationLauncher()
	{
		IPath path = EclipseUtil.getApplicationLauncher();
		assertNotNull(path);

		boolean match = false;
		String name = path.removeFileExtension().lastSegment();
		for (String launcherName : EclipseUtil.LAUNCHER_NAMES)
		{
			if (launcherName.equalsIgnoreCase(name))
			{
				match = true;
				break;
			}
		}
		assertTrue(match);
	}

	public void testGetProductVersion()
	{
		String productVersion = EclipseUtil.getProductVersion();
		Version version = Platform.getProduct().getDefiningBundle().getVersion();

		assertEquals(version.getMajor() + "." + version.getMinor() + "." + version.getMicro(), productVersion);
	}

	public void testGetTraceableItems()
	{
		Map<String, String> items = EclipseUtil.getTraceableItems();

		assertTrue(items.containsKey("com.aptana.core/debug"));
		assertTrue(items.containsKey("com.aptana.core/debug/builder"));
		assertTrue(items.containsKey("com.aptana.core/debug/logger"));
		assertTrue(items.containsKey("com.aptana.core/debug/shell"));
		assertTrue(items.containsKey("com.aptana.editor.common/perf/content_assist"));
		assertTrue(items.containsKey("com.aptana.rcp/debug"));
	}

	public void testGetCurrentDebuggableComponents()
	{
		String[] components = EclipseUtil.getCurrentDebuggableComponents();
		assertEquals(0, components.length);

		String[] testComponents = new String[] { "com.aptana.core/debug", "com.aptana.rcp/debug" };
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(CorePlugin.PLUGIN_ID);
		prefs.put(ICorePreferenceConstants.PREF_DEBUG_COMPONENT_LIST, StringUtil.join(",", testComponents));

		components = EclipseUtil.getCurrentDebuggableComponents();
		assertEquals(testComponents.length, components.length);
		for (int i = 0; i < testComponents.length; ++i)
		{
			assertEquals(testComponents[i], components[i]);
		}
	}
}
