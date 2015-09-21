package com.hylg.igolf.ui.common;

import java.util.HashMap;

public class RegionData {
	public String name;
	public String dictKey;
	// 主要用于省级存储市级
//	public HashMap<String, RegionData> children;
	// 只要key，name
	public HashMap<String, String> children;
	public RegionData(String name, String dictKey) {
		this(name, dictKey, null);
	}
	public RegionData(String name, String dictKey, HashMap<String, String> children) {
		this.name = name;
		this.dictKey = dictKey;
		this.children = children;
	}
//	public RegionData(String name, String dictKey, HashMap<String, RegionData> children) {
//		this.name = name;
//		this.dictKey = dictKey;
//		this.children = children;
//	}
}
