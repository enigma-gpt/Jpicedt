// EnclosingExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: EnclosingExpression.java,v 1.7 2013/03/31 06:59:49 vincentb1 Exp $
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

import java.io.*;

/**
 * An expression that can encompass a sub-expression it encloses with markers, e.g. "{" + sub-expression + "}"<br>
 * The interpret() methods work as follows :<br>
 * <ul>
 * <li>look up an endMarker matching beginMarker in Context.getRemainingSubstring (that is, skip enclosed blocks with the same markers type)
 * <li>set this endMarker as the new Context's endMarker
 * <li>save enclosed expression as "value", and interpret it
 * <li>restore old Context's endMarker
 * </ul>
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: EnclosingExpression.java,v 1.7 2013/03/31 06:59:49 vincentb1 Exp $
 */
public class EnclosingExpression extends AbstractRegularExpression {

	private String value, openingDelimiter, closingDelimiter;
	private AbstractRegularExpression child;
	private boolean noLineFeed;

	/**
	 * @param openingDelimiter the opening delimiter
	 * @param child the Expression that must be parsed inside the delimiters (can be null)
	 * @param closingDelimiter the closing delimiter
	 * @param noLineFeed if true, closingDelimiter must be on the same line as openingDelimiter
	 *
	 * A typical use would be : EnclosingExpression("{", new WildChar(ANY_CHAR),"}",false)
	 */
	public EnclosingExpression(String openingDelimiter, AbstractRegularExpression child, String closingDelimiter, boolean noLineFeed){

		this.child = child;
		this.openingDelimiter=openingDelimiter;
		this.closingDelimiter=closingDelimiter;
		this.noLineFeed = noLineFeed;
	}

	/**
	 * @param openingDelimiter the opening delimiter
	 * @param child the Expression that must be parse inside the delimiter (can be null)
	 * @param closingDelimiter the closing delimiter
	 * Linefeeds are allowed.
	 */
	public EnclosingExpression(String openingDelimiter, AbstractRegularExpression child, String closingDelimiter){
		this(openingDelimiter, child, closingDelimiter, false);
	}

	/**
	 * Change the Expression that must be parsed inside the delimiters
	 * to the given expression (can be null)
	 */
	public void setChild(AbstractRegularExpression child){
		this.child = child;
	}

	/**
	 * Call action() with value=enclosed string
	 * @return TRUE if delimiters as well as inward expr have been found,
	 */
	public boolean interpret(Context context) throws REParserException {

		if (!context.matchAndMove(openingDelimiter)) return false;
		context.mark(); // mark pos. just behin openingDelimiter

		// look up closingDelimiter:
		int inwardBlocks = 0;
		while(true){
			if (context.matchAndMove(openingDelimiter)) { // <-- enter inner block
				inwardBlocks++;
				continue;
			}
			if (context.matchAndMove(closingDelimiter)) { // <-- exit inner block
				if (inwardBlocks==0){ // ok, this closingDelimiter matches the FIRST openingDelimiter,
					// i.e. this is the outermost one : first compute new block boundaries, then enter block.
					int pastClosingDelimiter = context.getCaretPosition();
					int newEndBlockMarker = pastClosingDelimiter - closingDelimiter.length();
					context.reset(); // move caret back to block start, to set newBlockStart
					context.enterBlock(newEndBlockMarker);
					// now parse the content of this new block :
					boolean result;
					if (child != null) result = child.interpret(context);
					else result = true;
					value=context.getBlockContent();
					if (result==true) action(new ParserEvent(this, context, true, value));
					// restore old context: (caret now points to "newEndBlockMarker")
					context.exitBlock();
					// move cursor just after closingDelimiter :
					context.moveCaretTo(pastClosingDelimiter);
					return result;
				}
				else { // we are still "inside" the outermost block : go on until we find the outermost closingDelimiter
					inwardBlocks--;
					continue;
				}
			}
			try {
				Character C = context.read(); // move one step forward
				if (C == null) break; // end-of-block (EOF raising a REParserException.EOF exception)
				if (noLineFeed && C.charValue()=='\n') break;
			}
			catch (REParserException.EOF pe){
				break; // mutate EOF exception to BlockMismatch exception
			}
		}
		throw new REParserException.BlockMismatch(context,this);
	}

	/**
	 * @return the enclosed string (= block content as returned by Context)
	 */
	public String getEnclosedString(){
		return value;
	}

	/**
	 *
	 */
	public String toString(){

		return "[EnclosingExpression:\"" + openingDelimiter + "\", " + child + ", \"" + closingDelimiter + "\"]";
	}
}
