// PsCircleExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PsCircleExpression.java,v 1.10 2013/03/31 06:54:14 vincentb1 Exp $
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
import jpicedt.graphic.model.*;
import jpicedt.graphic.PicPoint;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * Quick circle :<ul>
 * <li>\\pscircle[parameters](x0,y0){rad}
 * <li>\\pscircle*[parameters](x0,y0){rad}
 * </ul>
 * @author Sylvain Reynal
 * @since jpicedt 1.3.1
 * @version $Id: PsCircleExpression.java,v 1.10 2013/03/31 06:54:14 vincentb1 Exp $
 * <p>
 *
 */
public class PsCircleExpression extends SequenceExpression {

	private Pool pool;

	/**
	 * Uses default tag.
	 */
	public PsCircleExpression(Pool pl){
		this(pl,null);
	}

	/**
	 * @param tag if null, default to \\pscircle
	 */
	public PsCircleExpression(Pool pl, String tag){

		super(true); // throw IncompleteSequence Exception
		pool = pl;
		if (tag == null) tag = "\\pscircle";

		add(new PSTInstanciationExpression(tag, new PicEllipse(),pool));
		add(WHITE_SPACES_OR_EOL);
		add(new OptionalExpression(new StarExpression(pool)));
		add(WHITE_SPACES_OR_EOL);
		// add optional param here
		add(new OptionalExpression(new EnclosingExpression("[",new PSTParametersExpression(pool,Pool.CURRENT_OBJ_ATTRIBUTES),"]"))); // push in object's attributeSet
		add(WHITE_SPACES_OR_EOL);
		// circle's center
		add(new PSTPicPointExpression(PicEllipse.P_CENTER,pool));
		add(WHITE_SPACES_OR_EOL);
		// radius :
		add(new LiteralExpression("{"));
		add(WHITE_SPACES_OR_EOL);
		add(new NumericalExpression(DOUBLE,POSITIVE,"}",true){ // parse radius
			    public void action(ParserEvent e){
				    if (DEBUG) System.out.println(e);
					double radius = ((Double)e.getValue()).doubleValue() * pool.get(PstricksParser.KEY_R_UNIT);
					// recall center in order to build BL and TR corners :
					PicPoint pBL = pool.currentObj.getCtrlPt(PicEllipse.P_CENTER,null);
					PicPoint pTR = new PicPoint(pBL);
					pBL.translate(-radius, -radius);
					pTR.translate(radius, radius);
					((PicEllipse)(pool.currentObj)).setCtrlPt(PicEllipse.P_BL, pBL,null);
					((PicEllipse)(pool.currentObj)).setCtrlPt(PicEllipse.P_TR, pTR,null);
			    }});
	}
	public String toString(){
		return "[PsCircleExpression]";
	}
}
