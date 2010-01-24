package com.aptana.scripting.model;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jruby.Ruby;
import org.jruby.embed.EmbedEvalUnit;
import org.jruby.embed.EvalFailedException;
import org.jruby.embed.ParseFailedException;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;

import com.aptana.scripting.Messages;
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptingEngine;

public class ScriptLoadJob extends AbstractScriptJob
{
	private String _filename;
	private Object _returnValue;
	
	/**
	 * ExecuteScriptJob
	 * 
	 * @param filename
	 */
	public ScriptLoadJob(String filename)
	{
		this("Execute JRuby File", filename, null);
	}
	
	/**
	 * ExecuteScriptJob
	 * 
	 * @param filename
	 * @param loadPaths
	 */
	public ScriptLoadJob(String filename, List<String> loadPaths)
	{
		this("Execute JRuby File", filename, loadPaths);
	}
	
	/**
	 * ExecuteScriptJob
	 * 
	 * @param name
	 * @param filename
	 */
	public ScriptLoadJob(String name, String filename)
	{
		this(name, filename, null);
	}
	
	/**
	 * ExecuteScriptJob
	 * 
	 * @param name
	 * @param filename
	 * @param loadPaths
	 */
	public ScriptLoadJob(String name, String filename, List<String> loadPaths)
	{
		super(name, loadPaths);
		
		this._filename = filename;
	}

	/**
	 * getReturnValue
	 * 
	 * @return
	 */
	public Object getReturnValue()
	{
		return this._returnValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor)
	{
		ScriptingContainer container = ScriptingEngine.getInstance().getScriptingContainer();
		Ruby runtime = container.getProvider().getRuntime(); 
		Object result = null;
		
		// apply load paths
		this.applyLoadPaths(runtime);
		
		// TODO: $0 should work, but until then, we'll use this hack so scripts
		// can get their full path
		container.put("$fullpath", this._filename); //$NON-NLS-1$
		
		// compile
		try
		{
			EmbedEvalUnit unit = container.parse(PathType.ABSOLUTE, this._filename);

			// execute
			result = unit.run();
		}
		catch (ParseFailedException e)
		{
			String message = MessageFormat.format(
				Messages.ScriptingEngine_Parse_Error,
				new Object[] { this._filename, e.getMessage() }
			);

			ScriptLogger.logError(message);
		}
		catch (EvalFailedException e)
		{
			String message = MessageFormat.format(
				Messages.ScriptingEngine_Execution_Error,
				new Object[] { this._filename, e.getMessage() }
			);

			ScriptLogger.logError(message);
		}
		
		// unapply load paths
		this.unapplyLoadPaths(runtime);
		
		// save result
		this.setReturnValue(result);
		
		// return status
		return Status.OK_STATUS;
	}
	
	/**
	 * setReturnValue
	 * 
	 * @param value
	 */
	protected void setReturnValue(Object value)
	{
		this._returnValue = value;
	}
}
