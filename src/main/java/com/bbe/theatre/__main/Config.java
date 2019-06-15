package com.bbe.theatre.__main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.bbe.theatre.DataBase;
import com.bbe.theatre.personne.Personnage;
import com.bbe.theatre.personne.Personne;
import com.bbe.theatre.spectacle.DisponibiliteJour;
import com.bbe.theatre.spectacle.EccartTypePersistance;
import com.bbe.theatre.spectacle.Semaine;
import com.bbe.theatre.spectacle.Spectacle;
import com.bbe.theatre.spectacle.Team;

import au.com.bytecode.opencsv.CSVReader;

public class Config {
	
	private static DataBase dataBase;
	private static Properties prop;
	private static boolean exitAlgo = false;
	private static boolean testPlaning1 = false;
	private static List<Double> listeSemaines = new ArrayList<>();
	private static Map<Integer, Team> listeTeam = new HashMap<>();
	private static Map<Integer, Personne> listePersonnes2 = new HashMap<>();
	private static Map<Double,  List<Spectacle>> listeSpectacleParSemaine = new HashMap<>();
	private static int tauxMutation;	
	private static int compt;
	private static String[] personnages;
	private static String[] dateForcee;
	public static List<Double> debugIncompatibilitePersonne = new ArrayList<>();
	
	static {
		try {
			PropertyConfigurator.configure("log4j.properties");
			prop = new Properties();
			prop.load(new FileInputStream("src\\main\\resources\\global.properties"));
			for (int i = 0; i < Config.getProp().getProperty("debugIncompatibilitePersonne").trim().split(";").length; i++) {
				debugIncompatibilitePersonne.add(Double.parseDouble(Config.getProp().getProperty("debugIncompatibilitePersonne").trim().split(";")[i]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int id = 0;
	private int idTeam = 0;	
	
	private boolean addTeam = true;
	private boolean phaseCreationPersonne = false;

	private Map<Personnage, List<Personne>> listePersonnes = new HashMap<>();
	private Map<Double, List<DisponibiliteJour>> dispos = new HashMap<>();
	private Map<Double, Semaine> semaines = new HashMap<>();
	public int heureSpectacleAprem = Integer.parseInt(Config.getProp().getProperty("heureSpectacleAprem").trim().split(":")[0]);
	public int minuteSpectacleAprem = Integer.parseInt(Config.getProp().getProperty("heureSpectacleAprem").trim().split(":")[1]);
	public int heureSoir = Integer.parseInt(Config.getProp().getProperty("heureSpectacleSoir").trim().split(":")[0]);
	public int minuteSoir = Integer.parseInt(Config.getProp().getProperty("heureSpectacleSoir").trim().split(":")[1]);

	private Personne p;
	private Personnage personnage;
	private CSVReader reader;
	private EccartTypePersistance eccartTypePersistance = new EccartTypePersistance();

	private StringBuilder sb;
	private String f2 = "src\\main\\resources\\dates\\";
	private String sqlQueryDatabase = "CREATE DATABASE IF NOT EXISTS `simulation` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;\n";
	private String[] neDoitPasRencontrer;
	private String[] doitRencontrer;
	private String[] line;

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
	
	public String sqlQuery4(){
		String s = "CREATE TABLE IF NOT EXISTS `personnes` (\n"
				+"  `id_unique` int(3) NOT NULL AUTO_INCREMENT,\n"
				+"  `nom_personne` varchar(22) NOT NULL,\n"
				+"  `nom_personnage` varchar(20) NOT NULL,\n"
				+"  `id_personne` varchar(3) NOT NULL,\n"
				+"  `nb_spect_min` varchar(3) NOT NULL,\n"
				+"  `isAncien` varchar(1) NOT NULL,\n"
				+"  UNIQUE KEY `id_unique` (`id_unique`)\n"
				+") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;";
		return s;
	} 
	
	public String sqlQuery5(){
		String s = "CREATE TABLE IF NOT EXISTS `rel_team_personnes` (\n"
				+"  `id_unique` int(3) NOT NULL AUTO_INCREMENT,\n"
				+"  `id_team` varchar(5) NOT NULL,\n"
				+"  `id_personne` varchar(2) NOT NULL,\n"
				+"  UNIQUE KEY `id_unique` (`id_unique`)\n"
				+") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;";
		return s;
	} 
	
	public String sqlQuery6(){
		String s = "CREATE TABLE IF NOT EXISTS `spectacles` (\n"
				+"  `id_unique` int(3) NOT NULL AUTO_INCREMENT,\n"
				+"  `date_spectacle` varchar(16) NOT NULL,\n"
				+"  `id_team` varchar(5) NOT NULL,\n"
				+"  UNIQUE KEY `id_unique` (`id_unique`)\n"
				+") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;";
		return s;
	} 
	
	public String sqlQuery7(){
		String s = "CREATE TABLE IF NOT EXISTS `indispo` ( `id_unique` int(3) NOT NULL AUTO_INCREMENT, `id_personne` int(3) NOT NULL, `date` varchar(16) NOT NULL, `dispo` varchar(3) NOT NULL, KEY `id_personne` (`id_personne`), UNIQUE KEY `id_unique` (`id_unique`) ) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=latin1";
		return s;
	}
	
	public String getFileName2() {
		return "src\\main\\resources\\dates\\"+id+".csv";
	}

	public static int calculEccartType(int equipeCourante, int equipeSuivante) {
		Team eCourante = Config.listeTeam.get(equipeCourante);
		Team eSuivante = Config.listeTeam.get(equipeSuivante);
		compt = 0;

		eCourante.getTeamPourLeSpectacle().forEach( (personnage,p) -> {
			eSuivante.getTeamPourLeSpectacle().forEach( (personnage2,p2) -> {
				if (personnage.getNom().equals(personnage2.getNom())) {
					if (p.getId()!=p2.getId()) {
						compt++;
					}
				}
			});
		});

		return compt;
	}

	public static DataBase getDataBase() {
		return dataBase;
	}

	public static void setDataBase(DataBase dataBase) {
		Config.dataBase = dataBase;
	}

	public static boolean isExitAlgo() {
		return exitAlgo;
	}

	public static void setExitAlgo(boolean exitAlgo) {
		Config.exitAlgo = exitAlgo;
	}

	public static boolean isTestPlaning1() {
		return testPlaning1;
	}

	public static void setTestPlaning1(boolean testPlaning1) {
		Config.testPlaning1 = testPlaning1;
	}

	public static List<Double> getListeSemaines() {
		return listeSemaines;
	}

	public static void setListeSemaines(List<Double> listeSemaines) {
		Config.listeSemaines = listeSemaines;
	}

	public static Map<Integer, Personne> getListePersonnes2() {
		return listePersonnes2;
	}

	public static void setListePersonnes2(Map<Integer, Personne> listePersonnes2) {
		Config.listePersonnes2 = listePersonnes2;
	}

	public static Map<Double,  List<Spectacle>> getListeSpectacleParSemaine() {
		return listeSpectacleParSemaine;
	}

	public static void setListeSpectacleParSemaine(Map<Double,  List<Spectacle>> listeSpectacleParSemaine) {
		Config.listeSpectacleParSemaine = listeSpectacleParSemaine;
	}

	public static int getTauxMutation() {
		return tauxMutation;
	}

	public static void setTauxMutation(int tauxMutation) {
		Config.tauxMutation = tauxMutation;
	}

	public EccartTypePersistance getEccartTypePersistance() {
		return eccartTypePersistance;
	}

	public void setEccartTypePersistance(EccartTypePersistance eccartTypePersistance_) {
		eccartTypePersistance = eccartTypePersistance_;
	}

	public static String[] getPersonnages() {
		return personnages;
	}

	public static void setPersonnages(String[] personnages) {
		Config.personnages = personnages;
	}

	public static Map<Integer, Team> getListeTeam() {
		return listeTeam;
	}

	public static void setListeTeam(Map<Integer, Team> listeTeam) {
		Config.listeTeam = listeTeam;
	}

	public static Properties getProp() {
		return prop;
	}

	public static void setProp(Properties prop) {
		Config.prop = prop;
	}

	public int getIdTeam() {
		return idTeam;
	}

	public void setIdTeam(int idTeam) {
		this.idTeam = idTeam;
	}

	public boolean isAddTeam() {
		return addTeam;
	}

	public void setAddTeam(boolean addTeam) {
		this.addTeam = addTeam;
	}

	public boolean isPhaseCreationPersonne() {
		return phaseCreationPersonne;
	}

	public void phaseCreationPersonne(boolean test) {
		this.phaseCreationPersonne = test;
	}

	public Map<Personnage, List<Personne>> getListePersonnes() {
		return listePersonnes;
	}

	public void setListePersonnes(Map<Personnage, List<Personne>> listePersonnes) {
		this.listePersonnes = listePersonnes;
	}

	public Map<Double, List<DisponibiliteJour>> getDispos() {
		return dispos;
	}

	public void setDispos(Map<Double, List<DisponibiliteJour>> dispos) {
		this.dispos = dispos;
	}

	public Map<Double, Semaine> getSemaines() {
		return semaines;
	}

	public void setSemaines(Map<Double, Semaine> semaines) {
		this.semaines = semaines;
	}

	public Personne getP() {
		return p;
	}

	public void setP(Personne p) {
		this.p = p;
	}

	public Personnage getPersonnage() {
		return personnage;
	}

	public void setPersonnage(Personnage personnage) {
		this.personnage = personnage;
	}

	public CSVReader getReader() {
		return reader;
	}

	public void setReader(CSVReader reader) {
		this.reader = reader;
	}

	public StringBuilder getSb() {
		return sb;
	}

	public void setSb(StringBuilder sb) {
		this.sb = sb;
	}

	public String getF2() {
		return f2;
	}

	public void setF2(String f2) {
		this.f2 = f2;
	}

	public String getSqlQueryDatabase() {
		return sqlQueryDatabase;
	}

	public void setSqlQueryDatabase(String sqlQueryDatabase) {
		this.sqlQueryDatabase = sqlQueryDatabase;
	}

	public String[] getNeDoitPasRencontrer() {
		return neDoitPasRencontrer;
	}

	public void setNeDoitPasRencontrer(String[] neDoitPasRencontrer) {
		this.neDoitPasRencontrer = neDoitPasRencontrer;
	}

	public String[] getLine() {
		return line;
	}

	public void setLine(String[] line) {
		this.line = line;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String[] getDoitRencontrer() {
		return doitRencontrer;
	}

	public void setDoitRencontrer(String[] doitRencontrer) {
		this.doitRencontrer = doitRencontrer;
	}

	public static String[] getDateForcee() {
		return dateForcee;
	}

	public static void setDateForcee(String[] dateForcee) {
		if ( ! dateForcee[0].equals("")) {
			Config.dateForcee = dateForcee;
		}
	}

	public Integer getHeureSpectacleAprem() {
		return heureSpectacleAprem;
	}

	public Integer getHeureSoir() {
		return heureSoir;
	}
	
}
