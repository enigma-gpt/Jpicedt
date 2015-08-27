// DrawToolFactory.java --- -*- coding: iso-8859-1 -*-
// February 25, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
//
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ÉNSÉA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: DrawToolFactory.java,v 1.44 2013/03/27 06:58:11 vincentb1 Exp $
// Keywords:
// X-URL: http://www.jpicedt.org/
//
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
package jpicedt.graphic.toolkit;

import jpicedt.JPicEdt;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.model.AbstractCurve;
import jpicedt.graphic.model.EditPointConstraint;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicAttributeName;
import jpicedt.graphic.model.PicCircleFrom3Points;
import jpicedt.graphic.model.PicEllipse;
import jpicedt.graphic.model.PicMultiCurve;
import jpicedt.graphic.model.PicParallelogram;
import jpicedt.graphic.model.PicSmoothPolygon;
import jpicedt.graphic.model.PicPsCurve;
import jpicedt.graphic.model.PicText;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.ui.dialog.UserConfirmationCache;
import jpicedt.graphic.view.View;

import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.GroupLayout;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;

import static jpicedt.Localizer.*;
import static jpicedt.Log.*;
import static jpicedt.graphic.PECanvas.SelectionBehavior.*;
import static jpicedt.graphic.model.EditPointConstraint.EditConstraint.*;

/**
 * A factory that produces mouse-tools for drawing (ie adding) graphical element's to a canvas.
 * @author Sylvain Reynal
 * @since jpicedt 1.3.2
 * @version $Id: DrawToolFactory.java,v 1.44 2013/03/27 06:58:11 vincentb1 Exp $
 */
public class DrawToolFactory {

	public static final String LINE = "action.draw.Line";
	public static final String RECTANGLE = "action.draw.Rectangle";
	public static final String PARALLELOGRAM = "action.draw.Parallelogram";
	public static final String ELLIPSE_FROM_PARALLELO = "action.draw.EllipseFromParallelo";
	public static final String ARC_CHORD_FROM_PARALLELO = "action.draw.ArcChordFromParallelo";
	public static final String ARC_PIE_FROM_PARALLELO = "action.draw.ArcPieFromParallelo";
	public static final String ARC_OPEN_FROM_PARALLELO = "action.draw.ArcOpenFromParallelo";
	public static final String ELLIPSE = "action.draw.Ellipse";
	public static final String ARC_CHORD = "action.draw.ArcChord";
	public static final String ARC_PIE = "action.draw.ArcPie";
	public static final String ARC_OPEN = "action.draw.ArcOpen";
	public static final String CIRCLE_FROM_3PTS = "action.draw.CircleFrom3Pts";
	public static final String ARC_CHORD_FROM_3PTS = "action.draw.ArcChordFrom3Pts";
	public static final String ARC_PIE_FROM_3PTS = "action.draw.ArcPieFrom3Pts";
	public static final String ARC_OPEN_FROM_3PTS = "action.draw.ArcOpenFrom3Pts";
	public static final String MULTI_CURVE = "action.draw.MultiCurve";
	public static final String CLOSED_MULTI_CURVE = "action.draw.ClosedMultiCurve";
	public static final String SMOOTH_POLY = "action.draw.SmoothPoly";
	public static final String CLOSED_SMOOTH_POLY = "action.draw.ClosedSmoothPoly";
	public static final String PS_CURVE = "action.draw.PsCurve";
	public static final String CLOSED_PS_CURVE = "action.draw.ClosedPsCurve";
	public static final String TEXT = "action.draw.Text";

	// add new tool name here :
	private static final String[][] ALL_TOOL_NAMES = new String[][]{
	            {TEXT},{LINE,RECTANGLE,PARALLELOGRAM,MULTI_CURVE,CLOSED_MULTI_CURVE},
	            {ELLIPSE,ARC_PIE,ARC_CHORD,ARC_OPEN},
	            {ELLIPSE_FROM_PARALLELO,ARC_PIE_FROM_PARALLELO,ARC_CHORD_FROM_PARALLELO,ARC_OPEN_FROM_PARALLELO},
	            {CIRCLE_FROM_3PTS,ARC_PIE_FROM_3PTS,ARC_CHORD_FROM_3PTS,ARC_OPEN_FROM_3PTS},
	            {SMOOTH_POLY,CLOSED_SMOOTH_POLY},{PS_CURVE,CLOSED_PS_CURVE}};

	private CursorFactory cursorFactory = new CursorFactory();
	private EditorKit kit;

	/**
	 * @param editorKit the EditorKit
	 */
	public DrawToolFactory(EditorKit editorKit){
		this.kit = editorKit;
	}

	/**
	 * Returns an array of arrays of String containing the name of each tool this factory can produce,
	 * sorted by groups of tightly related tools.<br>
	 * This may be useful to register tools with the hosting <code>EditorKit</code>, or to
	 * automate the building process for related <code>PEAction</code>'s
	 */
	public static String[][] getAvailableToolNames(){
		String[][] names = new String[ALL_TOOL_NAMES.length][];
		for (int i=0; i<names.length; i++){
			names[i] = new String[ALL_TOOL_NAMES[i].length];
			for (int j=0; j<names[i].length; j++){
				names[i][j] = ALL_TOOL_NAMES[i][j].toString();
			}
		}
		return names;
	}

	/**
	 * Returns a <code>MouseTool</code> of the given type.
	 * @param type one of LINE, RECTANGLE,&hellip; constant fields.
	 * @since jpicedt 1.3.2
	 */
	public MouseTool createDrawTool(String type){

		/* How to design a new XXX_ITERATOR ?
		First, it's to be understood that this scheme only works for Element's that have a finite number of
		control-points (ie PicSmoothPolygon and PicMultiCurve don't fit here).
		The XXX_ITERATOR is an array of arrays of control-point indices that is fed to the GenericDrawTool
		contructor, so that the latter knows - at each cycle step - which points it has to update when the user drags the mouse.
		Let's take RECTANGLE_ITERATOR for example :

		int[][] RECTANGLE_ITERATOR = {{PicParallelogram.P_BL,PicParallelogram.P_TR},{PicParallelogram.P_TR}};

		This leads to the following cycle :
		- first mouse-press : sets P_BL and P_TR (all at once) to the current mouse position.
		- mouse-move: moves P_TR
		- second mouse-press : sets P_TR to its final value
		*/
		final int[][] LINE_ITERATOR = {{0,3},{3}}; // actually uses PicMultiCurve
		final int[][] PARALLELOGRAM_ITERATOR = {{PicParallelogram.P_BL,PicParallelogram.P_TR},{PicParallelogram.SIDE_R},{PicParallelogram.SIDE_T}};
		final int[][] RECTANGLE_ITERATOR = {{PicParallelogram.P_BL,PicParallelogram.P_TR},{PicParallelogram.P_TR}};
		final int[][] ARC_FROM_PARALLELO_ITERATOR = {{PicParallelogram.P_BL,PicParallelogram.P_TR},{PicParallelogram.SIDE_R},{PicParallelogram.SIDE_T},{PicEllipse.P_ANGLE_END},{PicEllipse.P_ANGLE_START}};
		final int[][] ARC_ITERATOR = {{PicParallelogram.P_BL,PicParallelogram.P_TR},{PicParallelogram.P_TR},{PicEllipse.P_ANGLE_END},{PicEllipse.P_ANGLE_START}};
		final int[][] _3PTS_ITERATOR = {{PicCircleFrom3Points.P_1,PicCircleFrom3Points.P_2,PicCircleFrom3Points.P_3},{PicCircleFrom3Points.P_3},{PicCircleFrom3Points.P_2}};

		if (type==RECTANGLE) return new GenericDrawTool(new PicParallelogram(), DEFAULT, RECTANGLE_ITERATOR);
		if (type==PARALLELOGRAM) return new GenericDrawTool(new PicParallelogram(), DEFAULT, PARALLELOGRAM_ITERATOR);

		if (type==ELLIPSE) return new GenericDrawTool(new PicEllipse(), DEFAULT,RECTANGLE_ITERATOR);
		if (type==ARC_PIE) return new GenericDrawTool(new PicEllipse(Arc2D.PIE), DEFAULT, ARC_ITERATOR);
		if (type==ARC_OPEN) return new GenericDrawTool(new PicEllipse(Arc2D.OPEN), DEFAULT, ARC_ITERATOR);
		if (type==ARC_CHORD) return new GenericDrawTool(new PicEllipse(Arc2D.CHORD), DEFAULT, ARC_ITERATOR);

		if (type==ELLIPSE_FROM_PARALLELO) return new GenericDrawTool(new PicEllipse(), DEFAULT, PARALLELOGRAM_ITERATOR);
		if (type==ARC_PIE_FROM_PARALLELO) return new GenericDrawTool(new PicEllipse(Arc2D.PIE), DEFAULT, ARC_FROM_PARALLELO_ITERATOR);
		if (type==ARC_CHORD_FROM_PARALLELO) return new GenericDrawTool(new PicEllipse(Arc2D.CHORD), DEFAULT, ARC_FROM_PARALLELO_ITERATOR);
		if (type==ARC_OPEN_FROM_PARALLELO) return new GenericDrawTool(new PicEllipse(Arc2D.OPEN), DEFAULT, ARC_FROM_PARALLELO_ITERATOR);

		if (type==CIRCLE_FROM_3PTS) return new GenericDrawTool(new PicCircleFrom3Points(), DEFAULT, _3PTS_ITERATOR);
		if (type==ARC_OPEN_FROM_3PTS) return new GenericDrawTool(new PicCircleFrom3Points(false,Arc2D.OPEN),null, _3PTS_ITERATOR);
		if (type==ARC_PIE_FROM_3PTS) return new GenericDrawTool(new PicCircleFrom3Points(false,Arc2D.PIE),null, _3PTS_ITERATOR);
		if (type==ARC_CHORD_FROM_3PTS) return new GenericDrawTool(new PicCircleFrom3Points(false,Arc2D.CHORD),null, _3PTS_ITERATOR);

		if (type==LINE) return new GenericDrawTool(new PicMultiCurve(new PicPoint(),new PicPoint()),SMOOTHNESS, LINE_ITERATOR);
		if (type==MULTI_CURVE) return new AbstractCurveDrawTool(new PicMultiCurve(false));
		if (type==CLOSED_MULTI_CURVE) return new AbstractCurveDrawTool(new PicMultiCurve(true));
		if (type==SMOOTH_POLY) return new AbstractCurveDrawTool(new PicSmoothPolygon(false));
		if (type==CLOSED_SMOOTH_POLY) return new AbstractCurveDrawTool(new PicSmoothPolygon(true));
		if (type==PS_CURVE) return new AbstractCurveDrawTool(new PicPsCurve(false));
		if (type==CLOSED_PS_CURVE) return new AbstractCurveDrawTool(new PicPsCurve(true));

		if (type==TEXT) return new TextDrawTool(new PicText());
		return null;
	}

	/////////////////////////////////////////////////////////////////////////////
	//// GenericDrawTool
	/////////////////////////////////////////////////////////////////////////////

	/**
	 * A <code>MouseTool</code> aimed at drawing new <code>Element</code>s. It works like an iterator, by
	 * iterating (cyclically, creating a new Element at each cycle) through a given array of point indexes, or
	 * (if the latter are not provided in the constructor) by computing it from the available control points
	 * for the given element.<p>
	 * <code>GenericDrawTool</code> targets Element having a finite number of control points ONLY !
	 * @author Sylvain Reynal
	 */
	protected class GenericDrawTool extends MouseTool {

		private Element prototype, currentElement;
		private BasicEditPointConstraint editPtConstraint = new BasicEditPointConstraint(DEFAULT);
		private EditPointConstraint.EditConstraint defaultConstraint;
		private int currentTaskIndex; // indexes the current draw-task, gets increased at each mouse-press event
		private int[][] drawPoints; // array of arrays of indices of points to be moved at each draw-task ;
		//  e.g. drawPoints[2] contains points to be moved during 3rd draw-task, ie after two mouse-clicks.
		/* buffer */
		private PicPoint ptBuffer = new PicPoint();
		private PicPoint ptBuffer1 = new PicPoint();
		// set to true as soon as all points have been set
		private boolean isCompleted;
		// help message to be posted by the hosting EditorKit (this may vary according to key modifiers)
		private String helpMsg;
		private String helpMsgDEFAULT="help-message.Draw";

		/**
		 * @param prototype the <code>Element</code> that will serve as the prototype (getting cloned, then
		 *        added to the drawing).
		 * @param constraint which constraint to use when moving control-points
		 * @param drawPoints an array of array of point indexes which the iterator will iterate through (in
		 *        ascending order) to draw the element (at each iteration, the corresponding list of points
		 *        will be updated together).
		 * @see jpicedt.graphic.model.Element#setCtrlPt
		 */
		public GenericDrawTool(Element prototype, EditPointConstraint.EditConstraint constraint, int[][] drawPoints){
			this.prototype = prototype;
			this.defaultConstraint = constraint; // because constraint can be modified during the invokation of mouseDragged()
			editPtConstraint.setEditConstraint(constraint);
			this.drawPoints = drawPoints;
			isCompleted = true; // mark has completed -> force (re)start on next mousePressed event
			helpMsg = helpMsgDEFAULT;
		}

		/**
		 * The <code>drawPoints</code> array gets computed
		 * from all available control-points for the given element.
		 * @param prototype the <code>Element</code> that will serve as the prototype (getting cloned, then
		 *        added to the drawing).
		 * @param constraint Point edition constraint to be fulfilled during edition
		 */
		public GenericDrawTool(Element prototype,  EditPointConstraint.EditConstraint constraint){
			this.prototype = prototype;
			this.editPtConstraint.setEditConstraint(constraint);
			this.defaultConstraint = constraint;
			// there are as many "drawPoints" indices as control-points for this element :
			drawPoints = new int[prototype.getLastPointIndex()-prototype.getFirstPointIndex()+1][];
			drawPoints[0] = new int[prototype.getLastPointIndex()-prototype.getFirstPointIndex()+1];
			int i,j;
			// 1) create a set of control-point indices to be dragged immediately after the first mousePressed occured :
			// (our policy is just to move ALL control-points at once)
			for (i=prototype.getFirstPointIndex(), j=0; i<prototype.getLastPointIndex();i++,j++){
				drawPoints[0][j] = i; // include ALL indices here
			}
			// 2) then, at each subsequent cycle step, we'll simply move a different control-point, starting from
			//    the second available one (the first control-point was set in the previous step)
			//    until all control-points have been properly dragged.
			//    Hence : a) the array is reduced to a single element b) this element
			//    is an int whose value is the very control-point index to be dragged.
			for (i=prototype.getFirstPointIndex()+1, j=0; i<prototype.getLastPointIndex();i++,j++){
				drawPoints[j]=new int[1]; // there's only one index
				drawPoints[j][0] = i; // and it's the very index of the control-point to be dragged
			}
			//??? je comprends pas a quoi ca sert ce drawpoints calcule, c'est le truc par defaut, c'est ca ? [SR] oui
			isCompleted = true; // mark has completed -> force (re)start on next mousePressed event
			helpMsg = helpMsgDEFAULT;
		}

		/**
		 * A popup-trigger mouse event (e.g. a right click on Unix/Windows) :<ul>
		 * <li>switches back to <code>SELECT_MODE</code> if the task-iterator is completed</li>
		 * <li>or cancels the current task if the task-iterator is underway.</li></ul>
		 * A left- or middle-button SINGLE click either :<ul>
		 * <li>(re)start the task iterator if it was completed</li>
		 * <li>or select the next point if there are more points</li>
		 * <li>or terminate the drawing process if there are no more points.</li></ul>
		 * <b>author:</b> Sylvain Reynal
		 * @since jPicEdt
		 */
		public void mousePressed(PEMouseEvent e) {
			super.mousePressed(e);
			// right button pressed
			if (e.isPopupTrigger()) {
				// either : back to SELECT_MODE
				if (isCompleted){
					if(kit.getCurrentMouseToolType() == MouseTool.MouseToolType.DRAWING_MOUSE_TOOL)
						kit.setCurrentMouseTool(EditorKit.SELECT); // calls back flush()
				}
				// or : cancel last draw-operation
				else { // ARCS only !
					if (currentElement instanceof PicEllipse){
						PicEllipse ellipse = (PicEllipse)currentElement;
						if (!ellipse.isPlain()){ // it's an arc -> cycle through arc types
							int closure = ellipse.getArcType();
							switch (closure){
							case PicEllipse.OPEN:
								ellipse.setArcType(PicEllipse.PIE);
								break;
							case PicEllipse.PIE:
								ellipse.setArcType(PicEllipse.CHORD);
								break;
							case PicEllipse.CHORD:
								ellipse.setArcType(PicEllipse.OPEN);
								break;
							default:
							}
						}
					}
				}
			}
			// left button pressed :
			else if (e.isLeftButton()) {
				e.getCanvas().getGrid().nearestNeighbour(e.getPicPoint(),ptBuffer);
				// restart tool :
				if (isCompleted) {
					isCompleted = false;
					e.getCanvas().unSelectAll();
					// fill ptBuffer with nearest-neighbour on grid :
					e.getCanvas().getGrid().nearestNeighbour(e.getPicPoint(),ptBuffer);
					// instanciates a new Element from the prototype :
					currentElement = (Element)prototype.clone();
					currentElement.setAttributeSet(kit.getInputAttributes());
					// init ALL object's points at once by giving them the value of the current clicked point :
					currentTaskIndex = 0;
					for (int i=0; i<(drawPoints[currentTaskIndex]).length;i++)
						currentElement.setCtrlPt(drawPoints[currentTaskIndex][i],ptBuffer,editPtConstraint);

					e.getCanvas().beginUndoableUpdate(localize("action.editorkit.Draw") +" ("+prototype.getName()+")");
					e.getCanvas().getDrawing().add(currentElement);
				}
				// end ?
				if (currentTaskIndex >= drawPoints.length-1){
					setCurrentPoint(e);
					isCompleted = true;
					editPtConstraint.setEditConstraint(defaultConstraint); // in case constraint has been modified by mouseDragged()
					e.getCanvas().endUndoableUpdate();
					e.getCanvas().select(currentElement,REPLACE); // replace selection
					// [pending] fire undo event
				}
				// next task
				else {
					currentTaskIndex++;
					setCurrentPoint(e);
				}
			}
		}

		/** set current point */
		public void mouseDragged(PEMouseEvent e) {
			super.mouseDragged(e);
			if (e.isPopupTrigger())  return; // right button -> do nothing :
			setCurrentPoint(e);
		}

		/** set cursor for canvas, then call mouseDragged */
		public void mouseMoved(PEMouseEvent e){
			super.mouseMoved(e);
			e.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.DRAW));
			updateHelpMessage(e);
			kit.postHelpMessage(helpMsg);
			mouseDragged(e);
		}

		/**
		 * Updates the value of the current Help-Message to be posted by the hosting <code>EditorKit</code>,
		 * according to current key modifiers. This aims at mimicking the behavior in
		 * <code>EditElementMouseTransformFactory</code>, where modifiers-dependent messages are posted in
		 * real-time.
		 */
		private void updateHelpMessage(PEMouseEvent e){
			helpMsg = helpMsgDEFAULT;
			if (currentElement instanceof PicEllipse){
				PicEllipse ellipse = (PicEllipse)currentElement;
				if (!ellipse.isPlain()){ // it's an arc -> cycle through arc types (this is valid only after the surrounding parallelogram has been set)
					helpMsg = "help-message.DrawArc";
					return;
				}
			}
			// --- parallelograms and ellipses ---
			if (currentElement instanceof PicParallelogram && !(currentElement instanceof PicCircleFrom3Points)){
				if (e.isControlDown() && e.isAltDown()) {
					if (e.isShiftDown()) {
						if (currentElement instanceof PicEllipse)  helpMsg = "help-message.EllipseCircle";
						else helpMsg = "help-message.ParalleloSquare";
					}
					else helpMsg = "help-message.MovePointCenterFixed";
				}
			}
		}

		/**
		 * move the points indexed in drawPoints[currentTaskIndex] to the current click-point
		 * (possibly after grid alignment).
		 */
		public void setCurrentPoint(PEMouseEvent e){
			if (isCompleted) return;
			e.getCanvas().getGrid().nearestNeighbour(e.getPicPoint(),ptBuffer);
			if (ptBuffer.equals(currentElement.getCtrlPt(drawPoints[currentTaskIndex][0],ptBuffer1))) return;
			if (currentElement instanceof PicParallelogram && !(currentElement instanceof PicCircleFrom3Points)){
				if (e.isControlDown() && e.isAltDown()) {
					if (e.isShiftDown()) editPtConstraint.setEditConstraint(SQUARE); // same as in EditElementMouseTransformFactory
					else editPtConstraint.setEditConstraint(CENTER_FIXED);
				}
				else editPtConstraint.setEditConstraint(DEFAULT);
			}
			for (int i=0; i<(drawPoints[currentTaskIndex]).length;i++)
				currentElement.setCtrlPt(drawPoints[currentTaskIndex][i],ptBuffer,editPtConstraint);
		}

		/**
		 * called when this tool is being activated in the hosting editor kit
		 */
		public void init(){
			kit.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.DRAW));
			editPtConstraint.setUserConfirmationCache(new UserConfirmationCache(JPicEdt.getMDIManager()));
		}

		/** called when this tool is being replaced by another mouse-tool in the hosting
		 * editor kit ; this is mainly for mousetools using more than one sequence, for it
		 * gives them a chance to clean themselves up for the next time */
		public void flush(){
			isCompleted = true;
			currentElement=null;
		}
		/**
		 * This method is called by the hosting EditorKit : this implementation paints
		 * the current element's highlighter.
		 */
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){
			if (currentElement==null || currentElement.getView()==null) return;
			g.setPaint(Color.blue);
			View v = currentElement.getView();
			if (v!=null) v.paintHighlighter(g, allocation, scale);
		}
	} // GenericDrawTool


	/**
	 * a <code>MouseTool</code> suited for drawing any element inheriting from AbstractCurve.  Because of
	 * slight differences that appear in the drawing sequence, this class currently specializes on
	 * <code>PicMultiCurve</code>, <code>PicPsCurve</code> and <code>PicSmoothPolygon</code>. This should be
	 * improved in the near future by providing a common interface [SR:pending].
	 * <p> The drawing sequence is as follows:
	 * <ul><li>OPEN curve : mpress add 0,1,2,3&rarr;drag 1&rarr;move 3&rarr;mpress add 4,5,6&rarr;drag
	 * 4&rarr;move 6&rarr;&hellip;</li>
	 * <li>CLOSED curve : mpress add 0,1,2&rarr;drag 1&rarr;mrel add 3,4,5&rarr;move 3&rarr;mpress&rarr;drag
	 * 4&rarr;mrel add 6,7,8&rarr;move
	 * and is terminated by a right-button click.</li></ul>
	 * <b>author:</b> Vincent Guirardel, Sylvain Reynal
	 */
	protected class AbstractCurveDrawTool extends MouseTool {

		private AbstractCurve currentElement, prototype;
		/* index of current "moved" or "dragged" control-points ; index is being updated during each call to mousePressed()/mouseReleased()
		 * according to the sequence indicated in documentation above */
		private int currentDrawPointIndex;
		/* buffers */
		private PicPoint ptBuffer1 = new PicPoint(), ptBuffer2 = new PicPoint();
		private boolean isCompleted;
		private boolean isLastControlPoint; // true as soon as a right-click occurs -> drag control-point associated with last end-point
		// [SR:pending] the following may be made CTRL+ALT+SHIFT dependent in the near future...
		private BasicEditPointConstraint editPtConstraint = new BasicEditPointConstraint(DEFAULT);
		// help message to be posted by the hosting EditorKit (this may vary according to key modifiers)
		private String helpMsg;
		private String helpMsgDEFAULT="help-message.Draw";

		/**
		 * @param prototype Prototype which will be cloned, then added to the drawing.
		 */
		public AbstractCurveDrawTool(AbstractCurve prototype){
			if (DEBUG) debug("Abstract Curve constructor. Prototype : "+prototype.toString());
			isCompleted = true; // mark has completed -> force start to be called
			isLastControlPoint = false;
			this.prototype = prototype;
			if (prototype instanceof PicMultiCurve)  editPtConstraint.setEditConstraint(SMOOTHNESS_SYMMETRY);
			else editPtConstraint.setEditConstraint(DEFAULT);
			helpMsg = helpMsgDEFAULT;
		}

		/**
		 * Update the current EditPointConstraint from the key modifiers associated with the given mouse event.
		 * As a by-product, the current help message (to be posted by the hosting EditorKit) get also updated.
		 */
		private void updateEditPointConstraint(PEMouseEvent me){
			this.helpMsg  = helpMsgDEFAULT;
			if (!(prototype instanceof PicMultiCurve)) this.editPtConstraint.setEditConstraint(DEFAULT);
			if (me.isShiftDown() && me.isControlDown() && !me.isAltDown()){ // SHIFT+CTRL
				this.editPtConstraint.setEditConstraint(SMOOTHNESS);
				this.helpMsg ="help-message.ControlSmooth";
			}
			else if (!me.isShiftDown() && me.isControlDown() && me.isAltDown()){ //CTRL+ALT
				this.editPtConstraint.setEditConstraint(SYMMETRY);
				this.helpMsg="help-message.ControlSymmetric";
			}
			else if (me.isShiftDown() && me.isControlDown() && me.isAltDown()){ // CTRL+ALT+SHIFT
				this.editPtConstraint.setEditConstraint(FREELY);
				this.helpMsg="help-message.ControlFreely";
			}
			else {
				this.editPtConstraint.setEditConstraint(SMOOTHNESS_SYMMETRY); // default (=CTRL in EditElementMouseTransformFactory)
				this.helpMsg = "help-message.ControlSmoothAndSymmetric";
			}
		}

		private void markCompleted(PEMouseEvent e){
			isCompleted = true;
			isLastControlPoint = false;
			e.getCanvas().endUndoableUpdate();
			e.getCanvas().select(currentElement,REPLACE); // select element, replacing old selection
			currentElement=null;
			if (prototype instanceof PicMultiCurve)  editPtConstraint.setEditConstraint(SMOOTHNESS_SYMMETRY);
			else editPtConstraint.setEditConstraint(DEFAULT);
		}

		/**
		 * A popup-trigger mouse event (e.g. a right click on Unix/Windows):
		 * <ul><li> switches back to SELECT_MODE if the iterator is completed
		 * <li> or terminates the drawing process.
		 *</ul>
		 * A left-button SINGLE click either:
		 * <ul><li> (re)start the task iterator if completed;
		 * <li> or add a new point;</ul>
		 */
		public void mousePressed(PEMouseEvent e) {
			super.mousePressed(e);
			e.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.DRAW));

			// 		    if (DEBUG) debug("Current element2 : "+currentElement.toString());

			// right button pressed:
			if (e.isPopupTrigger()) {
				// either : back to SELECT_MODE
				if (isCompleted) kit.setCurrentMouseTool(EditorKit.SELECT);
				else { // or next move will drag last control-point
					if (currentElement instanceof PicSmoothPolygon){
						markCompleted(e);
						return;
					}
					if (currentElement instanceof PicPsCurve){
						if (currentElement.isClosed()){
							markCompleted(e); // no additional control point in a closed pscurve, so we are done
							return;
						}
						// yes, we're open !
						if (isLastControlPoint==false){
							isLastControlPoint=true; // next move drag control-point associated with last end-point
							currentDrawPointIndex= currentElement.getSegmentCount()+2; // index of last control point
							return;
						}
						else {
							markCompleted(e);
							return;
						}
					}
					// otherwise it's a multicurve
					int nbSeg = currentElement.getSegmentCount();
					boolean isStraightLastSegment = currentElement.isStraight(nbSeg-1);
					if (isStraightLastSegment==false && isLastControlPoint==false){
						isLastControlPoint=true; // next move drag control-point associated with last end-point
						currentDrawPointIndex=currentElement.getBezierPtsCount()-2; // now index of last control-point
					}
					else { // mark process as completed
						markCompleted(e);
						return;
					}
				}
			}

			// left button pressed:
			else {
				e.getCanvas().getGrid().nearestNeighbour(e.getPicPoint(),ptBuffer2);
				if (isLastControlPoint){ // previous move dragged last control-point => now complete drawing
					markCompleted(e);
					return;
				}
				else if (isCompleted) { // restart tool ; first mpress adds first segment
					e.getCanvas().unSelectAll(); // unselect all
					isCompleted = false;
					isLastControlPoint = false;
					currentElement = (AbstractCurve)prototype.clone(); // instanciates a new Element from the prototype
					currentElement.setAttributeSet(kit.getInputAttributes());
					currentElement.addPoint(ptBuffer2); // add first point for OPEN curve, first three points for CLOSED curve
					currentDrawPointIndex = 0;
					e.getCanvas().beginUndoableUpdate(localize("action.editorkit.Draw") +" ("+prototype.getName()+")");
					e.getCanvas().getDrawing().add(currentElement);
				}
				currentDrawPointIndex ++; // move to next subdivision-point (multicurve) or next point (smoothpoly, closed pscurve)
				if (currentElement instanceof PicSmoothPolygon
				        || (currentElement instanceof PicMultiCurve && !currentElement.isClosed())
				        || (currentElement instanceof PicPsCurve && currentElement.isClosed()))
					currentElement.addPoint(ptBuffer2);
				// closed PicMultiCurve : this is being done in mouseReleased (see sequence in this class documentation)
				if (currentElement instanceof PicPsCurve && !currentElement.isClosed()) {
					if (currentDrawPointIndex==1){
						// we need 3 points for open pscurve: 2 control points + one non-control point
						currentElement.addPoint(ptBuffer2);// 1st and only non-control point
						currentElement.addPoint(ptBuffer2);// last control point
						currentDrawPointIndex=0 ; //dragging the mouse should change initial control point (index 0)
					}
					else {
						currentElement.addPoint(ptBuffer2);// adds a point. This is the final control point.
						currentElement.setCtrlPt(currentDrawPointIndex,ptBuffer2);// sets current point (=former final control point)
					}
				}
			}
		}

		/** CLOSED curve only : add a new point by splitting last segment */
		public void mouseReleased(PEMouseEvent e){
			super.mouseReleased(e);
			if (e.isLeftButton() && !isCompleted && currentElement instanceof PicMultiCurve){
				if (currentElement.isClosed()){
					e.getCanvas().getGrid().nearestNeighbour(e.getPicPoint(),ptBuffer2);
					currentElement.addPoint(ptBuffer2);
					currentDrawPointIndex +=2;
				}
				else {
					currentDrawPointIndex +=2; // addPoint() is being done is mousePressed()
				}
			}
			if (e.isLeftButton() && !isCompleted && currentElement instanceof PicPsCurve
			        && !currentElement.isClosed() && currentDrawPointIndex==0){
				currentDrawPointIndex =2; // next time, we'll go to next (non-control) point (index 2).
				currentElement.addPoint(ptBuffer2);//point with index 3
				currentElement.setCtrlPt(2,ptBuffer2);

			}

		}

		/** set the current point */
		public void mouseDragged(PEMouseEvent e) {
			if (DEBUG) debug("Dragged. currentDrawPointIndex : "+currentDrawPointIndex);
			super.mouseDragged(e);
			if (e.isPopupTrigger() || isCompleted)  return; // right button -> do nothing :
			updateEditPointConstraint(e);
			e.getCanvas().getGrid().nearestNeighbour(e.getPicPoint(),ptBuffer2);
			if (ptBuffer2.equals(currentElement.getCtrlPt(currentDrawPointIndex,ptBuffer1))) return;
			currentElement.setCtrlPt(currentDrawPointIndex,ptBuffer2,editPtConstraint); // set control-point
			if (currentElement instanceof PicPsCurve && !currentElement.isClosed() && currentDrawPointIndex !=0 && !isLastControlPoint)
				currentElement.setCtrlPt(currentDrawPointIndex+1,ptBuffer2,editPtConstraint); // we also need to set the final control-point
		}

		/** set cursor for canvas, then call mouseDragged */
		public void mouseMoved(PEMouseEvent e){
			super.mouseMoved(e);
			e.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.DRAW));
			mouseDragged(e); // also update helpMsg
			kit.postHelpMessage(helpMsg);
		}

		/**
		 * called when this tool is being activated in the hosting editor kit
		 */
		public void init(){
			kit.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.DRAW));
			editPtConstraint.setUserConfirmationCache(new UserConfirmationCache(JPicEdt.getMDIManager()));
		}

		/** called when this tool is being replaced by another mouse-tool in the hosting
		 * editor kit ; this is mainly for mousetools using more than one sequence, for it
		 * gives them a chance to clean themselves up for the next time */
		public void flush(){
			isCompleted = true;
			isLastControlPoint = false;
			currentElement=null;
		}
		/**
		 * This method is called by the hosting EditorKit : this implementation paints
		 * the current element's highlighter.
		 */
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){
			if (currentElement==null || currentElement.getView()==null) return;
			g.setPaint(Color.blue);
			View v = currentElement.getView();
			if (v != null) v.paintHighlighter(g, allocation, scale);
		}
	}


	/////////////////////////////////////////////////////////////////////////////
	//// TextDrawTool
	/////////////////////////////////////////////////////////////////////////////

	/**
	 * A tool used to place PicText's ; it opens a dialog box for editing text content.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	protected class TextDrawTool extends MouseTool {

		private PicText prototype, currentElement;
		/* buffer */
		private PicPoint ptBuffer = new PicPoint();
		private PicPoint ptBuffer1 = new PicPoint();
		private boolean shift = false;


		/**
		 * @param prototype the Element that will serve as the prototype
		 * (getting cloned, then added to the drawing).
		 */
		public TextDrawTool(PicText prototype){
			this.prototype = prototype;
		}

		/**
		 * A popup-trigger mouse event (e.g. a right click on Unix/Windows):
		 * <ul>
		 * <li>switches back to SELECT_MODE if the task-iterator is completed</li>
		 * <li>or cancels the current task if the task-iterator is underway.</li>
		 *</ul>
		 * A left- or middle-button SINGLE click either:
		 *<ul>
		 * <li>(re)start the task iterator if it was completed</li>
		 * <li>or select the next point if there are more points</li>
		 * <li>or terminate the drawing process if there are no more points.</li>
		 *</ul>
		 * <br><b>author:</b> Sylvain Reynal
		 * @since jPicEdt
		 */
		public void mousePressed(PEMouseEvent e) {
			super.mousePressed(e);

			// right button pressed
			if (e.isPopupTrigger()) {
				kit.setCurrentMouseTool(EditorKit.SELECT); // calls back flush()
			}
			// left button pressed :
			else {
				e.getCanvas().getGrid().nearestNeighbour(e.getPicPoint(),ptBuffer);
				// instanciates a new Element from the prototype
				currentElement = (PicText)prototype.clone();
				currentElement.setAttributeSet(kit.getInputAttributes());
				currentElement.setCtrlPt(PicText.P_ANCHOR,ptBuffer,null); // no constraint

				if(!currentElement.getAreaSet())
				{
					if(shift)
						currentElement.getAttributeSet().setAttribute(PicAttributeName.TEXT_MODE,
								PicText.TextMode.TEXT_AREA);
					else
						currentElement.getAttributeSet().setAttribute(PicAttributeName.TEXT_MODE,
								PicText.TextMode.TEXT_FIELD);
				}
				// open dialog box for text editing :
				DialogFactory df = kit.getDialogFactory();
				/* [pending: august 2006] Pending modal internal dialog...
				AbstractCustomizer pane = ((CustomizerFactory)currentElement).createCustomizer();
				CustomizerDialog dlg = df.createCustomizerDialog(pane,CustomizerDialog.OK_BUTTON);
				dlg.setOkButtonClosesDialog(true);
				dlg.setVisible(true);
				*/
				String text;
				if(shift)
				{
					TextInputDialog sp = new TextInputDialog();
					text = sp.getText();
				}
				else
				{
					text = df.showInputDialog(localize("action.editorkit.TextEnter"),
											  prototype.getName(), javax.swing.JOptionPane.QUESTION_MESSAGE);
				}
				if (text != null){
					e.getCanvas().beginUndoableUpdate(localize("action.editorkit.Draw") +" ("+prototype.getName()+")");
					currentElement.setText(text);
					currentElement.setAttribute(PicAttributeName.TEXT_ICON, PicText.TextIcon.TEXT_MODE);
					e.getCanvas().getDrawing().add(currentElement);
					e.getCanvas().endUndoableUpdate();
					e.getCanvas().select(currentElement,REPLACE); // replace selection
				}
			}
			shift = false;
		}

		/** do nothing */
		public void mouseDragged(PEMouseEvent e) {
			super.mouseDragged(e);
			if (e.isPopupTrigger())  return; // right button -> do nothing :
		}

		/** set cursor for canvas, then call mouseDragged */
		public void mouseMoved(PEMouseEvent e){
			super.mouseMoved(e);
			e.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.DRAW));
			kit.postHelpMessage("help-message.Draw");
			mouseDragged(e);
		}

		/**
		 * called when this tool is being activated in the hosting editor kit
		 */
		public void init(){
			kit.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.DRAW));
		}

		/** called when this tool is being replaced by another mouse-tool in the hosting
		 * editor kit ; this is mainly for mousetools using more than one sequence, for it
		 * gives them a chance to clean themselves up for the next time */
		public void flush(){
		}
		public void keyPressed(KeyEvent e){
            System.err.println("key " + e.getKeyCode() + " is pressed!");
			if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
				shift = true;
                System.err.println("It's the shift key");
            }
		}
		public void keyReleased(KeyEvent e){
            System.err.println("key " + e.getKeyCode() + " is released!");
			if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
				shift = false;
                System.err.println("It's the shift key");
            }
		}
		public void keyTyped(KeyEvent e){
		}
	} // TextDrawTool

	/**
	 * A special window for textinput in a textarea
	 */
	public class TextInputDialog extends JDialog {
		private String text;
		private JTextArea jTextArea1;

		public TextInputDialog()
		{
			setDefaultLookAndFeelDecorated(true);
			setModal(true);
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);


			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			JLabel jLabel1 = new JLabel();
		     	JScrollPane jScrollPane1 = new JScrollPane();
		        jTextArea1 = new JTextArea();
		        JButton jButton1 = new JButton();
			JButton jButton2 = new JButton();

			jButton2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					TextInputDialog.this.setVisible(false);
					TextInputDialog.this.dispose();
				}
			});

			jButton1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					TextInputDialog.this.setVisible(false);
					TextInputDialog.this.dispose();
					text = jTextArea1.getText();
				}
			});



			jLabel1.setText(localize("action.editorkit.TextEnter"));

			jTextArea1.setColumns(20);
			jTextArea1.setRows(6);

			jScrollPane1.setViewportView(jTextArea1);
			jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPane1.setAutoscrolls(true);

			jButton1.setText(localize("button.OK"));

			jButton2.setText(localize("button.Cancel"));


			GroupLayout layout = new GroupLayout(this.getContentPane());
			this.getContentPane().setLayout(layout);

			layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
									.addGroup(layout.createSequentialGroup()
											  .addContainerGap()
											  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel1)))
									.addGroup(layout.createSequentialGroup()
											  .addGap(96, 96, 96)
											  .addComponent(jButton1)
											  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											  .addComponent(jButton2)))
						  .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		        );
			layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						  .addContainerGap()
						  .addComponent(jLabel1)
						  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						  .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
									.addComponent(jButton2)
									.addComponent(jButton1))
						  .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		        );
			pack();
			setLocation((int)(screenSize.getWidth()/2-this.getSize().getWidth()/2), (int)(screenSize.getHeight()/2-this.getSize().getHeight()/2));
			setTitle("Text");
			setResizable(false);
			setVisible(true);
			setDefaultLookAndFeelDecorated(true);
		}

		public String getText(){
			return text;
		}
	}

} // class DrawToolFactory
