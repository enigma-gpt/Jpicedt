// UserDefinedColorExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: UserDefinedColorExpression.java,v 1.8 2013/03/31 06:54:49 vincentb1 Exp $
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

import java.awt.*;
import java.awt.color.*;
import java.util.*;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Parse statement defining user-colours, that is :<ul>
 * <li> \\newgray{user-def colour name}{num} (where num is a gray scale specification b/w 0 and 1)<br>
 * <li> \\newrgbcolor{user-def-colour-name}{num1 num2 num3} (where num are numbers b/w 0 and 1) <br>
 * <li> \\newhsbcolor{user-def-colour-name}{num1 num2 num3} (where num are numbers b/w 0 and 1) <br>
 * <li> \\newcmykcolor{user-def-colour-name}{num1 num2 num3 num4} (where num are numbers b/w 0 and 1)<br>
 * </ul>
 * Colour names may contain letters and numbers. Spaces are NOT allowed, except as separators b/w colour
 * numbers. New colours
 * are then added to the pool's hashtable associated with key = PstricksParser.KEY_USER_COLOURS.<br>
 * @author Sylvain Reynal
 * @version $Id: UserDefinedColorExpression.java,v 1.8 2013/03/31 06:54:49 vincentb1 Exp $
 */
public class UserDefinedColorExpression extends AlternateExpression {

	private Pool pool;
	private String newColourName;
	private float num1, num2, num3, num4;


	/**
	 * Constructor for the UserDefinedColorExpression object
	 *
	 */
	public UserDefinedColorExpression(Pool pl) {

		this.pool = pl;
		// ================ \\newgray ===============================
		SequenceExpression newGray = new SequenceExpression(true);
		newGray.add(new LiteralExpression("\\newgray{"));
		newGray.add(
			new WordExpression("}", true) {// postfix="}", swallowed
				public void action(ParserEvent e) {
					if (DEBUG)
						System.out.println(e);
					// set new gray name
					newColourName = (String)(e.getValue());
				}
			});
		newGray.add(new LiteralExpression("{"));
		newGray.add(
			new NumericalExpression(DOUBLE, POSITIVE, "}", true) {// postfix="}", swallows it
				public void action(ParserEvent e) throws REParserException {
					if (DEBUG)
						System.out.println(e);
					// add new gray name with its value to the User-defined colours hashtable
					float val = ((Number)e.getValue()).floatValue();
					if (val > 1.0) throw new REParserException.NumberFormat(e.getContext(), this);
					HashMap<String,Color> userColoursMap = pool.get(PstricksParser.KEY_USER_COLOURS);
					userColoursMap.put(newColourName, new Color(val, val, val));// this is a gray-level !
				}
			});
		add(newGray);

		// ================ \\newrgbcolor ===============================
		SequenceExpression newRgb = new SequenceExpression(true);
		newRgb.add(new LiteralExpression("\\newrgbcolor{"));
		newRgb.add(
			new WordExpression("}", true) {// postfix="}", swallowed
				public void action(ParserEvent e) {
					if (DEBUG) System.out.println(e);
					newColourName = (String)(e.getValue());
				}
			});
		newRgb.add(new LiteralExpression("{"));
		// num1 (red)
		newRgb.add(
			new NumericalExpression(DOUBLE, POSITIVE, " ", true) {// postfix=" ", swallows it
				public void action(ParserEvent e) throws REParserException {
					if (DEBUG) System.out.println(e);
					num1 = ((Number)e.getValue()).floatValue();
					if (num1 > 1.0) throw new REParserException.NumberFormat(e.getContext(), this);
				}
			});
		// num2 (green)
		newRgb.add(
			new NumericalExpression(DOUBLE, POSITIVE, " ", true) {// postfix=" ", swallows it
				public void action(ParserEvent e) throws REParserException {
					if (DEBUG) System.out.println(e);
					num2 = ((Number)e.getValue()).floatValue();
					if (num2 > 1.0) throw new REParserException.NumberFormat(e.getContext(), this);
				}
			});
		// num3 (blue)
		newRgb.add(
			new NumericalExpression(DOUBLE, POSITIVE, "}", true) {// postfix="}", swallows it
				public void action(ParserEvent e) throws REParserException {
					if (DEBUG) System.out.println(e);
					num3 = ((Number)e.getValue()).floatValue();
					if (num3 > 1.0) throw new REParserException.NumberFormat(e.getContext(), this);
					HashMap<String,Color> userColoursMap = pool.get(PstricksParser.KEY_USER_COLOURS);
					userColoursMap.put(newColourName, new Color(num1, num2, num3));
				}
			});
		add(newRgb);

		// ================ \\newhsbcolor ===============================
		SequenceExpression newHsb = new SequenceExpression(true);
		newHsb.add(new LiteralExpression("\\newhsbcolor{"));
		newHsb.add(
			new WordExpression("}", true) {// postfix="}", swallowed
				public void action(ParserEvent e) {
					if (DEBUG)
						System.out.println(e);
					newColourName = (String)(e.getValue());
				}
			});
		newHsb.add(new LiteralExpression("{"));
		// num1 (hue)
		newHsb.add(
			new NumericalExpression(DOUBLE, POSITIVE, " ", true) {// postfix=" ", swallows it
				public void action(ParserEvent e) {
					if (DEBUG) System.out.println(e);
					num1 = ((Number)e.getValue()).floatValue();// any positive number
				}
			});
		// num2 (saturation)
		newHsb.add(
			new NumericalExpression(DOUBLE, POSITIVE, " ", true) {// postfix=" ", swallows it
				public void action(ParserEvent e) throws REParserException {
					if (DEBUG) System.out.println(e);
					num2 = ((Number)e.getValue()).floatValue();
					if (num2 > 1.0) throw new REParserException.NumberFormat(e.getContext(), this);
				}
			});
		// num3 (brightness)
		newHsb.add(
			new NumericalExpression(DOUBLE, POSITIVE, "}", true) {// postfix="}", swallows it
				public void action(ParserEvent e) throws REParserException {
					if (DEBUG) System.out.println(e);
					num3 = ((Number)e.getValue()).floatValue();
					if (num3 > 1.0) throw new REParserException.NumberFormat(e.getContext(), this);
					System.out.println("HSB->RGB = " + Color.getHSBColor(num1, num2, num3));
					HashMap<String,Color> userColoursMap = pool.get(PstricksParser.KEY_USER_COLOURS);
					userColoursMap.put(newColourName, Color.getHSBColor(num1, num2, num3));
				}
			});
		add(newHsb);

		// ================ \\newcmykcolor ===============================
		// [pending] BUGGY !!! On Linux platforms, raises an IllegalArgumentException : Unknown color space ... and I don't know why...
		SequenceExpression newCmyk = new SequenceExpression(true);
		newCmyk.add(new LiteralExpression("\\newcmykcolor{"));
		newCmyk.add(
			new WordExpression("}", true) {// postfix="}", swallowed
				public void action(ParserEvent e) {
					if (DEBUG) System.out.println(e);
					newColourName = (String)(e.getValue());
				}
			});
		newCmyk.add(new LiteralExpression("{"));
		// num1 (Cyan)
		newCmyk.add(
			new NumericalExpression(DOUBLE, POSITIVE, " ", true) {// postfix=" ", swallows it
				public void action(ParserEvent e) throws REParserException {
					if (DEBUG) System.out.println(e);
					num1 = ((Number)e.getValue()).floatValue();
					if (num1 > 1.0) throw new REParserException.NumberFormat(e.getContext(), this);
				}
			});
		// num2 (Magenta)
		newCmyk.add(
			new NumericalExpression(DOUBLE, POSITIVE, " ", true) {// postfix=" ", swallows it
				public void action(ParserEvent e) throws REParserException {
					if (DEBUG) System.out.println(e);
					num2 = ((Number)e.getValue()).floatValue();
					if (num2 > 1.0) throw new REParserException.NumberFormat(e.getContext(), this);
				}
			});
		// num3 (Yellow)
		newCmyk.add(
			new NumericalExpression(DOUBLE, POSITIVE, " ", true) {// postfix=" ", swallows it
				public void action(ParserEvent e) throws REParserException {
					if (DEBUG) System.out.println(e);
					num3 = ((Number)e.getValue()).floatValue();
					if (num3 > 1.0) throw new REParserException.NumberFormat(e.getContext(), this);
				}
			});
		// num4 (Black)
		newCmyk.add(
			new NumericalExpression(DOUBLE, POSITIVE, "}", true) {// postfix="}", swallows it
				public void action(ParserEvent e) throws REParserException {
					if (DEBUG) System.out.println(e);
					num4 = ((Number)e.getValue()).floatValue();
					if (num4 > 1.0) throw new REParserException.NumberFormat(e.getContext(), this);
					float[] components = new float[4];
					components[0] = num1;
					components[1] = num2;
					components[2] = num3;
					components[3] = num4;
					HashMap<String,Color> userColoursMap = pool.get(PstricksParser.KEY_USER_COLOURS);
					userColoursMap.put(newColourName, new Color(ColorSpace.getInstance(ColorSpace.TYPE_CMYK), components, 1.0f));
				}
			});
		add(newCmyk);
	}

	public String toString(){
		return "[UserDefinedColorExpression]";
	}

}
