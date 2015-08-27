// WildCharExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: WildCharExpression.java,v 1.9 2013/03/31 06:58:04 vincentb1 Exp $
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
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * a RegExp that represents a single occurence of a wild-char, i.e.
 * ANY_CHAR, EOL or ANY_CHAR_EOL (as defined in <a href="ExpressionConstants.html">ExpressionConstants</a>
 * or one of the predefinite fields in java.lang.Character.
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: WildCharExpression.java,v 1.9 2013/03/31 06:58:04 vincentb1 Exp $
 *
 */
public class WildCharExpression extends AbstractRegularExpression {

    private int type;
	private Character C;

    /**
     * @param type one of predefinite types available in class java.lang.Character.
	 *        May also be ANY_CHAR (any character), EOL (end of line), or ANY_CHAR_EOL (a combination of both).
     */
    public WildCharExpression(int type){
		this.type=type;
    }

    public int getType(){
		return type;
	}

    /**
     * @return TRUE if the first character of the remaining substring is a wild-char of the permitted type
     * If TRUE, send a ParserEvent with value=char found ('\n' in case of an EOL)
     */
    public boolean interpret(Context context) throws REParserException{

		C = context.read();
		if (C == null) return false; // at EOF or at end-of-block
		char c = C.charValue();
		// EOL ?
		if (c=='\n'){
			if (type==EOL || type==ANY_CHAR_EOL) {
				action(new ParserEvent(this, context, true, new Character(c)));
				return true;
			}
			else {
				context.pushBack();
				return false;
			}
		}

		// otherwise, we compare the current char with the given wildchar type, and proceed further
		// by sending the appropriate ParserEvent :
		if (type==ANY_CHAR || type==ANY_CHAR_EOL || Character.getType(c) == this.type) {
			action(new ParserEvent(this, context, true, C));
			return true;
		}
		// no match:
		context.pushBack();
		return false;
    }

    /**
     * Return the last character parsed.
     */
	public Character getCharacter(){
		return C;
	}


    public String toString(){

		String charType;
		switch(type){
			case ANY_CHAR:
				charType="ANY_CHAR";
				break;
			case EOL:
				charType="EOL";
				break;
			case ANY_CHAR_EOL:
				charType="ANY_CHAR_EOL";
				break;
			default:
				charType=new Integer(type).toString();
		}
		return "[WildChar:" +  charType + "]";
    }
}
