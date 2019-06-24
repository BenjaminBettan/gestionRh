package com.bbe.theatre;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.bbe.theatre._enum.DATABASE_CONFIG;
import com.bbe.theatre._enum.DATABASE_TYPE;
import com.bbe.theatre._enum.STATUS_DB;


/**
 * Connect to your database and execute requests
 */

public class DataBase {

	private Statement state;
	private STATUS_DB STATUS_DB_ = STATUS_DB.UNKNOWN;
	private ResultSet result;
	private ResultSetMetaData resultMeta;
	private Connection connexion;
	private DATABASE_CONFIG dataBaseConf;
	private static Logger logger = Logger.getLogger(DataBase.class);

	/**
	 * Connect to the database
	 * @param DATABASE_CONFIG_ the database informations to connect
	 */

	public DataBase setBaseNameAndConnect(String s){
		dataBaseConf.setBaseName(s);
		connect(this.dataBaseConf);
		return this;
	}

	public DataBase connect() {
		dataBaseConf = DATABASE_CONFIG.MYSQL;
		connect(dataBaseConf);
		return this;
	}

	private void connect(DATABASE_CONFIG DATABASE_CONFIG_) 
	{
		boolean exit = false;

		for (int i = 0; ! exit; i++) {

			if (i>200) {
				System.exit(1);
			}
			if (DATABASE_CONFIG_==null) {
				DATABASE_CONFIG_ = DATABASE_CONFIG.MYSQL;

			}
			dataBaseConf = DATABASE_CONFIG_;
			String url = "jdbc:" + dataBaseConf.getDatabaseType() + "://" + dataBaseConf.getIp() + ":" + dataBaseConf.getPort() + "/" + dataBaseConf.getBaseName();

			try 
			{
				if (dataBaseConf.getDatabaseType().equals(DATABASE_TYPE.MYSQL)) 
				{
					try 
					{
						Class.forName("com.mysql.jdbc.Driver");	
					} 
					catch (ClassNotFoundException e) 
					{
						logger.warn("Class not found exception for this database type : " + DATABASE_TYPE.MYSQL);
						System.exit(1);
					}
				}
				else 
				{
					try 
					{
						Class.forName("org." + dataBaseConf.getDatabaseType() + ".Driver");	
					} 
					catch (ClassNotFoundException e) 
					{
						logger.warn("Class not found exception for this database type : "+DATABASE_CONFIG_.getDatabaseType());
						System.exit(1);
					}
				}

				connexion = DriverManager.getConnection(url, DATABASE_CONFIG_.getUser(), DATABASE_CONFIG_.getPassword());

				connexion.setAutoCommit(false);

				//Create statement object
				this.state = connexion.createStatement();
				this.STATUS_DB_ = STATUS_DB.REACHABLE;
				exit = true;
			} 
			catch (Exception e) 
			{

				logger.warn(e.getMessage());
				System.out.println("Veuillez redemarer les services - le bouton vert (sauf si vous l avez deja fait). Programme en pause 30 secondes.");

				try {
					Thread.sleep(30_000);
				} catch (InterruptedException e1) {
				}
				connect(DATABASE_CONFIG_);
				this.STATUS_DB_ = STATUS_DB.UNREACHABLE;
			}		

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

	public String select(String request) {

		StringBuilder resultOfRequest = new StringBuilder("");
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
						resultOfRequest.append(this.result.getObject(i).toString() +"/");
					}
				}
				resultOfRequest.append("\n");
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

		return resultOfRequest.toString();

	}

	/**
	 * Send an SQL request (write)
	 * @param string request the SQL request
	 * @return number of affected rows
	 */

	public int update(String string){
		try 
		{
			int status =this.state.executeUpdate(string);
			connexion.commit();

			return status;
		} 
		catch (Exception e) 
		{
			logger.warn(e.getMessage());
			connect();
			int status = update(string);
			return status;
		}
	}

	/**
	 * Database status 
	 * @return true if connection is OK (else false)
	 */

	public STATUS_DB isConnectionOk() {
		return this.STATUS_DB_;
	}

	public Connection getConnexion() {
		return connexion;
	}

}
