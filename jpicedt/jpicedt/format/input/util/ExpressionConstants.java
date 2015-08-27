// ExpressionConstants.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: ExpressionConstants.java,v 1.11 2013/03/31 06:59:44 vincentb1 Exp $
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
package jpicedt.format.input.util;

/**
 * Constants used by subclasses of AbstractRegularExpression.
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: ExpressionConstants.java,v 1.11 2013/03/31 06:59:44 vincentb1 Exp $
 *
 */
public class ExpressionConstants {

	/** debug flag ; turn on for verbose parsing */
	public static final boolean DEBUG=false;

	/** type for NumericalExpression */
	public static final int INTEGER=0;
	/** type for NumericalExpression */
	public static final int DOUBLE=1;

	/** sign for NumericalExpression */
	public static final int ANY_SIGN=0;
	/** sign for NumericalExpression */
	public static final int POSITIVE=1;
	/** sign for NumericalExpression */
	public static final int NEGATIVE=2;
	/** sign for NumericalExpression */
	public static final int STRICTLY_POSITIVE=3;
	/** sign for NumericalExpression */
	public static final int STRICTLY_NEGATIVE=4;
	/** sign for NumericalExpression */
	public static final int NON_NULL=5;

	/** RepeatExpression mode */
	public static final int EXACTLY=0;
	/** RepeatExpression mode */
	public static final int AT_LEAST=1;
	/** RepeatExpression mode */
	public static final int AT_MOST=2;

	/** constant for WildCharExpression */
	public static final int ANY_CHAR=0; // excluding EOL
	/** constant for WildCharExpression */
	public static final int ANY_CHAR_EOL=-2; // including EOL
	/** constant for WildCharExpression */
	public static final int EOL=-1;

	/** placeholder for WildSpaces expression (which is used pretty often) */
	public static final WhiteSpaces WHITE_SPACES = new WhiteSpaces();
	/** placeholder for WildSpacesOrEOL expression (which is used pretty often) */
	public static final WhiteSpacesOrEOL WHITE_SPACES_OR_EOL = new WhiteSpacesOrEOL();
}
