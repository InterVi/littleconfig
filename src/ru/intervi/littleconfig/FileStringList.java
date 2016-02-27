package ru.intervi.littleconfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

import ru.intervi.littleconfig.utils.EasyLogger;

/**
 * простой класс для чтения и записи текстового файла на основе ArrayList<String>
 */
public class FileStringList { //класс-основа для создания БД на файле
	public FileStringList() {}
	/**
	 * вызывает метод read(String path)
	 * @param file путь к текстовому файлу
	 * @throws IOException ошибка при чтении
	 */
	public FileStringList(String file) throws IOException {read(file);}
	/**
	 * вызывает метод read(File file)
	 * @param file объект File текстового файла
	 * @throws IOException ошибка при чтении
	 */
	public FileStringList(File file) throws IOException {read(file);}
	
	/**
	 * используемый логгер для вывода сообщений
	 */
	public EasyLogger Log = new EasyLogger();
	private boolean load = false;
	
	/**
	 * лист с содержимым текстового файла
	 */
	public ArrayList<String> list = new ArrayList<String>();
	
	/**
	 * чтение файла
	 * @param path путь к файлу
	 * @throws IOException ошибка при чтении
	 */
	public void read(String path) throws IOException { //чтение файла
		if (path == null) {Log.warn("FileStringList: error read, null path"); return;}
		read(new File(path));
	}
	
	/**
	 * чтение файла
	 * @param file читаемый объект File
	 * @throws IOException ошибка при чтении
	 */
	public void read(File file) throws IOException { //чтение файла
		if (file == null) {Log.warn("FileStringList: error read, null file"); return;}
		if (file.isFile()) {
			if (!list.isEmpty()) list.clear();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while(reader.ready()) {
				String line = reader.readLine();
				if (line != null) list.add(line);
			}
			reader.close();
			load = true;
		} else {
			Log.warn("FileStringList: file " + file + " not found");
			load = false;
		}
	}
	
	/**
	 * запись в файл
	 * @param path путь к файлу
	 * @throws IOException ошибка при записи
	 */
	public void write(String path) throws IOException { //запись файла
		if (path == null) {Log.warn("FileStringList: error write, null path"); return;}
		write(new File(path));
	}
	
	/**
	 * запись в файл
	 * @param file записываемый объект File
	 * @throws IOException ошибка при записи
	 */
	public void write(File file) throws IOException { //запись файла
		if (file == null) {Log.warn("FileStringList: error write, null write"); return;}
		if (!list.isEmpty()) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			Iterator<String> iter = list.iterator();
			while(iter.hasNext()) {
				String line = iter.next();
				if (line != null) {
					writer.write(line);
					writer.newLine();
				}
			}
			writer.close();
		} else Log.warn("FileStringList: empty list " + file);
	}
		
	/**
	 * получить данные в виде String[]
	 * @return ArrayList<String> => String[]
	 */
	public String[] getStringArray() { //получить данные в виде массива строк
		String result[] = null;
		if (!list.isEmpty()) {
			result = list.toArray(new String[list.size()]);
		} else Log.warn("FileStringList: empty list");
		return result;
	}
	
	/**
	 * установить список
	 * @param lines список в виде String[]
	 */
	public void setList(String lines[]) { //установить список
		list.clear();
		for (int i = 0; i < lines.length; i++) list.add(lines[i]);
	}
	
	/**
	 * установить список
	 * @param l список в виде ArrayList<String>
	 */
	public void setList(ArrayList<String> l) { //установит список
		list = l;
	}
	
	/**
	 * удалась ли загрузка файла
	 * @return true если да; false если нет
	 */
	public final boolean isLoad() { //был ли загружен конфиг
		return load;
	}
}