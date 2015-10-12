package ru.intervi.littleconfig.utils;

/**
 * разные полезные утилиты
 */
public class Utils { //разные полезные методы
	/**
	 * удаляет часть из строки
	 * @param s строка
	 * @param p1 первый индекс
	 * @param p2 второй индекс
	 * @return строка без части, находящейся между p1 и p2
	 */
	public static String remChars(String s, int p1, int p2) { //метод для вырезания символов из строк
		   String pp1, pp2, result;
		   if (p1 > -1 && p2 > p1 && p2 <= s.length()) {
			   	int pr = p2 - p1; pr = p1 + pr;
		   		if (p1 != 0) pp1 = s.substring(0, p1); else pp1 = s;
		   		if(p2 == s.length()) {if (p2 != pr) pp2 = s.substring(pr, p2); else pp2 = s;} else pp2 = s.substring(p2, s.length());
		   		if (pp1.equals(s)) result = pp2; else if (pp2.equals(s)) result = pp1; else result = pp1 + pp2;
		   	} else result = s;
		   return result;
	}
	
	/**
	 * удаляет из строки символ
	 * @param s строка
	 * @param c символ
	 * @return строка без символа
	 */
	public static String remChar(String s, char c) { //чистка от символа
		char str[] = s.toCharArray();
		char result[] = new char[str.length];
		for (int i = 0; i < str.length; i++) {
			if (str[i] != c) result[i] = str[i];
		}
		return new String(result);
	}
	
	/**
	 * удаляет все пробелы из строки
	 * @param s строка
	 * @return строка без пробелов
	 */
	public static String trim(String s) { //чистка от пробелов
		return new String(remChar(s, ' '));
	}
}