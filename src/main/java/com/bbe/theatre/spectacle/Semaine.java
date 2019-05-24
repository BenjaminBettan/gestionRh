package com.bbe.theatre.spectacle;

import java.util.ArrayList;
import java.util.List;

public class Semaine {
	private List<Integer> team = new ArrayList<>();

	public List<Integer> getTeam() {
		return team;
	}

	public void addTeam(int team) {
		this.team.add(team);
	}
	
	
}
