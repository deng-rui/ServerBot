package com.github.dr.serverbot.util.translation;

// 需要KEY+ID
// 无法白嫖

import com.github.dr.serverbot.util.encryption.MD5;
//

import static com.github.dr.serverbot.net.HttpRequest.doPost;
import static com.github.dr.serverbot.util.ExtractUtil.unicodeDecode;
import static com.github.dr.serverbot.util.IsUtil.NotBlank;
//

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
 
public class Baidu {

	private String id = "";
	private String key = "";

	public String translate(String query, String from, String to) {
		StringBuffer sb = new StringBuffer();
		String salt = String.valueOf(System.currentTimeMillis());
		String result = null;
		sb.append("q=" + query)
		.append("&from=" + from)
		.append("&to=" + to)
		.append("&appid=" + id)
		// 随机数
		.append("&salt=" + salt)
		// 签名
		.append("&sign=" + MD5.md5(id + query + salt + key));
		JSONObject json = new JSONObject().parseObject(doPost("https://api.fanyi.baidu.com/api/trans/vip/translate",sb.toString()));
		JSONArray rArray = json.getJSONArray("trans_result");
		for (int i = 0; i < rArray.size(); i++) {
			JSONObject r = (JSONObject)rArray.get(i);
			if (NotBlank(r)) 
				result = r.getString("dst");
		}
		return unicodeDecode(result);
	}

	public String translate(String query, String to) {
		return translate(query,"auto",to);
	}

}
