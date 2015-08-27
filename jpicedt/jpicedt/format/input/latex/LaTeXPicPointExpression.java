// LaTeXPicPointExpression.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013  Sylvain Reynal
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
// Version: $Id: LaTeXPicPointExpression.java,v 1.10 2013/04/14 19:43:18 vincentb1 Exp $
// Keywords: parser
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
package jpicedt.format.input.latex;

import jpicedt.format.input.util.*;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.model.EditPointConstraint;

import static jpicedt.format.input.util.ExpressionConstants.*;


/**
 * an expression that parses "(x,y)" string (LaTeX-like PicPoint)
 * and gives the corresponding numerical values to the point number "ptNumber" of the current Element
 * using its <code>setCtrlPt()</code>, using the given constraint.
 * @see jpicedt.graphic.model.Element#setCtrlPt
 * @author Sylvain Reynal
 * @since jpicedt 1.3
 * @version $Id: LaTeXPicPointExpression.java,v 1.10 2013/04/14 19:43:18 vincentb1 Exp $
 *
 */
public class LaTeXPicPointExpression extends PicPointExpression  {

	private int ptNumber;
	private EditPointConstraint constraint;
	private Pool pool;

	public LaTeXPicPointExpression(int ptNumber,Pool pl){
		this(ptNumber, null, pl);
	}

	public LaTeXPicPointExpression(int ptNumber,EditPointConstraint constraint, Pool pl){
		super("(",",",")");
		this.ptNumber=ptNumber;
		this.pool = pl;
		this.constraint=constraint;
	}

	public void action(ParserEvent e){
		if (DEBUG) System.out.println(e);
		PicPoint pt = getPicPoint();
		double unitLength = pool.get(LaTeXParser.KEY_UNIT_LENGTH);
		pool.currentObj.setCtrlPt(ptNumber,pt.toMm(unitLength),constraint);
	}
}
