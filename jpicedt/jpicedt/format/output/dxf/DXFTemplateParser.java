// DXFTemplateParser.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFTemplateParser.java,v 1.5 2013/03/27 07:11:25 vincentb1 Exp $
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


/// Code:
package jpicedt.format.output.dxf;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DXFTemplateParser
{

	ArrayList<DXFInformation> infoStack = new ArrayList<DXFInformation>(900);


	public DXFTemplateParser(String templateFileName){
		String line;
		Pattern groupCodePattern = Pattern.compile(
			"^\\p{Blank}*([0-9]+)\\p{Blank}*$");
		Pattern extMinPattern = Pattern.compile(
			"^\\\\extMin\\p{Blank}*$");
		Pattern extMaxPattern = Pattern.compile(
			"^\\\\extMax\\p{Blank}*$");
		Pattern entitiesPattern = Pattern.compile(
			"^\\\\entities\\p{Blank}*$");
		Matcher m;
		try
		{
			BufferedReader input = new BufferedReader(new FileReader(templateFileName));
			int lineNb = 0;
			for(;;)
			{
				++lineNb;
				if((line=input.readLine())!=null)
				{
					if(line.startsWith("%"))
						continue; // comment
					else if(line.startsWith("\\"))
					{
						// command
						if(extMinPattern.matcher(line).matches())
						{
							infoStack.add(new DXFTaggedValue.DXFExtMinFormatter());
						}
						else if(extMaxPattern.matcher(line).matches())
						{
							infoStack.add(new DXFTaggedValue.DXFExtMaxFormatter());
						}
						else if( entitiesPattern.matcher(line).matches())
						{
							infoStack.add(new DXFTaggedValue.DXFEntitiesFormatter());
						}
						else
							throw new DXFTemplateParseException.UnknownCommand(lineNb);
					}
					else if((m = groupCodePattern.matcher(line)).matches())
					{
						int groupCode = Integer.valueOf(m.group(1));
						line = input.readLine();
						if(line == null)
							throw new DXFTemplateParseException
								.MissingGroupValue(lineNb);
						infoStack.add(new DXFTaggedValue.DXFTaggedString(groupCode,line));
					}
					else
						throw new DXFTemplateParseException.InvalidLine(lineNb);

				}
				else
					break;
			}

		}
		catch(FileNotFoundException fnfEx)
		{
			//[todo] � localiser
			System.out.println("Fichier patron DXF non trouv�: info par d�faut...");
			infoStack = null;
		}
		catch(IOException ioEx)
		{
			//[todo] � localiser
			System.out
				.println("Erreur lecteur fichier patron DXF: info par d�faut...");
			infoStack = null;
		}
		catch(DXFTemplateParseException tpeEx)
		{
			tpeEx.resolve();
			infoStack = null;
		}
	}

	public DXFInformation[] getTemplate(){
		if(infoStack == null)
			return DXFConstants.DXF_FALLBACK_TEMPLATE;
		else
		{
			DXFInformation[] array = new DXFInformation[infoStack.size()];
			infoStack.toArray(array);
			infoStack = null;
			return array;
		}
	}
};


/// DXFTemplateParser.java ends here
