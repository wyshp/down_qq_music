/**
 * DownQQMusicApp.java <br>
 * com.utaowo.down_qq_music <br>
 * Copyright (c) WYS 2019.
 */
package com.utaowo.down_qq_music;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.utaowo.utils.HttpUtil;

/**
 * 下载QQ官方收费音乐
 * <p>
 *
 * @author   WYS
 * @date	 2019年3月2日
 * @version  v1.0.0
 */
public class DownQQMusicApp {

	public static final String __LIST = "list";
	public static final String __SONG = "song";
	public static final String __TAB = "tab";
	public static final String __SINGER = "singer";
	public static final String __NAME = "name";
	public static final String __SONGNAME = "songname";
	public static final String __SONGMID = "songmid";
	public static final String __MP3_L = "mp3_l";
	public static final String __MID = "mid";

	public static final String QUERY_MUSIC_URL = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p=1&n=20&w=";
	public static final String DOWNURL = "http://www.douqq.com/qqmusic/qqapi.php";

	public static String getSeachData(String keyWord) {
		String url = QUERY_MUSIC_URL + keyWord;
		String resultStr = HttpUtil.doGet(url);
		int begin = resultStr.indexOf(__SONG) + 6;
		int end = resultStr.indexOf(__TAB) - 2;
		return resultStr.substring(begin, end);
	}

	public static Map<Integer, String> getMusicDic(JSONArray jsonArrayData) {
		Map<Integer, String> sonmidMap = new HashMap<Integer, String>();
		// 将音乐放入map，提供下载选择
		for (int i = 0; i < jsonArrayData.size(); i++) {
			JSONObject jsonObject = jsonArrayData.getJSONObject(i);
			JSONArray jsonArray = jsonObject.getJSONArray(__SINGER);
			String songName = jsonArray.getJSONObject(0).getString(__NAME);

			System.out.println(i + "---------" + jsonObject.getString(__SONGNAME) + "------" + songName);

			sonmidMap.put(i, jsonObject.getString(__SONGMID));
		}
		return sonmidMap;
	}

	public static String getDownLoadUrl(Map<Integer, String> musicDicMap, int order) {
		String post = HttpUtil.doPost(DOWNURL, "mid=" + musicDicMap.get(order));

		String res = com.alibaba.fastjson.JSON.parseObject(post, String.class);
		JSONObject jsp = JSONObject.parseObject(res);
		String byPath2 = jsp.getString(__MP3_L);

		return byPath2;
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		System.out.print("请输入歌曲名称：");
		String _keyWord = new Scanner(System.in).nextLine();

		String jsonData = getSeachData(_keyWord);

		JSONObject objectJsonData = JSONObject.parseObject(jsonData);
		JSONArray jsonArrayData = objectJsonData.getJSONArray(__LIST);

		Map<Integer, String> sonmidMap = getMusicDic(jsonArrayData);
		System.out.print("请输入歌曲编号：");
		int nextInt = new Scanner(System.in).nextInt();

		String downLoadUrl = getDownLoadUrl(sonmidMap, nextInt);
		System.out.println("请用浏览器打开，如果是H5播放页面，则点击播放页面省略号中的下载按钮即可");
		System.out.println(downLoadUrl);
	}
}
