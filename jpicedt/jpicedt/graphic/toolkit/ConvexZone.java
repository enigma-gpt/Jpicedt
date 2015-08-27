// ConvexZone.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZone.java,v 1.11 2013/03/27 06:59:46 vincentb1 Exp $
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

import java.lang.String;
import java.lang.StringBuffer;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.EditPointConstraint;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicAttributeName;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.model.PointIndexIterator;
import jpicedt.graphic.util.ConvexPolygonalZone;
import jpicedt.graphic.util.ConvexPolygonalZoneBoundary;
import jpicedt.graphic.view.ConvexZoneHalfPlaneView;

import static jpicedt.Log.DEBUG;
import static jpicedt.Log.debug;
import static jpicedt.graphic.view.ViewConstants.CLICK_DISTANCE;

/**
 * Objet graphique permettant de visualiser une zone polygonale convexe sur
 * la toile de dessin <code>PECanvas</code>, ainsi que dérouter les évenements
 * d'entrée de l'utilisateur.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela&iuml;che</a>
 * @version $Id: ConvexZone.java,v 1.11 2013/03/27 06:59:46 vincentb1 Exp $
 */
public class ConvexZone {

	/**
	 * La définition de la zone polyogonale convexe sous-jacente.
	 *
	 */
	protected ConvexPolygonalZone convexPolygonalZone;
	protected PECanvas canvas;

	// les chemins suivants servent à visionner la zone convexe.
	private   GeneralPath boundaryPath0;
	private   GeneralPath boundaryPath1;
	private   GeneralPath insidePath;
	/** premier chemin en pointillé à l'intersection de la zone convexe
	 *	<code>this</code> et de la toile <code>PECanvas</code> sur laquelle
	 *	elle porte.  Non-<code>null</code> uniquement pour les zones convexes
	 *	ayant des points à l'infini, comme les demi-plans ou les
	 *	bandes. <code>null</code> Pour les zones convexes qui ne zone ni une
	 *	bande ni un demi plan.
	 */
	private   GeneralPath allocationIntersectionPath0;

	/** Second chemin en pointillé à l'intersection de la zone conexe
	 *	<code>this</code> et de la toile <code>PECanvas</code> sur laquelle
	 *	elle porte.  Non-<code>null</code> uniquement pour les zones convexes
	 *	qui sont des bandes, et donc quo ont deux telles interesctions.
	 */
	private   GeneralPath allocationIntersectionPath1;
	private   ConvexZoneHalfPlaneView convexZoneHalfPlaneView;

	private   Rectangle2D bounds  = new Rectangle2D.Double();
	private ConvexPolygonalZoneBoundary boundary;

	public ConvexZone(ConvexPolygonalZone convexPolygonalZone,PECanvas canvas){
		this.convexPolygonalZone = convexPolygonalZone;
		this.canvas = canvas;
		this.convexZoneHalfPlaneView = new ConvexZoneHalfPlaneView(this);
	}

	public ConvexZone clone(){
		return new ConvexZone(convexPolygonalZone.clone(),canvas);
	}

	public String getName(){ return "canvas.convexZoneElement";}

	public ConvexPolygonalZone getConvexPolygonalZone(){ return convexPolygonalZone;}

	private class PointsInInfinity{

		private final double BARBELL = 0.5;
		private double x0,x1,y0,y1;
		private int xMap, yMap;
		private int anglePointNb,getAnglePointNb;
		private int phase0, phase1;

		PointsInInfinity(Rectangle2D allocation){
				x0 = allocation.getX()+BARBELL;
				x1 = allocation.getX() + allocation.getWidth()-BARBELL;
				y0 = allocation.getY()+BARBELL;
				y1 = allocation.getY() + allocation.getHeight()-BARBELL;
				resetMap();
		}

		PointsInInfinity(PointsInInfinity pif/* le chien*/){
				x0 = pif.x0;
				x1 = pif.x1;
				y0 = pif.y0;
				y1 = pif.y1;
				resetMap();
		}


		void resetMap(){
			xMap = yMap = anglePointNb = getAnglePointNb = 0;

		}

		/**
		 * Renvoie le point le plus loin de l'origine d'une demi-droite
		 * coupant le rectangle d'allocation passé à la construction de
		 * <code>this</code>. La demi-droite est déterminée comme l'une des
		 * demi-bordure d'un <code>ConvexPolygonalZone.HalfPlane</code> selon
		 * selon l'argument <code>direction</code>.
		 *
		 * @param hp une valeur <code>ConvexPolygonalZone.HalfPlane</code>
		 * déterminant l'une ou l'autre des demi-droite frontière du demi-plan
		 * de part et d'autre de son point d'origine.
		 * @param direction une valeur <code>double</code> égale à +1 ou -1,
		 * selectionnant la bonne des deux demi-droites.
		 * @return une valeur <code>PicPoint</code>
		 */
		PicPoint get(ConvexPolygonalZone.HalfPlane hp, double direction){
			double nx,ny,ux,uy,ox,oy;
			nx = hp.getDir().getX();
			ny = hp.getDir().getY();
			ux = direction * ny;
			uy = direction * -nx;
			ox = hp.getOrg().getX();
			oy = hp.getOrg().getY();
			// on cherche le point d'intersection entre la bordure de la zone
			// convexe et la bordure du PECanvas sous la forme de
			// o + lambda * u, avec o = (ox, oy) et u = (ux, uy)
			double lambdaX, lambdaY, lambda;
			int xMap = 0;
			int yMap = 0;
			int shiftLeft = 2*anglePointNb++;
			if(ux != 0.0)
			{
				lambdaX = (x0-ox)/ux;

				lambda = (x1-ox)/ux;
				if(lambda > lambdaX)
				{
					// la bordure verticale de *droite* du PECanvas est celle
					// qui est du bon côté
					lambdaX = lambda;
					xMap   = 3;
				}
				else
					// la bordure verticale de *gauche* du PECanvas est celle
					// qui est du bon côté
					xMap   = 1;

			}
			else
			{
				lambdaX = Double.POSITIVE_INFINITY;
				xMap = 2;
			}

			if(uy != 0.0)
			{
				lambdaY = (y0-oy)/uy;

				lambda = (y1-oy)/uy;
				if(lambda > lambdaY)
				{
					// la bordure verticale du *haut* du PECanvas est celle
					// qui est du bon côté
					lambdaY = lambda;
					yMap = 3;
				}
				else
					// la bordure verticale du *bas* du PECanvas est celle
					// qui est du bon côté
					yMap = 1;

			}
			else
			{
				lambdaY = Double.POSITIVE_INFINITY;
				yMap = 2;
			}

			if(lambdaY > lambdaX)
			{
				// la bordure de zone convexe coupe une bordure *verticale* de
				// PECanvas en premier
				lambda = lambdaX;
				if(ny*(yMap-2) < 0)
					yMap = 4-yMap;
			}
			else
			{
				// la bordure de zone convexe coupe une bordure *horizontale*
				// de PECanvas en premier
				lambda = lambdaY;
				if(nx*(xMap-2) < 0)
					xMap = 4-xMap;
			}

			this.xMap |= xMap<<shiftLeft;
			this.yMap |= yMap<<shiftLeft;

			return new PicPoint(ox + lambda * ux, oy + lambda * uy);
		}

		private int mapsToPhase(int xMap,int yMap){
			xMap &= 3;
			yMap &= 3;
			switch(yMap){
			case 1:
				return 4+xMap;
			case 2:
				return 2*(3-xMap);
			case 3:
				return 4-xMap;
			}
			return -1; // Ne doit jamais arriver, on met ça juste pour enlever
					   // l'avertissement à lacompilation.
		}

		private int roundedUpPhase(int arg){
			arg += (arg&1)^1;
			return arg&7;
		}
		private int roundedDownPhase(int arg){
			arg -= (arg&1)^1;
			return arg&7;
		}
		PicPoint getAnglePoint(){
			/* Map:
			(1,3)     (2,3)    (3,3)
                +--------------+
				|              |
   	       (1,2)|              |(2,2)
                |              |
				+--------------+
			(1,1)     (1,1)    (3,1)

               Phase:
	   	       3       2       1
                +--------------+
			   	|              |
   	           4|              |0
                |              |
				+--------------+
			   5       6        7


			 */
			int phase;
			double x,y;
			if(getAnglePointNb == 0){
				phase0 = roundedUpPhase(mapsToPhase(xMap,yMap));
				phase1 = roundedDownPhase(mapsToPhase(xMap>>2,yMap>>2));
				if(phase0 > phase1)
					phase1 += 8;
			}
			phase = phase0 + 2*getAnglePointNb;
			if(phase > phase1)
				return null;
			++getAnglePointNb;

			x = ((phase-2)&7)<=4 ? x0 : x1;
			y = (phase&7)<=4 ? y1:y0;
			return new PicPoint(x,y);
		}
	};

	/**
	 * Le méthode <code>createShape</code> crée la forme qui sert à visualiser
	 * le contour de la zone convexe.
	 */
	public void createShape(Rectangle2D allocation){
		if(boundaryPath0 != null)
			return;

		if(boundary == null)
			boundary = convexPolygonalZone.getBoundary();

		if(DEBUG) debug("this = " + toString() + "\n\tboundary = " + boundary.toString()
						+ "\n\tallocation=" + allocation.toString() );

		if(boundaryPath0 == null)
			boundaryPath0 = new GeneralPath();
		boundaryPath0.reset();
		if(boundary.subdivisionPoints.size() != 0)
		{
			// la frontière n'est pas une droite, mais comprend au moins un
			// point de sousdivision.
			boundaryPath1 = null;
			allocationIntersectionPath1 = null;

			PicPoint firstPointInInfinity = null;
			PicPoint secondPointInInfinity = null;
			PicPoint anglePoint = null;
			int i;
			if(boundary.isClosed())
			{
				// pas de points à l'infini
				allocationIntersectionPath0 = null;
				allocationIntersectionPath1 = null;

				bounds.setFrameFromDiagonal(boundary.subdivisionPoints.get(0),
											boundary.subdivisionPoints.get(1));
				i = 0;
			}
			else
			{
				PointsInInfinity pointsInInfinity = new PointsInInfinity(allocation);
				firstPointInInfinity = pointsInInfinity.get(boundary.halfPlanes.getFirst(),+1);
				boundaryPath0.moveTo(firstPointInInfinity.getX(),firstPointInInfinity.getY());
				secondPointInInfinity = pointsInInfinity.get(boundary.halfPlanes.getLast(),-1);
				anglePoint = pointsInInfinity.getAnglePoint();
				bounds.setFrameFromDiagonal(firstPointInInfinity, secondPointInInfinity);
				i = 1;
			}
			for(PicPoint pt : boundary.subdivisionPoints){
				if(i == 0)
				{
					boundaryPath0.moveTo(pt.getX(),pt.getY());
				}
				else
				{
					boundaryPath0.lineTo(pt.getX(),pt.getY());
					if(i > 1)
						bounds.add(pt);
				}
				++i;
			}
			if(! boundary.isClosed())
			{
				boundaryPath0.lineTo(secondPointInInfinity.getX(),secondPointInInfinity.getY());
				if(allocationIntersectionPath0 == null) allocationIntersectionPath0 = new GeneralPath();
				allocationIntersectionPath1 = null;
				allocationIntersectionPath0.reset();
				allocationIntersectionPath0.moveTo(firstPointInInfinity.getX(),firstPointInInfinity.getY());
				if(anglePoint != null)
					allocationIntersectionPath0.lineTo(anglePoint.getX(), anglePoint.getY());
				allocationIntersectionPath0.lineTo(secondPointInInfinity.getX(),secondPointInInfinity.getY());
			}
			else
				boundaryPath0.closePath();
			if(DEBUG)
				debug("boundaryPath0 = " + boundaryPath0.toString());
		}
		else if(boundary.halfPlanes.size() > 0)
		{
			// ici on n'a pas de point de sous-division, donc la zone convexe
			// est soit une bande, soit un demi-plan.
			PointsInInfinity pointsInInfinity0 = new PointsInInfinity(allocation);
			PicPoint firstPointInInfinity0 = pointsInInfinity0.get(boundary.halfPlanes.getFirst(),+1);
			PicPoint secondPointInInfinity0 = pointsInInfinity0.get(boundary.halfPlanes.getFirst(),-1);
			if(boundaryPath0 == null) boundaryPath0 = new GeneralPath();
			boundaryPath0.reset();
			boundaryPath0.moveTo(firstPointInInfinity0.getX(),firstPointInInfinity0.getY());
			boundaryPath0.lineTo(secondPointInInfinity0.getX(),secondPointInInfinity0.getY());

			if(boundary.halfPlanes.size() > 1)
			{
				// la zone convexe est une bande
				if(boundaryPath1 == null) boundaryPath1 = new GeneralPath();
				boundaryPath1.reset();
				boundaryPath1.moveTo(firstPointInInfinity0.getX(),firstPointInInfinity0.getY());
				PointsInInfinity pointsInInfinity1 = new PointsInInfinity(pointsInInfinity0);

				// Astuce : on prend le complémentaire de l'autre demi-plan de
				// sorte que les points d'angle tournent dans le même sens.
				ConvexPolygonalZone.HalfPlane complementLastHp = boundary.halfPlanes.getLast().dirCInverse();

				PicPoint firstPointInInfinity1 = pointsInInfinity1.get(complementLastHp,+1);
				PicPoint secondPointInInfinity1 = pointsInInfinity1.get(complementLastHp,-1);

				/*
				 first0  first1	   (0) := boundaryPath0
				  x	  (1) x	       (1) := allocationIntersectionPath0
				  |		  |	   	   (2) := boundaryPath1
		   	   (0)|>   	  |>   	   (3) :=  allocationIntersectionPath1
				  |		  |(2)
				  x	 (3)  x
				second0   second1

				 */
				if(boundaryPath1 == null) boundaryPath1 = new GeneralPath();
				boundaryPath1.reset();
				// on fait trace de `second' vers `first' parce que on a pris
				// le demi plan complémentaire, donc l'ordre est inversé si on
				// veut continuer à tourner dans le même sens.
				boundaryPath1.moveTo(secondPointInInfinity1.getX(),secondPointInInfinity1.getY());
				boundaryPath1.lineTo(firstPointInInfinity1.getX(),firstPointInInfinity1.getY());

				if(allocationIntersectionPath0 == null) allocationIntersectionPath0 = new GeneralPath();
				allocationIntersectionPath0.reset();
				allocationIntersectionPath0.moveTo(firstPointInInfinity0.getX(),firstPointInInfinity0.getY());

				if(allocationIntersectionPath1 == null) allocationIntersectionPath1 = new GeneralPath();
				allocationIntersectionPath1.reset();
				// idem, ici il faut partir de `second' et non de `first'
				// toujours pourn la même raison qu'on a pris le demi-plan
				// complémentaire.
				allocationIntersectionPath1.moveTo(secondPointInInfinity1.getX(),secondPointInInfinity1.getY());

				PicPoint anglePoint0, anglePoint1;

				anglePoint1 = pointsInInfinity1.getAnglePoint();
				while((anglePoint0 = pointsInInfinity0.getAnglePoint()) != null){
					if(anglePoint1 != null && anglePoint0.equals(anglePoint1))
						break;
					allocationIntersectionPath0.lineTo(anglePoint0.getX(),anglePoint0.getY());
				}
				allocationIntersectionPath0.lineTo(firstPointInInfinity1.getX(),
												   firstPointInInfinity1.getY());

				if(anglePoint1 != null && anglePoint0 != null){
					for(;;){
						anglePoint0 = pointsInInfinity0.getAnglePoint();
						anglePoint1 = pointsInInfinity1.getAnglePoint();
						if(anglePoint1 == null)
							break;
						if(anglePoint0 == null)
							break;
						if(!anglePoint0.equals(anglePoint1))
							break;
					}
					while(anglePoint0 != null){
						allocationIntersectionPath1.lineTo(anglePoint0.getX(),anglePoint0.getY());
						anglePoint0 = pointsInInfinity0.getAnglePoint();
					}
				}

				allocationIntersectionPath1.lineTo(secondPointInInfinity0.getX(),
												   secondPointInInfinity0.getY());
			}
			else
			{
				// la zone convexe est un demi-plan
				boundaryPath1 = null;
				allocationIntersectionPath1 = null;
				if(allocationIntersectionPath0 == null) allocationIntersectionPath0 = new GeneralPath();
				allocationIntersectionPath0.moveTo(firstPointInInfinity0.getX(),firstPointInInfinity0.getY());
				PicPoint anglePoint;
				while((anglePoint= pointsInInfinity0.getAnglePoint()) != null){
					allocationIntersectionPath0.lineTo(anglePoint.getX(),anglePoint.getY());
				}
				allocationIntersectionPath0.lineTo(secondPointInInfinity0.getX(),
												   secondPointInInfinity0.getY());

			}
		}
	}

	/**
	 * Describe <code>hitTest</code> method here.
	 *
	 * @param e a <code>PEMouseEvent</code> value
	 * @param isHighlightVisible a <code>boolean</code> value
	 * @return a <code>ConvexZoneHitTest</code> value
	 */
	public ConvexZoneHitInfo hitTest(PEMouseEvent e, boolean isHighlightVisible){

		if(boundaryPath0 == null) return null;
		double clickDistance = CLICK_DISTANCE / e.getCanvas().getZoomFactor();
		int segmentIndex = PEToolKit.testDistanceToPath(boundaryPath0, e.getPicPoint(), clickDistance);
		if (segmentIndex >= 0) return new ConvexZoneHitInfo.Stroke(this,segmentIndex,e);
		if(boundaryPath1 == null) return null;
		segmentIndex = PEToolKit.testDistanceToPath(boundaryPath1, e.getPicPoint(), clickDistance);
		if (segmentIndex >= 0) return new ConvexZoneHitInfo.Stroke(this,segmentIndex,e);
		return null;
	}


	public void paint(Graphics2D g, Rectangle2D allocation, double scale){

		if(boundaryPath0 == null)
			createShape(allocation);

		if(insidePath != null)					g.draw(insidePath);
		if(boundaryPath0 != null)				g.draw(boundaryPath0);
		if(boundaryPath1 != null)				g.draw(boundaryPath1);
		g.setStroke(ConvexZoneViewParameters.getDashedLineStroke());
		if(allocationIntersectionPath0 != null) g.draw(allocationIntersectionPath0);
		if(allocationIntersectionPath1 != null) g.draw(allocationIntersectionPath1);
		g.setStroke(ConvexZoneViewParameters.getSolidLineStroke());

		convexZoneHalfPlaneView.paint(g,allocation,scale);

		if(DEBUG) debug();

	}

	public Rectangle2D getBoundingBox(){
		return bounds;
	}
	public Drawing getDrawing(){
		if(canvas != null)
			return canvas.getDrawing();
		else
			return null;
	}

	public void shear(PicPoint pt,double shx,double shy){
		for(ConvexPolygonalZone.HalfPlane hp: convexPolygonalZone)
		{
			hp.getOrg().shear(pt,shx,shy);
			hp.getDir().scale(shx,shy);
		}
	}
	public void rotate(PicPoint org,double angle){
		for(ConvexPolygonalZone.HalfPlane hp: convexPolygonalZone)
		{
			hp.getOrg().rotate(org,angle);
			hp.getDir().rotate(angle);
		}
	}
	public void scale(double x,double y,double dx,double dy){
		for(ConvexPolygonalZone.HalfPlane hp: convexPolygonalZone)
		{
			hp.getOrg().scale(x,y,dx,dy);
			hp.getDir().scale(dx,dy);
		}
	}
	public void scale(PicPoint pt,double dx,double dy){
		for(ConvexPolygonalZone.HalfPlane hp: convexPolygonalZone)
		{
			hp.getOrg().scale(pt,dx,dy);
			hp.getDir().scale(dx,dy);
		}
	}
	public void translate(double dx,double dy){
		for(ConvexPolygonalZone.HalfPlane hp: convexPolygonalZone)
			hp.getOrg().translate(dx,dy);

		if(allocationIntersectionPath0 == null){
			// ici la zone convexe ne coupe pas le bord du PECanvas donc on
			// translate son contour tout bêtement. Il se peut que le résultat
			// se mette à couper le PECanvas, dans ce cas le tireté à la
			// coupure ne sera pas affiché.
			if(boundaryPath0 != null){
				AffineTransform at;
				boundaryPath0.transform(at= new AffineTransform(1.0,0.0,0.0,1.0,dx,dy));
				if(boundaryPath1 != null) boundaryPath1.transform(at);
			}
		}
		else{
			// ici la zone convexe coupe le bord du PECanvas donc on recalcule
			// brutalement les formes constituant sa vue.
			boundaryPath0 = null;
		}
		// toutefois on essaie de conserver le calcul de la frontière
		if(boundary != null)
			for(PicPoint pt : boundary.subdivisionPoints)
				pt.translate(dx,dy);
	}
	// public PointIndexIterator anchorPointsIterator(){ return null;}
	// public void setCtrlPt(int index,PicPoint pt,EditPointConstraint epc){}
	// public void setCtrlPt(int index,PicPoint pt){}
	// public int getLastPointIndex(){ return 0;}
	// public int getFirstPointIndex(){ return 0;}
	// public PicPoint getCtrlPt(int index){ return null;}
	// public PicPoint getCtrlPt(int index,PicPoint pt){ return null;}

	//-----------------------------------------------------------------------
	public void paintHighlighter(Graphics2D g, Rectangle2D allocation, double scale){
		convexZoneHalfPlaneView.paintHighlighter(g,allocation,scale);
	}

	/**
	 * Renvoie une <code>String</code> décrivant <code>this</code> à des fin
	 * de débogage.
	 *
	 * @return une valeur <code>String</code> décrivant <code>this</code>.
	 */
	public String toString(){
		StringBuffer ret = new StringBuffer();
		ret.append("[convexPolygonalZone=");
		ret.append(convexPolygonalZone.toString());
		ret.append("]");
		return ret.toString();
	}

	public boolean containsPoint(PicPoint pt){
		return convexPolygonalZone.contains(pt);
	}
}
/// ConvexZone.java ends here
