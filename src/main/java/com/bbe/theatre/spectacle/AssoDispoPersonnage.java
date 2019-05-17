package com.bbe.theatre.spectacle;

import com.bbe.theatre.personne.Personnage;

public class AssoDispoPersonnage {
	private Personnage p;
	private Disponibilite d;
	
	public AssoDispoPersonnage(Personnage p, Disponibilite d) {
		super();
		this.p = p;
		this.d = d;
	}
	public Personnage getP() {
		return p;
	}
	public Disponibilite getD() {
		return d;
	}
	
	@Override
	public String toString() {
		return "AssoDispoPersonnage [p=" + p + ", d=" + d + "]";
	}
	
}
