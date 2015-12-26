package ru.intervi.littleconfig.utils;

import java.util.ArrayList;

import ru.intervi.littleconfig.ConfigLoader;

/**
 * парсер двухмерных массивов
 */
public class TwoDimenParser {
	/**
	 * сложить двухмерный массив строк в одномерный, с перечислением элементов в квадратных скобках
	 * @param array двухмерный массив строк
	 * @return одномерный массив строк, с элементами в квадратных скобках
	 */
	public static String[] parseInArray(String[][] array) { //складывает двухмерный массив в одномерный, перечисляя элементы в квадратных скобках
		if (array == null) return null;
		String result[] = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null) continue;
			String line = "['" + array[i][0] + '\'';
			for (int n = 1; n < array[i].length; n++) {
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
		ArrayList<String[]> list = new ArrayList<String[]>();
		int l = 0;
		for (int i = 0; i < array.length; i++) {
			ConfigLoader loader = new ConfigLoader();
			String data[] = {"array: " + array[i]};
			loader.fakeLoad(data); //грузим фейковый конфиг
			String add[] = loader.getStringArray("array"); //получаем готовый массив
			list.add(add);
			if (l < add.length) l = add.length; //вычисление максимальной длинны
		}
		return list.toArray(new String[list.size()][l]);
	}
}