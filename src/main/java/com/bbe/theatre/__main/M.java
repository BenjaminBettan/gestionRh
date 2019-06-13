package com.bbe.theatre.__main;

import com.bbe.theatre.DataBase;

public class M {
	private static DataBase d = new DataBase();

	public static void main(String[] args) {
		d.connect();
		d.setBaseNameAndConnect("simulation");
		String[] s = d.select("SELECT * FROM `spectacles` WHERE `date_spectacle`='2019-10-12T16:00';").split("/");
		for (String string : s) {
			System.out.println(string);

		}
		String idTeamOld = s[s.length-2];
	}

}
