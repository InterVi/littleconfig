package ru.intervi.littleconfig.utils;

import java.lang.Class;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * разные полезные утилиты
 */
public class Utils { //разные полезные методы
	/**
	 * удаляет часть из строки
	 * @param s строка
	 * @param p1 индекс первого элемента
	 * @param p2 индекс второго элемента
	 * @return строка без части, находящейся между p1 и p2
	 */
	public static String remChars(String s, int p1, int p2) { //метод для вырезания символов из строк
		if (s == null) return null;
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
		if (s == null) return null;
		char str[] = s.toCharArray();
		ArrayList<char[]> list = new ArrayList<char[]>();
		for (int i = 0; i < str.length; i++) {
			if (str[i] != c) {
				char add[] = {str[i]};
				list.add(add);
			}
		}
		char result[] = new char[list.size()];
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) result[i] = list.get(i)[0];
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
	
	/**
	 * получить количество символов в строке
	 * @param str строка для подсчета
	 * @param c искомый символ
	 * @return кол-во символов в строке (-1 если str == null)
	 */
	public static int numChars(String str, char c) { //подсчитать количество символов в строке
		if (str == null) return -1;
		int result = 0;
		char cc[] = str.toCharArray();
		for (int i = 0; i < cc.length; i++) {
			if (cc[i] == c) result++;
		}
		return result;
	}
	
	/**
	 * получить byte из строки
	 * @param s строка с числом
	 * @return числовое значение из строки (0 в случае ошибки)
	 */
	public static byte byteFromString(String s) { //байты из строки
		if (s == null) return 0;
		try {
			return Byte.parseByte(s);
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * получить short из строки
	 * @param s строка с числом
	 * @return числовое значение из строки (0 в случае ошибки)
	 */
	public static short shortFromString(String s) { //шорт из строки
		if (s == null) return 0;
		try {
			return Short.parseShort(s);
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * получить int из строки
	 * @param s строка с числом
	 * @return числовое значение из строки (0 в случае ошибки)
	 */
	public static int intFromString(String s) { //инт из строки
		if (s == null) return 0;
		try {
			return Integer.parseInt(s);
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * получить long из строки
	 * @param s строка с числом
	 * @return числовое значение из строки (0 в случае ошибки)
	 */
	public static long longFromString(String s) { //лонг из строки
		if (s == null) return 0;
		try {
			return Long.parseLong(s);
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * получить double из строки
	 * @param s строка с числом
	 * @return числовое значение из строки (0 в случае ошибки)
	 */
	public static double doubleFromString(String s) { //доубл из строки
		if (s == null) return 0;
		try {
			return Double.parseDouble(s);
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * получить boolean из строки
	 * @param s строка с булевой
	 * @return булево значение из строки (false в случае ошибки)
	 */
	public static boolean booleanFromString(String s) { //булева из строки
		if (s == null) return false;
		try {
			return Boolean.parseBoolean(s);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * массив byte из массива строк
	 * @param s массив строк
	 * @return массив byte (null в случае ошибки)
	 */
	public static byte[] byteFromStringArray(String s[]) { //массив байт из массива строк
		if (s == null) return null;
		byte result[] = null;
		try {
			result = new byte[s.length];
			for (int i = 0; i < s.length; i++) {
				if (s[i] != null) result[i] = Byte.parseByte(s[i]);
			}
		} catch(Exception e) {e.printStackTrace();}
		return result;
	}
	
	/**
	 * массив short из массива строк
	 * @param s массив строк
	 * @return массив short (null в случае ошибки)
	 */
	public static short[] shortFromStringArray(String s[]) { //массив шорт из массива строк
		if (s == null) return null;
		short result[] = null;
		try {
			result = new short[s.length];
			for (int i = 0; i < s.length; i++) {
				if (s[i] != null) result[i] = Short.parseShort(s[i]);
			}
		} catch(Exception e) {e.printStackTrace();}
		return result;
	}
	
	/**
	 * массив int из массива строк
	 * @param s массив строк
	 * @return массив int (null в случае ошибки)
	 */
	public static int[] intFromStringArray(String s[]) { //массив инт из массива строк
		if (s == null) return null;
		int result[] = null;
		try {
			result = new int[s.length];
			for (int i = 0; i < s.length; i++) {
				if (s[i] != null) result[i] = Integer.parseInt(s[i]);
			}
		} catch(Exception e) {e.printStackTrace();}
		return result;
	}
	
	/**
	 * массив long из массива строк
	 * @param s массив строк
	 * @return массив long (null в случае ошибки)
	 */
	public static long[] longFromStringArray(String s[]) { //массив лонг из массива строк
		if (s == null) return null;
		long result[] = null;
		try {
			result = new long[s.length];
			for (int i = 0; i < s.length; i++) {
				if (s[i] != null) result[i] = Long.parseLong(s[i]);
			}
		} catch(Exception e) {e.printStackTrace();}
		return result;
	}
	
	/**
	 * массив double из массива строк
	 * @param s массив строк
	 * @return массив double (null в случае ошибки)
	 */
	public static double[] doubleFromStringArray(String s[]) { //массив доубл из массива строк
		if (s == null) return null;
		double result[] = null;
		try {
			result = new double[s.length];
			for (int i = 0; i < s.length; i++) {
				if (s[i] != null) result[i] = Double.parseDouble(s[i]);
			}
		} catch(Exception e) {e.printStackTrace();}
		return result;
	}
	
	/**
	 * массив boolean из массива строк
	 * @param s массив строк
	 * @return массив boolean (null в случае ошибки)
	 */
	public static boolean[] booleanFromStringArray(String s[]) { //массив булева из массива строк
		if (s == null) return null;
		boolean result[] = null;
		try {
			result = new boolean[s.length];
			for (int i = 0; i < s.length; i++) {
				if (s[i] != null) result[i] = Boolean.parseBoolean(s[i]);
			}
		} catch(Exception e) {e.printStackTrace();}
		return result;
	}
	
	/**
	 * получить путь к папке с jar
	 * @param c класс, от которого берется путь
	 * @return объект File папки с jar (null в случае ошибки)
	 */
	public static File getFolder(Class<?> c) { //получить папку с jar
		if (c == null) return null;
		return new File(getFolderPath(c));
	}
	
	/**
	 * получить путь к папке с jar
	 * @param c класс, от которого берется путь
	 * @return абсолютный путь к папке с jar
	 */
	public static String getFolderPath(Class<?> c) { //путь к папке с jar
		if (c == null) return null;
		try {
			String path = new File(c.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
			return path.substring(0, path.lastIndexOf(File.separator));
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * сохранить поток в файл
	 * @param stream поток из файла (например, запакованного в jar)
	 * @param file файл, куда сохранять
	 * @return true если операция удалась; false если нет
	 */
	public static boolean saveFile(InputStream stream, File file) { //сохранить поток в файл
		if (stream == null | file == null) return false;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buff = new byte[65536];
			int n;
			while((n = stream.read(buff)) > 0){
				fos.write(buff, 0, n);
				fos.flush();
			}
			fos.close();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * заменить часть массива
	 * @param value массив строк, в котором производится замена
	 * @param replace замена
	 * @param firstIndex индекс первого элемента, с которого начинается вставка замены
	 * @param lastIndex индекс последнего элемента, до которого вставляется замена
	 * @return массив строк с произведенной заменой
	 */
	public String[] replacePart(String[] value, String[] replace, int firstIndex, int lastIndex) {
		if (value == null | replace == null) return null;
		if (firstIndex < 0 | lastIndex < 0) return null;
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < firstIndex & i < value.length; i++) {
			if (value[i] != null) list.add(value[i]);
		}
		for (int i = 0; i < replace.length; i++) {
			if (replace[i] != null) list.add(replace[i]);
		}
		for (int i = lastIndex; i < value.length; i++) {
			if (value[i] != null) list.add(value[i]);
		}
		return list.toArray(new String[list.size()]);
	}
}