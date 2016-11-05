package hactarce.cfunge;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

class CFungeIP {

	final FungeWorld world;
	Vector pos = Vector.ORIGIN;
	Vector delta = Vector.EAST;
	StringBuilder stringBuffer;
	Deque<Callback> callStack = new ArrayDeque<>();
	StringMode stringMode = null;

	CFungeIP(FungeWorld world) {
		this.world = world;
	}

	void step() {
		exec((char) world.get(pos));
	}

	private void exec(char instruction) {
		if (stringMode == null) switch (instruction) {
			case 0x02: //region QUOTE N CHARS CLASSICAL/END STRING CLASSICAL
				stringBuffer = new StringBuilder();
				stringMode = StringMode.STRING_CLASSIC;
				break; //endregion
			case 0x03: //region QUOTE N CHARS FORWARD/END STRING FORWARD
				stringBuffer = new StringBuilder();
				stringMode = StringMode.STRING_CORRECTED;
				break; //endregion
			case 0x07: //region BEEP
				Toolkit.getDefaultToolkit().beep();
				break; //endregion
			case 0x09: //region STOP VERT (stop vertical movement)
				pos = pos.setY(0);
				break; //endregion
			case 0x0B: //region STOP HORIZ (stop horizontal movement)
				pos = pos.setX(0);
				break; //endregion
			case 0x18: //region NORTH
				delta = Vector.NORTH;
				break; //endregion
			case 0x19: //region SOUTH
				delta = Vector.SOUTH;
				break; //endregion
			case 0x1A: //region EAST
				delta = Vector.EAST;
				break; //endregion
			case 0x1B: //region WEST
				delta = Vector.WEST;
				break; //endregion
			case '!': // INVERT

				break; //endregion
		}
		else switch (stringMode) {

		}
		pos = pos.add(delta);
	}

	private enum StringMode {STRING, STRING_CLASSIC, STRING_CORRECTED}

	private interface Callback {
		void onEnd();
	}

	private class Stack {

		boolean pushBottom = false;
		boolean popBottom = false;

		ArrayList<Integer> stack = new ArrayList<>();

		Stack() {
		}

		void push(int value) {
			if (pushBottom) stack.add(0, value);
			else stack.add(value);
		}

		void push(int value, int depth) {
			if (pushBottom) stack.add(Utils.mod(depth, stack.size()), value);
			else stack.add(stack.size() - Utils.mod(depth, stack.size()), value);
		}

		int pop() {
			if (stack.size() == 0) return 0;
			if (popBottom) return stack.remove(0);
			else return stack.remove(stack.size() - 1);
		}

		int pop(int depth) {
			if (stack.size() == 0) return 0;
			if (popBottom) return stack.remove(Utils.mod(depth, stack.size()));
			else return stack.remove(stack.size() - Utils.mod(depth, stack.size()));
		}

		int peek() {
			if (stack.size() == 0) return 0;
			if (popBottom) return stack.get(0);
			else return stack.get(stack.size() - 1);
		}

		int peek(int depth) {
			if (stack.size() == 0) return 0;
			if (popBottom) return stack.get(Utils.mod(depth, stack.size()));
			else return stack.get(stack.size() - Utils.mod(depth, stack.size()));
		}

		void pushVector(Vector vector) {
			push(vector.y);
			push(vector.x);
		}

		Vector popVector() {
			int y = pop();
			return new Vector(pop(), y);
		}

		void pushString(String string) {
			push(0);
			for (char c : string.toCharArray()) push(c);
		}

		String popString() {
			StringBuilder stringBuilder = new StringBuilder();
			int v;
			do if ((v = pop()) != 0) {
				stringBuilder.append((char) v);
			} while (v != 0);
			return stringBuilder.toString();
		}

	}

}
