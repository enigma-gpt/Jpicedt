// ConvexZoneSet.java ---  -*- coding: iso-8859-1 -*-
// Copyright 2010/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneSet.java,v 1.5 2013/03/27 06:59:21 vincentb1 Exp $
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
package jpicedt.graphic.toolkit;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.event.EventListenerList;

import jpicedt.graphic.event.ConvexZoneEvent;
import jpicedt.graphic.event.ConvexZoneListener;
import jpicedt.graphic.event.PEMouseEvent;
import static jpicedt.Log.*;


import java.util.ArrayList;

public class ConvexZoneSet extends ConvexZoneGroup {

	public void paint(Graphics2D g, Rectangle2D allocation, double scale){

		if(DEBUG) debug();

		ConvexZoneViewParameters.setScale(scale);
		g.setPaint(ConvexZoneViewParameters.getLineColor());
		g.setStroke(ConvexZoneViewParameters.getSolidLineStroke());

		for(ConvexZone cz : this)
			cz.paint(g,allocation,scale);
	}

	protected EventListenerList listenerList = new EventListenerList();

	protected void fireChangedUpdate(ConvexZoneSet changed, ConvexZoneEvent.EventType type){
		Object[] listeners = listenerList.getListenerList();
		DefaultConvexZoneEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			// lazily create the event :
			if (e==null) e = new DefaultConvexZoneEvent(changed, type);
			if (listeners[i]==ConvexZoneListener.class) {
				((ConvexZoneListener)listeners[i+1]).changedUpdate(e);
			}
		}
	}

	public void fireChangedUpdate(ConvexZoneEvent.EventType type){
		if(this == null) return;
		fireChangedUpdate(this, type);
	}


	public ConvexZoneHitInfo hitTest(PEMouseEvent e, boolean selectedZones){
		ConvexZoneHitInfo hi = null;
		for(ConvexZone cz : this){
			ConvexZoneHitInfo _hi = cz.hitTest(e, selectedZones);
			if(hi != null){
				if(_hi != null)
					hi.append(_hi);
			}
			else
				hi = _hi;
		}
		return hi;
	}

	//#######################################################################
	// GESTION DES �v�NEMENTS
	//#######################################################################

	/**
	 * an implementation of ConvexZoneEvent
	 */
	public class DefaultConvexZoneEvent implements ConvexZoneEvent {

		private ConvexZoneEvent.EventType eventType;
		private ConvexZoneSet convexZoneSet;

		/**
		 * @param convexZoneSet l'ensemble de zones convexes chang�
		 * @param type le type d'�v�nement
		 */
		public DefaultConvexZoneEvent(ConvexZoneSet convexZoneSet, ConvexZoneEvent.EventType type){

			this.convexZoneSet=convexZoneSet;
			this.eventType=type;
		}

		/**
		 * @return la source de l'�v�nement
		 */
		public ConvexZoneSet getConvexZoneSet(){
			return convexZoneSet;
		}


		/**
		 * @return le type d'�v�nement
		 */
		public EventType getType(){
			return eventType;
		}

		/**
		 * @return une <code>String</code> d�crivant cet �v�nement, � des fins
		 * de d�bogage
		 */
		public String toString(){

			String s = "[ConvexZoneEvent@" + Integer.toHexString(this.hashCode())
			       + ", type=" + getType()
				   + ", source=" + getConvexZoneSet() + "]";
			return s;
		}

	} // DefaultConvexZoneEvent

}
/// ConvexZoneSet.java ends here
