package ru.intervi.littleconfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

import ru.intervi.littleconfig.utils.EasyLogger;

/**
 * простой класс для чтения и записи текстового файла на основе ArrayList<String>
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
				load = true;
			} catch (Exception e) {e.printStackTrace(); load = false;}
		} else {
			Log.warn("FileStringList: file " + file + " not found");
			load = false;
		}
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
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(file));
				Iterator<String> iter = list.iterator();
				while(iter.hasNext()) {
					String line = iter.next();
					if (line != null) {
						writer.write(line);
						writer.newLine();
					}
				}
			} catch (Exception e) {e.printStackTrace();} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch(Exception e) {e.printStackTrace();}
				}
			}
		} else Log.warn("FileStringList: empty list " + file);
	}
	
	/**
	 * запись в файл
	 * @param file записываемый объект File
	 */
	public void write(File file) { //запись файла
		write(file.getAbsolutePath());
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
