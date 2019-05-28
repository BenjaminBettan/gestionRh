package com.bbe.theatre.spectacle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import com.bbe.theatre.__main.Config;
import com.bbe.theatre._enum.CRITERE;

public class Planning {

	private static Logger logger = Logger.getLogger(Planning.class);

	private Map<Integer, Semaine> semaines = new HashMap<>();
	private int critere1 = -1;
	private int critere2 = -1;
	private int i,idPlanning,compt = 0;
	private static int compteurPlanning = 0;
	private List<Semaine> semainesNonLockees = new ArrayList<>();

	{
		idPlanning = compteurPlanning++;
		
	}
	
	public Planning(){
		super();
	}
	/**
	 * Brassage génétique des 2 parents et mutation
	 * @param p1
	 * @param p2
	 */
	public Planning(Planning p1, Planning p2){
		super();
		Config.listeSemaines.forEach( 
				idSem ->{
					int rand = ThreadLocalRandom.current().nextInt(0, 2);//0 ou 1
					if (rand==0) {
						this.addSemaine(idSem, p1.semaines.get(idSem));//le pere
					}else {
						this.addSemaine(idSem, p2.semaines.get(idSem));//la mere
					}
				});
		this.mute();//on fait parfois une mutation (prise en compte de la proba de mutation au sein de cette methode)
	}

	public Map<Integer, Semaine> getSemaines() {
		return semaines;
	}

	public Planning addSemaine(int id, Semaine s){
		semaines.put(id, new Semaine(s));
		return this;
	}

	public Planning build() {
		semaines.forEach((id,sem)-> {
			if (sem.getTeam().isEmpty()) {
				logger.warn("Semaine " + sem.getNumSemaine() +" n a pas d equipe. Le programme va quitter");
				System.exit(1);
			}
			else if (sem.getTeam().size() == 1) {
				sem.setIdTeam(sem.getTeam().get(0));
				sem.setLocked(true);
				logger.warn("Semaine " + sem.getNumSemaine() +" a une seule equipe");
			}
			else if (sem.getTeam().size() > 1) {
				logger.debug("Taille suffisante 2 ou superieur, on attribue au hasard une equipe");
				sem.setIdTeam(sem.getTeam().get(ThreadLocalRandom.current().nextInt(0, sem.getTeam().size())));
			}
		});
		return this;
	}

	public int calculCritere1() {
		switch (CRITERE.CRITERE_1.getIntitule()) {
		case NB_SPECTACLE_MIN :
			critere1 = calculNbSpectMin();
			break;
		case ECCART_TYPE :
			critere1 = calculEccartType();
			break;
		default:
			break;
		}

		return critere1;
	}

	private int calculNbSpectMin() {
		// TODO Auto-generated method stub
		return 0;
	}

	private int calculEccartType() {
		int compteur = 0;
		for (int j = 0 ; j < Config.listeSemaines.size() - 1; j++) {
			int equipeCourante = semaines.get(Config.listeSemaines.get(j)).getIdTeam();
			int equipeSuivante = semaines.get(Config.listeSemaines.get(j+1)).getIdTeam();

			compteur += calculEccartType(equipeCourante,equipeSuivante);

		}
		return compteur;
	}

	private int calculEccartType(int equipeCourante, int equipeSuivante) {
		Team eCourante = Config.listeTeam.get(equipeCourante);
		Team eSuivante = Config.listeTeam.get(equipeSuivante);
		compt = 0;

		eCourante.getTeamPourLeSpectacle().forEach( (personnage,p) -> {
			eSuivante.getTeamPourLeSpectacle().forEach( (personnage2,p2) -> {
				if (personnage.getNom().equals(personnage2.getNom())) {
					if (p.getId()!=p2.getId()) {
						compt++;
					}
				}
			});
		});

		return compt;
	}

	public int calculCritere2() {
		switch (CRITERE.CRITERE_2.getIntitule()) {
		case NB_SPECTACLE_MIN :
			critere2 = calculNbSpectMin();
			break;
		case ECCART_TYPE :
			critere2 = calculEccartType();
			break;
		default:
			break;
		}

		return critere2;
	}

	public Planning mute(){

		int rand = ThreadLocalRandom.current().nextInt(0, 1000);//entre 0 et 999
		i = 0;


		if ( rand < Config.tauxMutation ) {
			//verification qu une mutation est possible dans le planning. A faire une seule fois
			if (Config.testPlaning1==false) {
				Config.testPlaning1=true;

				semaines.forEach((id,sem)-> {
					if ( ! sem.isLocked() ) {
						i++;
					}
				});
				if (i==0) {
					logger.warn("Le programme va quitter. Pas de mutation possible dans tout le planning.");
					System.exit(1);
				}
				i = 0;
			}

			//poursuite de la mutation. On recupere les semaines non lockées

			if (i==0) {
				i=1;
				semaines.forEach((id,sem)-> {
					if ( ! sem.isLocked() ) {
						semainesNonLockees.add(sem);
					}
				});
			}

			rand = ThreadLocalRandom.current().nextInt(0, semainesNonLockees.size());//entre 0 et semainesNonLockees.size()

			semainesNonLockees.get(rand).mute();
		}

		return this;
	}

	public int getCritere1() {
		if (critere1==-1) {
			return calculCritere1();
		}
		return critere1;
	}

	public int getCritere2() {
		if (critere2==-1) {
			return calculCritere2();
		}
		return critere2;
	}
	
	public int getIdPlanning() {
		return idPlanning;
	}
	
}
