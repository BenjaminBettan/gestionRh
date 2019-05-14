package com.bbe.franglaises;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.bbe.franglaises._enum.DATABASE_CONFIG;
import com.bbe.franglaises._enum.DATABASE_TYPE;
import com.bbe.franglaises._enum.STATUS_DB;


/**
 * Connect to your database and execute requests
 */

public class DataBase {
	
	private Statement state;
	private STATUS_DB STATUS_DB_ = STATUS_DB.UNKNOWN;
	private ResultSet result;
	private ResultSetMetaData resultMeta;
	private Connection connexion;
	private static Logger logger = Logger.getLogger(DataBase.class);
	
	/**
	 * This constructor create the instance then connects to the dataBase (try to connect 20 times with 5 seconds of pause between connections error)
	 * @param DATABASE_CONFIG_ the database informations to connect
	 */
	
	public DataBase (DATABASE_CONFIG DATABASE_CONFIG_) 
	
	{
		int maxTime = 20;
		for (int i = 0; i < maxTime; i++) {
			this.connect(DATABASE_CONFIG_);
			
			if (STATUS_DB_.equals(STATUS_DB.REACHABLE)) 
			{
				break;
			}
			else 
			{
				logger.warn("Error Detected while connecting to DataBase.");
				System.exit(1);
			}
		}
	}
	
	/**
	 * Connect to the database
	 * @param DATABASE_CONFIG_ the database informations to connect
	 */
	
	private void connect(DATABASE_CONFIG DATABASE_CONFIG_) 
	{
		
		
		String url = "jdbc:" + DATABASE_CONFIG_.getDatabaseType() + "://" + DATABASE_CONFIG_.getIp() + ":" + DATABASE_CONFIG_.getPort() + "/" + DATABASE_CONFIG_.getBaseName();

		try 
		{
			if (DATABASE_CONFIG_.getDatabaseType().equals(DATABASE_TYPE.MYSQL)) 
			{
				try 
				{
					Class.forName("com.mysql.jdbc.Driver");	
				} 
				catch (ClassNotFoundException e) 
				{
					logger.warn("Class not found exception for this database type : " + DATABASE_TYPE.MYSQL);
				}
			}
			else 
			{
				try 
				{
					Class.forName("org." + DATABASE_CONFIG_.getDatabaseType() + ".Driver");	
				} 
				catch (ClassNotFoundException e) 
				{
					logger.warn("Class not found exception for this database type : "+DATABASE_CONFIG_.getDatabaseType());
				}
			}

			connexion = DriverManager.getConnection(url, DATABASE_CONFIG_.getUser(), DATABASE_CONFIG_.getPassword());
			
			connexion.setAutoCommit(false);
			
			//Create statement object
			this.state = connexion.createStatement();
			this.STATUS_DB_ = STATUS_DB.REACHABLE;

		} 
		catch (Exception e) 
		{
			logger.warn(e.getMessage());
			e.printStackTrace();
			this.STATUS_DB_ = STATUS_DB.UNREACHABLE;
		}		
	}
	
	/**
	 * Close connection to the data base
	 */
	
	public void closeConnection() {
		
		if ( ! (this.result==null)) {
			try 
			{
				this.result.close();
			}
			catch (SQLException e) 
			{
				logger.warn(e.getMessage());	
				e.printStackTrace();
			}
			catch (Exception e) 
			{
				logger.warn(e.getMessage());
				e.printStackTrace();
				this.STATUS_DB_ = STATUS_DB.UNKNOWN;
			}
		}
		
		try 
		{
			this.state.close();
			this.STATUS_DB_ = STATUS_DB.UNREACHABLE;
		} 
		catch (SQLException e) 
		{
			logger.warn(e.getMessage());
			e.printStackTrace();
			this.STATUS_DB_ = STATUS_DB.UNKNOWN;
		}
		catch (Exception e) 
		{
			logger.warn(e.getMessage());
			e.printStackTrace();
			this.STATUS_DB_ = STATUS_DB.UNKNOWN;
		}
	}
	
	/**
	 * Send an SQL request (read)
	 * @param request the SQL request
	 * @return The SQL request result
	 */
	
	public String selectAndReturnObject(String request) {

		String resultOfRequest = "";
		try 
		{
			
			//result contains the result of the request
			this.result = this.state.executeQuery(request);
			this.resultMeta = this.result.getMetaData();
			
			while(this.result.next())
			{

				for(int i = 1; i <= this.resultMeta.getColumnCount(); i++)
				{
					if (this.result.getObject(i)==null) 
					{
						System.out.print("##NULL VALUE##");                                         
					}																					
					else 																				
					{																					
						resultOfRequest += this.result.getObject(i).toString() +"/";
					}
				}
			}
		} 
		catch (SQLException e) 
		{
			logger.warn(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			logger.warn(e.getMessage());
			e.printStackTrace();
		}   

		return resultOfRequest;
		
	}
	
	/**
	 * Send an SQL request (write)
	 * @param string request the SQL request
	 * @return number of affected rows
	 */
	
	public int update(String string) 
	{
		try 
		{
			int status =this.state.executeUpdate(string);
			connexion.commit();
			
			return status;
		} 
		catch (SQLException e) 
		{
			logger.warn(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			logger.warn(e.getMessage());
			e.printStackTrace();
		}

		return -1;
	}
	
	/**
	 * Database status 
	 * @return true if connection is OK (else false)
	 */
	
	public STATUS_DB isConnectionOk() {
		return this.STATUS_DB_;
	}

}
