// LaTeXLine.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: LaTeXLine.java,v 1.12 2013/03/31 06:57:19 vincentb1 Exp $
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
import static jpicedt.graphic.toolkit.BasicEditPointConstraint.*;

/**
 * Parses LaTeX lines, with or without arrows, i.e. <code>\\line</code> or <code>\\vector</code>. This is to
 * be used as a child expression of either {@link LaTeXPutExpression LaTeXPutExpression} or {@link
 * jpicedt.format.input.pstricks.PsRPutExpression#PsRPutExpression
 * jpicedt.format.input.pstricks.PsRPutExpression}.
 * <ul>
 * <li> <code>\\line(<var>x</var>,<var>y</var>){<var>hor-len</var>}</code> &rarr; line starting at
 *  (<var>x</var>,<var>y</var>), and having <var>hor-len</var> as its horizonal extent (except for vertical
 *  lines, where <var>hor-len</var> specifies the vertical extent.</li>
 * <li> <code>\\vector(<var>x</var>,<var>y</var>){<var>hor-len</var>}</code> &rarr; same thing, yet with an
 * arrow</li> <
 * /ul>
 * @author Sylvain Reynal
 * @since jpicedt 1.4pre2
 * @version $Id: LaTeXLine.java,v 1.12 2013/03/31 06:57:19 vincentb1 Exp $
 */
 public class LaTeXLine extends SequenceExpression {

	private double x,y; // used to compute the line slope
	private Pool pool;

	public LaTeXLine(Pool pl) {

		super(true);// throw IncompleteExpression
		this.pool = pl;

		// \line -> set first polygon point
		this.add(new AlternateExpression(
		             new LaTeXInstanciationExpression("\\line", new PicMultiCurve(new PicPoint(),new PicPoint()), pool),
		             new LaTeXInstanciationExpression("\\vector", new PicMultiCurve(new PicPoint(),new PicPoint()), pool){
			             public void action(ParserEvent e) {
				             super.action(e);
				             pool.currentObj.setAttribute(RIGHT_ARROW, ArrowStyle.ARROW_HEAD);
			             }}));
		this.add(WHITE_SPACES_OR_EOL);
		// (x,y) -> set slope
		this.add(
		    new PicPointExpression("(", ",", ")"){
			    public void action(ParserEvent e) {
				    if (DEBUG) System.out.println(e);
				    PicPoint pt = (PicPoint)e.getValue();
				    x = pt.x ; y = pt.y;
			    }
		    }
		);
		this.add(WHITE_SPACES_OR_EOL);
		// {
		this.add(new LiteralExpression("{"));
		// len} -> must be .ge. 0
		this.add(
		    new NumericalExpression(DOUBLE, POSITIVE, "}", true) {
			    public void action(ParserEvent e) {
				    if (DEBUG) System.out.println(e);
				    // horizontal extent (except for vertical lines) :
				    double len = ((Double)e.getValue()).doubleValue() * pool.get(LaTeXParser.KEY_UNIT_LENGTH);
				    PicMultiCurve pp = ((PicMultiCurve)(pool.currentObj));
					// set put point:
					PicPoint putPoint = (PicPoint)pool.get(PsRPutExpression.KEY_RPUT_POINT);
					if (putPoint==null) putPoint = (PicPoint)pool.get(LaTeXPutExpression.KEY_PUT_POINT);
					if (putPoint==null) putPoint = new PicPoint();
				    // vertical line :
				    PicPoint pt1 = new PicPoint(putPoint);
				    if (x==0){
					    if (y>=0) pt1.translate(0,len);
					    else pt1.translate(0,-len);
				    }
				    // other lines :
				    else {
					    if (x>=0) pt1.x += len;
					    else pt1.x -= len;
					    if (y>=0) pt1.y += len * Math.abs(y/x);
					    else pt1.y -= len * Math.abs(y/x);
				    }
				    pp.setCtrlPt(0,putPoint,FREELY); // first end-point (and drag corresponding control-point accordingly)
				    pp.setCtrlPt(3,pt1,FREELY); // second end-point (ibid.)
			    }
		    }
		);
	}
}
