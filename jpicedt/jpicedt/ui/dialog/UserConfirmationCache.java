// UserConfirmationCache.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: UserConfirmationCache.java,v 1.6 2013/03/27 06:51:31 vincentb1 Exp $
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
package jpicedt.ui.dialog;

import jpicedt.JPicEdt;
import jpicedt.ui.MDIManager;

import java.util.Properties;
import javax.swing.JOptionPane;

import static jpicedt.Localizer.localize;


/**
 * D�finit le traitement d'une transformation non conforme &mdash; c'est � dire ne conservant pas les
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
	 * Donne la confirmation dans un cas donn�.
	 *
	 */
	public interface Confirmation{
		int getValue();
	}


	/**
	 * Donne la confirmation en se basant sur les pr�f�rences utilisation.
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


	/** Une <code>UserConfirmationCache</code> qui dit tout le temps non. Par convention la r�ponse oui est
	 *	suppos�e �tre plus risqu�e/moins conservatrice que la r�ponse non. */
	public static final UserConfirmationCache DEFAULT = new UserConfirmationCache();

	public Confirmation picCircleFrom3PtsScaleHandling;
	public Confirmation picCircleFrom3PtsShearHandling;

	/**
	 * Indique � un �l�ment graphique subissant une conversion apr�s confirmation positive, si l'�l�ment
	 * converti devant le remplacer, est �galement � remplacer dans la s�lection.
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

	/** Cr�e le <code>DEFAULT</code>. */
	private UserConfirmationCache(){
		picCircleFrom3PtsScaleHandling = CONFIRMATION_NO;
		picCircleFrom3PtsShearHandling = CONFIRMATION_NO;
	}
}


/// UserConfirmationCache.java ends here
