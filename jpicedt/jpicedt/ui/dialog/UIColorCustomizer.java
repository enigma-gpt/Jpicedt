// UIColorCustomizer.java --- -*- coding: iso-8859-1 -*-
// December 31, 2001 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
//
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (�NS�A)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: UIColorCustomizer.java,v 1.12 2013/04/14 19:43:23 vincentb1 Exp $
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

import jpicedt.Localizer;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.grid.Grid;
import jpicedt.ui.internal.InternalFrameMDIManager;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.IOException;
import java.util.MissingResourceException;
import static jpicedt.Localizer.*;

/**
 * Un panneau pour �diter les couleur de l'Interface Utilisateur (grille, bureau,&hellip;)
 * [pending] selection highlighter colour
 * @author Sylvain Reynal
 * @since PicEdt 1.3
 */
public class UIColorCustomizer extends AbstractCustomizer implements ActionListener {

	private JButton gridColorB, selectionColorB, endPointColorB;
	private JButton boundingareaColorB, desktopColorB, textColorB;
	private Properties preferences;

	/**
	 * Constructeur d'un nouveau panneau pour l'�dition des couleur Interface Utilisateur.
	 */
	public UIColorCustomizer(Properties preferences){
		this.preferences  = preferences;
		JPanel p = new JPanel(new GridLayout(2,1,5,5));
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), localize("preferences.Colors")));

		gridColorB = new JButton(localize("preferences.Grid"));
		gridColorB.setToolTipText(localize("preferences.Grid"));
		gridColorB.addActionListener(this);
		p.add(gridColorB);

		desktopColorB = new JButton(localize("preferences.Desktop"));
		desktopColorB.setToolTipText(localize("preferences.Desktop"));
		desktopColorB.addActionListener(this);
		p.add(desktopColorB);

		add(p, BorderLayout.NORTH);
	}

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return le titre du panneau, utilis� par ex. pour le titre <code>Border</code> ou <code>Tabpane</code>.
	 * @since jPicEdt
	 */
	public String getTitle(){
		return localize("preferences.Colors");
	}


	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return l'<code>Icon</code> associ�e � ce panneau, utilis� par ex. pour la d�coration du
	 * <code>TabbedPane<code>.
	 * @since jPicEdt
	 */
	public Icon getIcon(){
		return null;
	}


	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return la cha�ne de conseil d'utilisation associ�e � ce panneau.
	 * @since jPicEdt
	 */
	public String getTooltip(){
		return localize("preferences.ColorChooser");
	}

	/**
	 * Charge les widgets avec la valeur d'affichage par d�faut.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void loadDefault() {

		/*
		selectionColorB.setBackground(Options.selectionColorDEFAULT);
		endPointColorB.setBackground(Options.endPointColorDEFAULT);
		textColorB.setBackground(Options.textColorDEFAULT);
		boundingareaColorB.setBackground(Options.boundingareaColorDEFAULT);
		*/
		gridColorB.setBackground(Grid.colorDEFAULT);
		desktopColorB.setBackground(InternalFrameMDIManager.desktopColorDEFAULT);
	}

	/**
	 * Charge les widgets avec les valeurs des pr�f�rences utilisateur courante.  <br><b>author:</b> Sylvain
	 * Reynal
	 * @since jPicEdt
	 */
	public void load() {
		/*
		selectionColorB.setBackground(Options.selectionColor);
		endPointColorB.setBackground(Options.endPointColor);
		textColorB.setBackground(Options.textColor);
		boundingareaColorB.setBackground(Options.boundingareaColor);
		*/
		gridColorB.setBackground(jpicedt.MiscUtilities.parseProperty(preferences,
															Grid.KEY_GRID_COLOR,Grid.colorDEFAULT));
		desktopColorB.setBackground(jpicedt.MiscUtilities.parseProperty(preferences,
				InternalFrameMDIManager.KEY_DESKTOP_COLOR,InternalFrameMDIManager.desktopColorDEFAULT));

	}

	/**
	 * Stocke la valeur courantes des widgets dans les pr�f�rences utilisateur.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	 public void store(){
		 store(Grid.KEY_GRID_COLOR, gridColorB);
		 store(InternalFrameMDIManager.KEY_DESKTOP_COLOR, desktopColorB);
		/*
		Options.selectionColor = selectionColorB.getBackground();
		Options.endPointColor = endPointColorB.getBackground();
		Options.boundingareaColor = boundingareaColorB.getBackground();
		Options.textColor = textColorB.getBackground();
		*/
	}

	// convenience used by store
	private void store(String key, JButton button){
		preferences.setProperty(key, Integer.toString(button.getBackground().getRGB()));
	}

	/** Ouvre un dialogue de s�lection de couleur <code>JColorChooser</code>*/
	public void actionPerformed(ActionEvent e){

		if (e.getSource() instanceof JButton){
			JButton b = (JButton)e.getSource();
			Color newColor = JColorChooser.showDialog(null, b.getText(), b.getBackground());
			if (newColor != null) b.setBackground(newColor);
		}
	}
} // PanelColor
