// WordExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: WordExpression.java,v 1.8 2013/03/31 06:57:59 vincentb1 Exp $
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

/**
 * A RegExp that parses a word, that is, a string:
 * <ul>
 * <li> either composed of letters only, or letters and digits only (see java.lang.Character.isLetter() for
 * details),</li> 
 * <li> or terminated by the specified end-delimiter (in which case it may contain chars not restricted to
 * letters)</li> 
 * </ul>
 * @since jpicedt 1.3
 * @version $Id: WordExpression.java,v 1.8 2013/03/31 06:57:59 vincentb1 Exp $
 * @author Sylvain Reynal
 */
public class WordExpression extends AbstractRegularExpression {

	private String postfix;
	private boolean swallowPostFix, allowDigit, noLineFeed;
	private String value;

	/**
	 * @param postfix a String that signals the end of the word (if NULL, swallows as many letters as
	 * possible) 
	 * @param swallowPostFix if TRUE, move cursor JUST BEHIND the postfix; else move it TO THE POSTFIX
	 * POSITION 
	 * @param allowDigit if TRUE, and postfix=NULL, swallows as many
	 * "letter-or-digit" as possible (i.e. alphanumeric expression)
	 * @param noLineFeed if true, expression must fit on a single line. Otherwise, it may span several lines.
	 */
	public WordExpression(String postfix, boolean swallowPostFix, boolean allowDigit, boolean noLineFeed){

		this.postfix=postfix;
		this.swallowPostFix = swallowPostFix;
		this.allowDigit = allowDigit;
		this.noLineFeed = noLineFeed;
	}

	/**
	 * Allow line-feed's, i.e. expression may span more than one line.
	 * @param postfix a String that signals the end of the word (if NULL, swallows as many letters as
	 * possible) 
	 * @param swallowPostFix if TRUE, move cursor JUST BEHIND the postfix; else move it TO THE POSTFIX
	 * POSITION 
	 * @param allowDigit if TRUE, and postfix=NULL, swallows as many
	 * "letter-or-digit" as possible (i.e. alphanumeric expression)
	 */
	public WordExpression(String postfix, boolean swallowPostFix, boolean allowDigit){
		this(postfix, swallowPostFix, allowDigit, false);
	}

	/**
	 * Constructor with allowsDigit=false (i.e. only alphabetic character are swallowed if postfix = null),
	 * and which allows linefeeds.
	 * @param postfix a String that signals the end of the word (if NULL, swallows as many letters as
	 * possible) 
	 * @param swallowPostFix if TRUE, move cursor JUST BEHIND the postfix; else move it TO THE POSTFIX
	 * POSITION 
	 */
	public WordExpression(String postfix, boolean swallowPostFix){

		this(postfix, swallowPostFix, false, false);
	}

	/**
	 * Parse an expression containing letters only, and stops when a non-letter char is found. Allows
	 * line-feed's, which can be removed later on using Context.removeLineFeeds (static method).
	 */
	public WordExpression(){
		this(null, false, false, false);
	}

	/**
	 * If parsing was successfull, sends a ParserEvent with value=string found (this may be an empty string)
	 * <p>
	 * You may then use <code>Context.removeLineFeeds(String)</code> to remove CR from "value".
	 * @return TRUE if expr has been found
	 */
	public boolean interpret(Context context) throws REParserException {

		// if there's a postfix specified, let's fetch its position in the current substring :
		if (postfix != null){
			int postFixIndex;
			if (noLineFeed) postFixIndex = context.indexOfBeforeEOL(postfix);
			else postFixIndex = context.indexOf(postfix); // return -1 if not found
			if (postFixIndex < 0) return false; // no matching postfix was found
			value = context.readTo(postFixIndex);
			if (swallowPostFix) context.read(); // read postfix but don't use it
			// send a ParserEvent with value=string found
			action(new ParserEvent(this, context, true, value));
			return true;
		}

		// no postfix specified => swallows as many letters as possible (swallowPostFix N/A here)
		else {
			StringBuffer buf = new StringBuffer(); // temp. storage for chars
			while(true){
				Character C=null;
				try {C = context.read();}
				catch (REParserException.EOF eofEx){ // EOF
					value = buf.toString();
					action(new ParserEvent(this, context, true, value));
					throw new REParserException.EOF(); // rethrows after action !!! (otherwise, the receiver never gets the event)
				}
				if (C == null) break; // end-of-block
				char c = C.charValue();
				if (c=='\n') {
					if (noLineFeed) {context.pushBack(); break;}
					else buf.append(c);
				}
				else if (allowDigit){
					if (Character.isLetterOrDigit(c)) buf.append(c);
					else {context.pushBack(); break;}
				}
				else if (Character.isLetter(c)) buf.append(c);
				else {context.pushBack(); break;}
			}
			// send a ParserEvent with value=string found
			value = buf.toString();
			action(new ParserEvent(this, context, true, value));
			return true;
		}
	}

	/**
	 * Return the parsed content of this expression after a successfull parsing.
	 */
	public String getValue(){
		return value;
	}

	public String toString(){
		return "[WordExpression:post=" + postfix + " swallow=" + swallowPostFix + "]";
	}
}
