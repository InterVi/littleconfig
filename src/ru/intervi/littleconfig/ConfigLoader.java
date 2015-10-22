package ru.intervi.littleconfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

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
	
	/**
	 * используемый логгер для вывода сообщений
	 */
	public EasyLogger Log = new EasyLogger();
	
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
		public boolean load;
	}
	
	/**
	 * прочитать конфиг
	 * @param f путь к конфигу
	 */
	public void load(String f) { //загрузка конфина
		if (f == null) {
			Log.info("ConfigLoader load(String f): null path");
			return;
		}
		LoaderResult result = getList(f);
		if (result.load) {
			file = result.list;
			get = true;
		}
	}
	
	/**
	 * прочитать конфиг
	 * @param f объект File конфига для чтения
	 */
	public void load(File f) { //загрузка конфига
		if (f == null) {
			Log.info("ConfigLoader load(File f): null File");
			return;
		}
		load(f.getAbsolutePath());
	}
	
	/**
	 * фейковая загрузка данных
	 * @param value массив строк, в котором представлен конфиг
	 */
	public void fakeLoad(String[] value) { //фейковая загрузка (установка значения из массива)
		file = value;
		get = true;
	}
	
	/**
	 * загрузка конфига, прямой метод (вызывается load(String f))
	 * @param f путь к конфигу
	 * @return результат в виде LoaderResult
	 */
	public LoaderResult getList(String f) { //получаем текстовый файл массивом, очищенный от комментов
		LoaderResult result = new LoaderResult();
		FileStringList list = new FileStringList(f);
		if (list.isLoad()) {
			result.list = list.getStringArray();
			result.load = true;
		}
		return result;
	}
	
	/**
	 * класс с результатами проверки строки и очистки ее от комментария
	 */
	public class ClearResult { //класс с результатом очистки строки от коммента
		/**
		 * была ли произведена очистка от комментария
		 */
		public boolean clear;
		/**
		 * занимает ли комментарий всю строку
		 */
		public boolean fullstr;
		/**
		 * является ли строка бракованной (не переменная)
		 */
		public boolean broken;
		/**
		 * является ли строка пустой (1 и менее символов)
		 */
		public boolean empty;
		/**
		 * оригинальная строка (не измененная)
		 */
		public String origin;
		/**
		 * строка, очищенная от комментария (заполняется даже если комментария не было)
		 */
		public String cleaned;
		/**
		 * комментарий
		 */
		public String com;
		/**
		 * содержание переменной
		 */
		public String content;
		/**
		 * имя переменной
		 */
		public String name;
		/**
		 * индекс первой кавычки (значение по умолчанию: -1)
		 */
		public int firstquote = -1;
		/**
		 * индекс второй кавычки (значение по умолчанию: -1)
		 */
		public int lastquote = -1;
		/**
		 * индекс первой квадратной скобки (значение по умолчанию: -1)
		 */
		public int firstsq = -1;
		/**
		 * индекс второй квадратной скобки (значение по умолчанию: -1)
		 */
		public int lastsq = -1;
		/**
		 * индекс первого дефиса в строке (значение по умолчанию: -1)
		 */
		public int hypindex = -1;
		/**
		 * индекс первого двоеточия в строке (значение по умолчанию: -1)
		 */
		public int colon = -1;
		/**
		 * индекс комментария (символа #) (значение по умолчанию: -1)
		 */
		public int comindex = -1;
		/**
		 * количество пробелов в строке
		 */
		public int probels;
	}
	
	private ClearResult clearStr(String s) { //очистка строки от комментов
		if (s == null) return null;
		ClearResult result = new ClearResult();
		result.origin = s;
		if (s.trim().toCharArray()[0] == '#') { //если коммент во всю строку
			result.comindex = 0;
			result.com = s.substring(1, s.length());
			result.fullstr = true;
			return result;
		}
		if (Utils.trim(s).length() <= 1) { //нормальная строка не может быть в 1 символ
			result.broken = true;
			result.empty = true;
			return result;
		}
		
		char c[] = s.toCharArray();
		int q = -1, q2 = -1, q3 = -1, q4 = -1, d = -1, ci = -1, p = 0, sq = -1, sq2 = -1, hyp = -1;
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') { //пропуск обработки пробелов
				p++;
				continue;
			}
			if (d == -1 | hyp == -1) { //нахождение двоеточия
				if (c[i] == ':') {
					if (i == 0 | i == (p+1)) { //если перед двоеточием нет символов - опция бракованная
						result.broken = true;
					} else d = i;
					continue;
				} else if (c[i] == '-') { //нахождения дефиса (для корректной очистки элементов массивов)
					hyp = i;
					result.broken = true;
					continue;
				} else result.broken = true; //если первый символ не двоеточие и не дефис, то строка бракованная
			} else if (d > -1 | hyp > -1) {
				if (q == -1 & ci == -1) {
					if (c[i] == '"') { //поиск кавычки
						if ((i-d) > 1) {
							boolean ok = true; //только если кавычка - первый символ после имени опции
							for (int n = (d+1); n < i; n++) {
								if (c[n] != ' ') {
									ok = false;
									break;
								}
							}
							if (ok) q = i;
						} else q = i;
						continue;
					}
				} else { //поиск второй кавычки
					if (q2 == -1) {
						if (c[i] == '"') {
							q2 = i;
							continue;
						}
					}
				}
				if (q3 == -1 & q == -1 && ci == -1) {
					if (c[i] == '\'') { //поиск кавычки другого вида
						if ((i-d) > 1) {
							boolean ok = true; //та же проверка
							for (int n = (d+1); n < i; n++) {
								if (c[n] != ' ') {
									ok = false;
									break;
								}
							}
							if (ok) q3 = i;
						} else q3 = i;
						continue;
					}
				} else if (q3 > -1) { //поиск второй кавычки
					if (q4 == -1) {
						if (c[i] == '\'') {
							q4 = i;
							continue;
						}
					}
				}
				//поиск квадратной скобки (для корректной обработки массивов, которые не могут содержать только "]" и те же кавычки)
				if (sq == -1) {
					if (c[i] == '[') {
						if ((i-d) > 1) {
							boolean ok = true; //только если скобка - первый символ после имени опции
							for (int n = (d+1); n < i; n++) {
								if (c[n] != ' ') ok = false;
							}
							if (ok) sq = i;
						} else sq = i;
						continue;
					}
				} else { //поиск второй скобки
					if (sq2 == -1) {
						if (c[i] == ']') {
							sq2 = i;
							continue;
						}
					}
				}
			}
			if (ci == -1 && c[i] == '#') { //поиск коммента
				//исключение символа, находящегося в кавычках
				if (q == -1 & q3 == -1 & sq == -1 || q > -1 & q2 > -1 || q3 > -1 & q4 > -1 || sq > -1 & sq2 > -1) {
					//если есть коммент, но нет опции, при этом символ коммента не первый в строке (не считая пробелы), то строка бракованная
					if (d == -1) {
						result.fullstr = true;
						result.broken = true;
					}
					ci = i;
				}
			}
		}
		
		//заполнение результатов
		result.probels = p;
		result.firstsq = sq;
		result.lastsq = sq2;
		result.hypindex = hyp;
		result.colon = d;
		if (q > -1 & q2 > -1) { //если найдены кавычки первого типа
			result.firstquote = q;
			result.lastquote = q2;
			if ((q2-q) <= 1) result.broken = true; //если между кавычек пусто - строка бракованная
			else
			result.content = s.substring((q+1), q2);
		} else if (q3 > -1 & q4 > -1) { //если найдены кавычки второго типа
			result.firstquote = q3;
			result.lastquote = q4;
			if ((q4-q3) <= 1) result.broken = true;
			else
			result.content = s.substring((q3+1), q4);
		}
		if (d > -1) {
			//если кавычек нет - вырезаем контент между двоеточием и концом строки (или комментом)
			int ch = s.substring(d, s.length()).trim().length();
			if (q == -1 & q3 == -1 && ch > 0) {
				if (ci == -1) result.content = s.substring((d+1), s.length()).trim();
				else result.content = s.substring((d+1), s.lastIndexOf(ci)).trim();
			} else if (ch <= 0) result.broken = true; //если строка кончается двоеточием - она бракованная
			result.name = s.substring(0, d).trim();
		} else {
			result.broken = true; //если нет двоеточия - строка бракованная
			if (hyp > -1) {
				//если кавычек нет - вырезаем контент между тире и концом строки (или комментом)
				int ch = s.substring(hyp, s.length()).trim().length();
				if (q == -1 & q3 == -1 && ch > 0) {
					if (ci == -1) result.content = s.substring((hyp+1), s.length()).trim();
					else result.content = s.substring((hyp+1), s.lastIndexOf(ci)).trim();
				} else if (ch <= 0) result.broken = true; //если строка кончается тире - она бракованная
			}
		}
		if (ci > -1) { // заполнение результатов, если в строке есть коммент
			result.comindex = ci;
			result.cleaned = s.substring(0, ci).trim();
			result.com = s.substring((ci+1), s.length());
			result.clear = true;
		} else result.cleaned = s;

		
		return result;
	}
	
	/**
	 * очистить весь конфиг от комментариев
	 */
	public void clearComments() { //очистить конфиг от комментариев
		if (!get | file == null) return;
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < file.length; i++) {
			if (file[i] != null) {
				ClearResult r = clearStr(file[i]);
				if (r.fullstr) continue;
				if (r.cleaned != null) list.add(r.cleaned);
			}
		}
		file = new String[list.size()];
		for (int i = 0; i < file.length; i++) file[i] = list.get(i);
	}
	
	private String getString(int index) { //получение переменной типа String по индексу
		String result = null;
		if (index < 0) {Log.info("ConfigLoader getString(index): failed, index < 0"); return result;}
		if (index >= file.length) {Log.info("ConfigLoader getString(index): failed, index >= file.length"); return result;}
		if (get & file != null) { //поиск и получение переменной из массива
			if (isSet(index)) {
				ClearResult r = clearStr(file[index]);
				if (!r.broken & !r.fullstr) {
					if (r.content != null) result = r.content;
					else Log.info("ConfigLoader getString(index) " + index + "(index) null content");
				} else Log.info("ConfigLoader getString(index): " + index + "(index) broken line");
			} else Log.info("ConfigLoader getString(index): " + index + "(index) no data");
		} else if (!get) Log.info("ConfigLoader getString(index): " + index + "(index) file not loaded");
		else if (file == null) Log.info("ConfigLoader getString(index): " + index + "(index) array file = null");
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
		if (get & file != null) {
			result = getString(getIndexNoSection(name));
		} else Log.info("ConfigLoader getString(name): " + name + " error (file not load or null array");
		if (result == null) Log.info("ConfigLoader getString(name): " + name + " error, var not found");
		return result;
	}
	
	private byte getByte(int index) { //получение переменной типа byte по индесу
		return Utils.byteFromString(getString(index));
	}
	
	/**
	 * получить значение переменной в виде byte
	 * @param name имя переменной
	 * @return значение переменной в виде byte (0, если переменная не найдена)
	 */
	public byte getByte(String name) { //получение переменной типа byte по названию
		if (name == null) {Log.info("ConfigLoader getInt: null name"); return 0;}
		return getByte(getIndexNoSection(name));
	}
	
	private short getShort(int index) { //получение переменной типа short по индексу
		return Utils.shortFromString(getString(index));
	}
	
	/**
	 * получить значение переменной в виде short
	 * @param name имя переменной
	 * @return значение переменной в виде short (0, если переменная не найдена)
	 */
	public short getShort(String name) { //получение переменной типа short по названию
		if (name == null) {Log.info("ConfigLoader getInt: null name"); return 0;}
		return getShort(getIndexNoSection(name));
	}
	
	private int getInt(int index) { //получение переменной типа int по индексу
		return Utils.intFromString(getString(index));
	}
	
	/**
	 * получить значение переменной в виде int
	 * @param name имя переменной
	 * @return значение переменной в виде int (0, если переменная не найдена)
	 */
	public int getInt(String name) { //получение переменной типа int по названию
		if (name == null) {Log.info("ConfigLoader getInt: null name"); return 0;}
		return getInt(getIndexNoSection(name));
	}
	
	private long getLong(int index) { //получение переменной типа long по индексу
		return Utils.longFromString(getString(index));
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
		return Utils.doubleFromString(getString(index));
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
		return Utils.booleanFromString(getString(index));
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
		if (get & file != null) return file; else {
			Log.info("ConfigLoader getAll: failed, returning null");
			return null;
		}
	}
	
	private String[] getStringArray(int index) { //получение переменной типа массив строк (по индексу)
		if (index < 0) {Log.info("ConfigLoader getStringArray(index): failed, index < 0"); return null;}
		if (index >= file.length) {Log.info("ConfigLoader getStringArray(index): failed, index >= file.length"); return null;}
		String[] result = null;
		IsArray a = isArray(index);
		if (a.array & !a.empty) {
			if (a.skobka) { //парсинг данных из однострочного массива
				String str = a.clear.content.substring(1, (a.clear.content.length()-1));
				int ch = str.indexOf('"');
				int ch2 = str.indexOf('\'');
				if (ch == -1 & ch2 == -1) { //если кавычки не применялись
					result = str.split(",");
					for (int i = 0; i < result.length; i++) {
						if (result[i] != null) result[i] = result[i].trim();
					}
				} else { //если применялись
					//сразу отсекаем бракованный массив
					if (ch > -1 & ch == str.lastIndexOf('"') || ch2 > -1 & ch2 == str.lastIndexOf('\'')) {
						Log.info("ConfigLoader getStringArray(index): " + index + "(index), broken array");
						return null;
					}
					ArrayList<int[]> q = new ArrayList<int[]>();
					char c[] = str.toCharArray();
					int f = -1;
					int add[] = null;
					for (int i = 0; i < c.length; i++) { //поиск и сохранение индексов кавычек
						if (f == -1) { //поиск первой кавычки
							if (c[i] == '"') f = i; else if (c[i] == '\'') f = i;
						} else { //поиск второй кавычки
							if (c[i] == '"') {
								if (add == null) { //сохранение результата
									add = new int[3];
									add[0] = f; add[1] = i;
									if ((i+1) == c.length) add[2] = (i+1); //если это конец строки
									f = -1;
									add = null;
								} else { //поиск запятой
									if (c[i] == ',') {
										for (int n = (add[1]+1); n < i; n++) {
											if (c[n] != ' ') { //если вместо запятой другой символ - массив бракованный
												Log.info("ConfigLoader getStringArray(index): " + index + "(index), broken array");
												return null;
											}
										}
										add[2] = i;
										q.add(add);
										f = -1;
										add = null;
									}
								}
							} else if (c[i] == '\'') { //тот же алгоритм
								if (add == null) {
									add = new int[3];
									add[0] = f; add[1] = i;
									if ((i+1) == c.length) add[2] = (i+1);
									f = -1;
									add = null;
								} else {
									if (c[i] == ',') {
										for (int n = (add[1]+1); n < i; n++) {
											if (c[n] != ' ') {
												Log.info("ConfigLoader getStringArray(index): " + index + "(index), broken array");
												return null;
											}
										}
										add[2] = i;
										q.add(add);
										f = -1;
										add = null;
									}
								}
							}
						}
					}
					ArrayList<String> list = new ArrayList<String>();
					Iterator<int[]> iter = q.iterator();
					while(iter.hasNext()) { //первыми добавляются строки, заключенные в кавычки
						int ind[] = iter.next();
						list.add(str.substring((ind[0]+1), ind[1]));
					}
					iter = q.iterator();
					while(iter.hasNext()) { //удаление добавленных элементов из строки
						int ind[] = iter.next();
						str = Utils.remChars(str, ind[0], ind[3]);
					}
					String split[] = str.split(",");
					if (split != null && split.length > 0) { //добавление оставшихся элементов без кавычек
						for (int i = 0; i < split.length; i++) list.add(split[i].trim());
					}
					result = new String[list.size()]; //сохранение результатов
					for (int i = 0; i < result.length; i++) result[i] = list.get(i);
				}
			} else { //парсинг данных из многострочного массива
				ArrayList<String> list = new ArrayList<String>();
				int ind = index+1;
				ClearResult r;
				do {
					r = clearStr(file[ind]);
					if (r.empty | r.hypindex == -1) continue; //пропускаем не нужное
					String add = r.cleaned; //получаем готовый элемент
					if (add != null) list.add(add);
					if ((ind+1) == file.length) break;
					ind++;
				} while(isArray(file[ind]) || r.empty | r.fullstr);
				result = new String[list.size()];
				for (int i = 0; i < result.length; i++) result[i] = list.get(i);
			}
		} else if (!a.array) Log.info("ConfigLoader getStringArray(index): " + index + "(index), not array");
		
		if (result != null && result.length == 0) return null;
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
		if (get & file != null) {
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
		 * является ли переменная заполненным массивом
		 */
		public boolean array; //массив ли эта переменная
		/**
		 * пустой ли массив
		 */
		public boolean empty;
		/**
		 * содержатся ли значения в квадратных скобках
		 */
		public boolean skobka; //данные в квадратных скобках или через тире
		/**
		 * удалось ли осуществаить проверку
		 */
		public boolean check; //удалась ли проверка
		/**
		 * результат очистки строки
		 */
		public ClearResult clear;
	}
	
	private IsArray isArray(int index) { //проверка переменной на то, является ли она массивом
		IsArray result = new IsArray();
		if (index < 0) {Log.info("ConfigLoader isArray(index): failed, index < 0"); return result;}
		if (index >= file.length) {Log.info("ConfigLoader isArray(index): failed, index >= file.length"); return result;}
		if (get & file != null) {
			ClearResult r = clearStr(file[index]);
			result.clear = r;
			if (r.cleaned != null) {
				if (r.content != null) { //если есть содержимое в опции - проверяем
					char ch[] = Utils.trim(r.content).toCharArray();
					if (ch != null) { //мало ли...
						if (ch.length > 0 && ch[0] == '[' & ch[(ch.length-1)] == ']') { //если строка закрыта в квадратные скобки
							if (ch.length == 2) { //между скобок пусто
								result.skobka = true;
								result.empty = true;
								result.check = true;
							} else { //не пусто - значит массив не пустой
								result.skobka = true;
								result.array = true;
								result.check = true;
							}
						} else { //содержимое есть, но не в скобках - значит не массив (или 0 длинна)
							result.empty = true;
							result.check = true;
						}
					}
				} else { //нет контента - проверяем следующую строку на тире
					if ((index+1) < file.length) {
						if (isArray(file[(index+1)])) {
							result.array = true;
							result.check = true;
						} else {
							result.empty = true;
							result.check = true;
						}
					}
				}
			}
		} else if (!get) Log.info("ConfigLoader isArray(index): " + index + "(index) file not loaded");
		else if (file == null) Log.info("ConfigLoader isArray(index): " + index + "(index) array file = null");
		return result;
	}
	
	private boolean isArray(String s) { //проверка, является ли строка компонентом массива (т.е. начинается с тире)
		if (s == null) return false;
		if (s.trim().length() <= 1) return false;
		if (Utils.trim(s).substring(0, 1).equals("-")) return true; else return false;
	}
	
	private byte[] getByteArray(int index) { //получение массива типа byte по индексу
		return Utils.byteFromStringArray(getStringArray(index));
	}
	
	/**
	 * получить значение переменной в виде массива byte
	 * @param name имя переменной
	 * @return значение переменной в виде массива byte (null, если переменная не найдена)
	 */
	public byte[] getByteArray(String name) { //получение массива типа byte по названию
		if (name == null) {Log.info("ConfigLoader getByteArray: null name"); return null;}
		return getByteArray(getIndexNoSection(name));
	}
	
	private short[] getShortArray(int index) { //получение массива типа short по индексу
		return Utils.shortFromStringArray(getStringArray(index));
	}
	
	/**
	 * получить значение переменной в виде массива short
	 * @param name имя переменной
	 * @return значение переменной в виде массива short (null, если переменная не найдена)
	 */
	public short[] getShortArray(String name) { //получение массива типа short по названию
		if (name == null) {Log.info("ConfigLoader getShortArray: null name"); return null;}
		return getShortArray(getIndexNoSection(name));
	}
	
	private int[] getIntArray(int index) { //получение массива типа int по индексу
		return Utils.intFromStringArray(getStringArray(index));
	}
	
	/**
	 * получить значение переменной в виде массива int
	 * @param name имя переменной
	 * @return значение переменной в виде массива int (null, если переменная не найдена)
	 */
	public int[] getIntArray(String name) { //получение массива типа int по названию
		if (name == null) {Log.info("ConfigLoader getIntArray: null name"); return null;}
		return getIntArray(getIndexNoSection(name));
	}
	
	private long[] getLongArray(int index) { //получение массива типа long по индексу
		return Utils.longFromStringArray(getStringArray(index));
	}
	
	/**
	 * получить значение переменной в виде массива long
	 * @param name имя переменной
	 * @return значение переменной в виде массива long (null, если переменная не найдена)
	 */
	public long[] getLongArray(String name) { //получение массива типа long по названию
		if (name == null) {Log.info("ConfigLoader getLongArray: null name"); return null;}
		return getLongArray(getIndexNoSection(name));
	}
	
	private double[] getDoubleArray(int index) { //получение массива типа double по индексу
		return Utils.doubleFromStringArray(getStringArray(index));
	}
	
	/**
	 * получить значение переменной в виде массива double
	 * @param name имя переменной
	 * @return значение переменной в виде массива double (null, если не найдено)
	 */
	public double[] getDoubleArray(String name) { //получение массива типа double по названию
		if (name == null) {Log.info("ConfigLoader getDoubleArray: null name"); return null;}
		return getDoubleArray(getIndexNoSection(name));
	}
	
	private boolean[] getBooleanArray(int index) { //получение массива типа boolean по индексу
		return Utils.booleanFromStringArray(getStringArray(index));
	}
	
	/**
	 * получить значение переменной в виде массива boolean
	 * @param name имя переменной
	 * @return значение переменной в виде массива boolean (null, если переменная не найдена)
	 */
	public boolean[] getBooleanArray(String name) { //получение массива типа boolean по названию
		if (name == null) {Log.info("ConfigLoader getBooleanArray: null name"); return null;}
		return getBooleanArray(getIndexNoSection(name));
	}
	
	private boolean isSet(int index) { //проверка, прописана ли переменная (по индексу)
		boolean result = false;
		if (index < 0) {Log.info("ConfigLoader isSet: failed, index < 0"); return result;}
		if (index >= file.length) {Log.info("ConfigLoader isSet: failed, index > file.length"); return result;}
		if (get & file != null) {
			result = isParam(index); //является ли строка параметром
			if (index+1 < file.length) {if (!result & isArray(file[index+1])) result = true;} //является ли она массивом
			if (!result & index+1 < file.length) {
				ClearResult r = clearStr(file[index+1]);
				if (r.name != null & r.broken & r.content == null) result = true; //является ли она секцией (упрощенный вариант проверки)
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
		if (get & file != null) {
			result = isSet(getIndexNoSection(name));
		} else Log.info("ConfigLoader isSet(name): failed check " + name + ", config not loaded");
		return result;
	}
	
	private boolean isSetArray(int index) { //проверка, прописан ли массив (по индексу)
		boolean result = false;
		if (index < 0) {Log.info("ConfigLoader isSetArray: failed, index < 0"); return result;}
		if (index >= file.length) {Log.info("ConfigLoader isSetArray: failed, index > file.length"); return result;}
		if (get & file != null) {
			result = isArray(index).array;
		} else Log.info("ConfigLoader isSet: failed check " + index + ", config not loaded");
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
		if (index >= file.length) {Log.info("ConfigLoader getProbels: failed, index > file.length"); return result;}
		if (get & file != null & file[index] != null) {
			ClearResult r = clearStr(file[index]);
			if (r.cleaned != null) {
				if (r.colon > -1) result = r.colon-1;
				else if (r.hypindex > -1) result = r.hypindex-1;
			}
		} else Log.info("ConfigLoader getProbels: failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	private boolean isParam(int index) { //является ли строка параметром
		boolean result = false;
		if (index < 0) {Log.info("ConfigLoader isParam: failed, index < 0"); return result;}
		if (index >= file.length) {Log.info("ConfigLoader isParam: failed, index > file.length"); return result;}
		if (get & file != null & file[index] != null) {
			result = clearStr(file[index]).broken;
		} else Log.info("ConfigLoader isParam: failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	private String getName(int index) { //получение названия переменной по индексу
		String result = null;
		if (index < 0) {Log.info("ConfigLoader getName: failed, index < 0"); return result;}
		if (index >= file.length) {Log.info("ConfigLoader getName: failed, index > file.length"); return result;}
		if (get & file != null & file[index] != null) {
			result = clearStr(file[index]).name;
		} else Log.info("ConfigLoader getName: failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	private int getIndexSection (String name) { //получить индекс секции по названию
		int result = -1;
		if (name == null) {Log.info("ConfigLoader getIndexSection: null name"); return result;}
		if (get & file != null) {
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null) {
					if (Utils.remChars(file[i], file[i].indexOf(":"), file[i].length()).trim().equals(name)) {
						if (isSection(i)) {
							result = i;
							break;
						}
					}
				}
			}
		} else Log.info("ConfigLoader getIndexSection: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	private int getIndexNoSection (String name) { //получить индекс переменной по названию (не секции)
		int result = -1;
		if (name == null) {Log.info("ConfigLoader getIndexNoSection: null name"); return result;}
		if (get & file != null) {
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null) {
					if (Utils.remChars(file[i], file[i].indexOf(":"), file[i].length()).trim().equals(name)) {
						if (isSet(i) && !isSection(i)) {
							result = i;
							break;
						}
					}
				}
			}
		} else Log.info("ConfigLoader getIndexNoSection: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	private boolean isSection(int index) { //проверка, является ли переменная секцией (по индексу)
		boolean result = false;
		if (index < 0) {Log.info("ConfigLoader isSection: failed, index < 0"); return result;}
		if (get & file != null) {
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