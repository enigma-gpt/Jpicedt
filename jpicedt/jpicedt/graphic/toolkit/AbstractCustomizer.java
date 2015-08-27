// AbstractCustomizer.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: AbstractCustomizer.java,v 1.11 2013/03/27 07:00:28 vincentb1 Exp $
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
package jpicedt.graphic.toolkit;

import javax.swing.Icon;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

/**
 * An abstract superclass that alleviates the burden of creating a customizer panel,
 * by providing a common set of behaviours where storing/retrieving value
 * to/from a Properties object (or any storage class) is concerned. In particular,
 * the hosting panel should provide UI commands to load default values, reload previously stored values,
 * and store value to the approriate stream (choice of which is being left to concrete implementation
 * of related abstract methods).
 * <p>
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @see jpicedt.graphic.toolkit.CustomizerDialog
 * @version $Id: AbstractCustomizer.java,v 1.11 2013/03/27 07:00:28 vincentb1 Exp $
 */
public abstract class AbstractCustomizer extends JPanel {

	/**
	 * Construct a new preferences-panel with <code>BorderLayout</code> as the default layout.
	 */
	public AbstractCustomizer(){
		super(new BorderLayout(5,5));
		addComponentListener(new ComponentHandler());
	}


	/**
	 * Loads widgets display content with a default value, presumably from a "default preferences" file or a
	 * dedicated storage class.
	 */
	public void loadDefault(){}

	/**
	 * Loads widgets display content, presumably from a "preferences" file or a dedicated storage class.
	 */
	abstract public void load();

	/**
	 * Stores current widgets value, presumably to a file or to a dedicated storage class.
	 */
	abstract public void store();

	/**
	 * @return the panel title, used e.g. for Border or Tabpane title.<br>Default implementation returns this
	 * class name.
	 */
	public String getTitle(){
		return getClass().getName();
	}

	/**
	 * @return the <code>Icon</code> associated with this panel, used e.g. for <code>TabbedPane</code>
	 * decoration; default implementation returns <code>null</code>.
	 */
	public Icon getIcon(){
		return null;
	}

	/**
	 * @return the tooltip string associated with this panel; default implementation returns <code>null</code>.
	 */
	public String getTooltip(){
		return null;
	}

	/**
	 * Called when this component gets activated; may be used to request focus this implementation does
	 * nothing.
	 */
	protected void activated(){
	}

	class ComponentHandler implements ComponentListener {

		/** Public as an implementation side effect. */
		public void componentHidden(ComponentEvent e){}

		/** Public as an implementation side effect. */
		public void componentMoved(ComponentEvent e){}

		/** Public as an implementation side effect. */
		public void componentResized(ComponentEvent e){}

		/** Calls {@link #activated() <code>activated</code>}. */
		public void componentShown(ComponentEvent e){
			activated();
		}
	}

} // class
