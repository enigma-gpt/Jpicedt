// PstricksCustomProperties.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: PstricksCustomProperties.java,v 1.5 2013/03/27 12:09:35 vincentb1 Exp $
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
import jpicedt.Log;
import java.util.*;
import jpicedt.format.output.util.ColorFormatter.ColorEncoding;
import static jpicedt.format.output.pstricks.PstricksConstants.*;
import static jpicedt.Log.warning;

public class PstricksCustomProperties implements PstricksCustomization{
	private PstricksAngleCorrection angleCorrection = DEFAULT_ANGLE_CORRECTION;
	private ColorEncoding formatterPredefinedColorSet = DEFAULT_FORMATTER_PREDEFINED_COLOR_SET;
	private ColorEncoding parserPredefinedColorSet = DEFAULT_PARSER_PREDEFINED_COLOR_SET;

	private final String ANGLE_CORRECTION_PREFERENCE_KEY = "pstricks.fmt.AngleCorrection";
	private final String FORMATTER_PREDEFINED_COLOR_SET_PREFERENCE_KEY = "pstricks.fmt.PredefinedColorSet";
	private final String PARSER_PREDEFINED_COLOR_SET_PREFERENCE_KEY = "pstricks.parse.PredefinedColorSet";

	public PstricksCustomProperties(){}
	public PstricksCustomProperties(int i){
			switch(i)
			{
			case 0:
				loadDefault();
				break;
			default:
				Log.error("Argument inattendu");
				break;
			}
	}

	public PstricksAngleCorrection getAngleCorrection(){
		return angleCorrection;
	}
	public void setAngleCorrection(PstricksAngleCorrection angleCorrection){
		this.angleCorrection = angleCorrection;
	}


	public ColorEncoding getFormatterPredefinedColorSet(){
		return formatterPredefinedColorSet;
	}
	public void setFormatterPredefinedColorSet(ColorEncoding predefinedColorSet){
		this.formatterPredefinedColorSet = predefinedColorSet;
	}

	public ColorEncoding getParserPredefinedColorSet(){
		return parserPredefinedColorSet;
	}
	public void setParserPredefinedColorSet(ColorEncoding predefinedColorSet){
		this.parserPredefinedColorSet = predefinedColorSet;
	}


	public void store(Properties preferences){
		preferences.setProperty(ANGLE_CORRECTION_PREFERENCE_KEY,
								angleCorrection.key());
		preferences.setProperty(FORMATTER_PREDEFINED_COLOR_SET_PREFERENCE_KEY,
								formatterPredefinedColorSet.toString());

		preferences.setProperty(PARSER_PREDEFINED_COLOR_SET_PREFERENCE_KEY,
								parserPredefinedColorSet.toString());
	}
	public int loadDefault(){
		this.angleCorrection = DEFAULT_ANGLE_CORRECTION;
		this.formatterPredefinedColorSet = DEFAULT_FORMATTER_PREDEFINED_COLOR_SET;
		this.parserPredefinedColorSet = DEFAULT_PARSER_PREDEFINED_COLOR_SET;
		return 0;
	}
	public int load(Properties preferences){
		angleCorrection = PstricksAngleCorrection.enumOf(
			preferences.getProperty(ANGLE_CORRECTION_PREFERENCE_KEY,
									DEFAULT_ANGLE_CORRECTION.key()));
		formatterPredefinedColorSet = ColorEncoding.enumOf(
			preferences.getProperty(FORMATTER_PREDEFINED_COLOR_SET_PREFERENCE_KEY,
									DEFAULT_FORMATTER_PREDEFINED_COLOR_SET.toString()));
		if(formatterPredefinedColorSet == null){
			if(Log.DEBUG)
				warning("valeur invalide pour la préférence "
						+ FORMATTER_PREDEFINED_COLOR_SET_PREFERENCE_KEY);
			formatterPredefinedColorSet = DEFAULT_FORMATTER_PREDEFINED_COLOR_SET;
		}

		parserPredefinedColorSet = ColorEncoding.enumOf(
			preferences.getProperty(PARSER_PREDEFINED_COLOR_SET_PREFERENCE_KEY,
									DEFAULT_PARSER_PREDEFINED_COLOR_SET.toString()));
		if(parserPredefinedColorSet == null){
			if(Log.DEBUG)
				warning("valeur invalide pour la préférence "
						+ PARSER_PREDEFINED_COLOR_SET_PREFERENCE_KEY);
			parserPredefinedColorSet = DEFAULT_PARSER_PREDEFINED_COLOR_SET;
		}

		return 0;
	}
};

/// PstricksCustomProperties.java ends here
