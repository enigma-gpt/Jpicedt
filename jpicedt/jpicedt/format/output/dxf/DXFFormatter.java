// DXFFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: DXFFormatter.java,v 1.10 2013/03/27 07:24:02 vincentb1 Exp $
// Keywords: AutoCAD, DXF (marque déposée)
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

// Installation:


/// Code:
package jpicedt.format.output.dxf;

import jpicedt.MiscUtilities;
import jpicedt.format.output.util.PicGroupFormatter;
import jpicedt.graphic.*;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatterFactory;
import jpicedt.graphic.io.formatter.CommentFormatting;
import jpicedt.graphic.io.formatter.AbstractDrawingFormatter;
import jpicedt.graphic.model.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static jpicedt.format.output.dxf.DXFConstants.*;
import static jpicedt.format.output.dxf.DXFConstants.DXFVersion.*;

import static jpicedt.graphic.model.StyleConstants.LineStyle.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;



/**
 * Autocad DXF (marque déposée) (Drawing eXchange Format) formatter. Fabrique pour convertir un
 * dessin depuis le format jPicEdt vers le format DXF (marque déposée) utilisé notamment par
 * AutoCad (marque déposée).
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jpicedt 1.6
 */
public class DXFFormatter extends AbstractFormatterFactory implements DXFCustomization
{
	static DXFCustomProperties dxfCustomProperties = new DXFCustomProperties(0);

	static DXFInformation[] dxfTemplate;
	/**
	 * @since jpicedt 1.6
	 */
	public DXFFormatter(){
		super();
		map(AbstractCurve.class,    AbstractCurveFormatter.class);
		map(PicEllipse.class,       PicEllipseFormatter.class);
		map(PicParallelogram.class, PicParallelogramFormatter.class);
		map(PicText.class,          PicTextFormatter.class);
		map(PicGroup.class, PicGroupFormatter.class);

		loadTemplate();
	}

	/**
	 * La méthode <code>loadTemplate</code> charge en mémoire le patron correspondant aux préférences
	 * utilisateur.
	 * @since jpicedt 1.6
	 */
	private static void loadTemplate(){
		dxfTemplate = new DXFTemplateParser(
			MiscUtilities.getJPicEdtHome()
			+File.separatorChar
			+"jpe-resources"
			+File.separatorChar
			+"template_"
			+ dxfCustomProperties.getDXFVersion().getDXFVersionId()
			+".dxftpl.jpicedt").getTemplate();
	}


	/**
	 * @since jpicedt 1.6
	 */
	public static void configure(Properties preferences)
		{
			dxfCustomProperties.load(preferences);

			loadTemplate();
		}

	protected boolean        showJpicB = false;
	// Attention : il ne faut pas commencer à 1, car dans les section
	// HEADER, CLASSES, TABLES, il y a déjà des valeurs de handles utilisées
	// en dessous de 100, et ça ferait des conflits.
	protected int            entityHandle = 100;

	/**
	 * Indique s'il faut insérer le println de chaque élément juste avant
	 * l'élément sous la forme d'un commentaire DXF (marque déposée).
	 * @since jpicedt 1.6
	 */
	public    boolean        showJpic(){ return showJpicB; }

	/**
	 * Ajoute à un tampon DXF (marque déposée) buf une définition de segment de droite joingant
	 * pt1 à pt2
	 * @param buf tampon de sortie qui sera composé dans le fichier DXF (marque déposée) en
	 * cours de formattage.
	 * @param pt1
	 * @param pt2
	 * @since jpicedt 1.6
	 */
	public void appendLine(DXFStringBuffer buf,
						   PicPoint pt1,
						   PicPoint pt2){

		buf.tagVal(0,"LINE");
		commonTagVal(buf);
		if(getDXFVersion().getValue() >= AUTO_CAD_RELEASE_13.getValue())
		   buf.tagVal(100,"AcDbLine");
		buf.tagVal(10,pt1.getX());
		buf.tagVal(20,pt1.getY());
		buf.tagVal(11,pt2.getX());
		buf.tagVal(21,pt2.getY());

	}

	/**
	 * Ajoute à un tampon DXF (marque déposée) buf les éléments d'information DXF (marque déposée) communs à
	 * toute entité DXF (marque déposée), et indépendants de l'instance d'entité.
	 * @since jpicedt 1.6
	 */
	public void commonTagVal(DXFStringBuffer buf){
		buf.tagVal(5,Integer.toHexString(entityHandle++));
		if(getDXFVersion().getValue() >= AUTO_CAD_RELEASE_13.getValue())
			buf.tagVal(100,"AcDbEntity");
		buf.tagVal(8,0);
	}



	/** Formatteur de document DXF (marque déposée) pour un dessin <code>Drawing</code> donné.
	 * @since jPicEdt 1.6
	 */
	class DrawingFormatter  extends AbstractDrawingFormatter implements DXFInfoFormatter
	{

		/** Le <code>Drawing</code> sur lequel ce  formateur agit. */
		protected DXFFormatter   factory;

		/**
		 * @since jPicEdt 1.6
		 */
		public Element getElement(){ return null;}


		public    boolean        showJpic(){ return factory.showJpic(); }

		private ArrayList<DXFInformation> extMin;
		private ArrayList<DXFInformation> extMax;
		private DXFStringBuffer           entities;

		private void formatDXFInformation() throws IOException{
			Rectangle2D box = drawing.getBoundingBox();
			if(box == null)
				box = new DXFEmptyDrawingBoundingBox();

			extMin = new ArrayList<DXFInformation>(3);
			extMin.add(new DXFTaggedValue.DXFTaggedString(9,"$EXTMIN"));
			extMin.add(new DXFTaggedValue.DXFTaggedDouble(10,box.getMinX()));
			extMin.add(new DXFTaggedValue.DXFTaggedDouble(20,box.getMinY()));

			extMax = new ArrayList<DXFInformation>(3);
			extMax.add(new DXFTaggedValue.DXFTaggedString(9,"$EXTMAX"));
			extMax.add(new DXFTaggedValue.DXFTaggedDouble(10,box.getMaxX()));
			extMax.add(new DXFTaggedValue.DXFTaggedDouble(20,box.getMaxY()));

			// entities
			entities = new DXFStringBuffer(100, getLineSeparator());
			for (Element e:drawing)
				entities.append(factory.createFormatter(e).format());
		}

		// sur-charge de DXFInfoFormatter
		/** Formate la variable $EXTMIN.
		 * @since jPicEdt 1.6
		 */
		public ArrayList<DXFInformation> getExtMin(){ return extMin; }
		public ArrayList<DXFInformation> getExtMax(){ return extMax; }
		public DXFStringBuffer getEntities(){ return entities; }


		/**
		 * Construit ce formateur pour le dessin donné.
		 * @since jPicEdt 1.6
		 */
		public DrawingFormatter
		(
			Drawing drawing,
			Object outputConstraint,
			DXFFormatter factory){
			super(drawing,outputConstraint);
			this.factory = factory;
		}

		/**
		 * Permet de formatter les préférences utilisateur qui ont servie à
		 * produire le document DXF (marque déposée). Ces préférences sont insérées dans un
		 * commentaire du document DXF (marque déposée) juste après le JPIC-XML.
		 * @since jPicEdt 1.6
		 */
		private String prefIntToString(int i)
			{
				return ":"+Integer.valueOf(i).toString();
			}

		/**
		 * Forme une en-tête donnant des informations suplémentaires liées au
		 * type de contenu. En l'occurrence il s'agit des préférences
		 * utilisateur avec lesquelles le document est formaté.
		 * @return l'en-tête spécifique au type de contenu.
		 * @since jPicEdt 1.6
		 */
		protected String createHeader(){
			DXFStringBuffer header =  new DXFStringBuffer(80, factory.getLineSeparator());
			header.comment("DXF pref:0"
						   +prefIntToString(factory.getElolCircle()     )
						   +prefIntToString(factory.getElolEllipse()    )
						   +prefIntToString(factory.getCurveMulticurve())
						   +prefIntToString(factory.getPlChord()        )
						   +prefIntToString(factory.getPlPie()          )
						   +prefIntToString(factory.getPlMulticurve()   )
						   +prefIntToString(factory.getPlParallelogram())
				);
			return header.toString();
		}
		/**
		 * @since jPicEdt 1.6
		 */
		public String format() throws IOException{

			DXFStringBuffer buf = new DXFStringBuffer(200, factory.getLineSeparator());
			buf.append(createHeader());

			formatDXFInformation();

			for(DXFInformation tv : dxfTemplate)
				tv.format(buf,this);

			return buf.toString();
		}
	};


	/**
	 * @since jPicEdt 1.6
	 */
	public Formatter createFormatter(Drawing d, Object outputConstraint){
		return new DrawingFormatter(d, outputConstraint,this);
	}

	/**
	 * @since jPicEdt 1.6
	 */
	public CommentFormatting getCommentFormatter()
		{
			return new DXFCommentFormatter(this);
		}

	// Redirection de dxfCustomProperties
	//#######################################################################
	/**
	 * Charge les propriété DXF (marque déposée) par leurs valeurs par défaut.
	 * prises des <code>DXFContants</code>.
	 * @since jPicEdt 1.6
	 */
	public int loadDefault() {
		return dxfCustomProperties.loadDefault();
	}
	/**
	 * Charge les propriétés DXF (marque déposée) à partir de l'objet <code>Properties</code> prop.
	 * @param prop les valeurs du widget à charger.
	 * @since jPicEdt 1.6
	 */
	public int load(Properties prop) {
		return dxfCustomProperties.load(prop);
	}

	/**
	 * Stocke les proriétés DXF (marque déposée) dans l'objet <code>prop</code>.
	 * @since jPicEdt 1.6
	 */
	 public void store(Properties prop){
		 dxfCustomProperties.store(prop);
	 }



	// Sur-charge de DXFCustomization.
	public DXFVersion getDXFVersion(){ return dxfCustomProperties.getDXFVersion(); }
   	public int getElolCircle()     {return dxfCustomProperties.getElolCircle()     ;}
   	public int getElolEllipse()    {return dxfCustomProperties.getElolEllipse()    ;}
	public int getCurveMulticurve(){return dxfCustomProperties.getCurveMulticurve();}
	public int getPlChord()        {return dxfCustomProperties.getPlChord()        ;}
	public int getPlPie()          {return dxfCustomProperties.getPlPie()          ;}
	public int getPlMulticurve()   {return dxfCustomProperties.getPlMulticurve()   ;}
	public int getPlParallelogram(){return dxfCustomProperties.getPlParallelogram();}

   	public void setElolCircle(int val)     {
		dxfCustomProperties.setElolCircle(val)     ;}
   	public void setElolEllipse(int val)    {
		dxfCustomProperties.setElolEllipse(val)    ;}
	public void setCurveMulticurve(int val){
		dxfCustomProperties.setCurveMulticurve(val);}
	public void setPlChord(int val)        {
		dxfCustomProperties.setPlChord(val)        ;}
	public void setPlPie(int val)          {
		dxfCustomProperties.setPlPie(val)          ;}
	public void setPlMulticruve(int val)   {
		dxfCustomProperties.setPlMulticruve(val)   ;}
	public void setPlParallelogram(int val){
		dxfCustomProperties.setPlParallelogram(val);}


};




/// DXFFormatter.java ends here
