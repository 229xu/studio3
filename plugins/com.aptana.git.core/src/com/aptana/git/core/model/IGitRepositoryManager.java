package com.aptana.git.core.model;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IGitRepositoryManager
{

	/**
	 * Used to clean up all the repos from memory when plugin stops.
	 */
	public void cleanup();

	public void addListener(IGitRepositoriesListener listener);

	public void removeListener(IGitRepositoriesListener listener);

	/**
	 * Generates a brand new git repository in the specified location.
	 */
	public void create(String path);

	/**
	 * Used when the user disconnects the project from the repository. We should notify listeners that the repo has been
	 * unattached. We should also flush the cached copy.
	 * 
	 * @param p
	 */
	public void removeRepository(IProject p);

	/**
	 * Used to retrieve a git repository for a project. Will return null if Eclipse team provider is not hooked up!
	 * 
	 * @param project
	 * @return
	 */
	public GitRepository getAttached(IProject project);

	/**
	 * Used solely for grabbing an existing repository when attaching Eclipse team stuff to a project!
	 * 
	 * @param path
	 * @return
	 */
	public GitRepository getUnattachedExisting(URI path);

	/**
	 * Given an existing repo on disk, we wrap it with our model and hook it up to the eclipse team provider.
	 * 
	 * @param project
	 * @param m
	 * @return
	 */
	public GitRepository attachExisting(IProject project, IProgressMonitor m) throws CoreException;

	public URI gitDirForURL(URI repositoryURL);

	/**
	 * Some classes will want to listen to changes to every repository. This is a utility method for hooking the same
	 * listener to every git repository.
	 * 
	 * @param gitLightweightDecorator
	 */
	public void addListenerToEachRepository(IGitRepositoryListener listener);

	public void removeListenerFromEachRepository(IGitRepositoryListener listener);
}
