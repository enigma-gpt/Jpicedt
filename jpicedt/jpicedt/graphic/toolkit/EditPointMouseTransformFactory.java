// EditPointMouseTransformFactory.java --- -*- coding: iso-8859-1 -*-
// February 28, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: EditPointMouseTransformFactory.java,v 1.28 2013/03/27 06:57:51 vincentb1 Exp $
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

import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.SelectionHandler;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.grid.Grid;
import jpicedt.graphic.model.AbstractCurve;
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.EditPointConstraint;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicCircleFrom3Points;
import jpicedt.graphic.model.PicEllipse;
import jpicedt.graphic.model.PicMultiCurve;
import jpicedt.graphic.model.PicMultiCurveConvertable;
import jpicedt.graphic.model.PicParallelogram;
import jpicedt.graphic.model.PicPsCurve;
import jpicedt.graphic.model.PicSmoothPolygon;
import jpicedt.graphic.view.HitInfo;
import jpicedt.graphic.view.View;
import jpicedt.graphic.view.highlighter.CompositeHighlighter;
import jpicedt.graphic.view.highlighter.DefaultHighlighterFactory;
import jpicedt.ui.dialog.UserConfirmationCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Comparator;
import java.util.TreeSet;

import static jpicedt.graphic.view.highlighter.CompositeHighlighter.*;
import static jpicedt.graphic.view.ViewConstants.*;
import static jpicedt.graphic.PECanvas.SelectionBehavior.*;
import static jpicedt.Log.*;
import static jpicedt.Localizer.*;
//import static jpicedt.graphic.model.EditPointConstraint.EditConstraint.*;


/**
 * A factory that produces MouseTransform's that may be plugged into the SelectionTool mouse-tool.
 * MouseTransform's created by this factory are dedicated to editing points of Elements
 * which either support a variable number of points (e.g. <code>AbstractCurve</code> and subclasses),
 * and/or whose point possess specific geometric feature (e.g. smoothness/symmetry for PicMultiCurve,
 * smoothness coefficient for PicSmoothPolygon,...).
 * So far, only AbstractCurve's are supported by the current implementation.
 * @author Sylvain Reynal
 * @since jPicEdt 1.4
 * @version $Id: EditPointMouseTransformFactory.java,v 1.28 2013/03/27 06:57:51 vincentb1 Exp $
 */
public class EditPointMouseTransformFactory extends AbstractMouseTransformFactory {

	private CursorFactory cursorFactory=new CursorFactory();

	// an array containing a list of selected points for the current selected Element, if applicable (ie
	// if this Element is an instance of an AbstractCurve).
	private SelectedPointsHandler selectedPointsHandler = new SelectedPointsHandler();

	// caches used by paint() to reduce object creation
	private double scale=0.0;
	private Rectangle2D.Double circle = new Rectangle2D.Double();
	private BasicStroke lineStroke = new BasicStroke(1.0f);
	private PicPoint ptBuffer = new PicPoint();

	private HighlightingMode oldSelHandlerHighlightingMode; // saves kit.selectionHandler.highlightingMode before this tool gets activated

	/**
	 * @param kit the hosting editor-kit
	 */
	public EditPointMouseTransformFactory(EditorKit kit){
		super(kit);
	}

	/**
	 * Returns a reference to the current <code>SelectedPointsHandler</code>.
	 */
	public SelectedPointsHandler getSelectedPointsHandler(){
		return selectedPointsHandler;
	}

	/**
	 * Return true is the given element is a valid target for this factory.
	 */
	protected boolean isValidTarget(Element e){
		return (e instanceof AbstractCurve) || (e instanceof PicParallelogram);
	}

	/**
	 * Allows this <code>EditPointMouseTransformFactory</code> to do specific graphic rendering when it's
	 * installed in a hosting <code>SelectionTool</code>. This implementation renders selected points using a
	 * specific highlighter (which superimposes to the standard highlighter).
	 * @since jpicedt 1.4
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		if (this.scale != scale){ // update stroke after zoom changed
			this.scale = scale;
			// scale thickness down so that it's displayed with the same thickness whatever value the current zoom factor may have
			lineStroke = new BasicStroke((float)(2.0/scale));
		}
		g.setPaint(Color.blue);
		g.setStroke(lineStroke);
		double barbellSize = 1.3*BARBELL_SIZE/scale;
		Element elem = selectedPointsHandler.getElement();
		for (Iterator it=selectedPointsHandler.indexIterator(); it.hasNext();){
			int selPtIdx = ((Integer)it.next()).intValue();
			elem.getCtrlPt(selPtIdx, ptBuffer); // dest = ptBuffer
			circle.setFrameFromCenter(ptBuffer.x, ptBuffer.y, ptBuffer.x+barbellSize, ptBuffer.y+barbellSize);
			g.draw(circle);
		}
	}

	/**
	 * called when the associated <code>SelectionTool</code> is being activated in the hosting
	 * <code>EditorKit</code>.  This cleans up the state of the underlying <code>SelectionHandler</code>, and
	 * checks for the state of the current selection (ie <code>EditorKit</code>'s selection handler) to be
	 * adequate with the mouse-transform's created by this factory. This means in particular that no
	 * <code>Element</code> must be selected. Besides, <code>highlightingMode</code> is forced to
	 * <code>LOCAL_MODE</code>, so that curve's control-points are visible ("green squares").
	 *
	 */
	public void init(UserConfirmationCache ucc){
		selectedPointsHandler.clear();
		PECanvas canvas = getEditorKit().getCanvas();
		if (canvas==null) return; // kit has not been installed yet
		switch (canvas.getSelectionSize()){
			case 0 : // leave it as is
				break;
			default : // selected element : deselect all
				canvas.unSelectAll();
				break;
		}
		// force kit's edit-point mode to true (store state for later use by flush()) :
		SelectionHandler sh = getEditorKit().getSelectionHandler();
		if (sh instanceof DefaultSelectionHandler){
			DefaultSelectionHandler dsh = (DefaultSelectionHandler)sh;
			this.oldSelHandlerHighlightingMode = dsh.getHighlightingMode();
			dsh.setHighlightingMode(HighlightingMode.LOCAL);
		}
	}

	/**
	 * Called when the associated <code>SelectionTool</code> is being deactivated in the hosting
	 * <code>EditorKit</code>.  This simply cleans up the state of the underlying
	 * <code>SelectionHandler</code>.
	 */
	public void flush(){
		SelectionHandler sh = getEditorKit().getSelectionHandler();
		if (sh instanceof DefaultSelectionHandler){
			DefaultSelectionHandler dsh = (DefaultSelectionHandler)sh;
			dsh.setHighlightingMode(this.oldSelHandlerHighlightingMode);
		}
	}

	/**
	 * Return a MouseTransform whose type is adequate with the given mouse-event.
	 * This can be null if no MouseTransform matches the given event.
	 * <p>
	 * Basically, we work with the following modifiers : Shift, Control, Alt. Other modifiers
	 * must be excluded, given their poor support on MacOS platforms, and their odd behaviours
	 * on some Unices. Similarly, double-click events should be avoided since these are rather hard to deal with
	 * seeing that a single-click event is ALWAYS posted beforehands.
	 */
	public MouseTransform createMouseTransform(PEMouseEvent e){

		// look up selection :
		HitInfo hitInSelection = getEditorKit().hitTest(e, true); // selection only

		if (hitInSelection==null) { // no selected Element under cursor
			HitInfo hitInDrawing = getEditorKit().hitTest(e,false); // whole drawing
			if (hitInDrawing==null){ // no Element at all under cursor
				if (e.getCanvas().getSelectionSize()==1) return new SelectPointsInAreaTransform(e.isShiftDown()); // replace or add to pts-selection
				if (e.getCanvas().getSelectionSize()>1) return new SelectElementTransform(null); // unselect all
				return new HelpMessageMouseTransform("help-message.SelectACurve");
			}
			// non-selected Element under cursor -> select it if applicable
			if (isValidTarget(hitInDrawing.getTarget()))  return new SelectElementTransform(hitInDrawing.getTarget());
			if (e.getCanvas().getSelectionSize()>1) return new SelectElementTransform(null); // unselect all
			return null;
		}

		// now, we know there's a selected Element under the cursor :
		// if it's not a valid target, unselect all, because an invalid target MUST not be selected !
		if (!isValidTarget(hitInSelection.getTarget())) return new SelectElementTransform(null);

		// ok, it's a valid target but there is more than one selected element... => leave only target selected
		if (e.getCanvas().getSelectionSize()>1) return new SelectElementTransform(hitInSelection.getTarget()); // deselect all, then select element under cursor

		// now, there is only ONE selected element, and it's a valid target !

		// NO modifiers :
		if (!e.isShiftDown() && !e.isControlDown() && !e.isAltDown()){
			// *) remove selected points or select points under cursor and/or select from rectangular area :
			if (hitInSelection instanceof HitInfo.Point){
				HitInfo.Point hip = (HitInfo.Point)hitInSelection;
				if (selectedPointsHandler.isSelected(hip)) return new RemovePointTransform();// look up selected point and remove ALL selected points
				return new SelectPointsInAreaTransform(hip, false); // replace selection + select points under cursor
			}
			// *) replace selection
			return new SelectPointsInAreaTransform(false);
		}

		// SHIFT
		if (e.isShiftDown() && !e.isControlDown() && !e.isAltDown()){
			// *) select/unselect
			if (hitInSelection instanceof HitInfo.Point){
				HitInfo.Point hip = (HitInfo.Point)hitInSelection;
				// unselect point if not previously selected :
				if (selectedPointsHandler.isSelected(hip)) return new UnSelectPointTransform(hip);
				return new SelectPointsInAreaTransform(hip, true); // extend selection + select points under cursor
			}
			return new SelectPointsInAreaTransform(true); // extend selection
		}

		// CONTROL
		if (!e.isShiftDown() && e.isControlDown() && !e.isAltDown()){

			// *) add point to PicMultiCurve
			if (hitInSelection.getTarget() instanceof PicMultiCurve){
				PicMultiCurve curve = (PicMultiCurve)hitInSelection.getTarget();
				if (hitInSelection instanceof HitInfo.Point){
					HitInfo.Point hip = (HitInfo.Point)hitInSelection;
					return null; // [SR:pending] fetch clicked point, then convert to segment index, then use
					             // either splitSegment or addPoint(pt) (if open curve) to add a point to the end of the curve
				}
				if (hitInSelection instanceof HitInfo.Stroke){
					HitInfo.Stroke his = (HitInfo.Stroke)hitInSelection;
					return new SplitSegmentTransform(curve, his.getClickedSegment(), e.getCanvas().getGrid());
				}
			}
			// *) add point to PicSmoothPolygon
			else if (hitInSelection.getTarget() instanceof PicSmoothPolygon){//= tangents (specific to SmoothPoly)
				PicSmoothPolygon curve = (PicSmoothPolygon)hitInSelection.getTarget();
				if (hitInSelection instanceof HitInfo.Point){
					HitInfo.Point hip = (HitInfo.Point)hitInSelection;
					return null; // [SR:pending] fetch clicked point, then use
					             // either splitSegment or addPoint(pt) to add a point to the end of the curve
				}
				if (hitInSelection instanceof HitInfo.HighlighterStroke){
					HitInfo.HighlighterStroke his = (HitInfo.HighlighterStroke)hitInSelection;
					return new SplitSegmentTransform(curve, his.getClickedSegment(), e.getCanvas().getGrid());
				}
			}
			// *) add point to PsCurve
			else if (hitInSelection.getTarget() instanceof PicPsCurve){
				PicPsCurve curve = (PicPsCurve)hitInSelection.getTarget();
				if (hitInSelection instanceof HitInfo.Point){
					HitInfo.Point hip = (HitInfo.Point)hitInSelection;
					return null;
				}
				else if (hitInSelection instanceof HitInfo.HighlighterStroke){ // seg=0 (start) or 1 (end) (open curve only)
					HitInfo.HighlighterStroke his = (HitInfo.HighlighterStroke)hitInSelection;
					//debug("(highlighter) idx="+his.getClickedSegment());
					switch (his.getClickedSegment()){
						case 0: // tangent at first end-point
							return new SplitSegmentTransform(curve, 0, e.getCanvas().getGrid());
						case 1: // tangent at last end-point
							return new SplitSegmentTransform(curve, curve.getLastPointIndex()-1, e.getCanvas().getGrid());
						default: // assert false
					}
				}
				else if (hitInSelection instanceof HitInfo.Stroke){
					HitInfo.Stroke his = (HitInfo.Stroke)hitInSelection;
					//debug("(stroke) idx="+his.getClickedSegment());
					// for open curves, add 1 to segment index, since initial tangent makes one segment, which is not reported
					// by HitInfo.Stroke.getClickedSegment
					if (curve.isClosed()) return new SplitSegmentTransform(curve, his.getClickedSegment(), e.getCanvas().getGrid());
					else return new SplitSegmentTransform(curve, his.getClickedSegment()+1, e.getCanvas().getGrid());
				}
			}
			return null;
		}

		// CTRL + ALT
		if (!e.isShiftDown() && e.isControlDown() && e.isAltDown()){
			// *) edit-point features :
			if (hitInSelection instanceof HitInfo.Point){
				HitInfo.Point hip = (HitInfo.Point)hitInSelection;
				if (hitInSelection.getTarget() instanceof PicMultiCurve){ // set smooth and symmetric
					return new InvalidMouseTransform(); // [SR:pending] open dialog
				}
				if (hitInSelection.getTarget() instanceof PicPsCurve){
					return new InvalidMouseTransform(); // [SR:pending] alter curvature
				}
				if (hitInSelection.getTarget() instanceof PicSmoothPolygon){ // edit smooth coeff
					return new EditSmoothCoeffTransform(); // look up selected point(s) and edit ALL selected points
				}
				return null;
			}
		}

		return null;
	} /* createMouseTransform */


	/////////////////////////////////////////////////////////////////////////////////
	//// SELECTION
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * a mouse-transform that selects all elements inside a rectangle dragged by the user
	 */
	protected class SelectPointsInAreaTransform extends SelectAreaTransform {

		private boolean addToSelection;
		private HitInfo.Point hip;

		/**
		 * @param addToSelection if true, selection of points is incremental.
		 */
		public SelectPointsInAreaTransform(boolean addToSelection){
			this.addToSelection = addToSelection;
			this.hip=null;
		}

		/**
		 * @param addToSelection if true, selection of points is incremental.
		 * @param hip if non-null, indices contained therein will be selected before
		 * starting to draw the selection rectangle (click on an Element's point) */
		public SelectPointsInAreaTransform(HitInfo.Point hip, boolean addToSelection){
			this.addToSelection = addToSelection;
			this.hip=hip;
		}

		/** called by mousePressed */
		public void start(PEMouseEvent e){
			super.start(e);
			if (e.getCanvas().getSelectionSize()!=1) return; // security
			if (!addToSelection) selectedPointsHandler.clear();
			if (hip!=null){ // add points under cursor
				if (selectedPointsHandler.getElement()==null || selectedPointsHandler.getElement()!=hip.getTarget())
					selectedPointsHandler.setElement(hip.getTarget());
				for (int i=0; i<hip.getNbHitPoints(); i++){ // look up selected point and remove ALL selected points
					selectedPointsHandler.selectPoint(hip.getIndex(i));
				}
			}
		}

		/**
		 * Called when the mouse is released. Selects every elements inside the selection area,
		 * including the element being currently under the cursor.
		 */
		public boolean next(PEMouseEvent e){
			super.next(e); // repaint area
			// first, if there's an element under the cursor, select it (if it was already selected, this
			// has no effet :
			// HitInfo hi = getEditorKit().hitTest(e,false); // whole drawing
			// if (hi != null) e.getCanvas().select(hi.getTarget(),true); // add to selection

			//if (diag.x1==diag.x2 && diag.y1==diag.y2) return; // reduced to a point... no need to check for sel. elements
			// compute number of objects contained in the selection area
			Rectangle2D rectArea = getSelectionRectangle();
			if (e.getCanvas().getSelectionSize()!=1) return false; // security
			Element elem;
			if (hip!=null) elem = hip.getTarget();
			else elem = (Element)e.getCanvas().selection().next(); // fetch selected Element

			// security :
			if (selectedPointsHandler.getElement() == null || selectedPointsHandler.getElement()!=elem){
				selectedPointsHandler.setElement(elem); // also clears list of selected points
			}
			for(int ptIndex=elem.getFirstPointIndex(); ptIndex <= elem.getLastPointIndex(); ptIndex++){
				ptBuffer = elem.getCtrlPt(ptIndex,ptBuffer);
				if (rectArea.contains(ptBuffer.x, ptBuffer.y)){
					selectedPointsHandler.selectPoint(ptIndex);
				}
			}
			if (DEBUG) debug( selectedPointsHandler.toString());
			return false; // finish here, only one sequence-step
		}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.SelectPointsInArea";
		}

		/** @return a textual representation of this transform for debugging purpose */
		public String toString(){
			return "[SelectPointInAreaTransform]";

		}

	} // SelectPointsInAreaTransform

	/**
	 * a mouse-transform that unselects points
	 */
	protected class UnSelectPointTransform implements MouseTransform {

		private HitInfo.Point hip;

		/**
		 * @param hip indices contained therein will be unselected
		 */
		public UnSelectPointTransform(HitInfo.Point hip){
			this.hip=hip;
		}

		/** called by mousePressed */
		public void start(PEMouseEvent e){
			if (e.getCanvas().getSelectionSize()!=1) return; // security
			if (hip!=null){ // unselect points under cursor
				if (selectedPointsHandler.getElement()==null || selectedPointsHandler.getElement()!=hip.getTarget())
					selectedPointsHandler.setElement(hip.getTarget());
				for (int i=0; i<hip.getNbHitPoints(); i++){ // look up selected point and unselect ALL selected points
					selectedPointsHandler.unSelectPoint(hip.getIndex(i));
				}
			}
		}

		public boolean next(PEMouseEvent e){
			return false; // finish here, only one sequence-step
		}

		public void process(PEMouseEvent e){}

		public void paint(Graphics2D g, Rectangle2D allocation, double scale){}
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.SELECT);
		}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.UnSelectPoint";
		}

		/** @return a textual representation of this transform for debugging purpose */
		public String toString(){
			return "[UnSelectPointTransform]";

		}

	} // UnSelectPointTransform

	/**
	 * Helper class for SelectPointsInAreaTransform.
	 * This is a typesafe container for ONE Element and the indices of its selected-points
	 * <p>
	 * Indices are ALWAYS sorted in descending order :
	 * this helps e.g. RemovePointTransform removing curve's points starting from the end (this is made necessary
	 * so as to preserve the meaning of yet-to-be-removed points indices ; removing points starting from
	 * index "0" would surely lead to odd behaviour since ensuing indices would be shifted to the "left").
	 */
	protected class SelectedPointsHandler {
		private Element target;
		private TreeSet<Integer> indexList;

		public SelectedPointsHandler(){
			indexList = new TreeSet<Integer>(new ReverseIntegerComparator()); // sort indices in descending order
		}

		/** clears the state of this handler by removing the reference to the current Element and its selected points */
		public void clear(){
			target=null;
			indexList.clear();
		}

		/** sets the current Element and clears the list of selected-points */
		public void setElement(Element e){
			this.target=e;
			indexList.clear();
		}

		/** returns the current target Element */
		public Element getElement(){
			return target;
		}

		/** adds the given point index to the selection */
		public void selectPoint(int idx){
			indexList.add(idx); // TreeSet arranges for no-duplicate indices !
		}

		/** removes the given point index to the selection */
		public void unSelectPoint(int idx){
			indexList.remove(idx);
		}

		/**
		 * Returns true if the point with the given index is selected.
		 */
		public boolean isSelected(int idx){
			return indexList.contains(idx); // test using "Object.equals" method, this is ok.
		}

		/**
		 * Return true if at least one point in the given HitInfo.Point is selected
		 */
		public boolean isSelected(HitInfo.Point hip){
			for (int i=0; i<hip.getNbHitPoints(); i++){ // look up points under cursor
				if (isSelected(hip.getIndex(i))) return true;
			}
			return false;
		}

		/** return the number of selected points for the current element */
		public int getSelectionSize(){
			return indexList.size();
		}

		/** return an Iterator over the set of selected-points indices (wrapped in Integer's) */
		public Iterator indexIterator(){
			return indexList.iterator();
		}

		public String toString(){
			String str = "[SelectedPointsHandler : target="+target+", indices=";
			for (Iterator it=indexList.iterator(); it.hasNext(); ){
				str += it.next().toString() + " ";
			}
			return str;
		}
	}

	/** a comparator that may be used to sort integers in descending order using standard "sort" methods in Java's Collection Framework */
	class ReverseIntegerComparator implements Comparator<Integer> {
		// return a positive integer as o1 > o2
		public int compare(Integer o1, Integer o2){
			int i1 = o1;
			int i2 = o2;
			return (i2-i1); // indeed leads to a descending order sorting.
		}
	}


	/////////////////////////////////////////////////////////////////////////////////
	//// split segment
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * a mouse-transform that add points to extensible curves
	 */
	protected class SplitSegmentTransform implements MouseTransform {

		private AbstractCurve target;
		private Grid grid;
		private int segIdx;
		private int ctrlPtIdx; // index of point that got inserted ; used by process() to move the newly added point.
		private int seqIndex=0; // PicMultiCurve only : 0=set subdiv-pt ; 1=set left control-pt ; 2=set right control-pt

		/**
		 * @param target the element upon which is transform will act
		 * @param segIdx index of the segment where the new point is to be inserted.
		 * @param grid the Grid instance used for alignment (if it's snap-on)
		 */
		public SplitSegmentTransform(AbstractCurve target, int segIdx, Grid grid){

			this.target = target;
			this.segIdx = segIdx;
			this.grid = grid;
		}

		/**
		 * Called when the mouse is pressed for the first time.
		 * @since jpicedt 1.4
		 */
		public void start(PEMouseEvent e){
			PicPoint pt = e.getPicPoint();
			grid.nearestNeighbour(pt,pt); // pt = nn(pt)
			e.getCanvas().beginUndoableUpdate(localize("action.editorkit.SplitSegment"));
			ctrlPtIdx=target.splitSegment(segIdx,pt);
		}

		/**
		 * Called when the mouse is dragged. This moves the newly added point and/or sets its control-point when
		 * applicable.
		 */
		public void process(PEMouseEvent e){
			PicPoint pt = e.getPicPoint();
			grid.nearestNeighbour(pt,pt); // pt = nn(pt)
			if (target instanceof PicMultiCurve) {
				switch (seqIndex){
					case 0 :
						target.setCtrlPt(ctrlPtIdx,pt,
										 BasicEditPointConstraint.SMOOTHNESS_SYMMETRY);
						break;
					case 1 :
						target.setCtrlPt(target.getPBCBezierIndex(ctrlPtIdx+1),pt,
										 BasicEditPointConstraint.SMOOTHNESS_SYMMETRY);
						break;
					default:
				}
			}
			else if (target instanceof PicSmoothPolygon) {
				target.setCtrlPt(ctrlPtIdx,pt,null);
			}
			else if (target instanceof PicPsCurve) {
				target.setCtrlPt(ctrlPtIdx,pt,null);
			}
		}

		/**
		 * Called when the mouse is released.
		 */
		public boolean next(PEMouseEvent e){
			if (target instanceof PicMultiCurve) {
				seqIndex++;
				if (seqIndex<2) return true;
			}
			e.getCanvas().endUndoableUpdate();
			return false;
		}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.SplitSegment";
		}

		/**
		 * Allows the MouseTransform to do specific graphic rendering when it's operating.
		 * This implementation does nothing.
		 * @since jpicedt 1.3.2
		 */
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		}

		/** @return a textual representation of this transform for debugging purpose */
		public String toString(){
			return "[SplitSegmentTransform : \n\tsegIdx = " + segIdx
			       + "\n\ttarget = " + target ;
		}

		/**
		 * @return a cursor adequate with this mouse-transform, delegating to CursorFactory.
		 */
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.ADD_ENDPT);
		}

	} // SplitSegmentTransform

	/////////////////////////////////////////////////////////////////////////////////
	//// remove point
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * a mouse-transform that removes selected points to/from extensible curves or parallelogram/ellipses after conversion
	 * to a multicurve.
	 * Target element is fetched from the selectedPointHandler.
	 */
	protected class RemovePointTransform implements MouseTransform {

		/**
		 * Called when the mouse is pressed. The transform should do the initialization work here.
		 * @since jpicedt 1.3.3
		 */
		public void start(PEMouseEvent e){
			Element elem = selectedPointsHandler.getElement();
			if (elem == null) return; // security
			if (!isValidTarget(elem)) return;

			PicPoint pt = e.getPicPoint();
			e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)

			e.getCanvas().beginUndoableUpdate(localize("action.editorkit.RemovePoint"));

			for (Iterator it=selectedPointsHandler.indexIterator(); it.hasNext();){ // automatically starts with higher selected point index
				int index = ((Integer)it.next()).intValue();
				if (elem instanceof PicMultiCurve){ // first remove ALL control-points
					PicMultiCurve c = (PicMultiCurve)elem;
					if (c.isControlPoint(index)) {
						c.removePoint(index);
						it.remove(); // equiv. to selectedPointsHandler.unSelectPoint(index), yet avoid ConcurrentModificationException
					}
				}
				else if (elem instanceof PicSmoothPolygon) {
					((PicSmoothPolygon)elem).removePoint(index);
					it.remove(); // equiv. to selectedPointsHandler.unSelectPoint(index), yet avoid ConcurrentModificationException
				}
				else if (elem instanceof PicPsCurve) {
					((PicPsCurve)elem).removePoint(index);
					it.remove();
				}
			}
			if (elem instanceof PicMultiCurve){ // then remove ALL subdivision-points
				PicMultiCurve c = (PicMultiCurve)elem;
				for (Iterator it=selectedPointsHandler.indexIterator(); it.hasNext();){ // start with higher selected point index
					int index = ((Integer)it.next()).intValue();
					c.removePoint(index); // subdiv-points, since ALL selected control-points have been removed
					it.remove(); // equiv. to selectedPointsHandler.unSelectPoint(index), yet avoid ConcurrentModificationException
				}
			}
			e.getCanvas().endUndoableUpdate();
		}

		/**
		 * Called when the mouse is dragged. This implementation does nothing.
		 */
		public void process(PEMouseEvent e){
		}

		/**
		 * Called when the mouse is released.
		 */
		public boolean next(PEMouseEvent e){
			return false;
		}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.RemovePoint";
		}

		/**
		 * Allows the MouseTransform to do specific graphic rendering when it's operating.
		 * This implementation does nothing.
		 * @since jpicedt 1.3.2
		 */
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		}

		/** @return a textual representation of this transform for debugging purpose */
		public String toString(){
			return "[RemovePointTransform : \n\tselectionHandler = " + selectedPointsHandler.toString() +"\n";
		}

		/**
		 * @return a cursor adequate with this mouse-transform, delegating to CursorFactory.
		 */
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.REMOVE_ENDPT);
		}

	} // RemovePointTransform

	/////////////////////////////////////////////////////////////////////////////////
	//// edit smooth coeffs
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * a mouse-transform that edit smooth-coefficients for a PicSmoothPolygon (edit ALL selected points at once)
	 */
	protected class EditSmoothCoeffTransform implements MouseTransform {

		double lastYMousePosition;
		PicSmoothPolygon target;
		double increment;

		/**
		 * Called when the mouse is pressed. The transform should do the initialization work here.
		 * @since jpicedt 1.4
		 */
		public void start(PEMouseEvent e){
			Element elem = selectedPointsHandler.getElement();
			if (elem == null) return; // security
			if (!(elem instanceof PicSmoothPolygon)) return;
			this.target = (PicSmoothPolygon)elem;

			PicPoint pt = e.getPicPoint();
			//e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)

			e.getCanvas().beginUndoableUpdate(localize("action.editorkit.EditSmoothCoeffs"));
			lastYMousePosition = e.getAwtMouseEvent().getPoint().y;
		}

		/**
		 * Called when the mouse is dragged. This implementation updates the current smooth coefficient value from the vertical
		 * mouse position, and update the target polygon accordingly.
		 */
		public void process(PEMouseEvent e){
			double newYMousePosition = e.getAwtMouseEvent().getPoint().y;
			this.increment = (newYMousePosition - lastYMousePosition)/100.0;
			for (Iterator it=selectedPointsHandler.indexIterator(); it.hasNext();){ // automatically starts with higher selected point index
				int index = ((Integer)it.next()).intValue();
				double coeff = target.getSmoothCoefficient(index);
				coeff += increment;
				System.out.println(coeff);
				target.setSmoothCoefficient(index,coeff);
			}
			lastYMousePosition = newYMousePosition;
		}

		/**
		 * Called when the mouse is released. Fires an end-undoable-event.
		 */
		public boolean next(PEMouseEvent e){
			e.getCanvas().endUndoableUpdate();
			return false;
		}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.EditSmoothCoeffs";
		}

		/**
		 * Allows the MouseTransform to do specific graphic rendering when it's operating.
		 * This implementation does nothing.
		 * @since jpicedt 1.3.2
		 */
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){

		}

		/** @return a textual representation of this transform for debugging purpose */
		public String toString(){
			return "[EditSmoothCoeffs : \n\tselectionHandler = " + selectedPointsHandler.toString() +"\n";
		}

		/**
		 * @return a cursor adequate with this mouse-transform, delegating to CursorFactory.
		 */
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.N_RESIZE);
		}

	} // EditSmoothCoeffsTransform

	////////////////////////////////////////////////////////////////////////////////
	//// SELECT Element
	/////////////////////////////////////////////////////////////////////////////////
	protected class SelectElementTransform implements MouseTransform {
		Element target;

		/**
		 * @param target the Element to be selected ; if null, all Element's get deselected.
		 */
		public SelectElementTransform(Element target){
			this.target = target;
		}

		public void start(PEMouseEvent e){
			if (target==null) {
				e.getCanvas().unSelectAll();
				selectedPointsHandler.clear();
			}
			else { // convert to multicurve on-the-fly:
				boolean needConversion = (target instanceof PicMultiCurveConvertable) && !(target instanceof AbstractCurve);
				if (needConversion){ // hence also PicEllipse and PicCirlceFrom3Points
					e.getCanvas().beginUndoableUpdate(localize("action.editorkit.ConvertParallelogramToMulticurve"));
					PicMultiCurve curve = ((PicMultiCurveConvertable)target).convertToMultiCurve();
					Drawing dr = target.getDrawing();
					if (dr != null){
						dr.replace(target, curve);
						target = curve;
					}
					e.getCanvas().endUndoableUpdate();
				}
				e.getCanvas().select(target,REPLACE); // replace selection
				selectedPointsHandler.setElement(target);
			}
		}

		public boolean next(PEMouseEvent e){return false;}
		public void process(PEMouseEvent e){}
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){}
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.SELECT);
		}
		public String getHelpMessage(){
			return "help-message.SelectACurveTransform";
		}

	}

} // EditPointMouseTransformFactory
