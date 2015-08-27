// TikzUtilities.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: TikzUtilities.java,v 1.16 2013/06/18 20:48:17 vincentb1 Exp $
// Keywords: Tikz, PGF
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
package jpicedt.format.output.tikz;

import java.util.BitSet;
import java.awt.Color;
import jpicedt.Log;
import jpicedt.format.output.util.ColorFormatter;
import jpicedt.format.output.util.ParameterString.UserDefinedColourList;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicAttributeSet;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static jpicedt.Log.debug;
import static jpicedt.format.output.tikz.TikzConstants.TZArrow;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.view.ArrowView.Direction;
/**
 * Collection de méthodes statiques pour le formatage TikZ.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class TikzUtilities
{

	/**
	 * Ajoute les commandes
	 * <code>\\definecolor{colourName}{rgb}{<var>r</var>,<var>g</var>,<var>b</var>}
	 * </code> au tampon <code>buf</code> passé en argument.
	 * @since jPicEdt 1.6
	 */
	static public void addUserDefinedColourDefinitions(StringBuffer buf, ParameterString p,TikzFormatter factory){
		 float  [] colourComponents = new float[3];
		 for(ParameterString.UserDefinedColour c : p.getUserDefinedColourList())
		 {
			 buf.append("\\definecolor{");
			 buf.append(c.name);
			 buf.append("}{rgb}{");
			 colourComponents = c.colour.getRGBColorComponents(colourComponents);
			 buf.append(PEToolKit.doubleToString(colourComponents[0]));
			 buf.append(",");
			 buf.append(PEToolKit.doubleToString(colourComponents[1]));
			 buf.append(",");
			 buf.append(PEToolKit.doubleToString(colourComponents[2]));
			 buf.append("}");
			 buf.append(factory.getLineSeparator());
		 }
	 }

	/**
	 * @return Le nom de couleur TikZ à partir d'un objet Color ; ou une
	 * String null String si aucun nom ne correspond (en tel cas l'appelant
	 * peut définir une nouvelle couleur avec une commande \definecolor.
	 * @since jPicEdt 1.6
	 */
	public static String getColorName(Color c,TikzFormatter factory){
		return ColorFormatter.format(c,factory.getCustomProperties().getFormatterPredefinedColorSet());
	}

	/**
	 * @return un objet Color correspondant au nom TikZ de couleur passé en
	 * argument ; un Color null si aucun ne correspond (en quel cas l'appelant
	 * peut chercher dans une table de couleurs définies par l'utilisateur).
	 * @since jPicEdt 1.6
	 */
	public static Color getColor(String name,TikzFormatter factory){

		BitSet bs = new BitSet();
		bs.set(factory.getCustomProperties().getFormatterPredefinedColorSet().value());
		bs.set(ColorFormatter.ColorEncoding.XCOLOR.value());
		return ColorFormatter.parse(name,bs);
	}

	/**
	 * Convertit un style de pointe de flèche <code>ArrowStyle</code> en
	 * l'énumérer <code>TZArrow</code> correspondant décrivant son codage en
	 * TikZ.
	 *
	 * @param arrow une valeur <code>ArrowStyle</code> ) convertit en <code>TZArrow</code>.
	 * @return le <code>TZArrow</code> correspondant à <code>arrow</code>.
	 * @since jPicEdt 1.6
	 */
	public static TZArrow toTZArrow(ArrowStyle arrow){
		for (TZArrow aa: TZArrow.values()){
			if (aa.getArrowStyle() == arrow){
				return aa;
			}
		}
		return null;
	}

	/**
	 * Describe <code>toArrowString</code> method here.
	 * @since jPicEdt 1.6
	 *
	 * @param arrow
	 * @param dir
	 * @param tikzCustomProperties
	 * @return a <code>String</code> representation of
	 * @since jPicEdt 1.6
	 */
	public static String toArrowString(ArrowStyle arrow, Direction dir,
									   TikzCustomization tikzCustomProperties){
		boolean hasArrowTipPackage = tikzCustomProperties.getHasArrowTipPackage();
		TZArrow aa = toTZArrow(arrow);
		if(aa != null)
			return aa.getTZArrow(hasArrowTipPackage).getString(dir);
		else
			Log.warning("TZZArrow manquant certains ArrowStyle.");
		return null;
	}

	/**
	 * @return spécificateur TikZ de flèche (e.g. "stealth-stealth") pour
	 * le <code>PicAttributeSet</code> passé en argument.
	 */
	public static StringBuffer createArrowStringFromArrows(PicAttributeSet attributes,TikzCustomization tikzCustomProperties,boolean swapArrows){

		StringBuffer buf = new StringBuffer(15);
		ArrowStyle leftArrow = attributes.getAttribute(LEFT_ARROW);
		ArrowStyle rightArrow = attributes.getAttribute(RIGHT_ARROW);
		if(swapArrows){
			ArrowStyle temp = rightArrow;
			rightArrow = leftArrow;
			leftArrow = temp;
		}
		boolean hasArrowTipPackage = tikzCustomProperties.getHasArrowTipPackage();

		if (leftArrow == ArrowStyle.NONE && rightArrow == ArrowStyle.NONE)
			return buf;
		else
		{
			TZArrow leftTZArrow = toTZArrow(leftArrow).getTZArrow(hasArrowTipPackage);
			TZArrow rightTZArrow = toTZArrow(rightArrow).getTZArrow(hasArrowTipPackage);
			ArrowStyle leftElement = leftTZArrow.getElementArrowStyle();
			ArrowStyle rightElement = rightTZArrow.getElementArrowStyle();
			TZArrow tZArrow = null;
			ArrowStyle arrow = ArrowStyle.NONE;
			ArrowStyle elementArrow;

			if(leftArrow == ArrowStyle.NONE || rightArrow == ArrowStyle.NONE
			   || leftElement == rightElement)
			{
				for(int direction = 0;direction < 2; ++direction)
				{
					char arrowSpec = 0;
					if(direction == 0)
					{
						if(leftArrow != ArrowStyle.NONE)
						{
							arrowSpec = '<';
							arrow = leftArrow;
							tZArrow = leftTZArrow;
						}

					}
					else
					{
						if(rightArrow != ArrowStyle.NONE)
						{
							arrow = rightArrow;
							arrowSpec = '>';
							tZArrow = rightTZArrow;
						}
					}
					if(arrowSpec != 0)
					{
						int i = tZArrow.getCount();
						if(i < 0)
						{
							arrowSpec = arrowSpec == '>' ? '<' : '>';
							for(int j=0; j > i; --j)
								buf.append(arrowSpec);
						}
						else if(i > 0)
						{
							for(int j=0; j < i; ++j)
								buf.append(arrowSpec);
						}

					}
					if(direction == 0)
						buf.append("-");

				}
				elementArrow = tZArrow.getElementArrowStyle();
				if(elementArrow != arrow)
					tZArrow = toTZArrow(elementArrow);
				buf.append(",>=");
				buf.append(tZArrow.getString(Direction.LEFT));
				for(int direction =0; direction < 2; ++direction)
				{
					tZArrow = direction == 0 ? leftTZArrow : rightTZArrow;
					String shorten = tZArrow.getShorten(direction == 0 ? Direction.LEFT : Direction.RIGHT);
					if(shorten != null)
					{
						buf.append(",shorten ");
						buf.append(direction==0?'<':'>');
						buf.append('=');
						buf.append(shorten);
					}
				}
			}
			else
			{
			}
		}
		return buf;
	}

	/**
	 * @return la liste de paramètres standard de TikZ pour l'objet élément
	 * passé en argument.
	 */
	static public ParameterString createParameterString(
		Element obj,
		PicAttributeSet defaultAttributes,
		TikzCustomProperties tikzCustomProperties,
		BitSet mask){

		ColorFormatter.ColorEncoding predefinedColorSet =
			tikzCustomProperties.getFormatterPredefinedColorSet();
		PicAttributeSet attributes = obj.getAttributeSet();

		StringBuffer paramBuf = new StringBuffer(100);
		ParameterString parameterString = new ParameterString(paramBuf,null);
		// no user-defined colour by default --------------------------^

		// linewidth
		double lineWidth = attributes.getAttribute(LINE_WIDTH);
		if (lineWidth != defaultAttributes.getAttribute(LINE_WIDTH)){
			paramBuf.append("line width=");
			paramBuf.append(PEToolKit.doubleToString(lineWidth));
			paramBuf.append("mm");
		}

		// linecolor
		Color lineColor = attributes.getAttribute(LINE_COLOR);
		if (!lineColor.equals(defaultAttributes.getAttribute(LINE_COLOR))){
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("color=");
			String lineColorName = ColorFormatter.format(lineColor,predefinedColorSet);
			if (lineColorName != null) paramBuf.append(lineColorName); // ok, this is a predefined colour
			else { // define a new colour named "userLineColor" (it doesn't matter if it's not a unique name, provided the caller insert it just BEFORE the command that needs it)
				parameterString.addUserDefinedColour("userLineColour",lineColor);
				paramBuf.append("userLineColour");
			}
		}

		// linestyle
		switch (attributes.getAttribute(LINE_STYLE)){
		case DASHED:
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("dash pattern= on ");
			paramBuf.append(PEToolKit.doubleToString(attributes.getAttribute(DASH_OPAQUE)));
			paramBuf.append("mm off ");
			paramBuf.append(PEToolKit.doubleToString(attributes.getAttribute(DASH_TRANSPARENT)));
			paramBuf.append("mm");
			break;
		case DOTTED:
			{
				double dotSep = attributes.getAttribute(DOT_SEP);
				if(dotSep > lineWidth)
				{
					if (paramBuf.length()>0) paramBuf.append(',');
					paramBuf.append("dash pattern=on \\pgflinewidth off ");
					paramBuf.append(PEToolKit.doubleToString(dotSep-lineWidth));
					paramBuf.append("mm");
				}
			}
		 	break;
		case SOLID:
			break;
		case NONE:
			parameterString.setDrawToNone();
			break;
		default:
			debug("Unexpected LINE_STYLE");
		}

		// overstrike (if border > 0)
		// if (attributes.getAttribute(OVER_STRIKE)==Boolean.TRUE){
		// 	if (paramBuf.length()>0) paramBuf.append(',');
		// 	paramBuf.append("border=");
		// 	paramBuf.append(PEToolKit.doubleToString(attributes.getAttribute(OVER_STRIKE_WIDTH)));
		// }


		// fill style and fill colour
		FillStyle fillStyle = attributes.getAttribute(FILL_STYLE);
		if (fillStyle==FillStyle.NONE){
		}
		else { // solid or hatches -> add fill colour (even if it's redundant for hatches that are not "starred"=
			Color fillColor = attributes.getAttribute(FILL_COLOR);
			if(!(fillColor.equals(defaultAttributes.getAttribute(FILL_COLOR)))){ // i.e. "black" so far
				String fillColorName = ColorFormatter.format(fillColor,tikzCustomProperties.getFormatterPredefinedColorSet());
				if (paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append("fill=");
				if (fillColorName != null) paramBuf.append(fillColorName); // ok, this is a predefined colour
				else { // define a new colour named "userFillColor" (it doesn't matter if it's not a unique name, provided the caller insert it just BEFORE the command that needs it)
					parameterString.addUserDefinedColour("userFillColour",fillColor);
					paramBuf.append("userFillColour");
				}
			}
			else
				parameterString.setFill();


			if (fillStyle==FillStyle.SOLID) {
			}
			else { // hatches
				// if (paramBuf.length()>0) paramBuf.append(',');
				// switch (fillStyle){
				// case VLINES: paramBuf.append("fillstyle=vlines"); break;
				// case VLINES_FILLED: paramBuf.append("fillstyle=vlines*"); break;
				// case HLINES: paramBuf.append("fillstyle=hlines"); break;
				// case HLINES_FILLED: paramBuf.append("fillstyle=hlines*"); break;
				// case CROSSHATCH: paramBuf.append("fillstyle=crosshatch"); break;
				// case CROSSHATCH_FILLED: paramBuf.append("fillstyle=crosshatch*"); break;
				// default://else throw new RuntimeException("not supported: fillstyle="+fillStyle);
				// }

				// double x = attributes.getAttribute(HATCH_WIDTH);
				// if (x!=defaultAttributes.getAttribute(HATCH_WIDTH)){
				// 	paramBuf.append(",hatchwidth=");
				// 	paramBuf.append(PEToolKit.doubleToString(x));
				// }
				// x = attributes.getAttribute(HATCH_SEP);
				// if (x!=defaultAttributes.getAttribute(HATCH_SEP)){
				// 	paramBuf.append(",hatchsep=");
				// 	paramBuf.append(PEToolKit.doubleToString(x));
				// }
				// x = attributes.getAttribute(HATCH_ANGLE);
				// if (x!=defaultAttributes.getAttribute(HATCH_ANGLE)){
				// 	paramBuf.append(",hatchangle=");
				// 	paramBuf.append(PEToolKit.doubleToString(x));
				// }

				// Color hatchColor = attributes.getAttribute(HATCH_COLOR);
				// if (!(hatchColor.equals(defaultAttributes.getAttribute(HATCH_COLOR)))){
				// 	paramBuf.append(",hatchcolor=");
				// 	String hatchColorName = TikzUtilities.getColorName(hatchColor);
				// 	if (hatchColorName != null) paramBuf.append(hatchColorName); // ok, this is a predefined colour
				// 	else { // define a new colour named "userHatchColor" (it doesn't matter if it's not a unique name, provided the caller insert it just BEFORE the command that needs it)
				// 		userDefinedColourList = addUserDefinedColour(userDefinedColourList,"userHatchColour",hatchColor);
				// 		paramBuf.append("userHatchColour");
				// 	}
				// }
			}
		}
		// shadow
		if (attributes.getAttribute(SHADOW)==Boolean.TRUE){
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("shadow={");
			int argCount = 0;
			double r = attributes.getAttribute(SHADOW_SIZE);
			double a = attributes.getAttribute(SHADOW_ANGLE);
			if (r!=defaultAttributes.getAttribute(SHADOW_SIZE)
				&& a!=defaultAttributes.getAttribute(SHADOW_ANGLE)){
				++argCount;
				a = toRadians(a);
				paramBuf.append("shadow xshift=");
				paramBuf.append(PEToolKit.doubleToString(r*cos(a)));
				paramBuf.append("mm ,shadow yshift=");
				paramBuf.append(PEToolKit.doubleToString(r*sin(a)));
				paramBuf.append("mm");
			}
			Color shadowColor = attributes.getAttribute(SHADOW_COLOR);
			if (!(shadowColor.equals(defaultAttributes.getAttribute(SHADOW_COLOR)))){
				if(argCount != 0)
					paramBuf.append(',');
				++argCount;

				paramBuf.append("fille=");
				String shadowColorName = ColorFormatter.format(shadowColor,predefinedColorSet);
				if (shadowColorName != null) paramBuf.append(shadowColorName); // ok, this is a predefined colour
				else { // define a new colour named "userShadowColor"
					parameterString.addUserDefinedColour("userShadowColour",shadowColor);
					paramBuf.append("userShadowColour");
				}
			}
			paramBuf.append('}');
		}

		// arrow parameters if at least one arrow is present
		if (attributes.getAttribute(LEFT_ARROW)!=ArrowStyle.NONE
			|| attributes.getAttribute(RIGHT_ARROW)!=ArrowStyle.NONE){
		 	// Tikz Arrow styles
		 	if (paramBuf.length()>0) paramBuf.append(',');
		 	paramBuf.append(createArrowStringFromArrows(
								attributes,tikzCustomProperties,
								mask.get(TikzConstants.DrawFlags.SWAP_ARROWS.getValue())));

		// 	double x,y;
		// 	x = attributes.getAttribute(ARROW_GLOBAL_SCALE_LENGTH);
		// 	y = attributes.getAttribute(ARROW_GLOBAL_SCALE_WIDTH);
		// 	if (x!=defaultAttributes.getAttribute(ARROW_GLOBAL_SCALE_LENGTH) || y!=defaultAttributes.getAttribute(ARROW_GLOBAL_SCALE_WIDTH)){
		// 		if (paramBuf.length()>0) paramBuf.append(',');
		// 		paramBuf.append("arrowscale=");
		// 		paramBuf.append(PEToolKit.doubleToString(y));
		// 		paramBuf.append(' ');
		// 		paramBuf.append(PEToolKit.doubleToString(x));
		// 	}
		// 	x = attributes.getAttribute(ARROW_WIDTH_MINIMUM_MM);
		// 	y = attributes.getAttribute(ARROW_WIDTH_LINEWIDTH_SCALE);
		// 	if (x!=defaultAttributes.getAttribute(ARROW_WIDTH_MINIMUM_MM) || y!=defaultAttributes.getAttribute(ARROW_WIDTH_LINEWIDTH_SCALE)){
		// 		if (paramBuf.length()>0) paramBuf.append(',');
		// 		paramBuf.append("arrowsize=");
		// 		paramBuf.append(PEToolKit.doubleToString(x));
		// 		paramBuf.append(' ');
		// 		paramBuf.append(PEToolKit.doubleToString(y));
		// 	}
		// 	x = attributes.getAttribute(ARROW_LENGTH_SCALE);
		// 	if (x!=defaultAttributes.getAttribute(ARROW_LENGTH_SCALE)){
		// 		if (paramBuf.length()>0) paramBuf.append(',');
		// 		paramBuf.append("arrowlength=");
		// 		paramBuf.append(PEToolKit.doubleToString(x));
		// 	}
		// 	x = attributes.getAttribute(ARROW_INSET_SCALE);
		// 	if (x!=defaultAttributes.getAttribute(ARROW_INSET_SCALE)){
		// 		if (paramBuf.length()>0) paramBuf.append(',');
		// 		paramBuf.append("arrowinset=");
		// 		paramBuf.append(PEToolKit.doubleToString(x));
		// 	}
		// 	x = attributes.getAttribute(TBAR_WIDTH_MINIMUM_MM);
		// 	y = attributes.getAttribute(TBAR_WIDTH_LINEWIDTH_SCALE);
		// 	if (x!=defaultAttributes.getAttribute(TBAR_WIDTH_MINIMUM_MM) || y!=defaultAttributes.getAttribute(TBAR_WIDTH_LINEWIDTH_SCALE)){
		// 		if (paramBuf.length()>0) paramBuf.append(',');
		// 		paramBuf.append("tbarsize=");
		// 		paramBuf.append(PEToolKit.doubleToString(x));
		// 		paramBuf.append(' ');
		// 		paramBuf.append(PEToolKit.doubleToString(y));
		// 	}
		// 	x = attributes.getAttribute(BRACKET_LENGTH_SCALE);
		// 	if (x!=defaultAttributes.getAttribute(BRACKET_LENGTH_SCALE)){
		// 		if (paramBuf.length()>0) paramBuf.append(',');
		// 		paramBuf.append("bracketlength=");
		// 		paramBuf.append(PEToolKit.doubleToString(x));
		// 	}
		// 	x = attributes.getAttribute(RBRACKET_LENGTH_SCALE);
		// 	if (x!=defaultAttributes.getAttribute(RBRACKET_LENGTH_SCALE)){
		// 		if (paramBuf.length()>0) paramBuf.append(',');
		// 		paramBuf.append("rbracketlength=");
		// 		paramBuf.append(PEToolKit.doubleToString(x));
		// 	}
		// 	// [pending] check if not already set by polydots
		// 	x = attributes.getAttribute(POLYDOTS_SIZE_MINIMUM_MM);
		// 	y = attributes.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE);
		// 	if (x!=defaultAttributes.getAttribute(POLYDOTS_SIZE_MINIMUM_MM) || y!=defaultAttributes.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE)){
		// 		if (paramBuf.length()>0) paramBuf.append(',');
		// 		paramBuf.append("dotsize=");
		// 		paramBuf.append(PEToolKit.doubleToString(x));
		// 		paramBuf.append(' ');
		// 		paramBuf.append(PEToolKit.doubleToString(y));
		// 	}
		}

		// double line
		// if (attributes.getAttribute(DOUBLE_LINE)==Boolean.TRUE){
		// 	if (paramBuf.length()>0) paramBuf.append(',');
		// 	paramBuf.append("double=");
		// 	double x = attributes.getAttribute(DOUBLE_SEP);
		// 	if (x!=defaultAttributes.getAttribute(DOUBLE_SEP)){
		// 		paramBuf.append(",doublesep=");
		// 		paramBuf.append(PEToolKit.doubleToString(x));
		// 	}
		// 	Color doubleColor =  attributes.getAttribute(DOUBLE_COLOR);
		// 	if (!(doubleColor.equals(defaultAttributes.getAttribute(DOUBLE_COLOR)))){
		// 		paramBuf.append(",doublecolor=");
		// 		String doubleColorName = TikzUtilities.getColorName(doubleColor);
		// 		if (doubleColorName != null) paramBuf.append(doubleColorName); // ok, this is a predefined colour
		// 		else { // define a new colour named "userDoubleColor"
		// 			userDefinedColourList = addUserDefinedColour(userDefinedColourList,"userDoubleColour",doubleColor);
		// 			paramBuf.append("userDoubleColour");
		// 		}
		// 	}
		// }

		// misc params
		// switch (attributes.getAttribute(DIMEN)){
		// case INNER:
		// 	if (paramBuf.length()>0) paramBuf.append(',');
		// 	paramBuf.append("dimen=inner");
		// 	break;
		// case MIDDLE:
		// 	if (paramBuf.length()>0) paramBuf.append(',');
		// 	paramBuf.append("dimen=middle");
		// 	break;
		// default:// else default="outer"
		// }

		// Custom TikZ parameters.
		{
			String customs = attributes.getAttribute(TIKZ_CUSTOM);
			if (customs.length() > 0){
				if(paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append(customs);
			}
		}

		return parameterString;
	}

	/**
	 * Returns a "\\psset{&hellip;}" buffer containing default parameters (useful
	 * if these differ from PsTricks's ones) This is very similar to
	 * <code>createParameterString()</code> except that a default attribute
	 * set is used, and dimensional parameters are taken into account, for
	 * which jPicEdt's default values slightly differ from PsTricks's default
	 * (mostly due to the use of mm instead of postscript points as the basic
	 * unit).
	 */
	static public StringBuffer createDefaultParameterString(
		PicAttributeSet  defaultAttributes,
		TikzFormatter factory
		){

		ColorFormatter.ColorEncoding predefinedColorSet =
			factory.getCustomProperties().getFormatterPredefinedColorSet();

		StringBuffer paramBuf = new StringBuffer(100);

		paramBuf.append("\\psset{");

		paramBuf.append("linewidth=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(LINE_WIDTH)));

		paramBuf.append(",dotsep=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(DOT_SEP)));

		paramBuf.append(",hatchwidth=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(HATCH_WIDTH)));
		paramBuf.append(",hatchsep=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(HATCH_SEP)));

		paramBuf.append(",shadowsize=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(SHADOW_SIZE)));

		paramBuf.append(",dimen=middle");//same as java


		// [SR:pending] not used yet
		//paramBuf.append(",doublesep=");
		//paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(DOUBLE_SEP)));

		paramBuf.append("}");
		paramBuf.append(factory.getLineSeparator());

		paramBuf.append("\\psset{");

		paramBuf.append("dotsize=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(POLYDOTS_SIZE_MINIMUM_MM)));
		paramBuf.append(' ');
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE)));
		paramBuf.append(",dotscale=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(POLYDOTS_SCALE_H)));
		paramBuf.append(' ');
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(POLYDOTS_SCALE_V)));
		// --- fill color = black (PsTricks doesn't seem to fill in black by default)
		paramBuf.append(",fillcolor=");
		paramBuf.append(ColorFormatter.format(defaultAttributes.getAttribute(FILL_COLOR),
											  predefinedColorSet));

		paramBuf.append("}");
		paramBuf.append(factory.getLineSeparator());

		// arrow params
		paramBuf.append("\\psset{");

		paramBuf.append("arrowsize=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(ARROW_WIDTH_MINIMUM_MM)));
		paramBuf.append(' ');
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(ARROW_WIDTH_LINEWIDTH_SCALE)));

		paramBuf.append(",arrowlength=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(ARROW_LENGTH_SCALE)));

		paramBuf.append(",arrowinset=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(ARROW_INSET_SCALE)));

		paramBuf.append(",tbarsize=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(TBAR_WIDTH_MINIMUM_MM)));
		paramBuf.append(' ');
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(TBAR_WIDTH_LINEWIDTH_SCALE)));

		paramBuf.append(",bracketlength=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(BRACKET_LENGTH_SCALE)));

		paramBuf.append(",rbracketlength=");
		paramBuf.append(PEToolKit.doubleToString(defaultAttributes.getAttribute(RBRACKET_LENGTH_SCALE)));

		// dotsize -> see above (polydots)
		// arrowscale : default to 1 ok

		paramBuf.append("}");
		paramBuf.append(factory.getLineSeparator());

		return paramBuf;
	}

}


/// TikzUtilities.java ends here
