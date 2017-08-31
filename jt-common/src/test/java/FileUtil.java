import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FileUtil {
	private static final String classNo = "19";

	@Test // 生成当前天的目录，没有目录创建day01，有了编写最后一天的dayxx
	public void create() throws IOException {
		String srcDir = "D:\\tonyVideo\\" + classNo + "\\jt";
		String tempFile = "D:\\tonyVideo\\课堂笔记.docx";

		File dir = new File(srcDir);
		File[] fs = dir.listFiles();
		if (null == fs) { // 空目录，则创建day01，拷贝word文档
			srcDir += "\\day01";
			FileUtil.makeDir(srcDir);
			FileUtils.copyFile(new File(tempFile), new File(srcDir + "\\课堂笔记.docx"));
		} else {
			Integer days = fs.length + 1;
			srcDir += "\\day" + String.format("%02d", days);
			FileUtil.makeDir(srcDir);
			FileUtils.copyFile(new File(tempFile), new File(srcDir + "\\课堂笔记.docx"));
		}
		System.out.println(srcDir);
	}
	
	@Test // 拷贝代码
	public void cpcode() throws Exception {
		String srcDir = "D:\\javaws\\" + classNo;
		String descDir = "D:\\tonyVideo\\" + classNo + "\\jt";
		String zipFileName = "jt-"+getDays(descDir)+".zip";
		descDir += "\\" + getDays(descDir);
		String tempDir = "D:\\tonyVideo\\" + classNo + "\\" +zipFileName.substring(0, zipFileName.lastIndexOf("."));	//在这个目录中组装数据
		
		//文件后缀为avi的不写入压缩包
		String extNameStr = "avi";	//^(?i).*?\\.(jpg|png|gif|bmp)$

		//复制到临时目录，最终拷贝回来
		cp(srcDir, tempDir);		//复制项目文件
		copyFilter(descDir, tempDir, extNameStr);		//复制本目录下非视频文件，这样课堂笔记等就都放在一个压缩包中
		fileMove(zip(tempDir, zipFileName), descDir+"/"+zipFileName);
		
		System.out.println("\n处理完成!");
	}
	
	
	public static void cp(String srcDir, String descDir) throws Exception {
		cp(srcDir, descDir, null);
	}
	//拷贝文件到指定目录，支持过滤后缀
	public static void cp(String srcDir, String descDir, String extNameStr) throws Exception {
		deleteAllFilesOfDir(new File(descDir));
		FileUtil.makeDir(descDir);

		File jtdir = new File(srcDir);
		File[] fs = jtdir.listFiles();
		for (File _dir : fs) {
			String dirName = _dir.getName();
			if (dirName.startsWith("jt-")) {
				if(null==extNameStr){
					copy(srcDir + "\\" + dirName, descDir + "\\" + dirName);
				}else{
					copyFilter(srcDir + "\\" + dirName, descDir + "\\" + dirName, extNameStr);
				}
				System.out.println(dirName + "目录下的文件拷贝完成!");
			}
		}
	}
	
	//压缩，返回压缩的全路径文件名
	public static String zip(String descDir, String zipFileName) throws Exception {
		String parentDir = descDir.substring(0, descDir.lastIndexOf("\\"))+"/";		//去掉最后一层目录，把文件放在父目录中
		compress(descDir, parentDir + zipFileName);
		deleteAllFilesOfDir(new File(descDir));
		System.out.println("\n压缩完成!");
		
		return parentDir + zipFileName;
	}

	// 获取最大天数
	public static String getDays(String srcDir) {
		return getDays(srcDir, 0);
	}

	// 获取下一天天数
	public static String getNextDays(String srcDir) {
		return getDays(srcDir, 1);
	}

	public static String getDays(String srcDir, Integer addValue) {
		File dir = new File(srcDir);
		File[] fs = dir.listFiles();
		if (null == fs) { // 空目录，则创建day01，拷贝word文档
			return "day01";
		} else {
			Integer days = fs.length + addValue;
			return "day" + String.format("%02d", days);
		}
	}

	// 删除目录
	public static void deleteAllFilesOfDir(File path) {
		if (!path.exists())
			return;
		if (path.isFile()) {
			path.delete();
			return;
		}
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			deleteAllFilesOfDir(files[i]);
		}
		path.delete();
	}

	// 路径中的多层目录,如果不存在,则建立(mkdir－只可建最后一层目录)
	public static synchronized void makeDir(String dirPath) throws FileNotFoundException {
		String s = "";

		dirPath = dirPath.replaceAll("\\t", "/t"); // replace tab key
		dirPath = dirPath.replaceAll("\\\\", "/");
		String[] aPath = dirPath.split("/");
		for (int i = 0; i < aPath.length; i++) {
			s = s + aPath[i] + "/";
			// System.out.println(s);
			File d = new File(s);
			if (!d.exists()) {
				d.mkdir();
			}
		}
	}
	
	private static void copy(String src, String des) {
		File file1 = new File(src);
		if (file1.exists()) {
			File[] fs = file1.listFiles();
			File file2 = new File(des);
			if (!file2.exists()) {
				file2.mkdirs();
			}
			for (File f : fs) {
				if (f.isFile()) {
					if (!f.getName().startsWith(".")) {
						fileCopy(f.getPath(), des + "\\" + f.getName()); // 调用文件拷贝的方法
					}
				} else if (f.isDirectory()) {
					if (!f.getName().equals("target") && !f.getName().startsWith(".")) {
						copy(f.getPath(), des + "\\" + f.getName());
					}
				}
			}
		}
	}
	
	//通过正则表达式过滤文件扩展名
	private static void copyFilter(String src, String des,String extNameStr) {
		File file1 = new File(src);
		if (file1.exists()) {
			File[] fs = file1.listFiles();
			File file2 = new File(des);
			if (!file2.exists()) {
				file2.mkdirs();
			}
			for (File f : fs) {
				if (f.isFile()) {
					if (!f.getName().startsWith(".")) {
						fileCopy(f.getPath(), des + "\\" + f.getName(), extNameStr); // 调用文件拷贝的方法
					}
				} else if (f.isDirectory()) {
					if (!f.getName().equals("target") && !f.getName().startsWith(".")) {
						copyFilter(f.getPath(), des + "\\" + f.getName(), extNameStr);
					}
				}
			}
		}
	}

	/**
	 * 文件拷贝的方法
	 * regex：去除文件的正则
	 */
	public static void fileCopy(String src, String des, String extNameStr) {
		//^(?i).*?\\.(jpg|png|gif|bmp)$
		if(src.matches("^(?i).*?\\.("+extNameStr+")$")){
			return;		//去除这些文件
		}
        File srcFile = new File(src);    
        File targetFile = new File(des);    
        try {    
            InputStream in = new FileInputStream(srcFile);     
            OutputStream out = new FileOutputStream(targetFile);    
            byte[] bytes = new byte[10240];    
            int len = -1;    
            while((len=in.read(bytes))!=-1) {    
                out.write(bytes, 0, len);    
            }    
            in.close();    
            out.close();    
        } catch (FileNotFoundException e) {    
            e.printStackTrace();    
        } catch (IOException e) {    
            e.printStackTrace();    
        }  
	}
	
	//复制文件
	public static void fileCopy(String src, String des) {
		File srcFile = new File(src);    
		File targetFile = new File(des);    
		try {    
			InputStream in = new FileInputStream(srcFile);     
			OutputStream out = new FileOutputStream(targetFile);    
			byte[] bytes = new byte[10240];    
			int len = -1;    
			while((len=in.read(bytes))!=-1) {    
				out.write(bytes, 0, len);    
			}    
			in.close();    
			out.close();    
		} catch (FileNotFoundException e) {    
			e.printStackTrace();    
		} catch (IOException e) {    
			e.printStackTrace();    
		}  
	}
	//移动文件
	public static void fileMove(String src, String des) {
		File srcFile = new File(src);    
		File targetFile = new File(des);    
		try {    
			InputStream in = new FileInputStream(srcFile);     
			OutputStream out = new FileOutputStream(targetFile);    
			byte[] bytes = new byte[10240];    
			int len = -1;    
			while((len=in.read(bytes))!=-1) {    
				out.write(bytes, 0, len);    
			}    
			in.close();    
			out.close();  
			
			srcFile.delete();		//复制完成，删除文件
		} catch (FileNotFoundException e) {    
			e.printStackTrace();    
		} catch (IOException e) {    
			e.printStackTrace();    
		}  
	}

	public static void compress(String srcFilePath, String destFilePath) {
		File src = new File(srcFilePath);
		if (!src.exists()) {
			throw new RuntimeException(srcFilePath + "不存在");
		}
		File zipFile = new File(destFilePath);
		try {
			FileOutputStream fos = new FileOutputStream(zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
			ZipOutputStream zos = new ZipOutputStream(cos);
			String baseDir = "";
			compressbyType(src, zos, baseDir);
			zos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void compressbyType(File src, ZipOutputStream zos, String baseDir) {
		if (!src.exists())
			return;
		if (src.isFile()) {
			compressFile(src, zos, baseDir);
		} else if (src.isDirectory()) {
			compressDir(src, zos, baseDir);
		}
	}

	private static void compressFile(File file, ZipOutputStream zos, String baseDir) {
		if (!file.exists())
			return;
		try {
			BufferedInputStream bis = new BufferedInputStream(
			new FileInputStream(file));
			ZipEntry entry = new ZipEntry(baseDir + file.getName());
			zos.putNextEntry(entry);
			int count;
			byte[] buf = new byte[10240];
			while ((count = bis.read(buf)) != -1) {
				zos.write(buf, 0, count);
			}
			bis.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static void compressDir(File dir, ZipOutputStream zos, String baseDir) {
		if (!dir.exists())
			return;
		File[] files = dir.listFiles();
		if (files.length == 0) {
			try {
				zos.putNextEntry(new ZipEntry(baseDir + dir.getName() + File.separator));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (File file : files) {
			compressbyType(file, zos, baseDir + dir.getName() + File.separator);
		}
	}
}