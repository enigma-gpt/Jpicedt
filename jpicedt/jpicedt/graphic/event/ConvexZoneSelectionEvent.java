// ConvexZoneSelectionEvent.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneSelectionEvent.java,v 1.5 2013/03/27 07:07:12 vincentb1 Exp $
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

import java.util.EventObject;
import java.util.Iterator;
import jpicedt.graphic.toolkit.ConvexZone;
import jpicedt.graphic.PECanvas;

/** Un �v�nement produit lorsque une <code>ConvexZone</code> est s�lectionn�e, ou d�s�lectionn�e.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 * @version $Id: ConvexZoneSelectionEvent.java,v 1.5 2013/03/27 07:07:12 vincentb1 Exp $
 */
public class ConvexZoneSelectionEvent extends EventObject {

	/** the event type */
	protected EventType type;
	/** the elements that were (un)selected */
	protected ConvexZone[] convexZones;

	/**
	 * A new <code>SelectionEvent</code> sourced from the given <code>PECanvas</code>
	 * Use this constructor when several elements were (un)selected.
	 * @param source the originator of the event
	 * @param convexZones the convexe zone that were (un)selected
	 * @param type the event type
	 */
	public ConvexZoneSelectionEvent(PECanvas source, ConvexZone[] convexZones, EventType type){
		super(source);
		this.type=type;
		this.convexZones=convexZones;
	}

	/**
	 * A new <code>SelectionEvent</code> sourced from the given <code>PECanvas</code>.
	 * Use this constructor when only one convexZone was (un)selected.
	 * @param source the originator of the event
	 * @param convexZone the convexZone that was (un)selected
	 * @param type the event type
	 */
	public ConvexZoneSelectionEvent(PECanvas source, ConvexZone convexZone, EventType type){
		super(source);
		this.type=type;
		convexZones = new ConvexZone[1];
		convexZones[0] = convexZone;
	}

	/**
	 * @return les <code>ConvexZone</code>s qui ont �t� s�l�ctionn�es/d�s�lectionn�es.
	 */
    public ConvexZone[] getConvexZones(){
		return convexZones;
	}

	/**
	 * @return the event type
	 */
    public EventType getType(){
		return type;
	}

	/**
	 * Une repr�sentation textuel de cet �v�nement.
	 */
	public String toString(){
			String s = "[SelectionEvent@" + Integer.toHexString(hashCode()) + ", type=" + getType();
			for (int i=0; i<convexZones.length; i++){
				s += ", (un)selected convexZone["+i+"]=" + getConvexZones()[i];
			}
			s += ", new-convex-zone-selection-buffer-content=";
			for(Iterator<ConvexZone> it = ((PECanvas)getSource()).getConvexZoneSelection(); it.hasNext();){
				s += it.next().toString() + ",";
			}
			s += "]";
			return s;
	}

	//////////////////////////////////////////////////////////////

	/**
	 * enumeration for SelectionEvent types
	 */
	public static class EventType {

		/** an convexZone was selected */
		public static final EventType SELECT = new EventType("selected");
		/** an convexZone was unselected */
		public static final EventType UNSELECT = new EventType("unselected");

		private String type;

		private EventType(String str){
			this.type = str;
		}

		public String toString(){
			return type;
		}
	}

}



/// ConvexZoneSelectionEvent.java ends here
