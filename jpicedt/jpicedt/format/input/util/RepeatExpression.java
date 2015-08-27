// RepeatExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: RepeatExpression.java,v 1.8 2013/03/31 06:58:44 vincentb1 Exp $
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
