// ConvexZoneSelectionEvent.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneSelectionEvent.java,v 1.5 2013/03/27 07:07:12 vincentb1 Exp $
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
package jpicedt.graphic.event;

import java.util.EventObject;
import java.util.Iterator;
import jpicedt.graphic.toolkit.ConvexZone;
import jpicedt.graphic.PECanvas;

/** Un événement produit lorsque une <code>ConvexZone</code> est sélectionnée, ou désélectionnée.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
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
	 * @return les <code>ConvexZone</code>s qui ont été séléctionnées/désélectionnées.
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
	 * Une représentation textuel de cet événement.
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
