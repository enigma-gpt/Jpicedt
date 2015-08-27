// JPICFormatter.java --- -*- coding: iso-8859-1 -*-
// July 29, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: JPICFormatter.java,v 1.30 2013/03/27 07:21:29 vincentb1 Exp $
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

import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.PicPoint;

import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.model.PicCircleFrom3Points;
import jpicedt.graphic.model.PicEllipse;
import jpicedt.graphic.model.PicGroup;
import jpicedt.graphic.model.PicMultiCurve;
import jpicedt.graphic.model.PicParallelogram;
import jpicedt.graphic.model.PicSmoothPolygon;
import jpicedt.graphic.model.PicPsCurve;
import jpicedt.graphic.model.PicText;
import jpicedt.graphic.model.StyleConstants;
import jpicedt.graphic.io.formatter.JPICEmptyDrawingBoundingBox;
import jpicedt.graphic.io.formatter.AbstractDrawingFormatter;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Date;
import java.util.Properties;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;


import static jpicedt.graphic.io.formatter.JPICConstants.*;

/**
 * a FormatterFactory that produces formatters for the JPIC-XML native format
 * <p>
 * Syntax for common attributes, that is, attributes not specific to a given subclass of Element, is
 * strongly inspired from the XML-SVG markup language, although it is restricted to a subset of SVG attributes
 * supported by jPicEdt, i.e. mainly by pstricks.
 * Hence attributes names and values strongly mimic those of PsTricks, except for some keyword changes.
 * So far, default values are hardcoded in {@link jpicedt.graphic.model.DefaultAttributeSet DefaultAttributeSet}.
 * This may be changed in the near future, once the language has stabilized and the DtD is ready for use.
 * <p>
 * Here is a list of supported attributes as of release 1.4-pre :
 * <ul>
 * <li> stroke-style=none|dashed|dotted|solid (default : solid)
 * <li> stroke-color=#a_color_in_HEX_radix (default #000000)
 * <li> stroke-width=length_in_mm
 * <li> stroke-dasharray=a_dash_array (comma separated values)
 * <li> stroke-dotsep=length_in_mm
 * <li> fill-style=none|solid|any_PsTricks_valid_hatches_style (default : solid)
 * <li> fill-color=#a_color_in_HEX_radix (default #000000)
 * <li> shadow=true|false (default : false)
 * <li> shadow-size=length_in_mm
 * <li> shadow-angle=angle_in_degrees
 * <li> shadow-color=#a_color_in_HEX_radix (default #000000)
 * <li> hatch-width=length_in_mm
 * <li> hatch-sep=length_in_mm
 * <li> hatch-angle=angle_in_degrees
 * <li> hatch-color=#a_color_in_HEX_radix (default #000000)
 * <li> doubleline=true|false (default : false)
 * <li> doubleline-sep=length_in_mm
 * <li> doubleline-color=length_in_mm (default #000000)
 * <li> left-arrow=arrow_name_as_in_Arrow_class (default ArrowStyle.NONE)
 * <li> right-arrow=arrow_name_as_in_Arrow_class (default ArrowStyle.NONE)
 * <li> arrow-global-scale-width=scale_factor_for_width
 * <li> arrow-global-scale-length=scale_factor_for_length
 * <li> arrow-width-minimum=minimum_width_in_mm
 * <li> arrow-width-linewidth-scale=width_scale_factor_wrt_linewidth
 * <li> arrow-length-scale=scale_factor_for_length
 * <li> arrow-inset-scale=scale_factor_for_inset
 * <li> tbar-width-minimum=minimum_width_for_tbars_in_mm
 * <li> tbar-width-linewidth-scale=width_scale_factor_for_tbars_wrt_linewidth
 * <li> bracket-length-scale=length_scale_factor_for_brackets
 * <li> rbracket-length-scale=length_scale_factor_for_rounded_brackets
 * <li> [todo] style=name_of_a_user_defined_style (see future &lt;style&gt; tag). Any attribute defined "in place" overrides
 *      the same attribute when defined in the corresponding &lt;style&gt; tag.
 * </ul>
 * These syntax rules are only briefly laid out here as a reminder to end-users.
 * The corresponding DtD, once it is set up, will be the only valid specification for that matter.
 *
 * @since jpicedt 1.4
 * @author Sylvain Reynal
 * @version $Id: JPICFormatter.java,v 1.30 2013/03/27 07:21:29 vincentb1 Exp $
 */
public class JPICFormatter extends AbstractFormatterFactory {

	private final PicAttributeSet defaultAttributes = new PicAttributeSet(); // [pending] still needed?
	public static final String STROKE_DASHARRAY="stroke-dasharray";

	protected String lineSeparatorTab = lineSeparator + "\t";

	public void   setLineSeparator(String lineSeparator){ 
		super.setLineSeparator(lineSeparator);
		lineSeparatorTab = lineSeparator + "\t";
	}



	/** Constructeur <code>JPICFormatter</code>. */
	public JPICFormatter(CommentFormatting commentFormatter){
		super();
		this.commentFormatter = commentFormatter;
		map(PicCircleFrom3Points.class, JPICCircleFormatter.class);
		map(PicEllipse.class, JPICEllipseFormatter.class);
		map(PicParallelogram.class, JPICParallelogramFormatter.class);
		map(PicSmoothPolygon.class, JPICSmoothPolygonFormatter.class);
		map(PicMultiCurve.class, JPICMultiCurveFormatter.class);
		map(PicPsCurve.class, JPICPsCurveFormatter.class);
		map(PicText.class, JPICTextFormatter.class);
		map(PicGroup.class, JPICGroupFormatter.class);
	}

	/*
	public Formatter createFormatter(Element e){
		// put daughters first !
		if (e instanceof PicCircleFrom3Points) return new JPICCircleFormatter((PicCircleFrom3Points)e,this);
		if (e instanceof PicEllipse) return new JPICEllipseFormatter((PicEllipse)e,this);
		if (e instanceof PicParallelogram) return new JPICParallelogramFormatter((PicParallelogram)e,this);
		if (e instanceof PicSmoothPolygon) return new JPICSmoothPolygonFormatter((PicSmoothPolygon)e,this);
		if (e instanceof PicMultiCurve) return new JPICMultiCurveFormatter((PicMultiCurve)e,this);
		if (e instanceof PicPsCurve) return new JPICPsCurveFormatter((PicPsCurve)e,this);
		if (e instanceof PicText) return new JPICTextFormatter((PicText)e,this);
		if (e instanceof PicGroup) return new JPICGroupFormatter((PicGroup)e,this);
		return new NonSupportedFormatter(e);
	}*/

	/*
	 * a formatter for Element's which are not supported yet by the JPIC-XML language ;
	 * this a reminder dedicated to "scutterbrain" developpers (like me...).
	 *
	class NonSupportedFormatter implements Formatter {
		Element element;
		NonSupportedFormatter(Element e){
			this.element = e;
		}
		public String format(){
			return "<notsupported>" + element.getName() + "</notsupported>" + CR_LF;
		}
	}*/

	/**
	 * Configure static fields using the given Properties object
	 * This implementation does nothing so far.
	 * @param preferences used to read shared parameters. If null, default values are used.
	 */
	public static void configure(Properties preferences){
	}

	/**
	 * Return a Formatter able to format the given Drawing in the JPIC-XML language ;
	 * @param outputConstraint constraint used by this factory to create a specific Formatter on-the-fly, with
	 *        e.g. dynamic constraints on the output format. There are no constraints so far for this factory,
	 *        and this parameter may safely be set to null.
	 */
	public Formatter createFormatter(Drawing d, Object outputConstraint){
		return new DrawingFormatter(d,outputConstraint,this);
	}

	////////////////////////////////////////////////////////////////////////////////////
	//// DRAWING
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 *  A Drawing's formatter for the JPIC-XML language.
	 */
	protected class DrawingFormatter extends AbstractDrawingFormatter {

		/** the Drawing this formatter acts upon */
		private CommentFormatting commentFormatter;

		/**
		 * init this formatter for the given drawing
		 * @param outputConstraints if MAKE_STANDALONE_FILE, prepend/append prolog and epilog.
		 */
		public DrawingFormatter(Drawing drawing,Object outputConstraints,AbstractFormatterFactory factory){
			super(drawing,outputConstraints);
			this.commentFormatter = factory.getCommentFormatter();
		}

		/**
		 * @return  a String representing this Drawing in the PsTricks format
		 * !!! PsTricks's default unit = 1cm !!!
		 */
		public String format() throws IOException{

			StringWriter stringWriter = new StringWriter();
			commentFormatter.setWriter(stringWriter);

			StringBuffer buf = new StringBuffer(200);

			buf.append("Created by jPicEdt "+jpicedt.Version.getVersion()
					   +": "
					   +commentFormatter.getContentTypeCommentFormatting()
					   +" format");
			buf.append(lineSeparator);
			buf.append("File type: "
					   +commentFormatter.getConcreteContentType());
			buf.append(lineSeparator);
			buf.append(new Date());
			buf.append(lineSeparator);
			buf.append("Begin JPIC-XML");

			commentFormatter.strongCommentFormat(buf.toString());

			buf = new StringBuffer(200);

			buf.append("<?xml version=\"1.0\" standalone=\"yes\"?>");
			buf.append(lineSeparator);
			// wait DtD is ok to uncomment :
			// buf.append("<!DOCTYPE jpic PUBLIC \"-JPIC 1.0//EN\" \"http://www.jpicedt.org/jpic.dtd\">");


			// start document with <jpic> tag :
			buf.append("<jpic");

			/* first we compute the picture size from the drawing's bounding box */

			Rectangle2D box = drawing.getBoundingBox(); // recursively calls getBounds2D() on each Element [pending] annoying bug with PicText objects !
			if (box == null)
				box = new JPICEmptyDrawingBoundingBox();
			//debug("bbox = "+box);
			buf.append(" x-min=\"");
			buf.append(PEToolKit.doubleToString(box.getMinX()));
			buf.append("\" x-max=\"");
			buf.append(PEToolKit.doubleToString(box.getMaxX()));
			buf.append("\" y-min=\"");
			buf.append(PEToolKit.doubleToString(box.getMinY()));
			buf.append("\" y-max=\"");
			buf.append(PEToolKit.doubleToString(box.getMaxY()));
			buf.append("\" auto-bounding=\"");
			buf.append(drawing.isAutoComputeBoundingBox() ? "true" : "false");
			buf.append("\">");
			buf.append(lineSeparator);

			/*  then for each Element in the Drawing, we call "toFormatedString" and append the returned String to our buffer */
			for (Element e:drawing){
				buf.append(createFormatter(e).format());
			}
			//epilogue
			buf.append("</jpic>");

			commentFormatter.weakCommentFormat(buf.toString());
			commentFormatter.strongCommentFormat("End JPIC-XML");

			return stringWriter.toString();
		}
	}

	public CommentFormatting getCommentFormatter(){
		return commentFormatter;
	}

}
