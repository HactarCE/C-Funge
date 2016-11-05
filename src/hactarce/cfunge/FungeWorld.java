package hactarce.cfunge;

import java.util.Arrays;
import java.util.stream.IntStream;

class FungeWorld {

	int[][] world;
	Vector size;

	FungeWorld(int w, int h) {
		world = newWorld(w, h);
		System.out.println(Arrays.deepToString(world));
	}

	void setSize(Vector vector) {
		setSize(vector.x, vector.y);
	}

	void setSize(int w, int h) {
		size = new Vector(w, h);
		int[][] newWorld = newWorld(w, h);
		int copyX = Integer.min(w, size.x);
		int copyY = Integer.min(h, size.y);
		for (int y = 0; y < copyY; y++) {
			System.arraycopy(world[y], 0, newWorld[y], 0, copyX);
		}
	}

	Vector getSize() {
		return size;
	}

	int[][] newWorld(int w, int h) {
		int[][] newWorld = new int[h][w];
		for (int y = 0; y < h; y++) {
			newWorld[y] = IntStream.generate(() -> ' ').limit(w).toArray();
		}
		return newWorld;
	}

	int get(Vector vector) {
		return get(vector.x, vector.y);
	}

	int get(int x, int y) {
		return world[wrapX(x)][wrapY(y)];
	}

	void put(int value, Vector vector) {
		put(value, vector.x, vector.y);
	}

	void put(int value, int x, int y) {
		world[wrapX(x)][wrapY(y)] = value;
	}

	int wrapX(int x) {
		return Utils.mod(x, size.x);
	}

	int wrapY(int y) {
		return Utils.mod(y, size.y);
	}

	Vector wrap(Vector vector) {
		return new Vector(wrapX(vector.x), wrapY(vector.y));
	}

}
