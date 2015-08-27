// PsPolygonExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PsPolygonExpression.java,v 1.9 2013/03/31 06:53:59 vincentb1 Exp $
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
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Parses \\pspolygon, \\psdots and \\psline commands : <ul>
 * <li>\\pspolygon[param](2,4)(3,15) // NO ARROW ALLOWED HERE !<br>
 * <li>\\pspolygon*[param](5,1)(5,8)...(xN,yN)<br>
 * <li>\\psline[param]{arrows}(5,1)(5,8)...(xN,yN)<br>
 * <li>\\psline*[param]{arrows}(5,1)(5,8)...(xN,yN)<br>
 * <li>\\psdots[param,dotstyle=style,dotscale=a b,dotangle=angle]{arrows}(5,1)(5,8)...(xN,yN)<br>
 * <li>\\psdots*[param]{arrows}(5,1)(5,8)...(xN,yN) (same as above, '*' being unused)<br>
 * </ul>
 * Note : PsPolygon -> close path ; PsLine -> open path ; PsDots -> dots only<br>
 * @author Sylvain Reynal
 * @since jpicedt 1.3.2
 * @version $Id: PsPolygonExpression.java,v 1.9 2013/03/31 06:53:59 vincentb1 Exp $
 * <p>
 *
 */
 public class PsPolygonExpression extends SequenceExpression {

	private Pool pool;
	private String type;
	/** parses a \\pspolygon command */
	public static final String POLYGON = "\\pspolygon";
	/** parses a \\psline command */
	public static final String LINE = "\\psline";
	/** parses a \\psdots command */
	public static final String DOTS = "\\psdots";

	/**
	 * Uses the given type as the default tag
	 */
	public PsPolygonExpression(Pool pl, String type){
		this(pl,type,null);
	}

	/**
	 * @param shape POLYGON, LINE or DOTS
	 * @param tag if null, default to shape
	 */
	public PsPolygonExpression(Pool pl, String shape, String tag){

		super(true); // throw IncompleteSequence Exception
		pool = pl;
		this.type = shape;
		if (tag==null) tag = shape;

		PicMultiCurve prototype = new PicMultiCurve();
		if (type==POLYGON) prototype.setClosed(true);
		else  prototype.setClosed(false);

		add(new PSTInstanciationExpression(tag, prototype,pool));
		add(WHITE_SPACES_OR_EOL);
		add(new OptionalExpression(new StarExpression(pool)));
		add(WHITE_SPACES_OR_EOL);
		// add optional param here
		add(new OptionalExpression(new EnclosingExpression("[",new PSTParametersExpression(pool,Pool.CURRENT_OBJ_ATTRIBUTES),"]"))); // push in object's attributeSet
		// add arrows if type==LINE
		if (type==LINE) add(new OptionalExpression(new PSTArrowExpression(pool)));
		// multi-points :
		add(WHITE_SPACES_OR_EOL);
		int minNbPts = (type==DOTS ? 1 : 2);
		add(new RepeatExpression(new SequenceExpression(
		                             WHITE_SPACES_OR_EOL,
		                             new PicPointExpression("(",",",")"){
			                             public void action(ParserEvent e){
				                             if (DEBUG) System.out.println(e);
				                             PicPoint pt = (PicPoint)e.getValue();
				                             ((PicMultiCurve)(pool.currentObj)).addPoint(pt.toMm(pool.get(PstricksParser.KEY_X_UNIT),pool.get(PstricksParser.KEY_Y_UNIT)));
											 if (type!=DOTS) pool.currentObj.setAttribute(POLYDOTS_STYLE,PolydotsStyle.NONE); // force "no dots"
											 // else dot style attributes have been parsed by PSTParametersExpressioin, and will be used
			                             }
		                             }),minNbPts, AT_LEAST));
	}

	public String toString(){
		return "[PsPolygonExpression."+type+"]";
	}
}
