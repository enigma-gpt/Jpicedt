// TikzCustomizer.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: TikzCustomizer.java,v 1.8 2013/03/27 07:08:37 vincentb1 Exp $
// Keywords: Tikz, PGF
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
 * Un panneau pour l'édition des préférences utilisateur pour l'import/export du format TikZ et jPicEdt.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
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
	 * Construit une nouvelle panneau pour l'édition des préférences TikZ.
	 * @param preferences objet <code>Properties</code> utilisée pour initialiser les champs des widgets, et
	 * pour stocker les valeurs quand <code>storePreferences</code> est appelé.
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
	 * @return Le titre de la panneau, utilisé e.g. pour les titre de <code>Border</code> ou de
	 * <code>Tabpane</code>.
	 * @since jPicEdt 1.6
	 */
	public String getTitle(){
		return "TikZ";
	}


	/**
	 * @return L'icône associée à ce panneau, utilisé e.g. pour la
	 * décoration des onglets <code>TabbedPane</code>.
	 * @since jPicEdt 1.6
	 */
	public Icon getIcon(){
		return null;
	}


	/**
	 * @return La chaîne  tooltip associée à ce panneau.
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
	 * Convertit l'état de l'attribut privé <code>tikzCustomProperties</code> vers
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
	 * Convertit l'état de l'ensemble des <code>JComboBox</code> vers l'attribut privé.
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
	 * Charge le contenu d'affichage des widgets avec les valeurs par défaut
	 * prises des <code>TikzContants</code>.
	 * @since jPicEdt 1.6
	 */
	public void loadDefault() {
		prologEpilogCustomizer.loadDefault();
		tikzCustomProperties.loadDefault();
		propertiesToWidget();
	}

	/**
	 * Charge les valeurs du widgets à partir de l'objet <code>Properties</code> passé au
	 * constructeur.
	 * @since jPicEdt 1.6
	 */
	public void load() {
		prologEpilogCustomizer.load(preferences);
		tikzCustomProperties.load(preferences);
		propertiesToWidget();
	}

	/**
	 * Stocke les valeurs courant dans ce widget vers l'objet <code>Properties</code> passé au constructeur,
	 * puis mise à jour de <code>TIKZFormatter</code>.
	 * @since jPicEdt 1.6
	 */
	public void store(){
		widgetToProperties();
		prologEpilogCustomizer.store(preferences);
		tikzCustomProperties.store(preferences);
		TikzFormatter.configure(preferences); // mise à jour.
		TikzViewFactory.configure(preferences);
	 }

}

/// TikzCustomizer.java ends here
