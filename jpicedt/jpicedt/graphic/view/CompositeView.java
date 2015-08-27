// CompositeView.java --- -*- coding: iso-8859-1 -*-
// September 24, 2003 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: CompositeView.java,v 1.16 2013/03/27 07:20:36 vincentb1 Exp $
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

import jpicedt.Log;
import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.view.highlighter.CompositeHighlighter;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;

import static jpicedt.Log.debug;
import static jpicedt.graphic.view.highlighter.CompositeHighlighter.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.view.ViewConstants.*;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.toRadians;

/**
 * A <code>CompositeView</code> is a graphic representation of a <code>BranchElement</code>. Since jpicedt
 * 1.5, this can also represent a path à-la-pscustom, that is, built from the children's path (except
 * <code>PicText</code>'s).
 * @author Sylvain Reynal
 * @since jpicedt 1.4
 * @version $Id: CompositeView.java,v 1.16 2013/03/27 07:20:36 vincentb1 Exp $
 */
public class CompositeView extends AbstractView {
	private boolean DEBUG = Log.DEBUG; // true;
	private PicPoint ptUL = new PicPoint(); // buffer
	private PicPoint ptLR = new PicPoint(); // buffer

	protected AttributesViewFactory factory;

	// --- the following are used when drawing pscustom-like groups ---
	private boolean isDisplayAsPath; // if true, displays the associated branchelement as aka pscustom
	/** shape to be painted ; subclass must update it properly */
	private Shape shape;
	/** shadow to be painted if non-null ; updated using shape */
	private Shape shadow;
	/** stroke for outline and arrows (cached) */
	private BasicStroke outlineStroke;
	/** stroke for overstrike (cached) */
	private BasicStroke overstrikeStroke;
	/** paint for interior (cached) */
	private Paint interiorPaint;
	/** paint for outline (cached) */
	private Paint outlinePaint;
	/** paint for shadow (cached) */
	private Paint shadowPaint;
	/** arrows to be painted if non-null */
	private ArrowView leftArrow, rightArrow;

	/**
	 * Construct a new <code>View</code> for the given <code>BranchElement</code>.
	 * @param f not used yet; used only for reflection purpose
	 */
	public CompositeView(BranchElement e, AttributesViewFactory f){
		super(e);
		this.factory = f;
		changedUpdate(null);
	}

	public BranchElement getElement(){
		return (BranchElement)element;
	}

	/**
	 * @return whether this view should display the associated <code>BranchElement</code> as a path (aka
	 * pscustom) or not. Fetches the appropriate information from the <code>BranchElement</code> itself
	 * (actually <code>PicGroup</code> as it is).  Note that any subclass of this view that does not support
	 * such feature should simply override to return false.
	 */
	protected boolean isDisplayAsPath(){
		return (getElement().getCompoundMode() == BranchElement.CompoundMode.JOINT);
	}

	/**
	 * Give notification from the model that a change occured for an element this view is responsible
	 * for rendering. This implementation update the view's bounds from the element's bounding box,
	 * delegating highlighter's bounds to the current <code>Highlighter</code> delegate.
	 */
	public void changedUpdate(DrawingEvent.EventType eventType){
		Rectangle2D oldClip = this.bounds;
		// only if pscustom:
		if (isDisplayAsPath()){
			PicAttributeSet set = element.getAttributeSet();
			// attributes only:
			if (eventType==null || eventType!=DrawingEvent.EventType.GEOMETRY_CHANGE) {
				outlineStroke = factory.createStroke(set);
				overstrikeStroke = factory.createStrokeForOverstrike(set);
				outlinePaint = factory.createPaintForOutline(set);
				interiorPaint = factory.createPaintForInterior(set);
				shadowPaint = factory.createPaintForShadow(set);
				leftArrow = factory.createArrow(element.getAttributeSet(),LEFT_ARROW);
				rightArrow = factory.createArrow(element.getAttributeSet(),RIGHT_ARROW);
			}
			shape = getElement().createShape();
			shadow = factory.createShadow(set, shape);
			//syncArrowsGeometry:
			if (leftArrow!=null)
				getElement().syncArrowGeometry(leftArrow, ArrowView.Direction.LEFT);
			if (rightArrow!=null)
				getElement().syncArrowGeometry(rightArrow, ArrowView.Direction.RIGHT);
		}
		else
		{
			// propage l'événement sur les sous-éléments
			for(Element e : getElement()){
				View v = e.getView();
				if (v!=null)
					v.changedUpdate(eventType);
			}
		}

		// update bounds
		this.bounds = element.getBoundingBox(null); // => null if branchElement is empty !
		if (highlighter!=null) {
			double s = 1.0;
			PECanvas canvas = getContainer();
			if (canvas != null) s=canvas.getScaleFactor();
			highlighter.changedUpdate(eventType,s);
			if (this.bounds!=null) this.bounds.add(highlighter.getBounds());
		}
		// if event == ADD/REMOVE/REPLACE child, don't bother, just repaint it all :
		if (eventType == null || eventType != DrawingEvent.EventType.GEOMETRY_CHANGE)
			repaint(null);
		else {
			if (oldClip != null) {
				if (this.bounds != null)
					oldClip.add(this.bounds);
				repaint(oldClip);
			}
			else {
				if (this.bounds != null)
					repaint(this.bounds);
				// else branchElement was empty, and is still empty : do nothing
			}
		}
	}

	/**
	 * Render the <code>View</code> for the graphic element to the given graphic context. This called "paint"
	 * on each child's view if its bounds rectangle intersects the clip.
	 * @param a the current graphic clip
	 */
	public void paint(Graphics2D g, Rectangle2D a){
		if (getElement().isEmpty()) return; // nothing to be painted then
		if (!a.intersects(bounds)) return;
		if (isDisplayAsPath()){
			if (shape==null) return;
			// possibly overstrike beforehands
			if (overstrikeStroke != null){ // means OVER_STRIKE = TRUE
				g.setPaint(Color.white); // [pending: check color]
				g.setStroke(overstrikeStroke);
				g.draw(shape);
			}

			g.setStroke(outlineStroke); // possibly used by shadow
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
			factory.paintHatches(g,element.getAttributeSet(),shape);
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
		else {
			for(Element e : getElement()){
				View v = e.getView();
				if (v!=null) v.paint(g,a);
			}
		}
	}

	/**
	 * @return a <code>HitInfo</code> corresponding to the given click-point in model-coordinate. The returned
	 * object depends on the highlighting mode of the associated <code>CompositeHighlighter</code>, if any.
	 * In <code>GLOBAL</code> mode, a <code>HitInfo.Composite</code> is returned on the first successfull
	 * hit-test on children, with the corresponding child-index.  In <code>LOCAL</code> mode, a
	 * <code>HitInfo</code> corresponding to all successfull hit-test on children is returned, i.e., possibly
	 * wrapped into a <code>HitInfo.List</code> if more than one <code>Element</code> matches the hit. In the
	 * latter case, a <code>HitInfo.Composite</code> is <code>NEVER</code> returned, except in the case where
	 * the <code>PicGroup</code> contains another <code>PicGroup</code> itself.<br>
	 * If the associated highlighter is not a <code>CompositeHighlighter</code>, the behavior defaults to the
	 * <code>GLOBAL</code> mode.
	 */
	public HitInfo hitTest(PEMouseEvent e){

		// [todo] if (isDisplayAsPath){...}

		// else ...
		// test if we may distinguish b/w LOCAL and GLOBAL mode:
		HighlightingMode hm=null;
		if (getHighlighter() instanceof CompositeHighlighter){
			hm = ((CompositeHighlighter)getHighlighter()).getHighlightingMode();
			if(DEBUG) debug("hm=" + hm.toString());
		}

		PicPoint ptClick = e.getPicPoint();
		HitInfo hi=null;
		HitInfo _hi; // tmp
		// test hit on children's view, highlighted or not, from top to bottom :
		BranchElement be = getElement();
		for(int i = be.size()-1; i>=0; i--){
			Element o = be.get(i);
			View v = o.getView();
			if (v==null)
				continue;
			_hi = v.hitTest(e,false); // view only, no highlighter hit test
			if (_hi != null){
				switch (hm){
				case GLOBAL:
					return new HitInfo.Composite(be, i, e);
				case LOCAL:
					if (hi==null)
						hi = _hi;
					else
						hi=hi.append(_hi); // now hi is a HitInfo.List containing Composite's
					break;
				default: // hm=null
					return new HitInfo.Composite(be, i, e);
				}
			}
		}
		// special further processing if hi is still null and PSCustom is active
		// in practice, this means: either a successful hit on a segment that joins two children
		// or on a filled area that sits in the interior of the whole path but NOT of a given child...
		// ok, let's proceed: if it's painted filled, test hit on interior :
		if (hi == null && isDisplayAsPath() && shape != null){
			if (interiorPaint != null && shape.contains(e.getPicPoint()))
				hi = new HitInfo.Interior(be,e);
			else {
				// otherwise, test hit on stroke, first scaling down CLICK_DISTANCE by the current zoom factor
				// so as to remain coherent with the policy used for end-points's hit test (see AbstractView) :
				double click_distance = CLICK_DISTANCE / e.getCanvas().getZoomFactor();
				int segmentIndex = PEToolKit.testDistanceToPath(shape, e.getPicPoint(), click_distance);
				if (segmentIndex >= 0)
					hi = new HitInfo.Stroke(be,segmentIndex,e);
			}
		}

		return hi;
	}

	/**
	 * If this view intersects the given rectangle, return the associated <code>BranchElement</code> in
	 * <code>GLOBAL</code> mode, or a child <code>Element</code> if in <code>LOCAL</code> mode. Otherwise
	 * return null.
	 */
	protected boolean intersect(Rectangle2D r, ArrayList<Element> list){
		// test if we may distinguish b/w LOCAL and GLOBAL mode:
		HighlightingMode hm=null;
		if (getHighlighter() instanceof CompositeHighlighter){
			hm = ((CompositeHighlighter)getHighlighter()).getHighlightingMode();
		}

		switch (hm){
			case LOCAL:
				boolean ok = false;
				for (Element child: getElement()){
					View v = child.getView();
					if (v==null) continue;
					ok |= v.intersect(r,false,list); // only view, no highlighter, list modified
				}
				return ok;
			default:
			case GLOBAL: // stop on first successfull hit
				for (Element child: getElement()){
					View v = child.getView();
					if (v==null) continue;
					if (v.intersect(r,false,null)){ // only view, no highlighter, list not modified
						if (list!=null) list.add(getElement());
						return true;
					}
				}
				return false;
		}
	}

} // CompositeView
