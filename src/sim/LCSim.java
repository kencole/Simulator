package sim;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.geom.*;

class LCSim extends JComponent implements MouseMotionListener, ActionListener, MouseListener{  //add charge
     private static final long serialVersionUID = 5996864279486733427L;
     public static LCSim lc;
     static JFrame frame;
     Timer t;
     Graph g1, g2;
     HorizontalSlider speedS, voltageS, capS, indS;
     TextSnippet speedT, voltageT, capT, indT, g1Label, timeLabel,
          g2Label, iDirection, energies, totalE, capE, indE, capLabel, indLabel,
          frequency, period, charge, current, dIdt, totalE2, capE2, indE2, kenCole,
          capVoltage, resetButtonText, indVoltage, timeConstant, magLabel, elecLabel;
     PlusMinusRedBlue p1, p2, p1a, p2a;
     DrawableAndClickable resetButton;
     FieldLines electricFieldLines;
     MagFieldLines magFieldLines;
     Circuit c1;
     BarChart b1, b2, b3;
     Arrow a1;
     int windowX, windowY;
     int mouseX, mouseY;
     final int BUFFER = 50;
     final double MAX_VOLTS_POSSIBLE = 24.0;
     final double MAX_CURRENT_POSSIBLE = MAX_VOLTS_POSSIBLE / Math.sqrt(0.4 / 24);
     final double MAX_U_POSSIBLE = .5 * MAX_VOLTS_POSSIBLE * 24 * 24;
     DrawableAndClickable grabbed;
     ArrayList<DrawableAndClickable> displayElements;
     double time = 0;
     double lastCurrent;
     double lastTime;
     public LCSim(){
          this(800,600);
     }
     public LCSim(int x, int y){
          windowX=x;
          windowY=y;
          t = new Timer(20, this);
          reset();
     }
     private void reset(){
          t.stop();
          c1 = new Circuit(50,40,300,300,Color.darkGray);
          speedS = new HorizontalSlider(20,550,300,5,Color.white);
          speedT = new TextSnippet(20,540,Color.white,"Simulation Speed");
          voltageS = new HorizontalSlider(20,500,300,5,Color.white);
          voltageT = new TextSnippet(20,490,Color.white,"Voltage:");
          indS = new HorizontalSlider(20,450,300,5,Color.white);
          indT = new TextSnippet(20,440,Color.white,"Inductance:");
          capS = new HorizontalSlider(20,400,300,5,Color.white);
          capT = new TextSnippet(20,390,Color.white,"Capacitance:");
          g1 = new Graph(390,450,350,120,Color.lightGray,Color.white,Color.red);
          g1Label = new TextSnippet(390,440,Color.white,"Capacitor Voltage v. time");
          g2 = new Graph(390,290,350,120, Color.lightGray, Color.white, Color.blue);
          g2Label = new TextSnippet(390,280,Color.white,"Circuit Current v. time");
          timeLabel = new TextSnippet(600,280,Color.white,"");
          iDirection = new TextSnippet(120,300,Color.white,"Direction of Current Flow");
          a1 = new Arrow(198,310,145,20,Color.blue);
          kenCole = new TextSnippet(668,593,Color.white,"Created by Ken Cole");
          capLabel = new TextSnippet(260,191,Color.white,"Capacitor");
          elecLabel = new TextSnippet(243,204, new Color(255,255,0), "Electric Field");
          indLabel = new TextSnippet(60,191,Color.white,"Inductor");
          magLabel = new TextSnippet(60,204, new Color(255,128,0), "Magnetic Field");
          frequency= new TextSnippet(390,241,Color.white,"Frequency");
          period = new TextSnippet(390,254,Color.white,"Period");
          charge = new TextSnippet(550,254,Color.white,"Charge");
          capVoltage = new TextSnippet(550,215,Color.white,"Capacitor Voltage:");
          indVoltage = new TextSnippet(550,228,Color.white,"Inductor EMF (V):");
          timeConstant = new TextSnippet(550, 241, Color.white, "LC Time Constant: ");
          current = new TextSnippet(390,215,Color.white,"Current(I): ");
          dIdt = new TextSnippet(390,228,Color.white,"dI/dt");
          totalE2 = new TextSnippet(410,193,Color.white,"Joules");
          capE2 = new TextSnippet(510,193,Color.white,"J");
          indE2 = new TextSnippet(610,193,Color.white,"J");
          energies = new TextSnippet(530,15,Color.white,"Energies");
          totalE = new TextSnippet(420,30,Color.white,"Total");
          capE = new TextSnippet(520,30,Color.white,"Capacitor");
          indE = new TextSnippet(620,30,Color.white,"Inductor");
          b1 = new BarChart(410,40,70,140,Color.green);
          b2 = new BarChart(510,40,70,140,Color.red);
          b3 = new BarChart(610,40,70,140,Color.blue);
          p1 = new PlusMinusRedBlue(328,155);
          p1a = new PlusMinusRedBlue(358,155);
          p2 = new PlusMinusRedBlue(328,212);
          p2a = new PlusMinusRedBlue(358,212);
          resetButton = new DrawableAndClickable(){
               Polygon p;
               Color background = Color.lightGray;
               //int width = 115; //JAR
               int width = 117; //Mac
               int height = 18;
               int x = 570;
               int y = 420;
               public void draw(Graphics g){
                    g.setColor(background);
                    g.fillRect(x,y,width,height);
               }
               public boolean contains(int X, int Y){
                    if(p==null)p = new Polygon(new int[]{x,x+width,x+width,x}, new int[]{y,y,y+height,y+height}, 4);
                    return p.contains(X,Y);
               }
               public void clicked(){
                    reset();
               }
          };
          electricFieldLines = new FieldLines(335,183,40,14);
          magFieldLines = new MagFieldLines();
          resetButtonText = new TextSnippet(576,433,Color.black,"Reset Simulation"); //JAR
          //resetButtonText = new TextSnippet(574,434,Color.black,"Reset Simulation"); //Dr. J
          displayElements = new ArrayList<DrawableAndClickable>();
          for(DrawableAndClickable d : new DrawableAndClickable[]{new BackGround(Color.black),
               a1, iDirection, energies, totalE, capE, indE, capVoltage, speedS, speedT, kenCole, 
               g1, g2, g2Label, c1, p1, p1a, p2a, p2, b1, b2, b3, timeLabel,g1Label,
               voltageT, voltageS, indT, indS, capT, capS, capLabel, indLabel, frequency,
               period, charge, current, dIdt, totalE2, capE2, indE2, indVoltage, timeConstant,
               resetButton, resetButtonText, electricFieldLines, magFieldLines, elecLabel,
               magLabel})
               displayElements.add(d);
          t.setDelay(20);
          time = 0.1;
          t.start();
     }
     public void mouseClicked(MouseEvent e){}
     public void mousePressed(MouseEvent e){
          for(int x = 0; x < displayElements.size();x++){
               if(displayElements.get(x).contains(mouseX, mouseY)){
                    displayElements.get(x).clicked();
                    if(displayElements.get(x) != resetButton) grabbed = displayElements.get(x);
               }
          }
     }
     public void mouseReleased(MouseEvent e){
          grabbed = null;
     }
     public void mouseEntered(MouseEvent e){}
     public void mouseExited(MouseEvent e){}
     public void mouseDragged(MouseEvent e){
          mouseX=e.getX()-1;
          mouseY=e.getY()-24;
          if(grabbed != null){
               grabbed.clicked();
               repaint();
          }
     }
     public void mouseMoved(MouseEvent e){
          mouseX=e.getX()-1;
          mouseY=e.getY()-24;
          //System.out.println("(" + mouseX + "," + mouseY + ")");
     }
     public void actionPerformed(ActionEvent e){
          double I = getInductance();
          double C = getCapacitance();
          double V = getMaxVoltage();
          double Vnow = getVoltage(I,C,V,time);
          t.setDelay((int)(speedS.getVal() * 30 + 50));
          voltageT.s = "Voltage: " + (int)(V*1000)/1000.0 + " V";
          indT.s = "Inductance: " + (int)(I*1000)/1000.0 + " H";
          capT.s = "Capacitance: " + (int)(C*1000)/1000.0 + " F";
          timeLabel.s = (int)(time*1000)/1000.0 + " seconds";
          double hz = getFrequency(I,C,V,time);
          p1.val = (getVoltage(I,C,V,time) / -V);
          p1a.val = p1.val;
          electricFieldLines.val = p1.val;
          magFieldLines.val = getCurrent(I,C,V,time) / getCurrent(I,C,V, 0.25 / getFrequency(I,C,V,0));
          p2.val = (getVoltage(I,C,V,time) / V);
          p2a.val = p2.val;
          frequency.s = "Frequency: " + (int)(hz*1000)/1000.0 + " Hz";
          period.s = "Period: " + (int)((1/hz)*1000)/1000.0 + " s";
          charge.s = "Charge: " + Math.abs((int)(1000 * C * getVoltage(I,C,V,time))/1000.0) + " C";
          current.s = "Current(I): " + (int)(getCurrent(I,C,V,time) * 1000)/1000.0 + " A";
          capVoltage.s = "Capacitor Voltage: " + (int)(Vnow*1000)/1000.0;
          lastCurrent = getCurrent(I,C,V,time-.1);
          lastTime = time-.1;
          double didt = (getCurrent(I,C,V,time+ 0.001) - getCurrent(I,C,V,time - 0.001)) / 0.002;
          indVoltage.s = "Inductor EMF (V): " + ((int)(Math.abs(didt * I)*1000))/1000.0;
          dIdt.s = "dI/dt: "+ (int)(1000 * didt)/1000.0 + " A/s";
          totalE2.s = ""+(int)(getUTotal(I,C,V,time)*1000)/1000.0 + " J";
          capE2.s = ""+(int)(getUCapacitor(I,C,V,time)*1000)/1000.0 + " J";
          indE2.s = ""+(int)(getUInductor(I,C,V,time)*1000)/1000.0 + " J";
          timeConstant.s = "LC Time Constant: " + ((int)(getLCConstant(I, C) * 1000.0 ))/ 1000.0;
          updateGraphs(I, C, V);
          time += 0.1;
          repaint();
     }
     private double getFrequency(double L, double C, double V, double t){
          return 1 / (6.28318530718 * Math.sqrt(L*C));
     }
     private double getVoltage(double L, double C, double V, double t){
          return Math.sqrt(L/C) * getCurrent(L,C,V,t - Math.sqrt(L*C) * 1.57079632679);
     }
     private double getCurrent(double L, double C, double maxV, double t){
          return -maxV / Math.sqrt(L / C) * Math.sin(1.0 / Math.sqrt(L*C) * t);
     }
     private double getUInductor(double L, double C, double V, double t){
          return getUTotal(L,C,V,t) - getUCapacitor(L,C,V,t);
     }
     private double getUCapacitor(double L, double C, double V, double t){
          return .5 * getCapacitance() * Math.pow(getVoltage(L,C,V,t),2);
     }
     private double getUTotal(double L, double C, double V, double t){
          return .5 * getCapacitance() * getMaxVoltage() * getMaxVoltage();
     }
     private double getMaxVoltage(){
          return (-voltageS.getVal() - 1) * 12 + 24.400000000000002;
     }
     private double getInductance(){
          return (-indS.getVal() - 1) * 12 + 24.400000000000002;
     }
     private double getCapacitance(){
          return (-capS.getVal() - 1) * 12 + 24.400000000000002;
     }
     private double getLCConstant(double L, double C){
          return Math.sqrt(L * C);
     }
     public void updateGraphs(double I, double C, double V){
          double current = getCurrent(I,C,V,time)/MAX_CURRENT_POSSIBLE;
          g1.updateVal(getVoltage(I,C,V,time)/MAX_VOLTS_POSSIBLE);
          g2.updateVal(current);
          a1.val = current;
          b1.val = getUTotal(I,C,V,time)/MAX_U_POSSIBLE;
          b2.val = getUCapacitor(I,C,V,time)/MAX_U_POSSIBLE;
          b3.val = getUInductor(I,C,V,time)/MAX_U_POSSIBLE;
     }
     public Dimension getPreferredSize(){
          return new Dimension(windowX,windowY);
     }
     protected void paintComponent(Graphics g){
    	 Graphics2D g2 = (Graphics2D)g;
    	 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    	 RenderingHints.VALUE_ANTIALIAS_ON);
    	 g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
    	 RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    	 //g2.setFont(g.getFont().deriveFont(Font.BOLD));
          for(int x = 0; x < displayElements.size();x++){
               displayElements.get(x).draw(g2);
          }
     }
     private abstract class DrawableAndClickable{
          public void draw(Graphics g){}
          public boolean contains(int x, int y){return false;}
          public void clicked(){}
     }
     private class BackGround extends DrawableAndClickable{
          Color c;
          BackGround(Color c){
               this.c=c;  
          }
          public void draw(Graphics g){
               g.setColor(c);
               g.fillRect(0,0,windowX+BUFFER,windowY+BUFFER);
               
          }
     }
     private class PlusMinusRedBlue extends DrawableAndClickable{
          int x, y;
          double val;
          PlusMinusRedBlue(int x, int y){
               this.x=x;
               this.y=y;
               val = 0.0;
          }
          public void draw(Graphics g){
               if(val > 0){
                    g.setColor(new Color(0,0,(int)(255 * val)));
                    g.fillRect(x+5,y,5,15);
                    g.fillRect(x,y+5,15,5);
               }else if(val < 0){
                    g.setColor(new Color((int)(255 * -val),0,0));
                    g.fillRect(x,y+5,15,5);
               }
          }
     }
     private class TextSnippet extends DrawableAndClickable{
          int x, y;
          Color c;
          String s;
          TextSnippet(int x, int y, Color c, String s){
               this.x=x;
               this.y=y;
               this.c=c;
               this.s=s;
          }
          public void draw(Graphics g){
               g.setColor(c);
               g.drawString(s,x,y);
          }
     }
     private class FieldLines extends DrawableAndClickable{
          int x, y, width, height;
          double val;
          FieldLines(int x, int y, int width, int height){
               this.x = x;
               this.y = y;
               this.width = width;
               this.height = height;
          }
          public void draw(Graphics g){
               double val2 = Math.abs(val);
               g.setColor(new Color((int)(255 * val2),(int)(255 * val2),0));
               for(int a = 0; a < 4; a++){
                    g.drawRect(x + (a * width)/4, y+1, width/64, height-2);
               }
               if(val < 0){
                    int spacing = 10;
                    for(int z = 0; z < 4; z++){
                         for(int x = 1 + z * spacing; x < 3 + z * spacing; x++){
                              for(int y = 0; y < (x - z * spacing) * 2; y++){
                                   drawPixel(g, 330+x, 187-y);
                              }
                         }
                         drawPixel(g, 334 + z * spacing, 183);
                         drawPixel(g, 335 + z * spacing, 183);
                         drawPixel(g, 336 + z * spacing, 183);
                         drawPixel(g, 335 + z * spacing, 182);
                    }
                    for(int z = 0; z < 4; z++){
                         for(int x = 1 + z * spacing; x < 3 + z * spacing; x++){
                              for(int y = 0; y < 6 - (x - z * spacing) * 2; y++){
                                   drawPixel(g, 337+x, 187-y);
                              }
                         }
                    }
               }else{
                    int spacing = 10;
                    for(int z = 0; z < 4; z++){
                         for(int x = 1 + z * spacing; x < 3 + z * spacing; x++){
                              for(int y = 0; y < (x - z * spacing) * 2; y++){
                                   drawPixel(g, 330+x, 193+y);
                              }
                         }
                         drawPixel(g, 334 + z * spacing, 197);
                         drawPixel(g, 335 + z * spacing, 197);
                         drawPixel(g, 336 + z * spacing, 197);
                         drawPixel(g, 335 + z * spacing, 198);
                    }
                    for(int z = 0; z < 4; z++){
                         for(int x = 1 + z * spacing; x < 3 + z * spacing; x++){
                              for(int y = 0; y < 6 - (x - z * spacing) * 2; y++){
                                   drawPixel(g, 337+x, 193+y);
                              }
                         }
                    }
               }
          }
     }
     private class MagFieldLines extends DrawableAndClickable{
          double val = 1;
          public void draw(Graphics g){
               Graphics2D g2d = (Graphics2D) g;
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            		   RenderingHints.VALUE_ANTIALIAS_ON);
            		   g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            		   RenderingHints.VALUE_INTERPOLATION_BILINEAR);
               Color orange = new Color(255,128,0,(int)(Math.abs(val * 255)));  //JAR
             //  Color orange = new Color((int)(Math.abs(val * 255)), (int)(Math.abs(val * 128)), 0); //DR.Java
               g2d.setColor(orange);
               g2d.setStroke(new BasicStroke(5));
               g2d.draw(new Arc2D.Float(44, 172, 50, 100, 180, 90, Arc2D.OPEN));
               g2d.setColor(Color.black);
               g2d.draw(new Arc2D.Float(-6, 172, 50, 100, 270, 90, Arc2D.OPEN));
               g2d.setColor(orange);
               g2d.draw(new Arc2D.Float(-6, 172, 50, 100, 270, 90, Arc2D.OPEN));
               g2d.setColor(Color.darkGray);
               g2d.draw(new QuadCurve2D.Float(50,40+300/2 + 2 * 10-1,
                                                   50-30,40+300/2+5 + 2 * 10,
                                                   50,40+300/2+15 + 2 * 10+1));
               g2d.setColor(orange);
               g2d.draw(new Line2D.Float(44,197.5f,44,217));
               g2d.setColor(Color.darkGray);
               g2d.draw(new QuadCurve2D.Float(50,40+300/2 + 0 * 10-1,
                                                   50-30,40+300/2+5 + 0 * 10,
                                                   50,40+300/2+15 + 0 * 10+1));
               g2d.setColor(orange);
               g2d.draw(new Line2D.Float(44,192.5f,44,177.5f));
               g2d.setColor(Color.darkGray);
               g2d.draw(new QuadCurve2D.Float(50,40+300/2 + -2 * 10-1,
                                                   50-30,40+300/2+5 + -2 * 10,
                                                   50,40+300/2+15 + -2 * 10+1));
               g2d.setColor(orange);
               g2d.draw(new Line2D.Float(44,172.5f,44,155));
               g2d.setColor(Color.darkGray);
               g2d.draw(new QuadCurve2D.Float(50,40+300/2 + -4 * 10-1,
                                                   50-30,40+300/2+5 + -4 * 10,
                                                   50,40+300/2+15 + -4 * 10+1));
               g2d.setColor(orange);
               g2d.draw(new Arc2D.Float(44, 105, 50, 100, 90, 90, Arc2D.OPEN));
               g2d.setColor(new Color(0,0,0,(int)(255 * Math.abs(val))));
               g2d.draw(new Arc2D.Float(-6, 105, 50, 100, 0, 90, Arc2D.OPEN));
               g2d.setColor(orange);
               g2d.draw(new Arc2D.Float(-6, 105, 50, 100, 0, 90, Arc2D.OPEN));

               if(val < 0){
                    drawTriangle(g2d, 72, 100, true);
                    drawTriangle(g2d, 17, 100, false);
               }else{
                    drawTriangle(g2d, 72, 266, true);
                    drawTriangle(g2d, 17, 266, false);
               }
          }
          void drawTriangle(Graphics2D g, int x, int y, boolean dir){
        	  g.fillPolygon(new int[]{x, x + (dir?12:-12), x},new int[]{y, y + 6, y + 12},3);
          }
     }
     private class HorizontalSlider extends DrawableAndClickable{
          int x, y, length, width, pos;
          Color c;
          Polygon p;
          HorizontalSlider(int x,int y,int width, int length, Color c){
               this.x=x;
               this.y=y;
               this.length=length;
               this.width=width;
               this.c=c;
               pos = width/2;
          }
          public void draw(Graphics g){
               g.setColor(c);
               g.fillRect(x,y,width,length);
               g.fillRect(x+pos,y-5,length,length+10);
          }
          public boolean contains(int X, int Y){
               if(p==null)p = new Polygon(new int[]{x,x+width,x+width,x},
                                          new int[]{y-5,y-5,y+length+5,y+length+5}, 4);
               return p.contains(X,Y);
          }
          double getVal(){
               return -2.0 * pos / width + 1;
          }
          public void clicked(){
               if(mouseX < x) pos = 0;
               else if(mouseX > (x + width-length)) pos = width-length;
               else pos = mouseX-x;
          }
     }
     private class BarChart extends DrawableAndClickable{
          int x, y, width, height;
          Color c;
          double val;
          BarChart(int x, int y, int width, int height, Color c){
               this.x=x;
               this.y=y;
               this.width=width;
               this.height=height;
               this.c=c;
          }
          public void draw(Graphics g){
               g.setColor(c);
               g.fillRect(x, (int)(y + height - height * val),width, (int)(height * val));
          }
     }
     private class Circuit extends DrawableAndClickable{
          int x, y, width, height;
          Color c;
          Circuit(int x,int y,int width,int height,Color c){
               this.x=x;
               this.y=y;
               this.width=width;
               this.height=height;
               this.c=c;
          }
          public void draw(Graphics g){
               Graphics2D g2D = (Graphics2D)g;
               g2D.setColor(c);
               g2D.setStroke(new BasicStroke(5));
               g2D.draw(new Line2D.Float(x,y,x+width,y));
               g2D.draw(new Line2D.Float(x+width,y,x+width,y+height/2-12));
               g2D.draw(new Line2D.Float(x+width-20,y+height/2-12,x+width+20,y+height/2-12));
               g2D.draw(new Line2D.Float(x+width-20,y+height/2+12,x+width+20,y+height/2+12));
               g2D.draw(new Line2D.Float(x+width,y+height/2+15,x+width,y+height));
               g2D.draw(new Line2D.Float(x,y,x,y+height/2 - 40));
               for(int a = -4; a < 4; a+=2){
                    g2D.draw(new QuadCurve2D.Float(x,y+height/2 + a * 10-1,
                                                   x-30,y+height/2+5 + a * 10,
                                                   x,y+height/2+15 + a * 10+1));
               }
               g2D.draw(new Line2D.Float(x,y+height/2 + 40,x,y+height));
               g2D.draw(new Line2D.Float(x,y+height,x+width,y + height));
          }
     }
     private class Arrow extends DrawableAndClickable{
          Polygon p;
          int x,width;
          Color c;
          double val;
          Arrow(int x, int y, int width, int height, Color c){
               this.x=x;
               this.width=width;
               this.c=c;
               p = new Polygon(new int[]{x,x,x}, new int[]{y,y+height/2,y+height}, 3);
          }
          public void draw(Graphics g){
               p.xpoints[1] = (int)(width * val) + x;
               g.setColor(c);
               g.fillPolygon(p);
          }
          
     }
     private class Graph extends DrawableAndClickable{
          int x, y, width, height, arPos;
          Color background, axes, graph;
          double[] vals;
          Graph(int x, int y, int width, int height, Color background, Color axes, Color graph){
               this.x=x;
               this.y=y;
               this.width=width;
               this.height=height;
               this.background=background;
               this.axes=axes;
               this.graph=graph;
               vals = new double[width/2];
               for(int a = 0; a < vals.length;a++)vals[a]=0.0;
               arPos = 0;
          }
          void updateVal(double val){
               vals[arPos++]=val;
               arPos%=vals.length;
          }
          public void draw(Graphics g){
        	   Graphics2D g2D = (Graphics2D)g;
               g.setColor(background);
               g.fillRect(x,y,width+2,height);
               g2D.setColor(axes);
               g2D.setStroke(new BasicStroke(2));
               g2D.draw(new Line2D.Float(x,y+1,x,y+height-2));
               g2D.draw(new Line2D.Float(x+1,y+height/2,x+width,y+height/2));
               g2D.setColor(graph);
               int z = arPos;
               int drawPos=0;
               int xAxis = y + height/2;
               do{
                    int j = (z+1)%vals.length;
                    g2D.draw(new Line2D.Float(x + drawPos+2, (int)(xAxis - vals[z] * height / 2),
                                              x + drawPos+4, (int)(xAxis - vals[j] * height / 2)));
                    z++;
                    drawPos+=2;
                    z %= vals.length;
               }while(z!=arPos && z+1 != arPos);
          }
     }
     private void drawPixel(Graphics g, int x, int y){
          g.fillRect(x,y,1,1);
     }
     public static void main(String[] args){
          frame = new JFrame("LC Circuit Simulator");
          lc = new LCSim();
          frame.add(lc);
          frame.pack();
          frame.setResizable(false);
          frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
          frame.setLocationRelativeTo(null);
          frame.addMouseMotionListener(lc);
          frame.addMouseListener(lc);
          frame.setVisible(true);
     }
}
