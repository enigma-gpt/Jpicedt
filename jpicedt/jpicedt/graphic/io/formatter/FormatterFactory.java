// FormatterFactory.java --- -*- coding: iso-8859-1 -*-
// February 26, 2002 - jPicEdt, a picture editor for LaTeX.
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
package jpicedt.graphic.io.formatter;

import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.Drawing;

public interface FormatterFactory {

	/** a constraint to indicate that the created Formatter should be able to
	 *  create a standalone String which once save, can be directly fed to, e.g., latex or ghostview,...
	 * as opposed to a "picture environment" file which still has to be included in a LaTeX file
	 */
	public static final String MAKE_STANDALONE_FILE = "make-standalone-file";

	/**
	 * @return a Formatter able to format the given Element according to the format of this factory
	 */
	Formatter createFormatter(Element e);

	/**
	 * @return a Formatter able to format the given Drawing according to the format of this factory ;
	 *         this may reliy on calls to <code>createFormatter(Element e)</code> on the elements
	 *         of the drawing, plus creating auxiliary
	 * @param outputConstraint constraint used by the factory to create a specific Formatter on-the-fly
	 */
	Formatter createFormatter(Drawing d, Object outputConstraint);


	/**
	 * @return le formatteur de commentaire correspondant au type de contenu
	 * offant cette interface.
	 */
	CommentFormatting getCommentFormatter();

	/**
	 * @return le s�parateur de ligne courant. Par exemple CR-LF pour MSWindows.
	 */
	String            getLineSeparator();

}
