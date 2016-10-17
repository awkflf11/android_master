package com.foryou.truck.tools;

import android.content.Context;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.foryou.truck.R;

public class StringUtils {
	private static String TAG = "StringUtils";



	/**
	 * @des:只设置Textview字体的颜色，没有设置textView的点击事件
	 * @start,end,都是相对于hint.length()长度而言，
	 */
	public static void setTextViewColor(Context context, String hint, TextView tv,
										int start, int end , int colorid) {
		SpannableString spanText = new SpannableString(hint);
		spanText.setSpan(new ForegroundColorSpan(context.getResources().getColor(
				colorid)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//		ClickableSpan clickSpan = new NoLineClickSpan(Constant.PHONE_NUMBER);
//		spanText.setSpan(clickSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//		tv.setMovementMethod(LinkMovementMethod.getInstance());
//		tv.setHighlightColor(Color.TRANSPARENT);
		tv.setText(spanText);
	}

	/**
	 * @des：实现类似：180****1234效果
	 */
	public static String getStyleText(String str) {
		String tempStr;
		if(str.length()>7){
			char[] chars=str.toCharArray();
			for (int i=0; i<chars.length;i++){
				if(i> 2&& i<=6){
					chars[i]='*';
				}
			}
			tempStr=  new String(chars);
		}else{
			tempStr= str;
		}
		return tempStr;
	}

	/**
	 * @des:切换密码状态的工具
     */

	public static void setPasswordState(boolean passwordState, TextView tv, ImageView imageView) {

		if(passwordState){//密码状态
			//imageView.setBackgroundResource(R.drawable.icon_password2);
			imageView.setImageResource(R.drawable.icon_password2);
			tv.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}else{// 明文状态
			//imageView.setBackgroundResource(R.drawable.icon_password);
			imageView.setImageResource(R.drawable.icon_password);
			tv.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}
	}




	/**
	 * @描述：是否是中文.
	 */
	public static Boolean isChinese(String str) {
		Boolean isChinese = true;
		String chinese = "[\u0391-\uFFE5]";
		if(!isEmpty(str)){
			//获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
			for (int i = 0; i < str.length(); i++) {
				//获取一个字符
				String temp = str.substring(i, i + 1);
				//判断是否为中文字符
				if (temp.matches(chinese)) {
				}else{
					isChinese = false;
				}
			}
		}else{
			isChinese=false;
		}
		return isChinese;
	}

	/**
	 * @描述：是否包含中文.
	 */
	public static Boolean isContainChinese(String str) {
		Boolean isChinese = false;
		String chinese = "[\u0391-\uFFE5]";
		if(!isEmpty(str)){
			//获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
			for (int i = 0; i < str.length(); i++) {
				//获取一个字符
				String temp = str.substring(i, i + 1);
				if (temp.matches(chinese)) {//判断是否为中文字符
					isChinese = true;
				}else{
				}
			}
		}
		return isChinese;
	}

	/**
	 * @描述：判断一个字符串是否为null或空值.
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}



}
