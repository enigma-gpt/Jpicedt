// GeneralCustomizer.java --- -*- coding: iso-8859-1 -*-
// December 31, 2001 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: GeneralCustomizer.java,v 1.12 2013/03/27 06:52:16 vincentb1 Exp $
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

// Configuration de l'onglet "Général" des préférences utilisateur.

/// Code:
package jpicedt.ui.dialog;

import jpicedt.JPicEdt;
import jpicedt.Localizer;
import jpicedt.MiscUtilities;
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.view.DefaultViewFactory;
import jpicedt.ui.LAFManager;
import jpicedt.ui.MDIManager;
import jpicedt.ui.dialog.UserConfirmationCache;
import jpicedt.widgets.FontSelector;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Locale;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static jpicedt.Localizer.*;

/**
 * Un panneau pour l'édition des préférences utilisateur générales&nbsp;: Apparence (LAF), langage,&hellip;.
 *
 * @author Sylvain Reynal
 * @since jPicEdt 1.3
 */
public class GeneralCustomizer extends AbstractCustomizer {

	private Properties preferences;
	private JLabel l;
	private JComboBox lookAndFeelList, mdiManagerList;
	private JComboBox languageList, contentTypeList;

	// non Conform Transform - PicCircleFrom3Points
	private JComboBox ncfPcf3pScaleList, ncfPcf3pShearList;

	private JSlider maxUndoStepJS;
	private JLabel maxUndoStepL;
	//private JCheckBox showTextAsTCB;
	private FontSelector fontSelector;


	/**
	 * Crée un nouveau panneau pour les préférences générales. Les <code>Properties</code> passées en
	 * arguments servent à charger les valeurs initiales des widgets, puis à stocker les nouvelles valeurs.
	 */
	public GeneralCustomizer(Properties preferences){

		this.preferences = preferences;

		Box box = new Box(BoxLayout.Y_AXIS);
		JPanel p;

		p = new JPanel(new GridLayout(6,2,5,5));
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), localize("preferences.General")));

		// Sub-Panel 1 : MDI Manager
		p.add(new JLabel(" "+localize("preferences.MDIManager") + " :"));
		mdiManagerList = new JComboBox(MDIManager.getInstalledMDIManagers());
		p.add(mdiManagerList);

		// Sub-Panel 2 : look'n feel
		p.add(new JLabel(" "+localize("preferences.LookAndFeel") + " :"));
		lookAndFeelList = new JComboBox(LAFManager.getInstalledLookAndFeelsNames());
		p.add(lookAndFeelList);

		// Sub-Panel 3 : language choice : fr, es, en...
		p.add(new JLabel(" "+localize("preferences.Language") + " :"));
		languageList =  new JComboBox(Localizer.currentLocalizer().getSupportedDisplayLanguages());
		// this is an array of language names localized using the current locale for jpicedt.Localizer
		p.add(languageList);

		// Sub-Panel 4 : undo/redo max step
		maxUndoStepL = new JLabel(" "+localize("preferences.UndoMaxStep") + " : ?");
		p.add(maxUndoStepL);
		maxUndoStepJS = new JSlider(0, 200, 100); // MaxValue = 200
		//maxUndoStepJS.setLabelTable(maxUndoStepJS.createStandardLabels(10));
		//maxUndoStepJS.setPaintLabels(true);
		maxUndoStepJS.setMajorTickSpacing(10);
		maxUndoStepJS.setPaintTicks(true);
		maxUndoStepJS.addChangeListener(new ChangeListener(){
			                                public void stateChanged(ChangeEvent e){
				                                maxUndoStepL.setText(" "+localize("preferences.UndoMaxStep") + " : " + Integer.toString(maxUndoStepJS.getValue()));
			                                }
		                                });
		p.add(maxUndoStepJS);

		// Sub-Panel 5 : font name
		p.add(new JLabel(" "+localize("preferences.Font")));
		p.add(fontSelector=new FontSelector());

		// Sub-Panel ? : display text as "T" or plain text
// 		l = new JLabel(" "+localize("preferences.ShowTextAsT.label")+" :");
// 		l.setToolTipText(localize("preferences.ShowTextAsT.tooltip"));
// 		p.add(l);
// 		showTextAsTCB = new JCheckBox(null, null, Options.showTextAsString);
// 		p.add(showTextAsTCB);

		// Sub-panel 6 : default content-type
		l = new JLabel(" "+localize("preferences.DefaultContentType.label")+" :");
		l.setToolTipText(localize("preferences.DefaultContentType.tooltip"));
		p.add(l);
		contentTypeList = new JComboBox(MiscUtilities.getAvailableContentTypesNames());
		p.add(contentTypeList);

		// ---
		box.add(p);

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		p = new JPanel(gbl);
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), localize("preferences.PicCircleFrom3Points")));
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 3;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weighty = 3.0;
		JTextArea txt = new  JTextArea(localize("preferences.PicCircleFrom3Points.help"),2,50);
		txt.setLineWrap(true);
		gbl.setConstraints(txt,gbc);
		p.add(txt);

		gbc.gridwidth = 2;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		l = new JLabel(localize("non-conform-transform.scale"));
		gbl.setConstraints(l,gbc);
		p.add(l);
		String[] yesNoAskMe = { "misc.YesNoAskMe.yes", "misc.YesNoAskMe.no", "misc.YesNoAskMe.ask-me" };
		yesNoAskMe = localize(yesNoAskMe);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		ncfPcf3pScaleList = new JComboBox(yesNoAskMe);
		gbl.setConstraints(ncfPcf3pScaleList,gbc);
		p.add(ncfPcf3pScaleList);

		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.gridwidth = 2;
		l = new JLabel(localize("non-conform-transform.shear"));
		gbl.setConstraints(l,gbc);
		p.add(l);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		ncfPcf3pShearList = new JComboBox(yesNoAskMe);
		gbl.setConstraints(ncfPcf3pShearList,gbc);
		p.add(ncfPcf3pShearList);

		// ---
		box.add(p);
		add(box, BorderLayout.NORTH);

	}

	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return the panel title, used e.g. for Border or Tabpane title.
	 * @since jPicEdt
	 */
	public String getTitle(){
		return localize("preferences.General");
	}


	/**
	 * <br><b>author:</b> Sylvain Reynal
	 * @return The Icon associated with this panel, used e.g. for TabbedPane decoration
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
	public String getTooltip(){ return localize("preferences.GeneralConfiguration");	}

	/**
	 * Load widgets display content with a default value retrieved from the Options class.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void loadDefault() {

		lookAndFeelList.setSelectedItem(LAFManager.getDefaultLafName());
		mdiManagerList.setSelectedItem(MDIManager.getDefaultManagerName());
		languageList.setSelectedItem(Localizer.currentLocalizer().getDefaultLanguage());
		contentTypeList.setSelectedIndex(MiscUtilities.DEFAULT_CONTENT_TYPE_INDEX);
		maxUndoStepJS.setValue(PECanvas.MAX_UNDOABLE_STEPS_DEFAULT);
		fontSelector.setFont(DefaultViewFactory.TEXT_FONT_DEFAULT);
		//showTextAsTCB.setSelected(Options.showTextAsStringDEFAULT);
		ncfPcf3pScaleList.setSelectedIndex(YesNoAskMe.getDefault().getIndex());
		ncfPcf3pShearList.setSelectedIndex(YesNoAskMe.getDefault().getIndex());
	}

	/**
	 * Load widgets value from the Options class.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	public void load() {
		lookAndFeelList.setSelectedItem(LAFManager.getCurrentLafName());
		mdiManagerList.setSelectedItem(jpicedt.JPicEdt.getMDIManager().getName());
		languageList.setSelectedItem(Localizer.currentLocalizer().getCurrentDisplayLanguage());
		contentTypeList.setSelectedIndex(MiscUtilities.getContentTypeIndex(preferences.getProperty(PECanvas.KEY_CONTENT_TYPE)));
		maxUndoStepJS.setValue(MiscUtilities.parseProperty(preferences,PECanvas.KEY_UNDOABLE_STEPS,PECanvas.MAX_UNDOABLE_STEPS_DEFAULT));

		// font :
		String val = preferences.getProperty(DefaultViewFactory.KEY_TEXT_FONT);
		Font textFont;
		if (val==null) textFont = DefaultViewFactory.TEXT_FONT_DEFAULT;
		else textFont = Font.decode(val);
		fontSelector.setFont(textFont);
		ncfPcf3pScaleList.setSelectedIndex(
			JPicEdt.getProperty(UserConfirmationCache.PIC_CIRCLE_FROM_3PTS_SCALE_NCT_KEY,
								YesNoAskMe.getDefault()).getIndex());
		ncfPcf3pShearList.setSelectedIndex(
						JPicEdt.getProperty(UserConfirmationCache.PIC_CIRCLE_FROM_3PTS_SHEAR_NCT_KEY,
								YesNoAskMe.getDefault()).getIndex());
	}

	/**
	 * Store current widgets value to the Properties object given in the constructor.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jPicEdt
	 */
	 public void store(){

		preferences.setProperty(LAFManager.KEY_LAF,(String)lookAndFeelList.getSelectedItem());
		preferences.setProperty(MDIManager.KEY_MDIMANAGER,(String)mdiManagerList.getSelectedItem());

		Locale[] supportedLocales = Localizer.currentLocalizer().getSupportedLocales();
		String languageISO = supportedLocales[languageList.getSelectedIndex()].getLanguage(); // e.g. "en" or "fr"
	    preferences.setProperty(Localizer.KEY_LANGUAGE,languageISO); // ISO Code !

		preferences.setProperty(PECanvas.KEY_CONTENT_TYPE, MiscUtilities.getAvailableContentTypes()[contentTypeList.getSelectedIndex()]);
		preferences.setProperty(PECanvas.KEY_UNDOABLE_STEPS, Integer.toString(maxUndoStepJS.getValue()));

		//Options.showTextAsString = showTextAsTCB.isSelected();
		Font textFont = fontSelector.getFont();
		String textProp = MiscUtilities.formatFontAsProperties(textFont);
		preferences.setProperty(DefaultViewFactory.KEY_TEXT_FONT,textProp);

		JPicEdt.setProperty(UserConfirmationCache.PIC_CIRCLE_FROM_3PTS_SCALE_NCT_KEY,
							YesNoAskMe.indexToYesNoAskMe(ncfPcf3pScaleList.getSelectedIndex()));
		JPicEdt.setProperty(UserConfirmationCache.PIC_CIRCLE_FROM_3PTS_SHEAR_NCT_KEY,
							YesNoAskMe.indexToYesNoAskMe(ncfPcf3pShearList.getSelectedIndex()));

	}

} // class
