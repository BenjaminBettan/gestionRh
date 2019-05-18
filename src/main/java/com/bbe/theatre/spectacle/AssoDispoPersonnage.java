package com.bbe.theatre.spectacle;

import com.bbe.theatre.personne.Personnage;

public class AssoDispoPersonnage {
	
	private Personnage p;
	private DisponibiliteSemaine d;
	
	public AssoDispoPersonnage(Personnage p, DisponibiliteSemaine d) {
		super();
		this.p = p;
		this.d = d;
	}
	
	public Personnage getP() {
		return p;
	}
	
	public DisponibiliteSemaine getD() {
		return d;
	}
	
	@Override
	public String toString() {
		return "AssoDispoPersonnage [p=" + p + ", d=" + d + "]";
	}
	
}
