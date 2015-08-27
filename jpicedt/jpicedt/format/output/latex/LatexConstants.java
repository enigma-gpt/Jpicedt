// LatexConstants.java --- -*- coding: iso-8859-1 -*-
// March 1, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: LatexConstants.java,v 1.13 2013/03/27 07:23:37 vincentb1 Exp $
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
package jpicedt.format.output.latex;
import  jpicedt.format.output.util.FormatConstants;

/**
 * "emulated latex" parameters : these parameters get used by LatexFormatter, LatexParser
 *  and LatexViewFactory.
 * <ul>
 * <li>lineThickness : (mm) ; used when parsing LaTeX file, when no \\lineThickness command is found
 * <li>emLineLength : (mm) when in "em. latex" mode, a line whose slope doesn't match an existing
 * <li>LaTeX slope (cf. picture environment) is emulated by tiny horizontal and/or
 * vertical segments ; this parameter determines the length of these segments
 * <li>maxLatexCircleDiameter (mm) : circles whose diameter is greater than this value are emulated ;
 *                                        this ususally depends on available "lasym" fonts
 * <li>maxLatexDiskDiameter (mm) : same thing, but for disks now.
 * <li>maxEmLineSlope : line with an absolute slope greater than this value are considered to be vertical, i.e. "line(0,1){etc...}"
 * <li>minEmLineSlope : same thing, but for horizontal lines.
 * <li>fileWrapperProlog and fileWrapperEpilog : things to prepend and append before and after
 *     "begin{picture}" and "end{picture}" to create a content able to be compiled by latex. May include
 *     things like "include{package}",...
 */
public class LatexConstants extends FormatConstants {

	//////////////////////////////
	// KEY for PROPERTIES
	/////////////////////////////
	public static final String KEY_EM_LINE_LENGTH = "latex.emulated-line-length";
	public static final String KEY_MAX_CIRCLE_DIAMETER = "latex.max-circle-diameter";
	public static final String KEY_MAX_DISK_DIAMETER ="latex.max-disk-diameter";
	public static final String KEY_MAX_EM_CIRCLE_SEGMENT_LENGTH = "latex.max-emulated-circle-segment-length";
	public static final String KEY_MAX_EM_LINE_SLOPE = "latex.max-emulated-line-slope";
	public static final String KEY_FILE_WRAPPER_PROLOG = "latex.file-wrapper-prolog";
	public static final String KEY_FILE_WRAPPER_EPILOG = "latex.file-wrapper-epilog";

	////////////////////////////////
	//// DEFAULT VALUES
	////////////////////////////////
	public static final double DEFAULT_EM_LINE_LENGTH = 0.12;
	public static final double DEFAULT_MAX_CIRCLE_DIAMETER = 14 ; //mm
	public static final double DEFAULT_MAX_DISK_DIAMETER = 5.4; //mm
	public static final double DEFAULT_MAX_EM_CIRCLE_SEGMENT_LENGTH = 1;
	public static final double DEFAULT_MAX_EM_LINE_SLOPE = 1000.0 ;
	public static final String DEFAULT_FILE_WRAPPER_PROLOG =
	        "\\documentclass{article} \n\\thispagestyle{empty}\n\\begin{document}\n";
	public static final String DEFAULT_FILE_WRAPPER_EPILOG = "\\end{document}";

	public static final String RESCALING_TEX_FUNCTION = "\\JPicScale";


}
