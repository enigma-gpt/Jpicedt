// LaTeXOval.java --- -*- coding: iso-8859-1 -*-
// November 13, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: LaTeXOval.java,v 1.9 2013/03/31 06:57:14 vincentb1 Exp $
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
