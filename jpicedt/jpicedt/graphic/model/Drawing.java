// Drawing.java --- -*- coding: iso-8859-1 -*-
// January 2, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: Drawing.java,v 1.29 2013/03/27 07:02:49 vincentb1 Exp $
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
import jpicedt.graphic.model.*;
import jpicedt.graphic.event.*;
import jpicedt.graphic.view.*;
import jpicedt.widgets.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
//import javax.swing.event.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.undo.*;

import static jpicedt.Log.*;

/**
 * An extensible array used to store Elements<p>
 * This is the MODEL part of the View-Model-Controller paradigm (aka javax.swing.text.Document), aimed
 * at being plugged into an existing PECanvas. It has the structure of a <b>tree-model</b>, with the root element
 * of the tree being Drawing.RootElement : Element's added to this Drawing are actually added to its RootElement ;
 * each Element in turn may or may not have children (see e.g. classes DefaultLeafElement and BranchElement).
 * <br>
 * Element's should be added to this Drawing using the Drawing's API (i.e. shouldn't be directly added to RootElement),
 * by using the <code>addElement()</code> method.
 * so that DrawingEvent's dispatching works properly.
 * <p>
 * Objects are stored in ascending order according to their z-value, i.e. from back to front.
 * Note that this class does NOT include any selection-handling mechanism : for a variety of reasons, this mechanism has been
 * moved to the associated EditorKit (aka "Controller" in the VMC paradigm), which may itself delegate this behaviour
 * to an appropriate SelectionHandler. This makes the Drawing class a versatile document model for handling a variety of
 * graphics elements without bothering about the UI interaction.
 * @see jpicedt.graphic.toolkit.EditorKit
 * @see jpicedt.graphic.PECanvas
 *
 * @since jPicEdt 1.3
 * @author Sylvain Reynal
 * @version $Id: Drawing.java,v 1.29 2013/03/27 07:02:49 vincentb1 Exp $
 */
public class Drawing implements StateEditable,java.util.List<Element> {

	////////////////////////////
	//// PUBLIC CONSTANT FIELDS
	////////////////////////////

	///////////////////////////////
	//// PRIVATE & PROTECTED FIELDS
	///////////////////////////////

	/** the root-element of the drawing */
	protected RootElement root;

	/** list of listener's (e.g. <code>DrawingListener</code>'s) that get notified events from this model */
	protected EventListenerList listenerList = new EventListenerList();

	/** a string containing commands read from file but not parsed */
	protected String notParsedCommands="";

	/** bounding box used when formatting to text ; null means that it'll be computed automatically */
	protected BoundingBox boundingBox;
	// latest ViewFactory used to build root-view (used for the StateEditable interface implementation)
	private ViewFactory viewFactory;

	/////////////////////////
	// CONSTRUCTORS
	/////////////////////////

	/**
	 * Construct a new empty Drawing
	 */
	public Drawing(){
		if (DEBUG) debug("<init>");
		root = new RootElement();
		boundingBox = new BoundingBox();
	}

	/**
	 * Construct a new Drawing from the given Collection, which is supposed to contain <code>Element</code>'s.
	 * Children are cloned beforehands.
	 */
	public Drawing(Collection<Element> c){
		if (DEBUG) debug("<init> from collection "+c);
		root = new RootElement(c);
		boundingBox = new BoundingBox();
	}

	/**
	 * Construct a new Drawing whose content is initialized from the content of the given
	 * <code>BranchElement</code>.  Children are cloned beforehands, so it's perfectly safe to use this
	 * constructor if one doesn't want to modify the initial content of the given <code>BranchElement</code>.
	 */
	public Drawing(BranchElement e){
		if (DEBUG) debug("<init> from BranchElement : " + e);
		root = new RootElement(e);
		boundingBox = new BoundingBox();
	}

	/**
	 * Cloning constructor.
	 */
	public Drawing(Drawing dr){
		if (DEBUG) debug("cloning : "+dr);
		root = new RootElement(dr.root);
		notParsedCommands = new String(dr.notParsedCommands);
		boundingBox = new BoundingBox(dr.boundingBox);
	}

	/**
	  * @return A deep copy of this <code>Drawing</code>.
	  */
	public Drawing clone(){
		return new Drawing(this);
	}


	///////////////////////////////////////////////////////////////////////////
	//// CONTENT HANDLING
	///////////////////////////////////////////////////////////////////////////

	/**
	 * @return the root-element for the tree hierarchy that stores element in this drawing.
	 */
	public RootElement getRootElement(){
		return root;
	}

	/**
	 * @return The number of graphic objects in this drawing.
	 */
	public int size(){
		return root.size();
	}

	/**
	 * Replace the element at the given index with the given graphic element
	 * No effet if "element" already belongs to the drawing.
	 */
	public Element set(int index, Element element){ // was replaceElement
		if (DEBUG) debug("OLD element = "+get(index)+"\n\tNEW element = "+element);
		return root.set(index, element); // fire a changed update to the drawing (REPLACE)
	}

	/**
	 * Replace the given "src" element with the given "dest" element, if "src" belongs to this drawing.
	 * No effet if "dest" already belongs to the drawing.
	 */
	public void replace(Element src, Element dest){ // was replaceElement
		root.replace(src, dest); // fire a changed update to the drawing (REPLACE)
	}

	/**
	 * @return The element at position <code>i</code>.
	 */
	public Element get(int i){
		return root.get(i);
	}

	/**
	 * @return The bounding-box of the drawing.
	 */
	public Rectangle2D getBoundingBox(){
		if (!boundingBox.isAutoCompute())
			return boundingBox.getBoundingBox(null); // manual mode

		root.updateBoundingBox(); // because implementation of forwardChangeUpdate in RootElement doesn't call updateBB
		return root.getBoundingBox(null);
	}

	/**
	 * Set bounding box manually.
	 */
	public void setBoundingBox(Rectangle2D bb){
		if (bb==null)
			setAutoComputeBoundingBox(true);
		else
			boundingBox.setGeometry(bb);
	}

	/**
	 * Return true if the bb is computed automatically on-the-fly.
	 */
	public boolean isAutoComputeBoundingBox(){
		return boundingBox.isAutoCompute();
	}

	/**
	 * Return true if the bb is computed automatically on-the-fly.
	 */
	public void setAutoComputeBoundingBox(boolean b){
		boundingBox.setAutoCompute(b);
	}

	/**
	 * Return true if the bb is computed automatically on-the-fly.
	 */
	public boolean isDisplayBoundingBox(){
		return boundingBox.isVisible();
	}

	/**
	 * Return true if the bb is computed automatically on-the-fly.
	 */
	public void setDisplayBoundingBox(boolean b){
		boundingBox.setVisible(b);
	}

	/////////////////////////
	//// PAINT
	/////////////////////////

	/**
	 * @return the root-view that renders this <code>Drawing</code> to a graphic context ; this is actually
	 *         the View associated with the root-element.
	 */
	public View getRootView(){
		return root.getView();
	}

	/**
	 * Set the view tree that renders this drawing to screen, starting from the root-element.
	 * The root-view produced by the factory (to be associated with the root-element)
	 * must know how to find its container (usually a <code>PECanvas</code>), otherwise
	 * calls to <code>repaint</code>, etc&hellip; will fail.
	 * @param f the <code>ViewFactory</code> that produces <code>View</code>'s for the <code>Element</code>'s
	 * of the tree.
	 */
	public void setViewTree(ViewFactory f){
		if (DEBUG) debug("using factory :"+f);
		viewFactory = f;
		root.setViewFromFactory(f);
	}




	/////////////////////////
	//// LISTENERS
	/////////////////////////

	/**
	 * adds a <code>DrawingListener</code> to the <code>Drawing</code>.
	 */
	public void addDrawingListener(DrawingListener l) {
		if (DEBUG) debug("addDrawingListener : "+l);
		listenerList.add(DrawingListener.class, l);
	}

	/**
	 * removes a <code>DrawingListener</code> from the <code>Drawing</code>.
	 */
	public void removeDrawingListener(DrawingListener l) {
		if (DEBUG) debug("removeDrawingListener : "+l);
		listenerList.remove(DrawingListener.class, l);
	}

	/**
	 * Notify all listeners that have registered interest for notification on this event type.
	 * @param changed an array containing the elements that changed
	 */
	protected void fireChangedUpdate(Element changed, DrawingEvent.EventType type) {
		Object[] listeners = listenerList.getListenerList();
		DefaultDrawingEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			// lazily create the event :
			if (e==null) e = new DefaultDrawingEvent(changed, type);
			if (listeners[i]==DrawingListener.class) {
				((DrawingListener)listeners[i+1]).changedUpdate(e);
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////
	//// StateEditable interface
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Store a clone of RootElement in the given Hashtable, with key = "state"
	 */
	public void storeState(Hashtable<Object,Object> map){
		RootElement clone = root.clone();
		//clone.setViewFromFactory(null); // remove views
		map.put("state", clone);
	}

	/**
	 * Restore RootElement from the given Hashtable, using key="state"
	 */
	public void restoreState(Hashtable<?,?> map){
		RootElement restored = (RootElement)map.get("state");
		root = restored.clone(); // if we don't clone, then any editing made to the drawing
		// (i.e. to "root") from now on, will modify, not only "root" itself (which is what we want!)
		// but also the state stored in the hashtable. PB is that the hash MUST remain immutable
		// if we want undo (or redo) to recall the states that were stored in the exact configuration
		// they had when they were stored !
		if (viewFactory != null) setViewTree(viewFactory); // ensure root is displayed using current viewfactory,
		// not the one it was stored with.
		root.fireChangedUpdate(DrawingEvent.EventType.INSERT);
	}

	/////////////////////////
	//// Collection interface
	/////////////////////////

	/** convenience call to RootElement's method */
	public boolean  add(Element o){
		return root.add(o); // fire a changed update to the drawing (INSERT)
	}
	/** convenience call to RootElement's method */
	public void  add(int position, Element o){
		root.add(position, o); // fire a changed update to the drawing (INSERT)
	}
	/** convenience call to RootElement's method */
	public boolean  addAll(Collection<? extends Element> c){
		return root.addAll(c);
	}
	/** convenience call to RootElement's method */
	public boolean  addAll(int pos, Collection<? extends Element> c){
		return root.addAll(pos,c);
	}
	/** convenience call to RootElement's method */
	public void  clear() {
		root.clear();
	}
	/** convenience call to RootElement's method */
	public boolean  contains(Object o){
		return root.contains(o);
	}
	/** convenience call to RootElement's method */
	public boolean  containsAll(Collection<?> c){
		return root.containsAll(c);
	}
	/** convenience call to RootElement's method */
	public boolean  isEmpty(){
		return root.isEmpty();
	}
	/** convenience call to RootElement's method */
	public Iterator<Element> iterator(){
		return root.iterator();
	}
	/** convenience call to RootElement's method */
	public boolean  remove(Object o){
		return root.remove(o); // fire a changed update to the drawing
	}
	/**
	 * Remove the Element with the given index from this BranchElement
	 */
	public Element remove(int index){
		return root.remove(index);
	}
	/** convenience call to RootElement's method */
	public boolean  removeAll(Collection<?> c){
		return root.removeAll(c);
	}
	/** convenience call to RootElement's method */
	public boolean  retainAll(Collection<?> c){
		return root.retainAll(c);
	}
	/** convenience call to RootElement's method */
	public Object[]  toArray(){
		return root.toArray();
	}
	/** convenience call to RootElement's method */
	public <T> T[] toArray(T[] a){
		return root.toArray(a);
	}

	public java.util.List<Element> subList(int fromIndex, int toIndex){
		return root.subList(fromIndex, toIndex);
	}

	public ListIterator<Element> listIterator(){
		return root.listIterator();
	}

	public ListIterator<Element> listIterator(int index){
		return root.listIterator(index);
	}

	/**
	 * Returns the last index of the given Element
	 * @return -1 if not found
	 */
	public int lastIndexOf(Object o){
		return root.lastIndexOf(o);
	}

	/**
	 * Returns the index of the given <code>Element</code>.
	 * @return -1 if not found
	 */
	public int indexOf(Object child){
		return root.indexOf(child);
	}

	////////////////////////////////////////////////////////////////////////////
	//// EVENTS HANDLING
	////////////////////////////////////////////////////////////////////////////

	/**
	 * An implementation of <code>DrawingEvent</code>.
	 */
	public class DefaultDrawingEvent implements DrawingEvent {

		private DrawingEvent.EventType eventType;
		private Element element;

		/**
		 * @param element the changed <code>Element</code>
		 * @param type the event type
		 */
		public DefaultDrawingEvent(Element element, DrawingEvent.EventType type){

			this.element=element;
			this.eventType=type;
		}

		/**
		 * @return the source of this event
		 */
		public Drawing getDrawing(){
			return Drawing.this;
		}

		/**
		 * @return the changed <code>Element</code>.
		 */
		public Element getElement(){
			return element;
		}

		/**
		 * @return the event type
		 */
		public EventType getType(){
			return eventType;
		}

		/**
		 * @return a string describing this event
		 */
		public String toString(){

			String s = "[DrawingEvent@" + Integer.toHexString(this.hashCode())
			       + ", type=" + getType()
				   + ", changed-element=" + getElement()
				   + ", source=" + getDrawing() + "]";
			return s;
		}

	} // DefaultDrawingEvent


	////////////////////////////////////////////////////////////////////////////
	//// ROOT ELEMENT
	////////////////////////////////////////////////////////////////////////////

	/**
	 * The <code>Element</code> that is the root of the tree of <code>Element</code>'s in this
	 * <code>Drawing</code>.  Children can be directly added to this <code>RootElement</code> (i.e. instead of
	 * using the Drawing API), since changed-update are always forwarded to the hosting drawing.
	 */
	public class RootElement extends BranchElement {

		/**
		 * Construct a new empty RootElement
		 */
		public RootElement(){
			super();
		}

		/**
		 * construct a new <code>RootElement</code> whose content is initialized from the content of the given
		 * BranchElement Children are cloned beforehands, so it's perfectly safe to use this constructor when
		 * one doesn't want to modify the initial content of the given <code>BranchElement</code>.
		 * @param e The Drawing object to clone
		 */
		public RootElement(BranchElement e){
			super(e);
		}

		/**
		 * Create a new <code>BranchElement</code> from the content of the given <code>Collection</code> of
		 * <code>Element</code>'s.  Children are cloned as well (i.e. this is a deep copy).
		 */
		public RootElement(Collection<Element> c){
			super(c);
		}

		// for the associated RootView, see EditorKit
		public BoundingBox getBoundingBox(){
			return boundingBox;
		}

		/**
		 * @return the name of this element
		 */
		public String getDefaultName(){
			return "drawing.root-element";
		}

		public String getName(){
			return getDefaultName();
		}

		/**
		  * @return a deep copy of this Drawing
		  */
		public RootElement clone(){
			return new RootElement(this);
		}

		/**
		 * Retrieves the underlying drawing
		 * Overriden to return this.
		 */
		public Drawing getDrawing() {
			return Drawing.this;
		}

		/**
		 * Gets the parent of the element. Overriden to return null (root element has no parent)
		 */
		public BranchElement getParent() {
			return null;
		}

		/**
		 * Sets the parent of the element. This implementation does nothing, since this root-element
		 * has no parent.
		 */
		public void setParent(BranchElement p) {
		}

		/**
		 * Called each time the content of this RootElement has changed. This fires the change to the hosting drawing.<br>
		 * Ex : called addChild() on this RootElement -> fires an INSERT event.
		 */
		protected void fireChangedUpdate(DrawingEvent.EventType eventType){
			if (DEBUG) debug("eventType="+eventType);
			if (Drawing.this==null) return; // this may be the cased during instanciation
			// of a new Drawing using a cloning constructor ; in this case, Drawing.this
			// is null as long as <init> isn't through.
			Drawing.this.fireChangedUpdate(this, eventType);
			if (view != null) view.changedUpdate(eventType); // updates rootView AFTER
			// listener have had a chance to update their state, so that a call to repaint
			// (triggered by view.changedUpdate) doesn't raise an Exception (this especially useful for
			// selection-handlers which must keep their state synchronized with the Drawing)
		}

		/**
		 * Called by a child to inform of some change that occured to it or one of its children.
		 * This fires a DrawingEvent with the given <code>child</code> as the element that changed.
		 * This mainly occurs when the geometry of an element is modified.
		 */
		public void forwardChangedUpdate(Element child,DrawingEvent.EventType eventType){
			if (DEBUG) debug("eventType="+eventType);
			if (Drawing.this==null) return; // see note above
			if (!changeLock) { // if a change is not underway here
				if (boundingBox.isAutoCompute() && boundingBox.isVisible()) {
					boundingBox.updateFromRootElement();
				}
				Drawing.this.fireChangedUpdate(child, eventType);
			}
		}

		/**
		 * return null
		 */
		public PicPoint getCtrlPt(int numPoint, PicPoint src){
			return null;
		}

		/**
		 * does nothing
		 */
		public void setCtrlPt(int numPoint, PicPoint src, EditPointConstraint c){
		}

		/**
		 * Return 0
		 */
		public int getFirstPointIndex(){
			return 0;
		}

		/**
		 * Return 0
		 */
		public int getLastPointIndex(){
			return 0;
		}

		/**
		 * Update the bounding box by "unioning" the children's bounding box.
		 * This implementation does nothing.
		 */
		//protected void updateBoundingBox(){}

		/**
		* set the view for this Element from the given view factory, then set the view for children.
		*/
		public void setViewFromFactory(ViewFactory f){
			super.setViewFromFactory(f); // set view for children
			boundingBox.setViewFromFactory(f);
		}
	} // RootElement


	public class BoundingBox extends PicParallelogram {

		private boolean isAutoCompute;
		private boolean isVisible;

		public BoundingBox(){
			super(new PicPoint(0,0), new PicPoint(10,10));
			_setAttributes();
			isAutoCompute = true;
			isVisible = false;
		}

		/**
		* "cloning" constructor (to be used by clone())
		*/
		public BoundingBox(BoundingBox bb){
			super(bb);
			_setAttributes();
			isAutoCompute = bb.isAutoCompute;
			isVisible = bb.isVisible;
		}

		private void _setAttributes(){
			super.setAttribute(PicAttributeName.LINE_COLOR, Color.blue);
			super.setAttribute(PicAttributeName.LINE_STYLE, StyleConstants.LineStyle.DOTTED);
		}

		public void setAutoCompute(boolean b){
			isAutoCompute = b;
			if (b)
				updateFromRootElement();
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		}

		public boolean isAutoCompute(){
			return isAutoCompute;
		}

		public void setVisible(boolean b){
			isVisible = b;
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		}

		public boolean isVisible(){
			return isVisible;
		}

		void updateFromRootElement(){
			root.updateBoundingBox(); // because implementation of forwardChangeUpdate in RootElement doesn't call updateBB
			setGeometry(root.getBoundingBox(null));
		}

		public BoundingBox clone(){
			return new BoundingBox(this);
		}

		public String getDefaultName(){
			return jpicedt.Localizer.currentLocalizer().get("misc.BoundingBox");
		}

		public String getName(){
			return getDefaultName();
		}

		public View getView(){
			if (isVisible())
				return view;
			return null;
		}

		public Drawing getDrawing() {
			return Drawing.this;
		}

		public BranchElement getParent() {
			return root;
		}

		protected void fireChangedUpdate(DrawingEvent.EventType eventType){
			if (view != null) view.changedUpdate(eventType);
			//BranchElement p = getParent();
			//if (p!=null) p.forwardChangedUpdate(this,eventType); // forward to parent , package restricted access.
		}

		public void setParent(BranchElement p) {
		}

		public void setAttributeSet(PicAttributeSet attributeSet){
		}

		public <T> void setAttribute(PicAttributeName<T> name, T value){
		}

		public void setCtrlPt(int numPoint, PicPoint pt, EditPointConstraint constraint){
			switch (numPoint){
			case SIDE_B: // forbid making it a parallelogram
			case SIDE_R:
			case SIDE_T:
			case SIDE_L:
				return;
			default:
				super.setCtrlPt(numPoint, pt, constraint);
			}
		}

		public void setGeometry(PicParallelogram para){
			if (!para.isRectangle())
				return;
			super.setGeometry(para);
		}

		public void rotate(PicPoint ptOrg, double angle){
		}

		public void mirror(PicPoint ptOrg, PicVector normalVector){
		}

		public void shear(PicPoint ptOrg, double shx, double shy){
		}

		public ArrayList<PEAction> createActions(ActionDispatcher actionDispatcher, ActionLocalizer localizer, HitInfo hi) {
			//return super.createActions(actionDispatcher, localizer, hi);
			return null; // need to define our own actions here
		}

		public AbstractCustomizer createCustomizer(){
			return null;
		}

	}

	///////////////////////////////////
	//// STRING FORMATING
	///////////////////////////////////

	/**
	 * Returns a String representing the drawing for debugging use only.
	 */
	public String toString(){

		String s = "[Drawing@" + Integer.toHexString(hashCode());
		s += ", size=" + root.size();
		s += " {";
		int j=0;
		for(Iterator<Element> i = root.iterator(); i.hasNext(); j++){
			StringBuffer buf = new StringBuffer(i.next().toString());
			int pos=0;
			while(true){
				pos = buf.toString().indexOf('\n',pos);
				if (pos == -1) break;
				buf.insert(pos+1,'\t');
				pos += 2;
			}
			s += "\n\t" + j + ":" + buf;
		}
		s += "\n}]";
		return s;
	}

	/**
	 * @param str a string containing commands read from file or inserted by user, but not parsable.
	 */
	public void setNotparsedCommands(String str){
		notParsedCommands = str;
	}

	/**
	 * @return a string containing (e.g. LaTeX) commands read from file or inserted by user, but not parsable.
	 */
	public String getNotparsedCommands(){
		return notParsedCommands;
	}

	////////////////////////////// CUSTOMIZER /////////////////////////////

	/**
	 * Return a bounding box customizer for this Drawing
	 */
	public Customizer getCustomizer(){
		return new Customizer();
	}

	/**
	 * a customizer for editing the bounding box
	 */
	public class Customizer extends AbstractCustomizer implements ChangeListener, ActionListener {

		private DecimalNumberField xMinTF, yMinTF, xMaxTF, yMaxTF;
		private JCheckBox automaticCB, isVisibleCB;

		/**
		 * Construct a customizer for editing the bounding box
		 */
		public Customizer(){
			initGui();
		}

		private void initGui(){
			JPanel p = new JPanel(new GridLayout(6,2,5,5));
			p.setBorder(BorderFactory.createEtchedBorder());
			p.add(new JLabel(" x-min (mm) :"));
			p.add(xMinTF = new DecimalNumberField(0,10));
			p.add(new JLabel(" x-max (mm) :"));
			p.add(xMaxTF = new DecimalNumberField(0,10));
			p.add(new JLabel(" y-min (mm) :"));
			p.add(yMinTF = new DecimalNumberField(0,10));
			p.add(new JLabel(" y-max (mm) :"));
			p.add(yMaxTF = new DecimalNumberField(0,10));
			p.add(new JLabel(" "+jpicedt.Localizer.currentLocalizer().get("misc.Automatic")));
			p.add(automaticCB = new JCheckBox());
			p.add(new JLabel(" "+jpicedt.Localizer.currentLocalizer().get("misc.Visible"))); // [pending] need sync'
			p.add(isVisibleCB = new JCheckBox());
			add(p, BorderLayout.NORTH);
		}

		private void addListeners(){
			xMinTF.addActionListener(this);
			xMaxTF.addActionListener(this);
			yMinTF.addActionListener(this);
			yMaxTF.addActionListener(this);
			automaticCB.addChangeListener(this);
			isVisibleCB.addChangeListener(this);
		}

		private void removeListeners(){
			xMinTF.removeActionListener(this);
			xMaxTF.removeActionListener(this);
			yMinTF.removeActionListener(this);
			yMaxTF.removeActionListener(this);
			automaticCB.removeChangeListener(this);
			isVisibleCB.removeChangeListener(this);
		}

		/** called by a click on "automaticCB" */
		public void stateChanged(ChangeEvent evt){
			boolean sel = automaticCB.isSelected();
			xMinTF.setEnabled(!sel);
			yMinTF.setEnabled(!sel);
			xMaxTF.setEnabled(!sel);
			yMaxTF.setEnabled(!sel);
			store();
		}

		public void actionPerformed(ActionEvent evt){
			store();
		}

		/**
		 * @return the panel title, used e.g. for Border or Tabpane title.
		 */
		public String getTitle(){
			return jpicedt.Localizer.currentLocalizer().get("misc.BoundingBox");
		}


		/**
		 * Load widgets display content with a default value,
		 * presumably from a "default preferences" file or a dedicated storage class.
		 */
		public void loadDefault(){
			automaticCB.setSelected(true);
			isVisibleCB.setSelected(false);
		}

		/**
		 * Load widgets value, presumably from a "preferences" file or a dedicated storage class
		 */
		public void load(){
			removeListeners();
			automaticCB.setSelected(boundingBox.isAutoCompute());
			isVisibleCB.setSelected(boundingBox.isVisible());
			Rectangle2D bb = getBoundingBox();
			xMinTF.setValue(bb.getMinX());
			yMinTF.setValue(bb.getMinY());
			xMaxTF.setValue(bb.getMaxX());
			yMaxTF.setValue(bb.getMaxY());
			addListeners();
		}

		/**
		 * Store current widgets value, presumably to a file or to a dedicated storage class
		 */
		public void store(){
			boundingBox.setAutoCompute(automaticCB.isSelected());
			boundingBox.setVisible(isVisibleCB.isSelected());
			Rectangle2D.Double bb = new Rectangle2D.Double();
			bb.x=xMinTF.getValue();
			bb.y=yMinTF.getValue();
			bb.width=xMaxTF.getValue()-bb.x;
			bb.height=yMaxTF.getValue()-bb.y;
			setBoundingBox(bb);
		}
	}

} // Drawing
