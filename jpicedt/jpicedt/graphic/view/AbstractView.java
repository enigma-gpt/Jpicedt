// AbstractView.java --- -*- coding: iso-8859-1 -*-
// February 14, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: AbstractView.java,v 1.24 2013/03/27 06:55:21 vincentb1 Exp $
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
package jpicedt.graphic.view;

import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PointIndexIterator;
import jpicedt.graphic.model.DefaultPointIndexIterator;
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.BranchElement;
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.view.highlighter.Highlighter;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.ArrayList;
import static jpicedt.graphic.view.ViewConstants.*;

import static jpicedt.Log.*;

/**
 * Abstract implementation that provide some basic common behaviours for <code>View</code>'s.
 * @since jpicedt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: AbstractView.java,v 1.24 2013/03/27 06:55:21 vincentb1 Exp $
 */
public abstract class AbstractView implements View {

	//private final static boolean DEBUG = true;

	/**
	 * The graphic element that this View renders.
	 */
	protected Element element;

	/**
	 * The bounds rectangle used for clipping (as returned by <code>getBounds</code>); this should be updated
	 * by <code>changedUpdate</code>.
	 */
	protected Rectangle2D bounds = new Rectangle2D.Double();

	/**
	 * The highlighter delegate for this <code>view</code>.
	 */
	protected Highlighter highlighter;


	/**
	 * Construct a new View for the given <code>Element</code>.
	 */
	public AbstractView(Element element){
		if (DEBUG) debug(".<init> for element " + element);
		this.element = element;
	}

	/**
	 * Construct a new <code>View</code> for the given <code>Element</code>, delegating highlighting
	 * to the given <code>Highlighter</code>.
	 */
	public AbstractView(Element element, Highlighter h){
		if (DEBUG) debug(".<init> for element " + element);
		this.element = element;
		this.highlighter = h;
	}

	/**
 	 * @return the element the View is responsible for rendering.
	 */
	public Element getElement(){
		return element;
	}

	/**
 	 * Set the element the View is responsible for rendering.
	 */
	public void setElement(Element e){
		this.element = e;
	}

	///////////////////////////////////////////////////////////////////
	//// tree-structure
	///////////////////////////////////////////////////////////////////

	/**
	 * Returns the parent of the view, as given by the tree-structure the
	 * associated graphic element belongs to.
	 * @return the parent view, null if none (either because the element has no parent, or
	 *  because the parent has no <code>View</code>).
	 */
	public View getParentView() {
		BranchElement parent = element.getParent();
		if (parent == null) return null;
		else return parent.getView();
	}


	/**
	 * Fetches the container hosting the view.  This is useful for
	 * things like scheduling a repaint, finding out the host
	 * components font, etc.  The default implementation
	 * of this is to forward the query to the parent view, if any&hellip;
	 * (that means that for this method to return non-null, the element attached to this view
	 * must have a parent, and this parent, a view, etc&hellip;)
	 *
	 * @return the container, null if none
	 */
	public PECanvas getContainer() {
		View v = getParentView();
		return (v != null) ? v.getContainer() : null;
	}

	/**
	 * Fetches the <code>ViewFactory</code> implementation that is feeding the
	 * view hierarchy.
	 * @return the factory, null if none
	 */
	public ViewFactory getViewFactory() {
		View v = getParentView();
		return (v != null) ? v.getViewFactory() : null;
	}

	/**
	 * Fetch a Graphics for rendering from the hosting container (if not null).
	 * This can be used to determine font characteristics.
	 * @since jPicEdt 1.3
	 */
	public Graphics getGraphics() {
		Component c = getContainer();
		if (c==null) return null;
		return c.getGraphics();
	}

	/**
	 * Fetches the drawing (aka model) associated with the view.
	 * @return the view's model, null if none
	 */
	public Drawing getDrawing() {
		return element.getDrawing();
	}


	////////////////////////////////////////////////////////
	//// PAINT
	////////////////////////////////////////////////////////

	/**
	 * Ask the hosting container (if it's non-null) to repaint itself
	 * @param clip the clip rectangle in model-coordinate; if null, simply
	 *  call <code>repaint()</code> on the hosting container.
	 */
	public void repaint(Rectangle2D clip){
		PECanvas c = getContainer();
		if (c==null)
			return; // if this view is not installed in a tree with a container
		if (clip != null)
			c.repaintFromModelRect(clip);
		else
			c.repaint();
	}


	/**
	 * @return the bounds of this View<br>
     *  This will determine the clipping rectangle passed as a parameter to
	 *  repaint, and may include the highlighter's bounds as well.<br>
	 *  Default implementation return <code>bounds</code>.
	 */
	public Rectangle2D getBounds(){
		return bounds;
	}

	//////////////////////////////////////////////////////
	//// HIGHLIGHTER
	//////////////////////////////////////////////////////

	/**
	 * Returns the <code>Highlighter</code> responsible for rendering the highlighted part of this view.
	 * @return <code>null</code> if this view cannot be highlighted
	 */
	public Highlighter getHighlighter(){
		return highlighter;
	}

	/**
	 * Sets the <code>Highlighter</code> responsible for rendering the highlighted part of this view.
	 */
	public void setHighlighter(Highlighter h){
		this.highlighter = h;
		changedUpdate(null);
	}

	/**
	 * Render the Highlighter to the given graphic context.
	 * @param allocation current clipping
	 * @param scale The current scale factor from model to screen for the
	 *  <code>Graphics2D</code> context ; this may be used to scale down line
	 *  thickess, etc&hellip; so that lines/rectangle/&hellip; appear with the
	 *  same lenght on the screen whatever the scale factor that's set to the
	 *  graphic context.
	 */
	public void paintHighlighter(Graphics2D g, Rectangle2D allocation, double scale){
		if (highlighter!=null)
			highlighter.paint(g,allocation,scale);
	}

	/////////////////////////////////////////
	/////// MOUSE
	/////////////////////////////////////////

	/**
	 * Returns a <code>HitInfo</code> corresponding to the given mouse-event.
	 * Only the view's shape should be included in this test.
	 *
	 */
	abstract protected HitInfo hitTest(PEMouseEvent e);

	/**
	 * Depending on the value of <code>isHighlighVisible</code>, current implementation first delegates
	 * to the hosted <code>Highlighter</code>'s <code>hitTest()</code> method, then calls
	 * {@link #hitTest(PEMouseEvent) hitTest(PEMouseEvent)}.
	 * @return a HitInfo corresponding to the given mouse-event
	 * @param isHighlightVisible whether the receiver should include the
	 *  highlighter shapes (e.g. end-points) in the click-sensitive area.
	 */
	public HitInfo hitTest(PEMouseEvent e, boolean isHighlightVisible){

		HitInfo hi=null;
		if (isHighlightVisible && highlighter!=null)
		{
			if(DEBUG) debug("trying highlighter hit testing on element " + element.getName());
			hi=highlighter.hitTest(e);
		}
		if (hi==null)
		{
			if(DEBUG) debug("element hit testing on element " + element.getName());
			hi = hitTest(e);
		}
		else if(DEBUG)
			debug("highlighter hit testing on element " + element.getName());
		return hi;
	}

	/**
	 * Depending on the value of <code>isHighlighVisible</code>, current implementation first delegates to the
	 * hosted <code>Highlighter</code>'s <code>intersect()</code> method, then calls {@link
	 * #intersect(Rectangle2D, ArrayList) intersect(Rectangle2D, ArrayList&lt;Element&gt;)}.
	 */
	public boolean intersect(Rectangle2D r, boolean isHighlightVisible,  ArrayList<Element> list){
		if (isHighlightVisible && highlighter!=null && highlighter.intersect(r,list))
			return true;
		return intersect(r,list);
	}

	/**
	 * If this view intersects the given rectangle, add the associated <code>Element</code>
	 * or a child <code>Element</code> if it's more appropriate (for
	 * instance for composite views) to the given list (if non-null), and returns true.
	 */
	abstract protected boolean intersect(Rectangle2D r, ArrayList<Element> list);

}
