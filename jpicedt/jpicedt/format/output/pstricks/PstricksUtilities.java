/*
 PstricksUtilities.java - March 1, 2002 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Département de Physique
 École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org/

*/
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

package jpicedt.format.output.pstricks;

import jpicedt.graphic.model.Element;

import java.awt.Color;
import java.util.StringTokenizer;
import java.util.BitSet;

import jpicedt.format.output.util.ColorFormatter;
import static jpicedt.format.output.pstricks.PstricksConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.view.ArrowView.Direction;

/**
 * Collection of static methods for the PsTricks format
 * @author Sylvain Reynal
 * @version $Id: PstricksUtilities.java,v 1.12 2013/03/27 07:09:00 vincentb1 Exp $
 * @since jPicEdt 1.3.1
 */
public class PstricksUtilities  {

	/**
	 * Returns PsTricks's colour name from the given Color object;
	 * a null String if none matches (in which case the caller might define a new colour by using a \\newrgbcolor or a \\newgray command...)
	 */
	public static String getPsTricksColorName(Color c,PstricksFormatter factory){
		return ColorFormatter.format(c,factory.getCustomProperties().getFormatterPredefinedColorSet());
	}

	/**
	 * Returns a Color object corresponding to the given PsTricks's colour
	 * name ; a null Color if none matches (in which case the caller might
	 * look up a table of user-defined colours to see if one matches)
	 */
	public static Color getPsTricksColor(String name){
		BitSet b = new BitSet();
		b.set(ColorFormatter.ColorEncoding.PSTRICKS.value());
		return ColorFormatter.parse(name, b);
	}


	public static String toPstricksString(ArrowStyle arrow, Direction dir){
		for (PSTArrow aa: PSTArrow.values()){
			if (aa.getArrowStyle() == arrow){
				return aa.getString(dir);
			}
		}
		return null;
	}

	/**
	 * @return PsTricks's arrow string for the given Element (e.g. "{&lt;-&gt;}") with parenthesis included.
	 */
	public static StringBuffer createPstricksStringFromArrows(Element obj){

		StringBuffer buf = new StringBuffer(5);
		ArrowStyle leftArrow = obj.getAttribute(LEFT_ARROW);
		ArrowStyle rightArrow = obj.getAttribute(RIGHT_ARROW);
		if (leftArrow == ArrowStyle.NONE && rightArrow == ArrowStyle.NONE) return buf; // no arrow => empty string !
		buf.append("{");
		buf.append(toPstricksString(leftArrow,Direction.LEFT)); // e.g. "<" for ARROW_HEAD
		buf.append("-");
		buf.append(toPstricksString(rightArrow,Direction.RIGHT)); // e.g. ">" for ARROW_HEAD (this way, we really get "<->" instead of ">->" which was wrong !)
		buf.append("}");
		return buf;
	}


	/**
	 * @return an array containing two Arrows corresponding to the given PsTricks string
	 * (e.g. "-" or "<-" or ">>->" ...), left arrow first.
	 */
	public static ArrowStyle[] createArrowsFromPstricksString(String str){

		ArrowStyle[] tab = new ArrowStyle[2];
		tab[0] = tab[1] = ArrowStyle.NONE; // security
		StringTokenizer tokenizer = new StringTokenizer(str, "-");
		String tokenLeft="";
		String tokenRight="";
		tab[0] = tab[1] = ArrowStyle.NONE; // security
		if (tokenizer.hasMoreTokens()){ // this is not a "-" string !
			if (str.startsWith("-")) { // e.g. "->"
				tokenLeft = "";
				tokenRight = tokenizer.nextToken();
			}
			else { // e.g. "<-" or "<->"
				tokenLeft = tokenizer.nextToken();
				if (tokenizer.hasMoreTokens()){ // e.g. "<->"
					tokenRight = tokenizer.nextToken();
				}
				else tokenRight = "";
			}
		}

		for (PSTArrow aa: PSTArrow.values()){
			if (tokenLeft.equals(aa.getString(Direction.LEFT))) {
				tab[0]=aa.getArrowStyle();
				break;
			}
		}
		for (PSTArrow aa: PSTArrow.values()) {
			if (tokenRight.equals(aa.getString(Direction.RIGHT))) {
				tab[1]=aa.getArrowStyle();
				break;
			}
		}
		return tab;
	}


	/**
	 * Test
	 */
	public static void main(String arg[]){

		//	System.out.println("arg=" + arg[0]);
		ArrowStyle[] arrows;
		String str = "<->";
		while(true){
			try {
				System.out.println("Input string ?");
				java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
				str = r.readLine();
				System.out.println("OK !");
			}
			catch(Exception e){e.printStackTrace();}

			arrows = createArrowsFromPstricksString(str);
			System.out.println("Left Arrow = (" + arrows[0] + ")");
			System.out.println("Right Arrow = (" + arrows[1] + ")");
		}
	}





}
