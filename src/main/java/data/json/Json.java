package com.github.dr.serverbot.data.json;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dr.serverbot.util.file.FileUtil;
import com.github.dr.serverbot.data.global.Data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
//Json
//写的越久，BUG越多，伤痕越疼，脾气越差/-活得越久 故事越多 伤痕越疼，脾气越差

public class Json {

	public static JSONObject getData(){
		FileUtil file = FileUtil.File(Data.Plugin_Data_Path).toPath("Data.json");
		JSONObject object = JSONObject.parseObject(file.readfile(false,file.readconfig()).toString());
		return object;
	}
//
	public static void InitializationJson() {
		Map<String, List<String>> date = Collections.synchronizedMap(new HashMap<String, List<String>>());
		date.put("qun", Arrays.asList("123456","11233456"));
		String json = JSONObject.toJSONString(date,SerializerFeature.PrettyFormat);
		FileUtil file = FileUtil.File(Data.Plugin_Data_Path).toPath("Data.json");
		file.writefile(json,true);
	}


}