package hactarce.cfunge;

import java.util.Locale;

class Vector {

	final int x;
	final int y;

	static Vector ORIGIN = new Vector(0, 0);
	static Vector NORTH = new Vector(-1, 0);
	static Vector SOUTH = new Vector(1, 0);
	static Vector EAST = new Vector(0, 1);
	static Vector WEST = new Vector(0, -1);

	Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}

	Vector add(Vector vector) {
		return new Vector(x + vector.x, y + vector.y);
	}

	Vector add(int x, int y) {
		return new Vector(this.x + x, this.y + y);
	}

	Vector sub(Vector vector) {
		return new Vector(x - vector.x, y - vector.y);
	}

	Vector sub(int x, int y) {
		return new Vector(this.x - x, this.y - y);
	}

	Vector scale(Vector vector) {
		return new Vector(x * vector.x, y * vector.y);
	}

	Vector scale(int x, int y) {
		return new Vector(this.x * x, this.y * y);
	}

	Vector scale(int scalar) {
		return new Vector(x * scalar, y * scalar);
	}

	Vector rotateRight() {
		return new Vector(-y, x);
	}

	Vector rotateLeft() {
		return new Vector(y, -x);
	}

	Vector setX(int x) {
		return new Vector(x, y);
	}

	Vector setY(int y) {
		return new Vector(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Vector && ((Vector) obj).x == x && ((Vector) obj).y == y;
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "(%d, %d)", x, y);
	}

}
