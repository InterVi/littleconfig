package ru.intervi.littleconfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class FileStringList { //класс-основа для создания БД на файле
	private ArrayList<String> list = new ArrayList<String>();
	
	public void read(String file) { //чтение файла
		File f = new File(file);
		if (f.isFile()) {
			if (!list.isEmpty()) list.clear();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				while(reader.ready()) {
					String line = reader.readLine();
					if (line != null) list.add(line);
				}
				reader.close();
			} catch (Exception e) {e.printStackTrace();}
		} else System.out.println("[littleconfig] FileStringList: file " + file + " not found");
	}
	
	public void read(File file) { //чтение файла
		read(file.getAbsolutePath());
	}
	
	public void write(String file) { //запись файла
		if (!list.isEmpty()) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				for (int i = 0; i < list.size(); i++) {
					writer.write(list.get(i));
					writer.newLine();
				}
				writer.close();
			} catch (Exception e) {e.printStackTrace();}
		} else System.out.println("[littleconfig] FileStringList: empty list " + file);
	}
	
	public void write(File file) { //запись файла
		write(file.getAbsolutePath());
	}
	
	public ArrayList<String> get() { //получение ArrayList
		return list;
	}
	
	public int size() { //получить размер ArrayList
		return list.size();
	}
	
	public String[] getString() { //получить данные в виде массива строк
		String result[] = null;
		if (!list.isEmpty()) {
			result = new String[list.size()];
			for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
		} else System.out.println("[littleconfig] FileStringList: empty list");
		return result;
	}
}
