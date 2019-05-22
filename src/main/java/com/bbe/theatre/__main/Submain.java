package com.bbe.theatre.__main;

import java.io.IOException;
import java.sql.SQLException;

public class Submain extends Submain_A{

	public void go() throws IOException, SQLException {
		long l = System.currentTimeMillis();
		cleanDb();
		init();
		System.out.println("TEMPS DE CALCUL : "+ (System.currentTimeMillis() - l) + "ms");
	}

}
