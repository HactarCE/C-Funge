package hactarce.cfunge;

import java.util.HashMap;
import java.util.Map;

class Cell {

	static final Map<Integer, Character> customEncoding = new HashMap<Integer, Character>() {{
		put(0x02, '‘');
		put(0x03, '’');
		put(0x07, '♪');
		put(0x09, '↔');
		put(0x0A, '¶');
		put(0x0B, '↕');
		put(0x11, '▲');
		put(0x12, '▼');
		put(0x13, '►');
		put(0x14, '◄');
		put(0x18, '↑');
		put(0x19, '↓');
		put(0x1A, '→');
		put(0x1B, '←');
	}};

	private Cell() {
	}

	static char c(int value) {
		if (customEncoding.containsKey(value)) {
			return customEncoding.get(value);
		} else return (char) value;
	}

	static boolean b(int value) {
		return value != 0;
	}

	static int v(boolean truish) {
		return truish ? 1 : 0;
	}

}
