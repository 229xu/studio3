/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

import java.util.Map;

/**
 * @author Ingo Muschenetz
 * @author Chris Williams
 */
public interface IProblem
{

	/**
	 * A unique id used to identify the exact problem type. Used to associate specific problems with quick fixes.
	 */
	public static final String ID = "id"; //$NON-NLS-1$

	/**
	 */
	public static final String FLAGS = "flags"; //$NON-NLS-1$

	/**
	 */
	public static final String ARGUMENTS = "arguments"; //$NON-NLS-1$

	/**
	 * Gets the text offset where the error starts.
	 * 
	 * @return the offset
	 */
	int getOffset();

	/**
	 * Gets the length of the errored text.
	 * 
	 * @return the length
	 */
	int getLength();

	/**
	 * Gets the line number of the errored text.
	 * 
	 * @return the line number
	 */
	int getLineNumber();

	/**
	 * Gets the error message.
	 * 
	 * @return the error message
	 */
	String getMessage();

	/**
	 * Gets the severity of the error.
	 * 
	 * @return the severity
	 */
	int getSeverity();

	/**
	 * Gets the priority of the task (typically unused for errors/warnings).
	 * 
	 * @return the priority
	 */
	int getPriority();

	/**
	 * Gets the path of the source.
	 * 
	 * @return the source path
	 */
	String getSourcePath();

	/**
	 * Creates a map of marker attributes.
	 * 
	 * @return the attributes in a map
	 */
	Map<String, Object> createMarkerAttributes();

	/**
	 * Return the mapping of custom attributes.
	 * 
	 * @return the attributes in a map
	 */
	Map<String, Object> getAttributes();

	/**
	 * Sets a custom attribute.
	 * 
	 * @param attrName
	 * @param value
	 */
	public void setAttribute(String attrName, Object value);

	/**
	 * Does this problem represent a warning?
	 * 
	 * @return
	 */
	boolean isWarning();

	/**
	 * Does this problem represent an error?
	 * 
	 * @return
	 */
	boolean isError();

	/**
	 * Does this problem represent a task?
	 * 
	 * @return
	 */
	boolean isTask();
}
