package com.bbe.theatre.spectacle;

import java.time.LocalDateTime;

import com.bbe.theatre._enum.DISPO;
import com.bbe.theatre.personne.Personnage;
import com.bbe.theatre.personne.Personne;

public class DisponibiliteJour {
	
	private Personne personne;
	private LocalDateTime idDate;
	private double numSemaine;
	private Personnage personnage;
	private DISPO dispo;

	public DisponibiliteJour setIdDate(LocalDateTime idDate) {
		this.idDate = idDate;
		return this;
	}

	public DisponibiliteJour setPersonnage(Personnage personnage) {
		this.personnage = personnage;
		return this;
	}

	public DisponibiliteJour(Personne personne, LocalDateTime idDate, Personnage personnage, DISPO dispoForte, double numSemaine) {
		super();
		this.personne = personne;
		this.idDate = idDate;
		this.personnage = personnage;
		this.dispo=dispoForte;
		this.numSemaine = numSemaine;
	}
	
	public Personne getPersonne() {
		return personne;
	}

	public LocalDateTime getIdDate() {
		return idDate;
	}


	public Personnage getPersonnage() {
		return personnage;
	}

	@Override
	public String toString() {
		return "Disponibilite [personne=" + personne + ", idDate=" + idDate + ", personnage=" + personnage + "]";
	}

	public DISPO getDispo() {
		return dispo;
	}

	public double getNumSemaine() {
		return numSemaine;
	}

}
