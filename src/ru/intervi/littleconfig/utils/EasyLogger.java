package ru.intervi.littleconfig.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.lang.StackTraceElement;
import java.util.ArrayList;
import java.util.Iterator;

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
	 * @param error префикс сообщения об ошибке
	 */
	public EasyLogger(String prefix, String info, String warn, String error) {
		this.prefix = prefix;
		this.info = info;
		this.warn = warn;
		this.error = error;
	}
	/**
	 * установить префиксы и дату
	 * @param prefix префикс с названием
	 * @param info префикс информационного сообщения
	 * @param warn префикс предупредительного сообщения
	 * @param error префикс сообщения об ошибке
	 * @param date формат вывода даты
	 */
	public EasyLogger(String prefix, String info, String warn, String error, String date) {
		this.prefix = prefix;
		this.info = info;
		this.warn = warn;
		this.error = error;
		d = new SimpleDateFormat(date);
	}
	/**
	 * установить префиксы, дату и файл лога (вывод в файл будет включен)
	 * @param prefix префикс с названием
	 * @param info префикс информационного сообщения
	 * @param warn префикс предупредительного сообщения
	 * @param error префикс сообщения об ошибке
	 * @param date формат вывода даты
	 * @param log файл лога
	 */
	public EasyLogger(String prefix, String info, String warn, String error, String date, File log) {
		this.prefix = prefix;
		this.info = info;
		this.warn = warn;
		this.error = error;
		d = new SimpleDateFormat(date);
		ToFile.setFile(log);
		ToFile.onLog();
	}
	/**
	 * установить префиксы и файл лога (вывод в файл будет включен)
	 * @param prefix префикс с названием
	 * @param info префикс информационного сообщения
	 * @param warn префикс предупредительного сообщения
	 * @param error префикс сообщения об ошибке
	 * @param log файл лога
	 */
	public EasyLogger(String prefix, String info, String warn, String error, File log) {
		this.prefix = prefix;
		this.info = info;
		this.warn = warn;
		this.error = error;
		ToFile.setFile(log);
		ToFile.onLog();
	}
	/**
	 * установить префикс с названием и файл лога (вывод в файл будет включен)
	 * @param prefix префикс с названием
	 * @param log файл лога
	 */
	public EasyLogger(String prefix, File log) {
		this.prefix = prefix;
		ToFile.setFile(log);
		ToFile.onLog();
	}
	
	/**
	 * установить файл лога (вывод в файл будет включен)
	 * @param log файл лога
	 */
	public EasyLogger(File log) {
		ToFile.setFile(log);
		ToFile.onLog();
	}
	
	private boolean send = true; //общий вывод в консоль
	private String prefix = "[littleconfig]";
	private SimpleDateFormat d = new SimpleDateFormat("YYYY-MM-dd/HH:mm:ss");
	private String info = "[INFO]";
	private String warn = "[WARN]";
	private String error = "[ERROR]";
	private boolean sinfo = true; //вывод инфо сообщений
	private boolean swarn = true; //вывод предупреждений
	private boolean serror = true; //вывод ошибок
	
	/**
	 * вывод инофрмационного сообщения в консоль и в лог
	 * @param text сообщение
	 */
	public void info(String text) {
		if (text == null) return;
		String mess = info + ' ' + prefix + " [" + d.format(new Date()) + "] " + text;
		if (send & sinfo) System.out.println(mess);
		ToFile.log(mess, TypeMess.INFO);
		MemoryLog.put(mess, TypeMess.INFO);
	}
	
	/**
	 * вывод инофрмационных сообщений в консоль и в лог
	 * @param lines массив строк для вывода
	 */
	public void info(String lines[]) {
		if (lines == null) return;
		for (int i = 0; i < lines.length; i++) info(lines[i]);
	}
	
	/**
	 * вывод предупредительного сообщения в консоль и в лог
	 * @param text сообщение
	 */
	public void warn(String text) {
		if (text == null) return;
		String mess = warn + ' ' + prefix + " [" + d.format(new Date()) + "] " + text;
		if (send & swarn) System.out.println(mess);
		ToFile.log(mess, TypeMess.WARN);
		MemoryLog.put(mess, TypeMess.WARN);
	}
	
	/**
	 * вывод предупредительных сообщений в консоль и в лог
	 * @param lines массив строк для вывода
	 */
	public void warn(String lines[]) {
		if (lines == null) return;
		for (int i = 0; i < lines.length; i++) warn(lines[i]);
	}
	
	/**
	 * вывод сообщения об ошибке в консоль и в лог
	 * @param text сообщение
	 */
	public void error(String text) {
		if (text == null) return;
		String mess = error + ' ' + prefix + " [" + d.format(new Date()) + "] " + text;
		if (send & serror) System.out.println(mess);
		ToFile.log(mess, TypeMess.ERROR);
		MemoryLog.put(mess, TypeMess.ERROR);
	}
	
	/**
	 * вывод сообщений об ошибках в консоль и в лог
	 * @param lines массив строк для вывода
	 */
	public void error(String lines[]) {
		if (lines == null) return;
		for (int i = 0; i < lines.length; i++) error(lines[i]);
	}
	
	/**
	 * вывод исключения в консоль и в лог
	 * @param e исключение
	 */
	public void error(Exception e) {
		if (error == null) return;
		ArrayList<String> mess = new ArrayList<String>();
		mess.add(e.toString());
		StackTraceElement[] el = e.fillInStackTrace().getStackTrace();
		for (int i = 0; i < el.length; i++) {
			mess.add(("   " + el[i].toString()));
		}
		Iterator<String> iter = mess.iterator();
		while(iter.hasNext()) {
			String str = iter.next();
			error(str);
			MemoryLog.put(str, TypeMess.ERROR);
		}
	}
	
	/**
	 * включить вывод всех сообщений в консоль
	 */
	public void onLog() {send = true;} //включить вывод
	/**
	 * выключить вывод всех сообщений в консоль
	 */
	public void offLog() {send = false;} //выключить вывод
	/**
	 * включить вывод информационных сообщений в консоль
	 */
	public void onInfo() {sinfo = true;}
	/**
	 * выключить вывод информационных сообщений в консоль
	 */
	public void offInfo() {sinfo = false;}
	/**
	 * включить вывод предупредительных сообщений в консоль
	 */
	public void onWarn() {swarn = true;}
	/**
	 * выключить вывод предупредительных сообщений в консоль
	 */
	public void offWarn() {swarn = false;}
	/**
	 * включить вывод сообщений об ошибках в консоль
	 */
	public void onError() {serror = true;}
	/**
	 * выключить вывод сообщений об ошибках в консоль
	 */
	public void offError() {serror = false;}
	/**
	 * получить статус вывода информационных сообщений в консоль
	 * @return true - вывод включен; false - вывод выключен
	 */
	public final boolean getInfoStatus() {return sinfo;}
	/**
	 * получить статус вывода предупредительных сообщений в консоль
	 * @return true - вывод включен; false - вывод выключен
	 */
	public final boolean getWarnStatus() {return swarn;}
	/**
	 * получить статус вывода сообщений об ошибках в консоль
	 * @return true - вывод включен; false - вывод выключен
	 */
	public final boolean getErrorStatus() {return serror;}
	/**
	 * получить статус вывода всех сообщений в консоль
	 * @return true - вывод включен; false - вывод выключен
	 */
	public final boolean getLogStatus() {return send;}
	
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
	 * получить префикс сообщения об ошибке, стандартный: "[ERROR]"
	 * @return префикс сообщения об ошибке
	 */
	public final String getError() {return error;}
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
	 * установить новый префикс сообщений об ошибке
	 * @param str новый префикс сообщений об ошибке
	 */
	public void setError(String str) {error = str;}
	
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
		
		private boolean fsend = false; //отправка всех сообщений
		private boolean isend = true; //информационных
		private boolean wsend = true; //предупредительных
		private boolean esend = true; //с ошибкой
		
		/**
		 * получить статус вывода всех сообщений в лог
		 * @return true - вывод включен; false - вывод выключен
		 */
		public final boolean getLog() {return fsend;}
		/**
		 * включить вывод всех сообщений в лог
		 * @return true - вывод был включен; false - вывод не был включен
		 */
		public boolean onLog() {
			if (file != null) {
				fsend = true;
				return true;
			} else return false;
		}
		/**
		 * выключить вывод всех сообщений в лог
		 */
		public void offLog() {fsend = false;}
		/**
		 * включить вывод информационных сообщений в лог
		 */
		public void onInfo() {isend = true;}
		/**
		 * выключить вывод информационных сообщений в лог
		 */
		public void offInfo() {isend = false;}
		/**
		 * включить вывод предупредительных сообщений в лог
		 */
		public void onWarn() {wsend = true;}
		/**
		 * выключить вывод предупредительных сообщений в лог
		 */
		public void offWarn() {wsend = false;}
		/**
		 * включить вывод сообщений об ошибках в лог
		 */
		public void onError() {esend = true;}
		/**
		 * выключить вывод сообщений об ошибках в лог
		 */
		public void offError() {esend = false;}
		/**
		 * получить статус вывода информационных сообщений в лог
		 * @return true - вывод включен; false - вывод выключен
		 */
		public final boolean getInfoStatus() {return isend;}
		/**
		 * получить статус вывода предупредительных сообщений в лог
		 * @return true - вывод включен; false - вывод выключен
		 */
		public final boolean getWarnStatus() {return wsend;}
		/**
		 * получить статус вывода сообщений об ошибках в лог
		 * @return true - вывод включен; false - вывод выключен
		 */
		public final boolean getErrorStatus() {return esend;}
		
		/**
		 * вывести сообщение в лог (вызывается автоматически методами info и warn)
		 * @param str строка для записи
		 * @param type тип сообщения
		 */
		public void log(String str, TypeMess type) {
			switch(type) { //выход их метода, если отправка данного типа выключена
			case INFO:
				if (!isend) return;
				break;
			case WARN:
				if (!wsend) return;
				break;
			case ERROR:
				if (!esend) return;
			}
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
	
	/**
	 * тип отправляемого в лог сообщения
	 */
	public enum TypeMess { //тип отправляемого сообщения
		/**
		 * информационное сообщение
		 */
		INFO, //информационное сообщение
		/**
		 * предупредительное
		 */
		WARN, //предупредительное
		/**
		 * об ошибке
		 */
		ERROR //с ошибкой
	}
	
	/**
	 * класс с методами для работы с виртуальным логом в оперативной памяти
	 */
	public class MemoryLog {
		private ArrayList<MLData> list = new ArrayList<MLData>();
		private int limit = 100; //лимит памяти
		private boolean pos = false; //вести ли виртуальный лог
		private boolean isend = true; //запись инфо сообщений
		private boolean wsend = true; //предупредительных
		private boolean esend = true; //с ошибками
		
		private void put(String str, TypeMess type) { //записать сообщение в лог
			if (!pos) return;
			if (str == null) return;
			switch(type) { //выход их метода, если отправка данного типа выключена
			case INFO:
				if (!isend) return;
				break;
			case WARN:
				if (!wsend) return;
				break;
			case ERROR:
				if (!esend) return;
			}
			MLData mld = new MLData();
			mld.type = type;
			mld.mess = str;
			mld.date = new Date();
			if (list.size() < limit) list.add(mld); else {
				list.remove(0);
				list.add(mld);
			}
		}
		
		/**
		 * установить лимит памяти виртуального лога
		 * @param limit лимит строк
		 */
		public void setLimit(int limit) {this.limit = limit;}
		/**
		 * включить ведение виртуального лога
		 */
		public void turnOnPosition() {pos = true;}
		/**
		 * выключить ведение виртуального лога
		 */
		public void turnOffPosition() {pos = false;}
		/**
		 * включить вывод информационных сообщений в виртуальный лог
		 */
		public void onInfo() {isend = true;}
		/**
		 * выключить вывод информационных сообщений в виртуальный лог
		 */
		public void offInfo() {isend = false;}
		/**
		 * включить вывод предупредительных сообщений в виртуальный лог
		 */
		public void onWarn() {wsend = true;}
		/**
		 * выключить вывод предупредительных сообщений в виртуальный лог
		 */
		public void offWarn() {wsend = false;}
		/**
		 * включить вывод сообщений об ошибках в виртуальный лог
		 */
		public void onError() {esend = true;}
		/**
		 * выключить вывод сообщений об ошибках в виртуальный лог
		 */
		public void offError() {esend = false;}
		/**
		 * получить статус вывода информационных сообщений в виртуальный лог
		 * @return true - вывод включен; false - вывод выключен
		 */
		public final boolean getInfoStatus() {return isend;}
		/**
		 * получить статус вывода предупредительных сообщений в виртуальный лог
		 * @return true - вывод включен; false - вывод выключен
		 */
		public final boolean getWarnStatus() {return wsend;}
		/**
		 * получить статус вывода сообщений об ошибках в виртуальный лог
		 * @return true - вывод включен; false - вывод выключен
		 */
		public final boolean getErrorStatus() {return esend;}
		
		/**
		 * получить лимит памяти (по-умолчанию: 100)
		 * @return максимальное количество строк в виртуальном логе
		 */
		public final int getLimit() {return limit;}
		/**
		 * получить статус виртуального лога (по-умолчанию: false)
		 * @return true - логирование включено; false - выключено
		 */
		public final boolean getPosition() {return pos;}
		
		/**
		 * получить весь виртуальный лог
		 * @return виртуальный лог в виде списка данных
		 */
		public final ArrayList<MLData> getAllLog() {return list;}
		/**
		 * получить весь виртуальный лог в виде массива строк
		 * @return виртуальный лог в виде массива строк
		 */
		public String[] getAllLogArray() {
			if (list.isEmpty()) return null;
			String result[] = new String[list.size()];
			for (int i = 0; i < list.size(); i++) result[i] = list.get(i).mess;
			return result;
		}
		/**
		 * получить сообщения из виртуального лога согласно типу
		 * @param type тип сообщений
		 * @return список данных, согласно типу
		 */
		public ArrayList<MLData> getLog(TypeMess type) {
			ArrayList<MLData> result = new ArrayList<MLData>();
			Iterator<MLData> iter = list.iterator();
			while(iter.hasNext()) {
				MLData mld = iter.next();
				if (mld.type.equals(type)) result.add(mld);
			}
			return result;
		}
		/**
		 * получить сообщения из виртуального лога согласно типу, в виде массива строк
		 * @param type тип сообщений
		 * @return массив строк, согласно типу
		 */
		public String[] getLogArray(TypeMess type) {
			if (list.isEmpty()) return null;
			ArrayList<String> result = new ArrayList<String>();
			Iterator<MLData> iter = list.iterator();
			while(iter.hasNext()) {
				MLData mld = iter.next();
				if (mld.type.equals(type)) result.add(mld.mess);
			}
			return result.toArray(new String[result.size()]);
		}
		
		/**
		 * очистить виртуальный лог
		 */
		public void clear() {list.clear();}
		/**
		 * получить весь класс
		 * @return new MemoryLog()
		 */
		public MemoryLog get() {return new MemoryLog();}
		
		/**
		 * класс с данными элемента виртуального лога
		 */
		public class MLData {
			/**
			 * тип сообщения
			 */
			public TypeMess type;
			/**
			 * сообщение
			 */
			public String mess;
			/**
			 * дата отправки
			 */
			public Date date;
		}
	}
	
	/**
	 * инициализированный объект MemoryLog
	 */
	public MemoryLog MemoryLog = new MemoryLog();
}