// BranchElement.java --- -*- coding: iso-8859-1 -*-
// February 10, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2007 Sylvain Reynal
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
// Version: $Id: BranchElement.java,v 1.34 2013/03/27 07:03:19 vincentb1 Exp $
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

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.toolkit.ConvexZoneGroup;
import jpicedt.graphic.view.ArrowView;
import jpicedt.graphic.view.ViewFactory;
import jpicedt.ui.dialog.UserConfirmationCache;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import static jpicedt.Log.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * A class for <code>Element</code>'s that allow children, eg, primarily:
 * <ul>
 * <li>groups, ie, clutches of objects that maintain a kind of relationship although their geometry is
 * specified in an independent way;
 * <li>custom paths (PSTricks' pscustom) made up by piecing objects' paths together.
 * </ul>
 * In both cases the geometry specification mostly delegates to
 * the children, except for the bounding-box, which is backed by an array of coordinates.
 * <p>
 * The implementation of the Collection interface is done through calls to
 * <code>addChild</code>, <code>removeChild</code>, <code>children</code>,
 * <code>removeAllChildren</code>, except for the <code>addAll</code> method which is implemented from
 * scratch so as to fire a changed-event only once all elements have been added.
 * <br>
 * As a result, subclasses only need to override these methods
 * to change the behaviour of content-modifying methods.
 * <p>
 * This element has only one control point, namely, the bottom-left corner of the bounding box.  Moving this
 * point result in a global translation of the element (however no rescale capability is available here using
 * the <code>setCtrlPt()</code> method, use the PicGroup class instead).
 *
 * @since jPicEdt 1.1; custom path behaviour added as of jpicedt 1.5
 * @author Sylvain Reynal
 */
public class BranchElement extends AbstractElement implements java.util.List<Element> {

	private static final boolean DEBUG = false; // jpicedt.Log.DEBUG;

	/** the array that contains children */
	protected ArrayList<Element> children = new ArrayList<Element>();

	/** A semaphor that signals a change of state is underway in this <code>BranchElement</code> and
	 * it shouldn't process events coming from its children before the change is completed
	 * (hence this is used inside {@link #forwardChangedUpdate forwardChangedUpdate()});
	 * set it to true each time you start modifying children <b>in batch mode</b> and you don't want
	 * events to be forwarded to the root of the hierarchy before everything is completed (e.g. to
	 * get rid of side-effects, or to reduce the burden for the repaint manager).<p>
	 * Example of use: whenever we call <code>translate()</code> on this <code>BranchElement</code>,
	 * <ul>
	 * <li> this first raises the changeLock semaphor,</li>
	 * <li> then calls <code>translate()</code> on every children: this in turn make children forward a
	 *    <code>DrawingEvent</code> to their parent (= this <code>BranchElement</code>), yet this event is
	 *    trapped here, hence not propagated upward ; note however that children update their own view
	 *    whatever the value of the changeLock semaphor;</li>
	 * <li> then lower the <code>changeLock</code> semaphor, and fire a change-update event (which in turn
	 *    will propagate the event upward along the tree).</li>
	 * </ul>
	 */
	protected boolean changeLock;

	private static final int P_ANCHOR = 0; // index of the only control point

	/** An array of X-coordinates representing the two opposite corner of the bounding box that contains all
	 * children */
	protected double[] ptsX;
	/** an array of Y-coordinates representing the two opposite corner of the bounding box that contains all
	 * children */
	protected double[] ptsY;

	/**
	 * This <code>BranchElement</code> can be displayed, either as a group of objects (=children, "SEPARATE"
	 * mode), or as a single path constructed by piecing children's paths together ("JOINT" mode).  SEPARATE
	 * is the default mode (this is the legacy behaviour).
	 */
	public static enum CompoundMode {SEPARATE, JOINT};

	/** a path constructed by appending every child's path, aka pscustom; may be null if N/A */
	protected GeneralPath shape;

	/** current CompoundMode */
	protected CompoundMode compoundMode;

	/** whether the path is closed or not (in "JOINT" mode) */
	protected boolean isPathClosed;

	/** see PSTricks' doc, page 36; may take only integer values b/w 0 and 2; default to 0 ; [underway]
	 * liftpen not use yet.*/
	protected int liftPen;

	/** [underway] liftpen not used yet. */
	public final static int DEFAULT_LIFTPEN = 0;


	//////////////////////////////
	/// CONSTRUCTORS
	//////////////////////////////

	/**
	 * Construct a <code>BranchElement</code> with no parent and a default <code>PicAttributeSet</code>.
	 */
	public BranchElement(){
		super();
		ptsX = new double[2];
		ptsY = new double[2];
		compoundMode = CompoundMode.SEPARATE;
		isPathClosed = false;
		liftPen = DEFAULT_LIFTPEN;
	}

	/**
	 * Construct a <code>BranchElement</code> with no parent and the given <code>PicAttributeSet</code>
	 */
	public BranchElement(PicAttributeSet attributeSet){
		this();
		this.attributeSet = new PicAttributeSet(attributeSet);
		compoundMode = CompoundMode.SEPARATE;
		isPathClosed = false;
		liftPen = DEFAULT_LIFTPEN;
	}

	/**
	 * Cloning constructor (though with no parent and no view):
	 * <ol>
	 * <li> attribute set is deeply copied.</li>
	 * <li> source's children are cloned, then the copy is added to this <code>BranchElement</code>.</li>
	 * </ol>
	 */
	public BranchElement(BranchElement src){
		super(src);
		this.compoundMode = src.compoundMode;
		this.isPathClosed = src.isPathClosed;
		this.liftPen = src.liftPen;
		if (DEBUG) debug("cloning " + src);
		ptsX = new double[2];
		ptsY = new double[2];
		for(Element obj: src){
			this.add(obj.clone());
		}
		// ensure end-points are in the same order as the source (addChild might've modified it)
		ptsX[0] = src.ptsX[0];
		ptsY[0] = src.ptsY[0];
		ptsX[1] = src.ptsX[1];
		ptsY[1] = src.ptsY[1];
	}

	/**
	 * Create a new <code>BranchElement</code> from the content of the given Collection of
	 * <code>Element</code>'s.  Children are cloned as well (i.e. this isa deep copy).
	 */
	public BranchElement(Collection<? extends Element> c){
		this();
		compoundMode = CompoundMode.SEPARATE;
		isPathClosed = false;
		liftPen = DEFAULT_LIFTPEN;
		if (DEBUG) debug("from collection " + c);
		for(Element e: c){
			this.add(e.clone());
		}
	}

	/**
	 * @return A clone of this element.
	 */
	 public BranchElement clone(){
		 return new BranchElement(this);
	 }

	 public String getDefaultName(){
		 return getClass().getName();
	 }



	/////////////////////////////////////////////////////////
	//// OPERATIONS ON CONTENT
	/////////////////////////////////////////////////////////

	/**
	 * @return The child at the given index, or null if no children.
	 */
	public Element get(int childIndex){
		return children.get(childIndex);
	}

	/**
	 * Add the given child to this <code>BranchElement</code>, setting its parent to this.
	 * Since a child can't have two parents, this also remove the child from its former parent, if any.
	 * <br>
	 * If view is non-null, use the associated <code>ViewFactory</code> to create <code>View</code>'s for the
	 * new child.
	 */
	public boolean add(Element child){
		if (DEBUG) debug("addChild : " + child);
		if (child==null || child==this || contains(child)) return false; // jpicedt 1.5: avoid duplicates
		// [pending] if child is contained, yet in undirect lineage, move it as direct child here?
		children.add(child);
		// change parent:
		BranchElement oldParent = child.getParent();
		if (oldParent != null) oldParent.remove(child);
		child.setParent(this);
		// create View for child
		if (view != null){
			ViewFactory f = view.getViewFactory();
			child.setViewFromFactory(f);
		}
		updateBoundingBox();
		fireChangedUpdate(DrawingEvent.EventType.INSERT);
		return true;
	}

	/**
	 * Insert the given child in this <code>BranchElement</code> at the given position, setting its parent to
	 * this, and create a view for the child using the <code>ViewFactory</code> that produced the View for
	 * this <code>BranchElement</code>.
	 */
	public void add(int position, Element child){
		if (DEBUG) debug("addChild at pos " + position + " : " + child);
		if (child==null || child==this || contains(child)) return; // jpicedt 1.5: avoid duplicates
		children.add(position,child);
		BranchElement oldParent = child.getParent();
		if (oldParent != null)
			oldParent.remove(child);
		child.setParent(this);
		// create View for child
		if (view != null){
			ViewFactory f = view.getViewFactory();
			child.setViewFromFactory(f);
		}
		updateBoundingBox();
		fireChangedUpdate(DrawingEvent.EventType.INSERT);
	}

	/**
	 * Replace the child at the given position by the given child.
	 * If the given child belonged to this <code>BranchElement</code>, this has no effect.
	 * @param newChild if null, this calls remove(position)
	 */
	public Element set(int position, Element newChild){
		if (newChild==null)
			return remove(position);
		if (contains(newChild))
			return get(position);

		Element oldChild = children.set(position,newChild);
		oldChild.removeView(); // remove view for old child if any
		oldChild.setParent(null); // make eligible for gc
		BranchElement oldParent = newChild.getParent();
		if (oldParent != null)
			oldParent.remove(newChild);
		newChild.setParent(this);
		// create View for child
		if (view != null){
			ViewFactory f = view.getViewFactory();
			newChild.setViewFromFactory(f);
		}
		updateBoundingBox();
		fireChangedUpdate(DrawingEvent.EventType.REPLACE);
		return oldChild;
	}

	/**
	 * Replace the given "src" element with the given "dest" element, if "src" belongs to this
	 * <code>BranchElement</code>.  No effect if "dest" already belongs to this <code>BranchElement</code>.
	 */
	public void replace(Element src, Element dest){ // was replaceElement
		if (contains(dest)) return;
		int index = indexOf(src);
		if(index!=-1)
			set(index, dest);
	}

	/**
	 * Remove the given child from this <code>BranchElement</code>, setting its parent to <code>null</code> so
	 * that the given child become (possibly) eligible for garbage collection if there are no other reference
	 * to it.
	 * @param child any children of this <code>BranchElement</code>, possibly through undirect lineage.
	 */
	public boolean remove(Object child){
		if (DEBUG) debug("removeChild : " + child);
		if ((child instanceof Element)==false || !contains(child)) // including indirect lineage
			return false;
		Element e = (Element)child;
		BranchElement p = e.getParent();
		if (p==this){ // direct lineage
			e.removeView();// remove view for child if any
			e.setParent(null);
			children.remove(e);
			updateBoundingBox();
			fireChangedUpdate(DrawingEvent.EventType.REMOVE);
			return true;
		}
		else // undirect lineage
			return p.remove(e);
	}


	/**
	 * Remove the child with the given index from this <code>BranchElement</code>.
	 */
	public Element remove(int index){ // note that this is necessarily a direct-line child
		Element e = children.remove(index); // can throw IndexOutOfBoundException
		e.removeView();
		e.setParent(null);
		updateBoundingBox();
		fireChangedUpdate(DrawingEvent.EventType.REMOVE);
		return e;
	}

	/**
	 * Removes all this collection's elements that are also contained in the
	 * specified collection.  After this call returns,
	 * this collection will contain no elements in common with the specified
	 * collection.
	 * @param c elements to be removed from this collection.
	 * @return <tt>true</tt> if this collection changed as a result of the call
	 */
	public boolean removeAll(Collection<?> c){
		if (isEmpty()) return false;
		boolean modified = false;
		Iterator<Element> it = iterator();
		while (it.hasNext()) {
			if(c.contains(it.next())) {
				it.remove(); // call remove, which in turns fire changed event.
				modified = true;
			}
		}
		return modified;
	}


	/**
	 * Remove all children from this <code>BranchElement</code>, set each child's parent to null.
	 */
	public void clear(){
		if (DEBUG) debug("removeAllChildren");
		if (isEmpty()) return;
		for(Element removed: this){
			removed.removeView(); // remove view for child if any
			removed.setParent(null);
		}
		children.clear();
		updateBoundingBox();
		fireChangedUpdate(DrawingEvent.EventType.REMOVE);
	}





	////////////////////////////////////////////////////////////////
	//// GEOMETRY
	////////////////////////////////////////////////////////////////

	/**
	 * @return the index of the first point that can be retrieved by <code>getCtrlPt</code>.
	 * This returns <code>PT_ANCHOR</code>.
	 */
	public int getFirstPointIndex(){
		return P_ANCHOR;
	}

	/**
	 * @return the index of the last point that can be retrieved by getCtrlPt
	 * This returns PT_ANCHOR.
	 */
	public int getLastPointIndex(){
		return P_ANCHOR;
	}

	/**
	 * @return the X-coord of the point indexed by <code>numPoint</code>.
	 * @param numPoint the point index, should be greater or equal to the value returned by
	 *        <code>getFirstPointIndex</code>, and lower or equal to <code>getLastPointIndex</code>.
	 * @since PicEdt 1.0
	 */
	public double getCtrlPtX(int numPoint){
		if (numPoint == P_ANCHOR) return min(ptsX[0],ptsX[1]);
		else throw new IndexOutOfBoundsException(new Integer(numPoint).toString());

	}

	/**
	 * This default implementation returns <code>ptsY[numPoint]</code>.This might be a valid implementation
	 * as long as subclasses don't have other control points.
	 * @return the Y-coord of the point indexed by <code>numPoint</code>.
	 * @param numPoint the point index, should be greater or equal to the value returned by
	 *        <code>getFirstPointIndex</code>, and lower or equal to <code>getLastPointIndex</code>.
	 * @since PicEdt 1.0
	 */
	public double getCtrlPtY(int numPoint){
		if (numPoint == P_ANCHOR) return min(ptsY[0],ptsY[1]);
		else throw new IndexOutOfBoundsException(new Integer(numPoint).toString());
	}

	/**
	 * Return the <b>user-controlled point</b> having the given index. The general contract in
	 * <code>Element</code> is to return an IMMUTABLE instance of <code>PicPoint</code>, so that the only way
	 * to alter the geometry of this element is by calling the <code>setCtrlPt</code> method, hence this
	 * implementation simply calls <code>getCtrlPtX()</code> and <code>getCtrlPtY()</code>.
	 * @return the point indexed by <code>numPoint</code> ;
	 *         if <code>src</code> is null, allocates a new PicPoint and return it,
	 *         otherwise directly modifies <code>src</code> and returns it as well for convenience.
	 * @param numPoint the point index, should be greater or equal to the value returned by
	 *        <code>getFirstPointIndex</code>, and lower or equal to <code>getLastPointIndex</code>.
	 * @since PicEdt 1.0
	 */
	public PicPoint getCtrlPt(int numPoint, PicPoint dest){
		if (dest==null) dest = new PicPoint();
		dest.x = getCtrlPtX(numPoint);
		dest.y = getCtrlPtY(numPoint);
		return dest;
	}

	/**
	 * Set the user-controlled point with the given index to the given value.  This default implementation
	 * simply call <code>setCtrlPt</code> with a null constraint, then fires a change-update of type
	 * <code>GEOMETRY_CHANGE</code>.
	 * @deprecated use setCtrlPt(int, PicPoint, EditPointConstraint) instead.
	 */
	public void setCtrlPt(int index, PicPoint pt){
		setCtrlPt(index,pt,null);
	}

	/**
	 * Set the point indexed by "numPoint" to the given value using the given constraint.
	 */
	public void setCtrlPt(int numPoint, PicPoint pt, EditPointConstraint c){
		if (numPoint == P_ANCHOR) translate(pt.x-getCtrlPtX(P_ANCHOR),pt.y-getCtrlPtY(P_ANCHOR)); // fire change event
		else throw new IndexOutOfBoundsException(new Integer(numPoint).toString());
	}

	/**
	 * Translate children by the given vector.
	 * @param dx The X coordinate of translation vector
	 * @param dy The Y coordinate of translation vector
	 * @since jPicEdt
	 */
	public void translate(double dx, double dy){
		// smart bounding-box update :
		ptsX[0] += dx;
		ptsY[0] += dy;
		ptsX[1] += dx;
		ptsY[1] += dy;

		changeLock = true; // filter out event from children
		for(Element o: this){
			o.translate(dx,dy); // children call forwardChangeUpdate via their fireChangedUpdate
			// but this has no effect since changeLock = true
		}
		changeLock = false;
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	public CtrlPtSubset getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension){
		CtrlPtSubsetGroup ret = null;
		Iterator<Element> it = children.iterator();
		while(it.hasNext()){
			Element e = it.next();
			CtrlPtSubset cps = e.getCtrlPtSubset(csg,czExtension);
			if(cps != null){
				ret = new CtrlPtSubsetGroup(children.size());
				ret.add(cps);
				while(it.hasNext()){
					e = it.next();
					cps = e.getCtrlPtSubset(csg,czExtension);
					if(cps != null) ret.add(cps);
				}
				break;
			}
		}
		return ret;
	}

	/**
	 * Scale children by <code>(sx,sy)</code> using <code>(ptOrgX,ptOrgY)</code> as origin; <code>sx</code>
	 * or <code>sy</code> can be negative.
	 */
	public void scale(double ptOrgX, double ptOrgY, double sx, double sy,UserConfirmationCache ucc){
		// smart bounding-box update :
		ptsX[0] = ptOrgX + sx * (ptsX[0] - ptOrgX);
		ptsY[0] = ptOrgY + sy * (ptsY[0] - ptOrgY);
		ptsX[1] = ptOrgX + sx * (ptsX[1] - ptOrgX);
		ptsY[1] = ptOrgY + sy * (ptsY[1] - ptOrgY);
		// scale children :
		changeLock = true; // block event from children
		// Attention: on s'alloue g pour éviter une exception
		// de type ConcurrentModification
		ArrayList<Element> g = new ArrayList<Element>(this);
		for(Element o : g)
			o.scale(ptOrgX,ptOrgY,sx,sy,ucc);

		changeLock = false; // unblock event from children
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * Effectue une reflection sur le <code>PicGroup</code>.
	 *
	 * @param ptOrg le <code>PicPoint</code> par lequel passe l'axe de la réflexion.
	 * @param normalVector le <code>PicVector</code> normal à l'axe de la réflexion.
	 */
	public void mirror(PicPoint ptOrg, PicVector normalVector){
		// mirror children :
		changeLock = true; // block event from children
		for(Element o: this){
			o.mirror(ptOrg,normalVector);
		}
		changeLock = false; // unblock event from children
		if(normalVector.getX() == 0 || normalVector.getY() == 0){
			// smart bounding-box update :
			//
			double mirroredPtsX[] = { ptsX[0], ptsX[1]};
			double mirroredPtsY[] = { ptsY[0], ptsY[1]};
			for(int i = 0; i< 2; ++i){
				double dotProduct =
					(mirroredPtsX[i]-ptOrg.getX())*normalVector.getX()
					+ (mirroredPtsY[i]-ptOrg.getY())*normalVector.getY();
				mirroredPtsX[i] -= 2*dotProduct*normalVector.getX();
				mirroredPtsY[i] -= 2*dotProduct*normalVector.getY();
			}
			ptsX[0] = min(mirroredPtsX[0], mirroredPtsX[1]);
			ptsY[0] = min(mirroredPtsY[0], mirroredPtsY[1]);
			ptsX[1] = max(mirroredPtsX[0], mirroredPtsX[1]);
			ptsY[1] = max(mirroredPtsY[0], mirroredPtsY[1]);
		}
		else
			updateBoundingBox();

		if(DEBUG)
			debug("pts={(" + Double.toString(ptsX[0])
                  + ","  + Double.toString(ptsY[0])
				  + "), ("
				  + Double.toString(ptsX[1])
                  + ","  + Double.toString(ptsY[1]) + ")}");

		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Rotate this <code>Element</code> by the given angle along the given point
	 * @param angle rotation angle in radians
	 */
	public void rotate(PicPoint ptOrg, double angle){
		// rotate children :
		changeLock = true; // block incoming event from children
		for(Element o:this){
			o.rotate(ptOrg,angle);
		}
		changeLock = false; // unblock event from children
		updateBoundingBox();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	public void shear(PicPoint ptOrg, double shx, double shy){
		shear(ptOrg, shx, shy,UserConfirmationCache.DEFAULT);
	}

	/**
	 * Shear this <code>Element</code> by the given params wrt to the given origin.
	 */
	public void shear(PicPoint ptOrg, double shx, double shy,UserConfirmationCache ucc){
		// shear children :
		changeLock = true; // block incoming event from children
		ArrayList<Element> g = new ArrayList<Element>(this);
		for(Element o: g){
			o.shear(ptOrg,shx,shy,ucc);
		}
		changeLock = false; // unblock event from children
		updateBoundingBox();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	////////////////////////////////////////////////////////////////
	//// VIEW
	////////////////////////////////////////////////////////////////

	/**
	 * If s is true, the associated view will attempt to paint this <code>BranchElement</code>
	 * as a single path made up by piecing childrens' shapes (aka PSTricks' pscustom).
	 * The associated formatter is expected to do likewise if its hosting content-type
	 * supports it. If s is false, this is the legacy behaviour, that is, every
	 * child is painted independently.
	 */
	public void setCompoundMode(CompoundMode m){
		this.compoundMode = m;
		// check if every child that's likely to be added to the PSCustom's path
		// is open:
		if (this.compoundMode == CompoundMode.JOINT){
			for (Element child : this){
				if (child instanceof AbstractCurve)
					((AbstractCurve)child).setClosed(false);
				else if (child instanceof PicEllipse)
					((PicEllipse)child).setArcType(PicEllipse.OPEN);
			}
		}
		// [pending] also fix issue with arrows... (from Attribute set)
		fireChangedUpdate(null);
	}

	/**
	 * Returns whether this group should be painted as single path or not.
	 */
	public CompoundMode getCompoundMode(){
		return this.compoundMode;
	}

	/**
	 * Toggles whether this group should be painted as a single path or not.
	 */
	public void toggleCompoundMode(){
		switch (getCompoundMode()){
		case SEPARATE:
			setCompoundMode(CompoundMode.JOINT);
			break;
		case JOINT:
			setCompoundMode(CompoundMode.SEPARATE);
			break;
		}
	}

	/**
	 * Closes the path generated by the children of this <code>BranchElement</code>.
	 * This only makes sense in "JOINT" mode.
	 */
	public void closePath(){
		this.isPathClosed = true;
		fireChangedUpdate(null);
	}

	/**
	 * Closes the path generated by the children of this <code>BranchElement</code>.
	 * This only makes sense in "JOINT" mode.
	 */
	public void openPath(){
		this.isPathClosed = false;
		fireChangedUpdate(null);
	}

	/**
	 * Returns whether the path generated by the children of this <code>BranchElement</code>
	 * is closed or not.
	 * This only makes sense in "JOINT" mode.
	 */
	public boolean isPathClosed(){
		return this.isPathClosed;
	}

	/**
	 * Toggles the closure of the path generated by the children of this <code>BranchElement</code>.
	 * This only makes sense in "JOINT" mode.
	 */
	public void togglePathClosure(){
		if (isPathClosed())
			openPath();
		else
			closePath();
	}

	/**
	 * Sets the value of the liftPen parameter. See PSTricks' doc, page 36.
	 * [underway] liftpen not used yet.
	 */
	public void setLiftPen(int i){
		if (i<0 || i>2)
			throw new IllegalArgumentException("liftpen: " + i);
		else
			this.liftPen = i;
	}

	/**
	 * Returns the value of the liftPen parameter. See PSTricks' doc, page 36.
	 * [underway] liftpen not used yet.
	 */
	public int getLiftPen(){
		return this.liftPen;
	}

	/**
	 * Creates a <code>GeneralPath</code> generated by the shapes of the
	 * children of this <code>BranchElement</code> (aka PSTricks' pscustom object).
	 */
	public Shape createShape(){
		if (shape == null)
			shape = new GeneralPath();
		shape.reset();
		final boolean connect = true; // ie, PSTricks' liftpen = 1
		for(Element o:this){
			Shape sh = o.createShape();
			if (sh != null)
				shape.append(sh, connect);
		}
		if (isPathClosed())
			shape.closePath();
		return shape;
	}

	/**
	 * Helper for the associated View. This implementation does nothing by default.
	 * [SR: underway]
	 */
	public void syncArrowGeometry(ArrowView v, ArrowView.Direction d){
	}

	/////////////////////////////////////
	/// EVENTS
	////////////////////////////////////

	/**
	 * Called by a child of this <code>BranchElement</code> to inform its parent of some change that occured
	 * to it or one of its children.  This gives a chance to the receiver to update its layout, then to
	 * propagate the change-event upward.  <br> This implementation update the bounding-box, then fire a
	 * changedUpdate event ONLY if changeLock is false.  Otherwise, does nothing (this is the exact semantic
	 * behind "changeLock").
	 * @param eventType the event type
	 * @param child
	 */
	public void forwardChangedUpdate(Element child,DrawingEvent.EventType eventType){
		if (DEBUG) debug("eventType="+eventType+", child="+child.toString());
		if (changeLock) return;  // if a change is underway here, and it's driven by THIS element,
		// we block incoming events until the change is completed ; the method which led the change
		// should obviously fire an event itself afterwards.
		updateBoundingBox();
		fireChangedUpdate(eventType); // update view then forward (see superclass)
	}


	/////////////////////////////////
	/// PAINT
	/////////////////////////////////

	/**
	 * Set the view for this <code>Element</code> from the given view factory, then set the view for children.
	 */
	public void setViewFromFactory(ViewFactory f){
		super.setViewFromFactory(f);
		if (DEBUG) debug("factory="+f);
		// set views for children
		for (Element e: this){
			e.setViewFromFactory(f);
		}
	}

	/**
	 * Remove the view that render this element and propagate to children ;
	 * this may be used to remove any reference to the view,
	 * and render it eligible for garbage collection ; if no View, does nothing.
	 */
	public void removeView(){
		super.removeView();
		if (DEBUG) debug("removeView");
		// remove views for children
		for (Element e: this){
			e.removeView();
		}
	}


	//////////////////////////////////////////////////
	//// CHILD DISPOSITION
	//////////////////////////////////////////////////

	/**
	 * Move the given child to back (i.e. following z-ordering policy),
	 * i.e. removes it from its current position and insert it at position 0
	 * <br>
	 * Does nothing if the given child can't be found in this Drawing or is already to back.
	 */
	public void bringToBack(Element obj){

		int index=indexOf(obj);
		if (index!=-1 && index>0){ // if not already at back
			children.remove(index);
			children.add(0,obj); // insert at bottom
		}
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * Move the given child to front, i.e. removes it from its current position and appends to the
	 * drawing.<br> Does nothing if the given obj can't be found in this <code>Drawing</code> or if it's
	 * already to front.
	 */
	public void bringToFront(Element obj){

		int index=indexOf(obj);
		if (index!=-1 && index<size()-1){ // if foudn and not already at front
			children.remove(index);
			children.add(obj); // add at top
		}
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Move the given child one position backward, i.e. removes it from its current position and insert it one
	 * position backward.<br>
	 * Does nothing if the given obj can't be found in this <code>Drawing</code>, or if it's already to back.
	 */
	public void bringBackward(Element obj){

		int index=indexOf(obj);
		if (index>0){ // if not already at back
			children.remove(index);
			children.add(index-1,obj);
		}
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE); // repaint !
	}

	/**
	 * Move the given child one position forward, i.e. removes it from its current position and insert it one
	 * position forward.<br>
	 * Does nothing if the given obj can't be found in this Drawing, or if it's already to front.
	 */
	public void bringForward(Element obj){

		int index=indexOf(obj);
		if (index!=-1 && index<size()-1){ // if found and not already at front
			children.remove(index);
			children.add(index+1,obj);
		}
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * @return true if the given object has the lowest z-value of all objects in this drawing.
	 */
	public boolean isToBack(Element obj){
		return indexOf(obj)==0;
	}

	/**
	 * @return true if the given object has the highest z-value of all objects in this drawing.
	 */
	public boolean isToFront(Element obj){
		return indexOf(obj)==size()-1;
	}










	///////////////////////
	/// BOUNDING BOX
	///////////////////////

	/**
	 * Returns the bounding box (i.e. the surrounding rectangle) in double precision
	 * Used e.g. to determine the arguments of the <code>\begin{picture}</code> command.<p>
	 * Straigthforwardly computed from the diagonal.
	 * @return null if this <code>BranchElement</code> has no children
	 * @since PicEdt 1.0
	 */
	public Rectangle2D getBoundingBox(Rectangle2D r){
		if (isEmpty()) return null; // [SR:underway] test pour pb CompositeView
		if (r==null) r = new Rectangle2D.Double();
		r.setFrameFromDiagonal(ptsX[0],ptsY[0],ptsX[1],ptsY[1]);
		return r;
	}

	/**
	 * Update the bounding box by "unioning" the children's bounding box (actually computes the
	 * two specification points defining the diagonal of the box).
	 * Should be called each time the content of this element gets modified : this may include
	 * addition or deletion of a child, and modification of a child by the child itself.
	 */
	protected void updateBoundingBox(){
		// this method use "bbBuffer"
		if (children.isEmpty()) {
			ptsX[0]=ptsY[0]=ptsX[1]=ptsY[1]=0;
			if (DEBUG) debug("empty");
		}
		else {
			Rectangle2D bb=null;
			double minX, minY, maxX, maxY;
			// initializes "bound" from first object, else we catch a (0,0) inside it !!!
			int i=0;
			while(bb == null && i < size())
				bb = children.get(i++).getBoundingBox(new Rectangle2D.Double());
			if (bb == null){ // means: not empty, but all children are empty PicGroup's or similar
				ptsX[0]=ptsY[0]=ptsX[1]=ptsY[1]=0;
				return;
			}
			minX=bb.getMinX();
			minY=bb.getMinY();
			maxX=bb.getMaxX();
			maxY=bb.getMaxY();
			for(i=1; i<children.size(); i++){
				Element e = children.get(i);
				e.getBoundingBox(bb);
				maxX=max(bb.getMaxX(),maxX);
				minX=min(bb.getMinX(),minX);
				maxY=max(bb.getMaxY(),maxY);
				minY=min(bb.getMinY(),minY);
			}
			ptsX[0] = minX;
			ptsY[0] = minY;
			ptsX[1] = maxX;
			ptsY[1] = maxY;
			if (DEBUG) debug(getBoundingBox(null).toString()+"\n\tthis="+this);
		}
	}








	////////////////////////////
	//// OTHER FIELDS ACCESSORS
	////////////////////////////

	/**
	 * Set <code>AttributeSet</code> for this <code>BranchElement</code> and propagate to children.
	 */
	public void setAttributeSet(PicAttributeSet set){
		changeLock = true;
		for(Iterator<Element> i = children.iterator(); i.hasNext();){
			i.next().setAttributeSet(set);
		}
		changeLock = false;
		super.setAttributeSet(set); // fire changed event
	}

	/**
	 * Set an <code>Attribute</code> for this <code>BranchElement</code> and propagate to children.
	 */
	public <T> void setAttribute(PicAttributeName<T> name, T value){
		changeLock = true;
		for(Element e: children){
			e.setAttribute(name,value);
		}
		changeLock = false;
		super.setAttribute(name,value); // fire event and synchronizes super's attribute set (useful when calling getAttribute())
	}


	/**
	 * Returns a String representing the group for debugging use only.
	 */
	public String toString(){

		String s = super.toString() + "\n\tchildren={";
		int j=0;
		for(Iterator<Element> i = children.iterator(); i.hasNext(); j++){
			StringBuffer buf = new StringBuffer(i.next().toString());
			int pos=0;
			while(true){
				pos = buf.toString().indexOf('\n',pos);
				if (pos == -1) break;
				buf.insert(pos+1,'\t');
				pos += 2;
			}
			s += "\n\t" + j + ":" + buf.toString();
		}
		return s + "\n\t}]";
	}


	///////////////////////////////////////////////
	//// Implementation of the Collection/List interface
	///////////////////////////////////////////////

	public int hashCode(){
		return children.hashCode();
	}

	/**
	 * Appends all the elements in the given collection that are instance of <code>Element</code>
	 * as children of this <code>BranchElement</code>.
	 */
	public boolean addAll(Collection<? extends Element> c){
		boolean changed = false;
		for(Element child: c){
			if (child==null || child==this || contains(child)) continue;
			// same as addChild(o), but fire event only in the end :
			BranchElement oldParent = child.getParent();
			if (oldParent != null)
				oldParent.remove(child);
			child.setParent(this);
			children.add(child);
			// create View for child
			if (view != null){
				ViewFactory f = view.getViewFactory();
				child.setViewFromFactory(f);
			}
			changed = true;
		}
		if (changed) {
			updateBoundingBox();
			fireChangedUpdate(DrawingEvent.EventType.INSERT);
		}
		return changed;
	}

	/**
	 * Inserts all the elements in the given collection that are instance of <code>Element</code>
	 * as children of this <code>BranchElement</code>, at the given position.
	 */
	public boolean addAll(int index, Collection<? extends Element> c){
		boolean changed = false;
		// same as add(o), but fire event only in the end :
		for(Element child: c){
			if (child==null || child==this || contains(child)) continue;
			children.add(index, child);
			BranchElement oldParent = child.getParent();
			if (oldParent != null)
				oldParent.remove(child);
			child.setParent(this);
			// create View for child
			if (view != null){
				ViewFactory f = view.getViewFactory();
				child.setViewFromFactory(f);
			}
			changed = true;
		}
		if (changed) {
			updateBoundingBox();
			fireChangedUpdate(DrawingEvent.EventType.INSERT);
		}
		return changed;
	}

	/**
	 * Return the number of children in this <code>BranchElement</code> (direct offspring only).
	 */
	public int size(){
		return children.size();
	}

	/**
	 * @return <tt>true</tt> if this <code>BranchElement</code> contains no children
	 */
	public boolean isEmpty(){
		return children.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if the given <code>Element</code> (or one of its ancestor) is contained in this
	 * <code>BranchElement</code>.
	 */
	public boolean contains(Object o){
		if (o==null)
			return false;
		if (children.contains(o))
			return true;
		if (!(o instanceof Element))
			return false;
		Element e = (Element)o;
		if (e.getParent()!=null && e.getParent()!=this) // [pending] need testing
			return contains(e.getParent()); // reentrant
		return false;
	}

	/**
	 * Returns an iterator over children.
	 */
	public Iterator<Element> iterator(){
		return children.iterator();
	}

	public java.util.List<Element> subList(int fromIndex, int toIndex){
		return children.subList(fromIndex, toIndex);
	}

	public ListIterator<Element> listIterator(){
		return children.listIterator();
	}

	public ListIterator<Element> listIterator(int index){
		return children.listIterator(index);
	}

	/**
	 * Returns the index of the given child amongst the offspring of this <code>BranchElement</code>.
	 * @return -1 if not found
	 */
	public int lastIndexOf(Object o){
		return children.lastIndexOf(o);
	}

	/**
	 * Returns the index of the given child amongst the offspring of this <code>BranchElement</code>.
	 * @return -1 if not found
	 */
	public int indexOf(Object child){
		return children.indexOf(child);
	}

	/**
	 * Returns an array containing all of the elements in this collection. */
	public Object[] toArray(){
		return children.toArray();
	}

	/**
	 * Returns an array containing all of the elements in this collection
	 * whose runtime type is that of the specified array.
	 */
	public <T> T[] toArray(T a[]){
		return children.toArray(a);
	}

	/**
	 * Returns <tt>true</tt> if this <code>BranchElement</code> contains all of the elements
	 * in the specified collection, possibly through indirect lineage.
	 */
	public boolean containsAll(Collection<?> c){
		Iterator<?> e = c.iterator();
		while (e.hasNext())
			if(!contains(e.next()))
				return false;
		return true;
	}

	public boolean equals(Object o){
		if (o instanceof BranchElement){
			return this.children.equals(((BranchElement)o).children);
		}
		return false;
	}

	/**
	 * Retains only the elements in this <code>BranchElement</code> that are contained in the specified
	 * collection.
	 * @param c elements to be retained in this collection.
	 * @return true if this collection changed as a result of the call
	 */
	public boolean retainAll(Collection<?> c){
		boolean modified = false;
		Iterator<Element> it = iterator();
		while (it.hasNext()) {
			if(!c.contains(it.next())) {
				it.remove(); // call remove, which in turns call removeChild and fire changed event.
				modified = true;
			}
		}
		return modified;
	}

	////////////////////////////////////////////////////////////////
	//// MISC
	////////////////////////////////////////////////////////////////

	/**
	 * Returns a list containing children, grand-children, etc. of this <code>BranchElement</code> that are of
	 * the same type or inherit the given <code>clazz</code>. Search down the children tree stops whenever a
	 * matching class is encountered.
	 * @since jpicedt 1.4pre5
	 */
	public <T extends Element> ArrayList<T> createFilteredCollection(Class<T> clazz){
		ArrayList<T> l = new ArrayList<T>();
		if (clazz==null)
			return l;
		for (Element o: this){
			if (clazz.isAssignableFrom(o.getClass()))
				l.add(clazz.cast(o));
			else if (o instanceof BranchElement)
				l.addAll(((BranchElement)o).createFilteredCollection(clazz));
		}
		return l;
	}

	/**
	 * Returns whether this <code>BranchElement</code> contains children, grandchildren, etc. that are of the
	 * same type of inherit from the given <code>clazz</code>.
	 * @since jpicedt 1.4pre5
	 */
	public boolean containsClass(Class<? extends Element> clazz){
		if (clazz==null)
			return false;
		for (Element o: this){
			if (clazz.isAssignableFrom(o.getClass())) return true;
			if (o instanceof BranchElement && ((BranchElement)o).containsClass(clazz))
				return true;
		}
		return false;
	}

}
