// TikzCustomizer.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: TikzCustomizer.java,v 1.8 2013/03/27 07:08:37 vincentb1 Exp $
// Keywords: Tikz, PGF
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

import jpicedt.Localizer;
import jpicedt.format.output.util.PrologEpilogCustomizer;
import jpicedt.format.output.util.ColorFormatter;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.PEToolKit;
import jpicedt.widgets.DecimalNumberField;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

import static jpicedt.format.output.tikz.TikzConstants.*;
import static jpicedt.Localizer.*;

/**
 * Un panneau pour l'�dition des pr�f�rences utilisateur pour l'import/export du format TikZ et jPicEdt.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 */
public class TikzCustomizer extends AbstractCustomizer {

	private Properties preferences;
	private TikzCustomProperties tikzCustomProperties = new TikzCustomProperties();
	private JCheckBox  hasArrowTipPackageCB;
	private JCheckBox  clipBasedOnJPE_BB_CB;
	private JComboBox  formatterPredefinedColorSetList;

	private PrologEpilogCustomizer prologEpilogCustomizer;

	/**
	 * Construit une nouvelle panneau pour l'�dition des pr�f�rences TikZ.
	 * @param preferences objet <code>Properties</code> utilis�e pour initialiser les champs des widgets, et
	 * pour stocker les valeurs quand <code>storePreferences</code> est appel�.
	 * @since jPicEdt 1.6
	 */
	public TikzCustomizer(Properties preferences){

		this.preferences = preferences;

		this.prologEpilogCustomizer = new PrologEpilogCustomizer(
			"format.PrologEpilog",
			PRPTY_KEY_DEFAULT_TABLE
			);

		// init main box
		Box box = new Box(BoxLayout.Y_AXIS);


		// Configuration de l'exportation Tikz
		GridBagLayout gbl = new GridBagLayout();
		JPanel p = new JPanel(gbl);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;

		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
													 localize("format.Formatter")));
		gbl.setConstraints(hasArrowTipPackageCB =
						  new JCheckBox(localize("format.tikz.Formatter.ArrowTip")),c);
		p.add(hasArrowTipPackageCB);
		gbl.setConstraints(clipBasedOnJPE_BB_CB =
						  new JCheckBox(localize("format.tikz.Formatter.ClipBasedOnJPE_BB_Tip")),c);
		p.add(clipBasedOnJPE_BB_CB);

		c.gridwidth = 2;
		JLabel l = new JLabel(localize("format.tikz.PredefinedColor"));
		gbl.setConstraints(l,c);
		p.add(l);
		gbl.setConstraints(formatterPredefinedColorSetList
						  = new JComboBox(ColorFormatter.ColorEncoding.keys()),c);
		formatterPredefinedColorSetList.setToolTipText(localize("format.tikz.PredefinedColor.tooltip"));
		p.add(formatterPredefinedColorSetList);

		box.add(p);

		box.add(prologEpilogCustomizer);


		// completed :
		add(box, BorderLayout.NORTH);
	}

	/**
	 * @return Le titre de la panneau, utilis� e.g. pour les titre de <code>Border</code> ou de
	 * <code>Tabpane</code>.
	 * @since jPicEdt 1.6
	 */
	public String getTitle(){
		return "TikZ";
	}


	/**
	 * @return L'ic�ne associ�e � ce panneau, utilis� e.g. pour la
	 * d�coration des onglets <code>TabbedPane</code>.
	 * @since jPicEdt 1.6
	 */
	public Icon getIcon(){
		return null;
	}


	/**
	 * @return La cha�ne  tooltip associ�e � ce panneau.
	 * @since jPicEdt 1.6
	 */
	public String getTooltip(){
		return localize("format.tikz.tooltip");
	}

	private int getIndex(JComboBox jComboBox,int[] options){
		int id = jComboBox.getSelectedIndex();
		return options[id];
	}
	private void setIndex(JComboBox jComboBox,int[] options,int val){
		int id = 0;
		for(int i = 0; i < options.length;++i)
		{
			if(options[i] == val)
			{
				id = i;
				break;
			}
		}
		jComboBox.setSelectedIndex(id);
	}

	/**
	 * Convertit l'�tat de l'attribut priv� <code>tikzCustomProperties</code> vers
	 * l'ensemble des <code>JComboBox</code>.
	 * @since jPicEdt 1.6
	 */
	private void propertiesToWidget(){
		hasArrowTipPackageCB.setSelected(tikzCustomProperties.getHasArrowTipPackage());
		clipBasedOnJPE_BB_CB.setSelected(tikzCustomProperties.getClipBasedOnJPE_BB());
		formatterPredefinedColorSetList.setSelectedIndex(
			tikzCustomProperties.getFormatterPredefinedColorSet().value());
	}

	/**
	 * Convertit l'�tat de l'ensemble des <code>JComboBox</code> vers l'attribut priv�.
	 * tikzCustomProperties.
	 * @since jPicEdt 1.6
	 */
	private void widgetToProperties(){
		tikzCustomProperties.setHasArrowTipPackage(hasArrowTipPackageCB.isSelected());
		tikzCustomProperties.setClipBasedOnJPE_BB(clipBasedOnJPE_BB_CB.isSelected());
		tikzCustomProperties.setFormatterPredefinedColorSet(
			ColorFormatter.ColorEncoding.enumOf(formatterPredefinedColorSetList.getSelectedIndex()));
	}


	/**
	 * Charge le contenu d'affichage des widgets avec les valeurs par d�faut
	 * prises des <code>TikzContants</code>.
	 * @since jPicEdt 1.6
	 */
	public void loadDefault() {
		prologEpilogCustomizer.loadDefault();
		tikzCustomProperties.loadDefault();
		propertiesToWidget();
	}

	/**
	 * Charge les valeurs du widgets � partir de l'objet <code>Properties</code> pass� au
	 * constructeur.
	 * @since jPicEdt 1.6
	 */
	public void load() {
		prologEpilogCustomizer.load(preferences);
		tikzCustomProperties.load(preferences);
		propertiesToWidget();
	}

	/**
	 * Stocke les valeurs courant dans ce widget vers l'objet <code>Properties</code> pass� au constructeur,
	 * puis mise � jour de <code>TIKZFormatter</code>.
	 * @since jPicEdt 1.6
	 */
	public void store(){
		widgetToProperties();
		prologEpilogCustomizer.store(preferences);
		tikzCustomProperties.store(preferences);
		TikzFormatter.configure(preferences); // mise � jour.
		TikzViewFactory.configure(preferences);
	 }

}

/// TikzCustomizer.java ends here
