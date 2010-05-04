package com.aptana.ui.internal;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

public class WebPerspectiveFactory implements IPerspectiveFactory
{

	private static final String APP_EXPLORER_ID = "com.aptana.explorer.view"; //$NON-NLS-1$
	private static final String TERMINAL_VIEW = "com.aptana.terminal.views.terminal"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout)
	{
		// Get the editor area
		String editorArea = layout.getEditorArea();

		// Left
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f, editorArea); //$NON-NLS-1$
		left.addView(APP_EXPLORER_ID);

		// Bottom right
		IPlaceholderFolderLayout terminalArea = layout.createPlaceholderFolder(
				"terminalArea", IPageLayout.BOTTOM, 0.75f, //$NON-NLS-1$
				editorArea);
		terminalArea.addPlaceholder(TERMINAL_VIEW+":*"); //$NON-NLS-1$
	}
}
