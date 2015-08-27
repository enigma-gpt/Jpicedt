// MDIComponent.java --- -*- coding: iso-8859-1 -*-
// August 18, 2006 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: MDIComponent.java,v 1.4 2013/03/27 06:49:21 vincentb1 Exp $
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

import javax.swing.RootPaneContainer;
import javax.swing.JMenuBar;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.event.KeyListener;

/**
 * A common interface for UI components being hosted by a Multiple Document Interface.
 * Concrete implementation may rely on a <code>JFrame</code> or a <code>JInternalFrame</code>.
 * The need for this interface stems from the fact that the only superclass common to
 * JFrame and JInternalFrame is java.awt.Container, which doesn't contain some very important methods
 * for our purpose, e.g. <code>dispose()</code> or <code>setDefaultCloseOperation()</code>. Yet these method are implemented in both
 * <code>JFrame</code> and <code>JInternalFrame</code>...
 */
public interface MDIComponent extends RootPaneContainer {

	/** set the visible state of the component */
        void setVisible(boolean v);    // also in Component

	/** returns whether the panel is currently visible or not */
        boolean isVisible();    // also in Component

	/** gets the bounds of the component wrapped by this DockablePanel */
        Rectangle getBounds();    // also in Component

	Dimension getPreferredSize();

	void setSize(Dimension d);

	/** makes this container unselected, unvisible or closed */
        void dispose();    // both in JInternalFrame and in JFrame

	/** set the title of this container */
        void setTitle(String title);    // both in JInternalFrame and in JFrame

	/**
	* control the window-closing operation
	* @param i see javax.swing.WindowConstants
	*/
        void setDefaultCloseOperation(int i);    // both in JInternalFrame and in JFrame

        void pack();    // both in JInternalFrame and in JFrame

        void setLocation(int x, int y);    // also in Component

	Point getLocation();

        Dimension getSize();    // also in Component

        void reshape(int x, int y, int w, int h);    // in Component

        void setResizable(boolean b);

        Component getFocusOwner();

        Component getMostRecentFocusOwner();

        JMenuBar getJMenuBar();

        void setJMenuBar(JMenuBar b);

        void toBack();

        void toFront();

        void addKeyListener(KeyListener l);

}
