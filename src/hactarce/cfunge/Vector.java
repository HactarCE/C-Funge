package hactarce.cfunge;

class Vector {

	int x;
	int y;

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

	static Vector NORTH = new Vector(-1, 0);
	static Vector SOUTH = new Vector(1, 0);
	static Vector EAST = new Vector(0, 1);
	static Vector WEST = new Vector(0, -1);

}
