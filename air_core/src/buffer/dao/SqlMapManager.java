/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package buffer.dao;

import java.io.IOException;
import java.io.Reader;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class SqlMapManager 
{
	private static String resource = "buffer/dao/sql-map-config.xml";
	private static SqlMapClient sqlMap;
	private SqlMapManager(){}
	public static SqlMapClient getSqlMapInstance() throws IOException 
	{
		if(sqlMap==null)
		{
			Reader reader = Resources.getResourceAsReader(resource);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		}
		
		return sqlMap;
	}
	public static SqlMapClient getSqlMapInstance(boolean flag) throws IOException 
	{
		if(flag)
		{
			Reader reader = Resources.getResourceAsReader(resource);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		}
		
		return sqlMap;
	}
	
	

}
