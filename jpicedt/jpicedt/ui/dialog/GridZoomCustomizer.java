// GridZoomCustomizer.java --- -*- coding: iso-8859-1 -*-
// December 31, 2001 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: GridZoomCustomizer.java,v 1.13 2013/03/27 06:52:11 vincentb1 Exp $
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
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.grid.Grid;
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PEToolKit;

import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import static jpicedt.Localizer.*;

/**
 * <code>Grid</code> and zoom preferences editing.<br>
 * @author Sylvain Reynal
 * @since jPicEdt 1.3
 * @version $Id: GridZoomCustomizer.java,v 1.13 2013/03/27 06:52:11 vincentb1 Exp $
 *
 */
public class GridZoomCustomizer extends AbstractCustomizer {

	private JComboBox snapStepList, gridStyleList, gridStepList;
	private JCheckBox newGridIsOnCB, newSnapIsOnCB;
	private JComboBox zoomFactorList;
	private Properties preferences;
	private	NumberFormat formatPercent = NumberFormat.getPercentInstance(Locale.US); // for zoom

	//private HistoryTextField snapStepHTF;

	/**
	 * Construct a new panel for grid/zoom preferences editing.
	 */
	public GridZoomCustomizer(Properties preferences) {

		this.preferences = preferences;
		JPanel p;
		JLabel l;

		/* Sub-Panel 1 : grid step, grid on/off state and snap on/off state on startup */
		p = new JPanel(new GridLayout(6,2,5,5));
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
		            localize("preferences.ParametersOnStartup")));

		// grid visible
		l = new JLabel(" "+localize("preferences.NewGridIsOn.label")+" :");
		l.setToolTipText(localize("preferences.NewGridIsOn.tooltip"));
		p.add(l);
		newGridIsOnCB = new JCheckBox();
		p.add(newGridIsOnCB);

		// grid-step
		l = new JLabel(" "+localize("preferences.GridStep.label")+"  (mm) :");
		l.setToolTipText(localize("preferences.GridStep.tooltip"));
		p.add(l);
		gridStepList = new JComboBox();
		gridStepList.setEditable(true);
 		for(int i=0; i<Grid.PREDEFINED_GRID_STEPS.length; i++){
 			gridStepList.addItem(new Double(Grid.PREDEFINED_GRID_STEPS[i])); // Double's
 		}
		p.add(gridStepList);

		// grid style (solid or dash or ...)
		l = new JLabel(" "+localize("preferences.GridStyle.label")+" :");
		l.setToolTipText(localize("preferences.GridStyle.tooltip"));
		p.add(l);
		gridStyleList = new JComboBox();
 		for(int i=0; i<Grid.PREDEFINED_STYLES.length; i++){
 			gridStyleList.addItem(Grid.PREDEFINED_STYLES[i]);
 		}
		p.add(gridStyleList);

		// snap on
		l = new JLabel(" "+localize("preferences.NewSnapIsOn.label")+" :");
		l.setToolTipText(localize("preferences.NewSnapIsOn.tooltip"));
		p.add(l);
		newSnapIsOnCB = new JCheckBox();
		p.add(newSnapIsOnCB);

		// snap-step
		l = new JLabel(" "+localize("preferences.SnapStep.label")+"  (mm) :");
		l.setToolTipText(localize("preferences.SnapStep.tooltip"));
		p.add(l);
		snapStepList = new JComboBox();
		snapStepList.setEditable(true);
 		for(int i=0; i<Grid.PREDEFINED_SNAP_STEPS.length; i++){
 			snapStepList.addItem(new Double(Grid.PREDEFINED_SNAP_STEPS[i])); // Double's
 		}
		p.add(snapStepList);

		// zoom factor (display using the percent sign)
		l = new JLabel(" "+localize("preferences.ZoomFactor.label")+" :");
		l.setToolTipText(localize("preferences.ZoomFactor.tooltip"));
		p.add(l);
		zoomFactorList = new JComboBox();
		zoomFactorList.setEditable(true);
 		for(double x: PECanvas.PREDEFINED_ZOOMS){
 			zoomFactorList.addItem(formatPercent.format(x)); // Strings
 		}
		p.add(zoomFactorList);

// 		l = new JLabel("test history");
// 		p.add(l);
// 		snapStepHTF = new HistoryTextField("grid",true,true);
// 		HistoryModel hm = snapStepHTF.getModel();
// 		for(int i=0; i<Grid.PREDEFINED_SNAP_STEPS.length; i++){
// 			hm.addItem(Double.toString(Grid.PREDEFINED_SNAP_STEPS[i]));
// 		}
// 		p.add(snapStepHTF);

		add(p,BorderLayout.NORTH);
	}

	/**
	 * Returns the panel title, used e.g. for Border or Tabpane title.
	 */
	public String getTitle(){
		return localize("preferences.Grid")+"/"+localize("preferences.Zoom");
	}


	/**
	 * Returns the Icon associated with this panel, used e.g. for TabbedPane decoration
	 */
	public Icon getIcon(){
		return null;
	}


	/**
	 * Returns the tooltip string associated with this panel
	 */
	public String getTooltip(){
		return localize("preferences.GridZoomParameters");
	}

	/**
	 * Load widgets display content with a default value retrieved from the Options class.
	 */
	public void loadDefault() {

		snapStepList.setSelectedIndex(Grid.getSnapStepIndex(Grid.snapStepDEFAULT)); // [pending] test if -1
		gridStepList.setSelectedIndex(Grid.getSnapStepIndex(Grid.gridStepDEFAULT)); // [pending] test if -1
		newGridIsOnCB.setSelected(Grid.isVisibleDEFAULT);
		newSnapIsOnCB.setSelected(Grid.snapOnDEFAULT);
		zoomFactorList.setSelectedIndex(PECanvas.getZoomIndex(PECanvas.ZOOM_DEFAULT));  // [pending] test if -1
		//zoomFactorList.setSelectedItem(PECanvas.ZOOM_DEFAULT);  // [pending] test if -1
		gridStyleList.setSelectedItem(Grid.lineStyleDEFAULT);
	}

	/**
	 * Load widgets value from the Properties object
	 */
	public void load() {
		boolean b;
		int i;
		String s;

		// snap step
		Double snap = new Double(jpicedt.MiscUtilities.parseProperty(preferences, Grid.KEY_SNAP_STEP,Grid.snapStepDEFAULT));
		for (i=0,b=false; i<snapStepList.getItemCount(); i++){
			if (snapStepList.getItemAt(i).equals(snap)) {
				snapStepList.setSelectedIndex(i); b=true;
				break;
			}
		}
		if (b==false) {
			snapStepList.addItem(snap);
			snapStepList.setSelectedIndex(snapStepList.getItemCount()-1);
		}

		// grid visible
		b = jpicedt.MiscUtilities.parseProperty(preferences, Grid.KEY_VISIBLE, Grid.isVisibleDEFAULT);
		newGridIsOnCB.setSelected(b);

		// grid style
		s = preferences.getProperty(Grid.KEY_LINE_STYLE, Grid.lineStyleDEFAULT);
		gridStyleList.setSelectedItem(s);

		// grid step
		Double grid = new Double(jpicedt.MiscUtilities.parseProperty(preferences, Grid.KEY_GRID_STEP,Grid.gridStepDEFAULT));
		for (i=0,b=false; i<gridStepList.getItemCount(); i++){
			if (gridStepList.getItemAt(i).equals(grid)) {
				gridStepList.setSelectedIndex(i); b=true;
				break;
			}
		}
		if (b==false) {
			gridStepList.addItem(grid);
			gridStepList.setSelectedIndex(gridStepList.getItemCount()-1);
		}

		// snap on
		b = jpicedt.MiscUtilities.parseProperty(preferences, Grid.KEY_SNAP_ON, Grid.snapOnDEFAULT);
		newSnapIsOnCB.setSelected(b);

		// zoom
		double zoom = jpicedt.MiscUtilities.parseProperty(preferences,PECanvas.KEY_ZOOM,PECanvas.ZOOM_DEFAULT);
		s = formatPercent.format(zoom); // display in "%" format
		for (i=0,b=false; i<zoomFactorList.getItemCount(); i++){
			if (zoomFactorList.getItemAt(i).equals(s)) { // ComboBox was built from e.g. "200%" Strings
				zoomFactorList.setSelectedIndex(i); b=true;
				break;
			}
		}
		if (b==false) {
			zoomFactorList.addItem(s);
			zoomFactorList.setSelectedIndex(zoomFactorList.getItemCount()-1);
		}
	}

	/**
	 * Store current widgets value, presumably to a file or to a dedicated storage class
	 */
	 public void store(){

		preferences.setProperty(Grid.KEY_SNAP_STEP,snapStepList.getSelectedItem().toString());
		preferences.setProperty(Grid.KEY_GRID_STEP,gridStepList.getSelectedItem().toString());
		preferences.setProperty(Grid.KEY_VISIBLE, new Boolean(newGridIsOnCB.isSelected()).toString());
		preferences.setProperty(Grid.KEY_SNAP_ON, new Boolean(newSnapIsOnCB.isSelected()).toString());
		preferences.setProperty(Grid.KEY_LINE_STYLE, gridStyleList.getSelectedItem().toString());
		String s = (String)zoomFactorList.getSelectedItem();
		if (s.indexOf("%")==-1) { // if zoom doesn't contain a "%" sign, format it to percent.
			// this is how it's stored in the preferences file
			s = formatPercent.format(new Double(s));
		}
		preferences.setProperty(PECanvas.KEY_ZOOM,s);
	}
} // PanelGridZoom
