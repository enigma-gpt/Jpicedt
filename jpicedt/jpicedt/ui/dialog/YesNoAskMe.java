// YesNoAskMe.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: YesNoAskMe.java,v 1.4 2013/03/27 07:20:10 vincentb1 Exp $
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

import java.lang.String;
import javax.swing.JOptionPane;

/**
 * �num�r� donnant trois options pour reconfirmation par l'utilisateur:
 * <dl>
 * <dt><code>YES</code></dt><dd>la conformation est affirm�e automatiquement sans demander �
 * l'utilisateur</dd>
 * <dt><code>NO</code></dt><dd>la confirmation est infirm�e automatiquement sans demander � l'utilisateur</dd>
 * <dt><code>ASK_ME</code></dt><dd>l'utilisateur est consult�</dd>
 * </dl>
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela&iuml;che</a>
 * @since jPicEdt 1.6
 * @version $Id: YesNoAskMe.java,v 1.4 2013/03/27 07:20:10 vincentb1 Exp $
 */
public enum YesNoAskMe{
	YES(Keys.YES_KEY, JOptionPane.YES_OPTION,0),
		NO(Keys.NO_KEY, JOptionPane.NO_OPTION,1),
		// JOptionPane.CLOSED_OPTION: truc, n'importe quelle valeur qui n'est ni YES_OPTION ni NO_OPTION.
		ASK_ME(Keys.ASK_ME_KEY,JOptionPane.CLOSED_OPTION,2);

	public class Keys{
		private static  final String YES_KEY = "yes";
		private static final String NO_KEY = "no";
		private static final String ASK_ME_KEY = "ask-me";
	};

	private final String key;
	private int jOptionPaneValue;
	private int index;

	YesNoAskMe(String key, int jOptionPaneValue,int index){
		this.key = key;
		this.jOptionPaneValue = jOptionPaneValue;
		this.index = index;
	}

	public static YesNoAskMe getDefault(){ return ASK_ME; }

	public static YesNoAskMe toYesNoAskMe(String key){
		if(key.equals("yes")){
			return YES;
		}
		else if(key.equals("no")){
			return NO;
		}
		else{
			return ASK_ME;
		}
	}
	public static YesNoAskMe jOptionPaneValueToYesNoAskMe(int value){
		if(value == JOptionPane.YES_OPTION){
			return YES;
		}
		else if(value == JOptionPane.NO_OPTION){
			return NO;
		}
		else{
			return ASK_ME;
		}
	}
	public static YesNoAskMe indexToYesNoAskMe(int value){
		if(value == 0){
			return YES;
		}
		else if(value == 1){
			return NO;
		}
		else{
			return ASK_ME;
		}
	}

	public int    getJOptionPaneValue(){ return jOptionPaneValue; }
	public int    getIndex(){ return index; }
	public String toString(){ return key; }
}


/// YesNoAskMe.java ends here
