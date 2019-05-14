package com.bbe.franglaises.spectacle;

import com.bbe.franglaises.personne.Personnage;

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
	
}
