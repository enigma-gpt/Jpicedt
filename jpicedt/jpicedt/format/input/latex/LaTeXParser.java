// LaTeXParser.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: LaTeXParser.java,v 1.11 2013/07/26 06:10:51 vincentb1 Exp $
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
package jpicedt.format.input.latex;

import jpicedt.format.input.util.RootExpression;
import jpicedt.format.input.util.Pool;
import jpicedt.format.input.util.ExpressionConstants;

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.model.PicAttributeSet;

import java.awt.Color;

import static jpicedt.format.input.util.ExpressionConstants.*;


/**
 * Grammar rules for the LaTeX's picture environment parser
 */
public class LaTeXParser extends RootExpression  {

	/** key associated with unit-length parameter (double) */
	public static final Pool.Key<Double> KEY_UNIT_LENGTH = new Pool.Key<Double>("latex.unit-length");

	/** key associated with the shared attribute set (PicAttributeSet) */
	public static final Pool.Key<PicAttributeSet> KEY_ATTRIBUTES = new Pool.Key<PicAttributeSet>("latex.attributes");

	private Pool pool;

	/**
	 * Creates a new LaTeXParser, and build grammar rules
	 * @param pool a hashmap used to share variables across the grammar tree
	 */
	public LaTeXParser(Pool pool) {
		this.pool = pool;

		// 0°) unitlength and begin picture
		add(new UnitLengthExpression(pool)); // \\unitlength
		add(new BeginPictureExpression()); // not used (but otherwise, it's considered a non-parsable exp)

		// 1°) parameters
		add(new LineThicknessExpression(pool)); 	// \\linethickness

		// 2°) groups
		add(new PicGroupExpression(pool));       // %Begin|End group

		// 3°) lines & polygons
		add(new PicLineExpression(pool));		// %PicLine
		add(new PicPolygonExpression(pool));	// %PicPolygon

		// 4°) frames
		add(new PicRectangleExpression(pool));	// %PicRectangle

		// 5°) ellipses and arcs
		add(new PicEllipseExpression(pool));	// %PicEllipse

		// 6°) splines
		add(new PicBezierExpression(pool));	// %PicBezier (quad), \\qbezier or \\bezier{N}

		// 7°) text and misc.
		add(new LaTeXPutExpression(pool));	// \\put(x,y){...} -> PicText, PicPut (also support LaTeX's "\\circle" command w/o PicEdt comment line)

		// 8°) comment
		add(new jpicedt.format.input.util.CommentExpression("%"));

		// [todo] multiput

		// throw REParserException.EndOfPicture to signals that a \end{picture} was found
		// and that the parsing process ends up here (otherwise, we stop at EOF)
		add(new EndPictureExpression());
	}


	/**
	 * reinit LaTeX related parameters
	 */
	public void reinit(){

		PicAttributeSet ltxSet = new PicAttributeSet();
		double unitLength = 1; // 1mm
		pool.put(KEY_UNIT_LENGTH, new Double(unitLength));
		pool.put(KEY_ATTRIBUTES, ltxSet);
	}
}
