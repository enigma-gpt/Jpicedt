// UserConfirmationCache.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: UserConfirmationCache.java,v 1.6 2013/03/27 06:51:31 vincentb1 Exp $
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
package jpicedt.ui.dialog;

import jpicedt.JPicEdt;
import jpicedt.ui.MDIManager;

import java.util.Properties;
import javax.swing.JOptionPane;

import static jpicedt.Localizer.localize;


/**
 * Définit le traitement d'une transformation non conforme &mdash; c'est à dire ne conservant pas les
 * proportions.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela&iuml;che</a>
 * @since jPicEdt 1.6
 * @version $Id: UserConfirmationCache.java,v 1.6 2013/03/27 06:51:31 vincentb1 Exp $
 */
public class UserConfirmationCache{
	public static final String PIC_CIRCLE_FROM_3PTS_SCALE_NCT_KEY =
		"non-conform-transform.PicCircleFrom3Points.scale.yes|no|ask-me";
	public static final String PIC_CIRCLE_FROM_3PTS_SHEAR_NCT_KEY =
		"non-conform-transform.PicCircleFrom3Points.shear.yes|no|ask-me";

   /**
    * Refuse toujours. */
	protected static class ConfirmationNo implements UserConfirmationCache.Confirmation{
		public static ConfirmationNo ME = new ConfirmationNo();
		public int getValue(){ return JOptionPane.NO_OPTION; }
	}

    /**
     * Accepte toujours. */
	protected static class ConfirmationYes implements UserConfirmationCache.Confirmation{
		public static ConfirmationYes ME = new ConfirmationYes();
		public int getValue(){ return JOptionPane.YES_OPTION; }
	}


	public static Confirmation CONFIRMATION_YES = ConfirmationYes.ME;
	public static Confirmation CONFIRMATION_NO = ConfirmationNo.ME;

	protected MDIManager mdimgr;

	/**
	 * Donne la confirmation dans un cas donné.
	 *
	 */
	public interface Confirmation{
		int getValue();
	}


	/**
	 * Donne la confirmation en se basant sur les préférences utilisation.
	 *
	 */
	private class ConfirmationBasedOnUserPreferences implements Confirmation{
		String       propertyKey;
		YesNoAskMe   state;

		private String makeMessage(String key){
			return localize(key.substring(0,key.length()-13)+ "msg");
		}

		ConfirmationBasedOnUserPreferences(String propertyKey){
		   this.propertyKey = propertyKey;
		   this.state =       JPicEdt.getProperty(propertyKey,YesNoAskMe.getDefault());
		}

		public int getValue(){
			if(state == YesNoAskMe.ASK_ME){
				int value = mdimgr.showDontAskMeAgainConfirmDialog(
					makeMessage(propertyKey),localize("non-conform-transform.dialog-title"),
					propertyKey,JOptionPane.QUESTION_MESSAGE);
				state = YesNoAskMe.jOptionPaneValueToYesNoAskMe(value);

			}
			return state.getJOptionPaneValue();
		}
	}


	/** Une <code>UserConfirmationCache</code> qui dit tout le temps non. Par convention la réponse oui est
	 *	supposée être plus risquée/moins conservatrice que la réponse non. */
	public static final UserConfirmationCache DEFAULT = new UserConfirmationCache();

	public Confirmation picCircleFrom3PtsScaleHandling;
	public Confirmation picCircleFrom3PtsShearHandling;

	/**
	 * Indique à un élément graphique subissant une conversion après confirmation positive, si l'élément
	 * converti devant le remplacer, est également à remplacer dans la sélection.
	 * @since jPicEdt 1.6
	 */
	public Confirmation replaceInSelectionHandling = CONFIRMATION_YES;


	public UserConfirmationCache(MDIManager mdimgr){
		this.mdimgr = mdimgr;

		picCircleFrom3PtsScaleHandling =
			new ConfirmationBasedOnUserPreferences(PIC_CIRCLE_FROM_3PTS_SCALE_NCT_KEY);
		picCircleFrom3PtsShearHandling =
			new ConfirmationBasedOnUserPreferences(PIC_CIRCLE_FROM_3PTS_SCALE_NCT_KEY);
	}

	/** Crée le <code>DEFAULT</code>. */
	private UserConfirmationCache(){
		picCircleFrom3PtsScaleHandling = CONFIRMATION_NO;
		picCircleFrom3PtsShearHandling = CONFIRMATION_NO;
	}
}


/// UserConfirmationCache.java ends here
