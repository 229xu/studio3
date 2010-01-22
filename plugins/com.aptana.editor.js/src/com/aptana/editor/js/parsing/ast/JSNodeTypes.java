package com.aptana.editor.js.parsing.ast;

public class JSNodeTypes
{
	public static final short ERROR = -1;

	public static final short EMPTY = 0;

	public static final short ASSIGN = 1;

	public static final short ADD_AND_ASSIGN = 2;

	public static final short ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN = 3;

	public static final short BITWISE_AND_AND_ASSIGN = 4;

	public static final short BITWISE_OR_AND_ASSIGN = 5;

	public static final short BITWISE_XOR_AND_ASSIGN = 6;

	public static final short DIVIDE_AND_ASSIGN = 7;

	public static final short MOD_AND_ASSIGN = 8;

	public static final short MULTIPLY_AND_ASSIGN = 9;

	public static final short SHIFT_LEFT_AND_ASSIGN = 10;

	public static final short SHIFT_RIGHT_AND_ASSIGN = 11;

	public static final short SUBTRACT_AND_ASSIGN = 12;

	public static final short NULL = 13;

	public static final short TRUE = 14;

	public static final short FALSE = 15;

	public static final short NUMBER = 16;

	public static final short STRING = 17;

	public static final short REGEX = 18;

	public static final short IDENTIFIER = 19;

	public static final short THIS = 20;

	public static final short STATEMENTS = 21;

	public static final short CONTINUE = 22;

	public static final short BREAK = 23;

	public static final short EQUAL = 24;

	public static final short GREATER_THAN = 25;

	public static final short GREATER_THAN_OR_EQUAL = 26;

	public static final short IDENTITY = 27;

	public static final short IN = 28;

	public static final short INSTANCE_OF = 29;

	public static final short LESS_THAN = 30;

	public static final short LESS_THAN_OR_EQUAL = 31;

	public static final short LOGICAL_AND = 32;

	public static final short LOGICAL_OR = 33;

	public static final short NOT_EQUAL = 34;

	public static final short NOT_IDENTITY = 35;

	public static final short ADD = 36;

	public static final short ARITHMETIC_SHIFT_RIGHT = 37;

	public static final short BITWISE_AND = 38;

	public static final short BITWISE_OR = 39;

	public static final short BITWISE_XOR = 40;

	public static final short DIVIDE = 41;

	public static final short MOD = 42;

	public static final short MULTIPLY = 43;

	public static final short SHIFT_LEFT = 44;

	public static final short SHIFT_RIGHT = 45;

	public static final short SUBTRACT = 46;

	public static final short GET_ELEMENT = 47;

	public static final short GET_PROPERTY = 48;

	public static final short DELETE = 49;

	public static final short LOGICAL_NOT = 50;

	public static final short NEGATIVE = 51;

	public static final short PRE_DECREMENT = 52;

	public static final short POSITIVE = 53;

	public static final short PRE_INCREMENT = 54;

	public static final short BITWISE_NOT = 55;

	public static final short TYPEOF = 56;

	public static final short VOID = 57;

	public static final short GROUP = 58;

	public static final short POST_DECREMENT = 59;

	public static final short POST_INCREMENT = 60;

	public static final short ARGUMENTS = 61;

	public static final short INVOKE = 62;

	public static final short DECLARATION = 63;

	public static final short VAR = 64;

	public static final short TRY = 65;

	public static final short CATCH = 66;

	public static final short FINALLY = 67;

	public static final short CONDITIONAL = 68;

	public static final short PARAMETERS = 69;

	public static final short FUNCTION = 70;
}
