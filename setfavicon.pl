#!/usr/bin/env perl
# setfavicon.pl --- -*- coding: utf-8 -*-
# Copyright 2012 Vincent Belaïche
#
# Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
# Version: $Id: setfavicon.pl,v 1.2 2013/03/16 17:40:13 vincentb1 Exp $
# Keywords:
# X-URL: http://www.jpicedt.org/
#
# Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant les principes de
# diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
# conditions de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA sur le site
# "http://www.cecill.info".
#
# En contrepartie de l'accessibilité au code source et des droits de copie, de modification et de
# redistribution accordés par cette licence, il n'est offert aux utilisateurs qu'une garantie limitée.  Pour
# les mêmes raisons, seule une responsabilité restreinte pèse sur l'auteur du programme, le titulaire des
# droits patrimoniaux et les concédants successifs.
#
# A cet égard l'attention de l'utilisateur est attirée sur les risques associés au chargement, à
# l'utilisation, à la modification et/ou au développement et à la reproduction du logiciel par l'utilisateur
# étant donné sa spécificité de logiciel libre, qui peut le rendre complexe à manipuler et qui le réserve donc
# à des développeurs et des professionnels avertis possédant des connaissances informatiques approfondies.
# Les utilisateurs sont donc invités à charger et tester l'adéquation du logiciel à leurs besoins dans des
# conditions permettant d'assurer la sécurité de leurs systèmes et ou de leurs données et, plus généralement,
# à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
#
# Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris connaissance de la licence
# CeCILL, et que vous en avez accepté les termes.
#
## Commentary:

#

## Installation:

## Code:
use strict;
use warnings;
use feature qw(say unicode_strings);
use PerlIO;
use File::Spec;

my $version='$Id: setfavicon.pl,v 1.2 2013/03/16 17:40:13 vincentb1 Exp $';
my $error_count = 0;
my $allpages_error_count = 0;
my $verbose;
my $quiet;

sub usage { 
	my $retval = shift; 
	print "This script force the favicon of an html page. By default the favicon is set with relative path.

Usage:
    setfavicon.pl ARGUMENTS

    --version           : show version and exit
    -?, -h, --help      : show this message and exit
    -i, --icon ARG      : path of the favicon file
    -f, --format ARG    : format string, default is:
                         <link rel=\"icon\" href=\"%s\" type=\"image/png\" sizes=\"48x48\"/>
    -p, --page ARG      : HTML page file-name is ARG, must be provide after --icon argument.
    -l, --page-list ARG : ARG is the file-name of a file that contains file-names of pages to be processed, one filename per line. for instance if file ARG has two lines with P1 on the first line, and P2 on the second line, then this is equivalent to doing --page P1 --page P2.
    -v, --verbose       : Output some more messages
";
	exit $retval;
}


my $pagefile;
my $formatstring = 0;
my $iconpath;

my @args;
my $argcount = 0;

my @open_at_file_names;
my @open_at_file_handles;
my $open_at_file_count = 0;

my $i = 0;
while($i < @ARGV){
	if($ARGV[$i] eq "-@" || $ARGV[$i] eq "--at")
	{
		$open_at_file_names[$open_at_file_count] = $ARGV[++ $i];
		if($verbose)
        {
			say STDOUT "getting ARGV[$i]=file of args: $open_at_file_names[$open_at_file_count].";
		}
		++$i;
		my $filehandle;
		open($filehandle,"<:crlf",$open_at_file_names[$open_at_file_count])
			or die "Can't open $open_at_file_names[$open_at_file_count]";
		$open_at_file_handles[$open_at_file_count ++] =  $filehandle;
	  NEXT_FH:
		{
			while(my $line = <$filehandle>){
				$line =~ s/^([^\r\n]*)\r?\n?$/$1/;
				if($verbose)
				{
					say STDOUT "getting arg from file=$line.";
				}
				if($line eq "-@" || $line eq "--at")
				{
					$line = <$filehandle>;
					$open_at_file_names[$open_at_file_count] = $line;
					open($filehandle,"<:crlf",$line);
					$open_at_file_handles[$open_at_file_count ++] = $filehandle;
				}
				else
                {
					$args[$argcount ++ ] = $line;
				}
			}
			close($filehandle);
		}

		if(-- $open_at_file_count > 0)
		{
			$filehandle = $open_at_file_handles[$open_at_file_count];
			goto NEXT_FH;
		}
	}
	elsif($ARGV[$i] =~ /\A-(l|-page-list)\Z/)
    {
		my $filehandle;
		open($filehandle,"<:crlf",$ARGV[++ $i]);
		++ $i;
		while(my $line = <$filehandle>){
			$line =~ s/^([^\r\n]*)\r?\n?$/$1/;
			$args[$argcount ++ ] = "-p";
			$args[$argcount ++ ] = $line;
		}
		close($filehandle);
	}
	elsif($ARGV[$i] =~ /\A-(v|-verbose)\Z/)
    {
		$verbose = 1;
		$i ++;
		use Date::Calc qw(Today_and_Now);
	} 
	else
	{
		if($verbose)
        {
			say STDOUT "getting ARGV[$i]=$ARGV[$i].";
		}
		$args[$argcount ++ ] = $ARGV[$i ++];
	}
}

sub process()
{
# Get icon relative path
	if(!$formatstring)
    {
		$formatstring = "<link rel=\"icon\" href=\"%s\" type=\"image/png\" sizes=\"48x48\"/>";
	} 

	my @iconpath_dirs = File::Spec->splitdir($iconpath);
	my @pagefile_dirs = File::Spec->splitdir($pagefile);
	
	my $l;
	if(@iconpath_dirs < @pagefile_dirs)
    {
		$l = @iconpath_dirs;
	}
	else
    {
		$l = @pagefile_dirs;
	}
	my $i = 0;
	my $reliconpath;
	
	
	while($i < $l)
    {
		if($iconpath_dirs[$i] ne $pagefile_dirs[$i])
		{
			my $j = $i--;
			while($j < $#pagefile_dirs)
			{
				$iconpath_dirs[$i --] = "..";
				++$j;
			}
			$reliconpath = join("/", @iconpath_dirs[ ($i+1) .. $#iconpath_dirs ]);
			goto DONE_WITH_RELICONPATH;
		}
		else
		{
			++$i;
		}
	}
DONE_WITH_RELICONPATH:
	
	my $link = sprintf($formatstring, $reliconpath);


	my $page;
	open(PAGE, "<:crlf" ,"$pagefile") || die("Could not open file \"$pagefile\"");
    {
		my @pages = <PAGE>;
		$page = join("",@pages);
	}
	close(PAGE);
	
	
	if($page =~ m!<head>(.*)</head>!ism )
    {
		my $prolog = $` . "<head>";
		my $header = $1;
		my $epilog = "\n</head>" . $'; #';
		
		# Get icon if any
		if($header =~ m!(<link\b[^>]+rel[ \t\n]*=[ \t\n]*"icon"[^>]*/>)!ms)
		{
			$header = $` . $link . $'; #';
		}
		else
		{
			$header = $header . $link;
		}
		
		open(PAGE, ">" ,"$pagefile") || die("Could not open file \"$pagefile\"");
		print PAGE $prolog;
		print PAGE $header;
		print PAGE $epilog;
		close(PAGE);
	}
	else
    {
		$error_count = 1+ $error_count;
		say STDERR "Error: can't find header";
	}
	
	if(!$quiet)
    {
		printf STDOUT "[setfavicon] processed file \"$pagefile\"\n";
		if($verbose)
		{
			my @today_now = Today_and_Now();
			printf STDOUT "\trun on %02d-%02d-%02dT%02d:%02d:%02d\n" , @today_now;
		}
    }

	$allpages_error_count += $error_count;
}


$i = 0;
while($i < $argcount){
	if($i + 1 < $argcount)
	{
		if($args[$i] =~ /\A-(i|-icon)\Z/)
		{
			$iconpath = $args[$i+1];
			$i = $i +2;
		}
		elsif($args[$i] =~ /\A-(p|-page)\Z/)
		{
			$pagefile = $args[$i+1];
			$i = $i +2;
			process();
		}
		elsif($args[$i] =~ /\A-(f|-format)\Z/)
		{
			$formatstring = $args[$i+1];
			$i = $i +2;
		}
		else
		{
			goto ONE_ARG;
		}
	}
	else
	{
	  ONE_ARG:
		{
			if($args[$i] =~ /\A-(q|-quiet)\Z/)
			{
				$quiet = 1;
				$i ++;
			}
			elsif($args[$i] eq "--version")
			{
				say STDOUT "Version of javaprop-re-comment.pl = $version\n";
				exit 0;
			}
			elsif($args[$i] =~ /\A-\?|-h|--help\Z/)
			{
				usage(0);
				exit;
			}
			else
			{
				say STDOUT "Invalid remaining arguments: $args[$i .. $argcount]\n";
				usage(-1);
			}
		}
	}
}



if(!$quiet)
{
   say STDOUT "[setfavicon] Done: setfavicon is finished.\n";
}
exit -$error_count;
