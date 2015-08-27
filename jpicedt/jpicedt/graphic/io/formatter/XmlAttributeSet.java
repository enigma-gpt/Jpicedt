// XmlAttributeSet.java --- -*- coding: iso-8859-1 -*-
// January 9, 2006 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 19992/2006 Sylvain Reynal
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
// Version: $Id: XmlAttributeSet.java,v 1.7 2013/03/27 07:04:29 vincentb1 Exp $
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
package jpicedt.graphic.io.formatter;

import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.awt.Color;

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PEToolKit;

import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.model.PicAttributeName;
import jpicedt.graphic.model.PicCircleFrom3Points;
import jpicedt.graphic.model.PicEllipse;
import jpicedt.graphic.model.PicGroup;
import jpicedt.graphic.model.PicMultiCurve;
import jpicedt.graphic.model.PicParallelogram;
import jpicedt.graphic.model.PicSmoothPolygon;
import jpicedt.graphic.model.PicPsCurve;
import jpicedt.graphic.model.PicText;
import jpicedt.graphic.model.StyleConstants;

import static jpicedt.graphic.io.formatter.JPICConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.StyleConstants.FillStyle.*;
import static jpicedt.graphic.model.StyleConstants.LineStyle.*;
import static jpicedt.graphic.model.StyleConstants.ArrowStyle.*;


/**
 * A map that contains XML-like name=value pairs, to be ultimately formatted as XML.
 * With respect to jpicedt.graphic.element.AttributeSet, this is more XML-oriented, and
 * not all attributes contained in AttributeSet are included in this map so as to reduce
 * the size of the ultimate StringBuffer.
 * In comparison with the older scheme, where we would directly append
 * succesive XML-formatted name=value pairs to the main StringBuffer, this offers the benefit
 * of avoiding redundant definition of XML parameters, which XML strictly forbids.
 * @since jpicedt 1.4.pre5
 * @author Sylvain Reynal
 */
public class XmlAttributeSet extends HashMap<String,String>  {

	private final PicAttributeSet defaultAttributes = new PicAttributeSet(); // for comparison with default values

	/**
	 * Constructs an empty map
	 */
	public XmlAttributeSet(){
		super();
	}

	/**
	 * Returns a buffer constructed by iterating over the entries in this
	 *  and appending a name="value" string to the buffer for each attribute.
	 */
	protected StringBuffer toXML(){
		StringBuffer buf = new StringBuffer();
		Set<Map.Entry<String,String>> entries = entrySet();
		String str;
		for (Iterator<Map.Entry<String,String>> it = entries.iterator(); it.hasNext();){
			Map.Entry<String,String> entry = it.next();
			buf.append(' ');
			buf.append(entry.getKey()); // param
			buf.append('=');
			//buf.append(' ');
			str = entry.getValue().toString();
			if (!str.startsWith("\"")){
				buf.append('\"');
				buf.append(entry.getValue().toString()); // value
				buf.append('\"');
			}
			else
				buf.append(entry.getValue().toString()); // value
			buf.append(CR_LF_TAB);
		}
		return buf;
	}

	/**
	 * Fills the given map with XML-like entries (name->value will translate into name="value" once text formatted)
	 * for common attributes of the given Element, that is,
	 * attributes specific to a given subclass of Element are not included here, but should
	 * be handled directly by the appropriate formatter.<p>
	 * Attributes handled in this method include stroke-, fill-, shadow- and double-line attributes, as well as arrows,
	 * and are appended only if they differ from the default value in PicAttributeSet.
	 * See documentation on top of this class for details on the JPIC-XML syntax.
	 * <p>
	 * Not supported yet : "border" and "bordercolor"
	 */
	protected void putCommonAttributes(Element obj){

		PicAttributeSet attributes = obj.getAttributeSet();
		Color col;
		double x;

		// linewidth [SR:pending] add only if not the default value, this will save some more space
		putNameValuePair(LINE_WIDTH,attributes);

		// linecolor
		putNameValuePair(LINE_COLOR,attributes);

		// linestyle
		putNameValuePair(LINE_STYLE,attributes);
		switch (attributes.getAttribute(LINE_STYLE)){
		case SOLID:
			break;
		case DASHED:
			 // it's too complicated to distinguish b/w default and non-default values
			double[] dashArray = {attributes.getAttribute(DASH_OPAQUE),attributes.getAttribute(DASH_TRANSPARENT)};
			putNameValuePair(JPICFormatter.STROKE_DASHARRAY,dashArray);
			break;
		case DOTTED:
			putNameValuePair(DOT_SEP,attributes);
			break;
		case NONE:
			break;
		}
		//else buf.append(" stroke-style=\"solid\""); // default !

		// double line
		if (attributes.getAttribute(DOUBLE_LINE)==Boolean.TRUE) {
			putNameValuePair(DOUBLE_LINE,attributes);
			putNameValuePair(DOUBLE_SEP,attributes);
			putNameValuePair(DOUBLE_COLOR,attributes);
		}

		// fill style and fill colour
		// solid or hatches -> add fill colour (even if it's redundant for hatches that are not "starred"=
		putNameValuePair(FILL_STYLE,attributes);
		switch (attributes.getAttribute(FILL_STYLE)){
		case NONE:
			break;
		// hatches:
		case VLINES:
		case VLINES_FILLED:
		case HLINES:
		case HLINES_FILLED:
		case CROSSHATCH:
		case CROSSHATCH_FILLED:
			putNameValuePair(FILL_COLOR,attributes);
			putNameValuePair(HATCH_WIDTH,attributes);
			putNameValuePair(HATCH_SEP,attributes);
			putNameValuePair(HATCH_ANGLE,attributes);
			putNameValuePair(HATCH_COLOR,attributes);
			break;
		// solid:
		case SOLID:
			putNameValuePair(FILL_COLOR,attributes);
			break;
		default:
		}

		// shadow
		if (attributes.getAttribute(SHADOW)==Boolean.TRUE){
			putNameValuePair(SHADOW,attributes);
			putNameValuePair(SHADOW_SIZE,attributes);
			putNameValuePair(SHADOW_ANGLE,attributes);
			putNameValuePair(SHADOW_COLOR,attributes);
		}

		// arrows
		ArrowStyle leftArrow = obj.getAttribute(LEFT_ARROW);
		ArrowStyle rightArrow = obj.getAttribute(RIGHT_ARROW);
		if (leftArrow != ArrowStyle.NONE)
			putNameValuePair(LEFT_ARROW.getName(),leftArrow.toString());
		if (rightArrow != ArrowStyle.NONE)
			putNameValuePair(RIGHT_ARROW.getName(),rightArrow.toString());
		if (leftArrow != ArrowStyle.NONE || rightArrow != ArrowStyle.NONE){
			putNameValuePair(ARROW_GLOBAL_SCALE_WIDTH,attributes);
			putNameValuePair(ARROW_GLOBAL_SCALE_LENGTH,attributes);
			putNameValuePair(ARROW_WIDTH_MINIMUM_MM,attributes);
			putNameValuePair(ARROW_WIDTH_LINEWIDTH_SCALE,attributes);
			putNameValuePair(ARROW_LENGTH_SCALE,attributes);
			putNameValuePair(ARROW_INSET_SCALE,attributes);
			putNameValuePair(TBAR_WIDTH_MINIMUM_MM,attributes);
			putNameValuePair(TBAR_WIDTH_LINEWIDTH_SCALE,attributes);
			putNameValuePair(BRACKET_LENGTH_SCALE,attributes);
			putNameValuePair(RBRACKET_LENGTH_SCALE,attributes);
			putNameValuePair(POLYDOTS_SIZE_MINIMUM_MM,attributes);
			putNameValuePair(POLYDOTS_SIZE_LINEWIDTH_SCALE,attributes);
		}

		// overstrike :
		if (attributes.getAttribute(OVER_STRIKE)==Boolean.TRUE){
			putNameValuePair( OVER_STRIKE, attributes);
			putNameValuePair( OVER_STRIKE_WIDTH, attributes);
			putNameValuePair( OVER_STRIKE_COLOR, attributes);
		}

		// custom pstricks parameters
		putNameValuePair(PST_CUSTOM,attributes);

		// custom TikZ parameters
		putNameValuePair(TIKZ_CUSTOM,attributes);

		// misc params
		putNameValuePair( DIMEN, attributes);
	}

	/**
	 * Given an attribute name and an attribute set, retrieve the value associated with the
	 * given name in the set, compares it with the default value, and
	 * add a name/value entry to the given map if they differ.
	 */
	protected <T> void putNameValuePair(PicAttributeName<T> name, PicAttributeSet set){
		T value = set.getAttribute(name);
		T def = defaultAttributes.getAttribute(name);

		if (def != null && value != null && def.equals(value))
			return;
		if (value instanceof Double)
			put(name.getName(),PEToolKit.doubleToString((Double)value));
		else if (value instanceof Color)
			put(name.getName(),colorToHex((Color)value));
		else
			put(name.getName(),value.toString());
	}

	/**
	 * Given attribute name and value, append a name/value entry to the given map.
	 */
	protected void putNameValuePair(String name, String value){
		put(name,value);
	}

	/**
	 * Given attribute name and value, append a name/value entry to the given map if value differs from the given default value.
	 */
	protected void putNameValuePair(String name, Color col, Color def){
		if (def!=null && def.equals(col)) return;
		putNameValuePair( name, colorToHex(col));
	}

	/**
	 * Given attribute name and value, append a name/value entry to the given map.
	 */
	protected void putNameValuePair(String name, boolean bol){
		putNameValuePair( name, (bol==true ? "true" : "false"));
	}

	/**
	 * Given attribute name and value, append a name/value entry to the given map
	 */
	protected void putNameValuePair(String name, PicPoint pt){
		putNameValuePair( name, pt.toString());
	}

	/**
	 * Given attribute name and value, append a name/value entry to the given map.
	 * double are formatted with two-digit precision.
	 */
	protected void putNameValuePair(String name, double value){
		putNameValuePair( name, PEToolKit.doubleToString(value));
	}

	/**
	 * Given attribute name and value, append a name/value entry to the given map if value differs from the given default value.
	 * double are formatted with two-digit precision.
	 */
	protected void putNameValuePair(String name, double value, double def){
		if (def==value) return;
		putNameValuePair( name, PEToolKit.doubleToString(value));
	}

	/**
	 * Given attribute name and an array of doubles, append a name/value entry to the given map.
	 * doubles are formatted with two-digit precision, and are separated by semicolons.
	 */
	protected void putNameValuePair(String name, double[] values){
		StringBuffer buf =new StringBuffer(); // string of comma-separated-values
		for (int i=0; i<values.length-1; i++){
			buf.append(PEToolKit.doubleToString(values[i]));
			buf.append(";");
			if (i%18==17) buf.append(CR_LF_TAB);
		}
		buf.append(PEToolKit.doubleToString(values[values.length-1]));
		put(name, buf.toString());
	}

	/**
	 * Given attribute name and an array of PicPoint's, append a name/value entry to the given map.
	 * Points are formatted by means of their toString() method, and are separated by semicolons.
	 */
	protected void putNameValuePair(String name, PicPoint[] values){
		StringBuffer buf =new StringBuffer(); // string of comma-separated-values
		for (int i = 0; i < values.length-1; i++) {
			buf.append(values[i]);
			buf.append(";");
			if (i%6==5) buf.append(CR_LF_TAB);
		}
		buf.append(values[values.length-1]);
		put(name, buf.toString());
	}

	/**
	 * Convert the given Color to a XML-like RGB string, each colour being expressed in the hexadecimal radix
	 */
	protected String colorToHex(Color c){
		String red,green,blue;
		if (c.getRed()==0) red="00"; else red = Integer.toHexString(c.getRed());
		if (c.getGreen()==0) green="00"; else green = Integer.toHexString(c.getGreen());
		if (c.getBlue()==0) blue="00"; else blue = Integer.toHexString(c.getBlue());
		StringBuffer buf = new StringBuffer("#");
		buf.append(red);
		buf.append(green);
		buf.append(blue);
		return buf.toString();
	}

}
