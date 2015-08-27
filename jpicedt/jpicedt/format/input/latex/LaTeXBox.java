// LaTeXBox.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: LaTeXBox.java,v 1.12 2013/10/07 19:16:39 vincentb1 Exp $
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
import static jpicedt.graphic.model.PicText.*;

/**
 * Parses LaTeX boxes, i.e. makebox, framebox, frame and dashbox. This is to be used as a child expression of either
 * {@link LaTeXPutExpression LaTeXPutExpression} or
 * {@link jpicedt.format.input.pstricks.PsRPutExpression#PsRPutExpression jpicedt.format.input.pstricks.PsRPutExpression}.
 * <p>
 * Instanciates a PicText element. Argument =<ul>
 * <li> \\makebox(w,h)[vh]{text or LaTeX command}</li>
 * <li> \\framebox(w,h)[vh]{text or LaTeX command}</li>
 * <li> \\dashbox{dash}(w,h)[vh]{text or LaTeX command}</li>
 * </ul>
 * <p> The parsing code uses several keys/values pairs stored by in the Pool:
 * <ul>
 * <li> KEY_PUT_POINT : PicPoint's argument of the \put or \rput command
 * </li></ul>
 * <p>
 * <code>interpret()</code> return false if none of these have been found, and cursor position is left
 * unchanged<br>
 * @author Sylvain Reynal
 * @since jpicedt 1.4
 * @version $Id: LaTeXBox.java,v 1.12 2013/10/07 19:16:39 vincentb1 Exp $
 *
 */
public class LaTeXBox extends SequenceExpression  {

	private double boxWidth, boxHeight, boxDash;
	private boolean isFixedSizeBox=false;
	private HorAlign horAlign;
	private VertAlign vertAlign; // set by TextAlignment
	private Pool pool;


	public LaTeXBox(Pool pl) {

		super(true);// possibly throws IncompleteSequence Exception if only \\XXXXbox was found
		this.pool = pl;

		// boxtype :
		AlternateExpression boxType = new AlternateExpression();

		// \\makebox
		PicAttributeSet dummy = new PicAttributeSet();
		boxType.add(new LaTeXInstanciationExpression("\\makebox",new PicText(new PicPoint(), "", dummy),pool));

		// \\framebox
		dummy.setAttribute(TEXT_FRAME, FrameStyle.RECTANGLE);
		boxType.add(new LaTeXInstanciationExpression("\\framebox",new PicText(new PicPoint(), "", dummy),pool));

		// \\frame (must appear AFTER framebox !!!)
		boxType.add(new LaTeXInstanciationExpression("\\frame",new PicText(new PicPoint(), "", dummy),pool));

		// \\dashbox{dash}
		boxType.add(new SequenceExpression(
		                new LaTeXInstanciationExpression("\\dashbox",new PicText(new PicPoint(), "", dummy),pool),
		                WHITE_SPACES_OR_EOL,
		                new SequenceExpression(
		                    new LiteralExpression("{"), // {dash}
		                    new NumericalExpression(DOUBLE, POSITIVE, "}", true) {
			                    public void action(ParserEvent e) {
				                    if (DEBUG) System.out.println(e);
				                    boxDash = ((Double) e.getValue()).doubleValue() * pool.get(LaTeXParser.KEY_UNIT_LENGTH);
				                    pool.currentObj.setAttribute(DASH_OPAQUE, new Double(boxDash));
				                    pool.currentObj.setAttribute(DASH_TRANSPARENT, new Double(boxDash));
			                    }
		                    },
		                    true),
		                true));// possibly throws IncompleteSequence Exception once "{" was found
		this.add(boxType);
		this.add(WHITE_SPACES_OR_EOL);
		// (w,h) (optional ; if found, we instanciate a PicRectangle and make the previously instanciated
		//        PicText frameless)
		this.add(new OptionalExpression(new PicPointExpression("(", ",", ")"){
			                                public void action(ParserEvent e){
				                                if (DEBUG) System.out.println(e);
				                                isFixedSizeBox = true; // flag to inform the EnclosingExpression - see below - it must instanciate a PicRectangle
				                                PicPoint pt = (PicPoint)e.getValue();
				                                boxWidth = pt.x; boxHeight = pt.y;
			                                }}));
		this.add(WHITE_SPACES_OR_EOL);
		// optional alignment : [vh] where v = b|t|c|"" and h = r|l|c|"" ("" default to center)
		SequenceExpression alignmentExp = new SequenceExpression(true);// throw IncompleteSequence Exception
		alignmentExp.add(new LiteralExpression("["));
		alignmentExp.add(new TextAlignment()); // swallows chars till "]" is found
		alignmentExp.add(WHITE_SPACES_OR_EOL);
		this.add(new OptionalExpression(alignmentExp)); // set horAlign and vertAlign
		// text or LaTeX Command
		this.add(WHITE_SPACES_OR_EOL);
		this.add(new BoxEnclosedText()); // instanciate a new PicText, and sets its attributes from global vars
	}

	/** called when this SequenceExpression was successfully parsed -> reinit locals for next time */
	public void action(ParserEvent e){
		if (DEBUG) System.out.println(e);
		boxWidth = boxHeight = boxDash = 0;
		isFixedSizeBox=false;
		horAlign=HorAlign.CENTER;
		vertAlign=VertAlign.CENTER;
	}


	/**
	 * Handles text inside a box-command (e.g. \\makebox{text}), after replacing CR by white-spaces as TeX does.
	 * This is where a new PicText is added to the current stack
	 */
	class BoxEnclosedText extends EnclosingExpression {

		public BoxEnclosedText(){
			super("{", null, "}");
		}

		public void action(ParserEvent e) {
			if (DEBUG) System.out.println(e);
			// instanciate PicText:
			PicText text = ((PicText)pool.currentObj);
			// fill string:
			String s = getEnclosedString().replace('\n',' ');
			s = Context.removeRedundantWhiteSpaces(s);
			text.setText(s);
			// set alignment: (note that for makebox and the like, \\rput's alignment parameters are meaningless)
			text.setVertAlign(vertAlign);
			text.setHorAlign(horAlign);
			// set put point:
			PicPoint putPoint = (PicPoint)pool.get(PsRPutExpression.KEY_RPUT_POINT);
			if (putPoint==null) putPoint = (PicPoint)pool.get(LaTeXPutExpression.KEY_PUT_POINT);
			if (putPoint==null) putPoint = new PicPoint(); // default to (0,0)
			// set rotation if applicable (i.e. called from PsRPutExpression):
			// rotation:
			Double rotation = (Double)pool.get(PsRPutExpression.KEY_RPUT_ROTATION);
			if (rotation!=null) text.setAttribute(TEXT_ROTATION, rotation);
			// set box size, if applicable:
			if (isFixedSizeBox){
				// change text location  :
				PicPoint ptText = new PicPoint();
				switch (vertAlign){
				case TOP:
					ptText.y = putPoint.y + boxHeight;
					break;
				case BOTTOM:
					ptText.y = putPoint.y;
					break;
				default:
					ptText.y = putPoint.y + 0.5*boxHeight; // CENTER_V
				}
				switch (horAlign){
				case LEFT:
					ptText.x = putPoint.x;
					break;
				case RIGHT:
					ptText.x = putPoint.x + boxWidth;
					break;
				default:
					ptText.x = putPoint.x + 0.5*boxWidth; // CENTER_H
				}
				text.setCtrlPt(PicText.P_ANCHOR, ptText,null);
				// possibly add a box on its own, set its size and location :
				if (text.getFrameType() == FrameStyle.RECTANGLE){
					// suppress box from PicText
					text.setFrameType(FrameStyle.NO_FRAME);
					PicPoint pt2 = new PicPoint(putPoint.x + boxWidth, putPoint.y + boxHeight);
					// copy attribute set from text to box (dash, ...)
					PicParallelogram box = new PicParallelogram(putPoint,pt2);
					box.setAttributeSet(text.getAttributeSet());
					pool.currentGroup.add(box);
					pool.currentObj = box; // only for security reason... (probably useless)
				}
				isFixedSizeBox = false;	// reset flag for next time !
			}
			else {
				text.setCtrlPt(PicText.P_ANCHOR, putPoint,null);
			}
		}
	}


	/**
	 * handles boxes alignement, e.g. "bc]" ,... First "[" isn't parsed here.
	 * Modifies global vars "vertAlign" and "horAlign".
	 */
	class TextAlignment extends WordExpression {

		public TextAlignment(){
			super("]", true); // swallows "]" and can return an empty String as in "[]"
		}

		public void action(ParserEvent e) {
			if (DEBUG) System.out.println(e);
			String s = (String) e.getValue();

			if (s.indexOf("t")!=-1) vertAlign = VertAlign.TOP;
			else if (s.indexOf("b")!=-1) vertAlign = VertAlign.BOTTOM;
			else vertAlign = VertAlign.CENTER;

			if (s.indexOf("l")!=-1) horAlign = HorAlign.LEFT;
			else if (s.indexOf("r")!=-1) horAlign = HorAlign.RIGHT;
			else horAlign = HorAlign.CENTER;
		}
	}

}
