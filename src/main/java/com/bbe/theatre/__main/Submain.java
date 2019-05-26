package com.bbe.theatre.__main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.bbe.theatre.spectacle.Planning;

public class Submain extends Submain_A{
	
	private Set<Planning> planning = new HashSet<>();
	private int taillePopulation;
	private static Logger logger = Logger.getLogger(Submain.class);
	public void go() throws IOException, SQLException {
		long l = System.currentTimeMillis();
		init();
		logger.info("TEMPS DE CALCUL INIT : "+ (System.currentTimeMillis() - l) + "ms");

		creationPopulationInitiale();
		evaluationPopulation();
		extinctionPopulation();
		for (int i = 0; true; i++) {
			if (i%100==0) {
				logger.info(i + "eme generation");
			}
			naissance();
			evaluationPopulation();
			extinctionPopulation();
		}
	}

	private void naissance() {
		// TODO Auto-generated method stub
		
	}

	private void extinctionPopulation() {
		// TODO Auto-generated method stub
		
	}

	private void evaluationPopulation() {
		// TODO Auto-generated method stub
		
	}

	private void creationPopulationInitiale() {
		taillePopulation = Integer.parseInt(Config.prop.getProperty("taillePopulation"));
		
		logger.info("Creation population initiale taille : " + taillePopulation);

		for (int i = 0; i < taillePopulation; i++) {
			
			Planning p = new Planning();
			c.semaines.forEach((id,sem) -> {
				p.addSemaine(id, sem);
			});
			
			p.creationPopulationMelange();
			planning.add(p);
			
		}		
	}
}
