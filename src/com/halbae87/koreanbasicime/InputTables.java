/*
 * halbae87: this project is created from Soft Keyboard Sample source
 *  but this part is my original source
 */

package com.halbae87.koreanbasicime;

public final class InputTables {
	public static final int NUM_OF_FIRST = 19;
	public static final int NUM_OF_MIDDLE = 21;
	public static final int NUM_OF_LAST = 27;
	public static final int NUM_OF_LAST_INDEX = NUM_OF_LAST + 1; // add 1 for non-last consonant added characters
	
	public static final int KEYSTATE_NONE = 0;
	
	public static final int KEYSTATE_SHIFT = 1;
	public static final int KEYSTATE_SHIFT_LEFT = 1;
	public static final int KEYSTATE_SHIFT_RIGHT = 2;
	public static final int KEYSTATE_SHIFT_MASK = 3;
	
	public static final int KEYSTATE_ALT = 4;
	public static final int KEYSTATE_ALT_LEFT = 4;
	public static final int KEYSTATE_ALT_RIGHT = 8;
	public static final int KEYSTATE_ALT_MASK = 12;
	
	public static final int KEYSTATE_CTRL = 16;
	public static final int KEYSTATE_CTRL_LEFT = 16;
	public static final int KEYSTATE_CTRL_RIGHT = 32;
	public static final int KEYSTATE_CTRL_MASK = 48;
	
	public static final int KEYSTATE_FN = 64;	// just for future usage...
	
	public static final char BACK_SPACE = 0x8;

	// formula to get HANGUL_CODE by composing consonants and vowel indexes
	// HANGUL_CODE = HANGUL_START + iFirst*NUM_OF_MIDDLE*NUM_OF_LAST_INDEX + iMiddle*NUM_OF_LAST_INDEX + iLast
	
	// getting the first consonant index from code
	// iFirst = (vCode - HANGUL_START) / (NUM_OF_MIDDLE * NUM_OF_LAST_INDEX)

	// getting the vowel index from code
	// iMiddle = ((vCode - HANGUL_START) % (NUM_OF_MIDDLE * NUM_OF_LAST_INDEX)) / NUM_OF_LAST_INDEX

	// getting the last consonant index from code
	// iLast = (vCode - HANGUL_START) % NUM_OF_LAST_INDEX

	
	public static final class NormalKeyMap {
		public static final char Code[] 		= {0x3141,	0x3160,	0x314A,	0x3147,	0x3137,	0x3139,	0x314E,	0x3157,	0x3151,	0x3153,	0x314F,	0x3163,	0x3161,	0x315C,	0x3150,	0x3154,	0x3142,	0x3131,	0x3134,	0x3145,	0x3155,	0x314D,	0x3148,	0x314C,	0x315B,	0x314B};
		public static final int FirstIndex[] 	= {6,		-1,		14,		11,		3,		5,		18,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		7,		0,		2,		9,		-1,		17,		12,		16,		-1,		15};
		public static final int MiddleIndex[] 	= {-1,		17,		-1,		-1,		-1,		-1,		-1,		8,		2,		4,		0,		20,		18,		13,		1,		5,		-1,		-1,		-1,		-1,		6,		-1,		-1,		-1,		12,		-1};
		public static final int LastIndex[]		= {16,		-1,		23,		21,		7,		8,		27,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		17,		1,		4,		19,		-1,		26,		22,		25,		-1,		24};
	}

	public static final class ShiftedKeyMap {
		public static final char Code[] 		= {0x3141,	0x3160,	0x314A,	0x3147,	0x3138,	0x3139,	0x314E,	0x3157,	0x3151,	0x3153,	0x314F,	0x3163,	0x3161,	0x315C,	0x3152,	0x3156,	0x3143,	0x3132,	0x3134,	0x3146,	0x3155,	0x314D,	0x3149,	0x314C,	0x315B,	0x314B};
		public static final int FirstIndex[] 	= {6,		-1,		14,		11,		4,		5,		18,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		8,		1,		2,		10,		-1,		17,		13,		16,		-1,		15};
		public static final int MiddleIndex[] 	= {-1,		17,		-1,		-1,		-1,		-1,		-1,		8,		2,		4,		0,		20,		18,		13,		3,		7,		-1,		-1,		-1,		-1,		6,		-1,		-1,		-1,		12,		-1};
		public static final int LastIndex[]		= {16,		-1,		23,		21,		-1,		8,		27,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		2,		4,		20,		-1,		26,		-1,		25,		-1,		24};
	}

	public static final char FirstConsonantCodes[] = {0x3131,	0x3132,	0x3134,	0x3137,	0x3138,	0x3139,	0x3141,	0x3142,	0x3143,	0x3145,	0x3146,	0x3147,	0x3148,	0x3149,	0x314A,	0x314B,	0x314C,	0x314D,	0x314E };

	public static final class LastConsonants {
		public static final char Code[]  = {0x0,	0x3131,	0x3132,	0x3133,	0x3134,	0x3135,	0x3136,	0x3137,	0x3139,	0x313A,	0x313B,	0x313C,	0x313D,	0x313E,	0x313F,	0x3140,	0x3141,	0x3142,	0x3144,	0x3145,	0x3146,	0x3147,	0x3148,	0x314A,	0x314B,	0x314C,	0x314D,	0x314E};
		public static final int iLast[]	 = {-1,		-1,		-1,		1,		-1,		4,		4,		-1,		-1,		8,		8,		8,		8,		8,		8,		8,		-1,		-1,		17,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1};
		public static final int iFirst[] = {-1,		-1,		-1,		9,		-1,		12,		18,		-1,		-1,		0,		6,		7,		9,		16,		17,		18,		-1,		-1,		9,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1};
	}
	
	public static final class Vowels {
		public static final char Code[]  = {0x314F,	0x3150,	0x3151,	0x3152,	0x3153,	0x3154,	0x3155,	0x3156,	0x3157,	0x3158,	0x3159,	0x315A,	0x315B,	0x315C,	0x315D,	0x315E,	0x315F,	0x3160,	0x3161,	0x3162,	0x3163};
		public static final int iMiddle[] = {-1,	-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		8,		8,		8,		-1,		-1,		13,		13,		13,		-1,		-1,		18,		-1};
	}
}
