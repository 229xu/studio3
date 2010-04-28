package com.aptana.editor.js.model;

import java.util.LinkedList;
import java.util.List;

public class FunctionElement extends PropertyElement
{
	private List<ParameterElement> _parameters = new LinkedList<ParameterElement>();
	private List<String> _references = new LinkedList<String>();
	private List<ExceptionElement> _exceptions = new LinkedList<ExceptionElement>();

	private boolean _isConstructor;
	private boolean _isMethod;

	/**
	 * FunctionElement
	 */
	public FunctionElement()
	{
	}

	/**
	 * addException
	 * 
	 * @param currentException
	 */
	public void addException(ExceptionElement exception)
	{
		this._exceptions.add(exception);
	}

	/**
	 * addParameter
	 * 
	 * @param parameter
	 */
	public void addParameter(ParameterElement parameter)
	{
		this._parameters.add(parameter);
	}

	/**
	 * addReferences
	 * 
	 * @param reference
	 */
	public void addReference(String reference)
	{
		this._references.add(reference);
	}

	/**
	 * addReturnType
	 * 
	 * @param returnType
	 */
	public void addReturnType(ReturnTypeElement returnType)
	{
		this.addType(returnType);
	}

	/**
	 * getExceptions
	 * 
	 * @return
	 */
	public ExceptionElement[] getExceptions()
	{
		return this._exceptions.toArray(new ExceptionElement[this._exceptions.size()]);
	}

	/**
	 * getParameters
	 * 
	 * @return
	 */
	public ParameterElement[] getParameters()
	{
		return this._parameters.toArray(new ParameterElement[this._parameters.size()]);
	}

	/**
	 * getReferences
	 * 
	 * @return
	 */
	public String[] getReferences()
	{
		return this._references.toArray(new String[this._references.size()]);
	}

	/**
	 * getReturnTypes
	 * 
	 * @return
	 */
	public ReturnTypeElement[] getReturnTypes()
	{
		return this.getTypes();
	}

	/**
	 * isConstructor
	 * 
	 * @return
	 */
	public boolean isConstructor()
	{
		return this._isConstructor;
	}

	/**
	 * isMethod
	 * 
	 * @return
	 */
	public boolean isMethod()
	{
		return this._isMethod;
	}

	/**
	 * setIsConstructor
	 * 
	 * @param value
	 */
	public void setIsConstructor(boolean value)
	{
		this._isConstructor = value;
	}

	/**
	 * setIsMethod
	 * 
	 * @param value
	 */
	public void setIsMethod(boolean value)
	{
		this._isMethod = value;
	}
}
