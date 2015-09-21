package com.hylg.igolf.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;


public class FileTool {
	private final static String 					TAG = "FileTool";
	private Context 								mContext = null;
	
	public static FileTool 							mFileTool;
	
	public static FileTool getInstance(Context context)
	{
		if (mFileTool==null) {
			mFileTool = new FileTool(context);
		}
		return mFileTool;
	}
	
	public FileTool (Context context) {
		this.mContext = context;
	}
	
	/*
	 * ����ļ�·�����ļ���ƴ����ļ�
	 * @parameters filePath  �ļ�·��
	 * @parameters fileName  �ļ����
	 * @return �ļ������ɹ�true��ʧ��false
	 * */
	public boolean createFileByFilePathAndFileName (String filePath,String fileName)
	{
		File file = new File(filePath);
		boolean result = false;
		if (!file.exists()) {
			if (file.mkdirs()) {
				result = true;
			} else {
				result = false;
			}
		}
		//����Ŀ¼
		if (fileName != null) {
			file = null;
			file = new File(filePath, fileName);
			if (!file.isDirectory()) {
				try
				{
					result = file.createNewFile();
				}
				catch(Exception ex)
				{
					result = false;
					ex.printStackTrace();
				}
			}
		}
		file = null;
		return result;
	}
	
	/*
	 * ����ļ�·�����ļ���ƴ����ļ�
	 * @parameters filePath  �ļ�·��
	 * @parameters fileName  �ļ����
	 * @return �ļ������ɹ�true��ʧ��false
	 * */
	public  boolean createDirByDirPathAndDirName (String dirPath,String dirName)
	{
		File file = new File(dirPath+File.separator+dirName);
		boolean result = false;
		if (!file.exists()) {
			if (file.mkdirs()) {
				result = true;
			} else {
				result = false;
			}
		}
		file = null;
		return result;
	}
	
	/*
	 * ����ļ�·�����ļ���ƴ����ļ�
	 * @parameters filePath  �ļ�·��
	 * @parameters fileName  �ļ����
	 * @return �ļ������ɹ�true��ʧ��false
	 * */
	public  boolean createDirByDirPath (String dirPath)
	{
		File file = new File(dirPath);
		boolean result = false;
		boolean temp = file.exists();
		if (!temp) {
			if (file.mkdirs()) {
				result = true;
			} else {
				result = false;
			}
		}
		file = null;
		return result;
	}
	
	/*����ļ�·�������ļ���д������
	 * @param filePath �ļ�·��
	 * @param content �ֽ�����
	 * @FileNotFoundException  if file cannot be opened for writing  
	 * */
	public  void writeToFile (String filePath , byte[] content)
	{
		File file = new File(filePath);
		FileOutputStream fos = null;
		if (file.exists() && !file.isDirectory() && file.length()==0) {
			try
			{
				fos = new FileOutputStream(file);
				fos.write(content);
				fos.flush();
				fos.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		file =null;
	}
	
	/*����ļ�·�������ļ��ж�ȡ���ݵ��ֽ�����
	 * 
	 * 
	 * */
	public  void readFromFile(String filePath)
	{
		byte[] content = null;
		byte[] buffer = null;
		File file = new File(filePath);
		FileInputStream fis = null;
		if (file.exists() && file.isFile() && file.length() > 0) {
			try
			{
				fis = new FileInputStream(file);
				content = new byte[fis.available()];
				buffer = new byte[1024];
				int count = 0;
				int tempCount = 0;
				int tempByte = 0;
				while ((tempByte=fis.read()) != -1) {
					tempCount = fis.read(buffer);
					for (int i=0 ; i<count ; i++) {
						content[count+i] = buffer[i];
					}
					count+=tempCount;
				}
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				try
				{
					fis.close();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	/*����ļ�·���ж��ļ��Ƿ����
	 * @param filePath �ļ�·��
	 * @return ����true��������false
	 * */
	public  boolean isFileExists (String filePath)
	{
		File file = new File (filePath);
		return file.exists();
	}
	

	
	/*����ļ���·��ɾ���ļ�
	 * @param filePath �ļ���·��
	 * @return �ɹ�true ʧ��false
	 * */
	public  boolean deleteFileByFilePath (String filePath)
	{
		if (filePath == null || filePath.length() <= 0 ) {
			return false;
		}
		File file = new File(filePath);
		
		if (file != null && file.exists() && file.isFile()) {
			return file.delete();
		}
		return false;
	}
	
	/*����ļ���·��ɾ���ļ�
	 * @ɾ��Ŀ¼��ֻ��Ŀ¼Ϊ�յ�ʱ����ֱ��ɾ�����ʹ�õݹ�ɾ����Ŀ¼
	 * */
	public  boolean deleteDirByDirPath (File file)
	{
		if (file.exists() && file.isDirectory()) {
			File [] temp =  file.listFiles();
			if (temp.length == 0) {
				file.delete();
			} else {
				for (int i=0 ; i<temp.length ; i++) {
					File file1 = temp[i];
					if (file.isDirectory()) {
						deleteDirByDirPath(file1);
					}
					file1.delete();
				}
			}
		}
		//Ŀ¼��ʱΪ�գ��ſ���ɾ��Ŀ¼
		return file.delete();
	}
	
	/*����ɾ���ļ�
	 * */
	public  boolean deleteFileByFilePathData (ArrayList<String> selectFilePathDataArrary)
	{
		boolean result = false;
		
		if (selectFilePathDataArrary != null && selectFilePathDataArrary.size() > 0) {
			for (int i=0 ; i<selectFilePathDataArrary.size() ; i++) {
				File file = new File(selectFilePathDataArrary.get(i));
				//�ļ�ֱ��ɾ��
				if (file.exists() && file.isFile()) {
					result =  file.delete();
				}
				//Ŀ¼��ɾ��
				if (file.exists() && file.isDirectory()) {
					result = deleteDirByDirPath(file);
				}
			}
		}
	
		return result;
	}
	

	
	
	/*
	 * ���ͼƬ·������ȡͼƬ��bitmap
	 * *@param path �ļ�·��
	 */
	public Bitmap getBitmapByFielURI (Uri uri)
	{
		InputStream in =null;
		try
		{
			in = mContext.getContentResolver().openInputStream(uri);
		}
		catch(FileNotFoundException ex)
		{
			ex.printStackTrace();
		}
		Bitmap result  = BitmapFactory.decodeStream(in);
		return result;
	}
	
	/*��ȡ������ļ�*/
	public File getOutputMediaFile (int type, String timeStamp){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File("/sdcard/igolfPictrue");
        
        File timeStampStorageDir = new File("/sdcard/igolfPictrue"+ File.separator +timeStamp);
        
        //DebugTools.getDebug().debug_v("�洢ͼƬ��·��mediaStorageDir", "-------"+mediaStorageDir.getAbsolutePath());
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                //Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        
        if (! timeStampStorageDir.exists()){
            if (! timeStampStorageDir.mkdirs()){
                //Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
                                                                                                                                                                                                                                   
        // Create a media file name
        //String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 0) {
        	
           mediaFile = new File(timeStampStorageDir.getPath()+File.separator+System.currentTimeMillis() +".jpg");
           //mediaFile = new File(Global.setSavePath + File.separator +"201308281619" + ".jpg");
           
        } else if (type == 1) {
        	
            mediaFile = new File(timeStampStorageDir.getPath()+ File.separator+System.currentTimeMillis() + ".mp4");
            
        } else {
        	
            return null;
        }

        return mediaFile;
    }
	
	
	/*��ȡ������ļ�*/
	public File getInternalOutputMediaFile (int type, String timeStamp){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

		File mediaStorageDirsdf = Environment.getDataDirectory();
		
        File mediaStorageDir = new File("/sdcard/IGolf");
        
        File timeStampStorageDir = new File("/sdcard/IGolf"+ File.separator +"Picture");
        
        //DebugTools.getDebug().debug_v("�洢ͼƬ��·��mediaStorageDir", "-------"+mediaStorageDir.getAbsolutePath());
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                //Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        
        if (! timeStampStorageDir.exists()){
            if (! timeStampStorageDir.mkdirs()){
                //Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
                                                                                                                                                                                                                                   
        // Create a media file name
        //String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 0) {
        	
           mediaFile = new File(timeStampStorageDir.getPath()+File.separator+System.currentTimeMillis() +".jpg");
           //mediaFile = new File(Global.setSavePath + File.separator +"201308281619" + ".jpg");
           
        } else if (type == 1) {
        	
            mediaFile = new File(timeStampStorageDir.getPath()+ File.separator+System.currentTimeMillis() + ".mp4");
            
        } else {
        	
            return null;
        }

        return mediaFile;
    }
	
	
}
