// TikzConstants.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: TikzConstants.java,v 1.11 2013/06/18 20:48:32 vincentb1 Exp $
// Keywords: Tikz, PGF
// X-URL: http://www.jpicedt.org/
//
// Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilit� au code source et des droits de copie, de modification et de
// redistribution accord�s par cette licence, il n'est offert aux utilisateurs qu'une garantie limit�e.  Pour
// les m�mes raisons, seule une responsabilit� restreinte p�se sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les conc�dants successifs.
//
// � cet �gard l'attention de l'utilisateur est attir�e sur les risques associ�s au chargement, �
// l'utilisation, � la modification et/ou au d�veloppement et � la reproduction du logiciel par l'utilisateur
// �tant donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � manipuler et qui le r�serve
// donc � des d�veloppeurs et des professionnels avertis poss�dant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invit�s � charger et tester l'ad�quation du logiciel � leurs
// besoins dans des conditions permettant d'assurer la s�curit� de leurs syst�mes et ou de leurs donn�es et,
// plus g�n�ralement, � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�.
//
// Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accept� les termes.
//
/// Commentary:

//



/// Code:
package jpicedt.format.output.tikz;
import java.awt.Color;
import java.util.BitSet;

import jpicedt.graphic.view.ArrowView;
import jpicedt.format.output.util.ColorFormatter.ColorEncoding;
import static jpicedt.graphic.model.StyleConstants.*;


public class TikzConstants
{
	public enum DrawFlags{
		SWAP_ARROWS(0), MASK_ROTATE(1),SIZE(2);
		int value;
		public int getValue(){ return value; }
		DrawFlags(int value){ this.value = value; }
	};
	public static final BitSet   EMPTY_BITSET = new BitSet();

	static private BitSet makeSwapArrowsBS(){
		BitSet ret = new BitSet(DrawFlags.SIZE.getValue());
		ret.set(DrawFlags.SWAP_ARROWS.getValue());
		return ret; 
	}
	public static final BitSet   SWAP_ARROWS_BITSET = makeSwapArrowsBS();

	static private BitSet makeMaskRotateBS(){
		BitSet ret = new BitSet(DrawFlags.SIZE.getValue());
		ret.set(DrawFlags.MASK_ROTATE.getValue());
		return ret; 
	}
	public static final BitSet   MASK_ROTATE_BITSET = makeMaskRotateBS();

	public static final String[] PRPTY_KEY_DEFAULT_TABLE = {
		"tikz.file-wrapper-prolog",
		"\\documentclass{article}\n"
		+ "\\usepackage{tikz}\n"
		+ "\\thispagestyle{empty}\n"
		+ "\\begin{document}\n",
		"tikz.file-wrapper-epilog",
		"\\end{document}"
	};
	public static final String RESCALING_TEX_MACRO = "\\JPicScale";

	//-----------------------------------------------------------------------
	// Clefs d'identification des propri�t�s
	//-----------------------------------------------------------------------
	public static final String KEY_FMT_HAS_ARROW_TIP_PACKAGE       ="tikz.fmt.has.arrow.tip.package";
	public static final String KEY_FMT_CLIP_BASED_ON_JPE_BB        ="tikz.fmt.clip.based.on.jpe.bb";
	public static final String KEY_FMT_PREDEFINED_COLOR_SET = "tikz.fmt.predefined.color.set";
	//-----------------------------------------------------------------------
	// Valeurs par d�faut des propri�t�s
	//-----------------------------------------------------------------------
	public static final boolean DFLT_FMT_HAS_ARROW_TIP_PACKAGE        = false;
	public static final boolean DFLT_FMT_CLIP_BASED_ON_JPE_BB         = false;
	public static final ColorEncoding DFLT_FMT_PREDEFINED_COLOR_SET = ColorEncoding.XCOLOR;

	public static enum TZArrow {
		NONE(
			ArrowStyle.NONE,
			"",null,null,null,ArrowStyle.NONE,0),
			ARROW_HEAD(
				ArrowStyle.ARROW_HEAD,
				"stealth",null,null,null,ArrowStyle.ARROW_HEAD,1),
			REVERSE_ARROW_HEAD(
				ArrowStyle.REVERSE_ARROW_HEAD,
				"stealth reversed",null,null,null,ArrowStyle.ARROW_HEAD,-1),
			DOUBLE_ARROW_HEAD(
				ArrowStyle.DOUBLE_ARROW_HEAD,
				"stealth",null,null,null,ArrowStyle.DOUBLE_ARROW_HEAD,2),
			DOUBLE_REVERSE_ARROW_HEAD(
				ArrowStyle.DOUBLE_REVERSE_ARROW_HEAD,
				"stealth reversed",null,null,null,ArrowStyle.ARROW_HEAD,-2),
			T_BAR_CENTERED(
				ArrowStyle.T_BAR_CENTERED,
				"|",null,null,null,ArrowStyle.T_BAR_CENTERED,1),
			T_BAR_FLUSHED(
				ArrowStyle.T_BAR_FLUSHED,
				"|",null,null,null,ArrowStyle.T_BAR_FLUSHED,1),
			SQUARE_BRACKET(
				ArrowStyle.SQUARE_BRACKET,
				"[","]",null,TZArrow.T_BAR_FLUSHED, ArrowStyle.SQUARE_BRACKET,1),
			ROUNDED_BRACKET(
				ArrowStyle.ROUNDED_BRACKET,
				"(",")",null,TZArrow.T_BAR_FLUSHED,ArrowStyle.ROUNDED_BRACKET,1),
			CIRCLE_FLUSHED(
				ArrowStyle.CIRCLE_FLUSHED,
				"o",null,null,TZArrow.T_BAR_FLUSHED,ArrowStyle.CIRCLE_FLUSHED,1),
			CIRCLE_CENTERED(
				ArrowStyle.CIRCLE_CENTERED,
				"o",null,null,TZArrow.T_BAR_FLUSHED,ArrowStyle.CIRCLE_CENTERED,1),
			DISK_FLUSHED(
				ArrowStyle.DISK_FLUSHED,
				"*",null,null,TZArrow.T_BAR_FLUSHED,ArrowStyle.DISK_FLUSHED,1),
			DISK_CENTERED(
				ArrowStyle.DISK_CENTERED,
				"*",null,"-(1.8pt+1.4\\pgflinewidth)",TZArrow.T_BAR_FLUSHED,ArrowStyle.DISK_CENTERED,1);

		private ArrowStyle a;
		private String l,r;
		private String shorten;
		private TZArrow fallbackStyle;
		private ArrowStyle element;
		private int count;

		/**
		 * Cr�e un nouvel examplaire de <code>TZArrow</code>.
		 *
		 * @param a l'<code>ArrowStyle</code> d�crit par la constante �num�r�e.
		 * @param l le <code>String</code> donnant le code TikZ correspondant
		 * s'il s'agit de la pointe de gauche et que le paquetage TikZ Arrows est charg�.
		 * @param r le <code>String</code> donnant le code TikZ correspondant
		 * s'il s'agit de la pointe de gauche et que le paquetage TikZ Arrows
		 * est charg�. null si identifque au code de la pointe de gauche.
		 * @param fallbackStyle si non-<code>null</code>, l'<code>ArrowStyle</code> � utiliser � la place de
		 * <code>this</code> pour le codage.
		 * @param element l'<code>ArrowStyle</code> �l�mentaire correspndant �
		 * l'<code>ArrowStyle</code> � coder en Tikz.
		 * @param count indique un nombre de r�p�tition de
		 * l'<code>ArrowStyle</code> �l�mentaire, un nombre n�gatif indique
		 * que l'<code>ArrowStyle</code> �l�mentaire est invers�.
		 */
		TZArrow(ArrowStyle a, String l, String r,String shorten,TZArrow fallbackStyle,ArrowStyle element,
				int count){
			    this.a = a;
			    this.l = l;
			    this.r = r;
				this.shorten=shorten;
			    this.fallbackStyle = fallbackStyle;
				this.element = element;
				this.count = count;
		}

		public TZArrow getTZArrow(boolean hasArrowTipPackage){
			if(hasArrowTipPackage || fallbackStyle == null)
				return this;
			else
				return fallbackStyle;
		}

		public ArrowStyle getArrowStyle(){
			    return a;
		}
		public ArrowStyle getElementArrowStyle(){
			return element;
		}
		public int getCount(){
			return count;
		}

		public String getString(ArrowView.Direction d){
			switch (d)
			{
			case LEFT:
				return l;
			case RIGHT:
				if(r != null)
					return r;
				else
					return l;
			default:
				return null;
			}
		}
		public String getShorten(ArrowView.Direction d){
			return shorten;
		}

	}

}


/// TikzConstants.java ends here
