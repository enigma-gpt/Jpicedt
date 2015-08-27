// AbstractCurveView.java --- -*- coding: iso-8859-1 -*-
// February 9, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: AbstractCurveView.java,v 1.3 2013/03/27 07:10:15 vincentb1 Exp $
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

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import static jpicedt.format.output.latex.LatexConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;

/**
 * a View for rendering any concrete implementation of jpicedt.graphic.model.AbstractCurve
 * LaTeX-picture environment does not allow filling such shapes.
 */
public class AbstractCurveView extends jpicedt.graphic.view.AbstractCurveView {

	/**
	 * construct a new View for the given curve
	 */
	public AbstractCurveView(AbstractCurve curve, LatexViewFactory f){
		super(curve,f);
	}

	/**
	 * Inherits from superclass, except for filling behaviour
	 */
	protected void syncAttributes(){
		super.syncAttributes();
		interiorPaint = null;
	}
	/**
	 * Computes the Shape (centered at 0,0) used for drawing polygon's dots, built from the given attributes ;
	 * this implementation sets it to null, as dotting is not supported.
	 */
	protected void syncDotShape(){
		dotShape=null;
	}

}
