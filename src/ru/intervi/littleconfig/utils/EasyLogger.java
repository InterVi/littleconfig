package ru.intervi.littleconfig.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EasyLogger { //класс для вывода сообщений в консоль
	private boolean send = true;
	private String prefix = "[littleconfig]";
	private SimpleDateFormat d = new SimpleDateFormat("YYYY-MM-DD/HH:mm:ss");
	
	public void info(String text) {
		if (send) System.out.println("[" + d.format(new Date()) + "] " + prefix + " " + text);
	}
	
	public void onLog() {send = true;} //включить вывод отладочной информации
	public void offLog() {send = false;} //выключить вывод отладочной информаци
	
	public EasyLogger getLogger() {return new EasyLogger();} //получить весь класс
	
	public String getPrefix() {return prefix;} //получить префикс
	public void setPrefix(String p) {prefix = p;} //поменять префикс
	
	public String getDateFormat() {return d.toPattern();} //получить форматирование даты
	public void setDateFormat(String f) {d = new SimpleDateFormat(f);} //изменить форматирование даты
}
