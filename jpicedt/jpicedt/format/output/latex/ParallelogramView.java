// ParallelogramView.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: ParallelogramView.java,v 1.5 2013/03/27 07:09:50 vincentb1 Exp $
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

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import jpicedt.JPicEdt;
import jpicedt.format.output.eepic.EepicViewFactory;
import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.*;
import jpicedt.graphic.event.*;

import static jpicedt.format.output.latex.LatexConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;

/**
 * A View for rendering parallelograms.
 * Can be filled only if this is arectangle parallel to the horizontal axis
 */
public class ParallelogramView extends jpicedt.graphic.view.ParallelogramView {

	/**
	 * Construct a new View for the given <code>PicParallelogram</code>.
	 */
	public ParallelogramView(PicParallelogram para,LatexViewFactory f){
		super(para,f);
	}

	/**
	 * Inherits from superclass, except for filling behaviour.
	 */
	protected void syncAttributes(){
		super.syncAttributes();
		PicParallelogram para = (PicParallelogram)getElement();
		// test whether it's a rectangle with sides are parallel to X and Y axes
		if (!para.isXYRectangle()) interiorPaint = null;
	}

} // class
