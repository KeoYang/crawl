package craw;
/**
 * http://zhixing.court.gov.cn/search/
 * 最后修改时间：2015/5/11
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.httpclient.methods.PostMethod;
//import numberRecognize.MyImgFilter;

public class ImgProcess1 {  
	  
    private static Map<BufferedImage, String> trainMap = null;  
    //private static int index = 0;  
  
    public static int isWhite(int colorInt) {  
        Color color = new Color(colorInt);  
        if (color.getRed() + color.getGreen() + color.getBlue() > 600) {  
            return 1;  
        }  
        return 0;  
    }
    
    public static int isBlack(int colorInt) {  
        Color color = new Color(colorInt);
//        if(color.getRed() + color.getGreen() + color.getBlue() < 500){
//        	System.out.println(color.getRed() + color.getGreen() + color.getBlue());
//        }
        if (color.getRed() + color.getGreen() + color.getBlue() <= 500) {  
            return 1;  
        }  
        return 0;  
    }
    
    public static int isBlackOrWhite(int colorInt) {  
        if (getColorBright(colorInt) < 300 || getColorBright(colorInt) > 600) {  
            return 1;  
        }  
        return 0;  
    }
    
    public static int getRGBSum(int colorInt){
		Color color = new Color(colorInt);
		int sum = color.getBlue() + color.getGreen() + color.getRed();
		return sum;
	}
    
    public static int getColorBright(int colorInt) {  
        Color color = new Color(colorInt);  
        return color.getRed() + color.getGreen() + color.getBlue();  
  
    }
	
    public static BufferedImage removeBackgroud(BufferedImage img)  
            throws Exception {  
//        BufferedImage img = ImageIO.read(new File(picFile));  
		
        int width = img.getWidth();  
        int height = img.getHeight();  
        BufferedImage outputImage = new BufferedImage(width, height, img.getType());
        
        for (int j1 = img.getMinY(); j1 < img.getHeight(); j1++) {
            for (int j2 = img.getMinX(); j2 < img.getWidth(); j2++) {
              int rgb = img.getRGB(j2, j1);
              outputImage.setRGB(j2, j1, rgb);
            }
          }
        
        for (int x = 1; x < width - 3; ++x) {
        	
            Label2:for (int y = 1; y < height - 1; ++y) { 
            	if(isBlack(outputImage.getRGB(x, y)) == 1){
            		if (getColorBright(outputImage.getRGB(x, y)) < 300) {  
                        if (isBlackOrWhite(img.getRGB(x - 1, y))  
                                + isBlackOrWhite(outputImage.getRGB(x + 1, y))  
                                + isBlackOrWhite(outputImage.getRGB(x, y - 1))  
                                + isBlackOrWhite(outputImage.getRGB(x, y + 1)) == 4) {  
                        	outputImage.setRGB(x, y, Color.WHITE.getRGB());  
                        }  
                    } 
            	}
            }  
        }  
//        img = img.getSubimage(1, 1, img.getWidth() - 2, img.getHeight() - 2);  
        return outputImage;  
    }
	
    public static BufferedImage removeBlank(BufferedImage img) throws Exception {  
        int width = img.getWidth();  
        int height = img.getHeight();  
        int start = 0;  
        int end = 0;  
        Label1: for (int y = 0; y < height; ++y) {  
            int count = 0;  
            for (int x = 0; x < width; ++x) {  
                if (isWhite(img.getRGB(x, y)) == 1) {  
                    count++;  
                }  
                if (count >= 1) {  
                    start = y;  
                    break Label1;  
                }  
            }  
        }  
        Label2: for (int y = height - 1; y >= 0; --y) {  
            int count = 0;  
            for (int x = 0; x < width; ++x) {  
                if (isWhite(img.getRGB(x, y)) == 1) {  
                    count++;  
                }  
                if (count >= 1) {  
                    end = y;  
                    break Label2;  
                }  
            }  
        }  
        return img.getSubimage(0, start, width, end - start + 1);  
    }
  
    public static List<BufferedImage> splitImageByFixedWidth(BufferedImage img)  
            throws Exception {  
        List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
        int width = 10;
        int height = img.getHeight();
        int leftLocation = 0;
        int segment = (img.getWidth() + width -1) / width;
        for(int i = 0; i < segment-1; i++){
        	subImgs.add(img.getSubimage(leftLocation + 1, 0, width - 2, height)); 
        	leftLocation += width;
        }
        if(segment > 1){
        	int lastWidth = img.getWidth() - leftLocation;
        	subImgs.add(img.getSubimage(leftLocation, 0, lastWidth, height)); 
        }
        return subImgs;  
    }
    
    public static List<BufferedImage> splitImage_second(BufferedImage img)  
            throws Exception {
    	List<BufferedImage> subImgs = new ArrayList<BufferedImage>();  
        int width = img.getWidth();  
        int height = img.getHeight();
        List<Integer> weightlist = new ArrayList<Integer>(); 
        //traverse all the pixel
        for(int x = 0; x < width; ++x){
            int count = 0;  
            for (int y = 0; y < height; ++y) {  
                if (isBlack(img.getRGB(x, y)) == 1) {  
                    count++;  
                }
            }  
            weightlist.add(count);  
        }
        /**
         * 开始分割...
         */
        boolean backoff = false;
        Label1:for(int i = 0; i < weightlist.size();){
        	//数字1
        	if(weightlist.get(i) >= 19 && weightlist.get(i + 1) >= 19){
        		if(!backoff){
        			subImgs.add(img.getSubimage(i, 0, 2, height));
        		}
        		backoff = false;
        		i += 2;
        		continue;
        	}
        	//横杠-
        	if(weightlist.get(i) == 2 && weightlist.get(i + 1) == 2){
        		++i;
        		continue;
        	}
        	//先判断是否为最后一段
        	if(weightlist.size() - i < 12){
        		//统计black pixel
        		int blackCount = 0;
        		for(int j = 0; j < weightlist.size() - i; ++j){
        			blackCount += weightlist.get(i + j);
        		}
        		if(blackCount < 30 || weightlist.size() - i < 9){
        			break Label1;
        		}
        		
        		for(int s = 0; s < weightlist.size() - i -1; ++s){
        			//数字1
                	if(weightlist.get(i + s) >= 19 && weightlist.get(i + s + 1) >= 19){
                		subImgs.add(img.getSubimage(i + s, 0, 2, height));
                		i += 2;
                		break Label1;
                	}
                	//其他数字
                	if(weightlist.get(i) > 2 && weightlist.get(i + 1) > 2 && weightlist.get(i + 2) > 2){
                		int lastWidth = weightlist.size() - i - s;
            			subImgs.add(img.getSubimage(i + s, 0, lastWidth, height));
            			break Label1;
                	}
                	
        		}
        		break Label1;
    			
        	}
        	//其他数字
        	if(weightlist.get(i) > 2 && weightlist.get(i + 1) > 2 && weightlist.get(i + 2) > 2){
        		int blackCount = 0;
        		for(int j = 0; j < 12; ++j){
        			blackCount += weightlist.get(i + j);
        		}
        		//black pixel太稀疏，跳过该区域
        		if(blackCount < 60){
//        			System.out.println("rubbish");
        			i += 11;
        			continue;
        		}
        		subImgs.add(img.getSubimage(i, 0, 12, height));
        		if(weightlist.get(i + 8) + weightlist.get(i + 9) + weightlist.get(i + 10) + weightlist.get(i + 11) > 47 ||
        				weightlist.get(i + 7) + weightlist.get(i + 8) + weightlist.get(i + 9) + weightlist.get(i + 10) > 47){
        			backoff = true;
        			i += 9;
        			continue;
        		}
        		//判断前4列pixel
        		if(weightlist.get(i) + weightlist.get(i + 1) + weightlist.get(i + 2) + weightlist.get(i + 3) > 47){
        			i += 13;
        			continue;
        		}
        		i += 11;
        		continue;
        	}
        	//空白区域
        	++i;
        }
        
        return subImgs;  
    }
    
    public static List<BufferedImage> splitImage_first(BufferedImage img)  
            throws Exception {  
    	List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
    	
    	
    	int width = img.getWidth();  
        int height = img.getHeight();
        /**
         * split by white segment
         * 横向切分
         */
    	List<Integer> weightlist = new ArrayList<Integer>(); 
        //traverse all the pixel
        for(int x = 0; x < width; ++x){
            int count = 0;  
            for(int y = 0; y < height; ++y){  
                if (isBlack(img.getRGB(x, y)) == 1){  
                    count++;  
                }
            }
            weightlist.add(count);  
        }
    	List<BufferedImage> splitImages = new ArrayList<BufferedImage>();
        for(int it= 0; it < width; ++it){
        	int length = 0;
        	int begin = it;
        	while(it < width-1 && (weightlist.get(it) > 2 || weightlist.get(it+1) > 2)){
        		++length;
        		++it;
        	}
        	if(length > 0){
        		splitImages.add(img.getSubimage(begin, 0, length, height));
        		if(length > 2){
        			--it;
        		}
        	}
        }
        /**
         * 纵向切分
         */
        List<BufferedImage> splitImages_buf = new ArrayList<BufferedImage>();
        for(BufferedImage bufImg : splitImages){
        	int width_buf = bufImg.getWidth();
            int height_buf = bufImg.getHeight();
            
        	List<Integer> weightlist_buf = new ArrayList<Integer>();
            //traverse all the pixel
            for(int y = 0; y < height_buf; ++y){
                int count = 0;  
                for(int x = 0; x < width_buf; ++x){  
                    if (isBlack(bufImg.getRGB(x, y)) == 1){  
                        count++;  
                    }
                }
                weightlist_buf.add(count);  
            }
            
            for(int it= 0; it < height_buf; ++it){
            	int length = 0;
            	int begin = it;
            	while(it < height_buf-1 && (weightlist_buf.get(it) > 2 || weightlist_buf.get(it+1) > 2)){
            		++length;
            		++it;
            	}
            	if(length > 0){
            		splitImages_buf.add(bufImg.getSubimage(0, begin, width_buf, length));
            		if(length > 2){
            			--it;
            		}
            	}
            }
        }
        /**
         * to each partImage
         */
//    	for(BufferedImage part_Image : splitImages_buf){
//    		subImgs.addAll(splitImage_second(part_Image));
//    	}
        return subImgs = splitImages = splitImages_buf;  
    }
    
    
    public static Map<BufferedImage, String> loadTrainData(String trainingDataPath) throws Exception {  
        if (trainMap == null) {
        	System.err.println("loading train data...");
            Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();  
            //设置训练样本目录
            File dir = new File(trainingDataPath);  
            File[] files = dir.listFiles();  
            for (File file : files) {  
                map.put(ImageIO.read(file), file.getName().charAt(0) + "");  
            }  
            trainMap = map;  
        }  
        return trainMap;  
    }  
  
    public static String getSingleCharOcr(BufferedImage img,  
            Map<BufferedImage, String> map) {  
        String result = "#";  
        int width = img.getWidth();  
        int height = img.getHeight();  
        int max = 0;  
        //Traverse training_data
        for (BufferedImage bi : map.keySet()) {  
            int count = 0;  
//            if (Math.abs(bi.getWidth()-width) > 2)  
//                continue;  
            int widthmin = width < bi.getWidth() ? width : bi.getWidth();  
            int heightmin = height < bi.getHeight() ? height : bi.getHeight();  
            Label1: for (int x = 0; x < widthmin; ++x) {  
                for (int y = 0; y < heightmin; ++y) {  
                	//设置参数，调整与样本之间允许的差值
                    if (Math.abs(getRGBSum(img.getRGB(x, y)) - getRGBSum(bi.getRGB(x, y)))  < 200) {  
                        count++;  
                        if (count >= width * height)  
                            break Label1;  
                    }  
                }
            }
            if (count > max){
            	max = count;  
                result = map.get(bi);  
            }  
        }  
        return result;  
    }  
  
//    public static String numberRecognition(String imagePath, String trainingDataPath){
//    	String result = ""; 
//        FileInputStream fileInput;
//		try {
//			fileInput = new FileInputStream(imagePath);
//			BufferedImage imgBuff;
//			imgBuff = ImageIO.read(fileInput);
//			//图片过滤
//        	MyImgFilter imgFilter = new MyImgFilter(imgBuff);
//        	imgFilter.changeGrey();
//        	imgFilter.getGrey();
//        	imgBuff=imgFilter.getProcessedImg();
//        	//分割图片
//        	List<BufferedImage> listImg = splitImage_first(imgBuff);
//            //识别
//            Map<BufferedImage, String> map = loadTrainData(trainingDataPath);  
//             
//            for (BufferedImage ib : listImg) {  
//                result += getSingleCharOcr(ib, map);  
//            } 
////            System.out.println(imagePath + ": " + result);
//		}catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        return result;
//    }
 
  
    public static void downloadImage() {  
        HttpClient httpClient ; 
        GetMethod getMethod ;
//        deleteAll(new File("data\\httpzhixing.court.gov.cnsearch\\raw_sample"));
        for (int i = 1001; i < 1100; i++) {
            try {  
                // 执行getMethod  
            	httpClient = new HttpClient();
            	//HttpParams params = new BasicHttpParams(); 
            	
//            	getMethod = new GetMethod("http://www.wankoo.net/Angular/GetValidateCodeImg?time=new%20Date().getTime()");
            	String url = "http://www.jsgsj.gov.cn:58888/province/rand_img.jsp?type=7&temp=Mon%20Jul%2004%202016%2016:02:22%20GMT+0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4";
            	getMethod = new GetMethod(url);
//            	PostMethod postMethod = new PostMethod(url);
//            	getMethod = new GetMethod("http://credit.customs.gov.cn/ccppCopAction/createImage.action?0.8450086414813995");
//            	getMethod.getParams().setParameter("http.protocol.allow-circular-redirects", true);
//            	
//            	getMethod.addRequestHeader("Cookie", "JSESSIONID=YP9I1iziuA7ykOnFxmh3wfxmxEcE-_IvMmqNyGOM0p8V2m3zc-5_!1643854134; _pk_ref.1.c90b=%5B%22%22%2C%22%22%2C1465806910%2C%22http%3A%2F%2Fgsxt.saic.gov.cn%2F%22%5D; _pk_id.1.c90b=244388766a0e2ca1.1464655769.3.1465807609.1465801225.; _pk_ses.1.c90b=*");
//            	getMethod.addRequestHeader("Host", "tjcredit.gov.cn");
//            	getMethod.addRequestHeader("Referer", "http://tjcredit.gov.cn/platform/saic/index.ftl");
//            	getMethod.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
            	
            	
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=09tLKYNZ8AVo.iLdGQKhVyddKuDrNoEVcg0OA__PtQzX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=k92L6yg56wZT2YPj4dZ1UOshgmcyw__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=klxIayLN2e1aqYZgadVuD7ach4xiA__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=k92Ka8RcuZib29LPzbBuP5YxLEh6Q__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=k5xKKdJvr0ISjp3GFQ0BsOMIYorLQ__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=k53KK10Mwzv-serhhmWrPbo2kmn2Q__PtQyW");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=k5wKq9csSjUMIFOym5qHdl18hTNdQ__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=k92K6kpm-g--ShQaWr9ZG-hdtXu2w__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=khwKKcPHEmnw3QtxlqUcp0XnIycdA__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=khxKKhqZrcpDwYI1SZPQfQISXf0Sw__PtQyX");
            	
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=kdxLqq0cczlo45DxqWyYbC-DNiJBg__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=kZxIapoynaS-gqhWw5gdTn2q52kTg__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=kdxIapxawqtiqyVGt1tLYqrZcL70Q__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=U53K6ZyQSalZc-cioPH907zqFC7PtQ3X");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=EZwKav45bCnXHZCwRNUb7TccKJaFQ__PtQyW");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=UdxL68Y.kNKEHaOigzPgg5Y49bf5A__PtQyV");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=k5wLa.sX9iK5q6QFOi7wGxXVWg4bg__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=UhyKKvx13yvO.Y.SQ.lZpSnokqiPA__PtQyV");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=kZxIau9B48neHi2XsMbLvKdvlQpKA__PtQyX");
//            	getMethod = new GetMethod("http://www.ganji.com/tel_img/?c=09tLK4lajQ8n0TKXGJS8CATTt.3DRA_PtQzX");
            	
            	
            	//http://www.ganji.com/tel_img/?c=09tLKYNZ8AVo.iLdGQKhVyddKuDrNoEVcg0OA__PtQzX
            	//http://www.ganji.com/tel_img/?c=khxKKhqZrcpDwYI1SZPQfQISXf0Sw__PtQyX
            	//http://www.ganji.com/tel_img/?c=EZwK69KNsEqOzfQqxnvVKotzzoBGw__PtQyW
            	//http://www.ganji.com/tel_img/?c=U55K64s5bauxPQ4Pax8yxj6VJRFPtQ3X
            	
            	//http://www.ganji.com/tel_img/?c=k94Kqre5ZnQvG7wPpfzLYxqC8LcvQ__PtQyW
            	
            	//http://www.ganji.com/tel_img/?c=kl2LagK2o5jyWkSO7TkZANSn4Vixw__PtQyX
            	
            	//http://www.ganji.com/tel_img/?c=EZyKKftSY6Yn3pd8TlrlDQNqMgztw__PtQyW
            	
            	//http://www.ganji.com/tel_img/?c=UlyKKcZmx9FJ.X8R5g83QcokQc.Fw__PtQyV
            	
            	//http://www.ganji.com/tel_img/?c=kdxLqluo4mGq6h6.9cSxwa3hK1c4A__PtQyX
            	
            	//http://www.ganji.com/tel_img/?c=EhxIaep8rvNh2t5euW2.AXRhBGc6g__PtQyW
            	
            	int statusCode = httpClient.executeMethod(getMethod);  
                if (statusCode != HttpStatus.SC_OK) {  
                    System.err.println("Method failed: "  
                            + getMethod.getStatusLine());  
                }  
//                if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY ||
//                	statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
//                		    // 从头中取出转向的地址
//                	Header locationHeader = postMethod.getResponseHeader("location");
//                	String location = null;
//                	if (locationHeader != null) {
//                		location = locationHeader.getValue();
//                		System.out.println("The page was redirected to:" + location);
//                	} else {
//                	System.err.println("Location field value is null.");
//                	}
//                }
                // 读取内容  
                String picName = "C:\\Users\\Administrator.2R8T6XWGBLH0J1L\\Desktop\\江苏\\" + i + ".jpg";  
                InputStream inputStream = getMethod.getResponseBodyAsStream();  
                OutputStream outStream = new FileOutputStream(picName);  
                IOUtils.copy(inputStream, outStream);  
                
                outStream.close(); 
                getMethod.releaseConnection();
                System.out.println(i + " OK!");  
            } catch (Exception e) {  
                e.printStackTrace();  
            }
            
        }
    }  
   
    public static void deleteAll(File folder){
	    File[] files = folder.listFiles();  
	    for(File file : files){ 
	    	file.delete();      
	    }  
    }
    
    
//    public static void test(String testFolder, String trainingDataFolder){
////    	String trainingDataPath = "E:\\changhong\\workspace\\Test\\training_data\\";
////    	String to_be_recognize_folder = "to_be_test";
////    	ImgProcess1 imgPro = new ImgProcess1();
////    	imgPro.loadTrainData(trainingDataPath);
//    	File dir = new File(testFolder);
//    	File [] files = dir.listFiles();
//    	for(File img : files){
//    		String imgPath = img.getPath();
//    		String number = numberRecognition(imgPath, trainingDataFolder);
//    		System.out.println(img.getName() + ':' + number);
//    	}
//    }
//    
//    public static void test_singleFile() throws Exception{
//    	String imgPath = "E:\\changhong\\workspace\\numberRecognize\\data\\httpzhixing.court.gov.cnsearch\\temp\\0.jpg";
//    	FileInputStream fileInput=new FileInputStream(imgPath);
//   		BufferedImage imgBuff=ImageIO.read(fileInput);
//   	
//	   	//图片过滤
//	   	MyImgFilter imgFilter = new MyImgFilter(imgBuff);
//	   	imgFilter.changeGrey();
//	   	imgFilter.getGrey();
//	//  imgFilter.getBrighten();
//	   	imgBuff=imgFilter.getProcessedImg();
//	   	//输出过滤后图片(覆盖原图)
//	   	String pname=imgPath.substring(0,imgPath.lastIndexOf("."));
//	   	File fileTest = new File(pname+"_grey.jpg");
//	   	ImageIO.write(imgBuff, "jpg", fileTest);
//	   	
//	   	
//	   	List<BufferedImage> listImg = splitImage_first(imgBuff);
//	   	//输出分割文件
//	       for(int i = 0; i < listImg.size(); i++){
//	       	File file = new File("data\\httpzhixing.court.gov.cnsearch\\temp\\" + "0_temp" + "_" + i + ".jpg");
//	       	System.out.println(file.getPath());
//	       	ImageIO.write(listImg.get(i), "jpg", file);
//	       }
//    }
    
    /** 
     * @param args 
     * @throws Exception 
     */  
    public static void main(String[] args) throws Exception{
//    	String trainingDataPath = "data\\httpzhixing.court.gov.cnsearch\\training_data\\";
//    	String testFolder = "data\\httpzhixing.court.gov.cnsearch\\test\\";
//    	long b = System.currentTimeMillis();
//    	test(testFolder,trainingDataPath);
//    	long e = System.currentTimeMillis();
//    	System.out.println((e - b)/1000 + "seconds");
//    	
    	
    	/**
    	 * 下载图片
    	 */
    	downloadImage();
    	
    	/**
    	 * 产生样本
    	 */
//    	File dir = new File("data\\httpcredit.customs.gov.cn\\raw_sample");  
//        File[] files = dir.listFiles();  
//        int index = 0;
//        deleteAll(new File("data\\httpcredit.customs.gov.cn\\segment\\"));
//        for (File newFile : files) {  
//            String imgPath = newFile.getPath();
//            FileInputStream fileInput=new FileInputStream(imgPath);
//         	BufferedImage imgBuff=ImageIO.read(fileInput);
//         	
//         	//图片过滤
//         	MyImgFilter imgFilter = new MyImgFilter(imgBuff);
//         	imgFilter.changeGrey();
//         	imgFilter.getMedian();
//         	imgFilter.getGrey();
////         	imgFilter.getBrighten();
//         	imgBuff=imgFilter.getProcessedImg();
//         	//输出过滤后图片
//         	String pname=imgPath.substring(0,imgPath.lastIndexOf("."));
//         	File fileTest = new File(pname+"_grey.jpg");
//         	ImageIO.write(imgBuff, "jpg", fileTest);
//         	
//         	//分割图片
////         	List<BufferedImage> listImg = splitImageByFixedWidth(imgBuff);
////         	List<BufferedImage> listImg = splitImage(imgBuff);
//         	List<BufferedImage> listImg = splitImage_first(imgBuff);
//         	//输出分割文件
//             for(int i = 0; i < listImg.size(); i++){
//             	File file = new File("data\\httpcredit.customs.gov.cn\\segment\\" + ("a"+index) + "_" + i + ".jpg");
//             	System.out.println(file.getPath());
//             	ImageIO.write(listImg.get(i), "jpg", file);
//             }
//             index++;
//        }
    	
    	/**
    	 * 测试文件夹内所有图片
    	 */
//    	File dir = new File("result2");  
//      File[] files = dir.listFiles(); 
//      int index = 0;
//      for(File file : files){
//    	  
//    	  String imgPath = file.getPath();
//        FileInputStream fileInput=new FileInputStream(imgPath);
//    	BufferedImage imgBuff=ImageIO.read(fileInput);
//      	
//      	//图片过滤
//      	MyImgFilter imgFilter = new MyImgFilter(imgBuff);
//      	imgFilter.changeGrey();
////      	imgFilter.getMedian();
//      	imgFilter.getGrey();
////      	imgFilter.getBrighten();
//      	imgBuff=imgFilter.getProcessedImg();
//      	//输出过滤后图片(覆盖原图)
////      	String pname=imgPath.substring(0,imgPath.lastIndexOf("."));
////      	File fileTest = new File(pname+".jpg");
////      	ImageIO.write(imgBuff, "jpg", fileTest);
//      	
//      	//分割图片
//      	List<BufferedImage> listImg = splitImage_test(imgBuff);
//      	//输出分割文件
////          for(int i = 0; i < listImg.size(); i++){
////          	File newFile = new File(i+".jpg");
////          	System.out.println(newFile.getPath());
////          	ImageIO.write(listImg.get(i), "jpg", newFile);
////          }
//          
//          //识别
//          Map<BufferedImage, String> map = loadTrainData(training_data);  
//          String result = "";  
//          for (BufferedImage ib : listImg) {  
//              result += getSingleCharOcr(ib, map);  
//          } 
////          //删除图片
////          //fileTest.delete();
////          
//          System.out.println(file.getName() + ": " + result);
//          index++;
//          //**********************	  
//      }
      
      /**
       * ********************保留*************************
       */
//    	//图片路径
//    	String imgPath = "C:\\Users\\XXS\\workspace\\Test\\93592 - 副本 (2).jpg";
//    	FileInputStream fileInput=new FileInputStream(imgPath);
//    	BufferedImage imgBuff=ImageIO.read(fileInput);
//    	
//    	//图片过滤
//    	MyImgFilter imgFilter = new MyImgFilter(imgBuff);
//    	//二值过滤
//    	imgFilter.changeGrey();
//    	//中值过滤
////    	imgFilter.getMedian();
//    	imgFilter.getGrey();
//    	imgFilter.getBrighten();
//    	imgBuff=imgFilter.getProcessedImg();
//    	//输出过滤后图片(覆盖原图)
////    	String pname=imgPath.substring(0,imgPath.lastIndexOf("."));
////    	File fileTest = new File(pname+".jpg");
////    	ImageIO.write(imgBuff, "jpg", fileTest);
//    	
//    	//分割图片
//    	List<BufferedImage> listImg = splitImageByFixedWidth(imgBuff);
//    	//输出分割文件
//        for(int i = 0; i < listImg.size(); i++){
//        	File file = new File(i+".jpg");
//        	System.out.println(file.getPath());
//        	ImageIO.write(listImg.get(i), "jpg", file);
//        }
//        //识别
//        Map<BufferedImage, String> map = loadTrainData();  
//        String result = "";  
//        for (BufferedImage ib : listImg) {  
//            result += getSingleCharOcr(ib, map);  
//        } 
//        //删除图片
//        //fileTest.delete();
//        
//        System.out.println(result);
      //**********************保留*************************
    }  
} 
