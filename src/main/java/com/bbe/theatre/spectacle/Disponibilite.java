package com.bbe.theatre.spectacle;

import java.time.LocalDateTime;

import com.bbe.theatre.personne.Personnage;
import com.bbe.theatre.personne.Personne;

public class Disponibilite {
	private Personne personne;
	private LocalDateTime idDate;	
	private Personnage personnage;
	private boolean dispoForte;

	public Disponibilite setIdDate(LocalDateTime idDate) {
		this.idDate = idDate;
		return this;
	}

	public Disponibilite setPersonnage(Personnage personnage) {
		this.personnage = personnage;
		return this;
	}

	public Disponibilite(Personne personne, LocalDateTime idDate, Personnage personnage, boolean dispoForte) {
		super();
		this.personne = personne;
		this.idDate = idDate;
		this.personnage = personnage;
		this.dispoForte = dispoForte;
	}
	
	public Disponibilite(Personne personne, LocalDateTime idDate, Personnage personnage) {
		super();
		this.personne = personne;
		this.idDate = idDate;
		this.personnage = personnage;
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

	@Override
	public String toString() {
		return "Disponibilite [personne=" + personne + ", idDate=" + idDate + ", personnage=" + personnage
				+ ", dispoForte=" + dispoForte + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idDate == null) ? 0 : idDate.hashCode());
		result = prime * result + ((personnage == null) ? 0 : personnage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Disponibilite other = (Disponibilite) obj;
		if (idDate == null) {
			if (other.idDate != null)
				return false;
		} else if (!idDate.equals(other.idDate))
			return false;
		if (personnage == null) {
			if (other.personnage != null)
				return false;
		} else if (!personnage.equals(other.personnage))
			return false;
		return true;
	}

	
	
	
}
