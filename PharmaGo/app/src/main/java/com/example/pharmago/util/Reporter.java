package com.example.pharmago.util;

import android.os.Environment;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** @author Ceylon Linux (pvt) Ltd
 * */
public class Reporter {
	private static SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("EEE, d MMM yyyy 'at' HH.mm.ss.SSS");

	public static void writeEx(Exception ex) {
		File sdCard = Environment.getExternalStorageDirectory();
		File project = new File(sdCard.getAbsolutePath() + "/CWM");
		project.mkdir();
		File exceptionDir = new File(project.getAbsolutePath() + "/EXCEPTION");
		exceptionDir.mkdir();
		BufferedWriter out;
		try {
			File f = new File(exceptionDir.getAbsolutePath() + "/" + simpleDateFormatter.format(new Date(System.currentTimeMillis())) + ".txt");
			f.createNewFile();
			FileWriter fileWriter = new FileWriter(f);
			out = new BufferedWriter(fileWriter);
			out.write(ex.toString());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeJson(JSONObject json, String tag) {
		File sdCard = Environment.getExternalStorageDirectory();
		File project = new File(sdCard.getAbsolutePath() + "/FALCON");
		project.mkdir();
		File tagDir = new File(project.getAbsolutePath() + "/" + tag);
		tagDir.mkdir();

		BufferedWriter out;
		try {
			File f = new File(tagDir.getAbsolutePath() + "/" + simpleDateFormatter.format(new Date(System.currentTimeMillis())) + ".txt");
			f.createNewFile();
			FileWriter fileWriter = new FileWriter(f);
			out = new BufferedWriter(fileWriter);
			out.write(json.toString());
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeJsonRe(JSONObject json, String tag) {
		File sdCard = Environment.getExternalStorageDirectory();
		File project = new File(sdCard.getAbsolutePath() + "/FALCON_RE");
		project.mkdir();
		File tagDir = new File(project.getAbsolutePath() + "/" + tag);
		tagDir.mkdir();

		BufferedWriter out;
		try {
			File f = new File(tagDir.getAbsolutePath() + "/" + simpleDateFormatter.format(new Date(System.currentTimeMillis())) + ".txt");
			f.createNewFile();
			FileWriter fileWriter = new FileWriter(f);
			out = new BufferedWriter(fileWriter);
			out.write(json.toString());
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeString(String formatString, String tag) {
		File sdCard = Environment.getExternalStorageDirectory();
		File project = new File(sdCard.getAbsolutePath() + "/FALCON_SALES");
		project.mkdir();
		File tagDir = new File(project.getAbsolutePath() + "/" + tag);
		tagDir.mkdir();

		BufferedWriter out;
		try {
			File f = new File(tagDir.getAbsolutePath() + "/" + simpleDateFormatter.format(new Date(System.currentTimeMillis())) + ".txt");
			f.createNewFile();
			FileWriter fileWriter = new FileWriter(f);
			out = new BufferedWriter(fileWriter);
			out.write(formatString.toString());
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
