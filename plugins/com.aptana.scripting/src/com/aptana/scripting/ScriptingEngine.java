package com.aptana.scripting;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.osgi.framework.Bundle;

import com.aptana.scripting.model.ScriptLoadJob;
import com.aptana.scripting.model.RunType;
import com.aptana.util.ResourceUtils;

public class ScriptingEngine
{
	// framework_file extension point
	private static final String FRAMEWORK_FILE_ID = "frameworkFiles"; //$NON-NLS-1$
	private static final String TAG_FILE = "file"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$

	// loadpath extension point
	private static final String LOADPATH_ID = "loadPaths"; //$NON-NLS-1$
	private static final String TAG_LOADPATH = "loadPath"; //$NON-NLS-1$
	private static final String ATTR_PATH = "path"; //$NON-NLS-1$

	private static ScriptingEngine instance;

	private ScriptingContainer _scriptingContainer;
	private List<String> _loadPaths;
	private List<String> _frameworkFiles;
	private RunType _runType;

	/**
	 * ScriptingEngine
	 */
	private ScriptingEngine()
	{
		this._runType = Activator.getDefaultRunType();
	}

	/**
	 * createScriptingContainer
	 * 
	 * @param scope
	 * @return
	 */
	public ScriptingContainer createScriptingContainer(LocalContextScope scope)
	{
//		ScriptingContainer result = new ScriptingContainer(scope, LocalVariableBehavior.PERSISTENT);
		ScriptingContainer result = new ScriptingContainer(scope, LocalVariableBehavior.TRANSIENT);

		try
		{
			File jrubyHome = null;
			
			// try just exploding the jruby lib dir
			URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("lib"), null); //$NON-NLS-1$
			
			if (url != null)
			{
				File lib = ResourceUtils.resourcePathToFile(url);
				
				// Ok, now use the parent of exploded lib dir as JRuby Home
				jrubyHome = lib.getParentFile();
			}
			else
			{
				// Ok, just assume the plugin is unpacked and pass the root of the plugin as JRuby Home
				jrubyHome = FileLocator.getBundleFile(Activator.getDefault().getBundle());
			}
			
			result.getProvider().getRubyInstanceConfig().setJRubyHome(jrubyHome.getAbsolutePath());
		}
		catch (IOException e)
		{
			String message = MessageFormat.format(Messages.ScriptingEngine_Error_Setting_JRuby_Home,
					new Object[] { e.getMessage() });

			Activator.logError(message, e);
			ScriptLogger.logError(message);
		}
		
		return result;
	}

	/**
	 * getContributedLoadPaths
	 * 
	 * @return
	 */
	public List<String> getContributedLoadPaths()
	{
		if (this._loadPaths == null)
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			List<String> paths = new ArrayList<String>();

			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(Activator.PLUGIN_ID, LOADPATH_ID);

				if (extensionPoint != null)
				{
					IExtension[] extensions = extensionPoint.getExtensions();

					for (IExtension extension : extensions)
					{
						IConfigurationElement[] elements = extension.getConfigurationElements();

						for (IConfigurationElement element : elements)
						{
							if (element.getName().equals(TAG_LOADPATH))
							{
								String path = element.getAttribute(ATTR_PATH);

								IExtension declaring = element.getDeclaringExtension();
								String declaringPluginID = declaring.getNamespaceIdentifier();
								Bundle bundle = Platform.getBundle(declaringPluginID);
								URL url = bundle.getEntry(path);
								String urlAsPath = ResourceUtils.resourcePathToString(url);

								if (urlAsPath != null && urlAsPath.length() > 0)
								{
									paths.add(urlAsPath);
								}
								else
								{
									String message = MessageFormat.format(
										Messages.ScriptingEngine_Unable_To_Convert_Load_Path,
										new Object[] { declaringPluginID, url }
									);
									
									Activator.logError(message, null);
								}
							}
						}
					}
				}
			}

			this._loadPaths = Collections.unmodifiableList(paths);
		}

		return this._loadPaths;
	}
	
	/**
	 * getFrameworkFiles
	 * 
	 * @return
	 */
	public List<String> getFrameworkFiles()
	{
		if (this._frameworkFiles == null)
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			List<String> names = new ArrayList<String>();

			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(Activator.PLUGIN_ID, FRAMEWORK_FILE_ID);

				if (extensionPoint != null)
				{
					IExtension[] extensions = extensionPoint.getExtensions();

					for (IExtension extension : extensions)
					{
						IConfigurationElement[] elements = extension.getConfigurationElements();

						for (IConfigurationElement element : elements)
						{
							if (element.getName().equals(TAG_FILE))
							{
								names.add(element.getAttribute(ATTR_NAME));
							}
						}
					}
				}
			}

			this._frameworkFiles = Collections.unmodifiableList(names);
		}

		return this._frameworkFiles;
	}
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static ScriptingEngine getInstance()
	{
		if (instance == null)
		{
			instance = new ScriptingEngine();
		}

		return instance;
	}
	
	/**
	 * getScriptingContainer
	 * 
	 * @return
	 */
	public ScriptingContainer getScriptingContainer()
	{
		if (this._scriptingContainer == null)
		{
			this._scriptingContainer = this.createScriptingContainer(LocalContextScope.THREADSAFE);
		}

		return this._scriptingContainer;
	}

	/**
	 * runScript
	 * 
	 * @param fullPath
	 */
	public Object runScript(String fullPath, List<String> loadPaths)
	{
		return this.runScript(fullPath, loadPaths, false);
	}
	
	/**
	 * runScript
	 * 
	 * @param fullPath
	 * @param loadPaths
	 * @param async
	 * @return
	 */
	public Object runScript(String fullPath, List<String> loadPaths, boolean async)
	{
		ScriptLoadJob job = new ScriptLoadJob(fullPath, loadPaths);
		
		try
		{
			switch (this._runType)
			{
				case JOB:
					job.setPriority(Job.SHORT);
					job.schedule();
					
					if (async == false)
					{
						job.join();
					}
					break;
					
				case THREAD:
					Thread thread = new Thread(job, "Load '" + fullPath + "'"); //$NON-NLS-1$ //$NON-NLS-2$
					thread.start();
					
					if (async == false)
					{
						thread.join();
					}
					break;
					
				case CURRENT_THREAD:
				default:
					job.run();
					break;
			}
		}
		catch (InterruptedException e)
		{
			String message = MessageFormat.format(
				Messages.ScriptingEngine_Error_Executing_Script,
				new Object[] { fullPath }
			);
			
			ScriptUtils.logErrorWithStackTrace(message, e);
		}

		return (async && this._runType != RunType.CURRENT_THREAD) ? null : job.getReturnValue();
	}
}
