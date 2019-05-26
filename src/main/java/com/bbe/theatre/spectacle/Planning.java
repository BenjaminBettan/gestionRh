package com.bbe.theatre.spectacle;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import com.bbe.theatre._enum.CRITERE;

public class Planning {
	
	private Map<Integer, Semaine> semaines = new HashMap<>();
	private static Logger logger = Logger.getLogger(Planning.class);
	private int critere1 = -1;
	private int critere2 = -1;
	
	public Map<Integer, Semaine> getSemaines() {
		return semaines;
	}
	
	public Planning addSemaine(int id, Semaine s){
		semaines.put(id, new Semaine(s));
		return this;
	}
	
	public void creationPopulationMelange() {
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
				logger.debug("Taille suffisante 2 ou superieur, on melange");
				sem.setIdTeam(sem.getTeam().get(ThreadLocalRandom.current().nextInt(0, sem.getTeam().size())));
			}
		});
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
		// TODO Auto-generated method stub
		return 0;
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

}
