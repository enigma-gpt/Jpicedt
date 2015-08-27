// AbstractMouseTransform.java --- -*- coding: iso-8859-1 -*-
// February 23, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: AbstractMouseTransform.java,v 1.22 2013/03/27 07:00:23 vincentb1 Exp $
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

import jpicedt.graphic.PageFormat;
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.BranchElement;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.model.PicAttributeName;
import jpicedt.graphic.model.StyleConstants;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.view.View;
import jpicedt.graphic.view.ArrowView;
import jpicedt.graphic.view.AbstractView;
import jpicedt.graphic.view.DefaultViewFactory;
import jpicedt.graphic.view.HitInfo;

import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Paint;
import static jpicedt.Log.*;

/**
 * This implementation allows subclassers to modify a clone
 * of the target Element, then to update the original Element when the operation is completed.
 * The aim is to reduce the burden of repainting a (possibly) complex Element while transforming it,
 * by painting the Element using very lightweight View. This is done as follow :
 * <ul>
 * <li> first clone the given Element, and add it to a transient RootElement defined as an inner class ;</li>
 * <li> then create a "lightweight" View for this Element using a dedicated ViewFactory defined as an inner class ;
 * <li> apply the transformation to the clone while the mouse is being dragged/moved/pressed ... Note that the
 *      implementation of MouseTransform.paint() arranges for properly painting the clone by asking its
 *      lightweight View.</li>
 * <li> when the transformation has completed, update the original Element and delete the clone.</li>
 * </ul>

 * @author Sylvain Reynal
 * @since jPicEdt 1.3.2
 */
public abstract class AbstractMouseTransform implements MouseTransform {

	private Element target,clone;
	private PECanvas canvas;
	private double scale=1.0; // hold the current scale factor b/w model and screen coordinate ; used by paint()

	/**
 	 * @param target The target-element upon which this transform will act.
	 */
	protected AbstractMouseTransform(Element target){
		this.target = target;
	}

	/**
	 * Called by mousePressed.<br>
	 * Creates a clone of the target (e.g. a clicked Element) given as a parameter to the constructor,
	 * and sets its View using a dedicated ViewFactory (defined as an inner class) so that only the stroke
	 * of the element will be painted (using a default basic stroke to reduce the burden of repainting).
	 */
	public void start(PEMouseEvent e){
		canvas = e.getCanvas();
		PageFormat pf = e.getCanvas().getPageFormat();
		AffineTransform at = pf.getModel2ViewTransform(e.getCanvas().getZoomFactor());
		scale = at.getScaleX();
		if (clone == null){
			clone = (Element)target.clone(); // this will clone the View as well, but we want our own View
			//e.getCanvas().getDrawing().addElement(clone); // for debugging purpose
			clone.setParent(new RootElement(clone));
			clone.setViewFromFactory(new ViewFactory(scale)); // set our own view
		}
	}

	/**
	 * Called by mouseReleased.<br>
	 * This implementation set the clone's parent and view to null so as to make it elligible for gc.
	 * This should be called by subclassers at the end of the implementation of their "next" method.
	 */
	public boolean next(PEMouseEvent e){
		clone.setParent(null); // make clone
		clone.removeView();    // eligible for GC
		clone = null;
		return false; // this is the end !
	}

	/**
	 * Paints the clone. This is normally called by EditorKit upon request by the installed PECanvas.
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		if (clone==null) return;
		if (DEBUG) debug("paint");
		View v = clone.getView();
		if (v==null) return;
		v.paint(g, allocation);
		// [reynal:21/01/2003] ok, let's try to paint the highlighter as well :
		g.setPaint(Color.blue);
		v.paintHighlighter(g, allocation, scale);
	}

	/**
	 * Return the element this transform acts upon
	 */
	protected Element getTarget(){
		return target;
	}

	/**
	 * Return the clone of the target element, which was init'd by <code>startTransform</code>
	 */
	protected Element getClone(){
		return clone;
	}

	///////////////////////////////////////////////////////////////////////

	/**
	 * a ViewFactory that produces view suited for MouseTransform's, ie with a minimal set of attributes
	 * so as to reduce processor burden during the transform operation (e.g. moving, resizing,...).<br>
	 * Note that adding new "shared" methods in  DefaultViewFactory (e.g. createGradient, like) IMPOSES
	 * us to override them here IF we want the corresponding feature not to be painted during
	 * the MouseTransform. This might be fairly cumbersome in the future, yet i don't see any other solution.
	 */
	private class ViewFactory extends DefaultViewFactory {

		double scale;

		/**
		 * @param scale the current scaling factor b/w model and screen coordinate system ; this
		 *        usually depends on the current zoom factor in PECanvas, and may be retrieved using
		 *        the PageFormat associated with the canvas. Scale is needed here to construct a proper
		 *        BasicStroke object.
		 */
		ViewFactory(double scale){
			this.scale = scale;
		}

		/**
		 * return a basic stroke instead of the (possibly) pretty complex stroke created by DefaultViewFactory.
		 */
		public BasicStroke createStroke(PicAttributeSet set){
			return new BasicStroke((float)(0.5/scale));
		}

		public BasicStroke createStrokeForOverstrike(PicAttributeSet set){
			return null;

		}


		/**
		 * return the color "blue"
		 */
		public Paint createPaintForOutline(PicAttributeSet set){
			return Color.blue;
		}

		/**
		 * return null : no interior is painted during a mouse transform.
		 */
		public Paint createPaintForInterior(PicAttributeSet set){
			return null;
		}

		/**
		 * return null : no shadow must be painted.
		 */
		public Paint createPaintForShadow(PicAttributeSet set){
			return null;
		}

		/**
		 * would normally paint hatches (vlines,...) ; overriden to do nothing.
	 	 */
		public void paintHatches(Graphics2D g, PicAttributeSet set, Shape shape){
		}

		public Shape createShadow(PicAttributeSet set, Shape shape){
			return null;
		}

		/**
		 * Returns null (no arrow is rendered during a mouse transform operation)
		 */
		public ArrowView createArrow(PicAttributeSet set, PicAttributeName<StyleConstants.ArrowStyle> direction){
			return null;
		}

	}

	//////////////////////////////////////////////////////////
	/** RootElement hosts the target-Element being mouse-transformed during the operation */
	private class RootElement extends BranchElement {
		View v;
		/** directly add the given "clone" to the branch at init time (this is much simpler than using addChild) */
		public RootElement(Element clone){
			super();
			v = new RootView(this);
		}

		public RootElement clone(){
			return null; // not used here
		}
		public View getView(){
			if (DEBUG) debug("getView="+v);
			return v;
		}
		public String getName(){
			return "RootElement";
		}
	}

	///////////////////////////////////////////////////////////
	/** view for the RootElement only */
	private class RootView extends AbstractView {

		public RootView(RootElement e){
			super(e);
		}

		public void changedUpdate(DrawingEvent.EventType e){
		}

		/** return PECanvas : this is mandatory, as there's no other way for a View to fetch its container
		 *  than calling the root of the view-tree ! */
		public PECanvas getContainer() {
			if (DEBUG) debug("getContainer="+canvas);
			return  canvas;
		}
		/** does nothing */
		public HitInfo hitTest(PEMouseEvent e){return null;}
		/** does nothing */
		public void paint(Graphics2D g, Rectangle2D r){}

		public boolean intersect(Rectangle2D r, java.util.ArrayList<Element> list){
			return false;
		}
	}

} // MouseTransform
