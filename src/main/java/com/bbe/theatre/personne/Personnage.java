package com.bbe.theatre.personne;

public class Personnage {
	private String nom;
	private int id = 0;
	private boolean personnageFacultatif = false;
	
	@Override
	public String toString() {
		return "Personnage [nom=" + nom+"]";
	}

	public Personnage(int id,String nom) {
		super();
		this.nom = nom;
		this.id=id;
	}

	public int getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}

	public boolean isPersonnageFacultatif() {
		return personnageFacultatif;
	}
	
}
