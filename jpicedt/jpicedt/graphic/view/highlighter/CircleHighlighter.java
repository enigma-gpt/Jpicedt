// CircleHighlighter.java --- -*- coding: iso-8859-1 -*-
// September 20, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
//
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (�NS�A)
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
// Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilit� au code source et des droits de copie, de modification et de
// redistribution accord�s par cette licence, il n'est offert aux utilisateurs qu'une garantie limit�e.  Pour
// les m�mes raisons, seule une responsabilit� restreinte p�se sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les conc�dants successifs.
//
// � cet �gard l'attention de l'utilisateur est attir�e sur les risques associ�s au chargement, �
// l'utilisation, � la modification et/ou au d�veloppement et � la reproduction du logiciel par l'utilisateur
// �tant donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � manipuler et qui le r�serve
// donc � des d�veloppeurs et des professionnels avertis poss�dant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invit�s � charger et tester l'ad�quation du logiciel � leurs
// besoins dans des conditions permettant d'assurer la s�curit� de leurs syst�mes et ou de leurs donn�es et,
// plus g�n�ralement, � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�.
//
// Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accept� les termes.
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
