package com.aptana.scripting.model;

import java.io.OutputStream;

public class CommandResult
{
	private String _outputString;
	private OutputStream _outputStream;
	private String _errorString;
	private OutputStream _errorStream;
	private InputType _inputType;
	private OutputType _outputType;
	private int _returnValue;
	private boolean _executedSuccessfully;
	private CommandContext _context;
	private CommandElement _command;

	/**
	 * CommandResult
	 */
	public CommandResult(CommandElement command, CommandContext context)
	{
		this._command = command;
		this._context = context;
		this._outputType = (context != null) ? context.getOutputType() : OutputType.UNDEFINED;
	}

	/**
	 * executedSuccessfully
	 * 
	 * @return
	 */
	public boolean executedSuccessfully()
	{
		return this._executedSuccessfully;
	}

	/**
	 * getCommand
	 * 
	 * @return
	 */
	public CommandElement getCommand()
	{
		return this._command;
	}

	/**
	 * getContext
	 * 
	 * @return
	 */
	public CommandContext getContext()
	{
		return this._context;
	}

	/**
	 * getOutputStream
	 * 
	 * @return
	 */
	public OutputStream getErrorStream()
	{
		return this._errorStream;
	}

	/**
	 * getErrorString
	 * 
	 * @return
	 */
	public String getErrorString()
	{
		return this._errorString;
	}

	/**
	 * getInputType
	 * 
	 * @return
	 */
	public InputType getInputType()
	{
		return this._inputType;
	}

	/**
	 * getOutputStream
	 * 
	 * @return
	 */
	public OutputStream getOutputStream()
	{
		return this._outputStream;
	}

	/**
	 * getOutputString
	 * 
	 * @return
	 */
	public String getOutputString()
	{
		String result = this._outputString;
		
		if (result == null && this._outputStream != null)
		{
			result = this._outputStream.toString();
		}
		
		return result;
	}

	/**
	 * getOutputType
	 * 
	 * @return
	 */
	public OutputType getOutputType()
	{
		return (this._outputType != null) ? this._outputType : OutputType.UNDEFINED;
	}
	
	/**
	 * getReturnValue
	 * 
	 * @return
	 */
	public int getReturnValue()
	{
		return this._returnValue;
	}

	/**
	 * setExecutedSuccessfully
	 * 
	 * @param value
	 */
	void setExecutedSuccessfully(boolean value)
	{
		this._executedSuccessfully = value;
	}

	/**
	 * setInputType
	 * 
	 * @param inputType
	 */
	void setInputType(InputType inputType)
	{
		this._inputType = inputType;
	}

	/**
	 * setOutputStream
	 * 
	 * @param output
	 */
	void setOutputStream(OutputStream output)
	{
		this._outputStream = output;
	}
	
	/**
	 * setOutputString
	 * 
	 * @param output
	 */
	void setOutputString(String output)
	{
		this._outputString = output;
	}
	
	/**
	 * setReturnValue
	 * 
	 * @param value
	 */
	void setReturnValue(int value)
	{
		this._returnValue = value;
	}
}
