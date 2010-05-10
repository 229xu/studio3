package com.aptana.editor.html.parsing.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

public enum HTMLTokenType implements ITypePredicate
{
	UNDEFINED("undefined.html"), //$NON-NLS-1$
	DOUBLE_QUOTED_STRING("string.quoted.double.html"), //$NON-NLS-1$
	SINGLE_QUOTED_STRING("string.quoted.single.html"), //$NON-NLS-1$
	ATTRIBUTE("entity.other.attribute-name.html"), //$NON-NLS-1$
	ID("entity.other.attribute-name.id.html"), //$NON-NLS-1$
	CLASS("entity.other.attribute-name.class.html"), //$NON-NLS-1$
	META("meta.tag.other.html"), //$NON-NLS-1$
	SCRIPT("entity.name.tag.script.html"), //$NON-NLS-1$
	STYLE("entity.name.tag.style.html"), //$NON-NLS-1$
	STRUCTURE_TAG("entity.name.tag.structure.any.html"), //$NON-NLS-1$
	BLOCK_TAG("entity.name.tag.block.any.html"), //$NON-NLS-1$
	INLINE_TAG("entity.name.tag.inline.any.html"), //$NON-NLS-1$
	TAG_END("punctuation.definition.tag.end.html"), //$NON-NLS-1$
	EQUAL("punctuation.separator.key-value.html"), //$NON-NLS-1$
	TAG_START("punctuation.definition.tag.begin.html"), //$NON-NLS-1$
	TEXT("text"); //$NON-NLS-1$

	private static final Map<String, HTMLTokenType> NAME_MAP;
	private String _scope;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, HTMLTokenType>();

		for (HTMLTokenType type : EnumSet.allOf(HTMLTokenType.class))
		{
			NAME_MAP.put(type.getScope(), type);
		}
	}

	/**
	 * get
	 * 
	 * @param scope
	 * @return
	 */
	public static final HTMLTokenType get(String scope)
	{
		return (NAME_MAP.containsKey(scope)) ? NAME_MAP.get(scope) : UNDEFINED;
	}

	/**
	 * CSSTokenTypes
	 * 
	 * @param scope
	 */
	private HTMLTokenType(String scope)
	{
		this._scope = scope;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getScope()
	{
		return this._scope;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.lexer.ITypePredicate#isDefined()
	 */
	@Override
	public boolean isDefined()
	{
		return (this != UNDEFINED);
	}

	/**
	 * toString
	 */
	public String toString()
	{
		return this.getScope();
	}
}
