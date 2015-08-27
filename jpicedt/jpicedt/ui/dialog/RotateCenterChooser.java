// RotateCenterChooser.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011/2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: RotateCenterChooser.java,v 1.5 2013/03/27 06:51:46 vincentb1 Exp $
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.lang.Double;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jpicedt.JPicEdt;
import jpicedt.Localizer;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.PicPoint;
import jpicedt.ui.MDIManager;
import jpicedt.widgets.MDIComponent;

import static jpicedt.Localizer.localize;
import static java.lang.Math.round;


/**
 * La classe <code>RotateCenterChooser</code> sert à choisir le centre de
 * rotation pour l'outil rotation, lorsque la rotation est activée depuis la
 * <code>GridZoomToolBar</code> plutôt que par pointage.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 * @version $Id: RotateCenterChooser.java,v 1.5 2013/03/27 06:51:46 vincentb1 Exp $
 * @see jpicedt.ui.PEDrawingBoard.GridZoomToolBar
 */
public class RotateCenterChooser implements ActionListener{
	private final String KEY_CENTER = "ui.RotateCenterChooser.center";
	private final String KEY_POS = "ui.RotateCenterChooser.pos";
	private final String KEY_REAJUSTABLE = "ui.RotateCenterChooser.reajustable";

	private MDIComponent frame;
	private MDIManager mdimgr;
	private JCheckBox cbReajustable;
	private JButton buttonOk;
	private JButton buttonCancel;

	private double xOffset,yOffset;

	class Gravity{
		public double value;
		char[] tags;
		public Gravity(char[] tags){ this.tags = tags; value = Double.NaN; }
		public char getTag(){ return tags[(int)round(value*2)]; }
		public void setFromTag(char tag){
			for(int i =0; i < tags.length; ++i)
				if(tags[i] == tag){
					value = i*0.5;
					return;
				}
		}
	}

	public static class RotateCenterChoice{
		public PicPoint    pt = new PicPoint();
		public Rectangle2D bb = new Rectangle2D.Double();
		public boolean     reajustable;
		public boolean     cancelled;


	}

	private RotateCenterChoice choice;

	final char[] xGravityTags =  {'l','c','r'};
	final char[] yGravityTags =  {'b','m','t'};

	protected Gravity xGravity = new Gravity(xGravityTags);
	protected Gravity yGravity = new Gravity(yGravityTags);

	class GravityToggleButton extends JToggleButton{
		double referenceGravity;
		Gravity gravity;

		void update(){
			if(isSelected())
				gravity.value = referenceGravity;
		}
		GravityToggleButton(JPanel pannel,String icon,double referenceGravity,
							Gravity gravity,ButtonGroup bg){
			super(
				PEToolKit.createImageIcon("action.editorkit.RotateCenterChooser." + icon));

			this.referenceGravity = referenceGravity;
			this.gravity = gravity;
			pannel.add(this);
			bg.add(this);
			setSelected(gravity.value == referenceGravity);
			addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent e){
						GravityToggleButton button = (GravityToggleButton)e.getSource();
						button.update();
					}

				});
		}
	}

	private enum OffsetUnit {
		PERCENT("%"), MM("mm") , ERROR("");
		public String label;
		OffsetUnit(String label){ this.label=  label; }
	};
	private OffsetUnit  xOffsetUnit, yOffsetUnit;

	private GravityToggleButton buttonGravityTop;
	private GravityToggleButton buttonGravityMid;
	private GravityToggleButton buttonGravityBottom;
	private GravityToggleButton buttonGravityLeft;
	private GravityToggleButton buttonGravityCenter;
	private GravityToggleButton buttonGravityRight;

	private OffsetUnit parseOffsetUnit(String s){
		if(s.equals("%"))
			return OffsetUnit.PERCENT;
		else if(s.equals("mm"))
			return OffsetUnit.MM;
		else
			return OffsetUnit.ERROR;
	}

	public RotateCenterChooser(MDIManager mdimgr,RotateCenterChoice choice) {

		this.choice = choice;

		Pattern preferenceAnalysis = Pattern.compile("^([lcr])([tmb])([-+]?[0-9.e]+)(%|mm),([-+]?[0-9.e]+)(%|mm)\\s*$");
		String preference = JPicEdt.getProperty(KEY_CENTER,"mc0mm,0mm");
		Matcher matcher = preferenceAnalysis.matcher(preference);
		if(matcher.matches()){
			xGravity.setFromTag(preference.charAt(0));
			yGravity.setFromTag(preference.charAt(1));
			xOffset = Double.valueOf(matcher.group(3));
			xOffsetUnit = parseOffsetUnit(matcher.group(4));
			yOffset = Double.valueOf(matcher.group(5));
			yOffsetUnit = parseOffsetUnit(matcher.group(6));
		}
		else{
			xGravity.value = yGravity.value = 0.5;
			xOffset = yOffset = 0.0;
			xOffsetUnit =  yOffsetUnit = OffsetUnit.MM;
		}

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;

		JPanel contentPane = new JPanel(gbl,true);

		JPanel gravityPanel = new JPanel(new GridLayout(3,2,4,4),true);

		ButtonGroup bgVert  = new ButtonGroup();
		ButtonGroup bgHoriz = new ButtonGroup();

		buttonGravityTop    = new GravityToggleButton(gravityPanel,"t",1.0,yGravity,bgVert);
		buttonGravityLeft   = new GravityToggleButton(gravityPanel,"l",0.0,xGravity,bgHoriz);
		buttonGravityMid    = new GravityToggleButton(gravityPanel,"m",0.5,yGravity,bgVert);
		buttonGravityCenter = new GravityToggleButton(gravityPanel,"c",0.5,xGravity,bgHoriz);
		buttonGravityBottom = new GravityToggleButton(gravityPanel,"b",0.0,yGravity,bgVert);
		buttonGravityRight  = new GravityToggleButton(gravityPanel,"r",1.0,xGravity,bgHoriz);

		gravityPanel.setBorder(BorderFactory.createEtchedBorder());
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 2.0;
		gbl.setConstraints(gravityPanel,gbc);
		contentPane.add(gravityPanel);
		gbc.gridy ++;


		cbReajustable = new JCheckBox(localize("action.editorkit.RotateCenterChooser.reajustable"));
		cbReajustable.setSelected(JPicEdt.getProperty(KEY_REAJUSTABLE,false));
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbl.setConstraints(cbReajustable,gbc);
		contentPane.add(cbReajustable);
		gbc.gridy ++;

		buttonOk = new JButton(localize("button.OK"));
		buttonOk.addActionListener(this);
		buttonCancel = new JButton(localize("button.Cancel"));
		buttonCancel.addActionListener(this);
		gbc.gridwidth=2;
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.weightx = 1.0;
		gbl.setConstraints(buttonOk,gbc);
		contentPane.add(buttonOk);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbl.setConstraints(buttonCancel,gbc);
		contentPane.add(buttonCancel);
		gbc.gridy ++;

		String title = localize("action.editorkit.RotateCenterChooser");
		boolean modal = true;
		frame = mdimgr.createDialog(title, modal, contentPane);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


		Dimension dlgSize = frame.getPreferredSize();
		frame.setSize(dlgSize);

		//this.pack();

		initGeometry(); // size and location on screen


		frame.setVisible(true);

	}


	void initGeometry(){
		Properties preferences = JPicEdt.getPreferences();

		Dimension rootSize;
		if (frame instanceof JInternalFrame)
			rootSize = ((JInternalFrame)frame).getDesktopPane().getSize();
		else
			rootSize = Toolkit.getDefaultToolkit().getScreenSize();

		Dimension frameSize = frame.getSize();
		if (frameSize.height > rootSize.height)
			frameSize.height = rootSize.height;
		if (frameSize.width > rootSize.width)
			frameSize.width = rootSize.width;

		int x = rootSize.width - frameSize.width;
		int y = (rootSize.height -frameSize.height)/2;

		frame.setLocation(JPicEdt.getProperty(KEY_POS + ".x", x),
						  JPicEdt.getProperty(KEY_POS + ".y", y));

	}

	void updateRotateCenter(boolean reajustable){
		double x = choice.bb.getX() + xGravity.value*choice.bb.getWidth();
		switch(xOffsetUnit){
		case PERCENT: x += xOffset*choice.bb.getWidth(); break;
		case MM: x += xOffset; break;
		}

		double y = choice.bb.getY() + yGravity.value*choice.bb.getHeight();
		switch(yOffsetUnit){
		case PERCENT: y += yOffset*choice.bb.getWidth(); break;
		case MM: y += yOffset; break;
		}

		choice.pt.setCoordinates(x,y);
		choice.cancelled = false;
		choice.reajustable = reajustable;

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonOk) {

			updateRotateCenter(cbReajustable.isSelected());

			StringBuffer preferredCenter  = new StringBuffer();
			preferredCenter.append(xGravity.getTag());
			preferredCenter.append(yGravity.getTag());
			preferredCenter.append(xOffset);
			preferredCenter.append(xOffsetUnit.label);
			preferredCenter.append(',');
			preferredCenter.append(yOffset);
			preferredCenter.append(yOffsetUnit.label);
			Properties preferences = JPicEdt.getPreferences();
			preferences.setProperty(KEY_CENTER,preferredCenter.toString());

			Point pos = frame.getLocation();

			preferences.setProperty(KEY_POS + ".x",Integer.toString((int)pos.getX()));
			preferences.setProperty(KEY_POS + ".y",Integer.toString((int)pos.getY()));

			preferences.setProperty(KEY_REAJUSTABLE,Boolean.toString(cbReajustable.isSelected()));

			frame.dispose();
		}
		else if(e.getSource() == buttonCancel){
			choice.cancelled = true;

			Point pos = frame.getLocation();

			Properties preferences = JPicEdt.getPreferences();
			preferences.setProperty(KEY_POS + ".x",Integer.toString((int)pos.getX()));
			preferences.setProperty(KEY_POS + ".y",Integer.toString((int)pos.getY()));

			frame.dispose();
		}
	}
}


/// RotateCenterChooser.java ends here
