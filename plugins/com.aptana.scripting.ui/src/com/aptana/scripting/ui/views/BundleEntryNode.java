package com.aptana.scripting.ui.views;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleEntry;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.MenuElement;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class BundleEntryNode extends BaseNode
{
	private enum Property
	{
		NAME
	}

	private static final Image BUNDLE_ENTRY_ICON = ScriptingUIPlugin.getImage("icons/bundle_entry.png"); //$NON-NLS-1$
	private BundleEntry _entry;

	/**
	 * BundleEntryNode
	 * 
	 * @param entry
	 */
	public BundleEntryNode(BundleEntry entry)
	{
		this._entry = entry;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		List<Object> result = new LinkedList<Object>();

		// add bundle element that contribute to this bundle
		for (BundleElement bundle : this._entry.getBundles())
		{
			result.add(new BundleNode(bundle));
		}

		// divide commands into commands and snippets
		List<CommandElement> commands = new LinkedList<CommandElement>();
		List<SnippetElement> snippets = new LinkedList<SnippetElement>();

		for (CommandElement element : this._entry.getCommands())
		{
			if (element instanceof SnippetElement)
			{
				snippets.add((SnippetElement) element);
			}
			else
			{
				commands.add(element);
			}
		}

		// add visible commands
		if (commands.size() > 0)
		{
			result.add(new CommandsNode(commands.toArray(new CommandElement[commands.size()])));
		}

		// add visible snippets
		if (snippets.size() > 0)
		{
			result.add(new SnippetsNode(snippets.toArray(new CommandElement[snippets.size()])));
		}

		// add visible menus
		MenuElement[] menus = this._entry.getMenus();

		if (menus != null && menus.length > 0)
		{
			result.add(new MenusNode(menus));
		}

		return result.toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return BUNDLE_ENTRY_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return this._entry.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor nameProperty = new PropertyDescriptor(Property.NAME, "Name");

		return new IPropertyDescriptor[] { nameProperty };
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		Object result = null;

		if (id instanceof Property)
		{
			if (((Property) id) == Property.NAME)
			{
				result = this._entry.getName();
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		boolean result = false;

		for (BundleElement bundle : this._entry.getBundles())
		{
			if (bundle.hasCommands() || bundle.hasMenus())
			{
				result = true;
				break;
			}
		}

		return result;
	}
}
