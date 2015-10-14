package ru.intervi.littleconfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
/**
 * простой класс для чтения и записи файла на основе ArrayList<String>
 */
public class FileStringList { //класс-основа для создания БД на файле
	public FileStringList() {}
	/**
	 * вызывает метод read(String file)
	 * @param file путь к текстовому файлу
	 */
	public FileStringList(String file) {read(file);}
	/**
	 * вызывает метод read(File file)
	 * @param file объект File текстового файла
	 */
	public FileStringList(File file) {read(file);}
	
	private ArrayList<String> list = new ArrayList<String>();
	
	/**
	 * чтение файла
	 * @param file путь к файлу
	 */
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
	
	/**
	 * чтение файла
	 * @param file читаемый объект File
	 */
	public void read(File file) { //чтение файла
		read(file.getAbsolutePath());
	}
	
	/**
	 * запись в файл
	 * @param file путь к файлу
	 */
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
	
	/**
	 * запись в файл
	 * @param file записываемый объект File
	 */
	public void write(File file) { //запись файла
		write(file.getAbsolutePath());
	}
	
	/**
	 * получить лист с данными
	 * @return строки из прочтенного файла
	 */
	public ArrayList<String> get() { //получение ArrayList
		return this.list;
	}
	
	/**
	 * размер ArrayList
	 * @return ArrayList<String>.size()
	 */
	public int size() { //получить размер ArrayList
		return list.size();
	}
	
	/**
	 * получить данные в виде String[]
	 * @return ArrayList<String> => String[]
	 */
	public String[] getString() { //получить данные в виде массива строк
		String result[] = null;
		if (!list.isEmpty()) {
			result = new String[list.size()];
			for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
		} else System.out.println("[littleconfig] FileStringList: empty list");
		return result;
	}
	
	/**
	 * установить список
	 * @param line список в виде String[]
	 */
	public void setList(String line[]) { //установить список
		list.clear();
		for (int i = 0; i < line.length; i++) list.add(line[i]);
	}
	
	/**
	 * установить список
	 * @param l список в виде ArrayList<String>
	 */
	public void setList(ArrayList<String> l) { //установит список
		list = l;
	}
}
