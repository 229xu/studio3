package com.aptana.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A stream gobbler thread that reads from an InputStream, collects the read text, and allows retrieving it by calling
 * getResult, during, or at the end of the execution.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class InputStreamGobbler extends Thread
{
	private InputStream is;
	private String charset;
	private String newLineSeparator;
	private StringBuilder result;

	/**
	 * Constructs a new InputStreamGobbler
	 * 
	 * @param is
	 * @param charset
	 * @param newLineSeparator
	 */
	public InputStreamGobbler(InputStream is, String newLineSeparator, String charset)
	{
		if (is == null || newLineSeparator == null)
		{
			throw new IllegalArgumentException("The InputStream and the newLineSeparator cannot be null!"); //$NON-NLS-1$
		}
		this.is = is;
		this.charset = charset;
		this.newLineSeparator = newLineSeparator;
	}

	/**
	 * Returns the read text. If the thread was never started yet, the result will be null.
	 * 
	 * @return The string that was read from the input stream.
	 */
	public String getResult()
	{
		if (result == null)
		{
			return null;
		}
		return result.toString();
	}

	/**
	 * Run the gobbler as a thread that will read from the input stream and will store it in memory. The resulted text
	 * can be retrieved by calling {@link #getResult()}
	 */
	public void run()
	{
		InputStreamReader isr = null;
		try
		{
			if (charset != null)
			{
				isr = new InputStreamReader(is, charset);
			}
			else
			{
				isr = new InputStreamReader(is);
			}

			BufferedReader br = new BufferedReader(isr);
			result = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null)
			{
				result.append(line);
				result.append(newLineSeparator);
			}
			// delete last extraneous newline
			if (result.length() > 0)
			{
				result.deleteCharAt(result.length() - newLineSeparator.length());
			}
		}
		catch (IOException ioe)
		{
			UtilPlugin.logError(ioe.getMessage(), ioe);
		}
		finally
		{
			if (isr != null)
			{
				try
				{
					isr.close();
				}
				catch (Exception e)
				{
				}
			}
		}
	}
}