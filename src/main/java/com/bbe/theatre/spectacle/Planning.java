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

	private Map<Double, Semaine> semaines = new HashMap<>();
	private int critereEccartType = -1;
	private int critereNbSpectMin = -1;
	private int i,idPlanning = 0;
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
						this.addSemaine(idSem, new Semaine(p1.semaines.get(idSem)));//le pere
					}else {
						this.addSemaine(idSem, new Semaine(p2.semaines.get(idSem)));//la mere
					}
				});
		this.mute();//on fait parfois une mutation (prise en compte de la proba de mutation au sein de cette methode)
	}

	public Map<Double, Semaine> getSemaines() {
		return semaines;
	}

	public Planning addSemaine(double id, Semaine s){
		semaines.put(id, new Semaine(s));
		return this;
	}

	public Planning build() {
		boolean premierSucces = false;
		for (int i = 0; i < Config.listeSemaines.size(); i++) {
			Semaine sem = semaines.get(Config.listeSemaines.get(i));
			if (sem.getTeam().isEmpty()) {
				logger.warn("Semaine " + sem.getNumSemaine() +" n a pas d equipe. Le programme va quitter");
				System.exit(1);
			}
			else if (sem.getTeam().size() == 1 || sem.isLocked()) {
				premierSucces = true;
				sem.setIdTeam(sem.getTeam().get(0));
				sem.setLocked(true);
				logger.warn("Semaine " + sem.getNumSemaine() +" a une seule equipe ou a ete locke par l utilisateur");//TODO
			}
			else if (sem.getTeam().size() > 1) {
				if (! premierSucces) {
					premierSucces = true;
					logger.debug("Taille suffisante 2 ou superieur, on attribue au hasard une equipe");
					sem.setIdTeam(sem.getTeam().get(ThreadLocalRandom.current().nextInt(0, sem.getTeam().size())));
				}
				else {
					Integer idTeam = Config.eccartTypePersistance.getMeilleurTeam(semaines.get(Config.listeSemaines.get(i -1)).getIdTeam(), sem.getTeam());
					if (idTeam==null) {
						logger.error("Fin du programme. Veuillez ");
					}
					sem.setIdTeam(idTeam);
				}
			}
		}
		return this;
	}

	public int getValue(){
		return CRITERE.NB_SPECTACLE_MIN.getPonderation()*calculNbSpectMin() + CRITERE.ECCART_TYPE.getPonderation() * calculEccartType();
	}
	
	public int calculNbSpectMin() {
		if (critereNbSpectMin==-1) {
			critereNbSpectMin = 0;
			
			semaines.forEach((idSem,sem)->{
				Config.listeTeam.get(sem.getIdTeam()).getTeamPourLeSpectacle().forEach((personnage,personne)->{
					personne.incrementCalculNbSpectMin(sem.getNbSpectacle());
				});
			});
			
			Config.listePersonnes2.forEach((id,pers)->{
				critereNbSpectMin+=pers.getMalus();
				pers.setNbSpectacleCourant(0);
			});
			
		}
		return critereNbSpectMin;
	}

	public int calculEccartType() {
		if (critereEccartType==-1) {
			critereEccartType = 0;
			for (int j = 0 ; j < Config.listeSemaines.size() - 1; j++) {
				int equipeCourante = semaines.get(Config.listeSemaines.get(j)).getIdTeam();
				int equipeSuivante = semaines.get(Config.listeSemaines.get(j+1)).getIdTeam();

				critereEccartType += Config.calculEccartType(equipeCourante,equipeSuivante);

			}
		}
		return critereEccartType;
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

	public int getIdPlanning() {
		return idPlanning;
	}
	
}
