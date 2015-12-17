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
	 * проверить, загружен ли конфиг
	 * @return true если да; false если нет
	 */
	public boolean isLoad() { //загружен ли конфиг
		return get;
	}
	
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
			Log.warn("ConfigLoader load(String f): null path");
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
			Log.warn("ConfigLoader load(File f): null File");
			return;
		}
		load(f.getAbsolutePath());
	}
	
	/**
	 * фейковая загрузка данных
	 * @param value массив строк, в котором представлен конфиг
	 */
	public void fakeLoad(String[] value) { //фейковая загрузка (установка значения из массива)
		if (value == null) return;
		file = new String[value.length];
		for (int i = 0; i < value.length; i++) { //чистка от null'ов
			if (value[i] != null) file[i] = value[i]; else file[i] = "";
		}
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
		list.Log.offLog();
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
		public boolean broken = true;
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
		 * комментарий (null по умолчанию)
		 */
		public String com;
		/**
		 * содержание переменной (null по умолчанию)
		 */
		public String content;
		/**
		 * имя переменной (null по умолчанию)
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
		ClearResult result = new ClearResult();
		if (s == null) return result;
		result.origin = s;
		if (s.trim().charAt(0) == '#') { //если коммент во всю строку
			result.comindex = 0;
			result.com = s.substring(1, s.length());
			result.fullstr = true;
			return result;
		}
		if (Utils.trim(s).length() <= 1) { //нормальная строка не может быть в 1 символ
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
			if (d == -1 & hyp == -1) { //нахождение двоеточия
				if (c[i] == ':') {
					if (i != 0 | i != (p+1)) d = i; //если перед двоеточием нет символов - опция бракованная
					continue;
				} else if (c[i] == '-') { //нахождения дефиса (для корректной очистки элементов массивов)
					hyp = i;
					continue;
				}
			} else {
				if (q == -1) {
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
				if (q3 == -1 & q == -1) {
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
					if (d == -1) result.fullstr = true;
					ci = i;
					break;
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
			if ((q2-q) > 1) { //если между кавычек пусто - строка бракованная
				result.content = s.substring((q+1), q2);
				result.broken = false;
			}
		} else if (q3 > -1 & q4 > -1) { //если найдены кавычки второго типа
			result.firstquote = q3;
			result.lastquote = q4;
			if ((q4-q3) > 1) {
				result.content = s.substring((q3+1), q4);
				result.broken = false;
			}
		}
		if (d > -1) {
			if (q == -1 & q2 == -1 && q3 == -1 & q4 == -1) {
				//если кавычек нет - вырезаем контент между двоеточием и концом строки (или комментом)
				char ch[] = s.trim().toCharArray();
				boolean ok = true; //проверка, есть ли контент между двоеточием и комментом (кроме пробелов)
				if (ci > -1 & d < ci && Utils.trim(s.substring((d+1), ci)).length() <= 0) ok = false;
				if (ch[(ch.length-1)] != ':' && ci == -1 | ok) { //если строка кончается двоеточием - она бракованная
					if (ci == -1) result.content = s.substring((d+1), s.length()).trim();
					else result.content = s.substring((d+1), ci).trim();
					result.broken = false;
				}
			}
			result.name = s.split(":")[0].trim();
		} else {
			//если нет двоеточия - строка бракованная
			if (hyp > -1) {
				//если кавычек нет - вырезаем контент между тире и концом строки (или комментом)
				int ch = s.substring(hyp, s.length()).trim().length();
				if (q == -1 & q3 == -1 && ch > 0) {
					if (ci == -1) result.content = s.substring((hyp+1), s.length()).trim();
					else result.content = s.substring((hyp+1), ci).trim();
				}
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
	
	private String getString(int index) { //получение переменной типа String по индексу
		String result = null;
		if (index < 0) {Log.warn("ConfigLoader getString(index): failed, index < 0"); return result;}
		if (index >= file.length) {Log.warn("ConfigLoader getString(index): failed, index >= file.length"); return result;}
		if (get & file != null) { //поиск и получение переменной из массива
			if (isSet(index)) {
				ClearResult r = clearStr(file[index]);
				if (!r.broken & !r.fullstr) {
					if (r.content != null) result = r.content;
					else Log.warn("ConfigLoader getString(index) " + index + "(index) null content");
				} else Log.warn("ConfigLoader getString(index): " + index + "(index) broken line");
			} else Log.warn("ConfigLoader getString(index): " + index + "(index) no data");
		} else if (!get) Log.warn("ConfigLoader getString(index): " + index + "(index) file not loaded");
		else if (file == null) Log.warn("ConfigLoader getString(index): " + index + "(index) array file = null");
		return result;
	}
	
	/**
	 * получить значение переменной в винде строки
	 * @param name имя переменной
	 * @return значение в виде строки (null, если переменная не найдена)
	 */
	public String getString(String name) { //получение переменной типа String по названию
		String result = null;
		if (name == null) {Log.warn("ConfigLoader getString: null name"); return result;}
		if (get & file != null) {
			result = getString(getIndexNoSection(name));
		} else Log.warn("ConfigLoader getString(name): " + name + " error (file not load or null array");
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
		if (name == null) {Log.warn("ConfigLoader getInt: null name"); return 0;}
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
		if (name == null) {Log.warn("ConfigLoader getInt: null name"); return 0;}
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
		if (name == null) {Log.warn("ConfigLoader getInt: null name"); return 0;}
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
		if (name == null) {Log.warn("ConfigLoader getLong: null name"); return -1;}
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
		if (name == null) {Log.warn("ConfigLoader getDouble: null name"); return -1;}
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
		if (name == null) {Log.warn("ConfigLoader getBoolean: null name"); return false;}
		return getBoolean(getIndexNoSection(name));
	}
	
	/**
	 * получение всего конфига
	 * @return конфиг в виде массива строк
	 */
	public final String[] getAll() { //получение всего конфига массивом строк
		if (get & file != null) return file; else {
			Log.warn("ConfigLoader getAll: failed, returning null");
			return null;
		}
	}
	
	private String[] getStringArray(int index) { //получение переменной типа массив строк (по индексу)
		if (index < 0) {Log.warn("ConfigLoader getStringArray(index): failed, index < 0"); return null;}
		if (index >= file.length) {Log.warn("ConfigLoader getStringArray(index): failed, index >= file.length"); return null;}
		String[] result = null;
		IsArray a = isArray(index);
		if (a.array & !a.empty) {
			if (a.skobka) { //парсинг данных из однострочного массива
				String str = a.clear.content.substring(1, (a.clear.content.length()-1)); //отрезаем скобки
				if (Utils.numChars(str, '"') < 2 & Utils.numChars(str, '\'') < 2) { //если кавычки не применялись
					result = str.split(",");
					for (int i = 0; i < result.length; i++) {
						if (result[i] != null) result[i] = result[i].trim();
					}
				} else { //если применялись
					/*
					 * 0 - первая кавычка
					 * 1 - вторая кавычка
					 * 2 - запятая
					 */
					ArrayList<int[]> q = new ArrayList<int[]>(); //список кавычек и запятых
					char c[] = str.toCharArray();
					int f = -1;
					int add[] = null;
					for (int i = 0; i < c.length; i++) { //поиск и сохранение индексов кавычек
						if (f == -1) { //поиск первой кавычки
							if (c[i] == '"') f = i; else if (c[i] == '\'') f = i;
						} else { //поиск второй кавычки
							if (c[i] == '"' & c[f] == '"') {
								if (add == null) { //сохранение результата
									add = new int[3];
									add[0] = f; add[1] = i;
									if ((i+1) == c.length) { //если это конец строки
										add[2] = (i+1);
										q.add(add);
									}
									f = -1;
									add = null;
								} else { //поиск запятой
									if (c[i] == ',' & add != null) {
										for (int n = (add[1]+1); n < i; n++) {
											if (c[n] != ' ') { //если вместо запятой другой символ - массив бракованный
												Log.warn("ConfigLoader getStringArray(index): " + index + "(index), broken array");
												return null;
											}
										}
										add[2] = i;
										q.add(add);
										f = -1;
										add = null;
									}
								}
							} else if (c[i] == '\'' & c[f] == '\'') { //тот же алгоритм
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
												Log.warn("ConfigLoader getStringArray(index): " + index + "(index), broken array");
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
						str = Utils.remChars(str, ind[0], ind[2]);
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
				for (int i = (index+1); i < file.length; i++) {
					ClearResult r = clearStr(file[i]);
					if (r.colon > -1) break; //выход из цикла при попадании на опцию или секцию
					else if (r.hypindex == -1) continue; //пропускаем не нужное
					String add = r.content; //получаем готовый элемент
					if (add != null) {
						char q1 = add.charAt(0); //чистка от кавычек
						char q2 = add.charAt((add.length()-1));
						if (q1 == '"' & q2 == '"' || q1 == '\'' & q2 == '\'') add = add.substring(1, (add.length()-1));
						list.add(add);
					}
				}
				result = new String[list.size()];
				for (int i = 0; i < result.length; i++) result[i] = list.get(i);
			}
		} else if (!a.array) Log.warn("ConfigLoader getStringArray(index): " + index + "(index), not array");
		
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
		if (name == null) {Log.warn("ConfigLoader getStringArray(name): null name"); return result;}
		if (get & file != null) {
			result = getStringArray(getIndexNoSection(name));
		} else Log.warn("ConfigLoader getStringArray(name): get " + name + " failed (config not loaded or file = null)");
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
		if (index < 0) {Log.warn("ConfigLoader isArray(index): failed, index < 0"); return result;}
		if (index >= file.length) {Log.warn("ConfigLoader isArray(index): failed, index >= file.length"); return result;}
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
					for (int i = (index+1); i < file.length; i++) {
						ClearResult c = clearStr(file[i]);
						if (c.hypindex > -1) { //ищем строку с тире
							result.array = true;
							result.check = true;
							break;
						} else if (c.colon > -1) { //если первой попадается строка с опцией - значит никакого массива нет
							result.check = true;
							break;
						}
					}
				}
			}
		} else if (!get) Log.warn("ConfigLoader isArray(index): " + index + "(index) file not loaded");
		else if (file == null) Log.warn("ConfigLoader isArray(index): " + index + "(index) array file = null");
		return result;
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
		if (name == null) {Log.warn("ConfigLoader getByteArray: null name"); return null;}
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
		if (name == null) {Log.warn("ConfigLoader getShortArray: null name"); return null;}
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
		if (name == null) {Log.warn("ConfigLoader getIntArray: null name"); return null;}
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
		if (name == null) {Log.warn("ConfigLoader getLongArray: null name"); return null;}
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
		if (name == null) {Log.warn("ConfigLoader getDoubleArray: null name"); return null;}
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
		if (name == null) {Log.warn("ConfigLoader getBooleanArray: null name"); return null;}
		return getBooleanArray(getIndexNoSection(name));
	}
	
	/**
	 * получить названия переменных из всего конфига (не включает названия секций)
	 * @return названия переменных в виде массива строк (null, если ничего не найдено)
	 */
	public String[] getOptionNames() { //получение названия переменных из всего конфига
		String result[] = null;
		if (get & file != null) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < file.length; i++) { //парсинг имени всех опций в лист
				if (isSet(i) & !isSection(i)) list.add(clearStr(file[i]).name);
				else if (isSection(i)) {
					i += getSectionRealLength(i)-1; //секции пропускаем
					continue;
				}
			}
			if (!list.isEmpty()) { //заполнение результатов
				result = new String[list.size()];
				for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
			}
		} else Log.warn("ConfigLoader getConfigVars: failed, config not loaded");
		return result;
	}
	
	private boolean isSet(int index) { //проверка, прописана ли переменная (по индексу)
		boolean result = false;
		if (index < 0) {Log.warn("ConfigLoader isSet: failed, index < 0"); return result;}
		if (index >= file.length) {Log.warn("ConfigLoader isSet: failed, index > file.length"); return result;}
		if (get & file != null) {
			result = !clearStr(file[index]).broken; //является ли строка параметром
			if (!result) result = isArray(index).array; //является ли она массивом
			if (!result) result = isSection(index); //является ли она секцией
		} else Log.warn("ConfigLoader isSet(index): failed check " + index + ", config not loaded");
		return result;
	}
	
	/**
	 * проверить, есть ли какое-либо значение у переменной (подходит для проверки массивов и секций)
	 * @param name имя переменной
	 * @return true если есть; false если нету
	 */
	public boolean isSet(String name) { //проверка, прописана ли переменная (по названию)
		boolean result = false;
		if (name == null) {Log.warn("ConfigLoader isSet: null name"); return false;}
		if (get & file != null) {
			result = isSet(getIndexNoSection(name));
		} else Log.warn("ConfigLoader isSet(name): failed check " + name + ", config not loaded");
		return result;
	}
	
	private boolean isSetArray(int index) { //проверка, прописан ли массив (по индексу)
		boolean result = false;
		if (index < 0) {Log.warn("ConfigLoader isSetArray: failed, index < 0"); return result;}
		if (index >= file.length) {Log.warn("ConfigLoader isSetArray: failed, index > file.length"); return result;}
		if (get & file != null) {
			result = isArray(index).array;
		} else Log.warn("ConfigLoader isSet: failed check " + index + ", config not loaded");
		return result;
	}
	
	/**
	 * проверить, есть ли какое-либо значение у массива
	 * @param name имя переменной с массивом
	 * @return true если да; false если нет
	 */
	public boolean isSetArray(String name) { //проверка, прописан ли массив (по названию)
		if (name == null) {Log.warn("ConfigLoader isSetArray: null name"); return false;}
		return isSetArray(getIndexNoSection(name));
	}
	
	private int getProbels(String s) { //узнаем кол-во пробелов в начале строки
		if (s == null) {Log.warn("ConfigLoader getProbels: failed, null String"); return -1;}
		char c[] = s.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] != ' ') return i-1;
		}
		return -1;
	}
	
	private int getIndexSection(String name) { //получить индекс секции по названию
		int result = -1;
		if (name == null) {Log.warn("ConfigLoader getIndexSection: null name"); return result;}
		if (get & file != null) {
			for (int i = 0; i < file.length; i++) {
				ClearResult r = clearStr(file[i]);
				if (r.broken & r.name != null) {
					if (isSection(i) & r.name.equals(name)) {
						result = i;
						break;
					}
				}
			}
		} else Log.warn("ConfigLoader getIndexSection: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	private int getIndexNoSection(String name) { //получить индекс переменной по названию (не секции)
		int result = -1;
		if (name == null) {Log.warn("ConfigLoader getIndexNoSection: null name"); return result;}
		if (get & file != null) {
			for (int i = 0; i < file.length; i++) {
				if (isSection(i)) { //пропуск секций
					i += getSectionRealLength(i)-1;
					continue;
				}
				ClearResult r = clearStr(file[i]);
				if (!r.broken || isArray(i).array & r.name != null) {
					if (r.name.equals(name)) {
						result = i;
						break;
					}
				}
			}
		} else Log.warn("ConfigLoader getIndexNoSection: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	private boolean isSection(int index) { //проверка, является ли переменная секцией (по индексу)
		boolean result = false;
		if (index < 0) {Log.warn("ConfigLoader isSection: failed, index < 0"); return result;}
		if (index >= file.length) {Log.warn("ConfigLoader isSection: failed, index > file.length"); return result;}
		if (get & file != null) {
			ClearResult r = clearStr(file[index]);
			if (r.cleaned != null && r.broken & (r.colon+1) == r.cleaned.length() && r.name != null) { //строка не должна быть опцией
				int p = getProbels(file[index])+1;
				for (int i = (index+1); i < file.length; i++) {
					if (p > getProbels(file[i])) break; //выход из цикла
					ClearResult c = clearStr(file[i]);
					if (c.empty | c.fullstr) continue; //пропуск не нужного
					if (c.colon > -1 & c.name != null) { //если найдена опция - значит это секция
						result = true;
						break;
					}
				}
			}
		} else Log.warn("ConfigLoader isSection(index): failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	/**
	 * проверить, является ли переменная секцией
	 * @param name имя секции
	 * @return true если да; false если нет
	 */
	public boolean isSection(String name) { //проверка, является ли переменная секцией (по названию)
		boolean result = false;
		if (name == null) {Log.warn("ConfigLoader isSection: null name"); return false;}
		if (get == true && file != null) {
			if (getIndexSection(name) > -1) result = true;
		} else Log.warn("ConfigLoader isSection(name): failed check " + name + ", config not loaded or file[i] == null");
		return result;
	}
	
	/**
	 * получить названия переменных в данной секции (не проверяются методом isSet, не включает названия секций)
	 * @param name имя секции
	 * @return названия переменных в виде массива строк (null, если ничего не найдено)
	 */
	public String[] getSectionVars(String name) { //получение названий переменных секции
		String[] result = null;
		if (name == null) {Log.warn("ConfigLoader getSectionVars: null name"); return result;}
		if (get & file != null) {
			int index = getIndexSection(name);
			if (index == -1) return result; //страховка
			if (isSection(index)) {
				int p = getProbels(file[index])+1;
				ArrayList<String> list = new ArrayList<String>();
				for (int i = (index+1); i < file.length; i++) {
					ClearResult r = clearStr(file[i]);
					if (r.empty | r.fullstr | r.name == null) continue; //пропуск не нужного
					if (r.colon > -1 & getProbels(file[i]) < p) break; //выход из цикла, конец секции
					if (isSection(index)) {
						i += getSectionRealLength(i)-1; //пропуск секций
						continue;
					}
					if (r.colon > -1 & r.name != null) list.add(r.name);
				}
			}
		} else Log.warn("ConfigLoader isSection: failed check " + name + ", config not loaded or file[i] == null");
		return result;
	}
	
	private int getSectionRealLength(int index) { //получение реальной длинны секции в конфиге (кол-во строк) (по индексу)
		int result = -1;
		if (index < 0) {Log.warn("ConfigLoader getSectionRealLength(index): failed, index < 0"); return result;}
		if (index >= file.length) {Log.warn("ConfigLoader getSectionRealLength(index): failed, index > file.length"); return result;}
		if (get & file != null) {
			if (index == -1) return result; //страховка
			if (isSection(index)) {
				int p = getProbels(file[index])+1;
				for (int i = (index+1); i < file.length; i++) {
					ClearResult r = clearStr(file[i]);
					if (r.empty | r.fullstr) continue; //пропуск не нужного
					if (p > getProbels(file[i])) { //выход из цикла и сохранение результата
						result = (i-1)-(index-1);
						break;
					}
					if ((i+1) == file.length) result = i-(index-1); //если цикл дошел до конца массива
				}
			}
		} else Log.warn("ConfigLoader getSectionRealLength(index): failed get " + index + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить длинну секции (количество строк в конфиге, строка с названием секции входит в число)
	 * @param name имя секции
	 * @return количество строк в секции в виде int (-1, если секция не найдена либо пуста)
	 */
	public int getSectionRealLength(String name) { //получение реальной длинны секции в конфиге (кол-во строк) (по названию)
		if (name == null) {Log.warn("ConfigLoader getSectionRealLength: null name"); return -1;}
		return getSectionRealLength(getIndexSection(name));
	}
	
	/**
	 * получить названия секций из всего конфига
	 * @return названия секций в виде массива строк (null, если ничего не найдено)
	 */
	public String[] getSectionNames() { //получение названий секций во всем конфиге
		String[] result = null;
		if (get & file != null) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < file.length; i++) {
				ClearResult r = clearStr(file[i]);
				if (r.empty | r.fullstr) continue; //пропуск лишнего
				if (r.broken && isSection(i)) {
					list.add(r.name);
					i += getSectionRealLength(i)-1; //пропуск содержимого секций
				}
			}
			if (!list.isEmpty()) {
				result = new String[list.size()];
				for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
			}
		} else Log.warn("ConfigLoader getSectionNames: failed get sections names, config not loaded or file == null");
		return result;
	}
	
	/**
	 * получение названия секций из секции
	 * @param name имя секции
	 * @return названия секций в виде массива строк (null, если ничего не найдено)
	 */
	public String[] getSectionNames(String name) { //получение названий секций в секции
		String[] result = null;
		if (name == null) {Log.warn("ConfigLoader getSectionNames: null name"); return result;}
		if (get & file != null) {
			int index = getIndexSection(name);
			int p = getProbels(file[index])+1;
			ArrayList<String> list = new ArrayList<String>();
			for (int i = (index+1); i < file.length; i++) {
				ClearResult r = clearStr(file[i]);
				if (r.empty | r.fullstr) continue; //пропуск не нужного
				if (p > getProbels(file[i])) break; //выход из цикла
				if (isSection(i)) {
					list.add(r.name);
					i += getSectionRealLength(i)-1; //пропуск содержимого секций
				}
			}
			if (!list.isEmpty()) {
				result = new String[list.size()];
				for (int i = 0; i < result.length; i++) result[i] = list.get(i);
			}
		} else Log.warn("ConfigLoader getSectionNames: failed get " + name + " config not loaded or file == null");
		return result;
	}
	
	private int getIndexInSection(String section, String name) { //получение индекса переменной в секции
		int result = -1;
		if (name == null | section == null) {Log.warn("ConfigLoader getIndexInSection: null name or null section"); return result;}
		if (get & file != null) {
			int index = getIndexSection(section);
			if (index > -1 && isSection(index)) {
				int p = getProbels(file[index])+1;
				for (int i = (index+1); i < file.length; i++) {
					ClearResult r = clearStr(file[i]);
					if (r.empty | r.fullstr | r.colon == -1) continue;
					if (p > getProbels(file[i])) break;
					if (r.name != null && r.name.equals(name)) {
						result = i;
						break;
					}
				}
			}
		} else Log.warn("ConfigLoader getIndexInSection: failed get " + name + " in " + section + " config not loaded or file == null");
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
		if (name == null | section == null) {Log.warn("ConfigLoader isSetInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = isSet(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader isSetInSection: failed check " + name + " in " + section + " config not loaded or file == null");
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
		if (name == null | section == null) {Log.warn("ConfigLoader isSetArrayInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = isSetArray(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader isSetArrayInSection: failed check " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде строки из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде строки (null, если переменная не найдена)
	 */
	public String getStringInSection(String section, String name) { //получение переменной типа String из секции
		String result = null;
		if (name == null | section == null) {Log.warn("ConfigLoader getStringInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getString(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getStringInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде byte из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде byte (-1, если переменная не найдена)
	 */
	public byte getByteInSection(String section, String name) { //получение переменной типа byte из секции
		byte result = -1;
		if (name == null | section == null) {Log.warn("ConfigLoader getByteInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getByte(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getByteInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде short из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде short (-1, если переменная не найдена)
	 */
	public short getShortInSection(String section, String name) { //получение переменной типа short из секции
		short result = -1;
		if (name == null | section == null) {Log.warn("ConfigLoader getShortInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getShort(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getShortInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде int из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде int (-1, если переменная не найдена)
	 */
	public int getIntInSection(String section, String name) { //получение переменной типа int из секции
		int result = -1;
		if (name == null | section == null) {Log.warn("ConfigLoader getIntInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getInt(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getIntInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде long из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде long (-1, если переменная не найдена)
	 */
	public long getLongInSection(String section, String name) { //получение переменной типа long из секции
		long result = -1;
		if (name == null | section == null) {Log.warn("ConfigLoader getLongInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getLong(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getLongInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде double из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде double (-1, если переменная не найдена)
	 */
	public double getDoubleInSection(String section, String name) { //получение переменной типа double из секции
		double result = -1;
		if (name == null | section == null) {Log.warn("ConfigLoader getDoubleInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getDouble(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getDoubleInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде boolean из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде boolean (false, если переменная не найдена)
	 */
	public boolean getBooleanInSection(String section, String name) { //получение переменной типа boolean из секции
		boolean result = false;
		if (name == null | section == null) {Log.warn("ConfigLoader getBooleanInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getBoolean(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getBooleanInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива строк из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива строк (null, если переменная не найдена)
	 */
	public String[] getStringArrayInSection(String section, String name) { //получение переменной типа массив String из секции
		String[] result = null;
		if (name == null | section == null) {Log.warn("ConfigLoader getStringArrayInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getStringArray(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getStringArrayInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива byte из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива byte (null, если переменная не найдена)
	 */
	public byte[] getByteArrayInSection(String section, String name) { //получение переменной типа массив byte из секции
		byte[] result = null;
		if (name == null | section == null) {Log.warn("ConfigLoader getByteArrayInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getByteArray(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getByteArrayInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива short из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива short (null, если переменная не найдена)
	 */
	public short[] getShortArrayInSection(String section, String name) { //получение переменной типа массив short из секции
		short[] result = null;
		if (name == null | section == null) {Log.warn("ConfigLoader getShortArrayInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getShortArray(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getShortArrayInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива int из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива int (null, если переменная не найдена)
	 */
	public int[] getIntArrayInSection(String section, String name) { //получение переменной типа массив int из секции
		int[] result = null;
		if (name == null | section == null) {Log.warn("ConfigLoader getIntArrayInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getIntArray(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getIntArrayInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива long из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива long (null, если переменная не найдена)
	 */
	public long[] getLongArrayInSection(String section, String name) { //получение переменной типа массив long из секции
		long[] result = null;
		if (name == null | section == null) {Log.warn("ConfigLoader getLongArrayInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getLongArray(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getLongArrayInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива double из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива double (null, если переменная не найдена)
	 */
	public double[] getDoubleArrayInSection(String section, String name) { //получение переменной типа массив double из секции
		double[] result = null;
		if (name == null | section == null) {Log.warn("ConfigLoader getDoubleArrayInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getDoubleArray(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getDoubleArrayInSection: failed get " + name + " in " + section + " config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива boolean из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива boolean (null, если переменная не найдена)
	 */
	public boolean[] getBooleanArrayInSection(String section, String name) { //получение переменной типа массив boolean из секции
		boolean[] result = null;
		if (name == null | section == null) {Log.warn("ConfigLoader getBooleanArrayInSection: null name or null section"); return result;}
		if (get & file != null) {
			result = getBooleanArray(getIndexInSection(section, name));
		} else Log.warn("ConfigLoader getBooleanArrayInSection: failed get " + name + " in " + section + " config not loaded or file == null");
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
		public int recIndexNoSection(String name) { //узнать индекс параметра по имени
			return getIndexNoSection(name);
		}
		/**
		 * очистить и проверить строку внутренним методом clearStr
		 * @param s строка
		 * @return результат проверки и очистки
		 */
		public ClearResult clearString(String s) { //проверить и очистить строку от комментов
			return clearStr(s);
		}
		/**
		 * получить индекс секции по имени
		 * @param name имя секции
		 * @return индекс секции в конфиге
		 */
		public int recIndexSection(String name) { //узнать индекс секции по названию
			return getIndexSection(name);
		}
		/**
		 * проверить, является ли переменная секцией, по индексу
		 * @param index индекс переменной
		 * @return true если да; false если нет
		 */
		public boolean checkSection(int index) { //проверить, является ли параметр секцией (по индексу)
			return isSection(index);
		}
		/**
		 * получить индекс переменной в секции
		 * @param section имя секции
		 * @param name имя переменной
		 * @return индекс переменной в конфиге
		 */
		public int recIndexInSection(String section, String name) { //получить индекс параметра в секции
			return getIndexInSection(section, name);
		}
		/**
		 * получить количество пробелов в начале строки
		 * @param s строка
		 * @return количество пробелов перед символами в начале строки
		 */
		public int recProbels(String s) { //получить кол-во пробелов в начале строки
			return getProbels(s);
		}
		/**
		 * проверить, есть ли какое-либо значение у массива
		 * @param index индекс строки с массивом в конфиге (строка с его названием)
		 * @return true если да; false если нет
		 */
		public boolean checkSetArray(int index) { //проверка, прописан ли массив
			return isSetArray(index);
		}
		/**
		 * получить значение переменной в виде строки по индексу
		 * @param index индекс строки с переменной в конфиге
		 * @return значение переменной в виде строки (null, если перемменная не найдена)
		 */
		public String recString(int index) { //получить строку по индексу
			return getString(index);
		}
		/**
		 * получить значение перемменной в виде массива строк по индексу
		 * @param index индекс строки с массивом в конфине
		 * @return значение переменной в виде массива строк (null, если переменная не найдена)
		 */
		public String[] recStringArray(int index) { //получить массив строк по индексу
			return getStringArray(index);
		}
		/**
		 * проверить массив внутренним методом isArray
		 * @param index индекс строки в конфиге
		 * @return класс IsArray с данными проверки
		 */
		public IsArray checkArray(int index) { //проверить массив внутренним методом IsArray
			return isArray(index);
		}
		/**
		 * очистить весь конфиг от комментариев и пустых строк
		 */
		public void clearComments() { //очистить конфиг от комментариев
			if (!get | file == null) return;
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null) {
					ClearResult r = clearStr(file[i]);
					if (r.fullstr | r.empty) continue;
					if (r.cleaned != null) list.add(r.cleaned);
				}
			}
			file = new String[list.size()];
			for (int i = 0; i < file.length; i++) file[i] = list.get(i);
		}
		/**
		 * узнать реальную длинну многострочного массива в конфиге, включая строку с названием
		 * @param index индекс массива
		 * @return количество строк от первого элемента до последнего (-1 при бракованном массиве)
		 */
		public int getArrayRealLength(int index) { //узнать реальную длинну массива
			int result = -1;
			if (index < 0) {Log.warn("LoaderMethods getArrayRealLength(index): failed, index < 0"); return result;}
			if (index >= file.length) {Log.warn("LoaderMethods getArrayRealLength(index): failed, index > file.length"); return result;}
			IsArray ia = isArray(index);
			if (ia.array) {
				if (ia.skobka) result = 1; else {
					for (int i = (index+1); i < file.length; i++) { //узнаем конец массива
						ClearResult r = clearStr(file[i]);
						if (r.colon > -1) { //выход из цикла при попадании на опцию или секцию
							result = (i-1)-(index-1);
							break;
						}
						if (i == (file.length-1)) result = i-(index-1); //сохранение результата при достижении конца конфига
					}
					for (int i = (index+result-1); i > index; i--) { //вычитаем лишние строки в конце
						ClearResult r = clearStr(file[i]);
						if (r.hypindex > -1) { //выход из цикла при попадании на последний элемент массива
							result -= (index+result-1)-i;
							break;
						}
					}
				}
			}
			return result;
		}
	}
	/**
	 * инициализированный объект LoaderMethods
	 */
	public LoaderMethods Methods = new LoaderMethods();
}