// SequenceExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: SequenceExpression.java,v 1.8 2013/03/31 06:58:34 vincentb1 Exp $
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
 * An expression that represents a sequence of expressions to be matched exactly in
 * the same order they're being added to the sequence.
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: SequenceExpression.java,v 1.8 2013/03/31 06:58:34 vincentb1 Exp $
 *
 */
public class SequenceExpression extends AbstractRegularExpression {

	private ArrayList<AbstractRegularExpression> expressionList=new ArrayList<AbstractRegularExpression>();
	private boolean throwIncompleteSequenceException;

	/**
	 * constructs a sequence that contains no sub-expression and doesn't throw IncompleteSequenceException
	 */
	public SequenceExpression(){
		this.throwIncompleteSequenceException=false;
	}

	/**
	 * constructs a sequence that contains no sub-expression.
	 * @param throwIncompleteSequenceException if TRUE, and if the first sub-expression has been found but
	 *        any ensuing expression hasn't been found, throw a REParserException.IncompleteSequenceException.
	 */
	public SequenceExpression(boolean throwIncompleteSequenceException){
		this.throwIncompleteSequenceException=throwIncompleteSequenceException;
	}

	/**
	 * constructs a sequence with the given expression as the first sub-expression
	 * and which doesn't throw IncompleteSequenceException.
	 */
	public SequenceExpression(AbstractRegularExpression expr){
		this.throwIncompleteSequenceException=false;
		expressionList.add(expr);
	}

	/**
	 * constructs a sequence with the given expression as the first sub-expression.
	 * @param throwIncompleteSequenceException if TRUE, and if the first sub-expression has been found but
	 *        any ensuing sub-expression hasn't been found, throw a REParserException.IncompleteSequenceException.
	 */
	public SequenceExpression(AbstractRegularExpression expr, boolean throwIncompleteSequenceException){
		this.throwIncompleteSequenceException=throwIncompleteSequenceException;
		expressionList.add(expr);
	}

	/**
	 * constructs a sequence with the given expression as the first and second sub-expression and which doesn't throw Exception
	 */
	public SequenceExpression(AbstractRegularExpression expr1, AbstractRegularExpression expr2){

		this.throwIncompleteSequenceException=false;
		expressionList.add(expr1);
		expressionList.add(expr2);
	}

	/**
	 * constructs a sequence with the given expression as the first and second sub-expression
	 * @param throwIncompleteSequenceException if TRUE, and if the first sub-expression has been found but
	 *        any ensuing sub-expression hasn't been found, throw a REParserException.IncompleteSequenceException.
	 */
	public SequenceExpression(AbstractRegularExpression expr1, AbstractRegularExpression expr2, boolean throwIncompleteSequenceException){
		this.throwIncompleteSequenceException=throwIncompleteSequenceException;
		expressionList.add(expr1);
		expressionList.add(expr2);
	}

	/**
	 * constructs a sequence with three sub-expressions
	 * @param throwIncompleteSequenceException if TRUE, and if the first sub-expression has been found but
	 *        any ensuing sub-expression hasn't been found, throw a REParserException.IncompleteSequenceException.
	 */
	public SequenceExpression(AbstractRegularExpression expr1, AbstractRegularExpression expr2, AbstractRegularExpression expr3, boolean throwIncompleteSequenceException){
		this.throwIncompleteSequenceException=throwIncompleteSequenceException;
		expressionList.add(expr1);
		expressionList.add(expr2);
		expressionList.add(expr3);
	}

	/**
	 * add the given expression to the sequence
	 */
	public void add(AbstractRegularExpression expr){
		expressionList.add(expr);
	}

	/**
	 * @return TRUE if and only if every expression contained in this SequenceExpression were found,
	 *         in the same order as they were added to the sequence. FALSE otherwise.
	 * @throws REParserException.IncompleteSequence if flag throwIncompleteSequenceException is TRUE
	 *         and any expression but the first one wasn't found
	 *
	 * Whether TRUE or FALSE, calls action with key="&amp;" and value=new Integer(index of last expression
	 * parsed with success)
	 */
	public boolean interpret(Context c) throws REParserException {

		boolean firstExpFound = false;
		int j=0;
		for(Iterator i=expressionList.iterator(); i.hasNext();){
			if (((AbstractRegularExpression)i.next()).interpret(c) == false) {
				if (firstExpFound && throwIncompleteSequenceException) throw new REParserException.IncompleteSequence(c,this);
				return false;
			}
			firstExpFound=true;
			j++;
		}
		action(new ParserEvent(this, c, true, new Integer(j)));
		return true;
	}

	/**
	 *
	 */
	public String toString(){

		String s = "[SequenceExpression:";
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
			s += "\n\t\t&" + new Integer(j) + subExpStr + " ";
			j++;
		}
		return s + "]";
	}
}
