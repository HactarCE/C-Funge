#### This README is incomplete, and will be consistently updated. Not all implemented features are listed here, and not all features here are implemented.

# C-Funge

C-Funge is a Fungeiod, inspired by Befunge. Befunge-93, the first public version of Befunge, is lacking in features and restricted to a small 80x25 "world," making it impractical for larger programs. To the contrary, Befunge-98 is bloated with instructions that are vaguely documented and difficult to use, let alone impelment. C-Funge aims to be a compromise between the two, with a plethora of instructions practical for general use and code golfing alike.

This language is constantly changing, and any code written in it could break without warning. USE AT YOUR OWN RISK!



## Encoding

While Befunge limits itself to standard ASCII, C-Funge uses a custom encoding based very heavily on standard extended ASCII/Unicode, with a few custom characters in the 01-1F range:

- `0x02` = `‘`
- `0x03` = `’`
- `0x0A` = `¶` (for display purposes)
- `0x18` = `↑`
- `0x19` = `↓`
- `0x1A` = `→`
- `0x1B` = `←`

All of these symbols have been chosen to display properly in Windows Command Prompt (and likely other terminals). While this custom encoding is somewhat inconvenient to work with, most other instructions are simple letters and symbols that can be easily typed with an English US or international keyboard layout.



## Examples

Here are a few examples to get a feel for the language:

#### Hello, world!
	"Hello, world!’W,q

- `"` - Begin quoting a string.
- `Hello, world!` - Push the letters of `Hello, world!` onto the string buffer, with `H` at the bottom and `!` at the top.
- `’` - Push the buffer onto the stack in reverse order (causing 'H' to be at the top).
- `W` - While the top of the stack is not zero...
	- `,` - Pop an ASCII character off the stack and output it.
- `q` - Quit the program.

#### Cat program

	~W,q

- `~` - Input string.
- `W,q` - Print string and exit (as seen in the previous example).

#### Fibonacci sequence

	01.Dk{1G1G+}Wiq

- `01` - Push the numbers 0 and 1 onto the stack.
- `.` - Input an integer (the number of Fibonacci numbers to calculate).
- `D` - Decrement the integer (because we already know the first number -- 1).
- `k{...}` - Pop a number off the stack and do the following that many times...
	- `1G1G` - Grab the second item from the top of the stack, twice. (This effectively duplicates the top two items.)
	- `+` - Add the top two items of the stack.
- `Wi` - While the top of the stack is not zero, pop a number off and output it.
- `q` - Quit the program.

#### Factorial

	0.:Dk{1zI:1Z*}iq

- `0` - Push a zero onto the stack.
- `.` - Input an integer `n`, the number of which we'll be calculating the factorial.
- `D` - Decrement the top item of the stack.
- `k{...}` - Pop a number off the stack (`n-1`) and do the following that many times...
	- `1z` - Rotate the stack down by one, bringing the bottommost item to the top.
	- `I` - Increment the number at the top of the stack
	- `:1Z` - Duplicate it and rotate the stack back. (This number is now at the bottom and top of the stack.)
	- `*` - Multiply the top two stack items.
- `iq` - Output integer and quit.

#### I'm M.C. Quine and I'm here to say...

	7’2zW,q

- `7` - Push the number 7 onto the stack.
- `’` - Push the next 7 characters onto the stack in reverse order. (When the instruction pointer reaches the end of the program, it loops around and quotes the `7’` as well.) The stack is now the string `2zW,q7’`.
- `2` - Push the number 2 onto the stack.
- `z` - Rotate the stack down by two (bottom two items of the stack are now on top)
- `W,q` - Print string and exit

#### Quine with arbitrary-length 1-dimensional payload

	{PAYLOAD GOES HERE}n’"'"2aZW,q

- `{...}` - When reaching `{`, skip to the next `;` or `}`. (If there were really code there, the braces would be removed and the code would be executed as normal.)
- `n` - Clear the stack (in case there's something left over from the payload
- `’` - Quote 0 characters (do nothing).
- `"` - Copies text until next `‘` or `’`. After wrapping around, the instruction pointer reaches `’` again and pushes the string onto the stack with `'` (the first quoted character) on top and `n` (the last quoted character) on the bottom. The stack is now `’"'"2aZW,q{PAYLOAD GOES HERE}n`.
- `'` - Instead of executing the next instruction (`"`), push its ASCII value onto the stack.
- `2` - Push a the number 2 (the value of `’`) onto the stack.
- `a` - Push the number 10 (hexadecimal `0xA`) onto the stack.
- `Z` - Rotate the stack up by ten (putting `’"'"2aZW,q` at the bottom`).
- `W,q` - Print string and exit.

#### Hyperquine

	IÌ*'!’6zW{1ìk:1ìIk,}q

*(In this explanation, I will use `n` to refer to the "order" of the hyperquine. The stack will be written in Python array pseudocode.)*

- `I` - Increment 0 `n` times. The stack is now `[n]`.
- `Ì` - Duplicate the top value of the stack (`n`) onto the "grave" stack `n` times. The grave stack is now `[n] * n` (but the main stack hasn't changed).
- `*` - Scale the current delta vector `(1, 0)` by `n`. The delta vector is now `(n, 0)`, which means that the instruction pointer will only execute every `n`th instruction. The stack is now empty.
- `'!` - Push the ASCII value of `!`, which happens to be 21, which just so happens to be the length of the program, onto the stack.
- `’` - Quote 21 characters (the entire program)
- `6z` - Rotate the stack down by 6, so that `IÌ*'!’` is at the top instead of the bottom.
- `W{...}` - While the top of the stack is not zero...
	- `1ì` - Duplicate 1 value (in this case, `n`) from the grave stack onto the main stack.
	- `k` - Pop a number off the stack (in this case, `n`) and execute the next instruction that many times...
		- `:` - Duplicate the top stack item.
	- The topmost letter on the stack is now repeated `n+1` times.
	- `1ì` - Grab `n` from the grave stack again.
	- `I` - Increment the topmost item of the stack.
	- `k` - Pop a number off the stack (in this case, `n+1`) and execute the next instruction that many times...
		- `,` - Pop a letter off the stack and print it.
- `q` - Quit the program

#### DDoo  nnoott  rreeppeeaatt  yyoouurrsseellff!!

	II**""DDoo  nnoott  rreeppeeaatt  yyoouurrsseellff!!’’WW,,qq

- `II` - Increment 0 twice. The stack is now `[2]`.
- `*` - Scale the delta vector by 2. Now the instruction pointer will only exeucte other instruction.
- `"Do not repeat yourself!’` - Push a literal string onto the stack, with the first character on top and the last character on the bottom.
- `W,q` - Print string and exit.

#### Hyperprogramming - N+N, N*N and N^N all in one

	I:*"+×^’.:1zGEiq

*(As in the Hyperquine example, I will use `n` to refer to the "order" of the program and the stack will be written in Python array pseudocode.)*

- `I` - Increment 0 `n` times. The stack is now `[n]`.
- `:` - Duplicate `n` times. The stack is now `[n] * (n+1)`.
- `*` - Scale the delta vector by `n` to execute only every `n`th instruction. The stack is now `[n] * n`.
- `"+×^’` - Push a literal string onto the stack, with `+` at the top.
- `.:` - Input an integer and duplicate it. The stack is now `[n] * n + [integer] * 2`.
- `1z` - Rotate the stack down by 1. The stack is now `[n] * (n-1) + [integer] * 2 + [n]`.
- `G` - Pop a number off the stack (`n`) and grab the `n`th item in the stack (with 0 being the topmost). If `n` is 1, it will bring `+` to the top. If `n` is 2, it will bring `×` to the top; if `n` is 3, `^`.
- `E` - Pop a character off the stack and execute it.  (i.e. Execute the `+`/`×`/`^`, operating on the two copies of the input at the top of the stack.
- `iq` - Output integer and exit.



## PROGRAM FLOW

If you are already familiar with Befunge, you willl have no problem with program flow in C-Funge. When run, each program in C-Funge has a single instruction pointer (IP). This instruction pointer, like Befunge-98, has two vectors that define its motion: position and delta (velocity). The position vector is simple enough; it defines which character in the program the instruction pointer is executing. After each instruction (unless stated otherwise), the delta vector is added to the position vector, causing the instruction pointer to execute the next instruction.

An IP's delta vector can be any integer vector, positive or negative. Most of the time, the delta vector is one of the cardinal directions (north/south/east/west) -- east by default. The instruction pointer will move one character in this direction after each instruction. There are four special instructions that set the IP delta vector to the unit vector along these directions:

- `↑` - Set IP delta NORTH.
- `↓` - Set IP delta SOUTH.
- `→` - Set IP delta EAST.
- `←` - Set IP delta WEST.

A simple infinite loop could be constructed in C-Funge like this:

	→↓
	↑←

Or even more simply:

	→←

If the IP's delta vector is ever `(0, 0)`, the program ends. The instruction `q` does exactly this.

If the instruction pointer encounters `#`, it will skip over one instruction. Take the following code:

	→#↓"Code here will be executed.’W,q,W←
	  →"This code will never be reached.’↑

Here the instruction pointer skips over `↓` and continues execution in a straight line.



## EDGE HANDLING

Unlike Befunge-98, each program in C-Funge is a finite size. Unless another character populates it, all coordinates in the program grid are filled with the space character (` ` = 0x20). When a file is read, the number of lines (ignoring up to a single trailing empty line) is the height of the program, and the length of the longest line is the width. There are no negative coordinates in C-Funge -- the topmost line of the program is `y` = 0 with positive `y` values moving downward; the leftmost column of the program is `x` = 0 with positive `x` values moving rightward. (In the future, instructions may be added to expand or contract these boundaries.) There are only ever two dimensions in C-Funge; while I am a huge advocate of higher-dimensional thinking, a C-Funge program should never need more than a flat plane (and in fact, can usually easily fit within a single straight line).

The program grid in C-Funge is infinitely tiled in all directions. If a reference is made to coordinates outside of the bounds of the program, the modulus operator should be applied between the `x`/`y` coordinates and the program width/height respectively. C-Funge's wrapping function could be defined as `W(x, y) = (x % program_width, y % program_height)`. The overall effect of this is that if the instruction pointer travels `n` characters off an edge, they will appear `n` characters away from the opposite edge, but always within the program bounds. Unlike positions, the delta vector itself will never be automatically changed to be smaller than the bounds of the program.



## DATA TYPES

Like Befunge, C-Funge has only one type of data -- the integer. These integers can be interpreted as ASCII/Unicode characters, and these characters can form `0gnirts` strings, with the first character of the string at the top of the stack and a zero below the last character.



### Strings

In Befunge, all strings either have to be read right-to-left or have to be written backward, since characters are pushed onto the stack as they are read. C-Funge allows these types of strings (calling them "classical" strings), but also provides a more intuitive alternative.

There are two main ways to define a string in C-Funge. When the instruction pointer encounters `"`, it will push each value it encounters to a special "string buffer" until it meets either `‘` or `’`. If the string is terminated by `‘`, the values will be transferred onto the stack such that the last character read is at the top (and thus will be printed first). If the string is terminated by `’`, the values will be transferred onto the stack such that the first character read is at the top. Consider the following examples:

- `"This was a triumph.’W,q` - Outputs `This was a triumph.`.
- `"This was a triumph.‘W,q` - Outputs `.hpmuirt a saw sihT`.
- `←q,W‘This was a triumph."` - Outputs `This was a triumph.`. (The instruction pointer moves from right-to-left, so classical string interpretation produces the correct result.)
- `←q,W’This was a triumph."` - Outputs `.hpmuirt a saw sihT`.


## STORING DATA AND THE STACK(s?)

C-Funge has seven stacks, although most of them usually aren't needed. By default, every stack is LIFO. (Values are pushed onto the top and popped off the top). Stacks are usually represented in Pythonic pseudocode, with the end of an array being the top of a stack. For example the stack `[2, 4, 9]` has the number 2 at the bottom and 9 at the top.

The only type of value in C-Funge is the integer. 

### The main stack

The main stack in C-Funge (usually just referred to as "the stack") is where all of the operations happen. Most commands interact with the main stack in some way.

- `$` - Pop a value off the stack and ignore it.


## INSTRUCTION REFERENCE

### Program flow

- `↑` - NORTH
- `↓` - SOUTH
- `→` - EAST
- `←` - WEST
- `#` - JUMP
- `*` - SCALE DELTA VECTOR (Pop a value `n` off the stack and scale the delta vector by `n`)
- `/` - REFLECT FORWARD (Reflect across `y = x`)
- `;` - TOGGLE IGNORE (Jump forward until next `;`/`}`, or until the instruction pointer returns to the starting position)
- `=` - IF (Pop a value `n` off the stack; if `n` is zero then skip the next instruction/block, otherwise execute it)
- `?` - RANDOM DIRECTION (Set the IP delta randomly to either north, south, east or west)
- `C` - CALL GLOBAL SUBROUTINE

### Introspection

- `@` - RELATIVE SET ORIGIN (Pop a vector `v` off the stack; set the origin vector to `v + (current IP position)`)

### Data/stack manipulation

- `&` - SWAP TOP TWO STACK ITEMS
- `0`-`9` - LITERAL 0-9 (Push the number `0`-`9` onto the stack)
- `a`-`f` - LITERAL 10-15 (Push the hexadecimal number `0xA`-`0xF` onto the stack)

### Math/logic

- `!` - INVERT (Pop a value `n` off the stack; if `n` is zero then push `1`, otherwise push `0`)
- `%` - MODULUS (Pop two values `b` and `a` off the stack; push `a % b`)
- `+` - ADD (Pop two values `b` and `a` off the stack; push `a + b`)
- `-` - SUBTRACT (Pop two values `b` and `a` off the stack; push `a - b`)
- `<` - LESS THAN (Pop two values `b` and `a` off the stack; push `1` if `a < b`, otherwise push `0`)
- `>` - GREATER THAN (Pop two values `b` and `a` off the stack; push `1` if `a > b`, otherwise push `0`)
- `A` - ABSOLUTE VALUE (Pop a value `n` off the stack; push `|n|`)
- `D` - DECREMENT

### STRINGS AND I/O

- `'` - QUOTE CHARACTER (Instead of executing the next character, push it onto the stack)
- `,` - OUTPUT CHARACTER (Pop a single character off the stack and print it to stdout)
- `.` - [DEPRECATED] INPUT INTEGER
