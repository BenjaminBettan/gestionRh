package com.bbe.theatre.spectacle;

import java.util.HashMap;
import java.util.Map;

import com.bbe.theatre.personne.Personnage;
import com.bbe.theatre.personne.Personne;

public class Team {
	
	private int idTeam = 0;
	private Map<Personnage, Personne> teamPourLeSpectacle = new HashMap<>();
	
	@Override
	public String toString() {
		return "Team [teamPourLeSpectacle=" + teamPourLeSpectacle + "]";
	}

	public Team(int idTeam, Map<Personnage, Personne> teamPourLeSpectacle) {
		super();
		this.idTeam = idTeam;
		this.teamPourLeSpectacle = teamPourLeSpectacle;
	}

	public Team() {
	}

	public Map<Personnage, Personne> getTeamPourLeSpectacle() {
		return teamPourLeSpectacle;
	}

	public void setTeamPourLeSpectacle(int idTeam) {
		this.idTeam = idTeam;
	}

	public int getIdTeam() {
		return idTeam;
	}

}
