package com.aptana.git.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;
import com.aptana.git.core.model.GitExecutable;

public class GitExecutableLocationPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	private FileFieldEditor fileEditor;
	private BooleanFieldEditor pullIndicatorEditor;

	public GitExecutableLocationPage()
	{
		super();
	}

	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected void createFieldEditors()
	{
		// Git Executable location
		fileEditor = new FileFieldEditor(IPreferenceConstants.GIT_EXECUTABLE_PATH, "Git Executable", true,
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent())
		{
			@Override
			protected boolean checkState()
			{
				boolean ok = super.checkState();
				if (!ok)
					return ok;

				// Now check that the executable is ok
				String text = getTextControl().getText();
				if (text != null && text.trim().length() > 0)
				{
					if (!GitExecutable.acceptBinary(text))
					{
						showErrorMessage(NLS.bind("This path is not a valid git v{0} or higher binary.",
								GitExecutable.MIN_GIT_VERSION));
						return false;
					}
				}

				clearErrorMessage();
				return true;
			}
		};
		// Git pull indicator
		pullIndicatorEditor = new BooleanFieldEditor(IPreferenceConstants.GIT_CALCULATE_PULL_INDICATOR,
				"Calculate pull indicator (performs background fetches)", getFieldEditorParent()); //$NON-NLS-1$
		addField(fileEditor);
		addField(pullIndicatorEditor);
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return new ScopedPreferenceStore(new InstanceScope(), GitPlugin.getPluginId());
	}
}
