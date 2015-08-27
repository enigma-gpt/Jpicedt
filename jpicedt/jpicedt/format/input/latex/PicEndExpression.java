// PicEndExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PicEndExpression.java,v 1.8 2013/03/31 06:56:29 vincentb1 Exp $
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
