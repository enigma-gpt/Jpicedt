// DefaultSelectionHandler.java --- -*- coding: iso-8859-1 -*-
// September 22, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999/2006 Sylvain Reynal
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
// Version: $Id: DefaultSelectionHandler.java,v 1.23 2013/03/27 06:58:26 vincentb1 Exp $
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
import jpicedt.graphic.SelectionHandler;

import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.BranchElement;
import jpicedt.graphic.model.PicGroup;

import jpicedt.graphic.event.DrawingListener;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.event.PEMouseEvent;

import jpicedt.graphic.view.CompositeView;
import jpicedt.graphic.view.highlighter.DefaultHighlighterFactory;
import jpicedt.graphic.view.highlighter.Highlighter;
import jpicedt.graphic.view.highlighter.CompositeHighlighter;
import jpicedt.graphic.view.HitInfo;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import static jpicedt.graphic.view.ViewConstants.*;
import static jpicedt.graphic.view.highlighter.CompositeHighlighter.*;
import static jpicedt.Log.*;

/**
 * Stores references to selected <code>Element</code>'s.<p> This handler has the same capabilities as
 * jpicedt.graphic.model.PicGroup, except that it does not belong to any
 * <code>jpicedt.graphic.model.Drawing.RootElement</code>.  Hence it is guaranteed that this handler will
 * NEVER notify the parent document (=the hosting <code>Drawing</code>) when content gets added to/removed
 * from it, since it has no parent. This design choice is aimed at avoiding redundant event generation, since
 * (for instance) deleting a selected element would then trigger the same DrawingEvent twice (one on behalf of
 * this handler, the other one on behalf of the hosting Drawing).  Content modifying method are overriden so
 * as to reflect this.
 * @since jPicEdt 1.4
 * @author Sylvain Reynal
 * @version $Id: DefaultSelectionHandler.java,v 1.23 2013/03/27 06:58:26 vincentb1 Exp $
 */
public class DefaultSelectionHandler extends PicGroup implements SelectionHandler, DrawingListener {

	/** hosting EditorKit */
	private EditorKit kit;
	// inherited :
	// protected boolean changeLock
	// protected ArrayList children

	/**
	 * construct a new selection handler for this editor kit, with SelectionHandlerView as the default view.
	 * Highlighting mode is LOCAL by default.
	 */
	public DefaultSelectionHandler(EditorKit kit){
		if (DEBUG) debug("<init>");
		this.kit=kit;
		view = new CompositeView(this,null);
		Highlighter h = kit.getHighlighterFactory().createHighlighter(this);
		view.setHighlighter(h);
		setHighlightingMode(HighlightingMode.LOCAL);
	}

	/**
	 * @return a string that represents this object's name ; should be a key-entry to i18n files.
	 * @since jpicedt 1.3.3
	 */
	public String getName(){
		return "SelectionHandler"; // [pending] i18n
	}

	/** paint the selection handler highlighting */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		view.paintHighlighter(g,allocation,scale);
	}

	/** <code>hitTest</code> on the content of the selection only */
	public HitInfo hitTest(PEMouseEvent me){
		return view.hitTest(me,true); // highlighter.hitTest(), then possibly view.hitTest()
	}

	public boolean intersect(Rectangle2D r, java.util.ArrayList<Element> list){
		return view.intersect(r, true, list);
	}

	// overiden so as to support the SEPARATE mode only
	public void setCompoundMode(CompoundMode m){
	}

	///////////// SelectionHandler interface methods /////////////////////

	/**
	 * Return the selected elements wrapped in an array (may be a convenience call to asCollection)
	 */
	public Element[] asArray(){
		return toArray(new Element[0]);
	}


	/**
	 * Select all <code>Element</code>'s in the given drawing.
	 */
	public void selectAll(Drawing d){
		clear(); // avoid duplicate
		addAll(d);
	}

	/**
	 * Replace the current selection with the given element. This is a convenience call to
	 * <code>unSelectAll</code> then <code>addToSelection</code>.
	 */
	public void replace(Element e){
		clear();
		add(e);
	}


	/**
	 * La méthode correspondante de <code>PicGroup</code> est surchargée pour ne pas affecter les vues.
	 * @see SelectionHandler#replace(Element oldE, Element newE)
	 */
	public void replace(Element oldE, Element newE){
		if(remove(oldE))
			add(newE);
	}

	/**
	 * Delete all selected <code>Element</code>'s from the given <code>Drawing</code>, and remove the
	 * reference to them from the <code>SelectionHandler</code>.
	 */
	public void delete(Drawing dr){
		for (Iterator<Element> it = iterator(); it.hasNext();){ // iterate over selected elements
			Element e = it.next();
			it.remove(); // first remove element from selection-handler to avoid concurent modification (see note below)
			dr.remove(e); // then remove it from drawing => fires REMOVE DrawingEvent => calls changedUpdate => *might* raise concurrent modification exception if not taken care of properly
		}
		updateBoundingBox();
		fireChangedUpdate(DrawingEvent.EventType.REMOVE); // update view
	}

	/////// overriden from PicGroup so as to leave the added/removed element's parent unchanged. ////////

	/**
	 * overriden so that the parent of the added element isn't set to this, and its view is left unchanged.
	 * Addition is successfull only if neither child nor one of its parents is selected yet (for instance,
	 * it's impossible to select the child of a PicGroup that is already selected, since in a sense this child
	 * is already selected through indirect lineage).
	 */
	public boolean add(Element child){
		if (DEBUG) debug("child="+child);
		if (child==null||child == this||contains(child)) return false;
		children.add(child);
		// if child is a BranchElement, possibly unselect its children, if these were previously selected independently
		// (which may've occured by calling addToSelection() on a child at a previous time)
		if (child instanceof BranchElement){
			for (Element kinder: (BranchElement)child){
				remove(kinder); // no effect if not selected
			}
		}


		// rebuild buffer from scratch so as to preserve z-ordering in the drawing (use Collections.sort
		// with an appropriate Comparator)
		sort();

		updateBoundingBox();
		fireChangedUpdate(DrawingEvent.EventType.INSERT); // update view
		return true;
	}

	/**
	 * Convenient call to add(child).
	 */
	public void add(int position, Element child){
		add(child);
	}

	/**
	 * Appends all the elements in the given collection that are instance of Element
	 * as children of this BranchElement.
	 */
	public boolean addAll(Collection<? extends Element> c){
		boolean changed = false;
		for(Element child: c){
			if (child==null || child==this || contains(child)) continue;
			children.add(child);
			changed = true;
		}
		if (changed) {
			sort();
			updateBoundingBox();
			fireChangedUpdate(DrawingEvent.EventType.INSERT);
		}
		return changed;
	}

	/**
	 * Commodité d'appel de <code>addAll(c)</code>.
	 */
	public boolean addAll(int index, Collection<? extends Element> c){
		return addAll(c);
	}

	/**
	 * Overriden so as to leave child's parent and view unchanged.
	 * @param child if child is the selection-handler itself, call <code>removeAllChildren()</code>.
	 * @return <code>true</code> si child faisait partie de la sélection.
	 */
	public boolean remove(Object child){
		if (DEBUG) debug("child="+child);
		if (child == this) {
			clear(); // terrible hack to deselect all if click on a selection-handler's end-point
			return true;
		}
		else {
			/*if (!contains(child)) {
				if (DEBUG) debug("Already unselected!");
				return false; // already unselected
			}*/
			boolean removed=false;
			removed |= children.remove(child);
			if (child instanceof BranchElement){ // possibly unselect children as well if applicable (this is usually useless, except
					// if some children were separately selected
				for (Element kinder: (BranchElement)child){
					removed |= remove(kinder); // possibly reentrant if kinder is a BranchElement itself
				}
			}

			if (removed){
				updateBoundingBox();
				fireChangedUpdate(DrawingEvent.EventType.REMOVE); // update view
			}
			return removed;

		}
	}

	/**
	 * Remove the child with the given index from this <code>BranchElement</code>.
	 */
	public Element remove(int index){ // note that this is necessarily a direct-line child
		Element e = children.remove(index); // can throw IndexOutOfBoundException
		updateBoundingBox();
		fireChangedUpdate(DrawingEvent.EventType.REMOVE);
		return e;
	}

	/**
	 * overriden so as to leave children parent and view unchanged.
	 */
	public void clear(){
		if (isEmpty()) return;
		children.clear();
		updateBoundingBox();
		fireChangedUpdate(DrawingEvent.EventType.REMOVE); // update view
	}

	/**
	 * Gets the parent of the element.
	 * @return the current Drawing.RootElement attached to the hosting editor-kit; used
	 *         mainly by the attached view to fetch its container.
	 */
	public BranchElement getParent() {
		PECanvas c = kit.getCanvas();
		if (c!=null) return c.getDrawing().getRootElement();
		else return null;
	}

	/**
	 * Sort selected elements according to their z-ordering in the given drawing
	 */
	public void sort(){
		PECanvas canvas = kit.getCanvas();
		if (canvas==null) return;
		ZOrderingComparator comparator = new ZOrderingComparator(canvas.getDrawing());
		Collections.sort(children, comparator);
	}

	/**
	 * A comparator for z-axis ordering
	 */
	private class ZOrderingComparator implements Comparator<Element> {

		private Drawing dr;

		public ZOrderingComparator(Drawing dr){
			this.dr = dr;
		}

		/**
		 * Returns a positive number whenever o1 has a higher z-axis height than o2, etc.
		 */
		public int compare(Element o1, Element o2){
			BranchElement be = dr.getRootElement();
			int i1 = be.indexOf(o1);
			int i2 = be.indexOf(o2);
			return i1 - i2;
		}
	}

	/////////////////////////////////////
	/// EVENTS
	////////////////////////////////////

	/**
	 * Called each time this DefaultSelectionHandler changes.  This implementation is overriden from
	 * <code>AbstractElement</code> so as to update the associated <code>View</code> only, i.e. there is no
	 * event being forwarded to the parent (which here is the Drawing.RootElement, see
	 * e.g. <code>getParent()</code>).  The point is that this method gets called whenever
	 * setCtrlPt/scale/translate/&hellip; is called on the selection-handler, which indirectly triggers a
	 * DrawingEvent on behalf of the selected elements themselves.  Hence it doesn't make sense to post these
	 * events twice.  <br> If subclasser are willing to override this method, they should call
	 * <code>super.fireChangeUpdate</code>.
	 * @param eventType the event type
	 */
	protected void fireChangedUpdate(DrawingEvent.EventType eventType){
		if (DEBUG) debug("eventType="+eventType+", this="+toString());
		if (view != null) view.changedUpdate(eventType);
		// getParent().forwardChangedUpdate(this,eventType); => commented out so as not to post the same event twice.
	}


	//////////////////////////////////////////////

	/**
	 * Returns a String representing the group for debugging use only.
	 */
	public String toString(){

		String s = "[DefaultSelectionHandler@" + Integer.toHexString(hashCode()) + "{";
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


	/////////////////////// DrawingListener interface ////////////
	public void _changedUpdate(DrawingEvent e){ // debug only
		debug(e.toString());
	}
	/**
	 * Implementation of <code>DrawingListener</code> interface aimed at keeping this selection-handler always
	 * synchronized with the Drawing, especially when the Drawing content gets modified directly throught the
	 * Drawing API (as opposed to using selection-related methods in <code>PECanvas</code>).<p> This method is
	 * invoked when an element changed in the Drawing.<p> If this is a REMOVE event type, and if the removed
	 * children (of the changed <code>Element</code>) were selected, we remove these children from the
	 * selection as well, ie we always keep the content of the selection-handler synchronized with the
	 * associated Drawing.
	 */
	public void changedUpdate(DrawingEvent e){
		if (DEBUG)
			debug("DrawingEvent="+e+"\nCHANGE_LOCK="+changeLock);
		if (e.getType()==DrawingEvent.EventType.REMOVE || e.getType()==DrawingEvent.EventType.REPLACE){

			/*
			// fetch the parent from which children were removed/replaced : (e.g. Drawing.RootElement if Drawing.removeElement/replaceElement triggered this event)
			// if its removed/replaced children are still in the selectionHandler, remove them from the selection as well
			Element changed = e.getElement();

			// if "changed" is NOT a Drawing.RootElement, e.g. an element got removed from a selected PicGroup, there is nothing
			// specific to be done, since this selectionHandler only keeps references to direct children of Drawing.RootElement,
			// and as long as these references do not get removed from the Drawing, synchronization b/w the Drawing and
			// this SelectionHandler is not lost.
			if (!(changed instanceof Drawing.RootElement)) return;

			Drawing.RootElement rootElem = (Drawing.RootElement)changed;
			if (DEBUG) debugAppend("selection="+this.toString());
			for (Iterator<Element> it=iterator(); it.hasNext();){ // iterate over selected elements
				Element stillSelected = it.next();
				if (!rootElem.contains(stillSelected))  // if element was deleted from drawing, delete from selection as well
					it.remove(); // avoid concurrent modification exception
			}
			*/
			Drawing.RootElement rootElem = e.getDrawing().getRootElement();
			for (Iterator<Element> it=iterator(); it.hasNext();){ // iterate over selected elements
				Element stillSelected = it.next();// if element was deleted from drawing, delete from selection as well
				if (!rootElem.contains(stillSelected))  // contains walks down the lineage [July '06]
					it.remove(); // avoid concurrent modification exception
			}

			updateBoundingBox();
			view.changedUpdate(e.getType());
		}

		// the following event gets posted in two situations :
		// - either one of setCtrlPt/scale/translate/... was invoked on the SelectionHandler itself, and as a result
		//   all currently selected elements posted a DrawingEvent (we shall then receive as many DrawingEvent's as there are
		//   selected elements) : inherited field BranchElement.changeLock=true in this case, but because this SelectionHandler was
		//   NOT set as the parent of those selected elements (they indeed keep their original parent, ie Drawing.RootElement),
		//   events forwarded by modified children can not be trapped by the forwardChangedUpdate() method,
		//   and they indeed propagate upward until they reach the hosting Drawing. We must filter out these events, since
		//   they are of no interest where the state of this selectionHandler is concerned.
		// - or setCtrlPt/scale/translate/... was invoked directly on a selected element (either from a BSH macro, or a mouse-tool acting directly on an Element),
		//   and this element posted a DrawingEvent.
		//   BranchElement.changeLock=false in this case. This also means that the bounding-box is no longer up-to-date,
		//   hence we call updateBoundingBox, then fire a change-event to update the view
		// - or finally, the disposition of selected elements was changed through a call to bringXXX() in Drawing.RootElement
		//   => we must reorder the selection.
		else if (e.getType() == DrawingEvent.EventType.GEOMETRY_CHANGE && changeLock==false ){
			// && getHighlightingMode()==GLOBAL_MODE){ => commented out
			// The point is that, even in LOCAL_MODE, the bounding-box needs to
			// be up-to-date, since CompositeHighlighter uses it to know if it has to paint the highlighter or not (i.e. clip must intersect allocation)
			if (isEmpty()) return;  // On the other hand, if the selection is empty, this event originated from
			// the modification of the geometry of an
			// unselected Element, and we just don't care => this has no effect on the geometry of the selectionHandler

			Element changed = e.getElement(); // finally, check whether the changed element was selected or not:
			if (changed instanceof Drawing.RootElement){
				sort();
				return;
			}
			// Note that if "changed" is the SelectionHandler itself, contains() returns false => this is the way
			// we filter out useless events...
			if (contains(changed)){ // if changed is selected...
				updateBoundingBox();
				view.changedUpdate(e.getType()); // [SR:pending] the only annoying point is that we eventually trigger two "repaint",
				// one on behalf of the Element that got edited (through a call to its view), and one here.
				// this may slow down a bit.
			}
		}
	}

} // DefaultSelectionHandler
