package ru.intervi.littleconfig.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * простой логгер для вывода в консоль
 */
public class EasyLogger { //класс для вывода сообщений в консоль
	private boolean send = true;
	private String prefix = "[littleconfig]";
	private SimpleDateFormat d = new SimpleDateFormat("YYYY-MM-DD/HH:mm:ss");
	
	/**
	 * вывод сообщения в консоль
	 * @param text сообщение
	 */
	public void info(String text) {
		if (send) System.out.println("[" + d.format(new Date()) + "] " + prefix + " " + text);
	}
	
	/**
	 * включить вывод
	 */
	public void onLog() {send = true;} //включить вывод
	/**
	 * выключить вывод (сообщения не будут выводится в консоль)
	 */
	public void offLog() {send = false;} //выключить вывод
	
	/**
	 * получить префикс, стандартный: "[littleconfig]"
	 * @return префикс пред сообщениями
	 */
	public String getPrefix() {return prefix;} //получить префикс
	/**
	 * установить указаный префикс
	 * @param p новый префикс
	 */
	public void setPrefix(String p) {prefix = p;} //поменять префикс
	
	/**
	 * получить форматирование даты, стандартное: "YYYY-MM-DD/HH:mm:ss"
	 * @return форматирование даты
	 */
	public String getDateFormat() {return d.toPattern();} //получить форматирование даты
	/**
	 * установить указанное форматирование даты
	 * @param f новое форматирование
	 */
	public void setDateFormat(String f) {d = new SimpleDateFormat(f);} //изменить форматирование даты
}
