// RadioChoiceDialog.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: RadioChoiceDialog.java,v 1.4 2013/03/27 06:51:56 vincentb1 Exp $
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

import jpicedt.ui.MDIManager;
import jpicedt.widgets.MDIComponent;
import java.lang.String;
import java.lang.Integer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import static jpicedt.Localizer.localize;


/**
 * Ouvre un boîte de dialogue modale dans laquelle l'utilateur est invité à
 * faire un choix parmi plusieurs alternatives.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class RadioChoiceDialog  implements ActionListener{
	private MDIComponent frame;
	private MDIManager   mdimgr;
	private JButton      buttonOk;
	private int          userRadioChoice;
	private ButtonGroup  buttonGroup;

	/**
     * Construit une boîte de dialogue RadioChoiceDialog.
	 *@param mdimgr gestionnaire Mutiple Document Interface à utiliser pour
	 *ouvrir la boîte de dialogue.
	 *@param title clef de localisation du titre à donner à la boîte
	 *@param cause Cause de l'ouverture du dialogue (null si non précisée).
	 *@param prompt1 clef de localisation de l'explication faite à
	 *l'utilisateur de la cause de l'ouverture du dialogue. (null si par défaut).
	 *@param prompt2 clef de localisation de l'invite à l'utilisateur de faire
	 *un choix. Si null invite par défaut.
	 *@param choiceLabels clefs de localisation des étiquettes des choix.
	 *@since jPicEdt 1.6
	 */
	public RadioChoiceDialog(MDIManager mdimgr,String title,String cause,String prompt1,String prompt2,String[] choiceLabels,int initialChoice){
		userRadioChoice = initialChoice;

		buttonOk = new JButton(localize("button.OK"));
		buttonOk.addActionListener(this);

		JPanel buttonPanel = new JPanel(new FlowLayout(),false);
		buttonPanel.add(buttonOk);

		JPanel upperPanel = new JPanel(new GridLayout(9,1,5,5),true);
		upperPanel.setBorder(new EmptyBorder(10, 60, 10, 10));

		upperPanel.add(
			new JLabel(
				localize(
					prompt1 == null?
					( cause == null?
					  "radioChoiceDialog.noCause.defaultPrompt1":
					  "radioChoiceDialog.cause.defaultPrompt1"):
					prompt1
					)));

		if(cause != null)
			upperPanel.add(new JLabel(cause));

		upperPanel.add(
			new JLabel(
				localize(
					prompt2 == null?
					"radioChoiceDialog.defaultPrompt2":
					prompt2)));


		buttonGroup= new ButtonGroup();
		int i = 0;
		for(String s : choiceLabels)
		{
			JRadioButton button = new JRadioButton(localize(s));
			button.setActionCommand("="+Integer.toString(i));
			button.addActionListener(this);
			if(i++ == initialChoice)
				button.setSelected(true);
			buttonGroup.add(button);
			upperPanel.add(button);
		}


		JPanel contentPane = new JPanel(new BorderLayout(5,5));
		contentPane.add(upperPanel, BorderLayout.NORTH);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);


		boolean modal = true;
		frame = mdimgr.createDialog(localize(title), modal, contentPane);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


		Dimension dlgSize = frame.getPreferredSize();
		frame.setSize(dlgSize);

		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {


		if (e.getSource() == buttonOk) {
			frame.dispose();
		}
		else
		{
			String ac = e.getActionCommand();
			if(ac.startsWith("="))
				userRadioChoice = Integer.valueOf(ac.substring(1));
		}
	}

	/**
     * Renvoie le choix de l'utilisateur.
	 *@since jPicEdt 1.6
	 */
	public int getUserRadioChoice(){
		return userRadioChoice;
	}
}


/// RadioChoiceDialog.java ends here
