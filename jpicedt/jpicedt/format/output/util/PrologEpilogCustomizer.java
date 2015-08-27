// PrologEpilogCustomizer.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: PrologEpilogCustomizer.java,v 1.4 2013/03/27 07:07:42 vincentb1 Exp $
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
package jpicedt.format.output.util;

import java.lang.String;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.lang.String;
import java.util.Properties;
import static jpicedt.Localizer.localize;

public class PrologEpilogCustomizer extends JPanel{
	private JTextArea[] prologEpilogTA = new JTextArea[2];
	private String[] keyDefaultTable;

	/**
	 * Construit un configurateur pour préférences de
	 * Prologue/Épilogue.
	 * @param keyDefaultTable table contenant les informations suivantes<br>
	 *
	 */
	public PrologEpilogCustomizer(
		String localizationKey,
		String[] keyDefaultTable
		){
		GridBagLayout gbl = new GridBagLayout();
		super.setLayout(gbl);

		this.keyDefaultTable = keyDefaultTable;

		JLabel l;

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;

		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
		            localize(localizationKey)));

		l = new JLabel(" "+localize("format.Prolog"));
		l.setToolTipText(localize("format.Prolog.tooltip"));
		gbl.setConstraints(l,c);
		add(l);
		prologEpilogTA[0] = new JTextArea(5,50);
		gbl.setConstraints(prologEpilogTA[0],c);
		add(prologEpilogTA[0]);

		l = new JLabel(" "+localize("format.Epilog"));
		l.setToolTipText(localize("format.Epilog.tooltip"));
		gbl.setConstraints(l,c);
		add(l);
		prologEpilogTA[1] = new JTextArea(5,50);
		gbl.setConstraints(prologEpilogTA[1],c);
		add(prologEpilogTA[1]);

	}

	/** Charge le widget avec les valeurs correspondantes de prop */
	public void load(Properties prop) {
		for(int i = 0; i < 2; ++i)
			prologEpilogTA[i].setText(prop.getProperty(
										  keyDefaultTable[i*2],
										  keyDefaultTable[i*2+1]));
	}

	/**
	 * Charge dans le Widget les valeurs par défaut des champs.
	 */
	public void loadDefault(){
		for(int i = 0; i < 2; ++i)
			prologEpilogTA[i].setText(keyDefaultTable[i*2+1]);
	}

	/**
	 * Stocke les valeurs depuis le widget vers l'objet prop.
	 */
	 public void store(Properties prop){
		 for(int i = 0; i < 2; ++i)
			 prop.setProperty(keyDefaultTable[i*2],prologEpilogTA[i].getText());
	}



}

/// PrologEpilogCustomizer.java ends here
