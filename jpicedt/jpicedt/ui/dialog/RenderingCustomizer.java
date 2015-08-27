// RenderingCustomizer.java --- -*- coding: iso-8859-1 -*-
// April 14, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: RenderingCustomizer.java,v 1.9 2013/03/27 06:51:51 vincentb1 Exp $
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
