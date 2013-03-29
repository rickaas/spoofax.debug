build the rtree file using

strj -i ../trans/strategodebuglib.str -o strategodebuglib.ctree -p strategodebuglib --library --clean -la stratego-lib -F

the rtree file should be on the working path

copy to org.strategoxt.imp.debug.stratego.core/lib so the generated str files with debug info will report a module not found problem
