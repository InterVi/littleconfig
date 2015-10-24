package ru.intervi.littleconfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import ru.intervi.littleconfig.ConfigLoader.ClearResult;
import ru.intervi.littleconfig.utils.EasyLogger;

/**
 * запись и изменение файла конфигурации
 */
public class ConfigWriter { //запись и изменение конфига
	/**
	 * необходимо указать конфиг для работы с ним
	 */
	public ConfigWriter() {}
	/**
	 * вызывает метод setConfig(String file)
	 * @param file путь к конфигу
	 */
	public ConfigWriter(String file) {setConfig(file);}
	/**
	 * вызывает метод setConfig(File file)
	 * @param file объект File конфига для записи
	 */
	public ConfigWriter(File file) {setConfig(file);}
	
	private EasyLogger Log = new EasyLogger();
	
	private String file[] = null;
	private boolean set = false, neew = true;
	private String patch = null;
	
	/**
	 * указать файл конфига
	 * @param path путь к файлу
	 * @return true если процесс удался; false если нет
	 */
	public boolean setConfig(String path) { //установка конфига
		boolean result = false;
		File f = new File(path);
		result = f.isFile();
		if (!result) { //создаем конфиг, если его нет
			try {
				if (!f.createNewFile()) Log.info("ConfigWriter: cannot create " + path); else {
					set = true;
					patch = path;
					neew = true;
					file = null;
				}
			} catch (Exception e) {e.printStackTrace();}
		} else { //если есть, будем его перезаписывать
			set = true; neew = false;
			patch = path;
			ConfigLoader loader = new ConfigLoader();
			loader.load(path);
			file = loader.getAll();
		}
		return result;
	}
	
	/**
	 * указать файл конфига
	 * @param path объект File конфига для записи
	 * @return true если процесс удался; false если нет
	 */
	public boolean setConfig(File path) { //установка конфига
		return setConfig(path.getAbsolutePath());
	}
	
	private void writeFile() { //запись массива file
		if (set && file != null & patch != null) {
			try { //запись массива
				BufferedWriter write = new BufferedWriter(new FileWriter(patch));
				for (int i = 0; i < file.length; i++) {
					write.write(file[i]);
					write.newLine();
				}
				write.close();
			} catch (Exception e) {e.printStackTrace();}
		} else Log.info("ConfigWriter: cannot write file, config file not set");
	}
	
	private void setOption(String name, String value, int ind) { //запись значения переменной
		if (set && patch != null) {
			if (neew) { //если файл новый, то просто пишем в него опцию
				file = new String[1];
				file[0] = name + ": " + value;
				writeFile();
				neew = false;
			} else if (file != null) { //если конфиг уже есть, заменяем значение и перезаписываем массив строк
				if (file.length > 0) {
					ConfigLoader loader = new ConfigLoader();
					loader.fakeLoad(file);
					int index = -1;
					if (ind == -1) index = loader.Methods.getIndexNoSection(name); else index = ind;
					if (index > -1) { //перезаписываем переменную, если она есть
						ClearResult r = loader.Methods.clearStr(file[index]);
						if (r.broken) return; //страховка
						String rep = r.name + ": " + value; //подменяем значение
						if (r.com != null) rep += " #" + r.com; //возвращаем комментарий, если он был
						file[index] = rep;
						writeFile();
					} else { //если нет, то добавляем ее в конфиг
						ArrayList<String> newfile = new ArrayList<String>();
						for (int i = 0; i < file.length; i++) newfile.add(file[i]);
						newfile.add(name + ": " + value);
						file = new String[newfile.size()];
						for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
						writeFile();
					}
				} else Log.info("ConfigWriter: cannot write var " + name + ", var not found");
			} else Log.info("ConfigWriter: error write var " + name + ", config file not set");
		} else Log.info("ConfigWriter: error write var " + name + ", config file not set");
	}
	
	/**
	 * установить значение переменной
	 * @param name имя переменной
	 * @param value значение
	 */
	public void setOption(String name, String value) { //для внешних обращений
		setOption(name, value, -1);
	}
	
	private void setArray(String name, String[] value, boolean skobka, int ind) { //запись значения массива
		if (set && patch != null) {
			if (neew) { //есди записываем в новый конфиг
				if (skobka) { //если значения в квадратных скобках
					String send = name + ": [ " + value[0];
					for (int i = 1; i < value.length; i++) { //парсим в строку
						send += ", " + value[i]; 
					}
					send += " ]";
					file = new String[1];
					file[0] = send;
					writeFile();
					neew = false;
				} else { //если через тире
					String send[] = new String[value.length + 1];
					send[0] = name + ":";
					for (int i = 0; i < value.length; i++) {
						send[i+1] = "- " + value[i];
					}
					file = send;
					writeFile();
					neew = false;
				}
			} else if (file.length > 0) { //если нужно заменить значение в старом конфиге
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				int index = ind;
				if (index == -1) index = loader.Methods.getIndexNoSection(name);
				if (skobka) { //если значение в квадратных скобках
					if (index > -1) { //заменяем значение в параметре
						String rep = name + ": [ " + value[0];
						if (file[index].split(":").length > 1) rep = file[index].split(":")[0] + ": [ " + value[0];
						for (int i = 1; i < value.length; i++) {
							rep += ", " + value[i]; 
						}
						rep += " ]";
						file[index] = rep;
						writeFile();
					} else { //записываем массив, если его нет в конфиге
						ArrayList<String> newfile = new ArrayList<String>();
						for (int i = 0; i < file.length; i++) newfile.add(file[i]);
						String rep = name + ": [ " + value[0];
						for (int i = 1; i < value.length; i++) {
							rep += ", " + value[i]; 
						}
						rep += " ]";
						newfile.add(rep);
						file = new String[newfile.size()];
						for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
						writeFile();
					}
				} else { //если массив через тире
					if (index > -1) {
						String oldfile[] = loader.Methods.getStringArray(index);
						if (oldfile.length == value.length) { //если длинна одинакова, заменяем данные
							int pos = index+1;
							for (int i = 0; i < oldfile.length; i++) {
								if (pos >= file.length) break;
								file[pos] = file[pos].split("-")[0] + "- " + value[i];
								pos++;
							}
							writeFile();
						} else { //если нет, расширяем массив file
							ArrayList<String> newfile = new ArrayList<String>();
							for (int i = 0; i <= index; i++) newfile.add(file[i]);
							String prob = "  "; if (index+1 < file.length) loader.Methods.getProbels(file[(index+1)]);
							for (int i = 0; i < value.length; i++) newfile.add(prob + "- " + value[i]);
							for (int i = index+oldfile.length+1; i < file.length; i++) newfile.add(file[i]);
							file = new String[newfile.size()];
							for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
							writeFile();
						}
					} else { //записываем массив, если его нет в конфиге
						ArrayList<String> newfile = new ArrayList<String>();
						for (int i = 0; i < file.length; i++) newfile.add(file[i]);
						newfile.add(name + ":");
						for (int i = 0; i < value.length; i++) newfile.add("- " + value[i]);
						file = new String[newfile.size()];
						for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
						writeFile();
					}
				}
			} else Log.info("ConfigWriter: cannot write var " + name + ", array not found");
		} else Log.info("ConfigWriter: error write array " + name + ", config file not set");
	}
	
	/**
	 * установить значение массива
	 * @param name имя переменной с массивом
	 * @param value массив строк
	 * @param skobka перечислить ли значения через запятую в квадратных скобках или через перевод строки и тире
	 */
	public void setArray(String name, String[] value, boolean skobka) { //для внешних обращений
		setArray(name, value, skobka, -1);
	}
	
	/**
	 * установить значение переменной в секции
	 * @param name имя переменной
	 * @param value значение
	 * @param section имя секции
	 */
	public void setOptionInSection(String name, String value, String section) { //запись опции в секцию
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				Log.offLog();
				int index = loader.Methods.getIndexInSection(section, name);
				Log.onLog();
				if (index != -1) setOption(name, value, index); else { //если опции нет в конфиге (ее не надо заменять)
					ArrayList<String> newfile = new ArrayList<String>(); //запись секции, если ее нет в конфиге
					for (int i = 0; i < file.length; i++) newfile.add(file[i]);
					int sec = loader.Methods.getIndexSection(section);
					if (sec == -1) { //если создается новая секция
						newfile.add(section + ":");
						newfile.add("  " + name + ": " + value);
					} else if (sec + loader.getSectionRealLength(section) == file.length-1) { //если нужно добавить в старую, и она в конце конфига
						newfile.add("  " + name + ": " + value);
					} else { //если позиция плавающая
						newfile.clear();
						for (int i = 0; i < sec; i++) newfile.add(file[i]);
						for (int i = sec; i <= sec + loader.getSectionRealLength(section) & i < file.length; i++) newfile.add(file[i]);
						newfile.add("  " + name + ": " + value);
						for (int i = sec + loader.getSectionRealLength(section)+1; i < file.length; i++) newfile.add(file[i]);
					}
					file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
					writeFile();
				}
			} else {
				file = new String[2];
				file[0] = section + ":";
				file[1] = "  " + name + ": " + value;
				writeFile();
				neew = false;
			}
		} else Log.info("ConfigWriter: error write var " + name + " in section " + section + ", config file not set");
	}
	
	/**
	 * установить значение массива в секции
	 * @param name имя переменной с массивом
	 * @param value массив строк
	 * @param skobka перечислить ли значения через запятую в квадратных скобках или через перевод строки и тире
	 * @param section имя секции
	 */
	public void setArrayInSection(String name, String value[], boolean skobka, String section) { //запись массива в секцию
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				Log.offLog();
				int index = loader.Methods.getIndexInSection(section, name);
				Log.onLog();
				if (index != -1) setArray(name, value, skobka, index); else {
					ArrayList<String> newfile = new ArrayList<String>(); //запись массива, если его нет в конфиге
					for (int i = 0; i < file.length; i++) newfile.add(file[i]);
					int sec = loader.Methods.getIndexSection(section);
					if (sec == -1) { //если создается новая секция
						newfile.add(section + ":");
						if (!skobka) newfile.add("  " + name + ":"); else newfile.add("  " + name + ": []");
						if (!skobka) newfile.add("  - process write");
					} else if (sec + loader.getSectionRealLength(section) == file.length-1) { //если нужно добавить в старую, и она в конце конфига
						if (!skobka) newfile.add("  " + name + ":"); else newfile.add("  " + name + ": []");
						if (!skobka) newfile.add("  - process write");
					} else { //если позиция плавающая
						newfile.clear();
						for (int i = 0; i < sec; i++) newfile.add(file[i]);
						for (int i = sec; i <= sec + loader.getSectionRealLength(section) & i < file.length; i++) newfile.add(file[i]);
						if (!skobka) newfile.add("  " + name + ":"); else newfile.add("  " + name + ": []");
						if (!skobka) newfile.add("  - process write");
						for (int i = sec + loader.getSectionRealLength(section) + 1; i < file.length; i++) newfile.add(file[i]);
					}
					file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
					loader.fakeLoad(file);
					index = loader.Methods.getIndexInSection(section, name);
					setArray(name, value, skobka, index);
				}
			} else {
				if (skobka) {
					file = new String[2];
					file[0] = section + ":";
					String send = "  " + name + ": [ " + value[0];
					for (int i = 1; i < value.length; i++) { //парсим в строку
						send += ", " + value[i]; 
					}
					send += " ]";
					file[1] = send;
					writeFile();
					neew = false;
				} else {
					file = new String[value.length + 2];
					file[0] = section + ":";
					file[1] = "  " + name + ":";
					for (int i = 2; i < file.length; i++) {
						file[i] = "  - " + value[i-2];
					}
					writeFile();
					neew = false;
				}
			}
		} else Log.info("ConfigWriter: error write array " + name + " in section " + section + ", config file not set");
	}
	
	private void delOption(String name, int ind) { //удаление опции из конфига
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				int index = ind; if (index == -1) index = loader.Methods.getIndexNoSection(name);
				if (index > -1) {
					ArrayList<String> newfile = new ArrayList<String>();
					for (int i = 0; i < index; i++) newfile.add(file[i]);
					for (int i = index+1; i < file.length; i++) newfile.add(file[i]);
					file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
					writeFile();
				} else Log.info("ConfigWriter: error delete var " + name + ", var not found");
			} else Log.info("ConfigWriter: error delete var " + name + ", config file not found");
		} else Log.info("ConfigWriter: error delete var " + name + ", config file not set");
	}
	
	/**
	 * удалить переменную из конфига
	 * @param name имя переменной
	 */
	public void delOption(String name) { //для внешних обращений
		delOption(name, -1);
	}
	
	private void delArray(String name, int ind) { //удаление массива
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				int index = ind; if (index == -1) index = loader.Methods.getIndexNoSection(name);
				if (index > -1) {
					boolean skobka = loader.Methods.isArray(index).skobka;
					if (skobka) {
						delOption(name, index);
					} else {
						int leng = loader.Methods.getStringArray(index).length;
						ArrayList<String> newfile = new ArrayList<String>();
						for (int i = 0; i < index; i++) newfile.add(file[i]);
						for (int i = index+leng+1; i < file.length; i++) newfile.add(file[i]);
						file = new String[newfile.size()];
						for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
						writeFile();
					}
				} else Log.info("ConfigWriter: error delete array " + name + ", array not found");
			} else Log.info("ConfigWriter: error delete array " + name + ", config file not found");
		} else Log.info("ConfigWriter: error delete array " + name + ", config file not set");
	}
	
	/**
	 * удалить массив из конфига
	 * @param name имя переменной с массивом
	 */
	public void delArray(String name) { //для внешних обращений
		delArray(name, -1);
	}
	
	/**
	 * удалить переменную из секции
	 * @param name имя переменной
	 * @param section имя секции
	 */
	public void delOptionInSection(String name, String section) { //удаление параметра из секции
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				int index = loader.Methods.getIndexInSection(section, name);
				if (index > -1) {
					delOption(name, index);
				} else Log.info("ConfigWriter: error delete var " + name + " from section " + section + ", var not found");
			} else Log.info("ConfigWriter: error delete var " + name + " from section " + section + ", config file not found");
		} else Log.info("ConfigWriter: error delete var " + name + " from section " + section + ", config file not set");
	}
	
	/**
	 * удалить массив из секции
	 * @param name имя переменной с массивом
	 * @param section имя секции
	 */
	public void delArrayInSection(String name, String section) { //удаление массива из секции
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				int index = loader.Methods.getIndexInSection(section, name);
				if (index > -1) {
					delArray(name, index);
				} else Log.info("ConfigWriter: error delete array " + name + " from section " + section + ", array not found");
			} else Log.info("ConfigWriter: error delete array " + name + " from section " + section + ", config file not found");
		} else Log.info("ConfigWriter: error delete array " + name + " from section " + section + ", config file not set");
	}
	
	/**
	 * удалить секцию
	 * @param section имя секции
	 */
	public void delSection(String section) {
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				int index = loader.Methods.getIndexSection(section);
				if (index > -1) {
					int leng = loader.getSectionRealLength(section);
					ArrayList<String> newfile = new ArrayList<String>();
					for (int i = 0; i < index; i++) newfile.add(file[i]);
					for (int i = index+leng+1; i < file.length; i++) newfile.add(file[i]);
					file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
					writeFile();
				} else Log.info("ConfigWriter: error delete section " + section + ", section not found");
			} else Log.info("ConfigWriter: error delete section " + section + ", config file not found");
		} else Log.info("ConfigWriter: error delete section " + section + ", config file not set");
	}
	
	/**
	 * удалить файл конфига
	 * @return true если удалось удалить; false если нет
	 */
	public boolean removeConfig() { //удалить весь конфиг (сам файл)
		boolean result = false;
		if (set && patch != null) {
			if (!neew) {
				File conf = new File(patch);
				if (conf.isFile()) result = conf.delete(); else Log.info("ConfigWriter: error delete config file, not found");
			} else Log.info("ConfigWriter: error delete config file, not found");
		} else Log.info("ConfigWriter: error delete config file, not set");
		return result;
	}
	
	/**
	 * класс со внутренними методами для работы с конфигом
	 */
	public class WriterMethods { //класс со внутренними методами
		/**
		 * установить значение переменной по индексу
		 * @param name имя переменной
		 * @param value значение
		 * @param index индекс в конфиге
		 */
		public void SetOption(String name, String value, int index) { //установить опцию по индексу
			setOption(name, value, index);
		}
		/**
		 * установить значение массива по индексу
		 * @param name имя переменной с массивом
		 * @param value массив строк
		 * @param skobka перечислить ли значения через запятую в квадратных скобках или через перевод строки и тире
		 * @param index индекс в конфиге
		 */
		public void SetArray(String name, String[] value, boolean skobka, int index) { //установить массив по индексу
			setArray(name, value, skobka, index);
		}
		/**
		 * удалить опцию по индексу
		 * @param name имя опции
		 * @param index индекс в конфиге
		 */
		public void DelOption(String name, int index) { //удалить опцию по индексу
			delOption(name, index);
		}
		/**
		 * удалить массив по индексу
		 * @param name имя переменной с массивом
		 * @param index индекс в конфиге
		 */
		public void DelArray(String name, int index) { //удалить массив по индексу
			delArray(name, index);
		}
		/**
		 * получть весь класс
		 * @return new WriterMethods()
		 */
		public WriterMethods getMethods() {return new WriterMethods();} //получить весь класс
	}
	/**
	 * инициализированный объект WriterMethods
	 */
	public WriterMethods Methods = new WriterMethods();
	
	/**
	 * записать массив строк в конфиг как есть (создаст новый файл или полностью перепишет старый)
	 * @param array массив строк
	 */
	public void writeArray(String[] array) { //запись массива строк в файл как есть (создает новый конфиг или полностью перезаписывает текущий)
		if (set && patch != null) {
			if (array != null) {
				if (array.length > 0) {
					file = array;
					writeFile();
					setConfig(patch); //обновляем инфу
				}
			}
		} else Log.info("ConfigWriter: error write array, config not set");
	}
}