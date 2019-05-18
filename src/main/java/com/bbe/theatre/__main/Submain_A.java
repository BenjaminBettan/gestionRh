package com.bbe.theatre.__main;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.bbe.theatre.personne.Personnage;
import com.bbe.theatre.personne.Personne;
import com.bbe.theatre.spectacle.Spectacle;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSet;

import au.com.bytecode.opencsv.CSVReader;

public class Submain_A {

	private static Logger logger = Logger.getLogger(Submain_A.class);
	protected Config c = new Config();

	protected void init() throws IOException {
		//lecture de global.properties
		logger.info("on lit le fichier global.properties");

		c.prop.load(new FileInputStream(c.f1));
		//creation de la DB
		c.dataBase.update(c.sqlQueryDatabase);
		c.dataBase.setBaseName(c.dataBaseName);

		logger.info("on lit 1 fois les dates de l'utilisateur 0");
		remplirListeSsemaines();


		chargementContraintesJoueursTandem();

		creationPersonnages();

		for (c.id = 0; c.id < Integer.parseInt(c.prop.getProperty("effectifComediens").trim()); c.id++) {

			creationPersonne(Integer.parseInt(c.prop.getProperty(c.id+".role").trim()));
			chargeFichierDate();
			/*
			String[] listeDates = c.sb.toString().split("\n");
			for (String l : listeDates) {
				String[] listesDates_ = l.split(";");

				for (int j = 0; j < Integer.parseInt(listesDates_[1]); j++) {

					LocalDateTime t = LocalDateTime.of(Integer.parseInt("20"+listesDates_[0].split("/")[2]), 
							Integer.parseInt(listesDates_[0].split("/")[1]), 
							Integer.parseInt(listesDates_[0].split("/")[0]), 
							Integer.parseInt(listesDates_[1]) > 1 ?  (j == 0 ? 16 : 21 ) : 21 , 0);

					boolean dispoForte = listesDates_[2].equals("1");

					if ( ! listesDates_[2].equals("0")) {
						DisponibiliteJour d = new DisponibiliteJour(c.p, t,c.cePersonnage,dispoForte);
						AssoDispoPersonnage asso = new AssoDispoPersonnage(c.cePersonnage,d);
						HashSet<Personne> listePersonnes = c.assoDispoPersonnage.get(c.cePersonnage);
						if(listePersonnes==null){
							listePersonnes = new HashSet<>();
						}
						listePersonnes.add(c.p);
						c.assoDispoPersonnage.put(asso, listePersonnes);
					}
				}
			}
			 */
		}

		calculTeams();
		//		affichagePersonnes();
		//		affichageDispos();
		affichageTeams();
		System.out.println(c.listeSemaines);
		System.out.println(c.listeSemaines_);
	}

	private void creationPersonne(int idRole) {
		c.listePersonnes.forEach((personnage, map) -> {
			if (personnage.getId()==idRole) {
				c.p = new Personne().setNbSpectacleMin(50)
						.setId(c.id).setNomActeur(c.prop.getProperty(c.id+".nom").trim())
						.setPersonnage(personnage).setPersonneAvecQuiJeDoisJouer(calculDoitRencontrer(c.doitRencontrer))
						.setPersonneAvecQuiJeNeDoisPasJouer(calculDoitRencontrer(c.neDoitPasRencontrer));
				map.add(c.p);
			}
		});		
	}

	private int calculDoitRencontrer(String[] doitRencontrer) {
		int idDoitRencontrer = 0;
		for (int i = 0; i < doitRencontrer.length; i++) {
			if (doitRencontrer[i].equals(Integer.toString(c.id))) {
				if (i%2==0) {
					idDoitRencontrer = Integer.parseInt(doitRencontrer[i+1]);
				} else if (i%2==1) {
					idDoitRencontrer = Integer.parseInt(doitRencontrer[i-1]);
				}
				
			}
		}
		return idDoitRencontrer;
	}

	private void chargementContraintesJoueursTandem() {
		c.doitRencontrer = c.prop.getProperty("DoitRencontrer").trim().replace("{", "").replace("}", "").replace(";", ",").split(",");
		c.neDoitPasRencontrer = c.prop.getProperty("neDoitPasRencontrer").trim().split(",");
	}

	private void creationPersonnages() {
		c.personnages = c.prop.getProperty("listePersonnage").trim().split(",");

		for (int i = 0; i < c.personnages.length; i++) {
			Personnage p = new Personnage(i,c.personnages[i]);
			c.listePersonnes.put(p, new HashSet<>());
		}		
	}

	/** permet de remplir la map listeSpectacle
	 * @throws IOException
	 */
	private void remplirListeSsemaines() throws IOException {
		chargeFichierDate();
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
				}
				
				Map<Integer, Set<Spectacle>> listeSpectacles = new HashMap<>();
				Set<Spectacle> s = new HashSet<>();
				s.add(new Spectacle(t));
				
				listeSpectacles.put(numeroSemaine, s);
				
				c.listeSemaines_.add(listeSpectacles);
				
				c.listeSemaines_.forEach((n) -> {
					n.get(null);
				});
				
			}
		}
		
		
//		c.listeSemaines.forEach((numeroSemaine) -> {
//			c.listeSpectacles.forEach((id,s) -> {
//				if (s.getNumSemaine()==numeroSemaine) {
//					
//				}
//			});
//		});

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
					System.out.println("DROP TABLE '"+ctlgs.getString(1)+"';");
				}				
			}
		}		
	}

	/** Charge l'utilisateur c.id et mets le resultat dans c.sb
	 * @throws IOException
	 */
	private void chargeFichierDate() throws IOException {
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
				c.nbSpectaclePersonne = Integer.parseInt(c.line[5])*c.nbSpectacleParSemaine;
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

	}

	private void affichageTeams() {
		logger.info("Affichage des teams");
		c.listeTeam.forEach((i, team) -> {
			System.out.print(i+" ");
			System.out.println(team);
		});	
	}

	private void calculTeams() {
		logger.info("Calcul des permutations d'equipes");

		c.listePersonnes.forEach((personnage, mapPersonnes) -> {
			c.dataBase.update(c.sqlQueryDatabase2(personnage.getNom()));

		});
		c.listePersonnes.forEach((personnage, mapPersonnes) -> {

			for (Personne personne : mapPersonnes) {
				c.dataBase.update("INSERT INTO `" + personnage.getNom() + "` (`id_unique`, `id_personne`) VALUES (NULL, '"+personne.getId()+"');");
			}
		});
		calculDuCrossJoin();




		System.out.println(c.sb.toString());
		//		System.out.println(c.dataBase.select(c.sb.toString()));


	}

	private void calculDuCrossJoin() {
		c.sb = new StringBuilder("SELECT ");
		c.id = 0;

		c.listePersonnes.forEach((personnage, mapPersonnes) -> {
			if (c.id==0) {
				c.sb.append(personnage.getNom()+".id_personne as "+personnage.getNom());
				c.id++;
			}else {
				c.sb.append(", "+personnage.getNom()+".id_personne as "+personnage.getNom());
			}

		});

		c.id = 0;

		c.listePersonnes.forEach((personnage, mapPersonnes) -> {
			if (c.id==0) {
				c.sb.append(" FROM " + personnage.getNom());	
				c.id++;
			}else {
				c.sb.append( " CROSS JOIN "+personnage.getNom());	
			}
		});		

		c.id = 0;

		c.sb.append( " WHERE ");
		c.listePersonnes.forEach((personnage, mapPersonnes) -> {
			mapPersonnes.forEach((p) -> {
				if (p.getPersonneAvecQuiJeDoisJouer()==0) {

				}
			});
		});	

	}

}



/*
private void affichageDispos() {
	logger.info("Affichage des dispos");
	c.assoDispoPersonnage.forEach((asso, mapPersonnes) -> {
		System.out.println(asso);
		System.out.println(mapPersonnes);
		System.out.println();
	});			
}

private void affichagePersonnes() {
	logger.info("Affichage de la fine équipe");
	c.listePersonnes.forEach((personnage, mapPersonnes) -> {
		System.out.print(personnage+" ");
		System.out.println(mapPersonnes);
	});		
}
 */