// PicNodeConnection.java --- -*- coding: iso-8859-1 -*-
// April 09, 2005 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PicNodeConnection.java,v 1.16 2013/03/27 07:01:34 vincentb1 Exp $
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

import java.awt.*;
import java.awt.geom.*;

import java.util.*;
import jpicedt.graphic.*;
import jpicedt.graphic.event.*;
import jpicedt.graphic.view.*;
import jpicedt.ui.dialog.UserConfirmationCache;

import static jpicedt.Log.*;

/**
 * This class represents a connection between two <code>DefaultLeafElement</code>'s. It directly inherits from
 * <code>AbstractElement</code> because <code>BranchElement</code> (after thinking it over carefully) is not
 * the most appropriate choice in many respects:
 * <ul>
 * <li> first, the connection is a sort of branch-element, yet its geometry is directly implemented here (as a
 *   GeneralPath) rather than in a separate class, so this is quite different from BranchElement, where the
 *   whole geometry is delegated to children.
 * <li> second, the relationship b/w this class and the two <code>DefaultLeafElement</code>'s is definitely
 *   not a parent/child one, seeing that a node may be linked to more than one connection (which is forbidden
 *   in a parent/child relation as in BranchElement)
 * <li> third, (child-)nodes need to hold a reference to the many (parent) node-connections,
 * </ul>
 * so that the connection geometry can keep up with any change made to the geometry of the children.  This is
 * the idea behind the improvement of the <code>fireChangedUpdate()</code> method in
 * <code>DefaultLeafElement</code> (april'05) whereby, in addition to standard child-parent event dispatching,
 * change events are also dispatched to every appropriate connection, if any, irrespective of the nature of
 * the (real) parent (e.g., <code>Drawing.RootElement</code> or <code>PicGroup</code>). In particular, this
 * means that one DefaultLeafElement may belong to a given group A, while the other DefaultLeafElement may
 * belong to another group B, and at the same time both DefaultLeafElement's may share the same connection
 * (now, this also implies that a <code>PicNodeConnection</code> may have "dangling" bonds if we cut/delete
 * one of the groups [pending] the view might get confused ?).
 */
public class PicNodeConnection extends AbstractElement {

	private NodeableElement nodeA, nodeB; // the two vertices

	// note: PicAttributeSet will be added new fields for "nodesep", "offset", "linearc",... and
	// these will be obviously made editable from a specific tabpane in DockableAttributeToolbar

	private String edgeType; // e.g., ncline, nccurve,...

	// for instance:
	public static final String EDGE_NCLINE = "ncline";
	public static final String EDGE_NCCURVE = "nccurve";
	// ... add other ...

	private Shape edge; // holds the geometry of the connection, e.g., straight line, Bezier curve,...
	// I expect GeneralPath to be a wise choice in this respect.
	// Edge's parameters are stored in the attribute set associated with this element, including stroke, fill
	// and arrow parameters; hence there's no need for a distinct attribute set.

	private PicPoint controlPointA,controlPointB; // i expect 1 control point _per node_ to be enough to control the edge geometry, i.e.,
			// moving this control point may alter angleA/angleB/armA/armB/... depending on the current edgeType.
			// other parameters should then be computed automagically to reduce the burden from the user side

                        //VG: pas d'accord. Par ex, si c'est une courbe de Bezier, on veut gerer ca avec 2 points de controle:
                        // un de chaque cote !

	/** index of control point A */
	public static final int CONTROL_A = 0;
	/** index of control point B */
	public static final int CONTROL_B = 1;

    //VG: Point de vue interface utilisateur, gestion des transformations (translation d'un sommet d'une nccurve par ex).
    // Je vois 2 possibilites. La premiere, + proche du reste de Jpicedt, consiste a translater le sommet et son point de controle.
    // 2eme possibilite, + proche de pstricks, et peut-etre plus intelligente (quoique?) mais plus chiante a implementer qui
    // consiste a garder constant les parametres nccurveA et angleA
    // qui correspondent au point. Effet: notons C le point de controle, A le noeud correspondant, B l'autre noeud, C' son point de controle.
    // Le facteur NCcurveA definit la longueur AC  par la formule AC= NCcurve * AB . Donc si on rapproche A de B, ceci aura pour effet de raccourcir
    // AC (et aussi BC'). Plus on rapproche A de B, plus AC et BC' vont rappetisser....
    // Tu preferes quoi ??


	/** a semaphor that signals a change of state is underway in this PicNodeConnection and
	 * it shouldn't process events coming from its node before the change is completed
	 * (hence this is used inside {@link #forwardChangedUpdate forwardChangedUpdate()});
	 * set it to true each time you start modifying nodes AND edge <b>simulatneously</b> and you don't want
	 * events to be forwarded to the root of the hierarchy before everything is completed (e.g. to
	 * get rid of side-effects, or to reduce the burden for the repaint manager).<p>
	 * Example of use : whenever we call <code>translate()</code> on this PicNodeConnection
	 * <ul>
	 * <li> this first raises the changeLock semaphor,
	 * <li> then calls translate() on every node:
	 * this in turn make nodes forward a DrawingEvent to their connection (= this PicNodeConnection), yet this event is trapped here,
	 * hence not propagated upward ; note however that nodes update their own view whatever the value of the changeLock semaphor ;
	 * <li> then lower the changeLock semaphor, and fire a change-update event (which in turn will propagate the event upward
	 *      along the tree).
	 * <li> Notice that, as opposed to BranchElement (and as a consequence of each node having possibly more
	 * than one connection), translating (or rotating or whatsoever) this edge and its two nodes as a single block
	 * also results in OTHER edges being affected... There, however, the changeLock semaphor will be down, and
	 * these PicNodeConnection's will update their state properly.
	 * </ul>
	 */
	protected boolean changeLock;

	///////////////////////////////////// constructors ///////////////////////////////////

	public PicNodeConnection(NodeableElement nodeA, NodeableElement nodeB, String edgeType, PicAttributeSet set){
		super(set);
		System.out.println("############# New picnode connection :\n");
		this.edgeType=edgeType;
		//		this.edgeType=EDGE_NCLINE;//debug
		this.nodeA=nodeA;//dont call setnodeA because it calls update edge, etc...
		this.nodeB=nodeB;

		this.nodeA.getNodeConnectionManager().addConnection(this);//VG: a faire ici ??
		this.nodeB.getNodeConnectionManager().addConnection(this);
		updateEdge();
		changeLock=false;
	}

	// [pending] shallow clone!!!
	public PicNodeConnection(PicNodeConnection src){
		super(src);
		this.nodeA = src.nodeA;
		this.nodeB = src.nodeB;
		this.edgeType = edgeType;
		this.edge = new GeneralPath(src.edge);
		this.changeLock=src.changeLock;//VG: ??
	}

	public PicNodeConnection clone(){
		return new PicNodeConnection(this);
	}

	public String getDefaultName(){
		return "Node Connection"; // [pending] i18n
	}

	/**
	 * update the edge geometry given the current set of attributes and the nodes
	 */
	private void updateEdge(){
		// [SR:experimental] for very minimal testing purpose only (just a straight line joining the two text's anchor points)
	    if (nodeA==null || nodeB==null) {
		debug("############# update edge failed: nodeA="+nodeA+" and nodeB="+nodeB);
		return;
	    }
	    System.out.println("####### UPDATE EDGE #################");
	    System.out.println("type="+edgeType+"EDGE_NCLINE="+EDGE_NCLINE+"egal="+(edgeType==EDGE_NCLINE));
	    //System.out.println("############### this = \n"+this.toString());


	    if (this.edgeType==EDGE_NCLINE){
		System.out.println("line");
		double dx=nodeB.getNodeConnectionManager().nodeReferencePointX()-nodeA.getNodeConnectionManager().nodeReferencePointX();
		double dy=nodeB.getNodeConnectionManager().nodeReferencePointY()-nodeA.getNodeConnectionManager().nodeReferencePointY();
		GeneralPath path = new GeneralPath();
		PicPoint pt0 = nodeA.getNodeConnectionManager().nodeConnectionOrigin(dx,dy);
		path.moveTo((float)pt0.x,(float)pt0.y);
		PicPoint pt1 = nodeB.getNodeConnectionManager().nodeConnectionOrigin(-dx,-dy);
		path.lineTo((float)pt1.x,(float)pt1.y);
		System.out.println( "path:"+path);
		this.edge = path;
		//debug("edge="+edge);
		//		this.edgeControlPoint.setCoordinates(pt1.middle(pt0)); // this is totally useless, just to give it a go, like.

		// ... update this.edge (this is probably the most complicated part of the implementation)
		// we'll need:
		// - this.edgeType (nccurve, ncline, ...)
		// - maybe (not sure) this.attributeSet (arrows, stroke style and color,...), although this might be
		//   of relevance to the view rather than to this element.
		// and the result is a freshly updated GeneralPath (if indeed this.edge is implemented this way)
	    }
	    else if (this.edgeType==EDGE_NCCURVE){
		double xA=nodeA.getNodeConnectionManager().nodeReferencePointX();
		double xB=nodeB.getNodeConnectionManager().nodeReferencePointX();
		double yA=nodeA.getNodeConnectionManager().nodeReferencePointY();
		double yB=nodeB.getNodeConnectionManager().nodeReferencePointY();

		if (this.controlPointA==null || this.controlPointB==null) {
		    this.controlPointA = new PicPoint();
		    this.controlPointB = new PicPoint();
		    controlPointA.x=(2*xA+xB)/3; controlPointA.y=(2*yA+yB)/3;
		    controlPointB.x=(2*xB+xA)/3; controlPointB.y=(2*yB+yA)/3;
		}
		double dxA=controlPointA.x-xA; double dyA=controlPointA.y-yA;
		double dxB=controlPointB.x-xB; double dyB=controlPointB.y-yB;

		GeneralPath path = new GeneralPath();
		PicPoint pt0 = nodeA.getNodeConnectionManager().nodeConnectionOrigin(dxA,dyA);
		PicPoint pt1 = nodeB.getNodeConnectionManager().nodeConnectionOrigin(dxB,dyB);
		path.moveTo((float)pt0.x,(float)pt0.y);
		path.curveTo((float)controlPointA.x,(float)controlPointA.y,
			     (float)controlPointB.x,(float)controlPointB.y,
			     (float)pt1.x,(float)pt1.y);
		this.edge = path;
	    }
	    else {
		System.out.println("###################### TYPE INCONNU #################");
		edge =new GeneralPath();
	    }
	    System.out.println(edge);
	}

	//////////////////////////////////// View helpers ///////////////

	/**
	 * Returns a Shape that holds the geometry of the connection. May be used, for instance, by the associated view.
	 */
	public Shape createShape(){
		return this.edge;
	}

	/**
	 * Returns the coordinates of the edge's end-point at node A
	 */
	public PicPoint getEndPointA(PicPoint pt){
		if (pt==null) pt = new PicPoint();
		//[pending] pt.setCoordinates(?,?); // depends on edge geometry and nodes location
		return pt;
	}

	/**
	 * Returns the coordinates of the edge's end-point at node B
	 */
	public PicPoint getEndPointB(PicPoint pt){
		if (pt==null) pt = new PicPoint();
		//[pending] pt.setCoordinates(?,?); // depends on edge geometry and nodes location
		return pt;
	}

	/**
	 * Return the tangent to the node connection at node A. Useful for instance to compute the
	 * direction of the first arrow.
	 * @return a unit-length vector
	 */
	 public PicVector getTangentA(PicVector v){
		 if (v==null) v = new PicVector();
		 //[pending] v.setCoordinates(?,?); // depends on edge geometry and nodes location
		 v.normalize();
		 return v;
	 }

	/**
	 * Return the tangent to the node connection at node B. Useful for instance to compute the
	 * direction of the second arrow.
	 * @return a unit-length vector
	 */
	 public PicVector getTangentB(PicVector v){
		 if (v==null) v = new PicVector();
		 //[pending] v.setCoordinates(?,?); // depends on edge geometry and nodes location
		 v.normalize();
		 return v;
	 }

	/**
	 * Helper for the associated View. This implementation updates the geometry of
	 * the given ArrowView only if isArc()==true.
	 */
	public void syncArrowGeometry(ArrowView v, ArrowView.Direction d){
		PicPoint loc;
		PicVector dir;
		switch (d){
		case LEFT:
			loc=getEndPointA(null);
			dir=getTangentA(null);
			v.updateShape(loc,dir);
			break;
		case RIGHT:
			loc=getEndPointB(null);
			dir=getTangentB(null);
			v.updateShape(loc,dir);
			break;
		}
	}


	 //////////////// convenient wrappers for type enforcement //////////

	/**
	 * Returns the first node.
	 * @return null if nodeA not set yet
	 */
	public NodeableElement getNodeA(){
		return nodeA;
	}

	/**
	 * Returns the second node.
	 * @return null if mgrB not set yet
	 */
	public NodeableElement getNodeB(){
		return nodeB;
	}

	//////////////// convenient wrappers for easy-naming //////////

	/**
	 * Returns the index (0 or 1) of the given node, or -1 if the given node is not connected to this edge.
	 */
	public int getNodeIndex(NodeableElement o){
		if (o == nodeA) return 0;
		else if (o==nodeB) return 1;
		else return -1; // dirty !
	}

	/**
	 * Sets the first node.
	 */
	public void setNodeA(NodeableElement o){
		if (nodeA!=null)
			nodeA.getNodeConnectionManager().removeConnection(this);
		nodeA=o;
		o.getNodeConnectionManager().addConnection(this);
		updateEdge();
		fireChangedUpdate(DrawingEvent.EventType.REPLACE);
	}

	/**
	 * Sets the second node.
	 */
	public void setNodeB(NodeableElement o){
		if (nodeB!=null)
			nodeB.getNodeConnectionManager().removeConnection(this);
		nodeB=o;
		o.getNodeConnectionManager().addConnection(this);
		updateEdge();
		fireChangedUpdate(DrawingEvent.EventType.REPLACE);
	}

	//////////////// methods from AbstractElement that need implementing and/or overriding ///////////////////
	// Note that javadoc simply copy the code documentation from the superclass, so i didn't repeat it here
	// except where explicitely needed.

	public PicPoint getCtrlPt(int index, PicPoint src){
		if (src==null)
			src = new PicPoint();
		switch (index){
		case CONTROL_A:
			src.setCoordinates(controlPointA);
			return src;
		case CONTROL_B:
			src.setCoordinates(controlPointB);
			return src;
		default:
			throw new IndexOutOfBoundsException(new Integer(index).toString());
		}
	}

	 public int getFirstPointIndex(){
		 return 0;
	 }

	 public int getLastPointIndex(){
	     if (edgeType==EDGE_NCCURVE)
		 return 1;
	     else return -1;// no point !
	 }

	 public void setCtrlPt(int index, PicPoint pt){
		 setCtrlPt(index,pt,null);
	 }

	 public void setCtrlPt(int index, PicPoint pt, EditPointConstraint constraint){
	     if (index==0)
		 controlPointA.setCoordinates(pt);
	     else if (index==1)
		 controlPointB.setCoordinates(pt);
	     updateEdge(); // [pending] constraint not used so far... any idea of possible use?
	     fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE); // update view + inform parent of change
	 }

	/**
	 * Returns the bounding box (i.e. the surrounding rectangle) in double precision
	 * Used e.g. to determine the arguments of the \\begin{picture} command.<p>
	 */
	public Rectangle2D getBoundingBox(Rectangle2D r){
	    System.out.println("################## getBB  r : \n"+r);
	    //System.out.println(this);
	    System.out.println("EDGE:"+edge);
		if (r==null) r = new Rectangle2D.Double();
		r.setRect(this.edge.getBounds2D());
		return r;
	}

	/**
	 * Called by a child-node (=a DefaultLeafElement) to inform this connection of some change that occured to
	 * one of its nodes. See DefaultLeafElement.fireChangedUpdate() for more details.
	 * This gives a chance to the receiver to update its layout, then to propagate the change-event upward.<p>
	 * @param eventType the event type
	 * @param node the node that sent the change-event.
	 */
	public void forwardChangedUpdate(Element node, DrawingEvent.EventType eventType){
	    System.out.println("FORWARDCHANGEUPDATE #########");
		if (changeLock) return;  // if a change is underway here, and it's driven by THIS element (cf. for instance, translate() or rotate())
		// we block incoming events until the change is completed ; the method which led the change
		// should obviously fire an event itself afterwards.

		// now, the geometry of one of the node has changed, so we need to update the edge as well
		updateEdge();

		fireChangedUpdate(eventType); // update view then forward (see superclass)
	}

	/**
	 * Translate both nodes and connections by the given vector
	 * @param dx The X coordinate of translation vector
	 * @param dy The Y coordinate of translation vector
	 * @since jPicEdt 1.4pre5
	 */
	public void translate(double dx, double dy){
		// [pending] translate edge in a more clever way ?
		AffineTransform translation = AffineTransform.getTranslateInstance(dx,dy);
		edge=translation.createTransformedShape(this.edge);
		if (edgeType==EDGE_NCCURVE){
		    this.controlPointA.translate(dx,dy);
		    this.controlPointB.translate(dx,dy);
		}
		changeLock = true; // filter out event from nodes
		nodeA.translate(dx,dy); // this results in nodeA calling forwardChangeUpdate() through its fireChangedUpdate() method
		nodeB.translate(dx,dy); // but this has no effect HERE since changeLock = true (this may have effects in other PicNodeConnection's though)

		changeLock = false;
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Scale children by (sx,sy) using (ptOrgX,ptOrgY) as origin ; sx and sy can be negative.
	 */
	public void scale(double ptOrgX, double ptOrgY, double sx, double sy, UserConfirmationCache ucc){
		// [pending] scale edge (see translate for inspiration)
		// ...
	}

	/**
	 * Rotate this Element by the given angle along the given point
	 * @param angle rotation angle in radians
	 */
	public void rotate(PicPoint ptOrg, double angle){
		// [pending] rotate edge (see translate for inspiration)
		// ...
	}

	/**
	 * Effectue une réflexion sur <code>this</code> relativement à l'axe
	 * défini par <code>ptOrg</code> et <code>normalVector</code>.
	 *
	 * @param ptOrg le <code>PicPoint</code> par lequel passe l'axe de réflexion.
	 * @param normalVector le <code>PicVector</code> normal à l'axe de réflexion.
	 */
	public void mirror(PicPoint ptOrg, PicVector normalVector){
	}


	/**
	 * Shear this Element by the given params wrt to the given origin
	 */
	public void shear(PicPoint ptOrg, double shx, double shy, UserConfirmationCache ucc){
		// [pending] shear edge (see translate for inspiration)
		// ...
	}

    public String toString(){
	String s="Node Connection.\n"+super.toString();
	s+="edge= "+edge.toString()+"\n";
	s+="edge type= "+edgeType+"\n";
        s+="nodeA= "+nodeA+"\n";
	s+="nodeB= "+nodeB+"\n";
	s+="controlpointA= "+controlPointA+"\n";
	s+="controlPointB= "+controlPointB+"\n";
	return s;
    }

}
