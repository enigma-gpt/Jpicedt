// DefaultPointIndexIterator.java --- -*- coding: iso-8859-1 -*-
// September 12, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: DefaultPointIndexIterator.java,v 1.8 2013/03/27 07:02:54 vincentb1 Exp $
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
package jpicedt.graphic.model;

/**
 * Default implementation of the PointIndexIterator interface, which simply iterates in ascending order over all control points.
 */
public class DefaultPointIndexIterator implements PointIndexIterator {

	protected int counter;
	protected Element elem;

	/**
	 * create a new PointIndexIterator for the given Element
	 */
	public DefaultPointIndexIterator(Element e){
		elem=e;
		counter = elem.getFirstPointIndex();
	}

	/**
	 * Returns true if the iteration has more elements
	 */
	public boolean hasNext(){
		if (counter > elem.getLastPointIndex()) return false;
		return true;
	}

	/**
	 * Returns the index of the next PicPoint in the iteration.
	 */
	public int next(){
		if (!hasNext()) throw new java.util.NoSuchElementException(Integer.toString(counter));
		counter++;
		return counter-1;
	}

	public void reset(){
		counter = elem.getFirstPointIndex();
	}
}
