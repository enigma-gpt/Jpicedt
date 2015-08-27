// PsArcExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PsArcExpression.java,v 1.12 2013/03/31 06:54:24 vincentb1 Exp $
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

import jpicedt.format.input.util.ParserEvent;
import jpicedt.format.input.util.NumericalExpression;
import jpicedt.format.input.util.LiteralExpression;
import jpicedt.format.input.util.EnclosingExpression;
import jpicedt.format.input.util.OptionalExpression;
import jpicedt.format.input.util.WhiteSpacesOrEOL;
import jpicedt.format.input.util.Pool;
import jpicedt.format.input.util.SequenceExpression;
import jpicedt.graphic.model.*;
import jpicedt.graphic.PicPoint;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Parses \\psarc commands :
 * <ul>
 * <li>\\psarc[parameters]{&lt;-&gt;}(x0,y0){rad}{angleA}{angleB} ; where at least "arrows" or (x0,y0) args must be included
 * <li>\\psarc*[parameters]{&lt;-&gt;}(x0,y0){rad}{angleA}{angleB} ; idid. but filled
 * </ul>
 * @author Sylvain Reynal
 * @version $Id: PsArcExpression.java,v 1.12 2013/03/31 06:54:24 vincentb1 Exp $
 */
 public class PsArcExpression extends SequenceExpression {

	private Pool pool;
	private double startAngle;
	private String type;
	public static final String ARC = "\\psarc";
	public static final String WEDGE = "\\pswedge";

	/**
	 * @param type ARC, WEDGE
	 */
	public PsArcExpression(Pool pl, String type){
		this(pl,type,null);
	}

	/**
	 * @param type ARC, WEDGE
	 * @param tag optional (used only by \\newpsobject commands), can be null
	 */
	public PsArcExpression(Pool pl, String type, String tag){

		super(true); // throw IncompleteSequence Exception
		this.pool = pl;
		this.type = type;
		if (tag == null) tag = type;

		PicEllipse arc;
		if (type==ARC) arc = new PicEllipse(java.awt.geom.Arc2D.OPEN);
		else arc = new PicEllipse(java.awt.geom.Arc2D.PIE);

		add(new PSTInstanciationExpression(tag, arc, pool));
		add(WHITE_SPACES_OR_EOL);
		add(new OptionalExpression(new StarExpression(pool))); // fill solid
		add(WHITE_SPACES_OR_EOL);
		// add optional param here
		add(new OptionalExpression(new EnclosingExpression("[",new PSTParametersExpression(pool,Pool.CURRENT_OBJ_ATTRIBUTES),"]"))); // push in object's attributeSet
		// arrows (optional for arc, forbidden for wedge)
		add(WHITE_SPACES_OR_EOL);
		if (type==ARC) add(new OptionalExpression(new PSTArrowExpression(pool)));
		// arc's center
		add(WHITE_SPACES_OR_EOL);
		add(new PSTPicPointExpression(PicEllipse.P_CENTER,pool));
		add(WHITE_SPACES_OR_EOL);
		// radius :
		add(new LiteralExpression("{"));
		add(WHITE_SPACES_OR_EOL);
		add(new NumericalExpression(DOUBLE,POSITIVE,"}",true){
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
		add(WHITE_SPACES_OR_EOL);

		// angleA = startAngle :
		add(new LiteralExpression("{"));
		add(WHITE_SPACES_OR_EOL);
		add(new NumericalExpression(DOUBLE,ANY_SIGN,"}",true){
			    public void action(ParserEvent e){
				    if (DEBUG) System.out.println(e);
				    startAngle = ((Double)e.getValue()).doubleValue();
				    ((PicEllipse)(pool.currentObj)).setAngleStart(startAngle);
			    }});
		add(WHITE_SPACES_OR_EOL);
		// angleB = endAngle ; we need angleExtent = angleB - angleA
		add(new LiteralExpression("{"));
		add(WHITE_SPACES_OR_EOL);
		add(new NumericalExpression(DOUBLE,ANY_SIGN,"}",true){
			    public void action(ParserEvent e){
				    if (DEBUG) System.out.println(e);
				    double angleExtent = ((Double)e.getValue()).doubleValue() - startAngle;
				    ((PicEllipse)(pool.currentObj)).setAngleExtent(angleExtent);
			    }});
	}
	public String toString(){
		return "[PsArcExpression."+type+"]";
	}
}
