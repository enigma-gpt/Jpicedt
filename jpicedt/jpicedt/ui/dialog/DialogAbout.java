// DialogAbout.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C)   Sylvain Reynal
// Copyright (C) 2007/2013  Sylvain Reynal, Vincent Belaïche
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
// Version: $Id: DialogAbout.java,v 1.13 2013/03/27 07:20:15 vincentb1 Exp $
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

import jpicedt.Version;
import jpicedt.Localizer;
import jpicedt.ui.MDIManager;
import jpicedt.graphic.PEToolKit;
import jpicedt.widgets.MDIComponent;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

import static jpicedt.Localizer.*;

/**
 * "About&hellip;" dialog box ; displays various informations such as copyright, author(s),&hellip;
 * @author Sylvain Reynal
 * @since PicEdt 1.0
 * @version $Id: DialogAbout.java,v 1.13 2013/03/27 07:20:15 vincentb1 Exp $
 *
 */
public class DialogAbout implements ActionListener{

	private MDIComponent frame;
	private MDIManager mdimgr;
	private JButton buttonOk;

	/**
	 * Construct a new "about&hellip;" dialog
	 */
	public DialogAbout(MDIManager mdimgr) {

		buttonOk = new JButton(localize("button.OK"));
		buttonOk.addActionListener(this);

		JPanel buttonPanel = new JPanel(new FlowLayout(),false);
		buttonPanel.add(buttonOk);

		JPanel logoPanel = new JPanel(new FlowLayout(),false);
		logoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		logoPanel.add(PEToolKit.createJLabel("Frankenstein"));

		JPanel infoPanel = new JPanel(new GridLayout(9,1,5,5),true);
		infoPanel.setBorder(new EmptyBorder(10, 60, 10, 10));
		infoPanel.add(new JLabel("jPicEdt " + Version.getVersion() + " Built " + Version.getBuildDate()));
		infoPanel.add(new JLabel(localize("about.APictureEditorFor")));
		final String[] addressLines = {"(c) Sylvain Reynal"
								,"É.N.S.É.A. - Dept. of Physics"
								,"6, avenue du Ponceau"
								,"F-95014 CERGY Cedex"
								,"Fax: +33 (0) 130 736 667"
								,"reynal@ensea.fr"
								,"http://www.jpicedt.org" };
		for(String addressLine : addressLines)
			infoPanel.add(new JLabel(addressLine));

		JTabbedPane caveatPanel = new JTabbedPane();
		String[] tabKeys = {"license.lines","license.thirdparty.lines"};
		for(String tabKey : tabKeys)
		{
			JEditorPane caveatTA = new JEditorPane();
			caveatTA.setContentType("text/html; charset=" + localize(tabKey + ".encoding"));
			caveatTA.setEditable(false);
			caveatTA.setPreferredSize(new Dimension(485,300));
			JScrollPane scrollCaveat = new JScrollPane(caveatTA);
			caveatTA.setText(localize(tabKey));
			caveatPanel.addTab(localize(tabKey + ".tabname"),null,scrollCaveat,null);
		}

		caveatPanel.setBorder(BorderFactory.createEtchedBorder());

		JPanel upperPanel = new JPanel(new BorderLayout(),false);
		upperPanel.add(logoPanel, BorderLayout.WEST);
		upperPanel.add(infoPanel, BorderLayout.CENTER);
		upperPanel.add(caveatPanel, BorderLayout.SOUTH);
		upperPanel.setBorder(BorderFactory.createEtchedBorder());

		JPanel contentPane = new JPanel(new BorderLayout(5,5));
		contentPane.add(upperPanel, BorderLayout.NORTH);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);

		String title = localize("about.AboutPicEdt")+" "+Version.getVersion();
		boolean modal = true;
		frame = mdimgr.createDialog(title, modal, contentPane);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


		Dimension dlgSize = frame.getPreferredSize();
		frame.setSize(dlgSize);

		//this.pack();
		frame.setVisible(true);
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonOk) {
			frame.dispose();
		}
	}
}
