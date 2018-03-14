package ru.intervi.littleconfig.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.intervi.littleconfig.ConfigLoader;

/**
 * класс для парсинга данных из {@link ru.intervi.littleconfig.ConfigLoader} и обеспечения удобного доступа к ним
 */
public class ParsedConfig {
	public final Map<String, String> STRINGS;
	public final Map<String, Byte> BYTES;
	public final Map<String, Short> SHORTS;
	public final Map<String, Integer> INTEGERS;
	public final Map<String, Long> LONGS;
	public final Map<String, Float> FLOATS;
	public final Map<String, Double> DOUBLES;
	public final Map<String, Boolean> BOOLEANS;
	public final Map<String, String[]> STRING_ARRAYS;
	public final Map<String, byte[]> BYTE_ARRAYS;
	public final Map<String, short[]> SHORT_ARRAYS;
	public final Map<String, int[]> INTEGER_ARRAYS;
	public final Map<String, long[]> LONG_ARRAYS;
	public final Map<String, float[]> FLOAT_ARRAYS;
	public final Map<String, double[]> DOUBLE_ARRAYS;
	public final Map<String, boolean[]> BOOLEAN_ARRAYS;
	public final Map<String, ConfigLoader> SECTIONS;
	
	/**
	 * основной конструктор
	 * @param loader загруженный {@link ru.intervi.littleconfig.ConfigLoader}
	 * @param stringsOnly true - не парсить типы, загружать всё в STRINGS и STRING_ARRAYS
	 */
	public ParsedConfig(ConfigLoader loader, boolean stringsOnly) {
		HashMap<String, String> strings = new HashMap<>();
		HashMap<String, Byte> bytes = new HashMap<>();
		HashMap<String, Short> shorts = new HashMap<>();
		HashMap<String, Integer> integers = new HashMap<>();
		HashMap<String, Long> longs = new HashMap<>();
		HashMap<String, Float> floats = new HashMap<>();
		HashMap<String, Double> doubles = new HashMap<>();
		HashMap<String, Boolean> booleans = new HashMap<>();
		HashMap<String, String[]> stringArrays = new HashMap<>();
		HashMap<String, byte[]> byteArrays = new HashMap<>();
		HashMap<String, short[]> shortArrays = new HashMap<>();
		HashMap<String, int[]> integerArrays = new HashMap<>();
		HashMap<String, long[]> longArrays = new HashMap<>();
		HashMap<String, float[]> floatArrays = new HashMap<>();
		HashMap<String, double[]> doubleArrays = new HashMap<>();
		HashMap<String, boolean[]> booleanArrays = new HashMap<>();
		HashMap<String, ConfigLoader> sections = new HashMap<>();
		if (loader.isLoad()) {
			for (String name : loader.getOptionNames()) {
				if (stringsOnly) {
					if (loader.isSetArray(name)) stringArrays.put(name, loader.getStringArray(name));
					else strings.put(name, loader.getString(name));
					continue;
				}
				switch(loader.getType(name)) {
				case STRING:
					strings.put(name, loader.getString(name));
					break;
				case BYTE:
					bytes.put(name, loader.getByte(name));
					break;
				case SHORT:
					shorts.put(name, loader.getShort(name));
					break;
				case INT:
					integers.put(name, loader.getInt(name));
					break;
				case LONG:
					longs.put(name, loader.getLong(name));
					break;
				case FLOAT:
					floats.put(name, loader.getFloat(name));
					break;
				case DOUBLE:
					doubles.put(name, loader.getDouble(name));
					break;
				case BOOLEAN:
					booleans.put(name, loader.getBoolean(name));
					break;
				case STRING_ARRAY:
					stringArrays.put(name, loader.getStringArray(name));
					break;
				case BYTE_ARRAY:
					byteArrays.put(name, loader.getByteArray(name));
					break;
				case SHORT_ARRAY:
					shortArrays.put(name, loader.getShortArray(name));
					break;
				case INT_ARRAY:
					integerArrays.put(name, loader.getIntArray(name));
					break;
				case LONG_ARRAY:
					longArrays.put(name, loader.getLongArray(name));
					break;
				case FLOAT_ARRAY:
					floatArrays.put(name, loader.getFloatArray(name));
					break;
				case DOUBLE_ARRAY:
					doubleArrays.put(name, loader.getDoubleArray(name));
					break;
				case BOOLEAN_ARRAY:
					booleanArrays.put(name, loader.getBooleanArray(name));
					break;
				default:
					continue;
				}
			}
			for (String name : loader.getSectionNames()) {
				sections.put(name, loader.getSection(name));
			}
		}
		STRINGS = Collections.unmodifiableMap(strings);
		BYTES = Collections.unmodifiableMap(bytes);
		SHORTS = Collections.unmodifiableMap(shorts);
		INTEGERS = Collections.unmodifiableMap(integers);
		LONGS = Collections.unmodifiableMap(longs);
		FLOATS = Collections.unmodifiableMap(floats);
		DOUBLES = Collections.unmodifiableMap(doubles);
		BOOLEANS = Collections.unmodifiableMap(booleans);
		STRING_ARRAYS = Collections.unmodifiableMap(stringArrays);
		BYTE_ARRAYS = Collections.unmodifiableMap(byteArrays);
		SHORT_ARRAYS = Collections.unmodifiableMap(shortArrays);
		INTEGER_ARRAYS = Collections.unmodifiableMap(integerArrays);
		LONG_ARRAYS = Collections.unmodifiableMap(longArrays);
		FLOAT_ARRAYS = Collections.unmodifiableMap(floatArrays);
		DOUBLE_ARRAYS = Collections.unmodifiableMap(doubleArrays);
		BOOLEAN_ARRAYS = Collections.unmodifiableMap(booleanArrays);
		SECTIONS = Collections.unmodifiableMap(sections);
	}
}
