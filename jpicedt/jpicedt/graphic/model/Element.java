// Element.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: Element.java,v 1.25 2013/03/27 07:02:38 vincentb1 Exp $
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

// Interface de base des �l�ments grapiques d'un dessin.

/// Code:
package jpicedt.graphic.model;

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.toolkit.ConvexZoneGroup;
import jpicedt.graphic.view.ArrowView;
import jpicedt.graphic.view.View;
import jpicedt.graphic.view.ViewFactory;
import jpicedt.ui.dialog.UserConfirmationCache;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.BitSet;

/**
 * This interface specifies the general contract for models of graphic elements that may be added to a
 * Drawing.<p>
 * A <b>MODEL</b> of graphic element basically comprises :
 * <ul>
 * <li> A set of (possibly virtual, see implementation hints below) user-controlled points that helps the
 *      "user" alter the geometry of the element ; these points are made accessible by means of the various
 *      get/setCtrlPt() methods, using an index ranging from <code>getFirstPointIndex()</code> to
 *      <code>getLastPointIndex()</code> inclusive.  These methods may be used e.g. by the UI machinery when
 *      processing mouse-events, or by parsers when building a new Drawing from scratch.
 * <li> Amid this set, some points may serve as <b>anchor-points</b> for grid alignment (see
 *      <code>PointIterator</code>).
 * <li> An attribute set that allows binding the element's geometry to a set of colour, stroke,
 *      etc&hellip; attributes.
 * <li> A parent and possibly children once the element has been added to a tree defining a Drawing's
 *      model. This allows us to let the root element only hold state about the tree while still maintaining
 *      the ability for a child to retrieve this state at any time through a (possibly recursive) call to
 *      getParent.
 * <li> Besides, each Element may have a View attached to it (but this is not necessary as long as it has not
 *      to be rendered), which is responsible for rendering the element to a graphic context. View's are
 *      usually created by a ViewFactory that's suited for the kind of content the model represents
 *      (e.g. PostScript, LaTeX, JPIC-XML, &hellip;).
 * </ul>
 * <b>Implementation hints</b> : under the hood, the geometry may be backed e.g. by an array of points
 * (in which case these may be the control-points as well),
 * by a <code>java.awt.Shape</code>, or by any other kind of mechanism the developper thinks is appropriate
 * to the particular model she wants to implement.
 * </ul>
 * <p>
 *
 * @author Sylvain Reynal
 * @since PicEdt 1.0
 * @version $Id: Element.java,v 1.25 2013/03/27 07:02:38 vincentb1 Exp $
 */
public interface Element  {


	/**
	 * Return a non-localised string representing this element's name. This may be used by a UI to display
	 * some information related to this element, or by a localizer to fetch a i18n'd string.
	 */
	String getName();


	/**
	 * Return a deep copy of this element.
	 */
	Element clone();


	///////////////////////////////////////
	//// tree structure
	///////////////////////////////////////

	/**
	 * Retrieves the underlying drawing
	 * @return the drawing ; null if this Element doesn't belong to any drawing yet.
	 */
	Drawing getDrawing();

	/**
	 * Gets the parent of the element.
	 * @return the parent
	 */
	BranchElement getParent();

	/**
	 * La m�thode <code>replaceBy</code> remplace dans le dessin l'�l�ment <code>this</code> par l'�l�ment
	 * <code>by</code>. Sans effet si <code>this.getParent()</code> renvoie <code>null</code>. Si
	 * <code>replaceInSelection</code> est <code>true</code> et que <code>this</code> est dans la s�lection,
	 * alors il est �galement remplac� par <code>by</code> dans la s�lection.
	 * @param by l'<code>Element</code> servant de remplacement.
	 * @param replaceInSelection indique que <code>this</code> est �galement � remplacer dans la selection.
	 */
	void replaceBy(Element by, boolean replaceInSelection);

	/**
	 * Sets the parent of the element.
	 */
	void setParent(BranchElement p);

	///**
	// * Called by a child of this element to inform its parent of some change that occured to it or one of
	// * its children.  This gives a chance to the receiver to update its layout, then to propagate the
	// * change-event upward.  This method obviously makes sense only if this element allows children (else it
	// * will never be called anyway, hence concrete implementation may do nothing here).
	// * @param eventType the event type
	// * @param child the child that sent the change-event.
	// */
	// July '06 void forwardChangedUpdate(Element child,DrawingEvent.EventType eventType);

	////////////////////////////////////
	//// GEOM.
	///////////////////////////////////

	/**
	 * Return the <b>user-controlled point</b> having the given index. The general contract is to return an
	 * IMMUTABLE instance of PicPoint, so that the only way to alter the geometry of this element is
	 * by calling the <code>setPoint</code> method.
	 * @return the point indexed by <code>index</code> ;
	 *         if <code>dest</code> is null, concrete implementation of this method should
	 *         allocate a new PicPoint and return it,
	 *         otherwise directly modify <code>dest</code> and return it as well for convenience.
	 * @param index the point index, should be greater or equal to the value returned by
	 *        <code>getFirstPointIndex</code>, and lower or equal to <code>getLastPointIndex</code>.
	 * @since PicEdt 1.0
	 * [todo] change name to getControlPoint() or getUserPoint() or whatever seems more appropriate.
	 */
	PicPoint getCtrlPt(int index, PicPoint dest);

	/**
	 * Return the index of the first user-controlled point that can be retrieved by getCtrlPt()
	 */
	int getFirstPointIndex();

	/**
	 * Return the index of the last user-controlled point that can be retrieved by getCtrlPt()
	 */
	int getLastPointIndex();

	/**
	 * Set the user-controlled point indexed by "index" to the given value.
	 * This should be a convenience call to <code>setCtrlPt(index,pt,null)</code>,
	 * i.e. using no particular geometrical constraint.
	 */
	void setCtrlPt(int index, PicPoint pt);

	/**
	 * Set the user-controlled point indexed by "index" to the given value, using the specified geometrical
	 * constraint.  Constraints depend on the particular concrete implementation, and may involve restricting
	 * movement along a particular direction, moving several points at once to preserve parallelism,&hellip;
	 * @param constraint a geometry constraint, or null if no particular constraint is being imposed (aka default).
	 */
	void setCtrlPt(int index, PicPoint pt, EditPointConstraint constraint);

	/**
	 * Return an Iterator over user-controlled point indexes that can for instance serve as anchor points for
	 * grid alignment.  This is up to concrete implementations to decide what subset of user-controlled points
	 * is most appropriate for grid alignment.
	 */
	PointIndexIterator anchorPointsIterator();

	/**
	 * Translate this object by (dx,dy)
	 * @param dx The X coordinate of translation vector
	 * @param dy The Y coordinate of translation vector
	 *
	 * @since PicEdt 1.0
	 * [todo] add an "apply(Transform t)" method as replacement to "translate()" and "scale()", where Transform
	 *        may possibly inherit from java.awt.geom.AffineTransform [syd : 5/8/2002]
	 */
	void translate(double dx, double dy);


	/**
	 * Renvoie un la partie des points de contr�le qui satisfont � l'un des deux crit�res
	 * suivants:
	 * <ol>
	 * <li> ceux compris dans l'ensemble de zones convexes <code>csg</code>, et
	 * <li> ceux en relation avec les points de contr�le satisfaisant au premi�re crit�re selon une relation
	 *      d�finies par <code>czExtension</code>
	 * </ol>
	 * @param csg un ensemble de zones convexes
	 * @param czExtension un <code>BitSet</code> permettant d'�tendre l'ensemble des points de contr�le
	 * compris dans l'ensemble de zones convexes <code>csg</code>. La signification des bit est selon
	 * l'�num�r� {@link CtrlPtSubset.CZExtension}.
	 */
	CtrlPtSubset getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension);

	/**
	 * Scale this object by <code>(sx,sy)</code> using <code>ptOrg</code> as origin
	 * sx and sy may be negative. This can be implemented as a convenience call to
	 * scale(double,double,double,double)
	 */
	void scale(PicPoint ptOrg, double sx, double sy);

	/**
	 * Scale this object by <code>(sx,sy)</code> using <code>(ptOrgX,ptOrgY)</code> as origin
	 * <code>sx</code> and <code>sy</code> can be negative.
	 */
	void scale(double ptOrgX, double ptOrgY, double sx, double sy);

	/**
	 * Pareil que {@link #scale(double ptOrgX, double ptOrgY, double sx, double sy)} sauf que dans le cas
	 * d'une dilatation non conforme &mdash;&nbsp;c'est � dire ne conservant pas les proportions&nbsp;&mdash;,
	 * et d'une forme suppos�e � proportions contraintes (comme par exemple <code>PicCircleFrom3Points</code>)
	 * on demande, sauf pr�f�rences utilisateurs contraires, son avis � l'utilisateur.
	 *
	 * @param ucc une valeur <code>UserConfirmationCache</code> permettant de demander � l'utilisateur
	 * confirmation, de se souvenir de la derni�re confirmation qu'il a donn�, ou de ses pr�f�rences.
	 */
	void scale(double ptOrgX, double ptOrgY, double sx, double sy,UserConfirmationCache ucc);

	void scale(PicPoint ptOrg, double sx, double sy,UserConfirmationCache ucc);


	/**
	 * Rotate this Element by the given angle along the given point
	 * @param angle rotation angle in radians
	 */
	void rotate(PicPoint ptOrg, double angle);

	/**
	 * Shear this <code>Element</code> by the given params wrt to the given origin
	 */
	void shear(PicPoint ptOrg, double shx, double shy);


	/**
	 * Pareil que {@link #shear(PicPoint ptOrg, double shx, double shy)} sauf que dans le cas
	 * d'une dilatation non conforme &mdash;&nbsp;c'est � dire ne conservant pas les proportions&nbsp;&mdash;,
	 * et d'une forme suppos�e � proportions contraintes (comme par exemple <code>PicCircleFrom3Points</code>)
	 * on demande, sauf pr�f�rences utilisateurs contraires, son avis � l'utilisateur.
	 *
	 * @param ucc une valeur <code>UserConfirmationCache</code> permettant de demander � l'utilisateur
	 * confirmation, de se souvenir de la derni�re confirmation qu'il a donn�, ou de ses pr�f�rences.
	 */
	void shear(PicPoint ptOrg, double shx, double shy,UserConfirmationCache ucc);


	/**
	 * Effectue une r�flexion de l'objet <code>this</code> relativement � la
	 * droite passant par le point <code>ptOrg</code> et normale au vecteur
	 * <code>normalVector</code>.
	 *
	 * C'est � dire que tout point de contr�le <code>pt</code> est remplac� par:
	 *
	 * <code>pt - 2 * produit_scalaire(pt - ptOrg, normalVector)</code>
	 *
	 * Si <code>normalVector</code> n'est pas de norme euclidienne 1, alors
	 * <code>mirror</code> fait donc en plus une dilatation de coefficient
	 * �gal � la norme euclidienne de <code>normalVector</code>.
	 *
	 * @param ptOrg a <code>PicPoint</code> value
	 * @param normalVector a <code>PicVector</code> value
	 */
	void mirror(PicPoint ptOrg,PicVector normalVector);

	/////////////////////////////////
	/// BOUNDING BOX
	/////////////////////////////////

	/**
	 * @return the bounding box (i.e. the surrounding rectangle) in double precision
	 * Used e.g. to determine the arguments of the \\begin{picture} command.
	 * If r is null, allocate a new rectangle and returns it. Otherwise the source rectangle is
	 * modified and returned for convenience.
	 * [todo:reynal] this really need to be improved : this method would probably better be moved to the
	 *               attached view, since the latter knows exactly what the TRUE bounding box is.
	 * @since PicEdt 1.0
	 */
	Rectangle2D getBoundingBox(Rectangle2D r);

	/////////////////////////////////
	/// PAINT
	/////////////////////////////////

	/**
	 * @return the View that's responsible for rendering this Element
	 */
	View getView();

	/**
	 * set the view for this Element from the given view factory
	 */
	void setViewFromFactory(ViewFactory f);

	/**
	 * remove the view that render this element ; this may be used to remove any reference to the view,
	 * and render it eligible for garbage collection ; if no View, does nothing.
	 */
	void removeView();

	/**
	 * Creates a Shape that reflects the geometry of this model.
	 */
	Shape createShape();

	/**
	 * Helper for the associated View.
	 * Synchronizes the state (aka location and direction) of the given ArrowView with
	 * this model, if applicable (the model is indeed the most knowledgeable about these data).
	 * Otherwise does nothing, for instance if the associated geometry
	 * is closed and does not support arrows.
	 */
	void syncArrowGeometry(ArrowView v, ArrowView.Direction d);


	/////////////////////////////////
	/// VARIOUS FIELDS MANIPULATORS
	/////////////////////////////////

	/**
	 * Returns the AttributeSet for this Element
	 */
	PicAttributeSet getAttributeSet();

	/**
	 * Sets a new AttributeSet for this Element ; this actually make a deep copy of the
	 *        given attribute set beforehands.
	 */
	void setAttributeSet(PicAttributeSet attributeSet);

	/**
	 * Sets the given attribute name to the given value for this Element
	 */
	<T> void setAttribute(PicAttributeName<T> name, T value);

	/**
	 * Return the value for the given attribute name
	 */
	<T> T getAttribute(PicAttributeName<T> name);



} // Element interface
