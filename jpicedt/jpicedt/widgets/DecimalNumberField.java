// DecimalNumberField.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006  Sylvain Reynal
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
// Version: $Id: DecimalNumberField.java,v 1.9 2013/03/27 06:49:36 vincentb1 Exp $
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

import javax.swing.*;
import javax.swing.text.*;

import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * A JTextField that accept only DecimalNumbers
 * @author Sylvain Reynal
 * @since jpicedt 1.3.2
 * @version $Id: DecimalNumberField.java,v 1.9 2013/03/27 06:49:36 vincentb1 Exp $
 * <p>
 *
 */
public class DecimalNumberField extends JTextField {

    private Toolkit toolkit;
    private NumberFormat doubleFormatter;
    private boolean positiveOnly=false; // >=0 numbers

    /**
     * contructs a new Field with "columns" as the initial number of columns
     */
    public DecimalNumberField(int columns){this(0, columns);}

    /**
     * contructs a new Field with an initial value and an initial number of columns, and
     * the given "positiveOnly" flag
     */
    public DecimalNumberField(double value, int columns, boolean positiveOnly){

		this(value, columns);
		this.positiveOnly = positiveOnly;
    }

    /**
     * constructs a new Field with an initial value and an initial number of columns
     */
    public DecimalNumberField(double value, int columns) {
        super(columns);
        toolkit = Toolkit.getDefaultToolkit();
        doubleFormatter = NumberFormat.getNumberInstance(Locale.US);
		doubleFormatter.setGroupingUsed(false); // no 1,000,000 !
        //doubleFormatter.setParseIntegerOnly(true);
        setValue(value);
    }

    /**
     * @return the field's text parsed as a double
     */
    public double getValue() {
        double retVal = 0;
        try {
            retVal = doubleFormatter.parse(getText()).doubleValue();
        } catch (ParseException e) {
            toolkit.beep();
        }
        return retVal;
    }

    /**
     * set the field's text from the given double value
     */
    public void setValue(double value) {
        setText(doubleFormatter.format(value));
    }

    protected Document createDefaultModel() {
        return new DecimalNumberDocument();
    }

    protected class DecimalNumberDocument extends PlainDocument {

        public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {

            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;

            for (int i = 0; i < result.length; i++) {
                if (Character.isDigit(source[i]) || source[i]=='.' || (!positiveOnly & source[i]=='-'))
                    result[j++] = source[i];
                else {
                    toolkit.beep();
                    //System.err.println("insertString: " + source[i] + " from " + str);
                }
            }
            super.insertString(offs, new String(result, 0, j), a);
        }
    }

}
