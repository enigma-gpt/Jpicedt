// PstricksFormatter.java --- -*- coding: iso-8859-1 -*-
// 2001 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
// Copyright (C) 2007/2013 Sylvain Reynal, Vincent Belaïche
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
// Version: $Id: PstricksFormatter.java,v 1.38 2013/03/27 07:23:04 vincentb1 Exp $
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

package jpicedt.format.output.pstricks;

//import jpicedt.format.output.util.PicGroupFormatter;

import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.model.AbstractCurve;
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.model.PicEllipse;
import jpicedt.graphic.model.PicGroup;
import jpicedt.graphic.model.PicParallelogram;
import jpicedt.graphic.model.PicText;

import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.FormatterFactory;
import jpicedt.graphic.io.formatter.AbstractFormatterFactory;
import jpicedt.graphic.io.formatter.AbstractDrawingFormatter;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import java.util.Properties;
import java.util.Iterator;
import java.util.Stack;

// debug (see main())
import jpicedt.*;
import jpicedt.graphic.*;
import jpicedt.graphic.grid.*;
import jpicedt.graphic.io.parser.*;
import java.io.*;

import static jpicedt.format.output.pstricks.PstricksConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;

import static jpicedt.Log.*;
/**
 * Produces formatters for the PsTricks macro package.
 * @author Sylvain Reynal
 * @since jpicedt 1.3.2
 * @version $Id: PstricksFormatter.java,v 1.38 2013/03/27 07:23:04 vincentb1 Exp $
 *
 */
public class PstricksFormatter extends AbstractFormatterFactory {

	static PstricksCustomProperties pstricksCustomProperties = new PstricksCustomProperties(0);
	protected static String fileWrapperEpilog = PRPTY_KEY_DEFAULT_TABLE[1];
	protected static String fileWrapperProlog = PRPTY_KEY_DEFAULT_TABLE[3];

	private PicAttributeSet defaultAttributes = new PicAttributeSet();

	private Stack<PicAttributeSet>  defaultAttributesStack = new Stack<PicAttributeSet>();

	private PicGroup containerPsCustom = null;

	public PstricksCustomProperties getCustomProperties(){ return pstricksCustomProperties; }

	/**
     *Mémorise le dernier PicGroup ayant un compoundMode égal à JOINT, de
     *sorte à détecter l'exception  NestedPsCustomException.
	 *@since jPicEdt 1.6
	 *@link #NestedPsCustomException
	 *@link #getContainerPsCustom
	 */
	public void setContainerPsCustom(PicGroup group){
		containerPsCustom = group;
	}

	/**
     *Renvoie le dernier PicGroup ayant un compoundMode égal à JOINT, s'il y
     *en a un, null sinon, de sorte à détecter l'exception
     *NestedPsCustomException.
	 *@since jPicEdt 1.6
	 *@link #NestedPsCustomException
	 *@link #setContainerPsCustom
	 */
	public PicGroup getContainerPsCustom(){
		return containerPsCustom;
	}

	/**
	 * Creates a FormatterFactory able to format Element's in the PsTricks format
	 */
	public PstricksFormatter(){
		super();
		map(AbstractCurve.class, AbstractCurveFormatter.class);
		map(PicEllipse.class, PicEllipseFormatter.class);
		map(PicParallelogram.class, PicParallelogramFormatter.class);
		map(PicText.class, PicTextFormatter.class);
		map(PicGroup.class, PicGroupFormatter.class);
	}

	/*class NonSupportedFormatter implements Formatter {
		Element element;
		NonSupportedFormatter(Element e){
			this.element = e;
		}
		public String format(){
			return "% Sorry, " + element.getName() + " is not supported (yet) by this format";
		}
	}*/

	/**
	 * Configure static fields using the given Properties object
	 * @param preferences used to read shared parameters
	 *        If null, default values are used.
	 */
	public static void configure(Properties preferences){
		PstricksFormatter.fileWrapperProlog = preferences.getProperty(
			PRPTY_KEY_DEFAULT_TABLE[0],PRPTY_KEY_DEFAULT_TABLE[1]);
		PstricksFormatter.fileWrapperEpilog = preferences.getProperty(
			PRPTY_KEY_DEFAULT_TABLE[2],PRPTY_KEY_DEFAULT_TABLE[3]);
		pstricksCustomProperties.load(preferences);
	}

	/**
	 * @return a Formatter able to format the given Drawing in the PsTricks format ;
	 *         this may reliy on calls to <code>createFormatter(Element e)</code> on the elements
	 *         of the drawing, plus creating auxiliary
	 * @param outputConstraint constraint used by the factory to create a specific Formatter on-the-fly
	 */
	public Formatter createFormatter(Drawing d, Object outputConstraint){
		return new DrawingFormatter(d,outputConstraint);
	}

	////////////////////////////////////////////////////////////////////
	//// Toolkit
	////////////////////////////////////////////////////////////////////

	// [SR:pending] move the following methods to PstricksUtilities

	/**
	 * Append "\\newrgbcolor{colourName}{cR cG cB}" to the given buffer
	 */
	private void appendUserDefinedColour(StringBuffer userDefinedColorBuffer, String colourName, float[] colourComponents){
		userDefinedColorBuffer.append("\\newrgbcolor{");
		userDefinedColorBuffer.append(colourName);
		userDefinedColorBuffer.append("}{");
		userDefinedColorBuffer.append(PEToolKit.doubleToString(colourComponents[0]));
		userDefinedColorBuffer.append(" ");
		userDefinedColorBuffer.append(PEToolKit.doubleToString(colourComponents[1]));
		userDefinedColorBuffer.append(" ");
		userDefinedColorBuffer.append(PEToolKit.doubleToString(colourComponents[2]));
		userDefinedColorBuffer.append("}");
		userDefinedColorBuffer.append(getLineSeparator());
	}

	/**
	 * Returns PsTricks's "standard" parameter string for the given Element:
	 * <ul>
	 * <li> linewidth=xxx, linecolor=xxxx, doubleline=true/false and rel.
	 * <li> if dash non-nul : linestyle=dashed, dash=xx yy
	 * <li> if object if filled : fillstyle=solid, fillcolor=xxxx + possibly shadow and hatch parameters.
	 * </ul>
	 * Note that leading and trailing brackets must be added by the caller ! (this allows the caller to add its own set of parameters)
	 * <p>
	 * Since jpicedt 1.3.3: border is supported (not bordercolor), custom parameters are included verbatim
	 * @return an object encapsulating the parameter string as well as one or more \\newrgbcolor commands whenever deemed necessary.
	 */
	public ParameterString createParameterString(Element obj){

		PicAttributeSet attributes = obj.getAttributeSet();

		StringBuffer paramBuf = new StringBuffer(100);
		StringBuffer userDefinedColorBuffer=null; // no user-defined colour by default

		// linewidth
		double lineWidth = attributes.getAttribute(LINE_WIDTH);
		if (lineWidth != defaultAttributes.getAttribute(LINE_WIDTH)){
			paramBuf.append("linewidth=");
			paramBuf.append(PEToolKit.doubleToString(lineWidth));
		}

		// linecolor
		Color lineColor = attributes.getAttribute(LINE_COLOR);
		if (!lineColor.equals(defaultAttributes.getAttribute(LINE_COLOR))){
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("linecolor=");
			String lineColorName = PstricksUtilities.getPsTricksColorName(lineColor,this);
			if (lineColorName != null) paramBuf.append(lineColorName); // ok, this is a predefined colour
			else { // define a new colour named "userLineColor" (it doesn't matter if it's not a unique name, provided the caller insert it just BEFORE the command that needs it)
				userDefinedColorBuffer = new StringBuffer(100);
				appendUserDefinedColour(userDefinedColorBuffer,"userLineColour",lineColor.getRGBColorComponents(null));
				paramBuf.append("userLineColour");
			}
		}

		// linestyle
		switch (attributes.getAttribute(LINE_STYLE)){
		case DASHED:
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("linestyle=dashed,dash=");
			paramBuf.append(PEToolKit.doubleToString(attributes.getAttribute(DASH_OPAQUE)));
			paramBuf.append(" ");
			paramBuf.append(PEToolKit.doubleToString(attributes.getAttribute(DASH_TRANSPARENT)));
			break;
		case DOTTED:
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("linestyle=dotted");
			double dotSep = attributes.getAttribute(DOT_SEP);
			if (dotSep != defaultAttributes.getAttribute(DOT_SEP)){
				paramBuf.append(",dotsep=");
				paramBuf.append(PEToolKit.doubleToString(dotSep));
			}
			break;
		case NONE:
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("linestyle=none");
			break;
		default:
		}
		// else paramBuf.append(",linestyle=solid"); default

		// overstrike (if border > 0)
		if (attributes.getAttribute(OVER_STRIKE)==Boolean.TRUE){
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("border=");
			paramBuf.append(PEToolKit.doubleToString(attributes.getAttribute(OVER_STRIKE_WIDTH)));
		}


		// fill style and fill colour
		FillStyle fillStyle = attributes.getAttribute(FILL_STYLE);
		if (fillStyle==FillStyle.NONE){
		}
		else { // solid or hatches -> add fill colour (even if it's redundant for hatches that are not "starred"=
			Color fillColor = attributes.getAttribute(FILL_COLOR);
			if(!(fillColor.equals(defaultAttributes.getAttribute(FILL_COLOR)))){ // i.e. "black" so far
				String fillColorName = PstricksUtilities.getPsTricksColorName(fillColor,this);
				if (paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append("fillcolor=");
				if (fillColorName != null) paramBuf.append(fillColorName); // ok, this is a predefined colour
				else { // define a new colour named "userFillColor" (it doesn't matter if it's not a unique name, provided the caller insert it just BEFORE the command that needs it)
					if (userDefinedColorBuffer==null) userDefinedColorBuffer = new StringBuffer(100); // there was no user-defined colour for linecolor
					appendUserDefinedColour(userDefinedColorBuffer,"userFillColour",fillColor.getRGBColorComponents(null));
					paramBuf.append("userFillColour");
				}
			}

			if (fillStyle==FillStyle.SOLID) {
				if (paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append("fillstyle=solid");
			}
			else { // hatches
				if (paramBuf.length()>0) paramBuf.append(',');
				switch (fillStyle){
				case VLINES: paramBuf.append("fillstyle=vlines"); break;
				case VLINES_FILLED: paramBuf.append("fillstyle=vlines*"); break;
				case HLINES: paramBuf.append("fillstyle=hlines"); break;
				case HLINES_FILLED: paramBuf.append("fillstyle=hlines*"); break;
				case CROSSHATCH: paramBuf.append("fillstyle=crosshatch"); break;
				case CROSSHATCH_FILLED: paramBuf.append("fillstyle=crosshatch*"); break;
				default://else throw new RuntimeException("not supported: fillstyle="+fillStyle);
				}

				double x = attributes.getAttribute(HATCH_WIDTH);
				if (x!=defaultAttributes.getAttribute(HATCH_WIDTH)){
					paramBuf.append(",hatchwidth=");
					paramBuf.append(PEToolKit.doubleToString(x));
				}
				x = attributes.getAttribute(HATCH_SEP);
				if (x!=defaultAttributes.getAttribute(HATCH_SEP)){
					paramBuf.append(",hatchsep=");
					paramBuf.append(PEToolKit.doubleToString(x));
				}
				x = attributes.getAttribute(HATCH_ANGLE);
				if (x!=defaultAttributes.getAttribute(HATCH_ANGLE)){
					paramBuf.append(",hatchangle=");
					paramBuf.append(PEToolKit.doubleToString(x));
				}

				Color hatchColor = attributes.getAttribute(HATCH_COLOR);
				if (!(hatchColor.equals(defaultAttributes.getAttribute(HATCH_COLOR)))){
					paramBuf.append(",hatchcolor=");
					String hatchColorName = PstricksUtilities.getPsTricksColorName(hatchColor,this);
					if (hatchColorName != null) paramBuf.append(hatchColorName); // ok, this is a predefined colour
					else { // define a new colour named "userHatchColor" (it doesn't matter if it's not a unique name, provided the caller insert it just BEFORE the command that needs it)
						if (userDefinedColorBuffer==null) userDefinedColorBuffer = new StringBuffer(100); // there was no user-defined colour for linecolor or fillcolor
						appendUserDefinedColour(userDefinedColorBuffer,"userHatchColour",hatchColor.getRGBColorComponents(null));
						paramBuf.append("userHatchColour");
					}
				}
			}
		}
		// shadow
		if (attributes.getAttribute(SHADOW)==Boolean.TRUE){
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("shadow=true");

			double x = attributes.getAttribute(SHADOW_SIZE);
			if (x!=defaultAttributes.getAttribute(SHADOW_SIZE)){
				paramBuf.append(",shadowsize=");
				paramBuf.append(PEToolKit.doubleToString(x));
			}
			x = attributes.getAttribute(SHADOW_ANGLE);
			if (x!=defaultAttributes.getAttribute(SHADOW_ANGLE)){
				paramBuf.append(",shadowangle=");
				paramBuf.append(PEToolKit.doubleToString(x));
			}
			Color shadowColor = attributes.getAttribute(SHADOW_COLOR);
			if (!(shadowColor.equals(defaultAttributes.getAttribute(SHADOW_COLOR)))){
				paramBuf.append(",shadowcolor=");
				String shadowColorName = PstricksUtilities.getPsTricksColorName(shadowColor,this);
				if (shadowColorName != null) paramBuf.append(shadowColorName); // ok, this is a predefined colour
				else { // define a new colour named "userShadowColor"
					if (userDefinedColorBuffer==null) userDefinedColorBuffer = new StringBuffer(100); // there was no user-defined colour for linecolor or fillcolor
					appendUserDefinedColour(userDefinedColorBuffer,"userShadowColour",shadowColor.getRGBColorComponents(null));
					paramBuf.append("userShadowColour");
				}
			}
		}

		// arrow parameters if at least one arrow is present
		if (attributes.getAttribute(LEFT_ARROW)!=ArrowStyle.NONE || attributes.getAttribute(RIGHT_ARROW)!=ArrowStyle.NONE){
			double x,y;
			x = attributes.getAttribute(ARROW_GLOBAL_SCALE_LENGTH);
			y = attributes.getAttribute(ARROW_GLOBAL_SCALE_WIDTH);
			if (x!=defaultAttributes.getAttribute(ARROW_GLOBAL_SCALE_LENGTH) || y!=defaultAttributes.getAttribute(ARROW_GLOBAL_SCALE_WIDTH)){
				if (paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append("arrowscale=");
				paramBuf.append(PEToolKit.doubleToString(y));
				paramBuf.append(' ');
				paramBuf.append(PEToolKit.doubleToString(x));
			}
			x = attributes.getAttribute(ARROW_WIDTH_MINIMUM_MM);
			y = attributes.getAttribute(ARROW_WIDTH_LINEWIDTH_SCALE);
			if (x!=defaultAttributes.getAttribute(ARROW_WIDTH_MINIMUM_MM) || y!=defaultAttributes.getAttribute(ARROW_WIDTH_LINEWIDTH_SCALE)){
				if (paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append("arrowsize=");
				paramBuf.append(PEToolKit.doubleToString(x));
				paramBuf.append(' ');
				paramBuf.append(PEToolKit.doubleToString(y));
			}
			x = attributes.getAttribute(ARROW_LENGTH_SCALE);
			if (x!=defaultAttributes.getAttribute(ARROW_LENGTH_SCALE)){
				if (paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append("arrowlength=");
				paramBuf.append(PEToolKit.doubleToString(x));
			}
			x = attributes.getAttribute(ARROW_INSET_SCALE);
			if (x!=defaultAttributes.getAttribute(ARROW_INSET_SCALE)){
				if (paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append("arrowinset=");
				paramBuf.append(PEToolKit.doubleToString(x));
			}
			x = attributes.getAttribute(TBAR_WIDTH_MINIMUM_MM);
			y = attributes.getAttribute(TBAR_WIDTH_LINEWIDTH_SCALE);
			if (x!=defaultAttributes.getAttribute(TBAR_WIDTH_MINIMUM_MM) || y!=defaultAttributes.getAttribute(TBAR_WIDTH_LINEWIDTH_SCALE)){
				if (paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append("tbarsize=");
				paramBuf.append(PEToolKit.doubleToString(x));
				paramBuf.append(' ');
				paramBuf.append(PEToolKit.doubleToString(y));
			}
			x = attributes.getAttribute(BRACKET_LENGTH_SCALE);
			if (x!=defaultAttributes.getAttribute(BRACKET_LENGTH_SCALE)){
				if (paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append("bracketlength=");
				paramBuf.append(PEToolKit.doubleToString(x));
			}
			x = attributes.getAttribute(RBRACKET_LENGTH_SCALE);
			if (x!=defaultAttributes.getAttribute(RBRACKET_LENGTH_SCALE)){
				if (paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append("rbracketlength=");
				paramBuf.append(PEToolKit.doubleToString(x));
			}
			// [pending] check if not already set by polydots
			x = attributes.getAttribute(POLYDOTS_SIZE_MINIMUM_MM);
			y = attributes.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE);
			if (x!=defaultAttributes.getAttribute(POLYDOTS_SIZE_MINIMUM_MM) || y!=defaultAttributes.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE)){
				if (paramBuf.length()>0) paramBuf.append(',');
				paramBuf.append("dotsize=");
				paramBuf.append(PEToolKit.doubleToString(x));
				paramBuf.append(' ');
				paramBuf.append(PEToolKit.doubleToString(y));
			}
		}

		// double line
		if (attributes.getAttribute(DOUBLE_LINE)==Boolean.TRUE){
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("doubleline=true");
			double x = attributes.getAttribute(DOUBLE_SEP);
			if (x!=defaultAttributes.getAttribute(DOUBLE_SEP)){
				paramBuf.append(",doublesep=");
				paramBuf.append(PEToolKit.doubleToString(x));
			}
			Color doubleColor =  attributes.getAttribute(DOUBLE_COLOR);
			if (!(doubleColor.equals(defaultAttributes.getAttribute(DOUBLE_COLOR)))){
				paramBuf.append(",doublecolor=");
				String doubleColorName = PstricksUtilities.getPsTricksColorName(doubleColor,this);
				if (doubleColorName != null) paramBuf.append(doubleColorName); // ok, this is a predefined colour
				else { // define a new colour named "userDoubleColor"
					if (userDefinedColorBuffer==null) userDefinedColorBuffer = new StringBuffer(100);
					appendUserDefinedColour(userDefinedColorBuffer,"userDoubleColour",doubleColor.getRGBColorComponents(null));
					paramBuf.append("userDoubleColour");
				}
			}
		}

		// misc params
		switch (attributes.getAttribute(DIMEN)){
		case INNER:
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("dimen=inner");
			break;
		case MIDDLE:
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append("dimen=middle");
			break;
		default:// else default="outer"
		}


		// custom pstricks parameters
		if (attributes.getAttribute(PST_CUSTOM).length()>0){
			if (paramBuf.length()>0) paramBuf.append(',');
			paramBuf.append(attributes.getAttribute(PST_CUSTOM));
		}

		// lasy work-around for syntax errors...
		/* should never happen
		if (paramBuf.charAt(paramBuf.length()-1)==','){ // if buffer has a trailing ',', which will confuse PsTricks...
			paramBuf.deleteCharAt(paramBuf.length()-1);
	}
		*/
		return (new ParameterString(paramBuf, userDefinedColorBuffer));
	}


	/**
	 * Returns a "\\psset{...}" buffer containing default parameters (useful if these differ from PsTricks's ones)
	 * This is very similar to <code>createParameterString()</code> except that a default attribute set is used,
	 * and dimensional parameters are taken into account, for which jPicEdt's default values slightly
	 * differ from PsTricks's default (mostly due to the use of mm instead of postscript points as the basic unit).
	 */
	public ParameterString createDefaultParameterString(){


		StringBuffer paramBuf = new StringBuffer(100);
		StringBuffer userDefinedColorBuffer=null; // no user-defined colour by default

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
		paramBuf.append(getLineSeparator());

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
		Color fillColor = defaultAttributes.getAttribute(FILL_COLOR);
		String fillColorName = PstricksUtilities.getPsTricksColorName(fillColor,this);
		if (fillColorName != null) paramBuf.append(fillColorName); // ok, this is a predefined colour
		else { // define a new colour named "userFillColor" (it doesn't matter if it's not a unique name, provided the caller insert it just BEFORE the command that needs it)
			userDefinedColorBuffer = new StringBuffer(100);
			appendUserDefinedColour(userDefinedColorBuffer,"userFillColour",
									fillColor.getRGBColorComponents(null));
			paramBuf.append("userFillColour");
		}

		paramBuf.append("}");
		paramBuf.append(getLineSeparator());

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
		paramBuf.append(getLineSeparator());

		// Pstricks fait des correction angulaires, mais il faut désactiver
		// cette fonction
		{
			final String pssetCorrectAngleToFalse = "\\psset{correctAngle=false}";
			if(pstricksCustomProperties.getAngleCorrection() == PstricksAngleCorrection.ANGLE_CORRECTION_BY_PSTRICKS){
				paramBuf.append(pssetCorrectAngleToFalse);
				paramBuf.append(getLineSeparator());
			}
			else if(pstricksCustomProperties.getAngleCorrection() == PstricksAngleCorrection.ANGLE_CORRECTION_BY_PSTRICKS_AUTO){
				paramBuf.append("\\makeatletter\\@ifundefined{Pst@correctAnglefalse}{}{");
				paramBuf.append(pssetCorrectAngleToFalse);
				paramBuf.append("}\\makeatother");
				paramBuf.append(getLineSeparator());
			}
		}

		return (new ParameterString(paramBuf, userDefinedColorBuffer));
	}

	/**
	 * return a Pstrick's ParameterString made up of a concatenation of the given parameter and color buffers
	 * Mainly a helper method for outer classes that may need instanciating a ParameterString.
	 */
	ParameterString createParameterString(StringBuffer paramBuf, StringBuffer userDefinedColourBuf){
		return new ParameterString(paramBuf, userDefinedColourBuf);
	}

	/**
	 * an inner class that represents a PsTricks parameter string (useful especially to handle User Defined Colours)
	 *
	 * the basic problem is that, if a new colour has to be defined, this has to be done through e.g. a \\newrgbcolor command
	 * BEFORE the Element command appears in the file. Hence it's necessary to return an object encapsulating
	 * several strings, not only the parameter string that appears inside the Element formated string.
	 */
	public class ParameterString {

		/* formated parameter string */
		StringBuffer paramBuf;

		/* a formated string for user-defined colour commands (aka \\newrgbcolour... each being separated by CR's)
		 * that may be used for "linecolor" or "fillcolor" for instance.
		 * if non-null, there's a user-defined colour (i.e. a colour that doesn't belong to PsTricks default colour dictionary)
		 * if null, there's no user-defined colour
		 * It's the responsability of the caller to insert this string at the appropriate location */
		StringBuffer userDefinedColourBuf;

		public ParameterString(StringBuffer paramBuf, StringBuffer userDefinedColourBuf){

			this.paramBuf = paramBuf;
			this.userDefinedColourBuf = userDefinedColourBuf;
		}

		/**
		 * @return true if there's at least one user-defined colour that need to be defined
		 */
		public boolean isDefinedColourString(){
			return (userDefinedColourBuf != null);
		}

		/**
		 * @return the formated parameter string (w/o leading and trailing brackets)
		 */
		public StringBuffer getParameterBuffer(){
			return paramBuf;
		}

		/**
		 * @return a string containing \\newrgbcolor... like commands, separated by CR's,
		 *         or an empty stringBuffer if there's no user-defined colours.
		 */
		public StringBuffer getUserDefinedColourBuffer(){
			if (isDefinedColourString()) return userDefinedColourBuf;
			else return new StringBuffer(); // empty !
		}
	} // inner class





	////////////////////////////////////////////////////////////////////////////////////
	//// DRAWING
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * "begin{pspicture}(x0,y0)(x1,y1)" (PsTricks) where
	 * (x0,y0)=lower-left corner and
	 * (x1,y1)=upper-right corner
	 */
	public class DrawingFormatter extends AbstractDrawingFormatter {

		/**
		 * init this formatter for the given drawing
		 * @param outputConstraint if MAKE_STANDALONE_FILE, prepend/append prolog and epilog.
		 */
		public DrawingFormatter(Drawing drawing,Object outputConstraint){
			super(drawing, outputConstraint);
		}

		/**
		 * @return  a String representing this Drawing in the PsTricks format
		 * !!! PsTricks's default unit = 1cm !!!
		 */
		public String format() throws IOException{

			FormatterFactory factory = getFormatterFactory();
			StringWriter buf = new StringWriter(200);
			buf.write("%PSTricks content-type (pstricks.sty package needed)"+getLineSeparator());
			buf.write("%Add \\usepackage{pstricks} in the preamble of your LaTeX file"+getLineSeparator());
			buf.write("%You can rescale the whole picture (to 80% for instance) by using the command \\def"+
				   PstricksConstants.RESCALING_TEX_FUNCTION+"{0.8}"+getLineSeparator());
			if (outputConstraints == FormatterFactory.MAKE_STANDALONE_FILE) 
				stringWriteMultiLine(buf,fileWrapperProlog);
			buf.write("\\ifx"+PstricksConstants.RESCALING_TEX_FUNCTION+"\\undefined\\def"+
				   PstricksConstants.RESCALING_TEX_FUNCTION+"{1}\\fi"+getLineSeparator());// allow the user to change scale using \jPicScale
			buf.write("\\psset{unit="+PstricksConstants.RESCALING_TEX_FUNCTION+" mm}"+getLineSeparator()); // make 1mm the default unit and rescale

			// add default parameter values:
			ParameterString paramStr = createDefaultParameterString();
			if (paramStr.isDefinedColourString())
				buf.append(paramStr.getUserDefinedColourBuffer());
			buf.append(paramStr.getParameterBuffer());

			/* first we compute the coordinates of the "begin{pspicture}(x0,y0)(x1,y1)" command from the drawing's bounding box */

			//
			// first we compute the location of the upper-right corner of the picture,
			// then we convert it in LaTeX coordinates
			Rectangle2D box = drawing.getBoundingBox(); // recursively calls getBounds2D() on each Element [pending] annoying bug with PicText objects !
			if(box == null)
				box = new PstricksEmptyDrawingBoundingBox();
			//debug("bbox = "+box);
			buf.write("\\begin{pspicture}(");
			buf.write(PEToolKit.doubleToString(box.getMinX()));
			buf.write(',');
			buf.write(PEToolKit.doubleToString(box.getMinY()));
			buf.write(")(");
			buf.write(PEToolKit.doubleToString(box.getMaxX()));
			buf.write(",");
			buf.write(PEToolKit.doubleToString(box.getMaxY()));
			buf.write(")");
			buf.write(getLineSeparator());

			/*  then for each Element in the Drawing, we call "toFormatedString" and append the returned String to our buffer */
			for (Element e: drawing){
				buf.write(createFormatter(e).format());
			}
			// previously registered not-parsed-commands:
			String s = drawing.getNotparsedCommands();
			if (s != null  && !s.equals("")){
				buf.write("%Begin not parsed");
				buf.write(getLineSeparator());
				buf.write(s);
				buf.write("%End not parsed");
				buf.write(getLineSeparator());
			}
			// epilogue
			buf.write("\\end{pspicture}");
			buf.write(getLineSeparator());
			if (outputConstraints == FormatterFactory.MAKE_STANDALONE_FILE) 
				stringWriteMultiLine(buf,fileWrapperEpilog);
			return buf.toString();
		}
	}

	/** Sauvegarde les defaultAttribute courant dans la pile
	 * defaultAttributesStack, et remplace les par ceux passés en
	 * argument.
	 * @param defaultAttributes nouveaux defaultAttributes
	 * @since jPicEdt 1.5.1
	 */
	public void pushDefaultAttributes(PicAttributeSet defaultAttributes){
		defaultAttributesStack.push(this.defaultAttributes);
		this.defaultAttributes = defaultAttributes;
	}

	/** Remplace les defaultAttributes courante par ceux du sommet de pile
	 * defaultAttributesStack, et dépile.
	 * @since jPicEdt 1.5.1
	 */
	public void popDefaultAttributes(){
		defaultAttributes = defaultAttributesStack.pop();
	}


	public static void main(String[] args){
		try {
			Reader reader = new FileReader(args[0]);
			Parser parser = MiscUtilities.createParser();
			Drawing dr = parser.parse(reader); // takes some time...
			Rectangle2D bb = dr.getBoundingBox();
			System.out.println(bb);
			//System.out.println(dr);
			/*
			PstricksFormatter factory = new PstricksFormatter();
			Formatter df = factory.createFormatter(dr,null);
			String buffer = df.format();
			System.out.println(buffer);
			*/
		}
		catch (Exception e){
			//error(e.toString());
			e.printStackTrace();
		}
	}

}
