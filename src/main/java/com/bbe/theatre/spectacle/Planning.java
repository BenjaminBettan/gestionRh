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
	private int i = 0;
	private List<Semaine> semainesNonLockees = new ArrayList<>();

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
		for (int i = 0 ; i < Config.listeSemaines.size() - 1; i++) {
//			equipeCourante = semaines.get(Config.listeSemaines)
		}
		return compteur;
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
	
	
	
}
