package ru.intervi.littleconfig;

import java.io.File;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ru.intervi.littleconfig.utils.Utils;
import ru.intervi.littleconfig.utils.EasyLogger;

/**
 * чтение файла конфигураци
 */
public class ConfigLoader { //чтение конфига из файла и получение значений
	/**
	 * ======================================================================================
	 * ======================================================================================
	 * ---------------КОНСТРУКТОРЫ
	 * ======================================================================================
	 */
	/**
	 * необходимо загрузить конфиг для работы с ним
	 */
	public ConfigLoader() {}
	/**
	 * вызывает метод {@link ru.intervi.littleconfig.ConfigLoader#load(String, boolean)}
	 * @param path путь к конфигу
	 * @param gap true - читать до первого разрыва ("..." или "---"), false - весь файл
	 * @throws NullPointerException если File == null
	 * @throws FileNotFoundException если файл не существует
	 * @throws IOException потоковая ошибка
	 */
	public ConfigLoader(String path, boolean gap) throws NullPointerException, FileNotFoundException, IOException {
		load(path, gap);
	}
	/**
	 * вызывает метод {@link ru.intervi.littleconfig.ConfigLoader#load(File, boolean)}
	 * @param file объект File конфига для чтения
	 * @param gap true - читать до первого разрыва ("..." или "---"), false - весь файл
	 * @throws NullPointerException если File == null
	 * @throws FileNotFoundException если файл не существует
	 * @throws IOException потоковая ошибка
	 */
	public ConfigLoader(File file, boolean gap) throws NullPointerException, FileNotFoundException, IOException {
		load(file, gap);
	}
	/**
	 * вызывает метод {@link ru.intervi.littleconfig.ConfigLoader#fakeLoad(String[])}
	 * @param value конфиг в виде массива строк
	 */
	public ConfigLoader(String value[]) {fakeLoad(value);}
	/**
	 * вызывает метод {@link ru.intervi.littleconfig.ConfigLoader#load(String, boolean)} с false
	 * @param path путь к конфигу
	 * @throws NullPointerException если File == null
	 * @throws FileNotFoundException если файл не существует
	 * @throws IOException потоковая ошибка
	 */
	public ConfigLoader(String path) throws NullPointerException, FileNotFoundException, IOException {
		load(path, false);
	}
	/**
	 * вызывает метод {@link ru.intervi.littleconfig.ConfigLoader#load(File, boolean)} с false
	 * @param file объект File конфига для чтения
	 * @throws NullPointerException если File == null
	 * @throws FileNotFoundException если файл не существует
	 * @throws IOException потоковая ошибка
	 */
	public ConfigLoader(File file) throws NullPointerException, FileNotFoundException, IOException {
		load(file, false);
	}
	
	/**
	 * ======================================================================================
	 * ======================================================================================
	 * ---------------ПЕРЕМЕННЫЕ, МЕТОДЫ ЗАГРУЗКИ И ПРОВЕРКИ
	 * ======================================================================================
	 */
	/**
	 * используемый логгер для вывода сообщений
	 */
	public EasyLogger log = new EasyLogger();
	
	private boolean get = false; //загружен ли конфиг
	private String[] file; //массив с содержимым файла конфигурации
	private boolean tsc = false; //является ли этот экземпляр класса секцией
	private boolean tf = false; //использовалась ли фековая загрузка
	
	/**
	 * содержит ли данный экземпляр ConfigLoader секцию, полученную спец. методом (например {@link ru.intervi.littleconfig.ConfigLoader#getSection(String)})
	 * @return true если да; false если нет
	 */
	public final boolean thisIsSection() {
		return tsc;
	}
	
	protected void setThisIsSection() { //метод для правки переменной из ConfigWriter-а
		tsc = true;
	}
	
	/**
	 * проверить, загружен ли конфиг
	 * @return true если да; false если нет
	 */
	public final boolean isLoad() { //загружен ли конфиг
		return get;
	}
	
	/**
	 * использовалась ли фековая загрузка конфига
	 * @return true если да; false если нет
	 */
	public final boolean isFakeLoad() {
		return tf;
	}
	
	/**
	 * прочитать конфиг (вызовет {@link ru.intervi.littleconfig.ConfigLoader#load(File, boolean)})
	 * @param path путь к конфигу
	 * @param gap true - читать до первого разрыва ("..." или "---"), false - весь файл
	 * @throws NullPointerException если File == null
	 * @throws FileNotFoundException если файл не существует
	 * @throws IOException потоковая ошибка
	 */
	public void load(String path, boolean gap) throws NullPointerException, FileNotFoundException, IOException { //загрузка конфина
		if (path == null) {
			log.error("ConfigLoader load(String, boolean): null path");
			throw new NullPointerException("null String path");
		}
		load(new File(path), gap);
	}
	
	/**
	* прочитать конфиг
	 * @param file объект File конфига для чтения
	 * @param gap true - читать до первого разрыва ("..." или "---"), false - весь файл
	 * @throws NullPointerException если File == null
	 * @throws FileNotFoundException если файл не существует
	 * @throws IOException потоковая ошибка
	 */
	public void load(File file, boolean gap) throws NullPointerException, FileNotFoundException, IOException { //загрузка конфига
		if (file == null) {
			log.error("ConfigLoader load(File, boolean): null file");
			throw new NullPointerException("null object File");
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		ArrayList<String> list = new ArrayList<String>();
		while(reader.ready()) {
			String line = reader.readLine();
			if (line != null) {
				if (gap && line.trim().length() >= 3) {
					String check = line.trim().substring(0, 4);
					if (check.equals("...") || check.equals("---")) break;
				}
				list.add(line);
			}
		}
		reader.close();
		if (!list.isEmpty()) {
			get = true;
			this.file = list.toArray(new String[list.size()]);
		}
	}
	
	/**
	 * фейковая загрузка данных
	 * @param value массив строк, в котором представлен конфиг
	 * @throws NullPointerException если массив строк == null (null строки внутри массива будут пропускатся)
	 */
	public void fakeLoad(String[] value) throws NullPointerException { //фейковая загрузка (установка значения из массива)
		if (value == null) throw new NullPointerException("null String[] value");
		file = new String[value.length];
		for (int i = 0; i < value.length; i++) { //чистка от null'ов
			if (value[i] != null) file[i] = value[i]; else file[i] = "";
		}
		get = true;
		tf = true;
	}
	
	/**
	 * ======================================================================================
	 * ======================================================================================
	 * ---------------МЕТОД ОБРАБОТКИ СТРОК
	 * ======================================================================================
	 */
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
		 * является ли строка бракованной (не переменной)
		 */
		public boolean broken = true;
		/**
		 * является ли строка пустой
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
		 * <br/>присваивается только в том случае, если значение заключено в кавычки
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
		 * <br/>присваивается только в том случае, если значение заключено в квадратные скобки
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
		/**
		 * заключено ли название переменной в кавычки
		 */
		public boolean quname;
		/**
		 * индекс лишней кавычки вначале (-1 если ее нет, может быть равно firstquote, если вначале значения несколько кавычек)
		 */
		public int exquote = -1;
	}
	
	private ClearResult clearStr(String s) { //очистка строки от комментов
		ClearResult result = new ClearResult();
		if (s == null) return result;
		result.origin = s;
		int sleng = Utils.trim(s).length();
		if (sleng == 0) {
			result.empty = true;
			return result;
		} else if (sleng < 2) return result; //строку в 1 символ нет смысла обрабатывать
		if (s.trim().charAt(0) == '#') { //если коммент во всю строку
			result.comindex = 0;
			result.com = s.substring(1, s.length());
			result.fullstr = true;
			return result;
		}
		
		char c[] = s.toCharArray();
		int q = -1, q2 = -1, //индексы кавычек "
				q3 = -1, q4 = -1, //индексы кавычек '
				d = -1, //двоеточие
				ci = -1, //комментарий
				p = 0, //пробелы
				sq = -1, sq2 = -1, //квадратные скобки
				hyp = -1, //тире
				stq = -1, //страховочный механизм
				sts = -1,
				stf = -1, stff = -1, stt = -1; //страховка для первой кавычки
		boolean qn = false, //заключено ли название в кавычки
				br = false, //прервать ли цикл
				str = false; //страховка для первой кавычки, ftf == stff
		
		for (int i = 0; i < c.length; i++) {
			if (br) break; //прерывание обработки в случае нахождения ошибки
			if (c[i] == ' ') { //пропуск обработки пробелов
				p++;
				continue;
			}
			if (d == -1 && hyp == -1) { //нахождение стартовых символов элементов
				boolean qf = false; //найдена кавычка
				char qc = '\u0000'; //символ кавычки
				switch(c[i]) {
				case ':':
					if (i != 0 || i != p) d = i; else br = true; //если перед двоеточием нет символов - опция бракованная
					continue;
				case '-': //нахождения дефиса (для корректной очистки элементов массивов)
					if (i == 0 || i == p) hyp = i; else br = true; //перед дефисом наоборот символов быть не должно
					continue;
				case '"': //обработка названий, заключенных в кавычки
					if (i == 0 || i == p) { //та же проверка - перед кавычкой не должно быть символов
						qf = true;
						qc = '"';
					} else {
						br = true;
						continue;
					}
					break;
				case '\'':
					if (i == 0 || i == p) {
						qf = true;
						qc = '\'';
					} else {
						br = true;
						continue;
					}
					break;
				default:
					continue;
				}
				if (qf) { //обработка названия в кавычках
					/*
					 * поиск закрывающей конструкции: ':, ":
					 * (смотря какая кавычка)
					 */
					for (int n = (i+1); n < c.length; n++) {
						if (c[n] == ':' && c[n-1] == qc) {
							if ((n-i) <= 2) { //если между кавычек пусто
								br = true;
								break;
							}
							String name = s.substring((i+1), (n-1));
							if (Utils.trim(name).isEmpty()) { //если между кавычек одни пробелы
								br = true;
								break;
							}
							result.name = name;
							d = n;
							i = n;
							qn = true;
							break;
						}
						if ((n+1) == c.length) br = true; //если закрывающая конструкция так и не была найдена
					}
					continue;
				}
			} else {
				if (q3 == -1) {
					if (q == -1) {
						if (c[i] == '"' && sq == -1) { //поиск кавычки
							boolean ok = true; //только если кавычка - первый символ после имени опции
							if ((i-d) > 1) {
								for (int n = (d+1); n < i; n++) {
									if (c[n] != ' ' && c[n] != '"' && c[n] != '\'') {
										ok = false;
										break;
									}
								}
							}
							//определение лишней кавычки вначале
							if ((stt != i && (i+1) < c.length) && (c[(i+1)] == '"' || c[(i+1)] == '\'')) {
								if (stff == -1) stff = i; stf = i;
								if (result.exquote == -1) result.exquote = i;
								continue;
							}
							if (ok) q = i;
							continue;
						}
					} else if (sq2 >= sq || sts > -1) { //поиск второй кавычки
						if (q2 == -1 && c[i] == '"') {
							stq = i;
							boolean ok = true; //только если кавычка действительно на конце строки
							for (int n = (i+1); n < c.length; n++) {
								if (c[n] != ' ') {
									ok = false;
									n = c.length;
								}
							}
							if (ok) {
								q2 = i;
								if ((i+1) != c.length) continue;
							} else continue;
						}
					}
				}
				if (q == -1) {
					if (q3 == -1) {
						if (c[i] == '\'' && sq == -1) { //поиск кавычки другого вида
							boolean ok = true; //та же проверка
							if ((i-d) > 1) {
								for (int n = (d+1); n < i; n++) {
									if (c[n] != ' ' && c[n] != '"' && c[n] != '\'') {
										ok = false;
										break;
									}
								}
							}
							if ((stt != i && (i+1) < c.length) && (c[(i+1)] == '"' || c[(i+1)] == '\'')) {
								if (stff == -1) stff = i; stf = i;
								if (result.exquote == -1) result.exquote = i;
								continue;
							}
							if (ok) q3 = i;
							continue;
						}
					} else if (sq2 >= sq || sts > -1) { //поиск второй кавычки
						if (q4 == -1 && c[i] == '\'') {
							stq = i;
							boolean ok = true; //только если кавычка действительно на конце строки
							for (int n = (i+1); n < c.length; n++) {
								if (c[n] != ' ') {
									ok = false;
									n = c.length;
								}
							}
							if (ok) {
								q4 = i;
								if ((i+1) != c.length) continue;
							} else continue;
						}
					}
				}
				//поиск квадратной скобки (для корректной обработки массивов)
				if (sq == -1) {
					if (c[i] == '[') {
						if ((i-d) > 1) {
							boolean ok = true; //только если скобка - первый символ после имени опции
							for (int n = (d+1); n < i; n++) {
								if (c[n] == '\'' || c[n] == '"') continue;
								if (c[n] != ' ') {
									ok = false;
									break;
								}
							}
							if (ok) sq = i;
						} else sq = i;
						continue;
					}
				} else { //поиск второй скобки
					if (sq2 == -1) {
						if (c[i] == ']') {
							sts = i;
							boolean ok = true; //только если скобка действительно на конце строки
							for (int n = (i+1); n < c.length; n++) {
								if (c[n] == '#') {
									ci = n;
									break;
								}
								if (c[n] == '\'' || c[n] == '"') continue;
								if (c[n] != ' ') {
									ok = false;
									break;
								}
							}
							if (ok) {
								sq2 = i;
								continue;
							} else continue;
						}
					}
				}
			}
			if (ci == -1 && c[i] == '#') { //поиск коммента
				//исключение символа, находящегося в кавычках
				if (q2 >= q && q4 >= q3 && sq2 >= sq) {
					//если есть коммент, но нет опции, при этом символ коммента не первый в строке (не считая пробелы), то строка бракованная
					if (d == -1) result.fullstr = true;
					ci = i;
					break;
				}
			}
			if ((i+1) == c.length) {
				//повтор цикла с поиском другой кавычки
				if (!str && q2 == -1 && q4 == -1 && stf != -1) {
					if (q == -1 && q3 == -1) break;
					q = -1; q3 = -1; stq = -1;
					i = stff-1;
					if (stf == stff) str = true;
					stt = stf; stf = -1;
					continue;
				}
				//для правильной обработки одинаковых кавычек вначале
				char sc = '\u0000';
				if (stt < q || stt == q) sc = '"';
				else if (stt < q3 || stt == q3) sc = '\'';
				if (sc != '\u0000') {
					for (int n = (q-1); n > d && n > hyp; n--) {
						if (c[n] == sc) {
							switch(sc) {
							case '"':
								i = q;
								q = n;
								break;
							case '\'':
								i = q3;
								q3 = n;
							}
							q2 = -1; q4 = -1; stq = -1;
							break;
						}
					}
				}
			}
		}
		
		/*
		 * страховочный механизм
		 * если вторая кавычка не найдена на конце строки,
		 * значит после нее идут лишние символы
		 */
		if (q2 == -1 && q4 == -1 && stq != -1) {
			if (q != -1) q2 = stq;
			else q4 = stq;
		}
		if (sq != -1 && sq2 == -1 && sts != -1) sq2 = sts; //тоже самое с квадратными скобками
		
		//заполнение результатов
		result.probels = p;
		result.firstsq = sq;
		result.lastsq = sq2;
		result.hypindex = hyp;
		result.colon = d;
		result.quname = qn;
		if (q2 > q) { //если найдены кавычки первого типа
			result.firstquote = q;
			result.lastquote = q2;
			if ((q2-q) > 1) { //если между кавычек пусто - строка бракованная
				result.content = s.substring((q+1), q2);
				result.broken = false;
			}
		} else if (q4 > q3) { //если найдены кавычки второго типа
			result.firstquote = q3;
			result.lastquote = q4;
			if ((q4-q3) > 1) {
				result.content = s.substring((q3+1), q4);
				result.broken = false;
			}
		} else {
			if (q >= 0 && q3 == -1) result.firstquote = q;
			if (q3 >= 0 && q == -1) result.firstquote = q3;
			if (q2 >= 0 && q4 == -1) result.lastquote = q2;
			if (q4 >= 0 && q2 == -1) result.lastquote = q4;
		}
		if (d > -1) {
			if (q2 == -1 && q4 == -1) {
				//если кавычек нет - вырезаем контент между двоеточием и концом строки (или комментом)
				char ch[] = s.trim().toCharArray();
				boolean ok = true; //проверка, есть ли контент между двоеточием и комментом (кроме пробелов)
				if ((ci > -1 && d < ci) && Utils.trim(s.substring((d+1), ci)).length() <= 0) ok = false;
				if (ch[(ch.length-1)] != ':' && (ci == -1 || ok)) { //если строка кончается двоеточием - она бракованная
					if (ci == -1) result.content = s.substring((d+1), s.length()).trim();
					else result.content = s.substring((d+1), ci).trim();
					result.broken = false;
				}
			}
			if (!qn) result.name = s.split(":")[0].trim();
		} else {
			//если нет двоеточия - строка бракованная
			if (hyp > -1) {
				//если кавычек нет - вырезаем контент между тире и концом строки (или комментом)
				int ch = s.substring(hyp, s.length()).trim().length();
				if ((q == -1 && q3 == -1) && ch > 0) {
					if (ci == -1) result.content = s.substring((hyp+1), s.length()).trim();
					else result.content = s.substring((hyp+1), ci).trim();
				}
			}
		}
		if (result.broken) {
			if (sq2 > sq && (sq2-sq) > 1) { //страховка
				result.content = s.substring(sq, (sq2+1));
				result.broken = false;
			}
		}
		if (ci > -1) { // заполнение результатов, если в строке есть коммент
			result.comindex = ci;
			result.cleaned = s.substring(0, ci).trim();
			result.com = s.substring((ci+1), s.length());
			result.clear = true;
		} else if ((q != -1 || q3 != -1) && (q2 == -1 && q4 == -1)) {
			int cii = s.indexOf('#');
			if (cii != -1) result.cleaned = s.substring(0, cii);
		} else result.cleaned = s;

		return result;
	}
	
	/**
	 * ======================================================================================
	 * ======================================================================================
	 * ---------------ЧТЕНИЕ ДАННЫХ ИЗ СТРОК
	 * ======================================================================================
	 */
	private String getFullStr(int index) { //получить полное значение опции / элемента массива
		if (index < 0) {
			log.warn("ConfigLoader getFullStr: failed, index < 0");
			return null;
		}
		if (index >= file.length) {
			log.warn("ConfigLoader getFullStr: failed, index >= file.length");
			return null;
		}
		if (!get || file == null) {
			log.warn("ConfigLoader getFullStr: failed, config not set");
			return null;
		}
		String result = null;
		int p = 0;
		if (index >= 0) p = getProbels(file[index]);
		boolean oq = false, //использовались ли разбросанные скобки
				br = false; //выход из цикла
		char oqc = '\u0000'; //используемый символ
		for (int i = index; i < file.length; i++) {
			ClearResult cr = clearStr(file[i]);
			if (i == index) {
				if (!cr.broken || cr.hypindex != -1) {
					result = cr.content;
					if ((cr.firstquote != -1 && cr.lastquote == -1) || cr.exquote != -1) { //выявление незакрытой кавычки
						oq = true;
						if (cr.exquote == -1) oqc = cr.origin.charAt(cr.firstquote);
						else oqc = cr.origin.charAt(cr.exquote);
					}
					continue;
				} else if (cr.broken && cr.colon != -1) continue; else break;
			}
			if (cr.empty || (cr.origin == null || cr.fullstr)) continue;
			if (cr.colon != -1 || cr.hypindex != -1) break; //если строка - не часть значения другого параметра
			if (getProbels(file[i]) < p) break; //если у строки пробелов в начале меньше - это не часть значения
			String part = cr.origin.trim();
			char c[] = part.toCharArray();
			char qc = '\u0000'; //символ кавычки
			boolean q = false; //заключена ли строка в кавычки
			int ind[] = {0, c.length}; //индексы для обрезки строки
			switch(c[0]) { //определение первой кавычки
			case '\'':
				qc = '\'';
				q = true;
				ind[0] = 1;
				break;
			case '"':
				qc = '"';
				q = true;
				ind[0] = 1;
			}
			int st = -1; //страховка
			for (int n = 0; n < c.length; n++) { //посимвольный анализ
				if (n == 0 && q) continue;
				if (q) {
					if (c[n] == qc) { //найдена вторая кавычка
						st = n;
						boolean ok = true;
						for (int k = (n+1); k < c.length; k++) { //проверка, что она действительно закрывающая
							switch(c[k]) { //на конце ничего не должно быть (кроме коммента)
							case ' ':
								continue;
							case '#':
								k = c.length;
								break;
							case '"':
								if (oq && oqc == '"') {
									br = true;
									k = c.length;
								}
								else ok = false;
								break;
							case '\'':
								if (oq && oqc == '\'') {
									br = true;
									k = c.length;
								}
								else ok = false;
								break;
							default:
								ok = false;
							}
						}
						if (ok) {
							ind[0] = 1;
							ind[1] = n;
							break;
						} else ind[0] = 0;
					} else if ((n+1) == c.length) { //повтор цикла, если закрывающая кавычка не найдена
						if (st == -1) {
							q = false;
							n = 0;
							st = -1;
						} else {
							ind[0] = 1;
							ind[1] = st;
							break;
						}
					}
				} else { //если кавычки не используются, простой способ выявления коммента на конце
					switch(c[n]) {
					case '#':
						ind[1] = n;
						n = c.length;
						break;
					case '"':
						if (oq && oqc == '"') {
							br = true;
							ind[1] = n;
							st = n;
						}
						break;
					case '\'':
						if (oq && oqc == '\''){
							br = true;
							ind[1] = n;
							st = n;
						}
					}
					if (br) { //проверка, что кавычка закрывающая
						for (int k = (n+1); k < c.length; k++) {
							switch(c[k]) {
							case ' ':
								continue;
							case '#':
								k = c.length;
								break;
							default:
								br = false;
								ind[1] = c.length;
								k = c.length;
							}
						}
					}
					if (!br && (n+1) == c.length && st != -1) { //страховночный механизм
						br = true;
						ind[1] = st;
					}
				}
			}
			//обрезка и сохранение значения
			if (result == null) result = part.substring(ind[0], ind[1]);
			else result += part.substring(ind[0], ind[1]);
			if (br) {
				result = result.substring(1, result.length()); //обрезаем кавычку (вторая уже обрезана)
				break;
			}
		}
		return result;
	}
	
	private String getString(int index) { //получение переменной типа String по индексу
		String result = null;
		if (index < 0) {log.warn("ConfigLoader getString(index): failed, index < 0"); return result;}
		if (index >= file.length) {log.warn("ConfigLoader getString(index): failed, index >= file.length"); return result;}
		if (get && file != null) { //поиск и получение переменной из массива
			if (isSet(index)) {
					result = getFullStr(index);
				if (result == null) log.warn("ConfigLoader getString(index) " + index + "(index) null content or broken");
			} else log.warn("ConfigLoader getString(index): " + index + "(index) no data");
		} else if (!get) log.warn("ConfigLoader getString(index): " + index + "(index) file not loaded");
		else if (file == null) log.warn("ConfigLoader getString(index): " + index + "(index) array file = null");
		return result;
	}
	
	/**
	 * получить значение переменной в винде строки
	 * @param name имя переменной
	 * @return значение в виде строки (null, если переменная не найдена)
	 */
	public String getString(String name) { //получение переменной типа String по названию
		String result = null;
		if (name == null) {log.warn("ConfigLoader getString: null name"); return result;}
		if (get && file != null) {
			int index = getIndexNoSection(name);
			if (index == -1) log.warn("ConfigLoader getString(name): " + name + " error, not found");
			else result = getString(index);
		} else log.warn("ConfigLoader getString(name): " + name + " error (file not load or null array");
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
		if (name == null) {log.warn("ConfigLoader getInt: null name"); return 0;}
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
		if (name == null) {log.warn("ConfigLoader getInt: null name"); return 0;}
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
		if (name == null) {log.warn("ConfigLoader getInt: null name"); return 0;}
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
		if (name == null) {log.warn("ConfigLoader getLong: null name"); return -1;}
		return getLong(getIndexNoSection(name));
	}
	
	private float getFloat(int index) {
		return Utils.floatFromString(getString(index));
	}
	
	/**
	 * получить значение переменной в виде float
	 * @param name имя переменной
	 * @return значение переменной в виде float (0, если значение не найдено)
	 */
	public float getFloat(String name) {
		if (name == null) {log.warn("ConfigLoader getFloat: null name"); return -1;}
		return getFloat(getIndexNoSection(name));
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
		if (name == null) {log.warn("ConfigLoader getDouble: null name"); return -1;}
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
		if (name == null) {log.warn("ConfigLoader getBoolean: null name"); return false;}
		return getBoolean(getIndexNoSection(name));
	}
	
	/**
	 * получение всего конфига
	 * @return конфиг в виде массива строк
	 */
	public final String[] getAll() { //получение всего конфига массивом строк
		if (get && file != null) return file; else {
			log.warn("ConfigLoader getAll: failed, returning null");
			return null;
		}
	}
	
	/**
	 * ======================================================================================
	 * ======================================================================================
	 * ---------------ЧТЕНИЕ ДАННЫХ ИЗ МАССИВОВ
	 * ======================================================================================
	 */
	private String[] getStringArray(int index) { //получение переменной типа массив строк (по индексу)
		if (index < 0) {log.warn("ConfigLoader getStringArray(index): failed, index < 0"); return null;}
		if (index >= file.length) {log.warn("ConfigLoader getStringArray(index): failed, index >= file.length"); return null;}
		String[] result = null;
		IsArray a = isArray(index);
		if (a.array && !a.empty) {
			if (a.skobka) { //парсинг данных из однострочного массива
				String str = a.content.substring(1, (a.content.length()-1)).trim(); //отрезаем скобки
				if (Utils.numChars(str, '"') < 2 && Utils.numChars(str, '\'') < 2) { //если кавычки не применялись
					result = str.split(",");
					for (int i = 0; i < result.length; i++) {
						if (result[i] != null) result[i] = result[i].trim();
					}
				} else { //если применялись
					ArrayList<String> list = new ArrayList<String>(); //готовый список строк
					char c[] = str.toCharArray();
					int f = -1; //первая кавычка
					int fc = -1; //первый символ элемента (для обработки элементов без кавычек)
					boolean cz = false;
					for (int i = 0; i < c.length; i++) { //парсинг элементов в лист
						if (c[i] == ' ') continue; //пропуск пробелов
						if (cz) { //пропуск обработки до запятой (пропуск элементов без кавычек)
							if (c[i] == ',') {
								cz = false;
								list.add(str.substring(fc, i).trim()); //сохранение элементов
							}
							if ((i+1) == c.length) list.add(str.substring(fc, c.length)); //если это последняя интерация
							continue;
						}
						if (f == -1) { //поиск первой кавычки
							if (c[i] == '"' || c[i] == '\'') f = i;
						} else { //поиск второй кавычки и запятой
							if ((c[i] == '"' && c[f] == '"') || (c[i] == '\'' && c[f] == '\'')) {
								int z = -1;
								for (int n = (i+1); n < c.length; n++) { //поиск запятой после закрывающей кавычки
									if ((n+1) == c.length) { //если это последняя интерация
										z = c.length;
										break;
									}
									switch(c[n]) {
									case ' ':
										continue;
									case ']':
										if ((n+1) == c.length) {
											z = n;
											n = c.length; //для выхода из цикла
										}
										break;
									case ',':
										z = n;
										n = c.length;
										break;
									default:
										n = c.length;
									}
								}
								if (z > -1) { //сохранение элементов
									list.add(str.substring((f+1), i));
									f = -1;
									i = z;
									continue;
								}
								/*
								 * если это последняя интерация
								 * вложенный цикл for не срабатывает на последней интерации
								 * из-за условия n = (i+1), противоречащего n < c.length
								 */
								if ((i+1) == c.length) {
									list.add(str.substring((f+1), i));
									break;
								}
							}
							//если это последняя интерация и нет закрывающей кавычки
							if ((i+1) == c.length) {
								list.add(str.substring((f+1), c.length));
								break;
							}
						}
						if (f == -1 && (c[i] != ' ' || c[i] != '"' || c[i] != '\'' || c[i] != ',')) {
							//если до первой кавычки попался сторонний символ - значит элемент не заключен в кавычки
							//поиск запятой
							cz = true;
							fc = i;
						}
					}
					result = list.toArray(new String[list.size()]); //сохранение результатов
				}
			} else { //парсинг данных из многострочного массива
				ArrayList<String> list = new ArrayList<String>();
				for (int i = (index+1); i < file.length; i++) {
					ClearResult r = clearStr(file[i]);
					if (r.empty) continue;
					if (r.colon > -1) break; //выход из цикла при попадании на опцию или секцию
					if (r.hypindex == -1) continue;
					String add = getFullStr(i); //получаем готовый элемент
					if (add != null) {
						char q1 = add.charAt(0); //чистка от кавычек
						char q2 = add.charAt((add.length()-1));
						if ((q1 == '"' && q2 == '"') || (q1 == '\'' && q2 == '\'')) add = add.substring(1, (add.length()-1));
						list.add(add);
					}
				}
				result = list.toArray(new String[list.size()]);
			}
		} else if (!a.array) log.warn("ConfigLoader getStringArray(index): " + index + "(index), not array");
		
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
		if (name == null) {log.warn("ConfigLoader getStringArray(name): null name"); return result;}
		if (get && file != null) {
			int index = getIndexNoSection(name);
			if (index == -1) log.warn("ConfigLoader getStringArray(name): " + name + " error, not found");
			else result = getStringArray(index);
		} else log.warn("ConfigLoader getStringArray(name): get " + name + " failed (config not loaded or file = null)");
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
		/**
		 * содержимое опции (для односточных массивов)
		 */
		public String content;
	}
	
	private IsArray isArray(int index) { //проверка переменной на то, является ли она массивом
		IsArray result = new IsArray();
		if (index < 0) {log.warn("ConfigLoader isArray(index): failed, index < 0"); return result;}
		if (index >= file.length) {log.warn("ConfigLoader isArray(index): failed, index >= file.length"); return result;}
		if (get && file != null) {
			ClearResult r = clearStr(file[index]);
			result.clear = r;
			if (r.cleaned != null) {
				String con = getFullStr(index);
				if (con != null) { //если есть содержимое в опции - проверяем
					result.content = con;
					char ch[] = Utils.trim(con).toCharArray();
					if (ch != null) { //мало ли...
						if (ch.length > 0 && (ch[0] == '[' && ch[(ch.length-1)] == ']')) { //если строка закрыта в квадратные скобки
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
		} else if (!get) log.warn("ConfigLoader isArray(index): " + index + "(index) file not loaded");
		else if (file == null) log.warn("ConfigLoader isArray(index): " + index + "(index) array file = null");
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
		if (name == null) {log.warn("ConfigLoader getByteArray: null name"); return null;}
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
		if (name == null) {log.warn("ConfigLoader getShortArray: null name"); return null;}
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
		if (name == null) {log.warn("ConfigLoader getIntArray: null name"); return null;}
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
		if (name == null) {log.warn("ConfigLoader getLongArray: null name"); return null;}
		return getLongArray(getIndexNoSection(name));
	}
	
	private float[] getFloatArray(int index) {
		return Utils.floatFromStringArray(getStringArray(index));
	}
	
	/**
	 * получить значение переменной в виде массива float
	 * @param name имя переменной
	 * @return значение переменной в виде массива float (null, если не найдено)
	 */
	public float[] getFloatArray(String name) {
		if (name == null) {log.warn("ConfigLoader getFloatArray: null name"); return null;}
		return getFloatArray(getIndexNoSection(name));
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
		if (name == null) {log.warn("ConfigLoader getDoubleArray: null name"); return null;}
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
		if (name == null) {log.warn("ConfigLoader getBooleanArray: null name"); return null;}
		return getBooleanArray(getIndexNoSection(name));
	}
	
	/**
	 * ======================================================================================
	 * ======================================================================================
	 * ---------------МЕТОДЫ РАЗЛИЧНЫХ ПРОВЕРОК
	 * ======================================================================================
	 */
	/**
	 * получить названия переменных из всего конфига (не включает названия секций)
	 * @return названия переменных в виде массива строк (null, если ничего не найдено)
	 */
	public String[] getOptionNames() { //получение названия переменных из всего конфига
		String result[] = null;
		if (get && file != null) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < file.length; i++) { //парсинг имени всех опций в лист
				boolean sec = isSection(i);
				if (isSet(i) && !sec) {
					String name = clearStr(file[i]).name;
					if (name != null) list.add(name);
				}
				else if (sec) {
					i += getSectionRealLength(i)-1; //секции пропускаем
					continue;
				}
			}
			result = list.toArray(new String[list.size()]); //заполнение результатов
		} else log.warn("ConfigLoader getConfigVars: failed, config not loaded");
		return result;
	}
	
	private boolean isSet(int index) { //проверка, прописана ли переменная (по индексу)
		boolean result = false;
		if (index < 0) {log.warn("ConfigLoader isSet: failed, index < 0"); return result;}
		if (index >= file.length) {log.warn("ConfigLoader isSet: failed, index > file.length"); return result;}
		if (get && file != null) {
			if (clearStr(file[index]).colon != -1 && getFullStr(index) != null) result = true; //является ли строка параметром
			if (!result) result = isArray(index).array; //является ли она массивом
			if (!result) result = isSection(index); //является ли она секцией
		} else log.warn("ConfigLoader isSet(index): failed check " + index + ", config not loaded");
		return result;
	}
	
	/**
	 * проверить, есть ли какое-либо значение у переменной (подходит для проверки массивов и секций)
	 * @param name имя переменной
	 * @return true если есть; false если нету
	 */
	public boolean isSet(String name) { //проверка, прописана ли переменная (по названию)
		boolean result = false;
		if (name == null) {log.warn("ConfigLoader isSet: null name"); return false;}
		if (get && file != null) {
			result = isSet(getIndexNoSection(name));
		} else log.warn("ConfigLoader isSet(name): failed check " + name + ", config not loaded");
		return result;
	}
	
	private boolean isSetArray(int index) { //проверка, прописан ли массив (по индексу)
		boolean result = false;
		if (index < 0) {log.warn("ConfigLoader isSetArray: failed, index < 0"); return result;}
		if (index >= file.length) {log.warn("ConfigLoader isSetArray: failed, index > file.length"); return result;}
		if (get && file != null) {
			result = isArray(index).array;
		} else log.warn("ConfigLoader isSet: failed check " + index + ", config not loaded");
		return result;
	}
	
	/**
	 * проверить, есть ли какое-либо значение у массива
	 * @param name имя переменной с массивом
	 * @return true если да; false если нет
	 */
	public boolean isSetArray(String name) { //проверка, прописан ли массив (по названию)
		if (name == null) {log.warn("ConfigLoader isSetArray: null name"); return false;}
		return isSetArray(getIndexNoSection(name));
	}
	
	/**
	 * типы переменных
	 */
	public enum TypeValue {
		STRING,
		BYTE,
		SHORT,
		INT,
		LONG,
		FLOAT,
		DOUBLE,
		BOOLEAN,
		STRING_ARRAY,
		BYTE_ARRAY,
		SHORT_ARRAY,
		INT_ARRAY,
		LONG_ARRAY,
		FLOAT_ARRAY,
		DOUBLE_ARRAY,
		BOOLEAN_ARRAY,
		SECTION,
		NULL
	}
	
	private TypeValue getTypeData(String str) { //узнать тип данных в строке
		TypeValue result = TypeValue.NULL;
		if (str == null) {log.warn("ConfigLoader getTypeData: null str"); return result;}
		if (!get || file == null) {log.warn("ConfigLoader getTypeData: failed, config not loaded or file == null"); return result;}
		if (str.isEmpty()) return result;
		try {
			double t = Double.parseDouble(str); //вычисление по диапазонам значений
			if (t >= Byte.MIN_VALUE && t <= Byte.MAX_VALUE) result = TypeValue.BYTE;
			else if (t >= Short.MIN_VALUE && t <= Short.MAX_VALUE) result = TypeValue.SHORT;
			else if (t >= Integer.MIN_VALUE && t <= Integer.MAX_VALUE) result = TypeValue.INT;
			else if (t >= Long.MIN_VALUE && t <= Long.MAX_VALUE) result = TypeValue.LONG;
			else if (t >= Float.MIN_VALUE && t <= Float.MAX_VALUE) result = TypeValue.FLOAT;
			else result = TypeValue.DOUBLE;
		} catch(NumberFormatException e) {
			//проверка на булеву
			if (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false")) result = TypeValue.BOOLEAN;
			else result = TypeValue.STRING;
		} catch(Exception e) {log.error(e); result = TypeValue.NULL;}
		return result;
	}
	
	@SuppressWarnings("incomplete-switch")
	private TypeValue getTypeData(String value[]) { //узнать тип данных в массиве
		TypeValue result = TypeValue.NULL;
		if (value == null) {log.warn("ConfigLoader getTypeValue: null array"); return result;}
		for (int i = 0; i < value.length; i++) {
			TypeValue t = getTypeData(value[i]);
			if (i > 0 && !t.equals(result)) { //проверка на численный тип, в таком случае он повышается
				if ((result.ordinal() >= 1 && result.ordinal() <= 6) && (t.ordinal() >= 1 && t.ordinal() <= 6)) result = t;
				else {
					result = TypeValue.STRING;
					break;
				}
			} else if (i == 0) result = t;
			result = t;
		}
		switch (result) { //на основе анализа строк указывается тип массива
		case NULL:
			break;
		case STRING:
			result = TypeValue.STRING_ARRAY;
			break;
		case BYTE:
			result = TypeValue.BYTE_ARRAY;
			break;
		case SHORT:
			result = TypeValue.SHORT_ARRAY;
			break;
		case INT:
			result = TypeValue.INT_ARRAY;
			break;
		case LONG:
			result = TypeValue.LONG_ARRAY;
			break;
		case FLOAT:
			result = TypeValue.FLOAT_ARRAY;
			break;
		case DOUBLE:
			result = TypeValue.DOUBLE_ARRAY;
			break;
		case BOOLEAN:
			result = TypeValue.BOOLEAN_ARRAY;
		}
		return result;
	}
	
	/**
	 * узнать тип переменной
	 * @param name имя переменной
	 * @return тип переменной в виде {@link ru.intervi.littleconfig.ConfigLoader.TypeValue}
	 */
	public TypeValue getType(String name) {
		TypeValue result = TypeValue.NULL;
		if (name == null) {log.warn("ConfigLoader getType: null name"); return result;}
		if (get && file != null) {
			int index = getIndexNoSection(name);
			if (index > -1) {
				if (isArray(index).array) result = getTypeData(getStringArray(index));
				else result = getTypeData(getFullStr(index));
			} else if (isSection(name)) result = TypeValue.SECTION;
		} else log.warn("ConfigLoader getType: failed get " + name + ", config not loaded or file == null");
		return result;
	}
	
	private int getProbels(String s) { //узнаем кол-во пробелов в начале строки
		if (s == null) {log.warn("ConfigLoader getProbels: failed, null String"); return -1;}
		char c[] = s.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] != ' ') return i-1;
		}
		return -1;
	}
	
	/**
	 * ======================================================================================
	 * ======================================================================================
	 * ---------------МЕТОДЫ ДЛЯ РАБОТЫ С СЕКЦИЯМИ
	 * ======================================================================================
	 */
	private int getIndexSection(String name) { //получить индекс секции по названию
		int result = -1;
		if (name == null) {log.warn("ConfigLoader getIndexSection: null name"); return result;}
		if (get && file != null) {
			for (int i = 0; i < file.length; i++) {
				ClearResult r = clearStr(file[i]);
				if (r.broken && r.name != null) {
					if (isSection(i) && r.name.equals(name)) {
						result = i;
						break;
					}
				}
			}
		} else log.warn("ConfigLoader getIndexSection: failed get " + name + ", config not loaded or file == null");
		return result;
	}
	
	private int getIndexNoSection(String name) { //получить индекс переменной по названию (не секции)
		int result = -1;
		if (name == null) {log.warn("ConfigLoader getIndexNoSection: null name"); return result;}
		if (get && file != null) {
			for (int i = 0; i < file.length; i++) {
				if (isSection(i)) { //пропуск секций
					i += getSectionRealLength(i)-1;
					continue;
				}
				if (isSet(i) && clearStr(file[i]).name.equals(name)) {
					result = i;
					break;
				}
			}
		} else log.warn("ConfigLoader getIndexNoSection: failed get " + name + ", config not loaded or file == null");
		return result;
	}
	
	private boolean isSection(int index) { //проверка, является ли переменная секцией (по индексу)
		boolean result = false;
		if (index < 0) {log.warn("ConfigLoader isSection: failed, index < 0"); return result;}
		if (index >= file.length) {log.warn("ConfigLoader isSection: failed, index > file.length"); return result;}
		if (get && file != null) {
			ClearResult r = clearStr(file[index]);
			if (r.broken) { //строка не должна быть опцией
				int p = getProbels(file[index])+1;
				for (int i = (index+1); i < file.length; i++) {
					if (p > getProbels(file[i])) break; //выход из цикла
					ClearResult c = clearStr(file[i]);
					if (c.empty || c.fullstr) continue; //пропуск не нужного
					if (c.colon > -1) { //если найдена опция - значит это секция
						result = true;
						break;
					}
				}
			}
		} else log.warn("ConfigLoader isSection(index): failed check " + index + ", config not loaded or file[i] == null");
		return result;
	}
	
	/**
	 * проверить, является ли переменная секцией
	 * @param name имя секции
	 * @return true если да; false если нет
	 */
	public boolean isSection(String name) { //проверка, является ли переменная секцией (по названию)
		boolean result = false;
		if (name == null) {log.warn("ConfigLoader isSection: null name"); return false;}
		if (get && file != null) {
			if (getIndexSection(name) > -1) result = true;
		} else log.warn("ConfigLoader isSection(name): failed check " + name + ", config not loaded or file[i] == null");
		return result;
	}
	
	/**
	 * получить названия переменных в данной секции (не включает названия секций)
	 * @param name имя секции
	 * @return названия переменных в виде массива строк (null, если ничего не найдено)
	 */
	public String[] getSectionVars(String name) { //получение названий переменных секции
		String[] result = null;
		if (name == null) {log.warn("ConfigLoader getSectionVars: null name"); return result;}
		if (get && file != null) {
			int index = getIndexSection(name);
			if (index == -1) return result; //страховка
			if (isSection(index)) {
				int p = getProbels(file[index])+1;
				ArrayList<String> list = new ArrayList<String>();
				for (int i = (index+1); i < file.length; i++) {
					ClearResult r = clearStr(file[i]);
					if (r.empty || r.fullstr || r.name == null) continue; //пропуск не нужного
					if (r.colon > -1 && getProbels(file[i]) < p) break; //выход из цикла, конец секции
					if (isSection(i)) {
						i += getSectionRealLength(i)-1; //пропуск секций
						continue;
					}
					if (r.colon > -1 && isSet(i)) list.add(r.name);
				}
				result = list.toArray(new String[list.size()]);
			}
		} else log.warn("ConfigLoader isSection: failed check " + name + ", config not loaded or file[i] == null");
		return result;
	}
	
	private int getSectionRealLength(int index) { //получение реальной длинны секции в конфиге (кол-во строк) (по индексу)
		int result = -1;
		if (index < 0) {log.warn("ConfigLoader getSectionRealLength(index): failed, index < 0"); return result;}
		if (index >= file.length) {log.warn("ConfigLoader getSectionRealLength(index): failed, index > file.length"); return result;}
		if (get && file != null) {
			if (index == -1) return result; //страховка
			if (isSection(index)) {
				int p = getProbels(file[index])+1;
				for (int i = (index+1); i < file.length; i++) {
					ClearResult r = clearStr(file[i]);
					if (r.empty || r.fullstr) continue; //пропуск не нужного
					if (p > getProbels(file[i])) { //выход из цикла и сохранение результата
						result = (i-1)-(index-1);
						break;
					}
					if ((i+1) == file.length) result = i-(index-1); //если цикл дошел до конца массива
				}
			}
		} else log.warn("ConfigLoader getSectionRealLength(index): failed get " + index + ", config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить длинну секции (количество строк в секции, строка с названием секции входит в число)
	 * @param name имя секции
	 * @return количество строк в секции в виде int (-1, если секция не найдена либо пуста)
	 */
	public int getSectionRealLength(String name) { //получение реальной длинны секции в конфиге (кол-во строк) (по названию)
		if (name == null) {log.warn("ConfigLoader getSectionRealLength: null name"); return -1;}
		return getSectionRealLength(getIndexSection(name));
	}
	
	/**
	 * получить названия секций из всего конфига
	 * @return названия секций в виде массива строк (null, если ничего не найдено)
	 */
	public String[] getSectionNames() { //получение названий секций во всем конфиге
		String[] result = null;
		if (get && file != null) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < file.length; i++) {
				ClearResult r = clearStr(file[i]);
				if (r.empty || r.fullstr) continue; //пропуск лишнего
				if (r.broken && isSection(i)) {
					list.add(r.name);
					i += getSectionRealLength(i)-1; //пропуск содержимого секций
				}
			}
			result = list.toArray(new String[list.size()]);
		} else log.warn("ConfigLoader getSectionNames: failed get sections names, config not loaded or file == null");
		return result;
	}
	
	/**
	 * получение названия секций из секции
	 * @param name имя секции
	 * @return названия секций в виде массива строк (null, если ничего не найдено)
	 */
	public String[] getSectionNames(String name) { //получение названий секций в секции
		String[] result = null;
		if (name == null) {log.warn("ConfigLoader getSectionNames: null name"); return result;}
		if (get && file != null) {
			int index = getIndexSection(name);
			int p = getProbels(file[index])+1;
			ArrayList<String> list = new ArrayList<String>();
			for (int i = (index+1); i < file.length; i++) {
				ClearResult r = clearStr(file[i]);
				if (r.empty || r.fullstr) continue; //пропуск не нужного
				if (p > getProbels(file[i])) break; //выход из цикла
				if (isSection(i)) {
					list.add(r.name);
					i += getSectionRealLength(i)-1; //пропуск содержимого секций
				}
			}
			result = list.toArray(new String[list.size()]);
		} else log.warn("ConfigLoader getSectionNames: failed get " + name + ", config not loaded or file == null");
		return result;
	}
	
	private int getIndexInSection(String section, String name) { //получение индекса переменной в секции
		int result = -1;
		if (name == null || section == null) {log.warn("ConfigLoader getIndexInSection: null name or null section"); return result;}
		if (get && file != null) {
			int index = getIndexSection(section);
			if (index > -1 && isSection(index)) {
				int p = getProbels(file[index])+1;
				for (int i = (index+1); i < file.length; i++) {
					ClearResult r = clearStr(file[i]);
					if (r.empty || r.fullstr || r.colon == -1) continue;
					if (p > getProbels(file[i])) break;
					if (r.name != null && r.name.equals(name)) {
						result = i;
						break;
					}
				}
			}
		} else log.warn("ConfigLoader getIndexInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
		return result;
	}
	
	/**
	 * проверить, есть ли какое-либо значение у переменной (аналог {@link ru.intervi.littleconfig.ConfigLoader#isSet(String)} для секций)
	 * @param section имя секции
	 * @param name имя переменной
	 * @return true если да; false если нет
	 */
	public boolean isSetInSection(String section, String name) { //проверка, установлена ли переменная в сеции
		boolean result = false;
		if (name == null || section == null) {log.warn("ConfigLoader isSetInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = isSet(getIndexInSection(section, name));
		} else log.warn("ConfigLoader isSetInSection: failed check " + name + " in " + section + ", config not loaded or file == null");
		return result;
	}
	
	/**
	 * проверка, есть ли какое-либо значение у массива (аналог {@link ru.intervi.littleconfig.ConfigLoader#isSetArray(String)} для секций)
	 * @param section имя секции
	 * @param name имя переменной
	 * @return true если да; false если нет
	 */
	public boolean isSetArrayInSection(String section, String name) { //проверка, установлен ли массив в секции
		boolean result = false;
		if (name == null || section == null) {log.warn("ConfigLoader isSetArrayInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = isSetArray(getIndexInSection(section, name));
		} else log.warn("ConfigLoader isSetArrayInSection: failed check " + name + " in " + section + ", config not loaded or file == null");
		return result;
	}
	
	private ConfigLoader getSection(int index) { //получить лоадер с нужной секцией
		if (index < 0) {log.warn("ConfigLoader getSection: index < 0"); return null;}
		if (!get || file == null) {log.warn("ConfigLoader getSection(int index): failed, config not loaded or file == null"); return null;}
		int l = getSectionRealLength(index);
		ConfigLoader result = new ConfigLoader();
		if (l > 0) {
			String sec[] = new String[l-1];
			for (int i = index+1; i < (index+l) && i < file.length; i++) sec[(i-index-1)] = file[i];
			try {
				result.fakeLoad(sec);
				result.tsc = true;
			} catch(Exception e) {log.error(e);}
		}
		return result;
	}
	
	/**
	 * получить ConfigLoader с секцией для работы с ней
	 * @param name имя секции
	 * @return ConfigLoader с загруженной секцией либо null в случае ошибки, либо пустой класс (необходимо проверять через {@link ru.intervi.littleconfig.ConfigLoader#isLoad()})
	 */
	public ConfigLoader getSection(String name) {
		if (name == null) {log.warn("ConfigLoader getSection: null name"); return null;}
		if (!get || file == null) {log.warn("ConfigLoader getSection: config not set"); return null;}
		int index = getIndexSection(name);
		ConfigLoader loader = null;
		if (index >= 0) loader = getSection(index);
		else log.warn("ConfigLoader getSection: " + name + " failed, index < 0");
		return loader;
	}
	
	/**
	 * получить ConfigLoader с секцией из секции
	 * @param section название секции
	 * @param name имя нужной вложенной секции
	 * @return ConfigLoader с загруженной секцикй либо null в случае ошибки, либо пустой класс (необходимо проверять через {@link ru.intervi.littleconfig.ConfigLoader#isLoad()})
	 */
	public ConfigLoader getSectionInSection(String section, String name) {
		if (name == null || section == null) {log.warn("ConfigLoader getSectionInSection: null name or null section"); return null;}
		if (!get || file == null) {log.warn("ConfigLoader getSectionInSection: config not set"); return null;}
		int index = getIndexInSection(section, name);
		ConfigLoader result = null;
		if (index >= 0) {
			if (isSection(index)) result = getSection(index);
		} else log.warn("ConfigLoader getSectionInSection: " + name + " in " + section + " failed, index < 0");
		return result;
	}
	
	/**
	 * заменить секцию
	 * @param section имя секции на замену
	 * @param newsection новая секция в виде ConfigLoader, полученный спец. методом (например {@link ru.intervi.littleconfig.ConfigLoader#getSection(String)})
	 * @return true если замена удалась; false если нет
	 */
	public boolean replaceSection(String section, ConfigLoader newsection) {
		boolean result = false;
		if (section == null || newsection == null) {log.warn("ConfigLoader replaceSection: null name of section or null newsection"); return result;}
		if (!newsection.thisIsSection()) {log.warn("ConfigLoader replaceSection: newsection is not a section"); return result;}
		if (!get || file == null) {log.warn("ConfigLoader replaceSection: failed, config not loaded or file == null"); return result;}
		int index = getIndexSection(section);
		if (index >= 0) {
			int leng = getSectionRealLength(index);
			ArrayList<String> newfile = new ArrayList<String>();
			for (int i = 0; i < index; i++) newfile.add(file[i]);
			String paste[] = newsection.getAll();
			for (int i = 0; i < paste.length; i++) newfile.add(paste[i]);
			for (int i = (index+leng); i < file.length; i++) newfile.add(file[i]);
			file = newfile.toArray(new String[newfile.size()]);
			result = true;
		} else log.warn("ConfigLoader replaceSection: failed, index < 0");
		return result;
	}
	
	/**
	 * узнать тип переменной в секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return тип переменной в виде {@link ru.intervi.littleconfig.ConfigLoader.TypeValue}
	 */
	public TypeValue getTypeInSection(String section, String name) {
		TypeValue result = TypeValue.NULL;
		if (name == null || section == null) {log.warn("ConfigLoader getTypeInSection: null name or null section"); return result;}
		if (get && file != null) {
			int index = getIndexInSection(section, name);
			if (index > -1) {
				ClearResult r = clearStr(file[index]);
				if (!r.broken) result = getTypeData(r.content);
				else if (isArray(index).array) result = getTypeData(getStringArray(index));
				else if (isSection(index)) result = TypeValue.SECTION;
			}
		} else log.warn("ConfigLoader getTypeInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
		return result;
	}
	
	/**
	 * ======================================================================================
	 * ======================================================================================
	 * ---------------ЧТЕНИЕ ДАННЫХ ИЗ СЕКЦИЙ
	 * ======================================================================================
	 */
	/**
	 * получить значение переменной в виде строки из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде строки (null, если переменная не найдена)
	 */
	public String getStringInSection(String section, String name) { //получение переменной типа String из секции
		String result = null;
		if (name == null || section == null) {log.warn("ConfigLoader getStringInSection: null name or null section"); return result;}
		if (get && file != null) {
			int index = getIndexInSection(section, name);
			if (index == -1) log.warn("ConfigLoader getStringInSection: " + name + " in " + section + " error, not found");
			else result = getString(index);
		} else log.warn("ConfigLoader getStringInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getByteInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getByte(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getByteInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getShortInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getShort(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getShortInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getIntInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getInt(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getIntInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getLongInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getLong(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getLongInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде float из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде float (-1, если переменная не найдена)
	 */
	public float getFloatInSection(String section, String name) {
		float result = -1;
		if (name == null || section == null) {log.warn("ConfigLoader getFloatInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getFloat(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getFloatInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getDoubleInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getDouble(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getDoubleInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getBooleanInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getBoolean(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getBooleanInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getStringArrayInSection: null name or null section"); return result;}
		if (get && file != null) {
			int index = getIndexInSection(section, name);
			if (index == -1) log.warn("ConfigLoader getStringArrayInSection: " + name + " in " + section + " error, not found");
			else result = getStringArray(index);
		} else log.warn("ConfigLoader getStringArrayInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getByteArrayInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getByteArray(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getByteArrayInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getShortArrayInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getShortArray(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getShortArrayInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getIntArrayInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getIntArray(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getIntArrayInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getLongArrayInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getLongArray(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getLongArrayInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
		return result;
	}
	
	/**
	 * получить значение переменной в виде массива float из секции
	 * @param section имя секции
	 * @param name имя переменной
	 * @return значение переменной в виде массива float (null, если переменная не найдена)
	 */
	public float[] getFloatArrayInSection(String section, String name) {
		float[] result = null;
		if (name == null || section == null) {log.warn("ConfigLoader getFloatArrayInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getFloatArray(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getFloatArrayInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getDoubleArrayInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getDoubleArray(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getDoubleArrayInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
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
		if (name == null || section == null) {log.warn("ConfigLoader getBooleanArrayInSection: null name or null section"); return result;}
		if (get && file != null) {
			result = getBooleanArray(getIndexInSection(section, name));
		} else log.warn("ConfigLoader getBooleanArrayInSection: failed get " + name + " in " + section + ", config not loaded or file == null");
		return result;
	}
	
	/**
	 * класс со внутренними методами для работы с конфигом
	 */
	public class LoaderMethods { //класс со внутренними методами
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
			if (!get || file == null) return;
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < file.length; i++) {
				if (file[i] != null) {
					ClearResult r = clearStr(file[i]);
					if (r.fullstr || r.empty) continue;
					if (r.cleaned != null) list.add(r.cleaned);
				}
			}
			file = list.toArray(new String[list.size()]);
		}
		/**
		 * узнать реальную длинну многострочного массива в конфиге, включая строку с названием
		 * @param index индекс массива
		 * @return количество строк от первого элемента до последнего (-1 при бракованном массиве)
		 */
		public int getArrayRealLength(int index) { //узнать реальную длинну массива
			int result = -1;
			if (index < 0) {log.warn("LoaderMethods getArrayRealLength(index): failed, index < 0"); return result;}
			if (index >= file.length) {log.warn("LoaderMethods getArrayRealLength(index): failed, index > file.length"); return result;}
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
							int rl = getOptionRealLength(i);
							if (rl > 1) {
								result += rl-1;
							}
							break;
						}
					}
				}
			} else log.warn("LoaderMethods getArrayRealLength: index " + String.valueOf(index) + " not array");
			return result;
		}
		/**
		 * получить ConfigLoader с секцией (см. {@link ru.intervi.littleconfig.ConfigLoader#getSection(String)})
		 * @param index индекс секции
		 * @return см. {@link ru.intervi.littleconfig.ConfigLoader#getSection(String)}
		 */
		public ConfigLoader recSection(int index) {
			return getSection(index);
		}
		/**
		 * получить тип данных в строке с помощью внутреннего метода getTypeData(String)
		 * @param str строка
		 * @return тип данных в виде {@link ru.intervi.littleconfig.ConfigLoader.TypeValue}
		 */
		public TypeValue recTypeData(String str) {return getTypeData(str);}
		/**
		 * получить тип данных в массиве строк с помощью внутреннего метода getTypeData(String[])
		 * @param value массив строк
		 * @return тип данных в виде {@link ru.intervi.littleconfig.ConfigLoader.TypeValue}
		 */
		public TypeValue recTypeData(String value[]) {return getTypeData(value);}
		/**
		 * получить все строки как одну (до первого параметра)
		 * @param index индекс в массиве
		 * @return все строки одной строкой (применяется при чтении значения опций)
		 */
		public String recFullStr(int index) {
			return getFullStr(index);
		}
		/**
		 * узнать реальную длинну опции или элемента массива в конфиге
		 * @param index индекс опции или элемента массива в конфиге
		 * @return -1 в случае ошибки; 0 если опция пуста; количество строк, которое занимает значение опции или элемента массива в конфиге
		 */
		public int getOptionRealLength(int index) {
			if (index < 0) {log.warn("LoaderMethods getOptionRealLength: index < 0"); return -1;}
			if (!get || file == null) {log.warn("LoaderMethods getOptionRealLength: config not set"); return -1;}
			int result = 0;
			ClearResult cr = clearStr(file[index]);
			if (!cr.broken) result++;
			else if (cr.hypindex != -1 && cr.content != null) result++;
			int p = getProbels(file[index]);
			for (int i = (index+1); i < file.length; i++) { //поиск остальных частей
				ClearResult r = clearStr(file[i]);
				if (r.empty || r.origin == null || r.fullstr) continue; //выход при попадании на опцию
				if (r.colon != -1 || r.hypindex != -1) break;
				if (getProbels(file[i]) < p) break;
				result++;
				if ((cr.firstquote != -1 && cr.lastquote == -1) || cr.exquote != -1) {
					//если испольховались разбросанные кавычки - надо найти закрывающую
					char c[] = r.origin.toCharArray();
					char fq = '\u0000';
					switch(c[0]) { //определение первой кавычки в строке
					case '"':
						fq = '"';
						break;
					case '\'':
						fq = '\'';
						break;
					default:
						char cc = '\u0000';
						if (cr.exquote != -1) cc = cr.origin.charAt(cr.exquote);
						else cc = cr.origin.charAt(cr.firstquote);
						for (int n = 1; n < c.length; n++) {
							if (c[n] == cc) i = file.length;
						}
					}
					boolean f = false;
					if (fq == '\u0000') f = true;
					for (int n = 0; n < c.length; n++) {
						//поиск закрывающих кавычек
						if (fq != '\u0000' && c[n] == fq) f = true; //найдена вторая кавычка
						if (f) {
							for (int k = (n+1); k < c.length; k++) { //проверка, точно ли кавычка закрывающая
								switch(c[k]) {
								case ' ':
									continue;
								case '#':
									continue;
								case '"':
									if (cr.origin.charAt(cr.firstquote) == '"') {
										i = file.length;
										n = c.length;
										k = c.length;
										break;
									} else k = c.length;
								case '\'':
									if (cr.origin.charAt(cr.firstquote) == '\'') {
										i = file.length;
										n = c.length;
										k = c.length;
										break;
									} else k = c.length;
								default:
									k = c.length;
								}
							}
						} else if ((n+1) == c.length) { //повтор цикла, если закрывающая кавычка строки не найдена
							f = true;
							n = 0;
						}
					}
				}
			}
			return result;
		}
		/**
		 * получить реальную длинну секции
		 * @param index индекс секции в конфиге
		 * @return количество строк в секции (-1 в случае ошибки)
		 */
		public int recSectionRealLength(int index) {
			return getSectionRealLength(index);
		}
	}
	/**
	 * получить LoaderMethods
	 */
	public LoaderMethods getMethods() {
		return new LoaderMethods();
	}
}