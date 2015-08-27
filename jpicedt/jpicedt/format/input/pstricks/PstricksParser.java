// PstricksParser.java --- -*- coding: iso-8859-1 -*-
// July 24, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2013 Sylvain Reynal
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
// Version: $Id: PstricksParser.java,v 1.11 2013/03/31 06:54:59 vincentb1 Exp $
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
package jpicedt.format.input.pstricks;

import jpicedt.format.input.util.RootExpression;
import jpicedt.format.input.util.EnclosingExpression;
import jpicedt.format.input.util.Pool;
import jpicedt.format.input.util.ExpressionConstants;

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.model.*;

import java.awt.Color;
import java.io.*;
import java.util.*;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Root expression for the Pstricks parser, containing grammar rules for the pstricks format.
 * Pstricks support is pretty ok, though add-ons (like pst-nodes.sty) are still missing.
 * <p>
 * This class consists of grammar rules, wrapped in a RootExpression which can itself be directly added
 * to the main tree (for instance, using DefaultParser.addGrammar()).
 * <P>
 * Note : the current parser used across the
 * JPicEdt application can be retrieved using jpicedt.JPicEdt.getParser().
 * <p>
 * [TODO] : \psgrid, \psaxes, \pscustom, arrow size, dot style.
 * @author Sylvain Reynal
 * @version $Id: PstricksParser.java,v 1.11 2013/03/31 06:54:59 vincentb1 Exp $
 */
public class PstricksParser extends RootExpression {

	/** key associated with \\psxunit register (double) */
	public static final Pool.Key<Double> KEY_X_UNIT = new Pool.Key<Double>("pst.x-unit");
	/** key associated with \\psyunit register (double) */
	public static final Pool.Key<Double> KEY_Y_UNIT = new Pool.Key<Double>("pst.y-unit");
	/** key associated with \\psrunit register (double) */
	public static final Pool.Key<Double> KEY_R_UNIT = new Pool.Key<Double>("pst.r-unit");
	/** key associated with the attribute set which stores PsTricks default graphical parameters (PicAttributeSet) */
	public static final Pool.Key<PicAttributeSet> KEY_ATTRIBUTES = new Pool.Key<PicAttributeSet>("pst.attributes");
	/** user-defined colours (a HashMap which gets filled by UserDefinedColorsExpression, see \\newgray, \\newrgbcolor,... in PsTricks documentation p.5) */
	public static final Pool.Key<HashMap<String,Color>> KEY_USER_COLOURS = new Pool.Key<HashMap<String,Color>>("pst.user-colours");
	/** \\newpsobject : key associated with the HashMap that associates pairs "macroName -> param_string",
	 * e.g. "\\myline" -> "linecolor=green,filltype=solid" (HashMap) */
	public static final Pool.Key<HashMap<String,String>> KEY_NEWPSOBJECTS = new Pool.Key<HashMap<String,String>>("pst.newpsobjects");

	private Pool pool;

	/**
	 * Creates a new PstricksParser, and build grammar rules
	 */
	public PstricksParser(Pool pool) {

		this.pool = pool;

		// 0°) begin picture
		add(new BeginPsPictureExpression()); // not used (but otherwise, it's considered a non-parsable exp)

		// 1°) parameters
		add(new EnclosingExpression("\\psset{", new PSTParametersExpression(pool, KEY_ATTRIBUTES), "}")); // \\psset{...}
		add(new UserDefinedColorExpression(pool)); // \\newrgbcolor{...}() or similar commands

		// 2°) lines and polygons :
		add(new PsQLineExpression(pool)); // \\qline (PsTricks)
		add(new PsPolygonExpression(pool,PsPolygonExpression.POLYGON));	// \\pspolygon (closed)
		add(new PsPolygonExpression(pool,PsPolygonExpression.LINE));	//\\psline (open)
		add(new PsPolygonExpression(pool,PsPolygonExpression.DOTS));	//\\psdots

		// 3°) frames
		add(new PsFrameExpression(pool));

		// 4°) ellipses and arcs
		add(new PsEllipseExpression(pool));	// \\psellipse
		add(new PsCircleExpression(pool));	// \\pscircle
		add(new PsArcExpression(pool,PsArcExpression.ARC));	// \\psarc
		add(new PsArcExpression(pool,PsArcExpression.WEDGE));	// \\pswedge
		add(new PsQDiskExpression(pool)); // \\qdisk

		// 5°) splines
		add(new PsBezierExpression(pool));	// \\psbezier

		// 6°) text and misc.
		add(new PsRPutExpression(pool)); // \\rput...

		// 7°) comment
		add(new jpicedt.format.input.util.CommentExpression("%"));

		// 8°) \\newpsobject
		add(new PsObjectExpression(this,pool));

		// throw REParserException.EndOfPicture to signals that a \end{pspicture} was found
		// and that the parsing process ends up here (otherwise, we stop at EOF)
		add(new EndPsPictureExpression());

		//System.out.println("PsTricks grammar : \n" + this.toString());
	}


	/**
	 * reinit shared parameters belonging to the Pool
	 */
	public void reinit(){
		//System.out.println("Reinit' PstricksParser...");
		double  pstXunit,pstYunit,pstRunit;
		pstXunit = pstYunit = pstRunit = 10.0; // PsTricks's default = 1cm
		pool.put(KEY_X_UNIT, new Double(pstXunit));
		pool.put(KEY_Y_UNIT, new Double(pstYunit));
		pool.put(KEY_R_UNIT, new Double(pstRunit));
		pool.put(KEY_ATTRIBUTES, new PicAttributeSet()); // PsTricks's registers
		pool.put(KEY_NEWPSOBJECTS, new HashMap<String,String>());
		pool.put(KEY_USER_COLOURS, new HashMap<String,Color>());
	}
}
