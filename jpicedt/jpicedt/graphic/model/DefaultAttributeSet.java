// DefaultAttributeSet.java --- -*- coding: iso-8859-1 -*-
// March 30, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: DefaultAttributeSet.java,v 1.25 2013/03/27 07:02:59 vincentb1 Exp $
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
import java.util.*;

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicText.*;

/**
 * This an immutable attribute set that stores default attributes value, and can thus serve as
 * the default resolving parent for mutable PicAttributeSet's. The <code>setAttribute()</code> method
 * is overriden so as to do nothing. This set can't have any resolving parent.<p>
 * This is how to add a new default attribute pair :
 * <ul>
 * <li>In <code>PicObjectConstants</code>, define a new static final PicAttributeName
 *     with an explicit name (possibly following XML usual syntax, that is with hyphen-separated words)</li>
 * <li>Possibly define corresponding attribute values, if this makes sense (e.g. predefinite String's)</li>
 * <li>Initialize the pair in the constructor just below</li>
 * <p>
 * Refactored 30/03/2002 (hashtable)
 *
 */
/* package private */ class DefaultAttributeSet extends PicAttributeSet {

	/**
	 * Create a new PicAttributeSet with default values conforming to PsTricks default values, i.e. :
	 * <ul>
	 * <li>Line : style = solid, colours = black, width = Options.lineThickness</li>
	 * <li>Dash (transparent and opaque) = predefinite PicObjectConstants.DASH1</li>
	 * <li>Dot sep = predefinite PicObjectConstants.DOT_SEP1</li>
	 * <li>Double line : none, sep = 1.25*Options.lineThickness, colour = white</li>
	 * <li>Shadow : none, size=3pt, angle=-45, colour=dark gray</li>
	 * <li>Dimen = outer</li>
	 * <li>Fill : style = none, colour = black</li>
	 * <li>Hatch : width = 0.8pt, sep=4pt, colour=black, angle=45</li>
	 * <li>Arrows : left = no, right = no</li>
	 * </ul>
	 *
	 * @see jpicedt.graphic.model.PicObjectConstants
	 */
	/* package private */ DefaultAttributeSet(){

		super();
		parent=null;

		// Note that all values are given in "mm" when applicable ; besides, PS_POINT (i.e. 1pt) is defined in PicObjectConstants.
		// These default values mimick those of PsTricks as much as possible, except that some rounding is applied now and often when the corresponding
		// value would have too many digits once expressed in mm.

		double lineThickness = 0.3; // ~ 0.8pt

		_setAttribute(LINE_STYLE, LineStyle.SOLID);
		_setAttribute(LINE_COLOR, Color.black);
		_setAttribute(LINE_WIDTH, new Double(lineThickness));

		_setAttribute(DASH_TRANSPARENT, DASH1);
		_setAttribute(DASH_OPAQUE, DASH1);
		_setAttribute(DOT_SEP,DOT_SEP1);

		_setAttribute(DOUBLE_LINE, Boolean.FALSE);
		_setAttribute(DOUBLE_SEP,new Double(0.4)); // 0.4mm
		_setAttribute(DOUBLE_COLOR, Color.white);

		_setAttribute(SHADOW,Boolean.FALSE);
		_setAttribute(SHADOW_SIZE,new Double(1.0)); // 1mm
		_setAttribute(SHADOW_ANGLE, new Double(-45.0));
		_setAttribute(SHADOW_COLOR, Color.darkGray);

		_setAttribute(DIMEN, Dimen.OUTER);

		_setAttribute(FILL_STYLE,FillStyle.NONE);
		_setAttribute(FILL_COLOR,Color.black);

		_setAttribute(HATCH_WIDTH, new Double(0.3)); // .3mm
		_setAttribute(HATCH_SEP, new Double (1.5));// 1.5mm
		_setAttribute(HATCH_COLOR, Color.black);
		_setAttribute(HATCH_ANGLE, new Double(45));

		// see PsTricks's doc p.30 for arrow default values:
		_setAttribute(LEFT_ARROW, StyleConstants.ArrowStyle.NONE);
		_setAttribute(RIGHT_ARROW,StyleConstants.ArrowStyle.NONE);
		_setAttribute(ARROW_GLOBAL_SCALE_WIDTH, new Double(1.0));
		_setAttribute(ARROW_GLOBAL_SCALE_LENGTH, new Double(1.0));
		_setAttribute(ARROW_WIDTH_MINIMUM_MM, new Double(1));
		_setAttribute(ARROW_WIDTH_LINEWIDTH_SCALE, new Double(2));
		_setAttribute(ARROW_LENGTH_SCALE, new Double(1));
		_setAttribute(ARROW_INSET_SCALE, new Double(0.25));
		_setAttribute(TBAR_WIDTH_MINIMUM_MM, new Double(.7)); // ~ 2pt
		_setAttribute(TBAR_WIDTH_LINEWIDTH_SCALE, new Double(5.0));
		_setAttribute(BRACKET_LENGTH_SCALE, new Double(0.15));
		_setAttribute(RBRACKET_LENGTH_SCALE, new Double(0.15));
		// note also that dot parameters for arrows (i.e. circles and disks) are the same as for polydots; hence see below at POLYDOTS_SIZE_XXXX

		_setAttribute(POLYDOTS_STYLE,PolydotsStyle.NONE);
		_setAttribute(POLYDOTS_SUPERIMPOSE,Boolean.FALSE);
		_setAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE,new Double(2.5)); // 2.5 * line-width + minimum ; see pstricks doc p.30
		_setAttribute(POLYDOTS_SIZE_MINIMUM_MM,new Double(0.7));  // 2pt ~ 0.7mm (and PsTricks doc contains a mistake for that matter...)
		_setAttribute(POLYDOTS_SCALE_H,new Double(1));
		_setAttribute(POLYDOTS_SCALE_V,new Double(1));
		_setAttribute(POLYDOTS_ANGLE,new Double(0));

		_setAttribute(OVER_STRIKE, Boolean.FALSE);
		_setAttribute(OVER_STRIKE_WIDTH, new Double(lineThickness));
		_setAttribute(OVER_STRIKE_COLOR, Color.WHITE);

		_setAttribute(PST_CUSTOM,"");
		_setAttribute(TIKZ_CUSTOM,"");

		_setAttribute(TEXT_MODE, TextMode.TEXT_FIELD);
		_setAttribute(TEXT_ICON, TextIcon.TEXT_MODE);
		_setAttribute(TEXT_VERT_ALIGN,VertAlign.CENTER);
		_setAttribute(TEXT_HOR_ALIGN,HorAlign.CENTER);
		_setAttribute(TEXT_FRAME,FrameStyle.NO_FRAME);
		_setAttribute(TEXT_ROTATION, new Double(0));
	}

	/**
	 * Overriden to do nothing since this is an immutable set.
	 */
	public <T> void setAttribute(PicAttributeName<T> name, T value){
	}

	/**
	 * Convenience initialization method
	 * @param name attribute name
	 * @param value attribute value
	 */
	private <T> void _setAttribute(PicAttributeName<T> name, T value){
		// check that value.getClass() is assignable from name's allowedValueClass, i.e. value has the expected runtime type :
		//if (!name.getAllowedValueClass().isInstance(value)) throw new IllegalArgumentException("Illegal value class: "+value.getClass()+ " when "+name.getAllowedValueClass()+" was expected !");

		// check if name exist in this set :
		if (map.containsKey(name)) {
			// if name/value is not already defined here, replace old value with new one :
			if (!map.get(name).equals(value)) {
				map.put(name,value);
			}
			// otherwise leave unchanged
		}
		// otherwise add new name/value pair :
		else {
			map.put(name,value);
		}
	}

	/**
	 * @return the attribute with the given name, or null if the attribute name is not defined here.
	 */
	public <T> T getAttribute(PicAttributeName<T> name){
		if (map.containsKey(name)) return (T)map.get(name); // unchecked cast, ok, but how do we get rid of this w/o being stricter on the backing HashMap? (which incidentally turns out to be impossible)
		else return null;
	}

	/**
	 * set the resolving parent. Overriden to do nothing.
	 * @param parent if null, set the parent to DEFAULT_SET.
	 */
	public void setResolveParent(PicAttributeSet parent){
	}

	/**
	 * @return the resolving parent, i.e. null.
	 */
	public PicAttributeSet getResolveParent(){
		return null;
	}

	/**
	 * return this set (since it's immutable, a copy just makes no sense ; use new PicAttributeSet(this)
	 * if you really want to clone this set).
	 */
	public PicAttributeSet copyAttributes() {
		return this;
	}

	/** debug */
	public static void main(String[] args){
		DefaultAttributeSet set = new DefaultAttributeSet();
		set._setAttribute(LINE_WIDTH, 1.);
		double x = set.getAttribute(LINE_WIDTH);
		System.out.println(set);

	}

} // class
