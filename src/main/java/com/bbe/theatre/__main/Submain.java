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
	private int taillePopulation;
	private int taillePopulationRestante;
	
	public void go() throws IOException, SQLException {
		long l = System.currentTimeMillis();
		init();
		logger.info("TEMPS DE CALCUL INIT : "+ (System.currentTimeMillis() - l) + "ms");

		creationPopulationInitiale();
		
		for (int i = 1; true; i++) {
			if (i%100==0) {
				logger.info(i + " iteration");
			}
			
			naissanceDeLaNouvelleGeneration();
		}
	}

	private void naissanceDeLaNouvelleGeneration() {
		
		for (int i = 0; i < taillePopulation - taillePopulationRestante; i++) {
			plannings.remove(taillePopulationRestante);
		}
		
		System.out.println(plannings.get(0).getValue() + "  " + plannings.get(0).calculEccartType() + " " + plannings.get(0).calculNbSpectMin());
		
		
		
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
		

		
		evaluationPopulation();
	}


	private void evaluationPopulation() {
		plannings.sort(Comparator.comparing(Planning::getValue));
	}

	private void creationPopulationInitiale() {
		taillePopulation = Integer.parseInt(Config.prop.getProperty("taillePopulation"));
		taillePopulationRestante = Integer.parseInt(Config.prop.getProperty("taillePopulationRestante"));
		
		logger.info("Creation population initiale taille : " + taillePopulation);

		for (int i = 0; i < taillePopulation; i++) {
			
			Planning p = new Planning();
			c.semaines.forEach((id,sem) -> {p.addSemaine(id, sem);});//on charge les equipes dispo par semaine
			plannings.add(p.build());//on attribue au hasard une equipe par semaine
			
		}
		
		evaluationPopulation();
		
	}
}
