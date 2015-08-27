// WildCharExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: WildCharExpression.java,v 1.9 2013/03/31 06:58:04 vincentb1 Exp $
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
