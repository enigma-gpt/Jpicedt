/*
 PstricksCustomizer.java - March 4, 2002 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Departement de Physique
 École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org/

*/
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
 package jpicedt.format.output.pstricks;

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

import static jpicedt.format.output.pstricks.PstricksConstants.*;
import static jpicedt.Localizer.*;

/**
 * a panel for configuring the PstricksFormatter.
 * @author Sylvain Reynal
 * @since jPicEdt 1.3
 * @version $Id: PstricksCustomizer.java,v 1.15 2013/03/27 07:09:10 vincentb1 Exp $
 */
public class PstricksCustomizer extends AbstractCustomizer {

	private DecimalNumberField thinLinesTF, thickLinesTF;
	//private JRadioButton ellipticArcRadio,parametricPlotRadio;
	//private ButtonGroup ellipticArcGroup;
	private Properties preferences;
	private PstricksCustomProperties pstricksCustomProperties = new PstricksCustomProperties();
	private PrologEpilogCustomizer prologEpilogCustomizer;
	private JComboBox              formatterAngleCorrectionList;
	private JComboBox              formatterPredefinedColorSetList;
	private JComboBox              parserPredefinedColorSetList;

	/**
	 * construct a new panel for pstricks preferences editing.
	 * @param preferences Properties used to init the widgets fields, and to store the values
	 * when "storePreferences" is called.
	 */
	public PstricksCustomizer(Properties preferences){

		this.preferences = preferences;

		this.prologEpilogCustomizer = new PrologEpilogCustomizer(
			"format.PrologEpilog",
			PRPTY_KEY_DEFAULT_TABLE
			);

		// init main box
		Box box = new Box(BoxLayout.Y_AXIS);
		// temp. buffers
		JPanel p;
		JLabel l;

		/* Sub-Panel 1 :generic parameters for Pstricks */
		p = new JPanel(new GridLayout(2,2,5,5));
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
													 localize("format.Formatter")));
		p.add(new JLabel(" "+localize("format.pstricks.AngleCorrection") + " :"));

		p.add(formatterAngleCorrectionList =
			  new JComboBox(localize(PstricksConstants.PstricksAngleCorrection.keys())));
		ActionHandler ah = new ActionHandler();
		formatterAngleCorrectionList.addActionListener(ah);
		box.add(p);
		p.add(new JLabel(" "+localize("format.pstricks.PredefinedColor") + " :"));

		formatterPredefinedColorSetList = new JComboBox(ColorFormatter.ColorEncoding.keys());
		formatterPredefinedColorSetList.setToolTipText(localize("format.pstricks.PredefinedColor.tooltip"));
		p.add(formatterPredefinedColorSetList);

		/* Sous-panneau 2: importation */
		p = new JPanel(new GridLayout(1,2,5,5));
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
													 localize("format.Parser")));

		p.add(new JLabel(" "+localize("parser.pstricks.PredefinedColor") + " :"));

		parserPredefinedColorSetList = new JComboBox(ColorFormatter.ColorEncoding.keys());
		parserPredefinedColorSetList.setToolTipText(localize("parser.pstricks.PredefinedColor.tooltip"));
		p.add(parserPredefinedColorSetList);
		box.add(p);

		/* Sub-Panel 3 : prolog and epilog */

		box.add(this.prologEpilogCustomizer);

		/* Sub-Panel 4 : other things: ellipticArc. */

		/* [syd: now useless as recent releases of PSTricks (>=1.11) officially include psellipticarc and psellipticarcn]
		//GridBagLayout gbl = new GridBagLayout();
		//GridBagConstraints c = new GridBagConstraints();
		//c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor=GridBagConstraints.WEST;
		p = new JPanel(gbl);
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
		            localize("format.pstricks.PstEllipticArc")));

		ellipticArcRadio = new JRadioButton(" "+localize("format.pstricks.UsePstEllipticArc"));
		ellipticArcRadio.setToolTipText(localize("format.pstricks.UsePstEllipticArc.tooltip"));
		ActionHandler ah = new ActionHandler();
		ellipticArcRadio.addActionListener(ah);
		parametricPlotRadio = new JRadioButton(" "+localize("format.pstricks.UseParametricPlot"));
		parametricPlotRadio.setToolTipText(localize("format.pstricks.UseParametricPlot.tooltip"));
		parametricPlotRadio.addActionListener(ah);

		ButtonGroup ellipticArcGroup = new ButtonGroup();
		ellipticArcGroup.add(ellipticArcRadio);
		ellipticArcGroup.add(parametricPlotRadio);

		gbl.setConstraints(ellipticArcRadio,c);
		p.add(ellipticArcRadio);

		gbl.setConstraints(parametricPlotRadio,c);
		p.add(parametricPlotRadio);

		box.add(p);
		*/

		// completed :
		add(box, BorderLayout.NORTH);
	}

	/**
	 * @return the panel title, used e.g. for Border or Tabpane title.
	 */
	public String getTitle(){
		return "PSTricks";
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
		return localize("format.pstricks.tooltip");
	}

	/**
	 * Convertit l'état de l'attribut privé pstricksCustomProperties vers
	 * l'ensemble des jComboBox.
	 * @since jPicEdt 1.6
	 */
	private void propertiesToWidget(){
		int idx = pstricksCustomProperties.getAngleCorrection().value();
		formatterAngleCorrectionList.setSelectedIndex(idx);
		formatterAngleCorrectionList
			.setToolTipText(localize(
								PstricksConstants.PstricksAngleCorrection.enumOf(idx).key()
								+ ".tooltip"));
		idx = pstricksCustomProperties.getFormatterPredefinedColorSet().value();
		formatterPredefinedColorSetList.setSelectedIndex(idx);
		idx = pstricksCustomProperties.getParserPredefinedColorSet().value();
		parserPredefinedColorSetList.setSelectedIndex(idx);

	}

	/**
	 * Convertit l'état de l'ensemble des jComboBox vers l'attribut privé
	 * pstricksCustomProperties.
	 * @since jPicEdt 1.6
	 */
	private void widgetToProperties(){
		pstricksCustomProperties.setAngleCorrection(
			PstricksConstants.PstricksAngleCorrection.enumOf(
				formatterAngleCorrectionList.getSelectedIndex()));
		pstricksCustomProperties.setFormatterPredefinedColorSet(
			ColorFormatter.ColorEncoding.enumOf(
				formatterPredefinedColorSetList.getSelectedIndex()));
		pstricksCustomProperties.setParserPredefinedColorSet(
			ColorFormatter.ColorEncoding.enumOf(
				parserPredefinedColorSetList.getSelectedIndex()));

	}

	/**
	 * Load widgets display content with a default value retrieved from the LatexConstants interface.
	 */
	public void loadDefault() {
		prologEpilogCustomizer.loadDefault();
		pstricksCustomProperties.loadDefault();
		propertiesToWidget();

		/*
		if ((Boolean.valueOf(DEFAULT_USE_PS_ELLIPTIC_ARC_COMMAND)).booleanValue()){
		    ellipticArcRadio.setSelected(true);
			prologTA.setText(DEFAULT_FILE_WRAPPER_PROLOG_ELLIPTICSTY);
		}
		else {
		    parametricPlotRadio.setSelected(true);
			prologTA.setText(DEFAULT_FILE_WRAPPER_PROLOG_PSTPLOTSTY);
		}*/


	}

	/**
	 * Load widgets value from the Properties object given in the constructor.
	 */
	public void load() {
		prologEpilogCustomizer.load(preferences);
		pstricksCustomProperties.load(preferences);
		propertiesToWidget();

		/*
		if (Boolean.valueOf(preferences.getProperty(KEY_USE_PS_ELLIPTIC_ARC_COMMAND)).booleanValue()){
		    ellipticArcRadio.setSelected(true);
			prologTA.setText(preferences.getProperty(KEY_FILE_WRAPPER_PROLOG,DEFAULT_FILE_WRAPPER_PROLOG_ELLIPTICSTY));
		}
		else {
		    parametricPlotRadio.setSelected(true);
			prologTA.setText(preferences.getProperty(KEY_FILE_WRAPPER_PROLOG,DEFAULT_FILE_WRAPPER_PROLOG_PSTPLOTSTY));
		}*/

	}

	/**
	 * Store current widgets value to the Properties object given in the constructor,
	 * then update PstricksFormatter accordingly.
	 */
	 public void store(){
		 widgetToProperties();
		 prologEpilogCustomizer.store(preferences);
		 pstricksCustomProperties.store(preferences);

		//preferences.setProperty(KEY_USE_PS_ELLIPTIC_ARC_COMMAND,(new Boolean(ellipticArcRadio.isSelected())).toString());
		 PstricksFormatter.configure(preferences); // update
	}

	/** actionListener for radio-buttons related to parametric/elliptic choice ; allows to check
	 * proper inclusion of needed package in \\usepackage{} line */

	class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e){
			if(e.getSource() == formatterAngleCorrectionList){

				formatterAngleCorrectionList
					.setToolTipText(localize(
										PstricksConstants.PstricksAngleCorrection.enumOf(
											formatterAngleCorrectionList.getSelectedIndex()).key()
										+ ".tooltip"));
			}
			/*if (e.getSource()==ellipticArcRadio || e.getSource()==parametricPlotRadio){
				if (ellipticArcRadio.isSelected()) prologTA.setText(DEFAULT_FILE_WRAPPER_PROLOG_ELLIPTICSTY);
				else prologTA.setText(DEFAULT_FILE_WRAPPER_PROLOG_PSTPLOTSTY);
				}*/
		}
	}


}
