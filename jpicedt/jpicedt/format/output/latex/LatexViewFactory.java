// LatexViewFactory.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: LatexViewFactory.java,v 1.19 2013/03/27 07:09:55 vincentb1 Exp $
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
package jpicedt.format.output.latex;

import jpicedt.format.output.eepic.EepicViewFactory;
import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.*;
import jpicedt.graphic.event.*;
import jpicedt.graphic.view.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import static jpicedt.format.output.latex.LatexConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;

import static jpicedt.Log.*;
/**
 * Creates a <code>View</code> for a given <code>Element</code> when the underlying content type is the
 * LaTeX-picture environment.
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: LatexViewFactory.java,v 1.19 2013/03/27 07:09:55 vincentb1 Exp $
 * <p>
 *
 */
public class LatexViewFactory extends EepicViewFactory  {

	/** color for outlines */
	protected static final Color LATEX_STROKE = Color.black;
	/** color for interior */
	protected static final Color LATEX_FILL = Color.black;



	/**
	 * Configure static fields using the given <code>Properties</code> object.
	 * @param preferences used to read shared parameters
	 *        If null, default values are used.
	 */
	public static void configure(Properties preferences){
		EllipseView.maxLatexDiskDiameter = Double.parseDouble(preferences.getProperty(KEY_MAX_DISK_DIAMETER,PEToolKit.doubleToString(DEFAULT_MAX_DISK_DIAMETER)));
	}

	public LatexViewFactory(){
		super();
		if (DEBUG) debug("Initializing");
		map(AbstractCurve.class, AbstractCurveView.class); // also PicCircleFrom3Points
		map(PicEllipse.class, EllipseView.class);
		map(PicParallelogram.class, ParallelogramView.class);
		if (DEBUG) debug(toString());
	}

	/**
	 * @return a <code>View</code> for the given <code>Element</code>.
	 */
	public View createView(Element element){
		if (DEBUG) debug("Creating view for "+element);
		return super.createView(element);
		/*
		// curve : not filled at all, whether it is a polygon or not...
		if (element instanceof AbstractCurve) return new LatexViewFactory.AbstractCurveView((AbstractCurve)element);
		// ellipses : only small circles can be filled
		else if (element instanceof PicEllipse) return new LatexViewFactory.EllipseView((PicEllipse)element);
		// parallelo : only rectangle parallel to X and Y axes can be filled
		else if (element instanceof PicParallelogram) return new LatexViewFactory.ParallelogramView((PicParallelogram)element);
		return super.createView(element); // delegates to EepicViewFactory
		*/
	}

	/**
	 * Returns <code>LATEX_FILL</code>, ie black filling only is allowed.
	 * @return a Paint from the given attributes, suited for painting "interior"
	 */
	public Paint createPaintForInterior(PicAttributeSet set){
		if (set.getAttribute(FILL_STYLE)!=FillStyle.NONE) return LATEX_FILL;
		else return null;

	}

	// stroke : same as eepic (no dot)

	// hatches : same as eepic (no hatch)





	/////////////////////////////////////////////////////////////////////////////



}
