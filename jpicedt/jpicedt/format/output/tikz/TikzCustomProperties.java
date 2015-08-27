// TikzCustomProperties.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: TikzCustomProperties.java,v 1.8 2013/03/27 07:08:32 vincentb1 Exp $
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
package jpicedt.format.output.tikz;
import jpicedt.Log;
import jpicedt.format.output.util.ColorFormatter.ColorEncoding;
import java.util.*;

import static jpicedt.format.output.tikz.TikzConstants.*;
import static jpicedt.Log.warning;

/**
 * Préférences utilisateurs pour l'import/export de dessins au format TikZ.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jpicedt 1.6
 */
public class TikzCustomProperties implements TikzCustomization
{
   	private boolean hasArrowTipPackage = DFLT_FMT_HAS_ARROW_TIP_PACKAGE;
	private boolean clipBasedOnJPE_BB = DFLT_FMT_CLIP_BASED_ON_JPE_BB;
	private ColorEncoding formatterPredefinedColorSet = DFLT_FMT_PREDEFINED_COLOR_SET;

	public enum ChangedPropertyMask{
		HAS_ARROW_TIP_PACKAGE(1);
		ChangedPropertyMask(int value) { this.value = value; }
		private final int value;
		public int value() { return value; }
	};

	public boolean getHasArrowTipPackage(){      return hasArrowTipPackage;      }
	public boolean getClipBasedOnJPE_BB(){      return clipBasedOnJPE_BB;      }

   	public void setHasArrowTipPackage(boolean val){      hasArrowTipPackage      = val; }
   	public void setClipBasedOnJPE_BB(boolean val){      clipBasedOnJPE_BB      = val; }

	public ColorEncoding getFormatterPredefinedColorSet(){
		return formatterPredefinedColorSet;
	}
	public void setFormatterPredefinedColorSet(ColorEncoding predefinedColorSet){
		this.formatterPredefinedColorSet = predefinedColorSet;
	}

	/**
	 * @since jPicEdt 1.6
	 */
	TikzCustomProperties(){}

	/**
	 * @since jPicEdt 1.6
	 */
	TikzCustomProperties(int i)
		{
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

	/**
	 * Charge le contenu d'affichage des widgets avec les valeurs par défaut
	 * prises des <code>TikzContants</code>.
	 * @return un topogramme binaire indiquant que certains attributs changés. Le topogramme peut être testé
	 * selon les valeurs énumérées dans <code>ChangedPropertyMask</code>.
	 * @since jPicEdt 1.6
	 */
	public int loadDefault() {
		int changed = 0;
		if(hasArrowTipPackage != DFLT_FMT_HAS_ARROW_TIP_PACKAGE){
			hasArrowTipPackage = DFLT_FMT_HAS_ARROW_TIP_PACKAGE;
			changed |= ChangedPropertyMask.HAS_ARROW_TIP_PACKAGE.value();
		}
		clipBasedOnJPE_BB = DFLT_FMT_CLIP_BASED_ON_JPE_BB;
		formatterPredefinedColorSet = DFLT_FMT_PREDEFINED_COLOR_SET;

		return changed;
	}

	/**
	 * Lit une préférence à valeur booléenne depuis l'objet prop
	 * @param prop L'objet dans lequel la préférence est lue
	 * @param key  L'identificateur de la préférence lue
	 * @param defaultVal  La valeur par défaut de la préférence lue.
	 * @return la préférence lue
	 * @since jPicEdt 1.6
	 */
	private boolean getBooleanPreference(Properties prop,String key,boolean defaultVal){
		return Boolean.valueOf(
			prop.getProperty(key,String.valueOf(defaultVal)));
	}
	/**
	 * Charge les préférences TikZ depuis prop vers <code>this</code> (opération inverse de {@link #store
	 * <code>store</code>}).
	 * @param preferences L'objet depuis lequel on charge les préférences.
	 * @return un <code>int</code>, pareil que pour {@link #loadDefault() loadDefault}.
	 * @since jPicEdt 1.6
	 */
	public int load(Properties preferences) {
		// à partir de TikzConstants :
		int changed = 0;
		boolean newVal = getBooleanPreference(preferences,
													   KEY_FMT_HAS_ARROW_TIP_PACKAGE,
													   DFLT_FMT_HAS_ARROW_TIP_PACKAGE);
		if(hasArrowTipPackage != newVal){
			changed |= ChangedPropertyMask.HAS_ARROW_TIP_PACKAGE.value();
			hasArrowTipPackage = newVal;
		}
		clipBasedOnJPE_BB    = getBooleanPreference(preferences,
													KEY_FMT_CLIP_BASED_ON_JPE_BB,
													DFLT_FMT_CLIP_BASED_ON_JPE_BB);
		formatterPredefinedColorSet = ColorEncoding.enumOf(
			preferences.getProperty(KEY_FMT_PREDEFINED_COLOR_SET,
									DFLT_FMT_PREDEFINED_COLOR_SET.toString()));
		if(formatterPredefinedColorSet == null){
			if(Log.DEBUG)
				warning("valeur invalide pour la préférence "
						+ KEY_FMT_PREDEFINED_COLOR_SET);
			formatterPredefinedColorSet = DFLT_FMT_PREDEFINED_COLOR_SET;
		}
		return changed;
	}

	/**
	 * écrit une préférence à valeur booléenne dans l'objet prop
	 * @param prop  objet Properties dans lequel on écrit la préférence.
	 * @param key   identificateur de la préférences.
	 * @param value valeur écrite.
	 * @since jPicEdt 1.6
	 */
	private void setBooleanPreference(Properties prop,String key,boolean value){
		prop.setProperty(key,String.valueOf(value));
	}

	/**
	 * Stocke dans l'objet <code>preferences</code> les propriétés résidant dans <code>this</code> (opération
	 * inverse de {@link #load <code>load</code>}).
	 * @since jPicEdt 1.6
	 */
	 public void store(Properties preferences){
		 setBooleanPreference(preferences,KEY_FMT_HAS_ARROW_TIP_PACKAGE
							  ,hasArrowTipPackage     );
		 setBooleanPreference(preferences,KEY_FMT_CLIP_BASED_ON_JPE_BB
							  ,clipBasedOnJPE_BB );

		 preferences.setProperty(KEY_FMT_PREDEFINED_COLOR_SET,
								 formatterPredefinedColorSet.toString());

	}

}// fin de class TikzCustomProperties

/// TikzCustomProperties.java ends here
