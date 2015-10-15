package ru.intervi.littleconfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ru.intervi.littleconfig.utils.Utils;
import ru.intervi.littleconfig.utils.EasyLogger;

/**
 * чтение файла конфигураци
 */
public class ConfigLoader { //чтение конфига из файла и получение значений
	/**
	 * необходимо загрузить конфиг для работы с ним
	 */
	public ConfigLoader() {}
	/**
	 * вызывает метод load(String f)
	 * @param file путь к конфигу
	 */
	public ConfigLoader(String file) {load(file);}
	/**
	 * вызывает метод load(File f)
	 * @param file объект File конфига для чтения
	 */
	public ConfigLoader(File file) {load(file);}
	
	private EasyLogger Log = new EasyLogger();
	
	private boolean get = false;
	private String[] file;
	
	/**
	 * класс для передачи результата прогрузки файла
	 */
	public class LoaderResult { //класс для передачи результата прогрузки файла
		/**
		 * загруженный когфиг
		 */
		public String[] list;
		/**
		 * удалась ли загрузка
		 */
		public boolean load = false;
	}
	
	/**
	 * прочитать конфиг
	 * @param f путь к конфигу
	 */
	public void load(String f) { //загрузка конфина
		LoaderResult result = new LoaderResult();
		result = getList(f);
		if (result.load == true) {
			file = result.list;
			get = true;
		}
	}
	
	/**
	 * прочитать конфиг
	 * @param f объект File конфига для чтения
	 */
	public void load(File f) { //загрузка конфига
		load(f.getAbsolutePath());
	}
	
	/**
	 * фейковая загрузка данных
	 * @param value массив строк, в котором представлен конфиг
	 */
	public void fakeload(String[] value) { //фейковая загрузка (установка значения из массива)
		file = value;
		get = true;
	}
	
	/**
	 * загрузка конфига, прямой метод (вызывается load(String f))
	 * @param f путь к конфигу
	 * @return результат в виде LoaderResult
	 */
	public LoaderResult getList(String f) { //получаем текстовый файл массивом, очищенный от комментов
		String[] result;
		LoaderResult res = new LoaderResult();
		try {
			BufferedReader text = new BufferedReader(new FileReader(f));
			try { //узнаем кол-во строк, инициализируем и заполняем массив
				int l = 0;
				ArrayList<String> list = new ArrayList<String>();
				while(text.ready()) {
					String line = text.readLine();
					if (line != null) {
						if (line.trim().indexOf('#') != 0 && Utils.trim(line).length() > 0) { //если строка - комментарий или пустая, не добавляем ее
							list.add(line);
						}
					}
				}
				text.close();
				if (l > 0) { //проверка на нулевую длинну
					result = new String[l];
					for (int i = 0; i < l; i++) result[i] = list.get(i); //заполнение массива
				} else {result = null; Log.info("emty config");}
			} catch(IOException e) {result = null; e.printStackTrace();}
		} catch(FileNotFoundException e) {result = null; e.printStackTrace();}
		catch(Exception e) {result = null; e.printStackTrace();}
		if (result != null) { //сохранение результата
			result = clear(result);
			res.list = new String[result.length];
			res.list = result;
			res.load = true;
		}
		return res;
	}
	
	private String[] clear(String[] s) { //чистка от #комментов
		if (s == null) {Log.info("ConfigLoader clear: String is null"); return null;}
		for(int i = 0; i < s.length; i++) {
			if (s[i] != null) {
			while (s[i].indexOf('#') > -1) {
				String ch = s[i];
				int check = ch.indexOf('"'); //исключаем данные из параметра
				int check2 = ch.lastIndexOf('"');
				if (check > -1 & check2 > -1 & check2 > check) {
					ch = Utils.remChars(s[i], check, check2);
				}
				if (ch.indexOf('#') > -1) { //если коммент все же есть, удаляем его
					ch = s[i];
					s[i] = Utils.remChars(s[i], s[i].indexOf('#'), s[i].length());
				}
				if (ch.equals(s[i])) s[i] = " "; //на случай ошибок обрезки, в основном когда коммент на всю строку
			}}
		}
		return s;
	}
	
	private String getString(int index) { //получение переменной типа String по индексу
		String result = null;
		if (index < 0) {Log.info("ConfigLoader getString: failed, index < 0"); return result;}
		if (get == true && file != null) { //поиск и получение переменной из массива
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null) {
					result = file[index];
			}}
		} else if (get == false) Log.info("ConfigLoader getString(index): " + index + "(index) file not loaded");
		else if (file == null) Log.info("ConfigLoader getString(index): " + index + "(index) array file = null");
		if (result != null && result.indexOf(":") > -1) { //обрезка до двоеточия
			result = Utils.remChars(result, 0, result.indexOf(":")+1).trim();
			//обрезка от скобки до скобки, если они есть
			int ch = result.indexOf('"');
			int ch2 = result.lastIndexOf('"');
			if (ch > -1 & ch2 > -1 & ch2 > ch) {
				result = Utils.remChars(result, 0, ch+1);
				result = Utils.remChars(result, ch2-1, result.length());
			}
		} else if (result != null && result.indexOf(":") == -1) {Log.info("ConfigLoader getString(index): " + index + "(index) not ':', ride error"); result = null;}
		if (result == null) Log.info("ConfigLoader getString(index): " + index + "(index) = null");
		return result;
	}
	
	/**
	 * получить значение переменной в винде строки
	 * @param name имя переменной
	 * @return значение в виде строки (null, если переменная не найдена)
	 */
	public String getString(String name) { //получение переменной типа String по названию
		String result = null;
		if (name == null) {Log.info("ConfigLoader getString: null name"); return result;}
		if (get == true && file != null) {
			result = getString(getIndexNoSection(name));
		} else Log.info("ConfigLoader getString(name): " + name + " error (file not load or null array");
		if (result == null) Log.info("ConfigLoader getString(name): " + name + " error, var not found");
		return result;
	}
	
	private int getInt(int index) { //получение переменной типа int по индексу
		String str = getString(index);
		String name = getName(index);
		if (str != null) str = Utils.trim(str).toLowerCase(); else Log.info("ConfigLoader getInt: " + name + " str = null");
		int num = 0;
		String error = null;
		try {
			if (str != null) {
				num = Integer.parseInt(str);
			} else {
				error = "ConfigLoader getInt: " + name + " null String";
			}
		} catch(NumberFormatException e) {error = "ConfigLoader getInt: " + name + " NumberFormatException: " + e.getMessage();}
		if (error != null) Log.info(error);
		return num;
	}
	
	/**
	 * получить значение переменной в виде int
	 * @param name имя переменной
	 * @return значение переменной в виде int (0, если переменная не найдена)
	 */
	public int getInt(String name) { //получение переменной типа int по названию
		if (name == null) {Log.info("ConfigLoader getInt: null name"); return -1;}
		return getInt(getIndexNoSection(name));
	}
	
	private long getLong(int index) { //получение переменной типа long по индексу
		String name = getName(index);
		String str = getString(index);
		if (str != null) str = Utils.trim(str).toLowerCase(); else Log.info("ConfigLoader getLong: " + name + " str = null");
		long num = 0;
		String error = null;
		try {
			if (str != null) {
				num = Long.parseLong(str);
			} else error = "ConfigLoader getLong: " + name + " null String";
		} catch(NumberFormatException e) {error = "ConfigLoader getLong: " + name + " NumberFormatException: " + e.getMessage();}
		if (error != null) Log.info(error);
		return num;
	}
	
	/**
	 * получить значение переменной в виде long
	 * @param name имя переменной
	 * @return значение переменной в виде long (0, если переменная не найдена)
	 */
	public long getLong(String name) { //получение переменной типа long по названию
		if (name == null) {Log.info("ConfigLoader getLong: null name"); return -1;}
		return getLong(getIndexNoSection(name));
	}
	
	private double getDouble(int index) { //получение переменной типа double по индексу
		String str = getString(index);
		String name = getName(index);
		if (str != null) str = Utils.trim(str).toLowerCase(); else Log.info("ConfigLoader getDouble: " + name + " str = null");
		double num = 0;
		String error = null;
		try {
			if (str != null) {
				num = Double.parseDouble(str);
			} else error = "ConfigLoader getDouble: " + name + " null String";
		} catch(NumberFormatException e) {error = "ConfigLoader getDouble: " + name + " NumberFormatException: " + e.getMessage();}
		if (error != null) Log.info(error);
		return num;
	}
	
	/**
	 * получить значение переменной в виде double
	 * @param name имя переменной
	 * @return значение переменной в виде double (0, если значение не найдено)
	 */
	public double getDouble(String name) { //получение переменной типа double по названию
		if (name == null) {Log.info("ConfigLoader getDouble: null name"); return -1;}
		return getDouble(getIndexNoSection(name));
	}
	
	private boolean getBoolean(int index) { //получение переменной типа boolean по индексу
		String str = getString(index);
		String name = getName(index);
		if (str != null) str = Utils.trim(str).toLowerCase(); else Log.info("ConfigLoader getBoolean: " + name + " str = null");
		boolean res = false;
		if (str != null && str.equals("true") | str.equals("false")) {
			res = Boolean.parseBoolean(str);
		} else if(str == null) Log.info("ConfigLoader getBoolean: " + name  + " null String"); else
			Log.info("ConfigLoader getBoolean: " + name + " var not boolean");
		return res;
	}
	
	/**
	 * получить значение переменной в виде boolean
	 * @param name имя переменной
	 * @return значение переменной в виде boolean (false, если переменная не найдена)
	 */
	public boolean getBoolean(String name) { //получение переменной типа boolean по названию
		if (name == null) {Log.info("ConfigLoader getBoolean: null name"); return false;}
		return getBoolean(getIndexNoSection(name));
	}
	
	/**
	 * получение всего конфига
	 * @return конфиг в виде массива строк
	 */
	public String[] getAll() { //получение всего конфига массивом строк
		if (get == true && file != null) return file; else {
			Log.info("ConfigLoader getAll: failed, returning null");
			return null;
		}
	}
	
	private String[] getStringArray(int index) { //получение переменной типа массив строк (по индексу)
		String[] result = null;
		if (index < 0) {Log.info("ConfigLoader getStringArray: failed, index < 0"); return result;}
		int pos = -1;
		if (get == true && file != null) {
			if (file[index] != null) pos = index;
		} else Log.info("ConfigLoader getStringArray(index): get " + index + " failed (config not loaded or file = null)");
		if (pos != -1) { //если переменная найдена, то начинаем проверку и последующее извелечение данных
			IsArray is = new IsArray();
			is = isArray(file[pos], pos); //проверка, является ли переменная массивом и если да, то каким именно
			if(is.IsArray) {
				if(is.isSkobka) { //получение массива, заключенного в квадратные скобки
					String arr = file[pos];
					boolean empty; //проверка пустой ли массив (просто [])
					if ((arr.lastIndexOf("]") - (arr.indexOf("["))) > 2) empty = false; else empty = true;
					arr = Utils.remChars(arr, 0, arr.indexOf("[")+1);
					arr = Utils.remChars(arr, arr.lastIndexOf("]"), arr.length());
					if (Utils.trim(arr).length() > 2 && empty == false) empty = false; else empty = true; //еще проверка
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
								result[i] = Utils.remChars(result[i], 0, ch+1);
								result[i] = Utils.remChars(result[i], ch2-1, result[i].length());
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
						result[i] = Utils.remChars(file[pp], 0, file[pp].indexOf("-")+1).trim();
						//обрезка от скобки до скобки, если они есть
						int ch = result[i].indexOf('"');
						int ch2 = result[i].lastIndexOf('"');
						if (ch > -1 & ch2 > -1 & ch2 > ch) {
							result[i] = Utils.remChars(result[i], 0, ch+1);
							result[i] = Utils.remChars(result[i], ch2-1, result[i].length());
						}
						pp++;
					}
				}
			} else Log.info("ConfigLoader getStringArray(index): var " + index + " not array");
			if (is.isCheck == false) Log.info("ConfigLoader getStringArray(index): " + index + " failed check is a array");
		} else Log.info("ConfigLoader getStringArray(index): var " + index + " not found");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива строк
	 * @param name имя переменной
	 * @return значение переменной в виде массива строк (null, если переменная не найдена)
	 */
	public String[] getStringArray(String name) { //получение переменной типа массив строк (по названию)
		String[] result = null;
		if (name == null) {Log.info("ConfigLoader getStringArray(name): null name"); return result;}
		if (get == true && file != null) {
			result = getStringArray(getIndexNoSection(name));
		} else Log.info("ConfigLoader getStringArray(name): get " + name + " failed (config not loaded or file = null)");
		if (result == null) Log.info("ConfigLoader getStringArray(name): " + name + " error, var not found");
		return result;
	}
	
	/**
	 * класс с данными о результате проверки переменной на массив
	 */
	public class IsArray { //класс для возвращение результата проверки переменной на массив
		/**
		 * является ли переменная массивом
		 */
		public boolean IsArray = false; //массив ли эта переменная
		/**
		 * содержатся ли значения в квадратных скобках
		 */
		public boolean isSkobka = false; //данные в квадратных скобках или через тире
		/**
		 * удалось ли осуществаить проверку
		 */
		public boolean isCheck = false; //удалась ли проверка
	}
	
	private IsArray isArray(String s, int p) { //проверка переменной на то, является ли она массивом
		boolean result = false;
		IsArray res = new IsArray();
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
		}} else if (result != false && get == false | file == null) Log.info("ConfigLoader IsArray: " + s + " failed check, not loaded config");
		res.IsArray = result;
		return res;
	}
	
	private boolean isArray(String s) { //проверка, является ли строка компонентом массива (т.е. начинается с тире)
		boolean result = false;
		if (s == null) return result;
		String check = s;
		boolean check2 = false; //является ли тире первым символом в строке
		int tir = check.indexOf("-");
		if (tir > -1) { //проверка, является ли тире первым символом в строке (в таком случае это - ячейка массива)
			String check3 = Utils.trim(check).substring(0, 1);
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
			} catch(NumberFormatException e) {error = "ConfigLoader getIntArray: " + name + " index of " + i + " NumberFormatException: " + e.getMessage();}
		}} else Log.info("ConfigLoader getIntArray: " + name + " null text (StringArray)");
		if (error != null) Log.info(error);
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива int
	 * @param name имя переменной
	 * @return значение переменной в виде массива int (null, если переменная не найдена)
	 */
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
			} catch(NumberFormatException e) {error = "ConfigLoader getLongArray: " + name + " index of " + i + " NumberFormatException: " + e.getMessage();}
		}} else Log.info("ConfigLoader getLongArray: " + name + " null text (StringArray)");
		if (error != null) Log.info(error);
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива long
	 * @param name имя переменной
	 * @return значение переменной в виде массива long (null, если переменная не найдена)
	 */
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
			} catch(NumberFormatException e) {error = "ConfigLoader getLongArray: " + name + " index of " + i + " NumberFormatException: " + e.getMessage();}
		}} else Log.info("ConfigLoader getDoubleArray: " + name + " null text (StringArray)");
		if (error != null) Log.info(error);
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива double
	 * @param name имя переменной
	 * @return значение переменной в виде массива double (null, если не найдено)
	 */
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
			} else error = "ConfigLoader getBooleanArray: " + name + " error, var index " + i + " in array not boolean";
		}} else Log.info("ConfigLoader getBooleanArray: " + name + " null text (StringArray)");
		if (error != null) Log.info(error);
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива boolean
	 * @param name имя переменной
	 * @return значение переменной в виде массива boolean (null, если переменная не найдена)
	 */
	public boolean[] getBooleanArray(String name) { //получение массива типа boolean по названию
		if (name == null) return null;
		return getBooleanArray(getIndexNoSection(name));
	}
	
	private boolean isSet(int index) { //проверка, прописана ли переменная (по индексу)
		boolean result = false;
		if (index < 0) {Log.info("ConfigLoader isSet: failed, index < 0"); return result;}
		if (index >= file.length) {Log.info("ConfigLoader isSet: failed, index > file.length"); return result;}
		if (get == true && file != null) {
			result = isParam(index); //является ли строка параметром
			if (index+1 < file.length) {if (!result && isArray(file[index+1])) result = true;} //является ли она массивом
			if (!result && index+1 < file.length) {
				if (file[index+1].indexOf(":") > 1) result = true; //является ли она секцией (упрощенный вариант проверки)
			}
		} else Log.info("ConfigLoader isSet(index): failed check " + index + ", config not loaded");
		return result;
	}
	
	/**
	 * проверить, есть ли какое-либо значение у переменной (так же подходит для массивов)
	 * @param name имя переменной
	 * @return true если есть; false если нету
	 */
	public boolean isSet(String name) { //проверка, прописана ли переменная (по названию)
		boolean result = false;
		if (name == null) {Log.info("ConfigLoader isSet: null name"); return false;}
		if (get == true && file != null) {
			result = isSet(getIndexNoSection(name));
		} else Log.info("ConfigLoader isSet(name): failed check " + name + ", config not loaded");
		return result;
	}
	
	private boolean isSetArray(int index) { //проверка, прописан ли массив (по индексу)
		int pos = -1;
		boolean result = false;
		String name = getName(index);
		if (get == true && file != null) {
			pos = index;
			if (pos > -1) { //если переменная найдена
				IsArray isr = isArray(file[pos], pos);
				result = isr.IsArray;
				if (result && !isr.isSkobka) { //если массив через тире, то проверяем, есть ли хотя бы 1 элемент
					result = isArray(file[pos+1]);
					if (result) { //проверка, не пустой ли этот элемент
						if (Utils.trim(file[pos+1]).length() > 1) result = true; else result = false;
					}
				}
				if (result && isr.isSkobka) { //если массив через квадратные скобки, проверяем, есть ли там хотя бы 1 символ
					String arr = file[pos];
					arr = Utils.remChars(arr, 0, arr.indexOf("[")+1);
					arr = Utils.remChars(arr, arr.lastIndexOf("]"), arr.length());
					if (Utils.trim(arr).length() > 2) result = true; else result = false;
				}
			}
		} else Log.info("ConfigLoader isSet: failed check " + name + ", config not loaded");
		return result;
	}
	
	/**
	 * проверить, есть ли какое-либо значение у массива
	 * @param name имя переменной с массивом
	 * @return true если да; false если нет
	 */
	public boolean isSetArray(String name) { //проверка, прописан ли массив (по названию)
		if (name == null) {Log.info("ConfigLoader isSetArray: null name"); return false;}
		return isSetArray(getIndexNoSection(name));
	}
	
	private int getProbels(int index) { //узнаем кол-во пробелов в начале строки
		int result = -1;
		if (index < 0) {Log.info("ConfigLoader getProbels: failed, index < 0"); return result;}
		if (get == true && file != null & file[index] != null) {
			String str = file[index];
			String name = Utils.remChars(str.trim(), str.indexOf(":"), str.length());
			result = str.indexOf(name); //где первый символ названия и есть кол-во пробелов до него
		} else Log.info("ConfigLoader getProbels: failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	private boolean isParam(int index) { //является ли строка параметром
		boolean result = false;
		if (index < 0) {Log.info("ConfigLoader isParam: failed, index < 0"); return result;}
		if (get == true && file != null & file[index] != null) {
			String str = file[index].trim();
			if (str.indexOf(":") > 0 && !isArray(file[index])) { //проверка, есть ли что-то после двоеточия (элементы массивов не учитываем)
				String afterr[] = str.split(":");
				if (afterr.length > 1) {
					String after = afterr[1];
					if (after != null) {
						after = Utils.trim(after);
						if (after.length() > 0) result = true;
					}
				}
			}
		} else Log.info("ConfigLoader isParam: failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	private String getName(int index) { //получение названия переменной по индексу
		String result = null;
		if (index < 0) {Log.info("ConfigLoader getName: failed, index < 0"); return result;}
		if (get == true && file != null & file[index] != null && file[index].indexOf(":") != -1) {
			result = file[index].trim();
			result = Utils.remChars(result, result.indexOf(":"), result.length());
		} else Log.info("ConfigLoader getName: failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	private int getIndexSection (String name) { //получить индекс секции по названию
		int result = -1;
		if (name == null) {Log.info("ConfigLoader getIndexSection: null name"); return result;}
		if (get == true && file != null) {
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null) {
					if (Utils.remChars(file[i], file[i].indexOf(":"), file[i].length()).trim().equals(name)) {
						if (isSection(i)) {
							result = i;
							break;
						}
					}
			}}
		} else Log.info("ConfigLoader getIndexSection: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	private int getIndexNoSection (String name) { //получить индекс переменной по названию (не секции)
		int result = -1;
		if (name == null) {Log.info("ConfigLoader getIndexNoSection: null name"); return result;}
		if (get == true && file != null) {
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null) {
					if (Utils.remChars(file[i], file[i].indexOf(":"), file[i].length()).trim().equals(name)) {
						if (isSet(i) && !isSection(i)) {
							result = i;
							break;
						}
					}
			}}
		} else Log.info("ConfigLoader getIndexNoSection: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	private boolean isSection(int index) { //проверка, является ли переменная секцией (по индексу)
		boolean result = false;
		if (index < 0) {Log.info("ConfigLoader isSection: failed, index < 0"); return result;}
		if (get == true && file != null) {
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
		} else Log.info("ConfigLoader isSection(index): failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	/**
	 * проверить, является ли переменная секцией
	 * @param name имя секции
	 * @return true если да; false если нет
	 */
	public boolean isSection(String name) { //проверка, является ли переменная секцией (по названию)
		boolean result = false;
		if (name == null) {Log.info("ConfigLoader isSection: null name"); return false;}
		if (get == true && file != null) {
			result = isSection(getIndexSection(name));
		} else Log.info("ConfigLoader isSection(name): failed check " + name + ", config not loaded or file[i] == null");
		return result;
	}
	
	/**
	 * получить названия переменных в данной секции
	 * @param name имя секции
	 * @return названия переменных в виде массива строк (null, если ничего не найдено)
	 */
	public String[] getSectionVars(String name) { //получение названий переменных секции
		String[] result = null;
		if (name == null) {Log.info("ConfigLoader getSectionVars: null name"); return result;}
		if (get == true && file != null) {
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
		} else Log.info("ConfigLoader isSection: failed check " + name + ", config not loaded or file[i] == null");
		return result;
	}
	
	/**
	 * получить количество переменных в секции
	 * @param name имя секции
	 * @return кол-во переменных в виде int
	 */
	public int getSectionLength(String name) { //получение длинны секции (кол-ва переменных)
		int result = -1;
		if (name == null) {Log.info("ConfigLoader getSectionLength: null name"); return result;}
		if (get == true && file != null) {
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
		} else Log.info("ConfigLoader getSectionLength: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить длинну секции (количество строк в конфиге, строка с названием секции в число не входит)
	 * @param name имя секции
	 * @return количество строк в секции в виде int (-1, если секция не найдена либо пуста)
	 */
	public int getSectionRealLength(String name) { //получение реальной длинны секции в конфиге (кол-во строк)
		int result = -1;
		if (name == null) {Log.info("ConfigLoader getSectionRealLength: null name"); return result;}
		if (get == true && file != null) {
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
		} else Log.info("ConfigLoader getSectionRealLength: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить названия секций из всего конфига
	 * @return названия секций в виде массива строк (null, если ничего не найдено)
	 */
	public String[] getSectionNames() { //получение названий секций во всем конфиге
		String[] result = null;
		if (get == true && file != null) {
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
		} else Log.info("ConfigLoader getSectionNames: failed get sections names, config not loaded or file == null");
		return result;
	}
	
	/**
	 * получение названия секций из секции
	 * @param name имя секции
	 * @return названия секций в виде массива строк (null, если ничего не найдено)
	 */
	public String[] getSectionNames(String name) { //получение названий секций в секции
		String[] result = null;
		if (name == null) {Log.info("ConfigLoader getSectionNames: null name"); return result;}
		if (get == true && file != null) {
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
		} else Log.info("ConfigLoader getSectionNames: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	private int getIndexInSection(String section, String name) { //получение индекса переменной в секции
		int result = -1;
		if (name == null | section == null) {Log.info("ConfigLoader getIndexInSection: null name or null section"); return result;}
		if (get == true && file != null) {
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
		} else Log.info("ConfigLoader getIndexInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * проверить, есть ли какое-либо значение у переменной (аналог isSet для секций)
	 * @param section имя секции
	 * @param name имя переменной
	 * @return true если да; false если нет
	 */
	public boolean isSetInSection(String section, String name) { //проверка, установлена ли переменная в сеции
		boolean result = false;
		if (name == null | section == null) {Log.info("ConfigLoader isSetInSection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = isSet(getIndexInSection(section, name));
		} else Log.info("ConfigLoader isSetInSection: failed check " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * проверка, есть ли какое-либо значение у массива (аналог isSetArray для секций)
	 * @param section имя секции
	 * @param name имя переменной
	 * @return true если да; false если нет
	 */
	public boolean isSetArrayInSection(String section, String name) { //проверка, установлен ли массив в секции
		boolean result = false;
		if (name == null | section == null) {Log.info("ConfigLoader isSetArrayInSection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = isSetArray(getIndexInSection(section, name));
		} else Log.info("ConfigLoader isSetArrayInSection: failed check " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде строки из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде строки (null, если переменная не найдена)
	 */
	public String getStringSection(String section, String name) { //получение переменной типа String из секции
		String result = null;
		if (name == null | section == null) {Log.info("ConfigLoader getStringSection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = getString(getIndexInSection(section, name));
		} else Log.info("ConfigLoader getStringSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде int из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде int (-1, если переменная не найдена)
	 */
	public int getIntSection(String section, String name) { //получение переменной типа int из секции
		int result = -1;
		if (name == null | section == null) {Log.info("ConfigLoader getIntSection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = getInt(getIndexInSection(section, name));
		} else Log.info("ConfigLoader getIntSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде long из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде long (-1, если переменная не найдена)
	 */
	public long getLongSection(String section, String name) { //получение переменной типа long из секции
		long result = -1;
		if (name == null | section == null) {Log.info("ConfigLoader getLongSection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = getLong(getIndexInSection(section, name));
		} else Log.info("ConfigLoader getLongSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде double из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде double (-1, если переменная не найдена)
	 */
	public double getDoubleSection(String section, String name) { //получение переменной типа double из секции
		double result = -1;
		if (name == null | section == null) {Log.info("ConfigLoader getDoubleSection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = getDouble(getIndexInSection(section, name));
		} else Log.info("ConfigLoader getDoubleSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде boolean из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде boolean (false, если переменная не найдена)
	 */
	public boolean getBooleanSection(String section, String name) { //получение переменной типа boolean из секции
		boolean result = false;
		if (name == null | section == null) {Log.info("ConfigLoader getBooleanSection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = getBoolean(getIndexInSection(section, name));
		} else Log.info("ConfigLoader getBooleanSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива строк из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива строк (null, если переменная не найдена)
	 */
	public String[] getStringArraySection(String section, String name) { //получение переменной типа массив String из секции
		String[] result = null;
		if (name == null | section == null) {Log.info("ConfigLoader getStringArraySection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = getStringArray(getIndexInSection(section, name));
		} else Log.info("ConfigLoader getStringArraySection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива int из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива int (null, если переменная не найдена)
	 */
	public int[] getIntArraySection(String section, String name) { //получение переменной типа массив int из секции
		int[] result = null;
		if (name == null | section == null) {Log.info("ConfigLoader getIntArraySection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = getIntArray(getIndexInSection(section, name));
		} else Log.info("ConfigLoader getIntArraySection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива long из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива long (null, если переменная не найдена)
	 */
	public long[] getLongArraySection(String section, String name) { //получение переменной типа массив long из секции
		long[] result = null;
		if (name == null | section == null) {Log.info("ConfigLoader getLongArraySection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = getLongArray(getIndexInSection(section, name));
		} else Log.info("ConfigLoader getLongArraySection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива double из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива double (null, если переменная не найдена)
	 */
	public double[] getDoubleArraySection(String section, String name) { //получение переменной типа массив double из секции
		double[] result = null;
		if (name == null | section == null) {Log.info("ConfigLoader getDoubleArraySection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = getDoubleArray(getIndexInSection(section, name));
		} else Log.info("ConfigLoader getDoubleArraySection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива boolean из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива boolean (null, если переменная не найдена)
	 */
	public boolean[] getBooleanArraySection(String section, String name) { //получение переменной типа массив boolean из секции
		boolean[] result = null;
		if (name == null | section == null) {Log.info("ConfigLoader getBooleanArraySection: null name or null section"); return result;}
		if (get == true && file != null) {
			result = getBooleanArray(getIndexInSection(section, name));
		} else Log.info("ConfigLoader getBooleanArraySection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * класс со внутренними методами для работы с конфигом
	 */
	public class LoaderMethods { //класс со внутренними методами
		/**
		 * получить весь класс
		 * @return new LoaderMethods()
		 */
		public LoaderMethods getMethods() {return new LoaderMethods();} //получить весь класс
		/**
		 * получить индекс переменной по имени
		 * @param name имя переменной
		 * @return индекс переменной в конфиге
		 */
		public int RecIndexNoSection(String name) { //узнать индекс параметра по имени
			return getIndexNoSection(name);
		}
		/**
		 * получить имя переменной по индексу
		 * @param index индекс переменной
		 * @return имя переменной
		 */
		public String RecName(int index) { //узнать имя параметра по индексу
			return getName(index);
		}
		/**
		 * получить индекс секции по имени
		 * @param name имя секции
		 * @return индекс секции в конфиге
		 */
		public int RecIndexSection(String name) { //узнать индекс секции по названию
			return getIndexSection(name);
		}
		/**
		 * проверить, является ли переменная секцией, по индексу
		 * @param index индекс переменной
		 * @return true если да; false если нет
		 */
		public boolean IsSection(int index) { //проверить, является ли параметр секцией (по индексу)
			return isSection(index);
		}
		/**
		 * получить индекс переменной в секции
		 * @param section имя секции
		 * @param name имя переменной
		 * @return индекс переменной в конфиге
		 */
		public int RecIndexInSection(String section, String name) { //получить индекс параметра в секции
			return getIndexInSection(section, name);
		}
		/**
		 * проверить, является ли строка переменной
		 * @param index индекс строки в конфиге
		 * @return true если да; false если нет
		 */
		public boolean IsParam(int index) { //ялвяется ли строка параметром
			return isParam(index);
		}
		/**
		 * получить количество пробелов в начале строки
		 * @param index индекс строки в конфиге
		 * @return количество пробелов перед символами в начале строки
		 */
		public int RecProbels(int index) { //получить кол-во пробелов в начале строки
			return getProbels(index);
		}
		/**
		 * проверить, есть ли какое-либо значение у массива
		 * @param index индекс строки с массивом в конфиге (строка с его названием)
		 * @return true если да; false если нет
		 */
		public boolean IsSetArray(int index) { //проверка, прописан ли массив
			return isSetArray(index);
		}
		/**
		 * получить значение переменной в виде строки по индексу
		 * @param index индекс строки с переменной в конфиге
		 * @return значение переменной в виде строки (null, если перемменная не найдена)
		 */
		public String RecString(int index) { //получить строку по индексу
			return getString(index);
		}
		/**
		 * получить значение перемменной в виде массива строк по индексу
		 * @param index индекс строки с массивом в конфине
		 * @return значение переменной в виде массива строк (null, если переменная не найдена)
		 */
		public String[] RecStringArray(int index) { //получить массив строк по индексу
			return getStringArray(index);
		}
		/**
		 * получить класс IsArray
		 * @return new IsArray()
		 */
		public IsArray getIsArrayResult() { //получить внутренний класс IsArray
			IsArray is = new IsArray();
			return is;
		}
		/**
		 * проверить массив внутренним методом isArray
		 * @param line строка с массивом (его названием)
		 * @param index индекс строки в конфиге
		 * @return класс IsArray с данными проверки
		 */
		public IsArray IsArray(String line, int index) { //проверить массив внутренним методом IsArray
			return IsArray(line, index);
		}
	}
	/**
	 * инициализированный объект LoaderMethods
	 */
	public LoaderMethods Methods = new LoaderMethods();
}