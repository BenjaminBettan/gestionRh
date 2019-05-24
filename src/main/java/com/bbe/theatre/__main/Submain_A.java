package com.bbe.theatre.__main;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.bbe.theatre._enum.DISPO;
import com.bbe.theatre.personne.Personnage;
import com.bbe.theatre.personne.Personne;
import com.bbe.theatre.spectacle.DisponibiliteJour;
import com.bbe.theatre.spectacle.Spectacle;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSet;

import au.com.bytecode.opencsv.CSVReader;

public class Submain_A {

	private static Logger logger = Logger.getLogger(Submain_A.class);
	protected Config c = new Config();

	protected void init() throws IOException {
		//lecture de global.properties
		logger.debug("on lit le fichier global.properties");

		c.prop.load(new FileInputStream(c.f1));
		//creation de la DB
		c.dataBase.update(c.sqlQueryDatabase);
		c.dataBase.setBaseName(c.dataBaseName);
		c.dataBase.update(c.sqlQuery3());

		logger.debug("on lit 1 fois les dates de l'utilisateur 0");
		
		remplirListeSemaines();

		chargementContraintesJoueurs();

		creationPersonnages();

		initPersonne();

		calculTeams();

		affichage();

	}

	private void initPersonne() throws IOException {
		for (c.id = 0; c.id < Integer.parseInt(c.prop.getProperty("effectifComediens").trim()); c.id++) {
			creationPersonne();
			chargeFichierDateDeCettePersonne();
		}		
	}

	private void creationPersonne() {
		int idRole = Integer.parseInt(c.prop.getProperty(c.id+".role").trim());
		c.listePersonnes.forEach((personnage, map) -> {
			if (personnage.getId()==idRole) {
				c.personnage = personnage;
				logger.debug("Création de l'utilisateur " + c.id);
				c.p = new Personne().setNbSpectacleMin(Integer.parseInt(c.prop.getProperty(c.id+".nbSpectacle").trim()))
						.setId(c.id).setNomActeur(c.prop.getProperty(c.id+".nom").trim())
						.setPersonnage(personnage)
						//.setPersonnage(personnage).setPersonneAvecQuiJeDoisJouer(calculDoitRencontrer(c.doitRencontrer))
						.setPersonneAvecQuiJeNeDoisPasJouer(calculDoitRencontrer(c.neDoitPasRencontrer));
				c.listePersonnes2.put(c.id, c.p);
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
		c.neDoitPasRencontrer = c.prop.getProperty("neDoitPasRencontrer").trim().replace("{", "").replace("}", "").split(",");
	}

	private void creationPersonnages() {
		c.personnages = c.prop.getProperty("listePersonnage").trim().split(",");

		for (int i = 0; i < c.personnages.length; i++) {
			String s;
			Personnage p = new Personnage(i,c.personnages[i]);
			c.listePersonnes.put(p, new HashSet<>());
			if (i==0) {
				s = "id_unique";
			}
			else {
				s = c.personnages[i-1];
			}
			c.dataBase.update("ALTER TABLE `listeequipe` ADD `"+c.personnages[i]+"` VARCHAR(2) NOT NULL AFTER `"+s+"`;");
			//			ALTER TABLE `listeequipe` ADD `jonathan` VARCHAR(2) NOT NULL AFTER `s`;

		}
		c.dataBase.update("ALTER TABLE `listeequipe` ADD `ok` VARCHAR(1) NOT NULL AFTER `"+c.personnages[c.personnages.length-1]+"`;");
	}

	/** permet de remplir la map listeSpectacle
	 * @throws IOException
	 */
	private void remplirListeSemaines() throws IOException {
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

				if ( ! c.listeSemaines.contains(numeroSemaine)) {
					c.listeSemaines.add(numeroSemaine);
					c.listeSpectacleParSemaine.put(numeroSemaine,new HashSet<>());
					c.dispos.put(numeroSemaine, new ArrayList<>() );
				}

				c.listeSpectacleParSemaine.get(numeroSemaine).add(new Spectacle(t));

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

					DisponibiliteJour d;

					if (listesDates_[2].equals("1")) {
						d = new DisponibiliteJour(c.p, t,c.personnage,DISPO.DISPO,numeroSemaine);
					}
					else if (listesDates_[2].equals("0")) {
						d = new DisponibiliteJour(c.p, t,c.personnage,DISPO.PAS_DISPO,numeroSemaine);
					}
					else {
						d = new DisponibiliteJour(c.p, t,c.personnage,DISPO.MOYEN_DISPO,numeroSemaine);
					}
					System.out.println(numeroSemaine);
					List<DisponibiliteJour> ld = c.dispos.get(numeroSemaine);
					ld.add(d);
				}
			}
		}

	}

	private void affichageTeams() {
		logger.debug("Affichage des teams");
		c.listeTeam.forEach((i, team) -> {
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


		c.listePersonnes2.forEach((id, personne) -> {
			if ( ! personne.getPersonneAvecQuiJeNeDoisPasJouer().equals("")) {
				String[] p = personne.getPersonneAvecQuiJeNeDoisPasJouer().split(",");
				c.dataBase.update("UPDATE LISTEEQUIPE SET OK='T' WHERE OK='1' AND " + personne.getPersonnage().getNom() +" ='" + personne.getId()+"';");
				for (String string : p) {
					c.dataBase.update("UPDATE LISTEEQUIPE SET OK='0' WHERE OK='T' AND " + c.listePersonnes2.get(Integer.parseInt(string)).getPersonnage().getNom() +" ='" + string +"';");
				}
				c.dataBase.update("UPDATE LISTEEQUIPE SET OK='1' WHERE OK='T';");
			}
		});		
	}

	private void calculDuCrossJoin() {
		c.sb = new StringBuilder("SELECT ");
		c.id = 0;
		for (String s : c.personnages) {
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
		c.listePersonnes2.forEach((id, p) -> {
			logger.info(p);
		});
	}

	private void affichage() {
		affichagePersonnes();
		affichageTeams();
		logger.debug(c.listeSemaines);
		logger.debug(c.listeSpectacleParSemaine);
		c.dispos.forEach((numSemaine, dispo) -> {
			System.out.println(numSemaine);
			System.out.println(dispo);
		});
	}

}

