package com.bbe.franglaises.__main;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.bbe.franglaises.personne.Personnage;
import com.bbe.franglaises.personne.Personne;
import com.bbe.franglaises.spectacle.Spectacle;

import au.com.bytecode.opencsv.CSVReader;

public class Config {
	static {PropertyConfigurator.configure("log4j.properties");}
	public final int nbSpectacleParSemaine = 5;
	public int nb_spectacle_total = 0;
	protected Map<Integer, Spectacle> listeSpectacles = new HashMap<>();
	protected int idSpectacle = 0;

	public String[] line;
//	public DataBase dataBase = new DataBase(DATABASE_CONFIG.MYSQL);
	public String f1 = "src\\main\\resources\\global.properties";
	public String f2 = "src\\main\\resources\\dates\\";
	public File f = new File(f1);
	public File[] listOfFiles = f.listFiles();
	
	public boolean[] b0 = {true,false,false};
	public boolean[] b1 = {false,true,false};
	public boolean[] b2 = {false,false,true};
	public boolean[] b = b0;
	public int id = 0;
	public Properties prop = new Properties();;
	public String fileName;
	public CSVReader reader;
	public StringBuilder sb;
	public int nbSpectaclePersonne;
	public Map<Personnage, HashSet<Personne>> listePersonnes = new HashMap<>();

	public String getFileName2() {
		return "src\\main\\resources\\dates\\"+id+".csv";
	}
	
}
