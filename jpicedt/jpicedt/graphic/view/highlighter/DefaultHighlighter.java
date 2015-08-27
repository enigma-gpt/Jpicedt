// DefaultHighlighter.java --- -*- coding: iso-8859-1 -*-
// September 20, 2003 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: DefaultHighlighter.java,v 1.7 2013/03/27 06:54:36 vincentb1 Exp $
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

package jpicedt.graphic.view.highlighter;

import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.view.HitInfo;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D.Double;
import java.util.*;

import static jpicedt.graphic.view.ViewConstants.CLICK_DISTANCE;
import static jpicedt.graphic.view.ViewConstants.BARBELL_SIZE;
import static jpicedt.Log.DEBUG;


/**
 * Default implementation of the <code>Highlighter</code> interface.
 * @since jPicEdt 1.4
 * @author Sylvain Reynal
 * @version $Id: DefaultHighlighter.java,v 1.7 2013/03/27 06:54:36 vincentb1 Exp $
 */
public class DefaultHighlighter implements Highlighter {


	/** factory */
	protected DefaultHighlighterFactory factory;

	/** highlighted <code>Element</code> */
	protected Element element;

	/** iterator over visible control-points */
	protected PointIndexIterator pointIndexIterator;

	/** highlighter shape ; should be null if N/A in subclass */
	protected Shape shape;

	/** Cache for highlighter bounds. */
	protected Rectangle2D.Double bounds = new Rectangle2D.Double();

	// ====== protected buffers used by performance critical methods ========
	/** buffer used by paint only ; may be safely used by subclasses as well */
	protected Rectangle2D rectBuffer = new Rectangle2D.Double();
	/** buffer used by paint only ; may be safely used by subclasses as well */
	protected PicPoint ptBuffer = new PicPoint();

	private ArrayList<Integer> hitIndices = new ArrayList<Integer>(); // used by hitTest (avoid re-creating arrays at each invokation)


	/**
	 * @param element the <code>Element</code> to be highlighted
	 */
	public DefaultHighlighter(Element element,DefaultHighlighterFactory f){
		this.element = element;
		this.factory = f;
	}

	/** @return the higlighted <code>Element</code>. */
	public Element getElement(){
		return element;
	}

	/**
	 * Fetches the <code>HighlighterFactory</code> implementation that is feeding the view hierarchy.
	 * @return the factory, null if none
	 */
	public HighlighterFactory getHighlighterFactory(){
		return factory;
	}

	//////////////////////////////////////////////
	/// EVENT HANDLING
	/////////////////////////////////////////////

	/**
	 * Give notification from the model that a change occured for an element's highlighting
	 * this highlighter is responsible for rendering.
	 * To reduce the burden for subclasses, this implemention dispatches to the following methods,
	 * in that order, depending on the value of "eventType" :
	 * <ol>
	 * <li> <code>syncShape()</code> if it's a <code>DrawingEvent.GEOMETRY_CHANGE</code>
	 * <li> <code>syncBounds()</code> if it's a <code>DrawingEvent.GEOMETRY_CHANGE</code>
	 * </ol>
	 */
	public void changedUpdate(DrawingEvent.EventType eventType, double scale){
		if (eventType==null || eventType!=DrawingEvent.EventType.ATTRIBUTE_CHANGE) {
			syncShape(scale);
			syncBounds(scale);
		}
	}

	/**
	 * Synchronizes the highlighter's bounding rectangle (aka clip) with the model;
	 * "bounds" is first computed from the set of visible control-points,
	 * then its size gets increased by <code>BARBELL_SIZE</code>. Finally, if <code>shape</code>
	 * (see <code>syncShape()</code>) is non-null,
	 * it gets appended to the bounding rectangle.
	 * @param scale The current scale factor from-model-to-screen for the <code>Graphics2D</code> context ;
	 *        this may be used to scale down line thickess, etc&hellip; so that e.g. barbells appear with the
	 *        same size on the screen whatever the current zoom factor.
	 */
	protected void syncBounds(double scale){
		// 1) compute rectangle containing all visible control-points :
		boolean inited = false;
		PointIndexIterator it = getControlPointsIterator();
		for(it.reset(); it.hasNext();){
			int ptIndex = it.next();
			PicPoint pt = element.getCtrlPt(ptIndex, ptBuffer); // store in ptBuffer
			if (!inited) {
				bounds.setRect(pt.x, pt.y, 0,0);
				inited=true;
			}
			else
				bounds.add(pt);
		}

		// 3) add shape if applicable :
		if (shape !=null) {
			if (!inited) {
				bounds.setRect(shape.getBounds2D()); // ie if no control-points
				inited=true;
			}
			else bounds.add(shape.getBounds2D());
		}

		// 2) enlarge by BARBELL_SIZE :
		if (inited){
			double barbellSize = 2.0*BARBELL_SIZE/scale; // x 1.2 : good guess so that small smudges
			bounds.setFrame(
			        bounds.getX()-barbellSize, bounds.getY()-barbellSize,
			        bounds.getWidth()+2*barbellSize, bounds.getHeight()+2*barbellSize);
		}
	}

	/**
	 * Synchronize the shape needed to paint this highlighter, with the model; this could be, for instance,
	 * tangents in the case of a Bezier curve, the outline in the case of an ellipse, etc.
	 * This implementation does nothing.
	 * @param scale The current scale factor from-model-to-screen for the <code>Graphics2D</code> context ;
	 *        this may be used to scale down line thickess, etc&hellip; so that e.g. barbells appear with the
	 *        same size on the screen whatever the scale factor being set to the graphic context.
	 */
	protected void syncShape(double scale){
	}

	////////////////////////////////////////////////////////
	//// PAINT
	////////////////////////////////////////////////////////

	/**
	 * Render the Highlighter to the given graphic context.<br> Current implementation first paints the
	 * <code>shape</code> member if it's non-null (see the <code>syncShape()</code> method), then
	 * control-points by delegating to a {@link jpicedt.graphic.model.DefaultPointIndexIterator
	 * DefaultPointIndexIterator}, using the highlighter color if the given allocation intersects the bounds
	 * of this view,
	 * @param scale The current scale factor from-model-to-screen for the <code>Graphics2D</code> context ;
	 *        this may be used to scale down line thickess, etc&hellip; so that e.g. barbells appear with the
	 *        same size on the screen whatever the scale factor being set to the graphic context.
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		//debug("allocation="+allocation);
		if (!allocation.intersects(getBounds())) return;

		// compute barbell size for the current zoom factor
		double barbellSize = BARBELL_SIZE/scale;

		// 1) paint highlighter shape if applicable (e.g., outline, tangents, ...):
		g.setStroke(factory.createStroke(scale));
		if (shape!=null) g.draw(shape);

		// 2) paint control-points of the associated Element:
		PointIndexIterator it = getControlPointsIterator();
		for(it.reset(); it.hasNext();){
			int ptIndex = it.next();
			//debug("ptIndex="+ptIndex);
			PicPoint pt = element.getCtrlPt(ptIndex, ptBuffer);
			rectBuffer.setRect(pt.x-barbellSize,pt.y-barbellSize,2*barbellSize,2*barbellSize);
			g.fill(rectBuffer);
			if (DEBUG){ // displays control-point indices
				Paint oldCol = g.getPaint();
				Font font = new Font("SansSerif", Font.PLAIN, 6).deriveFont(AffineTransform.getScaleInstance(0.5,-0.5));
				FontRenderContext frc = g.getFontRenderContext();
				TextLayout tl = new TextLayout(new Integer(ptIndex).toString(), font, frc);
				g.setPaint(Color.black);
				tl.draw(g,(float)pt.x,(float)pt.y);
				g.setPaint(oldCol);
			}
		}
	}

	/**
	 * Returns an iterator over control-points that should be displayed by the hightligher.
	 * This default implementation iterates over all control-points.
	 */
	public PointIndexIterator getControlPointsIterator(){
		// lasily allocate new Iterator :
		if (this.pointIndexIterator==null)
			this.pointIndexIterator = new DefaultPointIndexIterator(element);
		return this.pointIndexIterator;
	}

	/**
	 * @return the bounds of this Highlighter<br>
	 *         This will determine the clipping rectangle passed as a parameter to repaint
	 *         in the hosting view
	 */
	public Rectangle2D getBounds(){
		return bounds;
	}




	/////////////////////////////////////////////////////
	//// MOUSE
	/////////////////////////////////////////////////////

	/**
	 * Current implementation returns a HitInfo.Point if a click
	 * occured on one of the controlled-point indices returned by
	 * {@link #getControlPointsIterator getControlPointsIterator()}; null otherwise.
	 * @return a HitInfo corresponding to the given mouse-event
	 */
	public HitInfo hitTest(PEMouseEvent e){

		PicPoint ptClick = e.getPicPoint(); // in model coords

		// 1) test if click occured outside the view bounds, which can speed up a lot...
		if (!getBounds().contains(ptClick)) return null;

		// 2) test click on control-points :
		// the true maximum distance for a click to be considered as a hit must be scaled down
		// by the current zoom factor, so that zooming in allows for a greater precision in the
		// selection/deselection of Element's :
		double click_distance = CLICK_DISTANCE / e.getCanvas().getZoomFactor();
		hitIndices.clear();
		PointIndexIterator it = getControlPointsIterator();
		for(it.reset(); it.hasNext();){
			int ptIndex = it.next();
			//debug("ptIndex="+ptIndex);
			element.getCtrlPt(ptIndex, ptBuffer); // dest = ptBuffer
			if (ptBuffer.distance(ptClick) <= click_distance)
				hitIndices.add(ptIndex);
		}
		if (!hitIndices.isEmpty()) return new HitInfo.Point(element, hitIndices, e);

		// 3) test click on hightlighter's shape, if applicable :
		if (shape != null){
			int segmentIndex = PEToolKit.testDistanceToPath(shape, ptClick, click_distance);
			if (segmentIndex >= 0) return new HitInfo.HighlighterStroke(element,segmentIndex,e);
		}

		// 4) no match
		return null;

	}

	/**
	 * If this highligher intersects the given rectangle, add the associated Element (or a child Element if it's more appropriate, for
	 * instance for composite views) to the given list, and returns true.
	 */
	public boolean intersect(Rectangle2D r, ArrayList<Element> list){
		// 1) check if r contains a control-point:
		PointIndexIterator it = getControlPointsIterator();
		for(it.reset(); it.hasNext();){
			ptBuffer = getElement().getCtrlPt(it.next(), ptBuffer);
			if (r.contains(ptBuffer.x, ptBuffer.y)){
				if (list!=null) list.add(getElement());
				return true;
			}
		}
		// 2) check if r intersects highlighter stroke:
		if (shape!=null && shape.intersects(r)){
			if (list!=null) list.add(getElement());
			return true;
		}
		return false;
	}

}
