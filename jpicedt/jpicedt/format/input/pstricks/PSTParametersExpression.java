// PSTParametersExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PSTParametersExpression.java,v 1.9 2013/03/31 06:55:14 vincentb1 Exp $
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
import java.awt.*;

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * PsTricks graphics parameters, e.g. "linewidth=5pt, fillcolor=blue,..." (no leading, nor trailing brackets,
 * these should be added to an enclosing expression)<p>
 * Currently supported parameters :<br><ul>
 * <li>xunit, yunit, runit, unit
 * <li>linestyle=xxx
 * <li>linewidth=xxx (with unit) + dash'n dot parameters
 * <li>linecolor=xxx (may be a native PsTrick colour, see predefined colours in PsTricksFormater, OR a user-defined colours)
 * <li>fillstyle=xxx
 * <li>fillcolor=xxx (same note as for linecolor)
 * <li>shadow parameters
 * <li>hatch parameters.
 * </ul>
 * <p>
 * Depending on the value of "attributeSetKey" given to the constructor,
 * these parameters may act on the attribute set of the current Element (in the Pool), or on another
 * attribute set (for instance, PsTricks's registers).
 * @author Sylvain Reynal
 * @version $Id: PSTParametersExpression.java,v 1.9 2013/03/31 06:55:14 vincentb1 Exp $
 * <p>
 */
 public class PSTParametersExpression extends RepeatExpression {


	/**
	 * @param pool parser's pool
	 * @param attributeSetKey used to fetch the attribute set in which parsed parameters are stored.
	 */
	public PSTParametersExpression(Pool pool, Pool.Key<PicAttributeSet> attributeSetKey){

		super(null, 0, AT_LEAST); // doesn't throw IncompleteSequenceException (which means that there might be nothing b/w [ and ])

		// an alternate list of every possible parameters that gets repeated until there are no more parameters to be parsed !
		AlternateExpression altParms = new AlternateExpression();

		// ******************************************************************
		// **************** UNIT LENGTH *******************************
		// ******************************************************************
		altParms.add(new PsUnitLengthExpression(pool,PsUnitLengthExpression.XUNIT)); // always push in pool !
		altParms.add(new PsUnitLengthExpression(pool,PsUnitLengthExpression.YUNIT)); // always push in pool !
		altParms.add(new PsUnitLengthExpression(pool,PsUnitLengthExpression.RUNIT)); // always push in pool !
		altParms.add(new PsUnitLengthExpression(pool,PsUnitLengthExpression.UNIT)); // always push in pool !

		// ******************************************************************
		// **************** STROKE properties *******************************
		// ******************************************************************

		// =============== LINEWIDTH ===========================================
		// ex : linewidth=13mm or linewidth=5.6 (default to current
		altParms.add(new PSTLengthParameter(pool, "linewidth", LINE_WIDTH, attributeSetKey));

		// ============= LINECOLOR ==============================================
		// ex : linecolor=green (native) or linecolor=mygray2 (user-defined)
		altParms.add(new PSTColorExpression(pool, "linecolor", LINE_COLOR, attributeSetKey));

		// ============ LINESTYLE ===================================================
		altParms.add(new PSTLineStyleExpression(pool, attributeSetKey));

		// ============= DASH ==============================================
		altParms.add(new PSTDashExpression(pool, attributeSetKey));

		// =============== DOTSEP ===========================================
		altParms.add(new PSTLengthParameter(pool, "dotsep", DOT_SEP, attributeSetKey));

		// ============= DOUBLELINE (true/false) ==============================================
		altParms.add(new PSTBooleanExpression(pool, "doubleline", DOUBLE_LINE, attributeSetKey));

		// =============== DOUBLESEP ===========================================
		altParms.add(new PSTLengthParameter(pool, "doublesep", DOUBLE_SEP, attributeSetKey));

		// ============= DOUBLECOLOR ==============================================
		altParms.add(new PSTColorExpression(pool, "doublecolor", DOUBLE_COLOR, attributeSetKey));

		// ******************************************************************
		// **************** FILL properties *******************************
		// ******************************************************************

		// ============ FILLSTYLE ===================================================
		altParms.add(new PSTFillStyleExpression(pool, attributeSetKey));

		// ============= FILLCOLOR ==============================================
		altParms.add(new PSTColorExpression(pool, "fillcolor", FILL_COLOR, attributeSetKey));

		// =============== HATCHWIDTH ===========================================
		altParms.add(new PSTLengthParameter(pool, "hatchwidth", HATCH_WIDTH, attributeSetKey));

		// =============== HATCHSEP ===========================================
		altParms.add(new PSTLengthParameter(pool, "hatchsep", HATCH_SEP, attributeSetKey));

		// ============= HATCHCOLOR ==============================================
		altParms.add(new PSTColorExpression(pool, "hatchcolor", HATCH_COLOR, attributeSetKey));

		// ============= HATCHANGLE ==============================================
		altParms.add(new PSTAngleParameter(pool, "hatchangle", HATCH_ANGLE, attributeSetKey));


		// ******************************************************************
		// **************** SHADOW properties *******************************
		// ******************************************************************

		// ============= SHADOW (true/false) ==============================================
		altParms.add(new PSTBooleanExpression(pool, "shadow", SHADOW, attributeSetKey));

		// ============= SHADOWANGLE ==============================================
		altParms.add(new PSTAngleParameter(pool, "shadowangle", SHADOW_ANGLE, attributeSetKey));

		// =============== SHADOWSIZE ===========================================
		altParms.add(new PSTLengthParameter(pool, "shadowsize", SHADOW_SIZE, attributeSetKey));

		// ============= SHADOWCOLOR ==============================================
		altParms.add(new PSTColorExpression(pool, "shadowcolor", SHADOW_COLOR, attributeSetKey));

		// ******************************************************************
		// **************** DOTS properties *******************************
		// ******************************************************************

		// ============== DOTSTYLE ===============================================
		altParms.add(new PSTDotStyleExpression(pool, attributeSetKey));

		// ============== DOTANGLE ================================================
		altParms.add(new PSTAngleParameter(pool, "dotangle", POLYDOTS_ANGLE, attributeSetKey));

		// [SR:pending] dotsize, dotscale.


		// [pending] =========== NOT PARSABLE PARAMETERS (either not implemented yet, or not syntaxically correct)
		// Note : non-implemented parameters are, so far : "dimen", "border" and related, "origin" and "swapaxes"
		altParms.add(new WildCharExpression(ANY_CHAR_EOL));
		// this exp. will be successfully parsed as many times
		// as needed for the non-implemented parameter to be completely swallowed.
		// Besides, it may also parse separating "," and EOL/spaces (even where they aren't accepted by PsTricks)

		// ================================================================
		setPattern(altParms);
	}

	public String toString(){
		return "[PSTParametersExpression]";
	}
}
