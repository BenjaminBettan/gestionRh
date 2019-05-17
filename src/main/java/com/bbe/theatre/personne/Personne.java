package com.bbe.theatre.personne;

public class Personne {

	private int id = 0;
	private int nbDateCourant = 0;
	private String nomActeur;
	private int nbDateMin;
	private Personnage personnage;

	@Override
	public String toString() {
		return "Personne [id=" + id + ", nbDateCourant=" + nbDateCourant + ", nomActeur=" + nomActeur + ", nbDateMin="
				+ nbDateMin + ", personnage=" + personnage + "]";
	}

	public Personne(){}
	
	public Personne(int id, String nomActeur, int nbDateMin, Personne[] remplacants
			,Personnage personnage
			) {
		super();
		this.id = id;
		this.nomActeur = nomActeur;
		this.nbDateMin = nbDateMin;
		this.personnage = personnage;
	}

	public int malus(){
		return nbDateCourant >= nbDateMin ? 0 : nbDateMin - nbDateCourant;
	}

	public int getId() {
		return id;
	}
	
}
