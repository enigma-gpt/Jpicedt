// MouseTool.java --- -*- coding: iso-8859-1 -*-
// January 4, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: MouseTool.java,v 1.18 2013/03/27 06:57:26 vincentb1 Exp $
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

import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.PEMouseInputListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import static jpicedt.Log.*;

/**
 * An implementation of PEMouseInputListener that factors common behaviour for
 * EditorKit's mouse tools, and implements KeyListener so that mouse-tools can
 * also listen to key events (e.g. to update cursor,&hellip;)
 * <p>
 * This implementation offers a convenient mechanism for generating MouseMoved
 * events as a result of a KeyPressed/KeyReleased event. If this behaviour is
 * to be preserved, subclassers should either call
 * <code>super.mouse<var>XXX</var>()</code> before all in their own implementation of the
 * corresponding methods, or alternately simply add the following line of
 * code: <code>lastMouseEvent=e</code>, where <code>e</code> denotes the
 * mouse-event parameter.
 * <p>
 * Besides, this class provide a "paint" method which, like Element's, allows
 * the tool to draw shapes (on the associated PECanvas) that are specific to
 * this tool.
 * @since jPicEdt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: MouseTool.java,v 1.18 2013/03/27 06:57:26 vincentb1 Exp $
 *
 */
public abstract class MouseTool extends KeyAdapter implements PEMouseInputListener {

	public enum MouseToolType{
		DRAWING_MOUSE_TOOL(0),
	    CONVEXZONE_MOUSE_TOOL(1),
		MOUSE_TOOL_COUNT(2);
		int value;
		MouseToolType(int value){ this.value = value; }
		int toInteger(){ return value; }
	}


	/**
	 * La méthode <code>getMouseToolType</code> renvoie soit
	 * <code>DRAWING_MOUSE_TOOL</code> pour les outils de dessin, soit
	 * <code>CONVEXZONE_MOUSE_TOOL</code> pour les outils de manipulation de
	 * zones convexes.
	 *
	 * @return une valeur énumérée <code>MouseToolType</code> identifiant de type d'outil souris.
	 * @since jPicEdt 1.6
	 */
	MouseToolType getMouseToolType(){ return MouseToolType.DRAWING_MOUSE_TOOL; }

	/**
	 * this flag is set to true/false whenever a mouseExited/Entered event is caught.
	 * <br>For instance, it may be used by subclasses to trigger an autoscrolling operation.
	 */
	protected boolean mouseOutside;

	/** this field remembers the last mouse-event produced by a call to one of the mouse<var>XXX</var> methods
	 * shared by every <code>MouseTool</code>
	 */
	protected static PEMouseEvent lastMouseEvent;

	// used by KeyListener to remember up- and down- keys modifiers
	private static int keyModifiers=0;

	/** this default implementation simply stores the given mouse-event in <code>lastMouseEvent</code> field
	 */
	public void mousePressed(PEMouseEvent e){
		if (DEBUG) debug("e="+e);
		lastMouseEvent = e; // store for later use by processKeyEvent
	}

	/** this default implementation simply stores the given mouse-event in <code>lastMouseEvent</code> field
	 */
	public void mouseReleased(PEMouseEvent e){
		if (DEBUG) debug("e="+e);
		lastMouseEvent = e; // store for later use by processKeyEvent
	}

	/** this default implementation simply stores the given mouse-event in <code>lastMouseEvent</code> field
	 */
	public void mouseClicked(PEMouseEvent e){
		if (DEBUG) debug("e="+e);
		lastMouseEvent = e; // store for later use by processKeyEvent
	}

	/** this default implementation simply stores the given mouse-event in <code>lastMouseEvent</code> field
	 */
	public void mouseMoved(PEMouseEvent e){
		if (DEBUG) debug("e="+e);
		lastMouseEvent = e; // store for later use by processKeyEvent
	}

	/** this default implementation simply stores the given mouse-event in <code>lastMouseEvent</code> field
	 */
	public void mouseDragged(PEMouseEvent e){
		if (DEBUG) debug("e="+e);
		lastMouseEvent = e; // store for later use by processKeyEvent
	}

	/**
	 * Returns the last mouse-event as produced by the last invokation of a mouse<var>XXX</var> method
	 */
	public PEMouseEvent getLastMouseEvent(){
		return lastMouseEvent;
	}

	/**
	 * Sets the mouseOutside flag to true, stores the given mouse-event in <code>lastMouseEvent</code> field,
	 * and request focus on the PECanvas that dispatched this mouse event.
	 */
	public void mouseEntered(PEMouseEvent e){
		if (DEBUG) debug("e="+e);
		mouseOutside = true;
		lastMouseEvent = e; // store for later use by processKeyEvent
		e.getCanvas().requestFocus();
	}

	/**
	 * Sets the mouseOutside flag to false, and stores the given mouse-event in <code>lastMouseEvent</code>
	 * field.
	 */
	public void mouseExited(PEMouseEvent e){
		if (DEBUG) debug("e="+e);
		lastMouseEvent = e; // store for later use by processKeyEvent
		mouseOutside = false;
	}

	/**
	 * Allow the tool to paint shapes that are specific to this tool. This method
	 * is called by the hosting EditorKit.
	 * <br>
	 * For instance, this method may paint selection areas, markers, &hellip;
	 * Current implementation does nothing.
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
	}

	/** called when this tool is being activated in the hosting
	 * editor kit ; this is mainly for mousetools that need to do some initialization work
	 * before any mouse-event occurs.
	 */
	public void init(){
	}

	/** called when this tool is being replaced by another mouse-tool in the hosting
	 * editor kit ; this is mainly for mousetools using more than one sequence, for it
	 * gives them a chance to clean themselves up for the next time */
	public void flush(){
	}

	/**
	 * Triggers a fake "mouseMoved" event when a key is pressed.
	 * Current implementation merely calls {@link #processKeyEvent}.
	 */
	public void keyPressed(KeyEvent ke){
		processKeyEvent(ke,true); // pressed
	}

	/**
	 * Triggers a fake "mouseMoved" event when a key is released.
	 * Current implementation merely calls {@link #processKeyEvent}.
	 */
	public void keyReleased(KeyEvent ke){
		processKeyEvent(ke, false); // released
	}

	/**
	 * Triggers a fake "mouseMoved" event if a key is pressed or released, with the adequate key modifiers ;
	 * this is a "lazy" handling of key-events
	 * which delegates to the mouse-handler. The goal is e.g. to allow the receiver to update
	 * things as cursor's shape/help-message/&hellip; when a key is pressed.<br>
	 * @param pressed if TRUE, process a keyPressed event, else process a keyReleased
	 */
	protected void processKeyEvent(KeyEvent ke, boolean pressed){
		// if this is called before the mouse ever moved, default behaviour :
		if (lastMouseEvent==null) {
			keyModifiers=0;
			//((PECanvas)ke.getSource()).setCursor(cursorFactory.getPECursor(CursorFactory.SELECT));
			return;
		}
		else {
			// update this.keyModifiers from keycode
			int keycode = ke.getKeyCode();
			int mask = 0;
			switch (keycode){
				case KeyEvent.VK_SHIFT :
					mask = InputEvent.SHIFT_DOWN_MASK; // [SR:pending] as of JDK1.4, use extended modifiers instead ? (look up Java tutorial)
					break;
				case KeyEvent.VK_CONTROL :
					mask = InputEvent.CTRL_DOWN_MASK;
					break;
				case KeyEvent.VK_ALT :
					mask = InputEvent.ALT_DOWN_MASK;
					break;
				default :
					return;
			}
			if (pressed) this.keyModifiers = this.keyModifiers | mask;
			else this.keyModifiers = this.keyModifiers & (~mask);
			if (DEBUG)
				debug("keyModifiers="+keyModifiers);

			// create a new mouse-event including keyModifiers
			MouseEvent oldMe = lastMouseEvent.getAwtMouseEvent();
			MouseEvent newMe = new MouseEvent(oldMe.getComponent(),oldMe.getID(),oldMe.getWhen(),
				keyModifiers,oldMe.getX(),oldMe.getY(),oldMe.getClickCount(),oldMe.isPopupTrigger());
			PEMouseEvent withModifiersEvent = new PEMouseEvent(newMe, lastMouseEvent.getCanvas(), lastMouseEvent.getPicPoint());
			mouseMoved(withModifiersEvent); // lazily delegates to mouseMoved !
		}
	}

}
