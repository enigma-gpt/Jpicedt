// DXFCustomizer.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFCustomizer.java,v 1.9 2013/03/27 07:24:12 vincentb1 Exp $
// Keywords: AutoCad, DXF (marques d�pos�es)
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

/// Installation:

/// Code:
package jpicedt.format.output.dxf;

import jpicedt.Localizer;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.PEToolKit;
import jpicedt.widgets.DecimalNumberField;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

import static jpicedt.format.output.dxf.DXFConstants.*;
import static jpicedt.Localizer.*;

/**
 * Un panneau pour l'�dition des pr�f�rences utilisateur pour la conversion
 * entre les formats DXF (marque d�pos�e) et jPicEdt.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 */
public class DXFCustomizer extends AbstractCustomizer{

	private Properties preferences;
	private DXFCustomProperties dxfCustomProperties = new DXFCustomProperties();
	private JComboBox version;
   	private JComboBox elolCircle;
   	private JComboBox elolEllipse;
	private JComboBox curveMulticurve;
	private JComboBox plChord;
	private JComboBox plPie;
	private JComboBox plMultiCurve;
	private JComboBox plParallelogram;

	private class LocalizedDXFVersion{
		DXFVersion v;
		public LocalizedDXFVersion(DXFVersion v){ this.v = v; }
		public DXFVersion getVersion(){ return v; }
		public String toString(){
			return localize("format.dxf.Formatter.version_" + v.getDXFVersionId());
		}
	}

	/**
	 * Construit un nouveau panneau pour l'�dition des pr�f�rences DXF (marque d�pos�e).
	 * @param preferences Properties utilis�e pour initialiser les champs des
	 * widgets, et pour stocker les valeurs quand "storePreferences" est
	 * appel�.
	 * @since jPicEdt 1.6
	 */
	public DXFCustomizer(Properties preferences){

		this.preferences = preferences;

		// init main box
		Box box = new Box(BoxLayout.Y_AXIS);
		// temp. buffers
		JPanel p;
		JPanel subP;
		JLabel l;

		subP = new JPanel(new GridLayout(1,2,5,5));
		l = new JLabel(" "+localize("format.dxf.Formatter.version")+" :");
		subP.add(l);
		{
			DXFVersion[] versions = DXFVersion.values();
			LocalizedDXFVersion[] localizedVersions = new LocalizedDXFVersion[versions.length];
			int i = 0;
			for(DXFVersion v : versions)
				localizedVersions[i++] = new LocalizedDXFVersion(v);
			version = new JComboBox(localizedVersions);
		}
		subP.add(version);
		box.add(subP);

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER; //1 element par ligne


		/* sous-panneau 1 : param�tres de formattage pour sortie DXF (marque d�pos�e) */
		p = new JPanel(gridBag);
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
		            localize("format.Formatter")));

		/* 1.1. PicEllipse */
		subP = new JPanel(new GridLayout(2,2,5,5));
		subP.setBorder(BorderFactory.createTitledBorder(
						   BorderFactory.createEtchedBorder(),
		            localize("format.dxf.Formatter.ellipse")));

		/* - PicEllipse circulaire*/
		l = new JLabel(" "+localize("format.dxf.Formatter.circleConversion")+" :");
		subP.add(l);
		elolCircle = new JComboBox();
		for(int i =0; i < OPTIONS_FMT_ELLIPSE_OUTLINE_CIRCLE.length;++i)
			elolCircle.addItem(
				localize(LOCAL_ID_CURVE[OPTIONS_FMT_ELLIPSE_OUTLINE_CIRCLE[i]]));
		subP.add(elolCircle);

		/* - PicEllipse � excentricit� < 1*/
		l = new JLabel(" "+localize("format.dxf.Formatter.ellipseConversion")+" :");
		subP.add(l);
		elolEllipse = new JComboBox();
		for(int i =0; i < OPTIONS_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE.length;++i)
			elolEllipse.addItem(
				localize(LOCAL_ID_CURVE[OPTIONS_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE[i]]));

		subP.add(elolEllipse);
		gridBag.setConstraints(subP, gbc);
		p.add(subP);


		/* 1.2 Lignes courbes  */
		subP = new JPanel(new GridLayout(1,2,5,5));
		subP.setBorder(BorderFactory.createTitledBorder(
						   BorderFactory.createEtchedBorder(),
		            localize("format.dxf.Formatter.multicurve")));

		l = new JLabel(" "+localize("format.dxf.Formatter.curveMulticurve")+" :");
		subP.add(l);

		curveMulticurve = new JComboBox();
		for(int i =0; i < OPTIONS_FMT_MULTICURVE .length;++i)
			curveMulticurve.addItem(
				localize(LOCAL_ID_CURVE[OPTIONS_FMT_MULTICURVE[i]]));

		subP.add(curveMulticurve);

		gridBag.setConstraints(subP, gbc);
		p.add(subP);


		/* 1.3 Lignes polygonales  */
		subP = new JPanel(new GridLayout(4,2,5,5));
		subP.setBorder(BorderFactory.createTitledBorder(
						   BorderFactory.createEtchedBorder(),
		            localize("format.dxf.Formatter.polyline")));

		/* - Corde d'un arc de PicEllipse � corde*/
		l = new JLabel(" "+localize("format.dxf.Formatter.plChord")+" :");
		subP.add(l);
		plChord = new JComboBox();
		for(int i =0; i < OPTIONS_FMT_POLYLINE_ELLIPSE_CHORD.length;++i)
			plChord.addItem(
				localize(LOCAL_ID_POLYLINE[OPTIONS_FMT_POLYLINE_ELLIPSE_CHORD[i]]));


		subP.add(plChord);
		/* - Coin d'un arc PicEllipse en camembert*/
		l = new JLabel(" "+localize("format.dxf.Formatter.plPie")+" :");
		subP.add(l);
		plPie = new JComboBox();
		for(int i =0; i < OPTIONS_FMT_POLYLINE_ELLIPSE_PIE_LINE.length;++i)
			plPie.addItem(
				localize(LOCAL_ID_POLYLINE[OPTIONS_FMT_POLYLINE_ELLIPSE_PIE_LINE[i]]));
		subP.add(plPie);
		/* - Section maximale de PicMultiCurve polygonale */
		l = new JLabel(" "+localize("format.dxf.Formatter.plMultiCurve")+" :");
		subP.add(l);
		plMultiCurve = new JComboBox();
		for(int i =0; i < OPTIONS_FMT_POLYLINE_POLYGON_MULTICURVE.length;++i)
			plMultiCurve.addItem(
				localize(LOCAL_ID_POLYLINE[OPTIONS_FMT_POLYLINE_POLYGON_MULTICURVE[i]]));
		subP.add(plMultiCurve);
		/* - PicParallelogram */
		l = new JLabel(" "+localize("format.dxf.Formatter.plParallelogram")+" :");
		subP.add(l);
		plParallelogram = new JComboBox();
		for(int i =0; i < OPTIONS_FMT_POLYLINE_PARALLELOGRAM.length;++i)
			plParallelogram.addItem(
				localize(LOCAL_ID_POLYLINE[OPTIONS_FMT_POLYLINE_PARALLELOGRAM[i]]));
		subP.add(plParallelogram);

		gridBag.setConstraints(subP, gbc);
		p.add(subP);

		/* 1.4 Texte  */
		subP = new JPanel(new GridLayout(1,2,5,5));
		subP.setBorder(BorderFactory.createTitledBorder(
						   BorderFactory.createEtchedBorder(),
		            localize("format.dxf.Formatter.text")));

		p.add(subP);

		gridBag.setConstraints(subP, gbc);
		box.add(p);


		/* sous-panneau 2 : param�tre d'analyse  pour entr�e DXF (marque d�pos�e) */
		p = new JPanel(new GridLayout(1,2,5,5));
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
		            localize("format.Parser")));


		box.add(p);

		// completed :
		add(box, BorderLayout.NORTH);
	}

	/**
	 * @return le titre de la panneau, utilis� e.g. pour les titre de Border
	 * ou de Tabpane.
	 * @since jPicEdt 1.6
	 */
	public String getTitle(){
		return "DXF";
	}


	/**
	 * @return l'ic�ne associ�e � ce panneau, utilis� e.g. pour la
	 * d�coration des onglets TabbedPane
	 * @since jPicEdt 1.6
	 */
	public Icon getIcon(){
		return null;
	}


	/**
	 * @return la cha�ne  tooltip associ�e � ce panneau
	 * @since jPicEdt 1.6
	 */
	public String getTooltip(){
		return localize("format.dxf.tooltip");
	}

	/**
	 * Convertit le choix de l'utilisateur sur un jComboBox en un index
	 * identifiant l'option.
	 * @since jPicEdt 1.6
	 */
	private int getIndex(JComboBox jComboBox,int[] options){
		int id = jComboBox.getSelectedIndex();
		return options[id];
	}
	/**
	 * Convertit un indexe identifiant une option, en une s�lection
	 * correspondante dans une JComboBox.
	 * @since jPicEdt 1.6
	 */
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
	 * Convertit l'�tat de l'attribut priv� dxfCustomProperties vers
	 * l'ensemble des jComboBox.
	 * @since jPicEdt 1.6
	 */
	private void propertiesToWidget(){
		version.setSelectedIndex(
			dxfCustomProperties.getDXFVersion().getValue());
		setIndex(elolCircle     ,
				 OPTIONS_FMT_ELLIPSE_OUTLINE_CIRCLE       ,
				 dxfCustomProperties.getElolCircle()     );
		setIndex(elolEllipse    ,
				 OPTIONS_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE ,
				 dxfCustomProperties.getElolEllipse()    );
		setIndex(curveMulticurve,
				 OPTIONS_FMT_MULTICURVE                   ,
				 dxfCustomProperties.getCurveMulticurve());
		setIndex(plChord        ,
				 OPTIONS_FMT_POLYLINE_ELLIPSE_CHORD       ,
				 dxfCustomProperties.getPlChord()        );
		setIndex(plPie          ,
				 OPTIONS_FMT_POLYLINE_ELLIPSE_PIE_LINE    ,
				 dxfCustomProperties.getPlPie()          );
		setIndex(plMultiCurve   ,
				 OPTIONS_FMT_POLYLINE_POLYGON_MULTICURVE  ,
				 dxfCustomProperties.getPlMulticurve()   );
		setIndex(plParallelogram,
				 OPTIONS_FMT_POLYLINE_PARALLELOGRAM       ,
				 dxfCustomProperties.getPlParallelogram());
	}

	/**
	 * Convertit l'�tat de l'ensemble des jComboBox vers l'attribut priv�
	 * dxfCustomProperties.
	 * @since jPicEdt 1.6
	 */
	private void widgetToProperties(){
		dxfCustomProperties.setDXFVersion(
			((LocalizedDXFVersion)version.getSelectedItem()).getVersion());
		dxfCustomProperties.setElolCircle(
			getIndex(elolCircle     ,
					 OPTIONS_FMT_ELLIPSE_OUTLINE_CIRCLE       ));
		dxfCustomProperties.setElolEllipse(
			getIndex(elolEllipse    ,
					 OPTIONS_FMT_ELLIPSE_OUTLINE_TRUE_ELLIPSE ));
		dxfCustomProperties.setCurveMulticurve(
			getIndex(curveMulticurve,
					 OPTIONS_FMT_MULTICURVE                   ));
		dxfCustomProperties.setPlChord(
			getIndex(plChord        ,
					 OPTIONS_FMT_POLYLINE_ELLIPSE_CHORD       ));
		dxfCustomProperties.setPlPie(
			getIndex(plPie          ,
					 OPTIONS_FMT_POLYLINE_ELLIPSE_PIE_LINE    ));
		dxfCustomProperties.setPlMulticruve(
			getIndex(plMultiCurve   ,
					 OPTIONS_FMT_POLYLINE_POLYGON_MULTICURVE  ));
		dxfCustomProperties.setPlParallelogram(
			getIndex(plParallelogram,
					 OPTIONS_FMT_POLYLINE_PARALLELOGRAM       ));
	}

	/**
	 * Charge le contenu d'affichage des widgets avec les valeurs par d�faut.
	 * prises des DXFContants.
	 * @since jPicEdt 1.6
	 */
	public void loadDefault() {
		dxfCustomProperties.loadDefault();
		propertiesToWidget();
	}

	/**
	 * Charge les valeurs du widgets � partir de l'objet <code>Properties</code> pass� au
	 * constructeur.
	 * @since jPicEdt 1.6
	 */
	public void load() {
		load(preferences);
		propertiesToWidget();
	}

	/**
	 * Stocke les valeurs courant dans ce widget vers l'objet <code>Properties</code> pass� au constructeur,
	 * puis mise � jour de <code>DXFFormatter</code>.
	 * @since jPicEdt 1.6
	 */
	public void store(){
		 widgetToProperties();
		 store(preferences);
		 DXFFormatter.configure(preferences); // mise � jour.
	 }


	// Redirection de dxfCustomProerties
	//#######################################################################

	/**
	 * Charge les propri�t�s DXF (marque d�pos�e) � partir de l'objet <code>Properties</code> <code>prop</code>.
	 * @param prop les valeurs du widget � charger.
	 * @since jPicEdt 1.6
	 */
	public void load(Properties prop) {
		dxfCustomProperties.load(prop);
	}



	/**
	 * Stocke les prori�t�s DXF (marque d�pos�e) dans l'objet <code>prop</code>.
	 * @since jPicEdt 1.6
	 */
	public void store(Properties prop){
		dxfCustomProperties.store(prop);
	}

	public DXFCustomization getCustomization(){ return dxfCustomProperties; }
}

/// DXFCustomizer.java ends here
