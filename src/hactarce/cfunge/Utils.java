package hactarce.cfunge;

class Utils {

	private Utils() {
	}

	static int mod(int divisor, int dividend) {
		return (((divisor % dividend) + dividend) % dividend);
	}

}
