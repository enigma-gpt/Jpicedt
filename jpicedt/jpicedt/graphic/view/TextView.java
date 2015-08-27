// TextView.java --- -*- coding: iso-8859-1 -*-
// February 9, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: TextView.java,v 1.11 2013/03/27 06:53:41 vincentb1 Exp $
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
import jpicedt.graphic.model.PicText;
import jpicedt.graphic.model.TextEditable;
import jpicedt.graphic.ContentType;
import jpicedt.ui.util.RunExternalCommand;

import java.awt.Shape;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
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

import static jpicedt.Log.*;

/**
 * a View for rendering Text's from TextLayout's
 */
public class TextView extends LeafElementView {

	/** the TextLayout that renders the text string of this TextEditable */
	protected TextLayout textLayout;
	/** TextLayout location with respect to PicText's anchor point */
	protected double strx, stry;
	// shape = frame box !!! (and not textLayout !)
	protected AffineTransform tl2ModelTr=new AffineTransform();; // maps TextLayout coordinates to Model coordinates


	/**
	 * construct a new <code>View</code> for the given <code>PicText</code>
	 */
	public TextView(PicText te, AttributesViewFactory f){
		super(te,f);
		changedUpdate(null);
	}

	public PicText getElement(){
		return (PicText)element;
	}

	/**
	 * Give notification from the model that a change occured to the text this view is responsible
	 * for rendering.<p>
	 */
	public void changedUpdate(DrawingEvent.EventType eventType){
		if (eventType==DrawingEvent.EventType.TEXT_CHANGE)
			super.changedUpdate(null); // update all
		else super.changedUpdate(eventType);
	}

	/**
	 * Returns the text rotation in radians : subclassers that don't support rotating text may return 0 here.
	 */
	protected double getRotation(){
		return Math.toRadians(element.getAttribute(TEXT_ROTATION).doubleValue());
	}

	/**
	 * Synchronize the textLayout and the shape (=frame box, by calling syncFrame) with the model
	 * This delegates to getRotation() where computing rotation angle is concerned, and
	 * updates the AffineTransform returned by getTextToModelTransform().
	 */
	protected void syncShape(){
		PicText te = (PicText)element;
		// update shape
		// Depending on text mode either the text string or the "T" symbol is displayed.
		textLayout = new TextLayout(
			te.getText(te.getTextMode()).length()==0 ? " " : te.getText(te.getTextMode()),
			te.getTextMode()
			? DefaultViewFactory.textFont // [SR: pending] static field so
										  // far, shouldmake a factory method
			: new Font("DejaVu LGC Serif Condensed", Font.BOLD, 6),
			new FontRenderContext(null,false,false));
		syncStringLocationX();
		syncStringLocationY();
		// terrible hack ! textLayout.getBounds2D() gives wrong results if text is to be drawn
		// upside-down, hence we reverse it before computing the bounding rectangle :
		tl2ModelTr.setToIdentity(); // reset
		PicPoint anchor= te.getCtrlPt(TextEditable.P_ANCHOR,null);
		tl2ModelTr.translate(anchor.x, anchor.y);
		tl2ModelTr.rotate(getRotation()); // rotate along P_ANCHOR !
		tl2ModelTr.translate(strx,stry);
		tl2ModelTr.scale(1.0,-1.0);
		syncFrame();
	}

	/**
	 * Synchronizes bounding box with the model ;
	 */
	protected void syncBounds(){
		if (shape!=null) { // if there's a frame around...
			super.syncBounds();
		}
		else {
			Rectangle2D tb = textLayout.getBounds();
			bounds = tl2ModelTr.createTransformedShape(tb).getBounds2D(); // at (0,0)
			double delta = 1;
			bounds.setFrame(
			        bounds.getX()-delta,
			        bounds.getY()-delta,
			        bounds.getWidth()+2*delta,
			        bounds.getHeight()+2*delta);
		}
	}

	/** synchronize frame shape and location */
	protected void syncFrame(){
		PicText te = (PicText)element;
		Rectangle2D tb;
		Ellipse2D.Double frame;
		switch (te.getFrameType()){
		case RECTANGLE:
			tb = textLayout.getBounds();
			shape = tl2ModelTr.createTransformedShape(tb);
			break;
		case OVAL:
			tb = textLayout.getBounds();
			frame = new Ellipse2D.Double();
			frame.setFrame(tb);
			shape = tl2ModelTr.createTransformedShape(frame);
			break;
		case CIRCLE:
			tb = textLayout.getBounds();
			frame = new Ellipse2D.Double();
			frame.setFrameFromCenter(tb.getCenterX(),tb.getCenterY(),tb.getCenterX()+tb.getWidth()*0.5,tb.getCenterY()+tb.getWidth()*0.5);
			shape = tl2ModelTr.createTransformedShape(frame);
			break;
		default:
			shape=null; // TEXT_BOX_NO_FRAME
		}
	}

	/** update strx = x-location of TextLayout's bottom-Left corner with respect to PicText's anchor-point */
	protected void syncStringLocationX(){
		PicText te = (PicText)element;
		double textWidth = textLayout.getBounds().getWidth();
		switch (te.getHorAlign()){
		case LEFT:
			strx = 0;
			break;
		case RIGHT:
			strx = - textWidth;
			break;
		default: // = if (te.getHorAlign() == TEXT_HALIGN_CENTER)
			strx = - textWidth/2;
		}
	}

	/** update strx = y-location of TextLayout's bottom-Left corner with respect to PicText's anchor-point */
	protected void syncStringLocationY(){
		PicText te = (PicText)element;
		//double ascent = textLayout.getAscent();
		// e.g. return : y = -7 ; h = 8 (ie upside-down)
		double ascent = -textLayout.getBounds().getMinY();
		//double descent = textLayout.getDescent();
		Rectangle2D tlb;
		switch (te.getVertAlign()){
		case BASELINE:
			stry = 0;
			break;
		case TOP:
			switch (te.getFrameType()){
			case CIRCLE:
				tlb = textLayout.getBounds();
				stry =  tlb.getCenterY() - tlb.getWidth()*0.5;
				break;
			default:
				stry = textLayout.getBounds().getMinY(); // should be positive (reverse Y-axis !)
			}
			break;
		case BOTTOM:
			switch (te.getFrameType()){
			case CIRCLE:
				tlb = textLayout.getBounds();
				stry =  tlb.getCenterY() + tlb.getWidth()*0.5;
				break;
			default:
				stry = textLayout.getBounds().getMaxY(); // should be negative  (reverse Y-axis !)
			}
			break;
		default:
			// = if (te.getVertAlign()== TEXT_VALIGN_CENTER) {
			tlb = textLayout.getBounds();
			stry = -tlb.getHeight()*0.5 + tlb.getMaxY(); // e.g. 8 * 0.5 - 1 = 3
		}
	}

	/**
	 * Render the View to the given graphic context.
	 * This implementation render the interior first, then the outline.
	 */
	public void paint(Graphics2D g,Rectangle2D a){
		if (!a.intersects(getBounds())) return;
		super.paint(g,a); // possibly paint framebox if non-null

		AffineTransform oldAT = g.getTransform();
		// paint text in black
		g.setPaint(Color.black);
		// from now on, we work in Y-direct (<0) coordinates to avoid inextricable problems with font being mirrored...
		g.transform(tl2ModelTr); // also include rotation
		textLayout.draw(g, 0.0f,0.0f);
		// get back to previous transform
		g.setTransform(oldAT);
		if (DEBUG) {
			g.setPaint(Color.red);
			g.draw(bounds);
		}
	}

	/**
	 * This implementation calls <code>super.hitTest</code> and returns the result if non-null
	 * (this should be a HitInfo.Point),
	 * then returns a HitInfo.Interior if the mouse-click occured inside the text bound (as defined
	 * by text layout)
	 *
	 * @return a HitInfo corresponding to the given mouse-event
	 */
	public HitInfo hitTest(PEMouseEvent e){

		if (!getBounds().contains(e.getPicPoint())) return null;

		PicText te = (PicText)element;
		// recompute textlayout b-box, but store it in a temporary field !
		Rectangle2D tb = textLayout.getBounds();
		Shape text_bounds = tl2ModelTr.createTransformedShape(tb);
		if (text_bounds.contains(e.getPicPoint())) {
			// [SR:pending] for the hitInfo to be reliable, getPicPoint() should first be transformed by
			//              inverse tl2ModelTr ! (especially when rotationAngle != 0)
			TextHitInfo thi = textLayout.hitTestChar((float)(e.getPicPoint().x - strx), (float)(e.getPicPoint().y - stry)); // guaranteed to return a non-null thi
			return new HitInfo.Text((PicText)element, thi, e);
		}
		// test hit on textlayout's bounding rectangle :
		//else if (bounds.contains(e.getPicPoint())) return new HitInfo.Interior(element,e);
		return null;


	}

	/**
	 * [SR:pending] make this view implement aka TextEditableView interface (or something like it), where
	 * TextEditableView is a subinterface of View with text-editing specific capabilities.
	 *
	 * Returns the TextLayout that is responsible for painting the textual content of this element
	 */
	public TextLayout getTextLayout(){
		return textLayout;
	}

	/** Return an affine transform which translat b/w the TextLayout coordinate system and the
	 *  jpicedt.graphic.model coordinate system.
	 * [SR:pending] refactor method name to something more explanatory
	 */
	public AffineTransform getTextToModelTransform(){
		return tl2ModelTr;
	}

	/**
	 * Si l'intersection de cette vue avec le rectangle <code>r</code> est non
	 * vide, alors l'<code>Element</code> associé est ajouté à la liste
	 * <code>liste</code>, si celle-ci est non <code>null</code>.
	 * @param r Le rectangle définissant la zone dont on test l'intersection.
	 * @param list La liste à laquelle ajouter l'élément lorsque
	 * l'intersection est non vide. Ignoré si <code>null</code>.
	 * @since jPicEdt 1.6
	 */
	protected boolean intersect(Rectangle2D r, ArrayList<Element> list){
		boolean intersected = false;
		if(((PicText)element).getTextMode()){
			// Le contenu textuel s'affiche. Dans ce cas on teste l'intersection
			// avec la vue de ce contenu
			if(shape != null)
				intersected = shape.intersects(r);
			else
			{
				Rectangle2D tb = textLayout.getBounds();
				Shape textBounds = tl2ModelTr.createTransformedShape(tb);
				intersected = textBounds.intersects(r);
			}
		}
		else
		{
			// Le symbole T s'affiche. Dans ce cas on ne teste que
			// l'intersection avec le point d'ancrage
			intersected = r.contains(getElement().getCtrlPt(0,null));
		}
		if(intersected){
			if (list!=null) list.add(getElement());
			return true;
		}
		else
			return false;
	}


} // TextView
