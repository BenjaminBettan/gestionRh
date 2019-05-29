package com.bbe.theatre.__main;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;

import com.bbe.theatre.DataBase;
import com.bbe.theatre.personne.Personnage;
import com.bbe.theatre.personne.Personne;
import com.bbe.theatre.spectacle.DisponibiliteJour;
import com.bbe.theatre.spectacle.Semaine;
import com.bbe.theatre.spectacle.Spectacle;
import com.bbe.theatre.spectacle.Team;

import au.com.bytecode.opencsv.CSVReader;

public class Config {
	public static Properties prop;
	public static boolean testPlaning1 = false;
	public static List<Double> listeSemaines = new ArrayList<>();
	public static Map<Integer, Team> listeTeam = new HashMap<>();
	public static Map<Integer, Personne> listePersonnes2 = new HashMap<>();
	public static int normeCritereNB_SPECTACLE_MIN;
	public static int normeCritereECCART_TYPE;

	static {
		PropertyConfigurator.configure("log4j.properties");
		prop = new Properties();
		try {
			prop.load(new FileInputStream("src\\main\\resources\\global.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		tauxMutation = Integer.parseInt(prop.getProperty("pourMilleMutation"));
	}
	
	public final int nbSpectacleParSemaine = 5;
	public int id = 0;
	public int idTeam = 0;	
	public int maxIndispoMoyenne = 0;	
	public static int tauxMutation;	
	
	public boolean addTeam = true;


	public DataBase dataBase;
	
	public StringBuilder sb;
	public String f2 = "src\\main\\resources\\dates\\";
	public String dataBaseName = "simulation"+LocalDateTime.now().toString().substring(2, 19).replace("-", "x").replace(":", "x");
	public String sqlQueryDatabase = 
			"CREATE DATABASE IF NOT EXISTS `"+dataBaseName+"` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;\n";
	public String[] personnages;
	public String[] neDoitPasRencontrer;
	public String[] line;

	public Personne p;
	public Personnage personnage;
	public CSVReader reader;
	public boolean test = false;
	
	public Map<Personnage, Set<Personne>> listePersonnes = new HashMap<>();
	public Map<Double,  Set<Spectacle>> listeSpectacleParSemaine = new HashMap<>();
	public Map<Double, List<DisponibiliteJour>> dispos = new HashMap<>();
	public Map<Double, Semaine> semaines = new HashMap<>();
	


	public String sqlQuery2(String nomUserTable){
		String s = "CREATE TABLE IF NOT EXISTS `"+nomUserTable+"` (\n"
				+"  `id_unique` int(3) NOT NULL AUTO_INCREMENT,\n"
				+"  `id_personne` varchar(3) NOT NULL,\n"
				+"  UNIQUE KEY `id_unique` (`id_unique`)\n"
				+") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;";
		return s;
	} 

	public String sqlQuery3(){
		String s = "CREATE TABLE IF NOT EXISTS `listeEquipe` (\n"
				+"  `id_unique` int(3) NOT NULL AUTO_INCREMENT,\n"
				+"  UNIQUE KEY `id_unique` (`id_unique`)\n"
				+") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;";
		return s;
	} 

	public String getFileName2() {
		return "src\\main\\resources\\dates\\"+id+".csv";
	}

}
