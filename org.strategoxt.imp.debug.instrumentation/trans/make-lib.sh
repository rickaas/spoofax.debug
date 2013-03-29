strj -i dsldi.str -o ../str-lib/dsldi.ctree -I ../ --clean --library -p dsldi -F

echo second
strj -i libdsldi/main.str -o ../str-lib/libdsldi.ctree -I ../ -I . --clean --library -p libdsldi -F
