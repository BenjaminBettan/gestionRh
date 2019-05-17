package com.bbe.theatre.personne;

public class Personne {

	private int id = 0;
	private int nbDateCourant = 0;
	private int nbSpectacleMin = 0;

	private String nomActeur;
	private Personnage personnage;
	
	private int personneAvecQuiJeDoisJouer = 0;
	private int personneAvecQuiJeNeDoisPasJouer = 0;

	public int getPersonneAvecQuiJeDoisJouer() {
		return personneAvecQuiJeDoisJouer;
	}

	public Personne setPersonneAvecQuiJeDoisJouer(int personneAvecQuiJeDoisJouer) {
		this.personneAvecQuiJeDoisJouer = personneAvecQuiJeDoisJouer;
		return this;
	}

	public int getPersonneAvecQuiJeNeDoisPasJouer() {
		return personneAvecQuiJeNeDoisPasJouer;
	}

	public Personne setPersonneAvecQuiJeNeDoisPasJouer(int personneAvecQuiJeNeDoisPasJouer) {
		this.personneAvecQuiJeNeDoisPasJouer = personneAvecQuiJeNeDoisPasJouer;
		return this;
	}

	public int getMalus(){
		return nbDateCourant >= nbSpectacleMin ? 0 : nbSpectacleMin - nbDateCourant;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "Personne [id=" + id + ", nbDateCourant=" + nbDateCourant + ", nomActeur=" + nomActeur + ", nbSpectacleMin="
				+ nbSpectacleMin + ", personnage=" + personnage + "]";
	}

	public Personne setNbSpectacleMin(int nbSpectacleMin) {
		this.nbSpectacleMin = nbSpectacleMin;
		return this;
	}
	
}
