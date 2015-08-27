// PicAttributeName.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006  Sylvain Reynal
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
// Version: $Id: PicAttributeName.java,v 1.18 2013/03/27 07:02:15 vincentb1 Exp $
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

package jpicedt.graphic.model;

import java.awt.Color;

/**
 * A wrapper for an attribute name that can be pushed into a PicAttributeSet.
 * This allows typesafe enumerations, and besides enforces compile-time checking as for
 * attributes values that can be safely attached to this PicAttributeName.
 * The type parameter T represents the type of values that can be attached to this PicAttributeName.
 * @author Sylvain Reynal
 * @since jpicedt 1.3.2 (generified since jpicedt 1.5)
 * @version $Id: PicAttributeName.java,v 1.18 2013/03/27 07:02:15 vincentb1 Exp $
 * <p>
 */
public class PicAttributeName<T> {

        public static final PicAttributeName<StyleConstants.LineStyle> LINE_STYLE = new PicAttributeName<StyleConstants.LineStyle>("stroke-style");

        public static final PicAttributeName<Color> LINE_COLOR = new PicAttributeName<Color> ("stroke-color");
        public static final PicAttributeName<Double> LINE_WIDTH = new PicAttributeName<Double> ("stroke-width");
        public static final PicAttributeName<Double> DASH_TRANSPARENT = new PicAttributeName<Double> ("stroke-dash-transparent");
        public static final PicAttributeName<Double> DASH_OPAQUE = new PicAttributeName<Double> ("stroke-dash-opaque");
        public static final PicAttributeName<Double> DOT_SEP = new PicAttributeName<Double> ("stroke-dotsep");
        public static final PicAttributeName<Boolean> DOUBLE_LINE = new PicAttributeName<Boolean> ("stroke-doubleline"); // true/false
        public static final PicAttributeName<Double> DOUBLE_SEP = new PicAttributeName<Double> ("stroke-doubleline-sep");
        public static final PicAttributeName<Color> DOUBLE_COLOR = new PicAttributeName<Color> ("stroke-doubleline-color");
        public static final PicAttributeName<Boolean> OVER_STRIKE = new PicAttributeName<Boolean> ("stroke-overstrike"); // do overstrike or not
        public static final PicAttributeName<Double> OVER_STRIKE_WIDTH = new PicAttributeName<Double> ("stroke-overstrike-width"); // width of overstriking
        public static final PicAttributeName<Color> OVER_STRIKE_COLOR = new PicAttributeName<Color> ("stroke-overstrike-color"); // border color

        public static final PicAttributeName<Boolean> SHADOW = new PicAttributeName<Boolean> ("shadow");
        public static final PicAttributeName<Double> SHADOW_SIZE = new PicAttributeName<Double> ("shadow-size");
        public static final PicAttributeName<Double> SHADOW_ANGLE = new PicAttributeName<Double> ("shadow-angle");
        public static final PicAttributeName<Color> SHADOW_COLOR = new PicAttributeName<Color> ("shadow-color");

        public static final PicAttributeName<StyleConstants.Dimen> DIMEN = new PicAttributeName<StyleConstants.Dimen> ("dimen");

        public static final PicAttributeName<StyleConstants.FillStyle> FILL_STYLE = new PicAttributeName<StyleConstants.FillStyle> ("fill-style");
        public static final PicAttributeName<Color> FILL_COLOR = new PicAttributeName<Color> ("fill-color");
        public static final PicAttributeName<Double> HATCH_WIDTH = new PicAttributeName<Double> ("fill-hatch-width");
        public static final PicAttributeName<Double> HATCH_SEP = new PicAttributeName<Double> ("fill-hatch-sep");
        public static final PicAttributeName<Color> HATCH_COLOR = new PicAttributeName<Color> ("fill-hatch-color");
        public static final PicAttributeName<Double> HATCH_ANGLE = new PicAttributeName<Double> ("fill-hatch-angle");

        public static final PicAttributeName<StyleConstants.ArrowStyle> LEFT_ARROW = new PicAttributeName<StyleConstants.ArrowStyle> ("left-arrow");
        public static final PicAttributeName<StyleConstants.ArrowStyle> RIGHT_ARROW = new PicAttributeName<StyleConstants.ArrowStyle> ("right-arrow");
        public static final PicAttributeName<Double> ARROW_GLOBAL_SCALE_WIDTH = new PicAttributeName<Double> ("arrow-global-scale-width");
        public static final PicAttributeName<Double> ARROW_GLOBAL_SCALE_LENGTH = new PicAttributeName<Double> ("arrow-global-scale-length");
        public static final PicAttributeName<Double> ARROW_WIDTH_MINIMUM_MM = new PicAttributeName<Double> ("arrow-head-width-minimum");
        public static final PicAttributeName<Double> ARROW_WIDTH_LINEWIDTH_SCALE = new PicAttributeName<Double> ("arrow-head-width-linewidth-scale");
        public static final PicAttributeName<Double> ARROW_LENGTH_SCALE = new PicAttributeName<Double> ("arrow-head-length-scale");
        public static final PicAttributeName<Double> ARROW_INSET_SCALE = new PicAttributeName<Double> ("arrow-head-inset-scale");
        public static final PicAttributeName<Double> TBAR_WIDTH_MINIMUM_MM = new PicAttributeName<Double> ("arrow-tbar-width-minimum");
        public static final PicAttributeName<Double> TBAR_WIDTH_LINEWIDTH_SCALE = new PicAttributeName<Double> ("arrow-tbar-width-linewidth-scale");
        public static final PicAttributeName<Double> BRACKET_LENGTH_SCALE = new PicAttributeName<Double> ("arrow-bracket-length-scale");
        public static final PicAttributeName<Double> RBRACKET_LENGTH_SCALE = new PicAttributeName<Double> ("arrow-rbracket-length-scale");
        // dotsize -> see below for polydots

        public static final PicAttributeName<StyleConstants.PolydotsStyle> POLYDOTS_STYLE = new PicAttributeName<StyleConstants.PolydotsStyle> ("polydots-style"); // style of ps-dotted AbstractCurve's (aka \\psdots)
        public static final PicAttributeName<Boolean> POLYDOTS_SUPERIMPOSE = new PicAttributeName<Boolean> ("polydots-superimpose"); // superimpose dots on top of underlying curve or not
        // the two followings attributes are also used for circle/disk arrows:
        public static final PicAttributeName<Double> POLYDOTS_SIZE_MINIMUM_MM = new PicAttributeName<Double> ("polydots-size-minimum"); // dotsize (1) = dim
        public static final PicAttributeName<Double> POLYDOTS_SIZE_LINEWIDTH_SCALE = new PicAttributeName<Double> ("polydots-size-linewidth-scale"); // dotsize (2) = num
        public static final PicAttributeName<Double> POLYDOTS_SCALE_H = new PicAttributeName<Double> ("polydots-scale-h"); // dotscale (1)
        public static final PicAttributeName<Double> POLYDOTS_SCALE_V = new PicAttributeName<Double> ("polydots-scale-v"); // dotscale (2)
        public static final PicAttributeName<Double> POLYDOTS_ANGLE = new PicAttributeName<Double> ("polydots-angle"); // dotangle

        public static final PicAttributeName<PicText.VertAlign> TEXT_VERT_ALIGN = new PicAttributeName<PicText.VertAlign> ("text-vert-align");
        public static final PicAttributeName<PicText.HorAlign> TEXT_HOR_ALIGN = new PicAttributeName<PicText.HorAlign> ("text-hor-align");
        public static final PicAttributeName<PicText.FrameStyle> TEXT_FRAME = new PicAttributeName<PicText.FrameStyle> ("text-frame");
        public static final PicAttributeName<Double> TEXT_ROTATION = new PicAttributeName<Double> ("text-rotation"); // angle in degrees

		// handle one-liner/multiline text value
	public static final PicAttributeName<PicText.TextMode> TEXT_MODE = new PicAttributeName<PicText.TextMode> ("text-mode");

	   // display text as icon/as text
        public static final PicAttributeName<PicText.TextIcon> TEXT_ICON = new PicAttributeName<PicText.TextIcon> ("text-icon");

        public static final PicAttributeName<String> PST_CUSTOM = new PicAttributeName<String> ("pstcustom"); // custom pstricks parameters

        public static final PicAttributeName<String> TIKZ_CUSTOM = new PicAttributeName<String> ("tikzcustom");// TikZ paramètre spécifique au mode d'édition

	// end enum

        private String name;

        public PicAttributeName(String name){
		this.name = name;
	}

        public String toString(){
	        return name;
        }

        /**
         * Return the name of the attribute described by this PicAttributeName
         */
        public String getName(){
	        return name;
        }

} // class
