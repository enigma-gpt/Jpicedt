// NumericalExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: NumericalExpression.java,v 1.8 2013/03/31 06:59:24 vincentb1 Exp $
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
 * An expression containing only digits, possibly preceded by whitespaces ;
 * a post-delimiters can be specified, as well as the number's type (int or double) and its sign
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: NumericalExpression.java,v 1.8 2013/03/31 06:59:24 vincentb1 Exp $
 *
 */
public class NumericalExpression extends AbstractRegularExpression {

    private int numberType;
    private int sign;
    private String postfix;
    private boolean swallowPostFix;

    private Number value;

    /**
     * @param numberType INTEGER or DOUBLE
     * @param sign ANY_SIGN, POSITIVE, NEGATIVE, STRICTLY_POSITIVE, STRICTLY_NEGATIVE or NON_NULL
     * @param postfix a String that signals the end of this numerical expression (if NULL, swallows as many digits as possible)
     * @param swallowPostFix if TRUE, move cursor JUST BEHIND the postfix; else move it TO THE POSTFIX POSITION
	 *        N/A if postFix is null.
     */
    public NumericalExpression(int numberType, int sign, String postfix, boolean swallowPostFix){

		this.numberType=numberType;
		this.sign=sign;
		this.postfix=postfix;
		this.swallowPostFix = swallowPostFix;
    }

    /**
	 * Constructor w/o postfix, i.e. parse as many digits as possible.
     * @param numberType INTEGER or DOUBLE
     * @param sign ANY_SIGN, POSITIVE, NEGATIVE, STRICTLY_POSITIVE, STRICTLY_NEGATIVE or NON_NULL
     */
    public NumericalExpression(int numberType, int sign){
		this(numberType, sign, null, false);
    }

    /**
	 * Constructor w/o postfix, type=DOUBLE and sign=ANY_SIGN.
     */
    public NumericalExpression(){
		this(DOUBLE, ANY_SIGN, null, false);
    }

    /**
     * @return TRUE if expr has been found on the current line
     * Calls action() in case of successful parsing, and send a ParserEvent with value=parsed number.
     */
    public boolean interpret(Context context) throws REParserException {

		String str;
		int postFixIndex;
		// if there's a postfix :
		if (postfix != null){
			postFixIndex = context.indexOfBeforeEOL(postfix); // it'd make no sense searching for occurences on the next
								// line since parseDouble would raise an exception if there's a "\n" in the string
			if (postFixIndex <= 0) return false; // no postfix found or lentgh=0
			str = context.readTo(postFixIndex);
			if (swallowPostFix) context.read(); // read postfix but don't use it
		}
		// no postfix -> swallow as many digits as possible (and postFixIndex = index of the last digit+1), dropping any leading whitespaces
		else {
			StringBuffer buf = new StringBuffer(); // temp. storage for characters
			postFixIndex=0;
			boolean dotFound = false; // "." can be found only once !
			boolean minusFound = false; // "-" can be found only at the beginning !
			boolean digitFound = false; // true as soon as one digit has been found
			while(true){
				Character cc = context.read();
				//System.out.println("Read char = " + cc);
				if (cc == null) break; // EOF or End-of-block
				char c = cc.charValue();
				if (c==' '){
					if (minusFound || digitFound || dotFound) break; // whitespaces must be leading only !
					buf.append(c);
					continue; // skip leading whitespaces
				}
				if (c=='-' || c=='+') {
					if (minusFound || digitFound || dotFound) break; // "-" must precede any occurence of a digit or the dot sign, so any ensuing "-" signals the end of the number
					minusFound = true;
					buf.append(c);
					continue;
				}
				if (c=='.') { // [pending] there's still a bug if "-.5" is parsed ! (it's accepted where it shouldn't)
					if (dotFound) break; // we can have only one ".", so the second one signals the end of the expression
					dotFound = true;
					buf.append(c);
					continue;
				}
				if (Character.isDigit(c)){
					digitFound = true;
					buf.append(c);
					continue;
				}
				context.moveCaretBy(-1); // push back last char read
				break; // any other char (including "\n") signals the end of the expression
			}
			str = buf.toString();
		}

		if (str.length() == 0) return false; // empty string

		try{
			switch(numberType){
				case INTEGER:
					value = new Integer(str);
					break;
				default:
				case DOUBLE:
					value = new Double(str);
					break;
			}
		}
		catch(NumberFormatException nfe){
			throw new REParserException.NumberFormat(context,this);
		}
		switch(sign){
			case POSITIVE:
				if (value.doubleValue() < 0) throw new REParserException.NumberSign(context,this);
				break;
			case NEGATIVE:
				if (value.doubleValue() > 0) throw new REParserException.NumberSign(context,this);
				break;
			case STRICTLY_POSITIVE:
				if (value.doubleValue() <= 0) throw new REParserException.NumberSign(context,this);
				break;
			case STRICTLY_NEGATIVE:
				if (value.doubleValue() >= 0) throw new REParserException.NumberSign(context,this);
				break;
			case NON_NULL:
				if (value.doubleValue() == 0) throw new REParserException.NumberSign(context,this);
				break;
			default:
			case ANY_SIGN:
				break;
		}

		action(new ParserEvent(this, context, true, value));
		return true;
    }

	/**
	 * Return the parsed value, wrapped in a Number (either Integer or Double)
	 */
	public Number getValue(){
		return value;
	}

    /**
     *
     */
    public String toString(){

		String typeStr;
		switch(numberType){
			case INTEGER:
				typeStr="int";
				break;
			default:
			case DOUBLE:
				typeStr="double";
				break;
		}
		String signStr;
		switch(sign){
			case POSITIVE:
				signStr=">=0";
				break;
			case NEGATIVE:
				signStr="<=0";
				break;
			case STRICTLY_POSITIVE:
				signStr=">0";
				break;
			case STRICTLY_NEGATIVE:
				signStr="<0";
				break;
			case NON_NULL:
				signStr="!=0";
				break;
			default:
			case ANY_SIGN:
				signStr="ANY";
				break;
		}

		return "[Numerical:" + typeStr + signStr + " post=" + postfix + " swallow=" + swallowPostFix + "]";
    }
}
