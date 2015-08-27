/*
 TestCanvas.java - August 15, 2002 - jPicEdt 1.3.3, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Departement de Physique
 École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org

*/
// Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilité au code source et des droits de copie, de modification et de
// redistribution accordés par cette licence, il n'est offert aux utilisateurs qu'une garantie limitée.  Pour
// les mêmes raisons, seule une responsabilité restreinte pèse sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les concédants successifs.
//
// À cet égard l'attention de l'utilisateur est attirée sur les risques associés au chargement, à
// l'utilisation, à la modification et/ou au développement et à la reproduction du logiciel par l'utilisateur
// étant donné sa spécificité de logiciel libre, qui peut le rendre complexe à manipuler et qui le réserve
// donc à des développeurs et des professionnels avertis possédant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invités à charger et tester l'adéquation du logiciel à leurs
// besoins dans des conditions permettant d'assurer la sécurité de leurs systèmes et ou de leurs données et,
// plus généralement, à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
//
// Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accepté les termes.
//
/// Commentary:

//



/// Code:
package jpicedt.test;

import jpicedt.*;
import jpicedt.graphic.*;
import jpicedt.graphic.event.*;
import jpicedt.graphic.io.formatter.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.view.*;
import jpicedt.graphic.grid.*;
import jpicedt.ui.*;
import jpicedt.ui.action.*;
import jpicedt.ui.dialog.*;
import jpicedt.ui.util.*;
import jpicedt.format.output.latex.*;

import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import bsh.*;
import bsh.util.*;

import jpicedt.graphic.model.StyleConstants.*;

/**
 * Test class incorporating a BSH interpreter facility for efficient debugging.
 * @author Sylvain Reynal
 * @since jpicedt 1.3.2
 * @version $Id: TestCanvas.java,v 1.44 2013/03/27 06:52:56 vincentb1 Exp $
 */
public class TestCanvas implements Runnable {

	public PECanvas canvas;
	public Interpreter interpreter; // BSH
	private JLabel mouseCoordsLbl = new JLabel();
	public PicText text,text2;
	public PicNodeConnection con;
	JFrame frame;

	/** test */
	public static void main(String arg[]){
		TestCanvas test = new TestCanvas();
	}

	public TestCanvas(){

		JPanel testCanvas = createTestCanvas();
		JConsole console = new JConsole();
		console.setPreferredSize(new Dimension(1000,400));
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, testCanvas, console);
		this.frame = new JFrame("test");
		frame.getContentPane().add(splitPane);
		frame.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});
		//System.out.println("Creating interpreter...");
		interpreter = new Interpreter(console);
		//System.out.println("Setting predefined variables and methods...");
		try {
			interpreter.eval("show();");
			interpreter.eval("import jpicedt.*;");
			interpreter.eval("import jpicedt.test.*;");
			interpreter.eval("import jpicedt.graphic.*;");
			interpreter.eval("import jpicedt.graphic.model.*;");
			interpreter.eval("import jpicedt.graphic.event.*;");
			interpreter.eval("import jpicedt.graphic.grid.*;");
			interpreter.eval("import jpicedt.graphic.view.*;");
			interpreter.eval("import jpicedt.graphic.view.highligher.*;");
			interpreter.eval("import jpicedt.graphic.toolkit.*;");
			interpreter.eval("import jpicedt.graphic.io.formatter.*;");
			interpreter.eval("import jpicedt.graphic.io.parser.*;");
			//interpreter.eval("show();");
			interpreter.set("cv",canvas);
			interpreter.set("dr",canvas.getDrawing());
			interpreter.set("kit",canvas.getEditorKit());
			interpreter.set("sh",canvas.getEditorKit().getSelectionHandler());
			interpreter.set("e0",canvas.getDrawing().get(0));
			//interpreter.set("e1",canvas.getDrawing().get(1));
			//interpreter.set("e2",canvas.getDrawing().get(2));
			interpreter.eval("select(Element e){cv.select(e,PECanvas.SelectionBehavior.REPLACE);}");
			interpreter.eval("unselect(Element e){cv.unSelect(e);}");
			//"canvas=test.canvas;" +
			//"drawing=canvas.getDrawing();");
			//"preferences=jpicedt.JPicEdt.getPreferences();" +
			//"editorkit(){return jpicedt.JPicEdt.getSelectedEditorKit();}");
		}
		catch (Exception ex){ex.printStackTrace();}
		frame.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(screenSize.width-frame.getWidth(),0);
		frame.setVisible(true);
		Thread t = new Thread(this);t.start();
	}

	/**
	 * Run the BSH interpreter in interactive mode.
	 */
	public void run(){
		//System.out.println("Starting interpreter...");
		interpreter.run(); // run interactively
	}


	public JPanel createTestCanvas(){
		JPanel pane = new JPanel();

		Grid grid = new  Grid(true, true, 10.0, 10.0, Color.LIGHT_GRAY, Grid.DASH);
		//grid.setVisible(false);
		grid.setSnapOn(true);
		//ContentType ct = new jpicedt.format.output.latex.LatexContentType();
		ContentType ct = new jpicedt.format.output.pstricks.PstricksContentType();
		//ContentType ct = null;
		canvas = new PECanvas(1.0, new PageFormat(200, 100, 50, 50),grid, ct);
		canvas.addPEMouseInputListener(new MouseHandler());
		//canvas.getEditorKit().addPropertyChangeListener(new PropertyChangeHandler());

		EditorKit kit = canvas.getEditorKit();
		PicAttributeSet set = kit.getInputAttributes();
		//set.setAttribute(RIGHT_ARROW, ArrowStyle.ARROW_HEAD);
		//((DefaultSelectionHandler)canvas.getEditorKit().getSelectionHandler()).setHighlightingMode(DefaultSelectionHandler.LOCAL_MODE);


		//canvas.addPropertyChangeListener(new PropertyChangeHandler());
		//canvas.addSelectionListener(new SelectionHandler());
		//canvas.addZoomListener(new ZoomHandler());
		//canvas.getEditorKit().addHelpMessageListener(new HelpMessageHandler());
		//canvas.getDrawing().addDrawingListener(new DrawingHandler());
		canvas.getEditorKit().setPopupMenuFactory(new jpicedt.ui.internal.PEPopupMenuFactory(new ActionRegistry(new DefaultActionDispatcher(canvas))));
		canvas.getEditorKit().setDialogFactory(new DefaultDialogFactory(this.frame));

		pane.setLayout(new BorderLayout(5,5));
		pane.add(canvas,BorderLayout.CENTER);

		// add control panel
		Box p = new Box(BoxLayout.X_AXIS);

		JButton b = new JButton("Write");
		b.addActionListener(new ActionListener(){
			                    public void actionPerformed(ActionEvent e){
				                    //c1.join(c2);
				                    //canvas.joinSelection();
				                    //canvas.getEditorKit().setCurrentMouseTool(DrawToolFactory.MULTI_CURVE);
				                    //text.setAttribute(TEXT_FRAME, TEXT_BOX_OVAL);
						    try {
							    Writer wr = new PrintWriter(System.out);
							    canvas.write(wr, false);
							    wr.flush();
						    }
						    catch (Exception ex){
							    ex.printStackTrace();
						    }


			                    }});
		p.add(b);


		p.add(this.mouseCoordsLbl);


		ActionListener al;

		al = new ActionListener(){
			     public void actionPerformed(ActionEvent e){
				     System.out.println();
				     System.out.print("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				     System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				     System.out.println();
			     }};
		canvas.registerKeyboardAction(al, KeyStroke.getKeyStroke('$'),JComponent.WHEN_FOCUSED);

		al  = new ActionListener(){
			      public void actionPerformed(ActionEvent e){
				      System.out.println(canvas.getDrawing());
				      System.out.println();
			      }};
		canvas.registerKeyboardAction(al, KeyStroke.getKeyStroke('a'),JComponent.WHEN_FOCUSED);

		al  = new ActionListener(){
			      public void actionPerformed(ActionEvent e){
				      canvas.deleteSelection();
			      }};
		canvas.registerKeyboardAction(al, KeyStroke.getKeyStroke('s'),JComponent.WHEN_FOCUSED);

		ActionLocalizer localizer = jpicedt.Localizer.currentLocalizer().getActionLocalizer();
		ActionDispatcher dispatcher = new DefaultActionDispatcher(canvas);
		PEAction ac = new EditorKit.ToggleEditPointsModeAction(dispatcher, localizer);
		canvas.registerKeyboardAction(ac, KeyStroke.getKeyStroke('c'),JComponent.WHEN_FOCUSED);

		pane.add(p,BorderLayout.SOUTH);


		// ---- PicNodeConnection test ---
		/*
		PicText nodeA = new PicText(new PicPoint(0,0), "nodeA", set);
		PicText nodeB = new PicText(new PicPoint(20,20), "nodeB", set);

		canvas.getDrawing().addElement(nodeA);
		canvas.getDrawing().addElement(nodeB);

		PicNodeConnection connectionAB = new PicNodeConnection(nodeA, nodeB, PicNodeConnection.EDGE_NCLINE, set);
		canvas.getDrawing().addElement(connectionAB);
		*/

		// --- bitmap for PicText test ---
		/*text = new PicText(new PicPoint(0,0), "1", set);
		text.setDimensions(5,2,0);
		canvas.getDrawing().addElement(text);
		PicAttributeSet set2=new PicAttributeSet(set);
		//TEXT_VALIGN_BOTTOM  TEXT_VALIGN_BASELINE TEXT_VALIGN_CENTER TEXT_VALIGN_TOP
		              set2.setAttribute(TEXT_VERT_ALIGN,TEXT_VALIGN_TOP);
		//TEXT_BOX_OVAL TEXT_BOX_CIRCLE
		set2.setAttribute(TEXT_FRAME, TEXT_BOX_RECTANGLE);
		text2 = new PicText(new PicPoint(30,30), "2", set2);
		text2.setDimensions(5,2,3);
		canvas.getDrawing().addElement(text2);
		con = new PicNodeConnection(text,text2,"ncline",set);
		canvas.getDrawing().addElement(con);*/

		//PicMultiCurve c1 = new PicMultiCurve(new PicPoint(0,0),new PicPoint(0,10),new PicPoint(10,10),new PicPoint(20,10));
		//PicMultiCurve c2 = new PicMultiCurve(new PicPoint(40,20),new PicPoint(50,30),new PicPoint(55,10),new PicPoint(60,20));
		//PicMultiCurve c3 = new PicMultiCurve(new PicPoint(0,30),new PicPoint(50,30));
		//c2 = new PicMultiCurve(new PicPoint(40,20));
		//c2 = new PicEllipse(new PicPoint(-30,-30), new PicPoint(30,-30), new PicPoint(30,30),PicEllipse.OPEN);
		//c2.setAngleStart(0.0);c2.setAngleEnd(180.0);
		//canvas.getDrawing().addElement(c1);
		//canvas.getDrawing().addElement(c2);
		//canvas.getDrawing().addElement(c3);

		//PicParallelogram para = new PicParallelogram(new PicPoint(0,0), new PicPoint(20,0), new PicPoint(40,50));
		//canvas.getDrawing().add(para);

		PicEllipse ell = new PicEllipse(new PicPoint(-30,-30), new PicPoint(30,-30), new PicPoint(30,30),PicEllipse.OPEN);
		ell.setAngleStart(0);ell.setAngleEnd(180);
		//canvas.getDrawing().add(ell);

		//PicParallelogram para = new PicParallelogram(new PicPoint(0,0), new PicPoint(10,0), new PicPoint(10,20));
		//canvas.getDrawing().add(para);

		PicMultiCurve curve = new PicMultiCurve(new PicPoint(10,-30), new PicPoint(40,-40),new PicPoint(40,-40), new PicPoint(30,-10));

		PicGroup gr = new PicGroup();
		gr.setCompoundMode(PicGroup.CompoundMode.JOINT);
		gr.add(curve);
		gr.add(ell);
		//gr.add(new PicMultiCurve(new PicPoint(20,20), new PicPoint(10,30)));
		gr.setAttribute(PicAttributeName.LINE_COLOR,Color.blue);
		gr.setAttribute(PicAttributeName.FILL_STYLE,FillStyle.SOLID);
		gr.setAttribute(PicAttributeName.FILL_COLOR,Color.green);

		canvas.getDrawing().add(gr);
		//canvas.select(gr, PECanvas.SelectionBehavior.REPLACE);

		/*
		PicText text = new PicText();
		text.setCtrlPt(PicText.P_ANCHOR, new PicPoint(50,50),null);
		text.setText("x=5+3*y");
		canvas.getDrawing().addElement(text);
		*/

		return pane;
	}

	///////////////////////////////////////////////////////////////////
	class MouseHandler extends PEMouseInputAdapter {

		public void mouseMoved(PEMouseEvent me){
			mouseCoordsLbl.setText(me.getPicPoint().toString());
		}

	}

	///////////////////////////////////////////////////////////////////
	class HelpMessageHandler implements HelpMessageListener {

		public void helpMessagePosted(HelpMessageEvent e){
			System.out.println("!!!!!!!!!! "+e.getMessage()+" !!!!!!!!!!");
		}
	}

	///////////////////////////////////////////////////////////////////
	class DrawingHandler implements DrawingListener {

		public void changedUpdate(DrawingEvent e){
			if(jpicedt.Log.DEBUG)
			{
				jpicedt.Log.debug("got DrawingEvent e = " + e);
			}
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
	class ZoomHandler implements ZoomListener {

		public void zoomUpdate(ZoomEvent e){
			if(jpicedt.Log.DEBUG)
				jpicedt.Log.debug("got ZoomEvent e = " + e);
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
