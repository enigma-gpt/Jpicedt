// PicBezierExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PicBezierExpression.java,v 1.11 2013/03/31 06:56:49 vincentb1 Exp $
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
package jpicedt.format.input.latex;

import jpicedt.format.input.util.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PEToolKit;
import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * Parser expression for :
 * <p>
 * %Bezier 0|1 0|1 (x1,y1)(xC,yC)(x2,y2) dash=value<br>
 * Any string<br>
 * %End Bezier<br>
 * where dash is optional. The whole expression MUST hold on a single line (as opposed to LaTeX or eepic commands)<br>
 * <p>
 * Or : <br>
 * \\qbezier(x1,y1)(xc,yc)(x2,y2)<br>
 * \\bezier{n}(x1,y1)(xc,yc)(x2,y2)<br>
 * @author Sylvain Reynal
 * @since jpicedt 1.3
 * @version $Id: PicBezierExpression.java,v 1.11 2013/03/31 06:56:49 vincentb1 Exp $
 *
 */
public class PicBezierExpression extends AlternateExpression  {

	Pool pool;
	PicPoint p1 = new PicPoint(); // buffer for quad => cubic conversion
	PicPoint pCtrl = new PicPoint();
	PicPoint p2 = new PicPoint();

    public PicBezierExpression(Pool pl){

		this.pool=pl;

		SequenceExpression picBezierExpression = new SequenceExpression(true); // throw IncompleteSequence Exception
		picBezierExpression.add(new LaTeXInstanciationExpression("%Bezier", new PicMultiCurve(new PicPoint()),pool));// curve reduced to a single point
		picBezierExpression.add(WHITE_SPACES);
		picBezierExpression.add(new PicArrowTypeExpression(pool));
		picBezierExpression.add(new BufferPicPointExpression(p1));
		picBezierExpression.add(WHITE_SPACES);
		picBezierExpression.add(new BufferPicPointExpression(pCtrl));
		picBezierExpression.add(WHITE_SPACES);
		picBezierExpression.add(new ConvertQuadToCubicExpression(p2));
		picBezierExpression.add(WHITE_SPACES);
		picBezierExpression.add(new OptionalExpression(new PicDashStatement(pool)));
		picBezierExpression.add(new PicEndExpression("%End Bezier"));
		this.add(picBezierExpression);

		SequenceExpression latexBezierExpression = new SequenceExpression(true);
		latexBezierExpression.add(new AlternateExpression(
			new LaTeXInstanciationExpression("\\qbezier", new PicMultiCurve(new PicPoint()),pool),
			new SequenceExpression(
				new LaTeXInstanciationExpression("\\bezier{", new PicMultiCurve(new PicPoint()),pool),
				new NumericalExpression(INTEGER, POSITIVE, "}", true),
				true
		)));
		latexBezierExpression.add(WHITE_SPACES_OR_EOL);
		latexBezierExpression.add(new BufferPicPointExpression(p1));
		latexBezierExpression.add(WHITE_SPACES);
		latexBezierExpression.add(new BufferPicPointExpression(pCtrl));
		latexBezierExpression.add(WHITE_SPACES);
		latexBezierExpression.add(new ConvertQuadToCubicExpression(p2));
		this.add(latexBezierExpression);
    }

	public String toString(){
		return "[PicBezierExpression]";
	}

	class BufferPicPointExpression extends PicPointExpression {
		PicPoint ptBuf;
		public BufferPicPointExpression(PicPoint pt){
			super("(",",",")");
			this.ptBuf = pt;
		}

		public void action(ParserEvent e){
			if (DEBUG) System.out.println(e);
			PicPoint pt = getPicPoint();
			double unitLength = pool.get(LaTeXParser.KEY_UNIT_LENGTH);
			ptBuf.setCoordinates(pt.toMm(unitLength));
		}
	}

	class ConvertQuadToCubicExpression extends BufferPicPointExpression {
		public ConvertQuadToCubicExpression(PicPoint pt){
			super(pt);
		}

		public void action(ParserEvent e){
			super.action(e); // store p2
			PicPoint[] cubicPts = PEToolKit.convertQuadBezierToCubic(p1,pCtrl,p2);
			pool.currentObj.setCtrlPt(0,cubicPts[0],null);
			((PicMultiCurve)pool.currentObj).curveTo(cubicPts[1], cubicPts[2], cubicPts[3]);
		}
	}

} // PicBezierExpression
