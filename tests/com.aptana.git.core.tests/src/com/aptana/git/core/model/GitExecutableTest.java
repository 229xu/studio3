package com.aptana.git.core.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;

@SuppressWarnings("nls")
public class GitExecutableTest extends TestCase
{
	// FIXME This certainly won't work on Windows!
	private static final String FAKE_GIT_1_5 = "fake_git_1.5.sh";
	private static final String FAKE_GIT_1_6 = "fake_git_1.6.sh";

	@Override
	protected void tearDown() throws Exception
	{
		GitExecutable.fgExecutable = null;
		IEclipsePreferences prefs = new InstanceScope().getNode(GitPlugin.getPluginId());
		prefs.remove(IPreferenceConstants.GIT_EXECUTABLE_PATH);
		prefs.flush();
		super.tearDown();
	}

	public void testAcceptBinary() throws Exception
	{
		URL url = makeURLForExecutableFile(new Path(FAKE_GIT_1_5));

		assertFalse(GitExecutable.acceptBinary(Path.fromOSString(url.getPath())));

		url = makeURLForExecutableFile(new Path(FAKE_GIT_1_6));
		assertTrue(GitExecutable.acceptBinary(Path.fromOSString(url.getPath())));
	}

	protected URL makeURLForExecutableFile(IPath path) throws IOException
	{
		URL url = FileLocator.find(GitPlugin.getDefault().getBundle(), path, null);
		url = FileLocator.toFileURL(url);
		File file = new File(url.getPath());
		file.setExecutable(true, false);
		return url;
	}

	// Test that it picks up pref value for location above all else
	public void testUsesPrefLocationFirst() throws Throwable
	{
		URL url = makeURLForExecutableFile(new Path(FAKE_GIT_1_6));

		IEclipsePreferences prefs = new InstanceScope().getNode(GitPlugin.getPluginId());
		prefs.put(IPreferenceConstants.GIT_EXECUTABLE_PATH, url.getPath());
		prefs.flush();

		GitExecutable executable = GitExecutable.instance();
		assertEquals(url.getPath(), executable.path().toPortableString());
	}

	public void testDetectsInStandardLocation() throws Throwable
	{
		GitExecutable executable = GitExecutable.instance();
		// FIXME This is hacky for test, but basically on my machine I have it in /usr.local.bin.git, while test box has
		// /usr/bin/git
		String expectedLocation = "/usr/bin/git";
		if (new File("/usr/local/bin/git").exists())
		{
			expectedLocation = "/usr/local/bin/git";
		}
		assertEquals(expectedLocation, executable.path().toPortableString());
	}

	// Test that it reacts to changes in pref location
	public void testReactsToPrefLocationChanges() throws Throwable
	{
		testUsesPrefLocationFirst();

		IEclipsePreferences prefs = new InstanceScope().getNode(GitPlugin.getPluginId());
		prefs.remove(IPreferenceConstants.GIT_EXECUTABLE_PATH);
		prefs.flush();

		testDetectsInStandardLocation();
	}
}
