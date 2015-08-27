*********************
Compiling the sources
*********************


What do you need?
------------------

 ---
|ANT|
 ---

There's a build.xml (aka Makefile) for the ANT utility in the CVS root directory
(that is, the parent directory of this one).
Hence you need ... ant to rebuild the binaries.
Ant can be downloaded from : http://ant.apache.org/
Once you got it installed, and assuming third party libraries (see below) are also 
installed, simply type 
* "ant all" to rebuild everything (including API documentation and jar archives), 
* "ant -projecthelp" to get a list of available tasks.

 ---------------------
|Third-party libraries|
 ---------------------

All third-party libraries used by jPicEdt were obviously released under the LGPL License.
jPicEdt requires the beanshell interpreter libray (used for macro support). 

BeanShell
---------
The source code for BeanShell won't be included in jpicedt's source tree as long as we use it
unmodified. This makes it easier to incorporate updates, by simply using the last (binary) 
release. Hence you need to download these libraries by yourself...

jPicEdt works and compiles fine with bsh-2.0b4.jar, and probably with later releases as well. 
Binaries and sources can be obtained, e.g. at:
  http://www.beanshell.org/ 
or (for latest snapshots), at :
  http://www.sourceforge.net/projects/beanshell/

Once you're done with downloading this binary jar files, unjar it by typing (in this directory) :

jar xvf bsh-2.0b4.jar


jarbundler 
---------- 

You may also need to download jarbundler binary jar file from
http://jarbundler.sourceforge.net/.  If so, create an osx directory under
jpicedt at the same level as the build.xml file, and place there
the jar file. Alternativelyn the osx directory may be created anywhere
where ant can find it (e.g. under ${user.home}/.ant/lib, where ${user.home}
directory depends on your system, c.f. ant documentation).

Currently jPicEdt build with jarbundler-2.1.0.jar. If you have a different
(more recent version) version of jarbundler, then you should modify the
corresponding class path in the build.xml file.


 ----------------
| Directory tree |
 ----------------
After a CVS checkout, your directory tree should look like this :

build.xml -> makefile for the Ant utility

jpicedt/ -> main source tree
jpicedt/README.SRC.txt/ -> THIS FILE
jpicedt/TODO.txt -> list of bugs and features requests.
jpicedt/CHANGES.txt -> list of major changes.
jpicedt/help-files/ -> on-line jPicEdt documentation  and CeCILL v2 license agreement.      
jpicedt/lang/ -> internationalization (i18n) files          
jpicedt/jpicedt/ -> jpicedt package sources
jpicedt/meta-inf/ -> manifest file (used to build jpicedt.jar)

add-ons/bsh/ -> BeanShell scripts for jPicEdt        
add-ons/windows/ -> DOS batches used to run programs from inside JPicEdt        
add-ons/unix/ -> Unix/Linux shells used to run programs from inside JPicEdt        
add-ons/fragments/ -> library of pictures and symbols        

dist/ -> distribution files (installer sources + some txt and html files)
osx/  -> jarbundler jar file (not needed if otherwise in your ant libs)

api-doc/ -> HTML files, GIF and PNG images, used to generate the API documentation (javadoc)

Uncompressed third-party jars should go here :
jpicedt/bsh/ -> BeanShell interpreter (decompressed jar file)

The create_links_for_debug.sh bash script allows you to create convenient links to extra directories that are not needed
to compile, but are useful when debugging (fragments and bsh macros).
--------------------------
Last-upd : 25th December 2008













