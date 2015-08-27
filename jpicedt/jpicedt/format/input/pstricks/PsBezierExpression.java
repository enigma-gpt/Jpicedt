// PsBezierExpression.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013  Sylvain Reynal
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
// Version: $Id: PsBezierExpression.java,v 1.9 2013/03/31 06:54:19 vincentb1 Exp $
// Keywords: parser
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
import jpicedt.graphic.model.*;
import jpicedt.graphic.PicPoint;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Rules for parsing cubic splines with a PsTricks syntax :
 * <ul>
 * <li>\\psbezier[parameters]{arrows}(x0,y0)(x1,y1)(x2,y2)(x3,y3)
 * <li>\\psbezier*[parameters]{arrows}(x0,y0)(x1,y1)(x2,y2)(x3,y3)
 * </ul>
 * or PsTricks's object previously defined by help of a \\newpsobject command.
 * @author Sylvain Reynal
 * @version $Id: PsBezierExpression.java,v 1.9 2013/03/31 06:54:19 vincentb1 Exp $
 *
 */
 public class PsBezierExpression extends SequenceExpression {

	private Pool pool;
	private double xQ0, yQ0;

	/**
	 * tag = \\psbezier and proto = new PicBezierCubic.
	 */
	public PsBezierExpression(Pool pl){
		this(pl, "\\psbezier");
	}

	/**
	 * Constructor allowing to set a non-standard tag. Used e.g. by PsObjectExpression
	 */
	public PsBezierExpression(Pool pl, String tag){

		super(true); // throw IncompleteSequence Exception
		pool = pl;

		PicMultiCurve proto = new PicMultiCurve(new PicPoint(),new PicPoint(),new PicPoint(),new PicPoint());
		add(new PSTInstanciationExpression(tag, proto,pool));
		add(WHITE_SPACES_OR_EOL);

		add(new OptionalExpression(new StarExpression(pool))); // filled ?
		add(WHITE_SPACES_OR_EOL);

		// add optional param here
		add(new OptionalExpression(new EnclosingExpression("[",new PSTParametersExpression(pool,Pool.CURRENT_OBJ_ATTRIBUTES),"]"))); // push in object's attributeSet
		add(WHITE_SPACES_OR_EOL);

		// arrows :
		add(new PSTArrowExpression(pool));
		add(WHITE_SPACES_OR_EOL);

		// first point
		add(new PSTPicPointExpression(0,null,pool)); // null => set specification point
		add(WHITE_SPACES_OR_EOL);

		// first control point
		add(new PSTPicPointExpression(1,null,pool));
		add(WHITE_SPACES_OR_EOL);

		// second control point
		add(new PSTPicPointExpression(2,null,pool));
		add(WHITE_SPACES_OR_EOL);

		// last point
		add(new PSTPicPointExpression(3,null,pool));
	}

	public String toString(){
		return "[PsBezierExpression]";
	}

}
