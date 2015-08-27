// CircleHighlighter.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: CircleHighlighter.java,v 1.4 2013/03/27 06:54:46 vincentb1 Exp $
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
import static jpicedt.graphic.model.PicCircleFrom3Points.*;

/**
 * a Highlighter for rendering circles. This comprises the three control-points
 * that make sense with PicCircleFrom3Points.
 * @since jPicEdt 1.4
 * @author Sylvain Reynal
 * @version $Id: CircleHighlighter.java,v 1.4 2013/03/27 06:54:46 vincentb1 Exp $
 */
public class CircleHighlighter extends DefaultHighlighter {

	/**
	 * construct a new Highlighter for the given ellipse
	 */
	public CircleHighlighter(PicCircleFrom3Points circle,DefaultHighlighterFactory f){
		super(circle,f);
	}

	public PicCircleFrom3Points getElement(){
		return (PicCircleFrom3Points)element;
	}


	/**
	 * Returns an iterator over control-points that should be displayed by the hightligher.
	 * This default implementation iterates over all control-points.
	 */
	public PointIndexIterator getControlPointsIterator(){
		// lasily allocate new Iterator :
		if (this.pointIndexIterator==null)
			this.pointIndexIterator = new CircleControlPointsIterator();
		return this.pointIndexIterator;
	}

	/** iterates over a subset of PicCircleFrom3Points control-points so as to reduce the number of visible control-points */
	class CircleControlPointsIterator implements PointIndexIterator {

		protected int counter;
		protected int[] idx = {P_CENTER,P_1,P_2,P_3};

		public CircleControlPointsIterator(){
			counter = 0;
		}

		public boolean hasNext(){
			if (counter >= idx.length) return false;
			return true;
		}

		public int next(){
			int i = idx[counter];
			counter++;
			return i;
		}

		public void reset(){
			counter = 0;
		}
	}
} // CircleHighlighter
