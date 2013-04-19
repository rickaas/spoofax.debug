spoofax.debug
=============

Language parametric debugger framework

Building from source
====================

    git clone git://github.com/rickaas/spoofax.debug.git
    cd spoofax.debug
    cd org.spoofax.debug.interfaces.java
    ant -f build.main.xml
    cd ..
    # artifacts are located in:
    # - org.spoofax.debug.interfaces/build/jar
    # - org.spoofax.debug.interfaces.java/build/jar

    import the org.strategoxt.imp.debug.instrumentation project in Eclipse and run build.main.xml
    # or
    cd org.strategoxt.imp.debug.instrumentation
    copy {aster.jar, make_permissive.jar, sdf2imp.jar, StrategoMix.def, strategoxt.jar} to utils
    ant -f build.main.external.xml all
    # The dist dir contains all jars and rtree artifacts
    
    cd ..
    cd ..
    # we are not in the top level dir
    git clone git://github.com/rickaas/strategoxt.debug.git
    # build strategoxt instrumentation

    # Build the strategoxt debug runtime
    cd ..
    cd 
    # Put them together with the other jar artifacts
