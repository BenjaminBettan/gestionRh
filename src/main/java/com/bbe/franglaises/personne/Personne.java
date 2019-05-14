package com.bbe.franglaises.personne;

import java.util.Arrays;

public class Personne {

	private boolean [] veutJouerLeSamedi = {false, false, false};//1 ere case aprem, soir les 2
	private int id = 0;
	private int nbDateCourant = 0;
	private String nomActeur;
	private int nbDateMin;
	private Personnage personnage;

	@Override
	public String toString() {
		return "Personne [nomActeur=" + nomActeur + ", personnage=" + personnage +",veutJouerLeSamedi=" + Arrays.toString(veutJouerLeSamedi) + ", id=" + id + ", nbDateCourant="
				+ nbDateCourant +  ", nbDateMin=" + nbDateMin + "]";
	}
	
	public Personne(){}
	
	public Personne(boolean[] veutJouerLeSamedi, int id, String nomActeur, int nbDateMin, Personne[] remplacants
			,Personnage personnage
			) {
		super();
		this.veutJouerLeSamedi = veutJouerLeSamedi;
		this.id = id;
		this.nomActeur = nomActeur;
		this.nbDateMin = nbDateMin;
		this.personnage = personnage;
	}

	public int malus(){
		return nbDateCourant >= nbDateMin ? 0 : nbDateMin - nbDateCourant;
	}
	
	public boolean veutJouerLeSamediAprem(){
		return veutJouerLeSamedi[0];
	}
	
	public boolean veutJouerLeSamediSoir(){
		return veutJouerLeSamedi[1];
	}
	
	public boolean veutJouerLeSamediTouteLaJournee(){
		return veutJouerLeSamedi[2];
	}

	public int getId() {
		return id;
	}
	
}
