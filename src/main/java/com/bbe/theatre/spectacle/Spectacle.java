package com.bbe.theatre.spectacle;

import java.time.LocalDateTime;

public class Spectacle {
	
	private final LocalDateTime idDate;
	private Team team = new Team();

	public Spectacle(LocalDateTime idDate) {
		super();
		this.idDate = idDate;
	}

	public LocalDateTime getIdDate() {
		return idDate;
	}
	
	public Team getTeam() {
		return team;
	}
}
