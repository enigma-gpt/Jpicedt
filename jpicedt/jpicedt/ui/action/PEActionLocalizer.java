/*
 PEActionLocalizer.java - January 2, 2002 - jPicEdt, a picture editor for LaTeX.
 Copyright (C)  Sylvain Reynal

 D�partement de Physique
 �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org

*/
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
package jpicedt.ui.action;

import jpicedt.Log;
import jpicedt.graphic.toolkit.ActionLocalizer;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;


/**
 * A class that can feed PEAction's with localized properties (e.g. tooltip,...)
 * provided by a properties file through a ResourceBundle.<p>
 * Each method takes a unique String argument, namely the non-localized Action name used in PEAction
 * constructor, and builds other Action's properties from this String.<p>
 * If there's no associated value for this String in the associated ResourceBundle, methods
 * catch the associated MissingResourceException and return a null value.
 * @since jPicEdt
 * @author Sylvain Reynal
 * @version $Id: PEActionLocalizer.java,v 1.11 2013/03/27 06:52:46 vincentb1 Exp $
 *
 */
public class PEActionLocalizer implements ActionLocalizer {

	/** the ResourceBundle that feeds this localizer with localized Strings */
	protected ResourceBundle resBundle;

	/**
	 * Construct a new PEActionLocalizer from the given ResourceBundle.
	 */
	public PEActionLocalizer(ResourceBundle resBundle){
		this.resBundle = resBundle;
	}

	/**
	 * @return  the ResourceBundle that feeds this localizer with localized Strings
	 */
	public ResourceBundle getResourceBundle(){
		return resBundle;
	}

	/**
	 * @return a localized Action name (ie Swing Action.NAME property) for the given name<br>
	 * This in effect will return the value associated with "actionName" in the ResourceBundle
	 * associated with the localizer.
	 */
	public String getActionName(String actionName){
		try { return resBundle.getString(actionName);}
		catch (MissingResourceException mre){return actionName+"[missing]";}
	}

	/**
	 * @return a localized Action tooltip for the given name (ie Swing Action.SHORT_DESCRIPTION)<p>
	 * This will return the value for the key build from : actionName + ".tooltip".
	 */
	public String getActionTooltip(String actionName){
		try { return resBundle.getString(actionName + ".tooltip");}
		catch (MissingResourceException mre){
			//warning(actionName+".tooltip [missing]");
			return null;
		}
	}

	/**
	 * @return a localized Action helper (ie Swing Action.LONG_DESCRIPTION)<p>
	 * This will return the value for the key build from : actionName + ".helper".
	 */
	public String getActionHelper(String actionName){
		try { return resBundle.getString(actionName + ".helper");}
		catch (MissingResourceException mre){return null;}
	}

	/**
	 * @return a localized Action mnemonic (ie Swing Action.MNEMONIC_KEY)<p>
	 * This will return a Character initialized from the first character
	 * of the value associated with the key : actionName + ".mnemonic".
	 */
	public Integer getActionMnemonic(String actionName){
		try {
			return new Integer(resBundle.getString(actionName + ".mnemonic").charAt(0));
		}
		catch (MissingResourceException mre){
			//warning(actionName+".mnemonic [missing]");
			return null;
		}
	}

	/**
	 * @return a localized Action accelerator keystroke (ie Swing Action.ACCELERATOR_KEY)<p>
	 * This will return a KeyStroke parsed from the following String: actionName + ".accelerator".
	 * @see javax.swing.KeyStroke#getKeyStroke(String)
	 */
	public KeyStroke getActionAccelerator(String actionName){
		try {
			return KeyStroke.getKeyStroke(resBundle.getString(actionName + ".accelerator"));
		}
		catch (MissingResourceException mre){return null;}
	}

	/**
	 * @return a localized Icon (ie Swing Action.SMALL_ICON)<p>
	 * This will return an ImageIcon initialized from a PNG file with path : "/jpicedt/images/"+actionName+".png"
	 * <br>Return null if the PNG file doesn't exist.
	 */
	public Icon getActionIcon(String actionName){
		java.net.URL u = getClass().getResource("/jpicedt/images/" + actionName + ".png");
		if (u!=null) return new ImageIcon(u);
		else {
			//Log.warning("/jpicedt/images/" + actionName + ".png not found");
			return null;
		}
	}

} // class
