package com.bbe.theatre.__main;

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
	private List<Planning> top10 = new ArrayList<>();
	private int taillePopulation;
	private int taillePopulationRestante;
	private int j;
	private int k;

	public void go() throws IOException, SQLException {

		init();

		while ( ! Config.isExitAlgo()) {

			creationPopulationInitiale();
			boolean exit = false;

			for (int i = 1; ! (exit || Config.isExitAlgo()); i++) {

				if (i%30==0) {
					logger.info(i + " iterations");

					j=0;
					plannings.forEach((p) -> {
						j+=p.getValue();
					});


					if (j==plannings.size()*plannings.get(0).getValue()) {
						
						if (taillePopulation>=1000) {
							taillePopulation = taillePopulation / 10;
							taillePopulationRestante = taillePopulationRestante / 10;
							logger.info("Reduction de la population à "+taillePopulation+" individus");
						}
						
						if (++k==10) {
							logger.info("A priori on est plus très loin de la fin");
							taillePopulationRestante = 2;
							Config.setTauxMutation(1000);
						}
						else if (++k==30) {
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
						k=0;
						Config.setTauxMutation(Integer.parseInt(Config.getProp().getProperty("pourMilleMutation")));
						taillePopulationRestante = Integer.parseInt(Config.getProp().getProperty("taillePopulationRestante"));
						if (taillePopulation!=Integer.parseInt(Config.getProp().getProperty("taillePopulation"))) {
							taillePopulation = Integer.parseInt(Config.getProp().getProperty("taillePopulation"));
							logger.info("Augmentation de la population à "+taillePopulation+" individus");
						}
					}
				}

				logger.info(plannings.get(0).getValue() + "  " + plannings.get(0).calculEccartType() + " " + plannings.get(0).calculNbSpectMin());

				naissanceDeLaNouvelleGeneration();
			}
		}

		//on a quitté l algo
		if (top10.isEmpty()) {
			top10.add(plannings.get(0));
		}
		Map<Double, Semaine> listeSemaines = top10.get(0).getSemaines();
		
		Config.getListeSpectacleParSemaine().forEach((idSemaine,spectacles)->{
			spectacles.forEach( s -> {
				Config.getDataBase().update("INSERT INTO `spectacles` (`id_unique`, `date_spectacle`, `id_team`) VALUES (NULL, '"+s.getIdDate()+"', '"+listeSemaines.get(idSemaine).getIdTeam()+"');");
			});
		});
		
		String[] cmd = {"cmd.exe","/c","start http://localhost:8080/birt/frameset?__report=home.rptdesign"};
		  
		Runtime.getRuntime().exec(cmd);
		
	}

	private void naissanceDeLaNouvelleGeneration() {
		int fin = plannings.size();
		for (int i = 0; i < fin - taillePopulationRestante; i++) {
			plannings.remove(taillePopulationRestante);
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
		k = 0;
		taillePopulation = Integer.parseInt(Config.getProp().getProperty("taillePopulation"));
		taillePopulationRestante = Integer.parseInt(Config.getProp().getProperty("taillePopulationRestante"));
		Config.setTauxMutation(Integer.parseInt(Config.getProp().getProperty("pourMilleMutation")));

		logger.info("Creation population initiale taille : " + taillePopulation);

		for (int i = 0; i < taillePopulation; i++) {
			if (Config.isExitAlgo()) {
				break;
			}
			Planning p = new Planning();
			c.getSemaines().forEach((id,sem) -> {p.addSemaine(id, sem);});//on charge les equipes dispo par semaine
			plannings.add(p.build());//on attribue au hasard une equipe par semaine
		}

		evaluationPopulation(plannings);

	}

}
