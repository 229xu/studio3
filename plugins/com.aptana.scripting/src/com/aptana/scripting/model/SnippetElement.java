package com.aptana.scripting.model;

public class SnippetElement extends CommandElement
{
	/**
	 * Snippet
	 * 
	 * @param name
	 */
	public SnippetElement(String path)
	{
		super(path);
		
		this.setInputType(InputType.NONE);
		this.setOutputType(OutputType.INSERT_AS_SNIPPET);
	}

	/**
	 * execute
	 */
	public CommandResult execute()
	{
		return execute(null);
	}

	/**
	 * execute
	 */
	public CommandResult execute(CommandContext context)
	{
		CommandResult result = new CommandResult(this.getExpansion());

		// indicate successful execution so that command result processing will work
		result.setExecutedSuccessfully(true);

		// grab input type so we can report back which input was used
		String inputTypeString = (String) context.get(CommandContext.INPUT_TYPE);
		InputType inputType = InputType.get(inputTypeString);
		
		result.setInputType(inputType);
		
		return result;
	}

	/**
	 * getElementName
	 */
	protected String getElementName()
	{
		return "snippet"; //$NON-NLS-1$
	}
	
	/**
	 * getExpansion
	 * 
	 * @return
	 */
	public String getExpansion()
	{
		return this.getInvoke();
	}

	/**
	 * setExpansion
	 * 
	 * @param expansion
	 */
	public void setExpansion(String expansion)
	{
		this.setInvoke(expansion);
	}
}
