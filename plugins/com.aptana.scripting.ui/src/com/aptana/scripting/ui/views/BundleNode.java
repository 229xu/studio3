package com.aptana.scripting.ui.views;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class BundleNode extends BaseNode
{
	private enum Property
	{
		NAME, PATH
	}

	private static final Image BUNDLE_ICON = ScriptingUIPlugin.getImage("icons/bundle_directory.png"); //$NON-NLS-1$
	private BundleElement _bundle;

	/**
	 * BundleNode
	 * 
	 * @param bundle
	 */
	public BundleNode(BundleElement bundle)
	{
		this._bundle = bundle;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		CommandsNode commands = new CommandsNode(this._bundle);
		SnippetsNode snippets = new SnippetsNode(this._bundle);
		MenusNode menus = new MenusNode(this._bundle);
		List<Object> items = new LinkedList<Object>();

		if (commands.hasChildren())
		{
			items.add(commands);
		}
		if (snippets.hasChildren())
		{
			items.add(snippets);
		}
		if (menus.hasChildren())
		{
			items.add(menus);
		}

		return items.toArray(new Object[items.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return BUNDLE_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		File file = new File(this._bundle.getPath());

		return file.getAbsolutePath();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor nameProperty = new PropertyDescriptor(Property.NAME, "Name");
		PropertyDescriptor pathProperty = new PropertyDescriptor(Property.PATH, "Path");

		return new IPropertyDescriptor[] { nameProperty, pathProperty };
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
			switch ((Property) id)
			{
				case NAME:
					result = this._bundle.getDisplayName();
					break;

				case PATH:
					result = this._bundle.getPath();
					break;

				default:
					break;
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
		return this._bundle.hasCommands() || this._bundle.hasMenus();
	}
}
