package com.aptana.git.ui.internal.sharing;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.team.ui.IConfigurationWizard;
import org.eclipse.team.ui.IConfigurationWizardExtension;
import org.eclipse.ui.IWorkbench;

import com.aptana.git.Activator;

public class SharingWizard extends Wizard implements IConfigurationWizard, IConfigurationWizardExtension
{

	IProject[] projects;
	private ExistingOrNewPage existingPage;

	public SharingWizard()
	{
		setWindowTitle("Configure Git repository");
		setNeedsProgressMonitor(true);
	}

	@Override
	public void init(IWorkbench workbench, IProject project)
	{
		init(workbench, new IProject[] { project });
	}

	@Override
	public void init(IWorkbench workbench, IProject[] projects)
	{
		this.projects = new IProject[projects.length];
		System.arraycopy(projects, 0, this.projects, 0, projects.length);
	}
	
	public void addPages() {
		existingPage = new ExistingOrNewPage(this);
		addPage(existingPage);
	}

	public boolean performFinish() {
		final ConnectProviderOperation op = new ConnectProviderOperation(
				existingPage.getProjects());
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor)
						throws InvocationTargetException {
					try {
						op.run(monitor);
					} catch (CoreException ce) {
						throw new InvocationTargetException(ce);
					}
				}
			});
			return true;
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException) {
				e = e.getCause();
			}
			final IStatus status;
			if (e instanceof CoreException) {
				status = ((CoreException) e).getStatus();
				e = status.getException();
			} else {
				status = new Status(IStatus.ERROR, Activator.getPluginId(), 1,
						"SharingWizard_failed", e);
			}
			Activator.logError("SharingWizard_failed", e);
			ErrorDialog.openError(getContainer().getShell(), getWindowTitle(),
					"SharingWizard_failed", status, status.getSeverity());
			return false;
		}
	}

	@Override
	public boolean canFinish() {
		return existingPage.isPageComplete();
	}

}
