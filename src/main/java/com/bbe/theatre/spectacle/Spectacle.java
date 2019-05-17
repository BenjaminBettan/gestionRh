package com.bbe.theatre.spectacle;

import java.time.LocalDateTime;

public class Spectacle {
	
	private final LocalDateTime idDate;
	private int nbDateCeJour;
	private Team team;

	public Spectacle(LocalDateTime idDate, int nbDateCeJour) {
		super();
		this.idDate = idDate;
		this.nbDateCeJour = nbDateCeJour;
		team = new Team();
	}

	public int getNbDateCeJour() {
		return nbDateCeJour;
	}

	public LocalDateTime getIdDate() {
		return idDate;
	}
	
	public Team getTeam() {
		return team;
	}
}
