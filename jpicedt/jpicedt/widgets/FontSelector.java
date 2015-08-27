// FontSelector.java --- -*- coding: iso-8859-1 -*-
// February 11, 2002 - jPicEdt, a picture editor for LaTeX.
// copyright (C) 1999/2006 Sylvain Reynal
// Portions copyright (C) 2000/2001 Slava Pestov
// Portions copyright (C) 1999 Jason Ginchereau
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
// Version: $Id: FontSelector.java,v 1.12 2013/03/27 06:49:31 vincentb1 Exp $
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
package jpicedt.widgets;

import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import static jpicedt.Localizer.*;

/**
 * A font chooser widget (adapted from jEdit source code)
 * @author Slava Pestov (jEdit), Sylvain Reynal
 * @since jpicedt 1.3.2.beta-9
 * @version $Id: FontSelector.java,v 1.12 2013/03/27 06:49:31 vincentb1 Exp $
 */
public class FontSelector extends JButton {

	static final String PLAIN="plain";
	static final String BOLD="bold";
	static final String BOLD_ITALIC="bold-italic";
	static final String ITALIC="italic";

	/** init with a default font */
	public FontSelector(){
		this(new Font("SansSerif", Font.PLAIN, 10));
	}

	/** init with the given font */
	public FontSelector(Font font){
		setFont(font);
		setRequestFocusEnabled(false);
		addActionListener(new ActionHandler());
	}

	public void setFont(Font font){
		super.setFont(font);
		updateText();
	}

	/**
	 * update button's text content from the current button's font.
	 */
	private void updateText(){
		Font font = getFont();
		String styleString;
		switch(font.getStyle()){
		case Font.PLAIN:
			styleString = PLAIN;
			break;
		case Font.BOLD:
			styleString = BOLD;
			break;
		case Font.ITALIC:
			styleString = ITALIC;
			break;
		case Font.BOLD | Font.ITALIC:
			styleString = BOLD_ITALIC;
			break;
		default:
			styleString = "UNKNOWN!!!???";
			break;
		}

		setText(font.getFamily() + " " + font.getSize() + " " + styleString);
	}

	/**
	 * button's action-listener ; open a FontSelectorDialog
	 */
	class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			Font font = new FontSelectorDialog(FontSelector.this,getFont()).getSelectedFont();
			if(font != null){
				setFont(font);
			}
		}
	}
}


///////////////////////////////////////////////////////////////////////////////

/**
 *
 */
class FontSelectorDialog extends JDialog {

	/**
	 *
	 */
	public FontSelectorDialog(Component comp, Font font) {

		super(JOptionPane.getFrameForComponent(comp),localize("widget.FontSelector"),true); //

		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		JPanel listPanel = new JPanel(new GridLayout(1,3,6,6));

		JPanel familyPanel = createTextFieldAndListPanel(
		                         localize("widget.FontFamily"),
		                         familyField = new JTextField(),
		                         familyList = new JList(getFontList()));
		listPanel.add(familyPanel);

		String[] sizes = { "9", "10", "12", "14", "16", "18", "24" };
		JPanel sizePanel = createTextFieldAndListPanel(
		                       localize("widget.FontSize"),
		                       sizeField = new JTextField(),
		                       sizeList = new JList(sizes));
		listPanel.add(sizePanel);

		String[] styles = {FontSelector.PLAIN,FontSelector.BOLD,FontSelector.ITALIC,FontSelector.BOLD_ITALIC};

		JPanel stylePanel = createTextFieldAndListPanel(
		                        localize("widget.FontStyle"),
		                        styleField = new JTextField(),
		                        styleList = new JList(styles));
		styleField.setEditable(false);
		listPanel.add(stylePanel);

		familyList.setSelectedValue(font.getFamily(),true);
		familyField.setText(font.getFamily());
		sizeList.setSelectedValue(String.valueOf(font.getSize()),true);
		sizeField.setText(String.valueOf(font.getSize()));
		styleList.setSelectedIndex(font.getStyle());
		styleField.setText((String)styleList.getSelectedValue());

		ListHandler listHandler = new ListHandler();
		familyList.addListSelectionListener(listHandler);
		sizeList.addListSelectionListener(listHandler);
		styleList.addListSelectionListener(listHandler);

		content.add(BorderLayout.NORTH,listPanel);

		preview = new JLabel(localize("widget.FontPreview.text"));
		preview.setBorder(new TitledBorder(localize("widget.FontPreview")));

		updatePreview();

		Dimension prefSize = preview.getPreferredSize();
		prefSize.height = 50;
		preview.setPreferredSize(prefSize);

		content.add(BorderLayout.CENTER,preview);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		buttons.setBorder(new EmptyBorder(12,0,0,0));
		buttons.add(Box.createGlue());

		ok = new JButton(localize("button.OK"));
		ok.addActionListener(new ActionHandler());
		getRootPane().setDefaultButton(ok);
		buttons.add(ok);

		buttons.add(Box.createHorizontalStrut(6));

		cancel = new JButton(localize("button.Cancel"));
		cancel.addActionListener(new ActionHandler());
		buttons.add(cancel);

		buttons.add(Box.createGlue());

		content.add(BorderLayout.SOUTH,buttons);

		pack();
		setLocationRelativeTo(JOptionPane.getFrameForComponent(comp));
		setVisible(true);
	}

	public void ok(){
		isOK = true;
		dispose();
	}

	public void cancel(){
		dispose();
	}

	public Font getSelectedFont(){
		if(!isOK)
			return null;

		int size;
		try{
			size = Integer.parseInt(sizeField.getText());
		}
		catch(Exception e){
			size = 14;
		}

		return new Font(familyField.getText(),styleList.getSelectedIndex(),size);
	}

	// private members
	private boolean isOK;
	private JTextField familyField;
	private JList familyList;
	private JTextField sizeField;
	private JList sizeList;
	private JTextField styleField;
	private JList styleList;
	private JLabel preview;
	private JButton ok;
	private JButton cancel;

	/**
	 * For some reason the default Java fonts show up in the
	 * list with .bold, .bolditalic, and .italic extensions.
	 */
	private static final String[] HIDEFONTS = {".bold",".italic"};

	private String[] getFontList(){
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			String[] nameArray = ge.getAvailableFontFamilyNames();
			Vector<String> nameVector = new Vector<String>(nameArray.length);

			for(int i = 0, j; i < nameArray.length; i++){
				for(j = 0; j < HIDEFONTS.length; j++){
					if(nameArray[i].indexOf(HIDEFONTS[j]) >= 0) break;
				}

				if(j == HIDEFONTS.length) nameVector.addElement(nameArray[i]);
			}

			String[] _array = new String[nameVector.size()];
			nameVector.copyInto(_array);
			return _array;
	}

	private JPanel createTextFieldAndListPanel(String label,JTextField textField, JList list){
		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridx = cons.gridy = 0;
		cons.gridwidth = cons.gridheight = 1;
		cons.fill = GridBagConstraints.BOTH;
		cons.weightx = 1.0f;

		JLabel _label = new JLabel(label);
		layout.setConstraints(_label,cons);
		panel.add(_label);

		cons.gridy = 1;
		Component vs = Box.createVerticalStrut(6);
		layout.setConstraints(vs,cons);
		panel.add(vs);

		cons.gridy = 2;
		layout.setConstraints(textField,cons);
		panel.add(textField);

		cons.gridy = 3;
		vs = Box.createVerticalStrut(6);
		layout.setConstraints(vs,cons);
		panel.add(vs);

		cons.gridy = 4;
		cons.gridheight = GridBagConstraints.REMAINDER;
		cons.weighty = 1.0f;
		JScrollPane scroller = new JScrollPane(list);
		layout.setConstraints(scroller,cons);
		panel.add(scroller);

		return panel;
	}

	private void updatePreview(){
		String family = familyField.getText();
		int size;
		try{
			size = Integer.parseInt(sizeField.getText());
		}
		catch(Exception e){
			size = 14;
		}
		int style = styleList.getSelectedIndex();
		preview.setFont(new Font(family,style,size));
	}

	class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt){
			if(evt.getSource() == ok)ok();
			else if(evt.getSource() == cancel)cancel();
		}
	}

	class ListHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent evt)
		{
			Object source = evt.getSource();
			if(source == familyList) {
				String family = (String)familyList.getSelectedValue();
				if(family != null)
					familyField.setText(family);
			}
			else if(source == sizeList) {
				String size = (String)sizeList.getSelectedValue();
				if(size != null)
					sizeField.setText(size);
			}
			else if(source == styleList) {
				String style = (String)styleList.getSelectedValue();
				if(style != null)
					styleField.setText(style);
			}
			updatePreview();
		}
	}
}
