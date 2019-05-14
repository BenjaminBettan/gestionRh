package com.bbe.franglaises.spectacle;

import java.time.LocalDateTime;

import com.bbe.franglaises.personne.Personnage;
import com.bbe.franglaises.personne.Personne;

public class Disponibilite {
	private Personne personne;
	private LocalDateTime idDate;	
	private Personnage personnage;
	private boolean dispoForte;


	public Disponibilite(Personne personne, LocalDateTime idDate, Personnage personnage, boolean dispoForte) {
		super();
		this.personne = personne;
		this.idDate = idDate;
		this.personnage = personnage;
		this.dispoForte = dispoForte;
	}

	public Personne getPersonne() {
		return personne;
	}

	public LocalDateTime getIdDate() {
		return idDate;
	}

	public boolean isDispoForte() {
		return dispoForte;
	}

	public Personnage getPersonnage() {
		return personnage;
	}


	
	
}
