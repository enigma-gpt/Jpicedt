// REParserException.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: REParserException.java,v 1.10 2013/03/31 06:58:54 vincentb1 Exp $
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

import jpicedt.graphic.io.parser.ParserException;

/**
 * An Exception manager to be used by RE-parsers (i.e. those built on top of AbstractRegularExpression's).
 * <p>
 * The main purpose is to build a meaningfull string so that the user
 * may be able to precisely locate the source of the syntax error and what kind of
 * error it is.
 * <p>
 * See also: key-entries starting with "parser-exception" in  jpicedt/lang/i18n_xx.properties resource files.
 * <p>
 * [Developpers] : new exceptions should be implemented as inner static classes so as to reduce namespace pollution.
 * Simply inherit your class from REParserException, feeding the mother-class constructor with a key-entry string of your choice,
 * then add an appropriate key/value pair in EACH jpicedt/lang.i18n_xx.properties resource file.
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: REParserException.java,v 1.10 2013/03/31 06:58:54 vincentb1 Exp $
 *
 */
public class REParserException extends ParserException {

	/** the current Context being used by the parser which raised an exception */
	private Context context;

	/** the expression that raised the exception */
	private AbstractRegularExpression expr;

	// [developpers] the "message" field has private access in java.lang.Exception ; the only way to
	// retrieve its value is through getMessage().

	/**
	 * create a new REParserException with the specified error code and line number.
	 * @param message used for localization purpose ; this
	 *        should be a key-entry in the current set of jpicedt/lang/i18n_xx.properties
	 *        resource files, with the corresponding value being a properly localized message.
	 */
	public REParserException(String message, Context context){
		this(message, context, null);
	}

	/**
	 * create a new REParserException, raised by the given Expression
	 * @param context the Context used by the parser raising this exception ; merely serves as a document locator.
	 */
	public REParserException(String message, Context context, AbstractRegularExpression expr){

		super(message); // i.e. related to getMessage() in class java.lang.Exception
		this.context=context;
		this.expr = expr;
	}


	// ======================= SOME USEFUL GETTERs/SETTERs overriden from Exception or ParserException ====================

	// i could have overriden getLocalizedMessage(), and leave getMessage() as is, yet the latter would
	// have returned the i18n key-entry, which just makes no sense where UI messaging is concerned, since
	// this key is not even a human-readable english text ;-)
	// Hence i decided to make this key-entry not available to the outside world, and simply override
	// getMessage(). Thus getLocalizedMessage() and toString() simply call getMessage(). This way, we get
	// a unified access to the exception messaging scheme.

	/**
	 * Return a meaningfull, human-readable, properly localized description of this exception
	 */
	public String getMessage(){

		String s="";

		if (context != null){
			String buffer = context.getBuffer();
			s += localize("misc.LineNumber") + " " + (context.getLineNumber()) + ": ";
			s += "\"" + buffer.substring(context.getBOL(), context.getCaretPosition())
				    + "?"
				    + buffer.substring(context.getCaretPosition(), context.getEOL())
					+ "\"\n";
				if (s.length()>=100) s = s.substring(0,100) + " ...\n";
			}
		if (expr != null) s += 	expr + " -> ";
		s += localize(super.getMessage()); // i.e. localize key-entry in i18n resource file
		return s;
	}

	/**
	 * Convenience call to getMessage()
	 */
	public String toString(){
		return getMessage();
	}

	/**
	 * Convenience call to getMessage()
	 */
	public String getLocalizedMessage(){
		return getMessage(); // note that getMessage() already handles localization properly.
	}


	// ========================= INNER STATIC CLASSES start here ================================

	/**
	 * signals an incomplete SequenceExpression
	 */
	public static class IncompleteSequence extends REParserException {
		public IncompleteSequence(Context c, AbstractRegularExpression expr){
			super("exception.parser.SyntaxError",c);
		}
	}

	/**
	 * aka NumberFormatException
	 */
	public static class NumberFormat extends REParserException {
		public NumberFormat(Context c, AbstractRegularExpression expr){
			super("exception.parser.NumberFormatError",c,expr);}
	}

	/**
	 * signals an error concerning the sign of a number (see NumericalExpression)
	 */
	public static class NumberSign extends REParserException {
		public NumberSign(Context c, AbstractRegularExpression expr){
			super("exception.parser.NumberSignError",c,expr);}
	}

	/**
	 * a "end group" has no matching "begin group"
	 */
	public static class EndGroupMismatch extends REParserException {
		public EndGroupMismatch(Context c, AbstractRegularExpression expr){
			super("exception.parser.EndGroupMismatch",c,expr);}
	}

	/**
	 * a "begin group" has no matching "end group"
	 */
	public static class BeginGroupMismatch extends REParserException {
		public BeginGroupMismatch(Context c, AbstractRegularExpression expr){
			super("exception.parser.BeginGroupMismatch",c,expr);}
	}

	/**
	 * a closing delimiter has no matching opening delimiter (see EnclosingExpression)
	 */
	public static class BlockMismatch extends REParserException {
		public BlockMismatch(Context c, AbstractRegularExpression expr){
			super("exception.parser.BlockMismatch",c,expr);}
	}
	/**
	 * a mandatory expression wasn't found
	 */
	public static class NotFoundInFile extends REParserException {
		public NotFoundInFile(Context c, AbstractRegularExpression expr){
			super("exception.parser.NotFoundInFile",c,expr);}
	}
	/**
	 * a syntax error has occured ; should be used as a last resort, when
	 * no specific exception message applies.
	 */
	public static class SyntaxError extends REParserException {
		public SyntaxError(Context c, AbstractRegularExpression expr){
			super("exception.parser.SyntaxError",c,expr);}
	}
	/**
	 * the end of the picture environment was encoutered.
	 * This is NOT an error, just a terrible hack to inform the underlying parser that
	 * the parsing process is over.
	 */
	public static class EndOfPicture extends REParserException {
		public EndOfPicture(){
			super("exception.parser.EOP",null);
		}
	}
	/**
	 * the end of the picture environment wasn't found in the current Reader.
	 */
	public static class EndOfPictureNotFound extends REParserException {
		public EndOfPictureNotFound(){
			super("exception.parser.EOPNotFound",null);
		}
	}
	/**
	 * the end of the file (or the underlying Reader) was reached abnormally, e.g. in the course
	 * of a AbstractRegularExpression.interpret() operation.
	 */
	public static class EOF extends REParserException {
		public EOF(){ super("exception.parser.EOF",null);}
		public EOF(Context c, AbstractRegularExpression expr){ super("exception.parser.AtEOF",c,expr);}
	}
} // REParserException
