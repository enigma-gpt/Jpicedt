// PSTDotStyleExpression.java --- -*- coding: iso-8859-1 -*-
// September 3, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2013 Sylvain Reynal
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
// Version: $Id: PSTDotStyleExpression.java,v 1.7 2013/03/31 06:55:29 vincentb1 Exp $
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

package jpicedt.format.input.pstricks;

import jpicedt.format.input.util.*;
import jpicedt.graphic.io.formatter.*;
import jpicedt.graphic.model.*;
import java.awt.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.PolydotsStyle.*;
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * Parses statements similar to "dotstyle=style" where style is one of PicObjectConstants predefined dot styles.
 * @author Sylvain Reynal
 * @since jpicedt 1.4
 * @version $Id: PSTDotStyleExpression.java,v 1.7 2013/03/31 06:55:29 vincentb1 Exp $
 * <p>
 *
 */
public class PSTDotStyleExpression extends SequenceExpression {

	private Pool pool;
	private Pool.Key<PicAttributeSet> attributeSetKey;


	/**
	 * @param pl parser's pool
	 * @param attributeSetKey used to fetch the attribute set in which parsed parameters are stored.
	 */
	public PSTDotStyleExpression(Pool pl, Pool.Key<PicAttributeSet> attributeSetKey) {

		super(false);// doesn't throw IncompleteSequenceException

		pool = pl;
		this.attributeSetKey = attributeSetKey;
	}

	public boolean interpret(Context context) throws REParserException {

		if (!context.matchAndMove("dotstyle=")) return false;
		WHITE_SPACES_OR_EOL.interpret(context);
		if (context.matchAndMove("none")) pool.setAttribute(attributeSetKey, POLYDOTS_STYLE,NONE); // not really allowed, though...
		else if (context.matchAndMove("*")) pool.setAttribute(attributeSetKey, POLYDOTS_STYLE,POLYDOTS_DISK);
		else if (context.matchAndMove("o")) pool.setAttribute(attributeSetKey, POLYDOTS_STYLE,POLYDOTS_CIRCLE);
		else if (context.matchAndMove("+")) pool.setAttribute(attributeSetKey, POLYDOTS_STYLE,POLYDOTS_PLUS);
		else if (context.matchAndMove("triangle*")) pool.setAttribute(attributeSetKey, POLYDOTS_STYLE,POLYDOTS_TRIANGLE_FILLED);
		else if (context.matchAndMove("triangle")) pool.setAttribute(attributeSetKey, POLYDOTS_STYLE,POLYDOTS_TRIANGLE);
		else if (context.matchAndMove("square*")) pool.setAttribute(attributeSetKey, POLYDOTS_STYLE,POLYDOTS_SQUARE_FILLED);
		else if (context.matchAndMove("square")) pool.setAttribute(attributeSetKey, POLYDOTS_STYLE,POLYDOTS_SQUARE);
		else if (context.matchAndMove("pentagon*")) pool.setAttribute(attributeSetKey, POLYDOTS_STYLE,POLYDOTS_PENTAGON_FILLED);
		else if (context.matchAndMove("pentagon")) pool.setAttribute(attributeSetKey, POLYDOTS_STYLE,POLYDOTS_PENTAGON);
		else if (context.matchAndMove("|")) pool.setAttribute(attributeSetKey, POLYDOTS_STYLE,POLYDOTS_DISK); // not supported yet
		else throw new REParserException.SyntaxError(context,this);
		return true;
	}
}
