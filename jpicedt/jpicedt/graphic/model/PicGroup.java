// PicGroup.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006  Sylvain Reynal
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
// Version: $Id: PicGroup.java,v 1.37 2013/03/27 07:01:49 vincentb1 Exp $
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
package jpicedt.graphic.model;

import jpicedt.graphic.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.widgets.*;
import jpicedt.graphic.view.*;
import jpicedt.graphic.view.highlighter.Highlighter;
import jpicedt.graphic.view.highlighter.CompositeHighlighter;
import jpicedt.ui.dialog.UserConfirmationCache;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import static jpicedt.graphic.view.highlighter.CompositeHighlighter.*;

import static java.lang.Math.abs;

/**
 * A concrete implementation of <code>BranchElement</code> that is suited for building groups of elements.  It
 * allows easy global resize/translate transformation by providing 8 control points that can be used by mouse
 * operation.
 *
 * @since PicEdt 1.0
 * @author Sylvain Reynal
 */
public class PicGroup extends BranchElement implements CustomizerFactory, ActionFactory{

	////////////////////////////
	//// PUBLIC CONSTANT FIELDS
	////////////////////////////

	// warning ! these are mnemonics only : keep in mind that UL can becom LR after a call to setCtrlPt...
	public static final int FIRST_PT = 0;
	public static final int UL = 0; // U = upper M = mid L = lower or left R = right
	public static final int UM = 1;
	public static final int UR = 2;
	public static final int ML = 3;
	public static final int MR = 4;
	public static final int LL = 5;
	public static final int LM = 6;
	public static final int LR = 7;
	public static final int LAST_PT = 7;

	//////////////////////////////
	/// CONSTRUCTORS
	//////////////////////////////

	/**
	 * construct a PicGroup with no parent and a default PicAttributeSet
	 */
	public PicGroup(){
		super();
	}

	/**
	 * construct a PicGroup with no parent and the given PicAttributeSet
	 */
	public PicGroup(PicAttributeSet attributeSet){
		super(attributeSet);
	}

	/**
	 * Cloning constructor.
	 * Simply call superclass's constructor.
	 */
	public PicGroup(BranchElement src){
		super(src);
	}

	/**
	 * Simply call superclass's constructor.
	 */
	public PicGroup(Collection<? extends Element> c){
		super(c);
	}

	/**
	 * Return a deep copy of this PicGroup
	 * @since PicEdt 1.0
	 */
	public PicGroup clone(){
		return new PicGroup(this);
	}

	/**
	 * @return a localised string that represents this object's name
	 */
	public String getDefaultName(){
		return jpicedt.Localizer.currentLocalizer().get("model.Group");
	}


	/////////////////////////////////////////////////////////
	//// OPERATIONS ON CONTENT (aka ArrayList implementation)
	/////////////////////////////////////////////////////////

	/**
	 * Fetch all <code>Element</code>'s belonging to the given <code>PicGroup</code> and add them to its
	 * parent, removing the given <code>PicGroup</code> from its parent afterward.
	 */
	public void unGroup(){
		PECanvas cv = null;
		View v = getView();
		if (v != null){
			cv = v.getContainer();
		}
		boolean wasSelected = false;
		if (cv != null){
			wasSelected = cv.isSelected(this);
			cv.unSelect(this);
		}
		BranchElement p = getParent();
		p.remove(this);
		int max = size();
		for (int i=0; i<max; i++){
			Element o = get(0); // always fetch at position 0
			p.add(o); // hence removed from group (was former parent)
			if (cv != null && wasSelected)
				cv.select(o,PECanvas.SelectionBehavior.INCREMENTAL);
		}
	}

	//////////////////////////////////
	//// EVENTS HANDLING
	//////////////////////////////////

	// inherited

	//////////////////////////////////
	/// OPERATIONS ON CONTROL POINTS
	//////////////////////////////////

	/**
	 * @return the index of the first point that can be retrieved by getCtrlPt
	 * This default implementation returns 0.
	 */
	public int getFirstPointIndex(){
		return FIRST_PT;
	}

	/**
	 * @return the index of the last point that can be retrieved by getCtrlPt
	 * This default implementation returns the greater index allowed in ptsX (or ptsY).
	 */
	public int getLastPointIndex(){
		return LAST_PT;
	}

	/**
	 * @return the X-coord of the point indexed by <code>numPoint</code>.
	 * @param numPoint the point index, should be greater or equal to the value returned by
	 *        <code>getFirstPointIndex</code>, and lower or equal to <code>getLastPointIndex</code>.
	 * @since PicEdt 1.0
	 */
	public double getCtrlPtX(int numPoint){
		if (numPoint==UL || numPoint==ML || numPoint==LL) return ptsX[0];
		if (numPoint==UM || numPoint==LM) return 0.5*(ptsX[0]+ptsX[1]);
		if (numPoint==UR || numPoint==MR || numPoint==LR) return ptsX[1];
		throw new IndexOutOfBoundsException(new Integer(numPoint).toString());
	}

	/**
	 * @return the Y-coord of the point indexed by <code>numPoint</code>.
	 * @param numPoint the point index, should be greater or equal to the value returned by
	 *        <code>getFirstPointIndex</code>, and lower or equal to <code>getLastPointIndex</code>.
	 * @since PicEdt 1.0
	 */
	public double getCtrlPtY(int numPoint){
		if (numPoint==UL || numPoint==UM || numPoint==UR) return ptsY[1];
		if (numPoint==ML || numPoint==MR) return 0.5*(ptsY[0]+ptsY[1]);
		if (numPoint==LL || numPoint==LM || numPoint==LR) return ptsY[0];
		throw new IndexOutOfBoundsException(new Integer(numPoint).toString());
	}

	/**
	 * Set the point numbered by <code>numPoint</code> to the <code>pt</code> value, and scales inward object
	 * accordingly.
	 * @param constraint not used here, may be safely set to null
	 * @since PicEdt 1.0
	 */
	public void setCtrlPt(int numPoint, PicPoint ptNew, EditPointConstraint constraint){

		double ptOrgX, ptOrgY;
		double ptOldX = getCtrlPtX(numPoint);
		double ptOldY = getCtrlPtY(numPoint);
		// Sélectionne l'origine de l'homothétie comme diamétralement opposée
		// au point qu'on veut régler.
		switch (numPoint){
		case UL:
			ptOrgX = getCtrlPtX(LR);ptOrgY = getCtrlPtY(LR);
			break;
		case UM:
			ptOrgX = getCtrlPtX(LL); ptOrgY = getCtrlPtY(LL); // LR would do as well
			ptOldX = ptNew.x; // sx=1
			break;
		case UR: //
			ptOrgX = getCtrlPtX(LL);ptOrgY = getCtrlPtY(LL);
			break;
		case ML:
			ptOrgX = getCtrlPtX(UR);ptOrgY = getCtrlPtY(UR); // LR would do as well
			ptOldY = ptNew.y; // sy=1
			break;
		case MR:
			ptOrgX = getCtrlPtX(UL);ptOrgY = getCtrlPtY(UL);// LL would do as well
			ptOldY = ptNew.y; // sy=1
			break;
		case LL:
			ptOrgX = getCtrlPtX(UR);ptOrgY = getCtrlPtY(UR);
			break;
		case LM:
			ptOrgX = getCtrlPtX(UL);ptOrgY = getCtrlPtY(UL); // UR would do as well
			ptOldX = ptNew.x; // sx=1
			break;
		case LR:
			ptOrgX = getCtrlPtX(UL);ptOrgY = getCtrlPtY(UL);
			break;
		default:
			throw new IndexOutOfBoundsException(new Integer(numPoint).toString());
		}
		double sx,sy;
		if(ptOldX == ptOrgX && ptOldX == ptNew.x)
			sx = 1.0;
		else if(ptOldX == ptOrgX)
			return; // division by zero
		else
			sx = (ptNew.x - ptOrgX)/(ptOldX - ptOrgX);

		if(ptOldY == ptOrgY && ptOldY == ptNew.y)
			sy = 1.0;
		else if(ptOldY == ptOrgY)
			return; // division by zero
		else
			sy = (ptNew.y - ptOrgY)/(ptOldY - ptOrgY);

		if (sx == 0 || sy == 0)
			return; // if we scale by 0, everything gets reduced to a point...

		UserConfirmationCache ucc = constraint != null ? constraint.getUserConfirmationCache() : null;
		scale(ptOrgX,ptOrgY,sx, sy, ucc); // fire changed event
	}


	/**
	 * @return an Iterator over points that can serve as anchor points for grid alignment.
	 *         This implementation returns a default <code>PointIterator</code> which simply iterates through
	 *         control points.
	 */
	public PointIndexIterator anchorPointsIterator(){
		return new GroupPointIterator();
	}

	/**
	 * An iterator over <code>PicPoint</code>'s that can serve as anchors for grid alignment.  This
	 * implementation iterates over the 4 control points : <code>UL</code>, <code>UR</code>, <code>LL</code>
	 * and <code>LR</code>.
	 */
	public class GroupPointIterator implements PointIndexIterator {

		protected int counter;
		private int[] anchors = {UL, UR, LL, LR};

		/**
		 * Create a new <code>PointIterator</code>.
		 */
		public GroupPointIterator(){
			super();
			counter = 0;
		}

		public void reset(){
			counter=0;
		}
		/**
		 * @return <code>true</code> if the iteration has more elements.
		 */
		public boolean hasNext(){
			if (counter > 3) return false;
			return true;
		}

		/**
		 * @return the next <code>PicPoint</code> in the iteration ; this doesn't create a new
		 *         <code>PicPoint</code>, hence the returned value might change over time.
		 */
		public int next(){
			counter++;
			return anchors[counter-1];
		}
	}

	///////////////////////////////
	//// STRING FORMATING
	///////////////////////////////

	/**
	 * Returns a String representing the group for debugging use only.
	 */
	public String toString(){

		String s = "[" + getName() + "@" + Integer.toHexString(hashCode()) + "{";
		int j=0;
		for(Element e: this){
			StringBuffer buf = new StringBuffer(e.toString());
			int pos=0;
			while(true){
				pos = buf.toString().indexOf('\n',pos);
				if (pos == -1) break;
				buf.insert(pos+1,'\t');
				pos += 2;
			}
			s += "\n\t" + j + ":" + buf;
			j++;
		}
		return s + "\n}]";
	}



	/////////////////////////////////
	/// View related
	/////////////////////////////////
	/**
	 * set the current highlighting mode ; this influences the way the <code>Highligther</code> is painted,
	 * but also the result returned by <code>HitTest</code>.
	 * @param mode or <code>LOCAL_MODE</code> or <code>GLOBAL_MODE</code>
	 */
	public void setHighlightingMode(HighlightingMode mode){
		if (view==null) return;
		updateBoundingBox();
		Highlighter h = view.getHighlighter();
		if (h instanceof CompositeHighlighter){
			CompositeHighlighter hc = (CompositeHighlighter)h;
			hc.setHighlightingMode(mode);
		}
	}

	/**
	 * Return the current highlighting mode
	 */
	public HighlightingMode getHighlightingMode(){
		if (view==null) return null;
		Highlighter h = view.getHighlighter();
		if (h instanceof CompositeHighlighter){
			CompositeHighlighter hc = (CompositeHighlighter)h;
			return hc.getHighlightingMode();
		}
		return null;
	}

	/**
	 * Toggle the current highlighting mode
	 */
	public void toggleHighlightingMode(){
		if (view==null) return;
		updateBoundingBox();
		Highlighter h = view.getHighlighter();
		if (h instanceof CompositeHighlighter){
			CompositeHighlighter hc = (CompositeHighlighter)h;
			hc.toggleHighlightingMode();
			switch (hc.getHighlightingMode()){ // new mode
			case LOCAL: // nothing to do
				break;
			case GLOBAL: // possibly unselect children, and select group itself
				PECanvas canvas = view.getContainer();
				canvas.unSelect(this); // unselect children
				canvas.select(this, PECanvas.SelectionBehavior.INCREMENTAL);
				break;
			}
		}
	}


	////////////////////////////////
	//// GUI
	////////////////////////////////


	/**
	 * Creates an array of <code>Action</code>'s related to this object.
	 */
	public ArrayList<PEAction> createActions(ActionDispatcher actionDispatcher, ActionLocalizer localizer, HitInfo hi) {

		// super's actions: edit geom, pull out of parent group and parent group's action if applicable,
		ArrayList<PEAction> actionArray = super.createActions(actionDispatcher, localizer, hi);
		if (actionArray==null)
			actionArray = new ArrayList<PEAction>();

		// local/global group mode:
		if (view !=null && view.getHighlighter()!=null && view.getHighlighter() instanceof CompositeHighlighter){
			actionArray.add(new ToggleHighlightingModeAction(actionDispatcher, localizer));
		}

		// PSCustom:
		actionArray.add(new ToggleCompoundModeAction(actionDispatcher, localizer, getCompoundMode()));

		if (getCompoundMode() == CompoundMode.JOINT)
			actionArray.add(new ClosePathAction(actionDispatcher, isPathClosed() ? ClosePathAction.OPEN : ClosePathAction.CLOSE, localizer));

		// ungroup:
		actionArray.add(new UngroupAction(actionDispatcher, localizer));

		// clicked child's actions:
		if (hi!=null){
			Element clickedChild = hi.getTarget();
			if (clickedChild == this && hi instanceof HitInfo.Composite){ // ie if HI.Composite
				clickedChild = ((HitInfo.Composite)hi).getClickedChild();
				if (clickedChild != null && clickedChild instanceof ActionFactory){
					ArrayList<PEAction> childAA = ((ActionFactory)clickedChild).createActions(actionDispatcher, localizer, hi);
					if (childAA != null){
						actionArray.add(null); // separator
						actionArray.addAll(childAA);
					}
				}
			}
		}

		return actionArray;
	}

	/**
	 * <code>ToggleHighlightingModeAction</code> may appear in <code>AbstractElement</code>'s action menu,
	 * hence this factory method.
	 */
	PEAction createToggleHighlightingModeAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
		return new ToggleHighlightingModeAction(actionDispatcher, localizer);
	}

	/**
	 * Toggle the attached View's highlightingMode b/w LOCAL_MODE and GLOBAL_MODE
	 * @see jpicedt.graphic.view.DefaultHighlighterFactory
	 */
	class ToggleHighlightingModeAction extends PEAction {

		public static final String KEY = "action.editorkit.ToggleGroupHighlightingMode";

		public ToggleHighlightingModeAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer) {
			super(actionDispatcher, KEY, localizer);
		}

		public void actionPerformed(ActionEvent e) {
			toggleHighlightingMode();
		}
	}

	/**
	 * Toggle the current CompoundMode b/w <code>JOINT</code> and <code>SEPARATE</code>.
	 * @since     jpicedt 1.5
	 */
	class ToggleCompoundModeAction extends PEAction {


		public ToggleCompoundModeAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer, CompoundMode cm) {
			super(actionDispatcher,
				  (cm == CompoundMode.JOINT)
				  ? "action.editorkit.ToggleGroupCompoundModeJoint"
				  : "action.editorkit.ToggleGroupCompoundModeSeparate", localizer);
		}

		public void actionPerformed(ActionEvent e) {
			toggleCompoundMode();
		}
	}

	/**
	 * Closes the path (if applicable, ie, if <code>CompoundMode</code> is set to <code>JOINT</code>).
	 * @since     jpicedt 1.5
	 */
	class ClosePathAction extends PEAction {
		final static String CLOSE = "action.editorkit.ClosePath";
		final static String OPEN = "action.editorkit.OpenPath";
		private String type;

		/**
		 * @param type CLOSE or OPEN
		 */
		public ClosePathAction(ActionDispatcher actionDispatcher, String type, ActionLocalizer localizer) {
			super(actionDispatcher, type, localizer);
			this.type = type;
		}

		public void undoableActionPerformed(ActionEvent e) {
			if (type.equals(CLOSE))
				closePath();
			else if (type.equals(OPEN))
				openPath();
		}
	}


	// ---- ungroup selected group ----

	/**
	 * Ungroup the current selection if this makes sense.
	 */
	class UngroupAction extends PEAction {

		public static final String KEY = "action.editorkit.UngroupSelection";

		public UngroupAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}

		public void undoableActionPerformed(ActionEvent e){
			unGroup();
		}
	}

	/**
	 * @return A <code>Customizer</code> for geometry editing.
	 */
	public AbstractCustomizer createCustomizer(){
		if (cachedCustomizer == null)
			cachedCustomizer = new Customizer();
		cachedCustomizer.load();
		return cachedCustomizer;
	}

	private Customizer cachedCustomizer = null;


	/**
	 * Geometry customizer.
	 */
	class Customizer extends AbstractCustomizer implements ActionListener {

		private DecimalNumberField groupBLCornerXTF, groupBLCornerYTF, groupWidthTF, groupHeightTF;
		private JCheckBox isClosedPathCB, isCustomPathCB;
		private boolean isListenersAdded = false; // flag

		public Customizer(){

			super();

			JPanel p = new JPanel(new GridLayout(5,3,5,5));

			// line 1: bottom left corner label
			p.add(PEToolKit.createJLabel("attributes.RectangleBLCorner"));
			// x-coordinate of ll-corner
			p.add(groupBLCornerXTF = new DecimalNumberField(4));
			// y-coordinate of ll-corner
			p.add(groupBLCornerYTF = new DecimalNumberField(4));

			// line 2: group width
			p.add(PEToolKit.createJLabel("attributes.RectangleWidth"));
			p.add(groupWidthTF = new DecimalNumberField(4));
			p.add(new JLabel());

			// line 3: group height
			p.add(PEToolKit.createJLabel("attributes.RectangleHeight"));
			p.add(groupHeightTF = new DecimalNumberField(4));
			p.add(new JLabel());

			// line 4: path or not path?
			p.add(new JLabel());
			p.add(isCustomPathCB = new JCheckBox(jpicedt.Localizer.currentLocalizer().get("action.editorkit.PSCustom")));
			p.add(new JLabel());

			// line 5: close path?
			p.add(new JLabel());
			p.add(isClosedPathCB = new JCheckBox(jpicedt.Localizer.currentLocalizer().get("action.editorkit.CloseCurve")));
			p.add(new JLabel());

			// cyclic focus TAB
			//groupHeightTF.setNextFocusableComponent(groupBLCornerXTF); // [pending] use FocusTraversalPolicy
			add(p, BorderLayout.NORTH);
			setPreferredSize(new Dimension(400,200));
		}

		/** Add action listeners to widgets to reflect changes immediately. */
		private void addActionListeners(){
			if (isListenersAdded) return; // already done
			groupBLCornerXTF.addActionListener(this);
			groupBLCornerYTF.addActionListener(this);
			groupWidthTF.addActionListener(this);
			groupHeightTF.addActionListener(this);
			isClosedPathCB.addActionListener(this);
			isCustomPathCB.addActionListener(this);
			isListenersAdded = true;
		}

		private void removeActionListeners(){
			if (!isListenersAdded) return; // already done
			groupBLCornerXTF.removeActionListener(this);
			groupBLCornerYTF.removeActionListener(this);
			groupWidthTF.removeActionListener(this);
			groupHeightTF.removeActionListener(this);
			isClosedPathCB.removeActionListener(this);
			isCustomPathCB.removeActionListener(this);
			isListenersAdded = false;
		}


		/**
		 * (re)init widgets with <code>Element</code>'s properties.
		 */
		public void load(){
			removeActionListeners();
			groupBLCornerXTF.setValue(getCtrlPtX(PicGroup.LL));
			groupBLCornerYTF.setValue(getCtrlPtY(PicGroup.LL));
			groupWidthTF.setValue(getBoundingBox(null).getWidth());
			groupHeightTF.setValue(getBoundingBox(null).getHeight());
			isClosedPathCB.setSelected(isPathClosed());
			isCustomPathCB.setSelected(getCompoundMode()==CompoundMode.JOINT);
			// add listeners AFTERWARDS ! otherwise loading widgets initial value has a painful side-effet...
			// since it call "store" before everything has been loaded
			addActionListeners(); // done the first time load is called
		}

		/**
		 * Update <code>Element</code>'s properties.
		 */
		public void store(){
			double width = groupWidthTF.getValue();
			double height = groupHeightTF.getValue();
			double llCornerX = groupBLCornerXTF.getValue();
			double llCornerY = groupBLCornerYTF.getValue();
			PicPoint urPtFrame = new PicPoint(llCornerX + width, llCornerY+height);
			translate(llCornerX - getCtrlPtX(LL), llCornerY - getCtrlPtY(LL));
			setCtrlPt(PicGroup.UR,urPtFrame,null);
			if(isClosedPathCB.isSelected())
				closePath();
			else
				openPath();
			if(isCustomPathCB.isSelected())
				setCompoundMode(CompoundMode.JOINT);
			else
				setCompoundMode(CompoundMode.SEPARATE);
		}

		public void actionPerformed(ActionEvent e){
			store();
		}

		/**
		 * @return the panel title, used e.g. for <code>Border</code> or <code>Tabpane</code> title.
		 * @since jPicEdt
		 * @author Sylvain Reynal
		 */
		public String getTitle(){
			return PicGroup.this.getName();
		}

	}
} // PicGroup
