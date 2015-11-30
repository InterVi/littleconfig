package ru.intervi.littleconfig.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;

/**
 * простой логгер для вывода сообщений в консоль и записи в файл
 */
public class EasyLogger { //класс для вывода сообщений в консоль
	private boolean send = true;
	private String prefix = "[littleconfig]";
	private SimpleDateFormat d = new SimpleDateFormat("YYYY-MM-DD/HH:mm:ss");
	
	/**
	 * вывод инофрмационного сообщения в консоль и в лог
	 * @param text сообщение
	 */
	public void info(String text) {
		String mess = "[INFO] [" + d.format(new Date()) + "] " + prefix + ' ' + text;
		if (send) System.out.println(mess);
		ToFile.log(mess);
	}
	
	/**
	 * вывод предупредительного сообщения в консоль и в лог
	 * @param text сообщение
	 */
	public void warn(String text) {
		String mess = "[WARN] [" + d.format(new Date()) + "] " + prefix + ' ' + text;
		if (send) System.out.println(mess);
		ToFile.log(mess);
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
	 * получить форматирование даты, стандартное: "YYYY-MM-DD/HH:mm:ss"
	 * @return форматирование даты
	 */
	public final String getDateFormat() {return d.toPattern();} //получить форматирование даты
	/**
	 * установить указанное форматирование даты
	 * @param f новое форматирование
	 */
	public void setDateFormat(String f) {d = new SimpleDateFormat(f);} //изменить форматирование даты
	
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
				try {
					FileWriter writer = new FileWriter(file, true);
					writer.write((str + '\n'));
					writer.close();
				} catch(Exception e) {e.printStackTrace();}
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
