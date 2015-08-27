// PSTColorExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PSTColorExpression.java,v 1.9 2013/03/31 06:55:34 vincentb1 Exp $
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
import jpicedt.format.output.pstricks.PstricksUtilities;
import jpicedt.graphic.model.*;

import java.awt.*;
import java.util.*;
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * Used by PSTParametersExpression to parse statements involving colours, for instance
 *  "linecolor=green" (predefined colour) or "fillcolor=MyGray" (user-defined colours).
 * <p>
 * If no pstricks's native colour matches the RHS, we fetch a HashMap from the Pool's hashtable
 * (value associated with key PstricksParser.KEY_USER_COLOURS) and look up the given colour ;
 * this HashMap may've been filled by UserDefinedColorExpression in the course of the parsing process.
 * @author Sylvain Reynal
 * @version $Id: PSTColorExpression.java,v 1.9 2013/03/31 06:55:34 vincentb1 Exp $
 *
 */
public class PSTColorExpression extends SequenceExpression {

    private Pool pool;
    private PicAttributeName<Color> attribute;
    private Pool.Key<PicAttributeSet> setKey;

    /**
     * @param pl parser's pool
     * @param tag LHS tag (e.g. "linecolor" or "fillcolor") for the StatementExpression
     * @param attributeName name of attribute to modify (must be a predefined
     * PicAttributeName of type "Color")
     * @param attributeSetKey used to fetch the attribute set in which parsed parameters are stored.
     */
    public PSTColorExpression(Pool pl, String tag, PicAttributeName<Color> attributeName, Pool.Key<PicAttributeSet> attributeSetKey){

		super(false); // doesn't throw IncompleteSequenceException (which means that there might be nothing b/w [ and ])

		pool = pl;
		attribute = attributeName;
		this.setKey = attributeSetKey;

		add(new LiteralExpression(tag+"=")); // e.g. "linecolor"
		add(new ParseColorValue());
    }

	private class ParseColorValue extends WordExpression {
		public ParseColorValue(){
			// no postfix, swallows as many letters-or-digit as possible (since user-defined colours may contain digits)
			super(null,false,true,true);
		}
		public void action(ParserEvent e){
			if (DEBUG) System.out.println(e);
			String colourStr = (String)(e.getValue());
			//System.out.println("!!!!!!!!!!!!!!!!!!!! FOUND COLOR : " + colourStr);
			Color colour = PstricksUtilities.getPsTricksColor(colourStr);
			if (colour == null) { // if not a native colour, look up hashtable
				HashMap<String,Color> userColoursMap = pool.get(PstricksParser.KEY_USER_COLOURS);
				colour = userColoursMap.get(colourStr);
				if (colour == null) // if nor a native colour, nor a user-defined colour, default to black...
					colour = Color.black;
			}
			pool.setAttribute(setKey,attribute, colour);
		}
	}
} // class
