package com.bbe.theatre.__main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.bbe.theatre.spectacle.Planning;

public class Submain extends Submain_A{

	private static Logger logger = Logger.getLogger(Submain.class);

	private List<Planning> plannings = new ArrayList<>();
	private int taillePopulation;
	
	public void go() throws IOException, SQLException {
		long l = System.currentTimeMillis();
		init();
		logger.info("TEMPS DE CALCUL INIT : "+ (System.currentTimeMillis() - l) + "ms");

		creationPopulationInitiale();
		extinctionPopulation();
		for (int i = 1; true; i++) {
			if (i%100==0) {
				logger.info(i + " iteration");
			}
			naissanceDeLaNouvelleGeneration();
			extinctionPopulation();
		}
	}

	private void naissanceDeLaNouvelleGeneration() {
		// TODO Auto-generated method stub
		
	}

	private void extinctionPopulation() {
		evaluationPopulation();
		
	}

	private void evaluationPopulation() {
		plannings.sort(Comparator.comparing(Planning::getCritere1).thenComparing(Planning::getCritere2));
	}

	private void creationPopulationInitiale() {
		taillePopulation = Integer.parseInt(Config.prop.getProperty("taillePopulation"));
		
		logger.info("Creation population initiale taille : " + taillePopulation);

		for (int i = 0; i < taillePopulation; i++) {
			
			Planning p = new Planning();
			c.semaines.forEach((id,sem) -> {p.addSemaine(id, sem);});//on charge les equipes dispo par semaine
			plannings.add(p.build());//on attribue au hasard une equipe par semaine
			
		}		
	}
}
