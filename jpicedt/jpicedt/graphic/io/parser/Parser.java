// Parser.java --- -*- coding: iso-8859-1 -*-
// July 30, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
// Copyright (C) 2007/2013 Sylvain Reynal, Vincent Bela�che
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

import jpicedt.graphic.model.Drawing;

import java.io.*;

/**
 * Ceci est l'interface dont tout <code>Parser</code> pass� en param�tre � <code>PECanvas.read()</code> (ou
 * une m�thode similaire de la classe {@link jpicedt.graphic.PECanvas <code>PECanvas</code>} ou de classe
 * l'utilisant) doit h�riter.
 */
public interface Parser {

	/**
	 * D�marre l'analyse du fichier.
	 * @return Un nouvel expample d'un <code>Drawing</code> peupl� des objets graphiques r�sultant des
	 * l'analyse des donn�es lues par le <code>reader</code> pass� en argument.
	 * @throws <code>ParserException<code> quand une erreur d'analyse survient, par ex. une erreur de
	 * syntaxe, un non appariement d'ouverture et cl�ture de bloc&hellip;
	 */
	public Drawing parse(Reader reader) throws ParserException;

	/**
	 * Analyse syntaxique d'un dessin cod� qui a �t� pr�c�demment extrait d'un
	 * fichier.  La m�thode {@link Drawing parse(Reader reader)} continue
	 * d'�tre fournie pour compatibilit� ascendante.
	 * @throws <code>ParserException</code> quand une erreur survient lors de l'analyse syntaxique.
	 * @since JPicEdt 1.6
	 */
	public void parse(Reader reader,ParsedDrawing parsedDrawing) throws ParserException;

}
