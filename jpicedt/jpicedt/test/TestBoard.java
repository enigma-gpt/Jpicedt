///////////////////////////////////////////////////////////////////////////
//// TEST
///////////////////////////////////////////////////////////////////////////

package jpicedt.test;

import jpicedt.graphic.event.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.view.*;
import jpicedt.graphic.*;
import jpicedt.graphic.grid.*;

import jpicedt.ui.*;
import jpicedt.ui.dialog.*;

import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

/**
 * test class
 */
public class TestBoard {

	PEDrawingBoard board;
	Grid grid;
	PicPoint pt = new PicPoint(); // buffer
	JLabel lblMouseX = new JLabel();
	JLabel lblMouseY = new JLabel();
	JLabel lblPEMouseX = new JLabel();
	JLabel lblPEMouseY = new JLabel();
	Properties preferences = new Properties();
	PageFormat pageFormat;
    public PicText text,text2;
    public PicNodeConnection con;
	
	/** test */
	public static void main(String arg[]){
		new TestBoard();
	}

	public TestBoard(){

		//RepaintManager.setCurrentManager(new jpicedt.ui.util.DebugRepaintManager());
		JFrame frame = new JFrame("test");
		frame.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});
		double zoom = 2.0;
		int untitled = 0;
		pageFormat = new PageFormat(200, 100, 50, 50);
		grid = new Grid(preferences);
		ContentType contentType = new jpicedt.format.output.pstricks.PstricksContentType();
		board = new PEDrawingBoard(0, zoom, pageFormat, grid, contentType); //
		//board = new PEDrawingBoard(untitled,preferences); //

		//board.getCanvas().getEditorKit().addPropertyChangeListener(new PropertyChangeHandler());
		//board.getCanvas().getEditorKit().addHelpMessageListener(new HelpMessageHandler());
 		//board.getCanvas().addPEMouseInputListener(new PEMouseHandler());
 		//board.getCanvas().addMouseMotionListener(new MouseHandler());
		//board.getCanvas().addPropertyChangeListener(new PropertyChangeHandler());
		//board.getCanvas().addSelectionListener(new SelectionHandler());
		//board.getCanvas().addZoomListener(new ZoomHandler());
		
		frame.getContentPane().setLayout(new BorderLayout(5,5));
		frame.getContentPane().add(board,BorderLayout.CENTER);
		// add control panel
		Box p = new Box(BoxLayout.X_AXIS);
		JButton b = new JButton("Add text");
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// --- bitmap for PicText test ---
				PicText text = new PicText(new PicPoint(20,10), "Hello world!", new PicAttributeSet());
				text.setFrameType(PicText.FrameStyle.OVAL);
				text.setHorAlign(PicText.HorAlign.RIGHT);
				text.setVertAlign(PicText.VertAlign.CENTER);
				board.getCanvas().getDrawing().add(text);
		}});
		p.add(b);
				
		b = new JButton("Save");
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					board.save("test.tex",false); // whole drawing
				}
				catch (IOException ioEx){ioEx.printStackTrace();}
		}});
		p.add(b);
				
		b = new JButton("Draw");
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				board.getCanvas().getEditorKit().setCurrentMouseTool(DrawToolFactory.ARC_PIE_FROM_PARALLELO); 
		}});
		p.add(b);
				
		b = new JButton("$$$$");
		ActionListener al  = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println();
				System.out.print("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				System.out.println();
		}};
		board.registerKeyboardAction(al,KeyStroke.getKeyStroke("$"),JComponent.WHEN_FOCUSED);
		b.addActionListener(al);
		p.add(b);
		p.add(lblMouseX); p.add(lblMouseY); p.add(lblPEMouseX);p.add(lblPEMouseY);
 		
		al  = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				board.getCanvas().deleteSelection();
		}};
		board.getCanvas().registerKeyboardAction(al, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0),JComponent.WHEN_FOCUSED);

		al  = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println(board.getCanvas().getDrawing());
				System.out.println();
		}};
		board.getCanvas().registerKeyboardAction(al, KeyStroke.getKeyStroke('a'),JComponent.WHEN_FOCUSED);
		
		frame.getContentPane().add(p,BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		
		Drawing dr = board.getCanvas().getDrawing();
		
		EditorKit kit = board.getCanvas().getEditorKit();
		PicAttributeSet set = kit.getInputAttributes();

		text = new PicText(new PicPoint(0,0), "1", set);
		text.setDimensions(5,2,0);
		dr.add(text);
		PicAttributeSet set2=new PicAttributeSet(set);   
		//TEXT_VALIGN_BOTTOM  TEXT_VALIGN_BASELINE TEXT_VALIGN_CENTER TEXT_VALIGN_TOP
                set2.setAttribute(PicAttributeName.TEXT_VERT_ALIGN,PicText.VertAlign.TOP);
		//TEXT_BOX_OVAL TEXT_BOX_CIRCLE
		set2.setAttribute(PicAttributeName.TEXT_FRAME, PicText.FrameStyle.RECTANGLE);
		text2 = new PicText(new PicPoint(30,30), "2", set2);
		text2.setDimensions(5,2,3);
		dr.add(text2);
		con = new PicNodeConnection(text,text2,"nccurve",set);
		dr.add(con);
		System.out.println("####COUCOU####"+text2);
		text2.setCtrlPt(0,new PicPoint(40,20),null);
		

//		dr.addDrawingListener(new DrawingHandler());
// 		PicAttributeSet set = new PicAttributeSet();
// 		PicLine line = new PicLine(new PicPoint(0.0,10.0),new PicPoint(20.0,40.0),set);
// 		dr.addElement(line);
		//			dr.addDrawable(new PicBezierQuad(new PicPoint(10.0,0.0),new PicPoint(40.0,40.0),new PicPoint(50.0,30.0),set));
		//			double boxW = 50.0;
		//			double boxH = 40.0;
		// 			dr.addDrawable(new PicText(new PicPoint(0.0,0.0), "a", true, boxW, boxH, PicText.LEFT, PicText.BOTTOM, false, set));
		// 			dr.addDrawable(new PicText(new PicPoint(40.0,60.0), "A", true, boxW,boxH, PicText.LEFT, PicText.TOP, false, set));
		// 			dr.addDrawable(new PicText(new PicPoint(0.0,100.0), "zjq", true, boxW,boxH, PicText.LEFT, PicText.CENTER, false, set));
		// 			dr.setEditPointsMode(true);
		//			board.getCanvas().getGrid().setSnapOn(false);
		
		

	}

	///////////////////////////////////////////////////////////////////
	class DrawingHandler implements DrawingListener {
		
		public void changedUpdate(DrawingEvent e){
			if(jpicedt.Log.DEBUG)
				jpicedt.Log.debug("got DrawingEvent e = " + e);
		}
	}
	///////////////////////////////////////////////////////////////////
	class SelectionHandler implements SelectionListener {
		
		public void selectionUpdate(SelectionEvent e){
			if(jpicedt.Log.DEBUG)
				jpicedt.Log.debug("got SelectionEvent e = " + e);
		}
	}
	///////////////////////////////////////////////////////////////////
	class HelpMessageHandler implements HelpMessageListener {
		
		public void helpMessagePosted(HelpMessageEvent e){
			lblMouseX.setText(e.getMessage());
		}
	}
	///////////////////////////////////////////////////////////////////
	class ZoomHandler implements ZoomListener {
		
		public void zoomUpdate(ZoomEvent e){
			if(jpicedt.Log.DEBUG)
				jpicedt.Log.debug("got ZoomEvent e = " + e);
		}
	}
	///////////////////////////////////////////////////////////////////
	class MouseHandler extends MouseInputAdapter {

		public void mouseMoved(MouseEvent e){
			Point pt = e.getPoint();
			lblMouseX.setText(" x="+Integer.toString(pt.x));
			lblMouseY.setText(" y="+Integer.toString(pt.x));
		}
	}
	///////////////////////////////////////////////////////////////////
	class PEMouseHandler extends PEMouseInputAdapter {

		public void mouseMoved(PEMouseEvent e){
			PicPoint pt = e.getPicPoint();
			lblPEMouseX.setText(" X(PE)="+Double.toString(pt.x));
			lblPEMouseY.setText(" Y(PE)="+Double.toString(pt.y));
		}
	}
	/////////////////////////////////////////////////////////////////////
	class PropertyChangeHandler implements PropertyChangeListener { 

		public void propertyChange(PropertyChangeEvent e){
			if(jpicedt.Log.DEBUG)
			{
				jpicedt.Log.debug("propertyChange");
				jpicedt.Log.debugAppendLn("source="+e.getSource());
				jpicedt.Log.debugAppendLn("property name="+e.getPropertyName());
				jpicedt.Log.debugAppendLn("old value="+e.getOldValue());
				jpicedt.Log.debugAppendLn("new value="+e.getNewValue());
			}
		}
	}
}
