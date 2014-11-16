package com.sytm.application;

import android.annotation.SuppressLint;
import java.util.regex.Pattern;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

@SuppressLint("DefaultLocale")
public class HanZiUtils {
	/**
	 * 返回一个字大写头字母
	 * 
	 * @param hanzi
	 * @return
	 */
	public static String toPinYin(String hanzi) {
		if (hanzi == null) {
			return "#";
		}
		if (hanzi.trim().length() == 0) {
			return "#";
		}
		char c = hanzi.charAt(0);
		if (c == '长') {
			return "C";
		}
		HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
		hanyuPinyin.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		hanyuPinyin.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		hanyuPinyin.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
		String[] pinyinArray = null;
		try {
			// 是否在汉字范围内
			if (c >= 0x4e00 && c <= 0x9fa5) {
				pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c,
						hanyuPinyin);
			} else {
				char b = hanzi.trim().substring(0, 1).charAt(0);
				// 正则表达式，判断首字母是否是英文字母
				Pattern pattern = Pattern.compile("^[A-Za-z]+$");
				if (pattern.matcher(b + "").matches()) {
					return (b + "").toUpperCase();
				} else {
					return "#";
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		String string = "";
		// 将获取到的拼音返回
		if (pinyinArray != null) {
			string = String.valueOf(pinyinArray[0].charAt(0));
		}
		return string.toUpperCase();
	}

	/**
	 * 将中文字符转换为相应的数组
	 * 
	 * @param chineseCharacter
	 * @param toneSelection
	 * @param vcharSelection
	 * @param caseSelection
	 */
	public static String updateFormattedText(String chineseCharacter) {
		// 拼音格式化对象
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		outputFormat.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
		// 获取字符串
		char[] cnStr = chineseCharacter.toCharArray();
		StringBuffer buffer = new StringBuffer();
		String outputString= "";
		for (char chineseChar : cnStr) {
			// 将字符转换为拼音数组
			String[] pinyinArray = null;
			if (chineseChar >= 0x4e00 && chineseChar <= 0x9fa5) {
				try {
					pinyinArray = PinyinHelper.toHanyuPinyinStringArray(
							chineseChar, outputFormat);
				} catch (BadHanyuPinyinOutputFormatCombination e1) {
					e1.printStackTrace();
				}
				outputString = concatPinyinStringArray(pinyinArray);
				buffer.append(outputString);
			} else {
				buffer.append(chineseChar);
			}
		}
		return buffer.toString();
	}

	/**
	 * 将转换的拼音数组转换为字符串
	 * 
	 * @param pinyinArray
	 * @return
	 */
	private static String concatPinyinStringArray(String[] pinyinArray) {
		StringBuffer pinyinStrBuf = new StringBuffer();
		if ((null != pinyinArray) && (pinyinArray.length > 0)) {
			for (int i = 0; i < pinyinArray.length; i++) {
				pinyinStrBuf.append(pinyinArray[i]);
				// pinyinStrBuf.append(System.getProperty("line.separator"));
			}
		}
		String outputString = pinyinStrBuf.toString();
		return outputString;
	}
}
