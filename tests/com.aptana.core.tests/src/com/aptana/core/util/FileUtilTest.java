/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

public class FileUtilTest extends TestCase
{

	public void testIsDirectoryAccessible()
	{
		assertFalse(FileUtil.isDirectoryAccessible(null));
		assertTrue(FileUtil.isDirectoryAccessible(new File("/tmp")));
		assertFalse(FileUtil.isDirectoryAccessible(new File("/tmp2")));
	}

	public void testCompressPath()
	{
		String path = "c:/Documents and Settings/username/My Documents/workspace/whatever.txt";
		assertEquals("c:/.../username/My Documents/workspace/whatever.txt", FileUtil.compressPath(path, 50));
		assertEquals(path, FileUtil.compressPath(path, 100));
	}

	public void testCompressPathForNull()
	{
		assertNull(FileUtil.compressPath(null, 10));
	}

	public void testCompressPathNoSlash()
	{
		String path = "a_path_with_no_slash";
		assertEquals(path, FileUtil.compressPath(path, 10));
	}

	public void testCompressPathSingleSlash()
	{
		String path = "test/compress_path";
		assertEquals(path, FileUtil.compressPath(path, 10));
		assertEquals(path, FileUtil.compressPath(path, 15));
	}

	public void testCompressPathLastPathLongerThanDesiredLength()
	{
		String path = "test/compress/a_really_long_last_path";
		assertEquals("test/.../a_really_long_last_path", FileUtil.compressPath(path, 30));
	}

	public void testCompressLeadingPath()
	{
		String path = "c:/Documents and Settings/username/My Documents/workspace/whatever.txt";
		assertEquals(".../username/My Documents/workspace/whatever.txt", FileUtil.compressLeadingPath(path, 50));
		assertEquals(path, FileUtil.compressLeadingPath(path, 100));
	}

	public void testCompressLeadingPathForNull()
	{
		assertNull(FileUtil.compressLeadingPath(null, 10));
	}

	public void testCompressLeadingPathLastPathLongerThanDesiredLength()
	{
		String path = "test/a_really_long_last_path";
		assertEquals(path, FileUtil.compressLeadingPath(path, 20));
	}

	public void testGetExtension()
	{
		String filename = "test.html";
		assertEquals("html", FileUtil.getExtension(filename));
	}

	public void testEmptyExtension()
	{
		String filename = "test";
		assertEquals(StringUtil.EMPTY, FileUtil.getExtension(filename));
	}

	public void testGetExtensionForNull()
	{
		assertNull(FileUtil.getExtension(null));
	}

	public void testGetRandomFileName()
	{
		String prefix = "test";
		String suffix = ".html";

		String filename = FileUtil.getRandomFileName(prefix, suffix);
		assertTrue(filename.startsWith(prefix));
		assertTrue(filename.endsWith(suffix));

		int beginIndex = filename.indexOf(prefix) + prefix.length();
		int endIndex = filename.lastIndexOf(suffix);
		try
		{
			Long.parseLong(filename.substring(beginIndex, endIndex));
		}
		catch (NumberFormatException e)
		{
			fail();
		}
	}

	public void testGetRandomFileNameWithNullPrefixSuffix()
	{
		String filename = FileUtil.getRandomFileName(null, null);
		try
		{
			Long.parseLong(filename);
		}
		catch (NumberFormatException e)
		{
			fail();
		}
	}

	public void testDeleteRecursively() throws Exception
	{
		File rootDir = File.createTempFile("deleteTest", Long.toString(System.nanoTime()));
		rootDir.delete();
		rootDir.mkdir();

		File level2Dir = new File(rootDir, "folder");
		level2Dir.mkdir();

		int count = 5;
		File[] files = new File[count];
		for (int i = 0; i < count; ++i)
		{
			files[i] = File.createTempFile("file", Long.toString(System.nanoTime()), level2Dir);
		}

		FileUtil.deleteRecursively(rootDir);

		assertFalse(rootDir.exists());
		assertFalse(level2Dir.exists());
		for (File file : files)
		{
			assertFalse(file.exists());
		}
	}

	public void testDeleteRecursivelyForNull()
	{
		assertFalse(FileUtil.deleteRecursively(null));
	}

	public void testGatherFilesFromCommandLineArguments()
	{
		String[] arguments = { "-os", "macosx", "-ws", "cocoa", "-arch", "x86", "-debug", "-keyring",
				"/Users/ingo/.eclipse_keyring", "-consoleLog", "-showlocation" };
		List<File> files = FileUtil.gatherFilesFromCommandLineArguments(arguments);
		assertEquals(0, files.size());

		try
		{
			File f = File.createTempFile("testGatherFilesFromCommandLineArguments", ".txt");
			f.createNewFile();
			f.deleteOnExit();

			arguments = new String[] { "-os", "win32", "-ws", "win32", "-arch", "x86", "-debug", f.getAbsolutePath() };
			files = FileUtil.gatherFilesFromCommandLineArguments(arguments);

			assertEquals(1, files.size());
			assertEquals(f.getAbsolutePath(), files.get(0).getAbsolutePath());
		}
		catch (IOException e)
		{
			fail("Unable to test parsing of file name from command line");
		}
	}

	public void testCountFilesWithNullArg()
	{
		assertEquals(0, FileUtil.countFiles(null));
	}

	public void testCountFilesWithSingleFile() throws Exception
	{
		File file = File.createTempFile("delete_me", null);
		file.deleteOnExit();
		assertEquals(1, FileUtil.countFiles(file));
	}

	public void testCountFilesWithDirectory() throws Exception
	{
		File dir = new File(FileUtil.getTempDirectory().toOSString(), "count_dir_" + System.currentTimeMillis());
		try
		{
			dir.mkdirs();
			int fileCount = 10;
			for (int i = 0; i < fileCount; i++)
			{
				new File(dir, Integer.toString(i)).createNewFile();
			}
			assertEquals(fileCount, FileUtil.countFiles(dir));
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}

	public void testCountFilesWithMultipleDirectories() throws Exception
	{
		File dir = new File(FileUtil.getTempDirectory().toOSString(), "count_dir_" + System.currentTimeMillis());
		try
		{
			dir.mkdirs();
			int dirCount = 5;
			int fileCount = 10;
			for (int j = 0; j < dirCount; j++)
			{
				File subDir = new File(dir, "dir_" + Integer.toString(j));
				subDir.mkdirs();
				for (int i = 0; i < fileCount; i++)
				{
					new File(subDir, Integer.toString(i)).createNewFile();
				}
			}

			assertEquals(dirCount * fileCount, FileUtil.countFiles(dir));
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}

	// TODO Add test for countFiles with symlink loop?

	public void testCountFilesWithMultipleDirectoriesAndSymlinkLoop() throws Exception
	{
		File dir = new File(FileUtil.getTempDirectory().toOSString(), "count_dir_" + System.currentTimeMillis());
		try
		{
			dir.mkdirs();
			int dirCount = 5;
			int fileCount = 10;
			for (int j = 0; j < dirCount; j++)
			{
				File subDir = new File(dir, "dir_" + Integer.toString(j));
				subDir.mkdirs();
				for (int i = 0; i < fileCount; i++)
				{
					new File(subDir, Integer.toString(i)).createNewFile();
				}

				IStatus status = ProcessUtil.runInBackground("ln", Path.fromOSString(subDir.getAbsolutePath()), "-s",
						dir.getAbsolutePath(), "symlink");
				assertTrue(status.isOK());
			}
			assertEquals(dirCount * (fileCount + 1), FileUtil.countFiles(dir));
		}
		finally
		{
			FileUtil.deleteRecursively(dir);
		}
	}
}
