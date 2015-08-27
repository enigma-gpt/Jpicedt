// SelectionTool.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: SelectionTool.java,v 1.24 2013/03/27 06:56:21 vincentb1 Exp $
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

import jpicedt.JPicEdt;
import jpicedt.graphic.*;
import jpicedt.graphic.grid.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.event.*;
import jpicedt.ui.dialog.UserConfirmationCache;

import javax.swing.JPopupMenu;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.*;

import static jpicedt.Log.*;

/**
 * This generic mouse-tool is dedicated to applying
 * <code>MouseTransform</code>'s to elements of a Drawing, following a uniform
 * sequence of mouse-events. This class works hand-in-hand with a pluggable
 * <code>MouseTransformFactory</code> which produces
 * <code>MouseTransform</code>'s on the fly from incoming mouse-events. Then
 * any ensuing mouse-event invokes a specific method from the produced
 * MouseTransform.
 * <p>
 * Basically, the sequence of mouse-events comprises:
 * <ul>
 * <li> an initial mouse-pressed event, which asks the factory to create an
 * appropriate MouseTransform
 * <li> a sequence of mouse-motion and mouse-button-changed events, where :
 * <ol>
 * <li> mouse-motion events effectively apply the transform to the element ;
 * <li> mouse-button-changed events asks the transform to jump to its next
 * available task, if any.
 * </ol>
 * </ul>
 * Besides, this tool opens a context-sensitive popup menu when a right-click
 * occurs, by delegating to the hosting EditorKit to obtain a suitable
 * <code>PopupMenuFactory</code>. It also sets the cursor shape according to
 * the currently installed <code>MouseTransform</code>.
 * <p>
 * [SR:pending] refactor class name to sth like "GenericTool" or "MouseTransformTool"&hellip;
 * @author Sylvain Reynal
 * @since jPicEdt 1.3.2
 * @version $Id: SelectionTool.java,v 1.24 2013/03/27 06:56:21 vincentb1 Exp $
 * <p>
 *
 */
public abstract class SelectionTool extends MouseTool {

	private MouseTransform currentMouseTransform;
	protected EditorKit editorKit;
	protected CursorFactory cursorFactory = new CursorFactory();
	protected MouseTransformFactory mtFactory;
	protected UserConfirmationCache ucc;

	/**
	 * @param editorKit the editorKit that owns this mouseTool
	 * @param mtFactory the factory that produces MouseTransform's in response to a mousePressed
	 */
	protected SelectionTool(EditorKit editorKit, MouseTransformFactory mtFactory){
		this.editorKit = editorKit;
		this.mtFactory = mtFactory;
	}

	/**
	 * Do tool specific painting. Merely delegates to the currently active mouse transform, as well
	 * as to the installed MouseTransformFactory.
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		if (DEBUG) debug("paint");
		mtFactory.paint(g,allocation,scale);
		if (currentMouseTransform != null) currentMouseTransform.paint(g,allocation,scale);
	}

	abstract void setCursor(PECanvas canvas);
	abstract void setMouseTool();
	abstract JPopupMenu createPopupMenu(PEMouseEvent e,PopupMenuFactory factory);


	/**
	 * Appelé lorsque cet outil est activé par l'<code>EditorKit</code> hôte&nbsp;; La fonction est en grande
	 * partie déléguée à la <code>MouseTransform</code> sousjacente.
	 */
	public void init(){
		setCursor(editorKit.getCanvas());
		ucc = new UserConfirmationCache(JPicEdt.getMDIManager());
		mtFactory.init(ucc);
	}

	/**
	 * Appelé lorsque cet outil est remplacé par un autre outil à la souris dans l'<code>EditorKit</code>
	 * hôte&nbsp;; La fonction est en grande partie déléguée à la <code>MouseTransform</code> sousjacente.
	 */
	public void flush(){
		mtFactory.flush();
	}

	/////////////////////////////////////////////////
	// MOUSE EVENTS
	/////////////////////////////////////////////////
	/**
	 *<ul>
	 * <li>clic droit souris (déclencheur de menu contextuel) &rarr; déroule le menu contextuel.</li>
	 * <li>clic gauche souris &rarr; Appelle <code>createMouseTransform</code> en utilisant la
	 *     <code>mtFactory</code> passée au constructeur.</li>
	 * </ul>
	 */
	public void mousePressed(PEMouseEvent e) {
		super.mousePressed(e);
		if (e.getClickCount() == 2) return;

		if (DEBUG) debug("MousePressed " + e);

		// mouse RIGHT button pressed : we open a JPopupMenu if applicable
		if (e.isPopupTrigger()) {
			// if we're currently not in SELECT mode (see, e.g., EDIT_POINT), force this mode before proceeding:
			setMouseTool();

			// Note : 23/03/2002 Reynal "Struggling to make the popup-menu visible !"
			// Explanation : in PECanvas.processMouseEvent, the call to super.processMouseEvent was done
			// after processing PEMouseEvent's, whereas it'd to be
			// done BEFORE, otherwise, given that JPopupMenu listens to PECanvas *standard* mouse-events
			// (I didn't know it, actually it's used to close the menu on a 2nd mouse-pressed that occurs outside
			// the menu-items, this is a standard Swing behaviour), we never see the menu (Swing closes
			// it immediately after we open it !) To sum up (with the old implementation of
			// PECanvas.processMouseEvent), we had :
			// 1) mousePressed -> process PEMouseEvent -> call popup.show in SelectionTool (i.e. here)
			// 2) super.processMouseEvent -> ok, Swing catches the event, and test if the menu is visible,
			//    if it is, it closes it, because the click occured OUTSIDE (or more exactly, on the border of)
			//    the JPopupMenu. Yeah, yeah, this SURE was tricky !
			// With the new implementation, it's just the other way around, so it just works perfect :-)
			PopupMenuFactory factory = editorKit.getPopupMenuFactory();
			if (factory != null){
				JPopupMenu menu = createPopupMenu(e,factory);
				if (menu != null){
					menu.show(e.getCanvas(), e.getAwtMouseEvent().getX(), e.getAwtMouseEvent().getY());
				}
			}
			return;
		}

		// mouse LEFT or MIDDLE button pressed (single click):

		// a) if a mouse-transform is currently active, try jumping to next task...
		if (currentMouseTransform != null){
			if (!currentMouseTransform.next(e)) currentMouseTransform = null; // finish now
			e.getCanvas().repaint(); // merely a security, since MouseTransform should've done the job anyway.
			return;
		}

		// b) else reinit a new mouse-transform...
		currentMouseTransform = mtFactory.createMouseTransform(e);
		if (currentMouseTransform != null) {// there's a selected object under the cursor (and this object can be the selection buffer itself)
			e.getCanvas().setCursor(currentMouseTransform.getCursor());
			currentMouseTransform.start(e);
			editorKit.postHelpMessage(currentMouseTransform.getHelpMessage());
		}
	}

	/**
	 * Invoke "process" on the current MouseTransform (if non-null).
	 */
	public void mouseDragged(PEMouseEvent e) {
		super.mouseDragged(e);
		if (currentMouseTransform != null) currentMouseTransform.process(e);
	}


	/**
	 * Invokes "next()" on the current MouseTransform if non-null.
	 */
	public void mouseReleased(PEMouseEvent e){
		super.mouseReleased(e);
		if (e.isPopupTrigger()) return;

		// left button :
		if (currentMouseTransform != null){
			if (!currentMouseTransform.next(e)) currentMouseTransform = null; // finish now
			e.getCanvas().repaint(); // merely a security, since MouseTransform should've done the job anyway.
			return;
		}
	}

	/**
	 * Invoke "process" on the current MouseTransform (if non-null).
	 * Otherwise change mouse cursor according to the MouseTransform returned by the factory for the
	 * given PEMouseEvent.
	 */
	public void mouseMoved(PEMouseEvent e) {
		super.mouseMoved(e);
		// if a mouse-transform is currently active, process it...
		if (currentMouseTransform != null) {
			currentMouseTransform.process(e);
			return;
		}
		// else update cursor shape...
		MouseTransform mt = mtFactory.createMouseTransform(e);
		if (DEBUG)
			debug("returned mt="+mt);
		if (mt != null){
			e.getCanvas().setCursor(mt.getCursor());
			editorKit.postHelpMessage(mt.getHelpMessage());
			// [pending] MDIManager.getCurrentManager().getStatusBar().showMessage(mt.getClickedObject().toMessageString(canvas.getSheetOrigin()));
		}
		else
			setCursor(e.getCanvas());
	}


} // class SelectionTool
