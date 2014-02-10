/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.parsing;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.xml.core.parsing.ast.XMLElementNode;
import com.aptana.xml.core.parsing.ast.XMLNodeType;

public class XMLParserTest
{

	private XMLParser fParser;

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();

		fParser = new XMLParser();
	}

	@After
	public void tearDown() throws Exception
	{
		try
		{
			fParser = null;
		}
		finally
		{
//			super.tearDown();
		}
	}

	@Test
	public void testSelfClosing() throws Exception
	{
		String source = "<html/>\n";
		IParseNode root = parseTest(source, "<html></html>\n");
		assertEquals(1, root.getChildCount());
		IParseNode html = root.getFirstChild();
		assertElement(0, 6, "html", 0, 6, html);
	}

	@Test
	public void testAttributes() throws Exception
	{
		String source = "<html class=\"myClass\" id=\"myId\"/>\n";
		IParseNode root = parseTest(source, "<html></html>\n");
		assertEquals(1, root.getChildCount());
		XMLElementNode html = (XMLElementNode) root.getFirstChild();
		assertElement(0, 32, "html", 0, 32, html);
		IParseNodeAttribute[] attrs = html.getAttributes();
		assertEquals(2, attrs.length);
		assertEquals("myId", html.getAttributeValue("id"));
		assertEquals("myClass", html.getAttributeValue("class"));
	}

	private void assertElement(int start, int end, String name, int nameStart, int nameEnd, IParseNode elementNode)
	{
		assertEquals("startingOffset", start, elementNode.getStartingOffset());
		assertEquals("endingOffset", end, elementNode.getEndingOffset());
		INameNode nameNode = elementNode.getNameNode();
		assertEquals("name", name, nameNode.getName());
		assertEquals("nameStart", nameStart, nameNode.getNameRange().getStartingOffset());
		assertEquals("nameEnd", nameEnd, nameNode.getNameRange().getEndingOffset());
	}

	@Test
	public void testTags() throws Exception
	{
		String source = "<html><head></head><body><p>Text</p></html>\n";
		IParseNode root = parseTest(source, "<html><head></head><body><p></p></body></html>\n");
		assertEquals(1, root.getChildCount());
		IParseNode html = root.getFirstChild();
		assertElement(0, 5, "html", 0, 5, html);
		assertEquals(2, html.getChildCount());
		IParseNode head = html.getFirstChild();
		assertElement(6, 18, "head", 6, 11, head);
		assertEquals(0, head.getChildCount());
		IParseNode body = html.getChild(1);
		assertEquals(1, body.getChildCount());
		assertElement(19, 42, "body", 19, 24, body);
	}

	@Test
	public void testBlah() throws Exception
	{
		// @formatter:off
		String source = "<?xml version=\"1.0\"\n" + 
				"encoding=\"ISO-8859-1\"?>\n" +
				"<note>\n" +
				"<to>Tove</to>\n" +
				"<from>Jani</from>\n" +
				"<heading>Reminder</heading>\n" +
				"<body>Don't forget me this weekend!</body>\n" +
				"</note>";
		// @formatter:on
		IParseNode root = parseTest(source, "<note><to></to><from></from><heading></heading><body></body></note>\n");
		assertEquals(1, root.getChildCount());
		IParseNode note = root.getFirstChild();
		assertElement(44, 160, "note", 44, 49, note);
		assertEquals(4, note.getChildCount());
		IParseNode to = note.getFirstChild();
		assertElement(51, 63, "to", 51, 54, to);
		assertEquals(0, to.getChildCount());
		IParseNode from = note.getChild(1);
		assertEquals(0, from.getChildCount());
		assertElement(65, 81, "from", 65, 70, from);
	}

	@Test
	public void testComment() throws Exception
	{
		String source = "<!-- this is a comment -->";
		ParseState parseState = new ParseState(source);
		IParseRootNode rootNode = fParser.parse(parseState).getRootNode();

		assertEquals(1, rootNode.getChildCount());

		IParseNode[] comments = rootNode.getCommentNodes();
		assertEquals(1, comments.length);
		assertEquals(rootNode.getChild(0), comments[0]);
		assertEquals(XMLNodeType.COMMENT.getIndex(), comments[0].getNodeType());
		assertEquals(0, comments[0].getStartingOffset());
		assertEquals(source.length() - 1, comments[0].getEndingOffset());
		assertEquals(source, comments[0].getText());
	}

	@Test
	public void testCDATA() throws Exception
	{
		String source = "<![CDATA[<author>Appcelerator</author>]]>";
		ParseState parseState = new ParseState(source);
		IParseRootNode rootNode = fParser.parse(parseState).getRootNode();

		assertEquals(1, rootNode.getChildCount());

		IParseNode cdataNode = rootNode.getChild(0);
		assertEquals(XMLNodeType.CDATA.getIndex(), cdataNode.getNodeType());
		assertEquals(0, cdataNode.getStartingOffset());
		assertEquals(source.length() - 1, cdataNode.getEndingOffset());
		assertEquals(source, cdataNode.getText());
	}

	protected IParseNode parseTest(String source) throws Exception
	{
		return parseTest(source, source);
	}

	protected IParseNode parseTest(String source, String expected) throws Exception
	{
		ParseState parseState = new ParseState(source);
		IParseNode result = fParser.parse(parseState).getRootNode();

		StringBuilder text = new StringBuilder();
		IParseNode[] children = result.getChildren();
		for (IParseNode child : children)
		{
			text.append(child).append("\n");
		}
		assertEquals(expected, text.toString());

		return result;
	}
}
