package com.aptana.editor.html.parsing;

import com.aptana.editor.common.parsing.CompositeTokenScanner;
import com.aptana.editor.common.parsing.IScannerSwitchStrategy;
import com.aptana.editor.common.parsing.ScannerSwitchStrategy;
import com.aptana.editor.html.parsing.lexer.HTMLTokens;

public class HTMLScanner extends CompositeTokenScanner
{

	private static final String[] CSS_ENTER_TOKENS = new String[] { HTMLTokens.getTokenName(HTMLTokens.STYLE) };
	private static final String[] CSS_EXIT_TOKENS = new String[] { HTMLTokens.getTokenName(HTMLTokens.STYLE_END) };
	private static final String[] JS_ENTER_TOKENS = new String[] { HTMLTokens.getTokenName(HTMLTokens.SCRIPT) };
	private static final String[] JS_EXIT_TOKENS = new String[] { HTMLTokens.getTokenName(HTMLTokens.SCRIPT_END) };

	private static final IScannerSwitchStrategy CSS_STRATEGY = new ScannerSwitchStrategy(CSS_ENTER_TOKENS,
			CSS_EXIT_TOKENS);
	private static final IScannerSwitchStrategy JS_STRATEGY = new ScannerSwitchStrategy(JS_ENTER_TOKENS, JS_EXIT_TOKENS);

	public HTMLScanner()
	{
		super(new HTMLTokenScanner(), new IScannerSwitchStrategy[] { CSS_STRATEGY, JS_STRATEGY });
	}

	public short getTokenType(Object data)
	{
		IScannerSwitchStrategy strategy = getCurrentSwitchStrategy();
		if (strategy == null)
		{
			// the primary token scanner is being used
			return HTMLTokens.getToken(data.toString());
		}
		if (strategy == CSS_STRATEGY)
		{
			return HTMLTokens.STYLE;
		}
		if (strategy == JS_STRATEGY)
		{
			return HTMLTokens.SCRIPT;
		}
		return HTMLTokens.UNKNOWN;
	}
}
