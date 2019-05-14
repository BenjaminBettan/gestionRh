package com.bbe.franglaises._enum;

/**
 * Your database config 
 */

public enum DATABASE_CONFIG 
{
	MYSQL(   
			 DATABASE_TYPE.MYSQL,	// this is a mysql db 
			 "localhost",			//install in local you can use WAMP software on Microsoft Windows
			 3306,					// this is the default port
			 "dates",				// Name of the data base
			 "root",				// name of the user
			 ""						//password);
		 ),
	
	;
	
	private DATABASE_TYPE database_type;
	private String ip;
	private int port;
	private String baseName;
	private String user;
	private String password;
	
	DATABASE_CONFIG(DATABASE_TYPE protocol_, String ip_, int port_, String baseName_, String user_, String password_)//constructor
	{
		this.database_type = protocol_;
		this.ip = ip_;
		this.port = port_;
		this.baseName = baseName_;
		this.user = user_;
		this.password = password_;
		
	}

	public DATABASE_TYPE getDatabaseType() {
		return database_type;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getBaseName() {
		return baseName;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

}
