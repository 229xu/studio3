package com.aptana.editor.css.parsing.ast;

import java.util.Arrays;
import java.util.List;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public class CSSSimpleSelectorNode extends CSSNode
{

	private String fTypeSelector;

	public CSSSimpleSelectorNode(Symbol typeSelector)
	{
		this(typeSelector, new CSSAttributeSelectorNode[0]);
	}

	public CSSSimpleSelectorNode(CSSAttributeSelectorNode[] attributeSelectors)
	{
		this(null, attributeSelectors);
	}

	public CSSSimpleSelectorNode(Symbol typeSelector, CSSAttributeSelectorNode[] attributeSelectors)
	{
		fTypeSelector = (typeSelector == null) ? null : typeSelector.value.toString();
		setChildren(attributeSelectors);

		if (typeSelector == null)
		{
			if (attributeSelectors.length > 0)
			{
				this.start = attributeSelectors[0].getStart();
				this.end = attributeSelectors[attributeSelectors.length - 1].getEnd();
			}
		}
		else
		{
			this.start = typeSelector.getStart();
			if (attributeSelectors.length == 0)
			{
				this.end = typeSelector.getEnd();
			}
			else
			{
				this.end = attributeSelectors[attributeSelectors.length - 1].getEnd();
			}
		}
	}

	public CSSAttributeSelectorNode[] getAttributeSelectors()
	{
		List<IParseNode> list = Arrays.asList(getChildren());
		return list.toArray(new CSSAttributeSelectorNode[list.size()]);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CSSSimpleSelectorNode))
		{
			return false;
		}
		CSSSimpleSelectorNode other = (CSSSimpleSelectorNode) obj;
		return (fTypeSelector == null ? other.fTypeSelector == null : fTypeSelector.equals(other.fTypeSelector))
				&& Arrays.equals(getAttributeSelectors(), other.getAttributeSelectors());
	}

	@Override
	public int hashCode()
	{
		return 31 * (fTypeSelector == null ? 0 : fTypeSelector.hashCode()) + Arrays.hashCode(getAttributeSelectors());
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		if (fTypeSelector != null)
		{
			text.append(fTypeSelector);
		}
		text.append(super.toString());
		return text.toString();
	}
}
