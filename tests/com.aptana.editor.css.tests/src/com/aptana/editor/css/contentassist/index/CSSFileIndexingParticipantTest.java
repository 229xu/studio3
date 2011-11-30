package com.aptana.editor.css.contentassist.index;

import java.io.File;
import java.io.FileOutputStream;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.build.BuildContext;

public class CSSFileIndexingParticipantTest extends TestCase
{

	private CSSFileIndexingParticipant indexer;

	protected void setUp() throws Exception
	{
		super.setUp();
		indexer = new CSSFileIndexingParticipant();
	}

	protected void tearDown() throws Exception
	{
		indexer = null;
		super.tearDown();
	}

	public void testIsColor() throws Exception
	{
		assertTrue(CSSFileIndexingParticipant.isColor("#c9c9c9"));
		assertTrue(CSSFileIndexingParticipant.isColor("#ccc"));
		assertTrue(CSSFileIndexingParticipant.isColor("#000"));
		assertTrue(CSSFileIndexingParticipant.isColor("#000000"));

		assertFalse(CSSFileIndexingParticipant.isColor("#g00000"));

		assertFalse(CSSFileIndexingParticipant.isColor("#0"));
		assertFalse(CSSFileIndexingParticipant.isColor("#00"));
		assertFalse(CSSFileIndexingParticipant.isColor("#0000"));
		assertFalse(CSSFileIndexingParticipant.isColor("#00000"));
		assertFalse(CSSFileIndexingParticipant.isColor("#0000000"));

		assertFalse(CSSFileIndexingParticipant.isColor("#a"));
		assertFalse(CSSFileIndexingParticipant.isColor("#aa"));
		assertFalse(CSSFileIndexingParticipant.isColor("#aaaa"));
		assertFalse(CSSFileIndexingParticipant.isColor("#aaaaa"));
		assertFalse(CSSFileIndexingParticipant.isColor("#aaaaaaa"));
	}

	public void testDetectTaskTagWithUnicodeCharacters() throws Exception
	{
		File tmpDir = null;
		try
		{
			// @formatter:off
			String src = 
			"body {\n" +
			"	/* TODO: Привет */\n" +
			"}\n";
			// @formatter:on

			// Generate some files to index!
			tmpDir = new File(System.getProperty("java.io.tmpdir"), "testIndex_" + System.currentTimeMillis());
			tmpDir.mkdirs();

			File coffeeFile = new File(tmpDir, "index_me.css");
			IOUtil.write(new FileOutputStream(coffeeFile), src);
			IFileStore fileStore = EFS.getStore(coffeeFile.toURI());
			BuildContext context = new FileStoreBuildContext(fileStore);

			Index index = IndexManager.getInstance().getIndex(tmpDir.toURI());
			indexer.index(context, index, new NullProgressMonitor());
		}
		finally
		{
			// Clean up the generated files!
			FileUtil.deleteRecursively(tmpDir);
		}
	}
}
