/*
 * halbae87: this project is created from Soft Keyboard Sample source
 * 	but this part is my original source. 
 */

package com.halbae87.koreanbasicime;

import com.halbae87.koreanbasicime.InputTables;
import android.util.Log;

public class KoreanAutomata {
	public static int HANGUL_START = 0xAC00;
	public static int HANGUL_END = 0xD7A3;
	public static int HANGUL_JAMO_START = 0x3131;
	public static int HANGUL_MO_START = 0x314F;
	public static int HANGUL_JAMO_END = 0x3163;
	private static final String TAG = "KoreanAutomata";
	
	// Action Codes
	public static final int ACTION_NONE = 0;
	public static final int ACTION_UPDATE_COMPOSITIONSTR = 1;
	public static final int ACTION_UPDATE_COMPLETESTR = 2;
	public static final int ACTION_USE_INPUT_AS_RESULT = 4;
	public static final int ACTION_APPEND = 8;
	// public static final int ACTION_BACKSPACE = 8; not used.
	public static final int ACTION_ERROR = -1;

	private int mState = 0;
	private String mCompositionString = "";
	private String mCompleteString = "";
	private boolean mKoreanMode = false; 
	
	public KoreanAutomata()
	{
		mState = 0;
		mCompositionString = "";
		mCompleteString = "";
		mKoreanMode = false; 
	}
	public int GetState()
	{
		return mState;
	};
	public String GetCompositionString()
	{
		return mCompositionString;
	};
	public String GetCompleteString()
	{
		return mCompleteString;
	};
	
	public void ToggleMode()
	{
		mKoreanMode = !mKoreanMode;
	}
	public boolean IsKoreanMode()
	{
		return mKoreanMode;
	}
	public boolean IsHangul(char code)
	{
		if ((code >= HANGUL_START) && (code <= HANGUL_END))
			return true;
		if ((code >= HANGUL_JAMO_START) && (code <= HANGUL_JAMO_END))
			return true;
		return false;
	}
	
	public boolean IsJAMO(char code)
	{
		if ((code >= HANGUL_JAMO_START) && (code <= HANGUL_JAMO_END))
			return true;
		return false;
	};
	
	public boolean IsConsonant(char code)
	{
		if ((code >= HANGUL_JAMO_START) && (code < HANGUL_MO_START))
			return true;
		return false;
	};

	public boolean IsVowel(char code)
	{
		if ((code >= HANGUL_MO_START) && (code <= HANGUL_JAMO_END))
			return true;
		return false;
	};

	/** not used.
	public boolean IsLastConsonanted(char code)
	{
		if (IsHangul(code))
		{
			if (IsJAMO(code)) // <- need to fix, if this routine is to be used...
				return true;
			int offset = code - HANGUL_START;
			if (offset % InputTables.NUM_OF_LAST_INDEX == 0)
				return false;
			else 
				return true;
		}
		else
		{
			// wrong input
			return false;
		}
	}
	**/
	
	public int GetLastConsonantIndex(char code)
	{
		int lcIndex= -1;
		if (IsHangul(code))
		{
			if (IsJAMO(code))
			{
				if (IsConsonant(code))
				{
					for (lcIndex = 0; lcIndex < InputTables.NUM_OF_LAST_INDEX; lcIndex++)
					{
						if (code == InputTables.LastConsonants.Code[lcIndex])
							break;
					}
					if (lcIndex >= InputTables.NUM_OF_LAST_INDEX)
						lcIndex = -1;
				}
				else
					lcIndex = -1;
			}
			else
			{
				int offset = code - HANGUL_START;
				lcIndex = (offset % InputTables.NUM_OF_LAST_INDEX); 
			}
		}
		return lcIndex;
	}

	public char GetLastConsonant(char code)
	{
		char lcCode;
		int lcIndex = GetLastConsonantIndex(code);
		if (lcIndex < 0)
			lcCode = (char) 0;
		else
			lcCode = InputTables.LastConsonants.Code[lcIndex]; 
		return lcCode;
	}
	
	public int GetFirstConsonantIndex(char code)
	{
		int fcIndex = -1;
		if (IsHangul(code))
		{
			if (IsConsonant(code))
			{
				for (fcIndex = 0; fcIndex < InputTables.NUM_OF_FIRST; fcIndex++)
					if (code == InputTables.FirstConsonantCodes[fcIndex])
						break;
				if (fcIndex >= InputTables.NUM_OF_FIRST)
					fcIndex = -1;
			}
			else if (IsVowel(code))
			{
				fcIndex = -1;
			}
			else
			{
				int offset = code - HANGUL_START;
				fcIndex = (offset / (InputTables.NUM_OF_MIDDLE * InputTables.NUM_OF_LAST_INDEX)); 
			}
		}
		
		return fcIndex;
	}

	public char GetFirstConsonant(char code)
	{
		char fcCode;
		int fcIndex = GetFirstConsonantIndex(code);
		if (fcIndex < 0)
			fcCode = (char) 0;
		else
			fcCode = InputTables.FirstConsonantCodes[fcIndex]; 
		return fcCode;
	}

	public int GetVowelIndex(char code)
	{
		int vIndex = -1;
		if (IsHangul(code))
		{
			if (IsVowel(code)) // vowel only character..
			{
				vIndex = ConvertVowelCodeToIndex(code);
			}
			else 
			{
				int offset = code - HANGUL_START;
				vIndex = (offset % (InputTables.NUM_OF_MIDDLE * InputTables.NUM_OF_LAST_INDEX))/InputTables.NUM_OF_LAST_INDEX;
			}
		}
		return vIndex;
	}

	public char GetVowel(char code)
	{
		char vCode;
		int vIndex = GetVowelIndex(code);
		if (vIndex < 0)
			vCode = (char) 0;
		else 
			vCode = InputTables.Vowels.Code[vIndex]; 
		return vCode;
	}
	
	public int ConvertFirstConsonantCodeToIndex(char fcCode) // fcCode should be one of "First Consonants" otherwise return -1
	{
		int fcIndex = 0;
		while  (fcIndex < InputTables.NUM_OF_FIRST)  
		{
			if (fcCode == InputTables.FirstConsonantCodes[fcIndex])
				break;
			fcIndex++;
		}
		if (fcIndex ==  InputTables.NUM_OF_FIRST)
			fcIndex = -1;
		return fcIndex;
	}

	public int ConvertLastConsonantCodeToIndex(char lcCode) // fcCode should be one of "Last Consonants", otherwise return -1
	{
		int lcIndex = 0;
		while  (lcIndex < InputTables.NUM_OF_LAST_INDEX)  
		{
			if (lcCode == InputTables.LastConsonants.Code[lcIndex])
				break;
			lcIndex++;
		}
		if (lcIndex == InputTables.NUM_OF_LAST_INDEX)
			lcIndex = -1;
		return lcIndex;
	}
	
	public int ConvertVowelCodeToIndex(char vCode)
	{
		if (vCode < InputTables.Vowels.Code[0])
			return -1;
		int vIndex = vCode - InputTables.Vowels.Code[0];
		if (vIndex >= InputTables.NUM_OF_MIDDLE)
			return -1;
		return vIndex;
	}
	
	public int CombineLastConsonantWithIndex(int cIndex1, int cIndex2)
	{
		int newIndex = 0;
		char newCode = (char) 0;

		if (InputTables.LastConsonants.Code[cIndex1] == 0x3131 && InputTables.LastConsonants.Code[cIndex2] == 0x3145)
			newCode = 0x3133; // ã„³
		
		if (InputTables.LastConsonants.Code[cIndex1] == 0x3142 && InputTables.LastConsonants.Code[cIndex2] == 0x3145)
			newCode = 0x3144; // ã…„
		
		if (InputTables.LastConsonants.Code[cIndex1] == 0x3134)
		{
			if (InputTables.LastConsonants.Code[cIndex2] == 0x3148)
				newCode = 0x3135; // ã„µ
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x314E)
				newCode = 0x3136; // ã„¶
		}
		
		if (InputTables.LastConsonants.Code[cIndex1] == 0x3139)
		{
			if (InputTables.LastConsonants.Code[cIndex2] == 0x3131)
				newCode = 0x313A; // ã„º
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x3141)
				newCode = 0x313B; // ã„»
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x3142)
				newCode = 0x313C; // ã„¼ 
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x3145)
				newCode = 0x313D; // ã„½
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x314C)
				newCode = 0x313E; // ã„¾
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x314D)
				newCode = 0x313F; // ã„¿
			else if (InputTables.LastConsonants.Code[cIndex2] == 0x314E)
				newCode = 0x3140; // ã…€
		}
		
		if (newCode == (char) 0)
			newIndex = -1;
		else 
			newIndex = ConvertLastConsonantCodeToIndex(newCode);
		
		return newIndex;
	}
	
	public char CombineLastConsonantWithCode(char lcCode1, char lcCode2)
	{
		char newCode = (char) 0;

		if (lcCode1 == 0x3131 && lcCode2 == 0x3145)
			newCode = 0x3133; // ã„³
		
		else if (lcCode1 == 0x3142 && lcCode2 == 0x3145)
			newCode = 0x3144; // ã…„
		
		else if (lcCode1 == 0x3134)
		{
			if (lcCode2 == 0x3148)
				newCode = 0x3135; // ã„µ
			else if (lcCode2 == 0x314E)
				newCode = 0x3136; // ã„¶
		}
		
		else if (lcCode1 == 0x3139)
		{
			if (lcCode2 == 0x3131)
				newCode = 0x313A; // ã„º
			else if (lcCode2 == 0x3141)
				newCode = 0x313B; // ã„»
			else if (lcCode2 == 0x3142)
				newCode = 0x313C; // ã„¼ 
			else if (lcCode2 == 0x3145)
				newCode = 0x313D; // ã„½
			else if (lcCode2 == 0x314C)
				newCode = 0x313E; // ã„¾
			else if (lcCode2 == 0x314D)
				newCode = 0x313F; // ã„¿
			else if (lcCode2 == 0x314E)
				newCode = 0x3140; // ã…€
		}
		
		return newCode;
	}

	public char CombineVowelWithCode(char vCode1, char vCode2)
	{
		char newCode = (char) 0;
		if (vCode1 == 0x3157) // ã…—
		{
			if (vCode2 == 0x314F) // ã…�
				newCode = 0x3158; // ã…˜
			else if (vCode2 == 0x3150) // ã…�
				newCode = 0x3159; // ã…™
			else if (vCode2 == 0x3163) // ã…£
				newCode = 0x315A; // ã…š
		}
		else if (vCode1 == 0x315C) // ã…œ
		{
			if (vCode2 == 0x3153) // ã…“
				newCode = 0x315D; // ã…�
			else if (vCode2 == 0x3154) // ã…”
				newCode = 0x315E;  // ã…ž
			else if (vCode2 == 0x3163) // ã…£
				newCode = 0x315F; // ã…Ÿ
		}
		else if (vCode1 == 0x3161) // ã…¡
		{
			if (vCode2 == 0x3163) // ã…£
				newCode = 0x3162; // ã…¢
		}
		return newCode;
	}

	public int CombineVowelWithIndex(int vIndex1, int vIndex2)
	{
		int newIndex = -1;
		char vCode1 = InputTables.Vowels.Code[vIndex1];
		char vCode2 = InputTables.Vowels.Code[vIndex2];
		
		char newCode = CombineVowelWithCode(vCode1, vCode2);
		if (newCode != (char) 0)
		{
			newIndex = ConvertVowelCodeToIndex(newCode);
		}
		return newIndex;
	}
	
	public char ComposeCharWithIndexs(int fcIndex, int vIndex, int lcIndex)
	{
		char Code = (char) 0;
		if ((fcIndex >= 0) && (fcIndex < InputTables.NUM_OF_FIRST))
		{
			if ((vIndex >= 0) && (vIndex < InputTables.NUM_OF_MIDDLE))
			{
				if ((lcIndex >= 0) && (lcIndex < InputTables.NUM_OF_LAST))
				{
					int offset = fcIndex*InputTables.NUM_OF_MIDDLE*InputTables.NUM_OF_LAST_INDEX + vIndex*InputTables.NUM_OF_LAST_INDEX + lcIndex;
					Code = (char) (offset + HANGUL_START);
				}
			}
		}
		return Code;
	}
	
	public int GetAlphabetIndex(char code)
	{
		if (code >= 'a' && code <= 'z')
			return (int) (code - 'a');
		if (code >= 'A' && code <= 'Z')
			return (int) (code - 'A');
		return -1;
	}
	
	/* not used...
	public boolean IsToggleKey(char code, int KeyState)
	{
		boolean bRet = false;
		if ((code == ' ') && ((KeyState & InputTables.KEYSTATE_SHIFT_MASK) != 0)) // SHIFT-SPACE
			bRet = true;
		return bRet;
	}
	*/
	
	public int DoBackSpace()
	{
		int ret = ACTION_NONE;
		char code;
		
		if (mCompositionString != "")
			code =  mCompositionString.charAt(0);
		else
			code = (char) 0;

		if (mState != 0 && code == (char) 0)
		{
			// Log.v(TAG, "DoBackSpace -- Error. CompositionString is NULL. mState = " + mState);
			return ACTION_ERROR;
		}

		switch (mState)
		{
		case  0: // current composition string: NULL 
			ret = ACTION_USE_INPUT_AS_RESULT; 
			break; 
		case  1:  // current composition string: single consonant only
		case  4:  // current composition string: single vowel only
			mCompositionString = "";
			mState = 0;
			// ret = ACTION_UPDATE_COMPOSITIONSTR; 
			ret = ACTION_USE_INPUT_AS_RESULT; 
			break; 

		case  2: // current composition string: single consonant + single vowel
			// iFirst = (vCode - HANGUL_START) / (NUM_OF_MIDDLE * NUM_OF_LAST_INDEX)
			{
				int fcIndex = GetFirstConsonantIndex(code);
				code = InputTables.FirstConsonantCodes[fcIndex];
				mCompositionString = "";
				mCompositionString += code;
				mState = 1;
			}
			ret = ACTION_UPDATE_COMPOSITIONSTR;
			break; 
		case  3: // current composition string: single consonant + single vowel + single consonant
			// iLast = (vCode - HANGUL_START) % NUM_OF_LAST_INDEX
			{
				int lcIndex = GetLastConsonantIndex(code);
				code = (char)((int)code - lcIndex);
				mCompositionString = "";
				mCompositionString += code;
				mState = 2;
			}
			ret = ACTION_UPDATE_COMPOSITIONSTR; 
			break; 
		case  5: // current composition string: a combined vowel
			{
				int vIndex = GetVowelIndex(code);
				if (vIndex < 0)
				{
					ret = ACTION_ERROR;
					break;
				}
				int newIndex = InputTables.Vowels.iMiddle[vIndex];
				if (newIndex < 0)
				{
					ret = ACTION_ERROR;
					break;
				}
				code = InputTables.Vowels.Code[newIndex];
				mCompositionString = "";
				mCompositionString += code;
				mState = 4;
			}
			ret = ACTION_UPDATE_COMPOSITIONSTR; 
			break; 
		case 10: // current composition string: a combined consonant
			{
				int lcIndex = GetLastConsonantIndex(code);
				if (lcIndex < 0)
				{
					ret = ACTION_ERROR;
					break;
				}
				int newIndex = InputTables.LastConsonants.iLast[lcIndex];
				if (newIndex < 0)
				{
					ret = ACTION_ERROR;
					break;
				}
				code = InputTables.LastConsonants.Code[newIndex];
				mCompositionString = "";
				mCompositionString += code;
				mState = 1;
			}
			ret = ACTION_UPDATE_COMPOSITIONSTR; 
			break; 
		case 11: // current composition string: single consonant + single vowel + a combined consonant
			{
				int lcIndex = GetLastConsonantIndex(code);
				if (lcIndex < 0)
				{
					ret = ACTION_ERROR;
					break;
				}
				int newIndex = InputTables.LastConsonants.iLast[lcIndex];
				if (newIndex < 0)
				{
					ret = ACTION_ERROR;
					break;
				}
				code = (char)((int) code - lcIndex + newIndex);
				mCompositionString = "";
				mCompositionString += code;
				mState = 3;
			}
			ret = ACTION_UPDATE_COMPOSITIONSTR; 
			break; 
		case 20: // current composition string: single consonant + a combined vowel
			{
				int fcIndex = GetFirstConsonantIndex(code);
				int vIndex = GetVowelIndex(code);
				int newIndex = InputTables.Vowels.iMiddle[vIndex];
				if (newIndex < 0)
				{
					ret = ACTION_ERROR;
					break;
				}
				code = ComposeCharWithIndexs(fcIndex, newIndex, 0);
				mCompositionString = "";
				mCompositionString += code;
				mState = 2;
			}
			ret = ACTION_UPDATE_COMPOSITIONSTR; 
			break; 
		case 21: // current composition string: single consonant + a combined vowel + single consonant
			{
				int lcIndex =  GetLastConsonantIndex(code);
				code =  (char) ((int) code - lcIndex);
				mCompositionString = "";
				mCompositionString += code;
				mState = 20;
			}
			ret = ACTION_UPDATE_COMPOSITIONSTR; 
			break; 
		case 22: // current composition string: single consonant + a combined vowel + a combined consonant
			{
				int lcIndex = GetLastConsonantIndex(code);
				if (lcIndex < 0)
				{
					ret = ACTION_ERROR;
					break;
				}
				int newIndex = InputTables.LastConsonants.iLast[lcIndex];
				if (newIndex < 0)
				{
					ret = ACTION_ERROR;
					break;
				}
				code = (char)((int) code - lcIndex + newIndex);
				mCompositionString = "";
				mCompositionString += code;
				mState = 21;
			}
			ret = ACTION_UPDATE_COMPOSITIONSTR; 
			break; 
		default: ret = ACTION_ERROR; // error. should not be here in any circumstance. 
		}
		return ret;
	}
	
	public int FinishAutomataWithoutInput() // Input is ended by external causes
	{
		int ret = ACTION_NONE;
		if (mKoreanMode) //  && mState > 0)
		{
			mCompleteString = "";
			mCompositionString = "";
			mState = 0;
			//ret |= ACTION_UPDATE_COMPOSITIONSTR;
			//ret |= ACTION_UPDATE_COMPLETESTR;
		}
		return ret;
	}

	public int DoAutomata(char code, int KeyState) // , String CurrentCompositionString)
	{
		// Log.v(TAG, "DoAutomata Entered - code = "+ code + " KeyState = " + KeyState + " mState = " + mState);
		
		int result = ACTION_NONE;
		int AlphaIndex = GetAlphabetIndex(code);
		char hcode;

		/* remove toggle key check and backspace check.
		// check toggle key first
		if (IsToggleKey(code, KeyState)) // SHIFT-SPACE
		{
			// toggle Korean/English
			if (mState != 0) // flushing..
			{
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mState = 0;
				result = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
			mKoreanMode = !mKoreanMode; // input mode toggle
		}
		else if (code == InputTables.BACK_SPACE)
		{
			// do back space
		}
		else */ 
		if (AlphaIndex < 0) // white spaces...
		{
			if (mKoreanMode)
			{
				// flush Korean characters first.
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mState = 0;
				result = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
			// process the code as English
			if ((KeyState & (InputTables.KEYSTATE_ALT_MASK | InputTables.KEYSTATE_CTRL_MASK | InputTables.KEYSTATE_FN)) == 0)
			{
				result |= ACTION_USE_INPUT_AS_RESULT;
			}
		}
		else if (!mKoreanMode){
			// process the code as English 
			result = ACTION_USE_INPUT_AS_RESULT;
		}
		else {
			if ((KeyState & InputTables.KEYSTATE_SHIFT_MASK) == 0)
			{
				hcode = InputTables.NormalKeyMap.Code[AlphaIndex];
			}
			else
			{
				hcode = InputTables.ShiftedKeyMap.Code[AlphaIndex];
			}
			// Log.v(TAG, "--DoAutomata() - hcode = " + hcode);
			
			switch (mState)
			{
			case 0: result = DoState00(hcode); break; // current composition string: NULL
			case 1: result = DoState01(hcode); break; // current composition string: single consonant only
			case 2: result = DoState02(hcode); break; // current composition string: single consonant + single vowel
			case 3: result = DoState03(hcode); break; // current composition string: single consonant + single vowel + single consonant
			case 4: result = DoState04(hcode); break; // current composition string: single vowel
			case 5: result = DoState05(hcode); break; // current composition string: a combined vowel
			case 10: result = DoState10(hcode); break; // current composition string: a combined consonant
			case 11: result = DoState11(hcode); break; // current composition string: single consonant + single vowel + a combined consonant
			case 20: result = DoState20(hcode); break; // current composition string: single consonant + a combined vowel
			case 21: result = DoState21(hcode); break; // current composition string: single consonant + a combined vowel + single consonant
			case 22: result = DoState22(hcode); break; // current composition string: single consonant + a combined vowel + a combined consonant
			default: result = ACTION_ERROR; // error. should not be here in any circumstance. 
			}
		}
		return result;
	};
	
	private int DoState00(char code) // current composition string: NULL
	{
		// Log.v(TAG, "State 0 Entered - code = "+ code );
		if (IsConsonant(code))
		{
			mState = 1;
		}
		else
		{
			mState = 4;
		}
		mCompleteString = "";
		mCompositionString = "";
		mCompositionString += code; 
		return ACTION_UPDATE_COMPOSITIONSTR | ACTION_APPEND;
	};

	private int DoState01(char code) // current composition string: single consonant only
	{
		// Log.v(TAG, "State 1 Entered - code = "+ code );
		if (mCompositionString == "")
		{
			// Log.v(TAG, "DoState 01 -- Error. CompositionString is NULL");
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			char newCode = CombineLastConsonantWithCode(mCompositionString.charAt(0), code);
			if (newCode == (char)0) // cannot combine last consonants
			{
				mCompleteString = mCompositionString; // flush
				mCompositionString = "";
				mCompositionString += code;
				mState = 1;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
			else // can combine last consonants
			{
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newCode;
				mState = 10;
				ret = ACTION_UPDATE_COMPOSITIONSTR;
			}
		}
		else
		{
			int fcIndex = ConvertFirstConsonantCodeToIndex(mCompositionString.charAt(0));
			int vIndex = ConvertVowelCodeToIndex(code);
			char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0);
			mCompleteString = "";
			mCompositionString = "";
			mCompositionString += newCode;
			mState = 2;		
			ret = ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	private int DoState02(char code) // current composition string: single consonant + single vowel
	{
		// Log.v(TAG, "State 2 Entered - code = "+ code );
		if (mCompositionString == "")
		{
			// Log.v(TAG, "DoState-02 -- Error. CompositionString is NULL");
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			int lcIndex = GetLastConsonantIndex(code);
			if (lcIndex != -1) // code can be last consonant..
			{
				char newCode = (char)((int) mCompositionString.charAt(0) + lcIndex);
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newCode;
				mState = 3;
				ret = ACTION_UPDATE_COMPOSITIONSTR;
			}
			else
			{
				mCompleteString = mCompositionString; 
				mCompositionString = ""; // flush
				mCompositionString += code;
				mState = 1;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
		}
		else // vowel
		{
			char vCode = GetVowel(mCompositionString.charAt(0));
			char newCode = CombineVowelWithCode(vCode, code);
			if (newCode != (char) 0)
			{
				int fcIndex = GetFirstConsonantIndex(mCompositionString.charAt(0));
				int vIndex = ConvertVowelCodeToIndex(newCode);
				char newChar = ComposeCharWithIndexs(fcIndex, vIndex, 0);
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newChar;
				mState = 20;
				ret = ACTION_UPDATE_COMPOSITIONSTR;
			}
			else 
			{
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mCompositionString += code;
				mState = 4;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
		}
		return ret;
	};

	private int DoState03(char code) // current composition string: single consonant + single vowel + single consonant
	{
		// Log.v(TAG, "State 3 Entered - code = "+ code );
		if (mCompositionString == "")
		{
			// Log.v(TAG, "DoState 03 -- Error. CompositionString is NULL");
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			int lcIndex = GetLastConsonantIndex(mCompositionString.charAt(0));
			if (lcIndex < 0)
			{
				// Log.v(TAG, " -- Error. consonant, lcIndex = " + lcIndex);
				return ACTION_ERROR;
			}
			char newCode = CombineLastConsonantWithCode(InputTables.LastConsonants.Code[lcIndex], code);
			if (newCode != (char)0) // Last Consonants can be combined
			{
				char newChar = (char) ((int) mCompositionString.charAt(0) - lcIndex + GetLastConsonantIndex(newCode));
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newChar;
				mState = 11;
				ret = ACTION_UPDATE_COMPOSITIONSTR;
			}
			else
			{
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mCompositionString += code;
				mState = 1;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
		}
		else // vowel
		{
			int lcIndex = GetLastConsonantIndex(mCompositionString.charAt(0));
			if (lcIndex < 0)
			{
				// Log.v(TAG, " -- complete Error. vowel, lcIndex = " + lcIndex);
				return ACTION_ERROR;
			}
			char newChar = (char) ((int) mCompositionString.charAt(0) - lcIndex); // remove last consonant and flush it.
			mCompleteString = "";
			mCompleteString += newChar;
			int fcIndex = GetFirstConsonantIndex(InputTables.LastConsonants.Code[lcIndex]);
			if (fcIndex < 0)
			{
				// Log.v(TAG, " -- composition Error, vowel, lcIndex = " + lcIndex);
				return ACTION_ERROR;
			}
			int vIndex = GetVowelIndex(code);
			char newCode = ComposeCharWithIndexs(fcIndex, vIndex, 0); // compose new composition string
			mCompositionString = "";
			mCompositionString += newCode;
			mState = 2;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	private int DoState04(char code) // current composition string: single vowel
	{
		// Log.v(TAG, "State 4 Entered - code = "+ code );
		if (mCompositionString == "")
		{
			// Log.v(TAG, "DoState 04 -- Error. CompositionString is NULL");
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 1;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		else
		{
			char newCode = CombineVowelWithCode(mCompositionString.charAt(0), code);
			if (newCode != (char) 0)
			{
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newCode;
				mState = 5;
				ret = ACTION_UPDATE_COMPOSITIONSTR;
			}
			else
			{
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mCompositionString += code;
				mState = 4;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
		}
		return ret;
	};

	private int DoState05(char code) // current composition string: a combined vowel
	{
		// Log.v(TAG, "State 5 Entered - code = "+ code );
		if (mCompositionString == "")
		{
			// Log.v(TAG, "DoState 05 -- Error. CompositionString is NULL");
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 1;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		else
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 4;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	private int DoState10(char code) // current composition string: a combined consonant
	{
		// Log.v(TAG, "State 10 Entered - code = "+ code );
		if (mCompositionString == "")
		{
			// Log.v(TAG, "DoState 10 -- Error. CompositionString is NULL");
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 1;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		else
		{
			int lcIndex0 = GetLastConsonantIndex(mCompositionString.charAt(0));
			int lcIndex1 = InputTables.LastConsonants.iLast[lcIndex0];
			int fcIndex = InputTables.LastConsonants.iFirst[lcIndex0];
			mCompleteString = "";
			mCompleteString += InputTables.LastConsonants.Code[lcIndex1];
			int vIndex = GetVowelIndex(code);
			char newChar = ComposeCharWithIndexs(fcIndex, vIndex, 0);
			mCompositionString = "";
			mCompositionString += newChar;
			mState = 2;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	private int DoState11(char code) // current composition string: single consonant + single vowel + a combined consonant
	{
		// Log.v(TAG, "State 11 Entered - code = "+ code );
		if (mCompositionString == "")
		{
			// Log.v(TAG, "DoState 11 -- Error. CompositionString is NULL");
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 1;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		else
		{
			int lcIndexOrg = GetLastConsonantIndex(mCompositionString.charAt(0));
			int fcIndexOrg = GetFirstConsonantIndex(mCompositionString.charAt(0));
			int vIndexOrg = GetVowelIndex(mCompositionString.charAt(0));
			int lcIndexNew = InputTables.LastConsonants.iLast[lcIndexOrg];
			char newChar = ComposeCharWithIndexs(fcIndexOrg, vIndexOrg, lcIndexNew);
			int fcIndexNew = InputTables.LastConsonants.iFirst[lcIndexOrg];
			int vIndexNew = ConvertVowelCodeToIndex(code);
			mCompleteString = "";
			mCompleteString += newChar;
			newChar = ComposeCharWithIndexs(fcIndexNew, vIndexNew, 0);
			mCompositionString = "";
			mCompositionString += newChar;
			mState = 2;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};
	
	private int DoState20(char code) // current composition string: single consonant + a combined vowel
	{
		// Log.v(TAG, "State 20 Entered - code = "+ code );
		if (mCompositionString == "")
		{
			// Log.v(TAG, "DoState 20 -- Error. CompositionString is NULL");
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			int lcIndex = ConvertLastConsonantCodeToIndex(code);
			if (lcIndex < 0) // cannot compose the code with composition string. flush it.
			{
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mCompositionString += code;
				mState = 1;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
			else // compose..
			{
				char newChar = mCompositionString.charAt(0);
				newChar = (char)((int)newChar + lcIndex);
				mCompleteString = "";
				mCompositionString = "";
				mCompositionString += newChar;
				mState = 21;
				ret = ACTION_UPDATE_COMPOSITIONSTR;
			}
		}
		else
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 4;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};
	
	private int DoState21(char code) // current composition string: single consonant + a combined vowel + single consonant
	{
		// Log.v(TAG, "State 21 Entered - code = "+ code );
		if (mCompositionString == "")
		{
			// Log.v(TAG, "DoState 20 -- Error. CompositionString is NULL");
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			int lcIndex = GetLastConsonantIndex(mCompositionString.charAt(0));
			int lcIndexTemp = ConvertLastConsonantCodeToIndex(code);
			if (lcIndexTemp < 0)
			{
				mCompleteString = mCompositionString;
				mCompositionString = "";
				mCompositionString += code;
				mState = 1;
				ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
			}
			else {
				int lcIndexNew = CombineLastConsonantWithIndex(lcIndex, lcIndexTemp);
				if (lcIndexNew < 0)
				{
					mCompleteString = mCompositionString;
					mCompositionString = "";
					mCompositionString += code;
					mState = 1;
					ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
				}
				else
				{
					char newChar = mCompositionString.charAt(0);
					newChar = (char)((int)newChar - lcIndex + lcIndexNew);
					mCompleteString = "";
					mCompositionString = "";
					mCompositionString += newChar;
					mState = 22;
					ret = ACTION_UPDATE_COMPOSITIONSTR;
				}
			}
			
		}
		else
		{
			char newChar = mCompositionString.charAt(0);
			int lcIndex = GetLastConsonantIndex(newChar);
			newChar = (char)((int)newChar - lcIndex);
			mCompleteString = "";
			mCompleteString += newChar;
			int fcIndex = ConvertFirstConsonantCodeToIndex(InputTables.LastConsonants.Code[lcIndex]);
			int vIndex = ConvertVowelCodeToIndex(code);
			newChar = ComposeCharWithIndexs(fcIndex, vIndex, 0);
			mCompositionString = "";
			mCompositionString += newChar;
			mState = 2;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};

	private int DoState22(char code) // current composition string: single consonant + a combined vowel + a combined consonant
	{
		// Log.v(TAG, "State 22 Entered - code = "+ code );
		if (mCompositionString == "")
		{
			// Log.v(TAG, "DoState 22 -- Error. CompositionString is NULL");
			return ACTION_ERROR;
		}

		int ret = ACTION_NONE;
		if (IsConsonant(code))
		{
			mCompleteString = mCompositionString;
			mCompositionString = "";
			mCompositionString += code;
			mState = 1;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		else
		{
			char tempChar = mCompositionString.charAt(0);
			int lcIndex0 = GetLastConsonantIndex(tempChar);
			int lcIndex1 = InputTables.LastConsonants.iLast[lcIndex0];
			int fcIndex = InputTables.LastConsonants.iFirst[lcIndex0];
			
			tempChar = (char) ((int) tempChar - lcIndex0 + lcIndex1);
			mCompleteString = "";
			mCompleteString += tempChar;
			
			int vIndex = GetVowelIndex(code);
			char newChar = ComposeCharWithIndexs(fcIndex, vIndex, 0);
			mCompositionString = "";
			mCompositionString += newChar;
			mState = 2;
			ret = ACTION_UPDATE_COMPLETESTR | ACTION_UPDATE_COMPOSITIONSTR;
		}
		return ret;
	};
}
