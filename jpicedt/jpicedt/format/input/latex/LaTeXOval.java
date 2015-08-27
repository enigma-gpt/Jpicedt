// LaTeXOval.java --- -*- coding: iso-8859-1 -*-
// November 13, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: LaTeXOval.java,v 1.9 2013/03/31 06:57:14 vincentb1 Exp $
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
package jpicedt.format.input.latex;

import jpicedt.format.input.pstricks.PsRPutExpression;
import jpicedt.format.input.util.*;
import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Parses LaTeX \\oval command. This is to be used as a child expression of either
 * {@link LaTeXPutExpression LaTeXPutExpression} or
 * {@link jpicedt.format.input.pstricks.PsRPutExpression#PsRPutExpression jpicedt.format.input.pstricks.PsRPutExpression}.
 * <p>
 * \\oval(w,h) or \oval(w,h)[align] (for half- or quarter- ovals).
 * <p>
 * [pending] add "rounded corner" capacity to PicRectangle
 * @author Sylvain Reynal
 * @since jpicedt 1.4pre2
 * @version $Id: LaTeXOval.java,v 1.9 2013/03/31 06:57:14 vincentb1 Exp $
 */
public class LaTeXOval extends SequenceExpression {

	private Pool pool;

	public LaTeXOval(Pool pl) {

		super(true);// throw IncompleteExpression
		this.pool = pl;

		this.add(new LaTeXInstanciationExpression("\\oval", new PicParallelogram(), pool)); // instanciate a rectangle
		this.add(WHITE_SPACES_OR_EOL);
		this.add(
		    new PicPointExpression("(", ",", ")"){ // parse "(width,height)"
			    public void action(ParserEvent e) {
				    if (DEBUG) System.out.println(e);
				    PicPoint pt = (PicPoint)e.getValue();
				    PicParallelogram oval = (PicParallelogram)pool.currentObj;
				    double width = pt.x * pool.get(LaTeXParser.KEY_UNIT_LENGTH);
				    double height = pt.y * pool.get(LaTeXParser.KEY_UNIT_LENGTH);
					// set put point:
					PicPoint putPoint = (PicPoint)pool.get(PsRPutExpression.KEY_RPUT_POINT);
					if (putPoint==null) putPoint = (PicPoint)pool.get(LaTeXPutExpression.KEY_PUT_POINT);
					if (putPoint==null) putPoint = new PicPoint();
				    // compute BL and TR corners :
				    PicPoint pBL = new PicPoint(putPoint); // center
				    pBL.translate(-width/2.0, -height/2.0);
				    PicPoint pTR = new PicPoint(putPoint); // center
				    pTR.translate(width/2.0, height/2.0);
				    oval.setCtrlPt(PicParallelogram.P_BL, pBL,null);
				    oval.setCtrlPt(PicParallelogram.P_TR, pTR,null);
			    }
		    }
		);
		this.add(WHITE_SPACES_OR_EOL);
		// [pending] not used so far !!!
		SequenceExpression alignmentExp = new SequenceExpression(true);// throw IncompleteSequence Exception
		alignmentExp.add(new LiteralExpression("["));
		alignmentExp.add(new WordExpression("]",true)); // swallows chars till "]" is found
		this.add(new OptionalExpression(alignmentExp));
	}
}
