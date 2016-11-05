package hactarce.cfunge;

class Vector {

	int x;
	int y;

	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}

	Vector add(Vector vector) {
		return new Vector(x + vector.x, y + vector.y);
	}

	void Add(Vector vector) {
		this.x += vector.x;
		this.y += vector.y;
	}

}
