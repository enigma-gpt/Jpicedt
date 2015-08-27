// NotParsableExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: NotParsableExpression.java,v 1.8 2013/03/31 06:59:29 vincentb1 Exp $
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
package jpicedt.format.input.util;
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * Any string (but w/o line-feeds !)<p>
 * Store the parsed string in Pool's string "notParsed".
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: NotParsableExpression.java,v 1.8 2013/03/31 06:59:29 vincentb1 Exp $
 *
 */
public class NotParsableExpression extends RepeatExpression {

	private Pool pool;

	public NotParsableExpression(Pool pl){

		super(null,0,AT_LEAST);
		pool = pl;

		setPattern(new WildCharExpression(ANY_CHAR){
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				Character C = getCharacter();
				if (C != null) pool.notParsed.append(C);}});
	}

	public void action(ParserEvent e){
		if (DEBUG) System.out.println(e);
		int counter = ((Integer)e.getValue()).intValue();
		// separate each line in "notParsed" with a CR if not an empty line :
		if (counter >0) pool.notParsed.append("\n");
	}
}
