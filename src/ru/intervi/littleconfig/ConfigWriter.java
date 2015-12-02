package ru.intervi.littleconfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

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
	
	/**
	 * используемый логгер для вывода сообщений
	 */
	public EasyLogger Log = new EasyLogger();
	
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
		if (path == null) {
			Log.warn("ConfigWriter setConfig(String path): null path");
			return result;
		}
		File f = new File(path);
		result = f.isFile();
		if (!result) { //создаем конфиг, если его нет
			try {
				if (!f.createNewFile()) Log.warn("ConfigWriter: cannot create " + path); else {
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
		if (path == null) {
			Log.warn("ConfigWriter setConfig(File path): null path");
			return false;
		}
		return setConfig(path.getAbsolutePath());
	}
	
	private void writeFile() { //запись массива file
		if (set && file != null & patch != null) {
			BufferedWriter write = null;
			try { //запись массива
				write = new BufferedWriter(new FileWriter(patch));
				for (int i = 0; i < file.length; i++) {
					write.write(file[i]);
					write.newLine();
				}
			} catch (Exception e) {e.printStackTrace();} finally {
				if (write != null) {
					try {
						write.close();
					} catch(Exception e) {e.printStackTrace();}
				}
			}
		} else Log.warn("ConfigWriter: cannot write file, config file not set");
	}
	
	private void setOption(String name, String value, int ind) { //запись значения переменной
		if (name == null || value == null) {
			Log.warn("ConfigWriter setOption: null name or null value");
			return;
		}
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
					if (ind == -1) index = loader.Methods.recIndexNoSection(name); else index = ind;
					if (index > -1) { //перезаписываем переменную, если она есть
						ClearResult r = loader.Methods.clearString(file[index]);
						if (r.broken) return; //страховка
						String p = "";
						int prob = loader.Methods.recProbels(file[index]);
						for (int i = 0; i <= prob; i++) p += ' ';
						String rep = p + r.name + ": " + value; //подменяем значение
						if (r.com != null) rep += " #" + r.com; //возвращаем комментарий, если он был
						file[index] = rep;
						writeFile();
					} else { //если нет, то добавляем ее в конфиг
						String rep[] = new String[(file.length+1)];
						for (int i = 0; i < file.length; i++) rep[i] = file[i];
						rep[(rep.length-1)] = name + ": " + value;
						file = rep;
						writeFile();
					}
				} else Log.warn("ConfigWriter: cannot write var " + name + ", var not found");
			} else Log.warn("ConfigWriter: error write var " + name + ", config file not set");
		} else Log.warn("ConfigWriter: error write var " + name + ", config file not set");
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
		if (name == null || value == null) {
			Log.warn("ConfigWriter setArray: null name or null value");
			return;
		}
		if (set && patch != null) {
			if (neew) { //есди записываем в новый конфиг
				if (skobka) { //если значения в квадратных скобках
					String send = name + ": [" + value[0];
					for (int i = 1; i < value.length; i++) { //парсим в строку
						if (value[i] == null) continue;
						send += ", " + value[i];
					}
					send += "]";
					file = new String[1];
					file[0] = send;
				} else { //если через тире
					file = new String[(value.length + 1)];
					file[0] = name + ":";
					for (int i = 0; i < value.length; i++) {
						if (value[i] == null) continue;
						file[(i+1)] = "- " + value[i];
					}
				}
				neew = false;
				writeFile();
			} else if (file != null) { //если нужно заменить значение в старом конфиге
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				int index = -1;
				if (ind == -1) index = loader.Methods.recIndexNoSection(name); else index = ind;
				if (index > -1) { //если нужно заменить значение у старого массива
					ArrayList<String> one = new ArrayList<String>(); //содержимое до массива
					ArrayList<String> two = new ArrayList<String>(); //после
					ArrayList<String> paste = new ArrayList<String>(); //что вставить вместо него
					//заполнение частей до и после массива
					for (int i = 0; i < index; i++) one.add(file[i]);
					for (int i = (index + loader.Methods.getArrayRealLength(index)); i < file.length; i++) two.add(file[i]);
					
					int p = loader.Methods.recProbels(file[index]);
					String pr = "";
					for (int i = 0; i <= p; i++) pr += ' ';
					if (skobka) { //если новый массив в скобках
						String add = pr + name + ": [" + value[0];
						for (int i = 1; i < value.length; i++) {
							if (value[i] == null) continue;
							add += ", " + value[i];
						}
						add += ']';
						paste.add(add);
					} else { //если новый массив через тире
						paste.add((pr + name + ":"));
						for (int i = 0; i < value.length; i++) {
							if (value[i] == null) continue;
							paste.add((pr + "- " + value[i]));
						}
					}
					
					//заполнение массива
					file = new String[(one.size()+two.size()+paste.size())];
					int pos = 0;
					Iterator<String> iter = one.iterator();
					while(iter.hasNext()) {
						file[pos] = iter.next();
						pos++;
					}
					iter = paste.iterator();
					while(iter.hasNext()) {
						file[pos] = iter.next();
						pos++;
					}
					iter = two.iterator();
					while(iter.hasNext()) {
						file[pos] = iter.next();
						pos++;
					}
				} else { //если нужно добавить новый массив
					ArrayList<String> newfile = new ArrayList<String>();
					for (int i = 0; i < file.length; i++) newfile.add(file[i]);
					if (skobka) {
						String add = name + ": [" + value[0];
						for (int i = 1; i < value.length; i++) {
							if (value[i] == null) continue;
							add += ", " + value[i];
						}
						add += ']';
						newfile.add(add);
					} else {
						newfile.add((name + ":"));
						for (int i = 0; i < value.length; i++) {
							if (value[i] == null) continue;
							newfile.add(("- " + value[i]));
						}
					}
					file = new String[newfile.size()];
					for (int i = 0; i < newfile.size(); i++) file[i] = newfile.get(i);
				}
				writeFile();
			} else Log.warn("ConfigWriter: error write array " + name + ", config file not set");
		} else Log.warn("ConfigWriter: error write array " + name + ", config file not set");
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
			if (!neew) { //если правится старый конфиг
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				int index = loader.Methods.recIndexInSection(section, name);
				if (index != -1) setOption(name, value, index); else { //если опции нет в конфиге (ее не надо заменять)
					ArrayList<String> newfile = new ArrayList<String>(); //запись секции, если ее нет в конфиге
					for (int i = 0; i < file.length; i++) newfile.add(file[i]);
					int sec = loader.Methods.recIndexSection(section);
					if (sec == -1) { //если создается новая секция
						newfile.add(section + ":");
						newfile.add("  " + name + ": " + value);
					} else if ((sec + loader.getSectionRealLength(section)) == (file.length-1)) { //если нужно добавить в старую, и она в конце конфига
						int p = loader.Methods.recProbels(file[sec]);
						String prob = "  ";
						for (int i = 0; i <= p; i++) prob += ' ';
						newfile.add(prob + name + ": " + value);
					} else { //если позиция плавающая
						newfile.clear();
						for (int i = 0; i <= (sec + loader.getSectionRealLength(section)) & i < file.length; i++) newfile.add(file[i]);
						int p = loader.Methods.recProbels(file[sec]);
						String prob = "  ";
						for (int i = 0; i <= p; i++) prob += ' ';
						newfile.add(prob + name + ": " + value);
						for (int i = (sec + loader.getSectionRealLength(section) + 1); i < file.length; i++) newfile.add(file[i]);
					}
					file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
				}
			} else { //если создается новый конфиг
				file = new String[2];
				file[0] = section + ":";
				file[1] = "  " + name + ": " + value;
				neew = false;
			}
			writeFile();
		} else Log.warn("ConfigWriter: error write var " + name + " in section " + section + ", config file not set");
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
			if (!neew) { //если редактируется старый конфиг
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				int index = loader.Methods.recIndexInSection(section, name);
				if (index != -1) setArray(name, value, skobka, index); else {
					ArrayList<String> newfile = new ArrayList<String>(); //запись массива, если его нет в конфиге
					for (int i = 0; i < file.length; i++) newfile.add(file[i]);
					int sec = loader.Methods.recIndexSection(section);
					if (sec == -1) { //если создается новая секция
						newfile.add(section + ":");
						if (skobka) { //если нужно создать массив в скобках
							String add = "  " + name + ": [" + value[0];
							for (int i = 1; i < value.length; i++) {
								if (value[i] == null) continue;
								add += ", " + value[i];
							}
							add += ']';
							newfile.add(add);
						} else { //если нужно создать массив через тире
							newfile.add(("  " + name + ":"));
							for (int i = 0; i < value.length; i++) {
								if (value[i] == null) continue;
								newfile.add(("  - " + value[i]));
							}
						}
					} else if ((sec + loader.getSectionRealLength(section)) == (file.length-1)) { //если нужно добавить в старую, и она в конце конфига
						int p = loader.Methods.recProbels(file[sec]);
						String prob = "  ";
						for (int i = 0; i <= p; i++) prob += ' ';
						if (skobka) {
							String add = prob + name + ": [" + value[0];
							for (int i = 1; i < value.length; i++) {
								if (value[i] == null) continue;
								add += ", " + value[i];
							}
							add += ']';
							newfile.add(add);
						} else {
							newfile.add((prob + name + ":"));
							for (int i = 0; i < value.length; i++) {
								if (value[i] == null) continue;
								newfile.add((prob + "- " + value[i]));
							}
						}
					} else { //если позиция плавающая
						newfile.clear();
						for (int i = 0; i <= (sec + loader.getSectionRealLength(section)) & i < file.length; i++) newfile.add(file[i]);
						int p = loader.Methods.recProbels(file[sec]);
						String prob = "  ";
						for (int i = 0; i <= p; i++) prob += ' ';
						if (skobka) {
							String add = prob + name + ": [" + value[0];
							for (int i = 1; i < value.length; i++) {
								if (value[i] == null) continue;
								add += ", " + value[i];
							}
							add += ']';
							newfile.add(add);
						} else {
							newfile.add((prob + name + ":"));
							for (int i = 0; i < value.length; i++) {
								if (value[i] == null) continue;
								newfile.add((prob + "- " + value[i]));
							}
						}
						for (int i = (sec + loader.getSectionRealLength(section) + 1); i < file.length; i++) newfile.add(file[i]);
					}
					file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
				}
			} else { //если создается новый конфиг
				if (skobka) {
					file = new String[2];
					file[0] = section + ":";
					String send = "  " + name + ": [" + value[0];
					for (int i = 1; i < value.length; i++) { //парсим в строку
						if (value[i] == null) continue;
						send += ", " + value[i]; 
					}
					send += ']';
					file[1] = send;
					neew = false;
				} else {
					file = new String[(value.length + 2)];
					file[0] = section + ":";
					file[1] = "  " + name + ":";
					for (int i = 2; i < file.length; i++) {
						if (value[(i-2)] == null) continue;
						file[i] = "  - " + value[(i-2)];
					}
					neew = false;
				}
			}
			writeFile();
		} else Log.warn("ConfigWriter: error write array " + name + " in section " + section + ", config file not set");
	}
	
	private void delOption(String name, int ind) { //удаление опции из конфига
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeLoad(file);
				int index = ind; if (index == -1) index = loader.Methods.recIndexNoSection(name);
				if (index > -1) {
					ArrayList<String> newfile = new ArrayList<String>();
					for (int i = 0; i < index; i++) newfile.add(file[i]);
					for (int i = (index+1); i < file.length; i++) newfile.add(file[i]);
					file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
					writeFile();
				} else Log.warn("ConfigWriter: error delete var " + name + ", var not found");
			} else Log.warn("ConfigWriter: error delete var " + name + ", config file not found");
		} else Log.warn("ConfigWriter: error delete var " + name + ", config file not set");
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
				int index = ind; if (index == -1) index = loader.Methods.recIndexNoSection(name);
				if (index > -1) {
					boolean skobka = loader.Methods.checkArray(index).skobka;
					if (skobka) {
						delOption(name, index);
					} else {
						int leng = loader.Methods.recStringArray(index).length;
						ArrayList<String> newfile = new ArrayList<String>();
						for (int i = 0; i < index; i++) newfile.add(file[i]);
						for (int i = (index+leng+1); i < file.length; i++) newfile.add(file[i]);
						file = new String[newfile.size()];
						for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
						writeFile();
					}
				} else Log.warn("ConfigWriter: error delete array " + name + ", array not found");
			} else Log.warn("ConfigWriter: error delete array " + name + ", config file not found");
		} else Log.warn("ConfigWriter: error delete array " + name + ", config file not set");
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
				int index = loader.Methods.recIndexInSection(section, name);
				if (index > -1) {
					delOption(name, index);
				} else Log.warn("ConfigWriter: error delete var " + name + " from section " + section + ", var not found");
			} else Log.warn("ConfigWriter: error delete var " + name + " from section " + section + ", config file not found");
		} else Log.warn("ConfigWriter: error delete var " + name + " from section " + section + ", config file not set");
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
				int index = loader.Methods.recIndexInSection(section, name);
				if (index > -1) {
					delArray(name, index);
				} else Log.warn("ConfigWriter: error delete array " + name + " from section " + section + ", array not found");
			} else Log.warn("ConfigWriter: error delete array " + name + " from section " + section + ", config file not found");
		} else Log.warn("ConfigWriter: error delete array " + name + " from section " + section + ", config file not set");
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
				int index = loader.Methods.recIndexSection(section);
				if (index > -1) {
					int leng = loader.getSectionRealLength(section);
					ArrayList<String> newfile = new ArrayList<String>();
					for (int i = 0; i < index; i++) newfile.add(file[i]);
					for (int i = (index+leng+1); i < file.length; i++) newfile.add(file[i]);
					file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
					writeFile();
				} else Log.warn("ConfigWriter: error delete section " + section + ", section not found");
			} else Log.warn("ConfigWriter: error delete section " + section + ", config file not found");
		} else Log.warn("ConfigWriter: error delete section " + section + ", config file not set");
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
				if (conf.isFile()) result = conf.delete(); else Log.warn("ConfigWriter: error delete config file, not found");
			} else Log.warn("ConfigWriter: error delete config file, not found");
		} else Log.warn("ConfigWriter: error delete config file, not set");
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
		public void setOption(String name, String value, int index) { //установить опцию по индексу
			setOption(name, value, index);
		}
		/**
		 * установить значение массива по индексу
		 * @param name имя переменной с массивом
		 * @param value массив строк
		 * @param skobka перечислить ли значения через запятую в квадратных скобках или через перевод строки и тире
		 * @param index индекс в конфиге
		 */
		public void setArray(String name, String[] value, boolean skobka, int index) { //установить массив по индексу
			setArray(name, value, skobka, index);
		}
		/**
		 * удалить опцию по индексу
		 * @param name имя опции
		 * @param index индекс в конфиге
		 */
		public void delOption(String name, int index) { //удалить опцию по индексу
			delOption(name, index);
		}
		/**
		 * удалить массив по индексу
		 * @param name имя переменной с массивом
		 * @param index индекс в конфиге
		 */
		public void delArray(String name, int index) { //удалить массив по индексу
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
		} else Log.warn("ConfigWriter: error write array, config not set");
	}
}