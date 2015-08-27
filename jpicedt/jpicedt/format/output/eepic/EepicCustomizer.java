// EepicCustomizer.java --- -*- coding: iso-8859-1 -*-
// March 4, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: EepicCustomizer.java,v 1.10 2013/03/27 07:10:45 vincentb1 Exp $
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
 package jpicedt.format.output.eepic;

import jpicedt.Localizer;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.PEToolKit;
import jpicedt.widgets.DecimalNumberField;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

import static jpicedt.format.output.eepic.EepicConstants.*;
import static jpicedt.Localizer.*;

/**
 * a panel for Epic/Eepic preferences editing
 * @author Sylvain Reynal
 * @since PicEdt 1.3
 */
public class EepicCustomizer extends AbstractCustomizer {

	private DecimalNumberField thinLinesTF, thickLinesTF;
	private JTextArea prologTA, epilogTA;
	private Properties preferences;

	/**
	 * construct a new panel for epic/eepic preferences editing.
	 * @param preferences Properties used to init the widgets fields, and to store the values
	 * when "storePreferences" is called.
	 */
	public EepicCustomizer(Properties preferences){

		this.preferences = preferences;

		// init main box
		Box box = new Box(BoxLayout.Y_AXIS);
		// temp. buffers
		JPanel p;
		JLabel l;

		/* Sub-Panel 1 :generic parameters for Eepic */
		p = new JPanel(new GridLayout(2,2,5,5));
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
		            localize("format.eepic.Parameters")));


		l = new JLabel(localize("format.eepic.thin-lines-max-width"));
		l.setToolTipText(localize("format.eepic.thin-lines-max-width.tooltip"));
		p.add(l);
		thinLinesTF = new DecimalNumberField(0,10,true);
		p.add(thinLinesTF);

		l = new JLabel(localize("format.eepic.thick-lines-max-width"));
		l.setToolTipText(localize("format.eepic.thick-lines-max-width.tooltip"));
		p.add(l);
		thickLinesTF = new DecimalNumberField(0,10,true);
		p.add(thickLinesTF);

		box.add(p);

		/* Sub-Panel 2 : prolog and epilog */

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;

		p = new JPanel(gbl);
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
		            localize("format.PrologEpilog")));

		l = new JLabel(" "+localize("format.Prolog"));
		l.setToolTipText(localize("format.Prolog.tooltip"));
		gbl.setConstraints(l,c);
		p.add(l);
		prologTA = new JTextArea(5,50);
		gbl.setConstraints(prologTA,c);
		p.add(prologTA);

		l = new JLabel(" "+localize("format.Epilog"));
		l.setToolTipText(localize("format.Epilog.tooltip"));
		gbl.setConstraints(l,c);
		p.add(l);
		epilogTA = new JTextArea(5,50);
		gbl.setConstraints(epilogTA,c);
		p.add(epilogTA);

		box.add(p);

		// completed :
		add(box, BorderLayout.NORTH);
	}

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return the panel title, used e.g. for Border or Tabpane title.
	 * @since jPicEdt
	 */
	public String getTitle(){
		return "Epic/eepic";
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
	public String getTooltip(){
		return localize("format.eepic.tooltip");
	}

	/**
	 * Load widgets display content with a default value retrieved from the EepicConstants interface.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void loadDefault() {
		thinLinesTF.setValue(DEFAULT_THIN_LINES_MAX_WIDTH);
		thickLinesTF.setValue(DEFAULT_THICK_LINES_MAX_WIDTH);
		prologTA.setText(DEFAULT_FILE_WRAPPER_PROLOG);
		epilogTA.setText(DEFAULT_FILE_WRAPPER_EPILOG);
	}

	/**
	 * Load widgets value from the Properties object given in the constructor.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void load() {

		// from EepicConstants :
		thinLinesTF.setText(preferences.getProperty(KEY_THIN_LINES_MAXWIDTH,PEToolKit.doubleToString(DEFAULT_THIN_LINES_MAX_WIDTH)));
		thickLinesTF.setText(preferences.getProperty(KEY_THICK_LINES_MAXWIDTH,PEToolKit.doubleToString(DEFAULT_THICK_LINES_MAX_WIDTH)));
		prologTA.setText(preferences.getProperty(KEY_FILE_WRAPPER_PROLOG,DEFAULT_FILE_WRAPPER_PROLOG));
		epilogTA.setText(preferences.getProperty(KEY_FILE_WRAPPER_EPILOG,DEFAULT_FILE_WRAPPER_EPILOG));
	}

	/**
	 * Store current widgets value to the Properties object given in the constructor,
	 * then update LatexFormatter accordingly.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	 public void store(){
		preferences.setProperty(KEY_THIN_LINES_MAXWIDTH, thinLinesTF.getText());
		preferences.setProperty(KEY_THICK_LINES_MAXWIDTH, thickLinesTF.getText());
		preferences.setProperty(KEY_FILE_WRAPPER_PROLOG,prologTA.getText());
		preferences.setProperty(KEY_FILE_WRAPPER_EPILOG,epilogTA.getText());
		EepicFormatter.configure(preferences); // update
	}
} // PanelLatex
