package ru.intervi.littleconfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigLoader { //чтение конфига из файла и получение значений
	private boolean get = false;
	private String[] file;
	
	private String remChars(String s, int p1, int p2) { //метод для вырезания символов из строк
		   String pp1, pp2, result;
		   if (p1 > -1 && p2 > p1 && p2 <= s.length()) {
			   	int pr = p2 - p1; pr = p1 + pr;
		   		if (p1 != 0) pp1 = s.substring(0, p1); else pp1 = s;
		   		if(p2 == s.length()) {if (p2 != pr) pp2 = s.substring(pr, p2); else pp2 = s;} else pp2 = s.substring(p2, s.length());
		   		if (pp1.equals(s)) result = pp2; else if (pp2.equals(s)) result = pp1; else result = pp1 + pp2;
		   	} else result = s;
		   return result;
	}
	
	private String trim(String s) { //чистка от пробелов
		char str[] = s.toCharArray();
		char result[] = new char[str.length];
		for (int i = 0; i < str.length; i++) {
			if (str[i] != ' ') result[i] = str[i];
		}
		return new String(result);
	}
	
	private class Logger { //отправка сообщений в консоль
		boolean send = true;
		public void info(String text) {
			if (send) System.out.println("[littleconfig]: " + text);
		}
	}
	private Logger log = new Logger();
	public void onLog() {log.send = true;} //включить вывод отладочной информации
	public void offLog() {log.send = false;} //выключить вывод отладочной информации
	
	public class result { //класс для передачи результата прогрузки файла
		public String[] list;
		public boolean load = false;
	}
	
	public void load(String f) { //загрузка конфина
		result result = new result();
		result = getList(f);
		if (result.load == true) {
			this.file = result.list;
			this.get = true;
		}
	}
	
	public void load(File f) { //загрузка конфига
		load(f.getAbsolutePath());
	}
	
	public void fakeload(String[] value) { //фейковая загрузка (установка значения из массива)
		this.file = value;
		this.get = true;
	}
	
	public result getList(String f) { //получаем текстовый файл массивом, очищенный от комментов
		String[] result;
		result res = new result();
		try {
			BufferedReader text = new BufferedReader(new FileReader(f));
			try { //узнаем кол-во строк, инициализируем и заполняем массив
				int l = 0;
				ArrayList<String> list = new ArrayList<String>();
				while(text.ready()) {
					String line = text.readLine();
					if (line != null) {
						if (line.trim().indexOf('#') != 0 && trim(line).length() > 0) { //если строка - комментарий или пустая, не добавляем ее
							list.add(line);
						}
					}
				}
				text.close();
				if (l > 0) { //проверка на нулевую длинну
					result = new String[l];
					for (int i = 0; i < l; i++) result[i] = list.get(i); //заполнение массива
				} else {result = null; log.info("emty config");}
			} catch(IOException e) {result = null; e.printStackTrace();}
		} catch(FileNotFoundException e) {result = null; e.printStackTrace();}
		if (result != null) { //сохранение результата
			result = clear(result);
			res.list = new String[result.length];
			res.list = result;
			res.load = true;
		}
		return res;
	}
	
	private String[] clear(String[] s) { //чистка от #комментов
		if (s == null) {log.info("configLoader clear: String is null"); return null;}
		for(int i = 0; i < s.length; i++) {
			if (s[i] != null) {
			while (s[i].indexOf('#') > -1) {
				String ch = s[i];
				int check = ch.indexOf('"'); //исключаем данные из параметра
				int check2 = ch.lastIndexOf('"');
				if (check > -1 & check2 > -1 & check2 > check) {
					ch = remChars(s[i], check, check2);
				}
				if (ch.indexOf('#') > -1) { //если коммент все же есть, удаляем его
					ch = s[i];
					s[i] = remChars(s[i], s[i].indexOf('#'), s[i].length());
				}
				if (ch.equals(s[i])) s[i] = " "; //на случай ошибок обрезки, в основном когда коммент на всю строку
			}}
		}
		return s;
	}
	
	private String getString(int index) { //получение переменной типа String по индексу
		String result = null;
		if (index < 0) {log.info("configLoader getString: failed, index < 0"); return result;}
		if (get == true && this.file != null) { //поиск и получение переменной из массива
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null) {
					result = file[index];
			}}
		} else if (get == false) log.info("configLoader getString(index): " + index + "(index) file not loaded");
		else if (file == null) log.info("configLoader getString(index): " + index + "(index) array file = null");
		if (result != null && result.indexOf(":") > -1) { //обрезка до двоеточия
			result = remChars(result, 0, result.indexOf(":")+1).trim();
			//обрезка от скобки до скобки, если они есть
			int ch = result.indexOf('"');
			int ch2 = result.lastIndexOf('"');
			if (ch > -1 & ch2 > -1 & ch2 > ch) {
				result = remChars(result, 0, ch+1);
				result = remChars(result, ch2-1, result.length());
			}
		} else if (result != null && result.indexOf(":") == -1) {log.info("configLoader getString(index): " + index + "(index) not ':', ride error"); result = null;}
		if (result == null) log.info("configLoader getString(index): " + index + "(index) = null");
		return result;
	}
	
	public String getString(String name) { //получение переменной типа String по названию
		String result = null;
		if (name == null) {log.info("configLoader getString: null name"); return result;}
		if (get == true && this.file != null) {
			result = getString(getIndexNoSection(name));
		} else log.info("configLoader getString(name): " + name + " error (file not load or null array");
		if (result == null) log.info("configLoader getString(name): " + name + " error, var not found");
		return result;
	}
	
	private int getInt(int index) { //получение переменной типа int по индексу
		String str = getString(index);
		String name = getName(index);
		if (str != null) str = trim(str).toLowerCase(); else log.info("configLoader getInt: " + name + " str = null");
		int num = 0;
		String error = null;
		try {
			if (str != null) {
				num = Integer.parseInt(str);
			} else {
				error = "configLoader getInt: " + name + " null String";
			}
		} catch(NumberFormatException e) {error = "configLoader getInt: " + name + " NumberFormatException: " + e.getMessage();}
		if (error != null) log.info(error);
		return num;
	}
	
	public int getInt(String name) { //получение переменной типа int по названию
		if (name == null) {log.info("configLoader getInt: null name"); return -1;}
		return getInt(getIndexNoSection(name));
	}
	
	private long getLong(int index) { //получение переменной типа long по индексу
		String name = getName(index);
		String str = getString(index);
		if (str != null) str = trim(str).toLowerCase(); else log.info("configLoader getLong: " + name + " str = null");
		long num = 0;
		String error = null;
		try {
			if (str != null) {
				num = Long.parseLong(str);
			} else error = "configLoader getLong: " + name + " null String";
		} catch(NumberFormatException e) {error = "configLoader getLong: " + name + " NumberFormatException: " + e.getMessage();}
		if (error != null) log.info(error);
		return num;
	}
	
	public long getLong(String name) { //получение переменной типа long по названию
		if (name == null) {log.info("configLoader getLong: null name"); return -1;}
		return getLong(getIndexNoSection(name));
	}
	
	private double getDouble(int index) { //получение переменной типа double по индексу
		String str = getString(index);
		String name = getName(index);
		if (str != null) str = trim(str).toLowerCase(); else log.info("configLoader getDouble: " + name + " str = null");
		double num = 0;
		String error = null;
		try {
			if (str != null) {
				num = Double.parseDouble(str);
			} else error = "configLoader getDouble: " + name + " null String";
		} catch(NumberFormatException e) {error = "configLoader getDouble: " + name + " NumberFormatException: " + e.getMessage();}
		if (error != null) log.info(error);
		return num;
	}
	
	public double getDouble(String name) { //получение переменной типа double по названию
		if (name == null) {log.info("configLoader getDouble: null name"); return -1;}
		return getDouble(getIndexNoSection(name));
	}
	
	private boolean getBoolean(int index) { //получение переменной типа boolean по индексу
		String str = getString(index);
		String name = getName(index);
		if (str != null) str = trim(str).toLowerCase(); else log.info("configLoader getBoolean: " + name + " str = null");
		boolean res = false;
		if (str != null && str.equals("true") | str.equals("false")) {
			res = Boolean.parseBoolean(str);
		} else if(str == null) log.info("configLoader getBoolean: " + name  + " null String"); else
			log.info("configLoader getBoolean: " + name + " var not boolean");
		return res;
	}
	
	public boolean getBoolean(String name) { //получение переменной типа boolean по названию
		if (name == null) {log.info("configLoader getBoolean: null name"); return false;}
		return getBoolean(getIndexNoSection(name));
	}
	
	public String[] getAll() { //получение всего конфига массивом строк
		if (get == true && file != null) return file; else {
			log.info("configLoader getAll: failed, returning null");
			return null;
		}
	}
	
	private String[] getStringArray(int index) { //получение переменной типа массив строк (по индексу)
		String[] result = null;
		if (index < 0) {log.info("configLoader getStringArray: failed, index < 0"); return result;}
		int pos = -1;
		if (this.get == true && file != null) {
			if (file[index] != null) pos = index;
		} else log.info("configLoader getStringArray(index): get " + index + " failed (config not loaded or file = null)");
		if (pos != -1) { //если переменная найдена, то начинаем проверку и последующее извелечение данных
			isarray is = new isarray();
			is = isArray(file[pos], pos); //проверка, является ли переменная массивом и если да, то каким именно
			if(is.isArray) {
				if(is.isSkobka) { //получение массива, заключенного в квадратные скобки
					String arr = file[pos];
					boolean empty; //проверка пустой ли массив (просто [])
					if ((arr.lastIndexOf("]") - (arr.indexOf("["))) > 2) empty = false; else empty = true;
					arr = remChars(arr, 0, arr.indexOf("[")+1);
					arr = remChars(arr, arr.lastIndexOf("]"), arr.length());
					if (trim(arr).length() > 2 && empty == false) empty = false; else empty = true; //еще проверка
					if (empty == false) {
					String[] result2 = null;
					result2 = arr.split(",");
					int resleng = 0;
					for(int i = 0; i < result2.length; i++) {
						if (result2[i] != null) resleng++;
					}
					result = new String[resleng];
					for(int i = 0; i < resleng; i++) {
						if (result2[i] != null) {
							result[i] = result2[i].trim();
							//обрезка от скобки до скобки, если они есть
							int ch = result[i].indexOf('"');
							int ch2 = result[i].lastIndexOf('"');
							if (ch > -1 & ch2 > -1 & ch2 > ch) {
								result[i] = remChars(result[i], 0, ch+1);
								result[i] = remChars(result[i], ch2-1, result[i].length());
							}
						}
					}
					} else result = new String[0]; //если массив пустой (просто [])
				} else { //получение массива, перечисленного через тире
					int leng = 0;
					int pp = pos+1;
					while(isArray(file[pp])) {leng++; pp++; if (pp >= file.length) break;}
					result = new String[leng];
					pp = pos+1;
					for(int i = 0; i < leng; i++) {
						result[i] = remChars(file[pp], 0, file[pp].indexOf("-")+1).trim();
						//обрезка от скобки до скобки, если они есть
						int ch = result[i].indexOf('"');
						int ch2 = result[i].lastIndexOf('"');
						if (ch > -1 & ch2 > -1 & ch2 > ch) {
							result[i] = remChars(result[i], 0, ch+1);
							result[i] = remChars(result[i], ch2-1, result[i].length());
						}
						pp++;
					}
				}
			} else log.info("configLoader getStringArray(index): var " + index + " not array");
			if (is.isCheck == false) log.info("configLoader getStringArray(index): " + index + " failed check is a array");
		} else log.info("configLoader getStringArray(index): var " + index + " not found");
		return result;
	}
	
	public String[] getStringArray(String name) { //получение переменной типа массив строк (по названию)
		String[] result = null;
		if (name == null) {log.info("configLoader getStringArray(name): null name"); return result;}
		if (this.get == true && file != null) {
			result = getStringArray(getIndexNoSection(name));
		} else log.info("configLoader getStringArray(name): get " + name + " failed (config not loaded or file = null)");
		if (result == null) log.info("configLoader getStringArray(name): " + name + " error, var not found");
		return result;
	}
	
	public class isarray { //класс для возвращение результата проверки переменной на массив
		public boolean isArray = false; //массив ли эта переменная
		public boolean isSkobka = false; //данные в квадратных скобках или через тире
		public boolean isCheck = false; //удалась ли проверка
	}
	
	private isarray isArray(String s, int p) { //проверка переменной на то, является ли она массивом
		boolean result = false;
		isarray res = new isarray();
		if (s == null) return res;
		int ps1 = s.indexOf("[");
		int ps2 = s.lastIndexOf("]");
		if (ps1 > -1 & ps2 > -1 & ps2 > ps1) { //проверка, заключены ли данные массива в квадратные скобки
			result = true;
			res.isSkobka = true;
			res.isCheck = true;
		}
		if (result == false && get == true && file != null) { //если нет, то проверка, не перечислены ли они через тире
			if ((p+1) < file.length) {
				res.isCheck = true;
				result = isArray(file[p+1]);
		}} else if (result != false && get == false | file == null) log.info("configLoader isArray: " + s + " failed check, not loaded config");
		res.isArray = result;
		return res;
	}
	
	private boolean isArray(String s) { //проверка, является ли строка компонентом массива (т.е. начинается с тире)
		boolean result = false;
		if (s == null) return result;
		String check = s;
		boolean check2 = false; //является ли тире первым символом в строке
		int tir = check.indexOf("-");
		if (tir > -1) { //проверка, является ли тире первым символом в строке (в таком случае это - ячейка массива)
			String check3 = trim(check).substring(0, 1);
			if (check3.equals("-")) check2 = true; else check2 = false;
		}
		result = check2;
		return result;
	}
	
	private int[] getIntArray(int index) { //получение массива типа int по индексу
		int[] result = null;
		String error = null;
		String[] text = getStringArray(index);
		String name = getName(index);
		if (text != null) {
		result = new int[text.length];
		for(int i = 0; i < text.length; i++) {
			try {
				result[i] = Integer.parseInt(text[i]);
			} catch(NumberFormatException e) {error = "configLoader getIntArray: " + name + " index of " + i + " NumberFormatException: " + e.getMessage();}
		}} else log.info("configLoader getIntArray: " + name + " null text (StringArray)");
		if (error != null) log.info(error);
		return result;
	}
	
	public int[] getIntArray(String name) { //получение массива типа int по названию
		if (name == null) return null;
		return getIntArray(getIndexNoSection(name));
	}
	
	private long[] getLongArray(int index) { //получение массива типа long по индексу
		long[] result = null;
		String error = null;
		String[] text = getStringArray(index);
		String name = getName(index);
		if (text != null) {
		result = new long[text.length];
		for(int i = 0; i < text.length; i++) {
			try {
				result[i] = Long.parseLong(text[i]);
			} catch(NumberFormatException e) {error = "configLoader getLongArray: " + name + " index of " + i + " NumberFormatException: " + e.getMessage();}
		}} else log.info("configLoader getLongArray: " + name + " null text (StringArray)");
		if (error != null) log.info(error);
		return result;
	}
	
	public long[] getLongArray(String name) { //получение массива типа long по названию
		if (name == null) return null;
		return getLongArray(getIndexNoSection(name));
	}
	
	private double[] getDoubleArray(int index) { //получение массива типа double по индексу
		double[] result = null;
		String error = null;
		String[] text = getStringArray(index);
		String name = getName(index);
		if (text != null) {
		result = new double[text.length];
		for(int i = 0; i < text.length; i++) {
			try {
				result[i] = Double.parseDouble(text[i]);
			} catch(NumberFormatException e) {error = "configLoader getLongArray: " + name + " index of " + i + " NumberFormatException: " + e.getMessage();}
		}} else log.info("configLoader getDoubleArray: " + name + " null text (StringArray)");
		if (error != null) log.info(error);
		return result;
	}
	
	public double[] getDoubleArray(String name) { //получение массива типа double по названию
		if (name == null) return null;
		return getDoubleArray(getIndexNoSection(name));
	}
	
	private boolean[] getBooleanArray(int index) { //получение массива типа boolean по индексу
		boolean[] result = null;
		String error = null;
		String[] text = getStringArray(index);
		String name = getName(index);
		if (text != null) {
		result = new boolean[text.length];
		for(int i = 0; i < text.length; i++) {
			if (text[i].equals("true") | text[i].equals("false")) {
				result[i] = Boolean.parseBoolean(text[i]);
			} else error = "configLoader getBooleanArray: " + name + " error, var index " + i + " in array not boolean";
		}} else log.info("configLoader getBooleanArray: " + name + " null text (StringArray)");
		if (error != null) log.info(error);
		return result;
	}
	
	public boolean[] getBooleanArray(String name) { //получение массива типа boolean по названию
		if (name == null) return null;
		return getBooleanArray(getIndexNoSection(name));
	}
	
	private boolean isSet(int index) { //проверка, прописана ли переменная (по индексу)
		boolean result = false;
		if (index < 0) {log.info("configLoader isSet: failed, index < 0"); return result;}
		if (index >= file.length) {log.info("configLoader isSet: failed, index > file.length"); return result;}
		if (get == true && this.file != null) {
			result = isParam(index); //является ли строка параметром
			if (index+1 < file.length) {if (!result && isArray(file[index+1])) result = true;} //является ли она массивом
			if (!result && index+1 < file.length) {
				if (file[index+1].indexOf(":") > 1) result = true; //является ли она секцией (упрощенный вариант проверки)
			}
		} else log.info("configLoader isSet(index): failed check " + index + ", config not loaded");
		return result;
	}
	
	public boolean isSet(String name) { //проверка, прописана ли переменная (по названию)
		boolean result = false;
		if (name == null) {log.info("configLoader isSet: null name"); return false;}
		if (get == true && this.file != null) {
			result = isSet(getIndexNoSection(name));
		} else log.info("configLoader isSet(name): failed check " + name + ", config not loaded");
		return result;
	}
	
	private boolean isSetArray(int index) { //проверка, прописан ли массив (по индексу)
		int pos = -1;
		boolean result = false;
		String name = getName(index);
		if (this.get == true && file != null) {
			pos = index;
			if (pos > -1) { //если переменная найдена
				isarray isr = isArray(file[pos], pos);
				result = isr.isArray;
				if (result && !isr.isSkobka) { //если массив через тире, то проверяем, есть ли хотя бы 1 элемент
					result = isArray(file[pos+1]);
					if (result) { //проверка, не пустой ли этот элемент
						if (trim(file[pos+1]).length() > 1) result = true; else result = false;
					}
				}
				if (result && isr.isSkobka) { //если массив через квадратные скобки, проверяем, есть ли там хотя бы 1 символ
					String arr = file[pos];
					arr = remChars(arr, 0, arr.indexOf("[")+1);
					arr = remChars(arr, arr.lastIndexOf("]"), arr.length());
					if (trim(arr).length() > 2) result = true; else result = false;
				}
			}
		} else log.info("configLoader isSet: failed check " + name + ", config not loaded");
		return result;
	}
	
	public boolean isSetArray(String name) { //проверка, прописан ли массив (по названию)
		if (name == null) {log.info("configLoader isSetArray: null name"); return false;}
		return isSetArray(getIndexNoSection(name));
	}
	
	private int getProbels(int index) { //узнаем кол-во пробелов в начале строки
		int result = -1;
		if (index < 0) {log.info("configLoader getProbels: failed, index < 0"); return result;}
		if (get == true && this.file != null & this.file[index] != null) {
			String str = file[index];
			String name = remChars(str.trim(), str.indexOf(":"), str.length());
			result = str.indexOf(name); //где первый символ названия и есть кол-во пробелов до него
		} else log.info("configLoader getProbels: failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	private boolean isParam(int index) { //является ли строка параметром
		boolean result = false;
		if (index < 0) {log.info("configLoader isParam: failed, index < 0"); return result;}
		if (get == true && this.file != null & this.file[index] != null) {
			String str = file[index].trim();
			if (str.indexOf(":") > 0 && !isArray(file[index])) { //проверка, есть ли что-то после двоеточия (элементы массивов не учитываем)
				String afterr[] = str.split(":");
				if (afterr.length > 1) {
					String after = afterr[1];
					if (after != null) {
						after = trim(after);
						if (after.length() > 0) result = true;
					}
				}
			}
		} else log.info("configLoader isParam: failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	private String getName(int index) { //получение названия переменной по индексу
		String result = null;
		if (index < 0) {log.info("configLoader getName: failed, index < 0"); return result;}
		if (get == true && this.file != null & this.file[index] != null && this.file[index].indexOf(":") != -1) {
			result = file[index].trim();
			result = remChars(result, result.indexOf(":"), result.length());
		} else log.info("configLoader getName: failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	private int getIndexSection (String name) { //получить индекс секции по названию
		int result = -1;
		if (name == null) {log.info("configLoader getIndexSection: null name"); return result;}
		if (this.get == true && file != null) {
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null) {
					if (remChars(file[i], file[i].indexOf(":"), file[i].length()).trim().equals(name)) {
						if (isSection(i)) {
							result = i;
							break;
						}
					}
			}}
		} else log.info("configLoader getIndexSection: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	private int getIndexNoSection (String name) { //получить индекс переменной по названию (не секции)
		int result = -1;
		if (name == null) {log.info("configLoader getIndexNoSection: null name"); return result;}
		if (this.get == true && file != null) {
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null) {
					if (remChars(file[i], file[i].indexOf(":"), file[i].length()).trim().equals(name)) {
						if (isSet(i) && !isSection(i)) {
							result = i;
							break;
						}
					}
			}}
		} else log.info("configLoader getIndexNoSection: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	private boolean isSection(int index) { //проверка, является ли переменная секцией (по индексу)
		boolean result = false;
		if (index < 0) {log.info("configLoader isSection: failed, index < 0"); return result;}
		if (this.get == true && file != null) {
			int posprob = getProbels(index), next = index+1;
			if (next < file.length) {
				int nextprob = getProbels(next);
				if (!isParam(index) & !isArray(file[next]) && nextprob > posprob) result = true;
				if (result) {
					boolean param = false; int i = next;
					do {
						int p = getProbels(i);
						if (p > posprob) {
							param = isSet(i);
						} else break;
						if (param) break;
						i++;
						if (i >= file.length) break;
					} while (!param);
					result = param; //если в секции не было параметров, значит это не секция
				}
			}
		} else log.info("configLoader isSection(index): failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	public boolean isSection(String name) { //проверка, является ли переменная секцией (по названию)
		boolean result = false;
		if (name == null) {log.info("configLoader isSection: null name"); return false;}
		if (this.get == true && file != null) {
			result = isSection(getIndexSection(name));
		} else log.info("configLoader isSection(name): failed check " + name + ", config not loaded or file[i] == null");
		return result;
	}
	
	public String[] getSectionVars(String name) { //получение названий переменных секции
		String[] result = null;
		if (name == null) {log.info("configLoader getSectionVars: null name"); return result;}
		if (this.get == true && file != null) {
			if (isSection(name)) {
				int index = getIndexSection(name), prob = getProbels(index);
				ArrayList<String> list = new ArrayList<String>();
				for (int i = index+1; i < file.length; i++) { //подсчитываем кол-во переменных
					int p = getProbels(i); //вложенные секции не учитываем
					if (p > prob && isSet(i)) {
						list.add(getName(i));
					}
				}
				int vars = list.size();
				result = new String[vars];
				for (int i = 0; i < vars; i++) { //заполняем массив
					result[i] = list.get(i);
				}
			}
		} else log.info("configLoader isSection: failed check " + name + ", config not loaded or file[i] == null");
		return result;
	}
	
	public int getSectionLength(String name) { //получение длинны секции (кол-ва переменных)
		int result = -1;
		if (name == null) {log.info("configLoader getSectionLength: null name"); return result;}
		if (this.get == true && file != null) {
			if (isSection(name)) {
				int index = getIndexSection(name);
				int p = getProbels(index);
				int p2 = 0, i = index+1;
				result = 0;
				do {
					p2 = getProbels(i);
					if (p2 > p && isSet(i)) result++;
					i++;
					if (i >= file.length) break;
				} while (p2 > p);
			}
		} else log.info("configLoader getSectionLength: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	public int getSectionRealLength(String name) { //получение реальной длинны секции в конфиге (кол-во строк)
		int result = -1;
		if (name == null) {log.info("configLoader getSectionRealLength: null name"); return result;}
		if (this.get == true && file != null) {
			if (isSection(name)) {
				int index = getIndexSection(name);
				int p = getProbels(index);
				int p2 = 0, i = index+1;
				result = 0;
				do {
					p2 = getProbels(i);
					if (p2 > p) result++;
					i++;
					if (i >= file.length) break;
				} while (p2 > p);
			}
		} else log.info("configLoader getSectionRealLength: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	public String[] getSectionNames() { //получение названий секций во всем конфиге
		String[] result = null;
		if (this.get == true && file != null) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null) {
					if (isSection(i)) list.add(getName(i));
				}
			}
			int l = list.size();
			result = new String[l];
			for (int i = 0; i < l; i++) {
				result[i] = list.get(i);
			}
		} else log.info("configLoader getSectionNames: failed get sections names, config not loaded or file == null");
		return result;
	}
	
	public String[] getSectionNames(String name) { //получение названий секций в секции
		String[] result = null;
		if (name == null) {log.info("configLoader getSectionNames: null name"); return result;}
		if (this.get == true && file != null) {
			int index = getIndexSection(name);
			int p = getProbels(index);
			ArrayList<String> list = new ArrayList<String>();
			for (int i = index+1; i < file.length; i++) {
				int p2 = getProbels(i);
				if (p2 > p) {
					if (isSection(i)) list.add(getName(i));
				} else break;
			}
			int l = list.size();
			result = new String[l];
			for (int i = 0; i < l; i++) {
				result[i] = list.get(i);
			}
		} else log.info("configLoader getSectionNames: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	private int getIndexInSection(String section, String name) { //получение индекса переменной в секции
		int result = -1;
		if (name == null | section == null) {log.info("configLoader getIndexInSection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			if (isSection(section)) {
				int index = getIndexSection(section);
				int p = getProbels(index);
				int p2 = 0, i = index+1;
				do {
					p2 = getProbels(i);
					if (p2 > p && isSet(i)) {
						if (getName(i).equals(name)) {
							result = i;
							break;
						}
					}
					i++;
					if (i >= file.length) break;
				} while (p2 > p);
			}
		} else log.info("configLoader getIndexInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public boolean isSetInSection(String section, String name) { //проверка, установлена ли переменная в сеции
		boolean result = false;
		if (name == null | section == null) {log.info("configLoader isSetInSection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = isSet(getIndexInSection(section, name));
		} else log.info("configLoader isSetInSection: failed check " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public boolean isSetArrayInSection(String section, String name) { //проверка, установлен ли массив в секции
		boolean result = false;
		if (name == null | section == null) {log.info("configLoader isSetArrayInSection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = isSetArray(getIndexInSection(section, name));
		} else log.info("configLoader isSetArrayInSection: failed check " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public String getStringSection(String section, String name) { //получение переменной типа String из секции
		String result = null;
		if (name == null | section == null) {log.info("configLoader getStringSection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = getString(getIndexInSection(section, name));
		} else log.info("configLoader getStringSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public int getIntSection(String section, String name) { //получение переменной типа int из секции
		int result = -1;
		if (name == null | section == null) {log.info("configLoader getIntSection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = getInt(getIndexInSection(section, name));
		} else log.info("configLoader getIntSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public long getLongSection(String section, String name) { //получение переменной типа long из секции
		long result = -1;
		if (name == null | section == null) {log.info("configLoader getLongSection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = getLong(getIndexInSection(section, name));
		} else log.info("configLoader getLongSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public double getDoubleSection(String section, String name) { //получение переменной типа double из секции
		double result = -1;
		if (name == null | section == null) {log.info("configLoader getDoubleSection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = getDouble(getIndexInSection(section, name));
		} else log.info("configLoader getDoubleSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public boolean getBooleanSection(String section, String name) { //получение переменной типа boolean из секции
		boolean result = false;
		if (name == null | section == null) {log.info("configLoader getBooleanSection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = getBoolean(getIndexInSection(section, name));
		} else log.info("configLoader getBooleanSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public String[] getStringArraySection(String section, String name) { //получение переменной типа массив String из секции
		String[] result = null;
		if (name == null | section == null) {log.info("configLoader getStringArraySection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = getStringArray(getIndexInSection(section, name));
		} else log.info("configLoader getStringArraySection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public int[] getIntArraySection(String section, String name) { //получение переменной типа массив int из секции
		int[] result = null;
		if (name == null | section == null) {log.info("configLoader getIntArraySection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = getIntArray(getIndexInSection(section, name));
		} else log.info("configLoader getIntArraySection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public long[] getLongArraySection(String section, String name) { //получение переменной типа массив long из секции
		long[] result = null;
		if (name == null | section == null) {log.info("configLoader getLongArraySection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = getLongArray(getIndexInSection(section, name));
		} else log.info("configLoader getLongArraySection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public double[] getDoubleArraySection(String section, String name) { //получение переменной типа массив double из секции
		double[] result = null;
		if (name == null | section == null) {log.info("configLoader getDoubleArraySection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = getDoubleArray(getIndexInSection(section, name));
		} else log.info("configLoader getDoubleArraySection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public boolean[] getBooleanArraySection(String section, String name) { //получение переменной типа массив boolean из секции
		boolean[] result = null;
		if (name == null | section == null) {log.info("configLoader getBooleanArraySection: null name or null section"); return result;}
		if (this.get == true && file != null) {
			result = getBooleanArray(getIndexInSection(section, name));
		} else log.info("configLoader getBooleanArraySection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	public ConfigLoader getLoader() {ConfigLoader loader = new ConfigLoader(); return loader;} //получение всего класса
	
	public class Methods { //класс со внутренними методами
		public Methods getMethods() {return new Methods();} //получить весь класс
		public int RecIndexNoSection(String name) { //узнать индекс параметра по имени
			return getIndexNoSection(name);
		}
		public String RecName(int index) { //узнать имя параметра по индексу
			return getName(index);
		}
		public int RecIndexSection(String name) { //узнать индекс секции по названию
			return getIndexSection(name);
		}
		public boolean IsSection(int index) { //проверить, является ли параметр секцией (по индексу)
			return isSection(index);
		}
		public int RecIndexInSection(String section, String name) { //получить индекс параметра в секции
			return getIndexInSection(section, name);
		}
		public boolean IsParam(int index) { //ялвяется ли строка параметром
			return isParam(index);
		}
		public int RecProbels(int index) { //получить кол-во пробелов в начале строки
			return getProbels(index);
		}
		public boolean IsSetArray(int index) { //проверка, прописан ли массив
			return isSetArray(index);
		}
		public String RecString(int index) { //получить строку по индексу
			return getString(index);
		}
		public String[] RecStringArray(int index) { //получить массив строк по индексу
			return getStringArray(index);
		}
		public isarray getIsArrayResult() { //получить внутренний класс isarray
			isarray is = new isarray();
			return is;
		}
		public isarray IsArray(String line, int index) { //проверить массив внутренним методом isArray
			return isArray(line, index);
		}
	}
	public Methods methods = new Methods();
}