// RenderingCustomizer.java --- -*- coding: iso-8859-1 -*-
// April 14, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: RenderingCustomizer.java,v 1.9 2013/03/27 06:51:51 vincentb1 Exp $
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
import jpicedt.MiscUtilities;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.PECanvas;
import jpicedt.widgets.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;
import static jpicedt.Localizer.*;

/**
 * a panel for editing rendering-preferences (anti-aliasing,...)
 *
 * @author Sylvain Reynal
 * @since jPicEdt 1.3
 * @version $Id: RenderingCustomizer.java,v 1.9 2013/03/27 06:51:51 vincentb1 Exp $
 */
public class RenderingCustomizer extends AbstractCustomizer {

	private Properties preferences;
	private JLabel l;
	private JCheckBox antiAliasingCB, qualityCB,textAntiAliasingCB,fractionalMetricsCB,ditherCB;


	/**
	 * creates a new panel for general preferences. The given Properties is used to load widgets values,
	 * then to store new values.
	 */
	public RenderingCustomizer(Properties preferences){

		this.preferences = preferences;
		JPanel p;

		p = new JPanel(new GridLayout(5,1,5,5));
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), localize("preferences.Rendering")));

		p.add(antiAliasingCB = new JCheckBox(localize("preferences.Rendering.anti-aliasing")));
		p.add(textAntiAliasingCB = new JCheckBox(localize("preferences.Rendering.text-anti-aliasing")));
		p.add(qualityCB = new JCheckBox(localize("preferences.Rendering.quality")));
		qualityCB.setToolTipText(localize("preferences.Rendering.quality.tooltip"));
		p.add(ditherCB = new JCheckBox(localize("preferences.Rendering.dither")));
		p.add(fractionalMetricsCB = new JCheckBox(localize("preferences.Rendering.fractional-metrics")));

		add(p, BorderLayout.NORTH);

	}

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return the panel title, used e.g. for Border or Tabpane title.
	 * @since jPicEdt
	 */
	public String getTitle(){
		return localize("preferences.Rendering");
	}


	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return the Icon associated with this panel, used e.g. for TabbedPane decoration
	 * @since jPicEdt
	 */
	public Icon getIcon(){
		return null;
	}


	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return the tooltip string associated with this panel
	 * @since jPicEdt
	 */
	public String getTooltip(){ return localize("preferences.Rendering.tooltip");	}

	/**
	 * Load widgets display content with a default value retrieved from the Options class.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void loadDefault() {
		antiAliasingCB.setSelected(false);
		qualityCB.setSelected(false);
		textAntiAliasingCB.setSelected(false);
		fractionalMetricsCB.setSelected(false);
		ditherCB.setSelected(false);
	}

	/**
	 * Load widgets value from the Options class.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void load() {
		RenderingHints rh = MiscUtilities.parseRenderingHints(preferences);
		if (rh.get(RenderingHints.KEY_ANTIALIASING)!=null)
			antiAliasingCB.setSelected(rh.get(RenderingHints.KEY_ANTIALIASING).equals(RenderingHints.VALUE_ANTIALIAS_ON));
		if (rh.get(RenderingHints.KEY_RENDERING)!=null)
			qualityCB.setSelected(rh.get(RenderingHints.KEY_RENDERING).equals(RenderingHints.VALUE_RENDER_QUALITY));
		if (rh.get(RenderingHints.KEY_TEXT_ANTIALIASING)!=null)
			textAntiAliasingCB.setSelected(rh.get(RenderingHints.KEY_TEXT_ANTIALIASING).equals(RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		if (rh.get(RenderingHints.KEY_FRACTIONALMETRICS)!=null)
			fractionalMetricsCB.setSelected(rh.get(RenderingHints.KEY_FRACTIONALMETRICS).equals(RenderingHints.VALUE_FRACTIONALMETRICS_ON));
		if (rh.get(RenderingHints.KEY_DITHERING)!=null)
			ditherCB.setSelected(rh.get(RenderingHints.KEY_DITHERING).equals(RenderingHints.VALUE_DITHER_ENABLE));
	}

	/**
	 * Store current widgets value to the Properties object given in the constructor.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	 public void store(){
		RenderingHints rh = new RenderingHints(null);
		String str;
		if (antiAliasingCB.isSelected()) rh.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		else rh.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		if (textAntiAliasingCB.isSelected()) rh.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		else rh.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		if (!qualityCB.isSelected()) rh.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
		else rh.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		if (ditherCB.isSelected()) rh.put(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE);
		else rh.put(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_DISABLE);
		if (fractionalMetricsCB.isSelected()) rh.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		else rh.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		MiscUtilities.formatRenderingHints(rh, preferences);
	}

} // class
