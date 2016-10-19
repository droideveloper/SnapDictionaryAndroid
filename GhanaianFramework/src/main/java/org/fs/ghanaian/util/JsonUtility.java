/*
Copyright 2014 GhanaianFramework.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.fs.ghanaian.util;

import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Fatih on 6.10.2014.
 */
public class JsonUtility
{	
	public static Object parse(String rawJson, Object defaultValue)
    {
		Object json = defaultValue;

		if (!TextUtils.isEmpty(rawJson))
        {
			try
            {
				json = new JSONTokener(rawJson).nextValue();
			}
            catch (JSONException e)
            {
                e.printStackTrace();
			}
		}

		return json;
	}

	public static JSONObject parseObject(String rawJson, JSONObject defaultValue)
    {
		Object json = JsonUtility.parse(rawJson, defaultValue);
		return (json instanceof JSONObject) ? ((JSONObject) json)
				: (defaultValue);
	}

	public static JSONArray parseArray(String rawJson, JSONArray defaultValue)
    {
		Object json = JsonUtility.parse(rawJson, defaultValue);
		return (json instanceof JSONArray) ? ((JSONArray) json)
				: (defaultValue);
	}

	public static Object getJsonValue(JSONObject json, String key,	Object defaultValue)
    {
		Object value = defaultValue;

		if (json != null)
        {
			try
            {
				if (!json.isNull(key))
                {
					value = json.get(key);
				}
			}
            catch (Exception e)
            {
                e.printStackTrace();
			}
		}

		return value;
	}

	public static String getJsonString(JSONObject json, String key, String defaultString)
    {
		Object value = getJsonValue(json, key, defaultString);

		String string = (value == null) ? ("") : (value.toString());

		return string;
	}

	public static Boolean getJsonBoolean(JSONObject json, String key, Boolean defaultBoolean)
    {
		Object booleanValue = getJsonValue(json, key, defaultBoolean);

		return ((booleanValue instanceof Boolean) ? ((Boolean) booleanValue)
				: (defaultBoolean));
	}
	
	public static Integer getJsonInteger(JSONObject json, String key, Integer defaultInteger)
    {
		Object integerValue = getJsonValue(json, key, defaultInteger);

		return ((integerValue instanceof Integer) ? ((Integer) integerValue)
				: (defaultInteger));
	}

	public static JSONObject getJsonObject(JSONObject json, String key, JSONObject defaultJsonObject)
    {
		Object jsonObject = getJsonValue(json, key, defaultJsonObject);

		return ((jsonObject instanceof JSONObject) ? ((JSONObject) jsonObject)
				: (defaultJsonObject));
	}

	public static JSONArray getJsonArray(JSONObject json, String key, JSONArray defaultJsonArray)
    {
		Object jsonArray = getJsonValue(json, key, defaultJsonArray);

		return ((jsonArray instanceof JSONArray) ? ((JSONArray) jsonArray)
				: (defaultJsonArray));
	}

	public static Object getArrayValue(JSONArray array, int index,	Object defaultValue)
    {
		Object value = defaultValue;

		if (array != null)
        {
			try
            {
				if (!array.isNull(index))
                {
					value = array.get(index);
				}
			}
            catch (Exception e)
            {
                e.printStackTrace();
			}
		}

		return value;
	}

	public static String getArrayString(JSONArray array, int index,	String defaultString)
    {
		Object value = getArrayValue(array, index, defaultString);

		String string = (value == null) ? ("") : (value.toString());

		return string;
	}

	public static JSONObject getArrayObject(JSONArray array, int index,	JSONObject defaultJsonObject)
    {
		Object value = JsonUtility.getArrayValue(array, index,
                defaultJsonObject);

		return ((value instanceof JSONObject) ? ((JSONObject) value)
				: (defaultJsonObject));
	}
	
	public static Bundle getArrayBundle(JSONArray array, int index, Bundle defaultBundle)
	{
		Bundle bundle = null;
		Object value = JsonUtility.getArrayValue(array, index, defaultBundle);
		if(value instanceof String)
		{
			JSONObject jsonObject = parseObject((String) value, null);
			
			if(jsonObject != null)
			{
				bundle = parseBundle(jsonObject);
			}
		}
		
		return bundle;
	}
	
	public static JSONObject parseObject(Bundle bundle)
	{
		JSONObject jsonObject = new JSONObject();
		
		if(bundle != null)
		{
			Set<String> keys = bundle.keySet();
			for (String key : keys)
			{
				Object object = bundle.get(key);
				
				if(object instanceof Bundle)
				{
					object = parseObject((Bundle) object);
				}
				
				try
				{
					jsonObject.put(key, object);
				}
				catch (JSONException e)
				{
					if(isLogEnabled())
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		return jsonObject;
	}
	
	@SuppressWarnings("unchecked")
	public static Bundle parseBundle(JSONObject jsonObject)
	{
		Bundle bundle = null;
		
		if(jsonObject != null)
		{
			bundle = new Bundle(jsonObject.length());
			
			for(Iterator<String> iterator = jsonObject.keys(); iterator.hasNext();)
			{
				String key = iterator.next();
				
				Object object = null;
				try
				{
					object = jsonObject.get(key);
				}
				
				catch (JSONException e)
				{
					if(isLogEnabled())
					{
						e.printStackTrace();
					}
				}
				putBundleObject(bundle, key, object);
			}
		}
		
		return bundle;
	}
	
	private static void putBundleObject(Bundle bundle, String key, Object object)
	{
		if(bundle !=null && object != null && !TextUtils.isEmpty(key))
		{
			if(object instanceof String)
			{
				bundle.putString(key, (String) object);
			}
			else if(object instanceof Boolean)
			{
				bundle.putBoolean(key, (Boolean) object);
			}
			else if(object instanceof Double)
			{
				bundle.putDouble(key, (Double) object);
			}
			else if(object instanceof Float)
			{
				Float value = (Float) object;
				
				bundle.putDouble(key, (double) value);
			}
			else if(object instanceof Integer)
			{
				bundle.putInt(key, (Integer) object);
			}
			else if(object instanceof Long)
			{
				bundle.putLong(key, (Long) object);
			}
			else if(object instanceof JSONObject)
			{
				object = parseBundle((JSONObject) object);
				
				bundle.putBundle(key, (Bundle) object);
			}
			else if(object instanceof JSONArray)
			{
				int elementQuantity = ((JSONArray) object).length();
				Bundle subBundle = new Bundle(elementQuantity);
				
				for (int i = 0; i < elementQuantity; i++)
				{
					Object subObject = getArrayValue((JSONArray) object, i, null);
					
					if(subObject != null)
					{
						putBundleObject(subBundle, key, subObject);
					}
				}
			}
		}
	}
	
	protected static boolean isLogEnabled()
	{
		return false;
	}

	protected static String getLogTag()
	{
		return null;
	}
}
