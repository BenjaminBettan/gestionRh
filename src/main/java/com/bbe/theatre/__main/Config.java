package com.bbe.theatre.__main;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;

import com.bbe.theatre.DataBase;
import com.bbe.theatre.personne.Personnage;
import com.bbe.theatre.personne.Personne;
import com.bbe.theatre.spectacle.AssoDispoPersonnage;
import com.bbe.theatre.spectacle.Spectacle;
import com.bbe.theatre.spectacle.Team;

import au.com.bytecode.opencsv.CSVReader;

public class Config {

	static {PropertyConfigurator.configure("log4j.properties");}

	public DataBase dataBase = new DataBase();
	public String f1 = "src\\main\\resources\\global.properties";
	public String f2 = "src\\main\\resources\\dates\\";
	public File f = new File(f1);
	protected int idSpectacle = 0;
	public String fileName;
	public int id = 0;
	public int idTeam = 0;

	public String[] line;
	public File[] listOfFiles = f.listFiles();	
	public Map<Personnage, HashSet<Personne>> listePersonnes = new HashMap<>();
	public Map<Integer, Team> listeTeam = new HashMap<>();
	public Map<AssoDispoPersonnage, HashSet<Personne>> assoDispoPersonnage = new HashMap<>();
	public int nb_spectacle_total = 0;
	public final int nbSpectacleParSemaine = 5;
	public int nbSpectaclePersonne;
	public Personne p;
	public Properties prop = new Properties();;
	public CSVReader reader;
	public StringBuilder sb;
	public String[] personnages;
	public String[] doitRencontrer;
	public String[] neDoitPasRencontrer;
	public Map<Integer,  Set<Spectacle>> listeSpectacleParSemaine = new HashMap<>();
	public Map<Integer,  Set<Integer>> mapNumSemaineTeamDispo = new HashMap<>();
	public Queue<Integer> listeSemaines = new LinkedList<>();
	public String dataBaseName = "simulation"+LocalDateTime.now().toString().substring(2, 19).replace("-", "x").replace(":", "x");
	public String sqlQueryDatabase = 
			"CREATE DATABASE IF NOT EXISTS `"+dataBaseName+"` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;\n";
	
	public String sqlQueryDatabase2(String nomUserTable){
		String s = "CREATE TABLE IF NOT EXISTS `"+nomUserTable+"` (\n"
				+"  `id_unique` int(3) NOT NULL AUTO_INCREMENT,\n"
				+"  `id_personne` varchar(3) NOT NULL,\n"
				+"  UNIQUE KEY `id_unique` (`id_unique`)\n"
				+") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;";
		return s;
	} 
	
	public String getFileName2() {
		return "src\\main\\resources\\dates\\"+id+".csv";
	}

}
