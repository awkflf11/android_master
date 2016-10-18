/*
 * Copyright 2011 woozzu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.woozzu.android.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class StringMatcher {

	private final static char KOREAN_UNICODE_START = '가';
	private final static char KOREAN_UNICODE_END = '힣';
	private final static char KOREAN_UNIT = '까' - '가';
	private final static char[] KOREAN_INITIAL = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ',
			'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ',
			'ㅎ' };

	public static boolean match(String value, String keyword) {
		if (value == null || keyword == null)
			return false;
		if (keyword.length() > value.length())
			return false;

		int i = 0, j = 0;
		do {
			if (isChineseCharacter(value.charAt(i))) {
				if (keyword.charAt(j) == getUpcaseCharacter(value)) {
					i++;
					j++;
				} else if (j > 0)
					break;
				else
					i++;
			}
			/*
			 * if (isKorean(value.charAt(i)) &&
			 * isInitialSound(keyword.charAt(j))) { if (keyword.charAt(j) ==
			 * getInitialSound(value.charAt(i))) { i++; j++; } else if (j > 0)
			 * break; else i++; }
			 */else {
				if (keyword.charAt(j) == value.charAt(i)) {
					i++;
					j++;
				} else if (j > 0)
					break;
				else
					i++;
			}
		} while (i < value.length() && j < keyword.length());

		return (j == keyword.length()) ? true : false;
	}

	private static boolean isKorean(char c) {
		return c >= KOREAN_UNICODE_START && c <= KOREAN_UNICODE_END;
	}

	private static boolean isInitialSound(char c) {
		for (char i : KOREAN_INITIAL) {
			if (c == i)
				return true;
		}
		return false;
	}

	private static char getInitialSound(char c) {

		if (!isKorean(c)) {
			return c;
		}

		return KOREAN_INITIAL[(c - KOREAN_UNICODE_START) / KOREAN_UNIT];
	}

	public static boolean isChineseCharacter(char ch) {
		return ch >= 0x4e00 && ch <= 0x9fa5;
	}

	public static char getUpcaseCharacter(String str) {
		if (isChineseCharacter(str.charAt(0))) {
			String ch = toPinYin(str.charAt(0));
			return ch.toUpperCase().charAt(0);
		} else {
			return str.toUpperCase().charAt(0);
		}
	}

	public static String toPinYin(char hanzi) {
		HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
		hanyuPinyin.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		// hanyuPinyin.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
		hanyuPinyin.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
		String[] pinyinArray = null;
		try {
			// 是否在汉字范围内
			if (hanzi >= 0x4e00 && hanzi <= 0x9fa5) {
				pinyinArray = PinyinHelper.toHanyuPinyinStringArray(hanzi,
						hanyuPinyin);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		// 将获取到的拼音返回
		return pinyinArray[0];
	}
}
