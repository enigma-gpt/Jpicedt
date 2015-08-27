// PSTDotStyleExpression.java --- -*- coding: iso-8859-1 -*-
// September 3, 2003 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PSTDotStyleExpression.java,v 1.7 2013/03/31 06:55:29 vincentb1 Exp $
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
