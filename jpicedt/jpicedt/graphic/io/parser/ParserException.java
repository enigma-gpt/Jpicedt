// ParserException.java --- -*- coding: iso-8859-1 -*-
// August 1, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: ParserException.java,v 1.10 2013/03/27 07:03:49 vincentb1 Exp $
// Keywords:
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
package jpicedt.graphic.io.parser;

import org.xml.sax.SAXParseException;

/**
 * Placeholder for the Exception class ; should serve as the base-class for any Exception related
 * to parsing processes, and may be used as an Exception wrapper as well, e.g. for SAX generated Exception's.
 */
public class ParserException extends Exception {


	public ParserException(String message){
		super(message);
	}

	/**
	 * Convenient constructor for wrapping an Exception into a ParserException
	 */
	public ParserException(Exception ex){
		super(ex);
	}

	/**
	 * Return a localized version of the given string, by relying on the currently installed
	 * jpicedt.Localizer.
	 */
	protected String localize(String str){
		return jpicedt.Localizer.currentLocalizer().get(str);
	}


	public String getMessage(){
		if (getCause() instanceof SAXParseException){
			SAXParseException saxEx = (SAXParseException)getCause();
			return saxEx.getMessage() + " line="+saxEx.getLineNumber() + " column="+saxEx.getColumnNumber();
		}
		else return super.getMessage();
	}


	/**
	 * May signal that the content being currently parsed does not correspond to
	 * any recognized/supported format
	 */
	public static class UnrecognizedFileFormat extends ParserException {

		public UnrecognizedFileFormat(){
			super("exception.parser.UnrecognizedFileFormat");
		}

		public String toString(){
			return localize(getMessage());
		}
	}

}
