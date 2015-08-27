// EllipseView.java --- -*- coding: iso-8859-1 -*-
// February 11, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: EllipseView.java,v 1.4 2013/03/27 07:10:35 vincentb1 Exp $
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

/**
 * view for ellipses specific to the Epic/Eepic content type.
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: EllipseView.java,v 1.4 2013/03/27 07:10:35 vincentb1 Exp $
 */
public class EllipseView extends jpicedt.graphic.view.EllipseView {

	/**
	 * Construct a View for the given PicEllipse.<p>
	 * Following rules apply for the Epic/Eepic content type :
	 * <ul>
	 * <li> Arcs (circular or elliptic) and ellipse can be rendered -> inherited from DefaultViewFactory.
	 *      [pending] bug : only OPEN arc ! No PIE !
	 * <li> Ellipse and circulars arcs can be filled only if surrounding parallelogram is a rectangle parallel to X and Y axes
	 * <li> Elliptic arcs can't be filled anyway.
	 * <li> Supported fill-colours are BLACK, GRAY and WHITE.
	 * <li> Only solid stroke is supported [pending] implementation in formatter
	 * </ul>
	 */
	public EllipseView(PicEllipse ellipse, EepicViewFactory f){
		super(ellipse,f);
	}

	/**
	 * Give notification from the model that a change occured for an element this view is responsible
	 * for rendering.<p>
	 */
	public void changedUpdate(DrawingEvent.EventType eventType){
		//if geometry changed from ellipse to circle or vice-versa, we need to *force*
		//update of interior paint attribute ; otherwise, if attribute changed,
		//no need to worry, for this is done automatically by superclass :
		if (eventType!=DrawingEvent.EventType.ATTRIBUTE_CHANGE) syncAttributes();
		super.changedUpdate(eventType); // super calls syncAttribute if ATTRIBUTE_CHANGE, it's ok.
	}

	/**
	 * Inherits from superclass, except for elliptic arcs, which are never filled, as well
	 * as rotated ellipses. Conversely, circles, circular arcs and non-rotated ellipses are always filled.
	 */
	protected void syncAttributes(){
		super.syncAttributes(); // that's ok for ellipses and circular arcs
		outlineStroke = new BasicStroke(element.getAttribute(LINE_WIDTH).floatValue()); // dont' dash or dot
		PicEllipse ellipse = (PicEllipse)element;
		// filled : isCircular() || (isPlain() && non-rotated)
		if (!ellipse.isCircular() && (ellipse.getRotationAngle()!=0 || !ellipse.isPlain())) { // elliptic arcs or rotated ellipses
			interiorPaint = null; // forbides filling
		}
		//never paint arrows :
		//arrowStart = arrowEnd = null; // now rendered by formatter
	}
}
