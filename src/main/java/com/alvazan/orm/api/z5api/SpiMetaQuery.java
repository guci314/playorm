package com.alvazan.orm.api.z5api;

import java.util.List;

import com.alvazan.orm.api.z8spi.meta.TypeInfo;
import com.alvazan.orm.api.z8spi.meta.TypedColumn;
import com.alvazan.orm.api.z8spi.meta.ViewInfo;

public interface SpiMetaQuery {
	
	/**
	 * We will call this method EVERY time we want to run a query so that the SpiIndexQuery can have
	 * state and store parameters!!!
	 * 
	 * NOTE: SpiIndexQuery typically has a prototype pattern where it has the query that will just
	 * be cloned as the adapter is created so the adapter can use it, and so one indexQuery can 
	 * have many SpiQueryAdapters associated with it that came from the same query but threads supply
	 * different parameters to it.  
	 * 

	 * @param session 
	 * @return A BRAND NEW instance of SpiQueryAdapter(must not be one you have returned previously)
	 */
	public SpiQueryAdapter createQueryInstanceFromQuery(NoSqlSession session);

	public TypeInfo getMetaFieldByParameter(String name);

	public String getQuery();

	List<ViewInfo> getTargetViews();

	/**
	 * Join happens after rows returned because don't need indices as it would just slow it down.
	 * @return List of ViewInfo
	 */
	public List<ViewInfo> getViewsDelayedJoin();

	/**
	 * Join happens from indices during the query
	 * @return List of ViewInfo
	 */
	public List<ViewInfo> getViewsEagerJoin();

	public List<TypedColumn> getUpdateList();

	public String getQueryType();
	
}
