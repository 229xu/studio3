package com.aptana.editor.html.parsing.lexer;

import java.util.HashMap;
import java.util.Map;

public class HTMLTokens
{
	public static final short UNKNOWN = -1;
	public static final short EOF = 0;
	public static final short STYLE = 1;
	public static final short SCRIPT = 2;
	public static final short START_TAG = 3;
	public static final short TEXT = 4;
	public static final short END_TAG = 5;
	public static final short XML_DECL = 6;
	public static final short STRING = 7;
	public static final short COMMENT = 8;
	public static final short DOCTYPE = 9;

	private static final short MAXIMUM = 9;

	@SuppressWarnings("nls")
	private static final String[] NAMES = { "EOF", "STYLE", "SCRIPT", "START_TAG", "TEXT", "END_TAG", "XML_DECL",
			"STRING", "COMMENT", "DOCTYPE" };
	private static final String NAME_UNKNOWN = "UNKNOWN"; //$NON-NLS-1$

	private static Map<String, Short> nameIndexMap;

	public static String getTokenName(short token)
	{
		init();
		if (token < 0 || token > MAXIMUM)
		{
			return NAME_UNKNOWN;
		}
		return NAMES[token];
	}

	public static short getToken(String tokenName)
	{
		init();
		Short token = nameIndexMap.get(tokenName);
		return (token == null) ? UNKNOWN : token;
	}

	private static void init()
	{
		if (nameIndexMap == null)
		{
			nameIndexMap = new HashMap<String, Short>();
			short index = 0;
			for (String name : NAMES)
			{
				nameIndexMap.put(name, index++);
			}
		}
	}

	private HTMLTokens()
	{
	}
}
