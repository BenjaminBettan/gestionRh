package com.bbe.theatre.spectacle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import com.bbe.theatre.__main.Config;

public class EccartTypePersistance {
	
	public final static String path ="C:\\tools\\EccartTypePersistanceDb\\";
	private static Logger logger = Logger.getLogger(EccartTypePersistance.class);
	public void setEccartTypePersistance(Integer idTeam, Integer idTeam2, int calculEccartType) {
		new File(path + idTeam + "\\"+calculEccartType+"\\").mkdirs();
		
		try {
			new File(path + idTeam + "\\"+calculEccartType+"\\"+idTeam2).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

	public Integer getMeilleurTeam(Integer idTeam, List<Integer> idTeams) {
		for (int i = 1; i <= Config.getPersonnages().length; i++) {
			if (new File(path + idTeam + "\\"+i+"\\").exists()) {
				List<Integer> idTeams_ = new ArrayList<>();
				for (int id : idTeams) {
					if (new File(path + idTeam + "\\"+i+"\\"+id).exists()) {
						idTeams_.add(id);
					}
				}
				logger.info(idTeams_.size());
				return idTeams_.get(ThreadLocalRandom.current().nextInt(0, idTeams_.size()));
			}
		}
		return null;
	}

}