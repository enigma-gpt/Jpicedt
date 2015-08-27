// StatementExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: StatementExpression.java,v 1.8 2013/03/31 06:58:29 vincentb1 Exp $
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

import jpicedt.graphic.PicPoint;

import java.util.*;
import static jpicedt.format.input.util.ExpressionConstants.*;
/**
 * An expression for "statement"-parsing, i.e. a name followed by an assignment sign followed by a numerical value
 *  e.g. "dash=4.0" or "thickness->8"
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: StatementExpression.java,v 1.8 2013/03/31 06:58:29 vincentb1 Exp $
 *
 */
public class StatementExpression extends AbstractRegularExpression {

    private Number value;
    private String lhs, as, pf; // leftHandSide, equalSymbol, postfix (used in toString() method)
    private AbstractRegularExpression exp1, exp2, exp3;

    /**
     * @param leftHandSide aka name of a variable
     * @param equalSymbol e.g. "=" or "->" etc...
     * @param postfix marks the end of the numerical expression (can be null, see NumericalExpression comments)
     * @param type type of numerical value (DOUBLE or INTEGER)
     * @param sign constraints laid on the sign of numerical values (ANY_SIGN, POSITIVE, ...)
     *
     * A typical use would be : StatementExpression("dash", "=", null, "pt_1", DOUBLE, POSITIVE)
     */
    public StatementExpression(String leftHandSide, String equalSymbol, String postfix, int type, int sign){

		lhs = leftHandSide;
		as = equalSymbol;
		pf = postfix;

		exp1 = new LiteralExpression(leftHandSide+equalSymbol);
		exp2 = new WhiteSpacesOrEOL();
		exp3 = new NumericalExpression(type, sign, postfix, true){
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
		    	value = (Number)e.getValue();}};
    	}

	/**
     * @return TRUE if and only if every expression contained in this expression has been found, in the proper order.
     * @throws REParserException.IncompleteSequence if any expression but the first one wasn't found
     *
     * Whether TRUE or FALSE, calls action with key="&" and value=value of number found (of type Number)
     */
    public boolean interpret(Context c) throws REParserException {

		if (!exp1.interpret(c)) return false;
		exp2.interpret(c); // always return true (repeat_at_least_0)
		if (!exp3.interpret(c)) throw new REParserException.IncompleteSequence(c,this);
		action(new ParserEvent(this, c, true, value));
		return true;
    }

	/**
	 * Return the value of the RHS, wrapped in a Number (either Integer or Double)
	 */
	public Number getValue(){
		return value;
	}

    /**
     * @return a String representation of this expression
     */
    public String toString(){

		return "[StatementExpression : " + lhs + ", " + as + ", " + pf + "]";
    }
}
