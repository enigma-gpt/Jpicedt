// PEEventMulticaster.java --- -*- coding: iso-8859-1 -*-
// January 3, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: PEEventMulticaster.java,v 1.9 2013/03/27 07:06:32 vincentb1 Exp $
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
package jpicedt.graphic.event;

import java.awt.AWTEventMulticaster;
import java.awt.event.*;
import java.util.*;

/**
 * An enhanced version of {@link java.awt.AWTEventMulticaster java.awt.AWTEventMulticaster}
 * that supports chaining {@link jpicedt.graphic.event.PEMouseInputListener PEMouseInputListener's}.
 * @author Sylvain Reynal
 * @version $Id: PEEventMulticaster.java,v 1.9 2013/03/27 07:06:32 vincentb1 Exp $
 * @since jpicedt 1.3.2
 */
public class PEEventMulticaster extends AWTEventMulticaster implements PEMouseInputListener {

	/**
	 * Creates a <code>PEEventMulticaster</code> instance which chains
	 * listener-<code>a</code> with listener-<code>b</code>
	 */
	protected PEEventMulticaster(EventListener a, EventListener b){
		super(a,b);
	}

    /**
     * Adds <code>PEMouseInputListener</code> &ldquo;<code>a</code>&rdquo; with
	 * <code>PEMouseInputListener</code> &ldquo;<code>b</code>&rdquo; and returns the
	 * resulting multicast listener.
     * @param a <code>PEMouseInputListener</code> &ldquo;<code>a</code>&rdquo;
     * @param b <code>PEMouseInputListener</code> &ldquo;<code>b</code>&rdquo;
     */
    public static PEMouseInputListener add(PEMouseInputListener a, PEMouseInputListener b) {
        return (PEMouseInputListener)addInternal(a, b);
    }

    /**
     * Removes the old PEMouseInputListener from PEMouseInputListener-l and
     * returns the resulting multicast listener.
     * @param l PEMouseInputListener-l
     * @param oldl the PEMouseInputListener being removed
     */
    public static PEMouseInputListener remove(PEMouseInputListener l, PEMouseInputListener oldl) {
		return (PEMouseInputListener) removeInternal(l, oldl);
    }

    /**
     * Removes a listener from this multicaster and returns the
     * resulting multicast listener.
     * @param oldl the listener to be removed
     */
    protected EventListener remove(EventListener oldl) {
		if (oldl == a)  return b;
		if (oldl == b)  return a;
		EventListener a2 = removeInternal(a, oldl);
		EventListener b2 = removeInternal(b, oldl);
		if (a2 == a && b2 == b) {
			return this;	// it's not here
		}
		return addInternal(a2, b2);
    }


    /**
     * Returns the multicast listener resulting from the addition of listener-a
     * and listener-b.<br>
     * If listener-a is null, it returns listener-b;
     * If listener-b is null, it returns listener-a
     * If neither are null, then it creates and returns
     * a new PEEventMulticaster instance which chains a with b.
     * @param a event listener-a
     * @param b event listener-b
     */
    protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)  return b;
		if (b == null)  return a;
		return new PEEventMulticaster(a, b);
    }

    /**
     * Returns the multicast listener obtained after removing the
     * old listener from listener-l.<br>
     * If listener-l equals the old listener OR listener-l is null,
     * returns null.<br>
     * Else if listener-l is an instance of AWTEventMulticaster,
     * then removes the old listener from it.<br>
     * Else, returns listener l.
     * @param l the listener being removed from
     * @param oldl the listener being removed
     */
    protected static EventListener removeInternal(EventListener l, EventListener oldl) {
		if (l == oldl || l == null) {
		    return null;
		} else if (l instanceof PEEventMulticaster) {
	    	return ((PEEventMulticaster)l).remove(oldl);
		} else {
	    	return l;		// it's not here
		}
    }


    /**
     * Handles a mouseClicked event by invoking the
     * mouseClicked methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseClicked(PEMouseEvent e) {
        ((PEMouseInputListener)a).mouseClicked(e);
        ((PEMouseInputListener)b).mouseClicked(e);
    }

    /**
     * Handles the mousePressed event by invoking the
     * mousePressed methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mousePressed(PEMouseEvent e) {
        ((PEMouseInputListener)a).mousePressed(e);
        ((PEMouseInputListener)b).mousePressed(e);
    }

    /**
     * Handles the mouseReleased event by invoking the
     * mouseReleased methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseReleased(PEMouseEvent e) {
        ((PEMouseInputListener)a).mouseReleased(e);
        ((PEMouseInputListener)b).mouseReleased(e);
    }

    /**
     * Handles the mouseEntered event by invoking the
     * mouseEntered methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseEntered(PEMouseEvent e) {
        ((PEMouseInputListener)a).mouseEntered(e);
        ((PEMouseInputListener)b).mouseEntered(e);
    }

    /**
     * Handles the mouseExited event by invoking the
     * mouseExited methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseExited(PEMouseEvent e) {
        ((PEMouseInputListener)a).mouseExited(e);
        ((PEMouseInputListener)b).mouseExited(e);
    }

    /**
     * Handles the mouseDragged event by invoking the
     * mouseDragged methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseDragged(PEMouseEvent e) {
        ((PEMouseInputListener)a).mouseDragged(e);
        ((PEMouseInputListener)b).mouseDragged(e);
    }

    /**
     * Handles the mouseMoved event by invoking the
     * mouseMoved methods on listener-a and listener-b.
     * @param e the mouse event
     */
    public void mouseMoved(PEMouseEvent e) {
        ((PEMouseInputListener)a).mouseMoved(e);
        ((PEMouseInputListener)b).mouseMoved(e);
    }

	public String toString(){
		return super.toString() + "[a=" + a + ", b="+ b+"]";
	}
}
