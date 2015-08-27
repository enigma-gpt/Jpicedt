// Plot_example_distrib.sce --- -*- coding: iso-8859-1 -*-
// jPicEdt 1.6, a picture editor for LaTeX.
// Copyright (C) 2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: Plot_example_distrib.sce,v 1.1 2013/10/07 19:16:17 vincentb1 Exp $
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
// A cet égard l'attention de l'utilisateur est attirée sur les risques associés au chargement, à
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

// This BeanShell script demonstrate the use of Plot.bshi
//

/// Code:
clc;
clear;
xdel(winsid());

old_path = pwd();
cd(get_absolute_file_path('Plot_example_distrib.sce'));

function distrib_XYdYd2Y_gaussian
  
  t = linspace(-1,1,8).';

  mu    = 0
  sigma = 0.45;
  sigma2 = sigma*sigma;

  y = exp(-(t-mu).^2 /sigma2) / sqrt(2*%pi*sigma2);
  dy = 2* (mu - t) .* y / sigma2
  d2y = 2* (2* (mu - t).^2 / sigma2 - 1) .*y / sigma2

  [fd, err] = mopen('Plot_example_XYdYd2Y_gaussian.txt','w');
  mfprintf(fd,'%f\t%f\t%f\t%f\n',t,y,dy,d2y);
  mclose(fd);

endfunction

function distrib_XYdYd2Y_cos
  
  t = linspace(-1,1,8).';

  pulsation = %pi*0.5;

  y  = cos(pulsation*t);
  dy = -pulsation*sin(pulsation*t);
  d2y = -pulsation^2*cos(pulsation*t);

  [fd, err] = mopen('Plot_example_XYdYd2Y_cos.txt','w');
  mfprintf(fd,'%f\t%f\t%f\t%f\n',t,y,dy,d2y);
  mclose(fd);

endfunction

//-----------------------------------------------------------------------------------------------------------

function distrib_XYdY_gaussian
  
  t = linspace(-1,1,8).';

  mu    = 0
  sigma = 0.45;
  sigma2 = sigma*sigma;

  y = exp(-(t-mu).^2 /sigma2) / sqrt(2*%pi*sigma2);
  dy = 2* (mu - t) .* y / sigma2

  [fd, err] = mopen('Plot_example_XYdY_gaussian.txt','w');
  mfprintf(fd,'%f\t%f\t%f\n',t,y,dy);
  mclose(fd);

endfunction


function distrib_XYdY_cos
  
  t = linspace(-1,1,8).';

  pulsation = %pi*0.5;

  y  = cos(pulsation*t);
  dy = -pulsation*sin(pulsation*t);

  [fd, err] = mopen('Plot_example_XYdY_cos.txt','w');
  mfprintf(fd,'%f\t%f\t%f\n',t,y,dy);
  mclose(fd);

endfunction

//-----------------------------------------------------------------------------------------------------------

function distrib_XY_gaussian
  
  t = linspace(-1,1,16).';

  mu    = 0
  sigma = 0.45;
  sigma2 = sigma*sigma;

  y = exp(-(t-mu).^2 /sigma2) / sqrt(2*%pi*sigma2);

  [fd, err] = mopen('Plot_example_XY_gaussian.txt','w');
  mfprintf(fd,'%f\t%f\n',t,y);
  mclose(fd);

endfunction


function distrib_XY_cos
  
  t = linspace(-1,1,17).';

  pulsation = %pi*0.5;

  y  = cos(pulsation*t);

  [fd, err] = mopen('Plot_example_XY_cos.txt','w');
  mfprintf(fd,'%f\t%f\n',t,y);
  mclose(fd);

endfunction

distrib_XYdYd2Y_gaussian();
distrib_XYdYd2Y_cos();

distrib_XYdY_gaussian();
distrib_XYdY_cos();

distrib_XY_gaussian();
distrib_XY_cos();


cd(old_path);

// ********************************	END	************************************ **
