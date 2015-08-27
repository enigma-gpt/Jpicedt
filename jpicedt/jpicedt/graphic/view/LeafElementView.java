// LeafElementView.java --- -*- coding: iso-8859-1 -*-
// July 19, 2006 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
// Copyright (C) 2007/2013 Sylvain Reynal, Vincent Belaïche
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
// Version: $Id: LeafElementView.java,v 1.15 2013/03/27 07:20:31 vincentb1 Exp $
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

import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.ContentType;
import jpicedt.ui.util.RunExternalCommand;

import java.awt.Shape;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Graphics2D;
import java.awt.Color;
//import java.awt.font.*;
//import java.awt.image.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.lang.reflect.*;

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.view.ViewConstants.*;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.toRadians;

/**
 * A View for rendering leaf-elements; implements attributes caching, and shape rendering.
 * Subclasses might just override the <code>changedUpdate</code> method,
 * and update the <code>shape</code> variable accordingly.
 *
 */
public class LeafElementView extends AbstractView {

	protected AttributesViewFactory attrFactory;

	/** shape to be painted ; subclass must update it properly */
	protected Shape shape;
	/** shadow to be painted if non-null ; updated using shape */
	protected Shape shadow;
	/** stroke for outline and arrows (cached) */
	protected BasicStroke outlineStroke;
	/** stroke for overstrike (cached) */
	protected BasicStroke overstrikeStroke;
	/** paint for interior (cached) */
	protected Paint interiorPaint;
	/** paint for outline (cached) */
	protected Paint outlinePaint;
	/** paint for shadow (cached) */
	protected Paint shadowPaint;
	/** arrows to be painted if non-null */
	protected ArrowView leftArrow, rightArrow;

	/**
	 * construct a new View for the given Element
	 */
	public LeafElementView(Element e, AttributesViewFactory f){
		super(e);
		this.attrFactory = f;
	}

	/**
	 * Give notification from the model that a change occured for an element this view is responsible
	 * for rendering.<p>
	 * To reduce the burden for subclasses, this implemention dispatches to the following methods,
	 * in that order, depending on the value of "eventType" :
	 * <ul>
	 * <li> <code>syncAttributes()</code> if it's an ATTRIBUTE_CHANGE event or during initialization
	 * <li> <code>syncShape()</code> in any case
	 * <li> <code>syncBounds()</code> in any case.
	 * <li> <code>changedUpdate</code> on the highlighter delegate, if any.
	 * </ul>
	 * then call repaint with the union of the old and the new bounds as the argument.<br>
	 * Subclass might simply want to update <code>shape</code> before calling <code>super.changedUpdate()</code>,
	 * or override <code>syncAttributes()</code> and <code>syncBounds()</code> if they must implement
	 * more specific behaviour.
	 */
	public void changedUpdate(DrawingEvent.EventType eventType){
		//if (DEBUG) debug("eventType="+eventType);
		Rectangle2D oldClip = bounds;
		// cache attributes
		if (eventType==null || eventType==DrawingEvent.EventType.ATTRIBUTE_CHANGE) {
			syncAttributes();
			syncShape();
			syncShadow();
			syncArrowsGeometry(); // arrows attribute are updated in syncAttributes()
			syncBounds();
			if (highlighter!=null) {
				double s = 1.0;
				PECanvas canvas = getContainer();
				if (canvas != null) s=canvas.getScaleFactor();
				highlighter.changedUpdate(eventType,s);
				bounds.add(highlighter.getBounds());
			}
			repaint(null); // repaint all in case arrow type/shadow/... changed.
		}
		else { // else other events may result from a translation/scaling/setCtrlPt/...
			syncShape();
			syncShadow();
			syncArrowsGeometry(); // [pending] could actually be optimized, in that updating is not necessary after a translation or a rescaling
			// always update bounds (in model-coordinate)
			syncBounds();
			if (highlighter!=null) {
				double s = 1.0;
				PECanvas canvas = getContainer();
				if (canvas != null) s=canvas.getScaleFactor();
				highlighter.changedUpdate(eventType,s);
				bounds.add(highlighter.getBounds());
			}
			// repaint
			oldClip.add(bounds);
			repaint(oldClip);
		}
	}

	/**
	 * Synchronizes cached attributes values with the model ;
	 * <ul>
	 * <li>outlineStroke, from the ViewFactory, using <code>createStroke</code>
	 * <li>overstrikeStroke, from the ViewFactory, using <code>createStrokeForOverstrike</code>
	 * <li>outlinePaint, from the ViewFactory, using <code>createPaintForOutline</code>
	 * <li>interiorPaint, from the ViewFactory, using <code>createPaintForInterior</code>
	 * <li>shadowPaint, from the ViewFactory, using <code>createPaintForShadow</code>
	 * <li>then calls <code>syncArrowAttributes()</code>
	 * </ul>
	 */
	protected void syncAttributes(){
		//if (DEBUG) debug("syncAttributes");
		PicAttributeSet set = element.getAttributeSet();
		outlineStroke = attrFactory.createStroke(set);
		overstrikeStroke = attrFactory.createStrokeForOverstrike(set);
		outlinePaint = attrFactory.createPaintForOutline(set);
		interiorPaint = attrFactory.createPaintForInterior(set);
		shadowPaint = attrFactory.createPaintForShadow(set);
		syncArrowsAttributes();
	}

	/**
	 * Synchronizes arrows' specific attributes with the model. Called from syncAttributes().
	 * This implementation creates arrows from ViewFactory.createArrow(). View that don't support arrows
	 * should override to do nothing. Those that support arrows only when the geometry enables this
	 * should override and call superclass when applicable.
	 */
	protected void syncArrowsAttributes(){
		leftArrow  = attrFactory.createArrow(element.getAttributeSet(),LEFT_ARROW);
		rightArrow = attrFactory.createArrow(element.getAttributeSet(),RIGHT_ARROW);
	}

	/**
	 * Synchronizes the bounding box with the model ;
	 * "bounds" is first computed from the current shape's bound, if any,
	 * then its size gets increased by BARBELL_SIZE, line-thickness, overstrike width,
	 * and possibly shadow and arrows areas.
	 */
	protected void syncBounds(){

		if (shape==null) return;
		bounds = shape.getBounds2D();
		if (shadow !=null)
			bounds.add(shadow.getBounds2D());
		if (leftArrow != null)
			bounds.add(leftArrow.getShape().getBounds2D());
		if (rightArrow != null)
			bounds.add(rightArrow.getShape().getBounds2D());

		double delta = (outlineStroke != null ? outlineStroke.getLineWidth() : 0);
		delta += (overstrikeStroke != null ? overstrikeStroke.getLineWidth() : 0);
		delta += 1;
		bounds.setFrame(
		        bounds.getX()-delta,
		        bounds.getY()-delta,
		        bounds.getWidth()+2*delta,
		        bounds.getHeight()+2*delta);
	}

	/**
	 * Synchronize the "shape" variable with the model
	 */
	protected void syncShape(){
		shape = getElement().createShape();
	}

	/**
	 * Sync the geometry of the current "left/rightArrowShape" variables with the model if applicable,
	 * that is, if these vars are non-null (means: not Arrow.NONE). This method gets called
	 * from changedUpdate() each time a GEOMETRY_CHANGE or ATTRIBUTE_CHANGE event occurs.
	 */
	protected void syncArrowsGeometry(){
		if (leftArrow!=null)
			getElement().syncArrowGeometry(leftArrow, ArrowView.Direction.LEFT);
		if (rightArrow!=null)
			getElement().syncArrowGeometry(rightArrow, ArrowView.Direction.RIGHT);
	}

	/**
	 * sync' the shadow with the current <b>shape</b> (which must therefore be valid)
	 * AND the current PicAttributeSet; hence must be called
	 * either when the geometry's changed, or when attributes have changed.
	 * Factories that do not support shadowing may override to do nothing.
	 */
	protected void syncShadow(){
		shadow = attrFactory.createShadow(element.getAttributeSet(), shape);
	}

	/**
	 * Render the View to the given graphic context.
	 * This implementation relies on a bottom-to-top z-ordering policy, ie
	 * first renders the shadow, then the interior and hatches, finally the outline (=stroke).
	 * @param a the current graphic clip
	 */
	public void paint(Graphics2D g,Rectangle2D a){
		if (shape==null) return;
		// possibly overstrike beforehands
		if (overstrikeStroke != null){ // means OVER_STRIKE = TRUE
			g.setPaint(Color.white); // [pending: check color]
			g.setStroke(overstrikeStroke);
			g.draw(shape);
		}

		g.setStroke(outlineStroke == null 
					? ViewConstants.NO_STROKE_FOR_SHADOW : outlineStroke); // possibly used by shadow
		// paint shadow beforehands
		if (shadow != null){
			g.setPaint(shadowPaint);
			// if shape is not filled, the shadow isn't filled as well ! [pending] check latest PSTricks release
			if (element.getAttribute(FILL_STYLE) == FillStyle.NONE)
				g.draw(shadow);
			else
				g.fill(shadow);
		}

		// paint interior :
		if (interiorPaint != null) {
			g.setPaint(interiorPaint);
			g.fill(shape);
		}
		// paint hatches
		attrFactory.paintHatches(g,element.getAttributeSet(),shape);
		// paint outline :
		if (outlinePaint != null && outlineStroke != null){
			g.setStroke(outlineStroke);
			g.setPaint(outlinePaint);
			g.draw(shape);
		}
		// paint arrows:
		if (leftArrow!=null)
			leftArrow.paint(g);
		if (rightArrow!=null)
			rightArrow.paint(g);
	}

	/**
	 * This implementation returns a HitInfo.Interior if the view is filled and a click occured on the
	 * interior, or a HitInfo.Stroke if a click occured on the stroke path (this use
	 * a FlatteningPathIterator built from the current<code>shape</code>).
	 *
	 * @return a HitInfo corresponding to the given mouse-event
	 */
	public HitInfo hitTest(PEMouseEvent e){

		if (shape == null) return null;
		// first, test if click occured outside the view bounds, which can speed up a lot...
		if (!getBounds().contains(e.getPicPoint())) return null;

		// if it's painted filled, test hit on interior :
		if (interiorPaint != null){
			if (shape.contains(e.getPicPoint())) return new HitInfo.Interior(element,e);
		}
		// otherwise, test hit on stroke, first scaling down CLICK_DISTANCE by the current zoom factor
		// so as to remain coherent with the policy used for end-points's hit test (see AbstractView) :
		double clickDistance = CLICK_DISTANCE / e.getCanvas().getZoomFactor();
		int segmentIndex = PEToolKit.testDistanceToPath(shape, e.getPicPoint(), clickDistance);
		if (segmentIndex >= 0) return new HitInfo.Stroke(element,segmentIndex,e);
		return null;
	}

	/**
	 * If this view intersects the given rectangle, adds the associated Element
	 * to the given list (if non-null) and returns true.
	 */
	protected boolean intersect(Rectangle2D r, ArrayList<Element> list){
		if (shape != null && shape.intersects(r)){
			if (list!=null) list.add(getElement());
			return true;
		}
		else
			return false;
	}


} // LeafElementView

