package com.aptana.git.ui.actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.actions.Messages;

/**
 * Base class for actions that simple call out to actions/commands on the Git executable to be run inside the Eclipse
 * console. Used for global actions like Push, Pull, Status.
 * 
 * @author cwilliams
 */
public abstract class GitAction extends Action implements IObjectActionDelegate, IWorkbenchWindowActionDelegate
{

	private ISelection selection;
	private Shell shell;
	private IWorkbenchPart targetPart;
	private IWorkbenchWindow window;

	private ISelectionListener selectionListener = new ISelectionListener()
	{
		public void selectionChanged(IWorkbenchPart part, ISelection selection)
		{
			if (selection instanceof IStructuredSelection)
				GitAction.this.selection = selection;
		}
	};

	private IPartListener2 targetPartListener = new IPartListener2()
	{
		public void partActivated(IWorkbenchPartReference partRef)
		{
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef)
		{
		}

		public void partClosed(IWorkbenchPartReference partRef)
		{
			if (targetPart == partRef.getPart(false))
			{
				targetPart = null;
			}
		}

		public void partDeactivated(IWorkbenchPartReference partRef)
		{
		}

		public void partHidden(IWorkbenchPartReference partRef)
		{
		}

		public void partInputChanged(IWorkbenchPartReference partRef)
		{
		}

		public void partOpened(IWorkbenchPartReference partRef)
		{
		}

		public void partVisible(IWorkbenchPartReference partRef)
		{
		}
	};

	public abstract void run();

	public void run(IAction action)
	{
		run();
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		if (selection instanceof IStructuredSelection)
		{
			this.selection = selection;
		}
	}

	public void init(IWorkbenchWindow window)
	{
		this.window = window;
		this.shell = window.getShell();
		window.getSelectionService().addPostSelectionListener(selectionListener);
		window.getActivePage().addPartListener(targetPartListener);
	}

	public void dispose()
	{
		if (window != null)
		{
			window.getSelectionService().removePostSelectionListener(selectionListener);
			if (window.getActivePage() != null)
			{
				window.getActivePage().removePartListener(targetPartListener);
			}
			targetPartListener = null;
		}
		// Don't hold on to anything when we are disposed to prevent memory leaks (see bug 195521)
		selection = null;
		window = null;
		targetPart = null;
		shell = null;
	}

	protected IResource[] getSelectedResources()
	{
		if (this.selection == null || !(this.selection instanceof IStructuredSelection))
		{

			final IResource[] editorResource = new IResource[1];
			Display.getDefault().syncExec(new Runnable()
			{

				public void run()
				{
					try
					{
						IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.getActiveEditor();
						IEditorInput input = part.getEditorInput();
						if (input == null)
							return;
						editorResource[0] = (IResource) input.getAdapter(IResource.class);
					}
					catch (Exception e)
					{
						// ignore
					}
				}
			});
			if (editorResource[0] != null)
				return editorResource;
			return new IResource[0];
		}

		Set<IResource> resources = new HashSet<IResource>();
		IStructuredSelection structured = (IStructuredSelection) this.selection;
		for (Object element : structured.toList())
		{
			if (element == null)
				continue;

			if (element instanceof IResource)
				resources.add((IResource) element);

			if (element instanceof IAdaptable)
			{
				IAdaptable adapt = (IAdaptable) element;
				IResource resource = (IResource) adapt.getAdapter(IResource.class);
				if (resource != null)
					resources.add(resource);
			}
		}
		return resources.toArray(new IResource[resources.size()]);
	}

	@Override
	public boolean isEnabled()
	{
		return getSelectedRepository() != null;
	}

	protected GitRepository getSelectedRepository()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length != 1)
			return null;
		IProject project = resources[0].getProject();
		return GitRepository.getAttached(project);
	}

	protected void refreshAffectedProjects()
	{
		final Set<IProject> affectedProjects = new HashSet<IProject>();
		GitRepository repo = getSelectedRepository();
		if (repo != null)
		{
			affectedProjects.addAll(getAssociatedProjects(repo));
		}

		WorkspaceJob job = new WorkspaceJob(Messages.PullAction_RefreshJob_Title)
		{
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
			{
				int work = 100 * affectedProjects.size();
				SubMonitor sub = SubMonitor.convert(monitor, work);
				for (IProject resource : affectedProjects)
				{
					if (sub.isCanceled())
						return Status.CANCEL_STATUS;
					resource.refreshLocal(IResource.DEPTH_INFINITE, sub.newChild(100));
				}
				sub.done();
				return Status.OK_STATUS;
			}
		};
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.setUser(true);
		job.schedule();
	}

	private Collection<? extends IProject> getAssociatedProjects(GitRepository repo)
	{
		Set<IProject> projects = new HashSet<IProject>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			GitRepository other = GitRepository.getAttached(project);
			if (other != null && other.equals(repo))
			{
				projects.add(project);
			}
		}
		return projects;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		if (targetPart != null)
		{
			this.shell = targetPart.getSite().getShell();
			this.targetPart = targetPart;
		}
	}

	protected Shell getShell()
	{
		if (shell != null)
		{
			return shell;
		}
		else if (targetPart != null)
		{
			return targetPart.getSite().getShell();
		}
		else
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench == null)
				return null;
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (window == null)
				return null;
			return window.getShell();
		}
	}

	/**
	 * @return IWorkbenchPart
	 */
	protected IWorkbenchPart getTargetPart()
	{
		if (targetPart == null)
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench == null)
				return null;
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (window == null)
				return null;
			IWorkbenchPage page = window.getActivePage();
			if (page != null)
			{
				targetPart = page.getActivePart();
			}
		}
		return targetPart;
	}
}
