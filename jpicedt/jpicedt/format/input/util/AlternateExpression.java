// AlternateExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: AlternateExpression.java,v 1.9 2013/07/26 06:10:36 vincentb1 Exp $
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

/**
 * A regular expression that mimics the "x|y" RegExp syntax.
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: AlternateExpression.java,v 1.9 2013/07/26 06:10:36 vincentb1 Exp $
 *
 */
public class AlternateExpression extends AbstractRegularExpression {

	private ArrayList<AbstractRegularExpression> expressionList=new ArrayList<AbstractRegularExpression>();

	/**
	 * Creates a new AlternateExpression with the given expr as first element
	 */
	public AlternateExpression(AbstractRegularExpression expr){
		expressionList.add(expr);
	}

	/**
	 * Creates a new AlternateExpression with the given expr as first and second element
	 */
	public AlternateExpression(AbstractRegularExpression expr1,AbstractRegularExpression expr2){
		expressionList.add(expr1);
		expressionList.add(expr2);
	}

	/**
	 * Creates a new AlternateExpression with the given expr as first, second and third element
	 */
	public AlternateExpression(AbstractRegularExpression expr1,AbstractRegularExpression expr2,AbstractRegularExpression expr3){
		expressionList.add(expr1);
		expressionList.add(expr2);
		expressionList.add(expr3);
	}

	/**
	 * Creates a new AlternateExpression with no element
	 */
	public AlternateExpression(){}

	/**
	 * Add a new child to this AlternateExpression.
	 */
	public void add(AbstractRegularExpression expr){expressionList.add(expr);}

	/**
	 * Calls "action" with value=Integer(index of first expression parsed with success,
	 * starting from 0).
	 * @return <code>true</code> as soon as one of the expressions contained in this
	 * <code>AlternateExpression</code> has been found ; <code>false</code> if no match.<p>
	 */
	public boolean interpret(Context c) throws REParserException {

		int j=0;
		for(Iterator i=expressionList.iterator(); i.hasNext();){
			AbstractRegularExpression are = (AbstractRegularExpression)i.next();
			if (are.interpret(c) == true) {
				action(new ParserEvent(this, c, true, new Integer(j)));
				return true;
			}
			j++;
		}
		//action(new ParserEvent(this, c, false, null));
		return false;
	}

	/**
	 * Return a list containing every expression in this AlternateExpression
	 */
	public ArrayList getExpressionList(){
		return expressionList;
	}

	/**
	 *
	 */
	public String toString(){

		String s = "[AlternateExpression:";
		int j=1;
		for(Iterator i=expressionList.iterator(); i.hasNext();){
			StringBuffer subExpStr = new StringBuffer(((AbstractRegularExpression)i.next()).toString());
			int pos=0;
			while(true){
				pos = subExpStr.toString().indexOf('\n',pos);
				if (pos == -1) break;
				subExpStr.insert(pos+1,'\t');
				pos += 2;
			}
			s += "\n\t\t|" + new Integer(j) + subExpStr + " ";
			j++;
		}
		return s + "]";

	}
}
