package com.bbe.theatre.spectacle;

import com.bbe.theatre.personne.Personnage;
import com.bbe.theatre.personne.Personne;

public class DisponibiliteSemaine {
	
	private Personne personne;
	private Personnage personnage;
	private int idDate;	//numero de semaine
	
	public Personne getPersonne() {
		return personne;
	}
	public DisponibiliteSemaine setPersonne(Personne personne) {
		this.personne = personne;
		return this;
	}
	public int getIdDate() {
		return idDate;
	}
	public DisponibiliteSemaine setIdDate(int idDate) {
		this.idDate = idDate;
		return this;
	}
	public Personnage getPersonnage() {
		return personnage;
	}
	public DisponibiliteSemaine setPersonnage(Personnage personnage) {
		this.personnage = personnage;
		return this;
	}
	
}
