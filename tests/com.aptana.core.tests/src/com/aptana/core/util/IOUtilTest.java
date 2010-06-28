package com.aptana.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import com.aptana.core.util.IOUtil;

public class IOUtilTest extends TestCase
{
	private Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	public void testRead() throws Exception
	{
		final byte[] b = new byte[8192];
		final int off = 0;
		final int len = 8192;
		final InputStream stream = context.mock(InputStream.class);
		context.checking(new Expectations()
		{
			{
				oneOf(stream).read(b, off, len);
				will(throwException(new IOException("")));
				oneOf(stream).close();
			}
		});
		IOUtil.read(stream);
		context.assertIsSatisfied();
	}

	public void testReadWithNullInputReturnsNull() throws Exception
	{
		assertNull(IOUtil.read(null));
	}

	public void testReadWithUnicodeCharacters() throws Exception
	{
		String stringwithUnicode = "�������";
		InputStream stream = new ByteArrayInputStream(stringwithUnicode.getBytes("UTF-8"));
		assertEquals(stringwithUnicode, IOUtil.read(stream, "UTF-8"));
	}

	public void testReadWithMultipleLinesWithSlashN() throws Exception
	{
		String multilineString = "line one\nline two\nline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals(multilineString, IOUtil.read(stream, "UTF-8"));
	}
	
	public void testReadWithMultipleLinesWithSlashRSlashN() throws Exception
	{
		String multilineString = "line one\r\nline two\r\nline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals("line one\nline two\nline three", IOUtil.read(stream, "UTF-8"));
	}
	
	public void testReadWithMultipleLinesWithSlashR() throws Exception
	{
		String multilineString = "line one\rline two\rline three";
		InputStream stream = new ByteArrayInputStream(multilineString.getBytes("UTF-8"));
		assertEquals("line one\nline two\nline three", IOUtil.read(stream, "UTF-8"));
	}
}
