package com.bbe.theatre.spectacle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.log4j.Logger;

public class Semaine {
	private List<Integer> team = new ArrayList<>();
	private final int nbSpectacle;
	private final int numSemaine;
	private int idTeam = -1;
	private boolean locked = false;
	private static Logger logger = Logger.getLogger(Semaine.class);

	public Semaine(int nbSpectacle, int numSemaine) {
		this.nbSpectacle = nbSpectacle;
		this.numSemaine = numSemaine;
	}

	public void mute(){
		//mutation
		logger.debug("Mutation !");
		int rand = ThreadLocalRandom.current().nextInt(0, this.getTeam().size());

		while (true) {
			if (team.get(rand)==idTeam) {//on a choppé le meme, on recommence
				rand = ThreadLocalRandom.current().nextInt(0, this.getTeam().size());
			}
			else {
				break;
			}
		}

		setIdTeam(team.get(rand));
	}

	public Semaine(Semaine s) {
		this.nbSpectacle = s.nbSpectacle;
		this.numSemaine = s.numSemaine;
		s.team.forEach((i)->{
			this.team.add(i);
		});
	}

	public List<Integer> getTeam() {
		return team;
	}

	public void addTeam(int team) {
		this.team.add(team);
	}

	public int getNbSpectacle() {
		return nbSpectacle;
	}

	@Override
	public String toString() {
		return "Semaine [team=" + team + ", nbSpectacle=" + nbSpectacle + ", numSemaine=" + numSemaine + "]";
	}

	public int getNumSemaine() {
		return numSemaine;
	}

	public int getIdTeam() {
		return idTeam;
	}

	public void setIdTeam(int idTeam) {
		this.idTeam = idTeam;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

}
