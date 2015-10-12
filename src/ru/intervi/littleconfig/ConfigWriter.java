package ru.intervi.littleconfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class ConfigWriter { //запись и изменение конфига
	private class Logger { //отправка сообщений в консоль
		boolean send = true;
		public void info(String text) {
			if (send) System.out.println("[littleconfig]: " + text);
		}
	}
	private Logger log = new Logger();
	public void onLog() {log.send = true;} //включить вывод отладочной информации
	public void offLog() {log.send = false;} //выключить вывод отладочной информаци
	
	private String file[] = null;
	private boolean set = false, neew = true;
	private String patch = null;
	
	public boolean setConfig(String path) { //установка конфига
		boolean result = false;
		File f = new File(path);
		result = f.isFile();
		if (!result) { //создаем конфиг, если его нет
			try {
				if (!f.createNewFile()) log.info("configWriter: cannot create " + path); else {
					set = true;
					patch = path;
					neew = true;
					this.file = null;
				}
			} catch (Exception e) {e.printStackTrace();}
		} else { //если есть, будем его перезаписывать
			set = true; neew = false;
			patch = path;
			ConfigLoader loader = new ConfigLoader();
			loader.load(path);
			this.file = loader.getAll();
		}
		return result;
	}
	
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
		} else log.info("configWriter: cannot write file, config file not set");
	}
	
	private void setOption(String name, String value, int ind) { //запись значения переменной
		if (set && patch != null) {
			if (neew) { //если файл новый, то просто пишем в него опцию
				this.file = new String[1];
				this.file[0] = name + ": " + value;
				writeFile();
				this.neew = false;
			} else if (file != null) { //если конфиг уже есть, заменяем значение и перезаписываем массив строк
				if (file.length > 0) {
					ConfigLoader loader = new ConfigLoader();
					loader.fakeload(file);
					int index = -1;
					if (ind == -1) index = loader.methods.RecIndexNoSection(name); else index = ind;
					if (index > -1) { //перезаписываем переменную, если она есть
						String rep = file[index].split(":")[0];
						rep += ": " + value; //подменяем значение
						this.file[index] = rep;
						writeFile();
					} else { //если нет, то добавляем ее в конфиг
						ArrayList<String> newfile = new ArrayList<String>();
						for (int i = 0; i < file.length; i++) newfile.add(file[i]);
						newfile.add(name + ": " + value);
						this.file = new String[newfile.size()];
						for (int i = 0; i < file.length; i++) this.file[i] = newfile.get(i);
						writeFile();
					}
				} else log.info("configWriter: cannot write var " + name + ", var not found");
			} else log.info("configWriter: error write var " + name + ", config file not set");
		} else log.info("configWriter: error write var " + name + ", config file not set");
	}
	
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
					this.file = new String[1];
					this.file[0] = send;
					writeFile();
					this.neew = false;
				} else { //если через тире
					String send[] = new String[value.length + 1];
					send[0] = name + ":";
					for (int i = 0; i < value.length; i++) {
						send[i+1] = "- " + value[i];
					}
					this.file = send;
					writeFile();
					this.neew = false;
				}
			} else if (file.length > 0) { //если нужно заменить значение в старом конфиге
				ConfigLoader loader = new ConfigLoader();
				loader.fakeload(file);
				int index = ind;
				if (index == -1) index = loader.methods.RecIndexNoSection(name);
				if (skobka) { //если значение в квадратных скобках
					if (index > -1) { //заменяем значение в параметре
						String rep = name + ": [ " + value[0];
						if (file[index].split(":").length > 1) rep = file[index].split(":")[0] + ": [ " + value[0];
						for (int i = 1; i < value.length; i++) {
							rep += ", " + value[i]; 
						}
						rep += " ]";
						this.file[index] = rep;
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
						this.file = new String[newfile.size()];
						for (int i = 0; i < file.length; i++) this.file[i] = newfile.get(i);
						writeFile();
					}
				} else { //если массив через тире
					if (index > -1) {
						String oldfile[] = loader.methods.RecStringArray(index);
						if (oldfile.length == value.length) { //если длинна одинакова, заменяем данные
							int pos = index+1;
							for (int i = 0; i < oldfile.length; i++) {
								if (pos >= file.length) break;
								this.file[pos] = file[pos].split("-")[0] + "- " + value[i];
								pos++;
							}
							writeFile();
						} else { //если нет, расширяем массив file
							ArrayList<String> newfile = new ArrayList<String>();
							for (int i = 0; i <= index; i++) newfile.add(file[i]);
							String prob = "  "; if (index+1 < file.length) loader.methods.RecProbels(index+1);
							for (int i = 0; i < value.length; i++) newfile.add(prob + "- " + value[i]);
							for (int i = index+oldfile.length+1; i < file.length; i++) newfile.add(file[i]);
							this.file = new String[newfile.size()];
							for (int i = 0; i < file.length; i++) file[i] = newfile.get(i);
							writeFile();
						}
					} else { //записываем массив, если его нет в конфиге
						ArrayList<String> newfile = new ArrayList<String>();
						for (int i = 0; i < file.length; i++) newfile.add(file[i]);
						newfile.add(name + ":");
						for (int i = 0; i < value.length; i++) newfile.add("- " + value[i]);
						this.file = new String[newfile.size()];
						for (int i = 0; i < file.length; i++) this.file[i] = newfile.get(i);
						writeFile();
					}
				}
			} else log.info("configWriter: cannot write var " + name + ", array not found");
		} else log.info("configWriter: error write array " + name + ", config file not set");
	}
	
	public void setArray(String name, String[] value, boolean skobka) { //для внешних обращений
		setArray(name, value, skobka, -1);
	}
	
	public void setOptionInSection(String name, String value, String section) { //запись опции в секцию
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeload(file);
				offLog();
				int index = loader.methods.RecIndexInSection(section, name);
				onLog();
				if (index != -1) setOption(name, value, index); else { //если опции нет в конфиге (ее не надо заменять)
					ArrayList<String> newfile = new ArrayList<String>(); //запись секции, если ее нет в конфиге
					for (int i = 0; i < file.length; i++) newfile.add(file[i]);
					int sec = loader.methods.RecIndexSection(section);
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
					this.file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) this.file[i] = newfile.get(i);
					writeFile();
				}
			} else {
				this.file = new String[2];
				this.file[0] = section + ":";
				this.file[1] = "  " + name + ": " + value;
				writeFile();
				this.neew = false;
			}
		} else log.info("configWriter: error write var " + name + " in section " + section + ", config file not set");
	}
	
	public void setArrayInSection(String name, String value[], boolean skobka, String section) { //запись массива в секцию
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeload(file);
				offLog();
				int index = loader.methods.RecIndexInSection(section, name);
				onLog();
				if (index != -1) setArray(name, value, skobka, index); else {
					ArrayList<String> newfile = new ArrayList<String>(); //запись массива, если его нет в конфиге
					for (int i = 0; i < file.length; i++) newfile.add(file[i]);
					int sec = loader.methods.RecIndexSection(section);
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
					this.file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) this.file[i] = newfile.get(i);
					loader.fakeload(file);
					index = loader.methods.RecIndexInSection(section, name);
					setArray(name, value, skobka, index);
				}
			} else {
				if (skobka) {
					this.file = new String[2];
					this.file[0] = section + ":";
					String send = "  " + name + ": [ " + value[0];
					for (int i = 1; i < value.length; i++) { //парсим в строку
						send += ", " + value[i]; 
					}
					send += " ]";
					this.file[1] = send;
					writeFile();
					this.neew = false;
				} else {
					this.file = new String[value.length + 2];
					this.file[0] = section + ":";
					this.file[1] = "  " + name + ":";
					for (int i = 2; i < file.length; i++) {
						this.file[i] = "  - " + value[i-2];
					}
					writeFile();
					this.neew = false;
				}
			}
		} else log.info("configWriter: error write array " + name + " in section " + section + ", config file not set");
	}
	
	private void delOption(String name, int ind) { //удаление опции из конфига
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeload(file);
				int index = ind; if (index == -1) index = loader.methods.RecIndexNoSection(name);
				if (index > -1) {
					ArrayList<String> newfile = new ArrayList<String>();
					for (int i = 0; i < index; i++) newfile.add(file[i]);
					for (int i = index+1; i < file.length; i++) newfile.add(file[i]);
					this.file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) this.file[i] = newfile.get(i);
					writeFile();
				} else log.info("configWriter: error delete var " + name + ", var not found");
			} else log.info("configWriter: error delete var " + name + ", config file not found");
		} else log.info("configWriter: error delete var " + name + ", config file not set");
	}
	
	public void delOption(String name) { //для внешних обращений
		delOption(name, -1);
	}
	
	private void delArray(String name, int ind) { //удаление массива
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeload(file);
				int index = ind; if (index == -1) index = loader.methods.RecIndexNoSection(name);
				if (index > -1) {
					boolean skobka = loader.methods.IsArray(file[index], index).isSkobka;
					if (skobka) {
						delOption(name, index);
					} else {
						int leng = loader.methods.RecStringArray(index).length;
						ArrayList<String> newfile = new ArrayList<String>();
						for (int i = 0; i < index; i++) newfile.add(file[i]);
						for (int i = index+leng+1; i < file.length; i++) newfile.add(file[i]);
						this.file = new String[newfile.size()];
						for (int i = 0; i < file.length; i++) this.file[i] = newfile.get(i);
						writeFile();
					}
				} else log.info("configWriter: error delete array " + name + ", array not found");
			} else log.info("configWriter: error delete array " + name + ", config file not found");
		} else log.info("configWriter: error delete array " + name + ", config file not set");
	}
	
	public void delArray(String name) { //для внешних обращений
		delArray(name, -1);
	}
	
	public void delOptionInSection(String name, String section) { //удаление параметра из секции
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeload(file);
				int index = loader.methods.RecIndexInSection(section, name);
				if (index > -1) {
					delOption(name, index);
				} else log.info("configWriter: error delete var " + name + " from section " + section + ", var not found");
			} else log.info("configWriter: error delete var " + name + " from section " + section + ", config file not found");
		} else log.info("configWriter: error delete var " + name + " from section " + section + ", config file not set");
	}
	
	public void delArrayInSection(String name, String section) { //удаление массива из секции
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeload(file);
				int index = loader.methods.RecIndexInSection(section, name);
				if (index > -1) {
					delArray(name, index);
				} else log.info("configWriter: error delete array " + name + " from section " + section + ", array not found");
			} else log.info("configWriter: error delete array " + name + " from section " + section + ", config file not found");
		} else log.info("configWriter: error delete array " + name + " from section " + section + ", config file not set");
	}
	
	public void delSection(String section) {
		if (set && patch != null) {
			if (!neew) {
				ConfigLoader loader = new ConfigLoader();
				loader.fakeload(file);
				int index = loader.methods.RecIndexSection(section);
				if (index > -1) {
					int leng = loader.getSectionRealLength(section);
					ArrayList<String> newfile = new ArrayList<String>();
					for (int i = 0; i < index; i++) newfile.add(file[i]);
					for (int i = index+leng+1; i < file.length; i++) newfile.add(file[i]);
					this.file = new String[newfile.size()];
					for (int i = 0; i < file.length; i++) this.file[i] = newfile.get(i);
					writeFile();
				} else log.info("configWriter: error delete section " + section + ", section not found");
			} else log.info("configWriter: error delete section " + section + ", config file not found");
		} else log.info("configWriter: error delete section " + section + ", config file not set");
	}
	
	public boolean removeConfig() { //удалить весь конфиг (сам файл)
		boolean result = false;
		if (set && patch != null) {
			if (!neew) {
				File conf = new File(patch);
				if (conf.isFile()) result = conf.delete(); else log.info("configWriter: error delete config file, not found");
			} else log.info("configWriter: error delete config file, not found");
		} else log.info("configWriter: error delete config file, not set");
		return result;
	}
	
	public class Methods { //класс со внутренними методами
		public void SetOption(String name, String value, int index) { //установить опцию по индексу
			setOption(name, value, index);
		}
		public void SetArray(String name, String[] value, boolean skobka, int index) { //установить массив по индексу
			setArray(name, value, skobka, index);
		}
		public void DelOption(String name, int index) { //удалить опцию по индексу
			delOption(name, index);
		}
		public void DelArray(String name, int index) { //удалить массив по индексу
			delArray(name, index);
		}
		public Methods getMethods() {Methods m = new Methods(); return m;} //получить весь класс
	}
	public Methods methods = new Methods();
	
	public ConfigWriter getWriter() { //получить весь класс
		ConfigWriter w = new ConfigWriter();
		return w;
	}
	
	public void writeArray(String[] array) { //запись массива строк в файл как есть (создает новый конфиг или полностью перезаписывает текущий)
		if (set && patch != null) {
			if (array != null) {
				if (array.length > 0) {
					this.file = array;
					writeFile();
					setConfig(patch); //обновляем инфу
				}
			}
		} else log.info("configWriter: error write array, config not set");
	}
}