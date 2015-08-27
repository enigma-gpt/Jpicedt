// PicPointExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PicPointExpression.java,v 1.8 2013/03/31 06:59:04 vincentb1 Exp $
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

import jpicedt.graphic.PicPoint;

import java.util.*;
import static jpicedt.format.input.util.ExpressionConstants.*;
/**
 * An expression for 2D-Point parsing e.g. "(12.3, 34.5)" or "[12.1;-16]"
 * If a coordinate conversion is necessary, it must be computed in the body of the action() method
 * @since jpicedt 1.3
 * @version $Id: PicPointExpression.java,v 1.8 2013/03/31 06:59:04 vincentb1 Exp $
 * @author Sylvain Reynal
 *
 */
public class PicPointExpression extends AbstractRegularExpression {

	private String prefix, delimiter, postfix;
	private Double x,y;
	private AbstractRegularExpression exp1, exp2, exp3, exp4;

	/**
	 * @param prefix the opening bracket e.g. "("
	 * @param delimiter the string that delimits the border between the two numbers e.g. ","
	 * @param postfix the closing bracket e.g. ")"
	 * A typical use would be : PicPointExpression("(", ",", ")")
	 */
	public PicPointExpression(String prefix, String delimiter, String postfix){

		this.prefix=prefix;
		this.delimiter=delimiter;
		this.postfix=postfix;
		exp1 = new LiteralExpression(prefix);
		exp2 = new NumericalExpression(DOUBLE, ANY_SIGN, delimiter, true){
			                         public void action(ParserEvent e){ x = (Double)e.getValue();}};
		exp3 = new NumericalExpression(DOUBLE, ANY_SIGN, postfix, true){
			   						 public void action(ParserEvent e){ y = (Double)e.getValue();}};

	}

	/**
	 * @return TRUE if expr has been found
	 * send a ParserEvent with value = parsed PicPoint
	 *
	 * @throws IncompleteSequence exception if there's nothing after the prefix...
	 */
	public boolean interpret(Context c) throws REParserException {

		// prefix:
		if (!exp1.interpret(c)) return false;

		// first number (x) :
		if (!exp2.interpret(c)) throw new REParserException.IncompleteSequence(c,this);

		// second number (y) :
		if (!exp3.interpret(c)) throw new REParserException.IncompleteSequence(c,this);

		// build PicPoint
		action(new ParserEvent(this, c, true, new PicPoint(x, y)));
		return true;
	}

	/**
	 * Returns the parsed PicPoint
	 */
	public PicPoint getPicPoint(){
		return new PicPoint(x,y);
	}

	/**
	 *
	 */
	public String toString(){

		return "[PicPointExpression : pre=\"" + prefix + "\" del=\"" + delimiter + "\" post=\"" + postfix + "\"]";
	}
}
