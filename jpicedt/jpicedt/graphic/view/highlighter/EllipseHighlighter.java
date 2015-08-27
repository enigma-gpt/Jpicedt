// EllipseHighlighter.java --- -*- coding: iso-8859-1 -*-
// September 20, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: EllipseHighlighter.java,v 1.4 2013/03/27 06:54:26 vincentb1 Exp $
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
// À'utilisation, à la modification et/ou au développement et à la reproduction du logiciel par l'utilisateur
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
package jpicedt.graphic.view.highlighter;

import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;

import static jpicedt.graphic.view.ViewConstants.*;


/**
 * a Highlighter for PicEllipse's. This comprises control-points and the
 * surrounding parallelogram.
 * @since jPicEdt 1.4
 * @author Sylvain Reynal
 * @version $Id: EllipseHighlighter.java,v 1.4 2013/03/27 06:54:26 vincentb1 Exp $
 */
public class EllipseHighlighter extends DefaultHighlighter {

	/**
	 * construct a new Highlighter for the given ellipse
	 */
	public EllipseHighlighter(PicEllipse ellipse,DefaultHighlighterFactory f){
		super(ellipse,f);
	}

	public PicEllipse getElement(){
		return (PicEllipse)element;
	}

	/**
	 * Synchronize the "shape" variable with the model.
	 */
	protected void syncShape(double scale){

		if (shape==null) shape = new GeneralPath();
		GeneralPath parallelo = (GeneralPath)shape;
		PicPoint pt = new PicPoint();
		parallelo.reset();
		pt = element.getCtrlPt(0,pt);
		parallelo.moveTo((float)pt.x, (float)pt.y); // set first point
		pt = element.getCtrlPt(1,pt);
		parallelo.lineTo((float)pt.x, (float)pt.y);
		pt = element.getCtrlPt(2,pt);
		parallelo.lineTo((float)pt.x, (float)pt.y);
		pt = element.getCtrlPt(3,pt);
		parallelo.lineTo((float)pt.x, (float)pt.y);
		parallelo.closePath();

	}

	/**
	 * Render the Highlighter to the given graphic context.<br>
	 * Current implementation paints end-points using the highlighter color if the given
	 * allocation intersects the bounds of this view, then paint the surrounding parallelogram.
	 * @param scale The current scale factor from model to screen for the Graphics2D context ;
	 *        this may be used to scale down line thickess, etc... so that lines/rectangle/... appear with the
	 *        same length on the screen whatever the scale factor that's set to the graphic context.
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		super.paint(g, allocation, scale);
		// paint SIDE_R control-point to help user know where angle starts
		double barbellSize = BARBELL_SIZE/scale/2.0;
		PicPoint pt = element.getCtrlPt(PicEllipse.SIDE_R,null);
		rectBuffer.setRect(pt.x-barbellSize,pt.y-barbellSize,2*barbellSize,2*barbellSize);
		g.setPaint(Color.black);
		g.fill(rectBuffer);
	}


} // EllipseHighlighter
