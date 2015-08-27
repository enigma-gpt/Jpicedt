// BeginPsPictureExpression.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013  Sylvain Reynal
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
// Version: $Id: BeginPsPictureExpression.java,v 1.8 2013/03/31 06:55:59 vincentb1 Exp $
// Keywords: parser
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
