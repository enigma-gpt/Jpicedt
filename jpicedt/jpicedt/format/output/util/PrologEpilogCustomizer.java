// PrologEpilogCustomizer.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: PrologEpilogCustomizer.java,v 1.4 2013/03/27 07:07:42 vincentb1 Exp $
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
	 * Construit un configurateur pour pr�f�rences de
	 * Prologue/�pilogue.
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
	 * Charge dans le Widget les valeurs par d�faut des champs.
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
