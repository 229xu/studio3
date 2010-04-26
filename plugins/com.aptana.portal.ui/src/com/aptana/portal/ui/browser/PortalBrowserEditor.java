package com.aptana.portal.ui.browser;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.browser.IBrowserViewerContainer;
import org.eclipse.ui.internal.browser.WebBrowserEditor;

import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.BrowserNotifier;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;
import com.aptana.portal.ui.dispatch.browserFunctions.DispatcherBrowserFunction;
import com.aptana.portal.ui.internal.Portal;

/**
 * A portal browser editor. We extends the Eclipse internal WebBrowserEditor. Although not a great act, it solves the
 * protected attributes access limitations which require reflections when we needed to access them.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
@SuppressWarnings("restriction")
public class PortalBrowserEditor extends WebBrowserEditor implements IBrowserViewerContainer
{
	public static final String WEB_BROWSER_EDITOR_ID = "com.aptana.portal.ui.browser.portal"; //$NON-NLS-1$
	private List<BrowserFunction> browserFunctions;

	/**
	 * Set the URL to display in the browser.
	 * 
	 * @param url
	 */
	public void setURL(URL url)
	{
		if (url != null)
		{
			this.webBrowser.setURL(url.toString());
		}
		else
		{
			PortalUIPlugin.logWarning("Ignoring a null URL that was passed to the Aptana Portal"); //$NON-NLS-1$
		}
	}

	/**
	 * Adds a dispose listener on the internal web browser.
	 * 
	 * @param listener
	 */
	public void addDisposeListener(DisposeListener listener)
	{
		this.webBrowser.addDisposeListener(listener);
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		final Browser browser = this.webBrowser.getBrowser();
		browser.setJavascriptEnabled(true);

		// Usually, we would just listen to a location change. However, since IE does not
		// behave well with notifying us when hitting refresh (F5), we have to do it on
		// a title change (which should work for all browsers)
		browser.addTitleListener(new PortalTitleListener(browser));

		// Register a location listener anyway, just to make sure that the functions are
		// removed when we have a location change.
		// The title-listener will place them back in when the TitleEvent is fired.
		browser.addProgressListener(new ProgressAdapter()
		{
			public void completed(ProgressEvent event)
			{
				browser.addLocationListener(new LocationAdapter()
				{
					public void changed(LocationEvent event)
					{
						browser.removeLocationListener(this);
						unregisterBrowserFunctions();
					}
				});
			}
		});
		// Register this browser to receive notifications from any Browser-Notifier that was
		// added to do so through the browserInteractions extension point.
		BrowserNotifier.getInstance().registerBrowser(WEB_BROWSER_EDITOR_ID, browser);
	}

	/**
	 * Register the browser functions into the given browser.
	 * 
	 * @param browser
	 */
	protected synchronized void registerBrowserFunctions(final Browser browser)
	{
		browserFunctions = new ArrayList<BrowserFunction>();
		// For now, we register a single browser function that dispatch all the
		// JavaScript requests through the browser-action-controller extensions.
		BrowserFunction dispatcherFunction = new DispatcherBrowserFunction(browser,
				IBrowserNotificationConstants.DISPATCH_FUNCTION_NAME);
		browserFunctions.add(dispatcherFunction);

		// Make sure that all the Javascript errors are being surfaced out of the internal
		// browser.
		browser.execute("window.onerror=customErrorHandler"); //$NON-NLS-1$
	}

	/**
	 * Un-register the browser functions.
	 */
	protected synchronized void unregisterBrowserFunctions()
	{
		if (browserFunctions != null)
		{
			for (BrowserFunction bf : browserFunctions)
			{
				bf.dispose();
			}
			browserFunctions = null;
		}
	}

	private class PortalTitleListener implements TitleListener
	{
		private final Browser browser;

		public PortalTitleListener(Browser browser)
		{
			this.browser = browser;
		}

		@Override
		public void changed(TitleEvent event)
		{
			// Dispose all BrowserFunctions when the location of the browser is no longer under
			// Aptana.com or the local machine.
			unregisterBrowserFunctions();
			String url = browser.getUrl();
			if (url != null && (url.startsWith(Portal.BASE_URL_PREFIX) || url.startsWith("file:"))) //$NON-NLS-1$
			{
				registerBrowserFunctions(browser);
			}
		}
	}
}