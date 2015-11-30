package ru.intervi.littleconfig.utils;

import java.util.ArrayList;

/**
 * парсер двухмерных массивов
 */
public class TwoDimenParser {
	/**
	 * сложить двухмерный массив строк в одномерный, с перечислением элементов в квадратных скобках
	 * @param array двухмерный массив строк
	 * @return одномерный массив строк, с элементами в квадратных скобках
	 */
	public static String[] parseInArray(String[][] array[][]) { //складывает двухмерный массив в одномерный, перечисляя элементы в квадратных скобках
		if (array == null) return null;
		String result[] = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null) continue;
			String line = "['" + array[i][0] + '\'';
			for (int n = 1; n < array[i][n].length; i++) {
				if (array[i][n] == null) continue;
				line += ", '" + array[i][n] + '\'';
			}
			line += ']';
			result[i] = line;
		}
		return result;
	}
	
	/**
	 * развернуть одномерный массив строк с элементами в квадратных скобках в двухмерный
	 * @param array одномерный массив строк с элементами в квадратных скобках
	 * @return двухмерный массив строк
	 */
	public static String[][] parseFromArray(String[] array) { //разворачивает одномерный массив с элементами в скобках в двухмерный массив
		if (array == null) return null;
		ArrayList<String[]> list = new ArrayList<String[]>(); //первичные данные
		int l = 0;
		for (int i = 0; i < array.length; i++) { //парсинг массива
			if (array[i] == null) continue;
			String line = array[i].substring(1, (array[i].length()-1)); //отсекаем скобки
			ArrayList<int[]> q = new ArrayList<int[]>(); //список кавычек
			char c[] = line.toCharArray();
			int f = -1;
			for (int n = 0; n < c.length; n++) { //поиск кавычек
				if (f == -1) { //поиск первой кавычки
					if (c[n] == '\'') f = n;
				} else { //поиск второй кавычки
					if (c[n] == '\'') {
						int add[] = {f, n};
						q.add(add);
					}
				}
			}
			if (l < q.size()) l = q.size(); //выисление максимального размера
			String add[] = new String[q.size()];
			for (int n = 0; n < q.size(); n++) { //парсинг строк из списка кавычек
				int qo[] = q.get(n);
				add[n] = line.substring((qo[0]+1), qo[1]);
			}
			list.add(add);
		}
		String result[][] = new String[array.length][l];
		for (int i = 0; i < array.length; i++) result[i] = list.get(i); //парсинг списка в массив
		return result;
	}
}