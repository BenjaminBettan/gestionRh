package com.bbe.theatre.__main;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.log4j.Logger;
import com.bbe.theatre.spectacle.Planning;
import com.bbe.theatre.spectacle.Semaine;

public class Submain extends Submain_A{

	private static Logger logger = Logger.getLogger(Submain.class);

	private List<Planning> plannings = new ArrayList<>();
	private List<Planning> top = new ArrayList<>();
	private int taillePopulation;
	private int taillePopulationRestante;
	private int j;
	private int k;
	private int iteration = Integer.parseInt(Config.getProp().getProperty("nbIterationControlAlgo").trim());
	private int tailleTop = Integer.parseInt(Config.getProp().getProperty("tailleTop").trim());
	private int tailleMin = Integer.parseInt(Config.getProp().getProperty("tailleMin").trim());
	private int onDivisePar = Integer.parseInt(Config.getProp().getProperty("onDivisePar").trim());
	private int evenementNbIteration = Integer.parseInt(Config.getProp().getProperty("evenementNbIteration").trim());
	private int conditionArret1 = Integer.parseInt(Config.getProp().getProperty("conditionArret1").trim());
	private int conditionArret2 = Integer.parseInt(Config.getProp().getProperty("conditionArret2").trim());
	private int valeurMinPourTravailler = Integer.parseInt(Config.getProp().getProperty("valeurMinPourTravailler").trim());

	public void go() throws IOException, SQLException {

		init();

		while ( ! Config.isExitAlgo()) {

			creationPopulationInitiale();
			boolean exit = false;

			for (int i = 1; ! (exit || Config.isExitAlgo()); i++) {

				Config.getProp().load(new FileInputStream("src\\main\\resources\\global.properties"));

				if ( i % iteration == 0 ) {
					logger.info(i + " iterations");
					
					if (valeurMinPourTravailler!=0) {
						if ( valeurMinPourTravailler < plannings.get(0).getValue() ) {
							exit = true;
						}
					}
					
					if ( i % evenementNbIteration == 0 ) {
						if ( taillePopulation >= tailleMin ) {
							taillePopulation = taillePopulation / onDivisePar;
							taillePopulationRestante = taillePopulationRestante / onDivisePar;
							logger.info("Reduction de la population à "+taillePopulation+" individus");
						}
					}

					if ( j == taillePopulationRestante*plannings.get(0).getValue() ) {
						logger.info("Tous les parents sont identiques " + k);
						if (++k==conditionArret1) {
							logger.info("A priori on est plus très loin de la fin");
							taillePopulationRestante = 2;
							Config.setTauxMutation(1000);
							logger.info("Passage du taux de mutation a 100%");
						}
						else if (k==conditionArret2) {
							
							exit = true;
							top.add(plannings.get(0));
							evaluationPopulation(top);
							
							if ( top.size() == tailleTop + 1 ) {
								top.remove(top.size()-1);
							}
							logger.info("Top :");

							top.forEach((p) -> {
								logger.info(p);
							});
						}
					}
					else {
						k=0;
						Config.setTauxMutation(Integer.parseInt(Config.getProp().getProperty("pourMilleMutation")));
						taillePopulationRestante = Integer.parseInt(Config.getProp().getProperty("taillePopulationRestante"));
						if (taillePopulation!=Integer.parseInt(Config.getProp().getProperty("taillePopulation"))) {
							taillePopulation = Integer.parseInt(Config.getProp().getProperty("taillePopulation"));
							logger.info("Augmentation de la population à "+taillePopulation+" individus");
						}
					}
				}

				if ( ! ( exit || Config.isExitAlgo() )) {
					logger.info(plannings.get(0));
					Config.setTauxMutation(Integer.parseInt(Config.getProp().getProperty("pourMilleMutation")));

					naissanceDeLaNouvelleGeneration(i);	
				}
			}
		}

		//on a quitté l algo
		if (top.isEmpty()) {
			top.add(plannings.get(0));
		}
		Map<Double, Semaine> listeSemaines = top.get(0).getSemaines();

		Config.getListeSpectacleParSemaine().forEach((idSemaine,spectacles)->{
			spectacles.forEach( s -> {
				System.out.println(idSemaine);
				Config.getDataBase().update("INSERT INTO `spectacles` (`id_unique`, `date_spectacle`, `id_team`) VALUES (NULL, '"+s.getIdDate()+"', '"+listeSemaines.get(idSemaine).getIdTeam()+"');");
			});
		});

		String[] cmd = {"cmd.exe","/c","start http://localhost:8080/franglaises_planning/franglaises.html"};

		Runtime.getRuntime().exec(cmd);
		System.out.println("Fin du programme avec succes");
	}

	private void naissanceDeLaNouvelleGeneration(int iterationAlgoPrincipal) {
		int fin = plannings.size();
		for (int i = 0; i < fin - taillePopulationRestante; i++) {
			plannings.remove(taillePopulationRestante);
		}

		if ( iterationAlgoPrincipal % iteration == iteration -1 ) {
			j=0;
			plannings.forEach((p) -> {
				j+=p.getValue();
			});
		}

		for (int i = 0; i < taillePopulation - taillePopulationRestante; i++) {
			if (Config.isExitAlgo()) {
				break;
			}
			int idPapa = ThreadLocalRandom.current().nextInt(0, taillePopulationRestante);
			int idMaman = ThreadLocalRandom.current().nextInt(0, taillePopulationRestante);

			while (idPapa==idMaman) {
				idMaman = ThreadLocalRandom.current().nextInt(0, taillePopulationRestante);	
			}
			Planning papa = plannings.get(idPapa);
			Planning maman = plannings.get(idMaman);
			Planning enfant = new Planning(papa, maman,Integer.parseInt(Config.getProp().getProperty("tailleBrin")));
			plannings.add(enfant);
		}

		evaluationPopulation(plannings);
	}

	private void evaluationPopulation(List<Planning> l) {
		l.sort(Comparator.comparing(Planning::getValue));
	}

	private void creationPopulationInitiale() {
		
		iteration = Integer.parseInt(Config.getProp().getProperty("nbIterationControlAlgo").trim());
		tailleTop = Integer.parseInt(Config.getProp().getProperty("tailleTop").trim());
		tailleMin = Integer.parseInt(Config.getProp().getProperty("tailleMin").trim());
		onDivisePar = Integer.parseInt(Config.getProp().getProperty("onDivisePar").trim());
		evenementNbIteration = Integer.parseInt(Config.getProp().getProperty("evenementNbIteration").trim());
		conditionArret1 = Integer.parseInt(Config.getProp().getProperty("conditionArret1").trim());
		conditionArret2 = Integer.parseInt(Config.getProp().getProperty("conditionArret2").trim());
		valeurMinPourTravailler = Integer.parseInt(Config.getProp().getProperty("valeurMinPourTravailler").trim());
		
		plannings.clear();
		k = 0;
		taillePopulation = Integer.parseInt(Config.getProp().getProperty("taillePopulation"));
		taillePopulationRestante = Integer.parseInt(Config.getProp().getProperty("taillePopulationRestante"));
		Config.setTauxMutation(Integer.parseInt(Config.getProp().getProperty("pourMilleMutation")));

		logger.info("Creation population initiale taille : " + taillePopulation);

		for (int i = 0; i < taillePopulation; i++) {
			if (Config.isExitAlgo()) {
				break;
			}
			
			boolean exit = false;
			
			for (; ! exit;) {
				
				Planning p = new Planning();
				Config.getSemaines().forEach((id,sem) -> {p.addSemaine(id, sem);});//on charge les equipes dispo par semaine
				
				p.build();//on attribue au hasard une equipe par semaine
				if (Config.getNoteMaxPourFairePartieDeLaPopulationInit() > p.getValue()) {
					plannings.add(p);
					logger.info(plannings.size() + "/"+taillePopulation+"  |   " + p);
					exit = true;
				}
			}
			
		}

		evaluationPopulation(plannings);

	}

}
