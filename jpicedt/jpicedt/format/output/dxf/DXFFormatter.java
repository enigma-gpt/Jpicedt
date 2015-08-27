// DXFFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFFormatter.java,v 1.10 2013/03/27 07:24:02 vincentb1 Exp $
// Keywords: AutoCAD, DXF (marque d�pos�e)
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
 * Autocad DXF (marque d�pos�e) (Drawing eXchange Format) formatter. Fabrique pour convertir un
 * dessin depuis le format jPicEdt vers le format DXF (marque d�pos�e) utilis� notamment par
 * AutoCad (marque d�pos�e).
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
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
	 * La m�thode <code>loadTemplate</code> charge en m�moire le patron correspondant aux pr�f�rences
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
	// Attention : il ne faut pas commencer � 1, car dans les section
	// HEADER, CLASSES, TABLES, il y a d�j� des valeurs de handles utilis�es
	// en dessous de 100, et �a ferait des conflits.
	protected int            entityHandle = 100;

	/**
	 * Indique s'il faut ins�rer le println de chaque �l�ment juste avant
	 * l'�l�ment sous la forme d'un commentaire DXF (marque d�pos�e).
	 * @since jpicedt 1.6
	 */
	public    boolean        showJpic(){ return showJpicB; }

	/**
	 * Ajoute � un tampon DXF (marque d�pos�e) buf une d�finition de segment de droite joingant
	 * pt1 � pt2
	 * @param buf tampon de sortie qui sera compos� dans le fichier DXF (marque d�pos�e) en
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
	 * Ajoute � un tampon DXF (marque d�pos�e) buf les �l�ments d'information DXF (marque d�pos�e) communs �
	 * toute entit� DXF (marque d�pos�e), et ind�pendants de l'instance d'entit�.
	 * @since jpicedt 1.6
	 */
	public void commonTagVal(DXFStringBuffer buf){
		buf.tagVal(5,Integer.toHexString(entityHandle++));
		if(getDXFVersion().getValue() >= AUTO_CAD_RELEASE_13.getValue())
			buf.tagVal(100,"AcDbEntity");
		buf.tagVal(8,0);
	}



	/** Formatteur de document DXF (marque d�pos�e) pour un dessin <code>Drawing</code> donn�.
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
		 * Construit ce formateur pour le dessin donn�.
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
		 * Permet de formatter les pr�f�rences utilisateur qui ont servie �
		 * produire le document DXF (marque d�pos�e). Ces pr�f�rences sont ins�r�es dans un
		 * commentaire du document DXF (marque d�pos�e) juste apr�s le JPIC-XML.
		 * @since jPicEdt 1.6
		 */
		private String prefIntToString(int i)
			{
				return ":"+Integer.valueOf(i).toString();
			}

		/**
		 * Forme une en-t�te donnant des informations supl�mentaires li�es au
		 * type de contenu. En l'occurrence il s'agit des pr�f�rences
		 * utilisateur avec lesquelles le document est format�.
		 * @return l'en-t�te sp�cifique au type de contenu.
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
	 * Charge les propri�t� DXF (marque d�pos�e) par leurs valeurs par d�faut.
	 * prises des <code>DXFContants</code>.
	 * @since jPicEdt 1.6
	 */
	public int loadDefault() {
		return dxfCustomProperties.loadDefault();
	}
	/**
	 * Charge les propri�t�s DXF (marque d�pos�e) � partir de l'objet <code>Properties</code> prop.
	 * @param prop les valeurs du widget � charger.
	 * @since jPicEdt 1.6
	 */
	public int load(Properties prop) {
		return dxfCustomProperties.load(prop);
	}

	/**
	 * Stocke les prori�t�s DXF (marque d�pos�e) dans l'objet <code>prop</code>.
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
