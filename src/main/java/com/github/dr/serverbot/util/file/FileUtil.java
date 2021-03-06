package com.github.dr.serverbot.util.file;

import org.jetbrains.annotations.NonNls;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
//Java

@NonNls
public class FileUtil {

	private static File file;
	private static String filepath;

	public FileUtil(String filepath){
		FileUtil.filepath = filepath;
		file = new File(filepath);
	}

	public FileUtil(File file, String filepath){
		FileUtil.file = file;
		FileUtil.filepath = filepath;
	}

	public synchronized static FileUtil File(String tofile) {
		File file;
		String filepath;
		String to = tofile;
		if (null!=tofile) {
			final String pth = "/";
            if(!pth.equals(String.valueOf(tofile.charAt(0)))) {
                to = "/" + tofile;
            }
        }
		try {
			File directory = new File("");
			filepath=directory.getCanonicalPath()+to;
			file = new File(filepath);
			if (null==tofile) {
                file = new File(directory.getCanonicalPath());
            }
		} catch (Exception e) {	
			filepath=System.getProperty("user.dir")+to;
			file = new File(filepath);
			if (null==tofile) {
                file = new File(System.getProperty("user.dir"));
            }
		}
		return new FileUtil(file,filepath);
	}

	public boolean exists() {
		return (file.exists());
	}

	public String getPath() {
		return filepath;
	}

	public String getPath(String filename) {
		String temp = filepath;
		new FileUtil(temp+"/"+filename);
		return temp+"/"+filename;
	} 

	public FileUtil toPath(String filename) {
		return new FileUtil(filepath+"/"+filename);
	}

	public List<File> getFileList() {
		File[] array = file.listFiles();
		List<File> filelist = new ArrayList<File>();
		for(int i=0;i<array.length;i++){
			if(!array[i].isDirectory()) {
                if(array[i].isFile()) {
                    filelist.add(array[i]);
                }
            }
		}
		return filelist;
	}

	public static void writefile(Object log, boolean cover) {
		OutputStreamWriter osw = null;
		try {
			File parent = file.getParentFile();
			if(!parent.exists()) {
                parent.mkdirs();
            }
			if(!file.exists()) {
                file.createNewFile();
            }
			FileOutputStream write = new FileOutputStream(file,cover);
			osw = new OutputStreamWriter(write, "UTF-8"); 
			osw.write(log.toString());
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != osw) {
                try {
					osw.flush();
				} catch (IOException e) {
					throw new RuntimeException("");
				}
            }

		}
	}

	public InputStreamReader readconfig() {
		try {
			return new InputStreamReader(new FileInputStream(file), "UTF-8");
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		return null;
	}

	public Object readfile(boolean list) {
		try {
			return readfile(list,new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		return null;
	}

	public static Object readfile(boolean list, InputStreamReader isr) {
		try { 
			BufferedReader br = new BufferedReader(isr); 
			String line = null; 
			if(list){
				List<String> FileContent = new ArrayList<String>();
				while ((line = br.readLine()) != null) { 
					FileContent.add(line);
				} 
				return FileContent;
			} else {
				String FileContent = "";
				while ((line = br.readLine()) != null) { 
					FileContent += line; 
					FileContent += "\r\n";
				}
				return FileContent;
			}
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		return null;
	}

}