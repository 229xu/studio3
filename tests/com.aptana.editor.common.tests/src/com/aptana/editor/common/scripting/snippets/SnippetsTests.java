package com.aptana.editor.common.scripting.snippets;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SnippetsTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(SnippetsTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(SnippetTemplateTranslatorTest.class);
		//$JUnit-END$
		return suite;
	}

}
