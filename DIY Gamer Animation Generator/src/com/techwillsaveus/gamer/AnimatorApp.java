/**
 * @author George Profenza for TWSU
 **/
package com.techwillsaveus.gamer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import processing.core.PApplet;
import processing.core.PImage;

@SuppressWarnings("serial")
public class AnimatorApp extends PApplet implements ActionListener {

	private static final String LOAD_ANIMATION = "Load Animation";
	private static final String SAVE_ANIMATION = "Save Animation";
	int currentFrame = 0;
	int totalFrames = 1;
	int s = 60;//size of each box representing a DIY Gamer pixel
	int offx = 124;//offset on X axis for the grid of pixels
	int offy = 160;//offset on Y axis for the grid of pixels
	int w = 8;//grid width
	int h = 8;//grid height
	int[][] canvas,buffer;//current canvas values and buffer(used for copy/paste frames)
	boolean autoUpdate;//is the timeline playing back frames ?
	ArrayList<int[][]> frames = new ArrayList<int[][]>();//the timeline
	boolean[] keys = new boolean[526];//keypresses, used for key combinations

	boolean eraseMode,showValues;
	PImage bg;
	PImage[] menu = new PImage[30];
	int mx = 85;//menu x offset
	int my = 25;//menu y offset
	int ms = 32;//menu item size
	int pad = 4;//menu item padding
	String[] menuLabels = {"Upload","Copy Code","Clear All Frames","Previous Frame","Next Frame","Add Blank Frame","Duplicate Current Frame","Remove Frame","Paint Mode","Erase Mode","Toggle Playback",SAVE_ANIMATION,LOAD_ANIMATION,"Show/Hide Values"};
	String message = "";//menu hover message
	final int GREEN = 0xFFA6CE91;

	int now,delay = 200;//200ms delay, same as Gamer library Animation example
	Slider animDelay = new Slider("delay",offx,109,243,33,10,1000,200);
	
	private AnimatorListener callback;//callback to notify the Tool
	
	private final String CODE_INCLUDE_HEADER = "#include \"Gamer.h\"\n";
	private final String CODE_DECLARE_GAMER  = "Gamer gamer;\n";
	private final String CODE_SETUP_BEGIN 	 = "void setup(){\n";
	private final String CODE_GAMER_INIT  	 = "\tgamer.begin();\n";
	private final String CODE_SETUP_END 	 = "}\n";
	private final String CODE_LOOP_BEGIN 	 = "void loop(){\n";
	private final String CODE_DRAW_IMAGE 	 = "\tfor(int i = 0; i < NUMFRAMES; i++){\n\t\tgamer.printImage(frames[i]);\n\t\tdelay($DELAY);\n\t}";
	private final String CODE_LOOP_END 		 = "}\n";
	
	private final JFileChooser chooser = new JFileChooser();
	private JDialog dialog;
	
	public AnimatorApp(){
        dialog = new JDialog();
        dialog.add(chooser,BorderLayout.CENTER);
        dialog.setSize(550, 400);
        dialog.setModal(false);
        dialog.setIconImage(new ImageIcon(loadBytes("twsu.png")).getImage());
        chooser.setApproveButtonText("Select");
        chooser.addActionListener(this);
	}
	public void setListener(AnimatorListener listener){
		callback = listener;
	}
	
	public void setup(){
	  frames.add(new int[8][8]);
	  canvas = frames.get(0);
	   
	  bg       = loadImage("Gamer_animator_background.png");
	  menu[0 ] = loadImage("animator_upload_s1.png");
	  menu[1 ] = loadImage("animator_upload_s2.png");
	  menu[2 ] = loadImage("animator_copy_s1.png");
	  menu[3 ] = loadImage("animator_copy_s2.png");
	  menu[4 ] = loadImage("x_s1.png");
	  menu[5 ] = loadImage("x_s2.png");
	  menu[6 ] = loadImage("left_s1.png");
	  menu[7 ] = loadImage("left_s2.png");
	  menu[8 ] = loadImage("right_s1.png");
	  menu[9 ] = loadImage("right_s2.png");
	  menu[10] = loadImage("+_s1.png");
	  menu[11] = loadImage("+_s2.png");
	  menu[12] = loadImage("duplicate_s1.png");
	  menu[13] = loadImage("duplicate_s2.png");
	  menu[14] = loadImage("-_s1.png");
	  menu[15] = loadImage("-_s2.png");
	  menu[16] = loadImage("paint_s1.png");
	  menu[17] = loadImage("paint_s2.png");
	  menu[18] = loadImage("erase_s1.png");
	  menu[19] = loadImage("erase_s2.png");
	  menu[20] = loadImage("play_s1.png");
	  menu[21] = loadImage("play_s2.png");
	  menu[22] = loadImage("save_s1.png");
	  menu[23] = loadImage("save_s2.png");
	  menu[24] = loadImage("load_s1.png");
	  menu[25] = loadImage("load_s2.png");
	  menu[26] = loadImage("animator_show_values_s2.png");
	  menu[27] = loadImage("animator_show_values_s1.png");//hmmm...have I seen this glasses before at TWSU ?
	  
	  menu[28] = loadImage("pause_s1.png");
	  menu[29] = loadImage("pause_s2.png");
	  
	  animDelay.bg = GREEN;
	  
	  size(bg.width,bg.height);
	  textFont(loadFont("Arial-BoldMT-12.vlw"),12);
	  now = millis();

	}
	
	public void draw(){
		  drawMenu();
		  //playback -> update frames
		  if (autoUpdate && totalFrames > 1) {
		    if(millis() - now >= delay){
		      currentFrame = ((currentFrame+1)%(totalFrames));
		      canvas = frames.get(currentFrame);
		      now = millis();
		    }
		  }
		  //draw -> update current frame pixels based on mouse coordinates
		  if(mousePressed && ((mouseX >= offx && mouseX <= offx + (w*s))
		                  &&  (mouseY >= offy && mouseY <= offy + (h*s)) )) {
		    
		     int dx = (mouseX-offx)/s;
		     int dy = (mouseY-offy)/s;  
		     if(dx < w && dy < h) canvas[dx][dy] = ((keyPressed && keyCode == SHIFT) || eraseMode ) ? 0 : 1;
		  }
		  //draw current frame
		  for(int y = 0; y < h; y++){
		    for(int x = 0; x < w; x++){
		      fill(canvas[x][y] == 1 ? color(255) : color(255,0)); 
		      stroke(0);
		      strokeWeight(4);
		      rect(x*s+offx,y*s+offy,s,s);
		      if(showValues){
		    	  fill(canvas[x][y] == 1 ? color(0) : color(255));
	    		  textSize(24);
	    		  text(""+canvas[x][y],x*s+offx+s*.5f-textWidth(""+canvas[x][y])*.5f,y*s+offy+s*.65f);
	    		  textSize(12);
	    		  text("["+x+"]["+y+"]",x*s+offx+4,y*s+offy+s*.25f);
	    	  }
		    }
		  }
		  drawOverlays(); 
		}
		void drawMenu(){
		  image(bg,0,0);
		  for(int i = 0 ; i < menuLabels.length; i++){
		    image(menu[i*2],mx+((ms+pad)*i),my);
		  }
		  if(eraseMode)  image(menu[19],mx+((ms+pad)*9),my);
		  else           image(menu[17],mx+((ms+pad)*8),my); 
		  if(autoUpdate) image(menu[28],mx+((ms+pad)*10),my);
		  if(showValues) image(menu[27],mx+((ms+pad)*13),my);
		  int menuIndex = isOverMenu();
		  if(menuIndex >= 0){
		    message = menuLabels[menuIndex];
		    image(menu[menuIndex*2+1],mx+((ms+pad)*menuIndex),my);
		    if(eraseMode) image(menu[18],mx+((ms+pad)*9),my);
		    else          image(menu[16],mx+((ms+pad)*8),my);
		    if(autoUpdate) image(menu[29],mx+((ms+pad)*10),my);
		    if(showValues) image(menu[26],mx+((ms+pad)*13),my);
		  }else message = "";  
		  animDelay.update(mouseX,mouseY,mousePressed);
		  animDelay.draw();
		  delay = (int)animDelay.value;
		}
		void drawOverlays(){
		  pushStyle();//draw frame number
		    noStroke();
		    String cf = (currentFrame+1) + " of " + totalFrames;
		    rectMode(CORNER);
		    fill(GREEN);
		    rect(540,109,80,33);
		    fill(0);
		    text(cf,560,130);
		  popStyle();
		  fill(0);//draw tool tip around menu
		  text(message,mouseX,mouseY-20);
		}
		public void keyPressed(){
		  keys[keyCode] = true;
		  if (keyCode == LEFT && currentFrame > 0)  			currentFrame--;
		  if (keyCode == RIGHT && currentFrame < totalFrames-1) currentFrame++;
		  if(keyCode == LEFT || keyCode == RIGHT) 				canvas = frames.get(currentFrame);
		  if (checkKey(ALT) && checkKey(KeyEvent.VK_C)) copyFrame();
		  if (checkKey(ALT) && checkKey(KeyEvent.VK_V)) pasteFrame();
		}
		public void keyReleased(){
		  keys[keyCode] = false;
		  if(key == 'C') copyToClipboard();
		  if(key == BACKSPACE) clearFrame();
		  if(key == 'i') invertFrame();
		  if(key == '=') addFrame();
		  if(key == '+') cloneFrame();
		  if(key == '-') removeFrame();
		  if(key == 'x') clear();
		  if(key == ' ') autoUpdate = !autoUpdate;
		  if(key == 'e') eraseMode = !eraseMode;
		  if(key == 's') saveAnimation();
		  if(key == 'l') loadAnimation();
		  if(key == 'V') showValues = !showValues;
		  if(key == 'F') insertManyBlankFrames();
		}
		public void mouseReleased(){
		  int menuIndex = isOverMenu();
		  if(menuIndex == 0) upload();
		  if(menuIndex == 1) copyToClipboard();
		  if(menuIndex == 2) clear();
		  if(menuIndex == 3  && currentFrame > 0) 			  currentFrame--;
		  if(menuIndex == 4  && currentFrame < totalFrames-1) currentFrame++;
		  if(menuIndex == 3 || menuIndex == 4) canvas = frames.get(currentFrame);
		  if(menuIndex == 5) addFrame();
		  if(menuIndex == 6) cloneFrame();
		  if(menuIndex == 7) removeFrame();
		  if(menuIndex == 8) eraseMode = false;
		  if(menuIndex == 9) eraseMode = true;
		  if(menuIndex == 10) autoUpdate = !autoUpdate;
		  if(menuIndex == 11) saveAnimation();
		  if(menuIndex == 12) loadAnimation();
		  if(menuIndex == 13) showValues = ! showValues;
		}
		boolean checkKey(int k) {
		  if (keys.length >= k) return keys[k];  
		  return false;
		}
		void clearFrame(){
		  for(int y = 0; y < h; y++) for(int x = 0; x < w; x++) canvas[x][y] = 0;
		}
		void invertFrame(){
		  for(int y = 0; y < h; y++) for(int x = 0; x < w; x++) canvas[x][y] = 1-canvas[x][y];
		}
		void addFrame(){
		  frames.add(new int[8][8]);
		  currentFrame++;
		  canvas = frames.get(currentFrame); 
		  totalFrames = frames.size();
		}
		void cloneFrame() {
		  int[][] clone = new int[8][8];
		  for(int y = 0; y < 8; y++) for(int x = 0; x < 8; x++) clone[x][y] = canvas[x][y];
		  frames.add(currentFrame,clone);
		  currentFrame++;
		  canvas = frames.get(currentFrame);
		  totalFrames = frames.size();
		}
		void removeFrame(){
		  if(totalFrames > 1){
		    noLoop();
		    frames.remove(currentFrame);
		    currentFrame--;
		    if(currentFrame < 0) currentFrame = 0;
		    canvas = frames.get(currentFrame);
		    totalFrames = frames.size();
		    loop();
		  }
		}
		void copyFrame() {
		  buffer = new int[8][8];
		  for(int y = 0; y < 8; y++) for(int x = 0; x < 8; x++) buffer[x][y] = canvas[x][y];
		}
		void pasteFrame() {
		  if(buffer != null)
		    for(int y = 0; y < 8; y++) for(int x = 0; x < 8; x++) canvas[x][y] = buffer[x][y];
		}
		void insertManyBlankFrames(){
			for(int i = 0; i < 60; i++) addFrame();
		}
		public String getCode(String name){
		  String out = "#define NUMFRAMES"+name.toUpperCase()+" "+totalFrames+"\nbyte "+name+"[NUMFRAMES"+name.toUpperCase()+"][8] = {";
		  for(int f = 0; f < totalFrames; f++){
		    out += "\n\t\t{";
		    int[][] cf = frames.get(f);
		    for(int y = 0; y < 8; y++){
		      String line = (y == 0 ? "" : "\t\t")+"B";
		      for(int x = 0; x < 8; x++) line += cf[x][y];
		      out += line;
		      if(y < 7) out += ",\n";
		    }
		    if(f < totalFrames-1) out += "},\n";
		  }
		  out += "}};\n";
		  return out;
		}
		public String getCode(){
			  String out = "#define NUMFRAMES "+totalFrames+"\nbyte frames[NUMFRAMES][8] = {";
			  for(int f = 0; f < totalFrames; f++){
			    out += "\n\t\t{";
			    int[][] cf = frames.get(f);
			    for(int y = 0; y < 8; y++){
			      String line = (y == 0 ? "" : "\t\t")+"B";
			      for(int x = 0; x < 8; x++) line += cf[x][y];
			      out += line;
			      if(y < 7) out += ",\n";
			    }
			    if(f < totalFrames-1) out += "},\n";
			  }
			  out += "}};\n";
			  return out;
			}
		
		void clear(){
		  noLoop();
		  frames.clear();
		  frames.add(new int[8][8]);
		  canvas = frames.get(0);
		  currentFrame = 0;
		  totalFrames = frames.size();
		  loop();
		}
		void copyToClipboard(){
			if(callback != null) callback.onCopyCode();
		}
		int isOverMenu(){
		  int index = -1;
		  for(int i = 0 ; i < menuLabels.length; i++){
		    if((mouseX > mx+((ms+pad)*i) && mouseX < mx+((ms+pad)*(i+1)))&&
		       (mouseY > my && mouseY < my+ms)){
		         index = i;
		         break;
		       }
		  }
		  return index;
		}
		String getSketchCode(){
			String code = "";
			code += CODE_INCLUDE_HEADER;
			code += CODE_DECLARE_GAMER;
			code += getCode();
			code += CODE_SETUP_BEGIN;
			code += CODE_GAMER_INIT;
			code += CODE_SETUP_END;
			code += CODE_LOOP_BEGIN;
			code += CODE_DRAW_IMAGE;
			code += CODE_LOOP_END;
			code = code.replace("$DELAY", ""+delay);
			return code;
		}
		void upload(){
			if(callback != null) callback.onUploadCode(getSketchCode());
		}
		void saveAnimation(){
			dialog.setTitle(SAVE_ANIMATION);
			dialog.setVisible(true);
		}
		void loadAnimation(){
			dialog.setTitle(LOAD_ANIMATION);
			dialog.setVisible(true);
		}
		void saveAnimationCSV(){
			String csv = "";
		    int npx = w * h;
		    for(int[][] f : frames){
		      for(int i = 0 ; i < npx ; i++){
		        int x = i%w;
		        int y = i/w;
		        csv += f[x][y];
		        if(i < npx-1) csv += ",";
		      }
		      csv += "\n";
		    }
			saveStrings(chooser.getSelectedFile().getAbsolutePath(), csv.split("\n"));
			System.out.println(chooser.getSelectedFile().getName()+" animation saved");
		}
		void loadAnimationCSV(){
			noLoop();
			try{
				String[] csv = loadStrings(chooser.getSelectedFile().getAbsoluteFile());
				if(csv != null){
				      try{
				        frames.clear();
				        for(int i = 0 ; i < csv.length; i++){
				          int[][] f = new int[w][h];
				          String[] px = csv[i].split(",");
				          for(int j = 0; j < px.length; j++){
				            int x = j%w;
				            int y = j/w;
				            f[x][y] = Integer.parseInt(px[j]);
				          }
				          frames.add(f);
				        }
				        totalFrames = frames.size();
				        canvas = frames.get(0);
				      }catch(Exception e){
				        loadError();
				        return;
				      }
				    }else {
				    	loadError();
				    	return;
				    }
				System.out.println(chooser.getSelectedFile().getName()+" animation loaded");
			}catch(Exception ex){
				//ex.printStackTrace();
				//System.err.println("Could not load and parse "+chooser.getSelectedFile().getName());
				loadError();
				return;
			}
			loop();
		}
		void loadError(){
			System.err.println("Unfortunately there were errors loading your file!\nPlease check if the file exists and is formatted correctly");
			clear();
		}

	
	class Slider{
		  float w,h,x,y;//width, height and position
		  float min,max,value;//slider values: minimum, maximum and current
		  float cx,pw = 20;//current slider picker position, picker width
		  
		  int bg = color(0);//background colour
		  int fg = color(255);//foreground colour
		  
		  String label;
		  
		  Slider(String label,float x,float y,float w,float h,float min,float max,float value){
		    this.x = x;
		    this.y = y;
		    this.w = w;
		    this.h = h;
		    this.min = min;
		    this.max = max;
		    this.value = value;
		    this.label = label;
		    cx = PApplet.map(value,min,max,x,x+w);
		  }
		  void update(int mx,int my,boolean md){
		    if(md){
		      if((mx >= x && mx <= (x+w)) &&
		         (my >= y && my <= (y+h))){
		        cx = mx;
		        value = PApplet.map(cx,x,x+w,min,max);
		      }
		    }
		  }
		  void draw(){
		    pushStyle();
		    noStroke();
		    fill(bg);
		    rect(x,y,w,h);
		    fill(fg);
		    rect(cx-pw*.5f,y,pw,h);
//		    rect(x,y,cx-x,h);
		    fill(0);
		    text(label+": "+(int)value,x+pw,y+h*.75f);
		    popStyle();
		  }
		}


	public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        dialog.setVisible(false);
        if (command.equals(JFileChooser.APPROVE_SELECTION)) {
          if(dialog.getTitle().equals(SAVE_ANIMATION)) saveAnimationCSV();
          if(dialog.getTitle().equals(LOAD_ANIMATION)) loadAnimationCSV();
        }
	}
	
}

