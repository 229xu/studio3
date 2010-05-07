package com.aptana.editor.html.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.contentassist.index.HTMLIndexReader;
import com.aptana.editor.html.contentassist.index.HTMLMetadataReader;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.index.core.Index;

public class HTMLIndexQueryHelper
{
	private HTMLIndexReader _reader;
	private HTMLMetadataReader _metadata;

	/**
	 * HTMLContentAssistHelper
	 */
	public HTMLIndexQueryHelper()
	{
	}

	/**
	 * getAttribute
	 * 
	 * @param name
	 * @return
	 */
	public List<AttributeElement> getAttribute(String name)
	{
		// NOTE: Right now the metadata format uses name references in elements
		// to list which properties it has. Unfortunately, if more than one
		// element have the same attribute name we lose the connection to the
		// right attribute when we do attribute lookups. So, for now, we're
		// returning the list of attributes that match the given name
		List<AttributeElement> result = new LinkedList<AttributeElement>();

		if (name != null && name.length() > 0)
		{
			// TODO: optimize with a name->attribute hash
			for (AttributeElement attribute : this.getAttributes())
			{
				if (name.equals(attribute.getName()))
				{
					result.add(attribute);
				}
			}
		}

		return result;
	}

	/**
	 * getAttributes
	 * 
	 * @return
	 */
	public List<AttributeElement> getAttributes()
	{
		return this.getMetadata().getAttributes();
	}

	/**
	 * getClasses
	 * 
	 * @return
	 */
	public Map<String, String> getClasses(Index index)
	{
		return this.getReader().getValues(index, CSSIndexConstants.CLASS);
	}

	/**
	 * getElement
	 * 
	 * @param name
	 * @return
	 */
	public ElementElement getElement(String name)
	{
		ElementElement result = null;

		if (name != null && name.length() > 0)
		{
			// TODO: optimize with a name->element hash
			for (ElementElement element : this.getElements())
			{
				if (name.equals(element.getName()))
				{
					result = element;
					break;
				}
			}
		}

		return result;
	}

	// /**
	// * getIndex
	// *
	// * @return
	// */
	// private Index getIndex()
	// {
	// return IndexManager.getInstance().getIndex(HTMLIndexConstants.METADATA);
	// }

	/**
	 * getElements
	 * 
	 * @return
	 */
	public List<ElementElement> getElements()
	{
		return this.getMetadata().getElements();
	}

	/**
	 * getEntities
	 * 
	 * @return
	 */
	public List<EntityElement> getEntities()
	{
		return this.getMetadata().getEntities();
	}

	/**
	 * getIDs
	 * 
	 * @param index
	 * @return
	 */
	public Map<String, String> getIDs(Index index)
	{
		return this.getReader().getValues(index, CSSIndexConstants.IDENTIFIER);
	}

	/**
	 * getMetadata
	 * 
	 * @return
	 */
	private HTMLMetadataReader getMetadata()
	{
		if (this._metadata == null)
		{
			this._metadata = new HTMLMetadataReader();
			String[] resources = this.getMetadataResources();

			for (String resource : resources)
			{
				URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(resource), null);

				if (url != null)
				{
					InputStream stream = null;

					try
					{
						stream = url.openStream();

						this._metadata.loadXML(stream);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					catch (Throwable t)
					{
						t.printStackTrace();
					}
					finally
					{
						if (stream != null)
						{
							try
							{
								stream.close();
							}
							catch (IOException e)
							{
							}
						}
					}
				}
			}
		}

		return this._metadata;
	}

	/**
	 * getMetadataResources
	 * 
	 * @return
	 */
	protected String[] getMetadataResources()
	{
		return new String[] { "/metadata/html_metadata.xml" }; //$NON-NLS-1$
	}

	/**
	 * getReader
	 * 
	 * @return
	 */
	protected HTMLIndexReader getReader()
	{
		if (this._reader == null)
		{
			this._reader = new HTMLIndexReader();
		}

		return this._reader;
	}
}
