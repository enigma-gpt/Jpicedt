// LaTeXPutExpression.java --- -*- coding: iso-8859-1 -*-
// 1999 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: LaTeXPutExpression.java,v 1.13 2013/03/31 06:57:04 vincentb1 Exp $
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

import jpicedt.format.input.util.*;
import jpicedt.format.input.pstricks.PsBox;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.model.*;

import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 *  \\put(x,y){argument}
 * <p>
 * Depending on the argument found, instanciates :
 * <ul>
 * <li> a PicText : \\makebox, \\framebox, \\dashbox or LR-argument </li>
 * <li> a PicMultiCurve : \\line, \\vector.</li>
 * <li>a PicEllipse : \\circle</li>
 * </ul>
 * <p>
 * The parsing code sets the following Pool's key/value pair (as set by the enclosing XXXPutExpression beforehands):
 * <ul>
 * <li> KEY_PUT_POINT: \\put's (x,y) parameter </li>
 * </ul>
 * This key/value pairs may then be used by client expression, e.g. PsBox, LaTeXCircle,...
 * @author Sylvain Reynal
 * @since jpicedt 1.3
 * @version $Id: LaTeXPutExpression.java,v 1.13 2013/03/31 06:57:04 vincentb1 Exp $
 *
 */
public class LaTeXPutExpression extends SequenceExpression  {

	/** key entry in Pool containing the (x,y) parameter */
	public static final Pool.Key<PicPoint> KEY_PUT_POINT = new Pool.Key<PicPoint>("put-point");

	private Pool pool;

	public LaTeXPutExpression(Pool pl) {

		super(true);
		pool = pl;

		add(new LiteralExpression("\\put"));
		add(WHITE_SPACES_OR_EOL);
		// "(x,y)" => set current "putPoint":
		add(
		    new PicPointExpression("(", ",", ")") {
			    public void action(ParserEvent e) {
				    if (DEBUG) System.out.println(e);
				    PicPoint putPoint = ((PicPoint) e.getValue()).toMm(pool.get(LaTeXParser.KEY_UNIT_LENGTH));// global var
				    pool.put(KEY_PUT_POINT, putPoint);
			    }
		    });
		add(WHITE_SPACES_OR_EOL);

		// {stuff} :
		AlternateExpression putArgs = new AlternateExpression();
		putArgs.add(new LaTeXBox(pool));// \\makebox, \\framebox, \\frame, \\dashbox
		putArgs.add(new LaTeXCircle(pool));// \\circle{d} and \\circle*{d}
		putArgs.add(new LaTeXOval(pool));// \\oval(w,h)
		putArgs.add(new LaTeXLine(pool));// \\line(x,y){len} and \\vector
		// PsTricks commands wrapped in a \\put command: (need input.pstricks.PsBox !)
		putArgs.add(new PsBox(pool, PsBox.RECTANGLE_BOX));// \\psframebox
		putArgs.add(new PsBox(pool, PsBox.CIRCLE_BOX));// \\pscirclebox
		putArgs.add(new PsBox(pool, PsBox.OVAL_BOX));// \\psovalbox

		putArgs.add(new LRArgument()); // pure HR argument (must always be added last)
		SequenceExpression putArgsEOL = new SequenceExpression(WHITE_SPACES_OR_EOL, putArgs, false);
		add(new EnclosingExpression("{", putArgsEOL, "}"));

	}

	/** called when this SequenceExpression was successfully parsed -> reinit locals and Pool's keys for next time */
	public void action(ParserEvent e){
		if (DEBUG) System.out.println(e);
		pool.put(KEY_PUT_POINT, null);
	}

	public String toString(){
		return "[LaTeXPutExpression]";
	}




	/**
	 * handles content of "{stuff}" when no other expression matches, by instanciating a PicText
	 * with "stuff" as the PicText string
	 */
	class LRArgument extends RepeatExpression {

		StringBuffer lrArgument = new StringBuffer();

		LRArgument(){
			super(null,0,AT_LEAST);
			setPattern(new LRArgumentSwallower());
		}

		public void action(ParserEvent e){
			if (DEBUG) System.out.println(e);
			String s = Context.removeRedundantWhiteSpaces(lrArgument.toString());
			// set put point :
			PicPoint putPoint = (PicPoint)pool.get(KEY_PUT_POINT);
			if (putPoint==null) putPoint = new PicPoint(); // security, default to (0,0)
			pool.currentObj = new PicText(putPoint, s, pool.getAttributeSet(LaTeXParser.KEY_ATTRIBUTES));
			pool.currentGroup.add(pool.currentObj);
			lrArgument = new StringBuffer(); // reset for next time
		}

		/**
		 * swallow as many chars as possible and push them in "lrArgument" buffer, replacing
		 * CR by whitespaces, as TeX does.
		 */
		class LRArgumentSwallower extends WildCharExpression {

			public LRArgumentSwallower(){
				super(ANY_CHAR_EOL);
			}
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				Character cc = getCharacter();
				if (cc.charValue() == '\n') lrArgument.append(' ');
				else lrArgument.append(cc);
			}
		}
	}

}
