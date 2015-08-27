// PsRPutExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PsRPutExpression.java,v 1.13 2013/03/31 06:53:44 vincentb1 Exp $
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
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.model.PicText;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicText.*;

/**
 * Parses PsTricks's \\rput commands, either:
 * <ul>
 * <li> \\rput[refpoint]{rotation}(x,y){stuff} </li>
 * <li> \\rput*[refpoint]{rotation}(x,y){stuff} </li>
 * </ul>
 * (line-breaks being allowed b/w each "block"), where : <br>
 * <ul>
 * <li>refpoint = B|b|t for vertical and l|r for horizontal (B = baseline), used only for psframebox and rel.
 * <li>rotation = any angle in degree or U,L,D,R,N,W,S or E.
 * <li>stuff = whatever ! (this allows in particular to rotate things)
 * </ul>
 * Depending on the "stuff" found, instanciates :<ul>
 * <li> PicText : \\psframebox, \\pscirclebox, \\psovalbox, LR-argument, pure LaTeX commands (e.g. \\circle, \\vector,...)
 * <li> [SR:pending] instanciate other elements + handle rotations.
 * </ul>
 * The parsing code sets the following Pool's key/value pairs (as set by the enclosing XXXPutExpression beforehands):
 * <ul>
 * <li> KEY_RPUT_VALIGN: \\rput's [bBt] vertical alignment parameter</li>
 * <li> KEY_RPUT_HALIGN: \\rput's [lr] horizontal alignment parameter </li>
 * <li> KEY_RPUT_POINT: \\rput's (x,y) parameter </li>
 * <li> KEY_RPUT_ROTATION: \\rput's {rotation} parameter </li>
 * </ul>
 * These key/value pairs may then be used by client expression, e.g. PsBox, ...
 * @author Sylvain Reynal
 * @since jpicedt 1.3
 * @version $Id: PsRPutExpression.java,v 1.13 2013/03/31 06:53:44 vincentb1 Exp $
 */
public class PsRPutExpression extends SequenceExpression  {

	private Pool pool;

	/** key entry in Pool containing the (x,y) parameter */
	public static final Pool.Key<VertAlign> KEY_RPUT_VALIGN = new Pool.Key<VertAlign>("rput-valign"); // used by psframebox and rel. only !
	/** key entry in Pool containing the horizontal alignment parameter */
	public static final Pool.Key<HorAlign> KEY_RPUT_HALIGN = new Pool.Key<HorAlign>("rput-halign"); // i.e. a psline for instance won't use it at all
	/** key entry in Pool containing the (x,y) parameter */
	public static final Pool.Key<PicPoint> KEY_RPUT_POINT = new Pool.Key<PicPoint>("rput-point");
	/** key entry in Pool containing the (x,y) parameter */
	public static final Pool.Key<Double> KEY_RPUT_ROTATION = new Pool.Key<Double>("rput-rotation");


	public PsRPutExpression(Pool pl){

		super(true); // throw exception if stop before end
		pool = pl;

		add(new LiteralExpression("\\rput")); // instanciation is done later on, depending on stuff found
		add(new OptionalExpression(new LiteralExpression("*"))); // not used so far
		add(WHITE_SPACES_OR_EOL);

		// refpoint -> optional alignment : "[vh]" where v = b|t|B|"" and h = r|l|"" ("" default to center)
		// modifes : this.horAlign and this.vertAlign
		SequenceExpression alignmentExp = new SequenceExpression(true); // throw IncompleteSequence Exception
		alignmentExp.add(new LiteralExpression("["));
		alignmentExp.add(new TextAlignment()); // swallows "]" and can return an empty String as in "[]"
		alignmentExp.add(WHITE_SPACES_OR_EOL);
		add(new OptionalExpression(alignmentExp));

		// rotation : "{angle}"
		SequenceExpression angleExp = new SequenceExpression(true); // throw IncompleteSequence Exception
		angleExp.add(new LiteralExpression("{"));
		angleExp.add(new WordExpression("}", true){ // swallow "}"
			             public void action(ParserEvent e){
				             if (DEBUG) System.out.println(e);
				             String s = (String)e.getValue();
					     double rotation=0;
				             try {
					             rotation = Double.parseDouble(s);
						     pool.put(KEY_RPUT_ROTATION, new Double(rotation));
					             return;
				             } catch (NumberFormatException nex){}
				             if (s.equals("U")) rotation = 0;
				             else if (s.equals("L")) rotation = 90;
				             else if (s.equals("R")) rotation = -90;
				             else if (s.equals("D")) rotation = 180;
					     pool.put(KEY_RPUT_ROTATION, new Double(rotation));
				             // [pending] other shortcuts
			             }});
		angleExp.add(WHITE_SPACES_OR_EOL);
		add(new OptionalExpression(angleExp));

		// "(x,y)" :
		add(new PicPointExpression("(",",",")"){
			    public void action(ParserEvent e){
				    if (DEBUG) System.out.println(e);
				    // same as LatexPutExpression except that we use pstXunit...
				    PicPoint putPoint = ((PicPoint)e.getValue()).toMm(pool.get(PstricksParser.KEY_X_UNIT),pool.get(PstricksParser.KEY_Y_UNIT)); // global var
					pool.put(KEY_RPUT_POINT, putPoint);
			    }});
		add(WHITE_SPACES_OR_EOL);

		// {stuff} :
		AlternateExpression rputArgs = new AlternateExpression();
		rputArgs.add(new PsBox(pl,PsBox.RECTANGLE_BOX)); // \\psframebox
		rputArgs.add(new PsBox(pl,PsBox.CIRCLE_BOX)); // \\pscirclebox
		rputArgs.add(new PsBox(pl,PsBox.OVAL_BOX)); // \\psovalbox
		rputArgs.add(new LRArgument()); // pure LR argument (must always be added last)
		SequenceExpression rputArgsEOL = new SequenceExpression(WHITE_SPACES_OR_EOL,rputArgs,false);
		add(new EnclosingExpression("{", rputArgsEOL, "}"));

	}

	public String toString(){
		return "[PsRPutExpression]";
	}

	/** called when this SequenceExpression was successfully parsed -> reinit locals for next time */
	public void action(ParserEvent e){
		if (DEBUG) System.out.println(e);
		// reset RPut related keys in the pool:
		pool.put(KEY_RPUT_VALIGN,VertAlign.CENTER);
		pool.put(KEY_RPUT_HALIGN,HorAlign.CENTER);
		pool.put(KEY_RPUT_POINT,null);
		pool.put(KEY_RPUT_ROTATION,new Double(0.0));
	}

	/**
	 * Parses the alignment string for \\rput. Meaningfull only if stuff is an HR-argument, or a box.
	 * Otherwise I noticed it'd no effect in PsTricks. [pending] am I right ?
	 * Note : order is non-significant here, i.e. [hv] or [vh]
	 * <p>
	 */
	class TextAlignment extends WordExpression {

		public TextAlignment(){
			super("]",true);
		}

		public void action(ParserEvent e){
			if (DEBUG) System.out.println(e);
			String s = (String)e.getValue();
			VertAlign vertAlign;
			if (s.indexOf("t")!=-1) vertAlign = VertAlign.TOP;
			else if (s.indexOf("b")!=-1) vertAlign = VertAlign.BOTTOM;
			else if (s.indexOf("B")!=-1) vertAlign = VertAlign.BASELINE;
			else vertAlign = VertAlign.CENTER;
			pool.put(KEY_RPUT_VALIGN, vertAlign);

			HorAlign horAlign;
			if (s.indexOf("l")!=-1) horAlign = HorAlign.LEFT;
			else if (s.indexOf("r")!=-1) horAlign = HorAlign.RIGHT;
			else horAlign =HorAlign.CENTER;
			pool.put(KEY_RPUT_HALIGN, horAlign);
		}
	}

	/////////////////////////////////////////// LR //////////////////////////////////

	/**
	 * handles content of "{stuff}" when no other expression matches, by instanciating a PicText
	 * with "stuff" as the PicText string
	 * Uses : this.horAlign, this.vertAlign, this.lrArgument.
	 */
	class LRArgument extends RepeatExpression {

		StringBuffer lrArgument = new StringBuffer();

		LRArgument(){
			super(null,0,AT_LEAST);
			setPattern(new LRArgumentSwallower());
		}

		public void action(ParserEvent e){
			if (DEBUG) System.out.println(e);
			PicPoint putPoint = pool.get(PsRPutExpression.KEY_RPUT_POINT);
			if (putPoint==null) putPoint = new PicPoint();
			pool.currentObj = new PicText(putPoint,  Context.removeRedundantWhiteSpaces(lrArgument.toString()), pool.getAttributeSet(PstricksParser.KEY_ATTRIBUTES));
			// handle alignment:
			HorAlign horAlign = pool.get(KEY_RPUT_HALIGN);
			VertAlign vertAlign = pool.get(KEY_RPUT_VALIGN);
			if (horAlign!=null) pool.currentObj.setAttribute(TEXT_HOR_ALIGN, horAlign);
			if (vertAlign!=null) pool.currentObj.setAttribute(TEXT_VERT_ALIGN, vertAlign);
			// rotation:
			Double rotation = pool.get(KEY_RPUT_ROTATION);
			if (rotation!=null) pool.currentObj.setAttribute(TEXT_ROTATION, rotation);
			// add to current group:
			pool.currentGroup.add(pool.currentObj);
			lrArgument = new StringBuffer(); // reset for next call
		}

		/**
		 * swallow as many chars as possible and push them in "lrArgument" buffer,
		 * replacing CR by whitespaces, as TeX does.
		 */
		class LRArgumentSwallower extends WildCharExpression {

			public LRArgumentSwallower(){
				super(ANY_CHAR_EOL);
			}
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				Character cc = getCharacter();
				if (cc.charValue() != '\n') lrArgument.append(cc);
				else lrArgument.append(' ');
			}
		}
	}


}
