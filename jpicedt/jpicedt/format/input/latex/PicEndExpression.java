// PicEndExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PicEndExpression.java,v 1.8 2013/03/31 06:56:29 vincentb1 Exp $
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
import java.util.*;
import java.util.regex.*;

/**
 * Match "%End XXXX"-like expressions at the beginning of a line (or preceeded by whitespaces),
 * after skipping as many lines as necessary.
 */
public class PicEndExpression extends AbstractRegularExpression {

	private String literal;
	private Pattern pattern;

	/**
	 * @param literal e.g. "%End Line"
	 */
	public PicEndExpression(String literal){
		try {
			this.pattern = Pattern.compile("\n *" + literal);
		}
		catch (PatternSyntaxException e){
			e.printStackTrace();
		}
		this.literal = literal;
	}

	/**
	 * @return TRUE in any case, else throw an exception (@EOF) ; (this means that exception throwing is
	 * the only way to get out of the this expression...)
	 * in case of success, call action() with value=leading String
	 * @throws REParserException.NotFoundInFile if EOF was reached before this expression could be found
	 */
	public boolean interpret(Context context) throws REParserException {

		context.mark();
		String skipped = context.find(pattern); // move caret past matched string if found
		if (skipped != null){
			action(new ParserEvent(this, context, true, skipped));
			return true;
		}
		throw new REParserException.NotFoundInFile(context, this); //EOF=>stop here
	}

	public String toString(){
		return "[PicEndExpression:" + literal + "]";
	}
}
