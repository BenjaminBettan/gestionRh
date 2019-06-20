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
	
	@Override
	public String toString() {
		return "" + Config.formatter.format(getValue()) + " [Ecart type = " + critereEccartType + ", Contrainte spectacle minimum echec = " + critereNbSpectMin + "]";
	}
	public Planning(){
		super();
	}
	/**
	 * Brassage génétique des 2 parents et mutation
	 * @param p1
	 * @param p2
	 */
	public Planning(Planning p1, Planning p2, int tailleBrin){
		super();
		int resteTailleBrin = Config.getListeSemaines().size() % tailleBrin;
		int nbIteration = Config.getListeSemaines().size() / tailleBrin;
		int position = 0;
		List<Double> idSem;
		
		
		for (int i = 0; i < nbIteration; i++) {
			idSem = new ArrayList<>();
			for (int j = 0; j < tailleBrin; j++) {
				idSem.add(Config.getListeSemaines().get(position + j));
			}
			
			position+=tailleBrin;
			
			int rand = ThreadLocalRandom.current().nextInt(0, 2);//0 ou 1
			
			for (Double id : idSem) {
				if (rand==0) {
					this.addSemaine(id, new Semaine(p1.semaines.get(id)));//le pere
				}else {
					this.addSemaine(id, new Semaine(p2.semaines.get(id)));//la mere
				}				
			}
		}
		
		if (resteTailleBrin!=0) {
			for (int i = 0; i < resteTailleBrin; i++) {
				idSem = new ArrayList<>();
				for (int j = 0; j < tailleBrin; j++) {
					idSem.add(Config.getListeSemaines().get(position + j));
				}
				
				position+=tailleBrin;
				
				int rand = ThreadLocalRandom.current().nextInt(0, 2);//0 ou 1
				
				for (Double id : idSem) {
					if (rand==0) {
						this.addSemaine(id, new Semaine(p1.semaines.get(id)));//le pere
					}else {
						this.addSemaine(id, new Semaine(p2.semaines.get(id)));//la mere
					}				
				}
			}

		}
		
		mute();//on fait parfois une mutation (prise en compte de la proba de mutation au sein de cette methode)
	}

	public Map<Double, Semaine> getSemaines() {
		return semaines;
	}

	public Planning addSemaine(double id, Semaine s){
		semaines.put(id, new Semaine(s));
		return this;
	}

	public Planning build() {
		for (int i = 0; i < Config.getListeSemaines().size(); i++) {
			Semaine sem = semaines.get(Config.getListeSemaines().get(i));
			boolean semaineForcee = false;
			for (int j = 0; j < Config.getDateForcee().length; j++) {
				if (sem.getNumSemaine().equals(Double.parseDouble(Config.getDateForcee()[j].split(";")[0]))) {
					semaineForcee = true;
					String[] equipeA_forcer = Config.getDateForcee()[j].split(";")[1].split(",");
					Config.setId(0);
					Config.getListeTeam().forEach((id,t)->{
						if (Config.getId()>=0) {
							Config.setId(0);
							t.getTeamPourLeSpectacle().forEach((personnage, personne)->{
								for (String e : equipeA_forcer) {
									if (personne.getId()==Integer.parseInt(e.trim())) {
										Config.setId(Config.getId()+1);
									}
								}
								if (Config.getId()==Config.getPersonnages().length) {
									Config.setId( - id );
								}
							});
						}
					});
					
					sem.setIdTeam( - Config.getId() );
					break;
				}
			}
			
			if ( ! semaineForcee) {
				if (sem.getTeam().isEmpty()) {
					logger.warn("Semaine " + sem.getNumSemaine() +" n a pas d equipe.");
				}
				else if (sem.getTeam().size() == 1 || sem.isLocked()) {
					sem.setIdTeam(sem.getTeam().get(0));
					sem.setLocked(true);
					logger.warn("Semaine " + sem.getNumSemaine() +" a une seule equipe");
				}
				else if (sem.getTeam().size() > 1) {
					logger.debug("Taille suffisante 2 ou superieur, on attribue au hasard une equipe");
					sem.setIdTeam(sem.getTeam().get(ThreadLocalRandom.current().nextInt(0, sem.getTeam().size())));
				}
			}
		}
		return this;
	}

	public double getValue(){
		return CRITERE.NB_SPECTACLE_MIN.getPonderation()*calculNbSpectMin() + CRITERE.ECCART_TYPE.getPonderation() * calculEccartType();
	}
	
	public int calculNbSpectMin() {
		if (critereNbSpectMin==-1) {
			critereNbSpectMin = 0;
			
			semaines.forEach((idSem,sem)->{
				Config.getListeTeam().get(sem.getIdTeam()).getTeamPourLeSpectacle().forEach((personnage,personne)->{
					personne.incrementCalculNbSpectMin(sem.getNbSpectacle());
				});
			});
			
			Config.getListePersonnes2().forEach((id,pers)->{
				critereNbSpectMin+=pers.getMalus();
				pers.setNbSpectacleCourant(0);
			});
			
		}
		return critereNbSpectMin;
	}

	public int calculEccartType() {
		if (critereEccartType==-1) {
			critereEccartType = 0;
			for (int j = 0 ; j < Config.getListeSemaines().size() - 1; j++) {
				int equipeCourante = semaines.get(Config.getListeSemaines().get(j)).getIdTeam();
				int equipeSuivante = semaines.get(Config.getListeSemaines().get(j+1)).getIdTeam();

				critereEccartType += Config.calculEccartType(equipeCourante,equipeSuivante);

			}
		}
		return critereEccartType;
	}
	
	public Planning mute(){

		int rand = ThreadLocalRandom.current().nextInt(0, 1000);//entre 0 et 999
		i = 0;


		if ( rand < Config.getTauxMutation() ) {
			//verification qu une mutation est possible dans le planning. A faire une seule fois
			if (Config.isTestPlaning1()==false) {
				Config.setTestPlaning1(true);

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
			
			boolean exit = false;
			while ( ! exit) {
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

				Semaine s_ = semainesNonLockees.get(rand).mute();
				
				if (s_!=null) {
					exit = true;
				}
				
			}

		}

		return this;
	}

	public int getIdPlanning() {
		return idPlanning;
	}
	
}
