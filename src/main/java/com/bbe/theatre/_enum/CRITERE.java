package com.bbe.theatre._enum;

public enum CRITERE 
{
	CRITERE_1(CRITERE_INTITULE.NB_SPECTACLE_MIN),
	CRITERE_2(CRITERE_INTITULE.ECCART_TYPE),
	;
	
	private CRITERE_INTITULE c;
	
	CRITERE(CRITERE_INTITULE password_)//constructor
	{
		this.c = password_;
		
	}

	public CRITERE_INTITULE getIntitule() {
		return c;
	}

}
