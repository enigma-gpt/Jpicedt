// EditElementMouseTransformFactory.java --- -*- coding: iso-8859-1 -*-
// February 28, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: EditElementMouseTransformFactory.java,v 1.35 2013/03/27 06:58:01 vincentb1 Exp $
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

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.grid.Grid;
import jpicedt.graphic.model.BranchElement;
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicCircleFrom3Points;
import jpicedt.graphic.model.PicEllipse;
import jpicedt.graphic.model.PicGroup;
import jpicedt.graphic.model.PicMultiCurve;
import jpicedt.graphic.model.PicParallelogram;
import jpicedt.graphic.model.PicPsCurve;
import jpicedt.graphic.model.PointIndexIterator;
import jpicedt.graphic.toolkit.BasicEditPointConstraint;
import jpicedt.graphic.model.EditPointConstraint;
import jpicedt.graphic.view.HitInfo;
import jpicedt.graphic.view.View;
import jpicedt.graphic.view.highlighter.DefaultHighlighter;
import jpicedt.ui.dialog.UserConfirmationCache;
import jpicedt.widgets.MDIComponent;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

import java.util.Iterator;
import java.util.ArrayList;

import static jpicedt.Localizer.*;
import static jpicedt.Log.*;
import static jpicedt.graphic.PECanvas.SelectionBehavior.*;
import static jpicedt.graphic.model.EditPointConstraint.EditConstraint.*;

/**
 * A factory that produces <code>MouseTransform</code>'s that may be plugged into the
 * <code>SelectionTool</code> mouse tool.
 * @author Sylvain Reynal
 * @since jPicEdt 1.3.2
 * @version $Id: EditElementMouseTransformFactory.java,v 1.35 2013/03/27 06:58:01 vincentb1 Exp $
 */
public class EditElementMouseTransformFactory extends AbstractMouseTransformFactory {

	private CursorFactory cursorFactory=new CursorFactory();

	/**
	 *  @param kit the hosting editor-kit
	 */
	public EditElementMouseTransformFactory(EditorKit kit){
		super(kit);
	}

	/**
	 * Return a <code>MouseTransform</code> whose type is adequate with the given mouse-event.  This can be
	 * null if no MouseTransform matches the given event.  <p> Basically, we work with the following
	 * modifiers: Shift, Control, Alt. Other modifiers must be excluded, given their poor support on MacOS
	 * platforms, and their odd behaviours on some Unices. Similarly, double-click events should be avoided
	 * since these are rather hard to deal with seeing that a single-click event is <strong>ALWAYS</strong>
	 * posted beforehands.
	 */
	public MouseTransform createMouseTransform(PEMouseEvent e){

		MouseTransform mt=null;
		if (DEBUG) {
			debug("HitInfo (selection): "+getEditorKit().hitTest(e, true));
			debug("HitInfo (drawing): "+getEditorKit().hitTest(e, false));
		}

		// normal selection mode (mouse):

		// look up selection : // [pending] add rotate
		HitInfo hiSel = getEditorKit().hitTest(e, true); // selection only

		// Note : if GLOBAL_MODE is on in DefaultSelectionHandler, hiSel is either a HitInfo.Point/HighlighterStroke
		//        if LOCAL_MODE is on, hiSel may be every HitInfo, including HitInfo.Composite

		// 1) no selected element under the cursor : start growing "select area"
		if (hiSel==null) {
			// -> look up drawing
			boolean incremental = e.isShiftDown();
			HitInfo hiNonSel = getEditorKit().hitTest(e,false); // => HitInfo.Stroke (click on a single element) or HitInfo.Composite (if a click occured on a PicGroup) or HitInfo.List
			if (hiNonSel == null)
				mt = new SelectElementsInAreaTransform(null,incremental); // no element at all under the cursor !
			else
				// alternate selection mode from dialog box: ALT+SHIFT
				if (e.isAltDown()  && !e.isControlDown() && e.isShiftDown()){
					return new SelectElementsDialogTransform(hiNonSel);
				}
				else
					mt = new SelectElementsInAreaTransform(hiNonSel.getTarget(),incremental); // select highest-z element
		}

		// 2) alternate selection mode from dialog box: SHIFT+ALT
		else if (e.isShiftDown() && !e.isControlDown() && e.isAltDown()){
			HitInfo hiNonSel = getEditorKit().hitTest(e,false);
			if (hiNonSel == null)
				return new SelectElementsDialogTransform(hiSel);
			else
				return new SelectElementsDialogTransform(hiSel.append(hiNonSel));
		}
		// 3) selected element under the cursor : SHIFT => "deselect" ...
		else if (e.isShiftDown() && !e.isControlDown() && !e.isAltDown()){
			Element elem = hiSel.getTarget();
			// if the target is the selection handler itself, returned the clicked child
			// (this happens if isPaintGroupEndPoint is true)
			// [SR:underway] HitInfo.Composite => bug prone ?
			if (elem == getEditorKit().getSelectionHandler() && hiSel instanceof HitInfo.Composite)
				elem = ((HitInfo.Composite)hiSel).getClickedChild();
			mt = new UnselectTransform(elem);
		}

		// 4) either selection-handler's control-points, or control-point of an element in the selection => move control-points
		else if (hiSel instanceof HitInfo.Point){
			mt = createMoveControlPointTransform((HitInfo.Point)hiSel,e,ucc);
		}
		// 5) Stroke/Interior on an element of the selection handler in "LOCAL MODE" => move
		else if (hiSel instanceof HitInfo.Composite){
			HitInfo.Composite hic = (HitInfo.Composite)hiSel; // clicked child serves as anchor element
			mt = new MoveElementTransform(hic.getTarget(),hic.getClickedChildIndex(),e.getPicPoint(),e.getCanvas().getGrid());
		}
		// 6) Stroke/Interior on an element of the selection handler in "GLOBAL MODE" => move
		else {
			mt = new MoveElementTransform(hiSel.getTarget(),e.getPicPoint(),e.getCanvas().getGrid());
		}
		if (DEBUG) debug("returned mt="+mt);
		return mt;
	} /* createMouseTransform */


	////////////////////// HELPERS ///////////////////////

	/**
	 * Helper-code for creating <code>MoveControlPointTransform</code>'s
	 */
	private MouseTransform createMoveControlPointTransform(HitInfo.Point hip, PEMouseEvent me,UserConfirmationCache ucc){
		// fetch target (either the selectionHandler or one of its children)
		// this will help the MouseTransform find the adequate cursor
		Element target = hip.getTarget();
		String helpMsg=null; // please follow std guideline, ie aka "help-message.xxx"
		BasicEditPointConstraint constraint = new BasicEditPointConstraint(ucc);

		// ----- PicMultiCurve -----
		//
		// NO_MODIFIERS : move subdivision-points or control-points, yet if some happen to be located at the place,
		//                the policy is to move subdivision-points. Control-points are moved with
		//                SMOOTHNESS and SYMMETRY constraint (ie as if using CTRL+)
		//                This is especially smart for straight-segments, since the basic UI beh. is to move segment end-points.
		// All other modifiers move control-points ONLY ! If a click occur on a subdiv-point, null is returned.
		// CTRL : move control-points with SMOOTHNESS and SYMMETRY
		// CTRL+SHIFT : ibid. with SMOOTHNESS only
		// CTRL+ALT : ibid. with SYMMETRY only
		// CTRL+ALT+SHIFT : idib. yet relaxes all constraint (aka PicMultiCurve.FREE_CONTROL constraint)
		if (target instanceof PicMultiCurve){
			PicMultiCurve curve = (PicMultiCurve)target;
			// a) move subdiv-points or control-points, with priority being given to subdiv-points :
			if (!me.isShiftDown() && !me.isControlDown() && !me.isAltDown()){
				constraint.setEditConstraint(SMOOTHNESS_SYMMETRY); // for subdiv point, this has no effect
				for (int i=0; i<hip.getNbHitPoints(); i++){ // look-up first subdiv-point in the set of clicked points
					if (!curve.isControlPoint(hip.getIndex(i))){  // ok, we got one !
						helpMsg = "help-message.MoveSubdivPoint";
						return new MoveControlPointTransform(curve, hip.getIndex(i), constraint, helpMsg, me.getCanvas().getGrid());
					}
				}
				// no subdiv-point => switch to first available control-point index:
				helpMsg="help-message.MoveControlPoint";
				return new MoveControlPointTransform(curve, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid());
			}
			// b) move control-points with or without contraints :
			for (int i=0; i<hip.getNbHitPoints(); i++){ // look-up first control-point in the set of clicked points
				if (curve.isControlPoint(hip.getIndex(i))) { // ok, we got one !

					if (!me.isShiftDown() && me.isControlDown() && !me.isAltDown()){ // CTRL
						constraint.setEditConstraint(SMOOTHNESS_SYMMETRY);
						helpMsg="help-message.ControlSmoothAndSymmetric";
					}
					else if (me.isShiftDown() && me.isControlDown() && !me.isAltDown()){ // SHIFT+CTRL
						constraint.setEditConstraint(SMOOTHNESS);
						helpMsg="help-message.ControlSmooth";
					}
					else if (!me.isShiftDown() && me.isControlDown() && me.isAltDown()){ //CTRL+ALT
						constraint.setEditConstraint(SYMMETRY);
						helpMsg="help-message.ControlSymmetric";;
					}
					else if (me.isShiftDown() && me.isControlDown() && me.isAltDown()){ // CTRL+ALT+SHIFT
						constraint.setEditConstraint(FREELY);
						helpMsg="help-message.ControlFreely";
					}
					else return new InvalidMouseTransform(); // other modifiers forbidden
					return new MoveControlPointTransform(curve, hip.getIndex(i), constraint, helpMsg, me.getCanvas().getGrid());
				}
			}
			// otherwise there was no control-point in the set, and we don't do anything.
			return null;
		}

		// ----- PicPsCurve -----
		//
		// NO_MODIFIERS : gives priority to curve's points
		// CTRL : gives priority to control-points (tangents)
		if (target instanceof PicPsCurve){
			PicPsCurve curve = (PicPsCurve)target;
			if (!me.isShiftDown() && me.isControlDown() && !me.isAltDown()){ // CTRL
				helpMsg="help-message.MoveControlPoint";
				for (int i=0; i<hip.getNbHitPoints(); i++){ // look-up first tangent's control-point in the set of clicked points
					if (hip.getIndex(i)==0 || hip.getIndex(i)==curve.getLastPointIndex())
						return new MoveControlPointTransform(target, hip.getIndex(i), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
				}
				// no tangent's control point under cursor -> that's ok to move curve's points
				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
			}
			else {
				for (int i=0; i<hip.getNbHitPoints(); i++){ // look-up first curve's point in the set of clicked points
					if (hip.getIndex(i)==0 || hip.getIndex(i)==curve.getLastPointIndex()) continue;
					return new MoveControlPointTransform(target, hip.getIndex(i), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
				}
				// no tangent's control point under cursor -> that's ok to move curve's points
				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
			}
		}

		// ----- PicEllipse, PicParallelogram and PicCircleFrom3Points -----
		//
		// modifiers just modify priority b/w parallelo- and arc- control-points, if some are identical :
		// no modifiers => priority given to parallelo control-points
		// CTRL => priority given to arc angles control points
		if (target instanceof PicCircleFrom3Points){
			return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
		}

		// parallelogram and ellipses
		if (target instanceof PicParallelogram){
			// *) NO MODIFIER:
			if (!me.isShiftDown() && !me.isControlDown() && !me.isAltDown()){
				// since parallelo control-points have lower indices than arcs ctrl-points, no need to iterate...
				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
			}
			// *) CTRL:
			if (target instanceof PicEllipse && !me.isShiftDown() && me.isControlDown() && !me.isAltDown()){
				for (int i=0; i<hip.getNbHitPoints(); i++){ // look-up first control-point in the set of clicked points
					if (hip.getIndex(i)==PicEllipse.P_ANGLE_START || hip.getIndex(i)==PicEllipse.P_ANGLE_END) { // ok, we got one !
						helpMsg = "help-message.ArcAngles";
						return new MoveControlPointTransform(target, hip.getIndex(i), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
					}
				}
				// no arc-angles control points => edit parallelo :
				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
			}
			// *) CTRL+ALT : move with center fixed
			if (!me.isShiftDown() && me.isControlDown() && me.isAltDown()){
				constraint.setEditConstraint(CENTER_FIXED);
				helpMsg = "help-message.MovePointCenterFixed";
				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
			}
			// *) CTRL+ALT+SHIFT : SQUARE constraint on surrounding parallelogram
			if (me.isShiftDown() && me.isControlDown() && me.isAltDown()){
				constraint.setEditConstraint(SQUARE);
				helpMsg = "help-message.EllipseCircle";
				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
			}
			return new InvalidMouseTransform(); // other modifiers forbidden
		}

		// ----- other targets -----
		//
		return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
	}





	////////////////////////////////////////////////////////////////////////////////
	//// UNSELECT
	/////////////////////////////////////////////////////////////////////////////////
	protected class UnselectTransform implements MouseTransform {
		Element target;

		public UnselectTransform(Element target){
			this.target = target;
		}

		public void start(PEMouseEvent e){
			e.getCanvas().unSelect(target);
		}

		public boolean next(PEMouseEvent e){return false;}
		public void process(PEMouseEvent e){}
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){}
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.SELECT);
		}
		public String getHelpMessage(){
			return "help-message.UnselectTransform";
		}

	}



	/////////////////////////////////////////////////////////////////////////////////
	//// MOVE-ENDPOINT MT
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * a mouse-transform that moves element's end-points, ie aka scales elements
	 */
	protected class _MoveControlPointTransform extends AbstractMouseTransform {
		// old code (with cloning element first, then editing it, then updating copy)

		private Grid grid;
		private int pointIndex;
		private PicPoint ptBuffer = new PicPoint();
		//[SR:en_cours]private boolean setPointFromCenter;
		private EditPointConstraint constraint;
		private String helpMessage;

		/**
		 * @param target the element upon which is transform will act
		 * @param pointIndex index of the Element's point that will be moved
		 * @param grid the Grid instance used for alignment (if it's snap-on)
		 * @param constraint the geometrical constraint to be used when moving control-points ; may be null
		 * @param helpMessage if non-null, this will be displayed on mouse-move events instead of the default help-message
		 *        for this mouse-transform.
		 * @see jpicedt.graphic.model.Element#setCtrlPt
		 */
		public _MoveControlPointTransform(Element target, int pointIndex, EditPointConstraint constraint,
		                                  String helpMessage, Grid grid){

			super(target);
			this.pointIndex = pointIndex;
			this.grid = grid;
			this.constraint = constraint;
			this.helpMessage = helpMessage;
			//[SR:en_cours]setPointFromCenter = false; // otherwise, lead to a ClassCastException at line 97
		}

		// /**
		//  * @param target the element upon which is transform will act ; this must be a PicRectangle
		//  * @param pointIndex index of the Element's point that will be moved
		//  * @param grid the Grid instance used for alignment (if it's snap-on)
		//  * @param setPointFromCenter if true, move points keeping center fixed (only supported by PicRectangle's)
		//  */
		// public MoveControlPointTransform(PicRectangle target, int pointIndex, Grid grid, boolean setPointFromCenter){

		// 	super(target);
		// 	this.pointIndex = pointIndex;
		// [pending] fetch adequate cursor from Element's class (e.g. PicGroup,...)
		// 	this.grid = grid;
		// 	this.setPointFromCenter = setPointFromCenter;
		// }

		/**
		 * Called when the mouse is dragged. If !isCompleted, sets the clone's point
		 * (with the index given as parameter
		 * in the constructor) to the current mouse position, or its nearet-neighbour on the grid if
		 * grid-snap is on.
		 */
		public void process(PEMouseEvent e){
			PicPoint pt = e.getPicPoint();
			grid.nearestNeighbour(pt,pt); // pt = nn(pt)
			if (getClone().getCtrlPt(pointIndex,ptBuffer).equals(pt)) return; // compare old and new point and return if nothing has moved
			//[SR:en_cours]if (setPointFromCenter){((PicRectangle)getClone()).setPointFromCenter(pointIndex,pt);}
			//[SR:en_cours]else
			getClone().setCtrlPt(pointIndex,pt,constraint);
			if (DEBUG) debugAppendLn("target=" + getTarget());
			if (DEBUG) debugAppendLn("clone=" + getClone());

		}

		/**
		 * Called when the mouse is released. Updates the original element, then call superclass.
		 */
		public boolean next(PEMouseEvent e){
			PicPoint pt = e.getPicPoint();
			grid.nearestNeighbour(pt,pt); // pt = nn(pt)
			//[SR:en_cours]if (setPointFromCenter){((PicRectangle)getTarget()).setPointFromCenter(pointIndex,pt);}
			e.getCanvas().beginUndoableUpdate(localize("action.editorkit.Scale.tooltip"));
			getTarget().setCtrlPt(pointIndex,pt,constraint);
			e.getCanvas().endUndoableUpdate();
			return super.next(e); // remove parent and view from clone, then mark as completed.
		}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			// [SR:pending] adapt according to target and constraint
			if (helpMessage==null) return "help-message.MoveEndPointTransform"; // [SR:pending] change to MoveControlPointTransform
			else return helpMessage;
		}

		/** @return a textual representation of this transform for debugging purpose */
		public String toString(){
			return "[MoveControlPointTransform : \n\tpointIndex = " + pointIndex
			       + "\n\ttarget = " + getTarget() ;

		}

		/**
		 * @return a cursor adequate with this mouse-transform, delegating to CursorFactory.
		 * If the target-element of <code>PicGroup</code>, returns a cursor according to
		 * <code>pointIndex</code>.
		 */
		public Cursor getCursor(){
			//[SR:en_cours]if (setPointFromCenter) return cursorFactory.getPECursor(CursorFactory.MOVE_ENDPT_FROM_CENTER);
			if (getTarget() instanceof PicGroup){
				switch (pointIndex){
				case PicGroup.UL: return cursorFactory.getPECursor(CursorFactory.NW_RESIZE);
				case PicGroup.UM: return cursorFactory.getPECursor(CursorFactory.N_RESIZE);
				case PicGroup.UR: return cursorFactory.getPECursor(CursorFactory.NE_RESIZE);
				case PicGroup.ML: return cursorFactory.getPECursor(CursorFactory.W_RESIZE);
				case PicGroup.MR: return cursorFactory.getPECursor(CursorFactory.E_RESIZE);
				case PicGroup.LL: return cursorFactory.getPECursor(CursorFactory.SW_RESIZE);
				case PicGroup.LM: return cursorFactory.getPECursor(CursorFactory.S_RESIZE);
				case PicGroup.LR: return cursorFactory.getPECursor(CursorFactory.SE_RESIZE);
				default:
				}
			}
			return cursorFactory.getPECursor(CursorFactory.MOVE_ENDPT);
		}

	}

	/**
	 * a mouse-transform that moves element's end-points, ie aka scales elements.  Code is heavily dependent
	 * on the run-time class of the target element.  [pending] one shortcoming of the present approach is that
	 * <code>EditPointConstraint</code>'s are set at init time (i.e. in the constructor), which forbids any
	 * further modification (e.g.  if the user press the control key AFTER starting to drag a point).
	 */
	protected class MoveControlPointTransform implements MouseTransform {
		// new code w/o cloning element before editing it (the "blue" copy...)
		private Element target;
		private Grid grid;
		private int pointIndex;
		private PicPoint ptBuffer = new PicPoint();
		private EditPointConstraint constraint;
		private String helpMessage;

		/**
		 * @param target the element upon which this transform will act
		 * @param pointIndex index of the <code>Element</code>'s point that will be moved
		 * @param grid the Grid instance used for alignment (if it's snap-on)
		 * @param constraint the geometrical constraint to be used when moving control-points ; may be null
		 * @param helpMessage if non-null, this will be displayed on mouse-move events instead of the default
		 *        help-message for this mouse-transform.
		 * @see jpicedt.graphic.model.Element#setCtrlPt
		 */
		public MoveControlPointTransform(Element target, int pointIndex, EditPointConstraint constraint,
		                                 String helpMessage, Grid grid){

			this.target = target;
			this.pointIndex = pointIndex;
			this.grid = grid;
			this.constraint = constraint;
			this.helpMessage = helpMessage;
			if (DEBUG) debug(toString());
		}

		/**
		 * Called when the mouse is pressed. This just fires a begin-undoable-event.<br>
		 */
		public void start(PEMouseEvent e){
			e.getCanvas().beginUndoableUpdate(localize("action.editorkit.Scale.tooltip"));
		}

		/**
		 * Called when the mouse is dragged.
		 * Sets the Element's point (with the index and the constraint given as a parameter in the constructor)
		 * to the current mouse position, or its nearest-neighbour on the grid if
		 * grid-snap is on.
		 */
		public void process(PEMouseEvent e){
			PicPoint pt = e.getPicPoint();
			grid.nearestNeighbour(pt,pt); // pt = nn(pt)
			if (target.getCtrlPt(pointIndex,ptBuffer).equals(pt)) return; // compare old and new point and return if nothing has moved
			target.setCtrlPt(pointIndex,pt,constraint);
			if (DEBUG) debugAppendLn("target=" + target);

		}

		/**
		 * Called when the mouse is released. This fires an end-undoable-event.
		 */
		public boolean next(PEMouseEvent e){
			PicPoint pt = e.getPicPoint();
			grid.nearestNeighbour(pt,pt); // pt = nn(pt)
			target.setCtrlPt(pointIndex,pt,constraint);
			e.getCanvas().endUndoableUpdate();
			return false;
		}

		/**
		 * Does nothing. Nothing to painted specifically for this tool.
		 */
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			// [SR:pending] adapt according to target and constraint
			if (helpMessage==null) return "help-message.MoveEndPointTransform"; // [SR:pending] change to MoveControlPointTransform
			else return helpMessage;
		}

		/** @return a textual representation of this transform for debugging purpose */
		public String toString(){
			return "[MoveControlPointTransform : \n\tpointIndex = " + pointIndex
			       + "\n\ttarget = " + target ;

		}

		/**
		 * @return a cursor adequate with this mouse-transform, delegating to CursorFactory.
		 * If the target-element of <code>PicGroup</code>, returns a cursor according to
		 * <code>pointIndex</code>.
		 */
		public Cursor getCursor(){
			//[SR:en_cours]if (setPointFromCenter) return cursorFactory.getPECursor(CursorFactory.MOVE_ENDPT_FROM_CENTER);
			if (target instanceof PicGroup){
				switch (pointIndex){
				case PicGroup.UL: return cursorFactory.getPECursor(CursorFactory.NW_RESIZE);
				case PicGroup.UM: return cursorFactory.getPECursor(CursorFactory.N_RESIZE);
				case PicGroup.UR: return cursorFactory.getPECursor(CursorFactory.NE_RESIZE);
				case PicGroup.ML: return cursorFactory.getPECursor(CursorFactory.W_RESIZE);
				case PicGroup.MR: return cursorFactory.getPECursor(CursorFactory.E_RESIZE);
				case PicGroup.LL: return cursorFactory.getPECursor(CursorFactory.SW_RESIZE);
				case PicGroup.LM: return cursorFactory.getPECursor(CursorFactory.S_RESIZE);
				case PicGroup.LR: return cursorFactory.getPECursor(CursorFactory.SE_RESIZE);
				default:
				}
			}
			return cursorFactory.getPECursor(CursorFactory.MOVE_ENDPT);
		}

	}




	/////////////////////////////////////////////////////////////////////////////////
	//// MOVE MT
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * A mouse-transform that can translate an element, or a group of element.
	 */
	protected class _MoveElementTransform extends AbstractMouseTransform {
		// old code : first clone element, then move the clone, finally update original.
		// this was aimed at reducing the burden for the GUI repaint manager, however
		// this looks real odd now, after a couple of months... i don't like it, definitely ;-(

		private Element anchor;
		private boolean useChildAsAnchor; // set by constructors, used by startTransform
		private int anchorChildIndex; // set by first constructor, used by startTransform
		private PicPoint lastDragPoint;
		private Grid grid;
		private double totalMouseLeapX, totalMouseLeapY;

		/**
		 * @param target the selection-handler upon which this transform acts (globally)
		 * @param anchorChildIndex index of target's child that will serve as
		 *        the reference-child for grid alignment ; if null, target is used instead ;
		 * @param clickPt
		 */
		public _MoveElementTransform(BranchElement target, int anchorChildIndex, PicPoint clickPt, Grid grid){

			super(target); // clone target
			this.anchorChildIndex = anchorChildIndex;
			useChildAsAnchor = true;
			this.lastDragPoint = new PicPoint(clickPt); // save click point
			this.grid = grid;
		}

		/**
		 * @param target the selection-handler upon which this transform acts ; also serve
		 *        as the anchor for grid alignment.
		 * @param clickPt
		 */
		public _MoveElementTransform(Element target, PicPoint clickPt, Grid grid){

			super(target);
			useChildAsAnchor = false;
			this.lastDragPoint = new PicPoint(clickPt); // save click point
			this.grid = grid;
		}

		/**
		 * Called when the mouse is pressed.<br>
		 * Create a clone of the clickedElement given as the parameter in the constructor.
		 */
		public void start(PEMouseEvent e){
			super.start(e);
			totalMouseLeapX =0.0;
			totalMouseLeapY =0.0;
			if (useChildAsAnchor)
				this.anchor = ((BranchElement)getClone()).get(anchorChildIndex); // !!! anchor must move with the clone
			else
				this.anchor = getClone(); // !!! anchor must move with the clone !!!
		}


		/**
		 * @return a Cursor whose type is adequate with this mouse-transform.
		 */
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.MOVE_ONE_ONLY);
		}

		/**
		 * Called when the mouse is dragged.
		 */
		public void process(PEMouseEvent me){

			if (DEBUG) debug("mouse = " + me.getPicPoint());
			double mouseLeapX = me.getPicPoint().x-lastDragPoint.x;
			double mouseLeapY = me.getPicPoint().y-lastDragPoint.y;

			/* Grid alignment : the main idea is that the object gets "snapped" to the grid point
			 * that's the nearest from any of its anchor points.
			 * OK, here we go :
			 * for every anchor points A(i) "produced" by the clicked object :
			 *    1/ we translate A(i) to B(i) by the mouse translation vector (current pt minus last drag point)
			 *    2/ we get the nearest neighbour of B(i) on the grid. Let's call it N(i).
			 *    3/ let d(i) = B(i)N(i), that is, the distance between an anchor point (once translated) and its nearest neighbour on the grid.
			 *
			 * Now, the anchor point with the minimum d(i) WINS the race.
			 * We then compute the effective translation vector A(i)N(i).
			 * - If it's 0, we do nothing since that means that the mouse leap is too small, and wait the next call to doTransform.
			 * - Else, we move the whole selection by this vector.
			 */
			if (grid.isSnapOn()){
				PointIndexIterator it = anchor.anchorPointsIterator();
				if (!it.hasNext()) return; // security check

				double d, newD;
				double dx=0.0;
				double dy=0.0;
				PicPoint ptB=new PicPoint();
				PicPoint ptN=new PicPoint();
				d = Double.MAX_VALUE; // ensure newD is properly init'd
				while(it.hasNext()){
					ptB = anchor.getCtrlPt(it.next(),ptB); // get A(i)
					ptB.translate(mouseLeapX,mouseLeapY); // fake move A->B
					ptN = grid.nearestNeighbour(ptB,ptN); // find nearest-neighbour of B on the grid
					newD = ptN.distanceSq(ptB); // take the square dist. to avoid Math.sqrt...
					if (newD < d) { // always true the first time
						d = newD;
						// if this anchor point eventually turns out to be the winner, we'll have :
						dx = ptN.x - ptB.x + mouseLeapX;
						dy = ptN.y - ptB.y + mouseLeapY;
					}
				}
				if (dx == 0 && dy == 0) return; // nothing changed since last call to doTransform
				if (DEBUG) debugAppendLn("dx="+dx+" dy="+dy);
				// ok, the mouse's leap's been big enough :
				getClone().translate(dx, dy); // fire changed event
				// and update lastDragPoint according to the REAL translation vector
				lastDragPoint.translate(dx, dy);
				totalMouseLeapX += dx;
				totalMouseLeapY += dy;
			}
			else {
				if (DEBUG) debugAppendLn("dx="+mouseLeapX+" dy="+mouseLeapY);
				// ok, the mouse's leap's been big enough :
				getClone().translate(mouseLeapX, mouseLeapY); // fire changed event
				// and update lastDragPoint according to the REAL translation vector
				lastDragPoint.translate(mouseLeapX, mouseLeapY);
				totalMouseLeapX += mouseLeapX;
				totalMouseLeapY += mouseLeapY;
			}
		}

		public boolean next(PEMouseEvent e){
			e.getCanvas().beginUndoableUpdate(localize("action.editorkit.Translate.tooltip"));
			getTarget().translate(totalMouseLeapX,totalMouseLeapY);
			e.getCanvas().endUndoableUpdate();
			return super.next(e); // false
		}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			//return Options.getLocale("StatusBarMessageMove");
			return "help-message.MoveTransform"; // [SR:pending] change to MoveElementTransform
		}

		public String toString(){
			return "[MoveElementTransform : \n\tlastDragPoint = "
			       + lastDragPoint + "\n\tanchor = "  + anchor;
		}

	} // MoveElementTransform


	/**
	 * A mouse-transform that can translate an element, or a group of element.
	 */
	protected class MoveElementTransform implements MouseTransform {
		// [SR:29/08/2003] new code w/o cloning element before moving (note how this is easily done by removing
		// inheritance from AbstractMouseTransform ;-)

		private Element target,anchor; // move target, use anchor for grid alignment
		private PicPoint lastDragPoint;
		private Grid grid;

		/**
		 * @param target the selection-handler upon which this transform acts (globally)
		 * @param anchorChildIndex index of target's child that will serve as
		 *        the reference-child for grid alignment ; if null, target is used instead ;
		 * @param clickPt
		 */
		public MoveElementTransform(BranchElement target, int anchorChildIndex, PicPoint clickPt, Grid grid){

			this.target=target;
			this.anchor = ((BranchElement)target).get(anchorChildIndex);
			this.lastDragPoint = new PicPoint(clickPt); // save click point
			this.grid = grid;
		}

		/**
		 * @param target the selection-handler upon which this transform acts ; also serve
		 *        as the anchor for grid alignment.
		 * @param clickPt
		 */
		public MoveElementTransform(Element target, PicPoint clickPt, Grid grid){

			this.target=target;
			this.anchor = target;
			this.lastDragPoint = new PicPoint(clickPt); // save click point
			this.grid = grid;
		}

		/**
		 * Called when the mouse is pressed.<br>
		 */
		public void start(PEMouseEvent e){
			e.getCanvas().beginUndoableUpdate(localize("action.editorkit.Translate.tooltip"));
		}


		/**
		 * @return a Cursor whose type is adequate with this mouse-transform.
		 */
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.MOVE_ONE_ONLY);
		}

		/**
		 * Called when the mouse is dragged.
		 */
		public void process(PEMouseEvent me){

			if (DEBUG) debug("mouse = " + me.getPicPoint());
			double mouseLeapX = me.getPicPoint().x-lastDragPoint.x;
			double mouseLeapY = me.getPicPoint().y-lastDragPoint.y;

			/* Grid alignment : the main idea is that the object gets "snapped" to the grid point
			 * that's the nearest from any of its anchor points.
			 * OK, here we go :
			 * for every anchor points A(i) "produced" by the clicked object :
			 *    1/ we translate A(i) to B(i) by the mouse translation vector (current pt minus last drag point)
			 *    2/ we get the nearest neighbour of B(i) on the grid. Let's call it N(i).
			 *    3/ let d(i) = B(i)N(i), that is, the distance between an anchor point (once translated) and its nearest neighbour on the grid.
			 *
			 * Now, the anchor point with the minimum d(i) WINS the race.
			 * We then compute the effective translation vector A(i)N(i).
			 * - If it's 0, we do nothing since that means that the mouse leap is too small, and wait the next call to doTransform.
			 * - Else, we move the whole selection by this vector.
			 */
			if (grid.isSnapOn()){
				PointIndexIterator it = anchor.anchorPointsIterator();
				if (!it.hasNext()) return; // security check

				double d, newD;
				double dx=0.0;
				double dy=0.0;
				PicPoint ptB=new PicPoint();
				PicPoint ptN=new PicPoint();
				d = Double.MAX_VALUE; // ensure newD is properly init'd
				while(it.hasNext()){
					ptB = anchor.getCtrlPt(it.next(),ptB); // get A(i)
					ptB.translate(mouseLeapX,mouseLeapY); // fake move A->B
					ptN = grid.nearestNeighbour(ptB,ptN); // find nearest-neighbour of B on the grid
					newD = ptN.distanceSq(ptB); // take the square dist. to avoid Math.sqrt...
					if (newD < d) { // always true the first time
						d = newD;
						// if this anchor point eventually turns out to be the winner, we'll have :
						dx = ptN.x - ptB.x + mouseLeapX;
						dy = ptN.y - ptB.y + mouseLeapY;
					}
				}
				if (dx == 0 && dy == 0) return; // nothing changed since last call to doTransform
				if (DEBUG) debugAppendLn("dx="+dx+" dy="+dy);
				// ok, the mouse's leap's been big enough :
				target.translate(dx, dy); // fire changed event
				// and update lastDragPoint according to the REAL translation vector
				lastDragPoint.translate(dx, dy);
			}
			else {
				if (DEBUG) debugAppendLn("dx="+mouseLeapX+" dy="+mouseLeapY);
				// ok, the mouse's leap's been big enough :
				target.translate(mouseLeapX, mouseLeapY); // fire changed event
				// and update lastDragPoint according to the REAL translation vector
				lastDragPoint.translate(mouseLeapX, mouseLeapY);
			}
		}

		public boolean next(PEMouseEvent e){
			e.getCanvas().endUndoableUpdate();
			return false;
		}

		public void paint(Graphics2D g, Rectangle2D allocation, double scale){}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.MoveTransform"; // [SR:pending] change to MoveElementTransform
		}

		public String toString(){
			return "[MoveElementTransform : \n\tlastDragPoint = "
			       + lastDragPoint + "\n\tanchor = "  + anchor;
		}

	} // MoveElementTransform









	/////////////////////////////////////////////////////////////////////////////////
	//// SELECTION
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * a mouse-transform that selects all elements inside a rectangle dragged by the user
	 */
	protected class SelectElementsInAreaTransform extends SelectAreaTransform {

		private Element target;
		private boolean addToSelection;

		/** @param target if non-null, this element will be selected by a call to "start", before
		 * starting to draw the selection rectangle (click on a non-selected element) */
		public SelectElementsInAreaTransform(Element target, boolean addToSelection){
			this.target = target;
			this.addToSelection = addToSelection;
		}

		/** called by mousePressed */
		public void start(PEMouseEvent e){
			super.start(e);
			if (!addToSelection) e.getCanvas().unSelectAll();
			if (target != null) {
				e.getCanvas().select(target,INCREMENTAL); // incremental
			}
		}

		/**
		 * Called when the mouse is released. Selects every elements inside the selection area,
		 * including the element being currently under the cursor.
		 */
		public boolean next(PEMouseEvent e){
			super.next(e);
			// first, if there's an element under the cursor, select it (if it was already selected, this
			// has no effet :
			/* [sr: bug fix] now that hitTest(e,false) returns non-selected element only, the following piece of code
			is bug-prone: if there's more than one element under the cursor, and the mouse ain't moved since start() was called,
			that'd select the one with index "1" (because that with index "0" would've already been selected at <init> time)
			Hence we'd --- weirdly enough --- end up with two selected elements

			HitInfo hi = getEditorKit().hitTest(e,false); // whole drawing
			if (hi != null) {
				e.getCanvas().select(hi.getTarget(),INCREMENTAL); // add to selection
			}*/

			// compute number of objects contained in the selection area
			Rectangle2D rectArea = getSelectionRectangle();
			Drawing drawing = e.getCanvas().getDrawing();
			ArrayList<Element> list = getEditorKit().intersect(rectArea, false); // non-selected elements only

			/*for(Element o: drawing){
					for(int ptIndex=o.getFirstPointIndex(); ptIndex <= o.getLastPointIndex(); ptIndex++){
						ptBuffer = o.getCtrlPt(ptIndex,ptBuffer);
						if (rectArea.contains(ptBuffer.x, ptBuffer.y)){
							list.add(o);
							break;
						}
					}
			}*/
			if (!list.isEmpty())
				e.getCanvas().selectCollection(list,INCREMENTAL); // incremental (add to selection)
			return false;
		}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.SelectArea";
		}

		/** @return a textual representation of this transform for debugging purpose */
		public String toString(){
			return "[SelectElementsInAreaTransform]: target="+target+", incremental="+addToSelection;

		}

	} // SelectElementsInAreaTransform

	////////////////////////// DIALOG transform ////////////////////////

	/**
	 * a mouse-transform that opens up a Dialog to allow the user to selects elements by hand
	 */
	protected class SelectElementsDialogTransform extends JPanel implements MouseTransform {

		private ArrayList<Element> elements;

		/**
		 */
		public SelectElementsDialogTransform(HitInfo hi){
			// create elements:
			elements = new ArrayList<Element>();
			if (hi instanceof HitInfo.List){
				for (HitInfo hit: (HitInfo.List)hi){
					elements.add(hit.getTarget());
				}
			}
			else
				elements.add(hi.getTarget());
			if (DEBUG) debug("elements:" + elements);

			JTable table = new JTable(new Model());
			JScrollPane scrollpane = new JScrollPane(table);
			add(scrollpane);
		}

		/** called by mousePressed */
		public void start(PEMouseEvent e){
			String title=localize("action.editorkit.SelectElementsDialog");
			boolean modal = false;
			MDIComponent dlg = getEditorKit().getDialogFactory().createDialog(title,modal,this);
			dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dlg.setVisible(true);
		}

		/**
		 * Called when the mouse is dragged.
		 * Sets the Element's point (with the index and the constraint given as a parameter in the constructor)
		 * to the current mouse position, or its nearest-neighbour on the grid if
		 * grid-snap is on.
		 */
		public void process(PEMouseEvent e){
		}

		/**
		 * Called when the mouse is released. Selects every elements inside the selection area,
		 * including the element being currently under the cursor.
		 */
		public boolean next(PEMouseEvent e){
			return false;
		}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.SelectDialog";
		}

		/** @return a textual representation of this transform for debugging purpose */
		public String toString(){
			return "[SelectElementsDialogTransform]";

		}

		/**
		 * Does nothing. Nothing to painted specifically for this tool.
		 */
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){}

		/**
		 * @return a cursor adequate with this mouse-transform, delegating to CursorFactory.
		 */
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.SELECT);
		}

		class Model extends javax.swing.table.AbstractTableModel {

			public String getColumnName(int colIndex) {
				switch (colIndex) {
				case 0:
					return "Element";
				case 1:
					return "Selection";
				default:
					return "";
				}
			}

			public int getColumnCount() {
				return 2;// 2 columns : element, selection-state
			}

			public int getRowCount() {
				return elements.size();// there are as many rows as elements to be (un)selected
			}

			public boolean isCellEditable(int rowIndex, int colIndex) {
				if (colIndex == 0)
					return false;// first column is not editable since it containts the element's name
				else
					return true;
			}

			/**
			 * Called when the associated JTable wants to know what to display at
			 * columnIndex and rowIndex.
			 */
			public Object getValueAt(int rowIndex, int colIndex) {

				switch (colIndex) {
				case 0: // String
					return elements.get(rowIndex).getName();
				case 1:
					return new Boolean(getEditorKit().getSelectionHandler().contains(elements.get(rowIndex))); // look up ancestor
				default:
					return null;
				}
			}

			public Class getColumnClass(int c) {
				switch (c) {
				case 0: return String.class;
				case 1: return Boolean.class;
				default: return null;
				}
			}


			/**
			 * Invoked by the UI (aka event-handler) when a user entered a new value in the cell at columnIndex and
			 * rowIndex.
			 */
			public void setValueAt(Object value, int rowIndex, int colIndex) {

				switch (colIndex) {
				case 1:
					if ((Boolean)value)
						getEditorKit().getSelectionHandler().add(elements.get(rowIndex)); // or replaceSelection()
					else
						getEditorKit().getSelectionHandler().remove(elements.get(rowIndex));
					break;
				default:
					return;
				}
				fireTableDataChanged();
			}
		}// inner class

	} // MouseTransformFactory
}
