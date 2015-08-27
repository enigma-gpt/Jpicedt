// AbstractElement.java --- -*- coding: iso-8859-1 -*-
// February 18, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: AbstractElement.java,v 1.31 2013/03/27 07:03:24 vincentb1 Exp $
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
import jpicedt.graphic.view.*;
import jpicedt.graphic.event.*;
import jpicedt.ui.dialog.UserConfirmationCache;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import static jpicedt.Log.*;
import static jpicedt.Localizer.*;

/**
 * An abstract class that can serve as the basis class for elements of a Drawing.<p>
 * This implementation provides some useful behaviour where the integration of <code>Element</code>'s in a
 * tree-model is concerned.
 * @author Sylvain Reynal
 * @since PicEdt 1.0
 */
public abstract class AbstractElement implements Element, ActionFactory {

	////////////////////////////
	//// PROTECTED MEMBERS
	///////////////////////////
	/** the name of this element, if set by the user */
	protected String name;
	/** the parent of this element */
	protected BranchElement parent;
	/** the attribute set */
	protected PicAttributeSet attributeSet;
	/** the view that render this element */
	protected View view;

	/**
	 * construct an <code>AbstractElement</code> with no parent and a default <code>PicAttributeSet</code>
	 */
	public AbstractElement(){
		attributeSet = new PicAttributeSet();
	}

	/**
	 * construct an <code>AbstractElement</code> with no parent and the given <code>PicAttributeSet</code>.
	 */
	public AbstractElement(PicAttributeSet attributeSet){
		this.attributeSet = new PicAttributeSet(attributeSet);
	}

	/**
	 * cloning constructor ; attribute set is deeply copied.
	 * The parent and the view are set to null.
	 */
	public AbstractElement(AbstractElement obj){
		if (DEBUG) debug("cloning " + obj);
		this.attributeSet = new PicAttributeSet(obj.getAttributeSet());
	}

	/**
	 * Returns a clone of this Element
	 */
	public abstract AbstractElement clone();


	/**
	 * @return a localised string that represents this object's name
	 */
	public String getName(){
		String s = getDefaultName();
		if (name!=null){
			s = name;
		}
		BranchElement parent = getParent(); // most probably: eithers root-element or a PicGroup
		if (parent != null)
			s += "#" + parent.indexOf(this); // z-axis
		/*
		Drawing dr = getDrawing();
		if (dr != null) // z-axis
			s += "#" + dr.indexOf(this);
			//i = hashCode();
		*/
		return s;
	}



	/**
	 * Sets the name of this object.
	 * @param name if null, reverts to the default name.
	 */
	public void setName(String name){
		this.name = name;
	}

	protected abstract String getDefaultName();

	///////////////////////////////////////
	//// tree structure
	///////////////////////////////////////

	/**
	 * Retrieves the underlying drawing
	 * @return the drawing ; null if this <code>AbstractElement</code> doesn't belong to any drawing yet.
	 */
	public Drawing getDrawing() {
		BranchElement p = getParent();
		return (p!=null) ? p.getDrawing() : null;
	}

	/**
	 * Gets the parent of the element.
	 * @return the parent
	 */
	public BranchElement getParent() {
		return parent;
	}

	/** Une implantation de {@link Element#replaceBy(Element by, boolean replaceInSelection) }.
	 */
	public void replaceBy(Element by, boolean replaceInSelection){
		BranchElement parent = this.getParent();
		if(parent == null)
			return;
		// Attention on traitement le replaceInSelection AVANT le parent.replace
		if(replaceInSelection){
			View v = parent.getView();
			if(v != null){
				PECanvas canvas = v.getContainer();
				SelectionHandler sel = canvas.getEditorKit().getSelectionHandler();
				sel.replace(this,by);
			}
		}
		parent.replace(this,by);
	}

	/**
	 * Sets the parent of this element to be the given element.
	 * @param p The parent to be set for this element.
	 */
	public void setParent(BranchElement p) {
		if (DEBUG) debug("parent=" + p + "\n\tthis="+this);
		this.parent = p;
	}

	/**
	 * If this element's parent is a <code>PicGroup</code>, pull it out this <code>PicGroup</code>, and add it
	 * to its grand-parent as a sibling of this <code>PicGroup</code>.
	 * @since jpicedt 1.5
	 * @return whether the operation succeeded or not
	 */
	public boolean pullOutOfGroup(){
		BranchElement parent=getParent();
		if (parent instanceof PicGroup){
			PECanvas cv = null;
			// try to remember if "this" was selected...
			View v = getView();
			if (v != null){
				cv = v.getContainer();
			}
			boolean wasSelected=false;
			if (cv !=null)
				wasSelected = cv.isSelected(this);

			BranchElement grandParent = parent.getParent();
			parent.remove(this);
			grandParent.add(this);
			if (cv != null && wasSelected)
				// ... and then revert to its initial selection:
				cv.select(this,PECanvas.SelectionBehavior.INCREMENTAL);
			// si après le retrait de this le groupe parent est devenu un singleton, alors on le dégroupe
			// également
			if (parent.size()==1){
				Element elem = parent.get(0);
				if (elem instanceof AbstractElement)
					((AbstractElement)elem).pullOutOfGroup();
			}
			if (parent.isEmpty())
				grandParent.remove(parent);
			return true;
		}
		return false;
	}

	/////////////////////////////////////
	/// EVENTS
	////////////////////////////////////

	/**
	 * Must be called each time this <code>AbstractElement</code> changes.  This default implementation
	 * updates the associated View if any, then propagates upward along the tree by calling
	 * <code>forwardChangedUpdate(this)</code> on the <code>Element</code>'s parent, if any.
	 * <br>
	 * Tree's root-element should override so as to fire the change to the hosting drawing.
	 * <br>
	 * If subclasser are willing to override this method, they should call
	 * <code>super.fireChangeUpdate</code>.
	 * @param eventType the event type
	 */
	protected void fireChangedUpdate(DrawingEvent.EventType eventType){
		if (DEBUG) debug("eventType="+eventType+", this="+toString());
		if (view != null) view.changedUpdate(eventType);
		BranchElement p = getParent();
		if (p!=null) p.forwardChangedUpdate(this,eventType); // forward to parent , package restricted access.
	}



	////////////////////////////////////
	//// GEOM.
	///////////////////////////////////

	/** convenience call to <code>setCtrlPt(int, PicPoint, null)</code> */
	public void setCtrlPt(int index, PicPoint pt) {
		setCtrlPt(index, pt, null);
	}

	/**
	 * Create an Iterator over points that can serve as anchor points for grid alignment.  This implementation
	 * returns a <code>DefaultPointIndexIterator</code> which simply iterates through all user-controlled
	 * points in ascending order.
	 */
	public PointIndexIterator anchorPointsIterator(){
		return new DefaultPointIndexIterator(this);
	}


	/**
	 * Dilate cet élément par <code>(sx,sy)</code> en utilisant <code>ptOrg</code> comme origine&nbsp;;
	 * <code>sx</code> ou <code>sy</code> peuvent être négatifs. Ceci est une commodité d'appel de
	 * <code>scale(ptOrg.x, ptOrg.y, sx, sy,ucc)</code>.
	 */
	public void scale(PicPoint ptOrg, double sx, double sy,UserConfirmationCache ucc){
		scale(ptOrg.x, ptOrg.y, sx, sy,ucc);
	}

	/**
	 * Dilate cet élément par <code>(sx,sy)</code> en utilisant <code>ptOrg</code> comme origine&nbsp;;
	 * <code>sx</code> ou <code>sy</code> peuvent être négatifs. Ceci est une commodité d'appel de
	 * <code>scale(ptOrg.x, ptOrg.y, sx, sy,UserConfirmationCache.DEFAULT)</code>.
	 */
	public void scale(PicPoint ptOrg, double sx, double sy){
		scale(ptOrg.x, ptOrg.y, sx, sy,UserConfirmationCache.DEFAULT);
	}

	/**
	 * Dilate cet élément par <code>(sx,sy)</code> en utilisant <code>(ptOrgX, ptOrgY)</code> comme
	 * origine&nbsp;; <code>sx</code> ou <code>sy</code> peuvent être négatifs. Ceci est une commodité d'appel
	 * de <code>scale(ptOrgX, ptOrgY, sx, sy,UserConfirmationCache.DEFAULT)</code>.
	 */
	public void scale(double ptOrgX, double ptOrgY, double sx, double sy){
		scale(ptOrgX, ptOrgY, sx, sy,UserConfirmationCache.DEFAULT);
	}

	/**
	 * Cisaille cet élément par <code>(shx,shy)</code> en utilisant <code>ptOrg</code> comme origine.
	 * Ceci est une commodité d'appel de
	 * <code>shear(ptOrg, shx, shy,UserConfirmationCache.DEFAULT)</code>.
	 */
	public void shear(PicPoint ptOrg, double shx, double shy){
		shear(ptOrg, shx, shy,UserConfirmationCache.DEFAULT);
	}




	/////////////////////////////////
	/// VIEW
	/////////////////////////////////

	/**
	 * Returns the View that's responsible for rendering this <code>AbstractElement</code>
	 */
	public View getView(){
		return view;
	}

	/**
	 * Set the view for this <code>AbstractElement</code> from the given view factory
	 * or remove the view if f is null.
	 */
	public void setViewFromFactory(ViewFactory f){
		if (DEBUG) debug("factory="+f);
		removeView(); // remove cross-reference
		if (f!=null) view = f.createView(this);
	}

	/**
	 * remove the view that render this element; this may be used to remove any reference to the view, and
	 * render it eligible for garbage collection; if no View, does nothing.
	 */
	public void removeView(){
		if (view == null) return;
		view.setHighlighter(null); // remove highlighter to avoid GC deadlocks
		view.setElement(null);
		view = null;
	}

	/**
	 * Creates a Shape that reflects the geometry of this model.
	 * This implementation returns null [underway].
	 */
	public Shape createShape(){
		return null;
	}

	/**
	 * Helper for the associated <code>View</code>. This implementation does nothing by default.
	 */
	public void syncArrowGeometry(ArrowView v, ArrowView.Direction d){
	}


	/////////////////////////////////
	/// ATTRIBUTE SET
	/////////////////////////////////

	/**
	 * Returns the <code>AttributeSet</code> bound to this <code>AbstractElement</code>.
	 * <p>
	 * Be careful that modifying attributes using this method does not fire any <code>DrawingEvent</code>'s,
	 * hence does not keep the view synchronized with the state of the model.
	 */
	public PicAttributeSet getAttributeSet(){
		return attributeSet;
	}

	/**
	 * Bind the given attributes set to this <code>Element</code>. This implementation actually makes a deep
	 * copy of the given set beforehands.
	 */
	public void setAttributeSet(PicAttributeSet attributeSet){
		this.attributeSet = new PicAttributeSet(attributeSet);
		fireChangedUpdate(DrawingEvent.EventType.ATTRIBUTE_CHANGE);
	}

	/**
	 * Set the given attribute name to the given value for this AbstractElement
	 */
	public <T> void setAttribute(PicAttributeName<T> name, T value){
		attributeSet.setAttribute(name,value);
		fireChangedUpdate(DrawingEvent.EventType.ATTRIBUTE_CHANGE);
	}

	/**
	 * Returns the value for the given attribute name
	 */
	public <T> T getAttribute(PicAttributeName<T> name){
		return attributeSet.getAttribute(name);
	}

	/////////////////////////////
	// node connection management
	/////////////////////////////

	///////////////////////////////
	//// STRING FORMATING
	///////////////////////////////

	/**
	 * Returns a <code>String</code> representation of the attribute set for this <code>AbstractElement</code>
	 */
	public String toString(){
		String s = "[" + getName() + "@" + Integer.toHexString(hashCode()) + ":\n\t" + attributeSet.toString();
		return s;
	}

	/**
	 * @return <code>null</code>
	 * @see jpicedt.graphic.model#Element.getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension)
	 * @since jPicEdt 1.6
	 */
	public CtrlPtSubset getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension){ return null; }

	/////////////////////////////////
	/// GUI
	/////////////////////////////////

	/**
	 * Creates an array of <code>Action</code>'s related to this object
	 */
	public ArrayList<PEAction> createActions(ActionDispatcher actionDispatcher, ActionLocalizer localizer, HitInfo hi) {
		ArrayList<PEAction> al = new ArrayList<PEAction>();

		// edit name:
		al.add(new SetNameAction(actionDispatcher, localizer));
		// edit geometry
		if(this instanceof CustomizerFactory)
			al.add(new EditorKit.EditGeometryAction(actionDispatcher, localizer, this));

		// new *************************** begin (by ss & bp)
		// edit textmode
		if(this instanceof PicText)
			al.add(new EditorKit.EditTextModeAction(actionDispatcher, localizer, this));
		// new *************************** end (by ss & bp)

		// pull out of group
		if(getParent() instanceof PicGroup)
			al.add(new PullOutOfGroupAction(actionDispatcher, localizer));

		// if parent is a PicGroup, add toggle-highlighting-mode
		if (getParent() instanceof PicGroup && !(hi instanceof HitInfo.Composite)){ // avoid infinite loop (see PicGroup.createActions())
			al.add(((PicGroup)getParent()).createToggleHighlightingModeAction(actionDispatcher, localizer));
			al.add(null); // separator
		}
		return al;
	}

	// ---- pull element out of parent group if applicable ----

	/**
	 * If this element's parent is a <code>PicGroup</code>, pull it out this <code>PicGroup</code>, and add it
	 * to its grand-parent as a sibling of this <code>PicGroup</code>.
	 */
	class PullOutOfGroupAction extends PEAction {

		public static final String KEY = "action.editorkit.PullOutOfGroup"; // [pending] need i18n sync'

		public PullOutOfGroupAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}

		public void undoableActionPerformed(ActionEvent e){
			pullOutOfGroup();
		}
	}

	/**
	 * If this element's parent is a PicGroup, pull it out this <code>PicGroup</code>, and add it
	 * to its grand-parent as a sibling of this <code>PicGroup</code>.
	 */
	class SetNameAction extends PEAction {

		public static final String KEY = "action.editorkit.SetName"; // [pending] need i18n sync'

		public SetNameAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}

		public void undoableActionPerformed(ActionEvent e){
			DialogFactory df = getEditorKit().getDialogFactory();
			String iv = name;
			if (iv == null)
				iv = getDefaultName();
			String ans = df.showInputDialog("Enter new name", "Name", JOptionPane.QUESTION_MESSAGE, iv);
			if (ans != null)
				setName(ans);
		}
	}

}
