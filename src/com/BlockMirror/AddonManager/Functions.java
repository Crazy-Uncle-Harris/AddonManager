package com.BlockMirror.AddonManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;

public class Functions {
	public static void downloadFile(String source, String to) {
		try {
			BufferedInputStream in = new java.io.BufferedInputStream(new URL(source).openStream());
			FileOutputStream fos;
			fos = new FileOutputStream(to);
			BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
			byte[] data = new byte[1024];
			int x=0;
			while((x=in.read(data,0,1024))>=0) {
				bout.write(data,0,x);
			}
			bout.close();
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String joinList(List<String> list) {
		String ret = "";
		
		for(String s: list) {
			ret = ret + s + ", ";
		}
		
		if(ret.length() > 2) {
			ret = ret.substring(0, ret.length() - 2);
		}
		return ret;
	}
}
