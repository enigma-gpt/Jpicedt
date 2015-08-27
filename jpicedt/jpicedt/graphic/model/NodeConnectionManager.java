// NodeConnectionManager.java --- -*- coding: iso-8859-1 -*-
// August 28, 2006 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: NodeConnectionManager.java,v 1.4 2013/03/27 07:02:23 vincentb1 Exp $
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
package jpicedt.graphic.model;

import jpicedt.graphic.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.view.*;
import jpicedt.graphic.event.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import static jpicedt.Log.*;

/**
 * An Element's delegate that can manage node connections with this Element. There
 * one such manager for each Element that is node-able.
 * @since jpicedt 1.5
 * @author Reynal
 */
 public class NodeConnectionManager { // was implemented in DefaultLeafElement

	 protected Element element;

	 /** name of node for pstricks export */
	 protected String nodeName ;
	 /** a list of node-connection this element is attached to */
	 protected ArrayList<PicNodeConnection> nodeConnections; // list of PicNodeConnection's

	 public NodeConnectionManager(Element e){
		 this.element = e;
		 this.nodeConnections = new ArrayList<PicNodeConnection>();
	 }

	/**
	 * Returns an iterator over all the node connections
	 */
	public Iterator<PicNodeConnection> iterator() {
		return this.nodeConnections.iterator();
	}

	/**
	* Are we a Node ?(ie do we have connections ?)
	*/
	public boolean isNode() {
		return !this.nodeConnections.isEmpty();
	}

	/** sets node name. If argument <code>name</code> is null, create a new name.*/

	public void setNodeName(String name) {
		if (name !=null)
			nodeName=name;
		else {
			nodeName="Node"+Math.floor(Math.random()*100000);
			// [pending:SR] while (nodeDB.exist(nodeName)) etc. etc.
			//[pending:VG] si t'as un moyen simple de faire des noms de node + petits et dont on soit sur qu'ils se recoupent pas, ca serait mieux...
		}
	}

	/** gets node name. If nodeName is null, create a new name.*/

	public String getNodeName() {
		if (nodeName == null)
			setNodeName(null);
		return nodeName;
	}




	public void addConnection(PicNodeConnection edge) {
		this.nodeConnections.add(edge);
	}

	public void removeConnection(PicNodeConnection edge) {
		this.nodeConnections.remove(edge);
	}

	public void removeAllConnections() {
		this.nodeConnections.clear();
	}

	// dummy node methods
	//[pending] is it the right way ?
	public PicPoint nodeReferencePoint() {
		return null;
	}

	public double nodeReferencePointX() {
		return Double.NaN;
	}

	public double nodeReferencePointY() {
		return Double.NaN;
	}

	public PicPoint nodeConnectionOrigin(double dx,double dy) {
		return null;
	}

	/**
	 * Overriden in order to support forwarding to node connections.
	 * @since jpicedt 1.4pre5
	 */
	protected void fireChangedUpdate(DrawingEvent.EventType eventType) {
		for (PicNodeConnection edge: nodeConnections) {
			edge.forwardChangedUpdate(element,eventType);
		}
	}


 }
