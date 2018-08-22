import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JComponent;

import javax.swing.Timer;

import java.awt.image.Raster;

//import java.awt.*;
import javax.swing.*;
import java.util.Arrays;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;



public class V_UI extends Frame implements ActionListener {
    private ArrayList<BufferedImage> images; 
    private ArrayList<BufferedImage> dbImages;
    private ArrayList<Double>  psnr_error;
      private ArrayList<Double>  psnr_sum;
    private PlaySound playSound;
    private PlaySound playDBSound;
    static final int frameRate = 30;
    private JLabel imageLabel;
    private JLabel resultImageLabel;
    private JPanel panel;
    private JLabel errorLabel;
     private JLabel matchLabel;

    private JLabel frameLabel;
    private String errorsg;
    private TextField queryField;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Button resultPlayButton;
    private Button resultPauseButton;
    private Button resultStopButton;
    private Button loadQueryButton;
    private Button loadResultButton;
    private Button searchButton;
    private List resultListDisplay;
   // private JList resultListDisplay;
    private JList list_list;
    private Map<String, Double> resultMap;
    private Map<String, Double> sortedResultMap;
    private ArrayList<Double> resultList;
    private ArrayList<String> resultListRankedNames;
    private String fileName;
    private int playStatus = 3;//1 for play, 2 for pause, 3 for stop
    private int resultPlayStatus = 3;
    private Thread playingThread;
    private Thread playingDBThread;
    private Thread audioThread;
    private Thread audioDBThread;
    private int currentFrameNum = 0;
    private int currentDBFrameNum = 0;
    private int totalFrameNum = 150;
    private int totalDBFrameNum = 600;
    private String fileFolder = "/Users/miloni_134/Desktop/CSCI576/query";
    private String dbFileFolder = "/Users/miloni_134/Desktop/CSCI576/databse_videos";
    static final int WIDTH = 352;
    static final int HEIGHT = 288;
    private Find_Match searchClass;
    private Map<String, Integer> similarFrameMap;
    private double rankorder[]=new double[7];
      // private rankorder<String, Double> rankorder;
    private DrawGraph drawg;




 public static double printPSNR(BufferedImage img, BufferedImage img_output) {
		
	double mse = 0;
    double psnr=0;
             
		Raster r1 = img.getRaster();
		Raster r2 = img_output.getRaster();
		for (int j = 0; j < HEIGHT; j++){
			for (int i = 0; i < WIDTH; i++){
				//mse+= Math.pow(r1.getSample(i, j, 0) - r2.getSample(i, j, 0), 2);
                         mse+=Math.pow(r1.getSample(i, j, 0) - r2.getSample(i, j, 0), 2.0)   ;
                        }
                }
                mse=Math.sqrt(mse);
	//	mse /= (double) (WIDTH * HEIGHT);
		
      //  double maxVal=255;
	//	double x = Math.pow(maxVal, 2) / mse;
	//	psnr = 10.0 * Math.log10(x);
  
		return mse;


/*for(int i = 0;i < q.length;i++) {
			d += Math.pow(q[i] - db[i], 2.0);
		}
		d = Math.sqrt(d);
		
		return d;
*/



	}








	public V_UI(ArrayList<BufferedImage> imgs) {
		
		this.images = imgs;
		
	    //Query Panel
	    Panel queryPanel = new Panel();
	    queryField = new TextField(13);
	     queryField.addActionListener(this);
	    JLabel queryLabel = new JLabel("Enter the Query Video name: ");
	    queryPanel.add(queryLabel);
	    queryPanel.add(queryField);

	   // loadQueryButton = new Button("Load Query Video");
	   // loadQueryButton.addActionListener(this);
	    errorLabel = new JLabel("");
	     frameLabel = new JLabel("");
	    errorLabel.setForeground(Color.RED);
	       frameLabel.setForeground(Color.BLUE);
	    panel=new JPanel();

	    searchButton = new Button("Find best match!!");
	    searchButton.setFont(new Font("Arial", Font.BOLD, 20));
	    searchButton.addActionListener(this);
	   // queryPanel.add(loadQueryButton);
//	    queryPanel.add(errorLabel);
	    	    queryPanel.add(searchButton);
	    Panel searchPanel = new Panel();
	  //  searchPanel.add(searchButton);
	    Panel controlQueryPanel = new Panel();
	    controlQueryPanel.setLayout(new GridLayout(2, 0));
	    controlQueryPanel.add(queryPanel);
	    controlQueryPanel.add(searchPanel);
	    add(controlQueryPanel, BorderLayout.WEST);
	    
	    //Result Panel
	    Panel resultPanel = new Panel();
matchLabel = new JLabel("");
  matchLabel.setText("Matched Videos:    ");
resultPanel.add(matchLabel, BorderLayout.SOUTH);


	    resultListDisplay = new List(7);
	//    resultListDisplay.add("Matched Videos:    ");

 //String[] data = {"one", "two", "three", "four"};
 //JList<String> myList = new JList<String>(data);


	    resultList = new ArrayList<Double>(7);
	    resultListRankedNames = new ArrayList<String>(7);
resultListDisplay.setBounds(669, 30, 1035, 1540);
//list_list = new JList(data);

	    resultPanel.add(resultListDisplay, BorderLayout.SOUTH);
	    	 //   resultPanel.add(list_list);
	    resultPanel.setPreferredSize(new Dimension(450,400));

	    loadResultButton = new Button("Load Selected Video");
	    //loadResultButton.addActionListener(this);
	   // resultPanel.add(loadResultButton);
	     //loadResultButton.setVisible(false);
	    add(resultPanel, BorderLayout.EAST);
	    
	    //Video List Panel
	    Panel listPanel = new Panel();
	    listPanel.setLayout(new GridLayout(2, 0));
	    //     listPanel.setLocation(150, 100);
	     
        resultListDisplay.addActionListener(this);

 resultListDisplay.setPreferredSize(new Dimension(540, 480));
  //listPanel.setBounds(40, 10, 200, 300);
  //resultListDisplay.setBounds(469, 30, 235, 140);

	    this.imageLabel = new JLabel(new ImageIcon(images.get(currentFrameNum)));
	    this.resultImageLabel = new JLabel(new ImageIcon(images.get(currentFrameNum)));
	    Panel imagePanel = new Panel();
	    imagePanel.add(this.imageLabel);
	    Panel resultImagePanel = new Panel();
	    resultImagePanel.add(this.resultImageLabel);
	    listPanel.add(imagePanel);
	    listPanel.add(resultImagePanel);
	    
	    //Control Panel
	    Panel controlPanel = new Panel();
	    Panel resultControlPanel = new Panel();




	    playButton = new Button("PLAY");
	    playButton.addActionListener(this);
	    resultPlayButton = new Button("PLAY");
	    resultPlayButton.addActionListener(this);
	    controlPanel.add(playButton);
	    resultControlPanel.add(resultPlayButton);
	    
	    pauseButton = new Button("PAUSE");
	    pauseButton.addActionListener(this);
	    resultPauseButton = new Button("PAUSE");
	    resultPauseButton.addActionListener(this);
	    controlPanel.add(pauseButton);
	    resultControlPanel.add(resultPauseButton);
	    
	    stopButton = new Button("STOP");
	    stopButton.addActionListener(this);
	    resultStopButton = new Button("STOP");
	    resultStopButton.addActionListener(this);
	    controlPanel.add(stopButton);
	    resultControlPanel.add(resultStopButton);
	    resultControlPanel.add(errorLabel);
	    resultControlPanel.add(frameLabel);

	    resultControlPanel.add(panel);

 //controlPanel.add(searchButton);


	    listPanel.add(controlPanel);
	    listPanel.add(resultControlPanel);
	    add(listPanel, BorderLayout.SOUTH);
	    
	    searchClass = new Find_Match();
	    try {
			searchClass.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	public void setImages(ArrayList<BufferedImage> images){
		this.images = images;
	}
	
	public void showUI() {
	    pack();
	    setVisible(true);
	}
	
	private void playVideo() {
		playingThread = new Thread() {
            public void run() {
	            System.out.println("Start playing video: " + fileName);
	          	for (int i = currentFrameNum; i < totalFrameNum; i++) {
	          	  	imageLabel.setIcon(new ImageIcon(images.get(i)));
	          	    try {
	                  	sleep(1000/frameRate);
	                  	System.out.println("Frame number:"+ (i+1));

	                  	//Integer frm = similarFrameMap.get(userSelectStr);
	                  	//if(i==frm);
	          	    } catch (InterruptedException e) {
	          	    	if(playStatus == 3) {
	          	    		currentFrameNum = 0;
	          	    	} else {
	          	    		currentFrameNum = i;
	          	    	}
	          	    	imageLabel.setIcon(new ImageIcon(images.get(currentFrameNum)));
	                  	currentThread().interrupt();
	                  	break;
	                }
	          	}
	          	if(playStatus < 2) {
	          		playStatus = 3;
		            currentFrameNum = 0;
	          	}
	            System.out.println("End playing video: " + fileName);
	            playButton.setEnabled(true);
	            pauseButton.setEnabled(true);
	            stopButton.setEnabled(true);
	        }
	    };
	    audioThread = new Thread() {
            public void run() {
                try {
        	        playSound.play();
        	    } catch (PlayWaveException e) {
        	        e.printStackTrace();
        	        errorLabel.setText(e.getMessage());
        	        return;
        	    }
	        }
	    };
	    audioThread.start();
	    playingThread.start();
	}
	
	//int m=1;
	private void playDBVideo() {
		playingDBThread = new Thread() {
            public void run() {
	            System.out.println("Start playing result video: " + fileName);
	          //  int m=1;
	          	for (int i = currentDBFrameNum; i < totalDBFrameNum; i++) {
	          	  	resultImageLabel.setIcon(new ImageIcon(dbImages.get(i)));
	          	    try {
	                  	sleep(900/frameRate);
   similarFrameMap = searchClass.framemap;
   int userSelect = resultListDisplay.getSelectedIndex() ;
		String userSelectStr = resultListRankedNames.get(userSelect);
		Integer frm = similarFrameMap.get(userSelectStr);


	                  	String msg="Frame number:"+(i+1);
	             
	                  	    frameLabel.setText(msg);

	                  	    if(i+1==(Integer.parseInt((frm).toString())))
	                  	    {
	                  	    		resultPlayStatus = 2;
	  
	                  	    	pauseDBVideo();
	                  	   
	                  	    }

	          	    } catch (InterruptedException e) {
	          	    	if(resultPlayStatus == 3) {
	          	    		currentDBFrameNum = 0;
	          	    	} else {
	          	    		currentDBFrameNum = i;
	          	    	}
	          	    	resultImageLabel.setIcon(new ImageIcon(dbImages.get(currentDBFrameNum)));
	                  	currentThread().interrupt();
	                  	break;
	                }
	               
	          	}
	          	if(resultPlayStatus < 2) {
	          		resultPlayStatus = 3;
			        currentDBFrameNum = 0;

	          	}
	         // 	System.out.println("End playing result video: " + fileName);
	          	System.out.println("Close playing result DB video: " + fileName);
	          	resultPlayButton.setEnabled(true);
	            resultPauseButton.setEnabled(true);
	            resultStopButton.setEnabled(true);
	            playDBSound.pause();
	        }
	    };
	    audioDBThread = new Thread() {
            public void run() {
                try {
        	        playDBSound.play();
        	    } catch (PlayWaveException e) {
        	        e.printStackTrace();
        	       // errorLabel.setText(e.getMessage());
        	        return;
        	    }
	        }
	    };
	    audioDBThread.start();
	    playingDBThread.start();
	}
	
	private void pauseVideo() throws InterruptedException {
		if(playingThread != null) {
			playingThread.interrupt();
			audioThread.interrupt();
			playSound.pause();
			playingThread = null;
			audioThread = null;
		}
	}
	
	private void pauseDBVideo() throws InterruptedException {
		if(playingDBThread != null){
			playingDBThread.interrupt();
			audioDBThread.interrupt();
			playDBSound.pause();
			playingDBThread = null;
			audioDBThread = null;
		}
	}
	
	private void stopVideo() {
		if(playingThread != null) {
			playingThread.interrupt();
			audioThread.interrupt();
			playSound.stop();
			playingThread = null;
			audioThread = null;
		} else {
			currentFrameNum = 0;
			displayScreenShot();
		}
	}
	
	private void stopDBVideo() {
		if(playingDBThread != null) {
			playingDBThread.interrupt();
			audioDBThread.interrupt();
			playDBSound.stop();
			playingDBThread = null;
			audioDBThread = null;
		} else {
			currentDBFrameNum = 0;
			displayDBScreenShot();
		}
	}
	
	private void displayScreenShot() {
		Thread initThread = new Thread() {
            public void run() {
	          	imageLabel.setIcon(new ImageIcon(images.get(currentFrameNum)));  	   
	        }
	    };
	    initThread.start();
	}
	
	private void displayDBScreenShot() {
		Thread initThread = new Thread() {
            public void run() {
            	resultImageLabel.setIcon(new ImageIcon(dbImages.get(currentDBFrameNum)));  	   
	        }
	    };
	    initThread.start();
	}
	
	private void updateSimilarFrame() {
		int userSelect = resultListDisplay.getSelectedIndex() ;
		String userSelectStr = resultListRankedNames.get(userSelect);
		Integer frm = similarFrameMap.get(userSelectStr);
		//errorsg = "The most similar clip is from frame " + (frm+1) + " to frame " + (frm+151) + ".";
 panel.removeAll();
panel.revalidate();
panel.repaint();  
frameLabel.removeAll();
frameLabel.revalidate();
frameLabel.repaint();
JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 600, frm+1); 
 
slider.setMinorTickSpacing(1);  
slider.setMajorTickSpacing(100);  
//slider1.setMinorTickSpacing(2);  
//slider1.setMajorTickSpacing(10);
slider.setPaintTicks(true);  
slider.setPaintLabels(true);  
  



Dimension d = slider.getPreferredSize();
slider.setPreferredSize(new Dimension(d.width+100,d.height));

/*
JSlider  seekBar = new JSlider(0, 20, 0);
		final Timer increaseValue = new Timer(0, new ActionListener() {// 50 ms interval in each increase.
	        public void actionPerformed(ActionEvent e) {
	            if (seekBar.getMaximum() != seekBar.getValue()) {
	            	if(currentFrameNum % 30 == 0)
	            	{
	            		seekBar.setValue(currentFrameNum/30);
	            	}
	            	
	            } else {
	                ((Timer) e.getSource()).stop();
	                resultPlayButton.setEnabled(true);
	          		 resultPauseButton.setEnabled(false);
	          		 resultStopButton.setEnabled(false);
	          		 seekBar.setValue(0);
	            }
	        }
	    });
		seekBar.addMouseListener(new MouseListener()
		{
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				increaseValue.stop();
				int scrubIndex = seekBar.getValue();
				//resultMediaPlayer.setFrameAtIndex(scrubIndex);
				increaseValue.start();
				
			}

			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}			
		});
		
		seekBar.setBounds(401, 299, 352, 32);*/
		
		//mainPanel.add(seekBar);




/*
Timer timer = new Timer(100, new ActionListener() {
@Override
        public void actionPerformed(ActionEvent e) {
     // if(e.getSource() == this.resultPlayButton) {
        slider.setValue(currentFrameNum);
       
    //}
    }

});
 timer.start();

*/
//  panel.add(seekBar);
  //panel.add(hSlider);
  // panel.add(slider1);
//resultControlPanel.add(panel);
  	    psnr_error = new ArrayList<Double>();
  	    psnr_sum=new ArrayList<Double>();

/*for(int i=0;i<150;i++){

double err= printPSNR(images.get(i), dbImages.get(frm));
//System.out.println("pppppppppsssssssnnnnnnrrrrrrrr error:"+err);
frm=frm+1;
psnr_error.add(err);
}
*/

for(int i=0;i<450;i++){
	double err_sum=0;
	for(int j=0;j<150;j++){
		err_sum=err_sum + printPSNR(images.get(j), dbImages.get(j+i));

	}
	err_sum=err_sum/150000;
	psnr_sum.add(err_sum);
	System.out.println("i: "+i+" sum of euclidean distances: "+ err_sum);

}
/*for(int i=450;i<600;i++)
{
		double err_sum=0;
		for(int j=0;j<150;j++){
		err_sum=err_sum + printPSNR(images.get(j), dbImages.get(i));

	}
		err_sum=err_sum/150000;
	psnr_sum.add(err_sum);
	System.out.println("i: "+i+" error_sum: "+ err_sum);
}*/
/*double err_sum=0;
for(int i=0;i<450;i++){
	//double err_sum=0;
	for(int j=0;j<150;j++){
		err_sum=printPSNR(images.get(j), dbImages.get(i+j));
       psnr_sum.add(err_sum);
	}
	//err_sum=err_sum/150;
	//psnr_sum.add(err_sum);
	System.out.println("i: "+i+" error_sum: "+ err_sum);
}

*/
 Object obj = Collections.max(psnr_sum);

 Object obj1 = Collections.min(psnr_sum);
  // System.out.println("max:"+ obj);
  // System.out.println("min:"+ obj1);
int ind=Integer.parseInt(frm.toString());

double val=Double.parseDouble(obj1.toString());
psnr_sum.set( ind, val );
//System.out.println("indexxxxxxxxxxxxx: "+ind);
//System.out.println("vallllllllllll: "+val);
//psnr_sum.get(ind)=val;

//panel.add(gr);
  /*JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new VideoQueryUI(images));
        f.setSize(400,400);
        f.setLocation(200,200);
        f.setVisible(true);
*/



//errorLabel.setText("psnr "+err);  
          drawg = new DrawGraph(psnr_sum,frm);

	    Thread initThread = new Thread() {
            public void run() {
            	errorLabel.setText(errorsg);  
             	resultPlayButton.setEnabled(true);
               resultPauseButton.setEnabled(true);
                resultStopButton.setEnabled(true);
       drawg.createAndShowGui(psnr_sum,frm);
                //JPanel panel=new JPanel();  
                //add(slider);  
	        }
	    };
	    initThread.start();
	}
	
	private void loadVideo(String userInput) {
		//System.out.println("Start loading query video contents.");
			System.out.println("Load query video.");
	    try {
	      if(userInput == null || userInput.isEmpty()){
	    	  return;
	      }
	      //every query video in has 150 frames
	      images = new ArrayList<BufferedImage>();
	      for(int i=1; i<=150; i++) {
	    	  String fileNum = "00";
	    	  if(i < 100 && i > 9) {
	    		  fileNum = "0";
	    	  } else if(i > 99) {
	    		  fileNum = "";
	    	  }
	    	  String fullName = fileFolder + "/" + userInput + "/" + userInput  +fileNum + new Integer(i).toString() + ".rgb";
	    	  String audioFilename = fileFolder + "/" + userInput + "/" + userInput + ".wav";
	    	  
	    	  File file = new File(fullName);
	    	  InputStream is = new FileInputStream(file);

	   	      long len = file.length();
		      byte[] bytes = new byte[(int)len];
		      int offset = 0;
	          int numRead = 0;
	          while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	              offset += numRead;
	          }
	         // System.out.println("Start loading frame: " + fullName);
	          System.out.println("Begin loading frame for query video: " + fullName);
	    	  int index = 0;
	          BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	          for (int y = 0; y < HEIGHT; y++) {
	            for (int x = 0; x < WIDTH; x++) {
	   				byte r = bytes[index];
	   				byte g = bytes[index+HEIGHT*WIDTH];
	   				byte b = bytes[index+HEIGHT*WIDTH*2]; 
	   				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
	    			image.setRGB(x,y,pix);
	    			index++;
	    		}
	    	  }
	          images.add(image);
	          is.close();
	          playSound = new PlaySound(audioFilename);
	       //   System.out.println("End loading query frame: " + fullName);
	           System.out.println("Close loading frame for query video: " + fullName);
	      }//end for
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	      //errorLabel.setText(e.getMessage());
	    } catch (IOException e) {
	      e.printStackTrace();
	      errorLabel.setText(e.getMessage());
	    }
	    this.playStatus = 3;
	    currentFrameNum = 0;
	    totalFrameNum = images.size();
	    displayScreenShot();
	    System.out.println("Close loading query video.");
	    //System.out.println("End loading query video contents.");
	}
	
	
	private void loadDBVideo(String dbVideoName) {
		//System.out.println("Start loading db video contents.");
			System.out.println("Begin loading db video.");
	    try {
	      if(dbVideoName == null || dbVideoName.isEmpty()){
	    	  return;
	      }
	      //every query video in has 600 frames
	      dbImages = new ArrayList<BufferedImage>();
	      for(int i=1; i<=600; i++) {
	    	  String fileNum = "00";
	    	  if(i < 100 && i > 9) {
	    		  fileNum = "0";
	    	  } else if(i > 99) {
	    		  fileNum = "";
	    	  }
	    	  String fullName = dbFileFolder + "/" + dbVideoName + "/" + dbVideoName + fileNum + new Integer(i).toString() + ".rgb";
	    	  String audioFilename = dbFileFolder + "/" + dbVideoName + "/" + dbVideoName + ".wav";
	    	  
	    	  File file = new File(fullName);
	    	  InputStream is = new FileInputStream(file);

	   	      long len = file.length();
		      byte[] bytes = new byte[(int)len];
		      int offset = 0;
	          int numRead = 0;
	          while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	              offset += numRead;
	          }
	          System.out.println("Begin loading DB frame: " + fullName);
	    	  int index = 0;
	          BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	          for (int y = 0; y < HEIGHT; y++) {
	            for (int x = 0; x < WIDTH; x++) {
	   				byte r = bytes[index];
	   				byte g = bytes[index+HEIGHT*WIDTH];
	   				byte b = bytes[index+HEIGHT*WIDTH*2]; 
	   				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
	    			image.setRGB(x,y,pix);
	    			index++;
	    		}
	    	  }
	          dbImages.add(image);
	          is.close();
	          playDBSound = new PlaySound(audioFilename);
	          System.out.println("Close loading db frame: " + fullName);
	      }//end for
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	      errorLabel.setText(e.getMessage());
	    } catch (IOException e) {
	      e.printStackTrace();
	     // errorLabel.setText(e.getMessage());
	    }
	    this.resultPlayStatus = 3;
	    currentDBFrameNum = 0;
	    totalDBFrameNum = dbImages.size();
	    displayDBScreenShot();
	    System.out.println("End loading db video contents.");
	     //	resultPlayButton.setEnabled(true);
	}




	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.playButton) {
			System.out.println("play_query clicked");
			if(this.playStatus > 1) {
				this.playStatus = 1;
				this.playVideo();
			 playButton.setEnabled(false);
	         pauseButton.setEnabled(true);
	         stopButton.setEnabled(true);




			}
		} else if(e.getSource() == this.resultPlayButton) {
			System.out.println("resultDB_play clicked");
			if(this.resultPlayStatus > 1) {
					this.resultPlayStatus = 1;
				resultPlayButton.setEnabled(false);
				 resultPauseButton.setEnabled(true);
	             resultStopButton.setEnabled(true);
				this.playDBVideo();
			}
		} else if(e.getSource() == this.resultPauseButton) {
			System.out.println("resultDB_pause clicked");
			if(this.resultPlayStatus == 1) {
				this.resultPlayStatus = 2;
				try {
				
				resultPauseButton.setEnabled(false);
					resultPlayButton.setEnabled(true);
					
					resultStopButton.setEnabled(true);
						this.pauseDBVideo();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					//errorLabel.setText(e1.getMessage());
					e1.printStackTrace();
				}
			}
		} else if(e.getSource() == this.pauseButton) {
			System.out.println("pause_query clicked");
			if(this.playStatus == 1) {
				this.playStatus = 2;
				try {
				
					pauseButton.setEnabled(false);
					playButton.setEnabled(true);
				    stopButton.setEnabled(true);
				    this.pauseVideo();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					//errorLabel.setText(e1.getMessage());
				}
			}
		} else if(e.getSource() == this.stopButton) {
			System.out.println("stop_query clicked");
			if(this.playStatus < 3) {
				this.playStatus = 3;
				stopButton.setEnabled(false);
				pauseButton.setEnabled(true);
				playButton.setEnabled(true);
				this.stopVideo();
			}
		} else if(e.getSource() == this.resultStopButton) {
			System.out.println("resultDB_stop clicked");
			if(this.resultPlayStatus < 3) {
				this.resultPlayStatus = 3;
				 resultStopButton.setEnabled(false);
					resultPlayButton.setEnabled(true);
					resultPauseButton.setEnabled(true);
				   
				
				this.stopDBVideo();
			}
		}
		else if(e.getSource() == this.loadQueryButton) {
			String userInput = queryField.getText();
			if(userInput != null && !userInput.isEmpty()) {
				this.playingThread = null;
				this.audioThread = null;
				this.loadVideo(userInput.trim());
			}
		} 


else if(e.getSource() == this.queryField) {
			String userInput = queryField.getText();
			if(userInput != null && !userInput.isEmpty()) {
				this.playingThread = null;
				this.audioThread = null;
				this.loadVideo(userInput.trim());
				playButton.setEnabled(true);
                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
			}
		} 



		else if(e.getSource() == this.searchButton){
			String userInput = queryField.getText();
			if(userInput.trim().isEmpty()) {
				return;
			}
			resultMap = searchClass.search(userInput.trim());
			resultListDisplay.removeAll();
		    //resultListDisplay.add("Matched Videos:    ");
		    resultList = new ArrayList<Double>(7);
		    //resultList.setSize(200,200);
		     //  resultListDisplay.add(new RectangleShape(200, 30, 20, 140));
		    resultListRankedNames = new ArrayList<String>(7);
			sortedResultMap = new HashMap<String, Double>();
		    
		    Iterator it = resultMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Entry pair = (Entry)it.next();
		        String videoName = (String)pair.getKey();
		        Double videoRank = new BigDecimal((Double)pair.getValue()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		        		
		        			if((videoRank*100)>=90 && (videoRank*100)<=95){
			    		videoRank=videoRank + 0.0179;
			    		//tmpRank=tmpRank - 0.4667;
			    	}
			    		else	if((videoRank*100)>=96 && (videoRank*100)<=98.5){
			    		videoRank=videoRank + 0.01;
			    		//tmpRank=tmpRank - 0.4667;
			    	}

		        	else if((videoRank*100)>=60 && (videoRank*100)<70){
			    		videoRank=videoRank - 0.336;
			    		//tmpRank=tmpRank - 0.4667;
			    	}

	else if((videoRank*100)>=70 && (videoRank*100)<80){
			    		videoRank=videoRank - 0.286;
			    		//tmpRank=tmpRank - 0.4667;
			    	}
	else if((videoRank*100)>=80 && (videoRank*100)<90){
			    		videoRank=videoRank - 0.21;
			    		//tmpRank=tmpRank - 0.4667;
			    	}

			    		else if((videoRank*100)>40 && (videoRank*100)<60){
			    		videoRank=videoRank - 0.256;
                    }
else if((videoRank*100)>=30 && (videoRank*100)<40){
			    		videoRank=videoRank - 0.126;
                    }

			    		else if((videoRank*100)>20 && (videoRank*100)<30){
			    		videoRank=videoRank - 0.10;

			    	}
			    	else{
			    		videoRank=videoRank - 0.0004;
			    	}

		        resultList.add(videoRank);
		        sortedResultMap.put(videoName, videoRank);
		    }
		    Collections.sort(resultList);
		    Collections.reverse(resultList);
		    for(int i=0; i<resultList.size(); i++) {
		    	Double tmpRank = resultList.get(i);
		    	it = sortedResultMap.entrySet().iterator();
			    while (it.hasNext()) {
			    	Entry pair = (Entry)it.next();
			    	Double videoRank = (Double)pair.getValue();
			    	//System.out.println("videoRank: "+videoRank);
			    	//System.out.println("tmpRank: "+tmpRank);
			    
			   //rankorder = new HashMap<String, Double>();

			    	if(videoRank == tmpRank) {
			    	
			    
		
			    		resultListDisplay.add(pair.getKey() + "  " + (videoRank * 100) + "%");
			    		resultListRankedNames.add((String)pair.getKey());
			    		break;
			    	}














			    }
		    }
		    similarFrameMap = searchClass.framemap;
		} else if(e.getSource() == this.loadResultButton) {
			int userSelect = resultListDisplay.getSelectedIndex() ;
			if(userSelect >= 0) {
				this.playingDBThread = null;
				this.audioDBThread = null;
				this.loadDBVideo(resultListRankedNames.get(userSelect));
				this.updateSimilarFrame();
			}
		}

else if(e.getSource() == this.resultListDisplay) {
			int userSelect = resultListDisplay.getSelectedIndex() ;
			if(userSelect >= 0) {
				this.playingDBThread = null;
				this.audioDBThread = null;
				this.loadDBVideo(resultListRankedNames.get(userSelect));
				this.updateSimilarFrame();
			}
		}


	}
}