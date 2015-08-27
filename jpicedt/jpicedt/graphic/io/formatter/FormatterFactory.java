// FormatterFactory.java --- -*- coding: iso-8859-1 -*-
// February 26, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
// Copyright (C) 2007/2013 Sylvain Reynal, Vincent Belaïche
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
	 * @return le séparateur de ligne courant. Par exemple CR-LF pour MSWindows.
	 */
	String            getLineSeparator();

}
