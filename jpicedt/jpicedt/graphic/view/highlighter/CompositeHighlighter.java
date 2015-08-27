// CompositeHighlighter.java --- -*- coding: iso-8859-1 -*-
// September 20, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999-2007 Sylvain Reynal
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
// Version: $Id: CompositeHighlighter.java,v 1.8 2013/03/27 06:54:41 vincentb1 Exp $
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
import jpicedt.graphic.view.View;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;

import static jpicedt.graphic.view.ViewConstants.*;


/**
 * A <code>Highlighter</code> for a <code>BranchElement</code>.
 * @since jPicEdt 1.4
 * @author Sylvain Reynal
 * @version $Id: CompositeHighlighter.java,v 1.8 2013/03/27 06:54:41 vincentb1 Exp $
 */
public class CompositeHighlighter extends DefaultHighlighter {

	/**
	 * Local highlighting mode corresponding to each <code>Element</code> in the parent
	 * <code>BranchElement</code> being highlighted Global highlighting mode corresponding to the 8 outer
	 * control-points being highlighted
	 */
	public static enum HighlightingMode {LOCAL,GLOBAL};

	/** highlight mode (default to <code>GLOBAL</code>) */
	protected HighlightingMode highlightingMode=HighlightingMode.GLOBAL;

	/**
	 * construct a new <code>Highlighter</code> for the given <code>BranchElement</code>.
	 */
	public CompositeHighlighter(BranchElement e, DefaultHighlighterFactory f){
		super(e,f);
	}

	public BranchElement getElement(){
		return (BranchElement)element;
	}


	/**
	 * set the current highlighting mode ; this influences the way the <code>Highligther</code> is painted,
	 * but also the result returned by <code>HitTest</code>.
	 * @param mode either <code>LOCAL_MODE</code> or <code>GLOBAL_MODE</code>
	 */
	public void setHighlightingMode(HighlightingMode mode){
		if (highlightingMode == mode) return;
		highlightingMode = mode;
		View v = element.getView();
		if (v!=null)
			v.changedUpdate(null);
	}

	/**
	 * Return the current highlighting mode.
	 */
	public HighlightingMode getHighlightingMode(){
		return highlightingMode;
	}

	/**
	 * Toggle the current highlighting mode.
	 */
	public void toggleHighlightingMode(){
		switch (highlightingMode){
		case LOCAL:
			setHighlightingMode(HighlightingMode.GLOBAL);
			break;
		default:
			setHighlightingMode(HighlightingMode.LOCAL);
		}
	}

	/**
	 * Synchronize the array of "shapes" needed to paint this highlighter, with the model ;
	 */
	protected void syncShape(double scale){
		if (shape==null) shape = element.getBoundingBox(null);
		else shape = element.getBoundingBox((Rectangle2D)shape);
	}

	/**
	 * Render the Highlighter to the given graphic context.<br>
	 * In <code>GLOBAL</code> mode, only the 8 control-points are rendered, whereas
	 * in <code>LOCAL</code> mode, only children's highlighters are rendered.
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		if (!allocation.intersects(getBounds())) return;
		switch (highlightingMode){
		case GLOBAL: // paint only group's control-points + highlighter's stroke
			g.setPaint(DefaultHighlighterFactory.GLOBAL_HIGHLIGHTING_COLOR);
			if (!getElement().isEmpty())
				super.paint(g,allocation,scale);
			break;
		case LOCAL: // paint only children's highlighter.
			for (Element e: getElement()){ // foreach child of the associated BranchElement
				View v = e.getView();
				g.setPaint(DefaultHighlighterFactory.LOCAL_HIGHLIGHTING_COLOR);
				if (v!=null)
					v.paintHighlighter(g, allocation, scale);
			}
			break;
		}
	}

	/**
	 * Returns a <code>HitInfo</code> corresponding to the given mouse-event. In <code>GLOBAL</code> mode, hit
	 * test occurs on the 8 control-points and the highlighter's stroke, while in <code>LOCAL</code> mode, it
	 * occurs on the children's view <em>and</em> highlighter. Note that in the latter case, the returned
	 * <code>HitInfo</code> is the same as the one given by invoking <code>hitTest()</code> directly on the
	 * corresponding child (i.e., no <code>HitInfo.Composite</code> is returned whatsoever).
	 */
	public HitInfo hitTest(PEMouseEvent e){

		BranchElement be = getElement();
		switch (highlightingMode){
		case GLOBAL:
			if (be.isEmpty())
				return null;
			return super.hitTest(e);// test hit on group's end-points + highlighter shape: HitInfo.Point of HighlighterStroke
		case LOCAL: // test hit on children, highlighted, from top to bottom :
			HitInfo hi = null;
			for(int i = be.size()-1; i>=0; i--){
				Element o = be.get(i);
				View v = o.getView();
				if (v==null) continue;
				HitInfo hit = v.hitTest(e,true); // include clicks on child view as well (i.e., not only their highlighted part)
				if (hit != null) {
					if (hi==null)
						hi = hit;
					else
						hi = hi.append(hit);
				}
			}
			return hi;
		default:
			return null;
		}
	}

	/**
	 * If this highligher intersects the given rectangle, add the associated <code>BranchElement</code> (or a
	 * child <code>Element</code> thereof if it's more appropriate) and returns true.
	 */
	public boolean intersect(Rectangle2D r, ArrayList<Element> list){
		switch (highlightingMode){
		case GLOBAL:
			return super.intersect(r,list); // control-point + highlighter stroke
		case LOCAL: // compute intersection with children
			boolean ok = false;
			for (Element child: getElement()){
				View v = child.getView();
				if (v==null) continue;
				ok |= v.intersect(r,true,list); // include clicks on child view as well (i.e., not only their highlighted part)
			}
			return ok;
		}
		return false;
	}

} // CompositeHighlighter
