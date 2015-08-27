// StyleConstants.java --- -*- coding: iso-8859-1 -*-
// March 30, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: StyleConstants.java,v 1.18 2013/03/27 07:01:03 vincentb1 Exp $
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

import java.awt.*;

import static jpicedt.graphic.model.PicAttributeName.*;
import static java.lang.Math.abs;

/**
 * A collection of enum's for <code>PicAttributeSet</code> attribute values.
 * @author Sylvain Reynal
 * @since jpicedt 1.3.2
 * @see jpicedt.graphic.model.PicAttributeSet
 * @version $Id: StyleConstants.java,v 1.18 2013/03/27 07:01:03 vincentb1 Exp $
 *
 */
public class StyleConstants {

	/**
	 * a PostScript point in mm
	 */
     	public static final double PS_POINT = 25.6 / 72.0; // 0.35555... mm

    	///////////////////// STROKE /////////////////////

	public static enum LineStyle {
		NONE("none"),
		SOLID("solid"), // PsTricks default
		DASHED("dashed"),
		DOTTED("dotted");

		private String value;

		LineStyle(String value){
			this.value=value;
		}
		public String toString(){
			return value;
		}
	}


	// [pending] what is it used for?
	public static final Double DASH1 = new Double(1.0);
	public static final Double DASH2 = new Double(2.0);
	public static final Double DASH3 = new Double(3.0);
	public static final Double DOT_SEP1 = new Double(1.0);
	public static final Double DOT_SEP2 = new Double(2.0);
	public static final Double DOT_SEP3 = new Double(3.0);

	//////////////////// DIMEN ///////////////////////////////

	public static enum Dimen {
		OUTER("outer"),
		MIDDLE("middle"),
		INNER("inner");

		private String value;

		Dimen(String value){
			this.value=value;
		}
		public String toString(){
			return value;
		}
	}

	//////////////////////// FILL //////////////////////////
	public static enum FillStyle {
		NONE("none"),
		SOLID("solid"), // PsTricks default
		VLINES("vlines"),
		VLINES_FILLED("vlines-filled"),
		HLINES("hlines"),
		HLINES_FILLED("hlines-filled"),
		CROSSHATCH("crosshatch"),
		CROSSHATCH_FILLED("crosshatch-filled");

		private String value;

		FillStyle(String value){
			this.value=value;
		}
		public String toString(){
			return value;
		}
	}

	/**
	 * Return the hatch angle, guaranteed to be b/w -90 and 90.
	 * @deprecated
	 */
	public static double getHatchAngle(PicAttributeSet set){
		double a = set.getAttribute(HATCH_ANGLE)%180; // b/w -180 and 180
		if (a<-90) a+= 180;
		else if (a>90) a-=180;
		return a;
	}

	/////////////////// PSDOTS ///////////////////////////////////

	public static enum PolydotsStyle {
		NONE("none"),
		POLYDOTS_DISK("polydots-disk"), // [pending]  remove leading POLYDOTS_
		POLYDOTS_CIRCLE("polydots-circle"),
		POLYDOTS_PLUS("polydots-plus"),
		POLYDOTS_TRIANGLE("polydots-triangle"),
		POLYDOTS_TRIANGLE_FILLED("polydots-triangle-filled"),
		POLYDOTS_SQUARE("polydots-square"),
		POLYDOTS_SQUARE_FILLED("polydots-square-filled"),
		POLYDOTS_PENTAGON("polydots-pentagon"),
		POLYDOTS_PENTAGON_FILLED("polydots-pentagon-filled");

		private String value;

		PolydotsStyle(String value){
			this.value=value;
		}
		public String toString(){
			return value;
		}
	}

	/////////////////// ARROWS ///////////////////////////////////

	public static enum ArrowStyle {
	        NONE("none"),
 	        ARROW_HEAD("head"),
	        REVERSE_ARROW_HEAD("reverse-head"),
	        DOUBLE_ARROW_HEAD("double-head"),
	        DOUBLE_REVERSE_ARROW_HEAD("double-reverse-head"),
	        T_BAR_CENTERED("t-bar-centered"),
	        T_BAR_FLUSHED("t-bar-flushed"),
	        SQUARE_BRACKET("square-bracket"),
	        ROUNDED_BRACKET("rounded-bracket"),
	        CIRCLE_FLUSHED("circle-flushed"),
	        CIRCLE_CENTERED("circle-centered"),
	        DISK_FLUSHED("disk-flushed"),
	        DISK_CENTERED("disk-centered");
		//ROUNDED_END_FLUSH [todo]
		//ROUNDED_END_EXTENDED [todo]
		//SQUARE_END [todo]

		private String value;

		ArrowStyle(String value){
			this.value=value;
		}
		public String toString(){
			return value;
		}
	}



}
