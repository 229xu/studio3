package com.aptana.git.ui.internal.history;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.ui.IWorkbenchPage;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.Diff;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.ui.GitUIPlugin;

class OpenRevisionAction extends Action
{

	private IWorkbenchPage page;
	private Table table;

	OpenRevisionAction(IWorkbenchPage page, Table table)
	{
		this.page = page;
		this.table = table;
	}

	@Override
	public String getText()
	{
		return "Open Revision";
	}

	@Override
	public void run()
	{
		TableItem[] selected = table.getSelection();
		final Diff d = (Diff) selected[0].getData();
		final GitCommit c = d.commit();
		final IFileRevision nextFile = GitPlugin.revisionForCommit(c, d.newName());
		try
		{
			Utils.openEditor(page, nextFile, new NullProgressMonitor());
		}
		catch (CoreException e)
		{
			GitUIPlugin.logError(e);
		}
	}
}
