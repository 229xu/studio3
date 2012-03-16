package com.aptana.buildpath.core;

import java.io.File;
import java.net.URI;

import junit.framework.TestCase;

public class BuildPathEntryTest extends TestCase
{

	public void testEqualsWithNullArg() throws Exception
	{
		String displayName = "name";
		File tmpFile = File.createTempFile("for_uri", null);
		URI path = tmpFile.toURI();
		BuildPathEntry entry = new BuildPathEntry(displayName, path);
		assertFalse(entry.equals(null));
	}

	public void testEquals() throws Exception
	{
		String displayName = "name";
		File tmpFile = File.createTempFile("for_uri", null);
		URI path = tmpFile.toURI();
		BuildPathEntry entry = new BuildPathEntry(displayName, path);
		assertTrue(entry.equals(entry));
		assertTrue(new BuildPathEntry(displayName, path).equals(entry));
		assertTrue(entry.equals(new BuildPathEntry(displayName, path)));
	}

	public void testGetters() throws Exception
	{
		String displayName = "name";
		File tmpFile = File.createTempFile("for_uri", null);
		URI path = tmpFile.toURI();
		BuildPathEntry entry = new BuildPathEntry(displayName, path);
		assertEquals(displayName, entry.getDisplayName());
		assertEquals(path, entry.getPath());
	}

	public void testIsSelected() throws Exception
	{
		String displayName = "name";
		File tmpFile = File.createTempFile("for_uri", null);
		URI path = tmpFile.toURI();
		BuildPathEntry entry = new BuildPathEntry(displayName, path);
		assertFalse(entry.isSelected());
		entry.setSelected(true);
		assertTrue(entry.isSelected());
		entry.setSelected(false);
		assertFalse(entry.isSelected());
	}
}
