// PsBox.java --- -*- coding: iso-8859-1 -*-
// November 13, 2003 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PsBox.java,v 1.10 2013/03/31 06:55:09 vincentb1 Exp $
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
package jpicedt.format.input.pstricks;

import jpicedt.format.input.util.*;
import jpicedt.format.input.latex.LaTeXPutExpression;
import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicText.*;

/**
 * Parses PsTricks boxes, i.e. (depending on the "type" parameter of the constructor)
 * <ul>
 * <li> \\psframebox[param]{text}}
 * <li> \\psovalbox[param]{text}}
 * <li> \\pscirclebox[param]{text}}
 * </ul>
 * This expression is to be used as a child expression of either
 * {@link jpicedt.format.input.latex.LaTeXPutExpression#LaTeXPutExpression jpicedt.format.input.latex.LaTeXPutExpression} or
 * {@link PsRPutExpression PsRPutExpression}.
 * <p>
 * The parsing code makes use of the following Pool's key/value pairs (as set by the enclosing XXXPutExpression beforehands):
 * <ul>
 * <li> KEY_RPUT_VALIGN: \\rput's [bBt] vertical alignment parameter</li>
 * <li> KEY_RPUT_HALIGN: \\rput's [lr] horizontal alignment parameter </li>
 * <li> KEY_RPUT_POINT: \\rput's (x,y) KEY_RPUT_VALIGNparameter </li>
 * <li> KEY_RPUT_ROTATION: \\rput's {rotation} parameter </li>
 * </ul>
 * For each of these four keys, permitted values are the same as in PicAttributeSet. For instance, the value
 * associated with KEY_RPUT_VALIGN may be TEXT_VALIGN_BOTTOM, TEXT_VALIGN_BASELINE, ...
 * <p>
 * @author Sylvain Reynal
 * @since jpicedt 1.4pre2
 * @version $Id: PsBox.java,v 1.10 2013/03/31 06:55:09 vincentb1 Exp $
 */
public class PsBox extends SequenceExpression  {

	/** expects a \\psframebox macro */
	public final static String RECTANGLE_BOX = "\\psframebox";
	/** expects a \\pscirclebox macro */
	public final static String CIRCLE_BOX = "\\pscirclebox";
	/** expects a \\psovalbox macro */
	public final static String OVAL_BOX = "\\psovalbox";

	private String type; // e.g. \\psframebox
	private Pool pool;

	/**
	 * @param type RECTANGLE_BOX, CIRCLE_BOX or OVAL_BOX depending on the expected box type.
	 */
	public PsBox(Pool pl, String type){
		super(true); // throw IncompleteSequence Exception
		this.pool = pl;
		this.type = type;
		// command :
		this.add(new PSTInstanciationExpression(type, new PicText(),pool));
		this.add(new OptionalExpression(new StarExpression(pool))); // "*"
		this.add(WHITE_SPACES_OR_EOL);
		// optional [param]
		this.add(new OptionalExpression(new EnclosingExpression("[",new PSTParametersExpression(pool,Pool.CURRENT_OBJ_ATTRIBUTES),"]"))); // push in object's attributeSet
		this.add(WHITE_SPACES_OR_EOL);
		// {text}
		this.add(new EnclosedText()); // set PicText's text
	}

	public void action(ParserEvent e){
		if (DEBUG) System.out.println(e);
		// set put point:
		PicPoint putPoint = pool.get(PsRPutExpression.KEY_RPUT_POINT);
		if (putPoint==null) putPoint = pool.get(LaTeXPutExpression.KEY_PUT_POINT);
		if (putPoint==null) putPoint = new PicPoint();
		((PicText)(pool.currentObj)).setCtrlPt(PicText.P_ANCHOR,putPoint,null);
		// handle alignment:
		HorAlign horAlign = pool.get(PsRPutExpression.KEY_RPUT_HALIGN);
		VertAlign vertAlign = pool.get(PsRPutExpression.KEY_RPUT_VALIGN);
		if (horAlign!=null) pool.currentObj.setAttribute(TEXT_HOR_ALIGN, horAlign);
		if (vertAlign!=null) pool.currentObj.setAttribute(TEXT_VERT_ALIGN, vertAlign);
		// set box type:
		if (type==RECTANGLE_BOX) ((PicText)(pool.currentObj)).setFrameType(FrameStyle.RECTANGLE);
		else if (type==CIRCLE_BOX) ((PicText)(pool.currentObj)).setFrameType(FrameStyle.CIRCLE);
		if (type==OVAL_BOX) ((PicText)(pool.currentObj)).setFrameType(FrameStyle.OVAL);
		// rotation:
		Double rotation = (Double)pool.get(PsRPutExpression.KEY_RPUT_ROTATION);
		if (rotation!=null) pool.currentObj.setAttribute(TEXT_ROTATION, rotation);
	}

	/**
	 * handles {text} content (for PsBox) by setting PicText's text content, replacing
	 * linefeeds by whitespaces beforehands (as TeX does...)
	 */
	class EnclosedText extends EnclosingExpression {

		public EnclosedText(){
			super("{", null, "}");
		}
		public void action(ParserEvent e){
			if (DEBUG) System.out.println(e);
			String txt = getEnclosedString().replace('\n',' ');
			txt = Context.removeRedundantWhiteSpaces(txt);
			((PicText)(pool.currentObj)).setText(txt);
		}
	}
}
