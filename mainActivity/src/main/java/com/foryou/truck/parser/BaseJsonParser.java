package com.foryou.truck.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseJsonParser implements JsonParser{
	
	
	public <T> List<T> getListByReflect(JSONObject data, String rootName, Class<T> clazz) throws JSONException {

		ArrayList<T> list = new ArrayList<T>();
		JSONArray array = data.getJSONArray(rootName);
		for (int i = 0; i < array.length(); i++) {
			T t = getBeanByReflect(array.getJSONObject(i), clazz);
			list.add(t);
		}
		return list;
	}

	public <T> T getBeanByReflect(JSONObject json, Class<T> clazz) {

		try {
			T t = clazz.newInstance();
			Field[] field = clazz.getDeclaredFields();

			for (Field f : field) {
				
				Class<?> type = f.getType();
				
				if (type.getName().equals(Integer.class.getName()) || Integer.TYPE == type) {
					f.setInt(t, json.getInt(f.getName()));
					
				} else if (type.getName().equals(Float.class.getName()) || Float.TYPE == type) {
					f.setFloat(t, (float) json.getDouble(f.getName()));
					
				} else if (type.getName().equals(Double.class.getName()) || Double.TYPE == type) {
					f.setDouble(t, json.getDouble(f.getName()));
					
				} else if (type.getName().equals(Long.class.getName()) || Long.TYPE == type) {
					f.setLong(t, json.getLong(f.getName()));
					
				} else {
					f.set(t, json.get(f.getName()));
				}
			}

			return t;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
