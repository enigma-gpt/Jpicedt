// PSTLengthParameter.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PSTLengthParameter.java,v 1.9 2013/03/31 06:55:19 vincentb1 Exp $
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
package jpicedt.format.input.pstricks;

import jpicedt.format.input.util.*;
import jpicedt.graphic.io.formatter.*;
import jpicedt.graphic.model.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * Parses statement involving length units (as set by PsTricks's "runit" register),
 * e.g. "linewidth=13mm" or "doublesep=5.6" (default to current "runit")<p>
 * Numerical parsed value is pushed in the Pool's PicAttributeSet having the given key,
 * (after conversion to mm)
 * <p>
 * This expression contains a StatementExpression with parameters "DOUBLE" and "POSITIVE".
 * So far, this expression parses "cm", "mm" and "pt".
 * <p>
 * This expression MUST NOT be used to parse expressions that modify one of "xunit", "yunit" or "runit", since
 * it relies on the value of these registers to compute the actual length value in case no unit is present !!!
 * @author Sylvain Reynal
 * @version $Id: PSTLengthParameter.java,v 1.9 2013/03/31 06:55:19 vincentb1 Exp $
 */
public class PSTLengthParameter extends SequenceExpression {

	private Pool pool;
	private PicAttributeName<Double> attribute;
	private Pool.Key<PicAttributeSet> setKey;
	private double val; // temporary stores parsed value

	/**
	 * @param pl parser's pool
	 * @param tag LHS tag (e.g. "linewidth" or "doublesep") for the StatementExpression
	 * @param attributeName name of attribute to modify (must be a predefined PicAttributeName of type "Double" or at least "Number")
	 * @param attributeSetKey used to fetch the attribute set in which parsed parameters are stored.
	 */
	public PSTLengthParameter(Pool pl, String tag, PicAttributeName<Double> attributeName, Pool.Key<PicAttributeSet> attributeSetKey){

		super(false);
		pool = pl;
		attribute = attributeName;
		this.setKey = attributeSetKey;

		add(new StatementExpression(tag,"=",null,DOUBLE,POSITIVE){ // no postfix, but possibly whitespaces before
			    public void action(ParserEvent e){
				    if(DEBUG) System.out.println(e);
				    val = pool.get(PstricksParser.KEY_R_UNIT) * ((Number)e.getValue()).doubleValue(); // suppose there's no unit
				    pool.setAttribute(setKey,attribute, new Double(val));
		}});
		// now, if there's a specified unit, we rescale to parsed value (i.e. dividing by pstRunit), then we compute the actual length value depending on the unit found
		add(new OptionalExpression(
			new AlternateExpression(
		    	new LiteralExpression("cm"){
					public void action(ParserEvent e){
						if (DEBUG) System.out.println(e);
				        val = val * 10.0 / pool.get(PstricksParser.KEY_R_UNIT);
				        pool.setAttribute(setKey,attribute, new Double(val));
				}},
		        new LiteralExpression("mm"){
					public void action(ParserEvent e){
						if (DEBUG) System.out.println(e);
				        val = val / pool.get(PstricksParser.KEY_R_UNIT);
				        pool.setAttribute(setKey,attribute, new Double(val));
				}},
		        new LiteralExpression("pt"){
					public void action(ParserEvent e){
						if (DEBUG) System.out.println(e);
				        val = val * PS_POINT / pool.get(PstricksParser.KEY_R_UNIT);
				        pool.setAttribute(setKey,attribute, new Double(val));
				}})));
	}
} // class
