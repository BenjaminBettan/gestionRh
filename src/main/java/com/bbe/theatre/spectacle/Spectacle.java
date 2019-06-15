package com.bbe.theatre.spectacle;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

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
	public int getNumSemaine(){
		return idDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
	}
	
	public Team getTeam() {
		return team;
	}

	@Override
	public String toString() {
		return "Spectacle [idDate=" + idDate + ", team=" + team + ", getNumSemaine()=" + getNumSemaine() + "]";
	}
	
}
