package ru.intervi.littleconfig.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

/**
 * простой логгер для вывода сообщений в консоль и записи в файл
 */
public class EasyLogger { //класс для вывода сообщений в консоль
	/**
	 * простой конструктор без заполнения данных
	 */
	public EasyLogger() {}
	/**
	 * установка префикса с названием
	 * @param prefix новый префикс
	 */
	public EasyLogger(String prefix) {
		this.prefix = prefix;
	}
	/**
	 * установить префиксы
	 * @param prefix префикс с названием
	 * @param info префикс информационного сообщения
	 * @param warn префикс предупредительного сообщения
	 */
	public EasyLogger(String prefix, String info, String warn) {
		this.prefix = prefix;
		this.info = info;
		this.warn = warn;
	}
	/**
	 * установить префиксы и дату
	 * @param prefix префикс с названием
	 * @param info префикс информационного сообщения
	 * @param warn префикс предупредительного сообщения
	 * @param date формат вывода даты
	 */
	public EasyLogger(String prefix, String info, String warn, String date) {
		this.prefix = prefix;
		this.info = info;
		this.warn = warn;
		d = new SimpleDateFormat(date);
	}
	/**
	 * установить префиксы, дату и файл лога (вывод в файл будет включен)
	 * @param prefix префикс с названием
	 * @param info префикс информационного сообщения
	 * @param warn префикс предупредительного сообщения
	 * @param date формат вывода даты
	 * @param log файл лога
	 */
	public EasyLogger(String prefix, String info, String warn, String date, File log) {
		this.prefix = prefix;
		this.info = info;
		this.warn = warn;
		d = new SimpleDateFormat(date);
		ToFile.setFile(log);
		ToFile.onLog();
	}
	/**
	 * установить префиксы и файл лога (вывод в файл будет включен)
	 * @param prefix префикс с названием
	 * @param info префикс информационного сообщения
	 * @param warn префикс предупредительного сообщения
	 * @param log файл лога
	 */
	public EasyLogger(String prefix, String info, String warn, File log) {
		this.prefix = prefix;
		this.info = info;
		this.warn = warn;
		ToFile.setFile(log);
		ToFile.onLog();
	}
	/**
	 * установить префикс с названием и файл лога
	 * @param prefix префикс с названием
	 * @param log файл лога
	 */
	public EasyLogger(String prefix, File log) {
		this.prefix = prefix;
		ToFile.setFile(log);
		ToFile.onLog();
	}
	
	private boolean send = true;
	private String prefix = "[littleconfig]";
	private SimpleDateFormat d = new SimpleDateFormat("YYYY-MM-dd/HH:mm:ss");
	private String info = "[INFO]";
	private String warn = "[WARN]";
	
	/**
	 * вывод инофрмационного сообщения в консоль и в лог
	 * @param text сообщение
	 */
	public void info(String text) {
		if (text == null) return;
		String mess = info + ' ' + prefix + " [" + d.format(new Date()) + "] " + text;
		if (send) System.out.println(mess);
		ToFile.log(mess);
	}
	
	/**
	 * вывод инофрмационных сообщений в консоль и в лог
	 * @param lines массив строк для вывода
	 */
	public void info(String lines[]) {
		for (int i = 0; i < lines.length; i++) info(lines[i]);
	}
	
	/**
	 * вывод предупредительного сообщения в консоль и в лог
	 * @param text сообщение
	 */
	public void warn(String text) {
		if (text == null) return;
		String mess = warn + ' ' + prefix + " [" + d.format(new Date()) + "] " + text;
		if (send) System.out.println(mess);
		ToFile.log(mess);
	}
	
	/**
	 * вывод предупредительных сообщений в консоль и в лог
	 * @param lines массив строк для вывода
	 */
	public void warn(String lines[]) {
		for (int i = 0; i < lines.length; i++) info(lines[i]);
	}
	
	/**
	 * включить вывод сообщений в консоль
	 */
	public void onLog() {send = true;} //включить вывод
	/**
	 * выключить вывод сообщений в консоль
	 */
	public void offLog() {send = false;} //выключить вывод
	/**
	 * получить статус вывода сообщений в консоль
	 * @return true - вывод включен; false - вывод выключен
	 */
	public final boolean getLog() {return send;}
	
	/**
	 * получить префикс, стандартный: "[littleconfig]"
	 * @return префикс пред сообщениями
	 */
	public final String getPrefix() {return prefix;} //получить префикс
	/**
	 * установить указаный префикс
	 * @param p новый префикс
	 */
	public void setPrefix(String p) {prefix = p;} //поменять префикс
	
	/**
	 * получить форматирование даты, стандартное: "YYYY-MM-dd/HH:mm:ss"
	 * @return форматирование даты
	 */
	public final String getDateFormat() {return d.toPattern();} //получить форматирование даты
	/**
	 * установить указанное форматирование даты
	 * @param f новое форматирование
	 */
	public void setDateFormat(String f) {d = new SimpleDateFormat(f);} //изменить форматирование даты
	
	/**
	 * получить префикс информационного сообщения, стандартный: "[INFO]"
	 * @return информационный префикс
	 */
	public final String getInfo() {return info;}
	/**
	 * получить префикс предупредительного сообщения, стандартный: "[WARN]"
	 * @return предупредительный префикс
	 */
	public final String getWarn() {return warn;}
	/**
	 * установить новый информационный префикс
	 * @param str новый информационный префикс
	 */
	public void setInfo(String str) {info = str;}
	/**
	 * установить новый предупредительный префикс
	 * @param str новый предупредительный префикс
	 */
	public void setWarn(String str) {warn = str;}
	
	/**
	 * класс с методами для записи сообщений в файловый лог
	 */
	public class ToFile {
		private File file = null;
		
		/**
		 * установить файл лога для записи
		 * @param f объект File лога для записи
		 */
		public void setFile(File f) {file = f;}
		/**
		 * установить файл лога для записи
		 * @param f путь к файлу лога
		 */
		public void setFile(String f) {file = new File(f);}
		
		private boolean fsend = false;
		
		/**
		 * получить статус вывода сообщений в лог
		 * @return true - вывод включен; false - вывод выключен
		 */
		public final boolean getLog() {return fsend;}
		/**
		 * включить вывод сообщений в лог
		 * @return true - вывод был включен; false - вывод не был включен
		 */
		public boolean onLog() {
			if (file != null) {
				fsend = true;
				return true;
			} else return false;
		}
		/**
		 * выключить вывод сообщений в лог
		 */
		public void offLog() {fsend = false;}
		
		/**
		 * вывести сообщение в лог (вызывается автоматически методами info и warn)
		 * @param str строка для записи
		 */
		public void log(String str) {
			if (file != null && str != null && fsend) {
				BufferedWriter writer = null;
				try {
					writer = new BufferedWriter(new FileWriter(file, true));
					writer.write(str);
					writer.newLine();
				} catch(Exception e) {e.printStackTrace();} finally {
					if (writer != null) {
						try {
							writer.close();
						} catch(Exception e) {e.printStackTrace();}
					}
				}
			}
		}
		
		/**
		 * получить весь класс
		 * @return new ToFile()
		 */
		public ToFile getToFile() {return new ToFile();}
	}
	/**
	 * инициализированный объект ToFile
	 */
	public ToFile ToFile = new ToFile();
}