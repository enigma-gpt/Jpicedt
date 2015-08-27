// PicEllipseExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PicEllipseExpression.java,v 1.10 2013/03/31 06:56:34 vincentb1 Exp $
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
package jpicedt.format.input.latex;

import jpicedt.format.input.util.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.PicPoint;
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * PicEllipse :
 * %Ellipse(x0,y0)(width)(height)
 * %Ellipse(x0,y0)(width,height) filled|whiten|blacken|shade arcStart= arcExtent=
 * Any string (multiput...)
 * %End Ellipse
 */
public class PicEllipseExpression extends SequenceExpression   {

    private Pool pool;
	private PicPoint pCenter=new PicPoint();
	private double width;

    public PicEllipseExpression(Pool pl){

		super(true); // throw IncompleteSequence Exception
		pool = pl;

		add(new LaTeXInstanciationExpression("%Ellipse", new PicEllipse(),pool));
		add(WHITE_SPACES);
		add(new OptionalExpression(new PicArrowTypeExpression(pool)));
		add(new CenterPicPointExpression()); // save (x0,y0) in pCenter
		add(WHITE_SPACES);

		// width :
		add(new LiteralExpression("("));
		add(new NumericalExpression(DOUBLE,POSITIVE,")",true){
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				width = ((Double)e.getValue()).doubleValue() * pool.get(LaTeXParser.KEY_UNIT_LENGTH);
		}});
		add(WHITE_SPACES);

		// height :
		add(new LiteralExpression("("));
		add(new NumericalExpression(DOUBLE,POSITIVE,")",true){
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				double height = ((Double)e.getValue()).doubleValue() * pool.get(LaTeXParser.KEY_UNIT_LENGTH);
				PicPoint pBL = new PicPoint(pCenter).translate(-width/2., -height/2.);
				PicPoint pTR = new PicPoint(pCenter).translate(width/2., height/2.);
				((PicEllipse)(pool.currentObj)).setCtrlPt(PicEllipse.P_BL,pBL,null); // no constraint
				((PicEllipse)(pool.currentObj)).setCtrlPt(PicEllipse.P_TR,pTR,null);
		}});
		add(WHITE_SPACES);

		AlternateExpression paramAlt = new AlternateExpression();
		// dash :
		paramAlt.add(new PicDashStatement(pool));
		// arcStart = ?
		paramAlt.add(new StatementExpression("arcStart","=",null, DOUBLE, ANY_SIGN){
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				((PicEllipse)(pool.currentObj)).setAngleStart(((Double)e.getValue()).doubleValue());
		}});
		// arcExtent = ?
		paramAlt.add(new StatementExpression("arcExtent","=",null, DOUBLE, ANY_SIGN){
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				((PicEllipse)(pool.currentObj)).setAngleExtent(((Double)e.getValue()).doubleValue());
		}});
		// fillcolor (WHITEN,...)
		paramAlt.add(new PicColorExpression(pool));
		OptionalExpression paramOpt = new OptionalExpression(paramAlt);
		add(paramOpt);
		add(WHITE_SPACES);
		add(paramOpt);
		add(WHITE_SPACES);
		add(paramOpt);
		add(WHITE_SPACES);
		add(paramOpt);
		add(new PicEndExpression("%End Ellipse"));
    }

	public String toString(){
		return "[PicEllipseExpression]";
	}

	/** store the ellipse center in a private class field for later processing */
	class CenterPicPointExpression extends PicPointExpression {
		public CenterPicPointExpression(){
			super("(",",",")");
		}

		public void action(ParserEvent e){
			if (DEBUG) System.out.println(e);
			PicPoint pt = getPicPoint();
			double unitLength = pool.get(LaTeXParser.KEY_UNIT_LENGTH);
			pCenter.setCoordinates(pt.toMm(unitLength));
		}
	}

} // PicEllipseExpression
