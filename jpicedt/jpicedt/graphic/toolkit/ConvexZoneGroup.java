// ConvexZoneGroup.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneGroup.java,v 1.6 2013/03/27 06:59:36 vincentb1 Exp $
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
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.event.PEMouseEvent;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;

import java.awt.event.ActionEvent;

/**
 * Describe class <code>ConvexZoneGroup</code> here.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela&iuml;che</a>
 * @since jPicEdt 1.6
 * @version $Id: ConvexZoneGroup.java,v 1.6 2013/03/27 06:59:36 vincentb1 Exp $
 */
public class ConvexZoneGroup extends ArrayList <ConvexZone>
	implements Collection<ConvexZone> ,ConvexZoneActionFactory{

	Rectangle2D bounds = null;
	boolean     boundsDirty = false;

	public ConvexZoneGroup(){}
	public ConvexZoneGroup(ConvexZone cz){
		super();
		super.add(cz);
		boundsDirty = true;
	}

	public ConvexZoneGroup(Collection<ConvexZone> collection){
		super(collection);
		if(!collection.isEmpty())
			boundsDirty = true;
	}

	public void append(ConvexZoneGroup czGroup){
		for(ConvexZone cz : czGroup){
			super.add(cz);
		}
		if(!czGroup.isEmpty())
			boundsDirty = true;
	}

	public boolean add(ConvexZone o){
		boundsDirty = true;
		return super.add(o);
	}

	protected Collection<ConvexZone> getCollection(){ return this; }

	void updateBoundingBox(){
		if(super.isEmpty())
			bounds = null;
		else{
			boolean init = false;
			for(ConvexZone cz : getCollection())
			{
				Rectangle2D bb = cz.getBoundingBox();
				if(init)
					bounds.add(bb);
				else
				{
					init = true;
					if(bounds == null)
					{
						bounds = (Rectangle2D)bb.clone();
					}
					else
						bounds.setRect(bb);

				}
			}
		}
		boundsDirty = false;
	}

	public Rectangle2D getBoundingBox(){
		if(boundsDirty)
			updateBoundingBox();
		return bounds;
	}

	public boolean containsPoint(PicPoint pt){
		for(ConvexZone cz : getCollection()){
			if(cz.containsPoint(pt))
				return true;
		}
		return false;
	}

	public void translate(double dx, double dy){
		if(!getCollection().isEmpty()){
			for(ConvexZone cz : getCollection())
				cz.translate(dx, dy);
			boundsDirty = true;
		}
	}

	public ArrayList<PEConvexZoneAction> createActions(ActionDispatcher actionDispatcher, ActionLocalizer localizer, ConvexZoneHitInfo hi){
		ArrayList<PEConvexZoneAction> actionArray = new ArrayList<PEConvexZoneAction>();

		actionArray.add(JPicEdt.getActiveEditorKit()
						.newDeleteConvexZoneAction(actionDispatcher,localizer));
		return actionArray;
	}

	//#######################################################################
	// extension indirècte de ArrayList<ConvexZone>
    // => peut-être à supprimer et faire que ConvexZoneGroup étendent
	// directement ArrayList<ConvexZone>.
	//#######################################################################
	//public void clear(){
	//	children.clear();
	//}
	//
	//
	//public boolean retainAll(Collection<?> collection){
	//	return children.retainAll(collection);
	//}
	//
	//public boolean removeAll(Collection<?> collection){
	//	return children.removeAll(collection);
	//}
	//
	//public boolean addAll(Collection<? extends ConvexZone> collection){
	//	return children.addAll(collection);
	//}
	//
	//public boolean containsAll(Collection<?> collection){
	//	return children.containsAll(collection);
	//}
	//
	//public boolean remove(Object o){
	//	return children.remove(o);
	//}
	//
	//public boolean add(ConvexZone o){
	//	return children.add(o);
	//}
	//
	//public Object[] toArray(){
	//	return children.toArray();
	//}
	//
	//public  <T> T[] toArray(T[] a){
	//	return children.toArray(a);
	//}
	//
	//
	//public Iterator<ConvexZone> iterator(){
	//	return children.iterator();
	//}
	//
	//public boolean contains(Object o){
	//	return children.contains(o);
	//}
	//
	//public boolean isEmpty(){
	//	return children.isEmpty();
	//}
	//
	//public int size(){
	//	return children.size();
	//}

}
/// ConvexZoneGroup.java ends here
