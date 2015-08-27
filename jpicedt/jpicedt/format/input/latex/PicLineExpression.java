// PicLineExpression.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: PicLineExpression.java,v 1.13 2013/03/31 06:56:19 vincentb1 Exp $
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
