// IncrementableTextField.java --- -*- coding: iso-8859-1 -*-
// Copyright (C) 1999/2006  Sylvain Reynal
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
// Version: $Id: IncrementableTextField.java,v 1.8 2013/03/27 06:49:26 vincentb1 Exp $
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

package jpicedt.widgets;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * A JComponent that lump together a JSpinner (with a +/- arrow field that allows the user to increment/decrement)
 * and prefix/postfix labels.
 * the field's value.
 * @author Sylvain Reynal
 * @since jpicedt 1.3
 * @version $Id: IncrementableTextField.java,v 1.8 2013/03/27 06:49:26 vincentb1 Exp $
 */
public class IncrementableTextField extends JPanel {

	private JSpinner sp;
	private SpinnerNumberModel model;
	private JLabel prefixLabel, postfixLabel;
	private String dialogTitle=new String();
	private String actionCommand=null;

	private Double minimum=new Double(0.0);
	private Double maximum = new Double(100.0);

	/**
	 * Creates a new IncrementableTextField with an etched border drawn around it.
	 * @param initialValue the initial value of the text field
	 * @param increment increment by which value get in-/de-cremented when a click occurs on one of the two arrows
	 * @param icon if non-null, icon gets added to the left of the component
	 * @param postFix if non-null, this string gets added just right of the testfield (it can be use to specify a unit)
	 */
	public IncrementableTextField(double initialValue, double increment, Icon icon, String postFix){
		this(initialValue, increment, icon, postFix, true);
	}

	/**
	 * @param initialValue the initial value of the text field
	 * @param increment increment by which value get in-/de-cremented when a click occurs on one of the two arrows
	 * @param icon if non-null, gets added to the left of the component
	 * @param postFix if non-null, gets added just right of the component (it can be use to specify a unit)
	 * @param drawBorder if TRUE, draw a border around the component
	 */
	public IncrementableTextField(double initialValue, double increment, Icon icon, String postFix, boolean drawBorder){
		this(initialValue, increment, (icon==null ? null : new JLabel(icon)), (postFix==null ? null : new JLabel(postFix)), drawBorder);
	}

	/**
	 * @param initialValue the initial value of the text field
	 * @param increment increment by which value get in-/de-cremented when a click occurs on one of the two arrows
	 * @param prefix if non-null, gets added to the left of the component
	 * @param postfix if non-null, gets added just right of the component (it can be use to specify a unit)
	 * @param drawBorder if TRUE, draw a border around the component
	 */
	public IncrementableTextField(double initialValue, double increment, JLabel prefix, JLabel postfix, boolean drawBorder){
		super(new FlowLayout(FlowLayout.LEFT,3,3));
		Box b = new Box(BoxLayout.X_AXIS);

		this.postfixLabel = postfix;
		this.prefixLabel = prefix;
		// prefix icon :
		if (prefix != null) {
			b.add(prefix);
			prefix.setAlignmentY(CENTER_ALIGNMENT);
		}
		b.add(Box.createHorizontalStrut(3));

		// events
		EventHandler handler = new EventHandler();

		// spinner :
		this.model = new SpinnerNumberModel(new Double(initialValue),null,null,new Double(increment)); // no bounds
		b.add(this.sp=new JSpinner(this.model));
		this.sp.addChangeListener(handler);
		if (sp.getEditor() instanceof JSpinner.DefaultEditor){
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)sp.getEditor();
			editor.getTextField().addActionListener(handler); // "ENTER" triggers actionPerformed !
			editor.getTextField().setColumns(5);
		}
		sp.setAlignmentY(CENTER_ALIGNMENT);

		// postfix :
		if(postfix != null) {
			b.add(postfix);
			postfix.setAlignmentY(CENTER_ALIGNMENT);
		}
		add(b);
		if (drawBorder) this.setBorder(BorderFactory.createEtchedBorder());
	}

	/**
	 * sets minimum and maximum bounds for "value" to the given doubles
	 */
	public void setBoundValues(double min, double max){
		this.minimum = new Double(min);
		this.maximum = new Double(max);
		// forward changes to model:
		if (this.isLowBounded()) model.setMinimum(this.minimum);
		if (this.isHighBounded()) model.setMaximum(this.maximum);
	}

	/**
	 * sets minimum bound for "value" to the given double
	 */
	public void setMinimum(double min){
		this.minimum = new Double(min);
		// forward changes to model:
		if (this.isLowBounded()) model.setMinimum(this.minimum);
	}

	/**
	 * sets maximum bound for "value" to the given double
	 */
	public void setMaximum(double max){
		this.maximum = new Double(max);
		if (this.isHighBounded()) model.setMaximum(this.maximum);
	}

	/**
	 * @return the maximum bound
	 */
	public double getMaximum(){ return maximum.doubleValue();}

	/**
	 * @return the minimum bound
	 */
	public double getMinimum(){ return minimum.doubleValue();}

	/**
	 * @param state if TRUE, DecimalNumberField's value is low-bounded
	 */
	public void setLowBounded(boolean state){
		if (state) model.setMinimum(this.minimum);
		else model.setMinimum(null);
	}

	/**
	 * @return TRUE if "value" is low-bounded
	 */
	public boolean isLowBounded(){ return model.getMinimum()!=null;}

	/**
	 * @param state if TRUE, DecimalNumberField's value is high-bounded
	 */
	public void setHighBounded(boolean state){
		if (state) model.setMaximum(maximum);
		else model.setMaximum(null);
	}

	/**
	 * @return TRUE if "value" is high-bounded
	 */
	public boolean isHighBounded(){ return model.getMaximum()!=null;}

	/**
	 * sets the title of the JDialog that opens when a NumberFormatException occurs
	 */
	public void setDialogTitle(String title){this.dialogTitle = title;}

	/**
	 * sets the actionCommand for this component to the given string
	 */
	public void setActionCommand(String s){this.actionCommand=s;}

	/**
	 * @return this component's action command
	 */
	public String getActionCommand(){return actionCommand;} // check if null ?

	/**
	 * @return the current double value contained in DecimalNumberField
	 */
	public double getValue() {
		return ((Number)sp.getValue()).doubleValue();
	}

	/**
	 * sets the DecimalNumberField double value, as well as the internal copy, to the given value, possibly modifying it so that it fits within the limits.
	 */
	public void setValue(double value){
		sp.setValue(new Double(value));
	}

	public void setValue(Number n){
		sp.setValue(n);
	}

	/**
	 * set the enable state of this component
	 */
	public void setEnabled(boolean b){
		sp.setEnabled(b);
		if (prefixLabel!=null) prefixLabel.setEnabled(b);
		if (postfixLabel!=null) postfixLabel.setEnabled(b);
	}

	/**
	 * Return the "enabled" state of this component
	 */
	public boolean isEnabled(){
		return sp.isEnabled();
	}

	/**
	 * Notify all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 */
	protected void fireActionPerformed() {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		ActionEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ActionListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new ActionEvent(this,
					                    ActionEvent.ACTION_PERFORMED,
					                    getActionCommand(),
					                    0);
				}
				((ActionListener)listeners[i+1]).actionPerformed(e);
			}
		}
	}

	/**
	 * adds an ActionListener to the component
	 */
	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	/**
	 * removes an ActionListener from the component
	 */
	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	private void checkValueInsideBounds(){
		double val = ((Number)sp.getValue()).doubleValue();
		if (isLowBounded()){
			if (val>maximum.doubleValue()) sp.setValue(maximum);
		}
		if (isHighBounded()){
			if (val<minimum.doubleValue()) sp.setValue(minimum);
		}
	}

	/////////////////////////////////////////////////////

	private class EventHandler implements ActionListener, ChangeListener {

		public void actionPerformed(ActionEvent e){
			checkValueInsideBounds();
			sp.setValue(sp.getValue());
			fireActionPerformed();
		}

		public void stateChanged(ChangeEvent e){
			checkValueInsideBounds();
			sp.setValue(sp.getValue());
			fireActionPerformed();
		}
	}


} // IncrementableTextField
