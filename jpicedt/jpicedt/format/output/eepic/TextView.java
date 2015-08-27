// TextView.java --- -*- coding: iso-8859-1 -*-
// February 11, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: TextView.java,v 1.5 2013/03/27 07:10:20 vincentb1 Exp $
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
package jpicedt.format.output.eepic;

import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.view.*;

import java.awt.*;
import java.awt.geom.*;

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicText.*;

import static jpicedt.Log.*;


/**
 * a View for rendering Text's : only rectangular frame boxes (through \\makebox and rel.) are supported,
 * + no filling + only black stroke.
 * Alignment :
 * - no frame : left, right or center ; top, bottom or center
 * - frame : left + baseline. (i.e. \\framebox{text} doesn't allow alignment specification if the
 *           box size is to be computed by LaTeX itself)
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: TextView.java,v 1.5 2013/03/27 07:10:20 vincentb1 Exp $
 */
public class TextView extends jpicedt.graphic.view.TextView {

	// inherited : strx, stry, textLayout, shape (=frame box), tl2ModelTr (affine transform updated from syncShape)
	/**
	 * construct a new View for the given PicRectangle
	 */
	public TextView(PicText te, EepicViewFactory f){
		super(te,f);
		if (DEBUG) debug("<init> (done!)");
	}

	/**
	 * Returns the text rotation in radians : we return 0, since eepic doesn't support rotating text
	 * This is used by syncShape to compute the current tl2ModelTr AffineTransform
	 */
	protected double getRotation(){
		return 0;
	}

	/** update frame shape : all frames are drawn as a rectangle */
	protected void syncFrame(){
		// frame : [SR:pending] oval and circle boxes ! (todo)
		PicText te = (PicText)element;
		if (te.getFrameType()!=FrameStyle.NO_FRAME){
			Rectangle2D tb = textLayout.getBounds();
			shape = tl2ModelTr.createTransformedShape(tb); // at (0,0)
		}
		else shape=null;
	}

	/** update strx */
	protected void syncStringLocationX(){
		if (DEBUG) debug("syncStringLocationX");
		PicText te = (PicText)element;
		double textWidth = textLayout.getBounds().getWidth();
		if (te.getFrameType()!=FrameStyle.NO_FRAME){ // framed -> left alignment
			strx = 0;
		}
		else {
			if (te.getHorAlign() == HorAlign.LEFT) strx = 0;
			else if (te.getHorAlign() == HorAlign.RIGHT) strx =  - textWidth;
			else {//if (te.getHorAlign() == TEXT_HALIGN_CENTER)
				strx =  - textWidth/2;
			}
		}
	}

	/** update stry */
	protected void syncStringLocationY(){
		if (DEBUG) debug("syncStringLocationY");
		PicText te = (PicText)element;
		//double ascent = textLayout.getAscent();
		// e.g. return : y = -7 ; h = 8 (i.e. upside-down)
		double ascent = -textLayout.getBounds().getMinY();
		//double descent = textLayout.getDescent();
		if (te.getFrameType()!=FrameStyle.NO_FRAME){ // framed -> baseline
			stry = 0;
		}
		else {
			if (te.getVertAlign()== VertAlign.TOP) {
				stry = textLayout.getBounds().getMinY();
			}
			else if (te.getVertAlign()== VertAlign.BOTTOM || te.getVertAlign()== VertAlign.BASELINE) {
				stry = 0;
			}
			else { // if (te.getVertAlign()== TEXT_VALIGN_CENTER) {
				Rectangle2D tlb = textLayout.getBounds();
				double d = tlb.getHeight()*0.5 - tlb.getMaxY(); // e.g. 8 * 0.5 - 1 = 3
				stry =  - d;
			}
		}
	}
}
