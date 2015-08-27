// SmoothPolygonHighlighter.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: SmoothPolygonHighlighter.java,v 1.4 2013/03/27 06:54:06 vincentb1 Exp $
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
 * a Highlighter for SmoothPolygon. So far, it is based on AbstractCurveHighlighter, except where computing
 * tangents is concerned, since for SmoothPolygon's, tangents are specified by polygon-point's rather than
 * Bezier-points.
 * @since jPicEdt 1.4
 * @author Sylvain Reynal
 * @version $Id: SmoothPolygonHighlighter.java,v 1.4 2013/03/27 06:54:06 vincentb1 Exp $
 */
public class SmoothPolygonHighlighter extends DefaultHighlighter {

	// shapes[0]=GeneralPath (polygon) + closing tangent if applicable

	/**
	 * construct a new Highlighter for the given smooth-polygon
	 */
	public SmoothPolygonHighlighter(PicSmoothPolygon curve, DefaultHighlighterFactory f){
		super(curve,f);
	}

	public PicSmoothPolygon getElement(){
		return (PicSmoothPolygon)element;
	}

	/**
	 * Synchronizes the highlighter's tangents with the model. This method is called from inside
	 * <code>syncShape()</code> only, and is overriden so as to compute tangents based on polygon's points
	 * rather than Bezier-points. Also compute the ``envelope'' polygon which is used for adding a new point
	 * (hitTest method).
	 */
	protected void syncShape(double scale){
		PicSmoothPolygon poly = getElement();

		if (shape==null) shape=new GeneralPath();
		GeneralPath envelope = (GeneralPath)shape;
		envelope.reset();
		PicPoint pt = poly.getCtrlPt(poly.getFirstPointIndex(),null);
		envelope.moveTo((float)pt.x, (float)pt.y);

		for (int i=poly.getFirstPointIndex(); i<poly.getLastPointIndex();i=i+1){
			pt = poly.getCtrlPt(i+1,pt);
			envelope.lineTo((float)pt.x,(float)pt.y);
		}
		if (poly.isClosed()){
			envelope.closePath();
		}
	}
} // SmoothPolygonHighlighter
