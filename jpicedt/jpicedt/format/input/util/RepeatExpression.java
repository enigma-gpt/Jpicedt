// RepeatExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: RepeatExpression.java,v 1.8 2013/03/31 06:58:44 vincentb1 Exp $
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

import java.util.*;
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * An expression that represents a pattern repeating a given number of times
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: RepeatExpression.java,v 1.8 2013/03/31 06:58:44 vincentb1 Exp $
 *
 */
public class RepeatExpression extends AbstractRegularExpression {


	private AbstractRegularExpression expr;
	private int repeat;
	private int mode;

	/**
	 * @param repeat number of times this expression has to be found sequentially ;
	 * @param mode EXACTLY, AT_LEAST or AT_MOST
	 */
	public RepeatExpression(AbstractRegularExpression expr, int repeat, int mode){

		this.expr=expr;
		this.repeat = repeat;
		this.mode=mode;
	}

	/**
	 * Set the base-pattern to the given expression
	 */
	public void setPattern(AbstractRegularExpression expr){
		this.expr=expr;
	}


	/**
	 * @return TRUE if and only if the given "expr" has been found as many times as specified by
	 *         the "mode" given in the constructor.
	 * If TRUE, calls action with : key="*", value=new Integer(number of effective repeat found)
	 */
	public boolean interpret(Context c) throws REParserException {

		int i=0;
		switch(mode){
		case AT_LEAST:
			for(i=0; i<repeat; i++){
				if (expr.interpret(c) == false) {
					action(new ParserEvent(this, c, false, new Integer(i)));
					return false;
				}
			}
			while(expr.interpret(c)){i++;} // <-- "swallow" remaining expr occurences
			action(new ParserEvent(this, c, true,  new Integer(i)));
			return true;
		case AT_MOST:
			for(i=0; i<repeat; i++){
				if (expr.interpret(c) == false) {
					action(new ParserEvent(this, c, true, new Integer(i)));
					return true; // <-- break as soon as no more occurence of expr
				}
			}
			if (expr.interpret(c) == true) {
				i++;
				action(new ParserEvent(this, c, false, new Integer(i)));
				return false; // <-- now, repeat count has been exceeded
			}
			action(new ParserEvent(this, c, true,  new Integer(i)));
			return true;
		case EXACTLY:
		default: // EXACTLY
			for(i=0; i<repeat; i++){
				if (expr.interpret(c) == false) {
					action(new ParserEvent(this, c, false, new Integer(i)));
					return false;
				}
			}
			action(new ParserEvent(this, c, true,  new Integer(i)));
			return true;
		}
	}

	/**
	 *
	 */
	public String toString(){

		String modeStr;
		switch(mode){
		case AT_LEAST :
			modeStr="AT_LEAST";
			break;
		case AT_MOST :
			modeStr="AT_MOST";
			break;
		default:
			modeStr="EXACT";
			break;
		}
		return "[Repeat:" + modeStr + " " + repeat + " " + expr + "]";
	}
}
