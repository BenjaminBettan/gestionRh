package com.bbe.franglaises.__main;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bbe.franglaises.personne.Personnage;
import com.bbe.franglaises.personne.Personne;
import com.bbe.franglaises.spectacle.AssoDispoPersonnage;
import com.bbe.franglaises.spectacle.Disponibilite;
import com.bbe.franglaises.spectacle.Spectacle;
import com.bbe.franglaises.spectacle.Team;

import au.com.bytecode.opencsv.CSVReader;

public class Submain_A {
	private static Logger logger = Logger.getLogger(Submain_A.class);
	protected Config c = new Config();

	private void chargeDate() throws IOException {
		chargeFichierDate(0);
		String[] listeDates = c.sb.toString().split("\n");
		for (String l : listeDates) {
			String[] listesDates_ = l.split(";");

			for (int j = 0; j < Integer.parseInt(listesDates_[1]); j++) {

				LocalDateTime t = LocalDateTime.of(Integer.parseInt("20"+listesDates_[0].split("/")[2]), 
						Integer.parseInt(listesDates_[0].split("/")[1]), 
						Integer.parseInt(listesDates_[0].split("/")[0]), 
						Integer.parseInt(listesDates_[1]) > 1 ?  (j == 0 ? 16 : 21 ) : 21 , 0);

				c.listeSpectacles.put(c.idSpectacle++, new Spectacle(t, Integer.parseInt(listesDates_[1])));
			}

		}
	}

	private void chargeFichierDate(int i) throws IOException {
		c.sb = new StringBuilder();
		c.reader = new CSVReader(new FileReader(c.f2 + Integer.toString(i) + ".csv"));

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

	protected void init() throws IOException {

		logger.info("on lit les dates");
		chargeDate();

		//lecture de global.properties
		logger.info("on lit le fichier global.properties");
		c.prop.load(new FileInputStream(c.f1));

		int effectifComediensFranglaises = Integer.parseInt(c.prop.getProperty("effectifComediens").trim());
		String[] personnages = c.prop.getProperty("listePersonnage").trim().split(",");

		for (int i = 0; i < personnages.length; i++) {
			Personnage p = new Personnage(i,personnages[i]);
			c.listePersonnes.put(p, new HashSet<>());
		}

		for (c.id = 0; c.id < effectifComediensFranglaises; c.id++) {

			switch (c.prop.getProperty(c.id+".peutJouerSamedi").trim()) {
			case "0":
				c.b = c.b0;
				break;
			case "1":
				c.b = c.b1;
				break;
			case "2":
				c.b = c.b2;
				break;
			default:
				logger.warn("Erreur pour utilisateur " + c.id + "peutJouerSamedi incorrect 0 ou 1 ou 2");
				System.exit(0);
				break;
			}

			int idRole = Integer.parseInt(c.prop.getProperty(c.id+".role").trim());

			c.listePersonnes.forEach((personnage, map) -> {
				if (personnage.getId()==idRole) {
					c.cePersonnage = personnage;
					c.p = new Personne(c.b, c.id, c.prop.getProperty(c.id+".nom").trim(), 0, null, personnage);
					map.add(c.p);
				}
			});

			chargeFichierDate(c.id);

			String[] listeDates = c.sb.toString().split("\n");
			for (String l : listeDates) {
				String[] listesDates_ = l.split(";");

				for (int j = 0; j < Integer.parseInt(listesDates_[1]); j++) {

					LocalDateTime t = LocalDateTime.of(Integer.parseInt("20"+listesDates_[0].split("/")[2]), 
							Integer.parseInt(listesDates_[0].split("/")[1]), 
							Integer.parseInt(listesDates_[0].split("/")[0]), 
							Integer.parseInt(listesDates_[1]) > 1 ?  (j == 0 ? 16 : 21 ) : 21 , 0);

					Disponibilite d;
					boolean dispoForte = listesDates_[2].equals("1");
					if ( ! listesDates_[2].equals("0")) {
						if (Integer.parseInt(listesDates_[1])==2) {
							if (j==0) {
								if (c.p.veutJouerLeSamediTouteLaJournee()) {
									d = new Disponibilite(c.p, t,c.cePersonnage,dispoForte);
								}else if (c.p.veutJouerLeSamediAprem()) {
									d = new Disponibilite(c.p, t,c.cePersonnage,dispoForte);
								}else{
									d = new Disponibilite(c.p, t,c.cePersonnage,false);
								}
							}
							else {
								if (c.p.veutJouerLeSamediTouteLaJournee()) {
									d = new Disponibilite(c.p, t,c.cePersonnage,dispoForte);
								}else if (c.p.veutJouerLeSamediAprem()) {
									d = new Disponibilite(c.p, t,c.cePersonnage,false);
								}else{
									d = new Disponibilite(c.p, t,c.cePersonnage,dispoForte);
								}				
							}
						}
						else {
							d = new Disponibilite(c.p, t,c.cePersonnage,dispoForte);
						}


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
		}
		
		calculTeams();
//		affichagePersonnes();
//		affichageDispos();
		affichageTeams();
		
	}

	private void affichageTeams() {
		logger.info("Affichage des teams");
		c.listeTeam.forEach((i, team) -> {
			System.out.print(i+" ");
			System.out.println(team);
		});	
	}

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
	private void calculTeams() {
		logger.info("Calcul des permutations d'equipes");
		c.listePersonnes.forEach((personnage, mapPersonnes) -> {
			System.out.println(personnage);
			c.listePersonnes.forEach((personnage2, mapPersonnes2) -> {
				System.out.println("--"+personnage2);
				System.out.println("--"+mapPersonnes2);

				
			});
//			Map<Personnage, Personne> teamPourLeSpectacle = new HashMap<>();
//			for (Personne personne : mapPersonnes) {
//				teamPourLeSpectacle.put(personnage, personne);
//			}
//			
//			c.listeTeam.put(c.idTeam, new Team(c.idTeam, teamPourLeSpectacle));
//			c.idTeam++;
		});		
	}
}





/*

	int nbPersonnage = personnages.length;



	c.reader = new CSVReader(new FileReader(c.getFileName2()));
	int nbSpectaclePersonne = 0;


		logger.debug(nbSpectaclePersonne + c.b.toString());

		//traitement


		if (c.id==0) {
			c.nb_spectacle_total+=Integer.parseInt(c.line[1].trim());
			if (Integer.parseInt(c.line[1].trim())>2) {
				logger.warn("Arret du programme plus de 2 dates detectées dans colone 2 pour l utilisateur 0");
				logger.warn(new Exception().getStackTrace());
				System.exit(1);// on lit le fichier excel pour la premiere fois. Il y a plus de 2 date pour une journée. On plante
			}


		}
		if ( ! c.line[2].trim().equals("0") ) {
			for (int i = 1; i <= Integer.parseInt(c.line[1].trim()); i++) {

				LocalDateTime t = LocalDateTime.of(Integer.parseInt("20"+c.line[0].trim().split("/")[2]), 
						Integer.parseInt(c.line[0].trim().split("/")[1]), 
						Integer.parseInt(c.line[0].trim().split("/")[0]), 
						Integer.parseInt(c.line[1].trim()) > 1 ?  (i == 0 ? 16 : 21 ) : 21 , 0);


				c.dataBase.update("INSERT INTO `listedates` (`id`, `date`, `idPersonne`, `dispoForte`, `numeroSemaine`) VALUES "
						+ "(NULL, '"+t+"', '"+c.id+"', '"+(c.line[2].trim().equals("0,5") ? "0" : "1")+"', '"+t.toLocalDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())+"');");

												listeSpectacle.put(line[0].trim()+"_"+i, new Spectacle(line[0].trim()+"_"+i,Integer.parseInt(line[1].trim())));


			}								
		}
	}

	if ( ! c.prop.getProperty(c.id+".nbSpectacleSouhaite").trim().equals("0")) {
		nbSpectaclePersonne = Integer.parseInt(c.prop.getProperty("nbSpectacleSouhaite").trim());
	}
	//					HashMap<Personnage,Personne> l = listePersonnes.get(p);
	//					l.put(p,new Personne(b, id, prop.getProperty("nom").trim(), nbSpectaclePersonne, null, p));
}*/
