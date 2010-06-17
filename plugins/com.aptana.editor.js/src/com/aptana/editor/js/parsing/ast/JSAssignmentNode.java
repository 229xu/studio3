package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.parsing.ast.IParseNode;

public class JSAssignmentNode extends JSNode
{
	private Symbol _operator;
	
	/**
	 * JSAssignmentNode
	 * 
	 * @param left
	 * @param assignOperator
	 * @param right
	 */
	public JSAssignmentNode(JSNode left, Symbol assignOperator, JSNode right)
	{
		this._operator = assignOperator;
		this.start = left.getStart();
		this.end = right.getEnd();

		short type = DEFAULT_TYPE;
		JSTokenType token = JSTokenType.get((String) assignOperator.value);
		switch (token)
		{
			case EQUAL:
				type = JSNodeTypes.ASSIGN;
				break;
			case PLUS_EQUAL:
				type = JSNodeTypes.ADD_AND_ASSIGN;
				break;
			case GREATER_GREATER_GREATER_EQUAL:
				type = JSNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN;
				break;
			case AMPERSAND_EQUAL:
				type = JSNodeTypes.BITWISE_AND_AND_ASSIGN;
				break;
			case PIPE_EQUAL:
				type = JSNodeTypes.BITWISE_OR_AND_ASSIGN;
				break;
			case CARET_EQUAL:
				type = JSNodeTypes.BITWISE_XOR_AND_ASSIGN;
				break;
			case FORWARD_SLASH_EQUAL:
				type = JSNodeTypes.DIVIDE_AND_ASSIGN;
				break;
			case PERCENT_EQUAL:
				type = JSNodeTypes.MOD_AND_ASSIGN;
				break;
			case STAR_EQUAL:
				type = JSNodeTypes.MULTIPLY_AND_ASSIGN;
				break;
			case LESS_LESS_EQUAL:
				type = JSNodeTypes.SHIFT_LEFT_AND_ASSIGN;
				break;
			case GREATER_GREATER_EQUAL:
				type = JSNodeTypes.SHIFT_RIGHT_AND_ASSIGN;
				break;
			case MINUS_EQUAL:
				type = JSNodeTypes.SUBTRACT_AND_ASSIGN;
				break;
		}
		setType(type);

		setChildren(new JSNode[] { left, right });
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#getLocationType(int)
	 */
	@Override
	LocationType getLocationType(int offset)
	{
		LocationType result = LocationType.UNKNOWN;
		
		if (this.contains(offset))
		{
			for (IParseNode child : this)
			{
				if (child.contains(offset))
				{
					if (child instanceof JSNode)
					{
						result = ((JSNode) child).getLocationType(offset);
					}
					
					break;
				}
			}
			
			if (result == LocationType.UNKNOWN)
			{
				if (offset < this._operator.getStart())
				{
					result = LocationType.NONE;
				}
				else if (this._operator.getEnd() <= offset)
				{
					result = LocationType.IN_GLOBAL;
				}
			}
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		String operator = "???"; //$NON-NLS-1$
		switch (getNodeType())
		{
			case JSNodeTypes.ASSIGN:
				operator = "="; //$NON-NLS-1$
				break;
			case JSNodeTypes.ADD_AND_ASSIGN:
				operator = "+="; //$NON-NLS-1$
				break;
			case JSNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN:
				operator = ">>>="; //$NON-NLS-1$
				break;
			case JSNodeTypes.BITWISE_AND_AND_ASSIGN:
				operator = "&="; //$NON-NLS-1$
				break;
			case JSNodeTypes.BITWISE_OR_AND_ASSIGN:
				operator = "|="; //$NON-NLS-1$
				break;
			case JSNodeTypes.BITWISE_XOR_AND_ASSIGN:
				operator = "^="; //$NON-NLS-1$
				break;
			case JSNodeTypes.DIVIDE_AND_ASSIGN:
				operator = "/="; //$NON-NLS-1$
				break;
			case JSNodeTypes.MOD_AND_ASSIGN:
				operator = "%="; //$NON-NLS-1$
				break;
			case JSNodeTypes.MULTIPLY_AND_ASSIGN:
				operator = "*="; //$NON-NLS-1$
				break;
			case JSNodeTypes.SHIFT_LEFT_AND_ASSIGN:
				operator = "<<="; //$NON-NLS-1$
				break;
			case JSNodeTypes.SHIFT_RIGHT_AND_ASSIGN:
				operator = ">>="; //$NON-NLS-1$
				break;
			case JSNodeTypes.SUBTRACT_AND_ASSIGN:
				operator = "-="; //$NON-NLS-1$
				break;

		}
		IParseNode[] children = getChildren();
		text.append(children[0]);
		text.append(" ").append(operator).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
		text.append(children[1]);

		this.appendSemicolon(text);

		return text.toString();
	}
}
