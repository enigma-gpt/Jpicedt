// PicLineExpression.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PicLineExpression.java,v 1.13 2013/03/31 06:56:19 vincentb1 Exp $
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
package jpicedt.format.input.latex;

import jpicedt.format.input.util.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.PicPoint;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.toolkit.BasicEditPointConstraint.*;

/**
 * <code>PicLineExpression</code> :<ul>
 * <li>  %Line 0 0 (x0,y0)(x1,y1) dash=value
 * <li>  %Line 0 1 (x0,y0)(x1,y1) dash=value
 * <li>  %Line 1 0 (x0,y0)(x1,y1) dash=value
 * <li>  %Line 1 1 (x0,y0)(x1,y1) dash=value (dash is optional)
 * <li>  Any string (\multiput, etc&hellip;)
 * <li>  %End Line
 * </ul>
 * @author Sylvain Reynal
 * @since jpicedt 1.3
 * @version $Id: PicLineExpression.java,v 1.13 2013/03/31 06:56:19 vincentb1 Exp $
 *
 */
public class PicLineExpression extends SequenceExpression   {

	public PicLineExpression(Pool pool){

		super(true); // throw IncompleteSequence Exception
		add(new LaTeXInstanciationExpression("%Line", new PicMultiCurve(new PicPoint(),new PicPoint()),pool));
		add(WHITE_SPACES);
		add(new PicArrowTypeExpression(pool));
		add(new LaTeXPicPointExpression(0,SMOOTHNESS,pool));
		add(WHITE_SPACES);
		add(new LaTeXPicPointExpression(3,SMOOTHNESS,pool));
		add(WHITE_SPACES);
		add(new OptionalExpression(new PicDashStatement(pool)));
		add(new PicEndExpression("%End Line"));
	}

	public String toString(){
		return "[PicLineExpression]";
	}


} // PicLineExpression
