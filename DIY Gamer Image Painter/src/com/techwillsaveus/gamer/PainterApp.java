/**
 * @author George Profenza for TWSU
 **/

package com.techwillsaveus.gamer;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import processing.core.PApplet;
import processing.core.PImage;


@SuppressWarnings("serial")
public class PainterApp extends PApplet implements ComponentListener{

	private static final String TOKEN_GAMER_IMAGE_BA = "byte image[8] = {";
	private static final String TOKEN_LOOP = "void loop()";
	private static final String TOKEN_SETUP = "void setup()";
	private static final String TOKEN_INCLUDE = "#include";
	int s = 60;
	int[][] canvas = new int[8][8];
	int offx = 80;
	int offy = 135;
	int w = 8;
	int h = 8;

	int mx = 200;//menu x offset
	int my = 25;//menu y offset
	int bw = 32;//button width
	int bh = 32;//button height
	int pad = 4;//menu item padding

	PImage bg;
	PImage[] menu = new PImage[14];
	String[] menuLabels = {"Upload","Insert Code","Copy Code","Clear","Paint","Erase","Show/Hide Values"};
	boolean eraseMode = false,showValues = false;
	String message="";
	
	private PainterListener callback;
	private JOptionPane optionPane;
	private JDialog dialog;
	private Object[] dialogOptions = { "Replace existing code", "Insert into existing code","Cancel" };
	
	private final String CODE_INCLUDE_HEADER = "#include \"Gamer.h\"\n";
	private final String CODE_DECLARE_GAMER  = "Gamer gamer;\n";
	private final String CODE_SETUP_BEGIN 	 = "void setup(){\n";
	private final String CODE_GAMER_INIT  	 = "\tgamer.begin();\n";
	private final String CODE_SETUP_END 	 = "}\n";
	private final String CODE_LOOP_BEGIN 	 = "void loop(){\n";
	private final String CODE_DRAW_IMAGE 	 = "\tgamer.printImage(image);\n";
	private final String CODE_LOOP_END 		 = "}\n";
	
	public PainterApp() {
		final JPanel panel = new JPanel();
		JPanel overwritePanel = new JPanel(new BorderLayout());
		overwritePanel.add(new JLabel("Found existing code. What shall we do about this ?"),BorderLayout.PAGE_START);
	    optionPane = new JOptionPane(overwritePanel,JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
	    optionPane.setOptions(dialogOptions);
        dialog = optionPane.createDialog(panel, "Found existing code");
        dialog.setModal(false);
        dialog.addComponentListener(this);
	}
	public void setListener(PainterListener listener){
		callback = listener;
	}

	public void setup(){
	  bg = loadImage("Gamer_painter_screen_background.png");
	  menu[0] = loadImage("upload_s1.png");
	  menu[1] = loadImage("upload_s2.png");
	  
	  menu[2] = loadImage("insert_s1.png");
	  menu[3] = loadImage("insert_s2.png");
	  
	  menu[4] = loadImage("copy_s1.png");
	  menu[5] = loadImage("copy_s2.png");
	  
	  menu[6] = loadImage("painter_x_s1.png");
	  menu[7] = loadImage("painter_x_s2.png");
	  
	  menu[8] = loadImage("painter_paint_s1.png");
	  menu[9] = loadImage("painter_paint_s2.png");
	  
	  menu[10]  = loadImage("painter_erase_s1.png");
	  menu[11] = loadImage("painter_erase_s2.png");
	  
	  menu[12]  = loadImage("show_values_s1.png");
	  menu[13] = loadImage("show_values_s2.png");
	  
	  size(bg.width,bg.height);
	  textFont(createFont("Arial Bold", 12));
	}
	public void draw(){
	  background(255);
	  image(bg,0,0);
	  for(int i = 0 ; i < menuLabels.length; i++) image(menu[i*2],mx+((bw+pad)*i),my);
	  if(eraseMode) image(menu[11],mx+((bw+pad)*5),my);
	  else          image(menu[9],mx+((bw+pad)*4),my);
	  if(showValues)image(menu[13],mx+((bw+pad)*6),my);
	  int menuIndex = isOverMenu();
	  if(menuIndex >= 0){
	    message = menuLabels[menuIndex];
	    image(menu[menuIndex*2+1],mx+((bw+pad)*menuIndex),my);
	    if(eraseMode) image(menu[10],mx+((bw+pad)*5),my);
	    else          image(menu[8],mx+((bw+pad)*4),my);
	    if(showValues)image(menu[12],mx+((bw+pad)*6),my);
	  }else message = "";
	  if(mousePressed && ((mouseX >= offx && mouseX <= offx + (w*s))
	                  &&  (mouseY >= offy && mouseY <= offy + (h*s)) )) {
	     int dx = (mouseX-offx)/s;
	     int dy = (mouseY-offy)/s;  
	     if(dx < w && dy < h) canvas[dx][dy] = ((keyPressed && keyCode == SHIFT) || eraseMode ) ? 0 : 1;
	  }
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
	  fill(0);
	  text(message,mouseX,mouseY-20);
	}
	void clear(){
	  for(int y = 0; y < h; y++) for(int x = 0; x < w; x++) canvas[x][y] = 0;
	}
	void invert(){
	  for(int y = 0; y < h; y++) for(int x = 0; x < w; x++) canvas[x][y] = 1-canvas[x][y];
	}
	public void keyReleased(){
	  if(key == 'e') eraseMode = !eraseMode;
	  if(key == 'c') copyToClipboard();
	  if(key == 'C') insertCode();
	  if(key == 'u') upload();
	  if(key == ' ') clear();
	  if(key == 'i') invert();
	  if(key == 'V') showValues = !showValues;
	}
	public void mouseReleased(){
	  int menuIndex = isOverMenu();
	  if(menuIndex == 0) upload();
	  if(menuIndex == 1) insertCode();
	  if(menuIndex == 2) copyToClipboard();
	  if(menuIndex == 3) clear();
	  if(menuIndex == 4) eraseMode = false;
	  if(menuIndex == 5) eraseMode = true;
	  if(menuIndex == 6) showValues = !showValues;
	}
	String getCode(){
	  String out = "";
	  String indent = getSpaces(17); 
	  for(int y = 0; y < h; y++){
	    String line = "B";
	    for(int x = 0; x < w; x++) line += canvas[x][y];
	    if(y == 0) out += line+",\n";
	    else       out += indent+line+(y < (h-1) ? ",\n" : "");
	  }
	  out += "};";
	  return out;
	}
	public String[] getCode(String var){
	  String[] out = new String[w];
	  out[0] = "byte "+var+"[8] = {";
	  String indent = getSpaces(out[0].length()); 
	  for(int y = 0; y < h; y++){
	    String line = "B";
	    for(int x = 0; x < w; x++) line += canvas[x][y];
	    if(y == 0) out[y] += line+",";
	    else       out[y] = indent+line+(y < (h-1) ? "," : "");
	  }
	  out[7] += "};\n";
	  return out;
	}
	String getCode(boolean overwrite){
		String code = "";
		if(overwrite){
			code += CODE_INCLUDE_HEADER;
			code += CODE_DECLARE_GAMER;
			code += join(getCode("image"),"\n");
			code += CODE_SETUP_BEGIN;
			code += CODE_GAMER_INIT;
			code += CODE_SETUP_END;
			code += CODE_LOOP_BEGIN;
			code += CODE_DRAW_IMAGE;
			code += CODE_LOOP_END;
		}else{
			code = Painter.getSketchCode();
			
			boolean hasGamerInclude = code.contains(CODE_INCLUDE_HEADER);
			if(!hasGamerInclude){
				boolean hasHeaders = code.contains(TOKEN_INCLUDE);
				if(hasHeaders){
					int lastHeaderStart = code.lastIndexOf(TOKEN_INCLUDE);
					int lastHeaderEnd = code.lastIndexOf("\n", lastHeaderStart);
					String before = code.substring(0, lastHeaderEnd);
					String after  = code.substring(lastHeaderStart, code.length());
					code = before + "\n" + CODE_INCLUDE_HEADER + after;
				}else code = CODE_INCLUDE_HEADER + code;
			}
			
			boolean hasSetup = code.contains(TOKEN_SETUP);
			if(!hasSetup) {
				code += CODE_SETUP_BEGIN+CODE_SETUP_END;
				hasSetup = true;
			}
			if(hasSetup){
				int setupIndex = code.indexOf(TOKEN_SETUP);
				String before = code.substring(0, setupIndex);
				String after  = code.substring(setupIndex, code.length());
				String codeToAdd = "";
				boolean hasGamerDeclared = code.contains(CODE_DECLARE_GAMER);
				if(!hasGamerDeclared) codeToAdd += CODE_DECLARE_GAMER+"\n";
				boolean hasGamerImage = code.contains(TOKEN_GAMER_IMAGE_BA);
				if(!hasGamerImage) codeToAdd += join(getCode("image"),"\n") + "\n";
				code = before + codeToAdd + after;
				if(hasGamerImage){//only update byte array
					String updatedCode = getCode();
					int start = code.indexOf(TOKEN_GAMER_IMAGE_BA)+TOKEN_GAMER_IMAGE_BA.length();
					String pre  = code.substring(0, start);
					String post = code.substring(code.indexOf('}', start)+2, code.length());
					code = pre + updatedCode + post;
				}
			}
			//else code = CODE_DECLARE_GAMER + join(getCode("image"),"\n")+"\n"+ code;
			
			boolean hasGameInit = code.contains(CODE_GAMER_INIT);
			if(hasSetup && !hasGameInit){
				int setupBlockNewLine = code.indexOf("\n",code.indexOf(TOKEN_SETUP)+TOKEN_SETUP.length()+1);//code.lastIndexOf("}", code.indexOf(TOKEN_SETUP)+1);
				String before = code.substring(0, setupBlockNewLine);
				String after  = code.substring(setupBlockNewLine, code.length());
				code = before + "\n" + CODE_GAMER_INIT +"\n"+ after;
			}
			boolean hasLoop = code.contains(TOKEN_LOOP);
			if(!hasLoop) {
				code += CODE_LOOP_BEGIN + CODE_LOOP_END;
				hasLoop = true;
			}
			boolean hasGamerDraw = code.contains(CODE_DRAW_IMAGE);
			if(hasLoop && !hasGamerDraw){
				int drawBlockNewLine = code.indexOf("\n",code.indexOf(TOKEN_LOOP)+TOKEN_LOOP.length()+1);//code.lastIndexOf("}", code.indexOf(TOKEN_SETUP)+1);
				String before = code.substring(0, drawBlockNewLine);
				String after  = code.substring(drawBlockNewLine, code.length());
				code = before + "\n" + CODE_DRAW_IMAGE +"\n"+ after;
			}
		}
		return code;
	}

	String getSpaces(int size){
	  String s = "";
	  for(int i = 0; i < size; i++) s += " ";
	  return s;
	}
	void upload(){
		if(callback != null) callback.onUploadCode(getCode(true));
	}
	void copyToClipboard(){
		if(callback != null) callback.onCopyCode();
	}
	void insertCode(){
		String code = Painter.getSketchCode();
		if(code.length() > 0) dialog.setVisible(true);
		else if(callback != null) callback.onPublishCode(getCode(true));
	}
	int isOverMenu(){
	  int index = -1;
	  for(int i = 0 ; i < menuLabels.length; i++){
	    if((mouseX > mx+((bw+pad)*i) && mouseX < mx+((bw+pad)*(i+1)))&&
	       (mouseY > my && mouseY < my+bh)){
	         index = i;
	         break;
	       }
	  }
	  return index;
	}
	public void componentHidden(ComponentEvent e) {
		Object value = optionPane.getValue();
		if(value.equals(dialogOptions[2])) return;
		if(callback != null) callback.onPublishCode(getCode(value.equals(dialogOptions[0])));
	}
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	
}
