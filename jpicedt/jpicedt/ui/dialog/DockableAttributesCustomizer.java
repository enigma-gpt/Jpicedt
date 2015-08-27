// DockableAttributesCustomizer.java --- -*- coding: iso-8859-1 -*-
// February 27, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: DockableAttributesCustomizer.java,v 1.32 2013/03/27 06:52:36 vincentb1 Exp $
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

import jpicedt.JPicEdt;
import jpicedt.Localizer;
import jpicedt.MiscUtilities;
import jpicedt.ui.MDIManager;
import jpicedt.ui.PEDrawingBoard;
import jpicedt.widgets.*;
import jpicedt.format.output.util.ColorFormatter;
import jpicedt.format.output.util.ColorFormatter.ColorEncoding;
import jpicedt.format.output.util.NamedColor;
import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.io.formatter.*;
import jpicedt.graphic.event.*;
import jpicedt.graphic.view.ArrowView;
import java.awt.GridLayout;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.event.*;
import javax.swing.colorchooser.*;
import java.util.*;
import static java.lang.Math.sqrt;
import static java.lang.Math.ceil;

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicText.*;
import static jpicedt.Localizer.*;

/**
 * A dockable customizer for attributes (ie PicAttributesSet).
 * This is currently implemented as a floating JPanel.
 * @author Sylvain Reynal
 * @since jPicEdt 1.3.2
 */
public class DockableAttributesCustomizer extends JPanel
	implements SelectionListener, ChangeListener, PropertyChangeListener {

	/** key for persistent storage */
	public static final String KEY = "dockable-panel.Attributes";

	//private Element obj; // current selection
	private PicAttributeSet inputAttributes=new PicAttributeSet(); // work-around for start-up
	private JLabel titleLbl;
	private HatchPropertiesPanel hatchPropertiesPanel; // enabled/disabled by FillPropertiesPanel
	private JTabbedPane tabbedPane;
	private Box box; // container for the tabbed pane
	private PECanvas canvas; // selectionUpdate takes care of keeping the customizer state synchronized with the selection content of the active canvas
	private boolean isEditingUnderway=false; // trick to know when to add a new UndoableEvent

	/** for debugging purpose ; run it if you just wanna see how this particular GUI looks like
	 * w/o having to run the whole jPicEdt application */
	public static void main(String[] args){

		JFrame f = new JFrame("test");
		f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
		f.getContentPane().add(new DockableAttributesCustomizer(null));
		f.pack();
		f.setVisible(true);

	}

	/**
	 * Create a <code>DockableAttributesCustomizer</code> with no specific <code>PECanvas</code> attached to
	 * it.
	 * @param progressBar when non-<code>null</code>, <code>increment()</code> is called at some points during
	 * the construction process.
	 */
	public DockableAttributesCustomizer(jpicedt.ui.util.PEProgressBar progressBar){
		setLayout(new BorderLayout(5,5));
		box = new Box(BoxLayout.Y_AXIS);

		// title
		titleLbl = new JLabel("[]"); // no title by default
		box.add(titleLbl);

		if (progressBar != null) progressBar.increment();

		// attribute pane :
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(this);
		AbstractCustomizer[] customizers = new AbstractCustomizer[8];
		if (progressBar != null) progressBar.increment();
		customizers[0] = new FillPropertiesPanel();
		customizers[1] = hatchPropertiesPanel  =new HatchPropertiesPanel();
		customizers[2] = new StrokePropertiesPanel();
		customizers[3] = new ShadowPropertiesPanel();
		customizers[4] = new ArrowPropertiesPanel();
		customizers[5] = new PolydotsPropertiesPanel();
		customizers[6] = new TextPropertiesPanel();
		customizers[7] = new EditModeCustomPropertiesPanel();
		for (int i=0; i<customizers.length; i++){
			tabbedPane.addTab(customizers[i].getTitle(),customizers[i].getIcon(),customizers[i],customizers[i].getTooltip());
		}

		box.add(tabbedPane);
		this.add(box,BorderLayout.CENTER);
	}

	/**
	 * Changes the customizer title
	 */
	public void setTitle(String title){
		StringBuffer buf = new StringBuffer();
		buf.append("JPicEdt: ");
		buf.append('[');
		buf.append(title);
		buf.append(']');
		titleLbl.setText(buf.toString());
	}

	/**
	 * Called :
	 * - by the colorchooser if the user picks a new colour ;
	 * - or by the tabbed pane when a different pane is made active (ie brought on top)
	 */
	public void stateChanged(ChangeEvent e){
		//System.out.println(e);
		if (e.getSource()==tabbedPane){
			//System.out.println("Inner pane " + ((AbstractCustomizer)tabbedPane.getSelectedComponent()).getTitle() + " was made active...");
			((AbstractCustomizer)tabbedPane.getSelectedComponent()).load();
		}
	}



	/**
	 * Implementation of the SelectionListener interface ;
	 * called when a change occurs in the selection from the target PECanvas
	 */
	public void selectionUpdate(SelectionEvent e){
		if (isEditingUnderway) {
			canvas.endUndoableUpdate(); // mark compound edit as completed before
			isEditingUnderway = false;
		}
		// either switching to a new active board, or starting a new edit.

		// SelectionEvent.EventType type = e.getType(); not used
		canvas = (PECanvas)e.getSource();
		// synchronize state of active pane with newly selected object (or with input attribute):
		AbstractCustomizer c = (AbstractCustomizer)tabbedPane.getSelectedComponent();
		c.load();

	}

	/**
	 * Update customizer's title from the given event if it's a <code>MDIManager.ACTIVE_BOARD_CHANGE</code>
	 * event.
	 */
	public void propertyChange(PropertyChangeEvent e){ // posted from MDIManager.MDIDesktopManager
		if (e.getPropertyName()==MDIManager.ACTIVE_BOARD_CHANGE){
			if (e.getNewValue()==null) setTitle("");
			else {
				PEDrawingBoard b = (PEDrawingBoard)e.getNewValue();
				setTitle(b.getTitle());
				// make all open editor-kit share the same input attribute set:
				b.getCanvas().getEditorKit().setInputAttributes(inputAttributes); // not a deep copy !
				selectionUpdate(new SelectionEvent(b.getCanvas(), (Element)null, null));
			}
		}
	}


	/**
	 * @return the attribute value for the given attribute name ; guaranteed to never return a null pointer.
	 * Depending on the size of the selection in the currently active drawing board, either fetch
	 * attribute value from the selected graphical element, or from the editor-kit's input attribute-set.
	 */
	private <T> T getAttribute(PicAttributeName<T> name){
		if (canvas==null || canvas.getSelectionSize()==0) return inputAttributes.getAttribute(name);
		/*
		if (canvas.getSelectionSize()==0){
			return canvas.getEditorKit().getInputAttributes().getAttribute(name);
	}
		*/
		// or fetch attributes from the first selected element
		// [pending] we should "gray" the widgets or sth if more than two elements are selected
		else {
			return (canvas.selection().next()).getAttribute(name);
		}
	}

	/**
	 	 * Set the attribute value for the given attribute name. Depending on the size of the selection
	 * in the currently active drawing board, either acts upon the attribute set of the selected graphical
	 * element, or upon the input attribute-set of the editor kit attached to this drawing board.
	 	 */
	private <T> void setAttribute(PicAttributeName<T> name, T value){
		//System.out.println("canvas=" + canvas);
		if (canvas==null || canvas.getSelectionSize()==0) {
			this.inputAttributes.setAttribute(name,value); // safe copy
			/*
			// set same input attribute set for all open EditorKit's:
			MDIManager mdiManager = JPicEdt.getMDIManager();
			PEDrawingBoard[] allOpenBoards = mdiManager.getAllDrawingBoards();
			for (int i=0; i<allOpenBoards.length; i++){
				EditorKit kit = allOpenBoards[i].getCanvas().getEditorKit();
				if (kit != null) kit.getInputAttributes().setAttribute(name,value);
		}
			*/
			return;
		}
		else {
			if (!isEditingUnderway) { // start a new compound edit ; end-update is done when de-selecting
				canvas.beginUndoableUpdate(localize("attributes.Edit"));
				isEditingUnderway=true;
			}
			for(Iterator<Element> it = canvas.selection(); it.hasNext(); ){
				Element e = it.next();
				e.setAttribute(name, value);
				//System.out.println("setAttribute : name = " + name + ", value = " + value + " for e = " + e);
			}
		}
	}

	/**
		 * A panel for editing of Element's fill attributes
	 */
	public class FillPropertiesPanel extends AbstractCustomizer  implements ActionListener, ChangeListener {

		private PEComboBox<FillStyle> fillStyleCB;
		private JColorChooser colorChooser;
		private EnumMap<FillStyle,ImageIcon> fillStyleToIconMap;

		/**
		 * creates a JPanel for attributes editing
		 */
		public FillPropertiesPanel(){

			super();
			this.add(createFillPanel(), BorderLayout.NORTH);
			// add listeners
			fillStyleCB.addActionListener(this);
			colorChooser.getSelectionModel().addChangeListener(this); // fire change event when user picks a colour
		}

		/**
		 * @return the customizer title , used e.g. for Border or Tabpane title.
		 */
		public String getTitle(){
			return localize("attributes.Fill");
		}

		/**
		 * creates a sub-panel for "fill" properties
		 */
		private JComponent createFillPanel(){

			Box box = new Box(BoxLayout.Y_AXIS);
			// 1) fillStyle :
			fillStyleToIconMap = new EnumMap<FillStyle,ImageIcon>(FillStyle.class);
			fillStyleToIconMap.put(FillStyle.NONE, PEToolKit.createImageIcon("attributes.FillStyleNone"));
			fillStyleToIconMap.put(FillStyle.SOLID, PEToolKit.createImageIcon("attributes.FillStyleSolid"));
			fillStyleToIconMap.put(FillStyle.VLINES, PEToolKit.createImageIcon("attributes.FillStyleVlines"));
			fillStyleToIconMap.put(FillStyle.VLINES_FILLED, PEToolKit.createImageIcon("attributes.FillStyleVlinesFilled"));
			fillStyleToIconMap.put(FillStyle.HLINES, PEToolKit.createImageIcon("attributes.FillStyleHlines"));
			fillStyleToIconMap.put(FillStyle.HLINES_FILLED, PEToolKit.createImageIcon("attributes.FillStyleHlinesFilled"));
			fillStyleToIconMap.put(FillStyle.CROSSHATCH, PEToolKit.createImageIcon("attributes.FillStyleCrosshatches"));
			fillStyleToIconMap.put(FillStyle.CROSSHATCH_FILLED, PEToolKit.createImageIcon("attributes.FillStyleCrosshatchesFilled"));
			box.add(fillStyleCB = createComboBox(fillStyleToIconMap));

			// 2) color chooser :
			colorChooser = createColorChooser();
			box.add(colorChooser);

			return box;
		}

		/**
		 * update widgets values according to currently active attribute set.
		 */
		public void load(){

			// first remove listeners to avoid deadlocks (through recursion) when setting widgets values ;-)
			fillStyleCB.removeActionListener(this);
			colorChooser.getSelectionModel().removeChangeListener(this);

			colorChooser.setColor(getAttribute(FILL_COLOR));

			FillStyle style = (FillStyle)getAttribute(FILL_STYLE);
			fillStyleCB.setSelectedKey(style);
			switch (style){
			case NONE:
			case SOLID:
				hatchPropertiesPanel.setEnabled(false);
				break;
			case VLINES:
			case VLINES_FILLED:
			case HLINES:
			case HLINES_FILLED:
			case CROSSHATCH:
			case CROSSHATCH_FILLED:
				hatchPropertiesPanel.setEnabled(true);
				break;
			default:
			}

			fillStyleCB.addActionListener(this);
			colorChooser.getSelectionModel().addChangeListener(this); // fire change event when user picks a colour
		}

		/**
		 * does nothing (handled by actionPerformed)
		 */
		public void store(){
		}

		/**
		 * Called by the colorchooser when the user picks a new colour, and this customizer lies currently
		 * on top.
		 */
		public void stateChanged(ChangeEvent e){
			setAttribute(FILL_COLOR,colorChooser.getColor());
		}

		/**
		 * synchronize the currently active attribute set with the widget's state.
		 */
		public void actionPerformed(ActionEvent e){

			FillStyle fillStyle = fillStyleCB.getSelectedKey();
			setAttribute(FILL_STYLE, fillStyle);
			switch (fillStyle){
			case NONE:
			case SOLID:hatchPropertiesPanel.setEnabled(false);break;
			case VLINES:
			case VLINES_FILLED:
			case HLINES:
			case HLINES_FILLED:
			case CROSSHATCH:
			case CROSSHATCH_FILLED:hatchPropertiesPanel.setEnabled(true);break;
			default:
			}
		}

	} // class


	/**
	 * A panel for editing of Element's hatch attributes
	 */
	public class HatchPropertiesPanel extends AbstractCustomizer  implements ActionListener, ChangeListener {

		private IncrementableTextField hatchWidthTF, hatchSepTF, hatchAngleTF;
		private JColorChooser colorChooser; // hatches color

		/**
		 * creates a JPanel for attributes editing
		 */
		public HatchPropertiesPanel(){

			super();
			this.add(createHatchPanel(), BorderLayout.NORTH);
			// add listeners
			hatchWidthTF.addActionListener(this);
			hatchSepTF.addActionListener(this);
			hatchAngleTF.addActionListener(this);
			colorChooser.getSelectionModel().addChangeListener(this); // fire change event when user picks a colour
		}

		/**
		 * @return the customizer title , used e.g. for Border or Tabpane title.
		 */
		public String getTitle(){
			return localize("attributes.Hatch");
		}

		public void setEnabled(boolean b){
			hatchWidthTF.setEnabled(b);
			hatchSepTF.setEnabled(b);
			hatchAngleTF.setEnabled(b);
			//colorChooser.setEnabled(b); // ca marche pas !
		}

		/**
		 * creates a sub-panel for "fill" properties
		 */
		private JComponent createHatchPanel(){

			Box box = new Box(BoxLayout.Y_AXIS);

			JPanel p = new JPanel(new GridLayout(1,3,5,5));
			// 1) hatch width :
			hatchWidthTF = createIncrementableTextField(0.05,"attributes.HatchWidth","mm");
			hatchWidthTF.setMinimum(0);
			hatchWidthTF.setLowBounded(true);
			p.add(hatchWidthTF);

			// 2) hatch sep :
			hatchSepTF = createIncrementableTextField(0.05,"attributes.HatchSep","mm");
			hatchSepTF.setMinimum(0);
			hatchSepTF.setLowBounded(true);
			p.add(hatchSepTF);

			// 3) hatch angle :
			hatchAngleTF = createIncrementableTextField(5.0,"attributes.HatchAngle","deg");
			p.add(hatchAngleTF);
			box.add(p);

			// 4) hatch color :
			box.add(colorChooser = createColorChooser());

			return box;
		}

		/**
		 * update widgets values according to currently active attribute set.
		 */
		public void load(){

			// first remove listeners to avoid deadlocks (through recursion) when setting widgets values ;-)
			hatchWidthTF.removeActionListener(this);
			hatchSepTF.removeActionListener(this);
			hatchAngleTF.removeActionListener(this);
			colorChooser.getSelectionModel().removeChangeListener(this);

			hatchWidthTF.setValue(getAttribute(HATCH_WIDTH));
			hatchSepTF.setValue(getAttribute(HATCH_SEP));
			hatchAngleTF.setValue(getAttribute(HATCH_ANGLE));
			colorChooser.setColor(getAttribute(HATCH_COLOR));

			hatchWidthTF.addActionListener(this);
			hatchSepTF.addActionListener(this);
			hatchAngleTF.addActionListener(this);
			colorChooser.getSelectionModel().addChangeListener(this); // fire change event when user picks a colour
		}

		/**
		 * does nothing (handled by actionPerformed)
		 */
		public void store(){
		}

		/**
		 * Called by the colorchooser when the user picks a new colour, and this customizer lies currently
		 * on top.
		 */
		public void stateChanged(ChangeEvent e){
			setAttribute(HATCH_COLOR,colorChooser.getColor());
		}

		/**
		 * synchronize the currently active attribute set with the widget's state.
		 */
		public void actionPerformed(ActionEvent e){

			// HATCH WIDTH
			if (e.getSource() == hatchWidthTF){
				setAttribute(HATCH_WIDTH, hatchWidthTF.getValue());
			}
			// HATCH SEP
			else if (e.getSource() == hatchSepTF){
				setAttribute(HATCH_SEP, hatchSepTF.getValue());
			}
			// HATCH ANGLE
			else if (e.getSource() == hatchAngleTF){
				setAttribute(HATCH_ANGLE, hatchAngleTF.getValue());
			}
		}

	} // class


	// -------------------- Arrows ---------------------------- //

	/**
	 * A panel for editing of Element's attributeSet
	 *
	 * see GraphicPropertiesToolBar which is very similar (though it has less widget than this Panel)
	 */
	public class ArrowPropertiesPanel extends AbstractCustomizer  implements ActionListener {

		private PEComboBox<ArrowStyle> leftArrowCB,rightArrowCB;
		private JTabbedPane tabbedPane; // one pane for each family of arrows
		private Map<ArrowStyle,ImageIcon> arrowIconsLeft, arrowIconsRight;
		private IncrementableTextField globalScaleWidthTF, globalScaleLengthTF;
		private IncrementableTextField headWidthMinimumMmTF, headWidthLineWidthScaleTF;
		private IncrementableTextField lengthScaleTF, insetScaleTF;
		private IncrementableTextField tbarWidthMinimumMmTF, tbarWidthLineWidthScaleTF;
		private IncrementableTextField bracketLengthScaleTF, roundedBracketLengthScaleTF;
		private IncrementableTextField dotSizeMinimumMmTF, dotSizeLineWidthScaleTF;

		/**
		 * creates a JPanel for attributes editing
		 */
		public ArrowPropertiesPanel(){

			super();
			arrowIconsLeft = ArrowView.createArrowIcons(ArrowView.Direction.LEFT);
			arrowIconsRight = ArrowView.createArrowIcons(ArrowView.Direction.RIGHT);
			this.add(createArrowPanel(),BorderLayout.NORTH);
			// add listeners...
			leftArrowCB.addActionListener(this);
			rightArrowCB.addActionListener(this);
			globalScaleWidthTF.addActionListener(this);
			globalScaleLengthTF.addActionListener(this);
			headWidthMinimumMmTF.addActionListener(this);
			headWidthLineWidthScaleTF.addActionListener(this);
			lengthScaleTF.addActionListener(this);
			insetScaleTF.addActionListener(this);
			tbarWidthMinimumMmTF.addActionListener(this);
			tbarWidthLineWidthScaleTF.addActionListener(this);
			bracketLengthScaleTF.addActionListener(this);
			roundedBracketLengthScaleTF.addActionListener(this);
			dotSizeMinimumMmTF.addActionListener(this);
			dotSizeLineWidthScaleTF.addActionListener(this);
		}

		/**
		 * @return the customizer title , used e.g. for Border or Tabpane title.
		 */
		public String getTitle(){
			return localize("attributes.Arrow");
		}

		/**
		 * create a sub-panel for arrows
		 */
		private Box createArrowPanel(){
			Box box = new Box(BoxLayout.Y_AXIS);
			JPanel p;

			// *) arrow choice panel
			p = new JPanel(new GridLayout(1,2,5,5));
			// left arrow:
			leftArrowCB = createComboBox(arrowIconsLeft);
			p.add(leftArrowCB);
			// right arrow:
			rightArrowCB = createComboBox(arrowIconsRight);
			p.add(rightArrowCB);
			box.add(p);

			// *) global scale factors
			p = new JPanel(new GridLayout(1,2,5,5));
			// global scale factors
			p.add(globalScaleWidthTF=new IncrementableTextField(0, 0.05, createLabel("attributes.ArrowGlobalScaleWidth"), null, false));
			globalScaleWidthTF.setMinimum(0);
			globalScaleWidthTF.setLowBounded(true);
			p.add(globalScaleLengthTF=new IncrementableTextField(0, 0.05, createLabel("attributes.ArrowGlobalScaleLength"), null, false));
			globalScaleLengthTF.setMinimum(0);
			globalScaleLengthTF.setLowBounded(true);
			box.add(p);

			// *) params specific to each arrow family
			tabbedPane = new JTabbedPane();

			// a) arrow heads: width
			JPanel pHeads = new JPanel(new GridLayout(3,1,5,5));
			pHeads.setBorder(BorderFactory.createEtchedBorder());
			p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(createLabel("attributes.ArrowHeadWidth"));
			p.add(headWidthMinimumMmTF=new IncrementableTextField(0, 0.05, new JLabel("="), new JLabel("mm"), false));
			headWidthMinimumMmTF.setMinimum(0);
			headWidthMinimumMmTF.setLowBounded(true);
			p.add(new JLabel("+ "));
			p.add(createLabel("attributes.LineWidth"));
			p.add(headWidthLineWidthScaleTF=new IncrementableTextField(0, 0.05, new JLabel("* "), null, false));
			headWidthLineWidthScaleTF.setMinimum(0);
			headWidthLineWidthScaleTF.setLowBounded(true);
			pHeads.add(p);
			// *) arrow heads: length
			p= new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(createLabel("attributes.ArrowHeadLength"));
			p.add(lengthScaleTF=new IncrementableTextField(0, 0.05, new JLabel("="), null, false));
			lengthScaleTF.setMinimum(0);
			lengthScaleTF.setLowBounded(true);
			p.add(new JLabel("* ("));
			p.add(createLabel("attributes.ArrowHeadWidth"));
			p.add(new JLabel(")"));
			pHeads.add(p);
			// *) arrow heads: inset
			p= new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(createLabel("attributes.ArrowHeadInset"));
			p.add(insetScaleTF=new IncrementableTextField(0, 0.05, new JLabel("="), null, false));
			insetScaleTF.setLowBounded(false);
			p.add(new JLabel("* ("));
			p.add(createLabel("attributes.ArrowHeadWidth"));
			p.add(new JLabel(")"));
			pHeads.add(p);
			tabbedPane.addTab(localize( // JHf
			                          "attributes.Heads"),null,pHeads,null); // JHf

			// b) T-bars width
			JPanel pTbars = new JPanel(new GridLayout(3,1,5,5));
			pTbars.setBorder(BorderFactory.createEtchedBorder());
			p= new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(createLabel("attributes.ArrowTBarWidth"));
			p.add(tbarWidthMinimumMmTF=new IncrementableTextField(0, 0.05, new JLabel("="), new JLabel("mm"), false));
			tbarWidthMinimumMmTF.setMinimum(0);
			tbarWidthMinimumMmTF.setLowBounded(true);
			p.add(new JLabel("+ "));
			p.add(createLabel("attributes.LineWidth"));
			p.add(tbarWidthLineWidthScaleTF=new IncrementableTextField(0, 0.05, new JLabel("* "), null, false));
			tbarWidthLineWidthScaleTF.setMinimum(0);
			tbarWidthLineWidthScaleTF.setLowBounded(true);
			pTbars.add(p);
			// *) bracket length
			p= new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(createLabel("attributes.ArrowBracketLength"));
			p.add(bracketLengthScaleTF=new IncrementableTextField(0, 0.05, new JLabel("="), null, false));
			bracketLengthScaleTF.setMinimum(0);
			bracketLengthScaleTF.setLowBounded(true);
			p.add(new JLabel("* ("));
			p.add(createLabel("attributes.ArrowTBarWidth"));
			p.add(new JLabel(")"));
			pTbars.add(p);
			// *) R-bracket length
			p= new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(createLabel("attributes.ArrowRoundedBracketLength"));
			p.add(roundedBracketLengthScaleTF=new IncrementableTextField(0, 0.05, new JLabel("="), null, false));
			roundedBracketLengthScaleTF.setMinimum(0);
			roundedBracketLengthScaleTF.setLowBounded(true);
			p.add(new JLabel("* ("));
		p.add(createLabel("attributes.ArrowTBarWidth"));
			p.add(new JLabel(")"));
			pTbars.add(p);
			tabbedPane.addTab(localize( // JHf
			                          "attributes.BarsBrackets"),null,pTbars,null); // JHf

			// c) dot size (circles and disks)
			JPanel pDotSize = new JPanel(new GridLayout(3,1,5,5));
			pDotSize.setBorder(BorderFactory.createEtchedBorder());
			p= new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(createLabel("attributes.ArrowDotSize"));
			p.add(dotSizeMinimumMmTF=new IncrementableTextField(0, 0.05, new JLabel("="), new JLabel("mm"), false));
			dotSizeMinimumMmTF.setMinimum(0);
			dotSizeMinimumMmTF.setLowBounded(true);
			p.add(new JLabel("+ "));
			p.add(createLabel("attributes.LineWidth"));
			p.add(dotSizeLineWidthScaleTF=new IncrementableTextField(0, 0.05, new JLabel("* "), null, false));
			dotSizeLineWidthScaleTF.setMinimum(0);
			dotSizeLineWidthScaleTF.setLowBounded(true);
			pDotSize.add(p);
			tabbedPane.addTab(localize( // JHf
			                          "attributes.CirclesDisks"),null,pDotSize,null); // JHf

			box.add(tabbedPane);
			return box;
		}

		private JLabel createLabel(String s){
			return new JLabel(localize(s));
		}

		/**
		 * update widgets values according to currently active attribute set.
		 */
		public void load(){
			// first remove listeners to avoid deadlocks (through recursion) when setting widgets values ;-)
			leftArrowCB.removeActionListener(this);
			rightArrowCB.removeActionListener(this);
			globalScaleWidthTF.removeActionListener(this);
			globalScaleLengthTF.removeActionListener(this);
			headWidthMinimumMmTF.removeActionListener(this);
			headWidthLineWidthScaleTF.removeActionListener(this);
			lengthScaleTF.removeActionListener(this);
			insetScaleTF.removeActionListener(this);
			tbarWidthMinimumMmTF.removeActionListener(this);
			tbarWidthLineWidthScaleTF.removeActionListener(this);
			bracketLengthScaleTF.removeActionListener(this);
			roundedBracketLengthScaleTF.removeActionListener(this);
			dotSizeMinimumMmTF.removeActionListener(this);
			dotSizeLineWidthScaleTF.removeActionListener(this);

			leftArrowCB.setSelectedKey(getAttribute(LEFT_ARROW));
			rightArrowCB.setSelectedKey(getAttribute(RIGHT_ARROW));
			globalScaleWidthTF.setValue(getAttribute(ARROW_GLOBAL_SCALE_WIDTH));
			globalScaleLengthTF.setValue(getAttribute(ARROW_GLOBAL_SCALE_LENGTH));
			headWidthMinimumMmTF.setValue(getAttribute(ARROW_WIDTH_MINIMUM_MM));
			headWidthLineWidthScaleTF.setValue(getAttribute(ARROW_WIDTH_LINEWIDTH_SCALE));
			lengthScaleTF.setValue(getAttribute(ARROW_LENGTH_SCALE));
			insetScaleTF.setValue(getAttribute(ARROW_INSET_SCALE));
			tbarWidthMinimumMmTF.setValue(getAttribute(TBAR_WIDTH_MINIMUM_MM));
			tbarWidthLineWidthScaleTF.setValue(getAttribute(TBAR_WIDTH_LINEWIDTH_SCALE));
			bracketLengthScaleTF.setValue(getAttribute(BRACKET_LENGTH_SCALE));
			roundedBracketLengthScaleTF.setValue(getAttribute(RBRACKET_LENGTH_SCALE));
			dotSizeMinimumMmTF.setValue(getAttribute(POLYDOTS_SIZE_MINIMUM_MM));
			dotSizeLineWidthScaleTF.setValue(getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE));

			leftArrowCB.addActionListener(this);
			rightArrowCB.addActionListener(this);
			globalScaleWidthTF.addActionListener(this);
			globalScaleLengthTF.addActionListener(this);
			headWidthMinimumMmTF.addActionListener(this);
			headWidthLineWidthScaleTF.addActionListener(this);
			lengthScaleTF.addActionListener(this);
			insetScaleTF.addActionListener(this);
			tbarWidthMinimumMmTF.addActionListener(this);
			tbarWidthLineWidthScaleTF.addActionListener(this);
			bracketLengthScaleTF.addActionListener(this);
			roundedBracketLengthScaleTF.addActionListener(this);
			dotSizeMinimumMmTF.addActionListener(this);
			dotSizeLineWidthScaleTF.addActionListener(this);
		}

		/**
		 * does nothing (handled by actionPerformed)
		 */
		public void store(){
		}

		/**
		 * synchronize the currently active attribute set with the widget's state.
		 */
		public void actionPerformed(ActionEvent e){
			if (e.getSource()==leftArrowCB)
				setAttribute(LEFT_ARROW,leftArrowCB.getSelectedKey());
			else if (e.getSource()==rightArrowCB)
				setAttribute(RIGHT_ARROW,rightArrowCB.getSelectedKey());
			else if (e.getSource()==globalScaleWidthTF)
				setAttribute(ARROW_GLOBAL_SCALE_WIDTH, globalScaleWidthTF.getValue());
			else if (e.getSource()==globalScaleLengthTF)
				setAttribute(ARROW_GLOBAL_SCALE_LENGTH, globalScaleLengthTF.getValue());
			else if (e.getSource()==headWidthMinimumMmTF)
				setAttribute(ARROW_WIDTH_MINIMUM_MM, headWidthMinimumMmTF.getValue());
			else if (e.getSource()==headWidthLineWidthScaleTF)
				setAttribute(ARROW_WIDTH_LINEWIDTH_SCALE, headWidthLineWidthScaleTF.getValue());
			else if (e.getSource()==lengthScaleTF)
				setAttribute(ARROW_LENGTH_SCALE, lengthScaleTF.getValue());
			else if (e.getSource()==insetScaleTF)
				setAttribute(ARROW_INSET_SCALE, insetScaleTF.getValue());
			else if (e.getSource()==tbarWidthMinimumMmTF)
				setAttribute(TBAR_WIDTH_MINIMUM_MM, tbarWidthMinimumMmTF.getValue());
			else if (e.getSource()==tbarWidthLineWidthScaleTF)
				setAttribute(TBAR_WIDTH_LINEWIDTH_SCALE, tbarWidthLineWidthScaleTF.getValue());
			else if (e.getSource()==bracketLengthScaleTF)
				setAttribute(BRACKET_LENGTH_SCALE, bracketLengthScaleTF.getValue());
			else if (e.getSource()==roundedBracketLengthScaleTF)
				setAttribute(RBRACKET_LENGTH_SCALE, roundedBracketLengthScaleTF.getValue());
			else if (e.getSource()==dotSizeMinimumMmTF)
				setAttribute(POLYDOTS_SIZE_MINIMUM_MM, dotSizeMinimumMmTF.getValue());
			else if (e.getSource()==dotSizeLineWidthScaleTF)
				setAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE, dotSizeLineWidthScaleTF.getValue());
		}

	} // class


	// -------------------- Shadow ---------------------------- //

	/**
	 * A customizer dedicated to editing shadow attributes
	 */
	public class ShadowPropertiesPanel extends AbstractCustomizer  implements ActionListener,ChangeListener {

		private IncrementableTextField shadowSizeTF, shadowAngleTF;
		private JToggleButton shadowB;
		private JColorChooser colorChooser;

		/**
		 * creates a customizer for shadow attributes editing
		 */
		public ShadowPropertiesPanel(){
			super();
			this.add(createShadowPanel(),BorderLayout.NORTH);
			// add listeners
			shadowB.addActionListener(this);
			shadowSizeTF.addActionListener(this);
			shadowAngleTF.addActionListener(this);
			colorChooser.getSelectionModel().addChangeListener(this);
		}

		/**
		 * @return the customizer title , used e.g. for Border or Tabpane title.
		 */
		public String getTitle(){
			return localize("attributes.Shadow");
		}

		/**
		 * creates a sub-panel for "shadow" properties : shadow, shadow_size, shadow_angle,shadow_color
		 */
		private JComponent createShadowPanel(){

			Box box = new Box(BoxLayout.Y_AXIS);

			JPanel p = new JPanel(new GridLayout(1,3,5,5));
			// 1) shadow (true/false) :
			shadowB = new JToggleButton(PEToolKit.createImageIcon("attributes.Shadow"));
			p.add(shadowB);

			// 2) shadow size :
			shadowSizeTF = createIncrementableTextField(0.05,"attributes.ShadowSize","mm");
			shadowSizeTF.setMinimum(0);
			shadowSizeTF.setLowBounded(true);
			p.add(shadowSizeTF);

			// 3) shadow angle :
			shadowAngleTF = createIncrementableTextField(5,"attributes.ShadowAngle","deg");
			p.add(shadowAngleTF);
			box.add(p);

			// 4) shadow color:
			box.add(colorChooser = createColorChooser());

			return box;
		}

		/**
		 * update widgets values according to currently active attribute set (ie either an appropriate
		 * input attribute, or the attribute set of a selected element).
		 */
		public void load(){

			// first remove listeners to avoid deadlocks (through recursion) when setting widgets values ;-)
			colorChooser.getSelectionModel().removeChangeListener(this);
			shadowB.removeActionListener(this);
			shadowSizeTF.removeActionListener(this);
			shadowAngleTF.removeActionListener(this);

			shadowB.setSelected(getAttribute(SHADOW));
			shadowSizeTF.setValue(getAttribute(SHADOW_SIZE));
			shadowAngleTF.setValue(getAttribute(SHADOW_ANGLE));
			colorChooser.setColor(getAttribute(SHADOW_COLOR));
			shadowSizeTF.setEnabled(shadowB.isSelected());
			shadowAngleTF.setEnabled(shadowB.isSelected());

			shadowB.addActionListener(this);
			shadowSizeTF.addActionListener(this);
			shadowAngleTF.addActionListener(this);
			colorChooser.getSelectionModel().addChangeListener(this);
		}

		/**
		 * does nothing (handled by actionPerformed)
		 */
		public void store(){ // done in actionPerformed
		}

		/**
		 * Called by the colorchooser when the user picks a new colour, and this customizer lies currently
		 * on top.
		 */
		public void stateChanged(ChangeEvent e){
			Color newColor = colorChooser.getColor();
			setAttribute(SHADOW_COLOR,newColor);
		}

		/**
		 * synchronize the currently active attribute set with the widget's state.
		 */
		public void actionPerformed(ActionEvent e){

			if (e.getSource() == shadowB) setAttribute(SHADOW, shadowB.isSelected());
			else if (e.getSource() == shadowSizeTF) setAttribute(SHADOW_SIZE, shadowSizeTF.getValue());
			else if (e.getSource() == shadowAngleTF) setAttribute(SHADOW_ANGLE, shadowAngleTF.getValue());
			shadowSizeTF.setEnabled(shadowB.isSelected());
			shadowAngleTF.setEnabled(shadowB.isSelected());
		}

	} // class




	// -------------------- Stroke ---------------------------- //

	/**
	 * A panel for editing Element's stroke attributes (width, colour, dashing, ...)
	 */
	public class StrokePropertiesPanel extends AbstractCustomizer  implements ActionListener, ChangeListener {

		private IncrementableTextField lineWidthTF, dashTF, dashOpaqueRatioTF, dotSepTF, overStrikeWidthTF;
		private PEComboBox<LineStyle> lineStyleCB;
		private EnumMap<LineStyle,ImageIcon> lineStyleToIconMap;
		private JCheckBox overStrikeCB;
		private JColorChooser colorChooser;

		/**
		 * creates a JPanel for stroke attributes editing
		 */
		public StrokePropertiesPanel(){
			super();
			this.add(createStrokePanel(),BorderLayout.NORTH);
			// add listeners
			lineStyleCB.addActionListener(this);
			lineWidthTF.addActionListener(this);
			dashTF.addActionListener(this);
			dashOpaqueRatioTF.addActionListener(this);
			dotSepTF.addActionListener(this);
			overStrikeCB.addActionListener(this);
			overStrikeWidthTF.addActionListener(this);
			colorChooser.getSelectionModel().addChangeListener(this); // fire change event when user picks a colour
		}

		/**
		 * @return the customizer title , used e.g. for Border or Tabpane title.
		 */
		public String getTitle(){
			return localize("attributes.Stroke");
		}

		/**
		 * creates sub-panel for stroke properties
		 */
		private JComponent createStrokePanel(){

			Box box = new Box(BoxLayout.Y_AXIS);
			JPanel p = new JPanel(new GridLayout(4,2,5,5));

			// 1)lineStyle
			lineStyleToIconMap = new EnumMap<LineStyle,ImageIcon>(LineStyle.class);
			lineStyleToIconMap.put(LineStyle.NONE, PEToolKit.createImageIcon("attributes.LineStyleNone"));
			lineStyleToIconMap.put(LineStyle.SOLID, PEToolKit.createImageIcon("attributes.LineStyleSolid"));
			lineStyleToIconMap.put(LineStyle.DASHED, PEToolKit.createImageIcon("attributes.LineStyleDashed"));
			lineStyleToIconMap.put(LineStyle.DOTTED, PEToolKit.createImageIcon("attributes.LineStyleDotted"));
			p.add(lineStyleCB = createComboBox(lineStyleToIconMap));

			// 2) lineWidth:
			lineWidthTF = createIncrementableTextField(0.05,"attributes.LineWidth"," mm ");
			lineWidthTF.setMinimum(0);
			lineWidthTF.setLowBounded(true);
			p.add(lineWidthTF);

			// 3) dash length :
			dashTF = createIncrementableTextField(1,"attributes.LineDash"," mm ");
			dashTF.setMinimum(0);
			dashTF.setLowBounded(true);
			p.add(dashTF);

			// 4) dash opaque ratio (100% = opaque only, 0% = transparent only)
			dashOpaqueRatioTF = createIncrementableTextField(10,"attributes.LineDashOpaque"," % ");
			dashOpaqueRatioTF.setMinimum(0);
			dashOpaqueRatioTF.setLowBounded(true);
			dashOpaqueRatioTF.setMaximum(100);
			dashOpaqueRatioTF.setHighBounded(true);
			p.add(dashOpaqueRatioTF);

			// 5) dot sep :
			dotSepTF = createIncrementableTextField(1,"attributes.LineDotSep"," mm ");
			dotSepTF.setMinimum(0);
			dotSepTF.setLowBounded(true);
			dotSepTF.setAlignmentY(CENTER_ALIGNMENT);
			p.add(dotSepTF);

			// 6) over strike :
			p.add(new JLabel());
			overStrikeCB = new JCheckBox(localize("attributes.LineOverstrike"));
			overStrikeCB.setSelected(false);
			p.add(overStrikeCB);
			overStrikeWidthTF = createIncrementableTextField(0.05,"attributes.LineOverstrikeWidth"," mm ");
			overStrikeWidthTF.setMinimum(0);
			overStrikeWidthTF.setLowBounded(true);
			overStrikeWidthTF.setAlignmentY(CENTER_ALIGNMENT);
			p.add(overStrikeWidthTF);

			box.add(p);
			box.add(colorChooser = createColorChooser());
			return box;

		}

		/**
		 * update widgets values according to currently active attribute set.
		 */
		public void load(){

			// first remove listeners to avoid deadlocks (through recursion) when setting widgets values ;-)
			lineStyleCB.removeActionListener(this);
			lineWidthTF.removeActionListener(this);
			dashTF.removeActionListener(this);
			dashOpaqueRatioTF.removeActionListener(this);
			dotSepTF.removeActionListener(this);
			overStrikeWidthTF.removeActionListener(this);
			overStrikeCB.removeActionListener(this);
			colorChooser.getSelectionModel().removeChangeListener(this);

			lineWidthTF.setValue(getAttribute(LINE_WIDTH));
			double dashOpaque = getAttribute(DASH_OPAQUE);
			double dashTransparent = getAttribute(DASH_TRANSPARENT);
			dashTF.setValue(dashOpaque + dashTransparent);
			dotSepTF.setValue(getAttribute(DOT_SEP));
			double opaqueRatio = dashOpaque/(dashTransparent+dashOpaque)*100.0;
			dashOpaqueRatioTF.setValue(opaqueRatio);
			overStrikeCB.setSelected(getAttribute(OVER_STRIKE));
			overStrikeWidthTF.setValue((getAttribute(OVER_STRIKE_WIDTH)));

			LineStyle lineStyle = (LineStyle)getAttribute(LINE_STYLE);
			lineStyleCB.setSelectedKey(lineStyle);
			switch (lineStyle){
			case NONE:
				dashTF.setEnabled(false); dashOpaqueRatioTF.setEnabled(false); dotSepTF.setEnabled(false);
				lineWidthTF.setEnabled(false);
				break;
			case SOLID:
				dashTF.setEnabled(false); dashOpaqueRatioTF.setEnabled(false); dotSepTF.setEnabled(false);
				lineWidthTF.setEnabled(true);
				break;
			case DASHED:
				dashTF.setEnabled(true); dashOpaqueRatioTF.setEnabled(true); dotSepTF.setEnabled(false);
				lineWidthTF.setEnabled(true);
				break;
			case DOTTED:
				dashTF.setEnabled(false); dashOpaqueRatioTF.setEnabled(false); dotSepTF.setEnabled(true);
				lineWidthTF.setEnabled(true);
				break;
			}

			colorChooser.setColor(getAttribute(LINE_COLOR));

			colorChooser.getSelectionModel().addChangeListener(this);
			lineStyleCB.addActionListener(this);
			lineWidthTF.addActionListener(this);
			dashTF.addActionListener(this);
			dashOpaqueRatioTF.addActionListener(this);
			dotSepTF.addActionListener(this);
			overStrikeWidthTF.addActionListener(this);
			overStrikeCB.addActionListener(this);

		}

		/**
		 * does nothing (handled by actionPerformed)
		 */
		public void store(){ // done in actionPerformed
		}

		/**
		 * Called by the colorchooser when the user picks a new colour, and this customizer lies currently
		 * on top.
		 */
		public void stateChanged(ChangeEvent e){
			Color newColor = colorChooser.getColor();
			setAttribute(LINE_COLOR,newColor);
		}

		/**
		 * synchronize the currently active attribute set with the widget's state.
		 */
		public void actionPerformed(ActionEvent e){

			if (e.getSource() == lineStyleCB){
				LineStyle style = lineStyleCB.getSelectedKey();
				setAttribute(LINE_STYLE, style);
				switch (style){
				case NONE :
					dashTF.setEnabled(false); dashOpaqueRatioTF.setEnabled(false); dotSepTF.setEnabled(false);
					lineWidthTF.setEnabled(false);
					break;
				case SOLID :
					dashTF.setEnabled(false); dashOpaqueRatioTF.setEnabled(false); dotSepTF.setEnabled(false);
					lineWidthTF.setEnabled(true);
					break;
				case DASHED :
					dashTF.setEnabled(true); dashOpaqueRatioTF.setEnabled(true); dotSepTF.setEnabled(false);
					lineWidthTF.setEnabled(true);
					break;
				case DOTTED:
					dashTF.setEnabled(false); dashOpaqueRatioTF.setEnabled(false); dotSepTF.setEnabled(true);
					lineWidthTF.setEnabled(true);
					break;
				default:
				}
			}
			else if (e.getSource() == lineWidthTF){
				setAttribute(LINE_WIDTH, lineWidthTF.getValue());
			}
			else if (e.getSource() == dashTF || e.getSource()==dashOpaqueRatioTF){
				double totalDash = dashTF.getValue();
				double opaqueRatio = dashOpaqueRatioTF.getValue()/100.0;
				setAttribute(DASH_OPAQUE, totalDash * opaqueRatio);
				setAttribute(DASH_TRANSPARENT, totalDash * (1.0-opaqueRatio));
			}
			else if (e.getSource() == dotSepTF){
				setAttribute(DOT_SEP, dotSepTF.getValue());
			}
			else if (e.getSource() == overStrikeCB){
				setAttribute(OVER_STRIKE, overStrikeCB.isSelected());
			}
			else if (e.getSource() == overStrikeWidthTF){
				setAttribute(OVER_STRIKE_WIDTH, overStrikeWidthTF.getValue());
			}

			// else color chooser
		}

	} // class



	// -------------------- PsDots Attributes --------------------------------- //
	/**
	 * A panel for editing AbstractCurve's polydots attributes
	 */
	public class PolydotsPropertiesPanel extends AbstractCustomizer implements ActionListener {

		private JCheckBox isSuperimposedDotsCB;
		private IncrementableTextField angleDNF, scaleHorDNF, scaleVertDNF, sizePrefactorDNF, sizeRelativeDNF;
		private PEComboBox<PolydotsStyle> dotStyleCombo;
		private EnumMap<PolydotsStyle,ImageIcon> polydotsStyleToIconMap;
		//private boolean isListenersAdded = false;// flag set to true after listener have been registered


		/**
		 * Initializes the GUI.
		 */
		public PolydotsPropertiesPanel() {
			super();
			this.add(createPolydotsPanel(), BorderLayout.NORTH);
			isSuperimposedDotsCB.addActionListener(this);
			dotStyleCombo.addActionListener(this);
			angleDNF.addActionListener(this);
			sizePrefactorDNF.addActionListener(this);
			sizeRelativeDNF.addActionListener(this);
			scaleHorDNF.addActionListener(this);
			scaleVertDNF.addActionListener(this);
		}

		/**
		 * @return the customizer title , used e.g. for Border or Tabpane title.
		 */
		public String getTitle(){
			return localize("attributes.PolydotsAttributes");
		}

		/**
		 * Create a sub-panel for polydots attributes
		 */
		private JPanel createPolydotsPanel(){
			JPanel p = new JPanel(new GridLayout(5,2,5,5));

			polydotsStyleToIconMap = new EnumMap<PolydotsStyle,ImageIcon>(PolydotsStyle.class);
			polydotsStyleToIconMap.put(PolydotsStyle.NONE, PEToolKit.createImageIcon("attributes.PolydotsNone"));
			polydotsStyleToIconMap.put(PolydotsStyle.POLYDOTS_CIRCLE, PEToolKit.createImageIcon("attributes.PolydotsCircle"));
			polydotsStyleToIconMap.put(PolydotsStyle.POLYDOTS_DISK, PEToolKit.createImageIcon("attributes.PolydotsDisk"));
			polydotsStyleToIconMap.put(PolydotsStyle.POLYDOTS_PLUS, PEToolKit.createImageIcon("attributes.PolydotsPlus"));
			polydotsStyleToIconMap.put(PolydotsStyle.POLYDOTS_TRIANGLE, PEToolKit.createImageIcon("attributes.PolydotsTriangle"));
			polydotsStyleToIconMap.put(PolydotsStyle.POLYDOTS_TRIANGLE_FILLED, PEToolKit.createImageIcon("attributes.PolydotsTriangleFilled"));
			polydotsStyleToIconMap.put(PolydotsStyle.POLYDOTS_SQUARE, PEToolKit.createImageIcon("attributes.PolydotsSquare"));
			polydotsStyleToIconMap.put(PolydotsStyle.POLYDOTS_SQUARE_FILLED, PEToolKit.createImageIcon("attributes.PolydotsSquareFilled"));
			polydotsStyleToIconMap.put(PolydotsStyle.POLYDOTS_PENTAGON, PEToolKit.createImageIcon("attributes.PolydotsPentagon"));
			polydotsStyleToIconMap.put(PolydotsStyle.POLYDOTS_PENTAGON_FILLED, PEToolKit.createImageIcon("attributes.PolydotsPentagonFilled"));
			p.add(dotStyleCombo = createComboBox(polydotsStyleToIconMap));

			p.add(isSuperimposedDotsCB = new JCheckBox(localize("attributes.PolydotsSuperimpose")));
			p.add(angleDNF=new IncrementableTextField(0, 5, createLabel("attributes.PolydotsAngle"), new JLabel("deg"), false));
			p.add(new JLabel(" ")); // dummy
			p.add(sizePrefactorDNF=new IncrementableTextField(1, 1, createLabel("attributes.PolydotsSizePrefactor"), null, false));
			p.add(sizeRelativeDNF=new IncrementableTextField(0, .1, createLabel("attributes.PolydotsSizeRelative"), new JLabel("mm"), false));
			p.add(scaleHorDNF=new IncrementableTextField(1, 1, createLabel("attributes.PolydotsScaleHor"), null, false));
			p.add(scaleVertDNF=new IncrementableTextField(1, 1, createLabel("attributes.PolydotsScaleVert"), null, false));
			return p;
		}

		private JLabel createLabel(String s){
			return new JLabel(localize(s));
		}


		/**
		 * load widgets with object's properties
		 */
		public void load() {
			// first remove listeners to avoid deadlocks (through recursion) when setting widgets values ;-)
			isSuperimposedDotsCB.removeActionListener(this);
			dotStyleCombo.removeActionListener(this);
			angleDNF.removeActionListener(this);
			sizePrefactorDNF.removeActionListener(this);
			sizeRelativeDNF.removeActionListener(this);
			scaleHorDNF.removeActionListener(this);
			scaleVertDNF.removeActionListener(this);

			// then update widgets
			PolydotsStyle dotStyle = (PolydotsStyle)getAttribute(POLYDOTS_STYLE);
			boolean isDotAttribute = (dotStyle != PolydotsStyle.NONE);

			isSuperimposedDotsCB.setEnabled(isDotAttribute);
			isSuperimposedDotsCB.setSelected(getAttribute(POLYDOTS_SUPERIMPOSE));

			angleDNF.setEnabled(isDotAttribute);
			angleDNF.setValue(getAttribute(POLYDOTS_ANGLE));
			sizePrefactorDNF.setEnabled(isDotAttribute);
			sizePrefactorDNF.setValue(getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE));
			sizePrefactorDNF.setBoundValues(0,1000);
			sizePrefactorDNF.setLowBounded(true);
			sizeRelativeDNF.setEnabled(isDotAttribute);
			sizeRelativeDNF.setValue(getAttribute(POLYDOTS_SIZE_MINIMUM_MM));
			scaleHorDNF.setEnabled(isDotAttribute);
			scaleHorDNF.setValue(getAttribute(POLYDOTS_SCALE_H));
			scaleHorDNF.setBoundValues(0,1000);
			scaleHorDNF.setLowBounded(true);
			scaleVertDNF.setEnabled(isDotAttribute);
			scaleVertDNF.setValue(getAttribute(POLYDOTS_SCALE_V));
			scaleVertDNF.setBoundValues(0,1000);
			scaleVertDNF.setLowBounded(true);

			dotStyleCombo.setSelectedKey(dotStyle);

			// finally re-register listeners :
			isSuperimposedDotsCB.addActionListener(this);
			dotStyleCombo.addActionListener(this);
			angleDNF.addActionListener(this);
			sizePrefactorDNF.addActionListener(this);
			sizeRelativeDNF.addActionListener(this);
			scaleHorDNF.addActionListener(this);
			scaleVertDNF.addActionListener(this);
		}

		/**
		 * does nothing (handled by actionPerformed)
		 */
		public void store() {
		}

		/**
		 * synchronize the currently active attribute set with the widget's state.
		 */
		public void actionPerformed(ActionEvent e){

			if (e.getSource()==dotStyleCombo){
				setAttribute(POLYDOTS_STYLE, dotStyleCombo.getSelectedKey());
				boolean isDotAttribute = (dotStyleCombo.getSelectedKey() != PolydotsStyle.NONE);
				isSuperimposedDotsCB.setEnabled(isDotAttribute);
				angleDNF.setEnabled(isDotAttribute);
				sizePrefactorDNF.setEnabled(isDotAttribute);
				sizeRelativeDNF.setEnabled(isDotAttribute);
				scaleHorDNF.setEnabled(isDotAttribute);
				scaleVertDNF.setEnabled(isDotAttribute);
				dotStyleCombo.setEnabled(isDotAttribute);
			}
			else if (e.getSource()==isSuperimposedDotsCB){
				setAttribute(POLYDOTS_SUPERIMPOSE, isSuperimposedDotsCB.isSelected());
			}
			else if (e.getSource()==angleDNF){
				setAttribute(POLYDOTS_ANGLE, angleDNF.getValue());
			}
			else if (e.getSource()==sizePrefactorDNF){
				setAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE, sizePrefactorDNF.getValue());
			}
			else if (e.getSource()==sizeRelativeDNF){
				setAttribute(POLYDOTS_SIZE_MINIMUM_MM, sizeRelativeDNF.getValue());
			}
			else if (e.getSource()==scaleHorDNF){
				setAttribute(POLYDOTS_SCALE_H, scaleHorDNF.getValue());
			}
			else if (e.getSource()==scaleVertDNF){
				setAttribute(POLYDOTS_SCALE_V, scaleVertDNF.getValue());
			}
		}
	}

	// -------------------- Text Attributes ----------------------------------- //

	/**
	 * A panel for editing text attributes (alignment + frame)
	 */
	public class TextPropertiesPanel extends AbstractCustomizer implements ActionListener {

		private EnumMap<VertAlign,ImageIcon> vertAlignToIconMap;
		private EnumMap<HorAlign,ImageIcon> horAlignToIconMap;
		private EnumMap<FrameStyle,ImageIcon> frameStyleToIconMap;

		/* widgets */
		private PEComboBox<VertAlign> vertAlignementCombo;
		private PEComboBox<HorAlign> horAlignementCombo;
		private PEComboBox<FrameStyle> boxTypeCombo;
		private IncrementableTextField angleTF; // text rotation

		/** GUI init */
		public TextPropertiesPanel(){

			super();
			this.add(createTextPanel(), BorderLayout.NORTH);
			horAlignementCombo.addActionListener(this);
			vertAlignementCombo.addActionListener(this);
			boxTypeCombo.addActionListener(this);
			angleTF.addActionListener(this);
		}

		/**
		 * @return le titre du dialogue de réglage à la demande, utilisé par exemple pour le titre du panneau
		 * à onglets ou de la bordure.
		 */
		public String getTitle(){
			return localize("attributes.Text");
		}

		/**
		 * Create a sub-panel for text attributes
		 */
		private JPanel createTextPanel(){
			JPanel p = new JPanel(new GridLayout(2,2,5,5));

			horAlignToIconMap = new EnumMap<HorAlign,ImageIcon>(PicText.HorAlign.class);
			horAlignToIconMap.put(HorAlign.LEFT, PEToolKit.createImageIcon("attributes.TextHalignLeft"));
			horAlignToIconMap.put(HorAlign.CENTER, PEToolKit.createImageIcon("attributes.TextHalignCenter"));
			horAlignToIconMap.put(HorAlign.RIGHT, PEToolKit.createImageIcon("attributes.TextHalignRight"));
			horAlignementCombo = PEToolKit.createComboBox(horAlignToIconMap);
			p.add(horAlignementCombo);

			vertAlignToIconMap = new EnumMap<VertAlign,ImageIcon>(PicText.VertAlign.class);
			vertAlignToIconMap.put(VertAlign.BOTTOM, PEToolKit.createImageIcon("attributes.TextValignBottom"));
			vertAlignToIconMap.put(VertAlign.BASELINE, PEToolKit.createImageIcon("attributes.TextValignBaseline"));
			vertAlignToIconMap.put(VertAlign.CENTER, PEToolKit.createImageIcon("attributes.TextValignMiddle"));
			vertAlignToIconMap.put(VertAlign.TOP, PEToolKit.createImageIcon("attributes.TextValignTop"));
			vertAlignementCombo = PEToolKit.createComboBox(vertAlignToIconMap);
			p.add(vertAlignementCombo);

			frameStyleToIconMap = new EnumMap<FrameStyle,ImageIcon>(PicText.FrameStyle.class);
			frameStyleToIconMap.put(FrameStyle.NO_FRAME, PEToolKit.createImageIcon("attributes.TextBoxNone"));
			frameStyleToIconMap.put(FrameStyle.RECTANGLE, PEToolKit.createImageIcon("attributes.TextBoxFrame"));
			frameStyleToIconMap.put(FrameStyle.CIRCLE, PEToolKit.createImageIcon("attributes.TextBoxCircle"));
			frameStyleToIconMap.put(FrameStyle.OVAL, PEToolKit.createImageIcon("attributes.TextBoxOval"));
			boxTypeCombo = PEToolKit.createComboBox(frameStyleToIconMap);
			p.add(boxTypeCombo);

			angleTF = createIncrementableTextField(5,"attributes.TextRotation","deg");
			p.add(angleTF);

			return p;
		}

		/**
		 * (re)init widgets with Element's properties
		 */
		public void load(){

			horAlignementCombo.removeActionListener(this);
			vertAlignementCombo.removeActionListener(this);
			boxTypeCombo.removeActionListener(this);
			angleTF.removeActionListener(this);

			angleTF.setValue(getAttribute(TEXT_ROTATION));

			vertAlignementCombo.setSelectedKey(getAttribute(TEXT_VERT_ALIGN));
			horAlignementCombo.setSelectedKey(getAttribute(TEXT_HOR_ALIGN));
			boxTypeCombo.setSelectedKey(getAttribute(TEXT_FRAME));

			horAlignementCombo.addActionListener(this);
			vertAlignementCombo.addActionListener(this);
			boxTypeCombo.addActionListener(this);
			angleTF.addActionListener(this);
		}

		/**
		 * does nothing (handled by actionPerformed)
		 */
		public void store(){
		}

		/**
		 * synchronize the currently active attribute set with the widget's state.
		 */
		public void actionPerformed(ActionEvent e){
			if (e.getSource()==horAlignementCombo){
				setAttribute(TEXT_HOR_ALIGN, horAlignementCombo.getSelectedKey());
			}
			else if (e.getSource()==vertAlignementCombo){
				setAttribute(TEXT_VERT_ALIGN, vertAlignementCombo.getSelectedKey());
			}
			else if (e.getSource()==boxTypeCombo){
				setAttribute(TEXT_FRAME, boxTypeCombo.getSelectedKey());
			}
			else if (e.getSource() == angleTF) setAttribute(TEXT_ROTATION, angleTF.getValue());
		}

	}

	// -------------------- Pst Custom Attributes  ---------------------------- //

	/** Un panneau pour les attributs de personnalisation spéciale à un mode d'édition.*/
	public class EditModeCustomPropertiesPanel extends AbstractCustomizer  implements ActionListener {

		private JTabbedPane tabbedPane;
		private AbstractCustomizer[] customizers = new AbstractCustomizer[2];
		public EditModeCustomPropertiesPanel(){
			super();
			add(createEditModeCustomPropertiesPanel());
		}

		JTabbedPane createEditModeCustomPropertiesPanel(){
			tabbedPane = new JTabbedPane();
			customizers[0] = new PstCustomPropertiesPanel();
			customizers[1] = new TikzCustomPropertiesPanel();
			for (int i=0; i<customizers.length; i++){
				tabbedPane.addTab(customizers[i].getTitle(),customizers[i].getIcon(),customizers[i],customizers[i].getTooltip());
			}
			return tabbedPane;
		}



		/**
		 * @return le titre du dialogue de réglage à la demande, utilisé par exemple pour le titre du panneau
		 * à onglets ou de la bordure.
		 */
		public String getTitle(){
			return localize("attributes.EditModeCustomSettings");
		}

		/**
		 * Met à jour les valeur du widget selon le jeu d'attributs courant.
		 */
		public void load(){
			for(int i=0; i < customizers.length; ++i)
				customizers[i].load();
		}

		/**
		 * Délègue aux panneaux d'onglet.
		 */
		public void store(){ // done in actionPerformed
			for(int i=0; i < customizers.length; ++i)
				customizers[i].store();
		}

		/**
		 * Synchronise l'attribute du jeu d'attribute actuellement active avec l'état du widget.
		 */
		public void actionPerformed(ActionEvent e){
		}

	}

	/**
	 * A panel for adding custom attributes (pstricks only)
	 * [todo] disable panel when not in Pstricks content-type
	 */
	public class PstCustomPropertiesPanel extends AbstractCustomizer  implements ActionListener {

		private JTextArea pstCustomTA;
		private JScrollPane pstCustomSP;
		private JButton applyButton;



		/**
		 * creates a JPanel for attributes editing
		 */
		public PstCustomPropertiesPanel(){
			super();
			this.add(createPstCustomPanel(),BorderLayout.NORTH);
			// add listeners
			applyButton.addActionListener(this);
		}

		/**
		 * @return the customizer title , used e.g. for Border or Tabpane title.
		 */
		public String getTitle(){
			return MiscUtilities.ContentTypeBasics.PSTRICKS.getContentTypeName();
		}

		/**
		 * creates sub-panel for stroke properties
		 */
		private JComponent createPstCustomPanel(){

			JPanel p = new JPanel(new BorderLayout(5,5));
			p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
			                Localizer.currentLocalizer().get("attributes.PstCustomParameters")));
			p.setToolTipText(Localizer.currentLocalizer().get("attributes.PstCustomParameters.tooltip"));
			//gbl.setConstraints(l,c);
			//p.add(l);
			pstCustomTA = new JTextArea(10,20);
			pstCustomTA.setEditable(true);
			pstCustomTA.setLineWrap(true);
			pstCustomTA.setWrapStyleWord(true);
			pstCustomTA.setToolTipText(Localizer.currentLocalizer().get("attributes.PstCustomParameters.tooltip"));
			pstCustomSP = new JScrollPane(pstCustomTA);
			pstCustomSP.setToolTipText(Localizer.currentLocalizer().get("attributes.PstCustomParameters.tooltip"));
			p.add(pstCustomSP, BorderLayout.NORTH);

			applyButton=new JButton(Localizer.currentLocalizer().get("button.OK"));
			p.add(applyButton, BorderLayout.SOUTH);
			return p;

		}

		/**
		 * update widgets values according to currently active attribute set.
		 */
		public void load(){

			applyButton.removeActionListener(this); // [SR:???]
			pstCustomTA.setText(((String)getAttribute(PST_CUSTOM)));
			applyButton.addActionListener(this);
		}

		/**
		 * does nothing (handled by actionPerformed)
		 */
		public void store(){ // done in actionPerformed
		}

		/**
		 * synchronize the currently active attribute set with the widget's state.
		 */
		public void actionPerformed(ActionEvent e){

			if (e.getSource() == applyButton)
				setAttribute(PST_CUSTOM,pstCustomTA.getText());
			// else color chooser
		}

	} // class

	/**
	 * A panel for adding custom attributes (pstricks only)
	 * [todo] disable panel when not in Pstricks content-type
	 */
	public class TikzCustomPropertiesPanel extends AbstractCustomizer  implements ActionListener {

		private JTextArea tikzCustomTA;
		private JScrollPane tikzCustomSP;
		private JButton applyButton;



		/**
		 * creates a JPanel for attributes editing
		 */
		public TikzCustomPropertiesPanel(){
			super();
			this.add(createTikzCustomPanel(),BorderLayout.NORTH);
			// add listeners
			applyButton.addActionListener(this);
		}

		/**
		 * @return the customizer title , used e.g. for Border or Tabpane title.
		 */
		public String getTitle(){
			return MiscUtilities.ContentTypeBasics.TIKZ.getContentTypeName();
		}

		/**
		 * creates sub-panel for stroke properties
		 */
		private JComponent createTikzCustomPanel(){

			JPanel p = new JPanel(new BorderLayout(5,5));
			p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
			                Localizer.currentLocalizer().get("attributes.TikzCustomParameters")));
			p.setToolTipText(Localizer.currentLocalizer().get("attributes.TikzCustomParameters.tooltip"));
			//gbl.setConstraints(l,c);
			//p.add(l);
			tikzCustomTA = new JTextArea(10,20);
			tikzCustomTA.setEditable(true);
			tikzCustomTA.setLineWrap(true);
			tikzCustomTA.setWrapStyleWord(true);
			tikzCustomTA.setToolTipText(Localizer.currentLocalizer().get("attributes.TikzCustomParameters.tooltip"));
			tikzCustomSP = new JScrollPane(tikzCustomTA);
			tikzCustomSP.setToolTipText(Localizer.currentLocalizer().get("attributes.TikzCustomParameters.tooltip"));
			p.add(tikzCustomSP, BorderLayout.NORTH);

			applyButton=new JButton(Localizer.currentLocalizer().get("button.OK"));
			p.add(applyButton, BorderLayout.SOUTH);
			return p;

		}

		/**
		 * update widgets values according to currently active attribute set.
		 */
		public void load(){

			applyButton.removeActionListener(this); // [SR:???]
			tikzCustomTA.setText(((String)getAttribute(TIKZ_CUSTOM)));
			applyButton.addActionListener(this);
		}

		/**
		 * does nothing (handled by actionPerformed)
		 */
		public void store(){ // done in actionPerformed
		}

		/**
		 * synchronize the currently active attribute set with the widget's state.
		 */
		public void actionPerformed(ActionEvent e){

			if (e.getSource() == applyButton)
				setAttribute(TIKZ_CUSTOM,tikzCustomTA.getText());
			// else color chooser
		}

	} // class


	////////////////////////////////////////////////////////////////
	//// COLORS
	////////////////////////////////////////////////////////////////
	PredefinedColorChooser predefinedColorChooser;

	/**
	 * @return the index of the given colour in array "PREDEFINED_COLORS", which holds the various colours
	 * displayed by the colour comboBoxes (either fill- or stroke- colour)
	 */
	private int getColorIndex(Color color){
		return predefinedColorChooser.getColorIndex(color);
	}

	/**
	 * @return the colour associated to the given index
	 * return null if index is out of range (it's the responsibility of the caller to handle this case)
	 */
	private Color getColor(int index){
		return predefinedColorChooser.getColor(index);
	}


	/**
	 * custom color chooser for predefined colours
	 */
	private static class PredefinedColorChooser extends AbstractColorChooserPanel implements ActionListener {

		JToggleButton[] buttons;
		NamedColor[] namedColors;
		HashMap<Color,Integer> buttonIndexMap;
		JComboBox colorSetSelector = new JComboBox();
		JPanel    buttonPanel = new JPanel();

		Color getColor(int index){
			if(index >= 0 && index < namedColors.length)
				return namedColors[index].getColor();
			else
				return null;
		}

		int getColorIndex(Color color){
			Integer index = buttonIndexMap.get(color);
			if(index == null)
				return -1; // couleur définie par l'utilisateur;
			else
				return index.intValue();
		}

		protected void childrenSetEnable(boolean enabled){
			if(buttons != null)
				for(JToggleButton button : buttons)
					button.setEnabled(enabled);
			colorSetSelector.setEnabled(enabled);
		}


		protected void setButtons(){
			childrenSetEnable(false);
			buttonPanel.removeAll();
			GridBagLayout gbl = new GridBagLayout();
			buttonPanel.setLayout(gbl);
			GridBagConstraints c = new GridBagConstraints();
			ColorEncoding colorSetId = ColorEncoding.values()[colorSetSelector.getSelectedIndex()];
			namedColors = ColorFormatter.getColors(colorSetId);
			int columnCount = (int)ceil(sqrt(2*namedColors.length));
			int rowCount = namedColors.length/columnCount;
			buttonIndexMap = new HashMap<Color,Integer>(namedColors.length);
			buttons = new JToggleButton[namedColors.length];

			BufferedImage im;
			ButtonGroup bg = new ButtonGroup();
			c.weightx=.25;
			c.weighty=.5;
			Dimension buttonSize = new Dimension(15,15);
			for (int i=0; i<namedColors.length; i++){
				im = new BufferedImage(16,16,BufferedImage.TYPE_INT_RGB);
				Graphics2D g = im.createGraphics();
				g.setPaint(Color.white);
				g.fill(new Rectangle(1,1,15,15));
				g.setStroke(new BasicStroke(2.0f));
				Color color = namedColors[i].getColor();
				g.setPaint(color);
				buttonIndexMap.put(color,i);
				g.fill(new Rectangle(3,3,11,11));
				buttons[i] = new JToggleButton(new ImageIcon(im));
				buttons[i].setPreferredSize(buttonSize);
				buttons[i].addActionListener(this);
				buttons[i].setToolTipText(namedColors[i].getName());
				bg.add(buttons[i]);
				c.gridx = 1 + (i % columnCount);
				c.gridy = i / columnCount;
				gbl.setConstraints(buttons[i],c);
				buttonPanel.add(buttons[i]);
			}

			revalidate();
			childrenSetEnable(true);
		}
		/** create the GUI */
		protected void buildChooser(){
			for(ColorFormatter.ColorEncoding e : ColorFormatter.ColorEncoding.values())
				colorSetSelector.addItem(e.toString());
			colorSetSelector.addActionListener(this);
			GridBagLayout gbl = new GridBagLayout();
			setLayout(gbl);
			GridBagConstraints c = new GridBagConstraints();
			c.ipadx = 2;
			gbl.setConstraints(colorSetSelector,c);
			add(colorSetSelector);
			gbl.setConstraints(buttonPanel,c);
			add(buttonPanel);
			setButtons();
		}
		/** called when the chooser panel is displayed */
		public void updateChooser(){
			Color color = getColorFromModel();
			ColorEncoding colorSetId = ColorEncoding.values()[colorSetSelector.getSelectedIndex()];

			int i = getColorIndex(color);
			if(i >= 0)
				buttons[i].setSelected(true);
		}
		public void actionPerformed(ActionEvent e) {
			JComponent sourceObject = (JComponent)e.getSource();
			if(sourceObject instanceof JToggleButton){
				JToggleButton source = (JToggleButton)sourceObject;
				for (int i=0; i<buttons.length; i++){
					if (source == buttons[i]){
						getColorSelectionModel().setSelectedColor(namedColors[i].getColor());
						return;
					}
				}
			}
			else if (sourceObject instanceof JComboBox){
				JComboBox source = (JComboBox)sourceObject;
				if(source == colorSetSelector)
					setButtons();
			}
		}

		/** @return the pane title */
		public String getDisplayName(){
			return localize("colour.predefined");
		}
		public Icon getSmallDisplayIcon(){
			return null;
		}
		public Icon getLargeDisplayIcon(){
			return null;
		}
	}


	//////////////////////////////////////////////////////////////
	//// GUI Utilities
	//////////////////////////////////////////////////////////////

	/** used by createComboBox */
	private static final Dimension dimButton = (new JLabel("dummy")).getPreferredSize();

	/** creates a JComboBox */
	private JComboBox createComboBox(Object[] items){
		JComboBox cb = PEToolKit.createComboBox(items);
		cb.setMaximumSize(new Dimension(dimButton.width * 3,dimButton.height));
		return cb;
	}

	/**
	 * creates a PEComboBox (aka JComboBox) from an EnumMap.
	 * @since jpicedt 1.5
	 */
	private <T> PEComboBox<T> createComboBox(Map<T,?> map){
		PEComboBox<T> cb = PEToolKit.createComboBox(map);
		cb.setMaximumSize(new Dimension(dimButton.width * 3,dimButton.height));
		return cb;
	}


	/**
	 * creates an IncrementableTextField :<br>
	 * - icon is built from  "/jpicedt/images/"+icon+".png"<br>
	 * - initial value is 0<br>
	 * - border are never drawn.<br>
	 * - other properties must be set apart.<br>
	 */
	private IncrementableTextField createIncrementableTextField(double initVal,
	                double increment, String icon, String postFix){ //, int width){
		IncrementableTextField tf = new IncrementableTextField(
		                                    initVal, // init value
		                                    increment, // increment
		                                    PEToolKit.createImageIcon(icon),
		                                    postFix, // postFix
		                                    false); // draw border
		//tf.setMaximumSize(new Dimension(dimButton.width * width,(int)(2*dimButton.height)));
		tf.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		return tf;
	}

	/**
	 * creates an IncrementableTextField :<br>
	 * - icon is built from  "/jpicedt/images/"+icon+".png"<br>
	 * - initial value is 0<br>
	 * - border are never drawn.<br>
	 * - other properties must be set apart.<br>
	 */
	private IncrementableTextField createIncrementableTextField(
	        double increment, String icon, String postFix){ //, int width){
		return createIncrementableTextField(0,increment,icon,postFix);
	}

	private JColorChooser createColorChooser(){
		JColorChooser colorChooser = new JColorChooser();
		AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
		//for (int i=0; i<panels.length; i++) colorChooser.removeChooserPanel(panels[i]);
		colorChooser.addChooserPanel(predefinedColorChooser = new PredefinedColorChooser());
		//for (int i=0; i<panels.length; i++) colorChooser.addChooserPanel(panels[i]);
		colorChooser.setPreviewPanel(new JPanel()); // remove preview panel
		return colorChooser;
		/*
		viewColorChooserCB.setAlignmentX(LEFT_ALIGNMENT);
		box.add(viewColorChooserCB);
		viewColorChooserCB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				if (viewColorChooserCB.isSelected()) box.add(colorChooser);
				else box.remove(colorChooser);
				invalidate();
				getRootPane().revalidate();
			}
	});
		*/
	}


} // class
