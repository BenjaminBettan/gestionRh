package com.bbe.theatre.spectacle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.log4j.Logger;

public class Semaine {
	private List<Integer> team = new ArrayList<>();
	private final int nbSpectacle;
	private final double numSemaine;
	private int idTeam = -1;
	private boolean locked = false;
	private static Logger logger = Logger.getLogger(Semaine.class);

	public Semaine(int nbSpectacle, double numSemaine) {
		this.nbSpectacle = nbSpectacle;
		this.numSemaine = numSemaine;
	}

	public Semaine mute(){
		//mutation
		logger.debug("Mutation !");
		if (this.getTeam().size()!=0) {
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
			return this;
		}
		else {
			return null;
		}
	}

	public Semaine(Semaine s) {
		this.nbSpectacle = s.nbSpectacle;
		this.numSemaine = s.numSemaine;
		this.idTeam = s.idTeam;
		this.locked = s.isLocked();
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

	public Double getNumSemaine() {
		return numSemaine;
	}

	public int getIdTeam() {
		return idTeam;
	}

	public Semaine setIdTeam(int idTeam) {
		this.idTeam = idTeam;
		return this;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

}
