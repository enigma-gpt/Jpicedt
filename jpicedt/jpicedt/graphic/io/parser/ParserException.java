// ParserException.java --- -*- coding: iso-8859-1 -*-
// August 1, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: ParserException.java,v 1.10 2013/03/27 07:03:49 vincentb1 Exp $
// Keywords:
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
