package com.bbe.theatre._enum;

import com.bbe.theatre.__main.Config;

public enum CRITERE 
{
	NB_SPECTACLE_MIN(Integer.parseInt(Config.getProp().getProperty("critere_NB_SPECTACLE_MIN_Ponderation"))),
	ECCART_TYPE(Integer.parseInt(Config.getProp().getProperty("critere_ECCART_TYPE_Ponderation"))),
	;
	
	private int ponderation;
	
	CRITERE( int ponderation_)
	{
		this.ponderation = ponderation_;
		
	}

	public int getPonderation() {
		return ponderation;
	}
}
