package com.aptana.editor.js.parsing.lexer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.aptana.parsing.lexer.ITypePredicate;

public enum JSTokenType implements ITypePredicate
{
	UNDEFINED("undefined.html"), //$NON-NLS-1$
	KEYWORD("keyword.operator.js"), //$NON-NLS-1$
	SUPPORT_FUNCTION("support.function.js"), //$NON-NLS-1$
	EVENT_HANDLER_FUNCTION("support.function.event-handler.js"), //$NON-NLS-1$
	DOM_FUNCTION("support.function.dom.js"), //$NON-NLS-1$
	FIREBUG_FUNCTION("support.function.js.firebug"), //$NON-NLS-1$
	OPERATOR("keyword.operator.js"), //$NON-NLS-1$
	SUPPORT_CONSTANT("support.constant.js"), //$NON-NLS-1$
	DOM_CONSTANTS("support.constant.dom.js"), //$NON-NLS-1$
	SOURCE("source.js"), //$NON-NLS-1$
	CONTROL_KEYWORD("keyword.control.js"), //$NON-NLS-1$
	STORAGE_TYPE("storage.type.js"), //$NON-NLS-1$
	STORAGE_MODIFIER("storage.modifier.js"), //$NON-NLS-1$
	SUPPORT_CLASS("support.class.js"), //$NON-NLS-1$
	SUPPORT_DOM_CONSTANT("support.constant.dom.js"), //$NON-NLS-1$
	TRUE("constant.language.boolean.true.js"), //$NON-NLS-1$
	FALSE("constant.language.boolean.false.js"), //$NON-NLS-1$
	NULL("constant.language.null.js"), //$NON-NLS-1$
	CONSTANT("constant.language.js"), //$NON-NLS-1$
	VARIABLE("variable.language.js"), //$NON-NLS-1$
	OTHER_KEYWORD("keyword.other.js"), //$NON-NLS-1$
	SEMICOLON("punctuation.terminator.statement.js"), //$NON-NLS-1$
	PARENTHESIS("meta.brace.round.js"), //$NON-NLS-1$
	BRACKET("meta.brace.square.js"), //$NON-NLS-1$
	CURLY_BRACE("meta.brace.curly.js"), //$NON-NLS-1$
	COMMA("meta.delimiter.object.comma.js"), //$NON-NLS-1$
	NUMBER("constant.numeric.js"); //$NON-NLS-1$

	private static final Map<String, JSTokenType> NAME_MAP;
	private String _scope;

	/**
	 * static
	 */
	static
	{
		NAME_MAP = new HashMap<String, JSTokenType>();

		for (JSTokenType type : EnumSet.allOf(JSTokenType.class))
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
	public static final JSTokenType get(String scope)
	{
		return (NAME_MAP.containsKey(scope)) ? NAME_MAP.get(scope) : UNDEFINED;
	}

	/**
	 * CSSTokenTypes
	 * 
	 * @param scope
	 */
	private JSTokenType(String scope)
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
