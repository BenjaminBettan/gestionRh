package com.bbe.theatre._enum;

import com.bbe.theatre.__main.Config;

public enum CRITERE 
{
	NB_SPECTACLE_MIN(Double.parseDouble(Config.getProp().getProperty("critere_NB_SPECTACLE_MIN_Ponderation"))),
	ECCART_TYPE(Double.parseDouble(Config.getProp().getProperty("critere_ECCART_TYPE_Ponderation"))),
	;
	
	private double ponderation;
	
	CRITERE( double ponderation_)
	{
		this.ponderation = ponderation_;
		
	}

	public double getPonderation() {
		return ponderation;
	}
}
