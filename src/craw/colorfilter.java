package craw;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class colorfilter extends Frame{
    Image im, tmp;
    int i, iw, ih;
    int[] pixels;
    boolean flag_load = false;
    
    public colorfilter(){    
        this.setTitle("��ɫͼ���ֵ��ֵ�˲�3*3");
    	Panel pdown;
    	Button load, mean, median, save, quit; 
    	addWindowListener(new WindowAdapter(){
    		public void windowClosing(WindowEvent e){
    			System.exit(0);
    		}
    	});
    	
    	pdown = new Panel();
    	pdown.setBackground(Color.lightGray);
    	//���尴ť
    	load = new Button("װ��ͼ��");    
    	mean = new Button("��ֵ�˲�3*3");
    	median = new Button("��ֵ�˲�3*3");
    	save = new Button("����");
    	quit = new Button("�˳�");
    	
    	this.add(pdown, BorderLayout.SOUTH);
    	//��Ӱ�ť
    	pdown.add(load);    
    	pdown.add(mean);
    	pdown.add(median);
    	pdown.add(save);
    	pdown.add(quit);
        //��ť�Ķ�������  װ��ͼ��
    	load.addActionListener(new ActionListener(){    
    		public void actionPerformed(ActionEvent e){
    			try {
				jLoad_ActionPerformed(e);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
    	});
    	//��ť�Ķ�������  ��ֵ�˲�
    	mean.addActionListener(new ActionListener(){    
    		public void actionPerformed(ActionEvent e){
    			jMean_ActionPerformed(e);
    		}
    	});
    	//��ť�Ķ�������  ��ֵ�˲�
    	median.addActionListener(new ActionListener(){    
    		public void actionPerformed(ActionEvent e){
    			jMedian_ActionPerformed(e);
    		}
    	});
    	//��ť�Ķ�������  ����
    	save.addActionListener(new ActionListener(){    
    		public void actionPerformed(ActionEvent e){
    			try {
				jSave_ActionPerformed(e);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
    	});
    	//��ť�Ķ�������  �˳�
    	quit.addActionListener(new ActionListener(){    
    		public void actionPerformed(ActionEvent e){
    			jQuit_ActionPerformed(e);
    		}
    	});
    }
  //��ť������ʵ��  ����ͼ��
    public void jLoad_ActionPerformed(ActionEvent e) throws IOException{    
    	File inputFile = new File("C:\\Users\\Administrator.2R8T6XWGBLH0J1L\\Desktop\\JavaVerify-master\\download3\\5.jpg");
        BufferedImage input = ImageIO.read(inputFile);
        iw = input.getWidth(this);
        ih = input.getHeight(this);
	pixels = new int[iw*ih];
	im = input;
	tmp = input;
        flag_load = true;
        repaint();
	}
  //��ť������ʵ�� ��ֵ�˲�  
  	public void jMean_ActionPerformed(ActionEvent e) {    
        if(flag_load){
        	try{
  			      PixelGrabber pg = new PixelGrabber(im,0,0,iw,ih,pixels,0,iw);
  			      pg.grabPixels();
  		      }catch(InterruptedException e3){
  			    e3.printStackTrace();
  		      }
  		    BufferedImage grayImage = new BufferedImage(iw, ih, 
					  BufferedImage.TYPE_INT_RGB);
  		    
  		    ColorModel cm = ColorModel.getRGBdefault();
  		    
  		    for(int i=1;i<ih-1;i++){
  		    	for(int j=1;j<iw-1;j++){
  		    		
  		    	    int red1 = cm.getRed(pixels[(i-1)*iw+j-1]);
  		    	    int red2 = cm.getRed(pixels[(i-1)*iw+j]);
  		    	    int red3 = cm.getRed(pixels[(i-1)*iw+j+1]);
  		    	    int red4 = cm.getRed(pixels[i*iw+j-1]);
  		    	    int red6 = cm.getRed(pixels[i*iw+j+1]);
  		    	    int red7 = cm.getRed(pixels[(i+1)*iw+j-1]);
  		    	    int red8 = cm.getRed(pixels[(i+1)*iw+j]);
  		    	    int red9 = cm.getRed(pixels[(i+1)*iw+j+1]);
  		    	    int meanRed = (red1+red2+red3+red4+red6+red7+red8+red9)/8;
  		    	    
  		    	    int green1 = cm.getGreen(pixels[(i-1)*iw+j-1]);
		    	    int green2 = cm.getGreen(pixels[(i-1)*iw+j]);
		    	    int green3 = cm.getGreen(pixels[(i-1)*iw+j+1]);
		    	    int green4 = cm.getGreen(pixels[i*iw+j-1]);
		    	    int green6 = cm.getGreen(pixels[i*iw+j+1]);
		    	    int green7 = cm.getGreen(pixels[(i+1)*iw+j-1]);
		    	    int green8 = cm.getGreen(pixels[(i+1)*iw+j]);
		    	    int green9 = cm.getGreen(pixels[(i+1)*iw+j+1]);
		    	    int meanGreen = (green1+green2+green3+green4+green6+green7+green8+green9)/8;
				    
		    	   int blue1 = cm.getBlue(pixels[(i-1)*iw+j-1]);
  		    	    int blue2 = cm.getBlue(pixels[(i-1)*iw+j]);
  		    	    int blue3 = cm.getBlue(pixels[(i-1)*iw+j+1]);
  		    	    int blue4 = cm.getBlue(pixels[i*iw+j-1]);
  		    	    int blue6 = cm.getBlue(pixels[i*iw+j+1]);
  		    	    int blue7 = cm.getBlue(pixels[(i+1)*iw+j-1]);
  		    	    int blue8 = cm.getBlue(pixels[(i+1)*iw+j]);
  		    	    int blue9 = cm.getBlue(pixels[(i+1)*iw+j+1]);
  		    	    int meanBlue = (blue1+blue2+blue3+blue4+blue6+blue7+blue8+blue9)/8;
		    	    
  		    	    int rgb = 255<<24|meanRed<<16|meanGreen<<8|meanBlue; 
		    	    grayImage.setRGB(j, i, rgb);
  		    	}	
  		    }
  		    tmp = grayImage;
			repaint();
  		
  	    }else{
  		    JOptionPane.showMessageDialog(null, "�ȵ����װ��ͼ�񡱣�3Q��","��ʾ��",
  				    JOptionPane.WARNING_MESSAGE);
  		    }
  	}
  //��ť������ʵ�� ��ֵ�˲�  
  	public void jMedian_ActionPerformed(ActionEvent e) {    
        if(flag_load){
        	try{
  			      PixelGrabber pg = new PixelGrabber(im,0,0,iw,ih,pixels,0,iw);
  			      pg.grabPixels();
  		      }catch(InterruptedException e3){
  			    e3.printStackTrace();
  		      }
  		    BufferedImage grayImage = new BufferedImage(iw, ih, 
					  BufferedImage.TYPE_INT_RGB);
  		    
  		    ColorModel cm = ColorModel.getRGBdefault();
  		    
  		    int[] tpRed = new int[9];
  		    int[] tpGreen = new int[9];
  		    int[] tpBlue = new int[9];
  		    
  		    for(int i=1;i<ih-1;i++){
  		    	for(int j=1;j<iw-1;j++){
  		    		
  		    		tpRed[0] = cm.getRed(pixels[(i-1)*iw+j-1]);
  		    		tpRed[1] = cm.getRed(pixels[(i-1)*iw+j]);
  		    		tpRed[2] = cm.getRed(pixels[(i-1)*iw+j+1]);
  		    		tpRed[3] = cm.getRed(pixels[i*iw+j-1]);
  		    		tpRed[4] = cm.getRed(pixels[i*iw+j]);
  		    		tpRed[5] = cm.getRed(pixels[i*iw+j+1]);
  		    		tpRed[6] = cm.getRed(pixels[(i+1)*iw+j-1]);
  		    		tpRed[7] = cm.getRed(pixels[(i+1)*iw+j]);
  		    		tpRed[8] = cm.getRed(pixels[(i+1)*iw+j+1]);
  		    	        for(int rj=0; rj<8; rj++){
  		    	    	for(int ri=0; ri<8-rj; ri++){
  		    	    		if(tpRed[ri]>tpRed[ri+1]){
  		    	    			int Red_Temp = tpRed[ri];
  	  		    	    		tpRed[ri] = tpRed[ri+1];
  	  		    	    		tpRed[ri+1] = Red_Temp;
  		    	    		}
  		    	    	}
  		    	    }
  		    	    int medianRed = tpRed[4];
  		    	    
  		    	    tpGreen[0] = cm.getGreen(pixels[(i-1)*iw+j-1]);
  		    	    tpGreen[1] = cm.getGreen(pixels[(i-1)*iw+j]);
  		    	    tpGreen[2] = cm.getGreen(pixels[(i-1)*iw+j+1]);
  		    	    tpGreen[3] = cm.getGreen(pixels[i*iw+j-1]);
  		    	    tpGreen[4] = cm.getGreen(pixels[i*iw+j]);
  		    	    tpGreen[5] = cm.getGreen(pixels[i*iw+j+1]);
  		    	    tpGreen[6] = cm.getGreen(pixels[(i+1)*iw+j-1]);
  		    	    tpGreen[7] = cm.getGreen(pixels[(i+1)*iw+j]);
  		    	    tpGreen[8] = cm.getGreen(pixels[(i+1)*iw+j+1]);
  		    	    for(int rj=0; rj<8; rj++){
		    	    	for(int ri=0; ri<8-rj; ri++){
		    	    		if(tpGreen[ri]>tpGreen[ri+1]){
		    	    			int Green_Temp = tpGreen[ri];
			    	    		tpGreen[ri] = tpGreen[ri+1];
			    	    		tpGreen[ri+1] = Green_Temp;
		    	    		}
		    	    	}
		    	    }
		    	    int medianGreen = tpGreen[4];
				    
		    	    tpBlue[0] = cm.getBlue(pixels[(i-1)*iw+j-1]);
		    	    tpBlue[1] = cm.getBlue(pixels[(i-1)*iw+j]);
		    	    tpBlue[2] = cm.getBlue(pixels[(i-1)*iw+j+1]);
		    	    tpBlue[3] = cm.getBlue(pixels[i*iw+j-1]);
		    	    tpBlue[4] = cm.getBlue(pixels[i*iw+j]);
		    	    tpBlue[5] = cm.getBlue(pixels[i*iw+j+1]);
		    	    tpBlue[6] = cm.getBlue(pixels[(i+1)*iw+j-1]);
		    	    tpBlue[7] = cm.getBlue(pixels[(i+1)*iw+j]);
		    	    tpBlue[8] = cm.getBlue(pixels[(i+1)*iw+j+1]);
		    	    for(int rj=0; rj<8; rj++){
		    	    	for(int ri=0; ri<8-rj; ri++){
		    	    		if(tpBlue[ri]>tpBlue[ri+1]){
		    	    			int Blue_Temp = tpBlue[ri];
			    	    		tpBlue[ri] = tpBlue[ri+1];
			    	    		tpBlue[ri+1] = Blue_Temp;
		    	    		}
		    	    	}
		    	    }
		    	    int medianBlue = tpBlue[4];
		    	    
  		    	    int rgb = 255<<24|medianRed<<16|medianGreen<<8|medianBlue; 
		    	    grayImage.setRGB(j, i, rgb);
  		    	}	
  		    }
  		    tmp = grayImage;
			repaint();
  		
  	    }else{
  		    JOptionPane.showMessageDialog(null, "�ȵ����װ��ͼ�񡱣�3Q��","��ʾ��",
  				    JOptionPane.WARNING_MESSAGE);
  		    }
  	}
  	//��ť������ʵ��  save
  	public void jSave_ActionPerformed(ActionEvent e) throws IOException{    
        if(flag_load){
        	
    		BufferedImage bi = new BufferedImage(tmp.getWidth(null),tmp.getHeight(null), 
    				BufferedImage.TYPE_INT_RGB);
            Graphics g = bi.getGraphics();
            g.drawImage(tmp,0, 0,null);
            g.dispose();
    		
  		    File save_path=new File("E:\\f2\\sc\\save.jpg");
            ImageIO.write(bi, "JPG", save_path);

  	}else{
  		JOptionPane.showMessageDialog(null, "�ȵ����װ��ͼ�񡱣�3Q��","��ʾ��",
  				JOptionPane.WARNING_MESSAGE);
  		}
  	}
  	//��ť������ʵ��  �˳�
  	public void jQuit_ActionPerformed(ActionEvent e){
  		System.exit(0);
  	}
  //��ͼ����
  	public void paint(Graphics g){
  		if(flag_load){
  			g.drawImage(tmp,50,50,this);
  		}else{}
  	}
  	
	public static void main(String[] args) {
		colorfilter ti = new colorfilter();
		ti.setSize(1000,860);
		ti.setVisible(true);
	}
}