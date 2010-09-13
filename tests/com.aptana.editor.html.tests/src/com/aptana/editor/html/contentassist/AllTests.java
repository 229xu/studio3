package com.aptana.editor.html.contentassist;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.html.contentassist");
		// $JUnit-BEGIN$
		suite.addTestSuite(ContentAssistFineLocationTests.class);
		suite.addTestSuite(ContentAssistCoarseLocationTests.class);
		suite.addTestSuite(HTMLContentAssistProcessorTest.class);
		suite.addTestSuite(MetadataTests.class);
		// $JUnit-END$
		return suite;
	}

}
