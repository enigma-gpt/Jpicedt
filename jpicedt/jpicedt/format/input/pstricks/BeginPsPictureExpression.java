// BeginPsPictureExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: BeginPsPictureExpression.java,v 1.8 2013/03/31 06:55:59 vincentb1 Exp $
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
package jpicedt.format.input.pstricks;

import jpicedt.format.input.util.*;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * \begin{pspicture}(x0,y0)(x1,y1) -> defines a box with BL=(x0,y0) and TR=(x1,y1)<br>
 * \begin{pspicture}*(x0,y0)(x1,y1) -> clipped<br>
 * \begin{pspicture}[baseline](x0,y0)(x1,y1) -> changes baseline (see pstricks's doc p.41)<br>
 * \begin{pspicture}[](x0,y0)(x1,y1) -> baseline passes across the origine<br>
 * And the same commands with \pspicture (that is, TeX-like).<br>
 * @author Sylvain Reynal
 * @version $Id: BeginPsPictureExpression.java,v 1.8 2013/03/31 06:55:59 vincentb1 Exp $
 */
public class BeginPsPictureExpression extends SequenceExpression  {

	public BeginPsPictureExpression(){

		super(true); // throw IncompleteSequence Exception
		add(new AlternateExpression(
			new LiteralExpression("\\pspicture"),
			new LiteralExpression("\\begin{pspicture}")));
		add(new OptionalExpression(new LiteralExpression("*"))); // clipped ?
		add(WHITE_SPACES_OR_EOL);
		add(new OptionalExpression(new AlternateExpression(
		                               new LiteralExpression("[]"), // "[]" (default baseline)
		                               new SequenceExpression(
		                                   new LiteralExpression("["), // "[baseline]"
		                                   new NumericalExpression(DOUBLE, POSITIVE, "]", true),true),
										WHITE_SPACES_OR_EOL)));
		add(new PicPointExpression("(", ",", ")")); // BL corner
		add(WHITE_SPACES_OR_EOL);
		add(new PicPointExpression("(", ",", ")")); // TR corner
	}

	public String toString(){
		return "[BeginPsPictureExpression]";
	}
}
