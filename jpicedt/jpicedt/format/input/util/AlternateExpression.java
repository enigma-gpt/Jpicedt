// AlternateExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: AlternateExpression.java,v 1.9 2013/07/26 06:10:36 vincentb1 Exp $
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
