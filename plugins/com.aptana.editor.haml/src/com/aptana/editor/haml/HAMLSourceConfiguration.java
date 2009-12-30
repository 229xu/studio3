/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.haml;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.ISourceViewerConfiguration;
import com.aptana.editor.common.TextUtils;
import com.aptana.editor.common.text.rules.ISubPartitionScanner;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.editor.ruby.IRubyConstants;
import com.aptana.editor.ruby.RubySourceConfiguration;

/**
 * @author Chris Williams
 * @author Max Stepanov
 */
public class HAMLSourceConfiguration implements IPartitioningConfiguration, ISourceViewerConfiguration
{

	public final static String PREFIX = "__haml_"; //$NON-NLS-1$
	public final static String DEFAULT = PREFIX + IDocument.DEFAULT_CONTENT_TYPE;
	public final static String DOCTYPE = PREFIX + "doctype"; //$NON-NLS-1$
	public final static String HAML_RUBY = PREFIX + "ruby"; //$NON-NLS-1$
	public final static String HTML_COMMENT = PREFIX + "html_comment"; //$NON-NLS-1$
	public final static String HAML_COMMENT = PREFIX + "haml_comment"; //$NON-NLS-1$

	public static final String[] CONTENT_TYPES = new String[] { DEFAULT, HTML_COMMENT, HAML_COMMENT, DOCTYPE, HAML_RUBY };

	private static final String[][] TOP_CONTENT_TYPES = new String[][] { { IHAMLConstants.CONTENT_TYPE_HAML },
			{ IHAMLConstants.CONTENT_TYPE_HAML, IRubyConstants.CONTENT_TYPE_RUBY } };

	private IPredicateRule[] partitioningRules;

	private RuleBasedScanner fCommentScanner;
	private RuleBasedScanner fCodeScanner;

	private static HAMLSourceConfiguration instance;

	public static HAMLSourceConfiguration getDefault()
	{
		if (instance == null)
		{
			instance = new HAMLSourceConfiguration();
		}
		return instance;
	}

	private HAMLSourceConfiguration()
	{
		IToken ruby = new Token(HAML_RUBY);
		partitioningRules = new IPredicateRule[] {
				// FIXME What about indented text getting wrapped too?
				// FIXME These have to occur at beginning of line (only can be preceded by spaces!)
				new EndOfLineRule("/", new Token(HTML_COMMENT)), //$NON-NLS-1$
				new EndOfLineRule("-#", new Token(HAML_COMMENT)), //$NON-NLS-1$
				new EndOfLineRule("!!!", new Token(DOCTYPE)), //$NON-NLS-1$
				// FIXME Not sure I'm setting up the language transition properly here
				// Ruby Single-liners
				new EndOfLineRule("=", ruby), //$NON-NLS-1$
				new EndOfLineRule("-", ruby), //$NON-NLS-1$
				new EndOfLineRule("~", ruby), //$NON-NLS-1$
				// String Interpolation
				new SingleLineRule("#{", "}", ruby, '\\'), //$NON-NLS-1$ //$NON-NLS-2$
				// Object
				new SingleLineRule("[", "]", ruby), //$NON-NLS-1$ //$NON-NLS-2$
				// Attributes
				new SingleLineRule("{", "}", ruby), //$NON-NLS-1$ //$NON-NLS-2$
		};
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getContentTypes()
	 */
	public String[] getContentTypes()
	{
		return TextUtils.combine(new String[][] { CONTENT_TYPES, RubySourceConfiguration.CONTENT_TYPES });
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ITopContentTypesProvider#getTopContentTypes()
	 */
	public String[][] getTopContentTypes()
	{
		return TOP_CONTENT_TYPES;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getPartitioningRules()
	 */
	public IPredicateRule[] getPartitioningRules()
	{
		return partitioningRules;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#createSubPartitionScanner()
	 */
	public ISubPartitionScanner createSubPartitionScanner()
	{
		return new HAMLSubPartitionScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitioningConfiguration#getDocumentDefaultContentType()
	 */
	public String getDocumentContentType(String contentType)
	{
		if (contentType.startsWith(PREFIX))
		{
			return IHAMLConstants.CONTENT_TYPE_HAML;
		}
		String result = RubySourceConfiguration.getDefault().getDocumentContentType(contentType);
		if (result != null)
		{
			return result;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.ISourceViewerConfiguration#setupPresentationReconciler(org.eclipse.jface.text.presentation
	 * .PresentationReconciler, org.eclipse.jface.text.source.ISourceViewer)
	 */
	public void setupPresentationReconciler(PresentationReconciler reconciler, ISourceViewer sourceViewer)
	{
		RubySourceConfiguration.getDefault().setupPresentationReconciler(reconciler, sourceViewer);

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		reconciler.setDamager(dr, DEFAULT);
		reconciler.setRepairer(dr, DEFAULT);

		dr = new DefaultDamagerRepairer(getWordScanner());
		reconciler.setDamager(dr, HTML_COMMENT);
		reconciler.setRepairer(dr, HTML_COMMENT);
	}

	protected ITokenScanner getCodeScanner()
	{
		if (fCodeScanner == null)
		{
			fCodeScanner = new HAMLScanner();
		}
		return fCodeScanner;
	}

	protected ITokenScanner getWordScanner()
	{
		if (fCommentScanner == null)
		{
			fCommentScanner = new RuleBasedScanner();
			fCommentScanner.setDefaultReturnToken(getToken("comment.block.haml")); //$NON-NLS-1$
		}
		return fCommentScanner;
	}

	protected IToken getToken(String name)
	{
		return getThemeManager().getToken(name);
	}

	protected IThemeManager getThemeManager()
	{
		return CommonEditorPlugin.getDefault().getThemeManager();
	}
}
