// Localizer.java --- -*- coding: iso-8859-1 -*-
// March 1, 2002 - jPicEdt 1.3.2, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: Localizer.java,v 1.17 2013/03/27 06:53:06 vincentb1 Exp $
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
package jpicedt;

import java.io.*;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;


/**
 * Localizer for all classes of the jpicedt's tree.<br>
 * It uses "lang/i18n_xx_yy.properties" resource file, where xx_yy is the Locale.
 * If not found, uses "lang/i18n.properties" (same as i18n_en.properties)
 * As of 2005/12, support for localized help-files added by Jobst Hoffmann.
 * @author Sylvain Reynal
 * @version $Id: Localizer.java,v 1.17 2013/03/27 06:53:06 vincentb1 Exp $
 * @since jpicedt 1.3.2
 */
public class Localizer {

	////////////////// STATIC ///////////////////////////////////

	/** the key used to retrieve the locale from the preferences file */
	public final static String KEY_LANGUAGE = "app.language";

	// currentLocalizer is init'd to this Localizer (the default)
	private static Localizer currentLocalizer = new Localizer();

	/**
	 * @return the current Localizer
	 */
	public static Localizer currentLocalizer(){
		return currentLocalizer;
	}

	/**
	 * set the Localizer that should be used for the jpicedt's class library
	 */
	public static void setCurrentLocalizer(Localizer aLocalizer){
		currentLocalizer = aLocalizer;
	}

	/**
	* Convenience call to currentLocalizer().get()
	*/
	public static String localize(String s){
		return currentLocalizer().get(s);
	}


	/**
	* Pour appeler <code>localize</code> sur un tableau de chaînes.
	*/
	public static String[] localize(String[] s){
		String[] ret = new String[s.length];
		for(int i = 0; i < s.length; ++i)
			ret[i] = currentLocalizer().get(s[i]);

		return ret;
	}

	////////////////// DEFAULT LOCALIZER ///////////////////////////////////

	/**
	 * Create a new Localizer instance init'd from the default locale
	 */
	public Localizer(){
		init();
	}

	/**
	 * Create a new Localizer instance init'd from the given Properties object
	 */
	public Localizer(Properties preferences){
		init(preferences);
	}

	/**
	 * @return whether the given Locale is supported by this Localizer or not.
	 *         Only the language is checked, not the country, nor the variant.
	 */
	public boolean isLocaleSupported(Locale l){
		for(int i=0; i<getSupportedLocales().length;i++){
			// locale.getLanguage() returns an ISO639 string, e.g. "pt" for portuguese
			if (l.getLanguage().equals(getSupportedLocales()[i].getLanguage())) return true;
		}
		return false;
	}

	/**
	 * Return an array containing all Locales this localizer supports, by looking up the "lang/" subdirectory.
	 */
	public Locale[] getSupportedLocales(){

		//if (supportedLocales != null) return supportedLocales;

		// else build array :
		File langDir = new File(MiscUtilities.getJPicEdtHome(),"lang");
		String[] langFiles = langDir.list(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.startsWith("i18n_") && name.endsWith(".properties");
		}}); // e.g. i18n_en.properties
		if (langFiles==null) // bug fix for "execute" task in build.xml
			return new Locale[]{Locale.US};
		supportedLocales = new Locale[langFiles.length];
		for (int i=0; i<langFiles.length; i++){
			String langCode = langFiles[i].substring(5,7);
			supportedLocales[i] = new Locale(langCode,"");
		}
		return supportedLocales;
	}

 	/**
	 * Return an array containing all languages this localizer supports, returned in the
	 *         same format as Locale.getDisplayLanguage(inLocale), yet localized using the current Locale
	 *         e.g. "anglais,espagnol,francais,portugais" is current locale is "fr".
	 */
	public String[] getSupportedDisplayLanguages(){
		String[] lang = new String[getSupportedLocales().length];
		for (int i=0; i<getSupportedLocales().length;i++){
			lang[i] = getSupportedLocales()[i].getDisplayLanguage(currentLocale);
		}
		return lang;
	}

	/**
	 * Return the current Locale for this Localizer
	 */
	public Locale getCurrentLocale(){
		return currentLocale;
	}

	/**
	 * Return the current Locale for this Localizer
	 */
	public String getCurrentDisplayLanguage(){
		return currentLocale.getDisplayLanguage(currentLocale);
	}

	/**
 	 * @return the default language (localized using the current locale), in the same format
	 * 		   as returned by Locale.getDisplayLanguage() and getSupportedLanguages(),
	 *         e.g. "anglais" if current locale is "fr", and default locale is "en".
	 */
	public String getDefaultLanguage(){
		return Locale.getDefault().getDisplayLanguage(currentLocale);
	}

	/**
	 * Init ResourceBundle using OS's default Locale
	 */
	 public void init(){

		try {
			// try to load ResourceBundle "lang/i18nXXX" using default Locale :
			if (isLocaleSupported(Locale.getDefault())){ // compare languages only
				currentLocale = Locale.getDefault();
			}
			else currentLocale = Locale.ENGLISH;
	    	i18n_res = ResourceBundle.getBundle(FILE_NAME);
		}
		catch (MissingResourceException e){
			jpicedt.Log.error("Unable to find ResourceBundle:" + FILE_NAME);
		}
	}

	/**
	 * (Re)init from a Properties object.<p>
	 * First a Locale is fetched from the given Properties object, using key=PREFERENCE_KEY
	 * (values having to be valid ISO639 codes acceptable by the Locale constructor)
	 * then a ResourceBundle is loaded using this Locale. Finally, the current Locale
	 * is written back to the given Properties to reflect change (if any, e.g. if the Locale
	 * was not found in the Properties object).
	 */
	 public void init(Properties preferences){
		String langKey = preferences.getProperty(KEY_LANGUAGE); // e.g. "fr" or "en" or "pt"...
		Locale l=Locale.getDefault();
		if (langKey!=null){ // key found in preferences so override default
			l = new Locale(langKey,""); // overrides default locale
		}
		if (isLocaleSupported(l)){
			currentLocale = l;
		}
		else {
			currentLocale = Locale.ENGLISH;
		}
		try {
	    	i18n_res = ResourceBundle.getBundle(FILE_NAME,currentLocale);
		}
		catch (MissingResourceException e){
			jpicedt.Log.error("Unable to find ResourceBundle: " + FILE_NAME
			+ " for Locale="+currentLocale.getDisplayLanguage());
		}
	}

	/**
	* @return a localized version of the given key ; the key if no ResourceBundle was found.
	* @since PicEdt 1.2
	*/
	public String get(String key){
		if (i18n_res == null) {
			System.err.println("[Error] Missing resource file name.");
			return key + "[i18n]";
		}
		try {
			return i18n_res.getString(key);
		}
 		catch(MissingResourceException mre){
			jpicedt.Log.warning("Unable to find localized version of \"" + key + "\"");
			return key+"[i18n]";
		}
   	}

	/**
	 * @return an action localizer that's suited for the current locale
	 * This implementation returns a PEActionLocalizer built from
	 * the same ResourceBundle as the one used for message internationalization.
	 */
	public jpicedt.graphic.toolkit.ActionLocalizer getActionLocalizer(){
		return new jpicedt.ui.action.PEActionLocalizer(i18n_res);
	}

	/////////////////////////////////////////////////////////////////

	private Locale[] supportedLocales = null;
	private Locale currentLocale = Locale.getDefault();
	private ResourceBundle i18n_res;
	private final String FILE_NAME = "lang.i18n";

}
