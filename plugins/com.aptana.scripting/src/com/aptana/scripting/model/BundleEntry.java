package com.aptana.scripting.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jruby.RubyRegexp;

import com.aptana.scope.ScopeSelector;

public class BundleEntry
{
	private String _name;
	private List<BundleElement> _bundles;
	private Comparator<BundleElement> _comparator = new Comparator<BundleElement>()
	{
		public int compare(BundleElement o1, BundleElement o2)
		{
			int result = o1.getBundlePrecedence().compareTo(o2.getBundlePrecedence());

			if (result == 0)
			{
				if (o1.isReference() == o2.isReference())
				{
					result = o1.getPath().compareTo(o2.getPath());
				}
				else
				{
					result = (o1.isReference()) ? 1 : -1;
				}
			}

			return result;
		}
	};

	/**
	 * BundleEntry
	 * 
	 * @param name
	 */
	public BundleEntry(String name)
	{
		if (name == null || name.length() == 0)
		{
			throw new IllegalArgumentException(Messages.BundleEntry_Name_Not_Defined);
		}

		this._name = name;
		this._bundles = new ArrayList<BundleElement>();
	}

	/**
	 * BundleEntry
	 * 
	 * @param name
	 * @param bundles
	 */
	private BundleEntry(String name, List<BundleElement> bundles)
	{
		this._name = name;
		this._bundles = bundles;
		
		Collections.sort(this._bundles, this._comparator);
	}
	
	/**
	 * add
	 * 
	 * @param bundle
	 */
	public void addBundle(BundleElement bundle)
	{
		if (bundle != null)
		{
			synchronized (this._bundles)
			{
				// get list of visible bundles before adding this new one
				Set<BundleElement> preVisibleBundles = new HashSet<BundleElement>(this.getContributingBundles());

				// add the bundle
				this._bundles.add(bundle);

				// keep bundles in canonical order
				Collections.sort(this._bundles, this._comparator);

				// fire visibility change events
				this.fireVisibilityEvents(preVisibleBundles);
				
				// fire add event
				BundleManager.getInstance().fireBundleAddedEvent(bundle);
			}
		}
	}

	/**
	 * fireVisibilityEvents
	 * 
	 * @param preVisibleBundles
	 */
	private void fireVisibilityEvents(Set<BundleElement> preVisibleBundles)
	{
		BundleManager manager = BundleManager.getInstance();
		
		// get current list of visible bundles
		Set<BundleElement> becameVisible = new HashSet<BundleElement>(this.getContributingBundles());
		
		// determine which bundles lost visibility and which gained visibility
		Set<BundleElement> becameHidden = new HashSet<BundleElement>(preVisibleBundles);
		
		becameHidden.removeAll(becameVisible);
		becameVisible.removeAll(preVisibleBundles);

		// fire hidden events
		if (becameHidden.size() > 0)
		{
			List<BundleElement> hiddenList = new ArrayList<BundleElement>(becameHidden);
			
			// set visibility flag
			for (BundleElement bundle : hiddenList)
			{
				bundle.setVisible(false);
			}
			
			// create new entry with these bundle elements. This is needed so the precedence
			// rules can be applied to this collection
			BundleEntry hiddenEntry = new BundleEntry(this.getName(), hiddenList);
			
			// fire hidden event
			manager.fireBundleBecameHiddenEvent(hiddenEntry);
		}
		
		// fire visible events
		if (becameVisible.size() > 0)
		{
			List<BundleElement> visibleList = new ArrayList<BundleElement>(becameVisible);
			
			// set visibility flag
			for (BundleElement bundle : visibleList)
			{
				bundle.setVisible(true);
			}
			
			// create new entry with these bundle elements. This is needed so the precedence
			// rules can be applied to this collection
			BundleEntry visibleEntry = new BundleEntry(this.getName(), visibleList);
			
			// fire visible event
			manager.fireBundleBecameVisibleEvent(visibleEntry);
		}
	}

	/**
	 * getBundles
	 * 
	 * @return
	 */
	public BundleElement[] getBundles()
	{
		BundleElement[] result;

		synchronized (this._bundles)
		{
			result = this._bundles.toArray(new BundleElement[this._bundles.size()]);
		}

		return result;
	}

	/**
	 * getCommands
	 * 
	 * @return
	 */
	public CommandElement[] getCommands()
	{
		final Set<String> names = new HashSet<String>();
		final List<CommandElement> result = new ArrayList<CommandElement>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				for (CommandElement command : bundle.getCommands())
				{
					String name = command.getDisplayName();

					if (names.contains(name) == false)
					{
						names.add(name);
						result.add(command);
					}
				}

				return true;
			}
		});

		return result.toArray(new CommandElement[result.size()]);
	}

	/**
	 * getContributingBundles
	 * 
	 * @return
	 */
	public List<BundleElement> getContributingBundles()
	{
		final List<BundleElement> result = new ArrayList<BundleElement>();
		
		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				result.add(bundle);
				return true;
			}
		});
		
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * getFileTypeRegistry
	 * 
	 * @return
	 */
	public Map<String, String> getFileTypeRegistry()
	{
		final Map<String, String> result = new HashMap<String, String>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				Map<String, String> registry = bundle.getFileTypeRegistry();
				
				if (registry != null)
				{
					result.putAll(registry);
					return false;
				}
				
				return true;
			}
		});

		return result;
	}
	
	/**
	 * getFileTypes
	 * 
	 * @return
	 */
	public List<String> getFileTypes()
	{
		final List<String> result = new ArrayList<String>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				List<String> registry = bundle.getFileTypes();
				
				if (registry != null)
				{
					result.addAll(registry);
				}
				
				return true;
			}
		});

		return result;
	}

	/**
	 * getFoldingStartMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getFoldingStartMarkers()
	{
		final Map<ScopeSelector, RubyRegexp> result = new HashMap<ScopeSelector, RubyRegexp>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				Map<ScopeSelector, RubyRegexp> registry = bundle.getFoldingStartMarkers();
				
				if (registry != null)
				{
					result.putAll(registry);
				}
				
				return true;
			}
		});

		return result;
	}

	/**
	 * getFoldingStopMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getFoldingStopMarkers()
	{
		final Map<ScopeSelector, RubyRegexp> result = new HashMap<ScopeSelector, RubyRegexp>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				Map<ScopeSelector, RubyRegexp> registry = bundle.getFoldingStopMarkers();
				
				if (registry != null)
				{
					result.putAll(registry);
				}
				
				return true;
			}
		});

		return result;
	}
	
	/**
	 * getDecreaseIndentMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getDecreaseIndentMarkers()
	{
		final Map<ScopeSelector, RubyRegexp> result = new HashMap<ScopeSelector, RubyRegexp>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				Map<ScopeSelector, RubyRegexp> registry = bundle.getDecreaseIndentMarkers();
				
				if (registry != null)
				{
					result.putAll(registry);
				}
				
				return true;
			}
		});

		return result;
	}

	/**
	 * getIncreaseIndentMarkers
	 * 
	 * @return
	 */
	public Map<ScopeSelector, RubyRegexp> getIncreaseIndentMarkers()
	{
		final Map<ScopeSelector, RubyRegexp> result = new HashMap<ScopeSelector, RubyRegexp>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				Map<ScopeSelector, RubyRegexp> registry = bundle.getIncreaseIndentMarkers();
				
				if (registry != null)
				{
					result.putAll(registry);
				}
				
				return true;
			}
		});

		return result;
	}

	/**
	 * getLoadPaths
	 * 
	 * @return
	 */
	public List<String> getLoadPaths()
	{
		final List<String> result = new LinkedList<String>();
		
		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				result.addAll(bundle.getLoadPaths());
				
				return true;
			}
		});
		
		return result;
	}
	
	/**
	 * geMenus
	 * 
	 * @return
	 */
	public MenuElement[] getMenus()
	{
		final Set<String> names = new HashSet<String>();
		final List<MenuElement> result = new ArrayList<MenuElement>();

		this.processBundles(new BundleProcessor()
		{
			public boolean processBundle(BundleEntry entry, BundleElement bundle)
			{
				for (MenuElement menu : bundle.getMenus())
				{
					String name = menu.getDisplayName();

					if (names.contains(name) == false)
					{
						names.add(name);
						result.add(menu);
					}
				}

				return true;
			}
		});

		return result.toArray(new MenuElement[result.size()]);
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * processBundles
	 * 
	 * @param processor
	 */
	protected void processBundles(BundleProcessor processor)
	{
		// NOTE: seems like a potentially long lock since we're running the processor
		// on each bundle instance
		synchronized (this._bundles)
		{
			// walk list of bundles in decreasing bundle scope precedence, processing
			// references before declarations
			for (int i = this._bundles.size() - 1; i >= 0; i--)
			{
				BundleElement bundle = this._bundles.get(i);

				// we're done processing if we've processed all bundle references and
				// one bundle declaration OR if our BundleProcessor tells us to stop

				// NOTE: the order of this conditional is important. We need to run
				// the processor on the current bundle before we decide to exit when
				// we hit a non-reference bundle
				if (processor.processBundle(this, bundle) == false || bundle.isReference() == false)
				{
					break;
				}
			}
		}
	}

	/**
	 * removeBundle
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean removeBundle(BundleElement bundle)
	{
		boolean result;

		synchronized (this._bundles)
		{
			// get list of visible bundles before adding this new one
			Set<BundleElement> preVisibleBundles = new HashSet<BundleElement>(this.getContributingBundles());
			
			result = this._bundles.remove(bundle);
			
			if (result)
			{
				// fire bundle deleted event
				BundleManager.getInstance().fireBundleDeletedEvent(bundle);
				
				// fire visibility change events
				this.fireVisibilityEvents(preVisibleBundles);
			}
		}

		return result;
	}

	/**
	 * size
	 * 
	 * @return
	 */
	public int size()
	{
		int size = 0;

		if (this._bundles != null)
		{
			size = this._bundles.size();
		}

		return size;
	}
}
