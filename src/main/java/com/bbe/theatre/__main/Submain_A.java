package com.bbe.theatre.__main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.bbe.theatre.DataBase;
import com.bbe.theatre._enum.DISPO;
import com.bbe.theatre.personne.Personnage;
import com.bbe.theatre.personne.Personne;
import com.bbe.theatre.spectacle.DisponibiliteJour;
import com.bbe.theatre.spectacle.EccartTypePersistance;
import com.bbe.theatre.spectacle.Semaine;
import com.bbe.theatre.spectacle.Spectacle;
import com.bbe.theatre.spectacle.Team;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSet;

import au.com.bytecode.opencsv.CSVReader;

public class Submain_A {

	protected static Logger logger = Logger.getLogger(Submain_A.class);
	protected Config c = new Config();
	private EccartTypePersistance eccart = new EccartTypePersistance();

	protected void init() throws IOException, SQLException {

		long l = System.currentTimeMillis();

		logger.info("init primaire : on connecte la base de donnée");
		initPrimaire();

		logger.info("on lit les dates de l'utilisateur 0");

		remplirListeSemaines();

		logger.info("on charge les contraintes des joueurs");

		chargementContraintesUtilisateur();

		logger.info("on créé les personnages");

		creationPersonnages();

		logger.info("on créé les personnes");

		creationPersonnes();

		logger.info("on calcule les équipes");

		calculTeams();

		affichage();

		logger.info("TEMPS DE CALCUL INIT : "+ (System.currentTimeMillis() - l) + "ms");

		logger.info("Entrez q sur la console Eclipse pour quitter");

		Thread thread = new Thread(){
			public void run(){
				String userInput = "";
				while ( ! userInput.toUpperCase().equals("Q")) {
					Scanner reader = new Scanner(System.in);  // Reading from System.in
					System.out.println("Entrez q pour quitter et validez ensuite");
					userInput = reader.next();
					reader.close();
				}
				Config.setExitAlgo(true);
			}
		};

		thread.start();
	}

	private void initPrimaire() throws FileNotFoundException, IOException, SQLException {

		Config.setDataBase(new DataBase());//on connecte la db
		cleanDb();
		//creation de la DB
		Config.getDataBase().update(c.getSqlQueryDatabase());
		Config.getDataBase().setBaseNameAndConnect("simulation");
		Config.getDataBase().update(c.sqlQuery3());
		Config.getDataBase().update(c.sqlQuery4());
		Config.getDataBase().update(c.sqlQuery5());
		Config.getDataBase().update(c.sqlQuery6());
		Config.getDataBase().update(c.sqlQuery7());
		Config.getDataBase().update("ALTER TABLE `rel_team_personnes` ADD INDEX(`id_team`);");
		Config.getDataBase().update("ALTER TABLE `personnes` ADD INDEX(`id_personne`);");
		Config.getDataBase().update("ALTER TABLE `spectacles` ADD INDEX(`date_spectacle`);");
	}

	private void creationPersonnes() throws IOException {

		c.phaseCreationPersonne(true);

		for (c.setId(0); c.getId() < Integer.parseInt(Config.getProp().getProperty("effectifComediens").trim()); c.setId(c.getId() +1)) {
			creationPersonne();
			chargeFichierDateDeCettePersonne();
		}
	}

	private void creationPersonne() {
		int idRole = Integer.parseInt(Config.getProp().getProperty(c.getId()+".role").trim());
		c.getListePersonnes().forEach((personnage, map) -> {
			if (personnage.getId()==idRole) {
				c.setPersonnage(personnage);
				logger.debug("Création de l'utilisateur " + c.getId());
				c.setP(new Personne().setNbSpectacleMin(Integer.parseInt(Config.getProp().getProperty(c.getId()+".nbSpectacle").trim()))
						.setId(c.getId()).setNomActeur(Config.getProp().getProperty(c.getId()+".nom").trim())
						.setPersonnage(personnage)
						.setPersonnage(personnage).setPersonneAvecQuiJeDoisJouer(calculDoitRencontrer(c.getDoitRencontrer()))
						.setPersonneAvecQuiJeNeDoisPasJouer(calculDoitRencontrer(c.getNeDoitPasRencontrer()))
						.setAncien(new Boolean(Config.getProp().getProperty(c.getId()+".isAncien").trim())));
				Config.getListePersonnes2().put(c.getId(), c.getP());
				map.add(c.getP());
				Config.getDataBase().update("INSERT INTO `personnes` (`id_unique`, `nom_personne`,`nom_personnage`, `id_personne`, `nb_spect_min`, `isAncien`) VALUES (NULL, '"+c.getP().getNomActeur()+"', '"+c.getP().getPersonnage().getNom()+"', '"+c.getP().getId()+"', '"+c.getP().getNbSpectacleMin()+"', '"+(c.getP().isAncien() ? "1":"0")+"');");
			}
		});		
	}

	private String calculDoitRencontrer(String[] doitRencontrer) {
		StringBuilder idDoitRencontrer = new StringBuilder();
		for (String string : doitRencontrer) {
			if ( ! string.equals("")) {
				if (c.getId()==Integer.parseInt(string.trim().split(";")[0].trim())) {
					idDoitRencontrer.append(string.trim().split(";")[1].trim()+",");
				}
			}
		}

		return idDoitRencontrer.toString();
	}

	private void chargementContraintesUtilisateur() {

		String[] s = Config.getProp().getProperty("neDoitPasRencontrer").trim().replace("{", "").replace("}", "").split(",");

		if ( ! (s.length==1 && s.equals("") ) ) {
			c.setNeDoitPasRencontrer(s);
		}

		s = Config.getProp().getProperty("doitRencontrer").trim().replace("{", "").replace("}", "").split(",");

		if ( ! (s.length==1 && s.equals("") ) ) {
			c.setDoitRencontrer(s);
		}

		Config.setDateForcee(Config.getProp().getProperty("dateForcee").trim().replace("{", "").replace("}", "").split(","));
	}

	private void creationPersonnages() {
		Config.setPersonnages(Config.getProp().getProperty("listePersonnage").trim().split(","));

		for (int i = 0; i < Config.getPersonnages().length; i++) {
			String s;
			Personnage p = new Personnage(i,Config.getPersonnages()[i]);
			c.getListePersonnes().put(p, new ArrayList<>());
			if (i==0) {
				s = "id_unique";
			}
			else {
				s = Config.getPersonnages()[i-1];
			}
			Config.getDataBase().update("ALTER TABLE `listeequipe` ADD `"+Config.getPersonnages()[i]+"` VARCHAR(2) NOT NULL AFTER `"+s+"`;");
		}
		Config.getDataBase().update("ALTER TABLE `listeequipe` ADD `ok` VARCHAR(1) NOT NULL AFTER `"+Config.getPersonnages()[Config.getPersonnages().length-1]+"`;");
	}

	/** permet de remplir la map listeSpectacle
	 * @throws IOException
	 */
	private void remplirListeSemaines() throws IOException {
		chargeFichierDateDeCettePersonne();//utilisateur 0
		String[] listeDates = c.getSb().toString().split("\n");

		for (String l : listeDates) {
			String[] listesDates_ = l.split(";");

			for (int j = 0; j < Integer.parseInt(listesDates_[1]); j++) {

				LocalDateTime t = LocalDateTime.of(
						Integer.parseInt("20"+listesDates_[0].split("/")[2]), 
						Integer.parseInt(listesDates_[0].split("/")[1]), 
						Integer.parseInt(listesDates_[0].split("/")[0]), 
						Integer.parseInt(listesDates_[1]) > 1 ?  (j == 0 ? c.heureSpectacleAprem : c.heureSoir ) : c.heureSoir , 
						Integer.parseInt(listesDates_[1]) > 1 ?  (j == 0 ? c.minuteSpectacleAprem : c.minuteSoir ) : c.minuteSoir);

				int numeroSemaine = t.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
				if (numeroSemaine==1) {
					if (t.getMonthValue()==12) {
						t.plusYears(1);
					}
				}
				double numSemaine = Double.parseDouble(numeroSemaine+"."+t.getYear());
				if ( ! Config.getListeSemaines().contains(numSemaine)) {
					Config.getListeSemaines().add(numSemaine);
					Config.getListeSpectacleParSemaine().put(numSemaine,new ArrayList<>());
					c.getDispos().put(numSemaine, new ArrayList<>() );
				}

				Config.getListeSpectacleParSemaine().get(numSemaine).add(new Spectacle(t));

			}
		}
	}

	/** vide la DB
	 * @throws SQLException
	 */
	protected void cleanDb() throws SQLException {
		Config.getDataBase().connect();

		DatabaseMetaData dbmd = (DatabaseMetaData) Config.getDataBase().getConnexion().getMetaData();

		ResultSet ctlgs = (ResultSet) dbmd.getCatalogs();

		while (ctlgs.next()){
			if (ctlgs.getString(1).length()> 5) {
				if (ctlgs.getString(1).substring(0, 5).equals("simul")) {
					Config.getDataBase().update("DROP DATABASE "+ctlgs.getString(1)+";");
					logger.debug("DROP DATABASE '"+ctlgs.getString(1)+"';");
				}				
			}
		}		
	}

	/** Charge l'utilisateur c.id et mets le resultat dans c.sb
	 * @throws IOException
	 */
	private void chargeFichierDateDeCettePersonne() throws IOException {
		c.setSb(new StringBuilder());
		c.setReader(new CSVReader(new FileReader(c.getF2() + Integer.toString(c.getId()) + ".csv")));

		while (true) {
			c.setLine(c.getReader().readNext());
			if (c.getLine()==null) {
				c.getReader().close();
				break;//fin du fichier
			}
			// on filtre les données inutiles. On aura une ligne par date
			if (c.getLine()[0].equals("")) {
				continue;
			}else if (c.getLine()[0].substring(0,3).equals("Dat")) {
				continue;
			}else if (c.getLine()[0].substring(0,3).equals("Sam")) {
				continue;
			}else if (c.getLine()[0].equals("Nombre souhaité de semaines")) {
				continue;
			}else if (c.getLine()[0].equals("Nombre total de semaines ")) {
				continue;
			}
			else if (c.getLine()[1].equals("")) {
				continue;
			}else if (c.getLine()[0].trim().length()>3) {
				if (c.getLine()[0].trim().substring(0,3).equals("Ind")) {
					continue;
				}
			}

			c.getSb().append(c.getLine()[0].trim()+";"+c.getLine()[1].trim()+";"+c.getLine()[2].trim()+";\n");
		}
		if (c.isPhaseCreationPersonne()) {
			String[] listeDates = c.getSb().toString().split("\n");
			for (String l : listeDates) {
				String[] listesDates_ = l.split(";");

				for (int j = 0; j < Integer.parseInt(listesDates_[1]); j++) {

					LocalDateTime t = LocalDateTime.of(Integer.parseInt("20"+listesDates_[0].split("/")[2]), 
							Integer.parseInt(listesDates_[0].split("/")[1]), 
							Integer.parseInt(listesDates_[0].split("/")[0]), 
							Integer.parseInt(listesDates_[1]) > 1 ?  (j == 0 ? 16 : 21 ) : 21 , 0);
					int numeroSemaine = t.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
					if (numeroSemaine==1) {
						if (t.getMonthValue()==12) {
							t.plusYears(1);
						}
					}

					double numSemaine = Double.parseDouble(numeroSemaine+"."+t.getYear());

					DisponibiliteJour d;

					if (listesDates_[2].equals("1")) {
						d = new DisponibiliteJour(c.getP(), t,c.getPersonnage(),DISPO.DISPO,numSemaine);
						Config.getDataBase().update("INSERT INTO `indispo` (`id_unique`, `id_personne`, `date`, `dispo`) VALUES (NULL, '"+ c.getP().getId() +"', '"+t+"', '1');");
					}
					else if (listesDates_[2].equals("0")) {
						d = new DisponibiliteJour(c.getP().addIndispo(numSemaine), t,c.getPersonnage(),DISPO.PAS_DISPO,numSemaine);
						Config.getDataBase().update("INSERT INTO `indispo` (`id_unique`, `id_personne`, `date`, `dispo`) VALUES (NULL, '"+ c.getP().getId() +"', '"+t+"', '0');");
						for (Double d_ : Config.debugIncompatibilitePersonne) {
							if (numSemaine==d_) {
								logger.info("Semaine "+d_.intValue()+" " + Double.valueOf(d_*10000 - d_.intValue()*10000).intValue() +"> ID PERSONNE "+c.getP().getId() + " est absent le " + t);
							}
						}
						
					}
					else {
						d = new DisponibiliteJour(c.getP(), t,c.getPersonnage(),DISPO.MOYEN_DISPO,numSemaine);
						Config.getDataBase().update("INSERT INTO `indispo` (`id_unique`, `id_personne`, `date`, `dispo`) VALUES (NULL, '"+ c.getP().getId() +"', '"+t+"', '0,5');");
					}
					c.getDispos().get(numSemaine).add(d);
				}
			}
		}
	}

	private void affichageTeams() {
		logger.debug("Affichage des teams");
		Config.getListeTeam().forEach((i, team) -> {
			logger.debug(i+" ");
			logger.debug(team);
		});	
	}

	private void calculTeams() {
		c.getListePersonnes().forEach((personnage, mapPersonnes) -> {
			Config.getDataBase().update(c.sqlQuery2(personnage.getNom()));

		});

		c.getListePersonnes().forEach((personnage, mapPersonnes) -> {

			for (Personne personne : mapPersonnes) {
				Config.getDataBase().update("INSERT INTO `" + personnage.getNom() + "` (`id_unique`, `id_personne`) VALUES (NULL, '"+personne.getId()+"');");
			}
		});
		calculDuCrossJoin();

		String[] result = Config.getDataBase().select(c.getSb().toString()).split("\n");

		for (String s : result) {
			c.setSb(new StringBuilder("INSERT INTO `listeequipe` VALUES (NULL, "));
			String[] result2 = s.split("/");
			for (String s2 : result2) {
				c.getSb().append("'"+s2+"',");
			}
			Config.getDataBase().update(c.getSb().toString().substring(0, c.getSb().toString().length()-1)+",'1');");
		}


		Config.getListePersonnes2().forEach((id, personne) -> {
			if ( ! personne.getPersonneAvecQuiJeNeDoisPasJouer().equals("")) {
				String[] p = personne.getPersonneAvecQuiJeNeDoisPasJouer().split(",");
				Config.getDataBase().update("UPDATE LISTEEQUIPE SET OK='T' WHERE OK='1' AND " + personne.getPersonnage().getNom() +" ='" + personne.getId()+"';");
				for (String string : p) {
					Config.getDataBase().update("UPDATE LISTEEQUIPE SET OK='0' WHERE OK='T' AND " + Config.getListePersonnes2().get(Integer.parseInt(string)).getPersonnage().getNom() +" ='" + string +"';");
				}
				Config.getDataBase().update("UPDATE LISTEEQUIPE SET OK='1' WHERE OK='T';");
			}
		});

		Config.getListePersonnes2().forEach((id, personne) -> {
			if ( ! personne.getPersonneAvecQuiJeDoisJouer().equals("")) {
				String[] p = personne.getPersonneAvecQuiJeDoisJouer().split(",");
				Config.getDataBase().update("UPDATE LISTEEQUIPE SET OK='0' WHERE OK='1' AND (" + Config.getListePersonnes2().get(Integer.parseInt(p[0])).getPersonnage().getNom() +" ='" + Config.getListePersonnes2().get(Integer.parseInt(p[0])).getId()+"' XOR " + personne.getPersonnage().getNom() +" ='" + id +"');");
			}
		});
		
		String[] l = Config.getDataBase().select("SELECT * FROM LISTEEQUIPE WHERE OK=1;").split("\n");
		for (String s : l) {

			String[] s2 = s.split("/");
			Map<Personnage, Personne> teamPourLeSpectacle = new HashMap<>();
			for (int i = 1; i < s2.length - 1; i++) {
				teamPourLeSpectacle.put(Config.getListePersonnes2().get(Integer.parseInt(s2[i])).getPersonnage(), Config.getListePersonnes2().get(Integer.parseInt(s2[i])));
			}
			Config.getListeTeam().put(Integer.parseInt(s2[0]), new Team(Integer.parseInt(s2[0]),teamPourLeSpectacle));
		}

		Config.getListeSemaines().forEach((numSemaine) -> {
			c.getSemaines().put(numSemaine, new Semaine(Config.getListeSpectacleParSemaine().get(numSemaine).size(),numSemaine));
		});

		Config.getListeSemaines().forEach((numSemaine) -> {
			Semaine sem = c.getSemaines().get(numSemaine);
			List<DisponibiliteJour> lDispos = c.getDispos().get(numSemaine);
			Config.getListePersonnes2().forEach((id,pers) -> {
				pers.setEstDispoCetteSemaine(true);
				for (DisponibiliteJour d : lDispos) {
					if (d.getPersonne().getId()==id) {//si c est ma personne
						if (d.getDispo().equals(DISPO.PAS_DISPO)) {
							pers.setEstDispoCetteSemaine(false);
							break;
						}
					}
				}
			});

			Config.getListeTeam().forEach((idTeam,t) -> {
				c.setAddTeam(true);
				t.getTeamPourLeSpectacle().forEach((idpers,pers2) -> {
					if ( ! pers2.estDispoCetteSemaine() ) {
						c.setAddTeam(false);
					}
				});
				if (c.isAddTeam()) {
					sem.addTeam(idTeam);
				}
			});
		});


		
		if (Boolean.parseBoolean(Config.getProp().getProperty("precalculsA_Faire"))) {

			File file = new File(Config.getProp().getProperty("dossierPrecalcul"));
			try {
				FileUtils.deleteDirectory(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<Integer> l2 = new ArrayList<>();
			logger.error(Config.getProp().getProperty("dateForcee").trim().equals(""));
			Arrays.asList(Config.getProp().getProperty("dateForcee").trim().replace("{", "").replace("}", "").split(","));
			if ( ! Config.getProp().getProperty("dateForcee").trim().equals("") ) {
				logger.error(Config.getProp().getProperty("dateForcee").trim().replace("{", "").replace("}", "").split(","));
				String[] s_ = Config.getProp().getProperty("dateForcee").trim().replace("{", "").replace("}", "").split(",");
				for (String string : s_) {
					String s2 = string.split(";")[1];
					if ( ! l2.contains(Integer.parseInt(s2)) ) {
						if ( ! Config.getListeTeam().containsKey(Integer.parseInt(s2))) {
							l2.add(Integer.parseInt(s2));
						}
					}
				}
			}
			for (Integer id : l2) {
				String[] l3 = Config.getDataBase().select("SELECT * FROM LISTEEQUIPE WHERE id_unique="+id+";").split("\n");

				
				for (String s : l3) {

					String[] s2 = s.split("/");
					Map<Personnage, Personne> teamPourLeSpectacle = new HashMap<>();
					for (int i = 1; i < s2.length - 1; i++) {
						teamPourLeSpectacle.put(Config.getListePersonnes2().get(Integer.parseInt(s2[i])).getPersonnage(), Config.getListePersonnes2().get(Integer.parseInt(s2[i])));
					}
					Config.getListeTeam().put(id, new Team(Integer.parseInt(s2[0]),teamPourLeSpectacle));
				}
			}
			
			Config.getListeTeam().forEach((idTeam,t) -> {
			Config.getListeTeam().forEach((idTeam2,t2) -> {
				if (idTeam!=idTeam2) {
					eccart.setEccartTypePersistance(idTeam,idTeam2,Config.calculEccartType(idTeam, idTeam2));
				}
			});
		});

		}
		
		String res = Config.getDataBase().select("SELECT * FROM listeequipe");
		
		for (String s : res.split("\n")) {
			String[] ll = s.split("/");
			for (int i = 1; i < ll.length - 1; i++) {
				Config.getDataBase().update("INSERT INTO `rel_team_personnes` (`id_unique`, `id_team`, `id_personne`) VALUES (NULL, '"+ll[0]+"', '"+ll[i]+"');");	
			}
		}
		
		c.getSemaines().forEach((idSemaine,sem) -> {
			if (sem.getTeam().size()==0) {
				for (String personnage : Config.getPersonnages()) {
					Config.getListePersonnes2().forEach((id,p)->{
						if (personnage.equals(p.getPersonnage())) {
							if (p.isDispo(idSemaine)) {
								logger.info("Semaine : " + idSemaine + p.getNomActeur() + "est present (id="+p.getId()+")");
							}
						}
					});
				}
			}
		});

	}

	private void calculDuCrossJoin() {
		c.setSb(new StringBuilder("SELECT "));
		c.setId(0);
		for (String s : Config.getPersonnages()) {
			if (c.getId()==0) {
				c.getSb().append(s+".id_personne as "+s);
				c.setId(c.getId() +1);
			}else {
				c.getSb().append(", "+s+".id_personne as "+s);
			}
		}




		c.setId(0);

		c.getListePersonnes().forEach((personnage, mapPersonnes) -> {
			if (c.getId()==0) {
				c.getSb().append(" FROM " + personnage.getNom());	
				c.setId(c.getId() +1);
			}else {
				c.getSb().append( " CROSS JOIN "+personnage.getNom());	
			}
		});

	}

	private void affichagePersonnes() {
		logger.info("Affichage de la fine équipe");
		Config.getListePersonnes2().forEach((id, p) -> {
			logger.info(p);
		});
	}

	private void affichage() {
		affichagePersonnes();
		affichageTeams();
		logger.info("Liste des semaines : "+Config.getListeSemaines());
		Config.getListeSemaines().forEach((l) -> {
			if (c.getSemaines().get(l)!=null) {
				logger.info("Numéro de semaine : "+l);
				logger.info(c.getSemaines().get(l).getTeam().size());
			}
		});
	}

}

