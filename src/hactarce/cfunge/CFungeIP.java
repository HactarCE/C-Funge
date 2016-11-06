package hactarce.cfunge;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;

class CFungeIP {

	final FungeWorld world;
	Vector pos = Vector.ORIGIN;
	Vector delta = Vector.EAST;
	Vector origin = Vector.ORIGIN;
	private StringBuilder stringBuilder;
	private Deque<Callback> callStack = new ArrayDeque<>();
	private Stack stack = new Stack();
	private Deque<Stack> metaStack = new ArrayDeque<>();
	boolean stringMode = false;
	private Random random = new Random();

	CFungeIP(FungeWorld world) {
		this.world = world;
	}

	void stepForward() throws StopException {
		exec((char) world.get(pos));
	}

	private void exec(char instruction) throws StopException {
		if (stringMode) {
			switch (instruction) {
				default:
					stringBuilder.append(instruction);
				case 0x02: //region QUOTE N CHARS CLASSICAL/END STRING CLASSICAL
					stack.pushString(stringBuilder.toString());
					stringBuilder = null;
					stringMode = false;
					break; //endregion
				case 0x03: //region QUOTE N CHARS FORWARD/END STRING FORWARD
					stack.pushString(stringBuilder.reverse().toString());
					stringBuilder = null;
					stringMode = false;
					break; //endregion
			}
		} else {
			int a, b;
			Vector vector;
			Vector oldPos = pos;
			Vector oldDelta = delta;
			Vector oldOrigin = origin;
			switch (instruction) {
				//region INSTRUCTIONS
				case 0x02: //region QUOTE N CHARS CLASSICAL/END STRING CLASSICAL
					stringBuilder = new StringBuilder();
					for (int i = stack.pop(); i > 0; i--) {
						moveForward();
						stringBuilder.append(world.get(pos));
					}
					stack.pushString(stringBuilder.toString());
					stringBuilder = null;
					break; //endregion
				case 0x03: //region QUOTE N CHARS FORWARD/END STRING FORWARD
					stringBuilder = new StringBuilder();
					for (int i = stack.pop(); i > 0; i++) {
						moveForward();
						stringBuilder.append(world.get(pos));
					}
					stack.pushString(stringBuilder.reverse().toString());
					stringBuilder = null;
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
				case '!': //region INVERT
					stack.push(!Cell.b(stack.pop()));
					break; //endregion
				case '"': //region BEGIN STRING
					stringMode = true;
					break; //endregion
				case '#': //region JUMP
					moveForward();
					break; //endregion
				case '$': //region POP
					stack.pop();
					break; //endregion
				case '%': //region MODULUS
					b = stack.pop();
					stack.push(Utils.mod(stack.pop(), b));
					break; //endregion
				case '&': //region SWAP TOP TWO STACK ITEMS
					stack.push(1, stack.pop());
					break; //endregion
				case '\'': //region QUOTE CHARACTER
					moveForward();
					stack.push(world.get(pos));
					break; //endregion
				case '(': //region [RESERVED]
					break; //endregion
				case ')': //region [RESERVED]
					break; //endregion
				case '*': //region SCALE DELTA VECTOR
					delta = delta.scale(stack.pop());
					break; //endregion
				case '+': //region ADD
					stack.push(stack.pop() + stack.pop());
					break; //endregion
				case ',': //region OUTPUT CHAR (TODO: REDIRECT OUTPUT)
					System.out.println(Cell.c(stack.pop()));
					break; //endregion
				case '-': //region SUBTRACT
					b = stack.pop();
					stack.push(stack.pop() - stack.pop());
					break; //endregion
				case '.': //region INPUT INTEGER (TODO: NYI)
					stack.push(0);
					break; //endregion
				case '/': //region REFLECT FORWARD
					delta = new Vector(-delta.y, -delta.x);
					break; //endregion
				case '0': //region LITERAL 0-9
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					stack.push(instruction - '0');
					break; //endregion
				case ':': //region DUPLICATE
					stack.push(stack.peek());
					break; //endregion
				case ';': //region TOGGLE IGNORE
					ignoreMode();
					break; //endregion
				case '<': //region LESS THAN
					b = stack.pop();
					stack.push(stack.pop() < b);
					break; //endregion
				case '=': //region IF
					moveForward();
					if (world.get(pos) != '{' ^ Cell.b(stack.pop())) {
						moveBackward();
					}
					break; //endregion
				case '>': //region GREATER THAN
					b = stack.pop();
					stack.push(stack.pop() > b);
					break; //endregion
				case '?': //region RANDOM DIRECTION
					switch (random.nextInt(4)) {
						default:
							delta = Vector.NORTH;
							break;
						case 1:
							delta = Vector.SOUTH;
							break;
						case 2:
							delta = Vector.EAST;
							break;
						case 3:
							delta = Vector.WEST;
							break;
					}
					break; //endregion
				case '@': //region SET ORIGIN (RELATIVE)
					origin = pos.add(stack.popVector());
					break; //endregion
				case 'A': //region ABSOLUTE VALUE
					stack.push(Math.abs(stack.pop()));
					break; //endregion
				case 'B': //region [RESERVED]
					break; //endregion
				case 'C': //region CALL GLOBAL SUBROUTINE
					origin = pos = stack.popVector();
					callStack.add(new Callback(() -> {
						pos = oldPos;
						delta = oldDelta;
						origin = oldOrigin;
						moveForward();
					}));
					break; //endregion
				case 'D': //region DECREMENT
					stack.push(stack.pop() - 1);
					break; //endregion
				case 'E': //region EXECUTE INSTRUCTION
					exec((char) stack.pop());
					return; //endregion
				case 'F': //region [RESERVED]
					break; //endregion
				case 'G': //region GET FROM STACK
					stack.push(stack.peek(stack.pop()));
					break; //endregion
				case 'H': //region [RESERVED]
					break; //endregion
				case 'I': //region INCREMENT
					stack.push(stack.pop() + 1);
					break; //endregion
				case 'J': //region RELATIVE JUMP
					pos = stack.popVector().add(origin);
					break; //endregion
				case 'K': //region REPEAT WITH CHUNKS (TODO: NYI)
					break; //endregion
				case 'L': //region LOCAL SUBROUTINE
					pos = stack.popVector().add(origin);
					callStack.add(new Callback(() -> {
						pos = oldPos;
						delta = oldDelta;
						origin = oldOrigin;
						moveForward();
					}));
					break; //endregion
				case 'M': //region [RESERVED]
					break; //endregion
				case 'N': //region [RESERVED]
					break; //endregion
				case 'O': //region META-STACK SIZE
					stack.push(metaStack.size());
					break; //endregion
				case 'P': //region PUT INTO STACK
					stack.push(stack.pop(), stack.pop());
					break; //endregion
				case 'Q': //region [RESERVED]
					break; //endregion
				case 'R': //region RETURN FROM SUBROUTINE/BREAK FROM LOOP
					if (callStack.size() == 0) delta = Vector.ORIGIN;
					else callStack.pop().onReturn();
					break; //endregion
				case 'S': //region [RESERVED]
					break; //endregion
				case 'T': //region TRANSPOSE
					int h = stack.pop();
					int w = stack.pop();
					int[][] array = new int[w][h];
					for (int y = 0; y < h; y++) {
						for (int x = 0; x < w; x++) {
							array[x][y] = stack.pop();
						}
					}
					for (int x = 0; x < w; x++) {
						for (int y = 0; y < h; y++) {
							stack.push(array[x][y]);
						}
					}
					break; //endregion
				case 'U': //region PUSH TO META-STACK
					metaStack.push(stack);
					break; //endregion
				case 'V': //region GET COMMAND-LINE ARG (TODO: NYI)
					stack.pushString("");
					break; //endregion
				case 'W': //region WHILE
					moveForward();
					if (Cell.b(stack.peek())) {
						if (world.get(pos) == '{') {
							callStack.push(new Callback(() -> {
							}, () -> {
								if (Cell.b(stack.peek())) {
									pos = oldPos;
									delta = oldDelta;
									origin = oldOrigin;
								} else callStack.pop();
							}));
						} else {
							stepForward();
							pos = oldPos;
							delta = oldDelta;
							origin = oldOrigin;
						}
						return;
					}
					break; //endregion
				case 'X': //region GET IP DELTA
					stack.pushVector(delta);
					break; //endregion
				case 'Y': //region GET ORIGIN
					stack.pushVector(origin);
					break; //endregion
				case 'Z': //region ROTATE STACK UP
					b = stack.pop();
					for (int i = 0; i < b; i++) {
						stack.push(-1, stack.pop());
					}
					break; //endregion
				case '[': //region TURN LEFT
					delta = delta.rotateLeft();
					break; //endregion
				case '\\': //region REFLECT BACKWARD
					delta = new Vector(delta.y, delta.x);
					break; //endregion
				case ']': //region TURN RIGHT
					delta = delta.rotateRight();
					break; //endregion
				case '^': //region EXPONENTIATE
					b = stack.pop();
					a = stack.pop();
					for (int i = 0; i < b; i++) {
						a *= a;
					}
					stack.push(a);
					break; //endregion
				case '_': //region [RESERVED]
					break; //endregion
				case '`': //region INPUT CHAR (TODO: NYI)
					break; //endregion
				case 'a': //region LITERAL 10-15
				case 'b':
				case 'c':
				case 'd':
				case 'e':
				case 'f':
					stack.push(instruction - 'a' + 10);
					break; //endregion
				case 'g': //region GET CHAR
					stack.push(world.get(stack.popVector().add(origin)));
					break; //endregion
				case 'h': //region HEX LITERAL
					stack.push(0);
					boolean isHex = true;
					while (isHex) {
						moveForward();
						b = world.get(pos);
						if ('0' <= b && b <= '9') {
							stack.push((stack.pop() << 4) + b - '0');
						} else if ('a' <= b && b <= 'f') {
							stack.push((stack.pop() << 4) + b - 'a' + 10);
						} else isHex = false;
					}
					return; //endregion
				case 'i': //region OUTPUT INTEGER
					System.out.println(stack.pop());
					break; //endregion
				case 'j': //region JUMP FORWARD
					pos = world.wrap(pos.add(delta.scale(stack.pop())));
					break; //endregion
				case 'k': //region REPEAT
					b = stack.pop();
					moveForward();
					if (b > 0) {
						if (world.get(pos) == '{') {
							callStack.push(new Callback(() -> {
							}, () -> {
								if (b - 1 > 0) {
									stack.push(b - 1);
									pos = oldPos;
									delta = oldDelta;
									origin = oldOrigin;
								} else callStack.pop();
							}, false));
						} else {
							stepForward();
							pos = oldPos;
							delta = oldDelta;
							origin = oldOrigin;
							stack.push(b + 1);
						}
						return;
					}
					break; //endregion
				case 'l': //region [RESERVED]
					break; //endregion
				case 'm': //region [RESERVED]
					break; //endregion
				case 'n': //region CLEAR STACK
					stack.clear();
					break; //endregion
				case 'o': //region GET STACK SIZE
					stack.push(stack.size());
					break; //endregion
				case 'p': //region PUT CHAR
					vector = stack.popVector();
					world.put(stack.pop(), vector);
					break; //endregion
				case 'q': //region QUIT
					delta = Vector.ORIGIN;
					break; //endregion
				case 'r': //region REFLECT
					delta = delta.scale(-1);
					break; //endregion
				case 's': //region [RESERVED]
					break; //endregion
				case 't': //region [RESERVED]
					break; //endregion
				case 'u': //region POP FROM META-STACK
					stack = metaStack.pop();
					break; //endregion
				case 'v': //region [RESERVED]
					break; //endregion
				case 'w': //region COMPARE SPLIT
					b = stack.pop();
					a = stack.pop();
					if (a > b) delta = delta.rotateRight();
					if (a < b) delta = delta.rotateLeft();
					break; //endregion
				case 'x': //region SET IP DELTA
					delta = stack.popVector();
					break; //endregion
				case 'y': //region SET ORIGIN (GLOBAL)
					origin = stack.popVector();
					break; //endregion
				case 'z': //region ROTATE STACK DOWN
					b = stack.pop();
					for (int i = 0; i < b; i++) {
						stack.push(stack.pop(-1));
					}
					break; //endregion
				case '{': //region BEGIN IGNORE/BEGIN BLOCK
					ignoreMode();
					break; //endregion
				case '}': //region END IGNORE/END BLOCK
					if (callStack.size() > 0 && callStack.peek().onWeakReturn != null) {
						callStack.pop().onWeakReturn();
						return;
					}
					break; //endregion
				case '~': //region INPUT LINE (TODO: NYI)
					break; //endregion
				// endregion
			}
		}
		if (delta.equals(Vector.ORIGIN)) throw new StopException();
		moveForward();
		if (callStack.size() > 0 && callStack.peek().instantWeakReturn) {
			callStack.pop().onWeakReturn();
		}
	}

	private void ignoreMode() {
		Vector oldPos = pos;
		moveForward();
		do moveForward();
		while (world.get(pos) != ';' && world.get(pos) != '}' && !pos.equals(oldPos));
	}

	private void moveForward() {
		pos = world.wrap(pos.add(delta));
	}

	private void moveBackward() {
		pos = world.wrap(pos.sub(delta));
	}

	private class Callback {
		private Runnable onReturn = null;
		private Runnable onWeakReturn = null;
		private boolean instantWeakReturn = false;

		Callback(Runnable onReturn) {
			this.onReturn = onReturn;
		}

		Callback(Runnable onReturn, Runnable onWeakReturn, boolean instantWeakReturn) {
			this.onReturn = onReturn;
			this.onWeakReturn = onWeakReturn;
			this.instantWeakReturn = instantWeakReturn;
		}

		void onReturn() {
			onReturn.run();
		}

		void onWeakReturn() {
			if (onWeakReturn != null) onWeakReturn.run();
		}
	}

	private class Stack {

		private ArrayList<Integer> stack = new ArrayList<>();

		boolean pushBottom = false;
		boolean popBottom = false;

		Stack() {
		}

		void push(boolean truish) {
			push(truish ? 1 : 0);
		}

		void push(int depth, boolean truish) {
			push(truish ? 1 : 0);
		}

		void push(int value) {
			if (pushBottom) stack.add(0, value);
			else stack.add(value);
		}

		void push(int depth, int value) {
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

		void clear() {
			stack = new ArrayList<>();
		}

		int size() {
			return stack.size();
		}

	}

	class StopException extends Exception {
	}

}
