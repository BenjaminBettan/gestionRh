package com.bbe.theatre.__main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.log4j.Logger;
import com.bbe.theatre.DataBase;
import com.bbe.theatre._enum.DISPO;
import com.bbe.theatre.personne.Personnage;
import com.bbe.theatre.personne.Personne;
import com.bbe.theatre.spectacle.DisponibiliteJour;
import com.bbe.theatre.spectacle.Semaine;
import com.bbe.theatre.spectacle.Spectacle;
import com.bbe.theatre.spectacle.Team;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSet;

import au.com.bytecode.opencsv.CSVReader;

public class Submain_A {

	protected static Logger logger = Logger.getLogger(Submain_A.class);
	protected Config c = new Config();

	protected void init() throws IOException, SQLException {
		
		long l = System.currentTimeMillis();

		logger.info("init primaire : on connecte la base de donnée");
		initPrimaire();

		logger.info("on lit les dates de l'utilisateur 0");

		remplirListeSemaines();

		logger.info("on charge les contraintes des joueurs");

		chargementContraintesJoueurs();

		logger.info("on créé les personnages");

		creationPersonnages();

		logger.info("on créé les personnes");

		creationPersonnes();

		logger.info("on calcul les équipes");

		calculTeams();

		affichage();
		
		logger.info("TEMPS DE CALCUL INIT : "+ (System.currentTimeMillis() - l) + "ms");

	}

	private void initPrimaire() throws FileNotFoundException, IOException, SQLException {
		c.dataBase = new DataBase();//on connecte la db
		cleanDb();
		//creation de la DB
		c.dataBase.update(c.sqlQueryDatabase);
		c.dataBase.setBaseName(c.dataBaseName);
		c.dataBase.update(c.sqlQuery3());
	}

	private void creationPersonnes() throws IOException {
		for (c.id = 0; c.id < Integer.parseInt(Config.prop.getProperty("effectifComediens").trim()); c.id++) {
			creationPersonne();
			chargeFichierDateDeCettePersonne();
		}
		
	}

	private void creationPersonne() {
		int idRole = Integer.parseInt(Config.prop.getProperty(c.id+".role").trim());
		c.listePersonnes.forEach((personnage, map) -> {
			if (personnage.getId()==idRole) {
				c.personnage = personnage;
				logger.debug("Création de l'utilisateur " + c.id);
				c.p = new Personne().setNbSpectacleMin(Integer.parseInt(Config.prop.getProperty(c.id+".nbSpectacle").trim()))
						.setId(c.id).setNomActeur(Config.prop.getProperty(c.id+".nom").trim())
						.setPersonnage(personnage)
						//.setPersonnage(personnage).setPersonneAvecQuiJeDoisJouer(calculDoitRencontrer(c.doitRencontrer))
						.setPersonneAvecQuiJeNeDoisPasJouer(calculDoitRencontrer(c.neDoitPasRencontrer));
				Config.listePersonnes2.put(c.id, c.p);
				map.add(c.p);
			}
		});		
	}

	private String calculDoitRencontrer(String[] doitRencontrer) {
		StringBuilder idDoitRencontrer = new StringBuilder();
		for (String string : doitRencontrer) {
			if (c.id==Integer.parseInt(string.trim().split(";")[0].trim())) {
				idDoitRencontrer.append(string.trim().split(";")[1].trim()+",");
			}
		}

		return idDoitRencontrer.toString();
	}

	private void chargementContraintesJoueurs() {
		//		c.doitRencontrer =
		c.neDoitPasRencontrer = Config.prop.getProperty("neDoitPasRencontrer").trim().replace("{", "").replace("}", "").split(",");
	}

	private void creationPersonnages() {
		Config.personnages = Config.prop.getProperty("listePersonnage").trim().split(",");

		for (int i = 0; i < Config.personnages.length; i++) {
			String s;
			Personnage p = new Personnage(i,Config.personnages[i]);
			c.listePersonnes.put(p, new HashSet<>());
			if (i==0) {
				s = "id_unique";
			}
			else {
				s = Config.personnages[i-1];
			}
			c.dataBase.update("ALTER TABLE `listeequipe` ADD `"+Config.personnages[i]+"` VARCHAR(2) NOT NULL AFTER `"+s+"`;");
			//			ALTER TABLE `listeequipe` ADD `jonathan` VARCHAR(2) NOT NULL AFTER `s`;

		}
		c.dataBase.update("ALTER TABLE `listeequipe` ADD `ok` VARCHAR(1) NOT NULL AFTER `"+Config.personnages[Config.personnages.length-1]+"`;");
	}

	/** permet de remplir la map listeSpectacle
	 * @throws IOException
	 */
	private void remplirListeSemaines() throws IOException {
		c.maxIndispoMoyenne = Integer.parseInt(Config.prop.getProperty("maxIndispoMoyenne"));
		chargeFichierDateDeCettePersonne();//utilisateur 0
		c.test = true;
		String[] listeDates = c.sb.toString().split("\n");

		for (String l : listeDates) {
			String[] listesDates_ = l.split(";");

			for (int j = 0; j < Integer.parseInt(listesDates_[1]); j++) {

				LocalDateTime t = LocalDateTime.of(Integer.parseInt("20"+listesDates_[0].split("/")[2]), 
						Integer.parseInt(listesDates_[0].split("/")[1]), 
						Integer.parseInt(listesDates_[0].split("/")[0]), 
						Integer.parseInt(listesDates_[1]) > 1 ?  (j == 0 ? 16 : 21 ) : 21 , 0);

				int numeroSemaine = t.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
				double numSemaine = Double.parseDouble(numeroSemaine+"."+t.getYear());
				if ( ! Config.listeSemaines.contains(numSemaine)) {
					Config.listeSemaines.add(numSemaine);
					c.listeSpectacleParSemaine.put(numSemaine,new HashSet<>());
					c.dispos.put(numSemaine, new ArrayList<>() );
				}

				c.listeSpectacleParSemaine.get(numSemaine).add(new Spectacle(t));

			}
		}
	}

	/** vide la DB
	 * @throws SQLException
	 */
	protected void cleanDb() throws SQLException {
		c.dataBase.connect();

		DatabaseMetaData dbmd = (DatabaseMetaData) c.dataBase.getConnexion().getMetaData();

		ResultSet ctlgs = (ResultSet) dbmd.getCatalogs();

		while (ctlgs.next()){
			if (ctlgs.getString(1).length()> 5) {
				if (ctlgs.getString(1).substring(0, 5).equals("simul")) {
					c.dataBase.update("DROP DATABASE "+ctlgs.getString(1)+";");
					logger.debug("DROP TABLE '"+ctlgs.getString(1)+"';");
				}				
			}
		}		
	}

	/** Charge l'utilisateur c.id et mets le resultat dans c.sb
	 * @throws IOException
	 */
	private void chargeFichierDateDeCettePersonne() throws IOException {
		c.sb = new StringBuilder();
		c.reader = new CSVReader(new FileReader(c.f2 + Integer.toString(c.id) + ".csv"));

		while (true) {
			c.line = c.reader.readNext();
			if (c.line==null) {
				c.reader.close();
				break;//fin du fichier
			}
			// on filtre les données inutiles. On aura une ligne par date
			if (c.line[0].equals("")) {
				continue;
			}else if (c.line[0].substring(0,3).equals("Dat")) {
				continue;
			}else if (c.line[0].substring(0,3).equals("Sam")) {
				continue;
			}else if (c.line[0].equals("Nombre souhaité de semaines")) {
				continue;
			}else if (c.line[0].equals("Nombre total de semaines ")) {
				continue;
			}
			else if (c.line[1].equals("")) {
				continue;
			}else if (c.line[0].trim().length()>3) {
				if (c.line[0].trim().substring(0,3).equals("Ind")) {
					continue;
				}
			}

			c.sb.append(c.line[0].trim()+";"+c.line[1].trim()+";"+c.line[2].trim()+";"+c.line[3].trim()+";"+c.line[4].trim()+";"+c.line[5].trim()+";\n");
		}
		if (c.test) {
			String[] listeDates = c.sb.toString().split("\n");
			for (String l : listeDates) {
				String[] listesDates_ = l.split(";");

				for (int j = 0; j < Integer.parseInt(listesDates_[1]); j++) {

					LocalDateTime t = LocalDateTime.of(Integer.parseInt("20"+listesDates_[0].split("/")[2]), 
							Integer.parseInt(listesDates_[0].split("/")[1]), 
							Integer.parseInt(listesDates_[0].split("/")[0]), 
							Integer.parseInt(listesDates_[1]) > 1 ?  (j == 0 ? 16 : 21 ) : 21 , 0);
					int numeroSemaine = t.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
					double numSemaine = Double.parseDouble(numeroSemaine+"."+t.getYear());

					DisponibiliteJour d;

					if (listesDates_[2].equals("1")) {
						d = new DisponibiliteJour(c.p, t,c.personnage,DISPO.DISPO,numSemaine);
					}
					else if (listesDates_[2].equals("0")) {
						d = new DisponibiliteJour(c.p, t,c.personnage,DISPO.PAS_DISPO,numSemaine);
					}
					else {
						d = new DisponibiliteJour(c.p, t,c.personnage,DISPO.MOYEN_DISPO,numSemaine);
					}

					c.dispos.get(numSemaine).add(d);
				}
			}
		}
	}

	private void affichageTeams() {
		logger.debug("Affichage des teams");
		Config.listeTeam.forEach((i, team) -> {
			logger.debug(i+" ");
			logger.debug(team);
		});	
	}

	private void calculTeams() {
		logger.debug("Calcul des permutations d'equipes");

		c.listePersonnes.forEach((personnage, mapPersonnes) -> {
			c.dataBase.update(c.sqlQuery2(personnage.getNom()));

		});

		c.listePersonnes.forEach((personnage, mapPersonnes) -> {

			for (Personne personne : mapPersonnes) {
				c.dataBase.update("INSERT INTO `" + personnage.getNom() + "` (`id_unique`, `id_personne`) VALUES (NULL, '"+personne.getId()+"');");
			}
		});
		calculDuCrossJoin();

		String[] result = c.dataBase.select(c.sb.toString()).split("\n");

		for (String s : result) {
			c.sb = new StringBuilder("INSERT INTO `listeequipe` VALUES (NULL, ");
			String[] result2 = s.split("/");
			for (String s2 : result2) {
				c.sb.append("'"+s2+"',");
			}
			c.dataBase.update(c.sb.toString().substring(0, c.sb.toString().length()-1)+",'1');");
		}


		Config.listePersonnes2.forEach((id, personne) -> {
			if ( ! personne.getPersonneAvecQuiJeNeDoisPasJouer().equals("")) {
				String[] p = personne.getPersonneAvecQuiJeNeDoisPasJouer().split(",");
				c.dataBase.update("UPDATE LISTEEQUIPE SET OK='T' WHERE OK='1' AND " + personne.getPersonnage().getNom() +" ='" + personne.getId()+"';");
				for (String string : p) {
					c.dataBase.update("UPDATE LISTEEQUIPE SET OK='0' WHERE OK='T' AND " + Config.listePersonnes2.get(Integer.parseInt(string)).getPersonnage().getNom() +" ='" + string +"';");
				}
				c.dataBase.update("UPDATE LISTEEQUIPE SET OK='1' WHERE OK='T';");
			}
		});
		String[] l = c.dataBase.select("SELECT * FROM LISTEEQUIPE WHERE OK=1;").split("\n");
		for (String s : l) {

			String[] s2 = s.split("/");
			Map<Personnage, Personne> teamPourLeSpectacle = new HashMap<>();
			for (int i = 1; i < s2.length - 1; i++) {
				teamPourLeSpectacle.put(Config.listePersonnes2.get(Integer.parseInt(s2[i])).getPersonnage(), Config.listePersonnes2.get(Integer.parseInt(s2[i])));
			}
			Config.listeTeam.put(Integer.parseInt(s2[0]), new Team(Integer.parseInt(s2[0]),teamPourLeSpectacle));
		}

		Config.listeSemaines.forEach((numSemaine) -> {
			c.semaines.put(numSemaine, new Semaine(c.listeSpectacleParSemaine.get(numSemaine).size(),numSemaine));
		});

		Config.listeSemaines.forEach((numSemaine) -> {
			Semaine sem = c.semaines.get(numSemaine);
			List<DisponibiliteJour> lDispos = c.dispos.get(numSemaine);
			Config.listePersonnes2.forEach((id,pers) -> {
				int compteurDispoMoyenne = 0;
				pers.setEstDispoCetteSemaine(true);
				for (DisponibiliteJour d : lDispos) {
					if (d.getPersonne().getId()==id) {//si c est ma personne
						if (d.getDispo().equals(DISPO.PAS_DISPO)) {
							pers.setEstDispoCetteSemaine(false);
							break;
						}
						else if (d.getDispo().equals(DISPO.MOYEN_DISPO)) {
							if ( ++compteurDispoMoyenne == c.maxIndispoMoyenne) {
								pers.setEstDispoCetteSemaine(false);
								break;							
							}
						}
					}
				}
			});

			Config.listeTeam.forEach((idTeam,t) -> {
				c.addTeam = true;
				t.getTeamPourLeSpectacle().forEach((idpers,pers2) -> {
					if ( ! pers2.estDispoCetteSemaine() ) {
						c.addTeam = false;
					}
				});
				if (c.addTeam) {
					sem.addTeam(idTeam);
				}
			});
		});
		
		if (Boolean.parseBoolean(Config.prop.getProperty("precalculsA_Faire"))) {
			Config.listeTeam.forEach((idTeam,t) -> {
				Config.listeTeam.forEach((idTeam2,t2) -> {
					if (idTeam!=idTeam2) {
						Config.eccartTypePersistance.setEccartTypePersistance(idTeam,idTeam2,Config.calculEccartType(idTeam, idTeam2));
					}
				});
			});
		}
		
	}

	private void calculDuCrossJoin() {
		c.sb = new StringBuilder("SELECT ");
		c.id = 0;
		for (String s : Config.personnages) {
			if (c.id==0) {
				c.sb.append(s+".id_personne as "+s);
				c.id++;
			}else {
				c.sb.append(", "+s+".id_personne as "+s);
			}
		}




		c.id = 0;

		c.listePersonnes.forEach((personnage, mapPersonnes) -> {
			if (c.id==0) {
				c.sb.append(" FROM " + personnage.getNom());	
				c.id++;
			}else {
				c.sb.append( " CROSS JOIN "+personnage.getNom());	
			}
		});

	}

	private void affichagePersonnes() {
		logger.info("Affichage de la fine équipe");
		Config.listePersonnes2.forEach((id, p) -> {
			logger.info(p);
		});
	}

	private void affichage() {
		affichagePersonnes();
		affichageTeams();
		logger.info("Liste des semaines : "+Config.listeSemaines);
		Config.listeSemaines.forEach((l) -> {
			if (c.semaines.get(l)!=null) {
				logger.info("Numéro de semaine : "+l);
				logger.info(c.semaines.get(l).getTeam().size());
			}
		});
	}

}

