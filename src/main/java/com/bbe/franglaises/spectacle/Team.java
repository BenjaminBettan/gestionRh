package com.bbe.franglaises.spectacle;

import java.util.HashMap;
import java.util.Map;

import com.bbe.franglaises.personne.Personnage;
import com.bbe.franglaises.personne.Personne;

public class Team {
	private int idTeam = 0;
	private boolean estValide = true;
	private String messageNonValide;
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

	public boolean isEstValide() {
		return estValide;
	}

	public void setEstValide(boolean estValide) {
		this.estValide = estValide;
	}

	public String getMessageNonValide() {
		return messageNonValide;
	}

	public void setMessageNonValide(String messageNonValide) {
		this.messageNonValide = messageNonValide;
	}

}
