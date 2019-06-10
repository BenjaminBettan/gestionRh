package com.bbe.theatre.personne;

public class Personne {

	private int id = 0;
	private int nbSpectacleCourant = 0;
	private int nbSpectacleMin = 0;

	private String nomActeur;
	private Personnage personnage;
	
	private String personneAvecQuiJeDoisJouer ="";
	private String personneAvecQuiJeNeDoisPasJouer ="";
	private boolean estDispoCetteSemaine = true;
	private boolean isAncien = false;

	public String getPersonneAvecQuiJeDoisJouer() {
		return personneAvecQuiJeDoisJouer;
	}

	public Personne setPersonneAvecQuiJeDoisJouer(String personneAvecQuiJeDoisJouer) {
		this.personneAvecQuiJeDoisJouer = personneAvecQuiJeDoisJouer;
		return this;
	}

	public String getPersonneAvecQuiJeNeDoisPasJouer() {
		return personneAvecQuiJeNeDoisPasJouer;
	}

	public Personne setPersonneAvecQuiJeNeDoisPasJouer(String personneAvecQuiJeNeDoisPasJouer) {
		this.personneAvecQuiJeNeDoisPasJouer = personneAvecQuiJeNeDoisPasJouer;
		return this;
	}

	public int getMalus(){
		return nbSpectacleCourant >= nbSpectacleMin ? 0 : nbSpectacleMin - nbSpectacleCourant;
	}

	public int getId() {
		return id;
	}
	
	

	@Override
	public String toString() {
		return "Personne [id=" + id + ", nbSpectacleCourant=" + nbSpectacleCourant + ", nbSpectacleMin=" + nbSpectacleMin
				+ ", nomActeur=" + nomActeur + ", personnage=" + personnage.getNom() + ", personneAvecQuiJeDoisJouer="
				+ personneAvecQuiJeDoisJouer + ", personneAvecQuiJeNeDoisPasJouer=" + personneAvecQuiJeNeDoisPasJouer
				+ "]";
	}

	public Personne setNbSpectacleMin(int nbSpectacleMin) {
		this.nbSpectacleMin = nbSpectacleMin;
		return this;
	}

	public int getNbSpectacleCourant() {
		return nbSpectacleCourant;
	}

	public Personne setNbSpectacleCourant(int nbDateCourant) {
		this.nbSpectacleCourant = nbDateCourant;
		return this;
	}

	public String getNomActeur() {
		return nomActeur;
	}

	public Personne setNomActeur(String nomActeur) {
		this.nomActeur = nomActeur;
		return this;
	}

	public Personnage getPersonnage() {
		return personnage;
	}

	public Personne setPersonnage(Personnage personnage) {
		this.personnage = personnage;
		return this;
	}

	public int getNbSpectacleMin() {
		return nbSpectacleMin;
	}

	public Personne setId(int id) {
		this.id = id;
		return this;
	}

	public boolean estDispoCetteSemaine() {
		return estDispoCetteSemaine;
	}

	public void setEstDispoCetteSemaine(boolean estDispoCetteSemaine) {
		this.estDispoCetteSemaine = estDispoCetteSemaine;
	}

	public void incrementCalculNbSpectMin(int i) {
		nbSpectacleCourant+=i;
	}

	public boolean isAncien() {
		return isAncien;
	}

	public Personne setAncien(boolean isAncien) {
		this.isAncien = isAncien;
		return this;
	}
	
}
