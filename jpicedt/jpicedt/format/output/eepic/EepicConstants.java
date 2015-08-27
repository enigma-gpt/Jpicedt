// EepicConstants.java --- -*- coding: iso-8859-1 -*-
// March 1, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: EepicConstants.java,v 1.9 2013/03/27 07:10:55 vincentb1 Exp $
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

package jpicedt.format.output.eepic;

/**
 * Constants useful for Eepic formatters
 */
public class EepicConstants extends jpicedt.format.output.latex.LatexConstants {

	//////////////////////////////
	// PROPERTIES NAMES
	/////////////////////////////

	public static final String KEY_THIN_LINES_MAXWIDTH = "eepic.thinlines";
	public static final String KEY_THICK_LINES_MAXWIDTH = "eepic.thicklines";
	public static final String KEY_FILE_WRAPPER_PROLOG = "eepic.file-wrapper-prolog";
	public static final String KEY_FILE_WRAPPER_EPILOG = "eepic.file-wrapper-epilog";

	////////////////////////////////
	//// DEFAULT VALUES
	////////////////////////////////
	/** all lines with a thickness .lt. thinLinesDEFAULT are formatted as "thinlines" */
    public static final double DEFAULT_THIN_LINES_MAX_WIDTH = 0.151;
	// 0.151 instead of 0.15 due to annoying rounding errors with some JVM...
	// e.g. sometimes, I get 0.150000000002 instead of 0.15, because java.lang.Math doesn't work with BigDecimal (instead, it relies on native C-lib)

	/** all lines with a thickness .lt. thickLinesDEFAULT but greater than thinLinesDEFAULT
	 * are formatted as "thicklines" ; other are formatted as "Thicklines" (case-sensitive !) */
    public static final double DEFAULT_THICK_LINES_MAX_WIDTH = 0.301;

	public static final String DEFAULT_FILE_WRAPPER_PROLOG =
		"\\documentclass{article} "+CR_LF+"\\usepackage{epic,eepic} "+CR_LF+ "\\thispagestyle{empty}" + CR_LF+"\\begin{document}"+CR_LF+"";
	public static final String DEFAULT_FILE_WRAPPER_EPILOG = "\\end{document}";


}
