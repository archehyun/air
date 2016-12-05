package api;

import java.util.HashMap;

public class QueryBundle extends HashMap<String , QueryEntity>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void addQueryResult(QueryEntity value)
	{
		
		this.put(value.getTid(), value);
	}
	
}
