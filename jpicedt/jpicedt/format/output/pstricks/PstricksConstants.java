// PstricksConstants.java --- -*- coding: iso-8859-1 -*-
// March 1, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
//
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (�NS�A)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: PstricksConstants.java,v 1.18 2013/03/27 07:23:10 vincentb1 Exp $
// Keywords:
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

package jpicedt.format.output.pstricks;

import java.awt.Color;
import jpicedt.graphic.view.ArrowView;
import jpicedt.format.output.util.ColorFormatter.ColorEncoding;
import jpicedt.Log;

import static jpicedt.Log.warning;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Useful constants used across the jpicedt.format.output.pstricks package.
 * @author Sylvain Reynal
 * @version $Id: PstricksConstants.java,v 1.18 2013/03/27 07:23:10 vincentb1 Exp $
 */
public class PstricksConstants {

	/**
	 * Line separator for the current platform (i.e. "\n" on Unix, "\n\r" on Windows, "\r" on Mac,..)
	 */
	public static final String[] PRPTY_KEY_DEFAULT_TABLE = {
		"pstricks.file-wrapper-prolog",
		"\\documentclass{article} \n\\usepackage{pst-all}\n"
		+ "\\thispagestyle{empty}\n\\begin{document}\n",
		"pstricks.file-wrapper-epilog",
		"\\end{document}"
	};
	public static enum PstricksAngleCorrection{
		ANGLE_CORRECTION_BY_PSTRICKS_AUTO(0,"format.pstricks.AngleCorrection.pstricksAuto"),
		ANGLE_CORRECTION_BY_JPICEDT_AUTO(1,"format.pstricks.AngleCorrection.jpicedtAuto"),
		ANGLE_CORRECTION_NONE(2,"format.pstricks.AngleCorrection.none"),
		ANGLE_CORRECTION_BY_PSTRICKS(3,"format.pstricks.AngleCorrection.pstricks"),
		ANGLE_CORRECTION_BY_JPICEDT(4,"format.pstricks.AngleCorrection.jpicedt");

		private final int value;
		private final String key;

		PstricksAngleCorrection(int value, String key){
			this.value = value;
			this.key = key;
		}

		static PstricksAngleCorrection enumOf(int value){
			return values()[value];
		}

		static PstricksAngleCorrection enumOf(String key){
			for(int i = 0; i < values().length; ++i){
				if(values()[i].key.equals(key))
					return values()[i];
			}
			if(Log.DEBUG)
				warning("key non d�finie");
			return DEFAULT_ANGLE_CORRECTION;
		}

		int value(){ return this.value; }
		String key(){ return this.key; }
		static String[] keys(){
			String[] ret = new String[values().length];
			for(PstricksAngleCorrection e : values())
				ret[e.value] = e.key;
			return ret;
		}
	};

	public static final PstricksAngleCorrection DEFAULT_ANGLE_CORRECTION =
		PstricksAngleCorrection.ANGLE_CORRECTION_BY_PSTRICKS_AUTO;
	public static final ColorEncoding DEFAULT_FORMATTER_PREDEFINED_COLOR_SET =
		ColorEncoding.PSTRICKS;
	public static final ColorEncoding DEFAULT_PARSER_PREDEFINED_COLOR_SET =
		ColorEncoding.PSTRICKS;

	public static final String RESCALING_TEX_FUNCTION = "\\JPicScale";

	public static enum PSTArrow {
	       NONE(ArrowStyle.NONE,"",""),
	       ARROW_HEAD(ArrowStyle.ARROW_HEAD,"<",">"),
	       REVERSE_ARROW_HEAD(ArrowStyle.REVERSE_ARROW_HEAD,">","<"),
	       DOUBLE_ARROW_HEAD(ArrowStyle.DOUBLE_ARROW_HEAD,"<<",">>"),
	       DOUBLE_REVERSE_ARROW_HEAD(ArrowStyle.DOUBLE_REVERSE_ARROW_HEAD,">>","<<"),
	       T_BAR_CENTERED(ArrowStyle.T_BAR_CENTERED,"|*","|*"),
	       T_BAR_FLUSHED(ArrowStyle.T_BAR_FLUSHED,"|","|"),
	       SQUARE_BRACKET(ArrowStyle.SQUARE_BRACKET,"[","]"),
	       ROUNDED_BRACKET(ArrowStyle.ROUNDED_BRACKET,"(",")"),
	       CIRCLE_FLUSHED(ArrowStyle.CIRCLE_FLUSHED,"oo","oo"),
	       CIRCLE_CENTERED(ArrowStyle.CIRCLE_CENTERED,"o","o"),
	       DISK_FLUSHED(ArrowStyle.DISK_FLUSHED,"**","**"),
	       DISK_CENTERED(ArrowStyle.DISK_CENTERED,"*","*");

		private final ArrowStyle a;
		private final String l,r;

		PSTArrow(ArrowStyle a, String l, String r){
			    this.a = a;
			    this.l = l;
			    this.r = r;
		}

		public ArrowStyle getArrowStyle(){
			    return a;
		}

		public String getString(ArrowView.Direction d){
			    switch (d){
			    case LEFT:return l;
			    case RIGHT: return r;
			    default: return null;
			    }
		}
	}
}
