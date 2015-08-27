// LatexCustomizer.java --- -*- coding: iso-8859-1 -*-
// December 31, 2001 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: LatexCustomizer.java,v 1.10 2013/03/27 07:10:00 vincentb1 Exp $
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
package jpicedt.format.output.latex;

import jpicedt.Localizer;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.PEToolKit;
import jpicedt.widgets.DecimalNumberField;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import static jpicedt.format.output.latex.LatexConstants.*;
import static jpicedt.Localizer.*;

/**
 * a panel for LaTeX preferences editing (emulation parameters,...)
 * @author Sylvain Reynal
 * @since PicEdt 1.3
 * @version $Id: LatexCustomizer.java,v 1.10 2013/03/27 07:10:00 vincentb1 Exp $
 *
 */
public class LatexCustomizer extends AbstractCustomizer  {

	private DecimalNumberField linethicknessTF, emLineLengthTF, maxEmLineSlopeTF, maxLatexDiskDiameterTF;
	private DecimalNumberField emCircleSegLengthTF, maxLatexCircleDiameterTF;
	private JTextArea prologTA, epilogTA;
	private Properties preferences;

	/**
	 * construct a new panel for LaTeX preferences editing.
	 * @param preferences Properties used to init the widgets fields, and to store the values
	 * when "storePreferences" is called.
	 */
	public LatexCustomizer(Properties preferences){

		this.preferences = preferences;

		// init main box
		Box box = new Box(BoxLayout.Y_AXIS);
		// temp. buffers
		JPanel p;
		JLabel l;

		/* Sub-Panel 1 :generic parameters for LaTeX */
		p = new JPanel(new GridLayout(2,2,5,5));
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
		            localize("format.latex.Parameters")));


		l = new JLabel(" "+localize("format.latex.MaxCircleDiameter.label")+"  (mm) :");
		l.setToolTipText(localize("format.latex.MaxCircleDiameter.tooltip"));
		p.add(l);
		maxLatexCircleDiameterTF = new DecimalNumberField(0,10,true);
		p.add(maxLatexCircleDiameterTF);

		l = new JLabel(" "+localize("format.latex.MaxDiskDiameter.label")+"  (mm) :");
		l.setToolTipText(localize("format.latex.MaxDiskDiameter.tooltip"));
		p.add(l);
		maxLatexDiskDiameterTF = new DecimalNumberField(0,10,true);
		p.add(maxLatexDiskDiameterTF);

		box.add(p);

		/* Sub-Panel 2 : emulated LaTeX configuration */

		p = new JPanel(new GridLayout(3,2,5,5));
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
		            localize("format.latex.Emulation")));

		l = new JLabel(" "+localize("format.latex.EmLineLength.label")+"  (mm) :");
		l.setToolTipText(localize("format.latex.EmLineLength.tooltip"));
		p.add(l);
		emLineLengthTF = new DecimalNumberField(0,10,true);
		p.add(emLineLengthTF);

		l = new JLabel(" "+localize("format.latex.EmMaxLineSlope.label"));
		l.setToolTipText(localize("format.latex.EmMaxLineSlope.tooltip"));
		p.add(l);
		maxEmLineSlopeTF = new DecimalNumberField(0,10,true);
		p.add(maxEmLineSlopeTF);

		l  = new JLabel(" "+localize("format.latex.EmCircleSegLength.label")+"  (mm) :");
		l.setToolTipText(localize("format.latex.EmCircleSegLength.tooltip"));
		p.add(l);
		emCircleSegLengthTF = new DecimalNumberField(0,10,true);
		p.add(emCircleSegLengthTF);

		box.add(p);

		/* Sub-Panel 3 : prolog and epilog */

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
	 * @return the panel title, used e.g. for Border or Tabpane title.
	 */
	public String getTitle(){
		return "LaTeX";
	}


	/**
	 * @return the Icon associated with this panel, used e.g. for TabbedPane decoration
	 */
	public Icon getIcon(){
		return null;
	}


	/**
	 * @return the tooltip string associated with this panel
	 */
	public String getTooltip(){
		return localize("format.latex.tooltip");
	}

	/**
	 * Load widgets display content with a default value retrieved from the LatexConstants interface.
	 */
	public void loadDefault() {
		// from LatexConstants :
		emLineLengthTF.setValue(DEFAULT_EM_LINE_LENGTH);
		maxEmLineSlopeTF.setValue(DEFAULT_MAX_EM_LINE_SLOPE);
		maxLatexCircleDiameterTF.setValue(DEFAULT_MAX_CIRCLE_DIAMETER);
		maxLatexDiskDiameterTF.setValue(DEFAULT_MAX_DISK_DIAMETER);
		emCircleSegLengthTF.setValue(DEFAULT_MAX_EM_CIRCLE_SEGMENT_LENGTH);
		prologTA.setText(DEFAULT_FILE_WRAPPER_PROLOG);
		epilogTA.setText(DEFAULT_FILE_WRAPPER_EPILOG);
	}

	/**
	 * Load widgets value from the Properties object given in the constructor.
	 */
	public void load() {

		// from LatexConstants :
		emLineLengthTF.setText(preferences.getProperty(KEY_EM_LINE_LENGTH,PEToolKit.doubleToString(DEFAULT_EM_LINE_LENGTH)));
		maxEmLineSlopeTF.setText(preferences.getProperty(KEY_MAX_EM_LINE_SLOPE,PEToolKit.doubleToString(DEFAULT_MAX_EM_LINE_SLOPE)));
		maxLatexCircleDiameterTF.setText(preferences.getProperty(KEY_MAX_CIRCLE_DIAMETER,PEToolKit.doubleToString(DEFAULT_MAX_CIRCLE_DIAMETER)));
		maxLatexDiskDiameterTF.setText(preferences.getProperty(KEY_MAX_DISK_DIAMETER,PEToolKit.doubleToString(DEFAULT_MAX_DISK_DIAMETER)));
		emCircleSegLengthTF.setText(preferences.getProperty(KEY_MAX_EM_CIRCLE_SEGMENT_LENGTH,PEToolKit.doubleToString(DEFAULT_MAX_EM_CIRCLE_SEGMENT_LENGTH)));
		prologTA.setText(preferences.getProperty(KEY_FILE_WRAPPER_PROLOG,DEFAULT_FILE_WRAPPER_PROLOG));
		epilogTA.setText(preferences.getProperty(KEY_FILE_WRAPPER_EPILOG,DEFAULT_FILE_WRAPPER_EPILOG));
	}

	/**
	 * Store current widgets value to the Properties object given in the constructor,
	 * then update LatexFormatter accordingly.
	 */
	 public void store(){
		preferences.setProperty(KEY_EM_LINE_LENGTH, emLineLengthTF.getText());
		preferences.setProperty(KEY_MAX_CIRCLE_DIAMETER, maxLatexCircleDiameterTF.getText());
		preferences.setProperty(KEY_MAX_DISK_DIAMETER, maxLatexDiskDiameterTF.getText());
		preferences.setProperty(KEY_MAX_EM_CIRCLE_SEGMENT_LENGTH,emCircleSegLengthTF.getText());
		preferences.setProperty(KEY_MAX_EM_LINE_SLOPE,maxEmLineSlopeTF.getText());
		preferences.setProperty(KEY_FILE_WRAPPER_PROLOG,prologTA.getText());
		preferences.setProperty(KEY_FILE_WRAPPER_EPILOG,epilogTA.getText());
		LatexFormatter.configure(preferences); // update
	}
} // PanelLatex
