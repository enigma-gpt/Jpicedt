// TikzCustomProperties.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: TikzCustomProperties.java,v 1.8 2013/03/27 07:08:32 vincentb1 Exp $
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
package jpicedt.format.output.tikz;
import jpicedt.Log;
import jpicedt.format.output.util.ColorFormatter.ColorEncoding;
import java.util.*;

import static jpicedt.format.output.tikz.TikzConstants.*;
import static jpicedt.Log.warning;

/**
 * Pr�f�rences utilisateurs pour l'import/export de dessins au format TikZ.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
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
	 * Charge le contenu d'affichage des widgets avec les valeurs par d�faut
	 * prises des <code>TikzContants</code>.
	 * @return un topogramme binaire indiquant que certains attributs chang�s. Le topogramme peut �tre test�
	 * selon les valeurs �num�r�es dans <code>ChangedPropertyMask</code>.
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
	 * Lit une pr�f�rence � valeur bool�enne depuis l'objet prop
	 * @param prop L'objet dans lequel la pr�f�rence est lue
	 * @param key  L'identificateur de la pr�f�rence lue
	 * @param defaultVal  La valeur par d�faut de la pr�f�rence lue.
	 * @return la pr�f�rence lue
	 * @since jPicEdt 1.6
	 */
	private boolean getBooleanPreference(Properties prop,String key,boolean defaultVal){
		return Boolean.valueOf(
			prop.getProperty(key,String.valueOf(defaultVal)));
	}
	/**
	 * Charge les pr�f�rences TikZ depuis prop vers <code>this</code> (op�ration inverse de {@link #store
	 * <code>store</code>}).
	 * @param preferences L'objet depuis lequel on charge les pr�f�rences.
	 * @return un <code>int</code>, pareil que pour {@link #loadDefault() loadDefault}.
	 * @since jPicEdt 1.6
	 */
	public int load(Properties preferences) {
		// � partir de TikzConstants :
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
				warning("valeur invalide pour la pr�f�rence "
						+ KEY_FMT_PREDEFINED_COLOR_SET);
			formatterPredefinedColorSet = DFLT_FMT_PREDEFINED_COLOR_SET;
		}
		return changed;
	}

	/**
	 * �crit une pr�f�rence � valeur bool�enne dans l'objet prop
	 * @param prop  objet Properties dans lequel on �crit la pr�f�rence.
	 * @param key   identificateur de la pr�f�rences.
	 * @param value valeur �crite.
	 * @since jPicEdt 1.6
	 */
	private void setBooleanPreference(Properties prop,String key,boolean value){
		prop.setProperty(key,String.valueOf(value));
	}

	/**
	 * Stocke dans l'objet <code>preferences</code> les propri�t�s r�sidant dans <code>this</code> (op�ration
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
