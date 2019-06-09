package com.bbe.theatre.__main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import com.bbe.theatre.spectacle.Planning;

public class Submain extends Submain_A{

	private static Logger logger = Logger.getLogger(Submain.class);

	private List<Planning> plannings = new ArrayList<>();
	private List<Planning> top10 = new ArrayList<>();
	private int taillePopulation;
	private int taillePopulationRestante;
	private int j,k;
	
	public void go() throws IOException, SQLException {
		
		init();
		
		while (true) {
			
			creationPopulationInitiale();
			boolean exit = false;
			
			for (int i = 1; ! exit; i++) {
				
				if (i%30==0) {
					logger.info(i + " iterations");
					
					j=0;
					plannings.forEach((p) -> {
						j+=p.getValue();
					});
					
					
					if (j==plannings.size()*plannings.get(0).getValue()) {
						if (k++==10) {
							logger.info("A priori on est plus très loin de la fin");
							taillePopulationRestante = 2;
							Config.tauxMutation = 1000;
						}
						else if (k++==30) {
							exit = true;
							top10.add(plannings.get(0));
							evaluationPopulation(top10);
							if (top10.size()==11) {
								top10.remove(top10.size()-1);
							}
							logger.info("Top 10 :");
							
							top10.forEach((p) -> {
								logger.info(p.getValue());
							});
						}
					}
					else {
						k=1;
						Config.tauxMutation = Integer.parseInt(Config.prop.getProperty("pourMilleMutation"));
						taillePopulationRestante = Integer.parseInt(Config.prop.getProperty("taillePopulationRestante"));
						taillePopulation = Integer.parseInt(Config.prop.getProperty("taillePopulation"));
						
						if (taillePopulation>=1000) {
							taillePopulation = taillePopulation / 10;
							taillePopulationRestante = taillePopulationRestante / 10;
							logger.info("Reduction de la population à "+taillePopulation+" individus");
						}
					}
				}
				
				logger.info(plannings.get(0).getValue() + "  " + plannings.get(0).calculEccartType() + " " + plannings.get(0).calculNbSpectMin());
				
				naissanceDeLaNouvelleGeneration();
			}
		}
	}

	private void naissanceDeLaNouvelleGeneration() {
		int fin = plannings.size();
		for (int i = 0; i < fin - taillePopulationRestante; i++) {
			plannings.remove(taillePopulationRestante);
		}
		
		for (int i = 0; i < taillePopulation - taillePopulationRestante; i++) {
			int idPapa = ThreadLocalRandom.current().nextInt(0, taillePopulationRestante);
			int idMaman = ThreadLocalRandom.current().nextInt(0, taillePopulationRestante);
			
			while (idPapa==idMaman) {
				idMaman = ThreadLocalRandom.current().nextInt(0, taillePopulationRestante);	
			}
			Planning papa = plannings.get(idPapa);
			Planning maman = plannings.get(idMaman);
			Planning enfant = new Planning(papa, maman);
			plannings.add(enfant);
		}
		
		evaluationPopulation(plannings);
	}

	private void evaluationPopulation(List<Planning> l) {
		l.sort(Comparator.comparing(Planning::getValue));
	}

	private void creationPopulationInitiale() {
		plannings.clear();
		k = 1;
		taillePopulation = Integer.parseInt(Config.prop.getProperty("taillePopulation"));
		taillePopulationRestante = Integer.parseInt(Config.prop.getProperty("taillePopulationRestante"));
		Config.tauxMutation = Integer.parseInt(Config.prop.getProperty("pourMilleMutation"));
		
		logger.info("Creation population initiale taille : " + taillePopulation);

		for (int i = 0; i < taillePopulation; i++) {
			
			Planning p = new Planning();
			c.semaines.forEach((id,sem) -> {p.addSemaine(id, sem);});//on charge les equipes dispo par semaine
			plannings.add(p.build());//on attribue au hasard une equipe par semaine
		}
		
		evaluationPopulation(plannings);
		
	}
}
